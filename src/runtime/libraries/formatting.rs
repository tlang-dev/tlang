// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::collections::BTreeMap;

use super::{
    super::{LeafObject, RuntimeError, Value, value_to_string},
    expect_string,
    token::expect_token,
};

const KIND_FIELD: &str = "kind";
const RULES_KIND: &str = "FormattingRules";
const INDENT_TEXT_FIELD: &str = "indent_text";
const SPACE_BETWEEN_FIELD: &str = "space_between";
const NEWLINE_AFTER_FIELD: &str = "newline_after";
const INDENT_AFTER_FIELD: &str = "indent_after";
const OUTDENT_BEFORE_FIELD: &str = "outdent_before";

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Formatting.create" => create(args),
        "TLang.Formatting.with_indent_text" => with_indent_text(args),
        "TLang.Formatting.space_between" => space_between(args),
        "TLang.Formatting.newline_after" => newline_after(args),
        "TLang.Formatting.indent_after" => indent_after(args),
        "TLang.Formatting.outdent_before" => outdent_before(args),
        "TLang.Formatting.render" => render(args),
        "TLang.Formatting.render_list" => render_list(args),
        _ => Err(RuntimeError(format!(
            "unknown formatting library function `{target}`"
        ))),
    }
}

fn create(args: &[Value]) -> Result<Value, RuntimeError> {
    if !args.is_empty() {
        return Err(RuntimeError(
            "TLang.Formatting.create expects no arguments".to_string(),
        ));
    }
    Ok(default_rules())
}

fn with_indent_text(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Formatting.with_indent_text expects exactly two arguments".to_string(),
        ));
    }
    let mut fields = expect_rules_fields(&args[0])?;
    let indent_text = expect_string(&args[1], "TLang.Formatting.with_indent_text indent_text")?;
    fields.insert(INDENT_TEXT_FIELD.to_string(), Value::String(indent_text));
    Ok(Value::Leaf(LeafObject::new(fields)))
}

fn space_between(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 3 {
        return Err(RuntimeError(
            "TLang.Formatting.space_between expects exactly three arguments".to_string(),
        ));
    }
    add_pair_rule(args, SPACE_BETWEEN_FIELD, "TLang.Formatting.space_between")
}

fn newline_after(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Formatting.newline_after expects exactly two arguments".to_string(),
        ));
    }
    add_single_rule(args, NEWLINE_AFTER_FIELD, "TLang.Formatting.newline_after")
}

fn indent_after(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Formatting.indent_after expects exactly two arguments".to_string(),
        ));
    }
    add_single_rule(args, INDENT_AFTER_FIELD, "TLang.Formatting.indent_after")
}

fn outdent_before(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Formatting.outdent_before expects exactly two arguments".to_string(),
        ));
    }
    add_single_rule(args, OUTDENT_BEFORE_FIELD, "TLang.Formatting.outdent_before")
}

fn render(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() < 2 {
        return Err(RuntimeError(
            "TLang.Formatting.render expects at least two arguments".to_string(),
        ));
    }
    let fields = expect_rules_fields(&args[0])?;
    let indent_text = read_string_field(&fields, INDENT_TEXT_FIELD)?;
    let space_between = read_set_field(&fields, SPACE_BETWEEN_FIELD)?;
    let newline_after = read_set_field(&fields, NEWLINE_AFTER_FIELD)?;
    let indent_after = read_set_field(&fields, INDENT_AFTER_FIELD)?;
    let outdent_before = read_set_field(&fields, OUTDENT_BEFORE_FIELD)?;

    let tokens = args[1..]
        .iter()
        .map(parse_token)
        .collect::<Result<Vec<_>, _>>()?;

    let mut output = String::new();
    let mut indent_level = 0usize;
    let mut at_line_start = true;

    for (index, token) in tokens.iter().enumerate() {
        if outdent_before.contains_key(&token.name) {
            indent_level = indent_level.saturating_sub(1);
        }

        if index > 0 {
            let prev = &tokens[index - 1];
            if newline_after.contains_key(&prev.name) {
                output.push('\n');
                at_line_start = true;
            } else if space_between.contains_key(&pair_key(&prev.name, &token.name)) && !at_line_start
            {
                output.push(' ');
            }
        }

        if at_line_start {
            for _ in 0..indent_level {
                output.push_str(&indent_text);
            }
            at_line_start = false;
        }

        output.push_str(&token.value);
        if indent_after.contains_key(&token.name) {
            indent_level += 1;
        }
    }

    Ok(Value::String(output))
}

fn render_list(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Formatting.render_list expects exactly two arguments".to_string(),
        ));
    }
    let Value::List(tokens) = &args[1] else {
        return Err(RuntimeError(
            "TLang.Formatting.render_list expects a list as second argument".to_string(),
        ));
    };
    if tokens.is_empty() {
        return Ok(Value::String(String::new()));
    }
    let mut render_args = vec![args[0].clone()];
    render_args.extend(tokens.iter().cloned());
    render(&render_args)
}

fn add_pair_rule(args: &[Value], field: &str, context: &str) -> Result<Value, RuntimeError> {
    let mut fields = expect_rules_fields(&args[0])?;
    let left = expect_string(&args[1], &format!("{context} left token"))?;
    let right = expect_string(&args[2], &format!("{context} right token"))?;
    let mut set = read_set_field(&fields, field)?;
    set.insert(pair_key(&left, &right), Value::Bool(true));
    fields.insert(field.to_string(), Value::Leaf(LeafObject::new(set)));
    Ok(Value::Leaf(LeafObject::new(fields)))
}

fn add_single_rule(args: &[Value], field: &str, context: &str) -> Result<Value, RuntimeError> {
    let mut fields = expect_rules_fields(&args[0])?;
    let token_name = expect_string(&args[1], &format!("{context} token name"))?;
    let mut set = read_set_field(&fields, field)?;
    set.insert(token_name, Value::Bool(true));
    fields.insert(field.to_string(), Value::Leaf(LeafObject::new(set)));
    Ok(Value::Leaf(LeafObject::new(fields)))
}

fn default_rules() -> Value {
    let mut fields = BTreeMap::new();
    fields.insert(KIND_FIELD.to_string(), Value::String(RULES_KIND.to_string()));
    fields.insert(
        INDENT_TEXT_FIELD.to_string(),
        Value::String("    ".to_string()),
    );
    fields.insert(
        SPACE_BETWEEN_FIELD.to_string(),
        Value::Leaf(LeafObject::new(BTreeMap::new())),
    );
    fields.insert(
        NEWLINE_AFTER_FIELD.to_string(),
        Value::Leaf(LeafObject::new(BTreeMap::new())),
    );
    fields.insert(
        INDENT_AFTER_FIELD.to_string(),
        Value::Leaf(LeafObject::new(BTreeMap::new())),
    );
    fields.insert(
        OUTDENT_BEFORE_FIELD.to_string(),
        Value::Leaf(LeafObject::new(BTreeMap::new())),
    );
    Value::Leaf(LeafObject::new(fields))
}

fn expect_rules_fields(value: &Value) -> Result<BTreeMap<String, Value>, RuntimeError> {
    let Value::Leaf(leaf) = value else {
        return Err(RuntimeError(
            "TLang.Formatting expects a formatting rules leaf".to_string(),
        ));
    };
    match leaf.get(KIND_FIELD) {
        Some(Value::String(kind)) if kind == RULES_KIND => Ok(leaf.fields.clone()),
        _ => Err(RuntimeError(
            "TLang.Formatting expects a formatting rules leaf".to_string(),
        )),
    }
}

fn read_string_field(
    fields: &BTreeMap<String, Value>,
    field: &str,
) -> Result<String, RuntimeError> {
    match fields.get(field) {
        Some(Value::String(value)) => Ok(value.clone()),
        _ => Err(RuntimeError(format!(
            "TLang.Formatting internal error: `{field}` must be a string"
        ))),
    }
}

fn read_set_field(
    fields: &BTreeMap<String, Value>,
    field: &str,
) -> Result<BTreeMap<String, Value>, RuntimeError> {
    match fields.get(field) {
        Some(Value::Leaf(leaf)) => Ok(leaf.fields.clone()),
        _ => Err(RuntimeError(format!(
            "TLang.Formatting internal error: `{field}` must be a leaf"
        ))),
    }
}

fn pair_key(left: &str, right: &str) -> String {
    format!("{left}::{right}")
}

struct RenderToken {
    name: String,
    value: String,
}

fn parse_token(value: &Value) -> Result<RenderToken, RuntimeError> {
    if let Ok(leaf) = expect_token(value) {
        let Some(Value::String(name)) = leaf.get("name") else {
            return Err(RuntimeError(
                "TLang.Formatting.render token is missing a name".to_string(),
            ));
        };
        let Some(Value::String(token_value)) = leaf.get("value") else {
            return Err(RuntimeError(
                "TLang.Formatting.render token is missing a value".to_string(),
            ));
        };
        return Ok(RenderToken {
            name: name.clone(),
            value: token_value.clone(),
        });
    }

    let literal = value_to_string(value);
    Ok(RenderToken {
        name: literal.clone(),
        value: literal,
    })
}

#[cfg(test)]
mod tests {
    use super::super::super::Value;
    use super::super::token::token;
    use super::call;

    #[test]
    fn renders_with_spacing_and_newline_rules() {
        let rules = call("TLang.Formatting.create", &[]).expect("rules should build");
        let rules = call(
            "TLang.Formatting.space_between",
            &[
                rules,
                Value::String("public".to_string()),
                Value::String("class".to_string()),
            ],
        )
        .expect("space rule should build");
        let rules = call(
            "TLang.Formatting.space_between",
            &[
                rules,
                Value::String("class".to_string()),
                Value::String("A".to_string()),
            ],
        )
        .expect("space rule should build");
        let rules = call(
            "TLang.Formatting.space_between",
            &[
                rules,
                Value::String("A".to_string()),
                Value::String("open_brace".to_string()),
            ],
        )
        .expect("space rule should build");
        let rules = call(
            "TLang.Formatting.newline_after",
            &[rules, Value::String("open_brace".to_string())],
        )
        .expect("newline rule should build");
        let rules = call(
            "TLang.Formatting.indent_after",
            &[rules, Value::String("open_brace".to_string())],
        )
        .expect("indent rule should build");
        let rules = call(
            "TLang.Formatting.outdent_before",
            &[rules, Value::String("close_brace".to_string())],
        )
        .expect("outdent rule should build");

        let rendered = call(
            "TLang.Formatting.render",
            &[
                rules,
                token("public".to_string(), "public".to_string()),
                token("class".to_string(), "class".to_string()),
                token("A".to_string(), "A".to_string()),
                token("open_brace".to_string(), "{".to_string()),
                token("close_brace".to_string(), "}".to_string()),
            ],
        )
        .expect("render should succeed");

        assert_eq!(
            rendered,
            Value::String("public class A {\n}".to_string())
        );
    }

    #[test]
    fn renders_nested_indentation_and_outdentation() {
        let rules = call("TLang.Formatting.create", &[]).expect("rules should build");
        let rules = call(
            "TLang.Formatting.with_indent_text",
            &[rules, Value::String("  ".to_string())],
        )
        .expect("indent text should be configurable");
        let rules = call(
            "TLang.Formatting.space_between",
            &[
                rules,
                Value::String("class".to_string()),
                Value::String("name".to_string()),
            ],
        )
        .expect("space rule should build");
        let rules = call(
            "TLang.Formatting.space_between",
            &[
                rules,
                Value::String("name".to_string()),
                Value::String("open_outer".to_string()),
            ],
        )
        .expect("space rule should build");
        let rules = call(
            "TLang.Formatting.newline_after",
            &[rules, Value::String("open_outer".to_string())],
        )
        .expect("newline rule should build");
        let rules = call(
            "TLang.Formatting.indent_after",
            &[rules, Value::String("open_outer".to_string())],
        )
        .expect("indent rule should build");
        let rules = call(
            "TLang.Formatting.newline_after",
            &[rules, Value::String("open_inner".to_string())],
        )
        .expect("newline rule should build");
        let rules = call(
            "TLang.Formatting.indent_after",
            &[rules, Value::String("open_inner".to_string())],
        )
        .expect("indent rule should build");
        let rules = call(
            "TLang.Formatting.newline_after",
            &[rules, Value::String("body".to_string())],
        )
        .expect("newline rule should build");
        let rules = call(
            "TLang.Formatting.outdent_before",
            &[rules, Value::String("close_inner".to_string())],
        )
        .expect("outdent rule should build");
        let rules = call(
            "TLang.Formatting.newline_after",
            &[rules, Value::String("close_inner".to_string())],
        )
        .expect("newline rule should build");
        let rules = call(
            "TLang.Formatting.outdent_before",
            &[rules, Value::String("close_outer".to_string())],
        )
        .expect("outdent rule should build");

        let rendered = call(
            "TLang.Formatting.render",
            &[
                rules,
                token("class".to_string(), "class".to_string()),
                token("name".to_string(), "A".to_string()),
                token("open_outer".to_string(), "{".to_string()),
                token("open_inner".to_string(), "{".to_string()),
                token("body".to_string(), "x".to_string()),
                token("close_inner".to_string(), "}".to_string()),
                token("close_outer".to_string(), "}".to_string()),
            ],
        )
        .expect("render should succeed");

        assert_eq!(rendered, Value::String("class A {\n  {\n    x\n  }\n}".to_string()));
    }

    #[test]
    fn render_list_produces_same_output_as_render() {
        let rules = call("TLang.Formatting.create", &[]).expect("rules should build");
        let rules = call(
            "TLang.Formatting.space_between",
            &[
                rules,
                Value::String("class_kw".to_string()),
                Value::String("class_name".to_string()),
            ],
        )
        .expect("space rule should build");
        let rules = call(
            "TLang.Formatting.newline_after",
            &[rules, Value::String("open_brace".to_string())],
        )
        .expect("newline rule should build");
        let rules = call(
            "TLang.Formatting.indent_after",
            &[rules, Value::String("open_brace".to_string())],
        )
        .expect("indent rule should build");
        let rules = call(
            "TLang.Formatting.outdent_before",
            &[rules, Value::String("close_brace".to_string())],
        )
        .expect("outdent rule should build");
        let rules = call(
            "TLang.Formatting.space_between",
            &[
                rules,
                Value::String("class_name".to_string()),
                Value::String("open_brace".to_string()),
            ],
        )
        .expect("space rule should build");

        let tokens = vec![
            token("class_kw".to_string(), "class".to_string()),
            token("class_name".to_string(), "Foo".to_string()),
            token("open_brace".to_string(), "{".to_string()),
            token("close_brace".to_string(), "}".to_string()),
        ];

        // render via render_list
        let list_result = call(
            "TLang.Formatting.render_list",
            &[rules.clone(), Value::List(tokens.clone())],
        )
        .expect("render_list should succeed");

        // render via variadic render (baseline)
        let mut render_args = vec![rules];
        render_args.extend(tokens);
        let render_result =
            call("TLang.Formatting.render", &render_args).expect("render should succeed");

        assert_eq!(list_result, render_result);
        assert_eq!(list_result, Value::String("class Foo {\n}".to_string()));
    }

    #[test]
    fn render_list_accepts_empty_token_list() {
        let rules = call("TLang.Formatting.create", &[]).expect("rules should build");
        let result =
            call("TLang.Formatting.render_list", &[rules, Value::List(vec![])])
                .expect("render_list with empty list should succeed");
        assert_eq!(result, Value::String(String::new()));
    }

    #[test]
    fn render_list_rejects_non_list_second_arg() {
        let rules = call("TLang.Formatting.create", &[]).expect("rules should build");
        let err = call(
            "TLang.Formatting.render_list",
            &[rules, Value::String("not a list".to_string())],
        )
        .expect_err("render_list with non-list should fail");
        assert!(err.0.contains("list"), "error: {}", err.0);
    }
}
