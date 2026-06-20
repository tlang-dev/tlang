// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use super::super::{Runtime, RuntimeError, Value};
use super::expect_string;

pub(crate) fn call(
    runtime: &mut Runtime<'_>,
    target: &str,
    args: &[Value],
) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Generator.generate" => generate(runtime, args),
        _ => Err(RuntimeError(format!(
            "unknown generator library function `{target}`"
        ))),
    }
}

fn kotlin_codegen_path() -> std::path::PathBuf {
    std::path::Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("generators")
        .join("kotlin")
        .join("KotlinCodegen.tlang")
}

fn doc_gen_path(format: &str) -> std::path::PathBuf {
    let filename = format!(
        "Doc{}Gen.tlang",
        format
            .chars()
            .enumerate()
            .map(|(i, c)| if i == 0 { c.to_ascii_uppercase() } else { c })
            .collect::<String>()
    );
    std::path::Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("generators")
        .join("doc")
        .join(format)
        .join(filename)
}

fn data_gen_path(format: &str) -> std::path::PathBuf {
    let filename = format!(
        "{}Gen.tlang",
        format
            .chars()
            .enumerate()
            .map(|(i, c)| if i == 0 { c.to_ascii_uppercase() } else { c })
            .collect::<String>()
    );
    std::path::Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("generators")
        .join(format)
        .join(filename)
}

fn style_gen_path(format: &str) -> std::path::PathBuf {
    let filename = format!(
        "Style{}Gen.tlang",
        format
            .chars()
            .enumerate()
            .map(|(i, c)| if i == 0 { c.to_ascii_uppercase() } else { c })
            .collect::<String>()
    );
    // Generic formats (json, js, …) live in their own top-level directory so
    // they can be shared with other template kinds.  Format-specific renderers
    // (css, scss, …) stay under generators/style/.
    let top_level = std::path::Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("generators")
        .join(format)
        .join(&filename);
    if top_level.exists() {
        return top_level;
    }
    std::path::Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("generators")
        .join("style")
        .join(format)
        .join(filename)
}

fn extract_kind_from_leaf(leaf: &Value) -> Option<String> {
    if let Value::Leaf(obj) = leaf {
        if let Some(Value::String(kind)) = obj.get("kind") {
            return Some(kind.clone());
        }
    }
    None
}

fn generate(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    if args.is_empty() || args.len() > 3 {
        return Err(RuntimeError(
            "TLang.Generator.generate expects 1, 2, or 3 arguments".to_string(),
        ));
    }

    let tmpl_leaf = args[0].clone();

    // Doc template leaves carry kind="doc" and route to built-in doc generators.
    if extract_kind_from_leaf(&tmpl_leaf).as_deref() == Some("doc") {
        let format = extract_lang_from_leaf(&tmpl_leaf)?;
        let normalized = normalize_language(&format);
        let gen_path = doc_gen_path(&normalized);
        if gen_path.exists() {
            return crate::runtime::call_in_file(&gen_path, "render", vec![tmpl_leaf])
                .map_err(|e| {
                    RuntimeError(format!(
                        "TLang.Generator.generate doc/{normalized} failed: {}",
                        e.0
                    ))
                });
        }
        // No built-in generator: fall through to user-supplied render_<format>.
        let function_name = format!("render_{normalized}");
        return runtime
            .call_user_function(&function_name, vec![tmpl_leaf])
            .map_err(|err| {
                RuntimeError(format!(
                    "TLang.Generator.generate doc/{normalized} failed: {}",
                    err.0
                ))
            });
    }

    // Style template leaves carry kind="style" and route to built-in style generators.
    if extract_kind_from_leaf(&tmpl_leaf).as_deref() == Some("style") {
        let format = extract_lang_from_leaf(&tmpl_leaf)?;
        let normalized = normalize_style_language(&format);
        let gen_path = style_gen_path(&normalized);
        if gen_path.exists() {
            return crate::runtime::call_in_file(&gen_path, "render", vec![tmpl_leaf])
                .map_err(|e| {
                    RuntimeError(format!(
                        "TLang.Generator.generate style/{normalized} failed: {}",
                        e.0
                    ))
                });
        }
        // No built-in generator: fall through to user-supplied render_<format>.
        let function_name = format!("render_{normalized}");
        return runtime
            .call_user_function(&function_name, vec![tmpl_leaf])
            .map_err(|err| {
                RuntimeError(format!(
                    "TLang.Generator.generate style/{normalized} failed: {}",
                    err.0
                ))
            });
    }

    // Cmd template leaves carry kind="cmd" and route to built-in command generators.
    if extract_kind_from_leaf(&tmpl_leaf).as_deref() == Some("cmd") {
        let format = extract_lang_from_leaf(&tmpl_leaf)?;
        let normalized = normalize_language(&format);
        let gen_path = data_gen_path(&normalized);
        if gen_path.exists() {
            return crate::runtime::call_in_file(&gen_path, "generate", vec![tmpl_leaf])
                .map_err(|e| {
                    RuntimeError(format!(
                        "TLang.Generator.generate cmd/{normalized} failed: {}",
                        e.0
                    ))
                });
        }
        let function_name = format!("generate_{normalized}");
        return runtime
            .call_user_function(&function_name, vec![tmpl_leaf])
            .map_err(|err| {
                RuntimeError(format!(
                    "TLang.Generator.generate cmd/{normalized} failed: {}",
                    err.0
                ))
            });
    }

    // Data template leaves carry kind="data" and route to built-in data generators.
    if extract_kind_from_leaf(&tmpl_leaf).as_deref() == Some("data") {
        let format = extract_lang_from_leaf(&tmpl_leaf)?;
        let normalized = normalize_language(&format);
        let gen_path = data_gen_path(&normalized);
        if gen_path.exists() {
            return crate::runtime::call_in_file(&gen_path, "generate", vec![tmpl_leaf])
                .map_err(|e| {
                    RuntimeError(format!(
                        "TLang.Generator.generate data/{normalized} failed: {}",
                        e.0
                    ))
                });
        }
        // No built-in generator: fall through to user-supplied generate_<format>.
        let function_name = format!("generate_{normalized}");
        return runtime
            .call_user_function(&function_name, vec![tmpl_leaf])
            .map_err(|err| {
                RuntimeError(format!(
                    "TLang.Generator.generate data/{normalized} failed: {}",
                    err.0
                ))
            });
    }

    // 1-arg form: Generator.generate(templateCall(...))
    // The language is derived from the `lang` field embedded in the instance leaf.
    let language = if args.len() == 1 {
        extract_lang_from_leaf(&tmpl_leaf)?
    } else {
        expect_string(&args[1], "TLang.Generator.generate language")?
    };

    let normalized = normalize_language(&language);

    // For Kotlin, run the on-disk TLang codegen program (a proper TLang source
    // file with `expose generate` header and `helper { }` block).
    if normalized == "kotlin" {
        return crate::runtime::call_in_file(&kotlin_codegen_path(), "generate", vec![tmpl_leaf])
            .map_err(|e| {
                RuntimeError(format!(
                    "TLang.Generator.generate failed for language `{language}`: {}",
                    e.0
                ))
            });
    }

    // For all other languages, call the user-supplied generate_<lang> function.
    let function_name = format!("generate_{normalized}");
    let generator_args = if args.len() == 3 {
        vec![tmpl_leaf, args[2].clone()]
    } else {
        vec![tmpl_leaf]
    };

    runtime
        .call_user_function(&function_name, generator_args)
        .map_err(|err| {
            RuntimeError(format!(
                "TLang.Generator.generate failed for language `{language}`: {}",
                err.0
            ))
        })
}

/// Extract the `lang` identifier from a template-instance Leaf value.
///
/// Template calls now return a Leaf that contains a dedicated `lang` field
/// (e.g. `"kotlin"`).  As a fallback we also parse it out of the stored
/// `value` string (`"lang [kotlin] …"`) for instances obtained through the
/// `Leaf.model()` / `Leaf.get()` path.
fn extract_lang_from_leaf(leaf: &Value) -> Result<String, RuntimeError> {
    if let Value::Leaf(obj) = leaf {
        // Preferred: explicit `lang` field added by instantiate_template.
        if let Some(Value::String(lang)) = obj.get("lang") {
            return Ok(lang.clone());
        }
        // Fallback: parse `lang [<id>]` from the stored value string.
        if let Some(Value::String(value_str)) = obj.get("value")
            && let Some(lang) = parse_lang_from_instance_value(value_str) {
                return Ok(lang);
            }
    }
    Err(RuntimeError(
        "TLang.Generator.generate: cannot determine language from template instance;          pass it as the second argument or use a template call with a lang [alias]".to_string(),
    ))
}

/// Parse the `lang` identifier out of a stored template-instance value string.
/// The format is `lang [<id>] <name>(<args>) { … }`.
fn parse_lang_from_instance_value(s: &str) -> Option<String> {
    let s = s.trim();
    if s.starts_with("lang [") {
        let end = s.find(']')?;
        let lang = s[6..end].trim().to_string();
        if !lang.is_empty() {
            return Some(lang);
        }
    }
    None
}

fn normalize_language(language: &str) -> String {
    language
        .trim()
        .chars()
        .map(|c| {
            if c.is_ascii_alphanumeric() {
                c.to_ascii_lowercase()
            } else {
                '_'
            }
        })
        .collect()
}

/// Normalise style format names: ts/typescript/javascript all resolve to "js";
/// scss and less stay distinct; everything else is lowercased.
fn normalize_style_language(language: &str) -> String {
    match language.trim().to_ascii_lowercase().as_str() {
        "ts" | "typescript" | "javascript" => "js".to_string(),
        other => normalize_language(other),
    }
}
