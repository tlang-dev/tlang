// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! `textDocument/semanticTokens/full` — syntax-highlight token emitter.
//!
//! [`compute_semantic_tokens`] walks the parsed AST and emits LSP delta-encoded
//! semantic tokens for keywords, identifiers, `<[ ... ]>` include directives,
//! and `${ ... }` interpolations inside template bodies.

use lsp_types::{SemanticToken, SemanticTokens};

use crate::parser::parse_domain_model;

use super::util::{is_ident_byte, offset_to_line_col};

/// Semantic token type indices — must match the order declared in the legend.
pub(super) const ST_KEYWORD: u32 = 0;
pub(super) const ST_MACRO: u32 = 1;
pub(super) const ST_VARIABLE: u32 = 2;
pub(super) const ST_NAMESPACE: u32 = 3;
pub(super) const ST_FUNCTION: u32 = 4;
pub(super) const ST_PARAMETER: u32 = 5;
pub(super) const ST_TYPE: u32 = 6;

/// TLang keywords that appear inside template bodies.
const TMPL_KEYWORDS: &[&str] = &[
    "impl", "func", "var", "if", "else", "match", "case", "default", "for", "while", "do",
    "return", "pkg", "use", "comment", "raw",
];

/// Keywords that appear inside data template bodies.
const DATA_KEYWORDS: &[&str] = &["true", "false"];

/// Compute semantic tokens for an entire TLang source document.
///
/// The implementation focuses on template block bodies where the tree-sitter
/// grammar treats content as opaque `raw_text`.  It highlights:
///
/// - TLang structural keywords (`impl`, `func`, `var`, `if`, `for`, `while`,
///   `return`, `pkg`, `use`) when used at word boundaries.
/// - `data` keyword and `[lang, …]` language-list tokens in data templates.
/// - `<[ ... ]>` include directives — both delimiters and the call expression.
/// - `${ ... }` template variable interpolations.
///
/// The returned token array uses the LSP delta-encoding: each token stores
/// `(deltaLine, deltaStartChar, length, tokenType, tokenModifiers)`.
pub(super) fn compute_semantic_tokens(source: &str) -> SemanticTokens {
    let model = match parse_domain_model(source) {
        Ok(m) => m,
        Err(_) => {
            return SemanticTokens {
                result_id: None,
                data: vec![],
            };
        }
    };

    // Collect raw (byte-offset, length, type) triples from every template body.
    let mut raw: Vec<(usize, usize, u32)> = Vec::new();

    for block in &model.body {
        match block {
            crate::ast::DomainBlock::Template(t) => {
                let (body_text, is_spec) = match &t.content {
                    crate::ast::TemplateContent::Full(b) => (b.as_str(), false),
                    crate::ast::TemplateContent::Specialized(b) => (b.as_str(), true),
                };

                // ── `lang` keyword token ────────────────────────────────────
                if t.tmpl_start > 0 || source.starts_with("lang") {
                    raw.push((t.tmpl_start, 4, ST_KEYWORD));
                }

                // ── `[language]` attribute token ────────────────────────────
                if t.lang_offset > 0 {
                    let before = &source[..t.lang_offset];
                    if let Some(bracket_pos) = before.rfind('[') {
                        let lang_len = t.lang.len();
                        let token_len = 1 + lang_len + 1;
                        raw.push((bracket_pos, token_len, ST_NAMESPACE));
                    }
                }

                let search_from = t.lang_offset;
                let body_offset = match source[search_from..].find(body_text) {
                    Some(rel) => search_from + rel,
                    None => continue,
                };
                let _ = is_spec;

                scan_tmpl_body(source, body_offset, body_offset + body_text.len(), &mut raw);
            }

            crate::ast::DomainBlock::Data(d) => {
                // ── `data` keyword token ────────────────────────────────────
                if d.data_start > 0 || source.starts_with("data") {
                    raw.push((d.data_start, 4, ST_KEYWORD));
                }

                // ── `[lang, lang, …]` language list token ──────────────────
                // lang_offset is the offset of the first language identifier.
                // Walk back to find `[`, walk forward to find `]`.
                let close_bracket_end = if d.lang_offset > 0 {
                    let before = &source[..d.lang_offset];
                    if let Some(bracket_pos) = before.rfind('[') {
                        // Find the closing `]` from lang_offset onward.
                        if let Some(rel_close) = source[d.lang_offset..].find(']') {
                            let close_pos = d.lang_offset + rel_close;
                            let token_len = close_pos - bracket_pos + 1;
                            raw.push((bracket_pos, token_len, ST_NAMESPACE));
                            close_pos + 1
                        } else {
                            d.lang_offset
                        }
                    } else {
                        d.lang_offset
                    }
                } else {
                    d.data_start + 4
                };

                // ── template name token ─────────────────────────────────────
                // The name appears between `]` and `(` on the header line.
                if !d.name.is_empty() {
                    let search_from_name = close_bracket_end;
                    if let Some(rel) = source[search_from_name..].find(d.name.as_str()) {
                        let name_offset = search_from_name + rel;
                        // Confirm it's a word boundary (not part of a longer ident).
                        let before_ok =
                            name_offset == 0 || !is_ident_byte(source.as_bytes()[name_offset - 1]);
                        let after_ok = {
                            let after = name_offset + d.name.len();
                            after >= source.len() || !is_ident_byte(source.as_bytes()[after])
                        };
                        if before_ok && after_ok {
                            raw.push((name_offset, d.name.len(), ST_FUNCTION));

                            // ── parameter list: `(name: Type, …)` ──────────
                            let after_name = name_offset + d.name.len();
                            if let Some(rel_open) = source[after_name..].find('(') {
                                let paren_open = after_name + rel_open + 1;
                                if let Some(rel_close) = source[paren_open..].find(')') {
                                    let paren_close = paren_open + rel_close;
                                    scan_data_params(source, paren_open, paren_close, &mut raw);
                                }
                            }
                        }
                    }
                }

                // ── data template body ──────────────────────────────────────
                let search_from = d.lang_offset;
                let body_offset = match source[search_from..].find(&d.content) {
                    Some(rel) => search_from + rel,
                    None => continue,
                };
                scan_data_body(source, body_offset, body_offset + d.content.len(), &mut raw);
            }

            crate::ast::DomainBlock::Cmd(c) => {
                // ── `cmd` keyword token ────────────────────────────────────
                if c.cmd_start > 0 || source.starts_with("cmd") {
                    raw.push((c.cmd_start, 3, ST_KEYWORD));
                }

                // ── `[lang, lang, …]` language list token ──────────────────
                if c.lang_offset > 0 {
                    let before = &source[..c.lang_offset];
                    if let Some(bracket_pos) = before.rfind('[') {
                        if let Some(rel_close) = source[c.lang_offset..].find(']') {
                            let close_pos = c.lang_offset + rel_close;
                            let token_len = close_pos - bracket_pos + 1;
                            raw.push((bracket_pos, token_len, ST_NAMESPACE));
                        }
                    }
                }

                // ── cmd template body ──────────────────────────────────────
                let search_from = c.lang_offset;
                let body_offset = match source[search_from..].find(&c.content) {
                    Some(rel) => search_from + rel,
                    None => continue,
                };
                scan_data_body(source, body_offset, body_offset + c.content.len(), &mut raw);
            }

            crate::ast::DomainBlock::Raw(r) => {
                // ── `raw` keyword token ────────────────────────────────────
                if r.raw_start > 0 || source.starts_with("raw") {
                    raw.push((r.raw_start, 3, ST_KEYWORD));
                }

                // ── `[AsIs]` / `[Replaced]` variant token ─────────────────
                if r.variant_offset > 0 {
                    let before = &source[..r.variant_offset];
                    if let Some(bracket_pos) = before.rfind('[') {
                        if let Some(rel_close) = source[r.variant_offset..].find(']') {
                            let close_pos = r.variant_offset + rel_close;
                            let token_len = close_pos - bracket_pos + 1;
                            raw.push((bracket_pos, token_len, ST_NAMESPACE));
                        }
                    }
                }

                // ── template name token ─────────────────────────────────────
                // The name appears after the closing `]` on the header line.
                let search_from_name = r.variant_offset;
                if !r.name.is_empty() {
                    if let Some(rel) = source[search_from_name..].find(r.name.as_str()) {
                        let name_offset = search_from_name + rel;
                        let before_ok =
                            name_offset == 0 || !is_ident_byte(source.as_bytes()[name_offset - 1]);
                        let after_ok = {
                            let after = name_offset + r.name.len();
                            after >= source.len() || !is_ident_byte(source.as_bytes()[after])
                        };
                        if before_ok && after_ok {
                            raw.push((name_offset, r.name.len(), ST_FUNCTION));
                        }
                    }
                }

                // ── raw body: intentionally left unstyled ──────────────────
                // The whole point of `raw` is that the LSP does not touch the
                // body content — no colouring, no indentation hints.
            }

            crate::ast::DomainBlock::Doc(d) => {
                // ── `doc` keyword token ────────────────────────────────────
                if d.doc_start > 0 || source.starts_with("doc") {
                    raw.push((d.doc_start, 3, ST_KEYWORD));
                }

                // ── `[lang, lang, …]` language list token ──────────────────
                let close_bracket_end = if d.lang_offset > 0 {
                    let before = &source[..d.lang_offset];
                    if let Some(bracket_pos) = before.rfind('[') {
                        if let Some(rel_close) = source[d.lang_offset..].find(']') {
                            let close_pos = d.lang_offset + rel_close;
                            let token_len = close_pos - bracket_pos + 1;
                            raw.push((bracket_pos, token_len, ST_NAMESPACE));
                            close_pos + 1
                        } else {
                            d.lang_offset
                        }
                    } else {
                        d.lang_offset
                    }
                } else {
                    d.doc_start + 3
                };

                // ── template name token ─────────────────────────────────────
                if !d.name.is_empty() {
                    let search_from_name = close_bracket_end;
                    if let Some(rel) = source[search_from_name..].find(d.name.as_str()) {
                        let name_offset = search_from_name + rel;
                        let before_ok =
                            name_offset == 0 || !is_ident_byte(source.as_bytes()[name_offset - 1]);
                        let after_ok = {
                            let after = name_offset + d.name.len();
                            after >= source.len() || !is_ident_byte(source.as_bytes()[after])
                        };
                        if before_ok && after_ok {
                            raw.push((name_offset, d.name.len(), ST_FUNCTION));

                            // ── parameter list: `(name: Type, …)` ──────────
                            let after_name = name_offset + d.name.len();
                            if let Some(rel_open) = source[after_name..].find('(') {
                                let paren_open = after_name + rel_open + 1;
                                if let Some(rel_close) = source[paren_open..].find(')') {
                                    let paren_close = paren_open + rel_close;
                                    scan_data_params(source, paren_open, paren_close, &mut raw);
                                }
                            }
                        }
                    }
                }

                // ── doc template body ──────────────────────────────────────
                let search_from = d.lang_offset;
                let body_offset = match source[search_from..].find(&d.content) {
                    Some(rel) => search_from + rel,
                    None => continue,
                };
                scan_data_body(source, body_offset, body_offset + d.content.len(), &mut raw);
            }

            crate::ast::DomainBlock::Style(s) => {
                // ── `style` keyword token ──────────────────────────────────
                if s.style_start > 0 || source.starts_with("style") {
                    raw.push((s.style_start, 5, ST_KEYWORD));
                }

                // ── `[lang, lang, …]` language list token ──────────────────
                let close_bracket_end = if s.lang_offset > 0 {
                    let before = &source[..s.lang_offset];
                    if let Some(bracket_pos) = before.rfind('[') {
                        if let Some(rel_close) = source[s.lang_offset..].find(']') {
                            let close_pos = s.lang_offset + rel_close;
                            let token_len = close_pos - bracket_pos + 1;
                            raw.push((bracket_pos, token_len, ST_NAMESPACE));
                            close_pos + 1
                        } else {
                            s.lang_offset
                        }
                    } else {
                        s.lang_offset
                    }
                } else {
                    s.style_start + 5
                };

                // ── template name token ─────────────────────────────────────
                if !s.name.is_empty() {
                    let search_from_name = close_bracket_end;
                    if let Some(rel) = source[search_from_name..].find(s.name.as_str()) {
                        let name_offset = search_from_name + rel;
                        let before_ok =
                            name_offset == 0 || !is_ident_byte(source.as_bytes()[name_offset - 1]);
                        let after_ok = {
                            let after = name_offset + s.name.len();
                            after >= source.len() || !is_ident_byte(source.as_bytes()[after])
                        };
                        if before_ok && after_ok {
                            raw.push((name_offset, s.name.len(), ST_FUNCTION));

                            // ── parameter list: `(name: Type, …)` ──────────
                            let after_name = name_offset + s.name.len();
                            if let Some(rel_open) = source[after_name..].find('(') {
                                let paren_open = after_name + rel_open + 1;
                                if let Some(rel_close) = source[paren_open..].find(')') {
                                    let paren_close = paren_open + rel_close;
                                    scan_data_params(source, paren_open, paren_close, &mut raw);
                                }
                            }
                        }
                    }
                }

                // ── style template body: highlight interpolations ──────────
                // Like `doc`, we scan the body for `${…}` interpolations and
                // `<[…]>` includes while leaving the CSS/SCSS/JSON structure
                // itself unstyled so the editor does not misinterpret it.
                let search_from = s.lang_offset;
                let body_offset = match source[search_from..].find(&s.content) {
                    Some(rel) => search_from + rel,
                    None => continue,
                };
                scan_data_body(source, body_offset, body_offset + s.content.len(), &mut raw);
            }

            _ => {}
        }
    }

    // Sort by byte offset (they should already be in order, but be safe).
    raw.sort_unstable_by_key(|(off, _, _)| *off);

    // Convert absolute byte offsets → LSP delta-encoded token array.
    let lines: Vec<&str> = source.split('\n').collect();
    let mut data: Vec<SemanticToken> = Vec::with_capacity(raw.len());
    let mut prev_line: u32 = 0;
    let mut prev_start: u32 = 0;

    for (byte_off, length, token_type) in raw {
        let (line, col) = offset_to_line_col(source, byte_off);
        let delta_line = line - prev_line;
        let delta_start = if delta_line == 0 {
            col - prev_start
        } else {
            col
        };
        // Clamp length to what fits on the line.
        let line_text = lines.get(line as usize).copied().unwrap_or("");
        let max_len = (line_text.len().saturating_sub(col as usize)) as u32;
        let len = (length as u32).min(max_len);
        if len == 0 {
            continue;
        }
        data.push(SemanticToken {
            delta_line,
            delta_start,
            length: len,
            token_type,
            token_modifiers_bitset: 0,
        });
        prev_line = line;
        prev_start = col;
    }

    SemanticTokens {
        result_id: None,
        data,
    }
}

/// Scan a data-template parameter list `name: Type, name: Type, …` and emit
/// `ST_PARAMETER` tokens for each parameter name and `ST_TYPE` tokens for each
/// type name.
pub(super) fn scan_data_params(
    source: &str,
    start: usize,
    end: usize,
    out: &mut Vec<(usize, usize, u32)>,
) {
    let bytes = source.as_bytes();
    let mut i = start;
    while i < end {
        // Skip whitespace and commas.
        while i < end
            && (bytes[i] == b' '
                || bytes[i] == b'\t'
                || bytes[i] == b'\n'
                || bytes[i] == b'\r'
                || bytes[i] == b',')
        {
            i += 1;
        }
        if i >= end {
            break;
        }
        // Read parameter name.
        if !is_ident_byte(bytes[i]) {
            i += 1;
            continue;
        }
        let name_start = i;
        while i < end && is_ident_byte(bytes[i]) {
            i += 1;
        }
        let name_end = i;
        // Skip whitespace.
        while i < end && (bytes[i] == b' ' || bytes[i] == b'\t') {
            i += 1;
        }
        if i < end && bytes[i] == b':' {
            // Emit the parameter name.
            out.push((name_start, name_end - name_start, ST_PARAMETER));
            i += 1; // skip `:`
            // Skip whitespace.
            while i < end && (bytes[i] == b' ' || bytes[i] == b'\t') {
                i += 1;
            }
            // Read type name.
            if i < end && is_ident_byte(bytes[i]) {
                let type_start = i;
                while i < end && is_ident_byte(bytes[i]) {
                    i += 1;
                }
                out.push((type_start, i - type_start, ST_TYPE));
            }
        }
    }
}

pub(super) fn scan_data_body(
    source: &str,
    start: usize,
    end: usize,
    out: &mut Vec<(usize, usize, u32)>,
) {
    let bytes = source.as_bytes();
    let mut i = start;

    while i < end {
        let ch = bytes[i];
        match ch {
            // ── String literals ─────────────────────────────────────────────
            b'"' | b'\'' => {
                let quote = ch;
                let str_start = i;
                i += 1;
                while i < end {
                    let c = bytes[i];
                    i += 1;
                    if c == b'\\' {
                        if i < end {
                            i += 1;
                        }
                    } else if c == quote {
                        break;
                    }
                }
                // Highlight the entire string literal as a variable/string token.
                out.push((str_start, i - str_start, ST_VARIABLE));
            }

            // ── ${ … } interpolation ────────────────────────────────────────
            b'$' if i + 1 < end && bytes[i + 1] == b'{' => {
                let tok_start = i;
                i += 2;
                let mut depth = 1usize;
                while i < end && depth > 0 {
                    match bytes[i] {
                        b'{' => {
                            depth += 1;
                            i += 1;
                        }
                        b'}' => {
                            depth -= 1;
                            i += 1;
                        }
                        _ => {
                            i += 1;
                        }
                    }
                }
                out.push((tok_start, i - tok_start, ST_VARIABLE));
            }

            // ── <[ … ]> include directive ───────────────────────────────────
            b'<' if i + 1 < end && bytes[i + 1] == b'[' => {
                let tok_start = i;
                i += 2;
                while i + 1 < end && !(bytes[i] == b']' && bytes[i + 1] == b'>') {
                    i += 1;
                }
                if i + 1 < end {
                    i += 2;
                }
                out.push((tok_start, i - tok_start, ST_MACRO));
            }

            // ── Identifiers — tag calls, attribute keys, and keywords ────────
            b'a'..=b'z' | b'A'..=b'Z' | b'_' => {
                let at_word_start = i == start || !is_ident_byte(bytes[i - 1]);
                let word_start = i;
                while i < end && is_ident_byte(bytes[i]) {
                    i += 1;
                }
                let word = &source[word_start..i];

                // Skip whitespace to peek at next significant byte.
                let mut j = i;
                while j < end && (bytes[j] == b' ' || bytes[j] == b'\t') {
                    j += 1;
                }
                let next_byte = bytes.get(j).copied();

                if at_word_start && DATA_KEYWORDS.contains(&word) {
                    // `true` / `false` → keyword token.
                    out.push((word_start, word.len(), ST_KEYWORD));
                } else if next_byte == Some(b'(') {
                    // Tag/element call name e.g. `div(`, `label(`, `select(` → function token.
                    out.push((word_start, word.len(), ST_FUNCTION));
                } else if next_byte == Some(b':') {
                    // Attribute key immediately followed by `:` → namespace token.
                    out.push((word_start, word.len(), ST_NAMESPACE));
                }
            }

            _ => {
                i += 1;
            }
        }
    }
}

/// Walk the byte range `[start, end)` in `source` (a template body including
/// the outer `{…}`) and push raw `(byte_offset, length, token_type)` entries
/// into `out`.
///
/// The scanner is brace-depth–aware so it recurses through nested blocks
/// while skipping string literals and `${…}` interpolations that are not
/// TLang directives.
pub(super) fn scan_tmpl_body(
    source: &str,
    start: usize,
    end: usize,
    out: &mut Vec<(usize, usize, u32)>,
) {
    let bytes = source.as_bytes();
    let mut i = start;
    // Track whether we are at the start of a "token position" (immediately
    // after whitespace / a newline / an opening brace).
    // We emit keyword tokens only when the keyword appears as the first
    // identifier-like token on a fresh statement, not in the middle of
    // arbitrary target-language text.  We approximate this by checking
    // whether the byte immediately before the keyword is a word char.
    while i < end {
        let ch = bytes[i];
        match ch {
            // ── String literals — skip over, no tokens inside ──────────
            b'"' | b'\'' => {
                i += 1;
                let quote = ch;
                while i < end {
                    let c = bytes[i];
                    i += 1;
                    if c == b'\\' {
                        if i < end {
                            i += 1;
                        }
                    } else if c == quote {
                        break;
                    }
                }
            }

            // ── ${ expr } interpolation ─────────────────────────────────
            b'$' if i + 1 < end && bytes[i + 1] == b'{' => {
                // Highlight from `$` through the closing `}`.
                let tok_start = i;
                i += 2; // skip `${`
                let mut depth = 1usize;
                while i < end && depth > 0 {
                    match bytes[i] {
                        b'{' => {
                            depth += 1;
                            i += 1;
                        }
                        b'}' => {
                            depth -= 1;
                            i += 1;
                        }
                        _ => {
                            i += 1;
                        }
                    }
                }
                out.push((tok_start, i - tok_start, ST_VARIABLE));
            }

            // ── <[ expr ]> include ──────────────────────────────────────
            b'<' if i + 1 < end && bytes[i + 1] == b'[' => {
                let tok_start = i;
                i += 2; // skip `<[`
                // Find the matching `]>`.
                while i + 1 < end && !(bytes[i] == b']' && bytes[i + 1] == b'>') {
                    i += 1;
                }
                if i + 1 < end {
                    i += 2; // skip `]>`
                }
                out.push((tok_start, i - tok_start, ST_MACRO));
            }

            // ── Possible TLang keyword ──────────────────────────────────
            b'a'..=b'z' | b'A'..=b'Z' | b'_' => {
                // Only emit a keyword token when the preceding character is
                // NOT an identifier character (word-boundary check).
                let at_word_start = i == start || !is_ident_byte(bytes[i - 1]);

                // Find the end of this identifier.
                let word_start = i;
                while i < end && is_ident_byte(bytes[i]) {
                    i += 1;
                }
                let word = &source[word_start..i];

                if at_word_start && TMPL_KEYWORDS.contains(&word) {
                    out.push((word_start, word.len(), ST_KEYWORD));
                }
            }

            _ => {
                i += 1;
            }
        }
    }
}
