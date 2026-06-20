// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Parsed AST for `lang [target] Name(params) { … }` code-generation template blocks.
//!
//! A `lang` template block describes how to emit code in a specific target
//! language (e.g. Kotlin, Java, TypeScript). The body is parsed into a tree of
//! [`TmplNodeTree`] nodes representing text fragments, parameter references,
//! include calls (`<[ call ]>`), conditionals, and loops.

use crate::ast::{TemplateContent, TemplateParam};
use crate::tree_context::TreeContext;

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct TmplBlockTree {
    pub lang: String,
    pub name: String,
    pub params: Vec<TemplateParam>,
    pub content: TmplContentTree,
    pub context: TreeContext,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum TmplContentTree {
    Full(TmplFullTree),
    Specialized(TmplSpecializedTree),
}

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct TmplFullTree {
    pub package: Option<Vec<String>>,
    pub uses: Vec<TmplUseTree>,
    pub nodes: Vec<TmplNodeTree>,
    pub context: TreeContext,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct TmplSpecializedTree {
    pub node: TmplNodeTree,
    pub context: TreeContext,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct TmplUseTree {
    pub path: Vec<String>,
    pub alias: Option<String>,
    pub context: TreeContext,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct TmplNodeTree {
    pub kind: TmplNodeKind,
    /// Raw source text of this node (including signature and body if any).
    pub text: String,
    /// Inline modifier from bracket notation, e.g. `impl[public interface]` → `"public interface"`.
    pub modifier: Option<String>,
    pub children: Vec<TmplNodeTree>,
    pub context: TreeContext,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum TmplNodeKind {
    Impl,
    Func,
    Var,
    If,
    For,
    While,
    DoWhile,
    Return,
    Include,
    Comment,
    Raw,
    Attribute,
    SetAttribute,
    Param,
    Operation,
    Unknown,
}

#[derive(Debug)]
pub struct TmplTreeError(pub String);

impl std::fmt::Display for TmplTreeError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.0)
    }
}

impl std::error::Error for TmplTreeError {}

pub fn parse_tmpl_block_tree(
    lang: &str,
    name: &str,
    params: &[TemplateParam],
    content: &TemplateContent,
) -> Result<TmplBlockTree, TmplTreeError> {
    let content = match content {
        TemplateContent::Full(block) => TmplContentTree::Full(parse_full_block(block)?),
        TemplateContent::Specialized(block) => {
            TmplContentTree::Specialized(parse_specialized_block(block)?)
        }
    };

    Ok(TmplBlockTree {
        lang: lang.to_string(),
        name: name.to_string(),
        params: params.to_vec(),
        content,
        context: TreeContext::default(),
    })
}

fn parse_full_block(block: &str) -> Result<TmplFullTree, TmplTreeError> {
    let inner = strip_outer_braces(block)?;
    let items = split_top_level_items(inner)?;
    let mut out = TmplFullTree {
        context: TreeContext::from_offset(inner, 0),
        ..TmplFullTree::default()
    };

    for item in items {
        let trimmed = item.text.trim();
        if trimmed.is_empty() {
            continue;
        }

        let kind = detect_kind(trimmed);
        match kind {
            TmplNodeKind::Unknown if trimmed.starts_with("pkg ") || trimmed == "pkg" => {
                out.package = Some(parse_path_after_keyword(trimmed, "pkg"));
            }
            TmplNodeKind::Unknown if trimmed.starts_with("use ") || trimmed == "use" => {
                out.uses.push(parse_use(trimmed, item.start, inner));
            }
            _ => out.nodes.push(parse_node(trimmed, item.start, inner)?),
        }
    }

    Ok(out)
}

fn parse_specialized_block(block: &str) -> Result<TmplSpecializedTree, TmplTreeError> {
    let inner = strip_outer_braces(block)?;
    let items = split_top_level_items(inner)?;
    let text = items
        .iter()
        .map(|item| item.text.as_str())
        .collect::<Vec<_>>()
        .join("\n")
        .trim()
        .to_string();
    if text.is_empty() {
        return Err(TmplTreeError("specialized tmpl block is empty".to_string()));
    }
    let first_start = items.first().map(|item| item.start).unwrap_or(0);
    Ok(TmplSpecializedTree {
        node: parse_node(&text, first_start, inner)?,
        context: TreeContext::from_offset(inner, first_start),
    })
}

fn parse_node(
    text: &str,
    start_offset: usize,
    source: &str,
) -> Result<TmplNodeTree, TmplTreeError> {
    let kind = detect_kind(text);
    let modifier = extract_modifier(text);
    let mut children = Vec::new();
    if let Some((start, end)) = find_first_top_level_block(text)? {
        let body = &text[start + 1..end];
        for child in split_top_level_items(body)? {
            let child_text = child.text.trim();
            if !child_text.is_empty() {
                children.push(parse_node(
                    child_text,
                    start_offset + start + 1 + child.start,
                    source,
                )?);
            }
        }
    }
    Ok(TmplNodeTree {
        kind,
        text: text.to_string(),
        modifier,
        children,
        context: TreeContext::from_offset(source, start_offset),
    })
}

fn parse_use(line: &str, start_offset: usize, source: &str) -> TmplUseTree {
    let without_keyword = line.trim().strip_prefix("use").unwrap_or("").trim();
    let mut alias = None;
    let path_part = if let Some((path, alias_part)) = without_keyword.split_once(" as ") {
        alias = Some(alias_part.trim().to_string());
        path.trim()
    } else {
        without_keyword
    };

    let path = path_part
        .split('.')
        .map(str::trim)
        .filter(|s| !s.is_empty())
        .map(ToOwned::to_owned)
        .collect();

    TmplUseTree {
        path,
        alias,
        context: TreeContext::from_offset(source, start_offset),
    }
}

fn parse_path_after_keyword(line: &str, keyword: &str) -> Vec<String> {
    let tail = line.trim().strip_prefix(keyword).unwrap_or("").trim();
    split_path_respecting_interpolations(tail)
}

/// Split `s` by `.` but treat the contents of `${...}` as atomic — dots
/// inside an interpolation expression are not considered path separators.
///
/// Examples:
/// - `"com.example.pkg"`         → `["com", "example", "pkg"]`
/// - `"${compositor.entity.pkg}"` → `["${compositor.entity.pkg}"]`
/// - `"com.${compositor.pkg}"`   → `["com", "${compositor.pkg}"]`
fn split_path_respecting_interpolations(s: &str) -> Vec<String> {
    let mut result = Vec::new();
    let mut current = String::new();
    let mut depth: usize = 0;
    let bytes = s.as_bytes();
    let mut i = 0;
    while i < bytes.len() {
        let ch = bytes[i];
        if ch == b'$' && depth == 0 && i + 1 < bytes.len() && bytes[i + 1] == b'{' {
            depth += 1;
            current.push('$');
            current.push('{');
            i += 2;
            continue;
        }
        if depth > 0 {
            if ch == b'{' {
                depth += 1;
            } else if ch == b'}' {
                depth -= 1;
            }
            current.push(ch as char);
        } else if ch == b'.' {
            let seg = current.trim().to_string();
            if !seg.is_empty() {
                result.push(seg);
            }
            current = String::new();
        } else {
            current.push(ch as char);
        }
        i += 1;
    }
    let seg = current.trim().to_string();
    if !seg.is_empty() {
        result.push(seg);
    }
    result
}

/// Extract the inline bracket modifier from a node header, if present.
///
/// Examples:
/// - `impl[public interface] MyInterface { ... }` → `Some("public interface")`
/// - `impl[public class] MyClass { ... }`         → `Some("public class")`
/// - `impl Service { ... }`                       → `None`
fn extract_modifier(text: &str) -> Option<String> {
    let trimmed = text.trim_start();
    // Skip over the leading keyword (e.g. "impl", "func")
    let rest = trimmed.trim_start_matches(|c: char| c.is_ascii_alphanumeric() || c == '_');
    let rest = rest.trim_start();
    if rest.starts_with('[') {
        let end = rest.find(']')?;
        let modifier = rest[1..end].trim().to_string();
        if modifier.is_empty() {
            None
        } else {
            Some(modifier)
        }
    } else {
        None
    }
}

fn detect_kind(text: &str) -> TmplNodeKind {
    let trimmed = text.trim_start();
    if trimmed.starts_with("<[") {
        return TmplNodeKind::Include;
    }

    // `raw:` prefix — emit the rest verbatim, no substitution, no keyword parsing.
    if trimmed.starts_with("raw:") || trimmed == "raw" {
        return TmplNodeKind::Raw;
    }

    let keyword = first_keyword(trimmed);
    match keyword.as_deref() {
        Some("impl") => TmplNodeKind::Impl,
        Some("func") => TmplNodeKind::Func,
        Some("var") => TmplNodeKind::Var,
        Some("if") => TmplNodeKind::If,
        Some("for") => TmplNodeKind::For,
        Some("while") => TmplNodeKind::While,
        Some("do") => TmplNodeKind::DoWhile,
        Some("return") => TmplNodeKind::Return,
        Some("comment") => TmplNodeKind::Comment,
        Some("setAttr") => TmplNodeKind::SetAttribute,
        _ => {
            if looks_like_param(trimmed) {
                TmplNodeKind::Param
            } else if looks_like_attribute(trimmed) {
                TmplNodeKind::Attribute
            } else if trimmed.starts_with("pkg ") || trimmed.starts_with("use ") {
                TmplNodeKind::Unknown
            } else {
                TmplNodeKind::Operation
            }
        }
    }
}

fn looks_like_param(text: &str) -> bool {
    let Some((left, _)) = text.split_once(':') else {
        return false;
    };
    let left = left.trim();
    !left.is_empty() && left.chars().all(|c| c.is_ascii_alphanumeric() || c == '_')
}

fn looks_like_attribute(text: &str) -> bool {
    text.contains(':') && !text.contains('(') && !text.contains("=>")
}

fn first_keyword(text: &str) -> Option<String> {
    let mut idx = 0usize;
    let bytes = text.as_bytes();

    while idx < bytes.len() {
        while idx < bytes.len() && bytes[idx].is_ascii_whitespace() {
            idx += 1;
        }
        if idx >= bytes.len() {
            return None;
        }
        if bytes[idx] == b'@' {
            while idx < bytes.len() && bytes[idx] != b'\n' {
                idx += 1;
            }
            continue;
        }
        break;
    }

    let start = idx;
    while idx < bytes.len()
        && (bytes[idx].is_ascii_alphanumeric() || bytes[idx] == b'_' || bytes[idx] == b'-')
    {
        idx += 1;
    }
    if start == idx {
        None
    } else {
        Some(text[start..idx].to_string())
    }
}

fn strip_outer_braces(input: &str) -> Result<&str, TmplTreeError> {
    let trimmed = input.trim();
    if !trimmed.starts_with('{') || !trimmed.ends_with('}') {
        return Err(TmplTreeError(
            "tmpl block content must be enclosed by braces".to_string(),
        ));
    }
    Ok(&trimmed[1..trimmed.len() - 1])
}

#[derive(Debug)]
struct ParsedSegment {
    text: String,
    start: usize,
}

fn split_top_level_items(input: &str) -> Result<Vec<ParsedSegment>, TmplTreeError> {
    let mut out = Vec::new();
    let mut idx = 0usize;
    while idx < input.len() {
        skip_ws_and_comments(input, &mut idx);
        if idx >= input.len() {
            break;
        }
        let start = idx;
        idx = read_item_end(input, idx)?;
        let raw = &input[start..idx];
        let trimmed = raw.trim();
        if !trimmed.is_empty() {
            let leading_ws = raw.len().saturating_sub(raw.trim_start().len());
            out.push(ParsedSegment {
                text: trimmed.to_string(),
                start: start + leading_ws,
            });
        }
    }
    Ok(out)
}

fn skip_ws_and_comments(input: &str, idx: &mut usize) {
    let bytes = input.as_bytes();
    loop {
        while *idx < bytes.len() && bytes[*idx].is_ascii_whitespace() {
            *idx += 1;
        }
        if *idx + 1 < bytes.len() && bytes[*idx] == b'/' && bytes[*idx + 1] == b'/' {
            *idx += 2;
            while *idx < bytes.len() && bytes[*idx] != b'\n' {
                *idx += 1;
            }
            continue;
        }
        break;
    }
}

fn read_item_end(input: &str, start: usize) -> Result<usize, TmplTreeError> {
    let bytes = input.as_bytes();
    let mut idx = start;
    let mut paren = 0usize;
    let mut bracket = 0usize;
    let mut has_block = false;
    let mut in_string: Option<u8> = None;

    while idx < bytes.len() {
        let ch = bytes[idx];
        if let Some(quote) = in_string {
            if ch == b'\\' {
                if idx + 1 < bytes.len() {
                    idx += 2;
                } else {
                    idx += 1;
                }
                continue;
            }
            if ch == quote {
                in_string = None;
            }
            idx += 1;
            continue;
        }

        match ch {
            b'"' | b'\'' => {
                in_string = Some(ch);
                idx += 1;
            }
            b'(' => {
                paren += 1;
                idx += 1;
            }
            b')' => {
                paren = paren.saturating_sub(1);
                idx += 1;
            }
            b'[' => {
                bracket += 1;
                idx += 1;
            }
            b']' => {
                bracket = bracket.saturating_sub(1);
                idx += 1;
            }
            b'{' if paren == 0 && bracket == 0 => {
                // `${...}` is a variable interpolation — skip it without marking
                // the item as having a body block.
                if idx > 0 && bytes[idx - 1] == b'$' {
                    idx = skip_balanced_braces(input, idx)?;
                } else {
                    has_block = true;
                    idx = skip_balanced_braces(input, idx)?;
                }
            }
            b';' if paren == 0 && bracket == 0 => {
                return Ok(idx + 1);
            }
            b'\n' if paren == 0 && bracket == 0 && !has_block => {
                return Ok(idx);
            }
            b'\n' if paren == 0 && bracket == 0 && has_block => {
                let rest = input[idx + 1..].trim_start();
                if rest.starts_with("while ") {
                    idx += 1;
                } else {
                    return Ok(idx);
                }
            }
            _ => idx += 1,
        }
    }

    Ok(input.len())
}

fn skip_balanced_braces(input: &str, start: usize) -> Result<usize, TmplTreeError> {
    let bytes = input.as_bytes();
    if bytes.get(start) != Some(&b'{') {
        return Err(TmplTreeError("expected opening brace".to_string()));
    }
    let mut idx = start;
    let mut depth = 0usize;
    let mut in_string: Option<u8> = None;

    while idx < bytes.len() {
        let ch = bytes[idx];
        if let Some(quote) = in_string {
            if ch == b'\\' {
                if idx + 1 < bytes.len() {
                    idx += 2;
                } else {
                    idx += 1;
                }
                continue;
            }
            if ch == quote {
                in_string = None;
            }
            idx += 1;
            continue;
        }
        match ch {
            b'"' | b'\'' => {
                in_string = Some(ch);
                idx += 1;
            }
            b'{' => {
                depth += 1;
                idx += 1;
            }
            b'}' => {
                depth = depth.saturating_sub(1);
                idx += 1;
                if depth == 0 {
                    return Ok(idx);
                }
            }
            _ => idx += 1,
        }
    }

    Err(TmplTreeError("unclosed brace block".to_string()))
}

fn find_first_top_level_block(input: &str) -> Result<Option<(usize, usize)>, TmplTreeError> {
    let bytes = input.as_bytes();
    let mut idx = 0usize;
    let mut paren = 0usize;
    let mut bracket = 0usize;
    let mut in_string: Option<u8> = None;

    while idx < bytes.len() {
        let ch = bytes[idx];
        if let Some(quote) = in_string {
            if ch == b'\\' {
                if idx + 1 < bytes.len() {
                    idx += 2;
                } else {
                    idx += 1;
                }
                continue;
            }
            if ch == quote {
                in_string = None;
            }
            idx += 1;
            continue;
        }
        match ch {
            b'"' | b'\'' => {
                in_string = Some(ch);
                idx += 1;
            }
            b'(' => {
                paren += 1;
                idx += 1;
            }
            b')' => {
                paren = paren.saturating_sub(1);
                idx += 1;
            }
            b'[' => {
                bracket += 1;
                idx += 1;
            }
            b']' => {
                bracket = bracket.saturating_sub(1);
                idx += 1;
            }
            b'{' if paren == 0 && bracket == 0 => {
                // `${...}` is a variable interpolation — skip it rather than
                // treating it as the node's body block.
                if idx > 0 && bytes[idx - 1] == b'$' {
                    idx = skip_balanced_braces(input, idx)?;
                } else {
                    let end = skip_balanced_braces(input, idx)?;
                    return Ok(Some((idx, end - 1)));
                }
            }
            _ => idx += 1,
        }
    }

    Ok(None)
}

#[cfg(test)]
mod tests {
    use crate::ast::{TemplateContent, TemplateParam};

    use super::{TmplNodeKind, parse_tmpl_block_tree};

    // ── full template (pkg / use / impl[modifier] / func) ───────────────────

    #[test]
    fn parses_full_tmpl_tree() {
        let content = TemplateContent::Full(
            r#"
                {
                    pkg core.render
                    use util.text as txt

                    impl[public interface] MyInterface {
                        func myFunc(param1: String, param2: Int)
                    }

                    impl[public class] MyClass {
                        func myFunc(param1: String, param2: Int): Int {
                            return 42
                        }
                    }

                    func main(): String {
                        <[txt.render()]>
                    }
                }
            "#
            .to_string(),
        );

        let tree = parse_tmpl_block_tree(
            "java",
            "myTemplate",
            &[TemplateParam {
                name: "varName".to_string(),
                ty: "String".to_string(),
            }],
            &content,
        )
        .expect("tree should parse");

        let full = match tree.content {
            super::TmplContentTree::Full(full) => full,
            _ => panic!("expected full content"),
        };

        assert_eq!(
            full.package,
            Some(vec!["core".to_string(), "render".to_string()])
        );
        assert_eq!(full.uses.len(), 1);
        assert_eq!(
            full.uses[0].path,
            vec!["util".to_string(), "text".to_string()]
        );
        assert_eq!(full.uses[0].alias.as_deref(), Some("txt"));

        // First node: impl[public interface]
        assert_eq!(full.nodes[0].kind, TmplNodeKind::Impl);
        assert_eq!(full.nodes[0].modifier.as_deref(), Some("public interface"));
        // Its child: abstract func (no body)
        assert_eq!(full.nodes[0].children[0].kind, TmplNodeKind::Func);
        assert!(full.nodes[0].children[0].children.is_empty());

        // Second node: impl[public class]
        assert_eq!(full.nodes[1].kind, TmplNodeKind::Impl);
        assert_eq!(full.nodes[1].modifier.as_deref(), Some("public class"));
        // Its child: concrete func with body
        assert_eq!(full.nodes[1].children[0].kind, TmplNodeKind::Func);
        assert!(!full.nodes[1].children[0].children.is_empty());

        // Third node: top-level func with an include child
        assert_eq!(full.nodes[2].kind, TmplNodeKind::Func);
        assert_eq!(full.nodes[2].children.len(), 1);
        let include_node = &full.nodes[2].children[0];
        assert_eq!(include_node.kind, TmplNodeKind::Include);
        assert!(
            include_node.text.contains("<[") && include_node.text.contains("]>"),
            "include node text should contain the <[...]> delimiters: {:?}",
            include_node.text
        );
        assert!(
            include_node.text.contains("txt.render()"),
            "include node text should contain the call expression: {:?}",
            include_node.text
        );

        // Context is populated
        assert_eq!(full.nodes[0].context.file, None);
        assert!(full.nodes[0].context.line.is_some());
        assert!(full.nodes[0].context.position.is_some());
        assert_eq!(full.uses[0].context.file, None);
        assert!(full.uses[0].context.line.is_some());
        assert!(full.uses[0].context.position.is_some());
    }

    // ── impl[modifier] ───────────────────────────────────────────────────────

    #[test]
    fn parses_impl_bracket_modifier() {
        let content = TemplateContent::Full(
            r#"
                {
                    impl[public interface] IService {
                        func doWork(input: String)
                    }

                    impl[public abstract class] BaseService {
                        func doWork(input: String): String {
                            return input
                        }
                    }

                    impl Service {
                        func doWork(input: String): String {
                            return input
                        }
                    }
                }
            "#
            .to_string(),
        );

        let tree = parse_tmpl_block_tree("java", "svc", &[], &content).expect("tree should parse");
        let full = match tree.content {
            super::TmplContentTree::Full(f) => f,
            _ => panic!("expected full"),
        };

        assert_eq!(full.nodes[0].modifier.as_deref(), Some("public interface"));
        assert_eq!(
            full.nodes[1].modifier.as_deref(),
            Some("public abstract class")
        );
        // impl with no bracket has no modifier
        assert_eq!(full.nodes[2].modifier, None);
    }

    // ── ${varName} interpolation in impl name ────────────────────────────────

    #[test]
    fn parses_impl_with_interpolated_name() {
        let content = TemplateContent::Full(
            r#"
                {
                    impl[public class] ${varName} {
                        func myFunc(param1: String, param2: String): Int {
                            return 42
                        }
                    }
                }
            "#
            .to_string(),
        );

        let tree = parse_tmpl_block_tree(
            "java",
            "myTemplate",
            &[TemplateParam {
                name: "varName".to_string(),
                ty: "String".to_string(),
            }],
            &content,
        )
        .expect("tree should parse");

        let full = match tree.content {
            super::TmplContentTree::Full(f) => f,
            _ => panic!("expected full"),
        };

        assert_eq!(full.nodes.len(), 1);
        let node = &full.nodes[0];
        assert_eq!(node.kind, TmplNodeKind::Impl);
        assert_eq!(node.modifier.as_deref(), Some("public class"));
        // The text contains the interpolation placeholder
        assert!(node.text.contains("${varName}"));
        // The impl body is correctly parsed — one func child
        assert_eq!(node.children.len(), 1);
        assert_eq!(node.children[0].kind, TmplNodeKind::Func);
    }

    // ── typed func signatures ────────────────────────────────────────────────

    #[test]
    fn parses_abstract_func_declaration() {
        // func with no body — just a signature line
        let content = TemplateContent::Full(
            r#"
                {
                    impl[public interface] IService {
                        func build(name: String, retries: Int)
                        func destroy()
                    }
                }
            "#
            .to_string(),
        );

        let tree = parse_tmpl_block_tree("java", "svc", &[], &content).expect("tree should parse");
        let full = match tree.content {
            super::TmplContentTree::Full(f) => f,
            _ => panic!("expected full"),
        };

        let iface = &full.nodes[0];
        assert_eq!(iface.kind, TmplNodeKind::Impl);
        assert_eq!(iface.children.len(), 2);
        // Both are Func nodes with no children (no body)
        assert_eq!(iface.children[0].kind, TmplNodeKind::Func);
        assert!(iface.children[0].children.is_empty());
        assert_eq!(iface.children[1].kind, TmplNodeKind::Func);
        assert!(iface.children[1].children.is_empty());
    }

    #[test]
    fn parses_func_with_array_param_and_return_type() {
        // func myFunc(param1: String[], param2: String): Int { return 42 }
        let content = TemplateContent::Full(
            r#"
                {
                    impl[public class] MyClass {
                        func myFunc(param1: String[], param2: String): Int {
                            return 42
                        }
                    }
                }
            "#
            .to_string(),
        );

        let tree =
            parse_tmpl_block_tree("java", "myTemplate", &[], &content).expect("tree should parse");
        let full = match tree.content {
            super::TmplContentTree::Full(f) => f,
            _ => panic!("expected full"),
        };

        let cls = &full.nodes[0];
        assert_eq!(cls.kind, TmplNodeKind::Impl);
        assert_eq!(cls.modifier.as_deref(), Some("public class"));
        assert_eq!(cls.children.len(), 1);

        let func = &cls.children[0];
        assert_eq!(func.kind, TmplNodeKind::Func);
        assert!(func.text.contains("param1: String[]"));
        assert!(func.text.contains("param2: String"));
        assert!(func.text.contains(": Int"));
        // Has a body with a return statement
        assert_eq!(func.children.len(), 1);
        assert_eq!(func.children[0].kind, TmplNodeKind::Return);
    }

    // ── complete example matching user-specified syntax ──────────────────────

    #[test]
    fn parses_full_java_template_example() {
        let content = TemplateContent::Full(
            r#"
                {
                    impl[public interface] myInterface {
                        func myFunc(param1: String, param2: Int)
                    }

                    impl[public class] myClass {
                        func myFunc(param1: String[], param2: String): Int {
                            return 42
                        }
                    }

                    impl[public class] ${varName} {
                        func myFunc(param1: String[], param2: String): Int {
                            return 42
                        }
                    }
                }
            "#
            .to_string(),
        );

        let tree = parse_tmpl_block_tree(
            "java",
            "myTemplate",
            &[TemplateParam {
                name: "varName".to_string(),
                ty: "String".to_string(),
            }],
            &content,
        )
        .expect("tree should parse");

        let full = match tree.content {
            super::TmplContentTree::Full(f) => f,
            _ => panic!("expected full"),
        };

        assert_eq!(full.nodes.len(), 3);

        // interface
        assert_eq!(full.nodes[0].kind, TmplNodeKind::Impl);
        assert_eq!(full.nodes[0].modifier.as_deref(), Some("public interface"));
        assert_eq!(full.nodes[0].children[0].kind, TmplNodeKind::Func);
        assert!(full.nodes[0].children[0].children.is_empty()); // no body

        // class
        assert_eq!(full.nodes[1].kind, TmplNodeKind::Impl);
        assert_eq!(full.nodes[1].modifier.as_deref(), Some("public class"));
        assert_eq!(full.nodes[1].children[0].kind, TmplNodeKind::Func);
        assert!(!full.nodes[1].children[0].children.is_empty()); // has body

        // interpolated class name
        assert_eq!(full.nodes[2].kind, TmplNodeKind::Impl);
        assert_eq!(full.nodes[2].modifier.as_deref(), Some("public class"));
        assert!(full.nodes[2].text.contains("${varName}"));
        assert_eq!(full.nodes[2].children[0].kind, TmplNodeKind::Func);
    }

    // ── specialized template ─────────────────────────────────────────────────

    #[test]
    fn parses_specialized_tmpl_tree() {
        let content = TemplateContent::Specialized(
            r#"
                {
                    setAttr value: call.name()
                }
            "#
            .to_string(),
        );

        let tree =
            parse_tmpl_block_tree("go", "handler", &[], &content).expect("tree should parse");
        let specialized = match tree.content {
            super::TmplContentTree::Specialized(spec) => spec,
            _ => panic!("expected specialized content"),
        };
        assert_eq!(specialized.node.kind, TmplNodeKind::SetAttribute);
        assert!(specialized.node.text.contains("setAttr"));
        assert_eq!(specialized.node.context.file, None);
        assert!(specialized.node.context.line.is_some());
        assert!(specialized.node.context.position.is_some());
    }

    // ── tmplInclude (<[ ... ]>) ───────────────────────────────────────────────

    /// A `<[ call ]>` node inside a top-level impl body is parsed as
    /// `TmplNodeKind::Include` and its text preserves the full `<[...]>` span.
    #[test]
    fn parses_include_in_impl_body() {
        let content = TemplateContent::Full(
            r#"
                {
                    impl[public class] MyClass {
                        <[ renderFields(fields) ]>
                    }
                }
            "#
            .to_string(),
        );

        let tree = parse_tmpl_block_tree("kotlin", "myTemplate", &[], &content)
            .expect("tree should parse");
        let full = match tree.content {
            super::TmplContentTree::Full(f) => f,
            _ => panic!("expected full content"),
        };

        assert_eq!(full.nodes.len(), 1);
        let impl_node = &full.nodes[0];
        assert_eq!(impl_node.kind, TmplNodeKind::Impl);
        assert_eq!(impl_node.children.len(), 1);

        let inc = &impl_node.children[0];
        assert_eq!(inc.kind, TmplNodeKind::Include);
        assert!(
            inc.text.contains("<[") && inc.text.contains("]>"),
            "include node text should preserve delimiters: {:?}",
            inc.text
        );
        assert!(
            inc.text.contains("renderFields(fields)"),
            "include node text should contain the call expression: {:?}",
            inc.text
        );
        // Context must be populated for LSP / error reporting.
        assert_eq!(inc.context.file, None);
        assert!(inc.context.line.is_some());
        assert!(inc.context.position.is_some());
    }

    /// A `<[ call ]>` node at the top level of a full template body (outside
    /// any impl) is also recognised as `TmplNodeKind::Include`.
    #[test]
    fn parses_include_at_top_level() {
        let content = TemplateContent::Full(
            r#"
                {
                    <[ generateHeader(name) ]>
                    impl[public class] Body {}
                }
            "#
            .to_string(),
        );

        let tree = parse_tmpl_block_tree("kotlin", "t", &[], &content).expect("tree should parse");
        let full = match tree.content {
            super::TmplContentTree::Full(f) => f,
            _ => panic!("expected full content"),
        };

        assert!(full.nodes.len() >= 1);
        let first = &full.nodes[0];
        assert_eq!(first.kind, TmplNodeKind::Include);
        assert!(first.text.contains("generateHeader(name)"));
    }

    /// A `<[ call ]>` node inside a specialized (`spec`) block is parsed as
    /// `TmplNodeKind::Include`.
    #[test]
    fn parses_include_in_spec_block() {
        let content = TemplateContent::Specialized(
            r#"{
                <[ renderBody(x) ]>
            }"#
            .to_string(),
        );

        let tree = parse_tmpl_block_tree("kotlin", "t", &[], &content).expect("tree should parse");
        let spec = match tree.content {
            super::TmplContentTree::Specialized(s) => s,
            _ => panic!("expected specialized content"),
        };

        assert_eq!(spec.node.kind, TmplNodeKind::Include);
        assert!(spec.node.text.contains("renderBody(x)"));
    }
}
