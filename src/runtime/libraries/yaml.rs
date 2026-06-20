// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::collections::BTreeMap;

use super::super::{LeafObject, RuntimeError, Value};
use super::expect_string;

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Yaml.toLeaf" => to_leaf(args),
        "TLang.Yaml.fromLeaf" => from_leaf(args),
        _ => Err(RuntimeError(format!(
            "unknown yaml library function `{target}`"
        ))),
    }
}

fn to_leaf(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Yaml.toLeaf expects exactly one argument".to_string(),
        ));
    }
    let source = expect_string(&args[0], "TLang.Yaml.toLeaf input")?;
    let parsed: serde_yaml::Value = serde_yaml::from_str(&source)
        .map_err(|err| RuntimeError(format!("TLang.Yaml.toLeaf parse failed: {err}")))?;
    match yaml_to_runtime_value(&parsed)? {
        Value::Leaf(leaf) => Ok(Value::Leaf(leaf)),
        _ => Err(RuntimeError(
            "TLang.Yaml.toLeaf expects a top-level mapping".to_string(),
        )),
    }
}

fn from_leaf(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Yaml.fromLeaf expects exactly one argument".to_string(),
        ));
    }
    let yaml_value = runtime_to_yaml_value(&args[0])?;
    serde_yaml::to_string(&yaml_value)
        .map(Value::String)
        .map_err(|err| RuntimeError(format!("TLang.Yaml.fromLeaf failed: {err}")))
}

fn yaml_to_runtime_value(value: &serde_yaml::Value) -> Result<Value, RuntimeError> {
    match value {
        serde_yaml::Value::Null => Ok(Value::Unit),
        serde_yaml::Value::Bool(v) => Ok(Value::Bool(*v)),
        serde_yaml::Value::Number(v) => {
            if let Some(i) = v.as_i64() {
                Ok(Value::Int(i))
            } else if let Some(f) = v.as_f64() {
                Ok(Value::Float(f))
            } else {
                Err(RuntimeError(
                    "TLang.Yaml.toLeaf encountered unsupported number".to_string(),
                ))
            }
        }
        serde_yaml::Value::String(v) => Ok(Value::String(v.clone())),
        serde_yaml::Value::Sequence(items) => items
            .iter()
            .map(yaml_to_runtime_value)
            .collect::<Result<Vec<_>, _>>()
            .map(Value::List),
        serde_yaml::Value::Mapping(map) => {
            let mut fields = BTreeMap::new();
            for (key, value) in map {
                let serde_yaml::Value::String(name) = key else {
                    return Err(RuntimeError(
                        "TLang.Yaml.toLeaf only supports string mapping keys".to_string(),
                    ));
                };
                fields.insert(name.clone(), yaml_to_runtime_value(value)?);
            }
            Ok(Value::Leaf(LeafObject::new(fields)))
        }
        serde_yaml::Value::Tagged(tagged) => yaml_to_runtime_value(&tagged.value),
    }
}

fn runtime_to_yaml_value(value: &Value) -> Result<serde_yaml::Value, RuntimeError> {
    match value {
        Value::Int(v) => Ok(serde_yaml::Value::Number((*v).into())),
        Value::Float(v) => Ok(serde_yaml::to_value(*v)
            .map_err(|err| RuntimeError(format!("TLang.Yaml.fromLeaf failed: {err}")))?),
        Value::Bool(v) => Ok(serde_yaml::Value::Bool(*v)),
        Value::String(v) => Ok(serde_yaml::Value::String(v.clone())),
        Value::Leaf(leaf) => {
            let mut map = serde_yaml::Mapping::new();
            for (key, value) in &leaf.fields {
                map.insert(
                    serde_yaml::Value::String(key.clone()),
                    runtime_to_yaml_value(value)?,
                );
            }
            Ok(serde_yaml::Value::Mapping(map))
        }
        Value::List(items) => items
            .iter()
            .map(runtime_to_yaml_value)
            .collect::<Result<Vec<_>, _>>()
            .map(serde_yaml::Value::Sequence),
        Value::Map(map) => {
            let mut out = serde_yaml::Mapping::new();
            for (key, value) in map {
                out.insert(
                    serde_yaml::Value::String(key.clone()),
                    runtime_to_yaml_value(value)?,
                );
            }
            Ok(serde_yaml::Value::Mapping(out))
        }
        Value::Unit => Ok(serde_yaml::Value::Null),
        Value::SetInstance(_) | Value::BoundAttr(_) | Value::StringBuilder(_) | Value::Lambda(_) | Value::PdfDoc(_) => Err(RuntimeError(
            "TLang.Yaml.fromLeaf only supports serializing data values".to_string(),
        )),
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn to_leaf_parses_mapping() {
        let result = call(
            "TLang.Yaml.toLeaf",
            &[Value::String(
                "name: T\ncount: 2\nitems:\n  - true\n".to_string(),
            )],
        )
        .unwrap();

        let Value::Leaf(root) = result else {
            panic!("expected leaf");
        };
        assert_eq!(root.get("name"), Some(&Value::String("T".to_string())));
        assert_eq!(root.get("count"), Some(&Value::Int(2)));
        assert_eq!(
            root.get("items"),
            Some(&Value::List(vec![Value::Bool(true)]))
        );
    }

    #[test]
    fn to_leaf_rejects_non_mapping_root() {
        let err = call(
            "TLang.Yaml.toLeaf",
            &[Value::String("- 1\n- 2\n".to_string())],
        )
        .unwrap_err();
        assert_eq!(err.0, "TLang.Yaml.toLeaf expects a top-level mapping");
    }

    #[test]
    fn from_leaf_serializes_leaf() {
        let mut fields = BTreeMap::new();
        fields.insert("name".to_string(), Value::String("alpha".to_string()));
        fields.insert("count".to_string(), Value::Int(2));
        let value = Value::Leaf(LeafObject::new(fields));

        let yaml = call("TLang.Yaml.fromLeaf", &[value]).unwrap();
        let Value::String(text) = yaml else {
            panic!("expected string");
        };

        let parsed: serde_yaml::Value = serde_yaml::from_str(&text).unwrap();
        let serde_yaml::Value::Mapping(map) = parsed else {
            panic!("expected mapping");
        };
        assert_eq!(
            map.get(serde_yaml::Value::String("name".to_string())),
            Some(&serde_yaml::Value::String("alpha".to_string()))
        );
    }
}
