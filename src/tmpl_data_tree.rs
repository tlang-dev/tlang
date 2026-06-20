// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Parser for structured data template content (`data [lang] name(params) { … }`).
//!
//! A data template describes format-agnostic structured data that can be
//! rendered into HTML, JSON, YAML, TOML, XML, or any other key/value /
//! hierarchical format by a matching generator.
//!
//! Syntax overview:
//!
//! ```text
//! data [html, json] myCard(title: String, items: List) {
//!   div(class: "card") {
//!     h1: "${title}",
//!     ul {
//!       <[ renderItems(items) ]>
//!     }
//!   }
//! }
//! ```
//!
//! Each data bloc is:  `(name)? (inline_attrs)? (children)?`
//! - `name`          — optional tag / key identifier (may contain `${…}`).
//! - `inline_attrs`  — `(key: value, …)` — like HTML attributes or constructor args.
//! - `children`      — `{ key: value, … }` — body attributes, nested blocs, includes.

use crate::ast::TemplateParam;
use crate::tree_context::TreeContext;

// ── Public tree types ─────────────────────────────────────────────────────────

/// The validated, parsed form of a complete `data […] name(params) { … }` declaration.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct TmplDataBlockTree {
    /// Language targets this template produces output for (e.g. `["html", "json"]`).
    pub langs: Vec<String>,
    /// Template name (e.g. `myCard`).
    pub name: String,
    /// Template parameters.
    pub params: Vec<TemplateParam>,
    /// Parsed content of the outer `{…}` block.
    pub content: TmplDataBlocTree,
    pub context: TreeContext,
}

/// A structured data node — tag name + inline attributes + body children.
///
/// All three parts are optional.  Examples:
/// - `div(class: "x") { p: "hello" }` — HTML-style element
/// - `{ name: "app", version: "1.0" }` — JSON-style object
/// - `root`                            — bare name only
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct TmplDataBlocTree {
    /// Tag / element name, possibly interpolated: `div`, `${prefix}Node`, `` `my-tag` ``.
    pub name: Option<String>,
    /// Attributes written in parentheses: `tag(key: val, key2: val2)`.
    pub inline_attrs: Vec<TmplDataAttrTree>,
    /// Attributes / children written in braces: `tag { key: val, child { … } }`.
    pub children: Vec<TmplDataAttrTree>,
}

/// A single attribute inside a data bloc — either an include directive or a
/// key/value pair.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum TmplDataAttrTree {
    /// `<[ call() ]>` — delegates to a helper function at generation time.
    Include(String),
    /// `key: value` or positional (unnamed) `value`.
    Set(TmplDataSetTree),
}

/// A key/value attribute.  The key is `None` for positional (unnamed) values.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct TmplDataSetTree {
    /// Attribute name (`"class"`, `type`, `${prefix}Key`).  `None` for
    /// positional children (e.g. a nested bloc without an explicit key).
    pub key: Option<String>,
    pub value: TmplDataValueTree,
}

/// The value part of an attribute.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum TmplDataValueTree {
    /// A nested data bloc: `child(attr: "x") { grandchild: "y" }`.
    Bloc(Box<TmplDataBlocTree>),
    /// An array literal: `["a", "b", 42]`.
    Array(Vec<TmplDataAttrTree>),
    /// A string value, possibly containing `${…}` interpolation.
    Str(String),
    /// A numeric literal (`42`, `3.14`, `-7`).
    Number(String),
    /// A boolean literal (`true` / `false`).
    Bool(bool),
}

// ── Error type ────────────────────────────────────────────────────────────────

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct TmplDataTreeError(pub String);

impl std::fmt::Display for TmplDataTreeError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.0)
    }
}
impl std::error::Error for TmplDataTreeError {}

// ── Entry point ───────────────────────────────────────────────────────────────

/// Parse a complete data-template body.
///
/// `content` is the raw `balanced_block` text returned by the PEG parser —
/// it **includes** the surrounding `{…}` braces.
pub fn parse_tmpl_data_block_tree(
    langs: &[String],
    name: &str,
    params: &[TemplateParam],
    content: &str,
) -> Result<TmplDataBlockTree, TmplDataTreeError> {
    let inner = strip_outer_braces(content);
    let mut pos = 0usize;
    let bloc = parse_data_bloc(inner, &mut pos)?;
    Ok(TmplDataBlockTree {
        langs: langs.to_vec(),
        name: name.to_string(),
        params: params.to_vec(),
        content: bloc,
        context: TreeContext::default(),
    })
}

// ── Core bloc parser ──────────────────────────────────────────────────────────

/// Parse a `(name)? (inline_attrs)? (children)?` bloc starting at `*pos`.
/// Advances `*pos` past everything consumed.
fn parse_data_bloc(text: &str, pos: &mut usize) -> Result<TmplDataBlocTree, TmplDataTreeError> {
    skip_ws(text, pos);

    // Optional tag / element name.
    let name = try_read_name(text, pos);

    skip_ws(text, pos);

    // Optional inline attributes in `(…)`.
    let inline_attrs = if peek(text, *pos) == Some(b'(') {
        *pos += 1;
        let attrs = parse_attr_list(text, pos, b')')?;
        expect_char(text, pos, b')');
        attrs
    } else {
        vec![]
    };

    skip_ws(text, pos);

    // Optional body / children in `{…}`.
    let children = if peek(text, *pos) == Some(b'{') {
        *pos += 1;
        let attrs = parse_attr_list(text, pos, b'}')?;
        expect_char(text, pos, b'}');
        attrs
    } else {
        vec![]
    };

    Ok(TmplDataBlocTree {
        name,
        inline_attrs,
        children,
    })
}

// ── Attribute list ────────────────────────────────────────────────────────────

/// Parse comma-separated attributes until `terminator` byte or end of input.
fn parse_attr_list(
    text: &str,
    pos: &mut usize,
    terminator: u8,
) -> Result<Vec<TmplDataAttrTree>, TmplDataTreeError> {
    let mut attrs = Vec::new();
    loop {
        skip_ws_commas(text, pos);
        if *pos >= text.len() || text.as_bytes()[*pos] == terminator {
            break;
        }
        attrs.push(parse_single_attr(text, pos)?);
    }
    Ok(attrs)
}

fn parse_single_attr(text: &str, pos: &mut usize) -> Result<TmplDataAttrTree, TmplDataTreeError> {
    skip_ws(text, pos);

    // Include directive: `<[ … ]>`
    if text[*pos..].starts_with("<[") {
        return parse_include(text, pos);
    }

    parse_set_attr(text, pos)
}

fn parse_include(text: &str, pos: &mut usize) -> Result<TmplDataAttrTree, TmplDataTreeError> {
    *pos += 2; // skip `<[`
    let start = *pos;
    while *pos + 1 < text.len()
        && !(text.as_bytes()[*pos] == b']' && text.as_bytes()[*pos + 1] == b'>')
    {
        *pos += 1;
    }
    let call = text[start..*pos].trim().to_string();
    if *pos + 1 < text.len() {
        *pos += 2; // skip `]>`
    }
    Ok(TmplDataAttrTree::Include(call))
}

fn parse_set_attr(text: &str, pos: &mut usize) -> Result<TmplDataAttrTree, TmplDataTreeError> {
    // Strategy: tentatively read a candidate key (identifier or string), then
    // look for `:`.  If found, the candidate is the key and what follows is the
    // value.  If not found, the candidate was really a value (or the start of a
    // nested bloc), so we restore `pos` and re-parse as a plain value.
    let saved = *pos;
    let candidate_key = try_read_name(text, pos);

    skip_ws(text, pos);

    if candidate_key.is_some() && peek(text, *pos) == Some(b':') {
        *pos += 1; // consume `:`
        skip_ws(text, pos);
        let value = parse_value(text, pos)?;
        return Ok(TmplDataAttrTree::Set(TmplDataSetTree {
            key: candidate_key,
            value,
        }));
    }

    // No colon — restore and parse a positional value (may be a nested bloc).
    *pos = saved;
    let value = parse_value(text, pos)?;
    Ok(TmplDataAttrTree::Set(TmplDataSetTree { key: None, value }))
}

// ── Value parser ──────────────────────────────────────────────────────────────

fn parse_value(text: &str, pos: &mut usize) -> Result<TmplDataValueTree, TmplDataTreeError> {
    skip_ws(text, pos);

    if *pos >= text.len() {
        return Err(TmplDataTreeError(
            "unexpected end of input while reading value".to_string(),
        ));
    }

    let bytes = text.as_bytes();

    match bytes[*pos] {
        // Array literal: `[ … ]`
        b'[' => {
            *pos += 1;
            let items = parse_attr_list(text, pos, b']')?;
            expect_char(text, pos, b']');
            Ok(TmplDataValueTree::Array(items))
        }

        // String literal: `"…"`
        b'"' => {
            let s = read_string(text, pos)?;
            Ok(TmplDataValueTree::Str(s))
        }

        // s"…" interpolated string (strip `s` prefix then read as normal string)
        b's' if *pos + 1 < bytes.len() && bytes[*pos + 1] == b'"' => {
            *pos += 1; // skip `s`
            let s = read_string(text, pos)?;
            Ok(TmplDataValueTree::Str(s))
        }

        // Negative number
        b'-' if *pos + 1 < bytes.len() && bytes[*pos + 1].is_ascii_digit() => {
            let n = read_number(text, pos);
            Ok(TmplDataValueTree::Number(n))
        }

        // Positive number
        b'0'..=b'9' => {
            let n = read_number(text, pos);
            Ok(TmplDataValueTree::Number(n))
        }

        // Identifier: `true`, `false`, a tag name, or bare string-like word
        b'a'..=b'z' | b'A'..=b'Z' | b'_' | b'`' => {
            let word_start = *pos;
            let word = read_raw_id(text, pos);
            skip_ws(text, pos);

            match word.as_str() {
                "true" => return Ok(TmplDataValueTree::Bool(true)),
                "false" => return Ok(TmplDataValueTree::Bool(false)),
                _ => {}
            }

            // If followed by `(` or `{`, this identifier is the name of a nested bloc.
            if peek(text, *pos) == Some(b'(') || peek(text, *pos) == Some(b'{') {
                *pos = word_start; // restore so parse_data_bloc re-reads the name
                let bloc = parse_data_bloc(text, pos)?;
                return Ok(TmplDataValueTree::Bloc(Box::new(bloc)));
            }

            // Plain identifier treated as a string-like value.
            Ok(TmplDataValueTree::Str(word))
        }

        // Interpolated identifier: `${expr}` or `prefix${expr}suffix`
        b'$' if *pos + 1 < bytes.len() && bytes[*pos + 1] == b'{' => {
            let word_start = *pos;
            let word = read_raw_id(text, pos);
            skip_ws(text, pos);

            if peek(text, *pos) == Some(b'(') || peek(text, *pos) == Some(b'{') {
                *pos = word_start;
                let bloc = parse_data_bloc(text, pos)?;
                return Ok(TmplDataValueTree::Bloc(Box::new(bloc)));
            }

            Ok(TmplDataValueTree::Str(word))
        }

        // Anonymous bare object: `{ key: val, … }`
        b'{' => {
            let bloc = parse_data_bloc(text, pos)?;
            Ok(TmplDataValueTree::Bloc(Box::new(bloc)))
        }

        other => Err(TmplDataTreeError(format!(
            "unexpected character '{}' while reading value",
            char::from(other)
        ))),
    }
}

// ── Token readers ─────────────────────────────────────────────────────────────

/// Try to read a tag/key name at `*pos`.  Handles plain identifiers,
/// backtick-escaped identifiers, `${…}` interpolations (possibly with a
/// prefix/suffix word), and double-quoted strings.
/// Returns `None` and does not advance if none of those patterns match.
fn try_read_name(text: &str, pos: &mut usize) -> Option<String> {
    skip_ws(text, pos);
    if *pos >= text.len() {
        return None;
    }
    let bytes = text.as_bytes();
    match bytes[*pos] {
        b'"' => read_string(text, pos).ok(),
        b'`' => {
            *pos += 1;
            let start = *pos;
            while *pos < text.len() && bytes[*pos] != b'`' {
                *pos += 1;
            }
            let id = text[start..*pos].to_string();
            if *pos < text.len() {
                *pos += 1;
            }
            Some(id)
        }
        b'a'..=b'z' | b'A'..=b'Z' | b'_' => Some(read_raw_id(text, pos)),
        b'$' if *pos + 1 < bytes.len() && bytes[*pos + 1] == b'{' => Some(read_raw_id(text, pos)),
        _ => None,
    }
}

/// Read an identifier token which may contain embedded `${…}` interpolations.
/// Examples: `myName`, `${prefix}Widget`, `item${suffix}`.
fn read_raw_id(text: &str, pos: &mut usize) -> String {
    let mut result = String::new();
    let bytes = text.as_bytes();

    loop {
        if *pos >= bytes.len() {
            break;
        }
        let ch = bytes[*pos];
        match ch {
            b'a'..=b'z' | b'A'..=b'Z' | b'0'..=b'9' | b'_' | b'-' => {
                result.push(char::from(ch));
                *pos += 1;
            }
            // Allow `:` when immediately followed by an identifier character so
            // that `hx-on:click` (HTMX 2.0 event syntax) is treated as a single
            // attribute name.  A lone `:` (key-value separator) or `://` (URL
            // scheme) is NOT consumed here because `/` and end-of-token chars
            // are not identifier bytes.
            b':' if *pos + 1 < bytes.len()
                && (bytes[*pos + 1].is_ascii_alphanumeric() || bytes[*pos + 1] == b'_') =>
            {
                result.push(':');
                *pos += 1;
            }
            b'$' if *pos + 1 < bytes.len() && bytes[*pos + 1] == b'{' => {
                let seg_start = *pos;
                *pos += 2; // skip `${`
                let mut depth = 1usize;
                while *pos < bytes.len() && depth > 0 {
                    match bytes[*pos] {
                        b'{' => {
                            depth += 1;
                            *pos += 1;
                        }
                        b'}' => {
                            depth -= 1;
                            *pos += 1;
                        }
                        _ => {
                            *pos += 1;
                        }
                    }
                }
                result.push_str(&text[seg_start..*pos]);
            }
            _ => break,
        }
    }
    result
}

/// Read a `"…"` double-quoted string literal.  Backslash escapes are
/// preserved verbatim.  `${…}` sequences inside are kept as-is.
fn read_string(text: &str, pos: &mut usize) -> Result<String, TmplDataTreeError> {
    if *pos >= text.len() || text.as_bytes()[*pos] != b'"' {
        return Err(TmplDataTreeError("expected '\"'".to_string()));
    }
    *pos += 1; // skip opening `"`
    let mut result = String::new();
    let bytes = text.as_bytes();
    while *pos < bytes.len() {
        match bytes[*pos] {
            b'\\' => {
                result.push('\\');
                *pos += 1;
                if *pos < bytes.len() {
                    // Preserve the escaped character literally.
                    let c = text[*pos..].chars().next().unwrap();
                    result.push(c);
                    *pos += c.len_utf8();
                }
            }
            b'"' => {
                *pos += 1;
                return Ok(result);
            }
            _ => {
                let c = text[*pos..].chars().next().unwrap();
                result.push(c);
                *pos += c.len_utf8();
            }
        }
    }
    Err(TmplDataTreeError("unterminated string literal".to_string()))
}

/// Read a numeric literal (`42`, `-7`, `3.14`).
fn read_number(text: &str, pos: &mut usize) -> String {
    let start = *pos;
    let bytes = text.as_bytes();
    if *pos < bytes.len() && bytes[*pos] == b'-' {
        *pos += 1;
    }
    while *pos < bytes.len() {
        let ch = bytes[*pos];
        #[allow(clippy::if_same_then_else)] // Both branches do `*pos += 1` but for distinct
        // lexical categories: decimal digits/dots vs. scientific-notation exponent markers.
        if ch.is_ascii_digit() || ch == b'.' {
            *pos += 1;
        } else if (ch == b'e' || ch == b'E')
            && *pos + 1 < bytes.len()
            && (bytes[*pos + 1].is_ascii_digit()
                || bytes[*pos + 1] == b'+'
                || bytes[*pos + 1] == b'-')
        {
            *pos += 1;
        } else {
            break;
        }
    }
    text[start..*pos].to_string()
}

// ── Whitespace / utility helpers ──────────────────────────────────────────────

fn skip_ws(text: &str, pos: &mut usize) {
    let bytes = text.as_bytes();
    loop {
        if *pos >= bytes.len() {
            break;
        }
        match bytes[*pos] {
            b' ' | b'\t' | b'\n' | b'\r' => *pos += 1,
            // Single-line comment `// …`
            b'/' if *pos + 1 < bytes.len() && bytes[*pos + 1] == b'/' => {
                while *pos < bytes.len() && bytes[*pos] != b'\n' {
                    *pos += 1;
                }
            }
            _ => break,
        }
    }
}

/// Skip whitespace AND commas (used between list items).
fn skip_ws_commas(text: &str, pos: &mut usize) {
    let bytes = text.as_bytes();
    loop {
        if *pos >= bytes.len() {
            break;
        }
        match bytes[*pos] {
            b' ' | b'\t' | b'\n' | b'\r' | b',' => *pos += 1,
            b'/' if *pos + 1 < bytes.len() && bytes[*pos + 1] == b'/' => {
                while *pos < bytes.len() && bytes[*pos] != b'\n' {
                    *pos += 1;
                }
            }
            _ => break,
        }
    }
}

fn peek(text: &str, pos: usize) -> Option<u8> {
    text.as_bytes().get(pos).copied()
}

fn expect_char(text: &str, pos: &mut usize, expected: u8) {
    if *pos < text.len() && text.as_bytes()[*pos] == expected {
        *pos += 1;
    }
}

fn strip_outer_braces(text: &str) -> &str {
    let text = text.trim();
    if text.starts_with('{') && text.ends_with('}') {
        &text[1..text.len() - 1]
    } else {
        text
    }
}

// ── Tests ─────────────────────────────────────────────────────────────────────

#[cfg(test)]
mod tests {
    use super::*;
    use crate::ast::TemplateParam;

    /// Parse the content of a data bloc (without outer `{…}` wrapper).
    fn parse(content: &str) -> TmplDataBlocTree {
        parse_tmpl_data_block_tree(
            &["html".to_string()],
            "test",
            &[],
            &format!("{{{}}}", content),
        )
        .expect("should parse without error")
        .content
    }

    // ── Basic structure ───────────────────────────────────────────────────────

    #[test]
    fn parses_empty_bloc() {
        let bloc = parse("");
        assert!(bloc.name.is_none());
        assert!(bloc.inline_attrs.is_empty());
        assert!(bloc.children.is_empty());
    }

    #[test]
    fn parses_bare_name() {
        let bloc = parse("root");
        assert_eq!(bloc.name.as_deref(), Some("root"));
        assert!(bloc.inline_attrs.is_empty());
        assert!(bloc.children.is_empty());
    }

    #[test]
    fn parses_name_with_empty_inline_attrs() {
        let bloc = parse("div()");
        assert_eq!(bloc.name.as_deref(), Some("div"));
        assert!(bloc.inline_attrs.is_empty());
    }

    #[test]
    fn parses_name_with_inline_string_attr() {
        let bloc = parse(r#"div(class: "card")"#);
        assert_eq!(bloc.name.as_deref(), Some("div"));
        assert_eq!(bloc.inline_attrs.len(), 1);
        match &bloc.inline_attrs[0] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.key.as_deref(), Some("class"));
                assert_eq!(s.value, TmplDataValueTree::Str("card".to_string()));
            }
            _ => panic!("expected set attr"),
        }
    }

    #[test]
    fn parses_name_with_multiple_inline_attrs() {
        let bloc = parse(r#"input(type: "text", disabled: true)"#);
        assert_eq!(bloc.inline_attrs.len(), 2);
        match &bloc.inline_attrs[1] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.key.as_deref(), Some("disabled"));
                assert_eq!(s.value, TmplDataValueTree::Bool(true));
            }
            _ => panic!(),
        }
    }

    #[test]
    fn parses_name_with_body_children() {
        let bloc = parse(r#"root { name: "app", version: "1.0" }"#);
        assert_eq!(bloc.name.as_deref(), Some("root"));
        assert_eq!(bloc.children.len(), 2);
    }

    // ── Value types ───────────────────────────────────────────────────────────

    #[test]
    fn parses_string_value() {
        let bloc = parse(r#"{ title: "Hello World" }"#);
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.key.as_deref(), Some("title"));
                assert_eq!(s.value, TmplDataValueTree::Str("Hello World".to_string()));
            }
            _ => panic!("expected set"),
        }
    }

    #[test]
    fn parses_interpolated_string_value() {
        let bloc = parse(r#"{ name: "${appName}" }"#);
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.value, TmplDataValueTree::Str("${appName}".to_string()));
            }
            _ => panic!(),
        }
    }

    #[test]
    fn parses_integer_number_value() {
        let bloc = parse("{ port: 8080 }");
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.value, TmplDataValueTree::Number("8080".to_string()));
            }
            _ => panic!("expected set"),
        }
    }

    #[test]
    fn parses_negative_number_value() {
        let bloc = parse("{ offset: -5 }");
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.value, TmplDataValueTree::Number("-5".to_string()));
            }
            _ => panic!(),
        }
    }

    #[test]
    fn parses_float_number_value() {
        let bloc = parse("{ ratio: 3.14 }");
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.value, TmplDataValueTree::Number("3.14".to_string()));
            }
            _ => panic!(),
        }
    }

    #[test]
    fn parses_bool_true() {
        let bloc = parse("{ enabled: true }");
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.value, TmplDataValueTree::Bool(true));
            }
            _ => panic!(),
        }
    }

    #[test]
    fn parses_bool_false() {
        let bloc = parse("{ debug: false }");
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.value, TmplDataValueTree::Bool(false));
            }
            _ => panic!(),
        }
    }

    #[test]
    fn parses_array_of_strings() {
        let bloc = parse(r#"{ tags: ["api", "v1", "stable"] }"#);
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => match &s.value {
                TmplDataValueTree::Array(items) => {
                    assert_eq!(items.len(), 3);
                    match &items[0] {
                        TmplDataAttrTree::Set(i) => {
                            assert_eq!(i.value, TmplDataValueTree::Str("api".to_string()));
                        }
                        _ => panic!(),
                    }
                }
                _ => panic!("expected array"),
            },
            _ => panic!("expected set"),
        }
    }

    #[test]
    fn parses_array_of_mixed_values() {
        let bloc = parse(r#"{ values: ["text", 42, true] }"#);
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => match &s.value {
                TmplDataValueTree::Array(items) => assert_eq!(items.len(), 3),
                _ => panic!("expected array"),
            },
            _ => panic!(),
        }
    }

    // ── Includes ──────────────────────────────────────────────────────────────

    #[test]
    fn parses_include_directive_in_body() {
        let bloc = parse("{ <[ renderItems(items) ]> }");
        assert_eq!(bloc.children.len(), 1);
        match &bloc.children[0] {
            TmplDataAttrTree::Include(call) => {
                assert_eq!(call, "renderItems(items)");
            }
            _ => panic!("expected include"),
        }
    }

    #[test]
    fn parses_include_directive_in_inline_attrs() {
        let bloc = parse("div(<[ buildAttrs() ]>)");
        assert_eq!(bloc.inline_attrs.len(), 1);
        match &bloc.inline_attrs[0] {
            TmplDataAttrTree::Include(call) => {
                assert_eq!(call, "buildAttrs()");
            }
            _ => panic!("expected include"),
        }
    }

    // ── Nested blocs ──────────────────────────────────────────────────────────

    #[test]
    fn parses_nested_bloc_as_child() {
        let bloc = parse(r#"root { child { value: 1 } }"#);
        assert_eq!(bloc.children.len(), 1);
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert!(s.key.is_none());
                match &s.value {
                    TmplDataValueTree::Bloc(inner) => {
                        assert_eq!(inner.name.as_deref(), Some("child"));
                        assert_eq!(inner.children.len(), 1);
                    }
                    _ => panic!("expected nested bloc"),
                }
            }
            _ => panic!("expected set"),
        }
    }

    #[test]
    fn parses_nested_bloc_as_keyed_value() {
        let bloc = parse(r#"{ meta: node { author: "alice" } }"#);
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.key.as_deref(), Some("meta"));
                match &s.value {
                    TmplDataValueTree::Bloc(inner) => {
                        assert_eq!(inner.name.as_deref(), Some("node"));
                    }
                    _ => panic!("expected bloc value"),
                }
            }
            _ => panic!("expected set"),
        }
    }

    #[test]
    fn parses_anonymous_bare_object() {
        let bloc = parse(r#"{ meta { author: "alice" } }"#);
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert!(s.key.is_none());
                match &s.value {
                    TmplDataValueTree::Bloc(inner) => {
                        assert_eq!(inner.name.as_deref(), Some("meta"));
                    }
                    _ => panic!("expected nested bloc"),
                }
            }
            _ => panic!(),
        }
    }

    #[test]
    fn parses_nested_bloc_with_inline_and_body_attrs() {
        let bloc = parse(r#"div(class: "card") { h2: "Title" }"#);
        assert_eq!(bloc.name.as_deref(), Some("div"));
        assert_eq!(bloc.inline_attrs.len(), 1);
        assert_eq!(bloc.children.len(), 1);
    }

    // ── Multi-level nesting ───────────────────────────────────────────────────

    #[test]
    fn parses_deeply_nested_structure() {
        let bloc = parse(
            r#"
            html {
                head { title: "My App" },
                body {
                    div(class: "container") {
                        h1: "Welcome",
                        p: "Hello"
                    }
                }
            }
        "#,
        );
        assert_eq!(bloc.name.as_deref(), Some("html"));
        assert_eq!(bloc.children.len(), 2);

        match &bloc.children[1] {
            TmplDataAttrTree::Set(s) => match &s.value {
                TmplDataValueTree::Bloc(body) => {
                    assert_eq!(body.name.as_deref(), Some("body"));
                    assert_eq!(body.children.len(), 1);
                    match &body.children[0] {
                        TmplDataAttrTree::Set(inner_s) => match &inner_s.value {
                            TmplDataValueTree::Bloc(div) => {
                                assert_eq!(div.name.as_deref(), Some("div"));
                                assert_eq!(div.inline_attrs.len(), 1);
                                assert_eq!(div.children.len(), 2);
                            }
                            _ => panic!("expected div bloc"),
                        },
                        _ => panic!(),
                    }
                }
                _ => panic!("expected body bloc"),
            },
            _ => panic!(),
        }
    }

    // ── Block-level validation ────────────────────────────────────────────────

    #[test]
    fn parses_multi_lang_data_block_tree() {
        let result = parse_tmpl_data_block_tree(
            &["json".to_string(), "yaml".to_string()],
            "config",
            &[
                TemplateParam {
                    name: "appName".to_string(),
                    ty: "String".to_string(),
                },
                TemplateParam {
                    name: "debug".to_string(),
                    ty: "Bool".to_string(),
                },
            ],
            r#"{ root { name: "${appName}", debug: false } }"#,
        );
        let tree = result.expect("should parse");
        assert_eq!(tree.langs, vec!["json", "yaml"]);
        assert_eq!(tree.name, "config");
        assert_eq!(tree.params.len(), 2);
        assert_eq!(tree.content.children.len(), 2);
    }

    #[test]
    fn parses_json_style_flat_object() {
        let bloc = parse(
            r#"
            {
                name: "myService",
                version: "2.1.0",
                port: 3000,
                enabled: true,
                tags: ["http", "rest"]
            }
        "#,
        );
        assert!(bloc.name.is_none());
        assert_eq!(bloc.children.len(), 5);
    }

    #[test]
    fn parses_xml_style_element_tree() {
        let bloc = parse(
            r#"
            manifest(version: "1.0", encoding: "UTF-8") {
                application(name: "${appName}") {
                    <[ renderActivities(activities) ]>
                }
            }
        "#,
        );
        assert_eq!(bloc.name.as_deref(), Some("manifest"));
        assert_eq!(bloc.inline_attrs.len(), 2);
        assert_eq!(bloc.children.len(), 1);
    }

    #[test]
    fn parses_yaml_style_nested_object() {
        let bloc = parse(
            r#"
            {
                apiVersion: "apps/v1",
                kind: "Deployment",
                spec {
                    replicas: 3,
                    selector {
                        app: "${appName}"
                    }
                }
            }
        "#,
        );
        assert_eq!(bloc.children.len(), 3);
        match &bloc.children[2] {
            TmplDataAttrTree::Set(s) => match &s.value {
                TmplDataValueTree::Bloc(spec) => {
                    assert_eq!(spec.name.as_deref(), Some("spec"));
                    assert_eq!(spec.children.len(), 2);
                }
                _ => panic!("expected spec bloc"),
            },
            _ => panic!(),
        }
    }

    #[test]
    fn parses_interpolated_tag_name() {
        let bloc = parse(r#"${"div"}(class: "x") { }"#);
        // Interpolated IDs starting with $ are read as the name
        assert!(bloc.name.is_some());
    }

    #[test]
    fn parses_s_prefix_interpolated_string() {
        let bloc = parse(r#"{ label: s"Hello ${name}!" }"#);
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => match &s.value {
                TmplDataValueTree::Str(v) => {
                    assert!(v.contains("Hello "));
                }
                _ => panic!("expected str"),
            },
            _ => panic!(),
        }
    }

    #[test]
    fn handles_trailing_comma() {
        // Trailing commas should not cause a parse error.
        let bloc = parse(r#"{ a: "x", b: "y", }"#);
        assert_eq!(bloc.children.len(), 2);
    }

    #[test]
    fn handles_string_with_escaped_quote() {
        let bloc = parse(r#"{ msg: "say \"hi\"" }"#);
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => match &s.value {
                TmplDataValueTree::Str(v) => {
                    assert!(v.contains("\\\"hi\\\""));
                }
                _ => panic!(),
            },
            _ => panic!(),
        }
    }

    #[test]
    fn handles_colon_in_attribute_name_htmx_on_event() {
        // `hx-on:click` is a valid HTMX 2.0 attribute name — the `:` separates
        // the directive from the event name and must be treated as part of the
        // identifier, not as the key-value separator.
        let bloc = parse(r#"{ button(hx-on:click: "doSomething()") { "click me" } }"#);
        assert_eq!(bloc.children.len(), 1);
        match &bloc.children[0] {
            TmplDataAttrTree::Set(s) => {
                assert_eq!(s.key, None);
                match &s.value {
                    TmplDataValueTree::Bloc(b) => {
                        assert_eq!(b.name.as_deref(), Some("button"));
                        assert_eq!(b.inline_attrs.len(), 1);
                        match &b.inline_attrs[0] {
                            TmplDataAttrTree::Set(attr) => {
                                assert_eq!(attr.key.as_deref(), Some("hx-on:click"));
                                match &attr.value {
                                    TmplDataValueTree::Str(v) => {
                                        assert_eq!(v, "doSomething()");
                                    }
                                    _ => panic!("expected str value for hx-on:click"),
                                }
                            }
                            _ => panic!("expected set attr"),
                        }
                    }
                    _ => panic!("expected bloc value"),
                }
            }
            _ => panic!("expected set"),
        }
    }
}
