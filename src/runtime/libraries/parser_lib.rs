// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang.Parser — lexer/tokenizer and token-manipulation utilities.
//!
//! # Overview
//!
//! `TLang.Parser` provides the building blocks for writing language parsers
//! inside TLang programs.  A typical workflow is:
//!
//! 1. Build a list of *lexer rules* with [`TLang.Parser.rule`].
//! 2. Tokenize a source string with [`TLang.Parser.tokenize`].
//! 3. Inspect and filter the resulting token list with the remaining helpers.
//!
//! # Rule ordering
//!
//! Rules are tried in list order, first-match wins.  Put longer / more-specific
//! patterns *before* shorter / more-general ones (e.g. `!=` before `!`).
//!
//! # Token representation
//!
//! Every token returned by `tokenize` is a `Leaf` with these fields:
//!
//! | field    | type   | description                             |
//! |----------|--------|-----------------------------------------|
//! | `kind`   | String | Always `"ParseToken"`                   |
//! | `type`   | String | Rule name that matched (e.g. `"ident")` |
//! | `value`  | String | Matched text                            |
//! | `line`   | Int    | 1-based line number                     |
//! | `col`    | Int    | 1-based column number                   |

use std::collections::BTreeMap;

use regex::Regex;

use super::super::{LeafObject, RuntimeError, Value};
use super::expect_string;

// ── constants ────────────────────────────────────────────────────────────────

const KIND_FIELD: &str = "kind";
const RULE_KIND: &str = "LexerRule";
const PARSE_TOKEN_KIND: &str = "ParseToken";

const RULE_NAME_FIELD: &str = "name";
const RULE_PATTERN_FIELD: &str = "pattern";

const TOKEN_TYPE_FIELD: &str = "type";
const TOKEN_VALUE_FIELD: &str = "value";
const TOKEN_LINE_FIELD: &str = "line";
const TOKEN_COL_FIELD: &str = "col";

/// Number of characters shown in an "unmatched input" error snippet.
const ERROR_SNIPPET_LEN: usize = 20;

// ── dispatch ─────────────────────────────────────────────────────────────────

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Parser.rule" => rule(args),
        "TLang.Parser.tokenize" => tokenize(args),
        "TLang.Parser.token_type" => token_type(args),
        "TLang.Parser.token_value" => token_value(args),
        "TLang.Parser.token_line" => token_line(args),
        "TLang.Parser.token_col" => token_col(args),
        "TLang.Parser.filter_type" => filter_type(args),
        "TLang.Parser.skip_types" => skip_types(args),
        _ => Err(RuntimeError(format!(
            "unknown Parser library function `{target}`"
        ))),
    }
}

// ── TLang.Parser.rule ────────────────────────────────────────────────────────

/// `TLang.Parser.rule(name: String, pattern: String) -> Leaf`
///
/// Creates a lexer rule leaf. `pattern` is a regular expression that must
/// match at the *start* of the remaining input (the library automatically
/// anchors it with `\A`).
fn rule(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Parser.rule expects exactly two arguments: name and pattern".to_string(),
        ));
    }
    let name = expect_string(&args[0], "TLang.Parser.rule name")?;
    let pattern = expect_string(&args[1], "TLang.Parser.rule pattern")?;

    // Validate the pattern at rule-creation time for early error reporting.
    let anchored = format!("\\A(?:{pattern})");
    Regex::new(&anchored).map_err(|e| {
        RuntimeError(format!(
            "TLang.Parser.rule: invalid regex pattern `{pattern}`: {e}"
        ))
    })?;

    let mut fields = BTreeMap::new();
    fields.insert(KIND_FIELD.to_string(), Value::String(RULE_KIND.to_string()));
    fields.insert(RULE_NAME_FIELD.to_string(), Value::String(name));
    fields.insert(RULE_PATTERN_FIELD.to_string(), Value::String(pattern));
    Ok(Value::Leaf(LeafObject::new(fields)))
}

// ── TLang.Parser.tokenize ────────────────────────────────────────────────────

/// `TLang.Parser.tokenize(source: String, rules: List) -> List`
///
/// Applies the list of lexer rules to `source` in order (first-match wins) and
/// returns a `List` of `ParseToken` leaves.  Unmatched characters cause a
/// `RuntimeError`.
fn tokenize(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Parser.tokenize expects exactly two arguments: source and rules".to_string(),
        ));
    }

    let source = expect_string(&args[0], "TLang.Parser.tokenize source")?;

    let Value::List(rule_values) = &args[1] else {
        return Err(RuntimeError(
            "TLang.Parser.tokenize expects a list as second argument (rules)".to_string(),
        ));
    };

    // Compile all rules once.
    let compiled: Vec<(String, Regex)> = rule_values
        .iter()
        .enumerate()
        .map(|(i, rv)| compile_rule(rv, i))
        .collect::<Result<Vec<_>, _>>()?;

    let mut tokens: Vec<Value> = Vec::new();
    let mut pos = 0usize;
    let mut line = 1usize;
    let mut col = 1usize;

    while pos < source.len() {
        let remaining = &source[pos..];
        let mut matched = false;

        for (name, re) in &compiled {
            if let Some(m) = re.find(remaining) {
                if m.end() == 0 {
                    return Err(RuntimeError(format!(
                        "TLang.Parser.tokenize: rule `{name}` matched an empty string at \
                         line {line}, col {col} (byte offset {pos}); \
                         patterns must consume at least one character"
                    )));
                }
                let text = m.as_str().to_string();
                // Capture the token's start position before advancing.
                let token_line = line;
                let token_col = col;
                // Advance line/col tracking.
                for ch in text.chars() {
                    if ch == '\n' {
                        line += 1;
                        col = 1;
                    } else {
                        col += 1;
                    }
                }
                pos += m.end();
                tokens.push(make_token(name.clone(), text, token_line, token_col));
                matched = true;
                break;
            }
        }

        if !matched {
            let snippet: String = remaining.chars().take(ERROR_SNIPPET_LEN).collect();
            return Err(RuntimeError(format!(
                "TLang.Parser.tokenize: no rule matched at line {line}, col {col}: `{snippet}`"
            )));
        }
    }

    Ok(Value::List(tokens))
}

// ── token accessors ──────────────────────────────────────────────────────────

/// `TLang.Parser.token_type(token: Leaf) -> String`
fn token_type(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Parser.token_type expects exactly one argument".to_string(),
        ));
    }
    let leaf = expect_parse_token(&args[0])?;
    string_field(leaf, TOKEN_TYPE_FIELD, "TLang.Parser.token_type")
}

/// `TLang.Parser.token_value(token: Leaf) -> String`
fn token_value(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Parser.token_value expects exactly one argument".to_string(),
        ));
    }
    let leaf = expect_parse_token(&args[0])?;
    string_field(leaf, TOKEN_VALUE_FIELD, "TLang.Parser.token_value")
}

/// `TLang.Parser.token_line(token: Leaf) -> Int`
fn token_line(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Parser.token_line expects exactly one argument".to_string(),
        ));
    }
    let leaf = expect_parse_token(&args[0])?;
    int_field(leaf, TOKEN_LINE_FIELD, "TLang.Parser.token_line")
}

/// `TLang.Parser.token_col(token: Leaf) -> Int`
fn token_col(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Parser.token_col expects exactly one argument".to_string(),
        ));
    }
    let leaf = expect_parse_token(&args[0])?;
    int_field(leaf, TOKEN_COL_FIELD, "TLang.Parser.token_col")
}

// ── token list utilities ─────────────────────────────────────────────────────

/// `TLang.Parser.filter_type(tokens: List, type_name: String) -> List`
///
/// Returns a new list containing only tokens whose type equals `type_name`.
fn filter_type(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Parser.filter_type expects exactly two arguments: tokens and type_name"
                .to_string(),
        ));
    }
    let Value::List(tokens) = &args[0] else {
        return Err(RuntimeError(
            "TLang.Parser.filter_type expects a list as first argument".to_string(),
        ));
    };
    let type_name = expect_string(&args[1], "TLang.Parser.filter_type type_name")?;

    let filtered = tokens
        .iter()
        .filter(|t| token_has_type(t, &type_name))
        .cloned()
        .collect();
    Ok(Value::List(filtered))
}

/// `TLang.Parser.skip_types(tokens: List, type_names: List) -> List`
///
/// Returns a new list with all tokens whose type is in `type_names` removed.
/// Useful for stripping whitespace, comments, etc. before further processing.
fn skip_types(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Parser.skip_types expects exactly two arguments: tokens and type_names"
                .to_string(),
        ));
    }
    let Value::List(tokens) = &args[0] else {
        return Err(RuntimeError(
            "TLang.Parser.skip_types expects a list as first argument".to_string(),
        ));
    };
    let Value::List(skip_list) = &args[1] else {
        return Err(RuntimeError(
            "TLang.Parser.skip_types expects a list as second argument (type_names)".to_string(),
        ));
    };

    let skip_set: Vec<String> = skip_list
        .iter()
        .map(|v| expect_string(v, "TLang.Parser.skip_types type name"))
        .collect::<Result<Vec<_>, _>>()?;

    let filtered = tokens
        .iter()
        .filter(|t| {
            if let Some(ty) = get_token_type_str(t) {
                !skip_set.iter().any(|s| s == ty)
            } else {
                true
            }
        })
        .cloned()
        .collect();
    Ok(Value::List(filtered))
}

// ── helpers ──────────────────────────────────────────────────────────────────

fn compile_rule(value: &Value, index: usize) -> Result<(String, Regex), RuntimeError> {
    let Value::Leaf(leaf) = value else {
        return Err(RuntimeError(format!(
            "TLang.Parser.tokenize: rule at index {index} must be a Leaf created by \
             TLang.Parser.rule"
        )));
    };
    match leaf.get(KIND_FIELD) {
        Some(Value::String(k)) if k == RULE_KIND => {}
        _ => {
            return Err(RuntimeError(format!(
                "TLang.Parser.tokenize: rule at index {index} must be a LexerRule leaf"
            )));
        }
    }
    let Some(Value::String(name)) = leaf.get(RULE_NAME_FIELD) else {
        return Err(RuntimeError(format!(
            "TLang.Parser.tokenize: rule at index {index} is missing `name`"
        )));
    };
    let Some(Value::String(pattern)) = leaf.get(RULE_PATTERN_FIELD) else {
        return Err(RuntimeError(format!(
            "TLang.Parser.tokenize: rule at index {index} is missing `pattern`"
        )));
    };
    let anchored = format!("\\A(?:{pattern})");
    let re = Regex::new(&anchored).map_err(|e| {
        RuntimeError(format!(
            "TLang.Parser.tokenize: invalid pattern for rule `{name}`: {e}"
        ))
    })?;
    Ok((name.clone(), re))
}

fn make_token(type_name: String, value: String, line: usize, col: usize) -> Value {
    let mut fields = BTreeMap::new();
    fields.insert(
        KIND_FIELD.to_string(),
        Value::String(PARSE_TOKEN_KIND.to_string()),
    );
    fields.insert(TOKEN_TYPE_FIELD.to_string(), Value::String(type_name));
    fields.insert(TOKEN_VALUE_FIELD.to_string(), Value::String(value));
    fields.insert(TOKEN_LINE_FIELD.to_string(), Value::Int(line as i64));
    fields.insert(TOKEN_COL_FIELD.to_string(), Value::Int(col as i64));
    Value::Leaf(LeafObject::new(fields))
}

fn expect_parse_token(value: &Value) -> Result<&LeafObject, RuntimeError> {
    let Value::Leaf(leaf) = value else {
        return Err(RuntimeError(
            "TLang.Parser expects a ParseToken leaf".to_string(),
        ));
    };
    match leaf.get(KIND_FIELD) {
        Some(Value::String(k)) if k == PARSE_TOKEN_KIND => Ok(leaf),
        _ => Err(RuntimeError(
            "TLang.Parser expects a ParseToken leaf".to_string(),
        )),
    }
}

fn string_field(leaf: &LeafObject, field: &str, context: &str) -> Result<Value, RuntimeError> {
    match leaf.get(field) {
        Some(Value::String(s)) => Ok(Value::String(s.clone())),
        _ => Err(RuntimeError(format!(
            "{context}: token is missing field `{field}`"
        ))),
    }
}

fn int_field(leaf: &LeafObject, field: &str, context: &str) -> Result<Value, RuntimeError> {
    match leaf.get(field) {
        Some(Value::Int(n)) => Ok(Value::Int(*n)),
        _ => Err(RuntimeError(format!(
            "{context}: token is missing field `{field}`"
        ))),
    }
}

fn get_token_type_str(value: &Value) -> Option<&str> {
    if let Value::Leaf(leaf) = value {
        if let Some(Value::String(ty)) = leaf.get(TOKEN_TYPE_FIELD) {
            return Some(ty.as_str());
        }
    }
    None
}

fn token_has_type(value: &Value, type_name: &str) -> bool {
    get_token_type_str(value)
        .map(|t| t == type_name)
        .unwrap_or(false)
}

// ── tests ────────────────────────────────────────────────────────────────────

#[cfg(test)]
mod tests {
    use super::super::super::Value;
    use super::call;

    fn make_rules() -> Value {
        // Rules for a tiny Kotlin-like snippet: keywords, idents, whitespace
        let kw_rule = call(
            "TLang.Parser.rule",
            &[
                Value::String("keyword".to_string()),
                Value::String(r"(?:class|fun|val|var)\b".to_string()),
            ],
        )
        .expect("rule should build");
        let ident_rule = call(
            "TLang.Parser.rule",
            &[
                Value::String("ident".to_string()),
                Value::String(r"[A-Za-z_]\w*".to_string()),
            ],
        )
        .expect("rule should build");
        let ws_rule = call(
            "TLang.Parser.rule",
            &[
                Value::String("whitespace".to_string()),
                Value::String(r"\s+".to_string()),
            ],
        )
        .expect("rule should build");
        Value::List(vec![kw_rule, ident_rule, ws_rule])
    }

    #[test]
    fn rule_creates_leaf_with_name_and_pattern() {
        let r = call(
            "TLang.Parser.rule",
            &[
                Value::String("ident".to_string()),
                Value::String(r"[A-Za-z_]\w*".to_string()),
            ],
        )
        .expect("rule should build");

        let Value::Leaf(leaf) = &r else {
            panic!("expected Leaf");
        };
        assert_eq!(
            leaf.get("kind"),
            Some(&Value::String("LexerRule".to_string()))
        );
        assert_eq!(
            leaf.get("name"),
            Some(&Value::String("ident".to_string()))
        );
    }

    #[test]
    fn rule_rejects_invalid_regex() {
        let err = call(
            "TLang.Parser.rule",
            &[
                Value::String("bad".to_string()),
                Value::String("[invalid".to_string()),
            ],
        )
        .expect_err("should fail for bad regex");
        assert!(err.0.contains("invalid"), "error: {}", err.0);
    }

    #[test]
    fn tokenize_basic_class_declaration() {
        let rules = make_rules();
        let tokens = call(
            "TLang.Parser.tokenize",
            &[Value::String("class Foo".to_string()), rules],
        )
        .expect("tokenize should succeed");

        let Value::List(list) = tokens else {
            panic!("expected List");
        };
        // Expect: keyword("class"), whitespace(" "), ident("Foo")
        assert_eq!(list.len(), 3);

        let ty = call("TLang.Parser.token_type", &[list[0].clone()])
            .expect("token_type should work");
        assert_eq!(ty, Value::String("keyword".to_string()));

        let val = call("TLang.Parser.token_value", &[list[0].clone()])
            .expect("token_value should work");
        assert_eq!(val, Value::String("class".to_string()));
    }

    #[test]
    fn tokenize_tracks_line_and_col() {
        let rules = make_rules();
        let tokens = call(
            "TLang.Parser.tokenize",
            &[Value::String("fun\nfoo".to_string()), rules],
        )
        .expect("tokenize should succeed");

        let Value::List(list) = tokens else {
            panic!("expected List");
        };
        // list[0] = keyword "fun" at line 1, col 1
        // list[1] = whitespace "\n" at line 1, col 4
        // list[2] = ident "foo" at line 2, col 1

        let line = call("TLang.Parser.token_line", &[list[2].clone()])
            .expect("token_line should work");
        assert_eq!(line, Value::Int(2));

        let col = call("TLang.Parser.token_col", &[list[2].clone()])
            .expect("token_col should work");
        assert_eq!(col, Value::Int(1));
    }

    #[test]
    fn tokenize_fails_on_unmatched_character() {
        let rules = make_rules(); // only keyword/ident/whitespace
        let err = call(
            "TLang.Parser.tokenize",
            &[Value::String("class@Foo".to_string()), rules],
        )
        .expect_err("should fail on '@'");
        assert!(err.0.contains("no rule matched"), "error: {}", err.0);
    }

    #[test]
    fn filter_type_keeps_only_matching_tokens() {
        let rules = make_rules();
        let tokens = call(
            "TLang.Parser.tokenize",
            &[Value::String("class Foo".to_string()), rules],
        )
        .expect("tokenize should succeed");

        let keywords = call(
            "TLang.Parser.filter_type",
            &[tokens, Value::String("keyword".to_string())],
        )
        .expect("filter_type should succeed");

        let Value::List(list) = keywords else {
            panic!("expected List");
        };
        assert_eq!(list.len(), 1);
        let val = call("TLang.Parser.token_value", &[list[0].clone()]).unwrap();
        assert_eq!(val, Value::String("class".to_string()));
    }

    #[test]
    fn skip_types_removes_whitespace() {
        let rules = make_rules();
        let tokens = call(
            "TLang.Parser.tokenize",
            &[Value::String("class Foo".to_string()), rules],
        )
        .expect("tokenize should succeed");

        let no_ws = call(
            "TLang.Parser.skip_types",
            &[
                tokens,
                Value::List(vec![Value::String("whitespace".to_string())]),
            ],
        )
        .expect("skip_types should succeed");

        let Value::List(list) = no_ws else {
            panic!("expected List");
        };
        assert_eq!(list.len(), 2);
    }

    #[test]
    fn tokenize_empty_source_returns_empty_list() {
        let rules = make_rules();
        let result = call(
            "TLang.Parser.tokenize",
            &[Value::String(String::new()), rules],
        )
        .expect("tokenize empty source should succeed");
        assert_eq!(result, Value::List(vec![]));
    }

    #[test]
    fn tokenize_errors_on_empty_match() {
        // `a*` can match zero characters — should not loop forever
        let zero_rule = call(
            "TLang.Parser.rule",
            &[
                Value::String("zero".to_string()),
                Value::String("a*".to_string()),
            ],
        )
        .expect("rule should build");
        let err = call(
            "TLang.Parser.tokenize",
            &[
                Value::String("b".to_string()),
                Value::List(vec![zero_rule]),
            ],
        )
        .expect_err("should fail on empty match");
        assert!(
            err.0.contains("empty string"),
            "expected empty-string error, got: {}",
            err.0
        );
    }
}
