// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::collections::HashMap;

use crate::ast::DomainUse;

use super::{Runtime, RuntimeError, Value};

pub(crate) mod assert;
pub(crate) mod call_lib;
pub(crate) mod file;
pub(crate) mod float_lib;
pub(crate) mod formatting;
pub(crate) mod generator;
pub(crate) mod int_lib;
pub(crate) mod json;

pub(crate) mod leaf;
pub(crate) mod list_lib;
pub(crate) mod long_lib;
pub(crate) mod map_lib;
pub(crate) mod math_lib;
pub(crate) mod naming;
pub(crate) mod parser_lib;
pub(crate) mod pdf;
pub(crate) mod prompt;
pub(crate) mod shell;
pub(crate) mod string_builder;
pub(crate) mod string_lib;
pub(crate) mod terminal;
pub(crate) mod tmpl_cmd_leaf;
pub(crate) mod tmpl_data_leaf;
pub(crate) mod tmpl_leaf;
pub(crate) mod token;
pub(crate) mod yaml;

#[derive(Debug, Clone, Default)]
pub(crate) struct ImportResolver {
    aliases: HashMap<String, String>,
}

impl ImportResolver {
    pub(super) fn aliases(&self) -> &HashMap<String, String> {
        &self.aliases
    }

    pub(super) fn from_aliases(aliases: HashMap<String, String>) -> Self {
        Self { aliases }
    }

    pub(crate) fn from_uses(uses: &[DomainUse]) -> Self {
        let aliases = uses
            .iter()
            .map(|domain_use| {
                let alias = domain_use
                    .alias
                    .clone()
                    .unwrap_or_else(|| domain_use.path.last().cloned().unwrap_or_default());
                (alias, domain_use.path.join("."))
            })
            .collect();
        Self { aliases }
    }

    pub(crate) fn resolve_call_target(&self, target: &str) -> String {
        let Some((prefix, suffix)) = target.split_once('.') else {
            return target.to_string();
        };
        match self.aliases.get(prefix) {
            // Single-segment unaliased import: `use Template` stores
            // aliases["Template"] = "Template".  Functions from that file are
            // merged flat into the program with their bare names, so
            // `Template.leader` must resolve to just `leader`.
            Some(path) if path == prefix => suffix.to_string(),
            Some(path) => format!("{path}.{suffix}"),
            None => target.to_string(),
        }
    }
}

pub(crate) fn call_builtin(
    runtime: &mut Runtime<'_>,
    target: &str,
    args: &[Value],
) -> Result<Option<Value>, RuntimeError> {
    // `TLang.Str.*` is an alias for `TLang.String.*`.
    let target = if let Some(rest) = target.strip_prefix("TLang.Str.") {
        std::borrow::Cow::Owned(format!("TLang.String.{rest}"))
    } else {
        std::borrow::Cow::Borrowed(target)
    };
    let target = target.as_ref();

    // Higher-order functions (need runtime to call lambdas) — checked first.
    if let Some(v) = list_lib::call_hof(runtime, target, args)? {
        return Ok(Some(v));
    }
    if let Some(v) = map_lib::call_hof(runtime, target, args)? {
        return Ok(Some(v));
    }
    // TLang.Call.invoke needs runtime access for dynamic dispatch.
    if target == "TLang.Call.invoke" {
        return call_lib::call(runtime, target, args).map(Some);
    }
    match target {
        "TLang.Naming.toCamelCase"
        | "TLang.Naming.toPascalCase"
        | "TLang.Naming.toSnakeCase"
        | "TLang.Naming.toScreamingSnake"
        | "TLang.Naming.toKebabCase"
        | "TLang.Naming.toDotCase"
        | "TLang.Naming.toTitleCase"
        | "TLang.Naming.capitalize"
        | "TLang.Naming.decapitalize"
        | "TLang.Naming.pluralize"
        | "TLang.Naming.singularize"
        | "TLang.Naming.words" => Ok(Some(naming::call(target, args)?)),
        "TLang.Math.abs" | "TLang.Math.min" | "TLang.Math.max" | "TLang.Math.clamp"
        | "TLang.Math.pow" | "TLang.Math.sqrt" | "TLang.Math.isEven" | "TLang.Math.isOdd"
        | "TLang.Math.sign" | "TLang.Math.gcd" | "TLang.Math.lcm" => {
            Ok(Some(math_lib::call(target, args)?))
        }
        "TLang.Int.toString"
        | "TLang.Int.parse"
        | "TLang.Int.toHex"
        | "TLang.Int.toBinary"
        | "TLang.Int.toOctal"
        | "TLang.Int.fromHex"
        | "TLang.Int.fromBinary"
        | "TLang.Int.fromOctal"
        | "TLang.Int.range"
        | "TLang.Int.rangeTo"
        | "TLang.Int.minValue"
        | "TLang.Int.maxValue"
        | "TLang.Int.abs"
        | "TLang.Int.clamp"
        | "TLang.Int.toFloat"
        | "TLang.Int.toDouble" => Ok(Some(int_lib::call(target, args)?)),
        "TLang.Long.toString"
        | "TLang.Long.parse"
        | "TLang.Long.toHex"
        | "TLang.Long.toBinary"
        | "TLang.Long.fromHex"
        | "TLang.Long.fromBinary"
        | "TLang.Long.range"
        | "TLang.Long.rangeTo"
        | "TLang.Long.minValue"
        | "TLang.Long.maxValue"
        | "TLang.Long.abs"
        | "TLang.Long.clamp"
        | "TLang.Long.toFloat"
        | "TLang.Long.toDouble" => Ok(Some(long_lib::call(target, args)?)),
        "TLang.Float.parse"
        | "TLang.Float.toString"
        | "TLang.Float.fromInt"
        | "TLang.Float.toInt"
        | "TLang.Float.floor"
        | "TLang.Float.ceil"
        | "TLang.Float.round"
        | "TLang.Float.abs"
        | "TLang.Float.min"
        | "TLang.Float.max"
        | "TLang.Float.sqrt"
        | "TLang.Float.pow"
        | "TLang.Float.isNaN"
        | "TLang.Float.isInfinite"
        | "TLang.Float.pi"
        | "TLang.Float.e"
        | "TLang.Float.infinity"
        | "TLang.Float.nan"
        | "TLang.Float.add"
        | "TLang.Float.sub"
        | "TLang.Float.mul"
        | "TLang.Float.div"
        | "TLang.Double.parse"
        | "TLang.Double.toString"
        | "TLang.Double.fromInt"
        | "TLang.Double.toInt"
        | "TLang.Double.floor"
        | "TLang.Double.ceil"
        | "TLang.Double.round"
        | "TLang.Double.abs"
        | "TLang.Double.min"
        | "TLang.Double.max"
        | "TLang.Double.sqrt"
        | "TLang.Double.pow"
        | "TLang.Double.isNaN"
        | "TLang.Double.isInfinite"
        | "TLang.Double.pi"
        | "TLang.Double.e"
        | "TLang.Double.infinity"
        | "TLang.Double.nan"
        | "TLang.Double.add"
        | "TLang.Double.sub"
        | "TLang.Double.mul"
        | "TLang.Double.div" => Ok(Some(float_lib::call(target, args)?)),
        "TLang.Terminal.print" | "TLang.Terminal.println" | "TLang.Terminal.read" => {
            Ok(Some(terminal::call(&mut runtime.output, target, args)?))
        }
        "TLang.Prompt.ask"
        | "TLang.Prompt.askWithDefault"
        | "TLang.Prompt.confirm"
        | "TLang.Prompt.select"
        | "TLang.Prompt.password" => {
            Ok(Some(prompt::call(&mut runtime.output, target, args)?))
        }
        "TLang.File.read"
        | "TLang.File.write"
        | "TLang.File.exists"
        | "TLang.File.searchReplace"
        | "TLang.File.append"
        | "TLang.File.appendAfter"
        | "TLang.File.prependBefore"
        | "TLang.File.createDir"
        | "TLang.File.deleteFile"
        | "TLang.File.deleteDir" => Ok(Some(file::call(target, args)?)),
        "TLang.Shell.run"
        | "TLang.Shell.runIn"
        | "TLang.Shell.capture"
        | "TLang.Shell.captureIn"
        | "TLang.Shell.stream"
        | "TLang.Shell.streamIn"
        | "TLang.Shell.spawnIn"
        | "TLang.Shell.env"
        | "TLang.Shell.which" => Ok(Some(shell::call(target, args)?)),
        "TLang.Leaf.model" | "TLang.Leaf.get" | "TLang.Leaf.keys" | "TLang.Leaf.has" => {
            Ok(Some(leaf::call(&runtime.model, target, args)?))
        }
        "TLang.Json.toLeaf" | "TLang.Json.fromLeaf" => Ok(Some(json::call(target, args)?)),
        "TLang.Yaml.toLeaf" | "TLang.Yaml.fromLeaf" => Ok(Some(yaml::call(target, args)?)),
        "TLang.Generator.generate" => Ok(Some(generator::call(runtime, target, args)?)),
        t if t.starts_with("TLang.Pdf.") => Ok(Some(pdf::call(target, args)?)),

        "TLang.Parser.rule"
        | "TLang.Parser.tokenize"
        | "TLang.Parser.token_type"
        | "TLang.Parser.token_value"
        | "TLang.Parser.token_line"
        | "TLang.Parser.token_col"
        | "TLang.Parser.filter_type"
        | "TLang.Parser.skip_types" => Ok(Some(parser_lib::call(target, args)?)),

        "TLang.Formatting.create"
        | "TLang.Formatting.with_indent_text"
        | "TLang.Formatting.space_between"
        | "TLang.Formatting.newline_after"
        | "TLang.Formatting.indent_after"
        | "TLang.Formatting.outdent_before"
        | "TLang.Formatting.render"
        | "TLang.Formatting.render_list" => Ok(Some(formatting::call(target, args)?)),
        "TLang.Token.keyword"
        | "TLang.Token.name"
        | "TLang.Token.value"
        | "TLang.Token.list"
        | "TLang.Token.push"
        | "TLang.Token.concat" => Ok(Some(token::call(target, args)?)),
        "TLang.StringBuilder.create"
        | "TLang.StringBuilder.new"
        | "TLang.StringBuilder.append"
        | "TLang.StringBuilder.build" => Ok(Some(string_builder::call(target, args)?)),
        "TLang.List.create"
        | "TLang.List.of"
        | "TLang.List.size"
        | "TLang.List.isEmpty"
        | "TLang.List.get"
        | "TLang.List.first"
        | "TLang.List.last"
        | "TLang.List.push"
        | "TLang.List.prepend"
        | "TLang.List.set"
        | "TLang.List.remove"
        | "TLang.List.tail"
        | "TLang.List.init"
        | "TLang.List.slice"
        | "TLang.List.concat"
        | "TLang.List.reverse"
        | "TLang.List.contains"
        | "TLang.List.indexOf"
        | "TLang.List.join"
        | "TLang.List.flatten"
        | "TLang.List.distinct"
        | "TLang.List.sort"
        | "TLang.List.take"
        | "TLang.List.drop"
        | "TLang.List.zip" => Ok(Some(list_lib::call(target, args)?)),
        "TLang.Map.create"
        | "TLang.Map.of"
        | "TLang.Map.size"
        | "TLang.Map.isEmpty"
        | "TLang.Map.has"
        | "TLang.Map.get"
        | "TLang.Map.getOrDefault"
        | "TLang.Map.set"
        | "TLang.Map.remove"
        | "TLang.Map.keys"
        | "TLang.Map.values"
        | "TLang.Map.entries"
        | "TLang.Map.merge"
        | "TLang.Map.fromLists"
        | "TLang.Map.toList" => Ok(Some(map_lib::call(target, args)?)),
        "TLang.String.length"
        | "TLang.String.isEmpty"
        | "TLang.String.toUpperCase"
        | "TLang.String.toLowerCase"
        | "TLang.String.trim"
        | "TLang.String.trimStart"
        | "TLang.String.trimEnd"
        | "TLang.String.contains"
        | "TLang.String.startsWith"
        | "TLang.String.endsWith"
        | "TLang.String.equals"
        | "TLang.String.equalsIgnoreCase"
        | "TLang.String.compare"
        | "TLang.String.indexOf"
        | "TLang.String.lastIndexOf"
        | "TLang.String.substring"
        | "TLang.String.slice"
        | "TLang.String.split"
        | "TLang.String.replace"
        | "TLang.String.replaceAll"
        | "TLang.String.concat"
        | "TLang.String.repeat"
        | "TLang.String.charAt"
        | "TLang.String.charCodeAt"
        | "TLang.String.fromCharCode"
        | "TLang.String.lines"
        | "TLang.String.words" => Ok(Some(string_lib::call(target, args)?)),
        "TLang.Assert.isTrue"
        | "TLang.Assert.isFalse"
        | "TLang.Assert.equals"
        | "TLang.Assert.notEquals"
        | "TLang.Assert.contains"
        | "TLang.Assert.notContains"
        | "TLang.Assert.startsWith"
        | "TLang.Assert.endsWith"
        | "TLang.Assert.isEmpty"
        | "TLang.Assert.notEmpty"
        | "TLang.Assert.isNull"
        | "TLang.Assert.notNull"
        | "TLang.Assert.fail" => Ok(Some(assert::call(target, args)?)),
        _ => Ok(None),
    }
}

pub(crate) fn expect_string(value: &Value, context: &str) -> Result<String, RuntimeError> {
    match value {
        Value::String(value) => Ok(value.clone()),
        _ => Err(RuntimeError(format!("{context} must be a string"))),
    }
}
