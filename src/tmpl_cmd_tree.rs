// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Parser for structured command template content (`cmd [lang] name(params) { … }`).
//!
//! A command template describes an executable command — a bash script, SQL
//! statement, HTTP request, or any other command-oriented invocation — in a
//! format-agnostic way.  Generators convert these descriptions into concrete
//! invocations for the target runtime.
//!
//! # Syntax overview
//!
//! ```text
//! // Bare command name — delegates the entire body to the named executor.
//! cmd [bash] runScript(script: String) {
//!     bash
//! }
//!
//! // Function-call style — structured invocation with named arguments.
//! cmd [http] fetchUser(host: String, id: String) {
//!     GET(url: ${host}, path: "/users/${id}", accept: "application/json")
//! }
//!
//! // SQL example
//! cmd [sql] findByEmail(email: String) {
//!     SELECT(table: "users", where: "email = ${email}")
//! }
//!
//! // SSH example
//! cmd [bash] deploy(host: String, script: String) {
//!     ssh(host: ${host}, command: ${script}, user: "deploy")
//! }
//! ```
//!
//! The body (the `{ … }` block) must contain **exactly one** of:
//! - A bare identifier such as `bash` or `sql` (a `CmdContentTree::Bare`).
//! - A function-call expression such as `GET(url: ${host})` (a
//!   `CmdContentTree::Call`).

use crate::ast::TemplateParam;
use crate::tree_context::TreeContext;

// ── Public tree types ─────────────────────────────────────────────────────────

/// The fully-parsed form of a `cmd […] name(params) { … }` declaration.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct TmplCmdBlockTree {
    /// Execution environments / command types (e.g. `["bash", "sql", "http"]`).
    pub langs: Vec<String>,
    /// Template name (e.g. `fetchUser`).
    pub name: String,
    /// Template parameters declared in the signature.
    pub params: Vec<TemplateParam>,
    /// Parsed body of the command block.
    pub content: CmdContentTree,
    pub context: TreeContext,
}

/// The body of a command block — either a bare executor name or a structured
/// function-call style invocation.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum CmdContentTree {
    /// A plain executor name with no arguments, e.g. `bash` or `sql`.
    ///
    /// The generator interprets the template parameters directly as the command
    /// arguments and uses the name to select the execution driver.
    Bare(String),

    /// A structured invocation: `name(arg1: val1, arg2: val2)`.
    ///
    /// The generator maps this onto the target protocol (HTTP method, SQL
    /// keyword, shell command, …) using the call name and arguments.
    Call(CmdCallTree),
}

/// A function-call style command body: `name(args)`.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct CmdCallTree {
    /// The call / command name, e.g. `GET`, `POST`, `ssh`, `SELECT`.
    pub name: String,
    /// Ordered list of arguments.
    pub args: Vec<CmdArgTree>,
}

/// A single argument in a command call.
///
/// ```text
/// url: ${host}          →  name = Some("url"),  value = "${host}"
/// "Content-Type": json  →  name = Some(r#""Content-Type""#), value = "json"
/// ${host}               →  name = None,          value = "${host}"
/// plain                 →  name = None,           value = "plain"
/// ```
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct CmdArgTree {
    /// Optional argument label.  May be a plain identifier, a quoted string,
    /// or an interpolated identifier / string.
    pub name: Option<String>,
    /// Argument value — always a `tmplID`: plain identifier, backtick-escaped
    /// identifier, or `${…}` interpolated expression.
    pub value: String,
}

// ── Error type ────────────────────────────────────────────────────────────────

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct TmplCmdTreeError(pub String);

impl std::fmt::Display for TmplCmdTreeError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.0)
    }
}
impl std::error::Error for TmplCmdTreeError {}

// ── Entry point ───────────────────────────────────────────────────────────────

/// Parse a complete command-template body.
///
/// `content` is the raw `balanced_block` text returned by the PEG parser —
/// it **includes** the surrounding `{…}` braces.
pub fn parse_tmpl_cmd_block_tree(
    langs: &[String],
    name: &str,
    params: &[TemplateParam],
    content: &str,
) -> Result<TmplCmdBlockTree, TmplCmdTreeError> {
    let inner = strip_outer_braces(content);
    let mut pos = 0usize;
    skip_ws(inner, &mut pos);

    if pos >= inner.len() {
        return Err(TmplCmdTreeError(format!(
            "cmd template `{name}` has an empty body"
        )));
    }

    let cmd_content = parse_cmd_content(inner, &mut pos)?;

    // Trailing whitespace / comments are acceptable; anything else is an error.
    skip_ws(inner, &mut pos);
    if pos < inner.len() {
        return Err(TmplCmdTreeError(format!(
            "unexpected content after command body in template `{name}`: `{}`",
            &inner[pos..]
        )));
    }

    Ok(TmplCmdBlockTree {
        langs: langs.to_vec(),
        name: name.to_string(),
        params: params.to_vec(),
        content: cmd_content,
        context: TreeContext::default(),
    })
}

// ── Core content parser ───────────────────────────────────────────────────────

/// Parse one command expression: either a bare name or a `name(args)` call.
fn parse_cmd_content(src: &str, pos: &mut usize) -> Result<CmdContentTree, TmplCmdTreeError> {
    // Read the leading identifier (the command / executor name).
    let name = read_ident(src, pos).ok_or_else(|| {
        TmplCmdTreeError(format!(
            "expected a command name at position {pos} but found `{}`",
            src.get(*pos..*pos + 20).unwrap_or(&src[*pos..])
        ))
    })?;

    skip_ws(src, pos);

    // If the next character is `(`, this is a call; otherwise it is bare.
    if peek(src, *pos) == Some('(') {
        *pos += 1; // consume '('
        skip_ws(src, pos);

        let args = if peek(src, *pos) == Some(')') {
            Vec::new()
        } else {
            parse_args(src, pos)?
        };

        skip_ws(src, pos);
        expect_char(src, pos, ')')?;

        Ok(CmdContentTree::Call(CmdCallTree { name, args }))
    } else {
        Ok(CmdContentTree::Bare(name))
    }
}

// ── Argument list parser ──────────────────────────────────────────────────────

fn parse_args(src: &str, pos: &mut usize) -> Result<Vec<CmdArgTree>, TmplCmdTreeError> {
    let mut args = Vec::new();

    loop {
        skip_ws(src, pos);
        if peek(src, *pos) == Some(')') || *pos >= src.len() {
            break;
        }

        let arg = parse_single_arg(src, pos)?;
        args.push(arg);

        skip_ws(src, pos);
        match peek(src, *pos) {
            Some(',') => {
                *pos += 1; // consume ','
            }
            Some(')') | None => break,
            Some(c) => {
                return Err(TmplCmdTreeError(format!(
                    "expected `,` or `)` after argument but found `{c}` at position {pos}"
                )));
            }
        }
    }

    Ok(args)
}

/// Parse one argument: `(name ':')? value`.
///
/// Both the optional name and the value may be any of:
/// - a plain identifier            `myArg`
/// - a backtick-escaped identifier `` `class` ``
/// - an interpolated expression    `${variable}` / `pre${variable}suf`
///
/// The name may additionally be a **string literal** (`"some-key"`) or an
/// interpolated string (`s"prefix-${var}-suffix"`).
fn parse_single_arg(src: &str, pos: &mut usize) -> Result<CmdArgTree, TmplCmdTreeError> {
    skip_ws(src, pos);

    // Attempt to read a tmplIdOrString token then look for a `:` separator.
    // If no `:` follows, treat the token as the *value* (unnamed argument).
    let first = read_tmpl_id_or_string(src, pos)?;

    skip_ws(src, pos);

    if peek(src, *pos) == Some(':') {
        // This was the argument name; consume `:` and read the value.
        *pos += 1;
        skip_ws(src, pos);
        // Values may also be string literals (plain or interpolated) even though
        // the base grammar declares `value=tmplID` — string values are required
        // for SQL WHERE clauses, HTTP path templates, header values, etc.
        let value = read_tmpl_id_or_string(src, pos)?;
        Ok(CmdArgTree {
            name: Some(first),
            value,
        })
    } else {
        // No `:` — the token we just read *is* the value.
        Ok(CmdArgTree {
            name: None,
            value: first,
        })
    }
}

// ── Token readers ─────────────────────────────────────────────────────────────

/// Read a `tmplIdOrString`: a `tmplID` *or* a string literal (plain or
/// interpolated).  Returns the raw source text of the token.
fn read_tmpl_id_or_string(src: &str, pos: &mut usize) -> Result<String, TmplCmdTreeError> {
    let ch = peek(src, *pos)
        .ok_or_else(|| TmplCmdTreeError(format!("unexpected end of input at position {pos}")))?;

    if ch == '"' {
        read_plain_string(src, pos)
    } else if src[*pos..].starts_with("s\"") {
        read_interp_string(src, pos)
    } else {
        read_tmpl_id(src, pos).ok_or_else(|| {
            TmplCmdTreeError(format!(
                "expected identifier or string at position {pos}, found `{}`",
                src.get(*pos..*pos + 10).unwrap_or(&src[*pos..])
            ))
        })
    }
}

/// Read a `tmplID`: plain identifier, backtick-escaped identifier, or
/// `${…}` interpolated identifier (with optional pre/post text).
/// Returns `None` if the current position does not start a valid tmplID.
fn read_tmpl_id(src: &str, pos: &mut usize) -> Option<String> {
    let start = *pos;
    let rest = &src[start..];

    if rest.starts_with('`') {
        // Backtick-escaped identifier: `someIdent`
        let end_tick = rest[1..].find('`')? + 1;
        *pos = start + end_tick + 1;
        return Some(src[start..*pos].to_string());
    }

    // May be `pre${…}suf` — read optional leading ident chars first.
    let pre_len = ident_len(rest);
    let after_pre = &rest[pre_len..];

    if after_pre.starts_with("${") {
        // Interpolated: optional-pre `${` … `}` optional-post
        let brace_start = pre_len + 2; // skip "${"
        let inner = &rest[brace_start..];
        let close = find_close_brace(inner)?;
        let after_brace = brace_start + close + 1; // past '}'
        // Optional trailing plain identifier chars
        let post_len = ident_len(&rest[after_brace..]);
        *pos = start + after_brace + post_len;
        return Some(src[start..*pos].to_string());
    }

    if pre_len > 0 {
        *pos = start + pre_len;
        return Some(src[start..*pos].to_string());
    }

    None
}

/// Read a plain (non-interpolated) quoted string `"…"` and return its full
/// source text including the surrounding quotes.
fn read_plain_string(src: &str, pos: &mut usize) -> Result<String, TmplCmdTreeError> {
    let start = *pos;
    if src[start..].chars().next() != Some('"') {
        return Err(TmplCmdTreeError(format!("expected '\"' at position {pos}")));
    }
    *pos += 1;
    loop {
        match src[*pos..].chars().next() {
            None => {
                return Err(TmplCmdTreeError("unterminated string literal".to_string()));
            }
            Some('\\') => {
                *pos += 1; // skip escape char
                if *pos < src.len() {
                    // skip the escaped character (multi-byte safe)
                    let ch = src[*pos..].chars().next().unwrap();
                    *pos += ch.len_utf8();
                }
            }
            Some('"') => {
                *pos += 1;
                break;
            }
            Some(c) => {
                *pos += c.len_utf8();
            }
        }
    }
    Ok(src[start..*pos].to_string())
}

/// Read an interpolated string `s"…${…}…"` and return its full source text.
fn read_interp_string(src: &str, pos: &mut usize) -> Result<String, TmplCmdTreeError> {
    let start = *pos;
    if !src[start..].starts_with("s\"") {
        return Err(TmplCmdTreeError(format!(
            "expected interpolated string (`s\"…\"`) at position {pos}"
        )));
    }
    *pos += 2; // skip `s"`
    let mut depth = 0usize;
    loop {
        match src[*pos..].chars().next() {
            None => {
                return Err(TmplCmdTreeError(
                    "unterminated interpolated string literal".to_string(),
                ));
            }
            Some('\\') => {
                *pos += 1;
                if *pos < src.len() {
                    let ch = src[*pos..].chars().next().unwrap();
                    *pos += ch.len_utf8();
                }
            }
            Some('$') if src[*pos..].starts_with("${") => {
                *pos += 2;
                depth += 1;
            }
            Some('}') if depth > 0 => {
                *pos += 1;
                depth -= 1;
            }
            Some('"') if depth == 0 => {
                *pos += 1;
                break;
            }
            Some(c) => {
                *pos += c.len_utf8();
            }
        }
    }
    Ok(src[start..*pos].to_string())
}

// ── Identifier helpers ────────────────────────────────────────────────────────

/// Read a plain (non-interpolated) identifier and return it, advancing `pos`.
fn read_ident(src: &str, pos: &mut usize) -> Option<String> {
    skip_ws(src, pos);
    let start = *pos;
    let len = ident_len(&src[start..]);
    if len == 0 {
        return None;
    }
    *pos = start + len;
    Some(src[start..*pos].to_string())
}

/// Return the number of bytes that form a valid identifier starting at `s`.
///
/// Mirrors the lexer rule: `'^'? (alpha|'_') (alphanumeric|'_'|'-')*`
fn ident_len(s: &str) -> usize {
    let mut chars = s.char_indices().peekable();

    // Optional leading caret
    let (mut offset, mut ch) = match chars.next() {
        Some(x) => x,
        None => return 0,
    };
    if ch == '^' {
        match chars.next() {
            Some(x) => {
                offset = x.0;
                ch = x.1;
            }
            None => return 0,
        }
    }

    if !ch.is_ascii_alphabetic() && ch != '_' {
        return 0;
    }

    let mut end = offset + ch.len_utf8();
    for (i, c) in chars {
        if c.is_ascii_alphanumeric() || c == '_' || c == '-' {
            end = i + c.len_utf8();
        } else {
            break;
        }
    }
    end
}

/// Find the matching `}` for an already-consumed `${`, handling nesting.
/// Returns the byte offset of `}` within `src` (relative to `src`'s start),
/// or `None` if the brace is never closed.
fn find_close_brace(src: &str) -> Option<usize> {
    let mut depth = 1usize;
    let mut i = 0usize;
    let bytes = src.as_bytes();
    while i < bytes.len() {
        match bytes[i] {
            b'{' => depth += 1,
            b'}' => {
                depth -= 1;
                if depth == 0 {
                    return Some(i);
                }
            }
            _ => {}
        }
        i += 1;
    }
    None
}

// ── Whitespace / utility helpers ──────────────────────────────────────────────

fn skip_ws(src: &str, pos: &mut usize) {
    let bytes = src.as_bytes();
    while *pos < bytes.len() {
        match bytes[*pos] {
            b' ' | b'\t' | b'\r' | b'\n' => *pos += 1,
            b'/' if bytes.get(*pos + 1) == Some(&b'/') => {
                // Line comment
                while *pos < bytes.len() && bytes[*pos] != b'\n' {
                    *pos += 1;
                }
            }
            b'/' if bytes.get(*pos + 1) == Some(&b'*') => {
                // Block comment
                *pos += 2;
                while *pos + 1 < bytes.len() {
                    if bytes[*pos] == b'*' && bytes[*pos + 1] == b'/' {
                        *pos += 2;
                        break;
                    }
                    *pos += 1;
                }
            }
            _ => break,
        }
    }
}

fn peek(src: &str, pos: usize) -> Option<char> {
    src[pos..].chars().next()
}

fn expect_char(src: &str, pos: &mut usize, expected: char) -> Result<(), TmplCmdTreeError> {
    match peek(src, *pos) {
        Some(c) if c == expected => {
            *pos += c.len_utf8();
            Ok(())
        }
        Some(c) => Err(TmplCmdTreeError(format!(
            "expected `{expected}` at position {pos} but found `{c}`"
        ))),
        None => Err(TmplCmdTreeError(format!(
            "expected `{expected}` at position {pos} but reached end of input"
        ))),
    }
}

/// Strip the outermost `{` … `}` braces from a `balanced_block` string.
fn strip_outer_braces(s: &str) -> &str {
    let s = s.trim();
    if s.starts_with('{') && s.ends_with('}') {
        &s[1..s.len() - 1]
    } else {
        s
    }
}

// ── Tests ─────────────────────────────────────────────────────────────────────

#[cfg(test)]
mod tests {
    use super::*;

    fn parse(content: &str) -> Result<TmplCmdBlockTree, TmplCmdTreeError> {
        parse_tmpl_cmd_block_tree(
            &["bash".to_string()],
            "testCmd",
            &[],
            &format!("{{ {content} }}"),
        )
    }

    // ── Bare name ─────────────────────────────────────────────────────────────

    #[test]
    fn parses_bare_name() {
        let tree = parse("bash").unwrap();
        assert_eq!(tree.content, CmdContentTree::Bare("bash".to_string()));
    }

    #[test]
    fn parses_bare_name_sql() {
        let tree = parse("sql").unwrap();
        assert_eq!(tree.content, CmdContentTree::Bare("sql".to_string()));
    }

    #[test]
    fn parses_bare_name_with_surrounding_whitespace() {
        let tree = parse("  http  ").unwrap();
        assert_eq!(tree.content, CmdContentTree::Bare("http".to_string()));
    }

    // ── Call with no args ─────────────────────────────────────────────────────

    #[test]
    fn parses_call_no_args() {
        let tree = parse("GET()").unwrap();
        match tree.content {
            CmdContentTree::Call(call) => {
                assert_eq!(call.name, "GET");
                assert!(call.args.is_empty());
            }
            _ => panic!("expected call"),
        }
    }

    // ── Call with named args ──────────────────────────────────────────────────

    #[test]
    fn parses_call_with_named_plain_args() {
        let tree = parse("GET(url: myUrl, accept: json)").unwrap();
        match tree.content {
            CmdContentTree::Call(call) => {
                assert_eq!(call.name, "GET");
                assert_eq!(call.args.len(), 2);
                assert_eq!(call.args[0].name.as_deref(), Some("url"));
                assert_eq!(call.args[0].value, "myUrl");
                assert_eq!(call.args[1].name.as_deref(), Some("accept"));
                assert_eq!(call.args[1].value, "json");
            }
            _ => panic!("expected call"),
        }
    }

    #[test]
    fn parses_call_with_interpolated_value() {
        let tree = parse("ssh(host: ${hostVar}, command: ${cmd})").unwrap();
        match tree.content {
            CmdContentTree::Call(call) => {
                assert_eq!(call.name, "ssh");
                assert_eq!(call.args.len(), 2);
                assert_eq!(call.args[0].name.as_deref(), Some("host"));
                assert_eq!(call.args[0].value, "${hostVar}");
                assert_eq!(call.args[1].name.as_deref(), Some("command"));
                assert_eq!(call.args[1].value, "${cmd}");
            }
            _ => panic!("expected call"),
        }
    }

    #[test]
    fn parses_call_with_prefixed_interpolated_value() {
        let tree = parse("curl(url: api${endpoint})").unwrap();
        match tree.content {
            CmdContentTree::Call(call) => {
                assert_eq!(call.args[0].value, "api${endpoint}");
            }
            _ => panic!("expected call"),
        }
    }

    // ── String keys ───────────────────────────────────────────────────────────

    #[test]
    fn parses_call_with_string_key() {
        let tree = parse(r#"PUT("Content-Type": json)"#).unwrap();
        match tree.content {
            CmdContentTree::Call(call) => {
                assert_eq!(call.args[0].name.as_deref(), Some(r#""Content-Type""#));
                assert_eq!(call.args[0].value, "json");
            }
            _ => panic!("expected call"),
        }
    }

    // ── Unnamed (positional) args ─────────────────────────────────────────────

    #[test]
    fn parses_call_with_unnamed_arg() {
        let tree = parse("bash(${scriptPath})").unwrap();
        match tree.content {
            CmdContentTree::Call(call) => {
                assert_eq!(call.args[0].name, None);
                assert_eq!(call.args[0].value, "${scriptPath}");
            }
            _ => panic!("expected call"),
        }
    }

    // ── Backtick-escaped value ────────────────────────────────────────────────

    #[test]
    fn parses_call_with_escaped_id_value() {
        let tree = parse("SELECT(table: `order`, col: id)").unwrap();
        match tree.content {
            CmdContentTree::Call(call) => {
                assert_eq!(call.args[0].value, "`order`");
                assert_eq!(call.args[1].value, "id");
            }
            _ => panic!("expected call"),
        }
    }

    // ── Multiple langs ────────────────────────────────────────────────────────

    #[test]
    fn parses_with_multiple_langs() {
        let tree = parse_tmpl_cmd_block_tree(
            &["bash".to_string(), "zsh".to_string()],
            "runScript",
            &[],
            "{ bash }",
        )
        .unwrap();
        assert_eq!(tree.langs, vec!["bash", "zsh"]);
        assert_eq!(tree.content, CmdContentTree::Bare("bash".to_string()));
    }

    // ── Empty body ────────────────────────────────────────────────────────────

    #[test]
    fn rejects_empty_body() {
        let err = parse("").unwrap_err();
        assert!(err.to_string().contains("empty body"));
    }

    // ── Trailing garbage ─────────────────────────────────────────────────────

    #[test]
    fn rejects_trailing_content() {
        let err = parse("bash extra").unwrap_err();
        assert!(
            err.to_string().contains("unexpected content"),
            "got: {}",
            err
        );
    }

    // ── Comments ─────────────────────────────────────────────────────────────

    #[test]
    fn parses_bare_name_with_line_comment() {
        let tree = parse("// execute via the bash driver\nbash").unwrap();
        assert_eq!(tree.content, CmdContentTree::Bare("bash".to_string()));
    }

    #[test]
    fn parses_call_with_block_comment() {
        let tree = parse("/* HTTP GET */ GET(url: ${u})").unwrap();
        match tree.content {
            CmdContentTree::Call(call) => {
                assert_eq!(call.name, "GET");
                assert_eq!(call.args[0].value, "${u}");
            }
            _ => panic!("expected call"),
        }
    }

    // ── Real-world style examples ─────────────────────────────────────────────

    #[test]
    fn http_get_example() {
        let content = r#"{
            GET(
                url:    ${baseUrl},
                path:   "/api/users/${userId}",
                accept: "application/json"
            )
        }"#;
        let tree = parse_tmpl_cmd_block_tree(
            &["http".to_string()],
            "getUser",
            &[
                TemplateParam {
                    name: "baseUrl".into(),
                    ty: "String".into(),
                },
                TemplateParam {
                    name: "userId".into(),
                    ty: "String".into(),
                },
            ],
            content,
        )
        .unwrap();

        match &tree.content {
            CmdContentTree::Call(call) => {
                assert_eq!(call.name, "GET");
                assert_eq!(call.args.len(), 3);
                assert_eq!(call.args[0].name.as_deref(), Some("url"));
                assert_eq!(call.args[0].value, "${baseUrl}");
                assert_eq!(call.args[1].name.as_deref(), Some("path"));
                assert_eq!(call.args[2].name.as_deref(), Some("accept"));
            }
            _ => panic!("expected call"),
        }
    }

    #[test]
    fn sql_select_example() {
        let content = "{ SELECT(table: users, where: \"email = ${email}\", limit: maxRows) }";
        let tree =
            parse_tmpl_cmd_block_tree(&["sql".to_string()], "findByEmail", &[], content).unwrap();

        match &tree.content {
            CmdContentTree::Call(call) => {
                assert_eq!(call.name, "SELECT");
                assert_eq!(call.args.len(), 3);
                assert_eq!(call.args[1].name.as_deref(), Some("where"));
            }
            _ => panic!("expected call"),
        }
    }
}
