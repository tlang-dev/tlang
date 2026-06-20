// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Canonical TLang source formatter.
//!
//! Re-serialises a [`crate::ast::DomainModel`] back into well-formatted TLang
//! source text. Used by the LSP to implement `textDocument/formatting`.
//!
//! The formatter normalises:
//! - `expose` and `use` declarations (one per line)
//! - Blank-line separation between header and body blocks
//! - `model` block content (whitespace, indentation, attribute alignment)
//!
//! Helper and template block bodies are reproduced verbatim because they
//! contain arbitrary target-language code that cannot be reformatted without
//! a language-specific formatter.

use crate::ast::{
    DocTemplateBlock, DomainBlock, DomainModel, DomainUse, ModelBlock, RawTemplateBlock,
    StyleTemplateBlock, TemplateBlock, TemplateContent, TemplateParam,
};
use crate::model_tree::{
    ModelAssignVarTree, ModelBlockTree, ModelNodeTree, ModelSetAttributeTree, ModelSetEntityTree,
    ModelValueTypeTree, parse_model_block_tree,
};
use crate::tmpl_tree::{TmplContentTree, TmplFullTree, TmplNodeTree, parse_tmpl_block_tree};

/// Format a [`DomainModel`] back into canonical TLang source text.
///
/// The formatter produces a deterministic, human-readable representation:
///
/// - Each `expose` and `use` directive appears on its own line.
/// - A blank line separates the header from the first body block.
/// - Body blocks (`helper`, `model`, `lang`) are each separated by a blank
///   line.
/// - `helper` and `lang` block bodies are reproduced verbatim (their content
///   is arbitrary code or target-language text that cannot be reformatted
///   without a language-specific formatter).
/// - `model` block bodies are fully re-serialised from the parsed AST,
///   normalising whitespace, indentation and attribute alignment.
pub fn format_model(model: &DomainModel) -> String {
    let mut out = String::new();

    // Header
    for expose in &model.header.exposes {
        out.push_str(&format!("expose {expose}\n"));
    }
    for use_decl in &model.header.uses {
        out.push_str(&format!("{}\n", format_use(use_decl)));
    }

    // Body blocks
    for block in &model.body {
        if !out.is_empty() && !out.ends_with("\n\n") {
            out.push('\n');
        }
        match block {
            DomainBlock::Helper(h) => {
                // Content is wrapped as "{\n<raw_func_decls>\n}" — strip outer braces.
                let inner = h
                    .content
                    .strip_prefix("{\n")
                    .and_then(|s| s.strip_suffix("\n}"))
                    .unwrap_or(&h.content);
                out.push_str(inner);
                out.push('\n');
            }
            DomainBlock::Model(m) => {
                out.push_str(&format_model_block_toplevel(m));
                out.push('\n');
            }
            DomainBlock::Template(t) => {
                let params = format_params(&t.params);
                let sig = if params.is_empty() {
                    format!("lang [{}] {}", t.lang, t.name)
                } else {
                    format!("lang [{}] {}({})", t.lang, t.name, params)
                };
                let body = format_template_content(t);
                out.push_str(&format!("{sig} {body}\n"));
            }
            DomainBlock::Data(d) => {
                let params = format_params(&d.params);
                let langs = d.langs.join(", ");
                let sig = if params.is_empty() {
                    format!("data [{}] {}", langs, d.name)
                } else {
                    format!("data [{}] {}({})", langs, d.name, params)
                };
                out.push_str(&format!("{sig} {}\n", d.content));
            }
            DomainBlock::Cmd(c) => {
                let params = format_params(&c.params);
                let langs = c.langs.join(", ");
                let sig = if params.is_empty() {
                    format!("cmd [{}] {}", langs, c.name)
                } else {
                    format!("cmd [{}] {}({})", langs, c.name, params)
                };
                out.push_str(&format!("{sig} {}\n", c.content));
            }
            DomainBlock::Raw(r) => {
                out.push_str(&format_raw_block(r));
                out.push('\n');
            }
            DomainBlock::Doc(d) => {
                out.push_str(&format_doc_block(d));
                out.push('\n');
            }
            DomainBlock::Style(s) => {
                out.push_str(&format_style_block(s));
                out.push('\n');
            }
            DomainBlock::Test(t) => {
                out.push_str(&format!("test {} {}\n", t.name, t.content));
            }
        }
    }

    out
}

// ---------------------------------------------------------------------------
// Raw block formatter
// ---------------------------------------------------------------------------

fn format_style_block(s: &StyleTemplateBlock) -> String {
    let params = format_params(&s.params);
    let langs = s.langs.join(", ");
    let sig = if params.is_empty() {
        format!("style [{}] {}", langs, s.name)
    } else {
        format!("style [{}] {}({})", langs, s.name, params)
    };
    format!("{sig} {}", s.content)
}

fn format_doc_block(d: &DocTemplateBlock) -> String {
    let params = format_params(&d.params);
    let langs = d.langs.join(", ");
    let sig = if params.is_empty() {
        format!("doc [{}] {}", langs, d.name)
    } else {
        format!("doc [{}] {}({})", langs, d.name, params)
    };
    format!("{sig} {}", d.content)
}

fn format_raw_block(r: &RawTemplateBlock) -> String {
    let params = format_params(&r.params);
    let sig = if params.is_empty() {
        format!("raw [{}] {}", r.variant, r.name)
    } else {
        format!("raw [{}] {}({})", r.variant, r.name, params)
    };
    // The content already includes the surrounding `{ }` (it is a
    // balanced_block), so emit it verbatim without any reformatting.
    format!("{sig} {}", r.content)
}

// ---------------------------------------------------------------------------
// Template block body formatter
// ---------------------------------------------------------------------------

/// Format the body of a `lang [...]` template block by round-tripping through
/// the template AST.  Normalises indentation to 4 spaces per depth level and
/// strips extraneous whitespace from node headers while preserving all
/// target-language content verbatim.
///
/// Falls back to the verbatim body text on any parse error.
fn format_template_content(t: &TemplateBlock) -> String {
    match parse_tmpl_block_tree(&t.lang, &t.name, &t.params, &t.content) {
        Ok(tree) => match &tree.content {
            TmplContentTree::Full(full) => format_tmpl_full(full),
            TmplContentTree::Specialized(spec) => {
                let mut body = String::from("spec {\n");
                format_tmpl_node_into(&mut body, &spec.node, 1);
                body.push('\n');
                body.push('}');
                body
            }
        },
        // Parse error: return verbatim body so the formatter never corrupts source.
        Err(_) => match &t.content {
            TemplateContent::Full(body) => body.clone(),
            TemplateContent::Specialized(body) => format!("spec {body}"),
        },
    }
}

fn format_tmpl_full(full: &TmplFullTree) -> String {
    let mut out = String::from("{\n");

    if let Some(pkg) = &full.package {
        out.push_str(&format!("    pkg {}\n", pkg.join(".")));
    }
    for u in &full.uses {
        let alias = u
            .alias
            .as_ref()
            .map(|a| format!(" as {a}"))
            .unwrap_or_default();
        out.push_str(&format!("    use {}{alias}\n", u.path.join(".")));
    }

    // Blank line between header directives and node list.
    if (full.package.is_some() || !full.uses.is_empty()) && !full.nodes.is_empty() {
        out.push('\n');
    }

    for node in &full.nodes {
        format_tmpl_node_into(&mut out, node, 1);
        out.push('\n');
    }

    out.push('}');
    out
}

/// Append a single template node to `out` at the given indentation depth.
fn format_tmpl_node_into(out: &mut String, node: &TmplNodeTree, depth: usize) {
    let indent = "    ".repeat(depth);
    let head = tmpl_node_header(&node.text);
    let has_body = head.len() < node.text.trim_end().len();

    if !has_body {
        // Leaf node: no `{…}` body — just print the trimmed text.
        out.push_str(&indent);
        out.push_str(node.text.trim());
    } else if node.children.is_empty() {
        // Block node with an empty body.
        out.push_str(&indent);
        out.push_str(head.trim_end());
        out.push_str(" {}");
    } else {
        // Block node with children.
        out.push_str(&indent);
        out.push_str(head.trim_end());
        out.push_str(" {\n");
        for child in &node.children {
            format_tmpl_node_into(out, child, depth + 1);
            out.push('\n');
        }
        out.push_str(&indent);
        out.push('}');
    }
}

/// Return the slice of `text` that precedes the first top-level `{` block
/// delimiter (i.e. a `{` that is not part of a `${…}` interpolation and is
/// not inside `(…)` or `[…]`).
///
/// If no such `{` is found the entire `text` slice is returned, indicating
/// the node has no body.
fn tmpl_node_header(text: &str) -> &str {
    let bytes = text.as_bytes();
    let mut i = 0usize;
    let mut depth = 0i32;
    while i < bytes.len() {
        match bytes[i] {
            // `${...}` interpolation — skip without treating `{` as a block.
            b'$' if i + 1 < bytes.len() && bytes[i + 1] == b'{' => {
                i += 2;
                while i < bytes.len() && bytes[i] != b'}' {
                    i += 1;
                }
                if i < bytes.len() {
                    i += 1; // skip closing `}`
                }
            }
            b'(' | b'[' => {
                depth += 1;
                i += 1;
            }
            b')' | b']' => {
                depth -= 1;
                i += 1;
            }
            b'{' if depth == 0 => return &text[..i],
            _ => i += 1,
        }
    }
    text
}

// ---------------------------------------------------------------------------
// Model block internal formatter
// ---------------------------------------------------------------------------

/// Format the nodes of a top-level `set`/`let` block without any `model {}`
/// wrapper.  Each node is emitted at zero indentation, with a blank line
/// between nodes.
fn format_model_block_toplevel(m: &ModelBlock) -> String {
    let tree = match parse_model_block_tree(m) {
        Ok(t) => t,
        Err(_) => {
            // Fallback: strip the synthetic wrapper and return the inner text.
            return m
                .content
                .strip_prefix("{\n")
                .and_then(|s| s.strip_suffix("\n}"))
                .unwrap_or(&m.content)
                .to_string();
        }
    };

    let last = tree.nodes.len().saturating_sub(1);
    let mut out = String::new();
    for (i, node) in tree.nodes.iter().enumerate() {
        match node {
            ModelNodeTree::AssignVar(assign) => {
                out.push_str(&format_assign_var(assign, ""));
            }
            ModelNodeTree::SetEntity(set) => {
                out.push_str(&format_set_entity(set, ""));
            }
        }
        out.push('\n');
        if i < last {
            out.push('\n');
        }
    }
    // Trim trailing newline — the caller appends its own.
    if out.ends_with('\n') {
        out.pop();
    }
    out
}

fn format_model_tree(tree: &ModelBlockTree) -> String {
    if tree.nodes.is_empty() {
        return "{}".to_string();
    }

    let mut out = String::from("{\n");
    let last = tree.nodes.len() - 1;

    for (i, node) in tree.nodes.iter().enumerate() {
        match node {
            ModelNodeTree::AssignVar(assign) => {
                out.push_str(&format_assign_var(assign, "    "));
            }
            ModelNodeTree::SetEntity(set) => {
                out.push_str(&format_set_entity(set, "    "));
            }
        }
        out.push('\n');
        // Blank line between top-level nodes, but not after the last one.
        if i < last {
            out.push('\n');
        }
    }

    out.push('}');
    out
}

fn format_assign_var(assign: &ModelAssignVarTree, indent: &str) -> String {
    match &assign.ty {
        Some(ty) => format!(
            "{indent}let {}: {} = {}",
            assign.name,
            format_value_type(ty),
            assign.value
        ),
        None => format!("{indent}let {} = {}", assign.name, assign.value),
    }
}

fn format_set_entity(set: &ModelSetEntityTree, indent: &str) -> String {
    let mut out = format!("{indent}set {}", set.name);

    for ext in &set.exts {
        out.push_str(" : ");
        out.push_str(ext);
    }

    if !set.params.is_empty() {
        out.push('(');
        let parts: Vec<String> = set.params.iter().map(format_attribute).collect();
        out.push_str(&parts.join(", "));
        out.push(')');
    }

    if let Some(output) = &set.output {
        let op = match output.mode {
            crate::model_tree::WriteMode::AlwaysWrite => ">>",
            crate::model_tree::WriteMode::WriteOnce => ">>?",
        };
        let escaped = output
            .path
            .replace('\\', "\\\\")
            .replace('"', "\\\"")
            .replace('\n', "\\n");
        out.push_str(&format!(" {op} \"{escaped}\""));
    }

    if let Some(exec) = &set.exec {
        out.push_str(&format!(" !> {}", exec.executor));
    }

    out.push(' ');

    if set.attrs.is_empty() {
        out.push_str("{}");
    } else {
        let attr_indent = format!("{indent}    ");
        out.push_str("{\n");
        let last = set.attrs.len() - 1;
        for (i, attr) in set.attrs.iter().enumerate() {
            out.push_str(&attr_indent);
            out.push_str(&format_attribute(attr));
            if i < last {
                out.push(',');
            }
            out.push('\n');
        }
        out.push_str(indent);
        out.push('}');
    }

    out
}

fn format_attribute(attr: &ModelSetAttributeTree) -> String {
    match &attr.attr {
        Some(name) => format!("{}: {}", name, format_value_type(&attr.value)),
        None => format_value_type(&attr.value),
    }
}

fn format_value_type(vt: &ModelValueTypeTree) -> String {
    match vt {
        ModelValueTypeTree::Type(s) => s.clone(),
        ModelValueTypeTree::Array(s) => format!("{s}[]"),
        ModelValueTypeTree::Generic { name, params } => format!("{name}<{}>", params.join(", ")),
        ModelValueTypeTree::ImplArray => "impl[]".to_string(),
        ModelValueTypeTree::FuncDef {
            param_types,
            ret_types,
        } => {
            let params = param_types.join(", ");
            if ret_types.is_empty() {
                format!("({params})")
            } else {
                format!("({params}):({})", ret_types.join(", "))
            }
        }
        ModelValueTypeTree::Ref { path, currying } => {
            use crate::model_tree::RefArg;
            let mut out = format!("&{}", path.join("."));
            for args in currying {
                out.push('(');
                let parts: Vec<String> = args
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
                out.push_str(&parts.join(", "));
                out.push(')');
            }
            out
        }
        ModelValueTypeTree::Impl { attrs } => {
            if attrs.is_empty() {
                "impl {}".to_string()
            } else {
                let inner: Vec<String> = attrs.iter().map(format_attribute).collect();
                format!("impl {{ {} }}", inner.join(", "))
            }
        }
        ModelValueTypeTree::StringLiteral(s) => {
            // Re-escape the decoded string back to a quoted literal.
            let escaped = s
                .replace('\\', "\\\\")
                .replace('"', "\\\"")
                .replace('\n', "\\n")
                .replace('\t', "\\t");
            format!("\"{escaped}\"")
        }
        ModelValueTypeTree::IntLiteral(n) => n.to_string(),
        ModelValueTypeTree::BoolLiteral(b) => b.to_string(),
        ModelValueTypeTree::ArrayLiteral(items) => {
            let inner: Vec<String> = items.iter().map(format_value_type).collect();
            format!("[{}]", inner.join(", "))
        }
    }
}

// ---------------------------------------------------------------------------
// Shared helpers
// ---------------------------------------------------------------------------

fn format_use(u: &DomainUse) -> String {
    let path = u.path.join(".");
    match &u.alias {
        Some(alias) => format!("use {path} as {alias}"),
        None => format!("use {path}"),
    }
}

fn format_params(params: &[TemplateParam]) -> String {
    params
        .iter()
        .map(|p| {
            if p.ty.is_empty() {
                p.name.clone()
            } else {
                format!("{}: {}", p.name, p.ty)
            }
        })
        .collect::<Vec<_>>()
        .join(", ")
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use super::*;
    use crate::parser::parse_domain_model;

    #[test]
    fn format_roundtrip_header() {
        let input = "expose Foo\nuse a.b as ab\nuse c.d\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("expose Foo"));
        assert!(formatted.contains("use a.b as ab"));
        assert!(formatted.contains("use c.d"));
    }

    #[test]
    fn format_template_block() {
        let input = "lang [rust] service(name: String) { fn build() {} }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("lang [rust] service(name: String)"));
        assert!(formatted.contains("fn build()"));
    }

    #[test]
    fn format_specialized_template() {
        let input = "lang [go] handler spec { include <[x()]> }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("lang [go] handler spec"));
    }

    #[test]
    fn format_model_block() {
        let input = "set Foo(x: i32) {}\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("set Foo(x: i32) {}"));
        assert!(formatted.contains("set Foo(x: i32) {}"));
    }

    #[test]
    fn format_model_block_normalises_internal_whitespace() {
        // Extra spaces around colon and inside body should be normalised.
        let input = "set   User  {  name :  String  ,  age :  Number  }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        // Attribute names and types are separated by exactly one space after colon.
        assert!(formatted.contains("name: String"));
        assert!(formatted.contains("age: Number"));
        // The entity header is on one line.
        assert!(formatted.contains("set User {"));
    }

    #[test]
    fn format_model_block_with_ext_and_array() {
        let input = "set Order : User { items: String[] }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("set Order : User {"));
        assert!(formatted.contains("items: String[]"));
    }

    #[test]
    fn format_model_block_with_multi_parent() {
        let input = "set Combo : A : B : C { label: String }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(
            formatted.contains("set Combo : A : B : C {"),
            "expected multi-parent formatting:\n{formatted}"
        );
    }

    #[test]
    fn format_model_block_with_output_decl() {
        let input = "set Repo(pkg: String) >> \"out/${pkg}.kt\" { }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(
            formatted.contains(">> \"out/${pkg}.kt\""),
            "expected >> output decl in formatted output:\n{formatted}"
        );
    }

    #[test]
    fn format_model_block_with_write_once_decl() {
        let input = "set Scaffold(pkg: String) >>? \"out/${pkg}.kt\" { }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(
            formatted.contains(">>? \"out/${pkg}.kt\""),
            "expected >>? output decl in formatted output:\n{formatted}"
        );
    }

    #[test]
    fn format_model_block_with_string_literal() {
        let input = "set Cfg { pkg: \"com.example\" }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("pkg: \"com.example\""));
    }

    #[test]
    fn format_model_block_with_impl_type() {
        let input = "set S { config: impl { port: Number } }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("config: impl { port: Number }"));
    }

    #[test]
    fn format_model_block_with_ref_type() {
        let input = "set S { dep: &core.lookup(&name) }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("dep: &core.lookup(&name)"));
    }

    #[test]
    fn format_model_block_with_func_def_type() {
        let input = "set S { callback: (String, Number):(Bool) }\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("callback: (String, Number):(Bool)"));
    }

    #[test]
    fn format_model_block_assign_var() {
        let input = "let seed: Number = 42\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("let seed: Number = 42"));
    }

    #[test]
    fn format_whole_document_with_all_block_types() {
        let input = concat!(
            "expose Api\n",
            "use core.helpers as helpers\n",
            "\n",
            "func trim(v: String) {\n",
            "    let a = v\n",
            "}\n",
            "\n",
            "lang [rust] service(name: String) { fn build() {} }\n",
            "\n",
            "set User { name: String }\n",
        );
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("expose Api"));
        assert!(formatted.contains("use core.helpers as helpers"));
        assert!(formatted.contains("func trim(v: String)"));
        assert!(formatted.contains("lang [rust] service(name: String)"));
        assert!(formatted.contains("set User {"));
        assert!(formatted.contains("name: String"));
    }

    #[test]
    fn format_toplevel_func_no_helper_wrapper() {
        let input = "use TLang.Terminal\n\nfunc main(): String {\n    return \"ok\"\n}\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(
            !formatted.contains("helper"),
            "formatter must not add 'helper' wrapper: {formatted}"
        );
        assert!(
            formatted.contains("func main(): String {"),
            "func declaration must be preserved: {formatted}"
        );
    }

    #[test]
    fn format_toplevel_set_no_model_wrapper() {
        let input = "set User(name: String) {}\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(
            !formatted.contains("model"),
            "formatter must not add 'model' wrapper: {formatted}"
        );
        assert!(
            formatted.contains("set User(name: String) {}"),
            "set declaration must be preserved: {formatted}"
        );
    }

    #[test]
    fn format_toplevel_set_with_interpolated_output_path() {
        let input = "set Repo(pkg: String) >> \"out/${pkg}.kt\" {\n    lead: &lead\n}\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(!formatted.contains("model"), "no model wrapper");
        assert!(
            formatted.contains(">> \"out/${pkg}.kt\""),
            "output path preserved: {formatted}"
        );
    }

    #[test]
    fn format_toplevel_roundtrip() {
        let input =
            "use TLang.Terminal\n\nset User(name: String) {}\n\nfunc main(): String {\n    return \"ok\"\n}\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        let model2 = parse_domain_model(&formatted).expect("re-parses");
        let formatted2 = format_model(&model2);
        assert_eq!(formatted, formatted2, "formatter is idempotent");
    }

    #[test]
    fn format_normalises_block_separators() {
        // Three blank lines between blocks should be collapsed to one.
        let input = "use TLang.Terminal\n\n\n\nfunc f() {}\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        // There must be exactly one blank line between the use directive and the func.
        assert!(formatted.contains("use TLang.Terminal\n\nfunc f()"));
    }

    #[test]
    fn format_empty_model_block() {
        // A model block with no statements should produce `{}`.
        // (Grammar requires balanced_block so this produces an empty tree.)
        let model = parse_domain_model("set Empty {}\n").expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("set Empty {}"));
    }

    // -----------------------------------------------------------------------
    // Template body formatting
    // -----------------------------------------------------------------------

    #[test]
    fn format_template_body_normalises_indentation() {
        // Over-indented body should be re-indented to 4 spaces per level.
        let input = concat!(
            "lang [kotlin] entity(className: String) {\n",
            "        impl[public class] ${className} {\n",
            "            var id: Long\n",
            "            var name: String\n",
            "        }\n",
            "}\n",
        );
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        // impl node at depth 1 → 4 spaces
        assert!(
            formatted.contains("    impl[public class] ${className} {"),
            "impl should be indented 4 spaces; got:\n{formatted}"
        );
        // var nodes at depth 2 → 8 spaces
        assert!(
            formatted.contains("        var id: Long"),
            "var should be indented 8 spaces; got:\n{formatted}"
        );
    }

    #[test]
    fn format_template_body_preserves_pkg_and_use() {
        let input = concat!(
            "lang [java] svc(pkg: String) {\n",
            "    pkg ${pkg}\n",
            "    use java.util.List\n",
            "    impl[public class] Service {}\n",
            "}\n",
        );
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("    pkg ${pkg}"), "pkg preserved");
        assert!(
            formatted.contains("    use java.util.List"),
            "use preserved"
        );
        assert!(
            formatted.contains("    impl[public class] Service {}"),
            "empty impl body"
        );
    }

    #[test]
    fn format_specialized_template_body() {
        let input = "lang [kotlin] field spec {    var ${decl}\n}\n";
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("spec {"), "spec keyword present");
        assert!(formatted.contains("var ${decl}"), "content preserved");
    }

    #[test]
    fn format_template_body_nested_blocks() {
        let input = concat!(
            "lang [kotlin] cls {\n",
            "    impl[public class] Outer {\n",
            "        impl[inner class] Inner {\n",
            "            var x: Int\n",
            "        }\n",
            "    }\n",
            "}\n",
        );
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        // Outer at depth 1 (4 spaces), Inner at depth 2 (8 spaces), var at depth 3 (12 spaces)
        assert!(formatted.contains("    impl[public class] Outer {"));
        assert!(formatted.contains("        impl[inner class] Inner {"));
        assert!(formatted.contains("            var x: Int"));
    }

    #[test]
    fn format_template_body_func_with_return() {
        let input = concat!(
            "lang [java] bean {\n",
            "    impl[public class] Bean {\n",
            "        func[public] String getName() {\n",
            "            return name\n",
            "        }\n",
            "    }\n",
            "}\n",
        );
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(formatted.contains("        func[public] String getName() {"));
        assert!(formatted.contains("            return name"));
    }

    #[test]
    fn format_template_body_include_preserved() {
        let input = concat!(
            "lang [kotlin] list {\n",
            "    impl[public class] MyList {\n",
            "        <[ renderFields(fields) ]>\n",
            "    }\n",
            "}\n",
        );
        let model = parse_domain_model(input).expect("parses");
        let formatted = format_model(&model);
        assert!(
            formatted.contains("<[ renderFields(fields) ]>"),
            "include directive preserved"
        );
    }

    // -----------------------------------------------------------------------
    // tmpl_node_header helper
    // -----------------------------------------------------------------------

    #[test]
    fn tmpl_node_header_stops_at_brace() {
        assert_eq!(tmpl_node_header("impl Foo {"), "impl Foo ");
    }

    #[test]
    fn tmpl_node_header_skips_interpolation_braces() {
        assert_eq!(tmpl_node_header("impl ${cls} {"), "impl ${cls} ");
    }

    #[test]
    fn tmpl_node_header_no_brace_returns_whole() {
        assert_eq!(tmpl_node_header("var id: Long"), "var id: Long");
    }

    #[test]
    fn tmpl_node_header_paren_depth() {
        // A `{` inside a method signature default-arg should not be the block opener.
        // (In practice this doesn't occur in TLang templates, but guards correctness.)
        assert_eq!(
            tmpl_node_header("func f(x: Map<K,V>) {"),
            "func f(x: Map<K,V>) "
        );
    }
}
