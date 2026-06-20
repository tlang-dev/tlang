// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang.Assert — test assertion library.
//!
//! Assert functions record failures to a thread-local list instead of
//! throwing RuntimeError.  This lets a test block run to completion and
//! report *all* failures at once.
//!
//! Use `begin_test()` before running a test block and `end_test()` after
//! to collect the accumulated failure messages.

use std::cell::RefCell;

use super::super::{RuntimeError, Value};

thread_local! {
    static FAILURES: RefCell<Vec<String>> = RefCell::new(Vec::new());
    static IN_TEST: RefCell<bool> = RefCell::new(false);
}

/// Start a test — clears the failure list and enables recording.
pub fn begin_test() {
    FAILURES.with(|f| f.borrow_mut().clear());
    IN_TEST.with(|t| *t.borrow_mut() = true);
}

/// End a test — disables recording and returns accumulated failures.
pub fn end_test() -> Vec<String> {
    IN_TEST.with(|t| *t.borrow_mut() = false);
    FAILURES.with(|f| f.borrow().clone())
}

fn record(msg: String) {
    FAILURES.with(|f| f.borrow_mut().push(msg));
}

// ── dispatch ────────────────────────────────────────────────────────────────

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Assert.isTrue" => is_true(args),
        "TLang.Assert.isFalse" => is_false(args),
        "TLang.Assert.equals" => equals(args),
        "TLang.Assert.notEquals" => not_equals(args),
        "TLang.Assert.contains" => contains(args),
        "TLang.Assert.notContains" => not_contains(args),
        "TLang.Assert.startsWith" => starts_with(args),
        "TLang.Assert.endsWith" => ends_with(args),
        "TLang.Assert.isEmpty" => is_empty(args),
        "TLang.Assert.notEmpty" => not_empty(args),
        "TLang.Assert.isNull" => is_null(args),
        "TLang.Assert.notNull" => not_null(args),
        "TLang.Assert.fail" => fail(args),
        _ => Err(RuntimeError(format!(
            "unknown Assert function `{target}`"
        ))),
    }
}

// ── helpers ─────────────────────────────────────────────────────────────────

fn value_display(v: &Value) -> String {
    match v {
        Value::String(s) => format!("\"{s}\""),
        Value::Int(n) => n.to_string(),
        Value::Bool(b) => b.to_string(),
        Value::Unit => "void".to_string(),
        Value::List(l) => format!("[{}]", l.iter().map(value_display).collect::<Vec<_>>().join(", ")),
        _ => format!("{v:?}"),
    }
}

fn label(args: &[Value], idx: usize) -> String {
    match args.get(idx) {
        Some(Value::String(s)) => format!(" — {s}"),
        _ => String::new(),
    }
}

// ── assertions ───────────────────────────────────────────────────────────────

fn is_true(args: &[Value]) -> Result<Value, RuntimeError> {
    let cond = args.first();
    let lbl = label(args, 1);
    match cond {
        Some(Value::Bool(true)) => {}
        Some(v) => record(format!("Assert.isTrue failed: got {}{lbl}", value_display(v))),
        None => record(format!("Assert.isTrue failed: missing argument{lbl}")),
    }
    Ok(Value::Unit)
}

fn is_false(args: &[Value]) -> Result<Value, RuntimeError> {
    let cond = args.first();
    let lbl = label(args, 1);
    match cond {
        Some(Value::Bool(false)) => {}
        Some(v) => record(format!("Assert.isFalse failed: got {}{lbl}", value_display(v))),
        None => record(format!("Assert.isFalse failed: missing argument{lbl}")),
    }
    Ok(Value::Unit)
}

fn equals(args: &[Value]) -> Result<Value, RuntimeError> {
    let lbl = label(args, 2);
    let (expected, actual) = (args.first(), args.get(1));
    match (expected, actual) {
        (Some(e), Some(a)) if e == a => {}
        (Some(e), Some(a)) => record(format!(
            "Assert.equals failed: expected {} but got {}{lbl}",
            value_display(e),
            value_display(a)
        )),
        _ => record(format!("Assert.equals failed: missing arguments{lbl}")),
    }
    Ok(Value::Unit)
}

fn not_equals(args: &[Value]) -> Result<Value, RuntimeError> {
    let lbl = label(args, 2);
    let (unexpected, actual) = (args.first(), args.get(1));
    match (unexpected, actual) {
        (Some(u), Some(a)) if u == a => record(format!(
            "Assert.notEquals failed: expected values to differ but both were {}{lbl}",
            value_display(u)
        )),
        (Some(_), Some(_)) => {}
        _ => record(format!("Assert.notEquals failed: missing arguments{lbl}")),
    }
    Ok(Value::Unit)
}

fn contains(args: &[Value]) -> Result<Value, RuntimeError> {
    let lbl = label(args, 2);
    match (args.first(), args.get(1)) {
        (Some(Value::String(haystack)), Some(Value::String(needle))) => {
            if !haystack.contains(needle.as_str()) {
                record(format!(
                    "Assert.contains failed: \"{haystack}\" does not contain \"{needle}\"{lbl}"
                ));
            }
        }
        (Some(Value::List(list)), Some(needle)) => {
            if !list.contains(needle) {
                record(format!(
                    "Assert.contains failed: list does not contain {}{lbl}",
                    value_display(needle)
                ));
            }
        }
        _ => record(format!("Assert.contains failed: invalid arguments{lbl}")),
    }
    Ok(Value::Unit)
}

fn not_contains(args: &[Value]) -> Result<Value, RuntimeError> {
    let lbl = label(args, 2);
    match (args.first(), args.get(1)) {
        (Some(Value::String(haystack)), Some(Value::String(needle))) => {
            if haystack.contains(needle.as_str()) {
                record(format!(
                    "Assert.notContains failed: \"{haystack}\" contains \"{needle}\"{lbl}"
                ));
            }
        }
        (Some(Value::List(list)), Some(needle)) => {
            if list.contains(needle) {
                record(format!(
                    "Assert.notContains failed: list contains {}{lbl}",
                    value_display(needle)
                ));
            }
        }
        _ => record(format!("Assert.notContains failed: invalid arguments{lbl}")),
    }
    Ok(Value::Unit)
}

fn starts_with(args: &[Value]) -> Result<Value, RuntimeError> {
    let lbl = label(args, 2);
    match (args.first(), args.get(1)) {
        (Some(Value::String(s)), Some(Value::String(prefix))) => {
            if !s.starts_with(prefix.as_str()) {
                record(format!(
                    "Assert.startsWith failed: \"{s}\" does not start with \"{prefix}\"{lbl}"
                ));
            }
        }
        _ => record(format!("Assert.startsWith failed: expected two strings{lbl}")),
    }
    Ok(Value::Unit)
}

fn ends_with(args: &[Value]) -> Result<Value, RuntimeError> {
    let lbl = label(args, 2);
    match (args.first(), args.get(1)) {
        (Some(Value::String(s)), Some(Value::String(suffix))) => {
            if !s.ends_with(suffix.as_str()) {
                record(format!(
                    "Assert.endsWith failed: \"{s}\" does not end with \"{suffix}\"{lbl}"
                ));
            }
        }
        _ => record(format!("Assert.endsWith failed: expected two strings{lbl}")),
    }
    Ok(Value::Unit)
}

fn is_empty(args: &[Value]) -> Result<Value, RuntimeError> {
    let lbl = label(args, 1);
    match args.first() {
        Some(Value::String(s)) if s.is_empty() => {}
        Some(Value::List(l)) if l.is_empty() => {}
        Some(v) => record(format!(
            "Assert.isEmpty failed: got {}{lbl}",
            value_display(v)
        )),
        None => record(format!("Assert.isEmpty failed: missing argument{lbl}")),
    }
    Ok(Value::Unit)
}

fn not_empty(args: &[Value]) -> Result<Value, RuntimeError> {
    let lbl = label(args, 1);
    match args.first() {
        Some(Value::String(s)) if !s.is_empty() => {}
        Some(Value::List(l)) if !l.is_empty() => {}
        Some(Value::String(_)) => record(format!("Assert.notEmpty failed: string is empty{lbl}")),
        Some(Value::List(_)) => record(format!("Assert.notEmpty failed: list is empty{lbl}")),
        Some(v) => record(format!(
            "Assert.notEmpty failed: got {}{lbl}",
            value_display(v)
        )),
        None => record(format!("Assert.notEmpty failed: missing argument{lbl}")),
    }
    Ok(Value::Unit)
}

fn is_null(args: &[Value]) -> Result<Value, RuntimeError> {
    let lbl = label(args, 1);
    match args.first() {
        Some(Value::Unit) | None => {}
        Some(v) => record(format!(
            "Assert.isNull failed: got {}{lbl}",
            value_display(v)
        )),
    }
    Ok(Value::Unit)
}

fn not_null(args: &[Value]) -> Result<Value, RuntimeError> {
    let lbl = label(args, 1);
    match args.first() {
        Some(Value::Unit) | None => {
            record(format!("Assert.notNull failed: value is null{lbl}"))
        }
        Some(_) => {}
    }
    Ok(Value::Unit)
}

fn fail(args: &[Value]) -> Result<Value, RuntimeError> {
    let msg = match args.first() {
        Some(Value::String(s)) => s.clone(),
        _ => "Assert.fail called".to_string(),
    };
    record(msg);
    Ok(Value::Unit)
}
