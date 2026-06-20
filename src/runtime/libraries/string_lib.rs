// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang.String — immutable string library.
//!
//! Every function returns a *new* value; no in-place mutation occurs.

use super::super::{RuntimeError, Value};
use super::expect_string;

// ── dispatch ────────────────────────────────────────────────────────────────

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.String.length" => length(args),
        "TLang.String.isEmpty" => is_empty(args),
        "TLang.String.toUpperCase" => to_upper_case(args),
        "TLang.String.toLowerCase" => to_lower_case(args),
        "TLang.String.trim" => trim(args),
        "TLang.String.trimStart" => trim_start(args),
        "TLang.String.trimEnd" => trim_end(args),
        "TLang.String.contains" => contains(args),
        "TLang.String.startsWith" => starts_with(args),
        "TLang.String.endsWith" => ends_with(args),
        "TLang.String.equals" => equals(args),
        "TLang.String.equalsIgnoreCase" => equals_ignore_case(args),
        "TLang.String.compare" => compare(args),
        "TLang.String.indexOf" => index_of(args),
        "TLang.String.lastIndexOf" => last_index_of(args),
        "TLang.String.substring" => substring(args),
        "TLang.String.slice" => slice(args),
        "TLang.String.split" => split(args),
        "TLang.String.replace" => replace(args),
        "TLang.String.replaceAll" => replace_all(args),
        "TLang.String.concat" => concat(args),
        "TLang.String.repeat" => repeat(args),
        "TLang.String.charAt" => char_at(args),
        "TLang.String.charCodeAt" => char_code_at(args),
        "TLang.String.fromCharCode" => from_char_code(args),
        "TLang.String.lines" => lines(args),
        "TLang.String.words" => words(args),
        _ => Err(RuntimeError(format!(
            "unknown String library function `{target}`"
        ))),
    }
}

// ── helpers ──────────────────────────────────────────────────────────────────

fn str_arg(args: &[Value], index: usize, context: &str) -> Result<String, RuntimeError> {
    args.get(index)
        .ok_or_else(|| RuntimeError(format!("{context}: missing argument at index {index}")))
        .and_then(|v| expect_string(v, context))
}

fn int_arg(args: &[Value], index: usize, context: &str) -> Result<i64, RuntimeError> {
    args.get(index)
        .ok_or_else(|| RuntimeError(format!("{context}: missing argument at index {index}")))
        .and_then(|v| match v {
            Value::Int(n) => Ok(*n),
            _ => Err(RuntimeError(format!(
                "{context}: argument at index {index} must be an Int"
            ))),
        })
}

fn check_arity(args: &[Value], expected: usize, name: &str) -> Result<(), RuntimeError> {
    if args.len() != expected {
        Err(RuntimeError(format!(
            "TLang.String.{name} expects exactly {expected} argument(s), got {}",
            args.len()
        )))
    } else {
        Ok(())
    }
}

/// Convert a byte index returned by Rust's `str::find` into a TLang Int
/// (character-level index so multi-byte UTF-8 is handled correctly).
fn byte_to_char_index(s: &str, byte_idx: usize) -> i64 {
    s[..byte_idx].chars().count() as i64
}

/// Convert a caller-supplied character index into a byte offset for slicing.
/// Returns an error if the index is out of bounds.
#[allow(dead_code)] // Utility for future string indexing/slicing operations.
fn char_to_byte(s: &str, char_idx: i64, context: &str) -> Result<usize, RuntimeError> {
    let count = s.chars().count() as i64;
    if char_idx < 0 || char_idx > count {
        return Err(RuntimeError(format!(
            "{context}: index {char_idx} out of bounds for string of length {count}"
        )));
    }
    Ok(s.char_indices()
        .nth(char_idx as usize)
        .map(|(b, _)| b)
        .unwrap_or(s.len()))
}

// ── implementations ───────────────────────────────────────────────────────────

/// `length(s) -> Int`  — number of Unicode characters (not bytes).
fn length(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "length")?;
    let s = str_arg(args, 0, "TLang.String.length")?;
    Ok(Value::Int(s.chars().count() as i64))
}

/// `isEmpty(s) -> Bool`
fn is_empty(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "isEmpty")?;
    let s = str_arg(args, 0, "TLang.String.isEmpty")?;
    Ok(Value::Bool(s.is_empty()))
}

/// `toUpperCase(s) -> String`
fn to_upper_case(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "toUpperCase")?;
    Ok(Value::String(
        str_arg(args, 0, "TLang.String.toUpperCase")?.to_uppercase(),
    ))
}

/// `toLowerCase(s) -> String`
fn to_lower_case(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "toLowerCase")?;
    Ok(Value::String(
        str_arg(args, 0, "TLang.String.toLowerCase")?.to_lowercase(),
    ))
}

/// `trim(s) -> String`  — remove leading & trailing whitespace.
fn trim(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "trim")?;
    Ok(Value::String(
        str_arg(args, 0, "TLang.String.trim")?.trim().to_string(),
    ))
}

/// `trimStart(s) -> String`
fn trim_start(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "trimStart")?;
    Ok(Value::String(
        str_arg(args, 0, "TLang.String.trimStart")?
            .trim_start()
            .to_string(),
    ))
}

/// `trimEnd(s) -> String`
fn trim_end(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "trimEnd")?;
    Ok(Value::String(
        str_arg(args, 0, "TLang.String.trimEnd")?
            .trim_end()
            .to_string(),
    ))
}

/// `contains(s, sub) -> Bool`
fn contains(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "contains")?;
    let s = str_arg(args, 0, "TLang.String.contains")?;
    let sub = str_arg(args, 1, "TLang.String.contains")?;
    Ok(Value::Bool(s.contains(sub.as_str())))
}

/// `startsWith(s, prefix) -> Bool`
fn starts_with(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "startsWith")?;
    let s = str_arg(args, 0, "TLang.String.startsWith")?;
    let prefix = str_arg(args, 1, "TLang.String.startsWith")?;
    Ok(Value::Bool(s.starts_with(prefix.as_str())))
}

/// `endsWith(s, suffix) -> Bool`
fn ends_with(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "endsWith")?;
    let s = str_arg(args, 0, "TLang.String.endsWith")?;
    let suffix = str_arg(args, 1, "TLang.String.endsWith")?;
    Ok(Value::Bool(s.ends_with(suffix.as_str())))
}

/// `equals(a, b) -> Bool`  — exact equality (case-sensitive).
fn equals(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "equals")?;
    let a = str_arg(args, 0, "TLang.String.equals")?;
    let b = str_arg(args, 1, "TLang.String.equals")?;
    Ok(Value::Bool(a == b))
}

/// `equalsIgnoreCase(a, b) -> Bool`
fn equals_ignore_case(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "equalsIgnoreCase")?;
    let a = str_arg(args, 0, "TLang.String.equalsIgnoreCase")?;
    let b = str_arg(args, 1, "TLang.String.equalsIgnoreCase")?;
    Ok(Value::Bool(a.to_lowercase() == b.to_lowercase()))
}

/// `compare(a, b) -> Int`  — lexicographic (alphabetical) order.
///   Returns -1 if a < b, 0 if a == b, 1 if a > b.
fn compare(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "compare")?;
    let a = str_arg(args, 0, "TLang.String.compare")?;
    let b = str_arg(args, 1, "TLang.String.compare")?;
    let ord = a.cmp(&b);
    Ok(Value::Int(match ord {
        std::cmp::Ordering::Less => -1,
        std::cmp::Ordering::Equal => 0,
        std::cmp::Ordering::Greater => 1,
    }))
}

/// `indexOf(s, sub) -> Int`  — first char-index of `sub`, or -1 if not found.
fn index_of(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "indexOf")?;
    let s = str_arg(args, 0, "TLang.String.indexOf")?;
    let sub = str_arg(args, 1, "TLang.String.indexOf")?;
    let idx = s
        .find(sub.as_str())
        .map(|b| byte_to_char_index(&s, b))
        .unwrap_or(-1);
    Ok(Value::Int(idx))
}

/// `lastIndexOf(s, sub) -> Int`  — last char-index of `sub`, or -1 if not found.
fn last_index_of(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "lastIndexOf")?;
    let s = str_arg(args, 0, "TLang.String.lastIndexOf")?;
    let sub = str_arg(args, 1, "TLang.String.lastIndexOf")?;
    let idx = s
        .rfind(sub.as_str())
        .map(|b| byte_to_char_index(&s, b))
        .unwrap_or(-1);
    Ok(Value::Int(idx))
}

/// `substring(s, start, end) -> String`
///   Characters from `start` (inclusive) to `end` (exclusive), both 0-based char indices.
fn substring(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 3, "substring")?;
    let s = str_arg(args, 0, "TLang.String.substring")?;
    let start = int_arg(args, 1, "TLang.String.substring")?;
    let end = int_arg(args, 2, "TLang.String.substring")?;

    let len = s.chars().count() as i64;
    if start < 0 || end < start || end > len {
        return Err(RuntimeError(format!(
            "TLang.String.substring: invalid range [{start}, {end}) for string of length {len}"
        )));
    }
    let result: String = s
        .chars()
        .skip(start as usize)
        .take((end - start) as usize)
        .collect();
    Ok(Value::String(result))
}

/// `slice(s, start, end) -> String`
///   Like `substring` but accepts negative indices which count from the end.
fn slice(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 3, "slice")?;
    let s = str_arg(args, 0, "TLang.String.slice")?;
    let len = s.chars().count() as i64;

    let mut start = int_arg(args, 1, "TLang.String.slice")?;
    let mut end = int_arg(args, 2, "TLang.String.slice")?;

    if start < 0 {
        start = (len + start).max(0);
    }
    if end < 0 {
        end = (len + end).max(0);
    }
    let start = start.min(len);
    let end = end.min(len);

    if start >= end {
        return Ok(Value::String(String::new()));
    }
    let result: String = s
        .chars()
        .skip(start as usize)
        .take((end - start) as usize)
        .collect();
    Ok(Value::String(result))
}

/// `split(s, delimiter) -> List<String>` (returned as a TLang array Value)
fn split(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "split")?;
    let s = str_arg(args, 0, "TLang.String.split")?;
    let sep = str_arg(args, 1, "TLang.String.split")?;
    let parts: Vec<Value> = s
        .split(sep.as_str())
        .map(|p| Value::String(p.to_string()))
        .collect();
    Ok(Value::List(parts))
}

/// `replace(s, from, to) -> String`  — replace the **first** occurrence.
fn replace(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 3, "replace")?;
    let s = str_arg(args, 0, "TLang.String.replace")?;
    let from = str_arg(args, 1, "TLang.String.replace")?;
    let to = str_arg(args, 2, "TLang.String.replace")?;
    Ok(Value::String(s.replacen(from.as_str(), &to, 1)))
}

/// `replaceAll(s, from, to) -> String`  — replace **all** occurrences.
fn replace_all(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 3, "replaceAll")?;
    let s = str_arg(args, 0, "TLang.String.replaceAll")?;
    let from = str_arg(args, 1, "TLang.String.replaceAll")?;
    let to = str_arg(args, 2, "TLang.String.replaceAll")?;
    Ok(Value::String(s.replace(from.as_str(), &to)))
}

/// `concat(a, b) -> String`  — concatenate two strings.
fn concat(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "concat")?;
    let a = str_arg(args, 0, "TLang.String.concat")?;
    let b = str_arg(args, 1, "TLang.String.concat")?;
    Ok(Value::String(format!("{a}{b}")))
}

/// `repeat(s, n) -> String`  — repeat `s` exactly `n` times.
fn repeat(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "repeat")?;
    let s = str_arg(args, 0, "TLang.String.repeat")?;
    let n = int_arg(args, 1, "TLang.String.repeat")?;
    if n < 0 {
        return Err(RuntimeError(format!(
            "TLang.String.repeat: count must be >= 0, got {n}"
        )));
    }
    Ok(Value::String(s.repeat(n as usize)))
}

/// `charAt(s, index) -> String`  — single-character string at `index`.
fn char_at(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "charAt")?;
    let s = str_arg(args, 0, "TLang.String.charAt")?;
    let idx = int_arg(args, 1, "TLang.String.charAt")?;
    let len = s.chars().count() as i64;
    if idx < 0 || idx >= len {
        return Err(RuntimeError(format!(
            "TLang.String.charAt: index {idx} out of bounds for string of length {len}"
        )));
    }
    let ch = s.chars().nth(idx as usize).unwrap();
    Ok(Value::String(ch.to_string()))
}

/// `charCodeAt(s, index) -> Int`  — Unicode code point at `index`.
fn char_code_at(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "charCodeAt")?;
    let s = str_arg(args, 0, "TLang.String.charCodeAt")?;
    let idx = int_arg(args, 1, "TLang.String.charCodeAt")?;
    let len = s.chars().count() as i64;
    if idx < 0 || idx >= len {
        return Err(RuntimeError(format!(
            "TLang.String.charCodeAt: index {idx} out of bounds for string of length {len}"
        )));
    }
    let cp = s.chars().nth(idx as usize).unwrap() as i64;
    Ok(Value::Int(cp))
}

/// `fromCharCode(code) -> String`  — string consisting of a single Unicode code point.
fn from_char_code(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "fromCharCode")?;
    let code = int_arg(args, 0, "TLang.String.fromCharCode")?;
    let ch = char::from_u32(code as u32).ok_or_else(|| {
        RuntimeError(format!(
            "TLang.String.fromCharCode: {code} is not a valid Unicode code point"
        ))
    })?;
    Ok(Value::String(ch.to_string()))
}

/// `lines(s) -> List<String>`  — split on newlines (handles `\r\n` and `\n`).
fn lines(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "lines")?;
    let s = str_arg(args, 0, "TLang.String.lines")?;
    let parts: Vec<Value> = s.lines().map(|l| Value::String(l.to_string())).collect();
    Ok(Value::List(parts))
}

/// `words(s) -> List<String>`  — split on any whitespace sequence.
fn words(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "words")?;
    let s = str_arg(args, 0, "TLang.String.words")?;
    let parts: Vec<Value> = s
        .split_whitespace()
        .map(|w| Value::String(w.to_string()))
        .collect();
    Ok(Value::List(parts))
}

// ── tests ─────────────────────────────────────────────────────────────────────

#[cfg(test)]
mod tests {
    use super::*;

    fn s(v: &str) -> Value {
        Value::String(v.to_string())
    }
    fn i(v: i64) -> Value {
        Value::Int(v)
    }
    fn b(v: bool) -> Value {
        Value::Bool(v)
    }

    // -- length ---------------------------------------------------------------
    #[test]
    fn test_length_ascii() {
        assert_eq!(call("TLang.String.length", &[s("hello")]).unwrap(), i(5));
    }

    #[test]
    fn test_length_unicode() {
        // "café" has 4 Unicode characters but 5 bytes in UTF-8
        assert_eq!(call("TLang.String.length", &[s("café")]).unwrap(), i(4));
    }

    #[test]
    fn test_length_empty() {
        assert_eq!(call("TLang.String.length", &[s("")]).unwrap(), i(0));
    }

    // -- isEmpty ---------------------------------------------------------------
    #[test]
    fn test_is_empty_true() {
        assert_eq!(call("TLang.String.isEmpty", &[s("")]).unwrap(), b(true));
    }

    #[test]
    fn test_is_empty_false() {
        assert_eq!(call("TLang.String.isEmpty", &[s("x")]).unwrap(), b(false));
    }

    // -- case -----------------------------------------------------------------
    #[test]
    fn test_to_upper() {
        assert_eq!(
            call("TLang.String.toUpperCase", &[s("hello")]).unwrap(),
            s("HELLO")
        );
    }

    #[test]
    fn test_to_lower() {
        assert_eq!(
            call("TLang.String.toLowerCase", &[s("WORLD")]).unwrap(),
            s("world")
        );
    }

    // -- trim -----------------------------------------------------------------
    #[test]
    fn test_trim() {
        assert_eq!(call("TLang.String.trim", &[s("  hi  ")]).unwrap(), s("hi"));
    }

    #[test]
    fn test_trim_start() {
        assert_eq!(
            call("TLang.String.trimStart", &[s("  hi  ")]).unwrap(),
            s("hi  ")
        );
    }

    #[test]
    fn test_trim_end() {
        assert_eq!(
            call("TLang.String.trimEnd", &[s("  hi  ")]).unwrap(),
            s("  hi")
        );
    }

    // -- contains / startsWith / endsWith -------------------------------------
    #[test]
    fn test_contains_true() {
        assert_eq!(
            call("TLang.String.contains", &[s("foobar"), s("oob")]).unwrap(),
            b(true)
        );
    }

    #[test]
    fn test_contains_false() {
        assert_eq!(
            call("TLang.String.contains", &[s("foobar"), s("xyz")]).unwrap(),
            b(false)
        );
    }

    #[test]
    fn test_starts_with() {
        assert_eq!(
            call("TLang.String.startsWith", &[s("foobar"), s("foo")]).unwrap(),
            b(true)
        );
    }

    #[test]
    fn test_ends_with() {
        assert_eq!(
            call("TLang.String.endsWith", &[s("foobar"), s("bar")]).unwrap(),
            b(true)
        );
    }

    // -- equals / compare -----------------------------------------------------
    #[test]
    fn test_equals_true() {
        assert_eq!(
            call("TLang.String.equals", &[s("abc"), s("abc")]).unwrap(),
            b(true)
        );
    }

    #[test]
    fn test_equals_false() {
        assert_eq!(
            call("TLang.String.equals", &[s("abc"), s("ABC")]).unwrap(),
            b(false)
        );
    }

    #[test]
    fn test_equals_ignore_case() {
        assert_eq!(
            call("TLang.String.equalsIgnoreCase", &[s("Hello"), s("hElLo")]).unwrap(),
            b(true)
        );
    }

    #[test]
    fn test_compare_less() {
        assert_eq!(
            call("TLang.String.compare", &[s("apple"), s("banana")]).unwrap(),
            i(-1)
        );
    }

    #[test]
    fn test_compare_equal() {
        assert_eq!(
            call("TLang.String.compare", &[s("same"), s("same")]).unwrap(),
            i(0)
        );
    }

    #[test]
    fn test_compare_greater() {
        assert_eq!(
            call("TLang.String.compare", &[s("zoo"), s("ant")]).unwrap(),
            i(1)
        );
    }

    // -- indexOf / lastIndexOf ------------------------------------------------
    #[test]
    fn test_index_of_found() {
        assert_eq!(
            call("TLang.String.indexOf", &[s("hello world"), s("world")]).unwrap(),
            i(6)
        );
    }

    #[test]
    fn test_index_of_not_found() {
        assert_eq!(
            call("TLang.String.indexOf", &[s("hello"), s("xyz")]).unwrap(),
            i(-1)
        );
    }

    #[test]
    fn test_last_index_of() {
        assert_eq!(
            call("TLang.String.lastIndexOf", &[s("abcabc"), s("bc")]).unwrap(),
            i(4)
        );
    }

    // -- substring / slice ----------------------------------------------------
    #[test]
    fn test_substring() {
        assert_eq!(
            call("TLang.String.substring", &[s("hello"), i(1), i(4)]).unwrap(),
            s("ell")
        );
    }

    #[test]
    fn test_substring_out_of_bounds() {
        assert!(call("TLang.String.substring", &[s("hi"), i(0), i(5)]).is_err());
    }

    #[test]
    fn test_slice_positive() {
        assert_eq!(
            call("TLang.String.slice", &[s("hello"), i(1), i(4)]).unwrap(),
            s("ell")
        );
    }

    #[test]
    fn test_slice_negative() {
        assert_eq!(
            call("TLang.String.slice", &[s("hello"), i(-3), i(-1)]).unwrap(),
            s("ll")
        );
    }

    // -- split ----------------------------------------------------------------
    #[test]
    fn test_split() {
        let result = call("TLang.String.split", &[s("a,b,c"), s(",")]).unwrap();
        assert_eq!(result, Value::List(vec![s("a"), s("b"), s("c")]));
    }

    // -- replace / replaceAll -------------------------------------------------
    #[test]
    fn test_replace_first() {
        assert_eq!(
            call("TLang.String.replace", &[s("aabbaa"), s("aa"), s("X")]).unwrap(),
            s("Xbbaa")
        );
    }

    #[test]
    fn test_replace_all() {
        assert_eq!(
            call("TLang.String.replaceAll", &[s("aabbaa"), s("aa"), s("X")]).unwrap(),
            s("XbbX")
        );
    }

    // -- concat / repeat ------------------------------------------------------
    #[test]
    fn test_concat() {
        assert_eq!(
            call("TLang.String.concat", &[s("foo"), s("bar")]).unwrap(),
            s("foobar")
        );
    }

    #[test]
    fn test_repeat() {
        assert_eq!(
            call("TLang.String.repeat", &[s("ab"), i(3)]).unwrap(),
            s("ababab")
        );
    }

    #[test]
    fn test_repeat_zero() {
        assert_eq!(
            call("TLang.String.repeat", &[s("ab"), i(0)]).unwrap(),
            s("")
        );
    }

    // -- charAt / charCodeAt / fromCharCode -----------------------------------
    #[test]
    fn test_char_at() {
        assert_eq!(
            call("TLang.String.charAt", &[s("hello"), i(1)]).unwrap(),
            s("e")
        );
    }

    #[test]
    fn test_char_at_out_of_bounds() {
        assert!(call("TLang.String.charAt", &[s("hi"), i(5)]).is_err());
    }

    #[test]
    fn test_char_code_at() {
        assert_eq!(
            call("TLang.String.charCodeAt", &[s("A"), i(0)]).unwrap(),
            i(65)
        );
    }

    #[test]
    fn test_from_char_code() {
        assert_eq!(call("TLang.String.fromCharCode", &[i(65)]).unwrap(), s("A"));
    }

    #[test]
    fn test_from_char_code_invalid() {
        // 0xD800 is a surrogate, not a valid scalar
        assert!(call("TLang.String.fromCharCode", &[i(0xD800)]).is_err());
    }

    // -- lines / words --------------------------------------------------------
    #[test]
    fn test_lines() {
        let result = call("TLang.String.lines", &[s("a\nb\nc")]).unwrap();
        assert_eq!(result, Value::List(vec![s("a"), s("b"), s("c")]));
    }

    #[test]
    fn test_words() {
        let result = call("TLang.String.words", &[s("  hello   world  ")]).unwrap();
        assert_eq!(result, Value::List(vec![s("hello"), s("world")]));
    }

    // -- arity errors ---------------------------------------------------------
    #[test]
    fn test_wrong_arity() {
        assert!(call("TLang.String.length", &[]).is_err());
        assert!(call("TLang.String.contains", &[s("x")]).is_err());
    }

    // -- unknown method -------------------------------------------------------
    #[test]
    fn test_unknown_method() {
        assert!(call("TLang.String.nonExistent", &[s("x")]).is_err());
    }
}
