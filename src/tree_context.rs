// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Source-position tracking for model and template AST nodes.
//!
//! [`TreeContext`] records the `(file, line, position)` of a parsed construct
//! and is embedded in AST nodes that need precise error reporting.

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct TreeContext {
    pub file: Option<String>,
    pub line: Option<usize>,
    pub position: Option<usize>,
}

impl TreeContext {
    pub fn from_offset(source: &str, offset: usize) -> Self {
        let bounded = offset.min(source.len());
        let mut line = 1usize;
        let mut position = 1usize;

        for ch in source[..bounded].chars() {
            if ch == '\n' {
                line += 1;
                position = 1;
            } else {
                position += 1;
            }
        }

        Self {
            file: None,
            line: Some(line),
            position: Some(position),
        }
    }
}
