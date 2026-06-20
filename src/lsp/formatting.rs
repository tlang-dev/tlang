// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! `textDocument/formatting` — whole-document formatter.
//!
//! [`compute_formatting`] parses the document, runs it through the TLang
//! canonical formatter, and returns a single full-document `TextEdit`. If the
//! document already matches canonical form or cannot be parsed, no edit is
//! emitted.

use lsp_server::{RequestId, Response};
use lsp_types::{Position, Range, TextEdit};

use crate::formatter::format_model;
use crate::parser::parse_domain_model;

use super::diagnostics::end_position;

/// Compute full-document formatting edits.
pub(super) fn compute_formatting(id: &RequestId, text: &str) -> Response {
    match parse_domain_model(text) {
        Ok(model) => {
            let formatted = format_model(&model);
            if formatted == text {
                // Already canonical — return empty edits.
                return Response::new_ok(
                    id.clone(),
                    serde_json::to_value::<Vec<TextEdit>>(vec![]).unwrap(),
                );
            }
            // Replace the whole document with the formatted version.
            let end = end_position(text);
            let edit = TextEdit {
                range: Range {
                    start: Position {
                        line: 0,
                        character: 0,
                    },
                    end,
                },
                new_text: formatted,
            };
            Response::new_ok(id.clone(), serde_json::to_value(vec![edit]).unwrap())
        }
        // If the document has parse errors we can't format; return null.
        Err(_) => Response::new_ok(id.clone(), serde_json::Value::Null),
    }
}
