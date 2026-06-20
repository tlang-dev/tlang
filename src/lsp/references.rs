// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! `textDocument/references`, `textDocument/rename`, and `textDocument/definition` providers.
//!
//! [`compute_references`] finds all word-boundary occurrences of the symbol under
//! the cursor, [`compute_rename`] produces workspace edits to rename it everywhere,
//! and [`compute_definition`] jumps to the symbol's declaration site.

use lsp_types::{Location, Position, Range, TextEdit, Uri, WorkspaceEdit};

use crate::error_checker::offset_to_line_position;

use super::util::{is_ident_byte, word_at_position};

/// Return every location in the document where the identifier under `position`
/// appears as a standalone word (identifier boundaries on both sides).
///
/// When `include_declaration` is `true` (from `ReferenceParams`) the
/// definition site is included; otherwise it is omitted.  Since every
/// occurrence is just a text scan we include all of them — the caller can
/// filter by inspecting whether the result overlaps the definition range.
pub(super) fn compute_references(
    text: &str,
    uri: &Uri,
    position: Position,
) -> Option<Vec<Location>> {
    let word = word_at_position(text, position)?;
    Some(find_all_references(text, uri, &word))
}

/// Scan `text` for every word-boundary occurrence of `name` and return them
/// as LSP `Location` values within `uri`.
pub(super) fn find_all_references(text: &str, uri: &Uri, name: &str) -> Vec<Location> {
    let mut locations: Vec<Location> = Vec::new();
    let name_bytes = name.as_bytes();
    let len = name.len();
    let bytes = text.as_bytes();

    let mut i = 0usize;
    while i + len <= bytes.len() {
        if bytes[i..i + len] == *name_bytes {
            // Check word boundaries.
            let left_ok = i == 0 || !is_ident_byte(bytes[i - 1]);
            let right_ok = i + len == bytes.len() || !is_ident_byte(bytes[i + len]);
            if left_ok && right_ok {
                let (line1, col1) = offset_to_line_position(text, i);
                let start = Position {
                    line: line1.saturating_sub(1) as u32,
                    character: col1.saturating_sub(1) as u32,
                };
                let end = Position {
                    line: start.line,
                    character: start.character + len as u32,
                };
                locations.push(Location {
                    uri: uri.clone(),
                    range: Range { start, end },
                });
            }
        }
        i += 1;
    }
    locations
}

/// Rename every occurrence of the identifier under `position` to `new_name`,
/// returning a `WorkspaceEdit` that atomically updates all reference sites.
///
/// Builds directly on [`find_all_references`]: every word-boundary match of
/// the old name is replaced.
pub(super) fn compute_rename(
    text: &str,
    uri: &Uri,
    position: Position,
    new_name: &str,
) -> Option<WorkspaceEdit> {
    let word = word_at_position(text, position)?;
    let locations = find_all_references(text, uri, &word);
    if locations.is_empty() {
        return None;
    }

    // Build TextEdits — one per reference site.
    let edits: Vec<TextEdit> = locations
        .into_iter()
        .map(|loc| TextEdit {
            range: loc.range,
            new_text: new_name.to_string(),
        })
        .collect();

    // Uri contains interior mutability (Arc internals) but is used as a read-only key here.
    #[allow(clippy::mutable_key_type)]
    let mut changes = std::collections::HashMap::new();
    changes.insert(uri.clone(), edits);

    Some(WorkspaceEdit {
        changes: Some(changes),
        document_changes: None,
        change_annotations: None,
    })
}

/// Jump to the definition of the function whose name is under `position`.
///
/// For functions defined in the current file `uri` is reused directly.
/// For functions loaded from a dependency file the full source path stored
/// in [`FunctionInfo::source_path`] is used to construct the target URI and
/// the byte offset is resolved against that file's text.
pub(super) fn compute_definition(
    text: &str,
    uri: &Uri,
    position: Position,
) -> Option<lsp_types::GotoDefinitionResponse> {
    use crate::error_checker::offset_to_line_position;
    use crate::parser::parse_domain_model;
    use crate::runtime::compile_from_domain_model;

    use super::util::path_to_uri;

    let word = word_at_position(text, position)?;

    // Compile — fail gracefully if the document currently has errors.
    let model = parse_domain_model(text).ok()?;
    let program = compile_from_domain_model(&model).ok()?;

    let info = program
        .function_infos()
        .into_iter()
        .find(|f| f.name == word)?;

    // Resolve the target file and its text.
    let (def_text, def_uri): (String, Uri) = if let Some(path) = &info.source_path {
        let content = std::fs::read_to_string(path).ok()?;
        let file_uri = path_to_uri(path)?;
        (content, file_uri)
    } else {
        (text.to_string(), uri.clone())
    };

    // Convert the byte offset of the `func` keyword to an LSP position.
    let (line1, col1) = offset_to_line_position(&def_text, info.offset);
    let pos = Position {
        line: line1.saturating_sub(1) as u32,
        character: col1.saturating_sub(1) as u32,
    };

    Some(lsp_types::GotoDefinitionResponse::Scalar(Location {
        uri: def_uri,
        range: Range {
            start: pos,
            end: pos,
        },
    }))
}
