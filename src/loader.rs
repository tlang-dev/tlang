// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Dependency resolver and multi-file program loader.
//!
//! Starting from an entry-point `.tlang` file, the loader:
//!
//! 1. Parses the file's `use` declarations.
//! 2. Resolves each path — sibling file, manifest dependency, or built-in `TLang.*` library.
//! 3. Recursively loads and parses each dependency (cycle detection included).
//! 4. Prepends dependency [`crate::ast::DomainBlock`]s before the consumer's
//!    own blocks so the compiler always sees dependencies first.
//! 5. Compiles the merged [`crate::ast::DomainModel`] into a [`crate::runtime::CompiledProgram`].
//!
//! # Entry points
//!
//! - [`load_program`] — load and compile from a file path.
//! - [`load_program_with_manifest`] — same, with an explicit [`crate::manifest::Manifest`].
//! - [`load_program_prefer_bytecode`] — load a `.tlangc` cache if fresh, else recompile.
//! - [`compile_to_bytecode_files`] — load, compile, and write `.tlangc` cache files.

use std::collections::{HashMap, HashSet};
use std::path::{Path, PathBuf};

use crate::ast::{DomainModel, DomainUse};
use crate::bytecode;
use crate::manifest::{
    Manifest, dependency_dirs_with_tbox, registry_dep_tbag_path, resolve_main, tbox_path,
    try_load_manifest,
};
use crate::parser::{ParseError, parse_domain_model_in_file};
use crate::runtime::{CompiledProgram, compile_from_domain_model};
use crate::tbag;

// ---------------------------------------------------------------------------
// Error type
// ---------------------------------------------------------------------------

/// Error variants produced by the file loader.
#[derive(Debug)]
pub enum LoadError {
    /// An I/O error while reading a file.
    Io(String),
    /// A parse error inside a loaded file.
    Parse(ParseError),
    /// A circular import was detected.
    Cycle(String),
    /// A `use` alias referenced a manifest dependency that could not be found.
    UnresolvedAlias(String),
}

impl std::fmt::Display for LoadError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            LoadError::Io(msg) => write!(f, "IO error: {msg}"),
            LoadError::Parse(e) => write!(f, "Parse error: {e}"),
            LoadError::Cycle(msg) => write!(f, "Circular import: {msg}"),
            LoadError::UnresolvedAlias(alias) => {
                write!(
                    f,
                    "unresolved import alias `{alias}` — check manifest.yml dependencies"
                )
            }
        }
    }
}

impl std::error::Error for LoadError {}

// ---------------------------------------------------------------------------
// Resolution helpers
// ---------------------------------------------------------------------------

/// Returns `true` when a `use` path refers to a user file rather than a
/// built-in `TLang.*` library.
fn is_file_use(path: &[String]) -> bool {
    path.first().map(|s| s != "TLang").unwrap_or(false)
}

/// Converts a `use` path into a `.tlang` file path relative to `current_dir`,
/// OR resolves it from a manifest alias map when the first path segment
/// matches a declared dependency alias.
///
/// Resolution rules (checked in order):
///
/// 1. If the first segment matches a key in `alias_dirs`, the remaining
///    segments are resolved as a sub-path inside that dependency directory.
///    For example `use MyLib.sub.File` with alias `MyLib → /dep/dir` resolves
///    to `/dep/dir/sub/File.tlang`.
///
///    **Main-only rule**: if the alias has a declared `main:` in its manifest
///    (recorded in `config.alias_mains`), only the main entry-point file is
///    accessible from outside.  Attempting to import any other file from that
///    package is an error.
///
///    When only the alias is given (`use Alias` — no file segment) and the
///    alias has a declared main, the path resolves directly to that main file.
///
/// 2. Otherwise, the whole path is resolved relative to `current_dir` as
///    before:
///    - `["SameFolderFile"]`        → `current_dir/SameFolderFile.tlang`
///    - `["SubFolder", "SubFile"]`  → `current_dir/SubFolder/SubFile.tlang`
///
/// Returns `Ok(None)` if `path` is empty, or an error when access to a
/// non-main package file is attempted.
fn resolve_use_path(
    current_dir: &Path,
    path: &[String],
    config: &LoaderConfig<'_>,
) -> Result<Option<PathBuf>, LoadError> {
    if path.is_empty() {
        return Ok(None);
    }

    let first = &path[0];

    // Check whether the first segment is a known manifest alias.
    if let Some(dep_dir) = config.alias_dirs.get(first) {
        // If the alias has a declared main, apply the main-only access rule.
        if let Some(main_path) = config.alias_mains.get(first) {
            if path.len() == 1 {
                // `use Alias` — resolve to the declared main entry point.
                return Ok(Some(main_path.clone()));
            }
            // `use Alias.FileName[.Sub…]` — only allow access to the main file.
            // The requested file is the last segment; any sub-dirs are middle
            // segments.  Build the resolved path and compare against main_path.
            let rest = &path[1..];
            let last = rest.last().unwrap();
            let mut resolved = dep_dir.clone();
            for seg in &rest[..rest.len() - 1] {
                resolved.push(seg);
            }
            resolved.push(format!("{last}.tlang"));

            // Normalise both paths before comparing.
            let resolved_canon = resolved.canonicalize().map_err(|e| {
                LoadError::Io(format!("cannot resolve path '{}': {e}", resolved.display()))
            })?;
            let main_canon = main_path.canonicalize().map_err(|e| {
                LoadError::Io(format!(
                    "cannot resolve main path '{}': {e}",
                    main_path.display()
                ))
            })?;

            if resolved_canon != main_canon {
                let main_stem = main_path
                    .file_stem()
                    .map(|s| s.to_string_lossy().into_owned())
                    .unwrap_or_default();
                let pretty_use = path.join(".");
                return Err(LoadError::Io(format!(
                    "`use {pretty_use}` is not allowed: `{first}` is a package whose only \
                     public entry point is `{main_stem}`. \
                     Use `use {first}.{main_stem}` or `use {first}` instead, \
                     and add `expose` declarations to `{main_stem}` for the symbols you need."
                )));
            }

            return Ok(Some(resolved));
        }

        // No declared main — legacy / file-dep behaviour: any file is accessible.
        if path.len() == 1 {
            return Ok(Some(dep_dir.join(format!("{first}.tlang"))));
        }
        let rest = &path[1..];
        let last = rest.last().unwrap();
        let mut result = dep_dir.clone();
        for seg in &rest[..rest.len() - 1] {
            result.push(seg);
        }
        result.push(format!("{last}.tlang"));
        return Ok(Some(result));
    }

    // Fallback: path relative to the current directory.
    let last = path.last().unwrap();
    let mut result = current_dir.to_path_buf();
    for segment in &path[..path.len() - 1] {
        result.push(segment);
    }
    result.push(format!("{last}.tlang"));
    Ok(Some(result))
}

// ---------------------------------------------------------------------------
// Loader configuration
// ---------------------------------------------------------------------------

/// Options that modify how the recursive file loader behaves.
struct LoaderConfig<'a> {
    /// Manifest alias → resolved directory map.
    alias_dirs: HashMap<String, PathBuf>,

    /// Manifest alias → absolute path to that package's main entry-point file.
    ///
    /// Populated for every dependency whose own `manifest.yml` declares a
    /// `main:` field.  When an alias has an entry here:
    /// - Only the main file can be imported from outside the package.
    /// - All accessible functions must be explicitly `expose`d in that file.
    alias_mains: HashMap<String, PathBuf>,

    /// The set of file stems (e.g. `"KotlinCodegen"`) that are the declared
    /// main entry points of a manifest-backed package.
    ///
    /// Used in `load_file_collecting` to decide whether a dep must have its
    /// `expose` list enforced even when the list is empty.
    alias_main_stems: HashSet<String>,

    /// Project root (currently unused by the recursive loader; retained for
    /// future use and to avoid breaking the `build_config` call sites).
    #[allow(dead_code)]
    project_root: Option<&'a Path>,
}

// ---------------------------------------------------------------------------
// Public API
// ---------------------------------------------------------------------------

/// Load and merge a `.tlang` program starting from `main_path`.
///
/// All file-level `use` imports are resolved, loaded recursively and merged
/// into the returned [`DomainModel`].  Built-in `TLang.*` uses are left
/// intact.  Duplicate imports are silently skipped; circular imports produce
/// a [`LoadError::Cycle`].
///
/// This overload uses no manifest; all imports are resolved relative to the
/// directory containing `main_path`.
pub fn load_program(main_path: &Path) -> Result<DomainModel, LoadError> {
    load_program_with_manifest(main_path, None)
}

/// Load and merge a `.tlang` program, using an optional [`Manifest`] to
/// resolve named dependency aliases declared with `file://` locators.
///
/// When `manifest` is `Some`, the first segment of any `use` path is checked
/// against the manifest's dependency aliases.  If a match is found the
/// remaining path is resolved inside the dependency directory.  All other
/// imports continue to be resolved relative to the directory of the importing
/// file.
pub fn load_program_with_manifest(
    main_path: &Path,
    manifest: Option<&Manifest>,
) -> Result<DomainModel, LoadError> {
    load_program_with_manifest_tbox(main_path, manifest, &tbox_path())
}

/// Like [`load_program_with_manifest`] but uses a caller-supplied tbox root
/// instead of the default `~/.tlang/tbox`.  Useful for tests.
pub fn load_program_with_manifest_tbox(
    main_path: &Path,
    manifest: Option<&Manifest>,
    tbox: &Path,
) -> Result<DomainModel, LoadError> {
    // Before building the config, ensure every registry dependency that ships
    // as a `.tbag` archive has been extracted into its dep dir.  This is a
    // no-op when the sentinel file is already present.
    if let Some(m) = manifest {
        ensure_tbag_deps_extracted(m, tbox)?;
    }

    let config = build_config(main_path, manifest, tbox, None);
    let mut in_progress: HashSet<PathBuf> = HashSet::new();
    let mut loaded: HashSet<PathBuf> = HashSet::new();
    load_file(main_path, &mut in_progress, &mut loaded, &config)
}

/// Like [`load_program_with_manifest`] but prefers a pre-compiled `.tlangc`
/// file from `project_root/target/tlang/` when it exists.  If found, the
/// [`CompiledProgram`] is loaded directly (no re-parsing or re-compiling).
/// Falls back to loading sources and compiling when no bytecode is present.
pub fn load_program_prefer_bytecode(
    main_path: &Path,
    project_root: &Path,
    manifest: Option<&Manifest>,
) -> Result<CompiledProgram, LoadError> {
    // Compute the expected .tlangc path for the main file.
    let main_abs = if main_path.is_absolute() {
        main_path.to_path_buf()
    } else {
        std::env::current_dir()
            .map_err(|e| LoadError::Io(format!("cannot get current dir: {e}")))?
            .join(main_path)
    };

    if let Some(bc_path) = bytecode::bytecode_path(&main_abs, project_root)
        && bc_path.exists()
    {
        return bytecode::read_bytecode(&bc_path).map_err(LoadError::Io);
    }

    // Fall back: load sources and compile.
    let model = load_program_with_manifest_tbox(main_path, manifest, &tbox_path())?;
    compile_from_domain_model(&model).map_err(|e| LoadError::Io(format!("compile error: {e}")))
}

/// Compile all `.tlang` files reachable from `main_path`, produce a single
/// [`CompiledProgram`], and write it as one `.tlangc` bytecode file under
/// `project_root/target/tlang/` for the main file.
///
/// Returns the compiled program together with a list of the `.tlangc` output
/// paths that were written (currently always exactly one entry).
pub fn compile_to_bytecode_files(
    main_path: &Path,
    project_root: &Path,
    manifest: Option<&Manifest>,
) -> Result<(CompiledProgram, Vec<PathBuf>), LoadError> {
    // Load and merge all source files.
    let model = load_program_with_manifest_tbox(main_path, manifest, &tbox_path())?;

    // Compile the merged model.
    let compiled = compile_from_domain_model(&model)
        .map_err(|e| LoadError::Io(format!("compile error: {e}")))?;

    // Compute the .tlangc output path for the main file.
    let main_abs = if main_path.is_absolute() {
        main_path.to_path_buf()
    } else {
        std::env::current_dir()
            .map_err(|e| LoadError::Io(format!("cannot get current dir: {e}")))?
            .join(main_path)
    };

    let bc_path = bytecode::bytecode_path(&main_abs, project_root)
        .ok_or_else(|| LoadError::Io("cannot compute bytecode path for main file".to_string()))?;

    bytecode::write_bytecode(&compiled, &bc_path).map_err(LoadError::Io)?;

    Ok((compiled, vec![bc_path]))
}

// ---------------------------------------------------------------------------
// Config builder
// ---------------------------------------------------------------------------

/// For every registry dependency in `manifest` that has a `.tbag` in its tbox
/// directory, extract the archive (if not already extracted) so that source
/// and bytecode files are accessible to the recursive loader.
fn ensure_tbag_deps_extracted(manifest: &Manifest, tbox: &Path) -> Result<(), LoadError> {
    for dep in &manifest.dependencies {
        if let Some(tbag_path) = registry_dep_tbag_path(tbox, dep) {
            // Only attempt extraction when a tbag actually exists.
            if tbag_path.exists() {
                let dep_dir = tbag_path
                    .parent()
                    .expect("tbag path should have a parent directory");
                tbag::ensure_extracted(dep_dir)
                    .map_err(|e| LoadError::Io(format!("failed to extract tbag: {e}")))?;
            }
        }
    }
    Ok(())
}

fn build_config<'a>(
    main_path: &Path,
    manifest: Option<&Manifest>,
    tbox: &Path,
    project_root: Option<&'a Path>,
) -> LoaderConfig<'a> {
    let alias_dirs: HashMap<String, PathBuf> = manifest
        .map(|m| {
            let root = main_path.parent().unwrap_or(Path::new("."));
            dependency_dirs_with_tbox(root, m, tbox)
        })
        .unwrap_or_default();

    // For each dep whose directory contains a manifest.yml with a `main:`
    // field, record the resolved main file path and its stem so that the
    // loader can enforce the main-only access rule and expose requirements.
    let mut alias_mains: HashMap<String, PathBuf> = HashMap::new();
    let mut alias_main_stems: HashSet<String> = HashSet::new();

    for (alias, dep_dir) in &alias_dirs {
        // Try to load the dep's own manifest.  Failures are silently ignored
        // so that deps without manifests (e.g. legacy file deps) keep working.
        if let Ok(Some(dep_manifest)) = try_load_manifest(dep_dir)
            && dep_manifest.main.is_some()
        {
            let main_file = resolve_main(dep_dir, Some(&dep_manifest));
            if let Some(stem) = main_file.file_stem() {
                alias_main_stems.insert(stem.to_string_lossy().into_owned());
            }
            alias_mains.insert(alias.clone(), main_file);
        }
    }

    LoaderConfig {
        alias_dirs,
        alias_mains,
        alias_main_stems,
        project_root,
    }
}

// ---------------------------------------------------------------------------
// Internal recursive loader
// ---------------------------------------------------------------------------

/// Standard recursive load — does not collect written paths.
fn load_file(
    path: &Path,
    in_progress: &mut HashSet<PathBuf>,
    loaded: &mut HashSet<PathBuf>,
    config: &LoaderConfig<'_>,
) -> Result<DomainModel, LoadError> {
    let mut written = Vec::new();
    load_file_collecting(path, in_progress, loaded, config, &mut written)
}

/// Recursive load with optional bytecode write/prefer, collecting written paths.
fn load_file_collecting(
    path: &Path,
    in_progress: &mut HashSet<PathBuf>,
    loaded: &mut HashSet<PathBuf>,
    config: &LoaderConfig<'_>,
    written: &mut Vec<PathBuf>,
) -> Result<DomainModel, LoadError> {
    // Resolve canonical path for cycle/dedup tracking.
    // When prefer_bytecode is active we may be given a .tlang path that does
    // not exist on disk (because only the .tlangc exists).  Canonicalize the
    // bytecode path instead in that case.
    let canonical = canonicalize_for_tracking(path, config)?;

    // Detect cycles.
    if in_progress.contains(&canonical) {
        return Err(LoadError::Cycle(format!(
            "circular import detected for '{}'",
            path.display()
        )));
    }

    // Deduplicate diamond-shaped dependency graphs.
    if loaded.contains(&canonical) {
        return Ok(crate::ast::DomainModel::default());
    }

    in_progress.insert(canonical.clone());

    // Load the individual file model — from bytecode if preferred and
    // available, otherwise from the .tlang source.
    let mut model = load_individual_model(path, config, written)?;

    let current_dir = path.parent().unwrap_or(Path::new("."));

    // Collect file-based uses before mutating the header.
    let file_uses: Vec<DomainUse> = model
        .header
        .uses
        .iter()
        .filter(|u| is_file_use(&u.path))
        .cloned()
        .collect();

    // Load each dependency and prepend its body blocks.
    for dep_use in &file_uses {
        // Enforce depth limit for relative (non-alias) imports.
        // Allowed:
        //   use FileName              (1 segment — same directory)
        //   use FolderName.FileName   (2 segments — one directory level deep)
        // Not allowed:
        //   use A.B.FileName          (3+ segments — too deep)
        // Manifest alias paths are exempt because their first segment is
        // resolved against the alias map, not as a local directory.
        let is_alias = config.alias_dirs.contains_key(&dep_use.path[0]);
        if !is_alias && dep_use.path.len() > 2 {
            let pretty = dep_use.path.join(".");
            return Err(LoadError::Io(format!(
                "import `use {pretty}` is too deep — cross-file imports are limited to one \
                 directory level. Use `use FolderName.FileName` to import a file one folder \
                 inside the current directory, or `use FileName` for a file in the same directory."
            )));
        }

        let dep_path = resolve_use_path(current_dir, &dep_use.path, config)?
            .ok_or_else(|| LoadError::Io(format!("empty use path in '{}'", path.display())))?;

        let mut dep_model = load_file_collecting(&dep_path, in_progress, loaded, config, written)?;

        // Determine the file stem for this dependency (e.g. "Utils" from "Utils.tlang").
        let dep_stem = dep_path
            .file_stem()
            .map(|s| s.to_string_lossy().into_owned())
            .unwrap_or_default();

        // Tag each helper block from this dependency with its source file stem
        // and full path so the type-checker can enforce visibility restrictions
        // and the LSP can navigate to cross-file definitions.
        // For manifest-backed packages also set package_name so the runtime
        // can register functions under their qualified key ("Pkg.func").
        let dep_path_str = dep_path.to_string_lossy().into_owned();
        let dep_pkg_name = if is_alias {
            Some(dep_use.path[0].clone())
        } else {
            None
        };
        for block in &mut dep_model.body {
            if let crate::ast::DomainBlock::Helper(h) = block {
                if h.source_file.is_none() {
                    h.source_file = Some(dep_stem.clone());
                }
                if h.source_path.is_none() {
                    h.source_path = Some(dep_path_str.clone());
                }
                if h.package_name.is_none() {
                    h.package_name = dep_pkg_name.clone();
                }
            }
        }

        // If the dependency declares any `expose` directives, record them so
        // the type-checker can reject calls to non-exposed symbols.
        //
        // For manifest-backed packages (those whose main file is declared in
        // their manifest), ALWAYS insert into dep_exposes — even when the
        // expose list is empty.  An empty list means nothing is accessible,
        // which is the correct "sealed by default" behaviour for libraries.
        // For plain file deps without a manifest, keep the old behaviour:
        // only insert when the file actually declares at least one expose.
        let is_manifest_main = config.alias_main_stems.contains(&dep_stem);
        if !dep_model.header.exposes.is_empty() || is_manifest_main {
            model
                .header
                .dep_exposes
                .insert(dep_stem.clone(), dep_model.header.exposes.clone());
        }

        // Propagate any dep_exposes that the dependency itself collected from
        // its own transitive dependencies.
        for (k, v) in dep_model.header.dep_exposes {
            model.header.dep_exposes.entry(k).or_insert(v);
        }

        // Merge body: dependency blocks first, then current file blocks.
        let mut merged_body = dep_model.body;
        merged_body.extend(std::mem::take(&mut model.body));
        model.body = merged_body;

        // Merge built-in uses from the dependency (avoid duplicates).
        for u in dep_model.header.uses {
            if !model.header.uses.contains(&u) {
                model.header.uses.push(u);
            }
        }
    }

    // Strip resolved file-based uses; keep only built-in TLang.* uses and
    // aliased uses (e.g. `use KotlinGen as kotlin`).  Aliased uses register
    // a language identifier for `lang [xxx]` validation in compile_from_domain_model
    // and must not be discarded even though the file itself has been loaded.
    model
        .header
        .uses
        .retain(|u| !is_file_use(&u.path) || u.alias.is_some());

    in_progress.remove(&canonical);
    loaded.insert(canonical);
    Ok(model)
}

/// Obtain a canonical path to use for cycle/dedup tracking.
fn canonicalize_for_tracking(
    path: &Path,
    _config: &LoaderConfig<'_>,
) -> Result<PathBuf, LoadError> {
    path.canonicalize()
        .map_err(|e| LoadError::Io(format!("cannot resolve '{}': {e}", path.display())))
}

/// Load the model for a single file without recursing into its imports.
fn load_individual_model(
    path: &Path,
    _config: &LoaderConfig<'_>,
    _written: &mut Vec<PathBuf>,
) -> Result<DomainModel, LoadError> {
    // Parse from .tlang source.
    let source = std::fs::read_to_string(path)
        .map_err(|e| LoadError::Io(format!("cannot read '{}': {e}", path.display())))?;

    parse_domain_model_in_file(&path.to_string_lossy(), &source).map_err(LoadError::Parse)
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use super::*;
    use crate::ast::DomainBlock;
    use crate::manifest::{Dependency, DependencyLocator, Stability};
    use crate::runtime::run_main;
    use std::fs;

    fn write_file(path: &Path, content: &str) {
        if let Some(parent) = path.parent() {
            fs::create_dir_all(parent).expect("create dirs");
        }
        fs::write(path, content).expect("write file");
    }

    fn simple_helper(name: &str) -> String {
        format!(
            r#"
func {name}(): Unit {{
    let x = 1;
}}
"#
        )
    }

    fn tempdir() -> PathBuf {
        use std::sync::atomic::{AtomicU64, Ordering};
        static COUNTER: AtomicU64 = AtomicU64::new(0);
        let id = COUNTER.fetch_add(1, Ordering::SeqCst);
        let pid = std::process::id();
        let path = std::env::temp_dir().join(format!("tlang_loader_test_{pid}_{id}"));
        fs::create_dir_all(&path).expect("create temp dir");
        path
    }

    // -----------------------------------------------------------------------
    // Original tests (path-relative resolution)
    // -----------------------------------------------------------------------

    #[test]
    fn loads_single_file_with_no_imports() {
        let dir = tempdir();
        let main = dir.join("main.tlang");
        write_file(&main, &simple_helper("main"));

        let model = load_program(&main).expect("should load");
        assert_eq!(model.body.len(), 1);
        assert!(matches!(model.body[0], DomainBlock::Helper(_)));
    }

    #[test]
    fn loads_same_folder_import() {
        let dir = tempdir();

        write_file(&dir.join("dep.tlang"), &simple_helper("dep_func"));

        let main_content = format!("use dep\n{}", simple_helper("main_func"));
        write_file(&dir.join("main.tlang"), &main_content);

        let model = load_program(&dir.join("main.tlang")).expect("should load");
        assert_eq!(model.body.len(), 2);
        match &model.body[0] {
            DomainBlock::Helper(h) => assert!(h.content.contains("dep_func")),
            _ => panic!("expected helper block from dep"),
        }
        match &model.body[1] {
            DomainBlock::Helper(h) => assert!(h.content.contains("main_func")),
            _ => panic!("expected helper block from main"),
        }
    }

    #[test]
    fn loads_subfolder_import() {
        let dir = tempdir();

        let sub = dir.join("Sub");
        write_file(&sub.join("Worker.tlang"), &simple_helper("worker_func"));

        let main_content = format!("use Sub.Worker\n{}", simple_helper("entry"));
        write_file(&dir.join("main.tlang"), &main_content);

        let model = load_program(&dir.join("main.tlang")).expect("should load");
        assert_eq!(model.body.len(), 2);
        match &model.body[0] {
            DomainBlock::Helper(h) => assert!(h.content.contains("worker_func")),
            _ => panic!("expected helper from Sub/Worker"),
        }
    }

    #[test]
    fn zero_param_template_loaded_via_use_compiles_and_runs() {
        // Regression test: `lang [kotlin] myTemplate() { ... }` with an explicit
        // empty parameter list `()` must parse correctly when the template is
        // defined in a dependency file and loaded via `use entity.Entity`.
        // Previously the grammar required at least one param inside `()`, so
        // `myTemplate()` triggered a parse error only when the file was loaded
        // as a dependency (the stricter path), while an identical template in
        // the root file happened to be tolerated.
        //
        // Files that declare `expose` are treated as generator/library packages
        // and are exempt from the "lang must be imported" check, so we can use
        // arbitrary language tags here without registering a real generator.
        let dir = tempdir();

        // Dependency file — generator/library package (has `expose`), so the
        // `lang [...]` language-alias check is skipped.  Contains one zero-param
        // template with explicit `()`.
        let entity_content = r#"
expose generate

lang [kotlin] emptyParamTemplate() {
    impl[public class] Empty {
        fun hello(): String {
            return "hi"
        }
    }
}

func generate(): String {
    return "ok"
}
"#;
        let entity_dir = dir.join("entity");
        write_file(&entity_dir.join("Entity.tlang"), entity_content);

        // Main file — also uses `expose` so the lang-alias check is skipped,
        // and contains its own zero-param template with explicit `()`.
        let main_content = r#"
expose run

use entity.Entity

lang [kotlin] rootZeroParam() {
    impl[public class] Root {}
}

func run(): String {
    return "done"
}
"#;
        write_file(&dir.join("Main.tlang"), main_content);

        // Loading (parse + merge) must succeed — no parse error for `()`.
        let model = load_program(&dir.join("Main.tlang"))
            .expect("zero-param template in dependency should load without error");

        // The merged body should contain template blocks from both files.
        let tmpl_count = model
            .body
            .iter()
            .filter(|b| matches!(b, DomainBlock::Template(_)))
            .count();
        assert_eq!(
            tmpl_count, 2,
            "expected 2 template blocks (one from dep, one from main), got {tmpl_count}"
        );
    }

    #[test]
    fn deduplicates_diamond_imports() {
        let dir = tempdir();

        write_file(&dir.join("D.tlang"), &simple_helper("d_func"));

        let b = format!("use D\n{}", simple_helper("b_func"));
        write_file(&dir.join("B.tlang"), &b);

        let c = format!("use D\n{}", simple_helper("c_func"));
        write_file(&dir.join("C.tlang"), &c);

        let main = format!("use B\nuse C\n{}", simple_helper("main_func"));
        write_file(&dir.join("main.tlang"), &main);

        let model = load_program(&dir.join("main.tlang")).expect("should load");

        let d_count = model
            .body
            .iter()
            .filter_map(|b| {
                if let DomainBlock::Helper(h) = b {
                    Some(h.content.as_str())
                } else {
                    None
                }
            })
            .filter(|h| h.contains("d_func"))
            .count();

        assert_eq!(d_count, 1, "D should be loaded exactly once, got {d_count}");
    }

    #[test]
    fn detects_direct_cycle() {
        let dir = tempdir();

        let a_path = dir.join("A.tlang");
        let b_path = dir.join("B.tlang");

        write_file(&a_path, &format!("use B\n{}", simple_helper("a_func")));
        write_file(&b_path, &format!("use A\n{}", simple_helper("b_func")));

        let err = load_program(&a_path).expect_err("should detect cycle");
        assert!(
            matches!(err, LoadError::Cycle(_)),
            "expected Cycle error, got {err}"
        );
    }

    #[test]
    fn error_on_missing_import() {
        let dir = tempdir();

        let main_content = format!("use Missing\n{}", simple_helper("main_func"));
        write_file(&dir.join("main.tlang"), &main_content);

        let err = load_program(&dir.join("main.tlang")).expect_err("should fail");
        assert!(
            matches!(err, LoadError::Io(_)),
            "expected IO error for missing file, got {err}"
        );
    }

    #[test]
    fn error_on_missing_main_file() {
        let dir = tempdir();
        let err = load_program(&dir.join("does_not_exist.tlang")).expect_err("should fail");
        assert!(
            matches!(err, LoadError::Io(_)),
            "expected IO error, got {err}"
        );
    }

    #[test]
    fn builtin_uses_are_preserved() {
        let dir = tempdir();

        let content = r#"
use TLang.Terminal
func main(): String {
    let x = 1
}
"#;
        write_file(&dir.join("main.tlang"), content);

        let model = load_program(&dir.join("main.tlang")).expect("should load");
        assert_eq!(model.header.uses.len(), 1);
        assert_eq!(model.header.uses[0].path, vec!["TLang", "Terminal"]);
    }

    #[test]
    fn deep_subfolder_import() {
        let dir = tempdir();

        let deep = dir.join("A").join("B");
        write_file(&deep.join("C.tlang"), &simple_helper("deep_func"));

        let main_content = format!("use A.B.C\n{}", simple_helper("root_func"));
        write_file(&dir.join("main.tlang"), &main_content);

        // Imports deeper than one directory level are no longer allowed.
        // `use A.B.C` has three segments (two directory levels) and must be
        // rejected with a clear error pointing the user to the correct form.
        let err = load_program(&dir.join("main.tlang"))
            .expect_err("three-segment relative import should be rejected");
        let msg = err.to_string();
        assert!(
            matches!(err, LoadError::Io(_)),
            "expected an Io error for too-deep import, got {err}"
        );
        assert!(
            msg.contains("too deep") || msg.contains("A.B.C"),
            "error should mention the depth limit or the offending path; got: {msg}"
        );
    }

    // -----------------------------------------------------------------------
    // Manifest-aware resolution tests
    // -----------------------------------------------------------------------

    fn make_manifest_with_file_dep(alias: &str, dir: &Path) -> Manifest {
        Manifest {
            name: "TestProject".into(),
            project: "Test".into(),
            organisation: "TLang".into(),
            version: "1.0.0".into(),
            stability: Stability::Alpha,
            release_number: 1,
            author: None,
            website: None,
            license: None,
            compatibility: None,
            dependencies: vec![Dependency {
                locator: DependencyLocator::File {
                    dir: dir.to_path_buf(),
                },
                alias: alias.to_string(),
            }],
            main: None,
            package_type: crate::manifest::PackageType::Library,
            command: None,
        }
    }

    #[test]
    fn resolves_alias_import_from_manifest_file_dep() {
        let lib_dir = tempdir();
        let main_dir = tempdir();

        // Write a file inside the lib directory.
        write_file(&lib_dir.join("Utils.tlang"), &simple_helper("utils_func"));

        // Main file imports via the alias `MyLib.Utils`.
        let main_content = format!("use MyLib.Utils\n{}", simple_helper("main_func"));
        write_file(&main_dir.join("Main.tlang"), &main_content);

        let manifest = make_manifest_with_file_dep("MyLib", &lib_dir);
        let model = load_program_with_manifest(&main_dir.join("Main.tlang"), Some(&manifest))
            .expect("should load with manifest");

        assert_eq!(model.body.len(), 2);
        match &model.body[0] {
            DomainBlock::Helper(h) => assert!(h.content.contains("utils_func")),
            _ => panic!("expected utils helper first"),
        }
        match &model.body[1] {
            DomainBlock::Helper(h) => assert!(h.content.contains("main_func")),
            _ => panic!("expected main helper second"),
        }
    }

    #[test]
    fn resolves_nested_alias_import_from_manifest_file_dep() {
        let lib_dir = tempdir();
        let main_dir = tempdir();

        // Write a file at lib_dir/sub/Deep.tlang
        let sub = lib_dir.join("sub");
        write_file(&sub.join("Deep.tlang"), &simple_helper("deep_func"));

        // Main file imports via the alias `Ext.sub.Deep`.
        let main_content = format!("use Ext.sub.Deep\n{}", simple_helper("entry_func"));
        write_file(&main_dir.join("Main.tlang"), &main_content);

        let manifest = make_manifest_with_file_dep("Ext", &lib_dir);
        let model = load_program_with_manifest(&main_dir.join("Main.tlang"), Some(&manifest))
            .expect("should load with manifest");

        assert_eq!(model.body.len(), 2);
        match &model.body[0] {
            DomainBlock::Helper(h) => assert!(h.content.contains("deep_func")),
            _ => panic!("expected deep helper first"),
        }
    }

    #[test]
    fn non_alias_import_still_resolves_relative_with_manifest() {
        let dir = tempdir();

        write_file(&dir.join("Local.tlang"), &simple_helper("local_func"));

        let main_content = format!("use Local\n{}", simple_helper("main_func"));
        write_file(&dir.join("Main.tlang"), &main_content);

        // Manifest has an unrelated alias — Local should still resolve from cwd.
        let manifest = Manifest {
            name: "X".into(),
            project: "Y".into(),
            organisation: "Z".into(),
            version: "1.0.0".into(),
            stability: Stability::Stable,
            release_number: 1,
            author: None,
            website: None,
            license: None,
            compatibility: None,
            dependencies: vec![Dependency {
                locator: DependencyLocator::File {
                    dir: PathBuf::from("/some/other/dir"),
                },
                alias: "OtherLib".into(),
            }],
            main: None,
            package_type: crate::manifest::PackageType::Library,
            command: None,
        };

        let model = load_program_with_manifest(&dir.join("Main.tlang"), Some(&manifest))
            .expect("should load");
        assert_eq!(model.body.len(), 2);
        match &model.body[0] {
            DomainBlock::Helper(h) => assert!(h.content.contains("local_func")),
            _ => panic!("expected local helper first"),
        }
    }

    // -----------------------------------------------------------------------
    // Tbox (registry dependency) resolution tests
    // -----------------------------------------------------------------------

    fn make_manifest_with_registry_dep(alias: &str, pkg_path: &[&str], version: &str) -> Manifest {
        use crate::manifest::VersionSpec;
        Manifest {
            name: "TestProject".into(),
            project: "Test".into(),
            organisation: "TLang".into(),
            version: "1.0.0".into(),
            stability: Stability::Alpha,
            release_number: 1,
            author: None,
            website: None,
            license: None,
            compatibility: None,
            dependencies: vec![Dependency {
                locator: DependencyLocator::Registry {
                    path: pkg_path.iter().map(|s| s.to_string()).collect(),
                    version: VersionSpec {
                        version: version.to_string(),
                        stability: None,
                        release_number: None,
                    },
                },
                alias: alias.to_string(),
            }],
            main: None,
            package_type: crate::manifest::PackageType::Library,
            command: None,
        }
    }

    #[test]
    fn resolves_registry_dep_from_tbox() {
        let tbox_dir = tempdir();
        let main_dir = tempdir();

        // Lay out the package in the mock tbox:
        // tbox/My/Pkg/1.0.0/Utils.tlang
        let pkg_dir = tbox_dir.join("My").join("Pkg").join("1.0.0");
        write_file(
            &pkg_dir.join("Utils.tlang"),
            &simple_helper("pkg_utils_func"),
        );

        let main_content = format!("use ExtPkg.Utils\n{}", simple_helper("main_func"));
        write_file(&main_dir.join("Main.tlang"), &main_content);

        let manifest = make_manifest_with_registry_dep("ExtPkg", &["My", "Pkg"], "1.0.0");
        let model = load_program_with_manifest_tbox(
            &main_dir.join("Main.tlang"),
            Some(&manifest),
            &tbox_dir,
        )
        .expect("should load registry dep from tbox");

        assert_eq!(model.body.len(), 2);
        match &model.body[0] {
            DomainBlock::Helper(h) => assert!(h.content.contains("pkg_utils_func")),
            _ => panic!("expected pkg helper first"),
        }
        match &model.body[1] {
            DomainBlock::Helper(h) => assert!(h.content.contains("main_func")),
            _ => panic!("expected main helper second"),
        }
    }

    #[test]
    fn registry_dep_not_in_tbox_returns_io_error() {
        let tbox_dir = tempdir(); // empty tbox — package not installed
        let main_dir = tempdir();

        let main_content = format!("use Missing.Utils\n{}", simple_helper("main_func"));
        write_file(&main_dir.join("Main.tlang"), &main_content);

        let manifest = make_manifest_with_registry_dep("Missing", &["Not", "Installed"], "1.0.0");
        let err = load_program_with_manifest_tbox(
            &main_dir.join("Main.tlang"),
            Some(&manifest),
            &tbox_dir,
        )
        .expect_err("should fail when package not in tbox");

        assert!(
            matches!(err, LoadError::Io(_)),
            "expected IO error for missing tbox package, got: {err}"
        );
    }

    // -----------------------------------------------------------------------
    // Bytecode write / prefer-bytecode tests
    // -----------------------------------------------------------------------

    /// Verifies that a function exposed by a file-dep (with `main:` declared in
    /// its manifest) is callable at runtime when the consumer imports it via
    /// `use Alias as lang` syntax.
    ///
    /// This is the exact pattern used by `examples/dashboard-htmx`:
    ///   `use HtmlGen as html`  →  `html.generate(inst)`
    ///
    /// The dep file exposes `greet`, the consumer defines a `generate_html`
    /// bridge that calls `html.greet(name)`, and `main` calls the bridge.
    #[test]
    fn alias_import_exposed_function_is_callable_at_runtime() {
        let lib_dir = tempdir();
        let main_dir = tempdir();

        // Write the "generator" library.  Its manifest declares `main: GenLib`
        // so the loader applies the main-only access rule.
        write_file(
            &lib_dir.join("manifest.yml"),
            "name: GenLib\nproject: Gen\norganisation: Test\nversion: 1.0.0\nstability: alpha\nreleaseNumber: 1\nmain: GenLib\n",
        );
        write_file(
            &lib_dir.join("GenLib.tlang"),
            r#"
expose greet

func greet(name: String): String {
    return "Hello, " + name + "!";
}
"#,
        );

        // Consumer: imports the dep via `use GenLib as gen` and calls `gen.greet`.
        write_file(
            &main_dir.join("Main.tlang"),
            r#"
use GenLib as gen

func generate_html(name: String): String {
    return gen.greet(name);
}

func main(): String {
    return generate_html("World");
}
"#,
        );

        let manifest = make_manifest_with_file_dep("GenLib", &lib_dir);
        let model = load_program_with_manifest(&main_dir.join("Main.tlang"), Some(&manifest))
            .expect("should load");

        let compiled = crate::runtime::compile_from_domain_model(&model).expect("should compile");

        // `greet` from the dep must be present in the merged function table.
        let fn_names: Vec<String> = compiled
            .function_infos()
            .iter()
            .map(|f| f.name.clone())
            .collect();
        assert!(
            fn_names.contains(&"greet".to_string()),
            "dep function `greet` missing from compiled program; \
             functions present: {fn_names:?}",
        );

        let result = run_main(&compiled).expect("main should run");
        assert_eq!(
            result.return_value,
            crate::runtime::Value::String("Hello, World!".to_string()),
        );
    }

    #[test]
    fn compile_to_bytecode_writes_tlangc_files() {
        let dir = tempdir();
        write_file(&dir.join("dep.tlang"), &simple_helper("dep_func"));
        // main.tlang must have a `main` function so the compiled program is valid.
        let main_content = format!("use dep\nfunc main(): String {{ let x = 1; }}\n");
        write_file(&dir.join("main.tlang"), &main_content);

        let project_root = dir.canonicalize().unwrap();
        let (_compiled, written) =
            compile_to_bytecode_files(&dir.join("main.tlang"), &project_root, None)
                .expect("should compile");

        // Only the main .tlangc is written (single-file approach).
        let expected_main = project_root
            .join("target")
            .join("tlang")
            .join("main.tlangc");
        assert!(
            written.contains(&expected_main),
            "expected main.tlangc in written list"
        );
        assert!(expected_main.exists(), "main.tlangc should exist on disk");

        // dep.tlangc is NOT written separately.
        let dep_tlangc = project_root.join("target").join("tlang").join("dep.tlangc");
        assert!(
            !dep_tlangc.exists(),
            "dep.tlangc should not be written in single-bytecode-file approach"
        );
    }

    #[test]
    fn load_prefer_bytecode_uses_tlangc_when_present() {
        let dir = tempdir();
        // Write the dep source.
        write_file(&dir.join("dep.tlang"), &simple_helper("dep_func"));
        // Include a proper `main` function so the program compiles.
        let main_content =
            "use dep\nfunc main(): String { TLang.Terminal.println(\"ok\"); }\n";
        write_file(&dir.join("main.tlang"), main_content);

        let project_root = dir.canonicalize().unwrap();

        // Step 1: compile to bytecode.
        compile_to_bytecode_files(&dir.join("main.tlang"), &project_root, None)
            .expect("should compile to bytecode");

        // Step 2: remove the .tlang source files so we *must* use .tlangc.
        fs::remove_file(dir.join("main.tlang")).unwrap();
        fs::remove_file(dir.join("dep.tlang")).unwrap();

        // Step 3: load via prefer-bytecode — should return CompiledProgram directly.
        let compiled = load_program_prefer_bytecode(&dir.join("main.tlang"), &project_root, None)
            .expect("should load from bytecode");

        // Verify the loaded program actually runs.
        let result = crate::runtime::run_main(&compiled).expect("should run");
        assert_eq!(result.output, "ok\n");
    }
}
