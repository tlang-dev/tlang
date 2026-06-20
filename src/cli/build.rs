// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Project build helpers.
//!
//! Covers file-path resolution, compiling TLang sources with bytecode staleness
//! checks (`compile_if_stale`, `is_bytecode_up_to_date`), running the compiled
//! program (`run_target`), and executing CLI package scripts (`exec_cli_package`).

use std::path::{Path, PathBuf};

use tlang::runtime::{RunOptions, Value};
use tlang::{
    compile_from_domain_model, compile_to_bytecode_files, is_extracted, load_manifest,
    load_program_with_manifest, resolve_main, try_load_manifest,
};

use super::market::cli_dir;
use super::util::{format_elapsed, print_primitive_return};

pub fn resolve_paths(
    target: Option<&Path>,
) -> Result<(PathBuf, PathBuf, Option<tlang::Manifest>), String> {
    let target = match target {
        Some(path) => path.to_path_buf(),
        None => {
            std::env::current_dir().map_err(|e| format!("cannot read current directory: {e}"))?
        }
    };

    let (project_root, main_path) = if target.is_dir() {
        (target.clone(), None)
    } else {
        let project_root = target
            .parent()
            .map(Path::to_path_buf)
            .unwrap_or_else(|| PathBuf::from("."));
        (project_root, Some(target))
    };

    let manifest = try_load_manifest(&project_root).map_err(|e| {
        format!(
            "failed to load manifest from '{}': {e}",
            project_root.display()
        )
    })?;
    let main_path = main_path.unwrap_or_else(|| resolve_main(&project_root, manifest.as_ref()));
    Ok((project_root, main_path, manifest))
}

/// Compile a target in-memory (no bytecode output).
pub fn compile_target(
    target: Option<&Path>,
) -> Result<(PathBuf, tlang::runtime::CompiledProgram), String> {
    let (_project_root, main_path, manifest) = resolve_paths(target)?;
    let model = load_program_with_manifest(&main_path, manifest.as_ref())
        .map_err(|e| format!("failed to load '{}': {e}", main_path.display()))?;
    let compiled = compile_from_domain_model(&model)
        .map_err(|e| format!("failed to compile '{}': {e}", main_path.display()))?;
    Ok((main_path, compiled))
}

/// Returns `true` when every `*.tlang` source file under `project_root` is
/// older than (or the same age as) the bytecode file at `bc_path`.
/// If `bc_path` does not exist yet, returns `false`.
pub fn is_bytecode_up_to_date(project_root: &Path, bc_path: &Path) -> bool {
    let bc_mtime = match std::fs::metadata(bc_path).and_then(|m| m.modified()) {
        Ok(t) => t,
        Err(_) => return false, // no bytecode → must compile
    };

    // Walk project_root for .tlang files, skipping target/.
    let target_dir = project_root.join("target");
    let mut stack = vec![project_root.to_path_buf()];
    while let Some(dir) = stack.pop() {
        let entries = match std::fs::read_dir(&dir) {
            Ok(e) => e,
            Err(_) => continue,
        };
        for entry in entries.flatten() {
            let path = entry.path();
            if path.starts_with(&target_dir) {
                continue;
            }
            if path.is_dir() {
                stack.push(path);
            } else if path.extension().and_then(|e| e.to_str()) == Some("tlang")
                && let Ok(src_mtime) = std::fs::metadata(&path).and_then(|m| m.modified())
                && src_mtime > bc_mtime
            {
                return false; // source is newer → stale
            }
        }
    }
    true
}

pub fn canonical_project_root(project_root: PathBuf) -> Result<PathBuf, String> {
    project_root.canonicalize().map_err(|e| {
        format!(
            "cannot canonicalize project root '{}': {e}",
            project_root.display()
        )
    })
}

pub fn absolutize_from_cwd(path: &Path, fallback_root: &Path) -> PathBuf {
    if path.is_absolute() {
        path.to_path_buf()
    } else {
        std::env::current_dir()
            .unwrap_or_else(|_| fallback_root.to_path_buf())
            .join(path)
    }
}

pub fn expected_bytecode_for_main(main_path: &Path, project_root: &Path) -> Option<PathBuf> {
    use tlang::bytecode::bytecode_path;
    let main_abs = absolutize_from_cwd(main_path, project_root);
    bytecode_path(&main_abs, project_root)
}

pub fn compile_if_stale(
    main_path: &Path,
    project_root: &Path,
    manifest: Option<&tlang::Manifest>,
) -> Result<Option<Vec<PathBuf>>, String> {
    let needs_compile = expected_bytecode_for_main(main_path, project_root)
        .as_ref()
        .map(|bc| !is_bytecode_up_to_date(project_root, bc))
        .unwrap_or(true);

    if !needs_compile {
        return Ok(None);
    }

    let (_, written) = compile_to_bytecode_files(main_path, project_root, manifest)
        .map_err(|e| format!("failed to compile '{}': {e}", main_path.display()))?;
    Ok(Some(written))
}

pub fn load_if_bytecode_is_fresh(
    main_path: &Path,
    project_root: &Path,
) -> Result<Option<(PathBuf, tlang::runtime::CompiledProgram)>, String> {
    let Some(bc_path) = expected_bytecode_for_main(main_path, project_root) else {
        return Ok(None);
    };
    if !is_bytecode_up_to_date(project_root, &bc_path) {
        return Ok(None);
    }
    let compiled = tlang::bytecode::read_bytecode(&bc_path)
        .map_err(|e| format!("failed to load bytecode '{}': {e}", bc_path.display()))?;
    Ok(Some((bc_path, compiled)))
}

/// Compile a target and write bytecode files; returns the main path, compiled
/// program, and list of written `.tlangc` paths.
pub fn compile_target_emit(
    target: Option<&Path>,
) -> Result<(PathBuf, tlang::runtime::CompiledProgram, Vec<PathBuf>), String> {
    let (project_root, main_path, manifest) = resolve_paths(target)?;
    let project_root = canonical_project_root(project_root)?;

    let (compiled, written) =
        compile_to_bytecode_files(&main_path, &project_root, manifest.as_ref())
            .map_err(|e| format!("failed to compile '{}': {e}", main_path.display()))?;
    Ok((main_path, compiled, written))
}

/// Load a target from its compiled `.tlangc` bytecode file only.
/// Errors if no bytecode exists — run `tlang compile` first.
pub fn run_target(
    target: Option<&Path>,
) -> Result<(PathBuf, PathBuf, tlang::runtime::CompiledProgram), String> {
    use tlang::bytecode;

    let (project_root, main_path, manifest) = resolve_paths(target)?;
    let _ = manifest;
    let project_root = canonical_project_root(project_root)?;

    let main_abs = if main_path.is_absolute() {
        main_path.clone()
    } else {
        std::env::current_dir()
            .map_err(|e| format!("cannot get current dir: {e}"))?
            .join(&main_path)
    };

    let bc_path = bytecode::bytecode_path(&main_abs, &project_root).ok_or_else(|| {
        format!(
            "cannot determine bytecode path for '{}' — run `tlang compile` first",
            main_path.display()
        )
    })?;

    if !bc_path.exists() {
        return Err(format!(
            "no compiled bytecode found for '{}' (expected '{}')\n\
             Run `tlang compile {}` first.",
            main_path.display(),
            bc_path.display(),
            main_path.display(),
        ));
    }

    let compiled = bytecode::read_bytecode(&bc_path)
        .map_err(|e| format!("failed to load bytecode '{}': {e}", bc_path.display()))?;

    Ok((main_path, project_root, compiled))
}

// ---------------------------------------------------------------------------
// exec --list
// ---------------------------------------------------------------------------

pub fn list_exec_commands(target: Option<&Path>) -> Result<(), String> {
    let mut any = false;

    // ── Project commands ──────────────────────────────────────────────────────
    let project_path = match target {
        Some(p) => p.to_path_buf(),
        None => std::env::current_dir()
            .map_err(|e| format!("cannot read current directory: {e}"))?,
    };
    match run_target(Some(&project_path)) {
        Ok((_, root, compiled)) => {
            let exposed = compiled.exposed_names();
            if !exposed.is_empty() {
                let label = root.file_name()
                    .map(|n| n.to_string_lossy().into_owned())
                    .unwrap_or_else(|| root.display().to_string());
                println!();
                println!("  Project commands  ({}/):", label);
                println!();

                // Build a map from name → FunctionInfo for signature lookup.
                let infos: std::collections::HashMap<String, _> = compiled
                    .function_infos()
                    .into_iter()
                    .map(|fi| (fi.name.clone(), fi))
                    .collect();

                let mut names: Vec<&String> = exposed.iter().collect();
                names.sort();
                for name in names {
                    match infos.get(name) {
                        Some(fi) => {
                            let sig = fi.signature();
                            match &fi.doc {
                                Some(doc) => {
                                    let summary = doc.lines().next().unwrap_or("").trim();
                                    println!("    {sig}");
                                    println!("      {summary}");
                                    println!();
                                }
                                None => println!("    {sig}"),
                            }
                        }
                        None => println!("    {name}()"),
                    }
                }
                any = true;
            }
        }
        Err(_) => {} // no bytecode — skip project section silently
    }

    // ── Global CLI commands ───────────────────────────────────────────────────
    let cli = cli_dir();
    if cli.is_dir() {
        let mut pkgs: Vec<String> = std::fs::read_dir(&cli)
            .map_err(|e| format!("cannot read CLI dir '{}': {e}", cli.display()))?
            .filter_map(|entry| entry.ok())
            .filter_map(|entry| {
                let path = entry.path();
                if path.extension().and_then(|e| e.to_str()) == Some("tbag") {
                    path.file_stem()
                        .and_then(|s| s.to_str())
                        .map(|s| s.to_string())
                } else {
                    None
                }
            })
            .collect();
        pkgs.sort();

        if !pkgs.is_empty() {
            println!();
            println!("  Global commands  (~/.tlang/cli/):");
            println!();
            for pkg in &pkgs {
                println!("    {pkg}");
            }
            any = true;
        }
    }

    if !any {
        println!();
        println!("  No commands found.");
        println!();
        println!("  Project commands:  add `expose myFunc` in your .tlang file, then `tlang compile`.");
        println!("  Global commands:   `tlang install <Org/Project/Name:version>`");
    }

    println!();
    Ok(())
}

// ---------------------------------------------------------------------------
// CLI package support
// ---------------------------------------------------------------------------

/// Run the `main` function of a CLI package installed at `~/.tlang/cli/{name}.tbag`.
///
/// Extracts the tbag on first use into `~/.tlang/cli/{name}/`.  Only `main` is
/// callable — `expose`d functions are intentionally not available.
pub fn exec_cli_package(
    name: &str,
    args: &[String],
    force_regen: bool,
    t0: std::time::Instant,
) -> Result<(), String> {
    use tlang::bytecode;

    let cli = cli_dir();
    let tbag_path = cli.join(format!("{name}.tbag"));

    if !tbag_path.exists() {
        return Err(format!(
            "no exposed function `{name}` in current project and no CLI package `{name}` installed\n\
             Install with: tlang install <Org/Project/{name}:version>"
        ));
    }

    let extract_dir = cli.join(name);
    if !is_extracted(&extract_dir) {
        println!("Extracting '{name}'…");
        tlang::extract_tbag(&tbag_path, &extract_dir)
            .map_err(|e| format!("failed to extract CLI package '{name}': {e}"))?;
    }

    let manifest_path = extract_dir.join("manifest.yml");
    let manifest = if manifest_path.exists() {
        match load_manifest(&manifest_path) {
            Ok(m) => Some(m),
            Err(e) => return Err(format!("failed to read manifest of '{name}': {e}")),
        }
    } else {
        None
    };

    let main_path = resolve_main(&extract_dir, manifest.as_ref());

    let extract_canon = extract_dir
        .canonicalize()
        .map_err(|e| format!("cannot canonicalize CLI package dir: {e}"))?;

    let main_abs = if main_path.is_absolute() {
        main_path.clone()
    } else {
        extract_canon.join(&main_path)
    };

    let bc_path = bytecode::bytecode_path(&main_abs, &extract_canon).ok_or_else(|| {
        format!("cannot determine bytecode path for CLI package '{name}'")
    })?;

    if !bc_path.exists() {
        return Err(format!(
            "no compiled bytecode found for CLI package '{name}' — the package may be corrupt\n\
             Try reinstalling: tlang install <Org/Project/{name}:version>"
        ));
    }

    let compiled = bytecode::read_bytecode(&bc_path)
        .map_err(|e| format!("failed to load bytecode for '{name}': {e}"))?;

    std::env::set_current_dir(&extract_canon)
        .map_err(|e| format!("cannot cd to CLI package dir: {e}"))?;

    let args_val = if args.is_empty() {
        Vec::new()
    } else {
        vec![Value::List(
            args.iter()
                .map(|a| Value::String(a.clone()))
                .collect(),
        )]
    };

    let result = tlang::runtime::run_main_with_options(
        &compiled,
        RunOptions {
            args: args_val,
            force_regen,
        },
    )
    .map_err(|e| format!("CLI package '{name}' failed: {e}"))?;

    print!("{}", result.output);
    print_primitive_return(&result.return_value);
    println!("Done in {}", format_elapsed(t0.elapsed()));
    Ok(())
}
