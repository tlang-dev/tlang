// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang.Float and TLang.Double — floating-point libraries.
//!
//! Both use `Value::Float(f64)` at runtime.
//! `TLang.Float` rounds to f32 precision; `TLang.Double` uses full f64 precision.

use super::super::{RuntimeError, Value};

// ── helpers ──────────────────────────────────────────────────────────────────

fn expect_float(v: &Value, ctx: &str) -> Result<f64, RuntimeError> {
    match v {
        Value::Float(f) => Ok(*f),
        Value::Int(n) => Ok(*n as f64),
        _ => Err(RuntimeError(format!("{ctx} must be a Float or Int"))),
    }
}

pub(crate) fn display_float(v: f64) -> String {
    if v.is_nan() {
        return "NaN".to_string();
    }
    if v.is_infinite() {
        return if v > 0.0 {
            "Infinity".to_string()
        } else {
            "-Infinity".to_string()
        };
    }
    let s = format!("{v}");
    if s.contains('.') || s.contains('e') || s.contains('E') {
        s
    } else {
        format!("{s}.0")
    }
}

// ── dispatch ─────────────────────────────────────────────────────────────────

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        // ── TLang.Float ──────────────────────────────────────────────────────
        "TLang.Float.parse" => float_parse(args),
        "TLang.Float.toString" => float_to_string(args),
        "TLang.Float.fromInt" => float_from_int(args),
        "TLang.Float.toInt" => float_to_int(args),
        "TLang.Float.floor" => float_floor(args),
        "TLang.Float.ceil" => float_ceil(args),
        "TLang.Float.round" => float_round(args),
        "TLang.Float.abs" => float_abs(args),
        "TLang.Float.min" => float_min(args),
        "TLang.Float.max" => float_max(args),
        "TLang.Float.sqrt" => float_sqrt(args),
        "TLang.Float.pow" => float_pow(args),
        "TLang.Float.isNaN" => float_is_nan(args),
        "TLang.Float.isInfinite" => float_is_infinite(args),
        "TLang.Float.pi" => float_pi(args),
        "TLang.Float.e" => float_e(args),
        "TLang.Float.infinity" => float_infinity(args),
        "TLang.Float.nan" => float_nan(args),
        "TLang.Float.add" => float_add(args),
        "TLang.Float.sub" => float_sub(args),
        "TLang.Float.mul" => float_mul(args),
        "TLang.Float.div" => float_div(args),

        // ── TLang.Double ─────────────────────────────────────────────────────
        "TLang.Double.parse" => double_parse(args),
        "TLang.Double.toString" => double_to_string(args),
        "TLang.Double.fromInt" => double_from_int(args),
        "TLang.Double.toInt" => double_to_int(args),
        "TLang.Double.floor" => double_floor(args),
        "TLang.Double.ceil" => double_ceil(args),
        "TLang.Double.round" => double_round(args),
        "TLang.Double.abs" => double_abs(args),
        "TLang.Double.min" => double_min(args),
        "TLang.Double.max" => double_max(args),
        "TLang.Double.sqrt" => double_sqrt(args),
        "TLang.Double.pow" => double_pow(args),
        "TLang.Double.isNaN" => double_is_nan(args),
        "TLang.Double.isInfinite" => double_is_infinite(args),
        "TLang.Double.pi" => double_pi(args),
        "TLang.Double.e" => double_e(args),
        "TLang.Double.infinity" => double_infinity(args),
        "TLang.Double.nan" => double_nan(args),
        "TLang.Double.add" => double_add(args),
        "TLang.Double.sub" => double_sub(args),
        "TLang.Double.mul" => double_mul(args),
        "TLang.Double.div" => double_div(args),

        _ => Err(RuntimeError(format!("Unknown float target: {target}"))),
    }
}

// ── TLang.Float implementations ───────────────────────────────────────────────

fn float_parse(args: &[Value]) -> Result<Value, RuntimeError> {
    let s = match args.first() {
        Some(Value::String(s)) => s,
        _ => {
            return Err(RuntimeError(
                "TLang.Float.parse requires a String argument".to_string(),
            ));
        }
    };
    match s.parse::<f32>() {
        Ok(f) => Ok(Value::Float(f as f64)),
        Err(_) => Err(RuntimeError(format!(
            "TLang.Float.parse: cannot parse {s:?} as Float"
        ))),
    }
}

fn float_to_string(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.toString argument",
    )?;
    Ok(Value::String(display_float(f)))
}

fn float_from_int(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.fromInt argument",
    )?;
    Ok(Value::Float(f as f32 as f64))
}

fn float_to_int(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.toInt argument",
    )?;
    Ok(Value::Int(f as i64))
}

fn float_floor(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.floor argument",
    )?;
    Ok(Value::Float((f as f32).floor() as f64))
}

fn float_ceil(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.ceil argument",
    )?;
    Ok(Value::Float((f as f32).ceil() as f64))
}

fn float_round(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.round argument",
    )?;
    Ok(Value::Float((f as f32).round() as f64))
}

fn float_abs(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.abs argument",
    )?;
    Ok(Value::Float((f as f32).abs() as f64))
}

fn float_min(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.min first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Float.min second argument",
    )?;
    Ok(Value::Float((a as f32).min(b as f32) as f64))
}

fn float_max(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.max first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Float.max second argument",
    )?;
    Ok(Value::Float((a as f32).max(b as f32) as f64))
}

fn float_sqrt(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.sqrt argument",
    )?;
    Ok(Value::Float((f as f32).sqrt() as f64))
}

fn float_pow(args: &[Value]) -> Result<Value, RuntimeError> {
    let base = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.pow base",
    )?;
    let exp = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Float.pow exponent",
    )?;
    Ok(Value::Float((base as f32).powf(exp as f32) as f64))
}

fn float_is_nan(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.isNaN argument",
    )?;
    Ok(Value::Bool(f.is_nan()))
}

fn float_is_infinite(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.isInfinite argument",
    )?;
    Ok(Value::Bool(f.is_infinite()))
}

fn float_pi(_args: &[Value]) -> Result<Value, RuntimeError> {
    Ok(Value::Float(std::f32::consts::PI as f64))
}

fn float_e(_args: &[Value]) -> Result<Value, RuntimeError> {
    Ok(Value::Float(std::f32::consts::E as f64))
}

fn float_infinity(_args: &[Value]) -> Result<Value, RuntimeError> {
    Ok(Value::Float(f32::INFINITY as f64))
}

fn float_nan(_args: &[Value]) -> Result<Value, RuntimeError> {
    Ok(Value::Float(f32::NAN as f64))
}

fn float_add(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.add first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Float.add second argument",
    )?;
    Ok(Value::Float(((a as f32) + (b as f32)) as f64))
}

fn float_sub(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.sub first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Float.sub second argument",
    )?;
    Ok(Value::Float(((a as f32) - (b as f32)) as f64))
}

fn float_mul(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.mul first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Float.mul second argument",
    )?;
    Ok(Value::Float(((a as f32) * (b as f32)) as f64))
}

fn float_div(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Float.div first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Float.div second argument",
    )?;
    Ok(Value::Float(((a as f32) / (b as f32)) as f64))
}

// ── TLang.Double implementations ──────────────────────────────────────────────

fn double_parse(args: &[Value]) -> Result<Value, RuntimeError> {
    let s = match args.first() {
        Some(Value::String(s)) => s,
        _ => {
            return Err(RuntimeError(
                "TLang.Double.parse requires a String argument".to_string(),
            ));
        }
    };
    match s.parse::<f64>() {
        Ok(f) => Ok(Value::Float(f)),
        Err(_) => Err(RuntimeError(format!(
            "TLang.Double.parse: cannot parse {s:?} as Double"
        ))),
    }
}

fn double_to_string(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.toString argument",
    )?;
    Ok(Value::String(display_float(f)))
}

fn double_from_int(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.fromInt argument",
    )?;
    Ok(Value::Float(f))
}

fn double_to_int(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.toInt argument",
    )?;
    Ok(Value::Int(f as i64))
}

fn double_floor(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.floor argument",
    )?;
    Ok(Value::Float(f.floor()))
}

fn double_ceil(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.ceil argument",
    )?;
    Ok(Value::Float(f.ceil()))
}

fn double_round(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.round argument",
    )?;
    Ok(Value::Float(f.round()))
}

fn double_abs(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.abs argument",
    )?;
    Ok(Value::Float(f.abs()))
}

fn double_min(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.min first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Double.min second argument",
    )?;
    Ok(Value::Float(a.min(b)))
}

fn double_max(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.max first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Double.max second argument",
    )?;
    Ok(Value::Float(a.max(b)))
}

fn double_sqrt(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.sqrt argument",
    )?;
    Ok(Value::Float(f.sqrt()))
}

fn double_pow(args: &[Value]) -> Result<Value, RuntimeError> {
    let base = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.pow base",
    )?;
    let exp = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Double.pow exponent",
    )?;
    Ok(Value::Float(base.powf(exp)))
}

fn double_is_nan(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.isNaN argument",
    )?;
    Ok(Value::Bool(f.is_nan()))
}

fn double_is_infinite(args: &[Value]) -> Result<Value, RuntimeError> {
    let f = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.isInfinite argument",
    )?;
    Ok(Value::Bool(f.is_infinite()))
}

fn double_pi(_args: &[Value]) -> Result<Value, RuntimeError> {
    Ok(Value::Float(std::f64::consts::PI))
}

fn double_e(_args: &[Value]) -> Result<Value, RuntimeError> {
    Ok(Value::Float(std::f64::consts::E))
}

fn double_infinity(_args: &[Value]) -> Result<Value, RuntimeError> {
    Ok(Value::Float(f64::INFINITY))
}

fn double_nan(_args: &[Value]) -> Result<Value, RuntimeError> {
    Ok(Value::Float(f64::NAN))
}

fn double_add(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.add first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Double.add second argument",
    )?;
    Ok(Value::Float(a + b))
}

fn double_sub(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.sub first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Double.sub second argument",
    )?;
    Ok(Value::Float(a - b))
}

fn double_mul(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.mul first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Double.mul second argument",
    )?;
    Ok(Value::Float(a * b))
}

fn double_div(args: &[Value]) -> Result<Value, RuntimeError> {
    let a = expect_float(
        args.first().unwrap_or(&Value::Bool(false)),
        "TLang.Double.div first argument",
    )?;
    let b = expect_float(
        args.get(1).unwrap_or(&Value::Bool(false)),
        "TLang.Double.div second argument",
    )?;
    Ok(Value::Float(a / b))
}
