// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Top-level AST types produced by the parser.
//!
//! A `.tlang` source file is represented as a [`DomainModel`], which contains
//! a [`DomainHeader`] (the `expose` and `use` declarations) and a list of
//! [`DomainBlock`]s (helper code, model definitions, and template blocks).
//!
//! All types implement `serde::{Serialize, Deserialize}` so they can be
//! embedded in `.tlangc` bytecode cache files.

#[derive(Debug, Clone, PartialEq, Eq, Default, serde::Serialize, serde::Deserialize)]
pub struct DomainModel {
    pub header: DomainHeader,
    pub body: Vec<DomainBlock>,
}

#[derive(Debug, Clone, PartialEq, Eq, Default, serde::Serialize, serde::Deserialize)]
pub struct DomainHeader {
    pub exposes: Vec<String>,
    /// Byte offset of each `expose` keyword in the source document (parallel to `exposes`).
    #[serde(default)]
    pub expose_offsets: Vec<usize>,
    pub uses: Vec<DomainUse>,
    /// Maps a dependency file stem (e.g. `"Utils"`) to the list of names it
    /// explicitly exposes.  Only populated for dependencies that have at least
    /// one `expose` declaration.  Used by the type-checker to enforce that
    /// callers only access symbols that a dependency has made public.
    #[serde(default)]
    pub dep_exposes: std::collections::HashMap<String, Vec<String>>,
}

#[derive(Debug, Clone, Eq, serde::Serialize, serde::Deserialize)]
pub struct DomainUse {
    pub path: Vec<String>,
    pub alias: Option<String>,
    /// Byte offset of the `use` keyword in the source document (diagnostics only).
    pub offset: usize,
}

impl PartialEq for DomainUse {
    fn eq(&self, other: &Self) -> bool {
        // Offset is a diagnostics-only hint and must not affect equality checks
        // (e.g. duplicate-import detection in the loader).
        self.path == other.path && self.alias == other.alias
    }
}

#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub enum DomainBlock {
    Helper(HelperBlock),
    Template(TemplateBlock),
    Data(DataTemplateBlock),
    Cmd(CmdTemplateBlock),
    Model(ModelBlock),
    Raw(RawTemplateBlock),
    Doc(DocTemplateBlock),
    Style(StyleTemplateBlock),
    Test(TestBlock),
}

#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub struct TestBlock {
    pub name: String,
    /// Raw balanced-block content including the outer braces.
    pub content: String,
}

#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub struct HelperBlock {
    pub content: String,
    /// Byte offset of the opening `{` of this helper block within the original
    /// source document.  Used by the LSP to map type-checker diagnostics back
    /// to precise document positions.
    pub content_start: usize,
    /// The file stem (without `.tlang`) of the file this helper block was
    /// loaded from, when it originates from a dependency.  `None` for blocks
    /// that belong to the main (entry-point) file.
    #[serde(default)]
    pub source_file: Option<String>,
    /// Full filesystem path of the file this helper block was loaded from,
    /// when it originates from a dependency.  `None` for blocks that belong
    /// to the main (entry-point) file.  Used by the LSP for cross-file
    /// go-to-definition navigation.
    #[serde(default)]
    pub source_path: Option<String>,
    /// The manifest alias name of the package this block was loaded from
    /// (e.g. `"QuarkusKotlin"`), set only for manifest-backed package imports.
    /// Used to register functions under their qualified name
    /// (`"PackageName.funcName"`) so multiple adapters can coexist in the
    /// function table without bare-name collisions.
    #[serde(default)]
    pub package_name: Option<String>,
}

#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub struct ModelBlock {
    pub content: String,
}

#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub struct TemplateBlock {
    pub lang: String,
    pub name: String,
    pub params: Vec<TemplateParam>,
    pub content: TemplateContent,
    #[serde(default)]
    pub lang_offset: usize,
    /// Byte offset of the `lang` keyword that opens this template block.
    #[serde(default)]
    pub tmpl_start: usize,
}

#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub struct TemplateParam {
    pub name: String,
    pub ty: String,
}

#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub enum TemplateContent {
    Full(String),
    Specialized(String),
}

/// A `data [lang, ...] name(params) { ... }` template block for generating
/// structured data outputs such as HTML, JSON, YAML, TOML, or XML.
#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub struct DataTemplateBlock {
    /// Target languages/formats this template supports (e.g. `["html", "json"]`).
    pub langs: Vec<String>,
    /// Template name.
    pub name: String,
    /// Declared parameters.
    pub params: Vec<TemplateParam>,
    /// Raw body text (the content between the outer `{ }`).
    pub content: String,
    /// Byte offset of the first language identifier (inside the `[...]`).
    #[serde(default)]
    pub lang_offset: usize,
    /// Byte offset of the `data` keyword.
    #[serde(default)]
    pub data_start: usize,
}

/// A `raw [AsIs|Replaced] name(params) { ... }` template block for emitting
/// arbitrary text content without any LSP formatting, indentation, or
/// language-specific processing.
///
/// - `AsIs` — the body is emitted verbatim; no `${param}` substitution is
///   performed.  Useful for embedding content that must not be touched at all.
/// - `Replaced` — the body is emitted with `${param}` references replaced by
///   the corresponding argument values, but no other processing is applied.
#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub struct RawTemplateBlock {
    /// The variant: `"AsIs"` or `"Replaced"`.
    pub variant: String,
    /// Template name.
    pub name: String,
    /// Declared parameters.
    pub params: Vec<TemplateParam>,
    /// Raw body text (the content between the outer `{ }`), including braces.
    pub content: String,
    /// Byte offset of the variant identifier (inside the `[...]`).
    #[serde(default)]
    pub variant_offset: usize,
    /// Byte offset of the `raw` keyword.
    #[serde(default)]
    pub raw_start: usize,
}

/// A `doc [lang, ...] name(params) { ... }` template block for generating
/// structured document outputs such as Markdown or HTML.
///
/// The body contains a structured document description using headings, sections,
/// text blocks, code blocks, images, links, lists, tables, and includes.
#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub struct DocTemplateBlock {
    /// Target output formats (e.g. `["md", "html"]`).
    pub langs: Vec<String>,
    /// Template name.
    pub name: String,
    /// Declared parameters.
    pub params: Vec<TemplateParam>,
    /// Raw body text (balanced block content including outer braces).
    pub content: String,
    /// Byte offset of the first language identifier (inside `[...]`).
    #[serde(default)]
    pub lang_offset: usize,
    /// Byte offset of the `doc` keyword.
    #[serde(default)]
    pub doc_start: usize,
}

/// A `style [lang, ...] name(params) { ... }` template block for generating
/// structured style outputs such as CSS, SCSS, or JSON-based style objects.
///
/// The body contains a structured style description using named selectors,
/// optional bracket-based modifier lists, and key–value attribute pairs.
/// Attribute values may be strings, identifiers, numbers, booleans, arrays,
/// or `${param}` interpolations.  Include directives (`<[ call ]>`) are also
/// supported inside attribute lists.
#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub struct StyleTemplateBlock {
    /// Target output formats (e.g. `["css", "scss"]`).
    pub langs: Vec<String>,
    /// Template name.
    pub name: String,
    /// Declared parameters.
    pub params: Vec<TemplateParam>,
    /// Raw body text (balanced block content including outer braces).
    pub content: String,
    /// Byte offset of the first language identifier (inside `[...]`).
    #[serde(default)]
    pub lang_offset: usize,
    /// Byte offset of the `style` keyword.
    #[serde(default)]
    pub style_start: usize,
}

/// A `cmd [lang, ...] name(params) { ... }` template block for describing
/// executable commands such as bash commands, SQL queries, or HTTP requests.
///
/// The body contains a structured command description: either a bare command
/// name (e.g. `bash`) or a function-call style invocation
/// (e.g. `GET(url: ${url}, headers: contentType)`).
#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
pub struct CmdTemplateBlock {
    /// Target command types / execution environments (e.g. `["bash", "sql"]`).
    pub langs: Vec<String>,
    /// Template name.
    pub name: String,
    /// Declared parameters.
    pub params: Vec<TemplateParam>,
    /// Raw body text (the content between the outer `{ }`).
    pub content: String,
    /// Byte offset of the first language identifier (inside the `[...]`).
    #[serde(default)]
    pub lang_offset: usize,
    /// Byte offset of the `cmd` keyword.
    #[serde(default)]
    pub cmd_start: usize,
}
