// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::cell::RefCell;
use std::rc::Rc;

use super::super::{RuntimeError, Value};

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.StringBuilder.create" | "TLang.StringBuilder.new" => create(args),
        "TLang.StringBuilder.append" => append(args),
        "TLang.StringBuilder.build" => build(args),
        _ => Err(RuntimeError(format!(
            "unknown StringBuilder function `{target}`"
        ))),
    }
}

// ── TLang.StringBuilder.new() ─────────────────────────────────────────────────

fn create(args: &[Value]) -> Result<Value, RuntimeError> {
    if !args.is_empty() {
        return Err(RuntimeError(
            "TLang.StringBuilder.new() expects no arguments".to_string(),
        ));
    }
    Ok(Value::StringBuilder(Rc::new(RefCell::new(String::new()))))
}

// ── TLang.StringBuilder.append(sb, text) ─────────────────────────────────────
//
// Static form kept for backward-compatibility with code that cannot yet use the
// method-call syntax `sb.append(text)`.
//
// When called with the new `Value::StringBuilder` the text is appended in-place
// and the same reference is returned, so callers that do
//
//     let result = TLang.StringBuilder.append(sb, text)
//
// get back the same builder.  No re-binding of `sb` is needed: just discard the
// return value or assign it to a fresh name.

fn append(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() < 2 {
        return Err(RuntimeError(
            "TLang.StringBuilder.append expects at least two arguments: (sb, text...)".to_string(),
        ));
    }

    match &args[0] {
        Value::StringBuilder(buf) => {
            // Mutable reference form: append in-place, return the same builder.
            for arg in &args[1..] {
                buf.borrow_mut()
                    .push_str(&super::super::value_to_string(arg));
            }
            Ok(args[0].clone())
        }
        _ => Err(RuntimeError(
            "TLang.StringBuilder.append: first argument must be a StringBuilder \
             (created with TLang.StringBuilder.new())"
                .to_string(),
        )),
    }
}

// ── TLang.StringBuilder.build(sb) ────────────────────────────────────────────
//
// Extracts the accumulated string.  The builder continues to be usable after
// this call; it is not consumed.

fn build(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.StringBuilder.build expects exactly one argument".to_string(),
        ));
    }

    match &args[0] {
        Value::StringBuilder(buf) => Ok(Value::String(buf.borrow().clone())),
        _ => Err(RuntimeError(
            "TLang.StringBuilder.build: argument must be a StringBuilder \
             (created with TLang.StringBuilder.new())"
                .to_string(),
        )),
    }
}
