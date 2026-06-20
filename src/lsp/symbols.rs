// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! `textDocument/documentSymbol` — document outline provider.
//!
//! [`compute_document_symbols`] scans the source text and returns a flat list of
//! navigable symbols: `func` declarations, `lang` templates, and `set` model
//! entities, each mapped to the appropriate [`lsp_types::SymbolKind`].

use lsp_types::{
    DocumentSymbolResponse, Location, Position, Range, SymbolInformation, SymbolKind, Uri,
};

/// Produce a flat list of navigable symbols for the "outline" panel.
///
/// Scans the source text for:
/// - `func NAME(` — TLang helper functions → `SymbolKind::Function`
/// - `lang [LANG] NAME(` — TLang templates → `SymbolKind::Module`
/// - `set NAME {` / `set NAME ext` — model entities → `SymbolKind::Struct`
#[allow(deprecated)] // SymbolInformation::deprecated field is required but deprecated
pub(super) fn compute_document_symbols(text: &str, uri: &Uri) -> DocumentSymbolResponse {
    let mut symbols: Vec<SymbolInformation> = Vec::new();

    for (line_idx, line) in text.lines().enumerate() {
        let trimmed = line.trim_start();
        let indent = line.len() - trimmed.len();

        // --- func NAME( ---
        if let Some(rest) = trimmed.strip_prefix("func ") {
            if let Some(name) = parse_ident_before_paren(rest) {
                let start = Position {
                    line: line_idx as u32,
                    character: indent as u32,
                };
                let end = Position {
                    line: line_idx as u32,
                    character: (indent + line.trim_end().len()) as u32,
                };
                symbols.push(SymbolInformation {
                    name,
                    kind: SymbolKind::FUNCTION,
                    tags: None,
                    deprecated: None,
                    location: Location {
                        uri: uri.clone(),
                        range: Range { start, end },
                    },
                    container_name: None,
                });
            }
        }
        // --- lang [LANG] NAME( ---
        else if let Some(rest) = trimmed.strip_prefix("lang ") {
            // Skip over "[LANG] " to reach the template name.
            if let Some(after_bracket) = rest.find(']') {
                let name_part = rest[after_bracket + 1..].trim_start();
                if let Some(name) = parse_ident_before_paren(name_part) {
                    let start = Position {
                        line: line_idx as u32,
                        character: indent as u32,
                    };
                    let end = Position {
                        line: line_idx as u32,
                        character: (indent + line.trim_end().len()) as u32,
                    };
                    symbols.push(SymbolInformation {
                        name,
                        kind: SymbolKind::MODULE,
                        tags: None,
                        deprecated: None,
                        location: Location {
                            uri: uri.clone(),
                            range: Range { start, end },
                        },
                        container_name: Some("template".to_string()),
                    });
                }
            }
        }
        // --- data [LANG, …] NAME( ---
        else if let Some(rest) = trimmed.strip_prefix("data ") {
            // Skip over "[LANG, …] " to reach the template name.
            if let Some(after_bracket) = rest.find(']') {
                let name_part = rest[after_bracket + 1..].trim_start();
                if let Some(name) = parse_ident_before_paren(name_part) {
                    // Extract the language list for the container_name label.
                    let langs_str = rest
                        .find('[')
                        .map(|lb| {
                            let inner = &rest[lb + 1..after_bracket];
                            inner.trim().to_string()
                        })
                        .unwrap_or_default();
                    let start = Position {
                        line: line_idx as u32,
                        character: indent as u32,
                    };
                    let end = Position {
                        line: line_idx as u32,
                        character: (indent + line.trim_end().len()) as u32,
                    };
                    symbols.push(SymbolInformation {
                        name,
                        kind: SymbolKind::OBJECT,
                        tags: None,
                        deprecated: None,
                        location: Location {
                            uri: uri.clone(),
                            range: Range { start, end },
                        },
                        container_name: Some(format!("data template [{langs_str}]")),
                    });
                }
            }
        }
        // --- doc [LANG, …] NAME( ---
        else if let Some(rest) = trimmed.strip_prefix("doc ") {
            if let Some(after_bracket) = rest.find(']') {
                let name_part = rest[after_bracket + 1..].trim_start();
                if let Some(name) = parse_ident_before_paren(name_part) {
                    let langs_str = rest
                        .find('[')
                        .map(|lb| {
                            let inner = &rest[lb + 1..after_bracket];
                            inner.trim().to_string()
                        })
                        .unwrap_or_default();
                    let start = Position {
                        line: line_idx as u32,
                        character: indent as u32,
                    };
                    let end = Position {
                        line: line_idx as u32,
                        character: (indent + line.trim_end().len()) as u32,
                    };
                    symbols.push(SymbolInformation {
                        name,
                        kind: SymbolKind::FUNCTION,
                        tags: None,
                        deprecated: None,
                        location: Location {
                            uri: uri.clone(),
                            range: Range { start, end },
                        },
                        container_name: Some(format!("doc template [{langs_str}]")),
                    });
                }
            }
        }
        // --- style [LANG, …] NAME( ---
        else if let Some(rest) = trimmed.strip_prefix("style ") {
            if let Some(after_bracket) = rest.find(']') {
                let name_part = rest[after_bracket + 1..].trim_start();
                if let Some(name) = parse_ident_before_paren(name_part) {
                    let langs_str = rest
                        .find('[')
                        .map(|lb| {
                            let inner = &rest[lb + 1..after_bracket];
                            inner.trim().to_string()
                        })
                        .unwrap_or_default();
                    let start = Position {
                        line: line_idx as u32,
                        character: indent as u32,
                    };
                    let end = Position {
                        line: line_idx as u32,
                        character: (indent + line.trim_end().len()) as u32,
                    };
                    symbols.push(SymbolInformation {
                        name,
                        kind: SymbolKind::FUNCTION,
                        tags: None,
                        deprecated: None,
                        location: Location {
                            uri: uri.clone(),
                            range: Range { start, end },
                        },
                        container_name: Some(format!("style template [{langs_str}]")),
                    });
                }
            }
        }
        // --- set NAME { / set NAME ext / set NAME( ---
        else if let Some(rest) = trimmed.strip_prefix("set ") {
            // Model entity name ends at whitespace, '(' or '{'.
            let name: String = rest
                .chars()
                .take_while(|c| c.is_alphanumeric() || *c == '_')
                .collect();
            if !name.is_empty() {
                let start = Position {
                    line: line_idx as u32,
                    character: indent as u32,
                };
                let end = Position {
                    line: line_idx as u32,
                    character: (indent + line.trim_end().len()) as u32,
                };
                // Build a container_name that includes parent(s) (`: A : B`)
                // and whether the entity has constructor params `(…)`.
                let after_name = rest[name.len()..].trim_start();
                let has_params = after_name.starts_with('(');

                // Collect all `: Parent` clauses from the declaration line.
                // They may appear after the optional `(…)` param list and
                // before the `>>` output declaration or `{` body.
                let parents: Vec<String> = {
                    let mut ps = Vec::new();
                    let mut scan = after_name;
                    // Skip over a `(…)` param list if present.
                    if scan.starts_with('(') {
                        let mut depth = 0usize;
                        let mut idx = 0;
                        for (i, c) in scan.char_indices() {
                            match c {
                                '(' => depth += 1,
                                ')' => {
                                    depth -= 1;
                                    if depth == 0 {
                                        idx = i + 1;
                                        break;
                                    }
                                }
                                _ => {}
                            }
                        }
                        scan = scan[idx..].trim_start();
                    }
                    // Each `: Name` segment is a parent.
                    while let Some(rest_colon) = scan.strip_prefix(':') {
                        let rest_colon = rest_colon.trim_start();
                        let parent: String = rest_colon
                            .chars()
                            .take_while(|c| c.is_alphanumeric() || *c == '_')
                            .collect();
                        if parent.is_empty() {
                            break;
                        }
                        ps.push(parent.clone());
                        scan = rest_colon[parent.len()..].trim_start();
                    }
                    ps
                };

                let mut parts: Vec<String> = vec!["model".to_string()];
                if !parents.is_empty() {
                    parts.push(format!("extends {}", parents.join(", ")));
                }
                if has_params {
                    parts.push("has params".to_string());
                }
                let container = parts.join(" • ");
                symbols.push(SymbolInformation {
                    name,
                    kind: SymbolKind::STRUCT,
                    tags: None,
                    deprecated: None,
                    location: Location {
                        uri: uri.clone(),
                        range: Range { start, end },
                    },
                    container_name: Some(container),
                });
            }
        }
    }

    DocumentSymbolResponse::Flat(symbols)
}

/// Extract an identifier that appears at the start of `s` and is immediately
/// followed by `(`.  Returns `None` when no such identifier exists.
pub(super) fn parse_ident_before_paren(s: &str) -> Option<String> {
    let name: String = s
        .chars()
        .take_while(|c| c.is_alphanumeric() || *c == '_')
        .collect();
    if name.is_empty() {
        return None;
    }
    // The character right after the name must be `(` (possibly with no space
    // in between, or the name itself could be the whole identifier).
    let after = s[name.len()..].trim_start();
    if after.starts_with('(') {
        Some(name)
    } else {
        None
    }
}
