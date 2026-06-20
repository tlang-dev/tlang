// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Runtime leaf builder for command templates.
//!
//! Converts a [`TmplCmdBlockTree`] (parsed by `tmpl_cmd_tree`) into a
//! [`Value::Leaf`] that generators can traverse to emit SQL, bash, HTTP, or
//! any other command-oriented output.
//!
//! ## Output shape
//!
//! ```text
//! {
//!   kind:  "cmd",
//!   lang:  "sql",          // first declared language
//!   langs: ["sql"],
//!   name:  "findByEmail",  // template name
//!   params: { email: "alice@example.com" },  // resolved param values
//!   content: <content leaf>,
//! }
//! ```
//!
//! For a **bare** cmd body (`{ sql }`):
//! ```text
//! content: { type: "bare", name: "sql" }
//! ```
//!
//! For a **call** body (`{ SELECT(table: users, where: "...") }`):
//! ```text
//! content: {
//!   type: "call",
//!   name: "SELECT",
//!   args: [
//!     { name: "table", value: "users" },
//!     { name: "where", value: "email = 'alice@example.com'" },
//!   ]
//! }
//! ```

use std::collections::BTreeMap;
use std::collections::HashMap;

use crate::tmpl_cmd_tree::{CmdContentTree, TmplCmdBlockTree};
use super::super::{LeafObject, Value};

// ── Entry point ───────────────────────────────────────────────────────────────

/// Build the top-level instance `Value::Leaf` for a cmd template call.
///
/// `args` maps each template parameter name to its resolved string value.
pub(crate) fn build_cmd_instance_leaf(
    tree: &TmplCmdBlockTree,
    args: &HashMap<String, String>,
) -> Value {
    let content = cmd_content_to_value(&tree.content, args);

    // Params map: { paramName: resolvedValue }
    let mut params_fields = BTreeMap::new();
    for param in &tree.params {
        let val = args.get(&param.name).cloned().unwrap_or_default();
        params_fields.insert(param.name.clone(), Value::String(val));
    }

    let mut fields = BTreeMap::new();
    fields.insert("kind".to_string(), Value::String("cmd".to_string()));
    fields.insert(
        "lang".to_string(),
        Value::String(tree.langs.first().cloned().unwrap_or_default()),
    );
    fields.insert(
        "langs".to_string(),
        Value::List(tree.langs.iter().map(|l| Value::String(l.clone())).collect()),
    );
    fields.insert("name".to_string(), Value::String(tree.name.clone()));
    fields.insert("params".to_string(), Value::Leaf(LeafObject::new(params_fields)));
    fields.insert("content".to_string(), content);

    Value::Leaf(LeafObject::new(fields))
}

// ── Content → Value ───────────────────────────────────────────────────────────

fn cmd_content_to_value(content: &CmdContentTree, args: &HashMap<String, String>) -> Value {
    match content {
        CmdContentTree::Bare(name) => {
            let mut fields = BTreeMap::new();
            fields.insert("type".to_string(), Value::String("bare".to_string()));
            fields.insert("name".to_string(), Value::String(name.clone()));
            Value::Leaf(LeafObject::new(fields))
        }
        CmdContentTree::Call(call) => {
            let arg_values: Vec<Value> = call
                .args
                .iter()
                .map(|a| {
                    let name = a.name.as_deref().unwrap_or("").to_string();
                    let value = substitute(&a.value, args);
                    let mut f = BTreeMap::new();
                    f.insert("name".to_string(), Value::String(name));
                    f.insert("value".to_string(), Value::String(value));
                    Value::Leaf(LeafObject::new(f))
                })
                .collect();

            let mut fields = BTreeMap::new();
            fields.insert("type".to_string(), Value::String("call".to_string()));
            fields.insert("name".to_string(), Value::String(call.name.clone()));
            fields.insert("args".to_string(), Value::List(arg_values));
            Value::Leaf(LeafObject::new(fields))
        }
    }
}

// ── Variable substitution ─────────────────────────────────────────────────────

/// Replace every `${name}` in `text` with the corresponding value from `args`.
fn substitute(text: &str, args: &HashMap<String, String>) -> String {
    let mut result = String::with_capacity(text.len());
    let mut remaining = text;

    // Strip surrounding quotes from string literals before substitution.
    let text = if (remaining.starts_with('"') && remaining.ends_with('"'))
        || (remaining.starts_with("s\"") && remaining.ends_with('"'))
    {
        let start = if remaining.starts_with("s\"") { 2 } else { 1 };
        &remaining[start..remaining.len() - 1]
    } else {
        remaining
    };

    remaining = text;

    while let Some(start) = remaining.find("${") {
        result.push_str(&remaining[..start]);
        remaining = &remaining[start + 2..];
        if let Some(end) = remaining.find('}') {
            let key = &remaining[..end];
            if let Some(val) = args.get(key) {
                result.push_str(val);
            } else {
                result.push_str("${");
                result.push_str(key);
                result.push('}');
            }
            remaining = &remaining[end + 1..];
        } else {
            result.push_str("${");
        }
    }
    result.push_str(remaining);
    result
}
