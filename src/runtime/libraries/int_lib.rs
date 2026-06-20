// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang.Int — 32-bit integer library.
//!
//! Both Int and Long use `Value::Int(i64)` at runtime.
//! Int semantics are capped to the 32-bit signed range for minValue/maxValue.

use super::super::{RuntimeError, Value};

// ── dispatch ─────────────────────────────────────────────────────────────────

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Int.toString" => to_string(args),
        "TLang.Int.parse" => parse(args),
        "TLang.Int.toHex" => to_hex(args),
        "TLang.Int.toBinary" => to_binary(args),
        "TLang.Int.toOctal" => to_octal(args),
        "TLang.Int.fromHex" => from_hex(args),
        "TLang.Int.fromBinary" => from_binary(args),
        "TLang.Int.fromOctal" => from_octal(args),
        "TLang.Int.range" => range(args),
        "TLang.Int.rangeTo" => range_to(args),
        "TLang.Int.minValue" => min_value(args),
        "TLang.Int.maxValue" => max_value(args),
        "TLang.Int.abs" => abs(args),
        "TLang.Int.clamp" => clamp(args),
        "TLang.Int.toFloat" => to_float(args),
        "TLang.Int.toDouble" => to_double(args),
        _ => Err(RuntimeError(format!(
            "Unknown TLang.Int function: {target}"
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
            "TLang.Int.{name} expects {n} argument(s), got {}",
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
        .map_err(|_| RuntimeError(format!("TLang.Int.parse: cannot parse {:?} as Int", s)))
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

fn to_octal(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "toOctal")?;
    let n = expect_int(&args[0], "argument")?;
    Ok(Value::String(format!("{:o}", n)))
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
        .map_err(|_| RuntimeError(format!("TLang.Int.fromHex: cannot parse {:?} as hex", s)))
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
                "TLang.Int.fromBinary: cannot parse {:?} as binary",
                s
            ))
        })
}

fn from_octal(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 1, "fromOctal")?;
    let s = expect_string(&args[0], "argument")?;
    let stripped = s
        .trim()
        .strip_prefix("0o")
        .or_else(|| s.trim().strip_prefix("0O"))
        .unwrap_or(s.trim());
    i64::from_str_radix(stripped, 8)
        .map(Value::Int)
        .map_err(|_| {
            RuntimeError(format!(
                "TLang.Int.fromOctal: cannot parse {:?} as octal",
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
    Ok(Value::Int(i32::MIN as i64))
}

fn max_value(args: &[Value]) -> Result<Value, RuntimeError> {
    expect_args(args, 0, "maxValue")?;
    Ok(Value::Int(i32::MAX as i64))
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
