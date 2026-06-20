// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! CLI argument parsing.
//!
//! [`parse_cli_args`] hand-parses `std::env::args()` into a [`Cli`] value.
//! [`usage`] returns the formatted help text shown for `--help` / `-h`.

use std::path::PathBuf;

use super::{Cli, Command};

pub fn usage(program: &str) -> String {
    format!(
        "Usage: {program} <command> [options] [-p <path>] [target]\n\n\
         Commands:\n\
           compile      Compile a TLang program (writes bytecode when stale)\n\
           run          Run a TLang program (reads compiled bytecode)\n\
           both         Compile then run a TLang program\n\
           compile-run  Alias for `both`\n\
           exec         Execute a named exposed helper function (falls back to installed CLI tools)\n\
           dev          Watch .tlang sources, re-run the `dev` function on each change\n\
           clean        Delete compiled bytecode under target/tlang\n\
           init         Initialise a new TLang/Kotlin hello-world project\n\
           package      Build a .tbag archive and push it to the local tbox\n\
           push         Push the packed .tbag to the TLang market (alias: publish)\n\
           publish      Alias for `push`\n\
           pull         Pull a package from the TLang market into the local tbox\n\
           search       Search packages on the TLang market\n\
           install      Install a CLI package from the market into ~/.tlang/cli/\n\
           login        Sign in to the TLang market (saves token to ~/.tlang/market_token)\n\
           logout       Remove the stored market token\n\
           lsp-server   Start the TLang Language Server (background)\n\
           lsp-stop     Stop the running TLang Language Server\n\
           lsp-restart  Stop (if running) then start the Language Server\n\
           mcp-server   Start the TLang MCP server (Model Context Protocol, stdio)\n\
           test         Run all test blocks in *Test.tlang files under the project\n\
           --help, -h   Show help\n\
           --version, -V Show version\n\n\
         Options (all project commands):\n\
           -p, --path <dir>  Set the project root directory (overrides positional target)\n\n\
         Options (compile):\n\
           --live         Watch .tlang sources and auto-compile on change\n\
           --force-regen  After compiling, also run the program and regenerate\n\
                          all output files (including >>? scaffold paths)\n\n\
         Options (run / both / compile-run):\n\
           --force-regen  Treat >>? (write-once) paths as >> (always-overwrite),\n\
                          regenerating scaffold files that already exist on disk\n\n\
         Options (both / compile-run):\n\
           --in-memory  Compile and run without writing bytecode to disk\n\n\
         Options (exec):\n\
           --force-regen  Same write-once override as for `run`\n\n\
         exec usage:\n\
           {program} exec <funcName> [args...] [-p <path>]\n\
           {program} exec --list [-p <path>]   (list all available commands)\n\n\
           <funcName> must be declared with `expose` in the .tlang source, or be the name\n\
           of an installed CLI package (~/.tlang/cli/<funcName>.tbag).  When the function\n\
           is not found in the current project, the CLI package's main function is run.\n\
           Each additional positional argument after <funcName> is forwarded\n\
           to the function as a String.  For functions that accept String[],\n\
           all args are collected into a single list.\n\n\
         dev usage:\n\
           {program} dev [args...] [-p <path>]\n\n\
           Watches .tlang source files for changes.  On each change, recompiles the\n\
           project and calls the `dev` exposed function with the supplied positional\n\
           arguments (passed as String[]).  Quarkus dev mode (started once by `dev`\n\
           on first run) picks up the regenerated .kt files via its built-in\n\
           hot-reload — the JVM is never restarted.\n\n\
         push usage:\n\
           {program} push [-p <path>] [--dev]\n\n\
           Reads manifest.yml from the project, locates target/tlang/<Name>.tbag\n\
           (produced by `tlang package`), then POSTs it to the TLang market.\n\
           Package metadata (name, version, author, tags) is taken from the manifest.\n\
           The package is immediately listed on the market after a successful push.\n\n\
         Options (push):\n\
           --dev  Target the local dev server (http://localhost:8080) instead of\n\
                  the default production market (https://market.tlang.dev)\n\n\
         pull usage:\n\
           {program} pull <Name>[:version] [--dev]\n\n\
           Downloads a package from the TLang market into the local tbox\n\
           (~/.tlang/tbox).  The package becomes immediately available as a\n\
           dependency for local projects.\n\
           If <version> is omitted, the latest published version is pulled.\n\
           Set TLANG_MARKET_URL to override the market URL without --dev.\n\n\
         Options (pull):\n\
           --dev  Pull from the local dev server (http://localhost:8080)\n\n\
         search usage:\n\
           {program} search [<query>] [--dev]\n\n\
           Searches the TLang market for packages matching the query.\n\
           Prints a table of results: name, version, author, downloads, description.\n\
           If <query> is omitted, lists the most popular packages.\n\n\
         Options (search):\n\
           --dev  Search the local dev server (http://localhost:8080)\n\n\
         install usage:\n\
           {program} install <slug> [--dev]          Downloads from the TLang market\n\
           {program} install <Org/Project/Name:version:stability:release>  From local tbox\n\n\
           Market install: resolves the package slug on the market, downloads the latest\n\
           .tbag, and installs it to ~/.tlang/cli/<slug>.tbag.\n\
           Tbox install: looks up in the local tbox (~/.tlang/tbox), verifies it is a\n\
           CLI package, and copies it to ~/.tlang/cli/.\n\n\
         login usage:\n\
           {program} login [--dev]\n\n\
           Prompts for email and password, authenticates against the TLang market, and\n\
           saves the token to ~/.tlang/market_token for use by `push`/`publish`.\n\n\
         logout usage:\n\
           {program} logout\n\n\
           Removes the stored token from ~/.tlang/market_token.\n\n\
         Target:\n\
           Optional positional path to a project directory or a .tlang file.\n\
           If omitted (and -p is not given), the current directory is used.\n"
    )
}

pub fn parse_cli_args(mut args: impl Iterator<Item = String>) -> Result<Cli, String> {
    let first = args
        .next()
        .ok_or_else(|| "missing command (try --help)".to_string())?;

    let command = match first.as_str() {
        "compile" => Command::Compile,
        "run" => Command::Run,
        "both" | "compile-run" => Command::Both,
        "exec" => Command::Exec,
        "dev" => Command::Dev,
        "clean" => Command::Clean,
        "init" => Command::Init,
        "package" => Command::Package,
        "push" | "publish" => Command::Push,
        "pull" => Command::Pull,
        "search" => Command::Search,
        "install" => Command::Install,
        "login" => Command::Login,
        "logout" => Command::Logout,
        "lsp-server" => Command::LspServer,
        "lsp-stop" => Command::LspStop,
        "lsp-restart" => Command::LspRestart,
        "mcp-server" | "mcp" => Command::McpServer,
        "test" => Command::Test,
        "--help" | "-h" | "help" => Command::Help,
        "--version" | "-V" | "version" => Command::Version,
        other => return Err(format!("unknown command `{other}`")),
    };

    // ── dev has its own grammar: dev [args…] [-p path]
    // Positional arguments are forwarded to the TLang `dev` exposed function.
    if command == Command::Dev {
        let mut exec_args: Vec<String> = Vec::new();
        let mut path_flag: Option<PathBuf> = None;
        let mut args = args.peekable();

        while let Some(arg) = args.next() {
            match arg.as_str() {
                "-p" | "--path" => {
                    let value = args
                        .next()
                        .ok_or_else(|| format!("`{arg}` requires a path argument"))?;
                    if path_flag.is_some() {
                        return Err(format!("`{arg}` specified more than once"));
                    }
                    path_flag = Some(PathBuf::from(value));
                }
                other if other.starts_with('-') => {
                    return Err(format!("unknown option `{other}`"));
                }
                _ => {
                    exec_args.push(arg);
                }
            }
        }

        return Ok(Cli {
            command,
            target: path_flag,
            in_memory: false,
            live: false,
            force_regen: false,
            exec_func: None,
            exec_args,
            install_pkg: None,
            pull_pkg: None,
            search_query: None,
            market_dev: false,
            list_commands: false,
        });
    }

    // ── exec has its own positional grammar: exec <funcName> [args…] [-p path | target]
    // Parse it separately before the generic flag loop.
    if command == Command::Exec {
        let mut exec_func: Option<String> = None;
        let mut exec_args: Vec<String> = Vec::new();
        let mut force_regen = false;
        let mut list_commands = false;
        let mut path_flag: Option<PathBuf> = None;
        let target: Option<PathBuf> = None;
        let mut args = args.peekable();

        while let Some(arg) = args.next() {
            match arg.as_str() {
                "--list" | "-l" => {
                    list_commands = true;
                }
                "--force-regen" => {
                    force_regen = true;
                }
                "-p" | "--path" => {
                    let value = args
                        .next()
                        .ok_or_else(|| format!("`{arg}` requires a path argument"))?;
                    if path_flag.is_some() {
                        return Err(format!("`{arg}` specified more than once"));
                    }
                    path_flag = Some(PathBuf::from(value));
                }
                other if other.starts_with('-') => {
                    return Err(format!("unknown option `{other}`"));
                }
                _ => {
                    if exec_func.is_none() {
                        exec_func = Some(arg);
                    } else {
                        exec_args.push(arg);
                    }
                }
            }
        }

        if path_flag.is_some() && target.is_some() {
            return Err(
                "cannot use both `-p/--path` and a positional target — pick one".to_string(),
            );
        }
        let target = path_flag.or(target);

        if !list_commands {
            exec_func = Some(exec_func.ok_or_else(|| {
                "exec requires a function name or --list: `tlang exec <funcName> [args…]`\n\
                 To list available commands: `tlang exec --list`".to_string()
            })?);
        }

        return Ok(Cli {
            command,
            target,
            in_memory: false,
            live: false,
            force_regen,
            exec_func,
            exec_args,
            install_pkg: None,
            pull_pkg: None,
            search_query: None,
            market_dev: false,
            list_commands,
        });
    }

    // ── install has its own grammar: install <pkg-ref> [--dev]
    if command == Command::Install {
        let mut args = args.peekable();
        let mut market_dev = false;
        let mut pkg_ref: Option<String> = None;
        while let Some(arg) = args.next() {
            match arg.as_str() {
                "--dev" => market_dev = true,
                other if other.starts_with('-') => {
                    return Err(format!("unknown option `{other}` for install"));
                }
                _ => {
                    if pkg_ref.is_some() {
                        return Err(format!("install takes exactly one argument, unexpected `{arg}`"));
                    }
                    pkg_ref = Some(arg);
                }
            }
        }
        let pkg_ref = pkg_ref.ok_or_else(|| {
            "install requires a package name (e.g. `tlang install my-pkg`) or tbox reference (e.g. `tlang install Org/Project/Name:1.0.0:alpha:1`)"
                .to_string()
        })?;
        return Ok(Cli {
            command,
            target: None,
            in_memory: false,
            live: false,
            force_regen: false,
            exec_func: None,
            exec_args: Vec::new(),
            install_pkg: Some(pkg_ref),
            pull_pkg: None,
            search_query: None,
            market_dev,
            list_commands: false,
        });
    }

    // ── login has its own grammar: login [--dev]
    if command == Command::Login {
        let mut market_dev = false;
        for arg in args {
            match arg.as_str() {
                "--dev" => market_dev = true,
                other => return Err(format!("unknown option `{other}` for login")),
            }
        }
        return Ok(Cli {
            command,
            target: None,
            in_memory: false,
            live: false,
            force_regen: false,
            exec_func: None,
            exec_args: Vec::new(),
            install_pkg: None,
            pull_pkg: None,
            search_query: None,
            market_dev,
            list_commands: false,
        });
    }

    // ── logout takes no arguments
    if command == Command::Logout {
        return Ok(Cli {
            command,
            target: None,
            in_memory: false,
            live: false,
            force_regen: false,
            exec_func: None,
            exec_args: Vec::new(),
            install_pkg: None,
            pull_pkg: None,
            search_query: None,
            market_dev: false,
            list_commands: false,
        });
    }

    // ── pull has its own grammar: pull <Name[:version]> [--dev]
    if command == Command::Pull {
        let mut args = args.peekable();
        let mut market_dev = false;
        let mut pkg_ref: Option<String> = None;
        while let Some(arg) = args.next() {
            match arg.as_str() {
                "--dev" => market_dev = true,
                other if other.starts_with('-') => {
                    return Err(format!("unknown option `{other}` for pull"));
                }
                _ => {
                    if pkg_ref.is_some() {
                        return Err(format!("pull takes exactly one package name, unexpected `{arg}`"));
                    }
                    pkg_ref = Some(arg);
                }
            }
        }
        let pkg_ref = pkg_ref.ok_or_else(|| {
            "pull requires a package name, e.g. `tlang pull Kotlin` or `tlang pull Kotlin:1.0.0`"
                .to_string()
        })?;
        return Ok(Cli {
            command,
            target: None,
            in_memory: false,
            live: false,
            force_regen: false,
            exec_func: None,
            exec_args: Vec::new(),
            install_pkg: None,
            pull_pkg: Some(pkg_ref),
            search_query: None,
            market_dev,
            list_commands: false,
        });
    }

    // ── search has its own grammar: search [query] [--dev]
    if command == Command::Search {
        let mut args = args.peekable();
        let mut market_dev = false;
        let mut query: Option<String> = None;
        while let Some(arg) = args.next() {
            match arg.as_str() {
                "--dev" => market_dev = true,
                other if other.starts_with('-') => {
                    return Err(format!("unknown option `{other}` for search"));
                }
                _ => {
                    if query.is_some() {
                        return Err(format!("search takes at most one query, unexpected `{arg}`"));
                    }
                    query = Some(arg);
                }
            }
        }
        return Ok(Cli {
            command,
            target: None,
            in_memory: false,
            live: false,
            force_regen: false,
            exec_func: None,
            exec_args: Vec::new(),
            install_pkg: None,
            pull_pkg: None,
            search_query: query,
            market_dev,
            list_commands: false,
        });
    }

    // Collect remaining args, separating flags from the positional target.
    let mut in_memory = false;
    let mut live = false;
    let mut force_regen = false;
    let mut market_dev = false;
    let mut target: Option<PathBuf> = None;
    let mut path_flag: Option<PathBuf> = None;
    let mut extra = false;
    let mut args = args.peekable();

    while let Some(arg) = args.next() {
        match arg.as_str() {
            "--in-memory" => {
                in_memory = true;
            }
            "--live" => {
                live = true;
            }
            "--force-regen" => {
                force_regen = true;
            }
            "--dev" => {
                market_dev = true;
            }
            "-p" | "--path" => {
                let value = args
                    .next()
                    .ok_or_else(|| format!("`{arg}` requires a path argument"))?;
                if path_flag.is_some() {
                    return Err(format!("`{arg}` specified more than once"));
                }
                path_flag = Some(PathBuf::from(value));
            }
            other if other.starts_with('-') => {
                return Err(format!("unknown option `{other}`"));
            }
            _ => {
                if target.is_some() {
                    extra = true;
                } else {
                    target = Some(PathBuf::from(arg));
                }
            }
        }
    }

    if extra {
        return Err("too many arguments".to_string());
    }

    // -p/--path and a positional target are mutually exclusive.
    if path_flag.is_some() && target.is_some() {
        return Err("cannot use both `-p/--path` and a positional target — pick one".to_string());
    }

    // Merge: named flag wins; fall back to positional.
    let target = path_flag.or(target);

    if matches!(
        command,
        Command::Help
            | Command::Version
            | Command::LspServer
            | Command::LspStop
            | Command::LspRestart
            | Command::McpServer
            | Command::Login
            | Command::Logout
    ) && target.is_some()
    {
        return Err("help, version and lsp-server commands do not take a target".to_string());
    }

    if in_memory && !matches!(command, Command::Both) {
        return Err("--in-memory is only valid with `both` and `compile-run`".to_string());
    }
    if live && !matches!(command, Command::Compile) {
        return Err("--live is only valid with `compile`".to_string());
    }
    if force_regen && !matches!(command, Command::Compile | Command::Run | Command::Both) {
        return Err("--force-regen is only valid with `compile`, `run`, and `both`".to_string());
    }
    if market_dev && !matches!(command, Command::Push | Command::Pull | Command::Search | Command::Install | Command::Login) {
        return Err("--dev is only valid with `push`, `pull`, `search`, `install`, or `login`".to_string());
    }

    Ok(Cli {
        command,
        target,
        in_memory,
        live,
        force_regen,
        exec_func: None,
        exec_args: Vec::new(),
        install_pkg: None,
        pull_pkg: None,
        search_query: None,
        market_dev,
        list_commands: false,
    })
}
