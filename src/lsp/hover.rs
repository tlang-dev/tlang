// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! `textDocument/hover` — type info and doc-comments at the cursor.
//!
//! [`compute_hover`] is called by the LSP request dispatcher in [`super`] and
//! returns markdown-formatted type signatures, doc-comments, and attribute
//! information for functions, model entities, and library methods.

use lsp_types::{Hover, HoverContents, MarkupContent, MarkupKind, Position};

use crate::parser::parse_domain_model;
use crate::runtime::compile_from_domain_model;

use super::util::{position_to_offset, word_at_position};

/// Show the full signature of the function under `position` as a markdown
/// hover popup.  When the function originates from an imported file the
/// source file name is noted below the signature.
///
/// Also shows `set` entity and attribute information when the cursor is on a
/// model entity name or on an `Entity.attr` dotted access expression.
pub(super) fn compute_hover(text: &str, position: Position) -> Option<Hover> {
    let word = word_at_position(text, position)?;

    // ── Special hover for the reserved `lead` attr keyword ────────────────
    // When the cursor is on `lead` and the current line looks like a set-body
    // attr definition (`lead: &…`), return a static explanation rather than
    // trying to resolve it as a function or dotted access.
    if word == "lead" {
        let line_idx = position.line as usize;
        if let Some(line) = text.lines().nth(line_idx) {
            let trimmed = line.trim();
            if trimmed.starts_with("lead") && trimmed.contains(": &") {
                return Some(Hover {
                    contents: HoverContents::Markup(MarkupContent {
                        kind: MarkupKind::Markdown,
                        value: concat!(
                            "**`lead`** — outer scaffold template\n\n",
                            "Designates the template that provides the outer structure for this set ",
                            "(e.g. the class or file wrapper).\n\n",
                            "Inside the lead template, `<[ attrs() ]>` is expanded with the inner ",
                            "body fragments of all other (non-lead) attrs in the set — producing ",
                            "one unified output file.\n\n",
                            "`generateAll()` instantiates the lead template last, after pre-rendering ",
                            "every other attr, and writes the combined result to the `>>` path."
                        ).to_string(),
                    }),
                    range: None,
                });
            }
        }
    }

    let model = parse_domain_model(text).ok()?;

    // ── Try set-entity / attribute hover first ─────────────────────────────
    // Check if we're hovering over a dotted access like `Entity.attr`.
    let offset = position_to_offset(text, position);
    if let Some(hover) = try_hover_model_attr(text, offset, &model) {
        return Some(hover);
    }

    // ── Fall back to function hover ────────────────────────────────────────
    let program = compile_from_domain_model(&model).ok()?;
    let info = program
        .function_infos()
        .into_iter()
        .find(|f| f.name == word)?;

    let sig = info.signature();

    // Append a source-file note for functions imported from other files.
    let body = match &info.source_file {
        Some(src) => format!("```tlang\n{sig}\n```\n\n*defined in `{src}`*"),
        None => format!("```tlang\n{sig}\n```"),
    };

    Some(Hover {
        contents: HoverContents::Markup(MarkupContent {
            kind: MarkupKind::Markdown,
            value: body,
        }),
        range: None,
    })
}

/// Attempt to provide hover information for a `set` entity attribute access.
///
/// Looks at the source text around `offset` to detect `Entity.attr` patterns
/// and returns a hover containing the attribute's declared type from the model.
pub(super) fn try_hover_model_attr(
    text: &str,
    offset: usize,
    model: &crate::ast::DomainModel,
) -> Option<Hover> {
    use crate::model_tree::{ModelNodeTree, ModelValueTypeTree, parse_model_block_tree};

    // Collect set entities from the model.
    let model_block = model.body.iter().find_map(|b| match b {
        crate::ast::DomainBlock::Model(m) => Some(m),
        _ => None,
    })?;
    let tree = parse_model_block_tree(model_block).ok()?;

    // Find the word under cursor and whether it is part of a dotted path.
    // Walk backwards to find a possible `Entity.word` context.
    let bytes = text.as_bytes();
    let _start = offset.saturating_sub(1);
    let mut attr_start = offset;
    while attr_start > 0
        && (bytes[attr_start - 1].is_ascii_alphanumeric() || bytes[attr_start - 1] == b'_')
    {
        attr_start -= 1;
    }
    let attr_name: String = text[attr_start..offset]
        .chars()
        .chain(
            text[offset..]
                .chars()
                .take_while(|c| c.is_alphanumeric() || *c == '_'),
        )
        .collect();

    if attr_name.is_empty() {
        return None;
    }

    // Check if there's a `.` right before attr_start.
    if attr_start == 0 || bytes[attr_start - 1] != b'.' {
        // Not a dotted access — check if this word is a set entity name.
        for node in &tree.nodes {
            if let ModelNodeTree::SetEntity(entity) = node
                && entity.name == attr_name
            {
                // Format a model value type as a readable string.
                fn fmt_value(v: &ModelValueTypeTree) -> String {
                    match v {
                        ModelValueTypeTree::Type(t) => t.clone(),
                        ModelValueTypeTree::Array(t) => format!("{t}[]"),
                        ModelValueTypeTree::StringLiteral(s) => format!("\"{s}\""),
                        ModelValueTypeTree::IntLiteral(n) => n.to_string(),
                        ModelValueTypeTree::BoolLiteral(b) => b.to_string(),
                        ModelValueTypeTree::ArrayLiteral(items) => {
                            let inner: Vec<String> = items.iter().map(fmt_value).collect();
                            format!("[{}]", inner.join(", "))
                        }
                        ModelValueTypeTree::Ref { path, currying } => {
                            use crate::model_tree::RefArg;
                            let mut out = format!("&{}", path.join("."));
                            for args in currying {
                                if !args.is_empty() {
                                    let parts: Vec<String> = args
                                        .iter()
                                        .map(|a| match a {
                                            RefArg::This => "this".to_string(),
                                            RefArg::Hole => "_".to_string(),
                                            RefArg::Str(s) => format!("\"{s}\""),
                                            RefArg::Int(n) => n.to_string(),
                                            RefArg::Bool(b) => b.to_string(),
                                            RefArg::Ref(rp) => format!("&{}", rp.join(".")),
                                            RefArg::ImplParam(name) => name.to_string(),
                                        })
                                        .collect();
                                    out.push('(');
                                    out.push_str(&parts.join(", "));
                                    out.push(')');
                                }
                            }
                            out
                        }
                        ModelValueTypeTree::FuncDef {
                            param_types,
                            ret_types,
                        } => {
                            format!("({}) -> ({})", param_types.join(", "), ret_types.join(", "))
                        }
                        ModelValueTypeTree::Impl { .. } => "impl".to_string(),
                        ModelValueTypeTree::ImplArray => "impl[]".to_string(),
                        ModelValueTypeTree::Generic { name, params } => {
                            format!("{name}<{}>", params.join(", "))
                        }
                    }
                }

                let params: Vec<String> = entity
                    .params
                    .iter()
                    .filter_map(|p| {
                        p.attr
                            .as_ref()
                            .map(|n| format!("`{}`: {}", n, fmt_value(&p.value)))
                    })
                    .collect();
                let constructor_sig = if !entity.params.is_empty() {
                    let sig_params: Vec<String> = entity
                        .params
                        .iter()
                        .filter_map(|p| {
                            p.attr
                                .as_ref()
                                .map(|n| format!("{}: {}", n, fmt_value(&p.value)))
                        })
                        .collect();
                    format!(
                        "\n\n**Usage:** `{}({})`",
                        entity.name,
                        sig_params.join(", ")
                    )
                } else {
                    format!("\n\n**Usage:** `{}()`", entity.name)
                };
                let attrs_desc: Vec<String> = entity
                    .attrs
                    .iter()
                    .filter_map(|a| {
                        a.attr.as_ref().map(|n| {
                            let label = if n == "lead" {
                                "  **`lead`** *(outer scaffold)*".to_string()
                            } else {
                                format!("  `{n}`")
                            };
                            format!("{}: {}", label, fmt_value(&a.value))
                        })
                    })
                    .collect();
                let mut body = format!("**set** `{}`", entity.name);
                for ext in &entity.exts {
                    body.push_str(&format!(" : `{}`", ext));
                }
                body.push_str(&constructor_sig);
                if !params.is_empty() {
                    body.push_str(&format!(
                        "\n\n**Constructor params:** {}",
                        params.join(", ")
                    ));
                }
                if !attrs_desc.is_empty() {
                    body.push_str(&format!("\n\n**Attributes:**\n{}", attrs_desc.join("\n")));
                }
                return Some(Hover {
                    contents: HoverContents::Markup(MarkupContent {
                        kind: MarkupKind::Markdown,
                        value: body,
                    }),
                    range: None,
                });
            }
        }
        return None;
    }

    // Find the entity name before the `.`.
    let dot_pos = attr_start - 1;
    let mut entity_start = dot_pos;
    while entity_start > 0
        && (bytes[entity_start - 1].is_ascii_alphanumeric() || bytes[entity_start - 1] == b'_')
    {
        entity_start -= 1;
    }
    let entity_name: String = text[entity_start..dot_pos].to_string();
    if entity_name.is_empty() {
        return None;
    }

    // Look up the entity and attribute.
    for node in &tree.nodes {
        if let ModelNodeTree::SetEntity(entity) = node {
            if entity.name != entity_name {
                continue;
            }
            // Search body attrs then constructor params.
            for attr_list in [&entity.attrs, &entity.params] {
                for attr in attr_list {
                    if attr.attr.as_deref() != Some(&attr_name) {
                        continue;
                    }
                    let type_desc = match &attr.value {
                        ModelValueTypeTree::Type(t) => format!("`{t}`"),
                        ModelValueTypeTree::Array(t) => format!("`{t}[]`"),
                        ModelValueTypeTree::StringLiteral(s) => format!("string literal `\"{s}\"`"),
                        ModelValueTypeTree::IntLiteral(n) => format!("integer literal `{n}`"),
                        ModelValueTypeTree::BoolLiteral(b) => format!("boolean literal `{b}`"),
                        ModelValueTypeTree::ArrayLiteral(items) => {
                            format!("array literal with {} item(s)", items.len())
                        }
                        ModelValueTypeTree::Ref { path, currying } => {
                            use crate::model_tree::RefArg;
                            let flat: Vec<&RefArg> = currying.iter().flatten().collect();
                            let mut sig = format!("&{}", path.join("."));
                            if !flat.is_empty() {
                                let parts: Vec<String> = flat
                                    .iter()
                                    .map(|a| match a {
                                        RefArg::This => "this".to_string(),
                                        RefArg::Hole => "_".to_string(),
                                        RefArg::Str(s) => format!("\"{s}\""),
                                        RefArg::Int(n) => n.to_string(),
                                        RefArg::Bool(b) => b.to_string(),
                                        RefArg::Ref(rp) => format!("&{}", rp.join(".")),
                                        RefArg::ImplParam(name) => name.to_string(),
                                    })
                                    .collect();
                                sig.push('(');
                                sig.push_str(&parts.join(", "));
                                sig.push(')');
                            }
                            let hole_count =
                                flat.iter().filter(|a| matches!(a, RefArg::Hole)).count();
                            if attr_name == "lead" {
                                if hole_count > 0 {
                                    format!(
                                        "lead template (outer scaffold) `{sig}` — \
                                         caller provides {hole_count} argument(s)"
                                    )
                                } else {
                                    format!("lead template (outer scaffold) `{sig}`")
                                }
                            } else if hole_count > 0 {
                                format!(
                                    "ref function `{sig}` — \
                                     caller provides {hole_count} argument(s)"
                                )
                            } else {
                                format!("ref function `{sig}`")
                            }
                        }
                        ModelValueTypeTree::FuncDef {
                            param_types,
                            ret_types,
                        } => {
                            format!(
                                "function definition `({}):({})`",
                                param_types.join(", "),
                                ret_types.join(", ")
                            )
                        }
                        ModelValueTypeTree::Impl { .. } => "`impl` placeholder".to_string(),
                        ModelValueTypeTree::ImplArray => "`impl[]` placeholder".to_string(),
                        ModelValueTypeTree::Generic { name, params } => {
                            format!("`{name}<{}>` generic type", params.join(", "))
                        }
                    };
                    let body = format!("**`{entity_name}.{attr_name}`**\n\nType: {type_desc}",);
                    return Some(Hover {
                        contents: HoverContents::Markup(MarkupContent {
                            kind: MarkupKind::Markdown,
                            value: body,
                        }),
                        range: None,
                    });
                }
            }
        }
    }

    None
}
