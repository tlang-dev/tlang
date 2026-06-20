// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Runtime leaf builder for structured data templates.
//!
//! Converts a [`TmplDataBlocTree`] (parsed by `tmpl_data_tree`) into a
//! hierarchy of [`Value::Leaf`] / [`Value::List`] objects that generators can
//! traverse to emit HTML, JSON, YAML, TOML, XML, or any other format.
//!
//! ## Output shape
//!
//! A data-template instance leaf looks like:
//!
//! ```text
//! {
//!   kind:  "data",
//!   lang:  "html",         // first declared language
//!   langs: ["html", "json"],
//!   name:  "myCard",
//!   root:  <bloc leaf>,
//! }
//! ```
//!
//! Each **bloc leaf** looks like:
//!
//! ```text
//! {
//!   kind:          "bloc",
//!   name:          "div",         // tag / element name, "" if anonymous
//!   inline_attrs:  [<attr>, …],   // attributes from (…)
//!   children:      [<attr>, …],   // attributes / children from {…}
//! }
//! ```
//!
//! Each **attr leaf** is one of:
//!
//! ```text
//! // Include directive
//! { kind: "include", call: "renderItems(items)" }
//!
//! // Key/value pair
//! { kind: "set", key: "class", value: <value leaf> }
//! // (key is "" when the attribute is positional / unnamed)
//!
//! // Value leaves
//! { kind: "string",  value: "hello ${name}" }
//! { kind: "number",  value: "42" }
//! { kind: "bool",    value: true  }
//! { kind: "array",   items: [<attr>, …] }
//! { kind: "bloc",    … }         // nested element
//! ```

use std::collections::BTreeMap;
use std::collections::HashMap;

use crate::tmpl_data_tree::{
    TmplDataAttrTree, TmplDataBlocTree, TmplDataBlockTree, TmplDataSetTree, TmplDataValueTree,
};

use super::super::{LeafObject, Value};

// ── Entry point ───────────────────────────────────────────────────────────────

/// Build the top-level instance `Value::Leaf` for a data template call.
///
/// `args` maps each template parameter name to its string representation.
pub(crate) fn build_data_instance_leaf(
    tree: &TmplDataBlockTree,
    args: &HashMap<String, String>,
) -> Value {
    let root = bloc_to_value(&tree.content, args);

    let mut fields = BTreeMap::new();
    fields.insert("kind".to_string(), Value::String("data".to_string()));
    fields.insert(
        "lang".to_string(),
        Value::String(tree.langs.first().cloned().unwrap_or_default()),
    );
    fields.insert(
        "langs".to_string(),
        Value::List(
            tree.langs
                .iter()
                .map(|l| Value::String(l.clone()))
                .collect(),
        ),
    );
    fields.insert("name".to_string(), Value::String(tree.name.clone()));
    fields.insert("root".to_string(), root);

    Value::Leaf(LeafObject::new(fields))
}

// ── Bloc → Value ──────────────────────────────────────────────────────────────

fn bloc_to_value(bloc: &TmplDataBlocTree, args: &HashMap<String, String>) -> Value {
    let name = bloc
        .name
        .as_deref()
        .map(|n| substitute(n, args))
        .unwrap_or_default();

    let inline_attrs: Vec<Value> = bloc
        .inline_attrs
        .iter()
        .map(|a| attr_to_value(a, args))
        .collect();

    let children: Vec<Value> = bloc
        .children
        .iter()
        .map(|a| attr_to_value(a, args))
        .collect();

    let mut fields = BTreeMap::new();
    fields.insert("kind".to_string(), Value::String("bloc".to_string()));
    fields.insert("name".to_string(), Value::String(name));
    fields.insert("inline_attrs".to_string(), Value::List(inline_attrs));
    fields.insert("children".to_string(), Value::List(children));

    Value::Leaf(LeafObject::new(fields))
}

// ── Attribute → Value ─────────────────────────────────────────────────────────

fn attr_to_value(attr: &TmplDataAttrTree, args: &HashMap<String, String>) -> Value {
    match attr {
        TmplDataAttrTree::Include(call) => {
            let call = substitute(call, args);
            let mut fields = BTreeMap::new();
            fields.insert("kind".to_string(), Value::String("include".to_string()));
            fields.insert("call".to_string(), Value::String(call));
            Value::Leaf(LeafObject::new(fields))
        }
        TmplDataAttrTree::Set(set) => set_to_value(set, args),
    }
}

fn set_to_value(set: &TmplDataSetTree, args: &HashMap<String, String>) -> Value {
    let key = set
        .key
        .as_deref()
        .map(|k| substitute(k, args))
        .unwrap_or_default();

    let value_leaf = data_value_to_value(&set.value, args);

    let mut fields = BTreeMap::new();
    fields.insert("kind".to_string(), Value::String("set".to_string()));
    fields.insert("key".to_string(), Value::String(key));
    fields.insert("value".to_string(), value_leaf);

    Value::Leaf(LeafObject::new(fields))
}

// ── Data value → Value ────────────────────────────────────────────────────────

fn data_value_to_value(v: &TmplDataValueTree, args: &HashMap<String, String>) -> Value {
    match v {
        TmplDataValueTree::Str(s) => {
            let s = substitute(s, args);
            let mut fields = BTreeMap::new();
            fields.insert("kind".to_string(), Value::String("string".to_string()));
            fields.insert("value".to_string(), Value::String(s));
            Value::Leaf(LeafObject::new(fields))
        }
        TmplDataValueTree::Number(n) => {
            let mut fields = BTreeMap::new();
            fields.insert("kind".to_string(), Value::String("number".to_string()));
            fields.insert("value".to_string(), Value::String(n.clone()));
            Value::Leaf(LeafObject::new(fields))
        }
        TmplDataValueTree::Bool(b) => {
            let mut fields = BTreeMap::new();
            fields.insert("kind".to_string(), Value::String("bool".to_string()));
            fields.insert("value".to_string(), Value::Bool(*b));
            Value::Leaf(LeafObject::new(fields))
        }
        TmplDataValueTree::Array(items) => {
            let values: Vec<Value> = items.iter().map(|a| attr_to_value(a, args)).collect();
            let mut fields = BTreeMap::new();
            fields.insert("kind".to_string(), Value::String("array".to_string()));
            fields.insert("items".to_string(), Value::List(values));
            Value::Leaf(LeafObject::new(fields))
        }
        TmplDataValueTree::Bloc(bloc) => bloc_to_value(bloc, args),
    }
}

// ── Variable substitution ─────────────────────────────────────────────────────

/// Replace every `${name}` in `text` with the corresponding value from `args`.
/// Unrecognised names are left unchanged.  `$$` is emitted as a literal `$`.
pub(crate) fn substitute(text: &str, args: &HashMap<String, String>) -> String {
    const PLACEHOLDER: &str = "\x00DOLLAR\x00";
    let text = text.replace("$$", PLACEHOLDER);
    let mut result = String::with_capacity(text.len());
    let mut remaining = text.as_str();

    while let Some(start) = remaining.find("${") {
        result.push_str(&remaining[..start]);
        remaining = &remaining[start + 2..];
        if let Some(end) = remaining.find('}') {
            let key = &remaining[..end];
            if let Some(val) = args.get(key) {
                result.push_str(val);
            } else {
                result.push_str("${");
                result.push_str(key);
                result.push('}');
            }
            remaining = &remaining[end + 1..];
        } else {
            result.push_str("${");
        }
    }
    result.push_str(remaining);
    result.replace(PLACEHOLDER, "$")
}

// ── Tests ─────────────────────────────────────────────────────────────────────

#[cfg(test)]
mod tests {
    use super::*;
    use crate::ast::TemplateParam;
    use crate::tmpl_data_tree::parse_tmpl_data_block_tree;

    fn make_args(pairs: &[(&str, &str)]) -> HashMap<String, String> {
        pairs
            .iter()
            .map(|(k, v)| (k.to_string(), v.to_string()))
            .collect()
    }

    fn build(content: &str, langs: &[&str], args: &HashMap<String, String>) -> Value {
        let langs: Vec<String> = langs.iter().map(|s| s.to_string()).collect();
        let tree = parse_tmpl_data_block_tree(&langs, "test", &[], &format!("{{{}}}", content))
            .expect("parse ok");
        build_data_instance_leaf(&tree, args)
    }

    fn leaf_get<'a>(v: &'a Value, key: &str) -> Option<&'a Value> {
        if let Value::Leaf(obj) = v {
            obj.get(key)
        } else {
            None
        }
    }

    fn leaf_str<'a>(v: &'a Value, key: &str) -> Option<&'a str> {
        if let Value::String(s) = leaf_get(v, key)? {
            Some(s.as_str())
        } else {
            None
        }
    }

    // ── Top-level instance fields ─────────────────────────────────────────────

    #[test]
    fn instance_leaf_has_correct_kind_and_lang() {
        let inst = build(r#"{ name: "test" }"#, &["json"], &HashMap::new());
        assert_eq!(leaf_str(&inst, "kind"), Some("data"));
        assert_eq!(leaf_str(&inst, "lang"), Some("json"));
        assert_eq!(leaf_str(&inst, "name"), Some("test"));
    }

    #[test]
    fn instance_leaf_langs_list_contains_all_languages() {
        let inst = build(r#"{ }"#, &["html", "xml"], &HashMap::new());
        if let Some(Value::List(langs)) = leaf_get(&inst, "langs") {
            assert_eq!(langs.len(), 2);
            assert_eq!(langs[0], Value::String("html".to_string()));
            assert_eq!(langs[1], Value::String("xml".to_string()));
        } else {
            panic!("expected langs list");
        }
    }

    #[test]
    fn instance_leaf_root_is_bloc() {
        let inst = build(r#"div { }"#, &["html"], &HashMap::new());
        let root = leaf_get(&inst, "root").expect("root must exist");
        assert_eq!(leaf_str(root, "kind"), Some("bloc"));
        assert_eq!(leaf_str(root, "name"), Some("div"));
    }

    // ── Bloc structure ────────────────────────────────────────────────────────

    #[test]
    fn bloc_has_inline_attrs_list() {
        let inst = build(r#"div(class: "card")"#, &["html"], &HashMap::new());
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(attrs)) = leaf_get(root, "inline_attrs") {
            assert_eq!(attrs.len(), 1);
        } else {
            panic!("expected inline_attrs list");
        }
    }

    #[test]
    fn bloc_has_children_list() {
        let inst = build(r#"root { a: "1", b: "2" }"#, &["json"], &HashMap::new());
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            assert_eq!(children.len(), 2);
        } else {
            panic!("expected children list");
        }
    }

    // ── Set attribute ─────────────────────────────────────────────────────────

    #[test]
    fn set_attr_has_kind_key_and_value() {
        let inst = build(r#"{ title: "Hello" }"#, &["json"], &HashMap::new());
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            let attr = &children[0];
            assert_eq!(leaf_str(attr, "kind"), Some("set"));
            assert_eq!(leaf_str(attr, "key"), Some("title"));
        } else {
            panic!();
        }
    }

    #[test]
    fn set_attr_value_has_string_kind() {
        let inst = build(r#"{ msg: "world" }"#, &["json"], &HashMap::new());
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            let attr = &children[0];
            let val = leaf_get(attr, "value").unwrap();
            assert_eq!(leaf_str(val, "kind"), Some("string"));
            assert_eq!(leaf_str(val, "value"), Some("world"));
        } else {
            panic!();
        }
    }

    #[test]
    fn set_attr_value_has_number_kind() {
        let inst = build("{ port: 8080 }", &["json"], &HashMap::new());
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            let val = leaf_get(&children[0], "value").unwrap();
            assert_eq!(leaf_str(val, "kind"), Some("number"));
            assert_eq!(leaf_str(val, "value"), Some("8080"));
        } else {
            panic!();
        }
    }

    #[test]
    fn set_attr_value_has_bool_kind() {
        let inst = build("{ enabled: true }", &["json"], &HashMap::new());
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            let val = leaf_get(&children[0], "value").unwrap();
            assert_eq!(leaf_str(val, "kind"), Some("bool"));
            assert_eq!(leaf_get(val, "value"), Some(&Value::Bool(true)));
        } else {
            panic!();
        }
    }

    #[test]
    fn set_attr_value_has_array_kind() {
        let inst = build(r#"{ tags: ["a", "b"] }"#, &["json"], &HashMap::new());
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            let val = leaf_get(&children[0], "value").unwrap();
            assert_eq!(leaf_str(val, "kind"), Some("array"));
            if let Some(Value::List(items)) = leaf_get(val, "items") {
                assert_eq!(items.len(), 2);
            } else {
                panic!("expected items list");
            }
        } else {
            panic!();
        }
    }

    // ── Include directive ─────────────────────────────────────────────────────

    #[test]
    fn include_attr_has_correct_kind_and_call() {
        let inst = build("{ <[ renderRows(rows) ]> }", &["html"], &HashMap::new());
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            assert_eq!(leaf_str(&children[0], "kind"), Some("include"));
            assert_eq!(leaf_str(&children[0], "call"), Some("renderRows(rows)"));
        } else {
            panic!();
        }
    }

    // ── Variable substitution ─────────────────────────────────────────────────

    #[test]
    fn substitutes_param_in_string_value() {
        let args = make_args(&[("title", "My App")]);
        let inst = build(r#"{ title: "${title}" }"#, &["json"], &args);
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            let val = leaf_get(&children[0], "value").unwrap();
            assert_eq!(leaf_str(val, "value"), Some("My App"));
        } else {
            panic!();
        }
    }

    #[test]
    fn substitutes_param_in_tag_name() {
        let args = make_args(&[("tag", "div")]);
        let inst = build(r#"${tag}(class: "x")"#, &["html"], &args);
        let root = leaf_get(&inst, "root").unwrap();
        // The name should have been substituted.
        // Since `${tag}` is kept as-is until build time, the raw text is the name.
        // The name field in the leaf should contain the substituted value.
        assert_eq!(leaf_str(root, "name"), Some("div"));
    }

    #[test]
    fn dollar_dollar_emits_literal_dollar() {
        let inst = build(r#"{ expr: "$$value" }"#, &["yaml"], &HashMap::new());
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            let val = leaf_get(&children[0], "value").unwrap();
            assert_eq!(leaf_str(val, "value"), Some("$value"));
        } else {
            panic!();
        }
    }

    #[test]
    fn unknown_param_left_unchanged() {
        let inst = build(r#"{ msg: "${unknown}" }"#, &["json"], &HashMap::new());
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            let val = leaf_get(&children[0], "value").unwrap();
            assert_eq!(leaf_str(val, "value"), Some("${unknown}"));
        } else {
            panic!();
        }
    }

    // ── Nested blocs ──────────────────────────────────────────────────────────

    #[test]
    fn nested_bloc_is_represented_as_bloc_value() {
        let inst = build(
            r#"root { child { value: "x" } }"#,
            &["yaml"],
            &HashMap::new(),
        );
        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            let set = &children[0];
            let val = leaf_get(set, "value").unwrap();
            assert_eq!(leaf_str(val, "kind"), Some("bloc"));
            assert_eq!(leaf_str(val, "name"), Some("child"));
        } else {
            panic!();
        }
    }

    #[test]
    fn builds_full_html_instance() {
        let args = make_args(&[("title", "Home"), ("bodyText", "Welcome")]);
        let inst = build(
            r#"
            html {
                head {
                    title: "${title}"
                },
                body {
                    h1: "${title}",
                    p: "${bodyText}"
                }
            }
        "#,
            &["html"],
            &args,
        );

        let root = leaf_get(&inst, "root").unwrap();
        assert_eq!(leaf_str(root, "name"), Some("html"));

        if let Some(Value::List(children)) = leaf_get(root, "children") {
            assert_eq!(children.len(), 2);

            // head
            let head_val = leaf_get(&children[0], "value").unwrap();
            assert_eq!(leaf_str(head_val, "name"), Some("head"));

            // body
            let body_val = leaf_get(&children[1], "value").unwrap();
            assert_eq!(leaf_str(body_val, "name"), Some("body"));

            if let Some(Value::List(body_ch)) = leaf_get(body_val, "children") {
                assert_eq!(body_ch.len(), 2);
                let h1_val = leaf_get(&body_ch[0], "value").unwrap();
                assert_eq!(leaf_str(h1_val, "value"), Some("Home"));
            } else {
                panic!("body children missing");
            }
        } else {
            panic!("root children missing");
        }
    }

    #[test]
    fn builds_json_flat_config() {
        let args = make_args(&[("appName", "my-service"), ("port", "3000")]);
        let inst = build(
            r#"
            {
                name: "${appName}",
                port: "${port}",
                debug: false,
                features: ["auth", "logging"]
            }
        "#,
            &["json"],
            &args,
        );

        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            assert_eq!(children.len(), 4);
            // name
            let name_val = leaf_get(&children[0], "value").unwrap();
            assert_eq!(leaf_str(name_val, "value"), Some("my-service"));
            // features (array)
            let feat_val = leaf_get(&children[3], "value").unwrap();
            assert_eq!(leaf_str(feat_val, "kind"), Some("array"));
        } else {
            panic!();
        }
    }

    // ── substitute helper ─────────────────────────────────────────────────────

    #[test]
    fn substitute_replaces_known_params() {
        let args = make_args(&[("x", "hello"), ("y", "world")]);
        assert_eq!(substitute("${x} ${y}", &args), "hello world");
    }

    #[test]
    fn substitute_leaves_unknown_params_unchanged() {
        let args = HashMap::new();
        assert_eq!(substitute("${unknown}", &args), "${unknown}");
    }

    #[test]
    fn substitute_dollar_dollar_becomes_dollar() {
        let args = HashMap::new();
        assert_eq!(substitute("$$ref", &args), "$ref");
    }

    #[test]
    fn substitute_handles_multiple_interpolations() {
        let args = make_args(&[("a", "1"), ("b", "2")]);
        assert_eq!(substitute("${a}+${b}=${a}", &args), "1+2=1");
    }

    // ── multi-lang data block via parse_tmpl_data_block_tree ─────────────────

    #[test]
    fn builds_yaml_deployment_manifest() {
        let args = make_args(&[("appName", "frontend"), ("replicas", "3")]);
        let tree = parse_tmpl_data_block_tree(
            &["yaml".to_string()],
            "deployment",
            &[
                TemplateParam {
                    name: "appName".to_string(),
                    ty: "String".to_string(),
                },
                TemplateParam {
                    name: "replicas".to_string(),
                    ty: "Int".to_string(),
                },
            ],
            r#"{
                {
                    apiVersion: "apps/v1",
                    kind: "Deployment",
                    spec {
                        replicas: "${replicas}",
                        selector {
                            app: "${appName}"
                        }
                    }
                }
            }"#,
        )
        .expect("parse ok");

        let inst = build_data_instance_leaf(&tree, &args);
        assert_eq!(leaf_str(&inst, "kind"), Some("data"));
        assert_eq!(leaf_str(&inst, "lang"), Some("yaml"));

        let root = leaf_get(&inst, "root").unwrap();
        if let Some(Value::List(children)) = leaf_get(root, "children") {
            // apiVersion, kind, spec
            assert_eq!(children.len(), 3);
            let spec_val = leaf_get(&children[2], "value").unwrap();
            assert_eq!(leaf_str(spec_val, "name"), Some("spec"));
        } else {
            panic!("root children missing");
        }
    }
}
