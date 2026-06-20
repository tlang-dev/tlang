// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use super::super::{RuntimeError, Value};

fn expect_int(v: &Value, ctx: &str) -> Result<i64, RuntimeError> {
    match v {
        Value::Int(n) => Ok(*n),
        _ => Err(RuntimeError(format!("{ctx} must be an Int"))),
    }
}

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Math.abs" => {
            let n = expect_int(&args[0], "TLang.Math.abs argument")?;
            Ok(Value::Int(n.abs()))
        }
        "TLang.Math.min" => {
            let a = expect_int(&args[0], "TLang.Math.min first argument")?;
            let b = expect_int(&args[1], "TLang.Math.min second argument")?;
            Ok(Value::Int(a.min(b)))
        }
        "TLang.Math.max" => {
            let a = expect_int(&args[0], "TLang.Math.max first argument")?;
            let b = expect_int(&args[1], "TLang.Math.max second argument")?;
            Ok(Value::Int(a.max(b)))
        }
        "TLang.Math.clamp" => {
            let n = expect_int(&args[0], "TLang.Math.clamp n")?;
            let lo = expect_int(&args[1], "TLang.Math.clamp lo")?;
            let hi = expect_int(&args[2], "TLang.Math.clamp hi")?;
            Ok(Value::Int(n.clamp(lo, hi)))
        }
        "TLang.Math.pow" => {
            let base = expect_int(&args[0], "TLang.Math.pow base")?;
            let exp = expect_int(&args[1], "TLang.Math.pow exp")?;
            if exp < 0 {
                return Err(RuntimeError(
                    "TLang.Math.pow exp must be >= 0".to_string(),
                ));
            }
            let exp_u32 = exp as u32;
            match base.checked_pow(exp_u32) {
                Some(result) => Ok(Value::Int(result)),
                None => Err(RuntimeError(
                    "TLang.Math.pow: integer overflow".to_string(),
                )),
            }
        }
        "TLang.Math.sqrt" => {
            let n = expect_int(&args[0], "TLang.Math.sqrt argument")?;
            if n < 0 {
                return Err(RuntimeError(
                    "TLang.Math.sqrt argument must be >= 0".to_string(),
                ));
            }
            Ok(Value::Int((n as f64).sqrt() as i64))
        }
        "TLang.Math.isEven" => {
            let n = expect_int(&args[0], "TLang.Math.isEven argument")?;
            Ok(Value::Bool(n % 2 == 0))
        }
        "TLang.Math.isOdd" => {
            let n = expect_int(&args[0], "TLang.Math.isOdd argument")?;
            Ok(Value::Bool(n % 2 != 0))
        }
        "TLang.Math.sign" => {
            let n = expect_int(&args[0], "TLang.Math.sign argument")?;
            Ok(Value::Int(n.signum()))
        }
        "TLang.Math.gcd" => {
            let a = expect_int(&args[0], "TLang.Math.gcd first argument")?;
            let b = expect_int(&args[1], "TLang.Math.gcd second argument")?;
            Ok(Value::Int(gcd(a.abs(), b.abs())))
        }
        "TLang.Math.lcm" => {
            let a = expect_int(&args[0], "TLang.Math.lcm first argument")?;
            let b = expect_int(&args[1], "TLang.Math.lcm second argument")?;
            if a == 0 || b == 0 {
                return Ok(Value::Int(0));
            }
            let g = gcd(a.abs(), b.abs());
            Ok(Value::Int((a * b).abs() / g))
        }
        _ => Err(RuntimeError(format!("Unknown Math target: {target}"))),
    }
}

fn gcd(mut a: i64, mut b: i64) -> i64 {
    while b != 0 {
        let t = b;
        b = a % b;
        a = t;
    }
    a
}
