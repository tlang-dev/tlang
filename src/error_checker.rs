// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Comprehensive error checking module for TLang
//!
//! This module provides error types and validation functions for:
//! - Function existence and parameter validation
//! - Variable accessibility checking
//! - Import/use statement validation
//! - Expose keyword validation
//!
//! All errors include file, line, and position context for precise error reporting
//! and LSP integration.

use std::collections::{HashMap, HashSet};
use std::fmt;

use pest::RuleType;
use pest::error::{Error as PestError, ErrorVariant, LineColLocation};

use crate::ast::{
    DomainBlock, DomainHeader, DomainModel, DomainUse, HelperBlock, TemplateBlock, TemplateParam,
};

// ---------------------------------------------------------------------------
// Error Types
// ---------------------------------------------------------------------------

/// A source location with file, line, and position information
#[derive(Debug, Clone, PartialEq, Eq, Hash)]
pub struct SourceLocation {
    /// The file path or identifier (e.g., "<input>" for inline input)
    pub file: String,
    /// 1-based line number
    pub line: usize,
    /// 1-based column position
    pub position: usize,
    /// Byte offset in the source file (0-based)
    pub offset: usize,
}

impl SourceLocation {
    /// Create a new source location
    pub fn new(file: impl Into<String>, line: usize, position: usize, offset: usize) -> Self {
        Self {
            file: file.into(),
            line,
            position,
            offset,
        }
    }

    /// Convert to LSP-compatible 0-based coordinates
    pub fn to_lsp_position(&self) -> (u32, u32) {
        (
            (self.line.saturating_sub(1)) as u32,
            (self.position.saturating_sub(1)) as u32,
        )
    }

    /// Format as a context string for error messages
    pub fn to_context_string(&self) -> String {
        format!(
            "file: {}, line: {}, position: {}",
            self.file, self.line, self.position
        )
    }
}

/// Severity level for errors and warnings
#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord, Hash)]
pub enum ErrorSeverity {
    /// Informational message
    Info,
    /// Warning - non-fatal issue
    Warning,
    /// Error - fatal issue that prevents compilation
    Error,
}

impl fmt::Display for ErrorSeverity {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            ErrorSeverity::Info => write!(f, "info"),
            ErrorSeverity::Warning => write!(f, "warning"),
            ErrorSeverity::Error => write!(f, "error"),
        }
    }
}

/// Category of error for classification and filtering
#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum ErrorCategory {
    /// Syntax parsing errors
    Syntax,
    /// Function-related errors (existence, parameters, types)
    Function,
    /// Variable-related errors (accessibility, scope)
    Variable,
    /// Import/use statement errors
    Import,
    /// Expose/visibility errors
    Visibility,
    /// Type checking errors
    Type,
    /// Model-related errors
    Model,
    /// Template-related errors
    Template,
    /// General compilation errors
    Compilation,
}

impl fmt::Display for ErrorCategory {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            ErrorCategory::Syntax => write!(f, "syntax"),
            ErrorCategory::Function => write!(f, "function"),
            ErrorCategory::Variable => write!(f, "variable"),
            ErrorCategory::Import => write!(f, "import"),
            ErrorCategory::Visibility => write!(f, "visibility"),
            ErrorCategory::Type => write!(f, "type"),
            ErrorCategory::Model => write!(f, "model"),
            ErrorCategory::Template => write!(f, "template"),
            ErrorCategory::Compilation => write!(f, "compilation"),
        }
    }
}

/// A comprehensive error with full context information
#[derive(Debug, Clone, PartialEq)]
pub struct TLangError {
    /// Error message
    pub message: String,
    /// Error category for classification
    pub category: ErrorCategory,
    /// Severity level
    pub severity: ErrorSeverity,
    /// Source location where the error occurred
    pub location: SourceLocation,
    /// Optional related information or suggestions
    pub hints: Vec<String>,
    /// Optional error code for documentation lookup
    pub code: Option<String>,
    /// Length of the token at the error location (for LSP underline range).
    /// 0 means use a single-character fallback.
    pub length: usize,
}

impl TLangError {
    /// Create a new error with the given parameters
    pub fn new(
        message: impl Into<String>,
        category: ErrorCategory,
        severity: ErrorSeverity,
        location: SourceLocation,
    ) -> Self {
        Self {
            message: message.into(),
            category,
            severity,
            location,
            hints: Vec::new(),
            code: None,
            length: 0,
        }
    }

    /// Set the token length for LSP underline range.
    pub fn with_length(mut self, length: usize) -> Self {
        self.length = length;
        self
    }

    /// Add a hint to the error
    pub fn with_hint(mut self, hint: impl Into<String>) -> Self {
        self.hints.push(hint.into());
        self
    }

    /// Add an error code to the error
    pub fn with_code(mut self, code: impl Into<String>) -> Self {
        self.code = Some(code.into());
        self
    }

    /// Format the error for display
    pub fn format(&self) -> String {
        let mut output = format!("[{}] {}: {}", self.severity, self.category, self.message);

        output.push_str(&format!(" ({})", self.location.to_context_string()));

        if !self.hints.is_empty() {
            output.push_str("\n  Hints:");
            for hint in &self.hints {
                output.push_str(&format!("\n    - {}", hint));
            }
        }

        if let Some(code) = &self.code {
            output.push_str(&format!("\n  Code: {}", code));
        }

        output
    }

    /// Format as a simple message with context (compatible with existing error format)
    pub fn format_simple(&self) -> String {
        format!(
            "{} (file: {}, line: {}, position: {})",
            self.message, self.location.file, self.location.line, self.location.position
        )
    }

    /// Convert to LSP Diagnostic
    pub fn to_lsp_diagnostic(&self) -> lsp_types::Diagnostic {
        use lsp_types::{Diagnostic, DiagnosticSeverity, NumberOrString, Position, Range};

        let (line, character) = self.location.to_lsp_position();

        let end_character = if self.length > 0 {
            character + self.length as u32
        } else {
            character + 1
        };

        let range = Range {
            start: Position { line, character },
            end: Position {
                line,
                character: end_character,
            },
        };

        let severity = match self.severity {
            ErrorSeverity::Info => DiagnosticSeverity::INFORMATION,
            ErrorSeverity::Warning => DiagnosticSeverity::WARNING,
            ErrorSeverity::Error => DiagnosticSeverity::ERROR,
        };

        let mut message = self.message.clone();

        // Append hints if present
        if !self.hints.is_empty() {
            message.push_str("\n");
            for hint in &self.hints {
                message.push_str(&format!("  Hint: {}\n", hint));
            }
            message.pop(); // Remove trailing newline
        }

        // Convert our error code to LSP's NumberOrString
        let code = self.code.clone().map(NumberOrString::String);

        Diagnostic {
            range,
            severity: Some(severity),
            message,
            source: Some("tlang".to_string()),
            code,
            ..Default::default()
        }
    }
}

impl fmt::Display for TLangError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.format())
    }
}

impl std::error::Error for TLangError {}

/// Collection of errors from validation
#[derive(Debug, Clone, Default)]
pub struct ErrorCollection {
    pub errors: Vec<TLangError>,
}

impl ErrorCollection {
    /// Create a new empty error collection
    pub fn new() -> Self {
        Self { errors: Vec::new() }
    }

    /// Add an error to the collection
    pub fn add_error(&mut self, error: TLangError) {
        self.errors.push(error);
    }

    /// Add multiple errors
    pub fn extend(&mut self, errors: impl IntoIterator<Item = TLangError>) {
        self.errors.extend(errors);
    }

    /// Check if there are any errors (not just warnings)
    pub fn has_errors(&self) -> bool {
        self.errors
            .iter()
            .any(|e| e.severity == ErrorSeverity::Error)
    }

    /// Check if there are any warnings
    pub fn has_warnings(&self) -> bool {
        self.errors
            .iter()
            .any(|e| e.severity == ErrorSeverity::Warning)
    }

    /// Get all errors (not warnings)
    pub fn get_errors(&self) -> Vec<&TLangError> {
        self.errors
            .iter()
            .filter(|e| e.severity == ErrorSeverity::Error)
            .collect()
    }

    /// Get all warnings
    pub fn get_warnings(&self) -> Vec<&TLangError> {
        self.errors
            .iter()
            .filter(|e| e.severity == ErrorSeverity::Warning)
            .collect()
    }

    /// Convert to LSP diagnostics
    pub fn to_lsp_diagnostics(&self) -> Vec<lsp_types::Diagnostic> {
        self.errors.iter().map(|e| e.to_lsp_diagnostic()).collect()
    }

    /// Merge with another error collection
    pub fn merge(&mut self, other: &ErrorCollection) {
        self.errors.extend(other.errors.clone());
    }
}

impl IntoIterator for ErrorCollection {
    type Item = TLangError;
    type IntoIter = std::vec::IntoIter<Self::Item>;

    fn into_iter(self) -> Self::IntoIter {
        self.errors.into_iter()
    }
}

impl<'a> IntoIterator for &'a ErrorCollection {
    type Item = &'a TLangError;
    type IntoIter = std::slice::Iter<'a, TLangError>;

    fn into_iter(self) -> Self::IntoIter {
        self.errors.iter()
    }
}

// ---------------------------------------------------------------------------
// Helper functions for source location
// ---------------------------------------------------------------------------

/// Convert a byte offset to line and position (1-based)
pub fn offset_to_line_position(source: &str, offset: usize) -> (usize, usize) {
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

    (line, position)
}

/// Create a source location from a file and offset
pub fn location_from_offset(
    file: impl Into<String>,
    source: &str,
    offset: usize,
) -> SourceLocation {
    let (line, position) = offset_to_line_position(source, offset);
    SourceLocation::new(file, line, position, offset)
}

// ---------------------------------------------------------------------------
// Pest error formatting
// ---------------------------------------------------------------------------

/// Format a pest parsing error with file context
pub fn format_pest_error<R: RuleType>(error: PestError<R>, file: &str) -> String {
    let (line, position) = match error.line_col {
        LineColLocation::Pos((line, position)) => (line, position),
        LineColLocation::Span((line, position), _) => (line, position),
    };

    let message = match error.variant {
        ErrorVariant::ParsingError {
            positives,
            negatives,
        } => {
            let expected = if positives.is_empty() {
                "n/a".to_string()
            } else {
                positives
                    .into_iter()
                    .map(|rule| format!("{rule:?}"))
                    .collect::<Vec<_>>()
                    .join(", ")
            };
            let unexpected = if negatives.is_empty() {
                "n/a".to_string()
            } else {
                negatives
                    .into_iter()
                    .map(|rule| format!("{rule:?}"))
                    .collect::<Vec<_>>()
                    .join(", ")
            };
            format!("syntax error: expected [{expected}], unexpected [{unexpected}]")
        }
        ErrorVariant::CustomError { message } => message,
    };

    format_with_context(&message, file, line, position)
}

/// Format a message with file, line, and position context
pub fn format_with_context(message: &str, file: &str, line: usize, position: usize) -> String {
    format!("{message} (file: {file}, line: {line}, position: {position})")
}

// ---------------------------------------------------------------------------
// Validation Context
// ---------------------------------------------------------------------------

/// Context for validation that tracks the current state
pub struct ValidationContext<'a> {
    /// The domain model being validated
    pub model: &'a DomainModel,
    /// The source text for the current file
    pub source: &'a str,
    /// Current file path
    pub file: String,
    /// Map of all loaded models by file stem for cross-file validation
    pub loaded_models: &'a HashMap<String, DomainModel>,
    /// Map of exposed symbols by file stem
    pub exposed_symbols: &'a HashMap<String, HashSet<String>>,
    /// Collection of errors found during validation
    pub errors: ErrorCollection,
    /// Track variables in current scope
    pub current_scope: Vec<HashSet<String>>,
    /// Track functions defined in current file
    pub current_file_functions: HashSet<String>,
    /// Track templates defined in current file
    pub current_file_templates: HashSet<String>,
}

impl<'a> ValidationContext<'a> {
    /// Create a new validation context
    pub fn new(
        model: &'a DomainModel,
        source: &'a str,
        file: impl Into<String>,
        loaded_models: &'a HashMap<String, DomainModel>,
        exposed_symbols: &'a HashMap<String, HashSet<String>>,
    ) -> Self {
        Self {
            model,
            source,
            file: file.into(),
            loaded_models,
            exposed_symbols,
            errors: ErrorCollection::new(),
            current_scope: vec![HashSet::new()],
            current_file_functions: HashSet::new(),
            current_file_templates: HashSet::new(),
        }
    }

    /// Add an error to the context
    pub fn add_error(&mut self, error: TLangError) {
        self.errors.add_error(error);
    }

    /// Add an error with the given parameters
    pub fn report_error(
        &mut self,
        message: impl Into<String>,
        category: ErrorCategory,
        offset: usize,
    ) {
        let location = location_from_offset(&self.file, self.source, offset);
        let error = TLangError::new(message, category, ErrorSeverity::Error, location);
        self.add_error(error);
    }

    /// Add a warning
    pub fn report_warning(
        &mut self,
        message: impl Into<String>,
        category: ErrorCategory,
        offset: usize,
    ) {
        let location = location_from_offset(&self.file, self.source, offset);
        let error = TLangError::new(message, category, ErrorSeverity::Warning, location);
        self.add_error(error);
    }

    /// Enter a new scope
    pub fn enter_scope(&mut self) {
        self.current_scope.push(HashSet::new());
    }

    /// Exit the current scope
    pub fn exit_scope(&mut self) {
        self.current_scope.pop();
    }

    /// Add a variable to the current scope
    pub fn add_variable(&mut self, name: impl Into<String>) {
        if let Some(current) = self.current_scope.last_mut() {
            current.insert(name.into());
        }
    }

    /// Check if a variable is accessible in the current scope
    pub fn is_variable_accessible(&self, name: &str) -> bool {
        for scope in self.current_scope.iter().rev() {
            if scope.contains(name) {
                return true;
            }
        }
        false
    }

    /// Get the current scope level (0 = global)
    pub fn current_scope_level(&self) -> usize {
        self.current_scope.len().saturating_sub(1)
    }
}

// ---------------------------------------------------------------------------
// Validation Functions
// ---------------------------------------------------------------------------

/// Validate use/import statements.
///
/// Import validation is only performed when `loaded_models` is non-empty —
/// an empty map signals that the caller has no file-system context (e.g. an
/// in-memory LSP session with no real file path), in which case we skip the
/// check to avoid false positives.
pub fn validate_imports(
    header: &DomainHeader,
    source: &str,
    file: &str,
    loaded_models: &HashMap<String, DomainModel>,
    _exposed_symbols: &HashMap<String, HashSet<String>>,
) -> ErrorCollection {
    let mut errors = ErrorCollection::new();

    // Without file-system context we cannot verify whether imported files exist.
    if loaded_models.is_empty() {
        return errors;
    }

    // Validate each use statement
    for use_stmt in &header.uses {
        // Check if the use path exists
        let path_str = use_stmt.path.join(".");
        let file_stem = use_stmt.path.last().cloned().unwrap_or_default();

        // Check if the file exists in loaded models
        if !loaded_models.contains_key(&file_stem) {
            // Check if it's a built-in TLang module
            if !is_builtin_module(&use_stmt.path) {
                // Point at the path (after "use "), not at "use" keyword itself.
                let use_offset = use_stmt.offset;
                let symbol_offset = if use_offset > 0 && source.len() > use_offset {
                    let after = &source[use_offset..];
                    // Skip "use " whitespace to the path start.
                    after
                        .find(|c: char| !c.is_whitespace() && c != 'u' && c != 's' && c != 'e')
                        .map(|rel| use_offset + rel)
                        .unwrap_or(use_offset)
                } else {
                    use_offset
                };
                let location = location_from_offset(file, source, symbol_offset);
                let error = TLangError::new(
                    format!("imported resource '{}' does not exist", path_str),
                    ErrorCategory::Import,
                    ErrorSeverity::Error,
                    location,
                )
                .with_length(path_str.len())
                .with_hint(format!(
                    "Check that the file '{}' exists and is properly imported",
                    file_stem
                ))
                .with_code("TLANG_IMPORT_NOT_FOUND");
                errors.add_error(error);
            }
        }
    }

    errors
}

/// Check if a path refers to a built-in TLang module
fn is_builtin_module(path: &[String]) -> bool {
    if path.is_empty() {
        return false;
    }
    matches!(path[0].as_str(), "TLang" | "MCP" | "System")
}

/// Validate expose declarations
pub fn validate_exposes(
    header: &DomainHeader,
    model: &DomainModel,
    source: &str,
    file: &str,
) -> ErrorCollection {
    let mut errors = ErrorCollection::new();

    // Collect all defined symbols in this file
    let mut defined_symbols: HashSet<String> = HashSet::new();

    for block in &model.body {
        match block {
            DomainBlock::Helper(helper) => {
                let func_names = extract_function_names(&helper.content);
                for name in func_names {
                    defined_symbols.insert(name);
                }
            }
            DomainBlock::Template(tmpl) => {
                defined_symbols.insert(tmpl.name.clone());
            }
            DomainBlock::Data(data) => {
                defined_symbols.insert(data.name.clone());
            }
            DomainBlock::Cmd(cmd) => {
                defined_symbols.insert(cmd.name.clone());
            }
            DomainBlock::Model(mb) => {
                for name in extract_set_entity_names(&mb.content) {
                    defined_symbols.insert(name);
                }
            }
            DomainBlock::Raw(raw) => {
                defined_symbols.insert(raw.name.clone());
            }
            DomainBlock::Doc(doc) => {
                defined_symbols.insert(doc.name.clone());
            }
            DomainBlock::Style(style) => {
                defined_symbols.insert(style.name.clone());
            }
            DomainBlock::Test(_) => {}
        }
    }

    // Check that all exposed symbols exist, using stored offsets for precise locations.
    for (idx, exposed) in header.exposes.iter().enumerate() {
        if !defined_symbols.contains(exposed) {
            let offset = header.expose_offsets.get(idx).copied().unwrap_or(0);
            // Point at the symbol name itself (after "expose "), not at the keyword.
            let symbol_offset = if offset > 0 && source.len() > offset {
                // Skip past "expose " to the symbol name.
                let after = &source[offset..];
                let keyword_end = after.find(exposed.as_str()).unwrap_or(0);
                offset + keyword_end
            } else {
                offset
            };
            let location = location_from_offset(file, source, symbol_offset);
            let error = TLangError::new(
                format!("exposed symbol '{}' is not defined in this file", exposed),
                ErrorCategory::Visibility,
                ErrorSeverity::Error,
                location,
            )
            .with_length(exposed.len())
            .with_hint(format!(
                "Available symbols: {}",
                {
                    let mut syms: Vec<_> = defined_symbols.iter().cloned().collect();
                    syms.sort();
                    syms.join(", ")
                }
            ))
            .with_code("TLANG_EXPOSE_UNDEFINED");
            errors.add_error(error);
        }
    }

    errors
}

/// Extract `set` entity names from a model block's content string.
fn extract_set_entity_names(content: &str) -> Vec<String> {
    let pattern = regex::Regex::new(r#"\bset\s+([a-zA-Z_][a-zA-Z0-9_]*)"#).unwrap();
    pattern
        .captures_iter(content)
        .filter_map(|cap| cap.get(1))
        .map(|m| m.as_str().to_string())
        .collect()
}

/// Extract function names from helper block content
fn extract_function_names(content: &str) -> Vec<String> {
    let mut names = Vec::new();

    // Simple regex to find function declarations
    // Pattern: func <name>( or <name> =
    let func_pattern = regex::Regex::new(r#"\bfunc\s+([a-zA-Z_][a-zA-Z0-9_]*)\s*\("#).unwrap();
    let let_pattern = regex::Regex::new(r#"\blet\s+([a-zA-Z_][a-zA-Z0-9_]*)\s*="#).unwrap();

    for cap in func_pattern.captures_iter(content) {
        if let Some(name) = cap.get(1) {
            names.push(name.as_str().to_string());
        }
    }

    for cap in let_pattern.captures_iter(content) {
        if let Some(name) = cap.get(1) {
            names.push(name.as_str().to_string());
        }
    }

    names
}

/// Validate function calls and variable references in helper blocks
pub fn validate_helper_blocks(
    model: &DomainModel,
    _source: &str,
    file: &str,
    loaded_models: &HashMap<String, DomainModel>,
    exposed_symbols: &HashMap<String, HashSet<String>>,
) -> ErrorCollection {
    let mut errors = ErrorCollection::new();

    for block in &model.body {
        if let DomainBlock::Helper(helper) = block {
            let block_errors = validate_helper_content(
                &helper.content,
                "",
                file,
                &model.header,
                loaded_models,
                exposed_symbols,
                helper.content_start,
            );
            errors.errors.extend(block_errors);
        }
    }

    errors
}

/// Validate the content of a helper block
fn validate_helper_content<'a>(
    content: &'a str,
    _full_source: &str,
    file: &str,
    header: &DomainHeader,
    loaded_models: &'a HashMap<String, DomainModel>,
    exposed_symbols: &'a HashMap<String, HashSet<String>>,
    content_start: usize,
) -> ErrorCollection {
    // Create a temporary model for validation
    let temp_model = DomainModel {
        header: header.clone(),
        body: Vec::new(),
    };

    let mut ctx =
        ValidationContext::new(&temp_model, content, file, loaded_models, exposed_symbols);

    // Parse the helper content to extract statements
    // For now, we'll do a simple text-based validation

    // Extract all function definitions and track them
    let func_names = extract_function_names(content);
    for name in &func_names {
        ctx.current_file_functions.insert(name.clone());
    }

    // Validate function calls
    validate_function_calls(&mut ctx, content, content_start);

    // Validate variable references
    validate_variable_references(&mut ctx, content, content_start);

    ctx.errors
}

/// Validate function calls in helper content
fn validate_function_calls(ctx: &mut ValidationContext, content: &str, offset_base: usize) {
    // Find all function calls in the content
    // Pattern: <name>( or <name>.
    let call_pattern = regex::Regex::new(r#"([a-zA-Z_][a-zA-Z0-9_]*)\s*\("#).unwrap();

    for cap in call_pattern.captures_iter(content) {
        if let Some(name_match) = cap.get(1) {
            let func_name = name_match.as_str();
            let call_offset = name_match.start() + offset_base;

            // Skip if it's a language keyword or built-in
            if is_keyword(func_name) {
                continue;
            }

            // Check if the function exists
            let function_exists = ctx.current_file_functions.contains(func_name)
                || is_builtin_function(func_name)
                || is_from_dependency(func_name, ctx);

            if !function_exists {
                ctx.report_error(
                    format!("function '{}' is not defined or not accessible", func_name),
                    ErrorCategory::Function,
                    call_offset,
                );
            } else {
                // Validate parameters if we can find the function definition
                validate_function_parameters(ctx, func_name, content, call_offset);
            }
        }
    }
}

/// Check if a name is a TLang keyword
fn is_keyword(name: &str) -> bool {
    matches!(
        name,
        "func"
            | "let"
            | "if"
            | "else"
            | "for"
            | "in"
            | "return"
            | "match"
            | "case"
            | "default"
            | "expose"
            | "use"
            | "set"
            | "attrs"
            | "op"
            | "true"
            | "false"
            | "null"
    )
}

/// Check if a name is a built-in function
fn is_builtin_function(name: &str) -> bool {
    // TLang built-in functions
    matches!(
        name,
        "println"
            | "print"
            | "error"
            | "warn"
            | "info"
            | "debug"
            | "len"
            | "isEmpty"
            | "contains"
            | "startsWith"
            | "endsWith"
            | "substring"
            | "trim"
            | "toUpper"
            | "toLower"
            | "split"
            | "join"
            | "parseInt"
            | "parseFloat"
            | "toString"
            | "range"
            | "listOf"
            | "mapOf"
            | "now"
            | "random"
    )
}

/// Check if a function is from a dependency
fn is_from_dependency(_func_name: &str, _ctx: &ValidationContext) -> bool {
    // Check if any loaded model defines this function
    // For now, we'll return false as we don't have full information
    // This is a simplification that can be enhanced later
    false
}

/// Validate function parameters
fn validate_function_parameters(
    _ctx: &mut ValidationContext,
    _func_name: &str,
    _content: &str,
    _call_offset: usize,
) {
    // Find the function call and extract arguments
    // This is a simplified implementation

    // For now, we'll just check that we can find the function
    // Full parameter validation would require parsing the function signature
    // and the call arguments
}

/// Validate variable references in helper content
fn validate_variable_references(ctx: &mut ValidationContext, content: &str, offset_base: usize) {
    // Find all variable references
    // Pattern: $<name> or just <name> in certain contexts
    let var_pattern = regex::Regex::new(r#"\$\{?([a-zA-Z_][a-zA-Z0-9_]*)\}?"#).unwrap();

    for cap in var_pattern.captures_iter(content) {
        if let Some(name_match) = cap.get(1) {
            let var_name = name_match.as_str();
            let var_offset = name_match.start() + offset_base;

            // Skip keywords
            if is_keyword(var_name) {
                continue;
            }

            // Check if variable is accessible
            if !ctx.is_variable_accessible(var_name) {
                ctx.report_warning(
                    format!(
                        "variable '{}' may not be accessible in this scope",
                        var_name
                    ),
                    ErrorCategory::Variable,
                    var_offset,
                );
            }
        }
    }
}

/// Validate template blocks
pub fn validate_templates(model: &DomainModel, source: &str, file: &str) -> ErrorCollection {
    let mut errors = ErrorCollection::new();

    for block in &model.body {
        match block {
            DomainBlock::Template(tmpl) => {
                validate_template(tmpl, source, file, &mut errors);
            }
            DomainBlock::Data(data) => {
                validate_data_template(data, source, file, &mut errors);
            }
            DomainBlock::Cmd(cmd) => {
                validate_cmd_template(cmd, source, file, &mut errors);
            }
            DomainBlock::Raw(raw) => {
                validate_raw_template(raw, source, file, &mut errors);
            }
            DomainBlock::Doc(doc) => {
                validate_doc_template(doc, source, file, &mut errors);
            }
            DomainBlock::Style(style) => {
                validate_style_template(style, source, file, &mut errors);
            }
            _ => {}
        }
    }

    errors
}

/// Validate a template block
fn validate_template(
    tmpl: &TemplateBlock,
    _source: &str,
    file: &str,
    errors: &mut ErrorCollection,
) {
    // Check that lang is not empty
    if tmpl.lang.is_empty() {
        let location = SourceLocation::new(file, 1, 1, tmpl.lang_offset);
        let error = TLangError::new(
            format!("template '{}' has no language specified", tmpl.name),
            ErrorCategory::Template,
            ErrorSeverity::Error,
            location,
        )
        .with_code("TLANG_TEMPLATE_NO_LANG");
        errors.add_error(error);
    }

    // Check for duplicate parameter names
    let mut param_names = HashSet::new();
    for param in &tmpl.params {
        if !param_names.insert(&param.name) {
            let location = SourceLocation::new(file, 1, 1, tmpl.tmpl_start);
            let error = TLangError::new(
                format!(
                    "template '{}' has duplicate parameter '{}'",
                    tmpl.name, param.name
                ),
                ErrorCategory::Template,
                ErrorSeverity::Error,
                location,
            )
            .with_code("TLANG_TEMPLATE_DUPLICATE_PARAM");
            errors.add_error(error);
        }
    }
}

/// Validate a data template block
fn validate_data_template(
    data: &crate::ast::DataTemplateBlock,
    _source: &str,
    file: &str,
    errors: &mut ErrorCollection,
) {
    // Check that langs is not empty
    if data.langs.is_empty() {
        let location = SourceLocation::new(file, 1, 1, data.lang_offset);
        let error = TLangError::new(
            format!("data template '{}' has no languages specified", data.name),
            ErrorCategory::Template,
            ErrorSeverity::Error,
            location,
        )
        .with_code("TLANG_DATA_TEMPLATE_NO_LANGS");
        errors.add_error(error);
    }

    // Check for duplicate parameter names
    let mut param_names = HashSet::new();
    for param in &data.params {
        if !param_names.insert(&param.name) {
            let location = SourceLocation::new(file, 1, 1, data.data_start);
            let error = TLangError::new(
                format!(
                    "data template '{}' has duplicate parameter '{}'",
                    data.name, param.name
                ),
                ErrorCategory::Template,
                ErrorSeverity::Error,
                location,
            )
            .with_code("TLANG_DATA_TEMPLATE_DUPLICATE_PARAM");
            errors.add_error(error);
        }
    }
}

/// Validate a cmd template block
fn validate_cmd_template(
    cmd: &crate::ast::CmdTemplateBlock,
    _source: &str,
    file: &str,
    errors: &mut ErrorCollection,
) {
    // Check that langs is not empty
    if cmd.langs.is_empty() {
        let location = SourceLocation::new(file, 1, 1, cmd.lang_offset);
        let error = TLangError::new(
            format!("cmd template '{}' has no languages specified", cmd.name),
            ErrorCategory::Template,
            ErrorSeverity::Error,
            location,
        )
        .with_code("TLANG_CMD_TEMPLATE_NO_LANGS");
        errors.add_error(error);
    }

    // Check for duplicate parameter names
    let mut param_names = HashSet::new();
    for param in &cmd.params {
        if !param_names.insert(&param.name) {
            let location = SourceLocation::new(file, 1, 1, cmd.cmd_start);
            let error = TLangError::new(
                format!(
                    "cmd template '{}' has duplicate parameter '{}'",
                    cmd.name, param.name
                ),
                ErrorCategory::Template,
                ErrorSeverity::Error,
                location,
            )
            .with_code("TLANG_CMD_TEMPLATE_DUPLICATE_PARAM");
            errors.add_error(error);
        }
    }
}

/// Validate a raw template block
fn validate_raw_template(
    raw: &crate::ast::RawTemplateBlock,
    _source: &str,
    file: &str,
    errors: &mut ErrorCollection,
) {
    // Check that variant is valid
    if raw.variant != "AsIs" && raw.variant != "Replaced" {
        let location = SourceLocation::new(file, 1, 1, raw.variant_offset);
        let error = TLangError::new(
            format!(
                "raw template '{}' has invalid variant '{}'",
                raw.name, raw.variant
            ),
            ErrorCategory::Template,
            ErrorSeverity::Error,
            location,
        )
        .with_hint("Valid variants are: AsIs, Replaced")
        .with_code("TLANG_RAW_TEMPLATE_INVALID_VARIANT");
        errors.add_error(error);
    }

    // Check for duplicate parameter names
    let mut param_names = HashSet::new();
    for param in &raw.params {
        if !param_names.insert(&param.name) {
            let location = SourceLocation::new(file, 1, 1, raw.raw_start);
            let error = TLangError::new(
                format!(
                    "raw template '{}' has duplicate parameter '{}'",
                    raw.name, param.name
                ),
                ErrorCategory::Template,
                ErrorSeverity::Error,
                location,
            )
            .with_code("TLANG_RAW_TEMPLATE_DUPLICATE_PARAM");
            errors.add_error(error);
        }
    }
}

/// Validate a doc template block
fn validate_doc_template(
    doc: &crate::ast::DocTemplateBlock,
    _source: &str,
    file: &str,
    errors: &mut ErrorCollection,
) {
    // Check that langs is not empty
    if doc.langs.is_empty() {
        let location = SourceLocation::new(file, 1, 1, doc.lang_offset);
        let error = TLangError::new(
            format!("doc template '{}' has no languages specified", doc.name),
            ErrorCategory::Template,
            ErrorSeverity::Error,
            location,
        )
        .with_code("TLANG_DOC_TEMPLATE_NO_LANGS");
        errors.add_error(error);
    }

    // Check for duplicate parameter names
    let mut param_names = HashSet::new();
    for param in &doc.params {
        if !param_names.insert(&param.name) {
            let location = SourceLocation::new(file, 1, 1, doc.doc_start);
            let error = TLangError::new(
                format!(
                    "doc template '{}' has duplicate parameter '{}'",
                    doc.name, param.name
                ),
                ErrorCategory::Template,
                ErrorSeverity::Error,
                location,
            )
            .with_code("TLANG_DOC_TEMPLATE_DUPLICATE_PARAM");
            errors.add_error(error);
        }
    }
}

/// Validate a style template block
fn validate_style_template(
    style: &crate::ast::StyleTemplateBlock,
    _source: &str,
    file: &str,
    errors: &mut ErrorCollection,
) {
    // Check that langs is not empty
    if style.langs.is_empty() {
        let location = SourceLocation::new(file, 1, 1, style.lang_offset);
        let error = TLangError::new(
            format!("style template '{}' has no languages specified", style.name),
            ErrorCategory::Template,
            ErrorSeverity::Error,
            location,
        )
        .with_code("TLANG_STYLE_TEMPLATE_NO_LANGS");
        errors.add_error(error);
    }

    // Check for duplicate parameter names
    let mut param_names = HashSet::new();
    for param in &style.params {
        if !param_names.insert(&param.name) {
            let location = SourceLocation::new(file, 1, 1, style.style_start);
            let error = TLangError::new(
                format!(
                    "style template '{}' has duplicate parameter '{}'",
                    style.name, param.name
                ),
                ErrorCategory::Template,
                ErrorSeverity::Error,
                location,
            )
            .with_code("TLANG_STYLE_TEMPLATE_DUPLICATE_PARAM");
            errors.add_error(error);
        }
    }
}

/// Run all validations on a domain model.
///
/// Note: helper-block function/variable checking is intentionally omitted here
/// because the real AST-based type checker in `compile_from_domain_model` already
/// performs those checks with full accuracy.  The regex-based `validate_helper_blocks`
/// was producing false positives and has been removed from this path.
pub fn validate_model(
    model: &DomainModel,
    source: &str,
    file: &str,
    loaded_models: &HashMap<String, DomainModel>,
    exposed_symbols: &HashMap<String, HashSet<String>>,
) -> ErrorCollection {
    let mut errors = ErrorCollection::new();

    // Validate imports (only when we have file-system context).
    let import_errors =
        validate_imports(&model.header, source, file, loaded_models, exposed_symbols);
    errors.errors.extend(import_errors.errors);

    // Validate exposes (symbols declared with `expose` must exist in this file).
    let expose_errors = validate_exposes(&model.header, model, source, file);
    errors.errors.extend(expose_errors.errors);

    // Validate template blocks (language declarations, duplicate parameters, etc.).
    let template_errors = validate_templates(model, source, file);
    errors.errors.extend(template_errors.errors);

    errors
}

// ---------------------------------------------------------------------------
// LSP Integration
// ---------------------------------------------------------------------------

/// Convert error collection to LSP diagnostics
pub fn error_collection_to_lsp_diagnostics(errors: &ErrorCollection) -> Vec<lsp_types::Diagnostic> {
    errors.to_lsp_diagnostics()
}

/// Create a diagnostic from a TLangError
pub fn create_lsp_diagnostic(error: &TLangError) -> lsp_types::Diagnostic {
    error.to_lsp_diagnostic()
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_offset_to_line_position() {
        let source = "line1\nline2\nline3";

        // Offset 0 = start of line 1
        assert_eq!(offset_to_line_position(source, 0), (1, 1));

        // Offset 6 = start of line 2 (after "line1\n")
        assert_eq!(offset_to_line_position(source, 6), (2, 1));

        // Offset 7 = position 2 of line 2
        assert_eq!(offset_to_line_position(source, 7), (2, 2));
    }

    #[test]
    fn test_source_location_to_lsp_position() {
        let loc = SourceLocation::new("test.tlang", 1, 1, 0);
        assert_eq!(loc.to_lsp_position(), (0, 0));

        let loc = SourceLocation::new("test.tlang", 2, 5, 10);
        assert_eq!(loc.to_lsp_position(), (1, 4));
    }

    #[test]
    fn test_tlang_error_format() {
        let loc = SourceLocation::new("test.tlang", 10, 5, 100);
        let error = TLangError::new(
            "test error",
            ErrorCategory::Function,
            ErrorSeverity::Error,
            loc,
        );

        let formatted = error.format();
        assert!(formatted.contains("[error] function: test error"));
        assert!(formatted.contains("file: test.tlang, line: 10, position: 5"));
    }

    #[test]
    fn test_error_collection() {
        let mut collection = ErrorCollection::new();

        let loc1 = SourceLocation::new("test.tlang", 1, 1, 0);
        collection.add_error(TLangError::new(
            "error 1",
            ErrorCategory::Function,
            ErrorSeverity::Error,
            loc1,
        ));

        let loc2 = SourceLocation::new("test.tlang", 2, 1, 10);
        collection.add_error(TLangError::new(
            "warning 1",
            ErrorCategory::Variable,
            ErrorSeverity::Warning,
            loc2,
        ));

        assert!(collection.has_errors());
        assert!(collection.has_warnings());
        assert_eq!(collection.get_errors().len(), 1);
        assert_eq!(collection.get_warnings().len(), 1);
    }

    #[test]
    fn test_is_builtin_module() {
        assert!(is_builtin_module(&["TLang".to_string()]));
        assert!(is_builtin_module(&[
            "TLang".to_string(),
            "String".to_string()
        ]));
        assert!(is_builtin_module(&["MCP".to_string()]));
        assert!(!is_builtin_module(&["MyModule".to_string()]));
    }

    #[test]
    fn test_is_keyword() {
        assert!(is_keyword("func"));
        assert!(is_keyword("let"));
        assert!(is_keyword("if"));
        assert!(!is_keyword("myFunction"));
    }

    #[test]
    fn test_is_builtin_function() {
        assert!(is_builtin_function("println"));
        assert!(is_builtin_function("len"));
        assert!(!is_builtin_function("myFunction"));
    }
}
