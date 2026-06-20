// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! CLI command definitions and top-level dispatch.
//!
//! Defines the [`Command`] enum, the [`Cli`] struct that holds parsed
//! arguments, and the `run()` function that dispatches every subcommand
//! to the appropriate handler in the sibling modules.

pub mod args;
pub mod build;
pub mod lsp_ctl;
pub mod market;
pub mod util;

use std::path::PathBuf;
use std::time::Duration;

use tlang::runtime::{RunOptions, Value};
use tlang::{
    PackageType, compile_to_bytecode_files, own_tbox_tbag_path, pack_tbag, parse_manifest,
    push_tbag, read_manifest_from_tbag, tbox_path,
};

use build::{
    canonical_project_root, compile_if_stale, expected_bytecode_for_main,
    is_bytecode_up_to_date, list_exec_commands, load_if_bytecode_is_fresh, resolve_paths,
    run_target, exec_cli_package,
};
use lsp_ctl::stop_lsp_server;
use market::{
    cli_dir, ensure_deps_from_market, find_tbag_in_tbox, install_from_market, load_market_token,
    login_to_market, logout_from_market, market_base_url, parse_package_ref, pull_from_market,
};
use util::{format_elapsed, load_env_file, print_primitive_return};

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum Command {
    Compile,
    Run,
    Both,
    Exec,
    Dev,
    Clean,
    Init,
    Package,
    Push,
    Pull,
    Search,
    Install,
    Login,
    Logout,
    LspServer,
    LspStop,
    LspRestart,
    McpServer,
    Test,
    Help,
    Version,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Cli {
    pub command: Command,
    /// Positional target path — a project directory or `.tlang` file.
    pub target: Option<PathBuf>,
    /// `--in-memory`: skip writing bytecode files when compiling-and-running.
    pub in_memory: bool,
    /// `--live`: watch for source changes and compile automatically.
    pub live: bool,
    /// `--force-regen`: treat `>>?` (write-once) output paths as `>>` (always-
    /// overwrite) so that scaffold files are regenerated after a template change.
    /// On `compile`, also executes the program after compiling.
    pub force_regen: bool,
    /// For `exec`: the name of the exposed function to invoke.
    pub exec_func: Option<String>,
    /// For `exec`: raw string arguments forwarded to the function.
    pub exec_args: Vec<String>,
    /// For `install`: the package reference string, e.g. `Org/Project/Name:1.0.0:alpha:1`.
    pub install_pkg: Option<String>,
    /// For `pull`: the package name (and optional version) to pull, e.g. `Kotlin` or `Kotlin:1.0.0`.
    pub pull_pkg: Option<String>,
    /// For `search`: the query string.
    pub search_query: Option<String>,
    /// For `push` / `pull` / `search`: target the local dev server (`http://localhost:8080`) instead of prod.
    pub market_dev: bool,
    /// `exec --list`: list all available commands instead of running one.
    pub list_commands: bool,
}

impl Cli {
    /// Return the effective project path: `-p/--path` (stored in `target`)
    /// takes precedence over the positional argument, which is the same field.
    /// Falls back to the current working directory.
    pub fn project_path(&self) -> Result<PathBuf, String> {
        match &self.target {
            Some(p) => Ok(p.clone()),
            None => {
                std::env::current_dir().map_err(|e| format!("cannot read current directory: {e}"))
            }
        }
    }
}

pub fn run(cli: Cli, program_name: &str) -> Result<(), String> {
    match cli.command {
        Command::Compile => {
            let (project_root, main_path, manifest) = resolve_paths(Some(&cli.project_path()?))?;
            let project_root = canonical_project_root(project_root)?;
            if let Some(ref m) = manifest {
                let base_url = market_base_url(false);
                let tbox = tbox_path();
                ensure_deps_from_market(m, &tbox, &base_url)?;
            }
            if cli.live {
                println!(
                    "Watching '{}' for changes (Ctrl+C to stop)…",
                    main_path.display()
                );
                loop {
                    let t0 = std::time::Instant::now();
                    if let Some(written) =
                        compile_if_stale(&main_path, &project_root, manifest.as_ref())?
                    {
                        println!("Compiled {}", main_path.display());
                        for path in &written {
                            println!("  wrote {}", path.display());
                        }
                        if cli.force_regen {
                            // Also run the program so >>? files are regenerated.
                            let compiled =
                                tlang::bytecode::read_bytecode(&written[0]).map_err(|e| {
                                    format!("failed to load bytecode after compile: {e}")
                                })?;
                            std::env::set_current_dir(&project_root)
                                .map_err(|e| format!("cannot cd to project root: {e}"))?;
                            let result = tlang::runtime::run_main_with_options(
                                &compiled,
                                RunOptions {
                                    force_regen: true,
                                    ..Default::default()
                                },
                            )
                            .map_err(|e| format!("runtime failed: {e}"))?;
                            print!("{}", result.output);
                            print_primitive_return(&result.return_value);
                        }
                        println!("Done in {}", format_elapsed(t0.elapsed()));
                    }
                    std::thread::sleep(Duration::from_millis(500));
                }
            } else {
                let t0 = std::time::Instant::now();
                let compiled_program = match compile_if_stale(
                    &main_path,
                    &project_root,
                    manifest.as_ref(),
                )? {
                    Some(written) => {
                        println!("Compiled {}", main_path.display());
                        for path in &written {
                            println!("  wrote {}", path.display());
                        }
                        if cli.force_regen {
                            // Load the freshly-written bytecode for execution.
                            Some(tlang::bytecode::read_bytecode(&written[0]).map_err(|e| {
                                format!("failed to load bytecode after compile: {e}")
                            })?)
                        } else {
                            None
                        }
                    }
                    None => {
                        println!("Compiled {} — up to date, skipping", main_path.display());
                        if cli.force_regen {
                            // Bytecode is fresh; load it so we can run.
                            Some(
                                    load_if_bytecode_is_fresh(&main_path, &project_root)?
                                        .map(|(_, p)| p)
                                        .ok_or_else(|| {
                                            format!(
                                                "cannot load bytecode for '{}' — run `tlang compile` first",
                                                main_path.display()
                                            )
                                        })?,
                                )
                        } else {
                            None
                        }
                    }
                };
                if let Some(compiled) = compiled_program {
                    std::env::set_current_dir(&project_root)
                        .map_err(|e| format!("cannot cd to project root: {e}"))?;
                    let result = tlang::runtime::run_main_with_options(
                        &compiled,
                        RunOptions {
                            force_regen: true,
                            ..Default::default()
                        },
                    )
                    .map_err(|e| format!("runtime failed: {e}"))?;
                    print!("{}", result.output);
                    print_primitive_return(&result.return_value);
                }
                println!("Done in {}", format_elapsed(t0.elapsed()));
                Ok(())
            }
        }
        Command::Run => {
            let t0 = std::time::Instant::now();
            let (_, project_root, compiled) = run_target(Some(&cli.project_path()?))?;
            std::env::set_current_dir(&project_root)
                .map_err(|e| format!("cannot cd to project root: {e}"))?;
            // Program arguments can be passed after `--` on the command line.
            // e.g. `tlang run -- arg1 arg2`
            let raw_args: Vec<String> = std::env::args()
                .skip_while(|a| a != "--")
                .skip(1) // skip the `--` itself
                .collect();
            let args = if raw_args.is_empty() {
                Vec::new()
            } else {
                vec![Value::List(
                    raw_args
                        .into_iter()
                        .map(Value::String)
                        .collect(),
                )]
            };
            let result = tlang::runtime::run_main_with_options(
                &compiled,
                RunOptions {
                    args,
                    force_regen: cli.force_regen,
                },
            )
            .map_err(|e| format!("runtime failed: {e}"))?;
            print!("{}", result.output);
            print_primitive_return(&result.return_value);
            println!("Done in {}", format_elapsed(t0.elapsed()));
            Ok(())
        }
        Command::Both => {
            let t0 = std::time::Instant::now();
            // Compile from source first, then run.
            // By default bytecode is written to disk; --in-memory skips that.
            let (_main_path, project_root, compiled) = {
                let (project_root, main_path, manifest) =
                    resolve_paths(Some(&cli.project_path()?))?;
                let project_root = canonical_project_root(project_root)?;
                if let Some(ref m) = manifest {
                    let base_url = market_base_url(false);
                    let tbox = tbox_path();
                    ensure_deps_from_market(m, &tbox, &base_url)?;
                }
                if cli.in_memory {
                    let model = tlang::load_program_with_manifest(&main_path, manifest.as_ref())
                        .map_err(|e| format!("failed to load '{}': {e}", main_path.display()))?;
                    let compiled = tlang::compile_from_domain_model(&model)
                        .map_err(|e| format!("failed to compile '{}': {e}", main_path.display()))?;
                    println!("Compiled {}", main_path.display());
                    (main_path, project_root, compiled)
                } else if let Some((bc_path, compiled)) =
                    load_if_bytecode_is_fresh(&main_path, &project_root)?
                {
                    println!("Compiled {} — up to date, skipping", main_path.display());
                    println!("  using {}", bc_path.display());
                    (main_path, project_root, compiled)
                } else {
                    let (compiled, written) =
                        compile_to_bytecode_files(&main_path, &project_root, manifest.as_ref())
                            .map_err(|e| {
                                format!("failed to compile '{}': {e}", main_path.display())
                            })?;
                    println!("Compiled {}", main_path.display());
                    for path in &written {
                        println!("  wrote {}", path.display());
                    }
                    (main_path, project_root, compiled)
                }
            };
            std::env::set_current_dir(&project_root)
                .map_err(|e| format!("cannot cd to project root: {e}"))?;
            let raw_args: Vec<String> =
                std::env::args().skip_while(|a| a != "--").skip(1).collect();
            let args = if raw_args.is_empty() {
                Vec::new()
            } else {
                vec![Value::List(
                    raw_args
                        .into_iter()
                        .map(Value::String)
                        .collect(),
                )]
            };
            let result = tlang::runtime::run_main_with_options(
                &compiled,
                RunOptions {
                    args,
                    force_regen: cli.force_regen,
                },
            )
            .map_err(|e| format!("runtime failed: {e}"))?;
            print!("{}", result.output);
            print_primitive_return(&result.return_value);
            println!("Done in {}", format_elapsed(t0.elapsed()));
            Ok(())
        }
        Command::Exec => {
            let t0 = std::time::Instant::now();

            if cli.list_commands {
                return list_exec_commands(cli.target.as_deref());
            }

            let func_name = cli
                .exec_func
                .as_deref()
                .expect("exec_func is always set for Command::Exec when not --list");

            // Try the current project first; fall back to an installed CLI package
            // when the project has no bytecode or the function is not exposed.
            let project_result = run_target(Some(&cli.project_path()?));

            let use_cli_fallback = match &project_result {
                Err(_) => true,
                Ok((_, _, compiled)) => {
                    // Fall back when the function is absent from the program entirely,
                    // or when exposes are declared and it isn't listed there.
                    !compiled.has_function(func_name)
                        || (!compiled.exposes.is_empty()
                            && !compiled.exposes.contains(&func_name.to_string()))
                }
            };

            if use_cli_fallback {
                return exec_cli_package(func_name, &cli.exec_args, cli.force_regen, t0);
            }

            let (_, project_root, compiled) = project_result.unwrap();
            std::env::set_current_dir(&project_root)
                .map_err(|e| format!("cannot cd to project root: {e}"))?;

            // Load .tlang.env from the project root (if present).
            let env_vars = load_env_file(&project_root)?;
            if !env_vars.is_empty() {
                println!("  Loaded {} var(s) from .tlang.env", env_vars.len());
                for key in {
                    let mut keys: Vec<_> = env_vars.keys().collect();
                    keys.sort();
                    keys
                } {
                    println!("    {key}=***");
                }
            }

            let result = tlang::runtime::run_exposed_function(
                &compiled,
                func_name,
                cli.exec_args.clone(),
                cli.force_regen,
            )
            .map_err(|e| format!("exec failed: {e}"))?;

            print!("{}", result.output);
            print_primitive_return(&result.return_value);
            println!("Done in {}", format_elapsed(t0.elapsed()));
            Ok(())
        }

        Command::Dev => {
            let (project_root, main_path, manifest) =
                resolve_paths(Some(&cli.project_path()?))?;
            let project_root = canonical_project_root(project_root)?;

            if let Some(ref m) = manifest {
                let base_url = market_base_url(false);
                let tbox = tbox_path();
                ensure_deps_from_market(m, &tbox, &base_url)?;
            }

            // Load .tlang.env once at startup.
            let env_vars = load_env_file(&project_root)?;
            if !env_vars.is_empty() {
                println!("  Loaded {} var(s) from .tlang.env", env_vars.len());
                for key in {
                    let mut keys: Vec<_> = env_vars.keys().collect();
                    keys.sort();
                    keys
                } {
                    println!("    {key}=***");
                }
            }

            println!(
                "TLang dev — watching '{}' for changes (Ctrl+C to stop)…",
                main_path.display()
            );

            // Helper closure: compile if stale, load bytecode, call `dev`.
            let run_dev = |recompiled: bool| -> Result<(), String> {
                let bc_path =
                    expected_bytecode_for_main(&main_path, &project_root).ok_or_else(|| {
                        format!(
                            "cannot determine bytecode path for '{}'",
                            main_path.display()
                        )
                    })?;
                let compiled = tlang::bytecode::read_bytecode(&bc_path)
                    .map_err(|e| format!("failed to load bytecode: {e}"))?;

                std::env::set_current_dir(&project_root)
                    .map_err(|e| format!("cannot cd to project root: {e}"))?;

                if recompiled {
                    println!("  ↺ TLang sources changed — regenerating…");
                }

                let t = std::time::Instant::now();
                let result = tlang::runtime::run_exposed_function(
                    &compiled,
                    "dev",
                    cli.exec_args.clone(),
                    false,
                )
                .map_err(|e| format!("dev function failed: {e}"))?;

                print!("{}", result.output);
                print_primitive_return(&result.return_value);
                println!("Done in {}", format_elapsed(t.elapsed()));
                Ok(())
            };

            // Initial compile + first dev call (codegen only).
            let t0 = std::time::Instant::now();
            compile_if_stale(&main_path, &project_root, manifest.as_ref())?;
            println!("Compiled {} in {}", main_path.display(), format_elapsed(t0.elapsed()));
            run_dev(false)?;

            // Spawn Quarkus in a background thread so its logs stream live to
            // the terminal.  We keep the Child alive via wait() so inherited
            // stdio stays connected for the full lifetime of the process.
            let quarkus_port = cli.exec_args.first().map(|s| s.as_str()).unwrap_or("8080");
            let quarkus_dir = project_root
                .join(cli.exec_args.get(1).map(|s| s.as_str()).unwrap_or("output"));
            if quarkus_dir.join("start-dev.sh").exists() {
                let already_running = std::process::Command::new("sh")
                    .arg("-c")
                    .arg(format!("lsof -ti:{quarkus_port} 2>/dev/null | head -1"))
                    .output()
                    .map(|o| !String::from_utf8_lossy(&o.stdout).trim().is_empty())
                    .unwrap_or(false);
                if already_running {
                    println!("  ↺ Quarkus already running on :{quarkus_port} — hot-reload active");
                } else {
                    println!("  ▶ Starting Quarkus (logs below)…");
                    std::thread::spawn(move || {
                        match std::process::Command::new("bash")
                            .arg("start-dev.sh")
                            .current_dir(&quarkus_dir)
                            .stdin(std::process::Stdio::null())
                            .stdout(std::process::Stdio::inherit())
                            .stderr(std::process::Stdio::inherit())
                            .spawn()
                            .and_then(|mut c| c.wait())
                        {
                            Ok(s) => eprintln!("\n[tlang] Quarkus exited ({s})"),
                            Err(e) => eprintln!("\n[tlang] Failed to start Quarkus: {e}"),
                        }
                    });
                }
            }

            // Watch loop: re-run codegen on every .tlang source change.
            // Quarkus picks up the regenerated .kt files via its hot-reload.
            loop {
                std::thread::sleep(Duration::from_millis(500));
                if compile_if_stale(&main_path, &project_root, manifest.as_ref())?.is_some() {
                    run_dev(true)?;
                }
            }
        }

        Command::Clean => {
            let (project_root, _, _) = resolve_paths(Some(&cli.project_path()?))?;
            let project_root = canonical_project_root(project_root)?;
            let target = project_root.join(tlang::bytecode::BYTECODE_TARGET_DIR);
            if target.exists() {
                std::fs::remove_dir_all(&target)
                    .map_err(|e| format!("failed to remove '{}': {e}", target.display()))?;
                println!("Removed {}", target.display());
            } else {
                println!("Nothing to clean at {}", target.display());
            }
            Ok(())
        }
        Command::Init => {
            let dir = cli.project_path()?;

            std::fs::create_dir_all(&dir)
                .map_err(|e| format!("cannot create directory '{}': {e}", dir.display()))?;

            let manifest_path = dir.join("manifest.yml");
            let main_path = dir.join("Main.tlang");

            if manifest_path.exists() || main_path.exists() {
                return Err(format!(
                    "directory '{}' already contains a manifest.yml or Main.tlang",
                    dir.display()
                ));
            }

            // Write a .gitignore (skip if already present).
            let gitignore_path = dir.join(".gitignore");
            if !gitignore_path.exists() {
                let gitignore_content = "# TLang build output\ntarget/\n\n# Local env vars — do not commit secrets\n.tlang.env\n";
                std::fs::write(&gitignore_path, gitignore_content)
                    .map_err(|e| format!("failed to write .gitignore: {e}"))?;
            }

            // Write a .tlang.env.example so users know what vars to set.
            let env_example_path = dir.join(".tlang.env.example");
            if !env_example_path.exists() {
                let env_example_content = "# Copy this file to .tlang.env and fill in your values.\n# .tlang.env is gitignored — never commit real secrets.\n\n# DB_URL=jdbc:postgresql://localhost:5432/mydb\n# DB_USER=dev\n# DB_PASSWORD=secret\n";
                std::fs::write(&env_example_path, env_example_content)
                    .map_err(|e| format!("failed to write .tlang.env.example: {e}"))?;
            }

            let manifest_content = "# TLang project manifest\n\
                 \n\
                 name: HelloWorld\n\
                 project: HelloWorldProject\n\
                 organisation: MyOrg\n\
                 version: 1.0.0\n\
                 stability: alpha\n\
                 releaseNumber: 1\n\
                 \n\
                 dependencies:\n\
                 \x20 - TLangGen/KotlinGen/Kotlin 1.0.0:alpha:1 KotlinGen\n"
                .to_string();

            let main_content = concat!(
                "use TLang.Generator
",
                "use TLang.File
",
                "use KotlinGen as kotlin
",
                "
",
                "lang [kotlin] helloWorld(pkg: String) {
",
                "    pkg ${pkg}
",
                "    impl[public class] Main {
",
                "        fun main(args: Array<String>) {
",
                "            println(\"Hello, World!\")
",
                "        }
",
                "    }
",
                "}
",
                "
",
                "func main(): String {
",
                "    let pkg    = HelloWorld.package;
",
                "    let output = Generator.generate(helloWorld(pkg));
",
                "    File.write(\"output/Main.kt\", output);
",
                "    return output;
",
                "}
",
                "
",
                "set HelloWorld {
",
                "    package: \"com.example\"
",
                "}
"
            );

            let prompts_dir = dir.join("prompts");

            let index_content = include_str!("../../prompts/tlang-index.md");
            let project_content = include_str!("../../prompts/tlang-project.md");
            let templates_content = include_str!("../../prompts/tlang-templates.md");
            let helpers_content = include_str!("../../prompts/tlang-helpers.md");
            let patterns_content = include_str!("../../prompts/tlang-patterns.md");
            let mistakes_content = include_str!("../../prompts/tlang-mistakes.md");

            std::fs::write(&manifest_path, &manifest_content)
                .map_err(|e| format!("failed to write manifest.yml: {e}"))?;
            std::fs::write(&main_path, main_content)
                .map_err(|e| format!("failed to write Main.tlang: {e}"))?;

            let output_dir = dir.join("output");
            std::fs::create_dir_all(&output_dir)
                .map_err(|e| format!("cannot create output/ directory: {e}"))?;

            std::fs::create_dir_all(&prompts_dir)
                .map_err(|e| format!("cannot create prompts/ directory: {e}"))?;

            let prompt_files = [
                ("tlang-index.md", index_content),
                ("tlang-project.md", project_content),
                ("tlang-templates.md", templates_content),
                ("tlang-helpers.md", helpers_content),
                ("tlang-patterns.md", patterns_content),
                ("tlang-mistakes.md", mistakes_content),
            ];
            for (filename, content) in &prompt_files {
                let path = prompts_dir.join(filename);
                std::fs::write(&path, content)
                    .map_err(|e| format!("failed to write prompts/{filename}: {e}"))?;
            }

            println!("Initialised TLang project in \'{}\'", dir.display());
            println!("  wrote {}", manifest_path.display());
            println!("  wrote {}", main_path.display());
            println!("  created {}", output_dir.display());
            for (filename, _) in &prompt_files {
                println!("  wrote {}", prompts_dir.join(filename).display());
            }
            println!("  wrote {}", dir.join(".gitignore").display());
            println!("  wrote {}", dir.join(".tlang.env.example").display());
            println!();
            println!("Env vars:  copy .tlang.env.example → .tlang.env and fill in secrets");
            println!("Run with:  tlang run {}", dir.display());
            println!(
                "The generated Kotlin will be written to {}/output/Main.kt",
                dir.display()
            );
            Ok(())
        }
        Command::Package => {
            let t0 = std::time::Instant::now();

            // Resolve project root and manifest — `package` requires a manifest.
            let (project_root, main_path, manifest) = resolve_paths(Some(&cli.project_path()?))?;
            let project_root = canonical_project_root(project_root)?;
            let manifest = manifest.ok_or_else(|| {
                "the `package` command requires a manifest.yml in the project directory".to_string()
            })?;

            println!(
                "Packaging {} v{} ({}/{})",
                manifest.name, manifest.version, manifest.organisation, manifest.project,
            );
            println!();

            // ── Step 1: compile if needed ────────────────────────────────────
            // Compute where the bytecode would live so we can check staleness.
            // main_path may be relative to CWD, so absolutize it against CWD
            // (not project_root) before calling bytecode_path, which uses
            // strip_prefix(project_root) internally.
            let main_path_abs = if main_path.is_absolute() {
                main_path.clone()
            } else {
                std::env::current_dir()
                    .unwrap_or_else(|_| project_root.clone())
                    .join(&main_path)
            };
            let expected_bc = {
                use tlang::bytecode::bytecode_path;
                bytecode_path(&main_path_abs, &project_root)
            };

            let already_fresh = expected_bc
                .as_ref()
                .map(|bc| is_bytecode_up_to_date(&project_root, bc))
                .unwrap_or(false);

            if already_fresh {
                println!("  [1/3] Compile   — up to date, skipping");
            } else {
                println!("  [1/3] Compiling {}…", main_path.display());
                let (_, written) =
                    compile_to_bytecode_files(&main_path, &project_root, Some(&manifest))
                        .map_err(|e| format!("failed to compile '{}': {e}", main_path.display()))?;
                for path in &written {
                    println!("        wrote {}", path.display());
                }
                println!("        OK");
            }

            // ── Step 2: pack source + bytecode into a .tbag archive ──────────
            let manifest_path = {
                let p = project_root.join("manifest.yml");
                if p.exists() {
                    p
                } else {
                    project_root.join("manifest.yaml")
                }
            };
            println!("  [2/3] Packing…");
            let tbag_path = pack_tbag(&project_root, &manifest_path, &manifest.name)
                .map_err(|e| format!("failed to create tbag: {e}"))?;
            println!("        → {}", tbag_path.display());

            // ── Step 3: push tbag to the local tbox ─────────────────────────
            let tbox = tbox_path();
            let dest = own_tbox_tbag_path(&tbox, &manifest);
            println!("  [3/3] Publishing to tbox…");
            push_tbag(&tbag_path, &dest)
                .map_err(|e| format!("failed to push tbag to tbox: {e}"))?;
            println!("        → {}", dest.display());

            println!();
            println!("Done in {}.", format_elapsed(t0.elapsed()));
            println!(
                "Add to a project manifest:  {}/{}/{} {}:{}:{} {}",
                manifest.organisation,
                manifest.project,
                manifest.name,
                manifest.version,
                manifest.stability,
                manifest.release_number,
                manifest.name,
            );
            Ok(())
        }
        Command::Push => {
            let t0 = std::time::Instant::now();

            // Resolve project root and manifest — push requires a manifest.yml.
            let (project_root, _, manifest) = resolve_paths(Some(&cli.project_path()?))?;
            let project_root = canonical_project_root(project_root)?;
            let manifest = manifest.ok_or_else(|| {
                "the `push` command requires a manifest.yml in the project directory".to_string()
            })?;

            // Locate the .tbag produced by `tlang package`.
            let tbag_path = project_root
                .join("target")
                .join("tlang")
                .join(format!("{}.tbag", manifest.name));
            if !tbag_path.exists() {
                return Err(format!(
                    "no .tbag found at '{}' — run `tlang package` first",
                    tbag_path.display()
                ));
            }

            // Select target URL.
            const DEV_URL: &str = "http://localhost:8080";
            const PROD_URL: &str = "https://market.tlang.dev";
            let base_url = if cli.market_dev { DEV_URL } else { PROD_URL };
            let push_url = format!("{base_url}/api/packages/push");

            let author = manifest
                .author
                .as_deref()
                .unwrap_or(&manifest.organisation)
                .to_string();

            // Derive tags from compatibility domain, falling back to project name.
            let tags = manifest
                .compatibility
                .as_ref()
                .and_then(|c| c.domain.as_deref())
                .unwrap_or_else(|| manifest.project.as_str())
                .to_lowercase();

            println!(
                "Pushing {} v{} ({}/{}) → {}",
                manifest.name,
                manifest.version,
                manifest.organisation,
                manifest.project,
                push_url,
            );
            println!();

            // Read tbag bytes.
            let tbag_bytes = std::fs::read(&tbag_path)
                .map_err(|e| format!("cannot read '{}': {e}", tbag_path.display()))?;
            let file_size = tbag_bytes.len();

            // Build the multipart form from manifest metadata.
            let filename = format!("{}.tbag", manifest.name);
            let file_part = reqwest::blocking::multipart::Part::bytes(tbag_bytes)
                .file_name(filename)
                .mime_str("application/octet-stream")
                .map_err(|e| format!("failed to build multipart part: {e}"))?;

            let form = reqwest::blocking::multipart::Form::new()
                .part("file", file_part)
                .text("name", manifest.name.clone())
                .text("version", manifest.version.clone())
                .text("author", author)
                .text("tags", tags);

            println!("  Uploading {} ({} bytes)…", tbag_path.display(), file_size);

            let token = load_market_token();
            if token.is_none() {
                return Err(
                    "Not authenticated. Run `tlang login` first, or set TLANG_MARKET_TOKEN.".to_string()
                );
            }

            let client = reqwest::blocking::Client::new();
            let mut req = client.post(&push_url).multipart(form);
            if let Some(tok) = token {
                req = req.header("Authorization", format!("Bearer {tok}"));
            }
            let resp = req
                .send()
                .map_err(|e| format!("push request failed: {e}"))?;

            let status = resp.status();
            let body = resp.text().unwrap_or_default();

            if status.is_success() {
                println!("  ✓ published  {}", body.trim());
                println!();
                println!("Done in {}.", format_elapsed(t0.elapsed()));
                println!(
                    "Package '{}' v{} is now listed on the market at {}/packages/{}",
                    manifest.name,
                    manifest.version,
                    base_url,
                    manifest.name.to_lowercase().replace(|c: char| !c.is_alphanumeric() && c != '-', "-"),
                );
                Ok(())
            } else {
                Err(format!(
                    "market returned HTTP {status}: {body}"
                ))
            }
        }

        Command::Pull => {
            let t0 = std::time::Instant::now();
            let pkg_arg = cli
                .pull_pkg
                .as_deref()
                .expect("pull_pkg is always set for Command::Pull");

            // Split optional version from name: "Kotlin:1.0.0" → ("Kotlin", Some("1.0.0"))
            let (pkg_name, version_filter) = if let Some(colon) = pkg_arg.find(':') {
                (&pkg_arg[..colon], Some(&pkg_arg[colon + 1..]))
            } else {
                (pkg_arg, None)
            };

            let base_url = market_base_url(cli.market_dev);
            let tbox = tbox_path();

            println!(
                "Pulling '{}'{} from {}…",
                pkg_name,
                version_filter.map(|v| format!(" v{v}")).unwrap_or_default(),
                base_url,
            );

            let (dest, actual_version) = pull_from_market(pkg_name, version_filter, &base_url, &tbox)?;

            println!();
            println!("  ✓ {} v{} saved to {}", pkg_name, actual_version, dest.display());
            println!();
            println!("Done in {}.", format_elapsed(t0.elapsed()));
            println!();
            println!("  Add to manifest.yml:");
            println!();
            println!("      {} {}", pkg_name, actual_version);
            println!();
            Ok(())
        }

        Command::Search => {
            let query = cli.search_query.as_deref().unwrap_or("");
            let base_url = market_base_url(cli.market_dev);
            let url = format!("{base_url}/api/packages/search?q={query}&tags=&page=1&size=20");
            let resp = reqwest::blocking::Client::new()
                .get(&url)
                .send()
                .map_err(|e| format!("search request failed: {e}"))?;
            if resp.status() != reqwest::StatusCode::OK {
                return Err(format!("market returned HTTP {}", resp.status()));
            }
            let body: serde_json::Value = resp
                .json()
                .map_err(|e| format!("invalid JSON response: {e}"))?;
            let items = body["items"].as_array().ok_or_else(|| "unexpected response format".to_string())?;
            if items.is_empty() {
                println!("No packages found for {:?}.", query);
                return Ok(());
            }
            let total = body["total"].as_i64().unwrap_or(items.len() as i64);
            println!();
            println!("  Found {} package(s):\n", total);
            println!("  {:<24} {:<10} {:<20} {:>8}  {}", "Name", "Version", "Author", "Downloads", "Description");
            println!("  {}", "-".repeat(90));
            for item in items {
                let name    = item["name"].as_str().unwrap_or("-");
                let version = item["version"].as_str().unwrap_or("-");
                let author  = item["author"].as_str().unwrap_or("-");
                let dl      = item["downloadCount"].as_i64().unwrap_or(0);
                let desc    = item["description"].as_str().unwrap_or("");
                let desc_short = if desc.len() > 40 { format!("{}…", &desc[..39]) } else { desc.to_string() };
                println!("  {:<24} {:<10} {:<20} {:>8}  {}", name, version, author, dl, desc_short);
            }
            println!();
            Ok(())
        }

        Command::Install => {
            let pkg_ref = cli
                .install_pkg
                .as_deref()
                .expect("install_pkg is always set for Command::Install");

            // If the reference contains a colon it is a tbox reference
            // (Org/Project/Name:version:stability:release). Otherwise treat it
            // as a market package slug and download from the market.
            if !pkg_ref.contains(':') {
                return install_from_market(pkg_ref, cli.market_dev);
            }

            let (path, version_opt) = parse_package_ref(pkg_ref)?;
            let pkg_name = path.last().expect("path is non-empty").clone();

            let tbox = tbox_path();
            println!("Looking up '{pkg_ref}' in tbox…");
            let tbag_path = find_tbag_in_tbox(&tbox, &path, version_opt.as_ref())?;
            println!("  found: {}", tbag_path.display());

            // Read the manifest from the tbag to verify it is a CLI package.
            let manifest_text = read_manifest_from_tbag(&tbag_path)?;
            let manifest = parse_manifest(&manifest_text)
                .map_err(|e| format!("cannot parse manifest from tbag: {e}"))?;

            if manifest.package_type != PackageType::Cli {
                return Err(format!(
                    "'{pkg_name}' is not a CLI package (type is 'library' — only 'type: cli' packages can be installed)"
                ));
            }

            // The install name is `command` when declared, otherwise the package name.
            let install_name = manifest
                .command
                .as_deref()
                .unwrap_or(&pkg_name)
                .to_string();

            // Copy the tbag to the CLI store.
            let dest_dir = cli_dir();
            std::fs::create_dir_all(&dest_dir)
                .map_err(|e| format!("cannot create CLI dir '{}': {e}", dest_dir.display()))?;
            let dest = dest_dir.join(format!("{install_name}.tbag"));

            // Remove any previous extraction so the new version is used on next exec.
            let extract_dir = dest_dir.join(&install_name);
            if extract_dir.exists() {
                std::fs::remove_dir_all(&extract_dir).map_err(|e| {
                    format!("cannot remove old extraction '{}': {e}", extract_dir.display())
                })?;
            }

            std::fs::copy(&tbag_path, &dest)
                .map_err(|e| format!("cannot install '{install_name}': {e}"))?;

            println!();
            println!(
                "Installed '{pkg_name}' v{} as '{}' to {}",
                manifest.version,
                install_name,
                dest.display()
            );
            println!("Run with: tlang exec {install_name} [args...]");
            Ok(())
        }
        Command::Login => login_to_market(cli.market_dev),
        Command::Logout => logout_from_market(),
        Command::LspStop => stop_lsp_server(),
        Command::LspRestart => {
            stop_lsp_server()?;
            // Small pause so the OS releases the stdio/socket handles.
            std::thread::sleep(std::time::Duration::from_millis(200));
            // Spawn a detached background process.
            let exe =
                std::env::current_exe().map_err(|e| format!("cannot locate executable: {e}"))?;
            std::process::Command::new(&exe)
                .arg("lsp-server")
                .stdin(std::process::Stdio::null())
                .stdout(std::process::Stdio::null())
                .stderr(std::process::Stdio::null())
                .spawn()
                .map_err(|e| format!("failed to start LSP server: {e}"))?;
            println!("TLang LSP server restarted.");
            Ok(())
        }
        Command::Test => run_test_command(&cli),
        Command::McpServer => crate::mcp::run_mcp_server().map_err(|e| format!("MCP server error: {e}")),
        Command::LspServer => {
            tlang::lsp::run_lsp_server().map_err(|e| format!("LSP server error: {e}"))
        }
        Command::Help => {
            println!("{}", args::usage(program_name));
            Ok(())
        }
        Command::Version => {
            println!("{}", env!("CARGO_PKG_VERSION"));
            Ok(())
        }
    }
}

fn run_test_command(cli: &Cli) -> Result<(), String> {
    let raw_path = cli.project_path()?;
    // If the target is a specific .tlang file, run just that file.
    // Otherwise, treat it as a directory and discover all *Test.tlang files.
    let (project_root, test_files) = if raw_path.is_file() {
        let dir = raw_path
            .parent()
            .map(|p| p.to_path_buf())
            .unwrap_or_else(|| PathBuf::from("."));
        let dir = canonical_project_root(dir)?;
        (dir, vec![raw_path])
    } else {
        let dir = canonical_project_root(raw_path)?;
        let files = find_test_files(&dir)?;
        (dir, files)
    };

    if test_files.is_empty() {
        println!("No *Test.tlang files found.");
        return Ok(());
    }

    let base_url = market_base_url(false);
    let tbox = tlang::tbox_path();

    let mut total = 0usize;
    let mut passed = 0usize;
    let mut failed = 0usize;

    for test_file in &test_files {
        // Resolve the manifest for this file's project (walk up to find manifest.yml).
        let (_, main_path, manifest) = resolve_paths(Some(test_file))?;
        let file_root = main_path
            .parent()
            .map(|p| p.to_path_buf())
            .unwrap_or_else(|| project_root.clone());
        let file_root = canonical_project_root(file_root)?;

        if let Some(ref m) = manifest {
            ensure_deps_from_market(m, &tbox, &base_url)?;
        }

        // Compile the test file (using the returned CompiledProgram directly).
        let (program, _written) =
            tlang::compile_to_bytecode_files(test_file, &file_root, manifest.as_ref())
                .map_err(|e| format!("compile error in {}: {e}", test_file.display()))?;

        if program.test_names.is_empty() {
            continue;
        }

        std::env::set_current_dir(&file_root)
            .map_err(|e| format!("cannot cd to project root: {e}"))?;

        println!("\n{}", test_file.display());
        let results = tlang::runtime::run_tests(&program);

        for result in results {
            total += 1;
            if result.passed {
                passed += 1;
                println!("  \x1b[32m✓\x1b[0m {}", result.name);
            } else {
                failed += 1;
                println!("  \x1b[31m✗\x1b[0m {}", result.name);
                for failure in &result.failures {
                    println!("      {failure}");
                }
            }
        }
    }

    println!();
    if failed == 0 {
        println!("\x1b[32m{passed}/{total} tests passed\x1b[0m");
        Ok(())
    } else {
        println!("\x1b[31m{passed}/{total} tests passed, {failed} failed\x1b[0m");
        Err(format!("{failed} test(s) failed"))
    }
}

fn find_test_files(root: &std::path::Path) -> Result<Vec<PathBuf>, String> {
    let mut files = Vec::new();
    visit_dir(root, &mut files)?;
    files.sort();
    Ok(files)
}

fn visit_dir(dir: &std::path::Path, files: &mut Vec<PathBuf>) -> Result<(), String> {
    let entries = std::fs::read_dir(dir)
        .map_err(|e| format!("cannot read directory {}: {e}", dir.display()))?;
    for entry in entries.flatten() {
        let path = entry.path();
        if path.is_dir() {
            let name = path.file_name().and_then(|n| n.to_str()).unwrap_or("");
            // Skip hidden dirs, target/, and node_modules/
            if !name.starts_with('.') && name != "target" && name != "node_modules" {
                visit_dir(&path, files)?;
            }
        } else if let Some(name) = path.file_name().and_then(|n| n.to_str()) {
            if name.ends_with("Test.tlang") {
                files.push(path);
            }
        }
    }
    Ok(())
}
