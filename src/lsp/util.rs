// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Shared LSP utilities.
//!
//! Provides byte-offset ↔ [`Position`] conversion ([`offset_to_line_col`],
//! [`position_to_offset`]), word extraction at the cursor ([`word_at_position`]),
//! URI path helpers, and percent-decoding ([`percent_decode_uri_path`]).

use lsp_types::{Position, Uri};

/// Convert a byte offset in `source` to a (line, column) pair, both 0-based.
pub(super) fn offset_to_line_col(source: &str, offset: usize) -> (u32, u32) {
    let clamped = offset.min(source.len());
    let prefix = &source[..clamped];
    let line = prefix.bytes().filter(|&b| b == b'\n').count() as u32;
    let col = prefix
        .rfind('\n')
        .map(|nl| clamped - nl - 1)
        .unwrap_or(clamped) as u32;
    (line, col)
}

/// Convert an LSP `Position` (0-based line + 0-based UTF-16 character) to a
/// byte offset in `text`.  For the ASCII / common-Unicode case (no surrogate
/// pairs) UTF-16 code-unit count equals Unicode scalar count, so we treat the
/// column as a character index.
pub(super) fn position_to_offset(text: &str, position: Position) -> usize {
    let target_line = position.line as usize;
    let target_col = position.character as usize;
    let mut offset = 0usize;

    for (line_idx, line) in text.split('\n').enumerate() {
        if line_idx == target_line {
            // Clamp column to actual line length (in bytes for ASCII).
            return offset + target_col.min(line.len());
        }
        offset += line.len() + 1; // +1 for the '\n'
    }
    offset
}

/// Return the identifier word that the cursor is on (or immediately before).
/// Returns `None` when the cursor is on whitespace or punctuation.
pub(super) fn word_at_position(text: &str, position: Position) -> Option<String> {
    let offset = position_to_offset(text, position);
    let bytes = text.as_bytes();

    // Scan backward to the start of the word.
    let mut start = offset.min(bytes.len());
    while start > 0 && is_ident_byte(bytes[start - 1]) {
        start -= 1;
    }

    // Scan forward to the end of the word.
    let mut end = offset.min(bytes.len());
    while end < bytes.len() && is_ident_byte(bytes[end]) {
        end += 1;
    }

    if start == end {
        return None;
    }
    let word = &text[start..end];
    if word.is_empty() {
        None
    } else {
        Some(word.to_string())
    }
}

#[inline]
pub(super) fn is_ident_byte(b: u8) -> bool {
    b.is_ascii_alphanumeric() || b == b'_'
}

/// Minimal percent-decoding for file URIs (handles `%20` etc.).
pub(super) fn percent_decode_uri_path(s: &str) -> String {
    let mut out = String::with_capacity(s.len());
    let bytes = s.as_bytes();
    let mut i = 0;
    while i < bytes.len() {
        if bytes[i] == b'%' && i + 2 < bytes.len() {
            if let Ok(hex) = std::str::from_utf8(&bytes[i + 1..i + 3]) {
                if let Ok(byte) = u8::from_str_radix(hex, 16) {
                    out.push(byte as char);
                    i += 3;
                    continue;
                }
            }
        }
        out.push(bytes[i] as char);
        i += 1;
    }
    out
}

/// Convert a filesystem path string to an LSP `Uri` (`file://…`).
pub(super) fn path_to_uri(path: &str) -> Option<Uri> {
    let p = std::path::Path::new(path);
    let abs = if p.is_absolute() {
        p.to_path_buf()
    } else {
        std::env::current_dir().ok()?.join(p)
    };
    // Build a file:// URI.  On Unix abs starts with '/' so this yields
    // "file:///path/…" which is the canonical three-slash form.
    format!("file://{}", abs.display()).parse::<Uri>().ok()
}
