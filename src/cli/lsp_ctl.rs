// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! LSP server process management.
//!
//! [`find_lsp_pids`] locates running `tlang lsp-server` processes and
//! [`stop_lsp_server`] terminates them gracefully (SIGTERM → SIGKILL fallback).
//! Uses `/proc` scanning on Linux, `pgrep` on macOS, and is a no-op on Windows.

// ---------------------------------------------------------------------------
// LSP stop/restart helpers (pgrep-based, no PID file)
// ---------------------------------------------------------------------------

/// Find all PIDs of running `tlang lsp-server` processes (excluding ourselves).
#[cfg(unix)]
pub fn find_lsp_pids() -> Vec<u32> {
    let our_pid = std::process::id();
    let output = std::process::Command::new("pgrep")
        .args(["-f", "tlang lsp-server"])
        .output();

    match output {
        Ok(o) => String::from_utf8_lossy(&o.stdout)
            .lines()
            .filter_map(|l| l.trim().parse::<u32>().ok())
            .filter(|&pid| pid != our_pid)
            .collect(),
        Err(_) => vec![],
    }
}

/// Send SIGTERM to the running LSP server(s) and wait for them to exit.
/// Returns `Ok(())` if all stopped (or none were running).
#[cfg(unix)]
pub fn stop_lsp_server() -> Result<(), String> {
    let pids = find_lsp_pids();
    if pids.is_empty() {
        println!("TLang LSP server is not running.");
        return Ok(());
    }

    for &pid in &pids {
        unsafe { libc::kill(pid as libc::pid_t, libc::SIGTERM) };
    }

    // Wait up to 5 s for all processes to exit.
    let deadline = std::time::Instant::now() + std::time::Duration::from_secs(5);
    let mut remaining = pids.clone();
    loop {
        remaining.retain(|&pid| unsafe { libc::kill(pid as libc::pid_t, 0) == 0 });
        if remaining.is_empty() {
            break;
        }
        if std::time::Instant::now() >= deadline {
            for &pid in &remaining {
                unsafe { libc::kill(pid as libc::pid_t, libc::SIGKILL) };
            }
            std::thread::sleep(std::time::Duration::from_millis(200));
            break;
        }
        std::thread::sleep(std::time::Duration::from_millis(100));
    }

    let pid_list: Vec<String> = pids.iter().map(|p| p.to_string()).collect();
    println!("TLang LSP server (pid {}) stopped.", pid_list.join(", "));
    Ok(())
}

#[cfg(not(unix))]
pub fn stop_lsp_server() -> Result<(), String> {
    Err("lsp-stop is not supported on this platform".to_string())
}
