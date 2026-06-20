// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang — a template language for structural code generation.
//!
//! # Crate layout
//!
//! | Module | Role |
//! |--------|------|
//! | [`parser`] | Pest-based parser: source text → [`ast::DomainModel`] |
//! | [`loader`] | Dependency resolver: loads and merges multi-file programs |
//! | [`runtime`] | Compiler + executor: produces and runs [`runtime::CompiledProgram`] |
//! | [`manifest`] | `manifest.yml` parsing and dependency path resolution |
//! | [`formatter`] | Canonical source formatter (used by the LSP) |
//! | [`error_checker`] | Semantic validation and LSP diagnostic helpers |
//! | [`lsp`] | Language Server Protocol server |
//! | [`tbag`] | `.tbag` package archive format |
//! | [`bytecode`] | `.tlangc` bytecode file I/O |

#![warn(clippy::all)]

pub mod ast;
pub mod bytecode;
#[cfg(test)]
mod conformity_tests;
pub mod error_checker;
pub mod formatter;
pub mod loader;
pub mod lsp;
pub mod manifest;
pub mod model_tree;
pub mod parser;
pub mod runtime;
pub mod tbag;
pub mod tmpl_cmd_tree;
pub mod tmpl_data_tree;
pub mod pdf_lib;
pub mod tmpl_doc_tree;
pub mod tmpl_style_tree;
pub mod tmpl_tree;
pub mod tree_context;

pub use error_checker::{
    ErrorCategory, ErrorCollection, ErrorSeverity, SourceLocation, TLangError,
    error_collection_to_lsp_diagnostics, format_pest_error, format_with_context,
    location_from_offset, offset_to_line_position, validate_model,
};
pub use loader::{
    LoadError, compile_to_bytecode_files, load_program, load_program_prefer_bytecode,
    load_program_with_manifest, load_program_with_manifest_tbox,
};
pub use manifest::{
    Compatibility, CompatibilityResult, Dependency, DependencyLocator, Manifest, ManifestError,
    PackageType, Stability, VersionSpec, check_compatibility, dependency_dirs,
    dependency_dirs_with_tbox, file_dependency_dirs, load_manifest, own_tbox_dir,
    own_tbox_tbag_path, parse_dependency, parse_manifest, parse_version_spec, registry_dep_dir,
    registry_dep_tbag_path, resolve_main, tbox_path, try_load_manifest,
};
pub use parser::{ParseError, parse_domain_model, parse_domain_model_in_file};
pub use runtime::{compile_from_domain_model, run_exposed_function, run_tests, TestBlockResult};
pub use tbag::{
    ensure_extracted, extract as extract_tbag, find_tbag, is_extracted, pack as pack_tbag,
    push as push_tbag, read_manifest_from_tbag, read_manifest_from_tbag_bytes,
};
