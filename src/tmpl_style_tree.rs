// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Parser and renderer for `style [lang, ...] name(params) { ... }` template blocks.
//!
//! # Grammar (from `TLangTmplStyle.g4`)
//!
//! ```text
//! tmplStyle : STYLE '[' langs ']' name '(' params? ')' '{' styleBlocks '}'
//!
//! styleBlocks  : styleStruct*
//!
//! styleStruct  : name? ('[' styleAttribute (',' styleAttribute)* ']')?
//!                '{' (styleAttribute (',' styleAttribute)*)? '}'
//!
//! styleAttribute : styleInclude | styleSetAttribute
//!
//! styleInclude     : '<[' callExpr ']>'
//! styleSetAttribute: (name ':')? value
//!
//! styleValue : '[' styleAttribute (',' styleAttribute)* ']'   // array
//!            | string | ident                                   // string/id
//!            | number                                          // number
//!            | 'true' | 'false'                               // bool
//! ```
//!
//! # Supported output formats
//!
//! | Language key  | Output                              |
//! |---------------|-------------------------------------|
//! | `css`         | Standard CSS rule blocks            |
//! | `scss`        | SCSS (identical to CSS for now)     |
//! | `less`        | LESS (identical to CSS for now)     |
//! | `json`        | JSON object representation          |
//! | `js` / `ts`   | JavaScript/TypeScript object literal|
//!
//! `${param}` placeholders are interpolated using the supplied argument map.
//!
//! # Example
//!
//! ```tlang
//! style [css] Button(color: String, size: String) {
//!     .button [hover] {
//!         background-color: ${color},
//!         font-size: ${size}
//!     }
//! }
//! ```

use std::collections::HashMap;

use crate::ast::TemplateParam;

// ---------------------------------------------------------------------------
// AST types
// ---------------------------------------------------------------------------

/// Top-level parsed style template.
#[derive(Debug, Clone)]
pub struct StyleBlockTree {
    pub langs: Vec<String>,
    pub name: String,
    pub params: Vec<TemplateParam>,
    pub structs: Vec<StyleStruct>,
}

/// A single CSS-like rule block:  `name [modifiers] { attrs }`.
#[derive(Debug, Clone)]
pub struct StyleStruct {
    /// Optional selector / rule name (e.g. `.button`, `h1`, a raw interpolation).
    pub name: Option<String>,
    /// Optional modifier list — the `[mod1, mod2]` part (e.g. `hover`, `focus`).
    pub modifiers: Vec<StyleAttribute>,
    /// Attribute list — the `{ key: value, ... }` body.
    pub attrs: Vec<StyleAttribute>,
}

/// A single attribute inside a style rule, either an include or a key–value pair.
#[derive(Debug, Clone)]
pub enum StyleAttribute {
    /// `<[ callExpr ]>` include directive.
    Include(String),
    /// `name: value` or just `value` (name is `None`).
    Set {
        name: Option<String>,
        value: StyleValue,
    },
}

/// A style value.
#[derive(Debug, Clone)]
pub enum StyleValue {
    /// String literal `"…"` or bare identifier.
    Str(String),
    /// Numeric literal.
    Number(String),
    /// Boolean literal.
    Bool(bool),
    /// Array `[item, item, …]`.
    Array(Vec<StyleAttribute>),
}

// ---------------------------------------------------------------------------
// Public entry points
// ---------------------------------------------------------------------------

/// Parse the raw balanced-block content of a `style` template into a
/// [`StyleBlockTree`].
///
/// `content` is the entire body string including the outer `{ }` braces.
pub fn parse_style_block_tree(
    langs: &[String],
    name: &str,
    params: &[TemplateParam],
    content: &str,
) -> Result<StyleBlockTree, String> {
    // Strip outer braces.
    let inner = strip_outer_braces(content)?;
    let structs = parse_style_blocks(inner.trim())?;
    Ok(StyleBlockTree {
        langs: langs.to_vec(),
        name: name.to_string(),
        params: params.to_vec(),
        structs,
    })
}

// ---------------------------------------------------------------------------
// Parser
// ---------------------------------------------------------------------------

fn strip_outer_braces(s: &str) -> Result<&str, String> {
    let s = s.trim();
    if s.starts_with('{') && s.ends_with('}') {
        Ok(&s[1..s.len() - 1])
    } else {
        Err(format!(
            "style block content must be wrapped in '{{' '}}', got: {}",
            &s[..s.len().min(40)]
        ))
    }
}

/// Parse a sequence of `styleStruct` items from raw text.
fn parse_style_blocks(text: &str) -> Result<Vec<StyleStruct>, String> {
    let mut structs = Vec::new();
    let mut pos = 0;
    let bytes = text.as_bytes();

    while pos < text.len() {
        // Skip whitespace.
        while pos < text.len()
            && (bytes[pos] == b' '
                || bytes[pos] == b'\t'
                || bytes[pos] == b'\n'
                || bytes[pos] == b'\r')
        {
            pos += 1;
        }
        if pos >= text.len() {
            break;
        }

        let (s, consumed) = parse_one_style_struct(&text[pos..])?;
        if consumed == 0 {
            // No progress — bail to avoid infinite loop.
            break;
        }
        pos += consumed;
        structs.push(s);
    }

    Ok(structs)
}

/// Try to parse one `styleStruct` from the front of `text`.
/// Returns `(struct, bytes_consumed)`.
fn parse_one_style_struct(text: &str) -> Result<(StyleStruct, usize), String> {
    let mut pos = 0;
    let bytes = text.as_bytes();

    // ── optional name ────────────────────────────────────────────────────────
    let name = if pos < text.len() && bytes[pos] != b'[' && bytes[pos] != b'{' {
        // Could be an interpolation `${…}` or an identifier / selector.
        let (n, adv) = read_selector_name(&text[pos..]);
        if !n.is_empty() {
            pos += adv;
            skip_whitespace(text, &mut pos);
            Some(n)
        } else {
            None
        }
    } else {
        None
    };

    // ── optional modifier list `[mod1, mod2]` ────────────────────────────────
    let modifiers = if pos < text.len() && bytes[pos] == b'[' {
        let (attrs, adv) = parse_bracket_attr_list(&text[pos..])?;
        pos += adv;
        skip_whitespace(text, &mut pos);
        attrs
    } else {
        Vec::new()
    };

    // ── required body `{ key: value, ... }` ──────────────────────────────────
    if pos >= text.len() || bytes[pos] != b'{' {
        // No body found — this is not a valid struct, return empty with no advance.
        // (Prevents infinite loops on malformed input.)
        if name.is_none() && modifiers.is_empty() {
            return Ok((
                StyleStruct {
                    name: None,
                    modifiers: Vec::new(),
                    attrs: Vec::new(),
                },
                0,
            ));
        }
        return Err(format!(
            "expected '{{' after style rule name{}, got '{}'",
            name.as_deref()
                .map(|n| format!(" `{n}`"))
                .unwrap_or_default(),
            text.chars().nth(pos).unwrap_or('?'),
        ));
    }

    let (attrs, adv) = parse_brace_attr_list(&text[pos..])?;
    pos += adv;

    Ok((
        StyleStruct {
            name,
            modifiers,
            attrs,
        },
        pos,
    ))
}

/// Read a selector name: either `${…}` interpolation or a CSS selector
/// string (which may contain `.`, `#`, `-`, `_`, alphanumeric and `::`).
fn read_selector_name(text: &str) -> (String, usize) {
    let bytes = text.as_bytes();
    let mut pos = 0;
    let mut name = String::new();

    while pos < text.len() {
        // `${…}` interpolation.
        if pos + 1 < text.len() && bytes[pos] == b'$' && bytes[pos + 1] == b'{' {
            let start = pos;
            pos += 2;
            while pos < text.len() && bytes[pos] != b'}' {
                pos += 1;
            }
            if pos < text.len() {
                pos += 1; // consume '}'
            }
            name.push_str(&text[start..pos]);
            continue;
        }
        // Quoted string: treat entire string as name (e.g. `"data-theme":`).
        if bytes[pos] == b'"' {
            let start = pos;
            pos += 1;
            while pos < text.len() && bytes[pos] != b'"' {
                if bytes[pos] == b'\\' {
                    pos += 1;
                }
                pos += 1;
            }
            if pos < text.len() {
                pos += 1;
            }
            name.push_str(&text[start..pos]);
            break; // quoted names stop here
        }
        let b = bytes[pos];
        // CSS selectors can contain: alphanumeric, `.`, `#`, `-`, `_`, `*`, `>`,
        // `+`, `~`, `:`, `[`, `]`, `=`, `"`, `(`, `)`, `%` — but we stop before
        // `{` or `,` which unambiguously start the body or a list separator.
        if b == b'{' || b == b',' || b == b'[' || b == b'}' || b == b'\n' || b == b'\r' {
            break;
        }
        name.push(b as char);
        pos += 1;
    }

    let trimmed = name.trim_end().to_string();
    let consumed = if trimmed.len() < name.len() {
        pos - (name.len() - trimmed.len())
    } else {
        pos
    };
    (trimmed, consumed)
}

/// Parse a `[attr, attr, ...]` bracket-enclosed attribute list.
/// Returns `(attrs, bytes_consumed_including_brackets)`.
fn parse_bracket_attr_list(text: &str) -> Result<(Vec<StyleAttribute>, usize), String> {
    debug_assert!(text.starts_with('['));
    let mut pos = 1; // skip '['
    let mut attrs = Vec::new();

    loop {
        skip_whitespace(text, &mut pos);
        if pos >= text.len() {
            return Err("unterminated '[' in style modifier list".to_string());
        }
        if text.as_bytes()[pos] == b']' {
            pos += 1;
            break;
        }
        if text.as_bytes()[pos] == b',' {
            pos += 1;
            continue;
        }
        let (attr, adv) = parse_one_attribute(&text[pos..])?;
        if adv == 0 {
            break;
        }
        pos += adv;
        attrs.push(attr);
    }

    Ok((attrs, pos))
}

/// Parse a `{ attr, attr, ... }` brace-enclosed attribute list.
/// Returns `(attrs, bytes_consumed_including_braces)`.
fn parse_brace_attr_list(text: &str) -> Result<(Vec<StyleAttribute>, usize), String> {
    debug_assert!(text.starts_with('{'));
    let mut pos = 1; // skip '{'
    let mut attrs = Vec::new();

    loop {
        skip_whitespace(text, &mut pos);
        if pos >= text.len() {
            return Err("unterminated '{' in style attribute block".to_string());
        }
        let b = text.as_bytes()[pos];
        if b == b'}' {
            pos += 1;
            break;
        }
        if b == b',' {
            pos += 1;
            continue;
        }
        let (attr, adv) = parse_one_attribute(&text[pos..])?;
        if adv == 0 {
            // Skip one byte to avoid infinite loop on unrecognised input.
            pos += 1;
            continue;
        }
        pos += adv;
        attrs.push(attr);
    }

    Ok((attrs, pos))
}

/// Parse a single attribute (include or set-attribute).
fn parse_one_attribute(text: &str) -> Result<(StyleAttribute, usize), String> {
    let bytes = text.as_bytes();

    // ── Include `<[ ... ]>` ─────────────────────────────────────────────────
    if text.starts_with("<[") {
        let end = text
            .find("]>")
            .ok_or("unterminated include directive `<[`")?;
        let expr = text[2..end].trim().to_string();
        return Ok((StyleAttribute::Include(expr), end + 2));
    }

    // ── Set attribute `name: value` or just `value` ─────────────────────────
    // We need to figure out whether this is `name: value` or just `value`.
    // Strategy: try to read a name followed by `:`, otherwise treat the whole
    // thing as an anonymous value.

    // Try to read a potential name token.
    let (potential_name, name_end) = read_attr_key(text);

    // Check for ':' after the potential name.
    let mut after_name = name_end;
    skip_whitespace(text, &mut after_name);

    if after_name < text.len() && bytes[after_name] == b':' {
        // `name: value` form.
        let value_start = after_name + 1;
        let (value, value_adv) = read_style_value(&text[value_start..])?;
        let total = value_start + value_adv;
        return Ok((
            StyleAttribute::Set {
                name: Some(potential_name),
                value,
            },
            total,
        ));
    }

    // No colon — anonymous value.
    if potential_name.is_empty() {
        // Nothing recognisable found.
        return Ok((
            StyleAttribute::Set {
                name: None,
                value: StyleValue::Str(String::new()),
            },
            0,
        ));
    }

    // The "name" we read is itself the value.
    Ok((
        StyleAttribute::Set {
            name: None,
            value: StyleValue::Str(potential_name),
        },
        name_end,
    ))
}

/// Read an attribute key: either a quoted string, an interpolation, or a bare
/// identifier-like token (may include `-`).
fn read_attr_key(text: &str) -> (String, usize) {
    let bytes = text.as_bytes();
    let mut pos = 0;
    let mut key = String::new();

    if pos < text.len() && bytes[pos] == b'"' {
        // Quoted key.
        pos += 1;
        while pos < text.len() && bytes[pos] != b'"' {
            if bytes[pos] == b'\\' {
                pos += 1;
            }
            if pos < text.len() {
                key.push(bytes[pos] as char);
                pos += 1;
            }
        }
        if pos < text.len() {
            pos += 1; // closing quote
        }
        return (key, pos);
    }

    // Interpolation `${…}` or bare ident (including `-` for CSS properties).
    while pos < text.len() {
        let b = bytes[pos];
        if b == b'$' && pos + 1 < text.len() && bytes[pos + 1] == b'{' {
            let start = pos;
            pos += 2;
            while pos < text.len() && bytes[pos] != b'}' {
                pos += 1;
            }
            if pos < text.len() {
                pos += 1;
            }
            key.push_str(&text[start..pos]);
            continue;
        }
        if b.is_ascii_alphanumeric() || b == b'-' || b == b'_' {
            key.push(b as char);
            pos += 1;
        } else {
            break;
        }
    }

    (key, pos)
}

/// Read a style value from the front of `text`.
fn read_style_value(text: &str) -> Result<(StyleValue, usize), String> {
    let mut pos = 0;
    skip_whitespace(text, &mut pos);
    let bytes = text.as_bytes();

    if pos >= text.len() {
        return Ok((StyleValue::Str(String::new()), pos));
    }

    // Array value.
    if bytes[pos] == b'[' {
        let (attrs, adv) = parse_bracket_attr_list(&text[pos..])?;
        return Ok((StyleValue::Array(attrs), pos + adv));
    }

    // Quoted string.
    if bytes[pos] == b'"' {
        let start = pos;
        pos += 1;
        while pos < text.len() && bytes[pos] != b'"' {
            if bytes[pos] == b'\\' {
                pos += 1;
            }
            pos += 1;
        }
        if pos < text.len() {
            pos += 1;
        }
        // Store without surrounding quotes.
        let s = text[start + 1..pos - 1].to_string();
        return Ok((StyleValue::Str(s), pos));
    }

    // Interpolation or bare token (ident, number, bool).
    let mut value = String::new();
    while pos < text.len() {
        let b = bytes[pos];
        // Stop at delimiters.
        if b == b',' || b == b'}' || b == b']' || b == b'\n' || b == b'\r' {
            break;
        }
        // `${…}` interpolation fragment.
        if b == b'$' && pos + 1 < text.len() && bytes[pos + 1] == b'{' {
            let start = pos;
            pos += 2;
            while pos < text.len() && bytes[pos] != b'}' {
                pos += 1;
            }
            if pos < text.len() {
                pos += 1;
            }
            value.push_str(&text[start..pos]);
            continue;
        }
        value.push(b as char);
        pos += 1;
    }

    let value = value.trim().to_string();

    // Classify.
    if value == "true" {
        return Ok((StyleValue::Bool(true), pos));
    }
    if value == "false" {
        return Ok((StyleValue::Bool(false), pos));
    }
    // Number detection: optional leading `-`, digits, optional `.digits`.
    if is_number_str(&value) {
        return Ok((StyleValue::Number(value), pos));
    }

    Ok((StyleValue::Str(value), pos))
}

fn is_number_str(s: &str) -> bool {
    if s.is_empty() {
        return false;
    }
    let s = s.strip_prefix('-').unwrap_or(s);
    let mut has_dot = false;
    for c in s.chars() {
        if c == '.' {
            if has_dot {
                return false;
            }
            has_dot = true;
        } else if !c.is_ascii_digit() {
            return false;
        }
    }
    !s.is_empty() && s.chars().next().map(|c| c != '.').unwrap_or(false)
}

fn skip_whitespace(text: &str, pos: &mut usize) {
    let bytes = text.as_bytes();
    while *pos < text.len()
        && (bytes[*pos] == b' '
            || bytes[*pos] == b'\t'
            || bytes[*pos] == b'\n'
            || bytes[*pos] == b'\r')
    {
        *pos += 1;
    }
}

// ---------------------------------------------------------------------------
// Interpolation
// ---------------------------------------------------------------------------

/// Replace all `${name}` placeholders in `text` with the corresponding entry
/// from `args`.  Unknown placeholders are left unchanged.
fn interpolate(text: &str, args: &HashMap<String, String>) -> String {
    if !text.contains("${") {
        return text.to_string();
    }
    let mut result = String::with_capacity(text.len());
    let mut rest = text;
    while let Some(start) = rest.find("${") {
        result.push_str(&rest[..start]);
        let after = &rest[start + 2..];
        if let Some(end) = after.find('}') {
            let key = &after[..end];
            if let Some(val) = args.get(key) {
                result.push_str(val);
            } else {
                result.push_str("${");
                result.push_str(key);
                result.push('}');
            }
            rest = &after[end + 1..];
        } else {
            result.push_str("${");
            rest = after;
        }
    }
    result.push_str(rest);
    result
}

fn interp_value(value: &StyleValue, args: &HashMap<String, String>) -> String {
    match value {
        StyleValue::Str(s) => interpolate(s, args),
        StyleValue::Number(n) => n.clone(),
        StyleValue::Bool(b) => b.to_string(),
        StyleValue::Array(items) => {
            let parts: Vec<String> = items
                .iter()
                .filter_map(|a| match a {
                    StyleAttribute::Set { value, .. } => Some(interp_value(value, args)),
                    StyleAttribute::Include(expr) => {
                        Some(format!("<[{}]>", interpolate(expr, args)))
                    }
                })
                .collect();
            parts.join(", ")
        }
    }
}

// ---------------------------------------------------------------------------
// Value serialisation (for external TLang generators)
// ---------------------------------------------------------------------------

/// Build the CSS selector string for a [`StyleStruct`]: base name plus any
/// modifier pseudo-classes joined with `:`.
fn build_selector(s: &StyleStruct, args: &HashMap<String, String>) -> String {
    let base = s
        .name
        .as_deref()
        .map(|n| interpolate(n, args))
        .unwrap_or_default();

    if s.modifiers.is_empty() {
        return base;
    }

    let mods: Vec<String> = s
        .modifiers
        .iter()
        .filter_map(|m| match m {
            StyleAttribute::Set { value, .. } => Some(interp_value(value, args)),
            StyleAttribute::Include(expr) => Some(format!("<[{}]>", interpolate(expr, args))),
        })
        .collect();

    if base.is_empty() {
        mods.join(", ")
    } else {
        format!("{}:{}", base, mods.join(":"))
    }
}

fn attr_to_value(attr: &StyleAttribute, args: &HashMap<String, String>) -> crate::runtime::Value {
    use std::collections::BTreeMap;
    use crate::runtime::Value;
    let mut m: BTreeMap<String, Value> = BTreeMap::new();
    match attr {
        StyleAttribute::Include(expr) => {
            m.insert("type".into(), Value::String("include".into()));
            m.insert("expr".into(), Value::String(interpolate(expr, args)));
        }
        StyleAttribute::Set { name, value } => {
            m.insert("type".into(), Value::String("set".into()));
            m.insert(
                "name".into(),
                Value::String(
                    name.as_deref()
                        .map(|n| interpolate(n, args))
                        .unwrap_or_default(),
                ),
            );
            m.insert("value".into(), Value::String(interp_value(value, args)));
        }
    }
    crate::runtime::Value::Map(m)
}

/// Serialize a [`StyleBlockTree`] to a `Value::List` of `Value::Map`s so that
/// style generator programs written in TLang can traverse the rule list using
/// `List.get`, `Map.get`, etc.
///
/// All `${}` parameter placeholders are resolved using `args` at this point.
pub fn style_structs_to_value(
    tree: &StyleBlockTree,
    args: &HashMap<String, String>,
) -> crate::runtime::Value {
    use std::collections::BTreeMap;
    use crate::runtime::Value;

    let structs: Vec<Value> = tree
        .structs
        .iter()
        .map(|s| {
            let mut m: BTreeMap<String, Value> = BTreeMap::new();
            m.insert("selector".into(), Value::String(build_selector(s, args)));
            m.insert(
                "attrs".into(),
                Value::List(s.attrs.iter().map(|a| attr_to_value(a, args)).collect()),
            );
            Value::Map(m)
        })
        .collect();

    Value::List(structs)
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use super::*;

    fn args(pairs: &[(&str, &str)]) -> HashMap<String, String> {
        pairs
            .iter()
            .map(|(k, v)| (k.to_string(), v.to_string()))
            .collect()
    }

    #[test]
    fn parse_simple_css_rule() {
        let content = r#"{
            .button {
                background-color: blue,
                font-size: 14px
            }
        }"#;
        let tree =
            parse_style_block_tree(&["css".to_string()], "ButtonStyle", &[], content).unwrap();
        assert_eq!(tree.structs.len(), 1);
        assert_eq!(tree.structs[0].name.as_deref(), Some(".button"));
        assert_eq!(tree.structs[0].attrs.len(), 2);
    }

    #[test]
    fn serialise_css_rule() {
        let content = r#"{
            .button {
                background-color: blue,
                font-size: 14px
            }
        }"#;
        let tree = parse_style_block_tree(&["css".to_string()], "S", &[], content).unwrap();
        let val = style_structs_to_value(&tree, &args(&[]));
        let structs = match val {
            crate::runtime::Value::List(v) => v,
            _ => panic!("expected list"),
        };
        assert_eq!(structs.len(), 1);
    }

    #[test]
    fn serialise_with_interpolation() {
        let content = r#"{
            .box {
                background-color: ${color},
                width: ${size}
            }
        }"#;
        let params = vec![
            TemplateParam { name: "color".to_string(), ty: String::new() },
            TemplateParam { name: "size".to_string(), ty: String::new() },
        ];
        let tree = parse_style_block_tree(&["css".to_string()], "S", &params, content).unwrap();
        let val = style_structs_to_value(&tree, &args(&[("color", "navy"), ("size", "100px")]));
        // Spot-check that interpolation was applied at serialisation time.
        let s = format!("{val:?}");
        assert!(s.contains("navy"), "got: {s}");
        assert!(s.contains("100px"), "got: {s}");
    }

    #[test]
    fn parse_multiple_structs() {
        let content = r#"{
            h1 { font-size: 2em }
            h2 { font-size: 1.5em }
        }"#;
        let tree = parse_style_block_tree(&["css".to_string()], "S", &[], content).unwrap();
        assert_eq!(tree.structs.len(), 2);
    }

    #[test]
    fn parse_no_name_struct() {
        let content = r#"{
            { color: red }
        }"#;
        let tree = parse_style_block_tree(&["css".to_string()], "S", &[], content).unwrap();
        assert_eq!(tree.structs.len(), 1);
        assert!(tree.structs[0].name.is_none());
    }

    #[test]
    fn is_number_str_recognises_integers() {
        assert!(is_number_str("42"));
        assert!(is_number_str("-7"));
        assert!(is_number_str("3.14"));
        assert!(!is_number_str("14px"));
        assert!(!is_number_str(""));
        assert!(!is_number_str("abc"));
    }
}
