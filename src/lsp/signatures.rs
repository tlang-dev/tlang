// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! `textDocument/signatureHelp` — parameter hints for function calls.
//!
//! [`compute_signature_help`] scans backwards from the cursor to locate the
//! innermost unclosed `(`, counts preceding commas to determine the active
//! parameter, then returns the matching function or library-method signature.

use lsp_types::{
    ParameterInformation, ParameterLabel, Position, SignatureHelp, SignatureInformation,
};

use crate::parser::parse_domain_model;
use crate::runtime::compile_from_domain_model;

use super::completion::{library_method_table, resolve_import_alias};
use super::util::position_to_offset;

/// Show the signature of the function being called at `position`, with the
/// currently active parameter highlighted.
///
/// Triggered on `(` and `,`.  Scans backwards from the cursor to find the
/// innermost unclosed `(` and counts preceding commas to determine which
/// parameter is active.
pub(super) fn compute_signature_help(text: &str, position: Position) -> Option<SignatureHelp> {
    use crate::model_tree::{ModelNodeTree, ModelValueTypeTree, parse_model_block_tree};

    let offset = position_to_offset(text, position);
    let text_before = &text[..offset];

    let (full_name, active_param) = find_active_call(text_before)?;

    // Split "Alias.method" → (Some("Alias"), "method") or (None, "method")
    let (alias_name, method_name): (Option<&str>, &str) = if let Some(dot) = full_name.rfind('.') {
        (Some(&full_name[..dot]), &full_name[dot + 1..])
    } else {
        (None, full_name.as_str())
    };

    let model = parse_domain_model(text).ok()?;

    // Try library method signature help first (when an alias qualifier is present).
    if let Some(alias) = alias_name {
        let fqn = resolve_import_alias(alias, text).unwrap_or_else(|| alias.to_string());
        let table = library_method_table(&fqn);
        if let Some((_, detail, _)) = table.iter().find(|(name, _, _)| *name == method_name) {
            return build_library_signature_help(detail, active_param);
        }
    }

    // Check if the call target is a set entity with constructor params.
    // If so, show the positional constructor signature.
    for block in &model.body {
        if let crate::ast::DomainBlock::Model(m) = block {
            if let Ok(tree) = parse_model_block_tree(m) {
                for node in &tree.nodes {
                    if let ModelNodeTree::SetEntity(entity) = node {
                        if entity.name != method_name || entity.params.is_empty() {
                            continue;
                        }
                        // Build a positional constructor signature.
                        fn fmt_param_type(v: &ModelValueTypeTree) -> String {
                            match v {
                                ModelValueTypeTree::Type(t) => t.clone(),
                                ModelValueTypeTree::Array(t) => format!("{t}[]"),
                                ModelValueTypeTree::Generic { name, params } => {
                                    format!("{name}<{}>", params.join(", "))
                                }
                                ModelValueTypeTree::FuncDef {
                                    param_types,
                                    ret_types,
                                } => {
                                    format!(
                                        "({}) -> ({})",
                                        param_types.join(", "),
                                        ret_types.join(", ")
                                    )
                                }
                                other => format!("{other:?}"),
                            }
                        }
                        let param_labels: Vec<String> = entity
                            .params
                            .iter()
                            .filter_map(|p| {
                                p.attr
                                    .as_ref()
                                    .map(|n| format!("{}: {}", n, fmt_param_type(&p.value)))
                            })
                            .collect();
                        let sig_label = format!("{}({})", method_name, param_labels.join(", "));
                        let parameters: Vec<ParameterInformation> = param_labels
                            .iter()
                            .map(|label| ParameterInformation {
                                label: ParameterLabel::Simple(label.clone()),
                                documentation: None,
                            })
                            .collect();
                        let active =
                            (active_param as u32).min(parameters.len().saturating_sub(1) as u32);
                        return Some(SignatureHelp {
                            signatures: vec![SignatureInformation {
                                label: sig_label,
                                documentation: None,
                                parameters: Some(parameters),
                                active_parameter: Some(active),
                            }],
                            active_signature: Some(0),
                            active_parameter: Some(active),
                        });
                    }
                }
            }
        }
    }

    let program = compile_from_domain_model(&model).ok()?;

    let info = program
        .function_infos()
        .into_iter()
        .find(|f| f.name == method_name)?;

    let sig = info.signature();

    let parameters: Vec<ParameterInformation> = info
        .params
        .iter()
        .map(|(n, t)| {
            let label = match t {
                Some(ty) => format!("{n}: {ty}"),
                None => n.clone(),
            };
            ParameterInformation {
                label: ParameterLabel::Simple(label),
                documentation: None,
            }
        })
        .collect();

    // Clamp active parameter index so it never exceeds the param count.
    let active = (active_param as u32).min(parameters.len().saturating_sub(1) as u32);

    Some(SignatureHelp {
        signatures: vec![SignatureInformation {
            label: sig,
            documentation: None,
            parameters: Some(parameters),
            active_parameter: Some(active),
        }],
        active_signature: Some(0),
        active_parameter: Some(active),
    })
}

/// Build a `SignatureHelp` response for a built-in library method from its
/// `detail` string (e.g. `"println(value)"` or `"get(list, index): Value"`).
pub(super) fn build_library_signature_help(
    detail: &str,
    active_param: usize,
) -> Option<SignatureHelp> {
    let open = detail.find('(')?;
    let close = detail[open..].find(')')? + open;
    let params_str = &detail[open + 1..close];

    let params: Vec<String> = if params_str.trim().is_empty() {
        vec![]
    } else {
        params_str
            .split(',')
            .map(|p| p.trim().to_string())
            .filter(|p| !p.is_empty())
            .collect()
    };

    let parameters: Vec<ParameterInformation> = params
        .iter()
        .map(|label| ParameterInformation {
            label: ParameterLabel::Simple(label.clone()),
            documentation: None,
        })
        .collect();

    let active = (active_param as u32).min(parameters.len().saturating_sub(1) as u32);

    Some(SignatureHelp {
        signatures: vec![SignatureInformation {
            label: detail.to_string(),
            documentation: None,
            parameters: Some(parameters),
            active_parameter: Some(active),
        }],
        active_signature: Some(0),
        active_parameter: Some(active),
    })
}

/// Scan backwards through `text_before` (text up to the cursor) to find the
/// innermost unclosed function call.
///
/// Returns `(function_name, active_parameter_index)` where the index is
/// zero-based (0 = first parameter).  Returns `None` when the cursor is not
/// inside any function call.
pub(super) fn find_active_call(text_before: &str) -> Option<(String, usize)> {
    let mut depth: i32 = 0;
    let mut active_param: usize = 0;
    let bytes = text_before.as_bytes();
    let mut i = bytes.len();

    while i > 0 {
        i -= 1;
        match bytes[i] {
            // Closing delimiters increase depth — we're inside a nested expr.
            b')' | b']' => depth += 1,
            b'(' => {
                if depth == 0 {
                    // This is the opening paren of the active call.
                    // The function name sits immediately before it.
                    let before = &text_before[..i];
                    // Include '.' so that "Alias.method" is collected whole.
                    let raw: String = before
                        .trim_end()
                        .chars()
                        .rev()
                        .take_while(|c| c.is_alphanumeric() || *c == '_' || *c == '.')
                        .collect::<String>()
                        .chars()
                        .rev()
                        .collect();
                    let func_name = raw.trim_matches('.').to_string();
                    return if func_name.is_empty() {
                        None
                    } else {
                        Some((func_name, active_param))
                    };
                }
                depth -= 1;
            }
            b'[' => {
                if depth > 0 {
                    depth -= 1;
                } else {
                    // Outside of any call — cursor is inside an array/index expression.
                    return None;
                }
            }
            b'{' => {
                // Entering a block from the right means we left the call scope.
                if depth == 0 {
                    return None;
                }
                depth -= 1;
            }
            b',' if depth == 0 => active_param += 1,
            _ => {}
        }
    }
    None
}
