// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! CLI utility functions.
//!
//! Provides [`format_elapsed`] for human-readable durations, [`print_primitive_return`]
//! for printing scalar `main` return values, and [`load_env_file`] for injecting
//! `.tlang.env` key-value pairs into the process environment.

use std::collections::HashMap;
use std::path::Path;
use std::time::Duration;

use tlang::runtime::Value;

/// Print a primitive return value from `main` to stdout, followed by a
/// newline.  Non-primitive values (Unit, List, Map, Leaf) are silently
/// ignored — they are not meaningful as program output.
pub fn print_primitive_return(value: &Value) {
    match value {
        Value::String(s) => println!("{s}"),
        Value::Int(n) => println!("{n}"),
        Value::Float(f) => println!("{f}"),
        Value::Bool(b) => println!("{b}"),
        _ => {}
    }
}

pub fn format_elapsed(d: Duration) -> String {
    let ms = d.as_millis();
    if ms < 1000 {
        format!("{ms}ms")
    } else {
        format!("{:.2}s", d.as_secs_f64())
    }
}

/// Load a `.tlang.env` file from `dir` (if it exists) and inject every
/// valid `KEY=VALUE` line into the current process environment.
///
/// Rules:
/// - Lines starting with `#` or blank lines are ignored.
/// - Keys must be non-empty; values may be empty.
/// - Surrounding whitespace around key and value is stripped.
/// - Inline `#` comments are stripped from values.
/// - Values wrapped in `"…"` or `'…'` have the quotes removed.
/// - Already-set env vars are **not** overwritten (dotenv semantics).
///
/// Returns a map of the vars that were actually set (for display).
pub fn load_env_file(dir: &Path) -> Result<HashMap<String, String>, String> {
    let env_path = dir.join(".tlang.env");
    if !env_path.exists() {
        return Ok(HashMap::new());
    }

    let content = std::fs::read_to_string(&env_path)
        .map_err(|e| format!("failed to read '{}': {e}", env_path.display()))?;

    let mut loaded = HashMap::new();

    for (lineno, raw) in content.lines().enumerate() {
        let line = raw.trim();
        if line.is_empty() || line.starts_with('#') {
            continue;
        }
        let Some((key, rest)) = line.split_once('=') else {
            eprintln!(
                "  .tlang.env:{}: skipping malformed line (no `=`)",
                lineno + 1
            );
            continue;
        };
        let key = key.trim();
        if key.is_empty() {
            continue;
        }
        // Strip inline comment from value.
        let value_raw = rest.trim();
        let value = strip_env_value(value_raw);

        // Don't overwrite vars already set in the environment.
        if std::env::var(key).is_err() {
            unsafe { std::env::set_var(key, &value) };
            loaded.insert(key.to_string(), value);
        }
    }

    Ok(loaded)
}

/// Strip surrounding quotes and inline `#` comments from a raw env value.
pub fn strip_env_value(raw: &str) -> String {
    // Quoted value — strip the quotes and return contents verbatim (no comment stripping).
    if (raw.starts_with('"') && raw.ends_with('"'))
        || (raw.starts_with('\'') && raw.ends_with('\''))
    {
        if raw.len() >= 2 {
            return raw[1..raw.len() - 1].to_string();
        }
        return String::new();
    }
    // Unquoted — strip trailing inline comment.
    let value = match raw.split_once(" #").or_else(|| raw.split_once("\t#")) {
        Some((before, _)) => before.trim(),
        None => raw,
    };
    value.to_string()
}
