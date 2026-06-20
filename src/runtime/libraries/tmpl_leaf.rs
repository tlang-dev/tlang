// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::collections::{BTreeMap, HashMap};

use crate::tmpl_tree::{TmplNodeKind, TmplNodeTree};

use super::super::{LeafObject, Value};

// ── template-instance-string parsing ─────────────────────────────────────────

/// Parse a `lang [<lang>] <name>(<args>) { … }` instance-value string into
/// its constituent parts: `(args_map, body, lang)`.
pub(crate) fn parse_tmpl_instance_string(
    s: &str,
) -> Result<(HashMap<String, String>, String, String), String> {
    let s = s.trim();
    let s = s
        .strip_prefix("lang")
        .ok_or("expected 'lang' keyword")?
        .trim_start();
    let s = s.strip_prefix('[').ok_or("expected '[' after 'lang'")?;
    let bracket_end = s.find(']').ok_or("expected ']' to close lang bracket")?;
    let lang = s[..bracket_end].trim().to_string();
    let s = s[bracket_end + 1..].trim_start();
    let paren_start = s
        .find('(')
        .ok_or("expected '(' to open template arguments")?;
    let s = &s[paren_start + 1..];
    let mut depth = 1usize;
    let mut paren_end = None;
    for (i, ch) in s.char_indices() {
        match ch {
            '(' => depth += 1,
            ')' => {
                depth -= 1;
                if depth == 0 {
                    paren_end = Some(i);
                    break;
                }
            }
            _ => {}
        }
    }
    let paren_end = paren_end.ok_or("unmatched '(' in template instance string")?;
    let args_str = &s[..paren_end];
    let body = s[paren_end + 1..].trim().to_string();
    Ok((parse_args(args_str), body, lang))
}

pub(crate) fn parse_args(args_str: &str) -> HashMap<String, String> {
    let mut map = HashMap::new();
    for pair in args_str.split(',') {
        let pair = pair.trim();
        if pair.is_empty() {
            continue;
        }
        if let Some((k, v)) = pair.split_once('=') {
            map.insert(k.trim().to_string(), v.trim().to_string());
        }
    }
    map
}

// ── node/tree helpers (shared across codegen modules) ────────────────────────

pub(crate) fn node_to_leaf(node: &TmplNodeTree, args: &HashMap<String, String>) -> Value {
    let mut fields = BTreeMap::new();
    match node.kind {
        TmplNodeKind::Impl => {
            let sig = sig_without_body(&node.text);
            let after_impl = sig.trim_start_matches("impl").trim_start();
            let (modifier, name_part) = if after_impl.starts_with('[') {
                let end = after_impl
                    .find(']')
                    .unwrap_or_else(|| after_impl.len().saturating_sub(1));
                let modifier = after_impl[1..end].trim().to_string();
                (modifier, after_impl[end + 1..].trim())
            } else {
                ("class".to_string(), after_impl.trim())
            };
            let name = substitute(name_part, args);
            let annotation = args
                .get("annotation")
                .map(String::as_str)
                .unwrap_or("")
                .trim()
                .to_string();
            let extends = args
                .get("extendsClass")
                .or_else(|| args.get("extends"))
                .map(String::as_str)
                .unwrap_or("")
                .trim()
                .to_string();
            let children: Vec<Value> = node
                .children
                .iter()
                .map(|c| node_to_leaf(c, args))
                .collect();

            fields.insert("kind".to_string(), Value::String("impl".to_string()));
            fields.insert("modifier".to_string(), Value::String(modifier));
            fields.insert("name".to_string(), Value::String(name));
            fields.insert("annotation".to_string(), Value::String(annotation));
            fields.insert("extends".to_string(), Value::String(extends));
            fields.insert("children".to_string(), Value::List(children));
        }
        TmplNodeKind::Func => {
            let sig = sig_without_body(&node.text);
            let after_func = sig.trim_start_matches("func").trim_start();
            let sig_subst = substitute(after_func, args);
            let has_body = !node.children.is_empty();
            let children: Vec<Value> = node
                .children
                .iter()
                .map(|c| node_to_leaf(c, args))
                .collect();

            fields.insert("kind".to_string(), Value::String("func".to_string()));
            fields.insert("sig".to_string(), Value::String(sig_subst));
            fields.insert("has_body".to_string(), Value::Bool(has_body));
            fields.insert("children".to_string(), Value::List(children));
        }
        TmplNodeKind::Var => {
            let text = node.text.replace(":=", "=");
            let text = substitute(&text, args);
            let rest = text.trim_start_matches("var").trim_start();
            let (keyword, rest) = if rest.starts_with('[') {
                let end = rest.find(']').map(|i| i + 1).unwrap_or(1);
                let props = &rest[1..end.saturating_sub(1)];
                let kw = if props.contains("val") { "val" } else { "var" };
                (kw, rest[end..].trim_start())
            } else {
                ("var", rest)
            };
            fields.insert("kind".to_string(), Value::String("var".to_string()));
            fields.insert("keyword".to_string(), Value::String(keyword.to_string()));
            fields.insert("rest".to_string(), Value::String(rest.to_string()));
        }
        TmplNodeKind::Return => {
            let expr = node.text.trim_start_matches("return").trim_start();
            let expr = substitute(expr, args);
            fields.insert("kind".to_string(), Value::String("return".to_string()));
            fields.insert("expr".to_string(), Value::String(expr));
        }
        TmplNodeKind::For => {
            let sig = sig_without_body(&node.text);
            let after_for = sig.trim_start_matches("for").trim_start();
            let header = substitute(after_for, args);
            let children: Vec<Value> = node
                .children
                .iter()
                .map(|c| node_to_leaf(c, args))
                .collect();

            fields.insert("kind".to_string(), Value::String("for".to_string()));
            fields.insert("header".to_string(), Value::String(header));
            fields.insert("children".to_string(), Value::List(children));
        }
        TmplNodeKind::While => {
            let sig = sig_without_body(&node.text);
            let cond = substitute(sig.trim_start_matches("while").trim_start(), args);
            let children: Vec<Value> = node
                .children
                .iter()
                .map(|c| node_to_leaf(c, args))
                .collect();

            fields.insert("kind".to_string(), Value::String("while".to_string()));
            fields.insert("header".to_string(), Value::String(cond));
            fields.insert("children".to_string(), Value::List(children));
        }
        TmplNodeKind::DoWhile => {
            let condition = do_while_condition(&node.text, args);
            let children: Vec<Value> = node
                .children
                .iter()
                .map(|c| node_to_leaf(c, args))
                .collect();

            fields.insert("kind".to_string(), Value::String("do_while".to_string()));
            fields.insert("condition".to_string(), Value::String(condition));
            fields.insert("children".to_string(), Value::List(children));
        }
        TmplNodeKind::If => {
            let sig = sig_without_body(&node.text);
            let cond = substitute(sig.trim_start_matches("if").trim_start(), args);
            let then_children: Vec<Value> = node
                .children
                .iter()
                .map(|c| node_to_leaf(c, args))
                .collect();
            let after_first = text_after_first_block(&node.text).unwrap_or("");
            let else_chain = parse_else_chain_to_values(after_first, args);

            fields.insert("kind".to_string(), Value::String("if".to_string()));
            fields.insert("condition".to_string(), Value::String(cond));
            fields.insert("then_children".to_string(), Value::List(then_children));
            fields.insert("else_chain".to_string(), Value::List(else_chain));
        }
        TmplNodeKind::Comment => {
            let after_kw = node.text.trim().trim_start_matches("comment").trim_start();
            let text = after_kw.strip_prefix(':').unwrap_or(after_kw).trim_start();
            let text = substitute(text, args);
            fields.insert("kind".to_string(), Value::String("comment".to_string()));
            fields.insert("text".to_string(), Value::String(text));
        }
        TmplNodeKind::Raw => {
            // Strip the `raw:` prefix and emit the rest completely verbatim —
            // no variable substitution, no keyword interpretation.
            let after_kw = node
                .text
                .trim()
                .strip_prefix("raw")
                .unwrap_or("")
                .trim_start();
            let text = after_kw.strip_prefix(':').unwrap_or(after_kw).trim_start();
            fields.insert("kind".to_string(), Value::String("raw".to_string()));
            fields.insert("text".to_string(), Value::String(text.to_string()));
        }
        TmplNodeKind::Include => {
            let raw = node.text.trim();
            let call = raw
                .strip_prefix("<[")
                .and_then(|s| s.strip_suffix("]>"))
                .unwrap_or(raw)
                .trim();
            let call = substitute(call, args);
            fields.insert("kind".to_string(), Value::String("include".to_string()));
            fields.insert("call".to_string(), Value::String(call));
        }
        _ => {
            let text = substitute(node.text.trim(), args);
            fields.insert("kind".to_string(), Value::String("expr".to_string()));
            fields.insert("text".to_string(), Value::String(text));
        }
    }
    Value::Leaf(LeafObject::new(fields))
}

pub(crate) fn parse_else_chain_to_values(text: &str, args: &HashMap<String, String>) -> Vec<Value> {
    let text = text.trim_start();
    let mut result = Vec::new();
    if text.starts_with("else if") {
        let sig = sig_without_body(text);
        let cond = substitute(sig.trim_start_matches("else if").trim_start(), args);
        let children: Vec<Value> = parse_first_block_children(text)
            .iter()
            .map(|c| node_to_leaf(c, args))
            .collect();
        let mut fields = BTreeMap::new();
        fields.insert("kind".to_string(), Value::String("else_if".to_string()));
        fields.insert("condition".to_string(), Value::String(cond));
        fields.insert("children".to_string(), Value::List(children));
        result.push(Value::Leaf(LeafObject::new(fields)));
        let rest = text_after_first_block(text).unwrap_or("");
        result.extend(parse_else_chain_to_values(rest, args));
    } else if text.starts_with("else") {
        let block_text = text.trim_start_matches("else").trim_start();
        let children: Vec<Value> = parse_first_block_children(block_text)
            .iter()
            .map(|c| node_to_leaf(c, args))
            .collect();
        let mut fields = BTreeMap::new();
        fields.insert("kind".to_string(), Value::String("else".to_string()));
        fields.insert("children".to_string(), Value::List(children));
        result.push(Value::Leaf(LeafObject::new(fields)));
    }
    result
}

pub(crate) fn sig_without_body(text: &str) -> &str {
    let bytes = text.as_bytes();
    let mut idx = 0;
    while idx < bytes.len() {
        if bytes[idx] == b'{' && (idx == 0 || bytes[idx - 1] != b'$') {
            return text[..idx].trim_end();
        }
        idx += 1;
    }
    text.trim()
}

pub(crate) fn text_after_first_block(text: &str) -> Option<&str> {
    let start = find_first_block_start(text)?;
    let end = find_block_end(text, start)?;
    let rest = text[end + 1..].trim_start();
    if rest.is_empty() { None } else { Some(rest) }
}

pub(crate) fn substitute(text: &str, args: &HashMap<String, String>) -> String {
    // Replace `$$` with a placeholder first so that `$${name}` is not treated
    // as an interpolation of `name` — the double-dollar escapes to a literal `$`.
    const PLACEHOLDER: &str = "\x00TLANG_DOLLAR\x00";
    let escaped = text.replace("$$", PLACEHOLDER);
    let mut result = escaped;
    for (key, value) in args {
        result = result.replace(&format!("${{{}}}", key), value);
    }
    // Restore the escaped `$$` sequences as literal `$` characters.
    result.replace(PLACEHOLDER, "$")
}

pub(crate) fn do_while_condition(text: &str, args: &HashMap<String, String>) -> String {
    let bytes = text.as_bytes();
    let mut last_close = 0;
    let mut depth = 0i32;
    let mut in_string: Option<u8> = None;
    for (i, &ch) in bytes.iter().enumerate() {
        if let Some(q) = in_string {
            if ch == b'\\' {
                continue;
            }
            if ch == q {
                in_string = None;
            }
            continue;
        }
        match ch {
            b'"' | b'\'' => {
                in_string = Some(ch);
            }
            b'{' => depth += 1,
            b'}' => {
                depth -= 1;
                if depth == 0 {
                    last_close = i;
                }
            }
            _ => {}
        }
    }
    let tail = text[last_close + 1..].trim();
    let cond = tail.trim_start_matches("while").trim_start();
    substitute(cond, args)
}

pub(crate) fn parse_first_block_children(text: &str) -> Vec<TmplNodeTree> {
    use crate::ast::TemplateContent;
    use crate::tmpl_tree::{TmplContentTree, parse_tmpl_block_tree};

    let start = match find_first_block_start(text) {
        Some(s) => s,
        None => return vec![],
    };
    let end = match find_block_end(text, start) {
        Some(e) => e,
        None => return vec![],
    };
    let block = &text[start..=end];
    let content = TemplateContent::Full(block.to_string());
    match parse_tmpl_block_tree("_", "_", &[], &content) {
        Ok(tree) => match tree.content {
            TmplContentTree::Full(full) => full.nodes,
            _ => vec![],
        },
        Err(_) => vec![],
    }
}

pub(crate) fn find_first_block_start(text: &str) -> Option<usize> {
    let bytes = text.as_bytes();
    let mut in_string: Option<u8> = None;
    let mut i = 0;
    while i < bytes.len() {
        let ch = bytes[i];
        if let Some(q) = in_string {
            if ch == b'\\' && i + 1 < bytes.len() {
                i += 2;
                continue;
            }
            if ch == q {
                in_string = None;
            }
            i += 1;
            continue;
        }
        match ch {
            b'"' | b'\'' => {
                in_string = Some(ch);
                i += 1;
            }
            b'{' if i > 0 && bytes[i - 1] == b'$' => {
                i += 1;
            }
            b'{' => return Some(i),
            _ => {
                i += 1;
            }
        }
    }
    None
}

pub(crate) fn find_block_end(text: &str, start: usize) -> Option<usize> {
    let bytes = text.as_bytes();
    if bytes.get(start) != Some(&b'{') {
        return None;
    }
    let mut depth = 0i32;
    let mut in_string: Option<u8> = None;
    let mut i = start;
    while i < bytes.len() {
        let ch = bytes[i];
        if let Some(q) = in_string {
            if ch == b'\\' && i + 1 < bytes.len() {
                i += 2;
                continue;
            }
            if ch == q {
                in_string = None;
            }
            i += 1;
            continue;
        }
        match ch {
            b'"' | b'\'' => {
                in_string = Some(ch);
                i += 1;
            }
            b'{' => {
                depth += 1;
                i += 1;
            }
            b'}' => {
                depth -= 1;
                if depth == 0 {
                    return Some(i);
                }
                i += 1;
            }
            _ => {
                i += 1;
            }
        }
    }
    None
}

// ── top-level builders ────────────────────────────────────────────────────────

/// Parse the expanded template body string and args map into the structured
/// `package` + `nodes` fields that generators consume via `TLang.Leaf.get`.
///
/// `content` is the post-include-expansion raw body string (still wrapped in
/// `{ … }` braces — the same string stored in `TemplateContent::Full`).
/// `args` is a `param_name → string_value` map used for `${param}` substitution.
pub(crate) fn build_tmpl_tree_fields(
    content: &str,
    lang: &str,
    args: &HashMap<String, String>,
) -> Result<(String, Vec<Value>), String> {
    use crate::ast::TemplateContent;
    use crate::tmpl_tree::{TmplContentTree, parse_tmpl_block_tree};

    let tmpl_content = TemplateContent::Full(content.to_string());
    let tree =
        parse_tmpl_block_tree(lang, "_gen", &[], &tmpl_content).map_err(|e| e.to_string())?;

    match tree.content {
        TmplContentTree::Full(full) => {
            let pkg = full
                .package
                .as_ref()
                .map(|parts| {
                    parts
                        .iter()
                        .map(|p| substitute(p, args))
                        .collect::<Vec<_>>()
                        .join(".")
                })
                .unwrap_or_default();
            let nodes: Vec<Value> = full.nodes.iter().map(|n| node_to_leaf(n, args)).collect();
            Ok((pkg, nodes))
        }
        TmplContentTree::Specialized(spec) => {
            // Specialized blocks have a single content node; no package.
            Ok((String::new(), vec![node_to_leaf(&spec.node, args)]))
        }
    }
}

/// Parse a full template-instance value string (as stored in the model) into
/// `(lang, package, nodes)`.  Returns `None` if the string cannot be parsed.
pub(crate) fn build_tmpl_tree_fields_from_instance_str(
    instance_value: &str,
) -> Option<(String, String, Vec<Value>)> {
    let (args, body, lang) = parse_tmpl_instance_string(instance_value).ok()?;
    let (pkg, nodes) = build_tmpl_tree_fields(&body, &lang, &args).ok()?;
    Some((lang, pkg, nodes))
}

/// Parse a full template-instance value string (as stored in the model / `value` field)
/// into a structured Leaf with `kind = "ParsedTemplate"`, `package`, and `nodes`.
///
/// This is the Rust-level counterpart to the old `TLang.Kotlin.parse` built-in.
/// It is now used only by tests — at runtime the fields are pre-populated by
/// `instantiate_template` so generators can access them via `TLang.Leaf.get`.
#[cfg(test)]
pub(crate) fn parse_template_to_leaf(tmpl_str: &str) -> Result<Value, String> {
    use super::super::LeafObject;
    use std::collections::BTreeMap;
    let (args, body, lang) = parse_tmpl_instance_string(tmpl_str)?;
    let (pkg, nodes) = build_tmpl_tree_fields(&body, &lang, &args)?;
    let mut fields = BTreeMap::new();
    fields.insert(
        "kind".to_string(),
        Value::String("ParsedTemplate".to_string()),
    );
    fields.insert("package".to_string(), Value::String(pkg));
    fields.insert("nodes".to_string(), Value::List(nodes));
    Ok(Value::Leaf(LeafObject::new(fields)))
}

#[cfg(test)]
mod tests {
    use std::collections::BTreeMap;

    use super::*;
    use crate::runtime::{LeafObject, Value, call_in_file};

    fn codegen_path() -> std::path::PathBuf {
        std::path::Path::new(env!("CARGO_MANIFEST_DIR"))
            .join("generators")
            .join("kotlin")
            .join("KotlinCodegen.tlang")
    }

    /// Build a fully-populated `TmplLang` leaf from a raw template-instance string.
    /// Mirrors what `instantiate_template` does at runtime: the leaf carries both
    /// the raw `value` string and the pre-parsed `package` / `nodes` fields.
    fn make_tmpl_leaf(tmpl_str: &str) -> Value {
        let parsed = parse_template_to_leaf(tmpl_str).expect("make_tmpl_leaf: parse failed");
        let (pkg, nodes) = match &parsed {
            Value::Leaf(l) => (
                l.get("package")
                    .cloned()
                    .unwrap_or(Value::String(String::new())),
                l.get("nodes").cloned().unwrap_or(Value::List(Vec::new())),
            ),
            _ => (Value::String(String::new()), Value::List(Vec::new())),
        };
        let mut fields = BTreeMap::new();
        fields.insert(
            "kind".to_string(),
            Value::String("TemplateInstance".to_string()),
        );
        fields.insert("value".to_string(), Value::String(tmpl_str.to_string()));
        fields.insert("package".to_string(), pkg);
        fields.insert("nodes".to_string(), nodes);
        Value::Leaf(LeafObject::new(fields))
    }

    /// Run `generate_kotlin` in `KotlinCodegen.tlang` for the given raw template string.
    fn generate(tmpl_str: &str) -> String {
        let leaf = make_tmpl_leaf(tmpl_str);
        match call_in_file(&codegen_path(), "generate_kotlin", vec![leaf])
            .expect("generate_kotlin should succeed")
        {
            Value::String(s) => s,
            other => panic!("expected String, got {other:?}"),
        }
    }

    // ── parse helper unit tests ───────────────────────────────────────────────

    #[test]
    fn parses_template_args() {
        let m = parse_args("pkg=com.example.demo, className=UserService, annotation=@Component");
        assert_eq!(m.get("pkg").map(String::as_str), Some("com.example.demo"));
        assert_eq!(m.get("className").map(String::as_str), Some("UserService"));
        assert_eq!(m.get("annotation").map(String::as_str), Some("@Component"));
    }

    #[test]
    fn parses_empty_args() {
        assert!(parse_args("").is_empty());
    }

    #[test]
    fn sig_without_body_finds_opening_brace() {
        assert_eq!(sig_without_body("func foo() { return 1 }"), "func foo()");
    }

    #[test]
    fn sig_without_body_skips_interpolation_braces() {
        assert_eq!(
            sig_without_body("impl[public class] ${className} {"),
            "impl[public class] ${className}"
        );
    }

    #[test]
    fn find_block_end_finds_matching_brace() {
        let text = "foo { bar { baz } qux } rest";
        let start = find_first_block_start(text).unwrap();
        let end = find_block_end(text, start).unwrap();
        assert_eq!(&text[start..=end], "{ bar { baz } qux }");
    }

    #[test]
    fn text_after_first_block_returns_else_chain() {
        let text = "if (x) { return 1 } else { return 2 }";
        let rest = text_after_first_block(text).unwrap();
        assert!(rest.starts_with("else"), "got: {rest}");
    }

    // ── build_tmpl_tree_fields structural tests ───────────────────────────────

    #[test]
    fn parse_returns_package_and_nodes() {
        let tmpl = "lang [kotlin] t(pkg=com.example, className=Foo, annotation=@X, extendsClass=Base) {\n    pkg ${pkg}\n    impl[public class] ${className} {\n    }\n}";
        let leaf = make_tmpl_leaf(tmpl);
        // Access the pre-populated package / nodes from the leaf directly.
        let Value::Leaf(l) = &leaf else {
            panic!("expected leaf")
        };
        assert_eq!(
            l.get("package"),
            Some(&Value::String("com.example".to_string()))
        );
        let Value::List(nodes) = l.get("nodes").unwrap() else {
            panic!("nodes should be list")
        };
        assert_eq!(nodes.len(), 1);
        let Value::Leaf(impl_node) = &nodes[0] else {
            panic!("node should be leaf")
        };
        assert_eq!(
            impl_node.get("kind"),
            Some(&Value::String("impl".to_string()))
        );
        assert_eq!(
            impl_node.get("name"),
            Some(&Value::String("Foo".to_string()))
        );
        assert_eq!(
            impl_node.get("annotation"),
            Some(&Value::String("@X".to_string()))
        );
        assert_eq!(
            impl_node.get("extends"),
            Some(&Value::String("Base".to_string()))
        );
    }

    #[test]
    fn parse_func_node_has_sig_and_has_body() {
        let tmpl = "lang [kotlin] t() {\n    impl[public class] Foo {\n        func greet(who: String): String {\n            return \"Hi\"\n        }\n    }\n}";
        let leaf = make_tmpl_leaf(tmpl);
        let Value::Leaf(l) = &leaf else { panic!() };
        let Value::List(nodes) = l.get("nodes").unwrap() else {
            panic!()
        };
        let Value::Leaf(impl_node) = &nodes[0] else {
            panic!()
        };
        let Value::List(children) = impl_node.get("children").unwrap() else {
            panic!()
        };
        let Value::Leaf(func_node) = &children[0] else {
            panic!()
        };
        assert_eq!(
            func_node.get("kind"),
            Some(&Value::String("func".to_string()))
        );
        assert_eq!(
            func_node.get("sig"),
            Some(&Value::String("greet(who: String): String".to_string()))
        );
        assert_eq!(func_node.get("has_body"), Some(&Value::Bool(true)));
    }

    #[test]
    fn parse_var_node_keyword_and_rest() {
        let tmpl = "lang [kotlin] t() {\n    impl[public class] Foo {\n        var [val] maxRetries: Int := 3\n    }\n}";
        let leaf = make_tmpl_leaf(tmpl);
        let Value::Leaf(l) = &leaf else { panic!() };
        let Value::List(nodes) = l.get("nodes").unwrap() else {
            panic!()
        };
        let Value::Leaf(impl_node) = &nodes[0] else {
            panic!()
        };
        let Value::List(children) = impl_node.get("children").unwrap() else {
            panic!()
        };
        let Value::Leaf(var_node) = &children[0] else {
            panic!()
        };
        assert_eq!(
            var_node.get("kind"),
            Some(&Value::String("var".to_string()))
        );
        assert_eq!(
            var_node.get("keyword"),
            Some(&Value::String("val".to_string()))
        );
        assert_eq!(
            var_node.get("rest"),
            Some(&Value::String("maxRetries: Int = 3".to_string()))
        );
    }

    #[test]
    fn parse_if_node_has_condition_and_else_chain() {
        let tmpl = concat!(
            "lang [kotlin] t() {\n    impl[public class] Foo {\n        func f(): String {\n",
            r#"            if (n < 0) { return "neg" } else if (n == 0) { return "zero" } else { return "pos" }"#,
            "\n        }\n    }\n}"
        );
        let leaf = make_tmpl_leaf(tmpl);
        let Value::Leaf(top) = &leaf else { panic!() };
        let Value::List(nodes) = top.get("nodes").unwrap() else {
            panic!()
        };
        let Value::Leaf(impl_node) = &nodes[0] else {
            panic!()
        };
        let Value::List(impl_children) = impl_node.get("children").unwrap() else {
            panic!()
        };
        let Value::Leaf(func_node) = &impl_children[0] else {
            panic!()
        };
        let Value::List(func_children) = func_node.get("children").unwrap() else {
            panic!()
        };
        let Value::Leaf(if_node) = &func_children[0] else {
            panic!()
        };
        assert_eq!(if_node.get("kind"), Some(&Value::String("if".to_string())));
        assert_eq!(
            if_node.get("condition"),
            Some(&Value::String("(n < 0)".to_string()))
        );
        let Value::List(else_chain) = if_node.get("else_chain").unwrap() else {
            panic!()
        };
        assert_eq!(else_chain.len(), 2);
        let Value::Leaf(else_if) = &else_chain[0] else {
            panic!()
        };
        assert_eq!(
            else_if.get("kind"),
            Some(&Value::String("else_if".to_string()))
        );
        let Value::Leaf(else_node) = &else_chain[1] else {
            panic!()
        };
        assert_eq!(
            else_node.get("kind"),
            Some(&Value::String("else".to_string()))
        );
    }

    // ── KotlinCodegen.tlang integration tests ─────────────────────────────────

    #[test]
    fn generates_simple_class() {
        let tmpl = "lang [kotlin] t(annotation=@Service, extendsClass=Base) {\n    impl[public class] MyClass {\n    }\n}";
        let result = generate(tmpl);
        assert!(
            result.contains("@Service\nclass MyClass : Base {"),
            "result:\n{result}"
        );
        assert!(result.trim_end().ends_with('}'), "result:\n{result}");
    }

    #[test]
    fn generates_interface() {
        let tmpl = "lang [kotlin] t() {\n    impl[public interface] MyInterface {\n        func doSomething(x: Int)\n    }\n}";
        let result = generate(tmpl);
        assert!(
            result.contains("interface MyInterface {"),
            "result:\n{result}"
        );
        assert!(
            result.contains("fun doSomething(x: Int)"),
            "result:\n{result}"
        );
    }

    #[test]
    fn generates_package_declaration() {
        let tmpl = "lang [kotlin] t(pkg=com.example) {\n    pkg ${pkg}\n    impl[public class] Foo {\n    }\n}";
        let result = generate(tmpl);
        assert!(
            result.starts_with("package com.example\n\n"),
            "result:\n{result}"
        );
    }

    #[test]
    fn generates_var_and_val() {
        let tmpl = "lang [kotlin] t() {\n    impl[public class] Foo {\n        var count: Int = 0\n        var [val] max: Int := 10\n    }\n}";
        let result = generate(tmpl);
        assert!(result.contains("var count: Int = 0"), "result:\n{result}");
        assert!(result.contains("val max: Int = 10"), "result:\n{result}");
    }

    #[test]
    fn generates_func_with_return() {
        let tmpl = "lang [kotlin] t() {\n    impl[public class] Foo {\n        func greet(who: String): String {\n            return \"Hello, \" + who\n        }\n    }\n}";
        let result = generate(tmpl);
        assert!(
            result.contains("fun greet(who: String): String {"),
            "result:\n{result}"
        );
        assert!(
            result.contains("return \"Hello, \" + who"),
            "result:\n{result}"
        );
    }

    #[test]
    fn generates_for_loop() {
        let tmpl = "lang [kotlin] t() {\n    impl[public class] Foo {\n        func sum(n: Int): Int {\n            var total = 0\n            for (i in 1..n) {\n                total = total + i\n            }\n            return total\n        }\n    }\n}";
        let result = generate(tmpl);
        assert!(result.contains("for (i in 1..n) {"), "result:\n{result}");
        assert!(result.contains("total = total + i"), "result:\n{result}");
    }

    #[test]
    fn generates_if_else_chain() {
        let tmpl = concat!(
            "lang [kotlin] t() {\n    impl[public class] Foo {\n        func classify(n: Int): String {\n",
            "            if (n < 0) {\n                return \"neg\"\n            } else if (n == 0) {\n                return \"zero\"\n            } else {\n                return \"pos\"\n            }\n",
            "        }\n    }\n}"
        );
        let result = generate(tmpl);
        assert!(result.contains("if (n < 0) {"), "result:\n{result}");
        assert!(result.contains("} else if (n == 0) {"), "result:\n{result}");
        assert!(result.contains("} else {"), "result:\n{result}");
        assert!(result.contains("return \"neg\""), "result:\n{result}");
        assert!(result.contains("return \"zero\""), "result:\n{result}");
        assert!(result.contains("return \"pos\""), "result:\n{result}");
    }

    #[test]
    fn generates_full_class_with_all_features() {
        let tmpl = concat!(
            "lang [kotlin] svc(pkg=com.example, className=Svc, extendsClass=Base, annotation=@X) {\n",
            "    pkg ${pkg}\n",
            "    impl[public class] ${className} {\n",
            "        func classify(n: Int): String {\n",
            "            if (n < 0) {\n                return \"neg\"\n            } else {\n                return \"pos\"\n            }\n",
            "        }\n",
            "    }\n",
            "}"
        );
        let result = generate(tmpl);
        assert!(result.starts_with("package com.example"), "pkg:\n{result}");
        assert!(
            result.contains("@X\nclass Svc : Base {"),
            "class:\n{result}"
        );
        assert!(
            result.contains("fun classify(n: Int): String {"),
            "func:\n{result}"
        );
        assert!(result.contains("if (n < 0) {"), "if:\n{result}");
        assert!(result.contains("} else {"), "else:\n{result}");
    }

    #[test]
    fn generates_do_while_loop() {
        let tmpl = "lang [kotlin] t() {\n    impl[public class] Foo {\n        func run(): Int {\n            var x = 0\n            do {\n                x = x + 1\n            } while (x < 3)\n            return x\n        }\n    }\n}";
        let result = generate(tmpl);
        assert!(result.contains("do {"), "result:\n{result}");
        assert!(result.contains("} while (x < 3)"), "result:\n{result}");
    }

    #[test]
    fn generates_while_loop() {
        let tmpl = "lang [kotlin] t() {\n    impl[public class] Foo {\n        func run(): Int {\n            var x = 0\n            while (x < 3) {\n                x = x + 1\n            }\n            return x\n        }\n    }\n}";
        let result = generate(tmpl);
        assert!(result.contains("while (x < 3) {"), "result:\n{result}");
        assert!(result.contains("x = x + 1"), "result:\n{result}");
    }

    #[test]
    fn generates_uses_token_and_formatting_not_stringbuilder() {
        // Regression guard: the Kotlin codegen must use TLang.Token / TLang.Formatting
        // and must NOT fall back to raw string concatenation.
        let tmpl = "lang [kotlin] t() {\n    impl[public class] Foo {\n        func greet(): String {\n            return \"Hello\"\n        }\n    }\n}";
        let result = generate(tmpl);
        // Proper indentation is only produced by the token/formatting pipeline.
        assert!(
            result.contains("    fun greet(): String {"),
            "expected 4-space indent from formatter:\n{result}"
        );
        assert!(
            result.contains("        return \"Hello\""),
            "expected 8-space indent from formatter:\n{result}"
        );
    }
}
