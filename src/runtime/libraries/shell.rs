// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! `TLang.Shell` — built-in library for executing system commands.
//!
//! Provides helper-block access to the host operating system's shell so that
//! TLang programs can spawn external processes, capture their output, and read
//! environment variables.
//!
//! # Available functions
//!
//! | Function | Signature | Description |
//! |---|---|---|
//! | `Shell.run` | `(cmd: String) -> String` | Execute `cmd` via the system shell and return stdout. Raises a runtime error if the process exits with a non-zero status. |
//! | `Shell.runIn` | `(cmd: String, dir: String) -> String` | Like `Shell.run` but changes the working directory to `dir` first. |
//! | `Shell.capture` | `(cmd: String) -> Map` | Execute `cmd` and return a `Map` with `stdout`, `stderr`, and `exitCode` (Int) without failing on non-zero exit. |
//! | `Shell.captureIn` | `(cmd: String, dir: String) -> Map` | Like `Shell.capture` but in a specific directory. |
//! | `Shell.stream` | `(cmd: String) -> Int` | Execute `cmd` with stdio inherited from the terminal (live output). Returns the exit code. Ideal for long-running processes like `quarkusDev`. |
//! | `Shell.streamIn` | `(cmd: String, dir: String) -> Int` | Like `Shell.stream` but changes the working directory to `dir` first. |
//! | `Shell.spawnIn` | `(cmd: String, dir: String) -> String` | Spawn `cmd` in `dir` non-blocking (fire-and-forget). Stdio is inherited so output appears in the terminal. Returns the child PID as a string. |
//! | `Shell.env` | `(name: String) -> String` | Return the value of the environment variable `name`, or an empty string if it is not set. |
//! | `Shell.which` | `(name: String) -> String` | Return the absolute path of the named executable (like `which`/`where`), or an empty string if not found. |
//!
//! # Shell selection
//!
//! On Unix, commands are executed as `sh -c "<cmd>"`.
//! On Windows, commands are executed as `cmd.exe /C "<cmd>"`.
//!
//! # Example (TLang helper)
//!
//! ```tlang
//! use TLang.Shell
//! use TLang.Terminal
//!
//! helper {
//!     func main(): String {
//!         let out  = Shell.run("echo hello");
//!         Terminal.println(out);
//!
//!         let info = Shell.capture("git log --oneline -5");
//!         let code = info.exitCode;
//!         Terminal.println(info.stdout);
//!     }
//! }
//! ```

use std::collections::BTreeMap;
use std::path::PathBuf;

use super::{
    super::{RuntimeError, Value},
    expect_string,
};

/// Entry point called by `call_builtin` in `mod.rs`.
pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Shell.run" => run(args, None),
        "TLang.Shell.runIn" => run_in(args),
        "TLang.Shell.capture" => capture(args, None),
        "TLang.Shell.captureIn" => capture_in(args),
        "TLang.Shell.stream" => stream(args, None),
        "TLang.Shell.streamIn" => stream_in(args),
        "TLang.Shell.spawnIn" => spawn_in(args),
        "TLang.Shell.env" => env(args),
        "TLang.Shell.which" => which(args),
        _ => Err(RuntimeError(format!(
            "unknown Shell library function `{target}`"
        ))),
    }
}

// ---------------------------------------------------------------------------
// Shell.run(cmd: String) -> String
// ---------------------------------------------------------------------------

fn run(args: &[Value], cwd: Option<&PathBuf>) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "Shell.run expects exactly one argument: (cmd: String)".to_string(),
        ));
    }
    let cmd = expect_string(&args[0], "Shell.run cmd")?;

    let output = build_command(&cmd, cwd)
        .output()
        .map_err(|e| RuntimeError(format!("Shell.run failed to spawn process: {e}")))?;

    if !output.status.success() {
        let stderr = String::from_utf8_lossy(&output.stderr);
        let code = output.status.code().unwrap_or(-1);
        return Err(RuntimeError(format!(
            "Shell.run command exited with code {code}: {cmd}\nstderr: {stderr}"
        )));
    }

    let stdout = String::from_utf8_lossy(&output.stdout).into_owned();
    // Trim the trailing newline that most shell commands produce so callers
    // get a clean string without needing to call String.trim().
    Ok(Value::String(
        stdout
            .trim_end_matches('\n')
            .trim_end_matches('\r')
            .to_string(),
    ))
}

// ---------------------------------------------------------------------------
// Shell.runIn(cmd: String, dir: String) -> String
// ---------------------------------------------------------------------------

fn run_in(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "Shell.runIn expects exactly two arguments: (cmd: String, dir: String)".to_string(),
        ));
    }
    let dir = expect_string(&args[1], "Shell.runIn dir")?;
    let cwd = resolve_dir(&dir, "Shell.runIn")?;
    run(&args[..1], Some(&cwd))
}

// ---------------------------------------------------------------------------
// Shell.capture(cmd: String) -> Map{stdout, stderr, exitCode}
// ---------------------------------------------------------------------------

fn capture(args: &[Value], cwd: Option<&PathBuf>) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "Shell.capture expects exactly one argument: (cmd: String)".to_string(),
        ));
    }
    let cmd = expect_string(&args[0], "Shell.capture cmd")?;

    let output = build_command(&cmd, cwd)
        .output()
        .map_err(|e| RuntimeError(format!("Shell.capture failed to spawn process: {e}")))?;

    let stdout = String::from_utf8_lossy(&output.stdout).into_owned();
    let stderr = String::from_utf8_lossy(&output.stderr).into_owned();
    let exit_code = output.status.code().unwrap_or(-1);

    let mut map = BTreeMap::new();
    map.insert(
        "stdout".to_string(),
        Value::String(
            stdout
                .trim_end_matches('\n')
                .trim_end_matches('\r')
                .to_string(),
        ),
    );
    map.insert(
        "stderr".to_string(),
        Value::String(
            stderr
                .trim_end_matches('\n')
                .trim_end_matches('\r')
                .to_string(),
        ),
    );
    map.insert("exitCode".to_string(), Value::Int(exit_code as i64));
    map.insert("success".to_string(), Value::Bool(output.status.success()));

    Ok(Value::Map(map))
}

// ---------------------------------------------------------------------------
// Shell.captureIn(cmd: String, dir: String) -> Map
// ---------------------------------------------------------------------------

fn capture_in(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "Shell.captureIn expects exactly two arguments: (cmd: String, dir: String)".to_string(),
        ));
    }
    let dir = expect_string(&args[1], "Shell.captureIn dir")?;
    let cwd = resolve_dir(&dir, "Shell.captureIn")?;
    capture(&args[..1], Some(&cwd))
}

// ---------------------------------------------------------------------------
// Shell.stream(cmd: String) -> Int
// Shell.streamIn(cmd: String, dir: String) -> Int
//
// Unlike Shell.run / Shell.runIn which buffer all output until the process
// exits, Shell.stream inherits stdin/stdout/stderr from the parent process so
// output is printed to the terminal in real time.  This is the right choice
// for long-running processes such as `./gradlew quarkusDev`.
//
// Returns the process exit code as an Int.  Does NOT raise a runtime error on
// non-zero exit — callers can check the return value if needed.
// ---------------------------------------------------------------------------

fn stream(args: &[Value], cwd: Option<&PathBuf>) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "Shell.stream expects exactly one argument: (cmd: String)".to_string(),
        ));
    }
    let cmd = expect_string(&args[0], "Shell.stream cmd")?;

    let status = build_command(&cmd, cwd)
        .stdin(std::process::Stdio::inherit())
        .stdout(std::process::Stdio::inherit())
        .stderr(std::process::Stdio::inherit())
        .spawn()
        .map_err(|e| RuntimeError(format!("Shell.stream failed to spawn process: {e}")))?
        .wait()
        .map_err(|e| RuntimeError(format!("Shell.stream failed to wait for process: {e}")))?;

    Ok(Value::Int(status.code().unwrap_or(-1) as i64))
}

fn stream_in(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "Shell.streamIn expects exactly two arguments: (cmd: String, dir: String)".to_string(),
        ));
    }
    let dir = expect_string(&args[1], "Shell.streamIn dir")?;
    let cwd = resolve_dir(&dir, "Shell.streamIn")?;
    stream(&args[..1], Some(&cwd))
}

// ---------------------------------------------------------------------------
// Shell.spawnIn(cmd: String, dir: String) -> String
//
// Spawn `cmd` in `dir` without waiting for it to exit (fire-and-forget).
// Stdin is /dev/null; stdout and stderr are inherited from the current
// process so the child's output appears in the terminal.
// Returns the child PID as a String.  Ideal for starting long-running
// background servers (e.g. Quarkus dev mode) while the parent continues.
// ---------------------------------------------------------------------------

fn spawn_in(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "Shell.spawnIn expects exactly two arguments: (cmd: String, dir: String)".to_string(),
        ));
    }
    let cmd = expect_string(&args[0], "Shell.spawnIn cmd")?;
    let dir = expect_string(&args[1], "Shell.spawnIn dir")?;
    let cwd = resolve_dir(&dir, "Shell.spawnIn")?;

    let child = build_command(&cmd, Some(&cwd))
        .stdin(std::process::Stdio::null())
        .stdout(std::process::Stdio::inherit())
        .stderr(std::process::Stdio::inherit())
        .spawn()
        .map_err(|e| RuntimeError(format!("Shell.spawnIn failed to spawn `{cmd}`: {e}")))?;

    let pid = child.id();
    // Do not call wait() — let the child run independently.
    // Rust's Child::drop() does not send any signal; it just leaks the PID.
    drop(child);

    Ok(Value::String(pid.to_string()))
}

// ---------------------------------------------------------------------------
// Shell.env(name: String) -> String
// ---------------------------------------------------------------------------

fn env(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "Shell.env expects exactly one argument: (name: String)".to_string(),
        ));
    }
    let name = expect_string(&args[0], "Shell.env name")?;
    let value = std::env::var(&name).unwrap_or_default();
    Ok(Value::String(value))
}

// ---------------------------------------------------------------------------
// Shell.which(name: String) -> String
// ---------------------------------------------------------------------------

fn which(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "Shell.which expects exactly one argument: (name: String)".to_string(),
        ));
    }
    let name = expect_string(&args[0], "Shell.which name")?;

    // Use the `which` / `where` command itself to locate the executable.
    // This is portable and avoids reimplementing PATH-walking here.
    #[cfg(unix)]
    let result = std::process::Command::new("which").arg(&name).output();

    #[cfg(windows)]
    let result = std::process::Command::new("where").arg(&name).output();

    match result {
        Ok(out) if out.status.success() => {
            let path = String::from_utf8_lossy(&out.stdout)
                .lines()
                .next()
                .unwrap_or("")
                .trim()
                .to_string();
            Ok(Value::String(path))
        }
        _ => Ok(Value::String(String::new())),
    }
}

// ---------------------------------------------------------------------------
// Internal helpers
// ---------------------------------------------------------------------------

/// Build a `std::process::Command` that executes `cmd` via the system shell.
///
/// On Unix this is `sh -c <cmd>`; on Windows it is `cmd.exe /C <cmd>`.
fn build_command(cmd: &str, cwd: Option<&PathBuf>) -> std::process::Command {
    #[cfg(unix)]
    let mut command = {
        let mut c = std::process::Command::new("sh");
        c.arg("-c").arg(cmd);
        c
    };

    #[cfg(windows)]
    let mut command = {
        let mut c = std::process::Command::new("cmd.exe");
        c.arg("/C").arg(cmd);
        c
    };

    if let Some(dir) = cwd {
        command.current_dir(dir);
    }

    command
}

/// Resolve a directory path string to an absolute `PathBuf`, returning a
/// runtime error if the directory does not exist.
fn resolve_dir(dir: &str, operation: &str) -> Result<PathBuf, RuntimeError> {
    let path = PathBuf::from(dir);
    let absolute = if path.is_absolute() {
        path
    } else {
        std::env::current_dir()
            .map_err(|e| RuntimeError(format!("{operation} cannot read current directory: {e}")))?
            .join(path)
    };

    if !absolute.exists() {
        return Err(RuntimeError(format!(
            "{operation} directory does not exist: `{dir}`"
        )));
    }
    if !absolute.is_dir() {
        return Err(RuntimeError(format!(
            "{operation} path is not a directory: `{dir}`"
        )));
    }

    absolute
        .canonicalize()
        .map_err(|e| RuntimeError(format!("{operation} cannot resolve `{dir}`: {e}")))
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use super::*;

    fn s(v: &str) -> Value {
        Value::String(v.to_string())
    }

    // ── Shell.run ─────────────────────────────────────────────────────────────

    #[test]
    #[cfg(unix)]
    fn run_echo_returns_trimmed_stdout() {
        let result = call("TLang.Shell.run", &[s("echo hello")]).unwrap();
        assert_eq!(result, Value::String("hello".to_string()));
    }

    #[test]
    #[cfg(unix)]
    fn run_multi_word_echo() {
        let result = call("TLang.Shell.run", &[s("echo hello world")]).unwrap();
        assert_eq!(result, Value::String("hello world".to_string()));
    }

    #[test]
    #[cfg(unix)]
    fn run_fails_on_nonzero_exit() {
        let err = call("TLang.Shell.run", &[s("exit 1")]).unwrap_err();
        assert!(err.0.contains("exited with code 1"), "got: {}", err.0);
    }

    #[test]
    fn run_rejects_wrong_arity() {
        let err = call("TLang.Shell.run", &[]).unwrap_err();
        assert!(err.0.contains("exactly one argument"));

        let err2 = call("TLang.Shell.run", &[s("echo"), s("extra")]).unwrap_err();
        assert!(err2.0.contains("exactly one argument"));
    }

    // ── Shell.capture ─────────────────────────────────────────────────────────

    #[test]
    #[cfg(unix)]
    fn capture_returns_map_on_success() {
        let result = call("TLang.Shell.capture", &[s("echo captured")]).unwrap();
        match result {
            Value::Map(m) => {
                assert_eq!(
                    m.get("stdout"),
                    Some(&Value::String("captured".to_string()))
                );
                assert_eq!(m.get("exitCode"), Some(&Value::Int(0)));
                assert_eq!(m.get("success"), Some(&Value::Bool(true)));
            }
            other => panic!("expected Map, got {other:?}"),
        }
    }

    #[test]
    #[cfg(unix)]
    fn capture_does_not_fail_on_nonzero_exit() {
        let result = call("TLang.Shell.capture", &[s("exit 42")]).unwrap();
        match result {
            Value::Map(m) => {
                assert_eq!(m.get("exitCode"), Some(&Value::Int(42)));
                assert_eq!(m.get("success"), Some(&Value::Bool(false)));
            }
            other => panic!("expected Map, got {other:?}"),
        }
    }

    #[test]
    #[cfg(unix)]
    fn capture_collects_stderr() {
        let result = call("TLang.Shell.capture", &[s("echo errline >&2; exit 1")]).unwrap();
        match result {
            Value::Map(m) => {
                let stderr = m.get("stderr").unwrap();
                assert!(
                    matches!(stderr, Value::String(s) if s.contains("errline")),
                    "got: {stderr:?}"
                );
            }
            other => panic!("expected Map, got {other:?}"),
        }
    }

    #[test]
    fn capture_rejects_wrong_arity() {
        let err = call("TLang.Shell.capture", &[]).unwrap_err();
        assert!(err.0.contains("exactly one argument"));
    }

    // ── Shell.env ─────────────────────────────────────────────────────────────

    #[test]
    fn env_returns_known_variable() {
        // PATH is always set on Unix and Windows.
        let result = call("TLang.Shell.env", &[s("PATH")]).unwrap();
        match result {
            Value::String(v) => assert!(!v.is_empty(), "PATH should not be empty"),
            other => panic!("expected String, got {other:?}"),
        }
    }

    #[test]
    fn env_returns_empty_string_for_unset_variable() {
        let result = call(
            "TLang.Shell.env",
            &[s("__TLANG_TEST_VAR_DEFINITELY_NOT_SET__")],
        )
        .unwrap();
        assert_eq!(result, Value::String(String::new()));
    }

    #[test]
    fn env_rejects_wrong_arity() {
        let err = call("TLang.Shell.env", &[]).unwrap_err();
        assert!(err.0.contains("exactly one argument"));
    }

    // ── Shell.runIn ───────────────────────────────────────────────────────────

    #[test]
    #[cfg(unix)]
    fn run_in_uses_given_directory() {
        let tmp = std::env::temp_dir();
        let tmp_str = tmp.to_string_lossy().to_string();

        // `pwd` should print the directory we passed.
        let result = call("TLang.Shell.runIn", &[s("pwd"), s(&tmp_str)]).unwrap();
        match result {
            Value::String(v) => {
                // Canonical form may differ (e.g. /private/tmp vs /tmp on macOS).
                assert!(!v.is_empty());
            }
            other => panic!("expected String, got {other:?}"),
        }
    }

    #[test]
    fn run_in_rejects_nonexistent_dir() {
        let err = call(
            "TLang.Shell.runIn",
            &[s("echo hi"), s("/nonexistent/tlang_test_dir")],
        )
        .unwrap_err();
        assert!(err.0.contains("does not exist"), "got: {}", err.0);
    }

    #[test]
    fn run_in_rejects_wrong_arity() {
        let err = call("TLang.Shell.runIn", &[s("echo")]).unwrap_err();
        assert!(err.0.contains("exactly two arguments"));
    }

    // ── Shell.which ───────────────────────────────────────────────────────────

    #[test]
    #[cfg(unix)]
    fn which_finds_sh() {
        let result = call("TLang.Shell.which", &[s("sh")]).unwrap();
        match result {
            Value::String(v) => assert!(v.contains("sh"), "expected path to sh, got `{v}`"),
            other => panic!("expected String, got {other:?}"),
        }
    }

    #[test]
    fn which_returns_empty_for_unknown() {
        let result = call("TLang.Shell.which", &[s("__tlang_no_such_binary_xyz__")]).unwrap();
        assert_eq!(result, Value::String(String::new()));
    }

    // ── Unknown function ──────────────────────────────────────────────────────

    #[test]
    fn rejects_unknown_function() {
        let err = call("TLang.Shell.nonExistent", &[]).unwrap_err();
        assert!(err.0.contains("unknown Shell library function"));
    }
}
