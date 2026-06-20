// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! `.tbag` – TLang package archive format.
//!
//! A `.tbag` file is a standard ZIP archive that bundles a compiled TLang
//! package for distribution through the TLang package store (tbox).
//!
//! ## Archive contents
//!
//! ```text
//! manifest.yml                   — project manifest (always at archive root)
//! <relative/path/to/File.tlang>  — all TLang source files (relative to project root)
//! target/tlang/<rel>.tlangc      — all compiled bytecode files
//! ```
//!
//! Source files are included so that consumer projects can compile against the
//! package; bytecode is included for the runtime's prefer-bytecode path.
//!
//! ## Tbox layout
//!
//! After `push`, the archive is placed at:
//! ```text
//! {tbox}/{org}/{project}/{name}/{version}/{stability}/{release}/{name}.tbag
//! ```
//!
//! When a consumer project first uses a registry dependency, the loader calls
//! [`ensure_extracted`] which unpacks the archive into the same directory so
//! that source and bytecode files are directly accessible.

use std::io::{Read, Write};
use std::path::{Path, PathBuf};

use zip::ZipArchive;
use zip::write::{FileOptions, ZipWriter};

/// Extension used for TLang package archives.
pub const TBAG_EXT: &str = "tbag";

/// Sentinel file written into an extracted dep directory so we know the tbag
/// has already been unpacked and we don't repeat the work on every run.
const EXTRACTED_SENTINEL: &str = ".tbag_extracted";

// ---------------------------------------------------------------------------
// Packing
// ---------------------------------------------------------------------------

/// Create a `.tbag` archive from a compiled project.
///
/// The archive is written to `{project_root}/target/tlang/{name}.tbag` and
/// that path is returned on success.
///
/// `name` is the package name taken from the manifest (`manifest.name`).
///
/// The archive contains:
/// - `manifest.yml` at the archive root.
/// - Every `*.tlang` source file under `project_root`, preserving relative
///   paths.  Files inside `target/` are skipped.
/// - Every `*.tlangc` bytecode file under `project_root/target/tlang/`,
///   preserving relative paths inside `target/tlang/`.
pub fn pack(
    project_root: &Path,
    manifest_path: &Path,
    package_name: &str,
) -> Result<PathBuf, String> {
    // Output path.
    let out_dir = project_root.join("target").join("tlang");
    std::fs::create_dir_all(&out_dir)
        .map_err(|e| format!("cannot create '{}': {e}", out_dir.display()))?;
    let tbag_path = out_dir.join(format!("{package_name}.tbag"));

    let file = std::fs::File::create(&tbag_path)
        .map_err(|e| format!("cannot create '{}': {e}", tbag_path.display()))?;
    let mut zip = ZipWriter::new(file);
    let options: FileOptions<'_, ()> =
        FileOptions::default().compression_method(zip::CompressionMethod::Deflated);

    // ---- manifest.yml --------------------------------------------------------
    add_file_to_zip(&mut zip, manifest_path, "manifest.yml", &options)?;

    // ---- source .tlang files (test files excluded) ---------------------------
    let target_dir = project_root.join("target");
    collect_files(
        project_root,
        project_root,
        &target_dir,
        "tlang",
        "Test",
        &mut zip,
        &options,
    )?;

    // ---- README.md (optional) -----------------------------------------------
    let readme_path = project_root.join("README.md");
    if readme_path.exists() {
        add_file_to_zip(&mut zip, &readme_path, "README.md", &options)?;
    }

    // ---- bytecode .tlangc files (test bytecode excluded) ---------------------
    let bc_dir = project_root.join("target").join("tlang");
    if bc_dir.exists() {
        collect_files(
            &bc_dir,
            project_root,
            &PathBuf::new(),
            "tlangc",
            "Test",
            &mut zip,
            &options,
        )?;
    }

    zip.finish()
        .map_err(|e| format!("cannot finalise tbag '{}': {e}", tbag_path.display()))?;

    Ok(tbag_path)
}

// ---------------------------------------------------------------------------
// Pushing
// ---------------------------------------------------------------------------

/// Copy `tbag_path` to `dest_path`, creating parent directories as needed.
///
/// `dest_path` is the full target path including the filename, as returned by
/// [`crate::manifest::own_tbox_tbag_path`].
pub fn push(tbag_path: &Path, dest_path: &Path) -> Result<(), String> {
    if let Some(parent) = dest_path.parent() {
        std::fs::create_dir_all(parent)
            .map_err(|e| format!("cannot create '{}': {e}", parent.display()))?;
    }
    std::fs::copy(tbag_path, dest_path)
        .map_err(|e| format!("cannot copy tbag to '{}': {e}", dest_path.display()))?;
    Ok(())
}

// ---------------------------------------------------------------------------
// Extraction
// ---------------------------------------------------------------------------

/// Extract all files from `tbag_path` into `dest_dir`, preserving the
/// relative paths stored in the archive.  Parent directories are created
/// automatically.
///
/// After extraction a sentinel file (`.tbag_extracted`) is written into
/// `dest_dir` so that future calls to [`ensure_extracted`] are free.
/// Lexically normalize a path by resolving `.` and `..` components without
/// touching the filesystem.  This is used to detect path traversal attempts
/// on paths that do not yet exist on disk (so `canonicalize` cannot be used).
fn lexical_clean(path: &Path) -> std::path::PathBuf {
    use std::path::Component;
    let mut stack: Vec<std::ffi::OsString> = Vec::new();
    for component in path.components() {
        match component {
            Component::CurDir => {}
            Component::ParentDir => {
                stack.pop();
            }
            Component::Normal(s) => stack.push(s.to_owned()),
            Component::RootDir => {
                stack.clear();
                stack.push(std::ffi::OsString::from("/"));
            }
            Component::Prefix(p) => {
                stack.clear();
                stack.push(p.as_os_str().to_owned());
            }
        }
    }
    stack.iter().collect()
}

pub fn extract(tbag_path: &Path, dest_dir: &Path) -> Result<(), String> {
    std::fs::create_dir_all(dest_dir)
        .map_err(|e| format!("cannot create '{}': {e}", dest_dir.display()))?;

    // Canonicalize dest_dir now that it exists so we have an absolute,
    // symlink-resolved anchor to check all extracted paths against.
    let canon_dest = dest_dir
        .canonicalize()
        .map_err(|e| format!("cannot resolve '{}': {e}", dest_dir.display()))?;

    let file = std::fs::File::open(tbag_path)
        .map_err(|e| format!("cannot open tbag '{}': {e}", tbag_path.display()))?;
    let mut archive = ZipArchive::new(file)
        .map_err(|e| format!("cannot read tbag '{}': {e}", tbag_path.display()))?;

    for i in 0..archive.len() {
        let mut entry = archive
            .by_index(i)
            .map_err(|e| format!("tbag read error: {e}"))?;

        // Sanitise: first do a cheap string-level check, then do a robust
        // canonicalized-path check to catch any remaining traversal tricks
        // (encoded separators, deeply nested `..` sequences, etc.).
        let name = entry.name().to_string();
        if name.starts_with('/') || name.contains("..") {
            return Err(format!("tbag contains suspicious path: '{name}'"));
        }

        let out_path = canon_dest.join(&name);

        // Lexically resolve `.` / `..` in the joined path and confirm it
        // still descends from the destination directory.
        let clean = lexical_clean(&out_path);
        if !clean.starts_with(&canon_dest) {
            return Err(format!(
                "tbag path traversal detected: '{name}' would escape destination directory"
            ));
        }

        // Use the cleaned path for all subsequent operations.
        let out_path = clean;

        if name.ends_with('/') {
            // Directory entry.
            std::fs::create_dir_all(&out_path)
                .map_err(|e| format!("cannot create dir '{}': {e}", out_path.display()))?;
            continue;
        }

        if let Some(parent) = out_path.parent() {
            std::fs::create_dir_all(parent)
                .map_err(|e| format!("cannot create dir '{}': {e}", parent.display()))?;
        }

        let mut out_file = std::fs::File::create(&out_path)
            .map_err(|e| format!("cannot create '{}': {e}", out_path.display()))?;
        let mut buf = Vec::new();
        entry
            .read_to_end(&mut buf)
            .map_err(|e| format!("tbag read error for '{name}': {e}"))?;
        out_file
            .write_all(&buf)
            .map_err(|e| format!("cannot write '{}': {e}", out_path.display()))?;
    }

    // Write sentinel.
    let sentinel = dest_dir.join(EXTRACTED_SENTINEL);
    std::fs::write(&sentinel, b"")
        .map_err(|e| format!("cannot write sentinel '{}': {e}", sentinel.display()))?;

    Ok(())
}

/// Check whether a tbag has already been extracted into `dest_dir` by looking
/// for the sentinel file.
pub fn is_extracted(dest_dir: &Path) -> bool {
    dest_dir.join(EXTRACTED_SENTINEL).exists()
}

/// Ensure that the tbag in `dep_dir` (if any) has been extracted.
///
/// Searches for a `*.tbag` file in `dep_dir`.  If one is found and the
/// sentinel is not yet present, extracts it.  If no tbag is found, does
/// nothing.
pub fn ensure_extracted(dep_dir: &Path) -> Result<(), String> {
    if is_extracted(dep_dir) {
        return Ok(());
    }
    if let Some(tbag_path) = find_tbag(dep_dir) {
        extract(&tbag_path, dep_dir)?;
    }
    Ok(())
}

// ---------------------------------------------------------------------------
// Manifest reading
// ---------------------------------------------------------------------------

/// Read the raw text of `manifest.yml` from a `.tbag` archive without
/// extracting the entire archive to disk.
pub fn read_manifest_from_tbag(tbag_path: &Path) -> Result<String, String> {
    let file = std::fs::File::open(tbag_path)
        .map_err(|e| format!("cannot open tbag '{}': {e}", tbag_path.display()))?;
    let mut archive = ZipArchive::new(file)
        .map_err(|e| format!("cannot read tbag '{}': {e}", tbag_path.display()))?;
    let mut entry = archive
        .by_name("manifest.yml")
        .map_err(|_| format!("tbag '{}' has no manifest.yml", tbag_path.display()))?;
    let mut content = String::new();
    entry
        .read_to_string(&mut content)
        .map_err(|e| format!("cannot read manifest.yml from '{}': {e}", tbag_path.display()))?;
    Ok(content)
}

/// Read `manifest.yml` from raw `.tbag` bytes (no file on disk needed).
pub fn read_manifest_from_tbag_bytes(bytes: &[u8]) -> Result<String, String> {
    let cursor = std::io::Cursor::new(bytes);
    let mut archive = ZipArchive::new(cursor)
        .map_err(|e| format!("cannot read tbag from bytes: {e}"))?;
    let mut entry = archive
        .by_name("manifest.yml")
        .map_err(|_| "tbag bytes have no manifest.yml".to_string())?;
    let mut content = String::new();
    entry
        .read_to_string(&mut content)
        .map_err(|e| format!("cannot read manifest.yml from tbag bytes: {e}"))?;
    Ok(content)
}

// ---------------------------------------------------------------------------
// Discovery
// ---------------------------------------------------------------------------

/// Find the first `*.tbag` file in `dir` (non-recursive).
///
/// Returns `None` if the directory does not exist or contains no tbag files.
pub fn find_tbag(dir: &Path) -> Option<PathBuf> {
    let entries = std::fs::read_dir(dir).ok()?;
    for entry in entries.flatten() {
        let path = entry.path();
        if path.extension().and_then(|e| e.to_str()) == Some(TBAG_EXT) {
            return Some(path);
        }
    }
    None
}

// ---------------------------------------------------------------------------
// Internal helpers
// ---------------------------------------------------------------------------

/// Recursively collect all files with `extension` under `dir`, skipping the
/// `skip_dir` subtree and any file whose stem ends with `skip_stem_suffix`
/// (e.g. `"Test"` to exclude `*Test.tlang` test files), and add them to `zip`.
///
/// The entry name stored in the archive is the path of each file relative to
/// `archive_root`.
fn collect_files(
    dir: &Path,
    archive_root: &Path,
    skip_dir: &Path,
    extension: &str,
    skip_stem_suffix: &str,
    zip: &mut ZipWriter<std::fs::File>,
    options: &FileOptions<'_, ()>,
) -> Result<(), String> {
    let entries = std::fs::read_dir(dir)
        .map_err(|e| format!("cannot read directory '{}': {e}", dir.display()))?;

    for entry in entries.flatten() {
        let path = entry.path();

        // Skip the excluded subtree (e.g. target/ when collecting sources).
        if !skip_dir.as_os_str().is_empty() && path.starts_with(skip_dir) {
            continue;
        }

        if path.is_dir() {
            collect_files(&path, archive_root, skip_dir, extension, skip_stem_suffix, zip, options)?;
        } else if path.extension().and_then(|e| e.to_str()) == Some(extension) {
            // Skip test files (e.g. *Test.tlang, *Test.tlangc).
            let stem = path
                .file_stem()
                .and_then(|s| s.to_str())
                .unwrap_or("");
            if !skip_stem_suffix.is_empty() && stem.ends_with(skip_stem_suffix) {
                continue;
            }
            // Compute archive entry name relative to archive_root.
            let rel = path.strip_prefix(archive_root).unwrap_or(&path);
            let entry_name = rel.to_string_lossy().replace('\\', "/");
            add_file_to_zip(zip, &path, &entry_name, options)?;
        }
    }

    Ok(())
}

/// Read `file_path` from disk and add it to `zip` as `entry_name`.
fn add_file_to_zip(
    zip: &mut ZipWriter<std::fs::File>,
    file_path: &Path,
    entry_name: &str,
    options: &FileOptions<'_, ()>,
) -> Result<(), String> {
    let data = std::fs::read(file_path)
        .map_err(|e| format!("cannot read '{}': {e}", file_path.display()))?;
    zip.start_file(entry_name, *options)
        .map_err(|e| format!("zip error for '{entry_name}': {e}"))?;
    zip.write_all(&data)
        .map_err(|e| format!("zip write error for '{entry_name}': {e}"))?;
    Ok(())
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use super::*;
    use std::fs;

    fn tmpdir() -> PathBuf {
        use std::sync::atomic::{AtomicU64, Ordering};
        static COUNTER: AtomicU64 = AtomicU64::new(0);
        let id = COUNTER.fetch_add(1, Ordering::SeqCst);
        let pid = std::process::id();
        std::env::temp_dir().join(format!("tlang_tbag_test_{pid}_{id}"))
    }

    fn write(path: &Path, content: &[u8]) {
        if let Some(p) = path.parent() {
            fs::create_dir_all(p).unwrap();
        }
        fs::write(path, content).unwrap();
    }

    #[test]
    fn pack_creates_tbag_with_expected_entries() {
        let root = tmpdir();
        fs::create_dir_all(&root).unwrap();

        write(&root.join("manifest.yml"), b"name: Test\n");
        write(&root.join("Main.tlang"), b"func main() {}");
        write(
            &root.join("sub").join("Util.tlang"),
            b"func util() {}",
        );
        // A bytecode file as if compiled.
        write(
            &root.join("target").join("tlang").join("Main.tlangc"),
            b"\x54\x4C\x43\x01",
        );

        let tbag_path = pack(&root, &root.join("manifest.yml"), "Test").unwrap();
        assert!(tbag_path.exists(), "tbag file should exist");
        assert_eq!(tbag_path.extension().and_then(|e| e.to_str()), Some("tbag"));

        // Open the archive and check entries.
        let file = fs::File::open(&tbag_path).unwrap();
        let mut archive = ZipArchive::new(file).unwrap();
        let names: Vec<String> = (0..archive.len())
            .map(|i| archive.by_index(i).unwrap().name().to_string())
            .collect();

        assert!(
            names.contains(&"manifest.yml".to_string()),
            "manifest.yml missing: {names:?}"
        );
        assert!(
            names.iter().any(|n| n.ends_with("Main.tlang")),
            "Main.tlang missing: {names:?}"
        );
        assert!(
            names.iter().any(|n| n.ends_with("Util.tlang")),
            "sub/Util.tlang missing: {names:?}"
        );
        assert!(
            names.iter().any(|n| n.ends_with("Main.tlangc")),
            "Main.tlangc missing: {names:?}"
        );
    }

    #[test]
    fn pack_excludes_target_sources() {
        let root = tmpdir();
        fs::create_dir_all(&root).unwrap();

        write(&root.join("manifest.yml"), b"name: Test\n");
        write(&root.join("Main.tlang"), b"func main() {}");
        // A .tlang file accidentally placed inside target/ — should be excluded.
        write(
            &root.join("target").join("gen").join("Out.tlang"),
            b"",
        );

        let tbag_path = pack(&root, &root.join("manifest.yml"), "Test").unwrap();
        let file = fs::File::open(&tbag_path).unwrap();
        let mut archive = ZipArchive::new(file).unwrap();
        let names: Vec<String> = (0..archive.len())
            .map(|i| archive.by_index(i).unwrap().name().to_string())
            .collect();

        assert!(
            !names.iter().any(|n| n.contains("Out.tlang")),
            "target/*.tlang should be excluded: {names:?}"
        );
        assert!(
            names.iter().any(|n| n.ends_with("Main.tlang")),
            "Main.tlang should be included: {names:?}"
        );
    }

    #[test]
    fn push_copies_tbag_to_dest() {
        let src_dir = tmpdir();
        let dst_dir = tmpdir();
        fs::create_dir_all(&src_dir).unwrap();

        let src = src_dir.join("Pkg.tbag");
        fs::write(&src, b"fake").unwrap();

        let dest = dst_dir.join("org").join("pkg").join("Pkg.tbag");
        push(&src, &dest).unwrap();

        assert!(dest.exists());
        assert_eq!(fs::read(&dest).unwrap(), b"fake");
    }

    #[test]
    fn extract_roundtrip() {
        let root = tmpdir();
        fs::create_dir_all(&root).unwrap();

        write(&root.join("manifest.yml"), b"name: Test\n");
        write(&root.join("Main.tlang"), b"func main() {}");
        write(
            &root.join("target").join("tlang").join("Main.tlangc"),
            b"\x54\x4C\x43\x01",
        );

        let tbag_path = pack(&root, &root.join("manifest.yml"), "Test").unwrap();

        let dest = tmpdir();
        extract(&tbag_path, &dest).unwrap();

        assert!(
            dest.join("manifest.yml").exists(),
            "manifest.yml not extracted"
        );
        assert!(dest.join("Main.tlang").exists(), "Main.tlang not extracted");
        assert!(dest.join(EXTRACTED_SENTINEL).exists(), "sentinel missing");
    }

    #[test]
    fn ensure_extracted_is_idempotent() {
        let root = tmpdir();
        fs::create_dir_all(&root).unwrap();

        write(&root.join("manifest.yml"), b"name: Test\n");
        write(&root.join("Main.tlang"), b"func main() {}");

        let tbag_path = pack(&root, &root.join("manifest.yml"), "Test").unwrap();

        let dest = tmpdir();
        // Place tbag into dest so ensure_extracted can find it.
        let tbag_in_dest = dest.join("Test.tbag");
        fs::create_dir_all(&dest).unwrap();
        fs::copy(&tbag_path, &tbag_in_dest).unwrap();

        // First call should extract.
        ensure_extracted(&dest).unwrap();
        assert!(is_extracted(&dest));

        // Second call should be a no-op (no error even though tbag is there).
        ensure_extracted(&dest).unwrap();
    }

    #[test]
    fn find_tbag_returns_none_for_empty_dir() {
        let dir = tmpdir();
        fs::create_dir_all(&dir).unwrap();
        assert_eq!(find_tbag(&dir), None);
    }

    #[test]
    fn find_tbag_finds_file() {
        let dir = tmpdir();
        fs::create_dir_all(&dir).unwrap();
        let path = dir.join("MyPkg.tbag");
        fs::write(&path, b"x").unwrap();
        assert_eq!(find_tbag(&dir), Some(path));
    }
}
