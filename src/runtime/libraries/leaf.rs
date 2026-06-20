// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::collections::{BTreeMap, HashMap, HashSet};

use crate::model_tree::{
    ModelAssignVarTree, ModelBlockTree, ModelNodeTree, ModelSetAttributeTree, ModelSetEntityTree,
    ModelValueTypeTree,
};

use super::{
    super::{LeafObject, RuntimeError, Value},
    expect_string,
    tmpl_leaf::build_tmpl_tree_fields_from_instance_str,
};

pub(crate) fn call(
    model: &ModelBlockTree,
    target: &str,
    args: &[Value],
) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Leaf.model" => model_leaf(model, args),
        "TLang.Leaf.get" => get(args),
        "TLang.Leaf.keys" => keys(args),
        "TLang.Leaf.has" => has(args),
        _ => Err(RuntimeError(format!(
            "unknown leaf library function `{target}`"
        ))),
    }
}

fn model_leaf(model: &ModelBlockTree, args: &[Value]) -> Result<Value, RuntimeError> {
    if !args.is_empty() {
        return Err(RuntimeError(
            "TLang.Leaf.model expects no arguments".to_string(),
        ));
    }

    // Build a name → entity index for parent-set resolution during inheritance.
    // The index uses the simple set name (as declared, without package qualifier)
    // because model nodes are keyed by their simple name.
    let set_index: HashMap<&str, &ModelSetEntityTree> = model
        .nodes
        .iter()
        .filter_map(|n| match n {
            ModelNodeTree::SetEntity(e) => Some((e.name.as_str(), e)),
            _ => None,
        })
        .collect();

    let mut fields = BTreeMap::new();
    for node in &model.nodes {
        let key = node_name(node);
        let value = match node {
            ModelNodeTree::AssignVar(a) => assign_var_to_value(a),
            ModelNodeTree::SetEntity(e) => set_entity_to_value(e, &set_index),
        };
        insert_unique(&mut fields, key, value);
    }
    Ok(Value::Leaf(LeafObject::new(fields)))
}

fn get(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Leaf.get expects exactly two arguments".to_string(),
        ));
    }

    let leaf = expect_leaf(&args[0])?;
    let key = expect_string(&args[1], "TLang.Leaf.get key")?;
    leaf.get(&key)
        .cloned()
        .ok_or_else(|| RuntimeError(format!("TLang.Leaf.get could not find `{key}`")))
}

fn has(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Leaf.has expects exactly two arguments".to_string(),
        ));
    }
    let leaf = expect_leaf(&args[0])?;
    let key = expect_string(&args[1], "TLang.Leaf.has key")?;
    Ok(Value::Bool(leaf.get(&key).is_some()))
}

fn keys(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Leaf.keys expects exactly one argument".to_string(),
        ));
    }
    let leaf = expect_leaf(&args[0])?;
    let keys = leaf
        .fields
        .keys()
        .map(|k| Value::String(k.clone()))
        .collect();
    Ok(Value::List(keys))
}

fn node_name(node: &ModelNodeTree) -> String {
    match node {
        ModelNodeTree::AssignVar(assign) => assign.name.clone(),
        ModelNodeTree::SetEntity(entity) => entity.name.clone(),
    }
}

fn assign_var_to_value(assign: &ModelAssignVarTree) -> Value {
    let mut fields = BTreeMap::new();
    fields.insert("kind".to_string(), Value::String("assign".to_string()));
    fields.insert("name".to_string(), Value::String(assign.name.clone()));
    if let Some(ty) = &assign.ty {
        fields.insert(
            "type".to_string(),
            Value::String(model_value_type_to_string(ty)),
        );
    }
    fields.insert("value".to_string(), Value::String(assign.value.clone()));

    // For TemplateInstance nodes, also populate the pre-parsed package/nodes/lang
    // fields so generators can use TLang.Leaf.get(tmpl, "package") etc. directly.
    let is_template_instance = assign
        .ty
        .as_ref()
        .map(|t| model_value_type_to_string(t) == "TemplateInstance")
        .unwrap_or(false);
    if is_template_instance
        && let Some((lang, pkg, nodes)) = build_tmpl_tree_fields_from_instance_str(&assign.value)
    {
        fields.insert("lang".to_string(), Value::String(lang));
        fields.insert("package".to_string(), Value::String(pkg));
        fields.insert("nodes".to_string(), Value::List(nodes));
    }

    Value::Leaf(LeafObject::new(fields))
}

/// Builds the Leaf value for a `set` entity, merging parent attrs into child attrs.
///
/// Inheritance semantics:
/// - Parent attrs are collected first (left-to-right, later parents override earlier)
/// - Child's own attrs are applied last, overriding any inherited value
/// - Circular inheritance is detected and broken by returning only own attrs
fn set_entity_to_value(
    entity: &ModelSetEntityTree,
    set_index: &HashMap<&str, &ModelSetEntityTree>,
) -> Value {
    let mut fields = BTreeMap::new();
    fields.insert("kind".to_string(), Value::String("set".to_string()));
    fields.insert("name".to_string(), Value::String(entity.name.clone()));
    // `ext` — first parent name as a String (backward-compatible single-parent access).
    if let Some(first) = entity.exts.first() {
        fields.insert("ext".to_string(), Value::String(first.clone()));
    }
    // `exts` — full list of parent names in declaration order.
    fields.insert(
        "exts".to_string(),
        Value::List(
            entity
                .exts
                .iter()
                .map(|e| Value::String(e.clone()))
                .collect(),
        ),
    );
    fields.insert("params".to_string(), attributes_to_leaf(&entity.params));

    // Compute attrs with inheritance: parent defaults + child overrides.
    let mut visiting = HashSet::new();
    let inherited = inherited_attrs(entity, set_index, &mut visiting);
    fields.insert("attrs".to_string(), Value::Leaf(LeafObject::new(inherited)));

    Value::Leaf(LeafObject::new(fields))
}

/// Recursively collects the merged attrs for `entity`, applying inheritance.
///
/// Parent attrs are collected first; child attrs override them.
/// `visiting` tracks the current resolution path to break cycles.
fn inherited_attrs(
    entity: &ModelSetEntityTree,
    set_index: &HashMap<&str, &ModelSetEntityTree>,
    visiting: &mut HashSet<String>,
) -> BTreeMap<String, Value> {
    if !visiting.insert(entity.name.clone()) {
        // Circular inheritance — return only own attrs to break the cycle.
        return own_attrs(entity);
    }

    let mut merged: BTreeMap<String, Value> = BTreeMap::new();

    // Collect parent attrs (left-to-right; later parents override earlier ones).
    for ext_name in &entity.exts {
        // Parent names are stored as written in source: "Forge.Entity".
        // Model nodes use the simple (unqualified) name: "Entity".
        // Resolve by taking the last dot-separated segment.
        let simple = ext_name.rsplit('.').next().unwrap_or(ext_name.as_str());
        if let Some(parent) = set_index.get(simple) {
            let parent_attrs = inherited_attrs(parent, set_index, visiting);
            merged.extend(parent_attrs);
        }
    }

    // Child's own attrs override all inherited values.
    merged.extend(own_attrs(entity));

    visiting.remove(&entity.name);
    merged
}

fn own_attrs(entity: &ModelSetEntityTree) -> BTreeMap<String, Value> {
    entity
        .attrs
        .iter()
        .enumerate()
        .map(|(i, a)| {
            let key = a.attr.clone().unwrap_or_else(|| i.to_string());
            (key, attribute_to_value(a))
        })
        .collect()
}

fn attributes_to_leaf(attributes: &[ModelSetAttributeTree]) -> Value {
    let mut fields = BTreeMap::new();
    for (index, attribute) in attributes.iter().enumerate() {
        let key = attribute.attr.clone().unwrap_or_else(|| index.to_string());
        insert_unique(&mut fields, key, attribute_to_value(attribute));
    }
    Value::Leaf(LeafObject::new(fields))
}

fn attribute_to_value(attribute: &ModelSetAttributeTree) -> Value {
    let mut fields = BTreeMap::new();
    if let Some(attr) = &attribute.attr {
        fields.insert("name".to_string(), Value::String(attr.clone()));
    }
    fields.insert(
        "value".to_string(),
        Value::String(model_value_type_to_string(&attribute.value)),
    );
    Value::Leaf(LeafObject::new(fields))
}

fn model_value_type_to_string(value: &ModelValueTypeTree) -> String {
    match value {
        ModelValueTypeTree::Type(value) => value.clone(),
        ModelValueTypeTree::Array(value) => format!("{value}[]"),
        ModelValueTypeTree::FuncDef {
            param_types,
            ret_types,
        } => {
            let params = param_types.join(", ");
            if ret_types.is_empty() {
                format!("({params})")
            } else {
                format!("({params}): {}", ret_types.join(", "))
            }
        }
        ModelValueTypeTree::Ref { path, currying } => {
            use crate::model_tree::RefArg;
            let mut text = format!("&{}", path.join("."));
            for curry in currying {
                if !curry.is_empty() {
                    let parts: Vec<String> = curry
                        .iter()
                        .map(|a| match a {
                            RefArg::This => "this".to_string(),
                            RefArg::Hole => "_".to_string(),
                            RefArg::Str(s) => {
                                let escaped = s
                                    .replace('\\', "\\\\")
                                    .replace('"', "\\\"")
                                    .replace('\n', "\\n")
                                    .replace('\t', "\\t");
                                format!("\"{escaped}\"")
                            }
                            RefArg::Int(n) => n.to_string(),
                            RefArg::Bool(b) => b.to_string(),
                            RefArg::Ref(ref_path) => format!("&{}", ref_path.join(".")),
                            RefArg::ImplParam(name) => name.to_string(),
                        })
                        .collect();
                    text.push('(');
                    text.push_str(&parts.join(", "));
                    text.push(')');
                }
            }
            text
        }
        ModelValueTypeTree::Impl { attrs } => {
            let attrs = attrs
                .iter()
                .map(|attribute| {
                    let mut text = String::new();
                    if let Some(name) = &attribute.attr {
                        text.push_str(name);
                        text.push_str(": ");
                    }
                    text.push_str(&model_value_type_to_string(&attribute.value));
                    text
                })
                .collect::<Vec<_>>()
                .join(", ");
            format!("impl {{ {attrs} }}")
        }
        ModelValueTypeTree::Generic { name, params } => format!("{name}<{}>", params.join(", ")),
        ModelValueTypeTree::ImplArray => "impl[]".to_string(),
        ModelValueTypeTree::StringLiteral(value) => value.clone(),
        ModelValueTypeTree::IntLiteral(n) => n.to_string(),
        ModelValueTypeTree::BoolLiteral(b) => b.to_string(),
        ModelValueTypeTree::ArrayLiteral(items) => {
            let inner: Vec<String> = items.iter().map(model_value_type_to_string).collect();
            format!("[{}]", inner.join(", "))
        }
    }
}

fn insert_unique(fields: &mut BTreeMap<String, Value>, key: String, value: Value) {
    if let std::collections::btree_map::Entry::Vacant(e) = fields.entry(key.clone()) {
        e.insert(value);
        return;
    }

    let mut index = 2usize;
    loop {
        let candidate = format!("{key}#{index}");
        if let std::collections::btree_map::Entry::Vacant(e) = fields.entry(candidate) {
            e.insert(value);
            return;
        }
        index += 1;
    }
}

fn expect_leaf(value: &Value) -> Result<&LeafObject, RuntimeError> {
    match value {
        Value::Leaf(leaf) => Ok(leaf),
        _ => Err(RuntimeError("TLang.Leaf.get expects a leaf".to_string())),
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::model_tree::{ModelSetAttributeTree, ModelValueTypeTree};
    use crate::tree_context::TreeContext;

    // ── Helpers ───────────────────────────────────────────────────────────────

    fn make_entity(name: &str, exts: Vec<&str>, attrs: Vec<(&str, &str)>) -> ModelSetEntityTree {
        ModelSetEntityTree {
            name: name.to_string(),
            exts: exts.iter().map(|s| s.to_string()).collect(),
            params: vec![],
            attrs: attrs
                .into_iter()
                .map(|(k, v)| ModelSetAttributeTree {
                    attr: Some(k.to_string()),
                    value: ModelValueTypeTree::StringLiteral(v.to_string()),
                    context: TreeContext::default(),
                })
                .collect(),
            output: None,
            exec: None,
            context: TreeContext::default(),
        }
    }

    fn index<'a>(entities: &'a [ModelSetEntityTree]) -> HashMap<&'a str, &'a ModelSetEntityTree> {
        entities.iter().map(|e| (e.name.as_str(), e)).collect()
    }

    /// Extract the `"value"` string from an attr leaf in the merged map.
    fn attr_value(merged: &BTreeMap<String, Value>, key: &str) -> Option<String> {
        match merged.get(key)? {
            Value::Leaf(leaf) => match leaf.get("value")? {
                Value::String(s) => Some(s.clone()),
                _ => None,
            },
            _ => None,
        }
    }

    // ── inherited_attrs ───────────────────────────────────────────────────────

    #[test]
    fn own_attrs_only_when_no_parents() {
        let base = make_entity("Base", vec![], vec![("kind", "animal"), ("legs", "4")]);
        let entities = [base.clone()];
        let idx = index(&entities);
        let mut visiting = HashSet::new();
        let merged = inherited_attrs(&base, &idx, &mut visiting);

        assert_eq!(attr_value(&merged, "kind").as_deref(), Some("animal"));
        assert_eq!(attr_value(&merged, "legs").as_deref(), Some("4"));
        assert_eq!(merged.len(), 2);
    }

    #[test]
    fn child_inherits_parent_attrs() {
        let base = make_entity("Base", vec![], vec![("kind", "animal"), ("color", "brown")]);
        let child = make_entity("Child", vec!["Base"], vec![("sound", "woof")]);
        let entities = [base, child.clone()];
        let idx = index(&entities);
        let mut visiting = HashSet::new();
        let merged = inherited_attrs(&child, &idx, &mut visiting);

        // Inherited from Base
        assert_eq!(attr_value(&merged, "kind").as_deref(), Some("animal"));
        assert_eq!(attr_value(&merged, "color").as_deref(), Some("brown"));
        // Own
        assert_eq!(attr_value(&merged, "sound").as_deref(), Some("woof"));
        assert_eq!(merged.len(), 3);
    }

    #[test]
    fn child_attr_overrides_parent() {
        let base = make_entity("Base", vec![], vec![("kind", "animal"), ("color", "brown")]);
        let over = make_entity("Override", vec!["Base"], vec![("color", "black")]);
        let entities = [base, over.clone()];
        let idx = index(&entities);
        let mut visiting = HashSet::new();
        let merged = inherited_attrs(&over, &idx, &mut visiting);

        // `kind` inherited; `color` overridden
        assert_eq!(attr_value(&merged, "kind").as_deref(), Some("animal"));
        assert_eq!(attr_value(&merged, "color").as_deref(), Some("black"));
        assert_eq!(merged.len(), 2);
    }

    #[test]
    fn multi_level_inheritance() {
        let base = make_entity("Base", vec![], vec![("kind", "animal"), ("legs", "4")]);
        let child = make_entity("Child", vec!["Base"], vec![("sound", "woof")]);
        let grand = make_entity("GrandChild", vec!["Child"], vec![("name", "rex")]);
        let entities = [base, child, grand.clone()];
        let idx = index(&entities);
        let mut visiting = HashSet::new();
        let merged = inherited_attrs(&grand, &idx, &mut visiting);

        // Two hops: GrandChild → Child → Base
        assert_eq!(attr_value(&merged, "kind").as_deref(), Some("animal"));
        assert_eq!(attr_value(&merged, "legs").as_deref(), Some("4"));
        // One hop: GrandChild → Child
        assert_eq!(attr_value(&merged, "sound").as_deref(), Some("woof"));
        // Own
        assert_eq!(attr_value(&merged, "name").as_deref(), Some("rex"));
        assert_eq!(merged.len(), 4);
    }

    #[test]
    fn qualified_parent_name_resolved_by_last_segment() {
        // "Forge.Entity" resolves to the model node named "Entity"
        let entity = make_entity("Entity", vec![], vec![("kind", "entity")]);
        let child = make_entity("Child", vec!["Forge.Entity"], vec![("field", "name")]);
        let entities = [entity, child.clone()];
        let idx = index(&entities);
        let mut visiting = HashSet::new();
        let merged = inherited_attrs(&child, &idx, &mut visiting);

        assert_eq!(attr_value(&merged, "kind").as_deref(), Some("entity"));
        assert_eq!(attr_value(&merged, "field").as_deref(), Some("name"));
    }

    #[test]
    fn unresolved_parent_silently_skipped() {
        let child = make_entity("Child", vec!["NonExistent"], vec![("own", "yes")]);
        let entities = [child.clone()];
        let idx = index(&entities);
        let mut visiting = HashSet::new();
        let merged = inherited_attrs(&child, &idx, &mut visiting);

        // Only the child's own attr — no panic from the missing parent
        assert_eq!(attr_value(&merged, "own").as_deref(), Some("yes"));
        assert_eq!(merged.len(), 1);
    }

    #[test]
    fn circular_inheritance_does_not_hang() {
        // A extends B, B extends A — must terminate without panic
        let a = make_entity("A", vec!["B"], vec![("from_a", "1")]);
        let b = make_entity("B", vec!["A"], vec![("from_b", "2")]);
        let entities = [a.clone(), b];
        let idx = index(&entities);
        let mut visiting = HashSet::new();
        let merged = inherited_attrs(&a, &idx, &mut visiting);

        // A's own attr must be present; the cycle is broken gracefully
        assert!(
            merged.contains_key("from_a"),
            "expected from_a in merged: {merged:?}"
        );
    }

    // ── Leaf.has ──────────────────────────────────────────────────────────────

    #[test]
    fn has_returns_true_for_existing_key() {
        let mut fields = BTreeMap::new();
        fields.insert("x".to_string(), Value::String("hello".to_string()));
        let leaf = Value::Leaf(LeafObject::new(fields));
        let result = has(&[leaf, Value::String("x".to_string())]).unwrap();
        assert_eq!(result, Value::Bool(true));
    }

    #[test]
    fn has_returns_false_for_absent_key() {
        let leaf = Value::Leaf(LeafObject::new(BTreeMap::new()));
        let result = has(&[leaf, Value::String("missing".to_string())]).unwrap();
        assert_eq!(result, Value::Bool(false));
    }

    #[test]
    fn has_errors_on_wrong_arg_count() {
        assert!(has(&[]).is_err());
        let leaf = Value::Leaf(LeafObject::new(BTreeMap::new()));
        assert!(has(&[leaf]).is_err());
    }
}
