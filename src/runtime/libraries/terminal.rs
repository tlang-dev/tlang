// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::io;

use super::super::{RuntimeError, Value, value_to_string};

pub(crate) fn call(
    output: &mut String,
    target: &str,
    args: &[Value],
) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Terminal.read" => read(args),
        "TLang.Terminal.print" | "TLang.Terminal.println" => write(output, target, args),
        _ => Err(RuntimeError(format!(
            "unknown terminal library function `{target}`"
        ))),
    }
}

fn write(output: &mut String, target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(format!(
            "{target} expects exactly one argument"
        )));
    }
    let text = value_to_string(&args[0]);
    if target.ends_with(".println") {
        output.push_str(&text);
        output.push('\n');
    } else {
        output.push_str(&text);
    }

    Ok(Value::Unit)
}

fn read(args: &[Value]) -> Result<Value, RuntimeError> {
    if !args.is_empty() {
        return Err(RuntimeError(
            "TLang.Terminal.read expects no arguments".to_string(),
        ));
    }

    let mut input = String::new();
    io::stdin()
        .read_line(&mut input)
        .map_err(|err| RuntimeError(format!("TLang.Terminal.read failed: {err}")))?;
    while input.ends_with('\n') || input.ends_with('\r') {
        input.pop();
    }
    Ok(Value::String(input))
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn print_writes_without_newline() {
        let mut output = String::new();
        let result = call(
            &mut output,
            "TLang.Terminal.print",
            &[Value::String("hello".to_string())],
        )
        .unwrap();
        assert_eq!(result, Value::Unit);
        assert_eq!(output, "hello");
    }

    #[test]
    fn println_writes_with_newline() {
        let mut output = String::new();
        let result = call(
            &mut output,
            "TLang.Terminal.println",
            &[Value::String("hello".to_string())],
        )
        .unwrap();
        assert_eq!(result, Value::Unit);
        assert_eq!(output, "hello\n");
    }

    #[test]
    fn read_rejects_arguments() {
        let mut output = String::new();
        let err = call(
            &mut output,
            "TLang.Terminal.read",
            &[Value::String("unexpected".to_string())],
        )
        .unwrap_err();
        assert_eq!(err.0, "TLang.Terminal.read expects no arguments");
    }
}
