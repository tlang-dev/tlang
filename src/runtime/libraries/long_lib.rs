// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang.Long — 64-bit integer library.
//!
//! Both Int and Long use `Value::Int(i64)` at runtime.
//! Long semantics span the full 64-bit signed range for minValue/maxValue.

use super::super::{RuntimeError, Value};

// ── dispatch ─────────────────────────────────────────────────────────────────

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Long.toString" => to_string(args),
        "TLang.Long.parse" => parse(args),
        "TLang.Long.toHex" => to_hex(args),
        "TLang.Long.toBinary" => to_binary(args),
        "TLang.Long.fromHex" => from_hex(args),
        "TLang.Long.fromBinary" => from_binary(args),
        "TLang.Long.range" => range(args),
        "TLang.Long.rangeTo" => range_to(args),
        "TLang.Long.minValue" => min_value(args),
        "TLang.Long.maxValue" => max_value(args),
        "TLang.Long.abs" => abs(args),
        "TLang.Long.clamp" => clamp(args),
        "TLang.Long.toFloat" => to_float(args),
        "TLang.Long.toDouble" => to_double(args),
        _ => Err(RuntimeError(format!(
            "Unknown TLang.Long function: {target}"
        ))),
    }
}

// ── helpers ───────────────────────────────────────────────────────────────────

fn expect_int(v: &Value, ctx: &str) -> Result<i64, RuntimeError> {
    match v {
        Value::Int(n) => Ok(*n),
        _ => Err(RuntimeError(format!("{ctx} must be an Int"))),
    }
}

fn expect_string<'a>(v: &'a Value, ctx: &str) -> Result<&'a str, RuntimeError> {
    match v {
        Value::String(s) => Ok(s.as_str()),
        _ => Err(RuntimeError(format!("{ctx} must be a String"))),
    }
}

fn expect_args(args: &[Value], n: usize, name: &str) -> Result<(), RuntimeError> {
    if args.len() == n {
        Ok(())
    } else {
        Err(RuntimeError(format!(
            "TLang.Long.{name} expects {n} argument(s), got {}",
            args.len()
        )))
    }
}

const MAX_RANGE: i64 = 1_000_000;

// ── implementations ───────────────────────────────────────────────────────────

fn to_string(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "toString")?;
    let n = expect_int(&args[0], "argument")?;
    Ok(Value::String(n.to_string()))
}

fn parse(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "parse")?;
    let s = expect_string(&args[0], "argument")?;
    s.trim()
        .parse::<i64>()
        .map(Value::Int)
        .map_err(|_| RuntimeError(format!("TLang.Long.parse: cannot parse {:?} as Long", s)))
}

fn to_hex(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "toHex")?;
    let n = expect_int(&args[0], "argument")?;
    Ok(Value::String(format!("{:x}", n)))
}

fn to_binary(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "toBinary")?;
    let n = expect_int(&args[0], "argument")?;
    Ok(Value::String(format!("{:b}", n)))
}

fn from_hex(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "fromHex")?;
    let s = expect_string(&args[0], "argument")?;
    let stripped = s
        .trim()
        .strip_prefix("0x")
        .or_else(|| s.trim().strip_prefix("0X"))
        .unwrap_or(s.trim());
    i64::from_str_radix(stripped, 16)
        .map(Value::Int)
        .map_err(|_| RuntimeError(format!("TLang.Long.fromHex: cannot parse {:?} as hex", s)))
}

fn from_binary(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "fromBinary")?;
    let s = expect_string(&args[0], "argument")?;
    let stripped = s
        .trim()
        .strip_prefix("0b")
        .or_else(|| s.trim().strip_prefix("0B"))
        .unwrap_or(s.trim());
    i64::from_str_radix(stripped, 2)
        .map(Value::Int)
        .map_err(|_| {
            RuntimeError(format!(
                "TLang.Long.fromBinary: cannot parse {:?} as binary",
                s
            ))
        })
}

fn range(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 2, "range")?;
    let start = expect_int(&args[0], "start")?;
    let end = expect_int(&args[1], "end")?;
    if start >= end {
        return Ok(Value::List(vec![]));
    }
    let len = (end - start).min(MAX_RANGE);
    let list = (start..start + len).map(Value::Int).collect();
    Ok(Value::List(list))
}

fn range_to(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 2, "rangeTo")?;
    let start = expect_int(&args[0], "start")?;
    let end = expect_int(&args[1], "end")?;
    if start > end {
        return Ok(Value::List(vec![]));
    }
    let len = (end - start + 1).min(MAX_RANGE);
    let list = (start..start + len).map(Value::Int).collect();
    Ok(Value::List(list))
}

fn min_value(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 0, "minValue")?;
    Ok(Value::Int(i64::MIN))
}

fn max_value(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 0, "maxValue")?;
    Ok(Value::Int(i64::MAX))
}

fn abs(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "abs")?;
    let n = expect_int(&args[0], "argument")?;
    Ok(Value::Int(n.abs()))
}

fn clamp(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 3, "clamp")?;
    let n = expect_int(&args[0], "n")?;
    let lo = expect_int(&args[1], "lo")?;
    let hi = expect_int(&args[2], "hi")?;
    Ok(Value::Int(n.clamp(lo, hi)))
}

fn to_float(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "toFloat")?;
    let n = expect_int(&args[0], "argument")?;
    Ok(Value::Float(n as f32 as f64))
}

fn to_double(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "toDouble")?;
    let n = expect_int(&args[0], "argument")?;
    Ok(Value::Float(n as f64))
}
