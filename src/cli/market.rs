// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang Market (registry) commands.
//!
//! Implements `login`, `logout`, `install`, `pull`, and dependency resolution
//! against the remote registry. Downloaded `.tbag` archives are extracted into
//! the local `.tbox` package store.

use std::path::{Path, PathBuf};

use tlang::{
    DependencyLocator, VersionSpec, own_tbox_tbag_path, parse_manifest, parse_version_spec,
    read_manifest_from_tbag_bytes, registry_dep_tbag_path,
};

const MARKET_PROD_URL: &str = "https://market.tlang.dev";
const MARKET_DEV_URL: &str = "http://localhost:8080";

/// Returns the path to the user's CLI package store: `~/.tlang/cli/`.
pub fn cli_dir() -> PathBuf {
    std::env::var_os("HOME")
        .or_else(|| std::env::var_os("USERPROFILE"))
        .map(|h| PathBuf::from(h).join(".tlang").join("cli"))
        .unwrap_or_else(|| {
            std::env::current_dir()
                .unwrap_or_else(|_| PathBuf::from("."))
                .join(".tlang")
                .join("cli")
        })
}

/// Parse a package reference of the form `Org/Project/Name:version:stability:release`
/// or `Org/Project/Name:latest`.
///
/// Returns `(path_segments, version_spec)` where `version_spec` is `None` for "latest".
pub fn parse_package_ref(s: &str) -> Result<(Vec<String>, Option<VersionSpec>), String> {
    let colon = s
        .find(':')
        .ok_or_else(|| format!("invalid package reference '{s}': expected 'Org/Project/Name:version' or 'Org/Project/Name:latest'"))?;
    let path_str = &s[..colon];
    let ver_str = &s[colon + 1..];

    let path: Vec<String> = path_str.split('/').map(|s| s.to_string()).collect();
    if path.len() < 2 || path.iter().any(|s| s.is_empty()) {
        return Err(format!(
            "invalid package path '{path_str}': expected at least 'Org/Name'"
        ));
    }

    if ver_str == "latest" {
        Ok((path, None))
    } else {
        let version = parse_version_spec(ver_str)
            .ok_or_else(|| format!("invalid version spec '{ver_str}'"))?;
        Ok((path, Some(version)))
    }
}

/// Path to the stored market auth token.
pub fn market_token_path() -> PathBuf {
    std::env::var_os("HOME")
        .or_else(|| std::env::var_os("USERPROFILE"))
        .map(|h| PathBuf::from(h).join(".tlang").join("market_token"))
        .unwrap_or_else(|| PathBuf::from(".tlang").join("market_token"))
}

/// Read the stored market auth token (env var takes precedence).
pub fn load_market_token() -> Option<String> {
    std::env::var("TLANG_MARKET_TOKEN").ok().or_else(|| {
        std::fs::read_to_string(market_token_path())
            .ok()
            .map(|s| s.trim().to_string())
            .filter(|s| !s.is_empty())
    })
}

/// Return the market base URL: `--dev` / `TLANG_MARKET_URL` env var / prod.
pub fn market_base_url(dev: bool) -> String {
    if dev {
        return MARKET_DEV_URL.to_string();
    }
    std::env::var("TLANG_MARKET_URL").unwrap_or_else(|_| MARKET_PROD_URL.to_string())
}

/// `tlang login` — interactive prompt then saves JWT to `~/.tlang/market_token`.
pub fn login_to_market(dev: bool) -> Result<(), String> {
    use std::io::{self, Write};
    let base = market_base_url(dev);
    println!("Signing in to {base}");
    println!();

    print!("Email: ");
    io::stdout().flush().ok();
    let mut email = String::new();
    io::stdin().read_line(&mut email).map_err(|e| format!("read error: {e}"))?;
    let email = email.trim().to_string();

    print!("Password: ");
    io::stdout().flush().ok();
    // Disable echo on Unix for password entry.
    #[cfg(unix)]
    let password = {
        let fd = 0i32;
        let mut old: libc::termios = unsafe { std::mem::zeroed() };
        unsafe { libc::tcgetattr(fd, &mut old) };
        let mut silent = old;
        silent.c_lflag &= !libc::ECHO;
        unsafe { libc::tcsetattr(fd, libc::TCSANOW, &silent) };
        let mut pw = String::new();
        io::stdin().read_line(&mut pw).ok();
        unsafe { libc::tcsetattr(fd, libc::TCSANOW, &old) };
        println!();
        pw.trim().to_string()
    };
    #[cfg(not(unix))]
    let password = {
        let mut pw = String::new();
        io::stdin().read_line(&mut pw).map_err(|e| format!("read error: {e}"))?;
        pw.trim().to_string()
    };

    if email.is_empty() || password.is_empty() {
        return Err("Email and password are required.".to_string());
    }

    let url = format!("{base}/api/auth/cli-login");
    let client = reqwest::blocking::Client::new();
    let resp = client
        .post(&url)
        .json(&serde_json::json!({"email": email, "password": password}))
        .send()
        .map_err(|e| format!("login request failed: {e}"))?;

    let status = resp.status();
    let body: serde_json::Value = resp
        .json()
        .map_err(|e| format!("invalid response: {e}"))?;

    if !status.is_success() {
        let msg = body["error"].as_str().unwrap_or("Login failed");
        return Err(msg.to_string());
    }

    let token = body["token"].as_str().ok_or("No token in response")?;
    let username = body["username"].as_str().unwrap_or(&email);

    let token_path = market_token_path();
    std::fs::create_dir_all(token_path.parent().unwrap())
        .map_err(|e| format!("cannot create .tlang dir: {e}"))?;
    std::fs::write(&token_path, token)
        .map_err(|e| format!("cannot save token: {e}"))?;

    println!("Logged in as {username}");
    println!("Token saved to {}", token_path.display());
    Ok(())
}

/// `tlang logout` — removes the stored auth token.
pub fn logout_from_market() -> Result<(), String> {
    let path = market_token_path();
    if path.exists() {
        std::fs::remove_file(&path).map_err(|e| format!("cannot remove token: {e}"))?;
        println!("Logged out. Token removed from {}", path.display());
    } else {
        println!("Not logged in (no token found).");
    }
    Ok(())
}

/// `tlang install <slug>` — download and install a package from the market.
pub fn install_from_market(slug: &str, dev: bool) -> Result<(), String> {
    let base = market_base_url(dev);
    let url = format!("{base}/api/upload/download-latest/{slug}");

    println!("Resolving '{slug}' from market…");

    let client = reqwest::blocking::Client::new();
    let resp = client
        .get(&url)
        .send()
        .map_err(|e| format!("market request failed: {e}"))?;

    let status = resp.status();
    if status == reqwest::StatusCode::NOT_FOUND {
        return Err(format!(
            "Package '{slug}' not found on the market.\n  \
             Try `tlang search {slug}` to look for similar packages."
        ));
    }
    if !status.is_success() {
        return Err(format!("market returned {status} for '{slug}'"));
    }

    let info: serde_json::Value = resp
        .json()
        .map_err(|e| format!("invalid response: {e}"))?;

    let download_url = info["url"].as_str().ok_or("No download URL in response")?;
    let version = info["version"].as_str().unwrap_or("unknown");
    let name = info["name"].as_str().unwrap_or(slug);

    println!("  found: {name} v{version}");
    println!("  downloading…");

    let bytes = client
        .get(download_url)
        .send()
        .map_err(|e| format!("download failed: {e}"))?
        .bytes()
        .map_err(|e| format!("download read failed: {e}"))?;

    let dest_dir = cli_dir();
    std::fs::create_dir_all(&dest_dir)
        .map_err(|e| format!("cannot create CLI dir: {e}"))?;
    let dest = dest_dir.join(format!("{slug}.tbag"));

    // Remove old extraction directory so the new version is used on next exec.
    let extract_dir = dest_dir.join(slug);
    if extract_dir.exists() {
        std::fs::remove_dir_all(&extract_dir)
            .map_err(|e| format!("cannot remove old version: {e}"))?;
    }

    std::fs::write(&dest, &bytes)
        .map_err(|e| format!("cannot write '{slug}.tbag': {e}"))?;

    println!();
    println!("Installed '{name}' v{version} to {}", dest.display());
    println!("Run with: tlang exec {slug} [args...]");
    Ok(())
}

/// Pull a package by name (and optional version string) from the market into
/// the local tbox.  Returns the path of the saved `.tbag` file.
///
/// Steps:
///   1. `GET /api/packages/slug/{slug}` — look up package id
///   2. `GET /api/versions/by-package/{id}` — get version list
///   3. Pick matching version (or first = latest when none specified)
///   4. `GET /api/upload/download/{versionId}` — get presigned URL
///   5. Download `.tbag` bytes from presigned URL
///   6. Parse manifest from bytes to get tbox path components
///   7. Write to `~/.tlang/tbox/{org}/{project}/{name}/{ver}/{stab}/{rel}/{name}.tbag`
pub fn pull_from_market(
    name: &str,
    version_filter: Option<&str>,
    base_url: &str,
    tbox: &Path,
) -> Result<(PathBuf, String), String> {
    let client = reqwest::blocking::Client::builder()
        .timeout(std::time::Duration::from_secs(120))
        .build()
        .map_err(|e| format!("cannot build HTTP client: {e}"))?;

    // Step 1: look up by slug
    let slug = name
        .to_lowercase()
        .chars()
        .map(|c| if c.is_alphanumeric() || c == '-' { c } else { '-' })
        .collect::<String>();
    let pkg_url = format!("{base_url}/api/packages/slug/{slug}");
    let pkg_resp = client
        .get(&pkg_url)
        .send()
        .map_err(|e| format!("market request failed: {e}"))?;
    if pkg_resp.status().as_u16() == 404 {
        return Err(format!(
            "package '{name}' not found on market ({base_url})"
        ));
    }
    if !pkg_resp.status().is_success() {
        return Err(format!(
            "market returned HTTP {} for '{name}'",
            pkg_resp.status()
        ));
    }
    let pkg_json: serde_json::Value = pkg_resp
        .json()
        .map_err(|e| format!("cannot parse package response: {e}"))?;
    let pkg_id = pkg_json["id"]
        .as_i64()
        .ok_or_else(|| "market response missing 'id'".to_string())?;

    // Step 2: get version list
    let versions_url = format!("{base_url}/api/versions/by-package/{pkg_id}");
    let vers_resp = client
        .get(&versions_url)
        .send()
        .map_err(|e| format!("versions request failed: {e}"))?;
    if !vers_resp.status().is_success() {
        return Err(format!(
            "market returned HTTP {} for versions of '{name}'",
            vers_resp.status()
        ));
    }
    let versions_val: serde_json::Value = vers_resp
        .json()
        .map_err(|e| format!("cannot parse versions response: {e}"))?;
    let versions = versions_val
        .as_array()
        .ok_or_else(|| "versions response is not an array".to_string())?;
    if versions.is_empty() {
        return Err(format!("package '{name}' has no versions on the market"));
    }

    // Step 3: pick version
    let version_entry = if let Some(vf) = version_filter {
        versions
            .iter()
            .find(|v| v["versionString"].as_str().map(|s| s == vf).unwrap_or(false))
            .ok_or_else(|| format!("version '{vf}' not found for package '{name}'"))?
    } else {
        &versions[0]
    };
    let version_id = version_entry["id"]
        .as_i64()
        .ok_or_else(|| "version entry missing 'id'".to_string())?;

    // Step 4: get presigned download URL
    let dl_info_url = format!("{base_url}/api/upload/download/{version_id}");
    let dl_info_resp = client
        .get(&dl_info_url)
        .send()
        .map_err(|e| format!("download URL request failed: {e}"))?;
    if !dl_info_resp.status().is_success() {
        return Err(format!(
            "market returned HTTP {} for download URL",
            dl_info_resp.status()
        ));
    }
    let dl_json: serde_json::Value = dl_info_resp
        .json()
        .map_err(|e| format!("cannot parse download URL response: {e}"))?;
    let presigned_url = dl_json["url"]
        .as_str()
        .ok_or_else(|| "download response missing 'url'".to_string())?
        .to_string();

    // Step 5: download bytes
    let bytes_resp = client
        .get(&presigned_url)
        .send()
        .map_err(|e| format!("tbag download failed: {e}"))?;
    if !bytes_resp.status().is_success() {
        return Err(format!(
            "tbag download returned HTTP {}",
            bytes_resp.status()
        ));
    }
    let tbag_bytes = bytes_resp
        .bytes()
        .map_err(|e| format!("failed to read tbag bytes: {e}"))?;

    // Step 6: parse manifest from bytes to get the tbox path components
    let manifest_text = read_manifest_from_tbag_bytes(&tbag_bytes)?;
    let manifest =
        parse_manifest(&manifest_text).map_err(|e| format!("cannot parse manifest from downloaded tbag: {e}"))?;

    // Step 7: write to tbox
    let dest = own_tbox_tbag_path(tbox, &manifest);
    std::fs::create_dir_all(dest.parent().expect("tbag path has parent"))
        .map_err(|e| format!("cannot create tbox dir: {e}"))?;
    std::fs::write(&dest, &tbag_bytes)
        .map_err(|e| format!("cannot write tbag to '{}': {e}", dest.display()))?;

    Ok((dest, manifest.version.clone()))
}

/// For every registry dependency in `manifest` that is missing from `tbox`,
/// pull it automatically from the market.  Silently skips deps that are
/// already present so compilation stays fast on the happy path.
pub fn ensure_deps_from_market(
    manifest: &tlang::Manifest,
    tbox: &Path,
    base_url: &str,
) -> Result<(), String> {
    for dep in &manifest.dependencies {
        if let DependencyLocator::Registry { path, version } = &dep.locator {
            if let Some(tbag_path) = registry_dep_tbag_path(tbox, dep) {
                if !tbag_path.exists() {
                    let pkg_name = path.last().expect("path is non-empty");
                    println!(
                        "  [auto-pull] '{}' not in tbox — pulling {} v{} from {}…",
                        dep.alias, pkg_name, version.version, base_url
                    );
                    let (dest, _) = pull_from_market(
                        pkg_name,
                        Some(&version.version),
                        base_url,
                        tbox,
                    )?;
                    println!("             → {}", dest.display());
                }
            }
        }
    }
    Ok(())
}

/// Locate a `.tbag` for a given package path + version in the tbox.
///
/// When `version` is `None`, the most-recently-modified tbag under the package
/// directory is returned ("latest").
pub fn find_tbag_in_tbox(
    tbox: &Path,
    path: &[String],
    version: Option<&VersionSpec>,
) -> Result<PathBuf, String> {
    let name = path.last().expect("path is non-empty");
    let mut base = tbox.to_path_buf();
    for segment in path {
        base.push(segment);
    }

    if let Some(ver) = version {
        let mut dir = base.clone();
        dir.push(&ver.version);
        if let Some(stability) = &ver.stability {
            dir.push(stability.to_string());
        }
        if let Some(release) = ver.release_number {
            dir.push(release.to_string());
        }
        let tbag = dir.join(format!("{name}.tbag"));
        if tbag.exists() {
            Ok(tbag)
        } else {
            Err(format!("package not found in tbox: '{}'", tbag.display()))
        }
    } else {
        find_latest_tbag_under(&base, name)
    }
}

/// Walk `base` recursively and return the path of the most-recently-modified
/// `{name}.tbag` file found, if any.
pub fn find_latest_tbag_under(base: &Path, name: &str) -> Result<PathBuf, String> {
    let tbag_name = format!("{name}.tbag");
    let mut best: Option<(PathBuf, std::time::SystemTime)> = None;

    fn walk(
        dir: &Path,
        tbag_name: &str,
        best: &mut Option<(PathBuf, std::time::SystemTime)>,
    ) {
        let Ok(entries) = std::fs::read_dir(dir) else {
            return;
        };
        for entry in entries.flatten() {
            let path = entry.path();
            if path.is_dir() {
                walk(&path, tbag_name, best);
            } else if path
                .file_name()
                .and_then(|n| n.to_str())
                == Some(tbag_name)
            {
                if let Ok(mtime) = std::fs::metadata(&path).and_then(|m| m.modified()) {
                    let is_newer = best.as_ref().map(|(_, t)| mtime > *t).unwrap_or(true);
                    if is_newer {
                        *best = Some((path, mtime));
                    }
                }
            }
        }
    }

    walk(base, &tbag_name, &mut best);

    best.map(|(p, _)| p).ok_or_else(|| {
        format!(
            "no package '{}' found in tbox under '{}'",
            name,
            base.display()
        )
    })
}
