// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Parse and semantic error diagnostics.
//!
//! [`compute_diagnostics`] runs the TLang parser, semantic checker, and
//! validator on the current document text and converts the results into LSP
//! `Diagnostic` values. [`publish_diagnostics_with_sender`] wraps the result
//! in a `PublishDiagnostics` notification and sends it over the LSP connection.

use lsp_server::{Message, Notification};
use lsp_types::notification::{Notification as NotificationTrait, PublishDiagnostics};
use lsp_types::{Diagnostic, DiagnosticSeverity, Position, PublishDiagnosticsParams, Range, Uri};

use crate::error_checker::{offset_to_line_position, validate_model};
use crate::parser::parse_domain_model;
use crate::runtime::{collect_semantic_warnings, compile_from_domain_model};

use super::util::percent_decode_uri_path;

/// Compute LSP diagnostics for `text`.
///
/// - Parse errors become ERROR diagnostics.
/// - Semantic / type-checker errors become ERROR diagnostics, **except** for
///   `"no helper block found"` which is silently suppressed: model-only and
///   template-only files are perfectly valid TLang source that simply have no
///   helper block, so showing a red squiggle on them is confusing.
/// - Non-fatal compiler warnings become WARNING diagnostics.
/// - Validation errors (imports, exposes, templates, etc.) are also included.
///
/// `file_path` is the absolute path of the document being checked.  When
/// provided, import statements are verified against the file system and the
/// correct file name appears in error messages.
pub(super) fn compute_diagnostics(
    text: &str,
    file_path: Option<&std::path::Path>,
) -> Vec<Diagnostic> {
    let file_name = file_path
        .and_then(|p| p.to_str())
        .unwrap_or("<input>");

    match parse_domain_model(text) {
        Err(e) => vec![diagnostic_from_parse_error(&e.0, text)],
        Ok(model) => {
            // Build a lightweight map of existing sibling files so that
            // `validate_imports` can check whether `use X` files exist on disk.
            let loaded_models = if let Some(fp) = file_path {
                build_sibling_file_stubs(fp, &model)
            } else {
                std::collections::HashMap::new()
            };

            // Run validation checks
            let validation_errors = validate_model(
                &model,
                text,
                file_name,
                &loaded_models,
                &std::collections::HashMap::new(),
            );

            let validation_diagnostics: Vec<Diagnostic> = validation_errors
                .errors
                .into_iter()
                .map(|e| e.to_lsp_diagnostic())
                .collect();

            // Also check compilation
            match compile_from_domain_model(&model) {
                Err(e) => {
                    let mut diagnostics = validation_diagnostics;
                    if e.0.contains("no helper block found") {
                        diagnostics
                    } else {
                        diagnostics.push(diagnostic_from_semantic_error(&e.0, text));
                        diagnostics
                    }
                }
                Ok(program) => {
                    let mut diagnostics = validation_diagnostics;
                    diagnostics.extend(
                        collect_semantic_warnings(&program)
                            .into_iter()
                            .map(|msg| diagnostic_warning(&msg, text)),
                    );
                    diagnostics
                }
            }
        }
    }
}

/// Publish diagnostics for `text` to the client via `sender`.
/// Designed to be called from a background thread.
pub(super) fn publish_diagnostics_with_sender(
    sender: &crossbeam_channel::Sender<Message>,
    uri: Uri,
    text: &str,
) {
    // Extract a file-system path from the URI so we can resolve imports.
    let file_path: Option<std::path::PathBuf> = uri
        .as_str()
        .strip_prefix("file://")
        .and_then(|s| {
            // URL-decode the path (handles %20 etc.) using a simple approach.
            let decoded = percent_decode_uri_path(s);
            Some(std::path::PathBuf::from(decoded))
        });

    let diagnostics = compute_diagnostics(text, file_path.as_deref());
    let params = PublishDiagnosticsParams {
        uri,
        diagnostics,
        version: None,
    };
    let notif = Notification::new(
        PublishDiagnostics::METHOD.to_string(),
        serde_json::to_value(params).unwrap(),
    );
    sender.send(Message::Notification(notif)).ok();
}

/// Build a stub map of loaded models for `validate_imports`.
///
/// For every `use` statement in the model that refers to a plain file (not a
/// built-in `TLang.*` module), check whether the corresponding `.tlang` file
/// exists on disk relative to `current_file`.  If it does, insert an empty
/// placeholder entry under its stem so `validate_imports` treats it as
/// "found".  Missing files are NOT inserted, causing `validate_imports` to
/// emit an error diagnostic.
pub(super) fn build_sibling_file_stubs(
    current_file: &std::path::Path,
    model: &crate::ast::DomainModel,
) -> std::collections::HashMap<String, crate::ast::DomainModel> {
    use crate::ast::DomainModel;
    let current_dir = current_file.parent().unwrap_or(std::path::Path::new("."));
    let mut map: std::collections::HashMap<String, DomainModel> =
        std::collections::HashMap::new();

    // Collect manifest dependency aliases by walking up from the current
    // directory until we find a manifest.yml.  Any alias declared in the
    // manifest is treated as "available" even if the package directory isn't
    // on disk (e.g. registry dependencies resolved via the tbox).
    let manifest_aliases = manifest_dependency_aliases(current_dir);

    for u in &model.header.uses {
        if u.path.is_empty() {
            continue;
        }
        // Skip built-in modules (TLang.*, MCP.*, System.*).
        if matches!(u.path[0].as_str(), "TLang" | "MCP" | "System") {
            continue;
        }
        let file_stem = u.path.last().cloned().unwrap_or_default();

        // A use whose first segment is a manifest dependency alias is valid —
        // e.g. `use KotlinGen as kotlin` where `KotlinGen` is declared in
        // manifest.yml as a dependency alias.
        if manifest_aliases.contains(u.path[0].as_str()) {
            map.insert(file_stem.clone(), DomainModel::default());
            continue;
        }

        // Resolve the file path the same way the loader does (one directory
        // level at most for relative imports).
        let resolved = if u.path.len() == 1 {
            current_dir.join(format!("{file_stem}.tlang"))
        } else {
            // e.g. `use SubFolder.File` → `current_dir/SubFolder/File.tlang`
            let mut p = current_dir.to_path_buf();
            for seg in &u.path[..u.path.len() - 1] {
                p.push(seg);
            }
            p.push(format!("{file_stem}.tlang"));
            p
        };

        if resolved.exists() {
            map.insert(file_stem, DomainModel::default());
        }
    }

    map
}

/// Walk up from `dir` until a `manifest.yml` is found, then return the set of
/// dependency alias names it declares.  Returns an empty set when no manifest
/// is found or it cannot be parsed.
pub(super) fn manifest_dependency_aliases(
    dir: &std::path::Path,
) -> std::collections::HashSet<String> {
    use crate::manifest::try_load_manifest;
    let mut current = dir;
    loop {
        if let Ok(Some(manifest)) = try_load_manifest(current) {
            return manifest
                .dependencies
                .iter()
                .map(|d| d.alias.clone())
                .collect();
        }
        match current.parent() {
            Some(parent) => current = parent,
            None => return std::collections::HashSet::new(),
        }
    }
}

/// and has the shape:
/// `<message> (file: <f>, line: <l>, position: <p>)`
/// We extract line/position from the message when available; otherwise we fall
/// back to the start of the document.
pub(super) fn diagnostic_from_parse_error(message: &str, _text: &str) -> Diagnostic {
    let (line, character) = extract_line_position(message);

    Diagnostic {
        range: Range {
            start: Position { line, character },
            end: Position { line, character },
        },
        severity: Some(DiagnosticSeverity::ERROR),
        message: message.to_string(),
        source: Some("tlang".to_string()),
        ..Default::default()
    }
}

/// Build an LSP [`Range`] from the `at_offset: N` tag embedded in type-checker
/// messages.
///
/// * `start` — the character at `at_offset`
/// * `end`   — the last non-whitespace character on the same line
///
/// This causes the editor to underline the whole relevant construct (function
/// signature, call expression, …) rather than just a single caret point.
/// Falls back to the document origin when the tag is absent.
pub(super) fn range_from_offset_tag(message: &str, source_text: &str) -> Range {
    let Some(offset) = parse_tagged_usize(message, "at_offset:") else {
        return Range {
            start: Position {
                line: 0,
                character: 0,
            },
            end: Position {
                line: 0,
                character: 0,
            },
        };
    };

    let (line_1, col_1) = offset_to_line_position(source_text, offset);
    // offset_to_line_position returns 1-based numbers; LSP uses 0-based.
    let line = line_1.saturating_sub(1) as u32;
    let col_start = col_1.saturating_sub(1) as u32;

    // Extend the underline to the trimmed end of the same source line.
    let rest = &source_text[offset..];
    let line_len = rest.find('\n').unwrap_or(rest.len());
    let trimmed = rest[..line_len].trim_end().len() as u32;
    let col_end = col_start + trimmed;

    Range {
        start: Position {
            line,
            character: col_start,
        },
        end: Position {
            line,
            character: col_end,
        },
    }
}

/// Strip the `(at_offset: N)` suffix that the type checker appends for
/// position tracking.  The cleaned message is shown in the editor UI.
pub(super) fn strip_offset_tag(message: &str) -> &str {
    if let Some(idx) = message.rfind(" (at_offset:") {
        message[..idx].trim_end()
    } else {
        message
    }
}

/// Convert a semantic / type-checker error into an LSP [`Diagnostic`].
pub(super) fn diagnostic_from_semantic_error(message: &str, source_text: &str) -> Diagnostic {
    Diagnostic {
        range: range_from_offset_tag(message, source_text),
        severity: Some(DiagnosticSeverity::ERROR),
        message: strip_offset_tag(message).to_string(),
        source: Some("tlang".to_string()),
        ..Default::default()
    }
}

/// Create a Warning-level [`Diagnostic`] (e.g. for missing type annotations).
pub(super) fn diagnostic_warning(message: &str, source_text: &str) -> Diagnostic {
    Diagnostic {
        range: range_from_offset_tag(message, source_text),
        severity: Some(DiagnosticSeverity::WARNING),
        message: strip_offset_tag(message).to_string(),
        source: Some("tlang".to_string()),
        ..Default::default()
    }
}

/// Extract `(line, position)` from an error message produced by
/// `format_with_context`, converting to 0-based LSP coordinates.
pub(super) fn extract_line_position(message: &str) -> (u32, u32) {
    // Pattern: "... (file: <f>, line: <l>, position: <p>)"
    let line = parse_tagged_usize(message, "line:").map(|l| l.saturating_sub(1) as u32);
    let pos = parse_tagged_usize(message, "position:").map(|p| p.saturating_sub(1) as u32);
    (line.unwrap_or(0), pos.unwrap_or(0))
}

pub(super) fn parse_tagged_usize(s: &str, tag: &str) -> Option<usize> {
    let idx = s.find(tag)?;
    let rest = s[idx + tag.len()..].trim_start();
    let end = rest
        .find(|c: char| !c.is_ascii_digit())
        .unwrap_or(rest.len());
    rest[..end].parse().ok()
}

pub(super) fn end_position(text: &str) -> Position {
    let mut line = 0u32;
    let mut character = 0u32;
    for ch in text.chars() {
        if ch == '\n' {
            line += 1;
            character = 0;
        } else {
            character += 1;
        }
    }
    Position { line, character }
}
