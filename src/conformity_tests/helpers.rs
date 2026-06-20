// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::path::{Path, PathBuf};

use crate::{
    load_program_with_manifest,
    runtime::{Value, compile_from_domain_model, run_main, run_main_with_args},
    try_load_manifest,
};

/// Resolve `conformity/<name>/Main.tlang` relative to the crate root.
pub(super) fn conformity_path(name: &str) -> PathBuf {
    let root = Path::new(env!("CARGO_MANIFEST_DIR"));
    root.join("conformity").join(name).join("Main.tlang")
}

/// Load, compile and run a conformity program.  Panics with a descriptive
/// message at each failure stage so test output pinpoints exactly where
/// things broke.
///
/// If a `manifest.yml` exists alongside `Main.tlang` it is loaded
/// automatically so that `file://` dependencies declared in it are
/// resolved correctly.  Programs without a manifest continue to work
/// exactly as before.
pub(super) fn run_conformity(name: &str) -> crate::runtime::RunResult {
    let path = conformity_path(name);

    // Optionally load manifest.yml from the same directory.
    // try_load_manifest takes a *directory* and appends "manifest.yml"
    // internally, so we pass the program directory, not the file path.
    let program_dir = path
        .parent()
        .expect("conformity path has a parent directory");
    let manifest = try_load_manifest(program_dir)
        .unwrap_or_else(|e| panic!("[{name}] manifest error: {e}"));

    let model = load_program_with_manifest(&path, manifest.as_ref())
        .unwrap_or_else(|e| panic!("[{name}] load failed: {e}"));
    let compiled = compile_from_domain_model(&model)
        .unwrap_or_else(|e| panic!("[{name}] compile failed: {e}"));
    run_main(&compiled).unwrap_or_else(|e| panic!("[{name}] runtime error: {e}"))
}

pub(super) fn run_kotlin_extractor(kotlin_src: &str) -> String {
    let root = Path::new(env!("CARGO_MANIFEST_DIR"));
    let path = root.join("extractor").join("kotlin").join("Main.tlang");
    let model = load_program_with_manifest(&path, None)
        .unwrap_or_else(|e| panic!("load extractor: {e}"));
    let compiled =
        compile_from_domain_model(&model).unwrap_or_else(|e| panic!("compile extractor: {e}"));
    let run = run_main_with_args(&compiled, vec![kotlin_src.to_string()])
        .unwrap_or_else(|e| panic!("run extractor: {e}"));
    match run.return_value {
        Value::String(s) => s,
        other => panic!("extractor returned non-string: {other:?}"),
    }
}
