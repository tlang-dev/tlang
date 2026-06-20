// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use super::super::{Runtime, RuntimeError, Value};

/// `TLang.Call.invoke(funcName: String, ...args): Any`
///
/// Dynamically calls a user-defined function by name at runtime. Enables
/// adapter dispatch without compile-time alias resolution:
///
/// ```tlang
/// let adapterName = Forge.projectAttr(projectName, "codeAdapter")
/// TLang.Call.invoke(adapterName + ".generateDatabase", dbName, projectName)
/// ```
///
/// The function is looked up in the program's function table using the same
/// logic as a direct call, including the qualified-name fallback for
/// manifest-backed packages.
pub(crate) fn call(
    runtime: &mut Runtime<'_>,
    target: &str,
    args: &[Value],
) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Call.invoke" => invoke(runtime, args),
        _ => Err(RuntimeError(format!(
            "unknown TLang.Call function `{target}`"
        ))),
    }
}

fn invoke(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    if args.is_empty() {
        return Err(RuntimeError(
            "TLang.Call.invoke requires at least 1 argument: the function name (String)".to_string(),
        ));
    }
    let func_name = match &args[0] {
        Value::String(s) => s.clone(),
        other => {
            return Err(RuntimeError(format!(
                "TLang.Call.invoke: first argument must be a String function name, got {}",
                super::super::value_type_name(other)
            )));
        }
    };
    let call_args = args[1..].to_vec();
    runtime.call_user_function(&func_name, call_args)
}
