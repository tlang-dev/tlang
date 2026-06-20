// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::collections::BTreeMap;

use super::{
    super::{LeafObject, RuntimeError, Value},
    expect_string,
};

const KIND_FIELD: &str = "kind";
const TOKEN_KIND: &str = "Token";
const TOKEN_TYPE_FIELD: &str = "token_type";
const TOKEN_NAME_FIELD: &str = "name";
const TOKEN_VALUE_FIELD: &str = "value";
const KEYWORD_TYPE: &str = "keyword";

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Token.keyword" => keyword(args),
        "TLang.Token.name" => name(args),
        "TLang.Token.value" => value(args),
        "TLang.Token.list" => list(args),
        "TLang.Token.push" => push(args),
        "TLang.Token.concat" => concat(args),
        _ => Err(RuntimeError(format!(
            "unknown token library function `{target}`"
        ))),
    }
}

fn keyword(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Token.keyword expects exactly two arguments".to_string(),
        ));
    }
    let name = expect_string(&args[0], "TLang.Token.keyword name")?;
    let value = expect_string(&args[1], "TLang.Token.keyword value")?;
    Ok(token(name, value))
}

fn name(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Token.name expects exactly one argument".to_string(),
        ));
    }
    let leaf = expect_token(&args[0])?;
    let Some(Value::String(name)) = leaf.get(TOKEN_NAME_FIELD) else {
        return Err(RuntimeError(
            "TLang.Token.name expects a token with a name".to_string(),
        ));
    };
    Ok(Value::String(name.clone()))
}

fn value(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Token.value expects exactly one argument".to_string(),
        ));
    }
    let leaf = expect_token(&args[0])?;
    let Some(Value::String(value)) = leaf.get(TOKEN_VALUE_FIELD) else {
        return Err(RuntimeError(
            "TLang.Token.value expects a token with a value".to_string(),
        ));
    };
    Ok(Value::String(value.clone()))
}

fn list(args: &[Value]) -> Result<Value, RuntimeError> {
    if !args.is_empty() {
        return Err(RuntimeError(
            "TLang.Token.list expects no arguments".to_string(),
        ));
    }
    Ok(Value::List(vec![]))
}

fn push(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Token.push expects exactly two arguments".to_string(),
        ));
    }
    let Value::List(items) = &args[0] else {
        return Err(RuntimeError(
            "TLang.Token.push expects a list as first argument".to_string(),
        ));
    };
    let mut new_items = items.clone();
    new_items.push(args[1].clone());
    Ok(Value::List(new_items))
}

fn concat(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Token.concat expects exactly two arguments".to_string(),
        ));
    }
    let Value::List(items1) = &args[0] else {
        return Err(RuntimeError(
            "TLang.Token.concat expects lists as arguments".to_string(),
        ));
    };
    let Value::List(items2) = &args[1] else {
        return Err(RuntimeError(
            "TLang.Token.concat expects lists as arguments".to_string(),
        ));
    };
    let mut new_items = items1.clone();
    new_items.extend(items2.iter().cloned());
    Ok(Value::List(new_items))
}

pub(crate) fn token(name: String, value: String) -> Value {
    let mut fields = BTreeMap::new();
    fields.insert(KIND_FIELD.to_string(), Value::String(TOKEN_KIND.to_string()));
    fields.insert(
        TOKEN_TYPE_FIELD.to_string(),
        Value::String(KEYWORD_TYPE.to_string()),
    );
    fields.insert(TOKEN_NAME_FIELD.to_string(), Value::String(name));
    fields.insert(TOKEN_VALUE_FIELD.to_string(), Value::String(value));
    Value::Leaf(LeafObject::new(fields))
}

pub(crate) fn expect_token(value: &Value) -> Result<&LeafObject, RuntimeError> {
    let Value::Leaf(leaf) = value else {
        return Err(RuntimeError("TLang.Token expects a token leaf".to_string()));
    };
    match leaf.get(KIND_FIELD) {
        Some(Value::String(kind)) if kind == TOKEN_KIND => Ok(leaf),
        _ => Err(RuntimeError("TLang.Token expects a token leaf".to_string())),
    }
}

#[cfg(test)]
mod tests {
    use super::super::super::Value;
    use super::call;

    #[test]
    fn keyword_builds_named_token() {
        let token = call(
            "TLang.Token.keyword",
            &[
                Value::String("class".to_string()),
                Value::String("class".to_string()),
            ],
        )
        .expect("keyword token should build");

        let name = call("TLang.Token.name", std::slice::from_ref(&token))
            .expect("token name should resolve");
        let value = call("TLang.Token.value", &[token]).expect("token value should resolve");

        assert_eq!(name, Value::String("class".to_string()));
        assert_eq!(value, Value::String("class".to_string()));
    }

    #[test]
    fn list_creates_empty_list() {
        let result = call("TLang.Token.list", &[]).expect("list should build");
        assert_eq!(result, Value::List(vec![]));
    }

    #[test]
    fn push_appends_token_to_list() {
        let list = call("TLang.Token.list", &[]).expect("list should build");
        let token = call(
            "TLang.Token.keyword",
            &[
                Value::String("class_kw".to_string()),
                Value::String("class".to_string()),
            ],
        )
        .expect("token should build");
        let result = call("TLang.Token.push", &[list, token.clone()])
            .expect("push should succeed");
        assert_eq!(result, Value::List(vec![token]));
    }

    #[test]
    fn push_rejects_non_list_first_arg() {
        let err = call(
            "TLang.Token.push",
            &[Value::String("not a list".to_string()), Value::String("x".to_string())],
        )
        .expect_err("push with non-list should fail");
        assert!(err.0.contains("list"), "error: {}", err.0);
    }

    #[test]
    fn concat_merges_two_lists() {
        let a = Value::List(vec![Value::String("x".to_string())]);
        let b = Value::List(vec![Value::String("y".to_string())]);
        let result = call("TLang.Token.concat", &[a, b]).expect("concat should succeed");
        assert_eq!(
            result,
            Value::List(vec![
                Value::String("x".to_string()),
                Value::String("y".to_string()),
            ])
        );
    }

    #[test]
    fn concat_with_empty_list_is_identity() {
        let empty = call("TLang.Token.list", &[]).expect("list should build");
        let list = Value::List(vec![Value::String("a".to_string())]);
        let result = call("TLang.Token.concat", &[list.clone(), empty])
            .expect("concat should succeed");
        assert_eq!(result, list);
    }

    #[test]
    fn concat_rejects_non_list_args() {
        let list = Value::List(vec![]);
        let err = call(
            "TLang.Token.concat",
            &[list, Value::String("not a list".to_string())],
        )
        .expect_err("concat with non-list should fail");
        assert!(err.0.contains("list"), "error: {}", err.0);
    }
}
