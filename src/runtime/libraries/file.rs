// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::{
    env,
    path::{Path, PathBuf},
};

use super::{
    super::{RuntimeError, Value, value_to_string},
    expect_string,
};

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.File.read" => read(args),
        "TLang.File.write" => write(args),
        "TLang.File.exists" => exists(args),
        "TLang.File.searchReplace" => search_replace(args),
        "TLang.File.append" => append(args),
        "TLang.File.appendAfter" => append_after(args),
        "TLang.File.prependBefore" => prepend_before(args),
        "TLang.File.createDir" => create_dir(args),
        "TLang.File.deleteFile" => delete_file(args),
        "TLang.File.deleteDir" => delete_dir(args),
        _ => Err(RuntimeError(format!(
            "unknown file library function `{target}`"
        ))),
    }
}

fn read(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.File.read expects exactly one argument".to_string(),
        ));
    }
    let path = expect_string(&args[0], "TLang.File.read path")?;
    let resolved = resolve_path(&path, true, "TLang.File.read")?;
    std::fs::read_to_string(&resolved)
        .map(Value::String)
        .map_err(|err| RuntimeError(format!("TLang.File.read failed for `{path}`: {err}")))
}

fn write(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() < 2 || args.len() > 3 {
        return Err(RuntimeError(
            "TLang.File.write expects two or three arguments: (path, content) or (path, content, overwrite)"
                .to_string(),
        ));
    }

    let path = expect_string(&args[0], "TLang.File.write path")?;
    let content = value_to_string(&args[1]);

    // Third argument: overwrite flag (default false — skip if file already exists).
    let overwrite = if args.len() == 3 {
        match &args[2] {
            Value::Bool(b) => *b,
            other => {
                return Err(RuntimeError(format!(
                    "TLang.File.write third argument (overwrite) must be a Bool, got `{other:?}`"
                )));
            }
        }
    } else {
        false
    };

    let resolved = resolve_path(&path, false, "TLang.File.write")?;

    // Default behaviour: do not overwrite an existing file.
    // Generators use this to write scaffold files the user is meant to edit.
    if !overwrite && resolved.exists() {
        return Ok(Value::Unit);
    }

    // Ensure parent directories exist before writing.
    if let Some(parent) = resolved.parent() {
        std::fs::create_dir_all(parent).map_err(|err| {
            RuntimeError(format!(
                "TLang.File.write could not create directories for `{path}`: {err}"
            ))
        })?;
    }

    std::fs::write(&resolved, content)
        .map_err(|err| RuntimeError(format!("TLang.File.write failed for `{path}`: {err}")))?;
    Ok(Value::Unit)
}

fn search_replace(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 3 {
        return Err(RuntimeError(
            "TLang.File.searchReplace expects exactly three arguments: (path, search, replace)"
                .to_string(),
        ));
    }
    let path = expect_string(&args[0], "TLang.File.searchReplace path")?;
    let search = expect_string(&args[1], "TLang.File.searchReplace search")?;
    let replace = expect_string(&args[2], "TLang.File.searchReplace replace")?;
    let resolved = resolve_path(&path, true, "TLang.File.searchReplace")?;
    let content = std::fs::read_to_string(&resolved).map_err(|err| {
        RuntimeError(format!(
            "TLang.File.searchReplace failed to read `{path}`: {err}"
        ))
    })?;
    if !content.contains(search.as_str()) {
        return Err(RuntimeError(format!(
            "TLang.File.searchReplace could not find search sequence in `{path}`"
        )));
    }
    let new_content = content.replacen(search.as_str(), replace.as_str(), 1);
    std::fs::write(&resolved, new_content).map_err(|err| {
        RuntimeError(format!(
            "TLang.File.searchReplace failed to write `{path}`: {err}"
        ))
    })?;
    Ok(Value::Unit)
}

fn append(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.File.append expects exactly two arguments: (path, content)".to_string(),
        ));
    }
    let path = expect_string(&args[0], "TLang.File.append path")?;
    let content = value_to_string(&args[1]);
    let resolved = resolve_path(&path, true, "TLang.File.append")?;
    use std::io::Write;
    let mut file = std::fs::OpenOptions::new()
        .append(true)
        .open(&resolved)
        .map_err(|err| RuntimeError(format!("TLang.File.append failed to open `{path}`: {err}")))?;
    file.write_all(content.as_bytes()).map_err(|err| {
        RuntimeError(format!("TLang.File.append failed to write `{path}`: {err}"))
    })?;
    Ok(Value::Unit)
}

fn append_after(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 3 {
        return Err(RuntimeError(
            "TLang.File.appendAfter expects exactly three arguments: (path, sequence, content)"
                .to_string(),
        ));
    }
    let path = expect_string(&args[0], "TLang.File.appendAfter path")?;
    let sequence = expect_string(&args[1], "TLang.File.appendAfter sequence")?;
    let content = value_to_string(&args[2]);
    let resolved = resolve_path(&path, true, "TLang.File.appendAfter")?;
    let existing = std::fs::read_to_string(&resolved).map_err(|err| {
        RuntimeError(format!(
            "TLang.File.appendAfter failed to read `{path}`: {err}"
        ))
    })?;
    let insert_pos = existing.find(sequence.as_str()).ok_or_else(|| {
        RuntimeError(format!(
            "TLang.File.appendAfter could not find sequence in `{path}`"
        ))
    })?;
    let offset = insert_pos + sequence.len();
    let mut new_content = String::with_capacity(existing.len() + content.len());
    new_content.push_str(&existing[..offset]);
    new_content.push_str(&content);
    new_content.push_str(&existing[offset..]);
    std::fs::write(&resolved, new_content).map_err(|err| {
        RuntimeError(format!(
            "TLang.File.appendAfter failed to write `{path}`: {err}"
        ))
    })?;
    Ok(Value::Unit)
}

fn prepend_before(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 3 {
        return Err(RuntimeError(
            "TLang.File.prependBefore expects exactly three arguments: (path, sequence, content)"
                .to_string(),
        ));
    }
    let path = expect_string(&args[0], "TLang.File.prependBefore path")?;
    let sequence = expect_string(&args[1], "TLang.File.prependBefore sequence")?;
    let content = value_to_string(&args[2]);
    let resolved = resolve_path(&path, true, "TLang.File.prependBefore")?;
    let existing = std::fs::read_to_string(&resolved).map_err(|err| {
        RuntimeError(format!(
            "TLang.File.prependBefore failed to read `{path}`: {err}"
        ))
    })?;
    let insert_pos = existing.find(sequence.as_str()).ok_or_else(|| {
        RuntimeError(format!(
            "TLang.File.prependBefore could not find sequence in `{path}`"
        ))
    })?;
    let mut new_content = String::with_capacity(existing.len() + content.len());
    new_content.push_str(&existing[..insert_pos]);
    new_content.push_str(&content);
    new_content.push_str(&existing[insert_pos..]);
    std::fs::write(&resolved, new_content).map_err(|err| {
        RuntimeError(format!(
            "TLang.File.prependBefore failed to write `{path}`: {err}"
        ))
    })?;
    Ok(Value::Unit)
}

fn exists(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.File.exists expects exactly one argument".to_string(),
        ));
    }
    let path = expect_string(&args[0], "TLang.File.exists path")?;
    // Resolve without requiring the path to exist yet.
    let resolved = resolve_path(&path, false, "TLang.File.exists")?;
    Ok(Value::Bool(resolved.exists()))
}

fn create_dir(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.File.createDir expects exactly one argument: (path)".to_string(),
        ));
    }
    let path = expect_string(&args[0], "TLang.File.createDir path")?;
    let resolved = resolve_path(&path, false, "TLang.File.createDir")?;
    std::fs::create_dir_all(&resolved).map_err(|err| {
        RuntimeError(format!(
            "TLang.File.createDir failed to create `{path}`: {err}"
        ))
    })?;
    Ok(Value::Unit)
}

fn delete_file(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.File.deleteFile expects exactly one argument: (path)".to_string(),
        ));
    }
    let path = expect_string(&args[0], "TLang.File.deleteFile path")?;
    let resolved = resolve_path(&path, true, "TLang.File.deleteFile")?;
    if resolved.is_dir() {
        return Err(RuntimeError(format!(
            "TLang.File.deleteFile `{path}` is a directory; use File.deleteDir instead"
        )));
    }
    std::fs::remove_file(&resolved).map_err(|err| {
        RuntimeError(format!(
            "TLang.File.deleteFile failed to delete `{path}`: {err}"
        ))
    })?;
    Ok(Value::Unit)
}

fn delete_dir(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() < 1 || args.len() > 2 {
        return Err(RuntimeError(
            "TLang.File.deleteDir expects one or two arguments: (path) or (path, recursive)"
                .to_string(),
        ));
    }
    let path = expect_string(&args[0], "TLang.File.deleteDir path")?;
    let recursive = if args.len() == 2 {
        match &args[1] {
            Value::Bool(b) => *b,
            other => {
                return Err(RuntimeError(format!(
                    "TLang.File.deleteDir second argument (recursive) must be a Bool, got `{other:?}`"
                )));
            }
        }
    } else {
        false
    };
    let resolved = resolve_path(&path, true, "TLang.File.deleteDir")?;
    if !resolved.is_dir() {
        return Err(RuntimeError(format!(
            "TLang.File.deleteDir `{path}` is not a directory; use File.deleteFile instead"
        )));
    }
    if recursive {
        std::fs::remove_dir_all(&resolved).map_err(|err| {
            RuntimeError(format!(
                "TLang.File.deleteDir failed to recursively delete `{path}`: {err}"
            ))
        })?;
    } else {
        std::fs::remove_dir(&resolved).map_err(|err| {
            RuntimeError(format!(
                "TLang.File.deleteDir failed to delete `{path}` (directory may not be empty; pass true to delete recursively): {err}"
            ))
        })?;
    }
    Ok(Value::Unit)
}

fn resolve_path(path: &str, must_exist: bool, operation: &str) -> Result<PathBuf, RuntimeError> {
    let candidate = PathBuf::from(path);
    let absolute = if candidate.is_absolute() {
        candidate
    } else {
        env::current_dir()
            .map_err(|err| {
                RuntimeError(format!(
                    "{operation} could not read current directory: {err}"
                ))
            })?
            .join(candidate)
    };

    let resolved = if must_exist {
        absolute
            .canonicalize()
            .map_err(|err| RuntimeError(format!("{operation} could not resolve `{path}`: {err}")))?
    } else {
        // Walk up the path to find the deepest ancestor that already exists,
        // canonicalize that, then re-append the remaining components.
        // This allows writing to deeply nested paths whose parents don't exist
        // yet (File.write calls create_dir_all before writing).
        if absolute.exists() {
            absolute.canonicalize().map_err(|err| {
                RuntimeError(format!("{operation} could not resolve `{path}`: {err}"))
            })?
        } else {
            let mut components: Vec<std::ffi::OsString> = Vec::new();
            let mut base = absolute.clone();
            loop {
                let parent = match base.parent() {
                    Some(p) if p != base => p.to_path_buf(),
                    _ => break,
                };
                components.push(
                    base.file_name()
                        .unwrap_or_else(|| std::ffi::OsStr::new(""))
                        .to_os_string(),
                );
                if parent.exists() {
                    base = parent.canonicalize().map_err(|err| {
                        RuntimeError(format!(
                            "{operation} could not resolve parent of `{path}`: {err}"
                        ))
                    })?;
                    break;
                }
                base = parent;
            }
            components.reverse();
            let mut result = base;
            for component in components {
                result = result.join(component);
            }
            result
        }
    };

    if allowed_roots()?
        .iter()
        .any(|root| resolved.starts_with(root))
    {
        Ok(resolved)
    } else {
        Err(RuntimeError(format!(
            "{operation} only allows paths inside the working directory or temp directory"
        )))
    }
}

fn allowed_roots() -> Result<Vec<PathBuf>, RuntimeError> {
    let mut roots = Vec::new();
    let current_dir = env::current_dir()
        .map_err(|err| {
            RuntimeError(format!(
                "TLang.File could not read current directory: {err}"
            ))
        })?
        .canonicalize()
        .map_err(|err| {
            RuntimeError(format!(
                "TLang.File could not resolve current directory: {err}"
            ))
        })?;
    roots.push(current_dir);

    if let Ok(temp_dir) = env::temp_dir().canonicalize()
        && !roots.iter().any(|root| root == &temp_dir)
    {
        roots.push(temp_dir);
    }

    Ok(roots)
}
