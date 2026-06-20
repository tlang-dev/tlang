// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Parser and renderer for `doc [lang, ...] name(params) { … }` template bodies.
//!
//! A doc template describes a structured document that can be rendered to
//! Markdown, HTML, or other document formats.
//!
//! # Syntax overview
//!
//! ```text
//! doc [md, html] readme(project: String) {
//!   # Main Heading with ${project}
//!   ## Sub Heading
//!   ### Sub-sub heading
//!   #(4) Level-4 heading
//!
//!   Some plain text paragraph.
//!
//!   [section "intro"
//!     Content inside the section.
//!   ]
//!
//!   [code "rust"
//!     fn main() {}
//!   ]
//!
//!   [img "path/to/img.png" "Alt text"]
//!   [link "https://example.com" "Click here"]
//!   [span "inline text"]
//!
//!   [list "unordered"
//!     - Item one
//!     - Item two
//!   ]
//!
//!   [table "Col A" | "Col B"]
//!
//!   [include myTemplate(arg)]
//!
//!   [asis
//!     Verbatim content.
//!   ]
//! }
//! ```

use std::collections::HashMap;

use crate::ast::TemplateParam;

// ── Public tree types ─────────────────────────────────────────────────────────

/// The parsed, validated form of a complete `doc […] name(params) { … }` declaration.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct DocTemplateTree {
    pub langs: Vec<String>,
    pub name: String,
    pub params: Vec<TemplateParam>,
    pub nodes: Vec<DocNode>,
}

/// A single structural element inside a doc template body.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum DocNode {
    Heading { level: u32, title: String },
    Section { name: String, nodes: Vec<DocNode> },
    Text { content: String },
    Img { src: String, alt: Option<String> },
    Link { src: String, text: String },
    Code { lang: String, code: String },
    Span { content: String },
    List { ordered: bool, items: Vec<String> },
    Table { headers: Vec<String> },
    Include { call: String },
    AsIs { content: String },
}

// ── Error type ────────────────────────────────────────────────────────────────

#[derive(Debug)]
pub struct DocTreeError(pub String);

impl std::fmt::Display for DocTreeError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.0)
    }
}

// ── Public API ────────────────────────────────────────────────────────────────

/// Parse the raw content string of a `doc [...]` template block (including the
/// outer `{ }` braces) into a [`DocTemplateTree`].
pub fn parse_doc_block_tree(
    langs: &[String],
    name: &str,
    params: &[TemplateParam],
    content: &str,
) -> Result<DocTemplateTree, DocTreeError> {
    // Strip outer `{ }`.
    let body = strip_outer_braces(content);
    let nodes = parse_nodes(body)?;
    Ok(DocTemplateTree {
        langs: langs.to_vec(),
        name: name.to_string(),
        params: params.to_vec(),
        nodes,
    })
}

// ── Parser internals ──────────────────────────────────────────────────────────

fn strip_outer_braces(s: &str) -> &str {
    let s = s.trim();
    if s.starts_with('{') && s.ends_with('}') {
        &s[1..s.len() - 1]
    } else {
        s
    }
}

/// Parse doc nodes from a body string (no outer braces).
fn parse_nodes(body: &str) -> Result<Vec<DocNode>, DocTreeError> {
    let mut nodes: Vec<DocNode> = Vec::new();
    let mut pos = 0;
    let chars: Vec<char> = body.chars().collect();
    let len = chars.len();

    // Accumulate plain text lines here.
    let mut text_lines: Vec<String> = Vec::new();

    macro_rules! flush_text {
        () => {
            if !text_lines.is_empty() {
                let combined = text_lines.join("\n").trim().to_string();
                if !combined.is_empty() {
                    nodes.push(DocNode::Text { content: combined });
                }
                text_lines.clear();
            }
        };
    }

    while pos < len {
        // Skip pure blank lines (just newlines/spaces) without accumulating.
        if chars[pos] == '\n' || chars[pos] == '\r' {
            pos += 1;
            continue;
        }

        // Peek at the current line.
        let line_start = pos;
        let line_end = find_line_end(&chars, pos);
        let line: String = chars[line_start..line_end].iter().collect();
        let trimmed = line.trim();

        if trimmed.is_empty() {
            pos = line_end + 1;
            continue;
        }

        // ── Heading: `# title`, `## title`, `### title`, `#(N) title` ────────
        if trimmed.starts_with('#') {
            flush_text!();

            if let Some((level, title)) = parse_heading(trimmed) {
                nodes.push(DocNode::Heading { level, title });
                pos = line_end + 1;
                continue;
            }
        }

        // ── Block directives starting with `[` ─────────────────────────────
        if trimmed.starts_with('[') {
            flush_text!();

            // Find absolute offset of the `[` on this trimmed line.
            let bracket_offset = line_start + (line.len() - line.trim_start().len());

            if trimmed.starts_with("[section ") || trimmed.starts_with("[section\"") {
                let (node, consumed) = parse_section_block(&chars, bracket_offset)?;
                nodes.push(node);
                pos = bracket_offset + consumed;
                // skip trailing newline
                if pos < len && chars[pos] == '\n' {
                    pos += 1;
                }
                continue;
            }

            if trimmed.starts_with("[code ") || trimmed.starts_with("[code\"") {
                let (node, consumed) = parse_code_block(&chars, bracket_offset)?;
                nodes.push(node);
                pos = bracket_offset + consumed;
                if pos < len && chars[pos] == '\n' {
                    pos += 1;
                }
                continue;
            }

            if trimmed.starts_with("[img ") || trimmed.starts_with("[img\"") {
                let (node, consumed) = parse_img_block(&chars, bracket_offset)?;
                nodes.push(node);
                pos = bracket_offset + consumed;
                if pos < len && chars[pos] == '\n' {
                    pos += 1;
                }
                continue;
            }

            if trimmed.starts_with("[link ") || trimmed.starts_with("[link\"") {
                let (node, consumed) = parse_link_block(&chars, bracket_offset)?;
                nodes.push(node);
                pos = bracket_offset + consumed;
                if pos < len && chars[pos] == '\n' {
                    pos += 1;
                }
                continue;
            }

            if trimmed.starts_with("[span ") || trimmed.starts_with("[span\"") {
                let (node, consumed) = parse_span_block(&chars, bracket_offset)?;
                nodes.push(node);
                pos = bracket_offset + consumed;
                if pos < len && chars[pos] == '\n' {
                    pos += 1;
                }
                continue;
            }

            if trimmed.starts_with("[list ") || trimmed.starts_with("[list\"") {
                let (node, consumed) = parse_list_block(&chars, bracket_offset)?;
                nodes.push(node);
                pos = bracket_offset + consumed;
                if pos < len && chars[pos] == '\n' {
                    pos += 1;
                }
                continue;
            }

            if trimmed.starts_with("[table ") || trimmed.starts_with("[table\"") {
                let (node, consumed) = parse_table_block(&chars, bracket_offset)?;
                nodes.push(node);
                pos = bracket_offset + consumed;
                if pos < len && chars[pos] == '\n' {
                    pos += 1;
                }
                continue;
            }

            if trimmed.starts_with("[include ") {
                let (node, consumed) = parse_include_block(&chars, bracket_offset)?;
                nodes.push(node);
                pos = bracket_offset + consumed;
                if pos < len && chars[pos] == '\n' {
                    pos += 1;
                }
                continue;
            }

            if trimmed.starts_with("[asis") {
                let (node, consumed) = parse_asis_block(&chars, bracket_offset)?;
                nodes.push(node);
                pos = bracket_offset + consumed;
                if pos < len && chars[pos] == '\n' {
                    pos += 1;
                }
                continue;
            }
        }

        // ── Plain text line ────────────────────────────────────────────────
        text_lines.push(trimmed.to_string());
        pos = line_end + 1;
    }

    flush_text!();

    Ok(nodes)
}

/// Returns (level, title) for a heading line like `## Hello` or `#(3) Hello`.
fn parse_heading(trimmed: &str) -> Option<(u32, String)> {
    // `#(N) title`
    if let Some(rest) = trimmed.strip_prefix("#(") {
        if let Some(close) = rest.find(')') {
            let level_str = &rest[..close];
            if let Ok(level) = level_str.parse::<u32>() {
                let title = rest[close + 1..].trim().to_string();
                return Some((level, title));
            }
        }
        return None;
    }

    // `##...# title`
    let mut level = 0u32;
    let mut chars = trimmed.chars().peekable();
    while chars.peek() == Some(&'#') {
        level += 1;
        chars.next();
    }
    if level == 0 {
        return None;
    }
    // Must be followed by a space (or end of line for empty heading).
    match chars.peek() {
        Some(' ') | Some('\t') | None => {}
        _ => return None,
    }
    let title: String = chars.collect::<String>().trim().to_string();
    Some((level, title))
}

/// Find the index just past the end of the current line (not including the `\n`).
fn find_line_end(chars: &[char], start: usize) -> usize {
    let mut pos = start;
    while pos < chars.len() && chars[pos] != '\n' {
        pos += 1;
    }
    pos
}

// ── Block parsers ─────────────────────────────────────────────────────────────
//
// Each parser receives:
//   `chars`  — the full character slice of the body
//   `start`  — the index of the opening `[`
//
// Returns (node, bytes_consumed_from_start).

/// Parse `[section "name" ... ]`
fn parse_section_block(chars: &[char], start: usize) -> Result<(DocNode, usize), DocTreeError> {
    // Skip `[section`
    let mut pos = start + "[section".len();
    skip_whitespace(chars, &mut pos);

    let sec_name = read_quoted_string(chars, &mut pos)
        .ok_or_else(|| DocTreeError("expected quoted section name after [section".to_string()))?;

    // Everything until a standalone `]` line is the section content.
    let content_start = pos;
    let content_end = find_standalone_close_bracket(chars, pos)
        .ok_or_else(|| DocTreeError(format!("unclosed [section \"{sec_name}\"")))?;

    let body: String = chars[content_start..content_end].iter().collect();
    let sub_nodes = parse_nodes(&body)?;

    // Skip past the `]`.
    let consumed = content_end - start + 1;
    Ok((
        DocNode::Section {
            name: sec_name,
            nodes: sub_nodes,
        },
        consumed,
    ))
}

/// Parse `[code "lang" ... ]`
fn parse_code_block(chars: &[char], start: usize) -> Result<(DocNode, usize), DocTreeError> {
    let mut pos = start + "[code".len();
    skip_whitespace(chars, &mut pos);

    let lang = read_quoted_string(chars, &mut pos)
        .ok_or_else(|| DocTreeError("expected quoted language after [code".to_string()))?;

    // Rest of content until standalone `]`.
    let content_start = pos;
    let content_end = find_standalone_close_bracket(chars, pos)
        .ok_or_else(|| DocTreeError(format!("unclosed [code \"{lang}\"")))?;

    let code: String = chars[content_start..content_end].iter().collect();
    // Trim leading/trailing newline only.
    let code = code
        .strip_prefix('\n')
        .unwrap_or(&code)
        .strip_suffix('\n')
        .unwrap_or(&code.strip_prefix('\n').unwrap_or(&code))
        .to_string();

    let consumed = content_end - start + 1;
    Ok((DocNode::Code { lang, code }, consumed))
}

/// Parse `[img "src" "alt?"]`
fn parse_img_block(chars: &[char], start: usize) -> Result<(DocNode, usize), DocTreeError> {
    let mut pos = start + "[img".len();
    skip_whitespace(chars, &mut pos);

    let src = read_quoted_string(chars, &mut pos)
        .ok_or_else(|| DocTreeError("expected quoted src after [img".to_string()))?;

    skip_whitespace(chars, &mut pos);

    let alt = if pos < chars.len() && chars[pos] == '"' {
        read_quoted_string(chars, &mut pos)
    } else {
        None
    };

    skip_whitespace(chars, &mut pos);
    // Expect `]`
    if pos < chars.len() && chars[pos] == ']' {
        pos += 1;
    }

    Ok((DocNode::Img { src, alt }, pos - start))
}

/// Parse `[link "src" "text"]`
fn parse_link_block(chars: &[char], start: usize) -> Result<(DocNode, usize), DocTreeError> {
    let mut pos = start + "[link".len();
    skip_whitespace(chars, &mut pos);

    let src = read_quoted_string(chars, &mut pos)
        .ok_or_else(|| DocTreeError("expected quoted src after [link".to_string()))?;

    skip_whitespace(chars, &mut pos);

    let text = read_quoted_string(chars, &mut pos)
        .ok_or_else(|| DocTreeError("expected quoted text after [link src".to_string()))?;

    skip_whitespace(chars, &mut pos);
    if pos < chars.len() && chars[pos] == ']' {
        pos += 1;
    }

    Ok((DocNode::Link { src, text }, pos - start))
}

/// Parse `[span "text"]`
fn parse_span_block(chars: &[char], start: usize) -> Result<(DocNode, usize), DocTreeError> {
    let mut pos = start + "[span".len();
    skip_whitespace(chars, &mut pos);

    let content = read_quoted_string(chars, &mut pos)
        .ok_or_else(|| DocTreeError("expected quoted content after [span".to_string()))?;

    skip_whitespace(chars, &mut pos);
    if pos < chars.len() && chars[pos] == ']' {
        pos += 1;
    }

    Ok((DocNode::Span { content }, pos - start))
}

/// Parse `[list "ordered|unordered" - item ... ]`
fn parse_list_block(chars: &[char], start: usize) -> Result<(DocNode, usize), DocTreeError> {
    let mut pos = start + "[list".len();
    skip_whitespace(chars, &mut pos);

    let order = read_quoted_string(chars, &mut pos)
        .ok_or_else(|| DocTreeError("expected quoted order after [list".to_string()))?;

    let ordered = order.to_lowercase() == "ordered";

    // Read items until standalone `]`.
    let content_end = find_standalone_close_bracket(chars, pos)
        .ok_or_else(|| DocTreeError("unclosed [list".to_string()))?;

    let body: String = chars[pos..content_end].iter().collect();
    let mut items = Vec::new();
    for line in body.lines() {
        let t = line.trim();
        if t.is_empty() {
            continue;
        }
        let item = if let Some(rest) = t.strip_prefix("- ") {
            rest.to_string()
        } else if let Some(rest) = t.strip_prefix('-') {
            rest.trim().to_string()
        } else {
            t.to_string()
        };
        if !item.is_empty() {
            items.push(item);
        }
    }

    let consumed = content_end - start + 1;
    Ok((DocNode::List { ordered, items }, consumed))
}

/// Parse `[table "h1" | "h2" | ... ]`
fn parse_table_block(chars: &[char], start: usize) -> Result<(DocNode, usize), DocTreeError> {
    let mut pos = start + "[table".len();
    skip_whitespace(chars, &mut pos);

    let mut headers = Vec::new();

    // Read headers until `]` (all on one line typically).
    while pos < chars.len() && chars[pos] != ']' && chars[pos] != '\n' {
        skip_whitespace(chars, &mut pos);
        if pos < chars.len() && chars[pos] == '|' {
            pos += 1;
            continue;
        }
        if pos < chars.len() && chars[pos] == '"' {
            if let Some(s) = read_quoted_string(chars, &mut pos) {
                headers.push(s);
            }
        } else if pos < chars.len() && chars[pos] == ']' {
            break;
        } else {
            pos += 1;
        }
    }

    if pos < chars.len() && chars[pos] == ']' {
        pos += 1;
    }

    Ok((DocNode::Table { headers }, pos - start))
}

/// Parse `[include expr]`
fn parse_include_block(chars: &[char], start: usize) -> Result<(DocNode, usize), DocTreeError> {
    let mut pos = start + "[include".len();
    skip_whitespace(chars, &mut pos);

    // Read until `]`.
    let call_start = pos;
    while pos < chars.len() && chars[pos] != ']' {
        pos += 1;
    }
    let call: String = chars[call_start..pos].iter().collect();
    let call = call.trim().to_string();

    if pos < chars.len() && chars[pos] == ']' {
        pos += 1;
    }

    Ok((DocNode::Include { call }, pos - start))
}

/// Parse `[asis ... ]`
fn parse_asis_block(chars: &[char], start: usize) -> Result<(DocNode, usize), DocTreeError> {
    let mut pos = start + "[asis".len();
    // skip optional whitespace/newline after `[asis`
    if pos < chars.len() && chars[pos] == '\n' {
        pos += 1;
    }

    let content_start = pos;
    let content_end = find_standalone_close_bracket(chars, pos)
        .ok_or_else(|| DocTreeError("unclosed [asis".to_string()))?;

    let content: String = chars[content_start..content_end].iter().collect();
    let content = content.strip_suffix('\n').unwrap_or(&content).to_string();

    let consumed = content_end - start + 1;
    Ok((DocNode::AsIs { content }, consumed))
}

// ── Character-level helpers ───────────────────────────────────────────────────

fn skip_whitespace(chars: &[char], pos: &mut usize) {
    while *pos < chars.len() && (chars[*pos] == ' ' || chars[*pos] == '\t') {
        *pos += 1;
    }
}

/// Read a `"quoted string"` at the current position, advancing `pos` past the
/// closing `"`.  Returns `None` if the current char is not `"`.
fn read_quoted_string(chars: &[char], pos: &mut usize) -> Option<String> {
    if *pos >= chars.len() || chars[*pos] != '"' {
        return None;
    }
    *pos += 1; // skip opening `"`
    let mut s = String::new();
    while *pos < chars.len() && chars[*pos] != '"' {
        if chars[*pos] == '\\' && *pos + 1 < chars.len() {
            *pos += 1;
            s.push(chars[*pos]);
        } else {
            s.push(chars[*pos]);
        }
        *pos += 1;
    }
    if *pos < chars.len() {
        *pos += 1; // skip closing `"`
    }
    Some(s)
}

/// Find the position of a `]` that appears alone on a line (only whitespace
/// before it on that line), starting from `start`.  Returns the index of the
/// `]` character, or `None` if not found.
fn find_standalone_close_bracket(chars: &[char], start: usize) -> Option<usize> {
    let mut pos = start;
    // Go to beginning of next line if we're mid-line.
    while pos < chars.len() && chars[pos] != '\n' {
        pos += 1;
    }
    if pos < chars.len() {
        pos += 1; // skip \n
    }

    // Track nesting depth so that `]` lines belonging to inner blocks
    // (e.g. a [code] inside a [section]) are not mistaken for the outer
    // block's terminator.
    let mut depth: i32 = 0;

    while pos < chars.len() {
        let line_start = pos;
        // Consume leading whitespace.
        while pos < chars.len() && (chars[pos] == ' ' || chars[pos] == '\t') {
            pos += 1;
        }
        if pos < chars.len() {
            if chars[pos] == ']' {
                if depth == 0 {
                    return Some(pos);
                }
                depth -= 1;
            } else if chars[pos] == '[' {
                // Peek ahead to detect block elements that end with a
                // standalone `]` (section, code, list, asis).  Single-line
                // elements (link, img, span, table, include) are NOT listed
                // here because they don't consume a standalone `]`.
                let peek: String = chars[pos..].iter().take(12).collect();
                if peek.starts_with("[code ")
                    || peek.starts_with("[section ")
                    || peek.starts_with("[list ")
                    || peek.starts_with("[asis")
                {
                    depth += 1;
                }
            }
        }
        // Skip to end of line.
        pos = line_start;
        while pos < chars.len() && chars[pos] != '\n' {
            pos += 1;
        }
        if pos < chars.len() {
            pos += 1;
        }
    }
    None
}

/// Serialize a slice of [`DocNode`]s to a `Value::List` of `Value::Map`s so
/// that doc generator programs written in TLang can traverse the tree using
/// `List.get`, `Map.get`, etc.
///
/// All `${}` parameter placeholders are resolved using `args` at this point,
/// so the generator receives fully-substituted content.
pub fn doc_nodes_to_value(
    nodes: &[DocNode],
    args: &HashMap<String, String>,
) -> crate::runtime::Value {
    use std::collections::BTreeMap;
    use crate::runtime::Value;

    fn node_to_value(node: &DocNode, args: &HashMap<String, String>) -> Value {
        let mut m: BTreeMap<String, Value> = BTreeMap::new();
        match node {
            DocNode::Heading { level, title } => {
                m.insert("type".into(), Value::String("heading".into()));
                m.insert("level".into(), Value::Int(*level as i64));
                m.insert("title".into(), Value::String(interpolate(title, args)));
            }
            DocNode::Text { content } => {
                m.insert("type".into(), Value::String("text".into()));
                m.insert("content".into(), Value::String(interpolate(content, args)));
            }
            DocNode::Code { lang, code } => {
                m.insert("type".into(), Value::String("code".into()));
                m.insert("lang".into(), Value::String(lang.clone()));
                m.insert("code".into(), Value::String(interpolate(code, args)));
            }
            DocNode::Section { name, nodes } => {
                m.insert("type".into(), Value::String("section".into()));
                m.insert("name".into(), Value::String(name.clone()));
                m.insert(
                    "nodes".into(),
                    Value::List(nodes.iter().map(|n| node_to_value(n, args)).collect()),
                );
            }
            DocNode::List { ordered, items } => {
                m.insert("type".into(), Value::String("list".into()));
                m.insert("ordered".into(), Value::Bool(*ordered));
                m.insert(
                    "items".into(),
                    Value::List(
                        items
                            .iter()
                            .map(|i| Value::String(interpolate(i, args)))
                            .collect(),
                    ),
                );
            }
            DocNode::Table { headers } => {
                m.insert("type".into(), Value::String("table".into()));
                m.insert(
                    "headers".into(),
                    Value::List(
                        headers
                            .iter()
                            .map(|h| Value::String(interpolate(h, args)))
                            .collect(),
                    ),
                );
            }
            DocNode::Link { src, text } => {
                m.insert("type".into(), Value::String("link".into()));
                m.insert("src".into(), Value::String(interpolate(src, args)));
                m.insert("text".into(), Value::String(interpolate(text, args)));
            }
            DocNode::Img { src, alt } => {
                m.insert("type".into(), Value::String("img".into()));
                m.insert("src".into(), Value::String(interpolate(src, args)));
                m.insert(
                    "alt".into(),
                    Value::String(
                        alt.as_deref()
                            .map(|a| interpolate(a, args))
                            .unwrap_or_default(),
                    ),
                );
            }
            DocNode::Span { content } => {
                m.insert("type".into(), Value::String("span".into()));
                m.insert("content".into(), Value::String(interpolate(content, args)));
            }
            DocNode::Include { call } => {
                m.insert("type".into(), Value::String("include".into()));
                m.insert("call".into(), Value::String(interpolate(call, args)));
            }
            DocNode::AsIs { content } => {
                m.insert("type".into(), Value::String("asis".into()));
                m.insert("content".into(), Value::String(interpolate(content, args)));
            }
        }
        Value::Map(m)
    }

    crate::runtime::Value::List(nodes.iter().map(|n| node_to_value(n, args)).collect())
}

/// Replace `${param}` placeholders in `text` with their corresponding values
/// from `args`.
fn interpolate(text: &str, args: &HashMap<String, String>) -> String {
    if args.is_empty() || !text.contains("${") {
        return text.to_string();
    }
    let mut result = text.to_string();
    for (name, value) in args {
        let placeholder = format!("${{{name}}}");
        result = result.replace(&placeholder, value);
    }
    result
}


// ── Tests ─────────────────────────────────────────────────────────────────────

#[cfg(test)]
mod tests {
    use super::*;

    fn make_tree(body: &str) -> DocTemplateTree {
        parse_doc_block_tree(&["md".to_string()], "test", &[], &format!("{{{body}}}")).unwrap()
    }

    #[test]
    fn parses_heading_levels() {
        let tree = make_tree("\n  # Hello\n  ## World\n  ### Deep\n  #(4) Four\n");
        assert_eq!(
            tree.nodes,
            vec![
                DocNode::Heading {
                    level: 1,
                    title: "Hello".to_string()
                },
                DocNode::Heading {
                    level: 2,
                    title: "World".to_string()
                },
                DocNode::Heading {
                    level: 3,
                    title: "Deep".to_string()
                },
                DocNode::Heading {
                    level: 4,
                    title: "Four".to_string()
                },
            ]
        );
    }

    #[test]
    fn parses_plain_text() {
        let tree = make_tree("\n  Hello world\n  more text\n");
        assert_eq!(
            tree.nodes,
            vec![DocNode::Text {
                content: "Hello world\nmore text".to_string()
            }]
        );
    }

    #[test]
    fn parses_code_block() {
        let tree = make_tree("\n  [code \"rust\"\n    fn main() {}\n  ]\n");
        assert!(matches!(&tree.nodes[0], DocNode::Code { lang, .. } if lang == "rust"));
    }

    #[test]
    fn parses_img_block() {
        let tree = make_tree("\n  [img \"path/to/img.png\" \"Alt text\"]\n");
        assert_eq!(
            tree.nodes,
            vec![DocNode::Img {
                src: "path/to/img.png".to_string(),
                alt: Some("Alt text".to_string()),
            }]
        );
    }

    #[test]
    fn parses_link_block() {
        let tree = make_tree("\n  [link \"https://example.com\" \"Click here\"]\n");
        assert_eq!(
            tree.nodes,
            vec![DocNode::Link {
                src: "https://example.com".to_string(),
                text: "Click here".to_string(),
            }]
        );
    }

    #[test]
    fn parses_span_block() {
        let tree = make_tree("\n  [span \"inline text\"]\n");
        assert_eq!(
            tree.nodes,
            vec![DocNode::Span {
                content: "inline text".to_string()
            }]
        );
    }

    #[test]
    fn parses_unordered_list() {
        let tree = make_tree("\n  [list \"unordered\"\n    - Alpha\n    - Beta\n  ]\n");
        assert_eq!(
            tree.nodes,
            vec![DocNode::List {
                ordered: false,
                items: vec!["Alpha".to_string(), "Beta".to_string()],
            }]
        );
    }

    #[test]
    fn parses_ordered_list() {
        let tree = make_tree("\n  [list \"ordered\"\n    - First\n    - Second\n  ]\n");
        assert_eq!(
            tree.nodes,
            vec![DocNode::List {
                ordered: true,
                items: vec!["First".to_string(), "Second".to_string()],
            }]
        );
    }

    #[test]
    fn parses_table() {
        let tree = make_tree("\n  [table \"Col A\" | \"Col B\"]\n");
        assert_eq!(
            tree.nodes,
            vec![DocNode::Table {
                headers: vec!["Col A".to_string(), "Col B".to_string()],
            }]
        );
    }

    #[test]
    fn parses_include() {
        let tree = make_tree("\n  [include myTemplate(arg1)]\n");
        assert_eq!(
            tree.nodes,
            vec![DocNode::Include {
                call: "myTemplate(arg1)".to_string()
            }]
        );
    }

    #[test]
    fn parses_asis() {
        let tree = make_tree("\n  [asis\n    verbatim content\n  ]\n");
        assert!(
            matches!(&tree.nodes[0], DocNode::AsIs { content } if content.contains("verbatim"))
        );
    }

    #[test]
    fn parses_section() {
        let tree = make_tree("\n  [section \"intro\"\n    Some text\n  ]\n");
        match &tree.nodes[0] {
            DocNode::Section { name, nodes } => {
                assert_eq!(name, "intro");
                assert!(matches!(&nodes[0], DocNode::Text { .. }));
            }
            other => panic!("expected Section, got {other:?}"),
        }
    }

    #[test]
    fn renders_md_heading() {
        let tree = make_tree("\n  ## Hello\n");
        let out = render_doc_tree(&tree, "md", &HashMap::new());
        assert_eq!(out, "## Hello\n\n");
    }

    #[test]
    fn renders_html_heading() {
        let tree = make_tree("\n  # Title\n");
        let out = render_doc_tree(&tree, "html", &HashMap::new());
        assert_eq!(out, "<h1>Title</h1>\n");
    }

    #[test]
    fn interpolates_params() {
        let tree = make_tree("\n  # Hello ${name}\n");
        let mut args = HashMap::new();
        args.insert("name".to_string(), "World".to_string());
        let out = render_doc_tree(&tree, "md", &args);
        assert!(out.contains("Hello World"));
    }

    #[test]
    fn renders_md_code_block() {
        let tree = make_tree("\n  [code \"rust\"\n    fn main() {}\n  ]\n");
        let out = render_doc_tree(&tree, "md", &HashMap::new());
        assert!(out.contains("```rust"));
        assert!(out.contains("fn main()"));
    }

    #[test]
    fn renders_html_list() {
        let tree = make_tree("\n  [list \"unordered\"\n    - Alpha\n    - Beta\n  ]\n");
        let out = render_doc_tree(&tree, "html", &HashMap::new());
        assert!(out.contains("<ul>"));
        assert!(out.contains("<li>Alpha</li>"));
    }

    #[test]
    fn renders_html_section() {
        let tree = make_tree("\n  [section \"intro\"\n    Hello\n  ]\n");
        let out = render_doc_tree(&tree, "html", &HashMap::new());
        assert!(out.contains("<section id=\"intro\">"));
        assert!(out.contains("</section>"));
    }
}
