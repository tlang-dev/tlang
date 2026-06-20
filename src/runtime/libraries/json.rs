// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::collections::BTreeMap;

use super::super::{LeafObject, RuntimeError, Value};
use super::expect_string;

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Json.toLeaf" => to_leaf(args),
        "TLang.Json.fromLeaf" => from_leaf(args),
        _ => Err(RuntimeError(format!(
            "unknown json library function `{target}`"
        ))),
    }
}

fn to_leaf(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Json.toLeaf expects exactly one argument".to_string(),
        ));
    }
    let source = expect_string(&args[0], "TLang.Json.toLeaf input")?;
    let parsed: serde_json::Value = serde_json::from_str(&source)
        .map_err(|err| RuntimeError(format!("TLang.Json.toLeaf parse failed: {err}")))?;
    match json_to_runtime_value(&parsed)? {
        Value::Leaf(leaf) => Ok(Value::Leaf(leaf)),
        _ => Err(RuntimeError(
            "TLang.Json.toLeaf expects a top-level JSON object".to_string(),
        )),
    }
}

fn from_leaf(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Json.fromLeaf expects exactly one argument".to_string(),
        ));
    }
    let json_value = runtime_to_json_value(&args[0])?;
    serde_json::to_string(&json_value)
        .map(Value::String)
        .map_err(|err| RuntimeError(format!("TLang.Json.fromLeaf failed: {err}")))
}

fn json_to_runtime_value(value: &serde_json::Value) -> Result<Value, RuntimeError> {
    match value {
        serde_json::Value::Null => Ok(Value::Unit),
        serde_json::Value::Bool(v) => Ok(Value::Bool(*v)),
        serde_json::Value::Number(v) => {
            if let Some(i) = v.as_i64() {
                Ok(Value::Int(i))
            } else if let Some(u) = v.as_u64() {
                if u <= i64::MAX as u64 {
                    Ok(Value::Int(u as i64))
                } else {
                    Err(RuntimeError(format!(
                        "TLang.Json.toLeaf cannot represent u64 value `{u}` as Int"
                    )))
                }
            } else if let Some(f) = v.as_f64() {
                Ok(Value::Float(f))
            } else {
                Err(RuntimeError(
                    "TLang.Json.toLeaf encountered unsupported number".to_string(),
                ))
            }
        }
        serde_json::Value::String(v) => Ok(Value::String(v.clone())),
        serde_json::Value::Array(items) => items
            .iter()
            .map(json_to_runtime_value)
            .collect::<Result<Vec<_>, _>>()
            .map(Value::List),
        serde_json::Value::Object(map) => {
            let fields = map
                .iter()
                .map(|(key, value)| json_to_runtime_value(value).map(|v| (key.clone(), v)))
                .collect::<Result<BTreeMap<_, _>, _>>()?;
            Ok(Value::Leaf(LeafObject::new(fields)))
        }
    }
}

fn runtime_to_json_value(value: &Value) -> Result<serde_json::Value, RuntimeError> {
    match value {
        Value::Int(v) => Ok(serde_json::Value::Number((*v).into())),
        Value::Float(v) => serde_json::Number::from_f64(*v)
            .map(serde_json::Value::Number)
            .ok_or_else(|| {
                RuntimeError(
                    "TLang.Json.fromLeaf cannot encode NaN or infinite Float values".to_string(),
                )
            }),
        Value::Bool(v) => Ok(serde_json::Value::Bool(*v)),
        Value::String(v) => Ok(serde_json::Value::String(v.clone())),
        Value::Leaf(leaf) => {
            let object = leaf
                .fields
                .iter()
                .map(|(key, value)| runtime_to_json_value(value).map(|v| (key.clone(), v)))
                .collect::<Result<serde_json::Map<_, _>, _>>()?;
            Ok(serde_json::Value::Object(object))
        }
        Value::List(items) => items
            .iter()
            .map(runtime_to_json_value)
            .collect::<Result<Vec<_>, _>>()
            .map(serde_json::Value::Array),
        Value::Map(map) => {
            let object = map
                .iter()
                .map(|(key, value)| runtime_to_json_value(value).map(|v| (key.clone(), v)))
                .collect::<Result<serde_json::Map<_, _>, _>>()?;
            Ok(serde_json::Value::Object(object))
        }
        Value::Unit => Ok(serde_json::Value::Null),
        Value::SetInstance(_) | Value::BoundAttr(_) | Value::StringBuilder(_) | Value::Lambda(_) | Value::PdfDoc(_) => Err(RuntimeError(
            "TLang.Json.fromLeaf only supports serializing data values".to_string(),
        )),
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn to_leaf_parses_object() {
        let result = call(
            "TLang.Json.toLeaf",
            &[Value::String(
                r#"{"name":"T","items":[1,true]}"#.to_string(),
            )],
        )
        .unwrap();

        let Value::Leaf(root) = result else {
            panic!("expected leaf");
        };
        assert_eq!(root.get("name"), Some(&Value::String("T".to_string())));
        assert_eq!(
            root.get("items"),
            Some(&Value::List(vec![Value::Int(1), Value::Bool(true)]))
        );
    }

    #[test]
    fn to_leaf_rejects_non_object_root() {
        let err = call("TLang.Json.toLeaf", &[Value::String("[1,2]".to_string())]).unwrap_err();
        assert_eq!(err.0, "TLang.Json.toLeaf expects a top-level JSON object");
    }

    #[test]
    fn from_leaf_serializes_leaf() {
        let mut fields = BTreeMap::new();
        fields.insert("name".to_string(), Value::String("alpha".to_string()));
        fields.insert("count".to_string(), Value::Int(2));
        let value = Value::Leaf(LeafObject::new(fields));

        let json = call("TLang.Json.fromLeaf", &[value]).unwrap();
        let Value::String(text) = json else {
            panic!("expected string");
        };

        let parsed: serde_json::Value = serde_json::from_str(&text).unwrap();
        assert_eq!(
            parsed["name"],
            serde_json::Value::String("alpha".to_string())
        );
        assert_eq!(parsed["count"], serde_json::Value::Number(2.into()));
    }
}
