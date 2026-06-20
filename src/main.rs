// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! `tlang` binary entry point.
//!
//! Parses CLI arguments via [`cli::args::parse_cli_args`] and dispatches to
//! [`cli::run`]. All command implementations live in [`cli`]; LSP and MCP
//! server implementations live in [`crate::lsp`] and [`mcp`] respectively.

mod cli;
mod mcp;

fn main() {
    let mut raw_args = std::env::args();
    let program_name = raw_args.next().unwrap_or_else(|| "tlang".to_string());
    let parsed_cli = match cli::args::parse_cli_args(raw_args) {
        Ok(c) => c,
        Err(msg) => {
            eprintln!("Error: {msg}\n");
            eprintln!("{}", cli::args::usage(&program_name));
            std::process::exit(2);
        }
    };

    if let Err(msg) = cli::run(parsed_cli, &program_name) {
        eprintln!("Error: {msg}");
        std::process::exit(1);
    }
}

#[cfg(test)]
mod tests {
    use crate::cli::{Command, args::parse_cli_args};
    use std::path::PathBuf;

    fn parse(args: &[&str]) -> Result<crate::cli::Cli, String> {
        parse_cli_args(args.iter().map(|s| s.to_string()))
    }

    #[test]
    fn parses_compile_with_target() {
        let cli = parse(&["compile", "Main.tlang"]).expect("should parse");
        assert_eq!(cli.command, Command::Compile);
        assert_eq!(cli.target, Some(PathBuf::from("Main.tlang")));
        assert!(!cli.in_memory);
        assert!(!cli.live);
    }

    #[test]
    fn parses_both_in_memory() {
        let cli = parse(&["both", "--in-memory"]).expect("should parse");
        assert_eq!(cli.command, Command::Both);
        assert!(cli.in_memory);
        assert!(!cli.live);
    }

    #[test]
    fn parses_both_in_memory_with_target() {
        let cli = parse(&["both", "--in-memory", "Main.tlang"]).expect("should parse");
        assert!(cli.in_memory);
        assert_eq!(cli.target, Some(PathBuf::from("Main.tlang")));
    }

    #[test]
    fn rejects_in_memory_on_compile() {
        let err = parse(&["compile", "--in-memory"]).expect_err("should fail");
        assert!(err.contains("--in-memory"), "got: {err}");
    }

    #[test]
    fn rejects_in_memory_on_run() {
        let err = parse(&["run", "--in-memory"]).expect_err("should fail");
        assert!(err.contains("--in-memory"), "got: {err}");
    }

    #[test]
    fn parses_run_without_target() {
        let cli = parse(&["run"]).expect("should parse");
        assert_eq!(cli.command, Command::Run);
        assert_eq!(cli.target, None);
        assert!(!cli.live);
    }

    #[test]
    fn parses_both_alias() {
        let cli = parse(&["compile-run"]).expect("should parse");
        assert_eq!(cli.command, Command::Both);
    }

    #[test]
    fn parses_lsp_server() {
        let cli = parse(&["lsp-server"]).expect("should parse");
        assert_eq!(cli.command, Command::LspServer);
    }

    #[test]
    fn parses_lsp_stop() {
        let cli = parse(&["lsp-stop"]).expect("should parse");
        assert_eq!(cli.command, Command::LspStop);
    }

    #[test]
    fn parses_lsp_restart() {
        let cli = parse(&["lsp-restart"]).expect("should parse");
        assert_eq!(cli.command, Command::LspRestart);
    }

    #[test]
    fn rejects_target_for_lsp_stop() {
        let err = parse(&["lsp-stop", "Main.tlang"]).expect_err("lsp-stop with target should fail");
        assert!(err.contains("do not take a target"));
    }

    #[test]
    fn rejects_target_for_lsp_restart() {
        let err =
            parse(&["lsp-restart", "Main.tlang"]).expect_err("lsp-restart with target should fail");
        assert!(err.contains("do not take a target"));
    }

    #[test]
    fn parses_help_and_version() {
        assert_eq!(parse(&["--help"]).expect("help").command, Command::Help);
        assert_eq!(
            parse(&["--version"]).expect("version").command,
            Command::Version
        );
        assert_eq!(parse(&["-V"]).expect("-V").command, Command::Version);
    }

    #[test]
    fn rejects_unknown_command() {
        let err = parse(&["unknown"]).expect_err("should fail");
        assert!(err.contains("unknown command"));
    }

    #[test]
    fn rejects_too_many_arguments() {
        let err = parse(&["compile", "a", "b"]).expect_err("should fail");
        assert_eq!(err, "too many arguments");
    }

    #[test]
    fn parses_package_command() {
        let cli = parse(&["package"]).unwrap();
        assert_eq!(cli.command, Command::Package);
        assert_eq!(cli.target, None);
    }

    #[test]
    fn parses_package_with_target() {
        let cli = parse(&["package", "my/project"]).unwrap();
        assert_eq!(cli.command, Command::Package);
        assert_eq!(cli.target, Some(PathBuf::from("my/project")));
    }

    #[test]
    fn rejects_in_memory_on_package() {
        let err = parse(&["package", "--in-memory"]).unwrap_err();
        assert!(err.contains("--in-memory"), "error: {err}");
    }

    #[test]
    fn parses_compile_live() {
        let cli = parse(&["compile", "--live"]).expect("should parse");
        assert_eq!(cli.command, Command::Compile);
        assert!(cli.live);
        assert!(!cli.in_memory);
    }

    #[test]
    fn rejects_live_on_run() {
        let err = parse(&["run", "--live"]).unwrap_err();
        assert!(err.contains("--live"), "error: {err}");
    }

    #[test]
    fn rejects_live_on_both() {
        let err = parse(&["both", "--live"]).unwrap_err();
        assert!(err.contains("--live"), "error: {err}");
    }

    #[test]
    fn parses_force_regen_on_compile() {
        let cli = parse(&["compile", "--force-regen"]).unwrap();
        assert_eq!(cli.command, Command::Compile);
        assert!(cli.force_regen, "force_regen should be true");
    }

    #[test]
    fn parses_force_regen_on_run() {
        let cli = parse(&["run", "--force-regen"]).unwrap();
        assert_eq!(cli.command, Command::Run);
        assert!(cli.force_regen, "force_regen should be true");
    }

    #[test]
    fn parses_force_regen_on_both() {
        let cli = parse(&["both", "--force-regen"]).unwrap();
        assert_eq!(cli.command, Command::Both);
        assert!(cli.force_regen, "force_regen should be true");
    }

    #[test]
    fn parses_force_regen_on_compile_run_alias() {
        let cli = parse(&["compile-run", "--force-regen"]).unwrap();
        assert_eq!(cli.command, Command::Both);
        assert!(cli.force_regen, "force_regen should be true");
    }

    #[test]
    fn parses_force_regen_with_target() {
        let cli = parse(&["run", "--force-regen", "Main.tlang"]).unwrap();
        assert_eq!(cli.command, Command::Run);
        assert!(cli.force_regen);
        assert_eq!(cli.target, Some(PathBuf::from("Main.tlang")));
    }

    #[test]
    fn rejects_force_regen_on_clean() {
        let err = parse(&["clean", "--force-regen"]).unwrap_err();
        assert!(err.contains("--force-regen"), "error: {err}");
    }

    #[test]
    fn rejects_force_regen_on_package() {
        let err = parse(&["package", "--force-regen"]).unwrap_err();
        assert!(err.contains("--force-regen"), "error: {err}");
    }

    #[test]
    fn force_regen_defaults_to_false() {
        let cli = parse(&["run"]).unwrap();
        assert!(!cli.force_regen, "force_regen should default to false");
        let cli = parse(&["both"]).unwrap();
        assert!(!cli.force_regen, "force_regen should default to false");
        let cli = parse(&["compile"]).unwrap();
        assert!(!cli.force_regen, "force_regen should default to false");
    }

    #[test]
    fn parses_clean_command() {
        let cli = parse(&["clean"]).unwrap();
        assert_eq!(cli.command, Command::Clean);
        assert_eq!(cli.target, None);
    }

    #[test]
    fn parses_clean_with_target() {
        let cli = parse(&["clean", "my/project"]).unwrap();
        assert_eq!(cli.command, Command::Clean);
        assert_eq!(cli.target, Some(PathBuf::from("my/project")));
    }

    #[test]
    fn parses_exec_with_func_name() {
        let cli = parse(&["exec", "deploy"]).expect("should parse");
        assert_eq!(cli.command, Command::Exec);
        assert_eq!(cli.exec_func.as_deref(), Some("deploy"));
        assert!(cli.exec_args.is_empty());
        assert!(cli.target.is_none());
    }

    #[test]
    fn parses_exec_with_func_name_and_args() {
        let cli = parse(&["exec", "deploy", "prod", "v2.0"]).expect("should parse");
        assert_eq!(cli.command, Command::Exec);
        assert_eq!(cli.exec_func.as_deref(), Some("deploy"));
        assert_eq!(cli.exec_args, vec!["prod", "v2.0"]);
    }

    #[test]
    fn parses_exec_with_path_flag() {
        let cli = parse(&["exec", "generate", "-p", "/my/project"]).expect("should parse");
        assert_eq!(cli.command, Command::Exec);
        assert_eq!(cli.exec_func.as_deref(), Some("generate"));
        assert_eq!(cli.target, Some(std::path::PathBuf::from("/my/project")));
        assert!(cli.exec_args.is_empty());
    }

    #[test]
    fn parses_exec_with_force_regen() {
        let cli = parse(&["exec", "build", "--force-regen"]).expect("should parse");
        assert_eq!(cli.command, Command::Exec);
        assert!(cli.force_regen);
        assert_eq!(cli.exec_func.as_deref(), Some("build"));
    }

    #[test]
    fn rejects_exec_without_func_name() {
        let err = parse_cli_args(["exec"].iter().map(|s| s.to_string()))
            .expect_err("exec without func name should fail");
        assert!(err.contains("function name"), "got: {err}");
    }

    #[test]
    fn rejects_target_for_help_or_version() {
        let help_err = parse(&["--help", "Main.tlang"]).expect_err("help with target should fail");
        assert!(help_err.contains("do not take a target"));

        let version_err =
            parse(&["--version", "Main.tlang"]).expect_err("version with target should fail");
        assert!(version_err.contains("do not take a target"));
    }

    #[test]
    fn rejects_target_for_lsp_server() {
        let err =
            parse(&["lsp-server", "Main.tlang"]).expect_err("lsp-server with target should fail");
        assert!(err.contains("do not take a target"));
    }
}
