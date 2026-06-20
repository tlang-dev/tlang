// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Bytecode (`.tlangc`) file support for TLang.
//!
//! A `.tlangc` file is a binary-encoded [`CompiledProgram`] produced during
//! compilation.  It can be loaded at runtime and executed directly without
//! re-parsing or re-compiling the source.
//!
//! ## Binary format
//!
//! ```text
//! Magic:  [0x54, 0x4C, 0x43, 0x05]   ("TLC" + version 5)
//! --- Constant Pool ---
//! pool_count: u32
//! pool entries[pool_count]: len: u16, utf8_bytes[len]
//! --- Imports ---
//! imports_count: u16
//! imports[imports_count]: alias_ref: u16, path_ref: u16
//! --- Functions ---
//! functions_count: u16
//! functions[functions_count]: <Function encoding>
//! --- Templates ---
//! templates_count: u16
//! templates[templates_count]: <TemplateFunction encoding>
//! --- Model ---
//! <ModelBlockTree encoding>
//! ```
//!
//! All multi-byte integers are big-endian.
//!
//! ## Layout on disk
//!
//! ```text
//! <project_root>/target/tlang/<relative_source_path>.tlangc
//! ```
//!
//! For example:
//! - `Main.tlang`          → `target/tlang/Main.tlangc`
//! - `Dir1/File1.tlang`    → `target/tlang/Dir1/File1.tlangc`

use std::path::{Path, PathBuf};

use crate::runtime::CompiledProgram;

/// Sub-directory under the project root where compiled bytecode files live.
pub const BYTECODE_TARGET_DIR: &str = "target/tlang";

/// Extension used for compiled bytecode files.
pub const BYTECODE_EXT: &str = "tlangc";

// ---------------------------------------------------------------------------
// Path helpers
// ---------------------------------------------------------------------------

/// Compute the `.tlangc` output path for a source file.
///
/// Returns `None` when `source_path` cannot be made relative to
/// `project_root` (e.g. the source lives outside the project tree, such as
/// a tbox registry dependency).
pub fn bytecode_path(source_path: &Path, project_root: &Path) -> Option<PathBuf> {
    let rel = source_path.strip_prefix(project_root).ok()?;
    let mut target = project_root.join(BYTECODE_TARGET_DIR).join(rel);
    target.set_extension(BYTECODE_EXT);
    Some(target)
}

// ---------------------------------------------------------------------------
// I/O
// ---------------------------------------------------------------------------

/// Serialize `compiled` to a binary `.tlangc` file at `bc_path`.
///
/// Parent directories are created automatically.
pub fn write_bytecode(compiled: &CompiledProgram, bc_path: &Path) -> Result<(), String> {
    if let Some(parent) = bc_path.parent() {
        std::fs::create_dir_all(parent)
            .map_err(|e| format!("cannot create '{}': {e}", parent.display()))?;
    }
    let bytes = compiled.encode();
    std::fs::write(bc_path, &bytes)
        .map_err(|e| format!("cannot write '{}': {e}", bc_path.display()))?;
    Ok(())
}

/// Deserialize a [`CompiledProgram`] from a binary `.tlangc` file at `bc_path`.
pub fn read_bytecode(bc_path: &Path) -> Result<CompiledProgram, String> {
    let bytes =
        std::fs::read(bc_path).map_err(|e| format!("cannot read '{}': {e}", bc_path.display()))?;
    CompiledProgram::decode(&bytes)
        .map_err(|e| format!("cannot decode '{}': {e}", bc_path.display()))
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use super::*;
    use crate::runtime::{compile_helper_block, run_main};
    use std::fs;

    fn tmpdir() -> PathBuf {
        use std::sync::atomic::{AtomicU64, Ordering};
        static COUNTER: AtomicU64 = AtomicU64::new(0);
        let id = COUNTER.fetch_add(1, Ordering::SeqCst);
        let pid = std::process::id();
        let p = std::env::temp_dir().join(format!("tlang_bytecode_test_{pid}_{id}"));
        fs::create_dir_all(&p).unwrap();
        p
    }

    /// Compile a small helper program with a `main()` function that prints a
    /// known string, returning the resulting [`CompiledProgram`].
    fn sample_program() -> CompiledProgram {
        compile_helper_block(
            r#"{
                func main(): String {
                    TLang.Terminal.println("hello bytecode");
                }
            }"#,
        )
        .expect("compile sample program")
    }

    #[test]
    fn roundtrip_encode_decode() {
        let original = sample_program();
        let bytes = original.encode();
        let restored = CompiledProgram::decode(&bytes).expect("decode");

        // Verify the restored program actually runs and produces the same output.
        let r1 = run_main(&original).expect("run original");
        let r2 = run_main(&restored).expect("run restored");
        assert_eq!(r1.output, r2.output);
        assert_eq!(r1.output, "hello bytecode\n");
    }

    #[test]
    fn roundtrip_write_read_file() {
        let root = tmpdir();
        let bc = root.join("target").join("tlang").join("Main.tlangc");
        let compiled = sample_program();

        write_bytecode(&compiled, &bc).expect("write");
        let loaded = read_bytecode(&bc).expect("read");

        let r1 = run_main(&compiled).expect("run original");
        let r2 = run_main(&loaded).expect("run loaded");
        assert_eq!(r1.output, r2.output);
    }

    #[test]
    fn magic_bytes_are_present() {
        let compiled = sample_program();
        let bytes = compiled.encode();
        assert_eq!(
            &bytes[..4],
            &[0x54, 0x4C, 0x43, 0x05],
            "magic must be TLC\\x05"
        );
    }

    /// A model with a `Ref` attribute that uses all `RefArg` variants should
    /// survive an encode→decode roundtrip and produce the same runtime output.
    #[test]
    fn roundtrip_ref_arg_all_variants() {
        use crate::parser::parse_domain_model;
        use crate::runtime::{CompiledProgram, compile_from_domain_model, run_main};

        let src = r#"
            use TLang.Terminal
            set Demo(prefix: String) {
                noArgs:   &noArgsFn,
                withThis: &withThisFn(this),
                withHole: &withHoleFn(this, _),
                withLits: &withLitsFn("hi", 7, false),
                withRef:  &withRefFn(&helperCb)
            }
            func noArgsFn(prefix: String): String { return prefix }
            func withThisFn(d: Demo): String { return d.prefix }
            func withHoleFn(d: Demo, extra: String): String { return d.prefix + extra }
            func withLitsFn(s: String, n: Int, b: Bool): String { return s }
            func withRefFn(cb: String): String { return cb }
            func helperCb(x: String): String { return x }
            func main(): String {
                let d = Demo(prefix: "ok")
                Terminal.println(d.withThis())
                Terminal.println(d.withHole("-suffix"))
                Terminal.println(d.withLits())
                Terminal.println(d.withRef())
                return "done"
            }
        "#;

        let model = parse_domain_model(src).expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");

        // Encode → decode → run: output must match the original.
        let bytes = compiled.encode();
        let restored = CompiledProgram::decode(&bytes).expect("should decode");

        let r1 = run_main(&compiled).expect("original should run");
        let r2 = run_main(&restored).expect("restored should run");
        assert_eq!(r1.output, r2.output);
        assert_eq!(r1.output, "ok\nok-suffix\nhi\nhelperCb\n");
    }

    #[test]
    fn rejects_old_json_format() {
        let json = b"{}";
        let err = CompiledProgram::decode(json).expect_err("should reject JSON");
        assert!(
            err.contains("JSON") || err.contains("json") || err.contains("recompile"),
            "error should mention old format: {err}"
        );
    }

    #[test]
    fn rejects_invalid_magic() {
        let bad = b"\x00\x00\x00\x00";
        let err = CompiledProgram::decode(bad).expect_err("should reject bad magic");
        assert!(
            err.contains("magic") || err.contains("invalid"),
            "error should mention magic: {err}"
        );
    }

    #[test]
    fn bytecode_path_relative() {
        let root = PathBuf::from("/proj");
        let source = PathBuf::from("/proj/Dir1/File1.tlang");
        let expected = PathBuf::from("/proj/target/tlang/Dir1/File1.tlangc");
        assert_eq!(bytecode_path(&source, &root), Some(expected));
    }

    #[test]
    fn bytecode_path_root_file() {
        let root = PathBuf::from("/proj");
        let source = PathBuf::from("/proj/Main.tlang");
        let expected = PathBuf::from("/proj/target/tlang/Main.tlangc");
        assert_eq!(bytecode_path(&source, &root), Some(expected));
    }

    #[test]
    fn bytecode_path_outside_project() {
        let root = PathBuf::from("/proj");
        let source = PathBuf::from("/other/Lib.tlang");
        assert_eq!(bytecode_path(&source, &root), None);
    }

    /// Verify that data templates defined in a single-file program survive a
    /// full bytecode encode → write → read → decode roundtrip.
    ///
    /// This is the exact failure mode seen with `examples/dashboard-htmx`:
    /// the first `tlang both` run succeeds (uses the in-memory CompiledProgram),
    /// but the second run reads the .tlangc from disk and fails with
    /// "function `sidebarNavItem` not found" because data templates were
    /// silently dropped during bytecode serialisation.
    #[test]
    fn data_templates_survive_bytecode_roundtrip() {
        use crate::loader::load_program;
        use crate::runtime::{compile_from_domain_model, run_main};

        // Write a temp .tlang file with a data template and a helper that calls it.
        let dir = tmpdir();
        let source = r#"
use TLang.Leaf

data [html] greeting(name: String) {
    div(class: "hello") {
        "${name}"
    }
}

func main(): String {
    let inst = greeting("World");
    let kind = TLang.Leaf.get(inst, "kind");
    return kind;
}
"#;
        let path = dir.join("Main.tlang");
        fs::write(&path, source).unwrap();

        let model = load_program(&path).expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");

        // Verify the data template survives encode → decode.
        let bytes = compiled.encode();
        let restored = CompiledProgram::decode(&bytes).expect("decode should succeed");

        // Run the restored program — greeting("World") returns a Leaf with kind=="data".
        let result = run_main(&restored).expect("restored program should run");
        assert_eq!(
            result.return_value,
            crate::runtime::Value::String("data".to_string()),
            "data template kind field should be 'data' after bytecode roundtrip"
        );
    }

    /// Verify that data templates from multi-file programs (helper imported
    /// via `use helpers.Foo`) are present after bytecode roundtrip.
    ///
    /// Simulates the dashboard scenario: Main.tlang uses `use helpers.DashTemplates`
    /// which defines `data [html]` templates; after compiling to bytecode and
    /// reading back, those templates must still be callable.
    #[test]
    fn data_templates_from_imported_helper_survive_bytecode_roundtrip() {
        use crate::loader::{compile_to_bytecode_files, load_program_prefer_bytecode};
        use crate::runtime::run_main;
        use std::fs;

        fn tmpdir2() -> PathBuf {
            use std::sync::atomic::{AtomicU64, Ordering};
            static COUNTER: AtomicU64 = AtomicU64::new(0);
            let id = COUNTER.fetch_add(1, Ordering::SeqCst);
            let pid = std::process::id();
            let p = std::env::temp_dir().join(format!("tlang_bc_data_tmpl_test_{pid}_{id}"));
            fs::create_dir_all(&p).unwrap();
            p
        }

        let project_root = tmpdir2();
        let helpers_dir = project_root.join("helpers");
        fs::create_dir_all(&helpers_dir).unwrap();

        // helpers/DashTemplates.tlang — defines a data [html] template.
        let dash_templates = r#"
data [html] navItem(label: String, href: String) {
    li(class: "nav-item") {
        a(href: "${href}") {
            "${label}"
        }
    }
}

func main(): String {
    return "DashTemplates loaded.";
}
"#;
        fs::write(helpers_dir.join("DashTemplates.tlang"), dash_templates).unwrap();

        // Main.tlang — imports the helper, calls navItem(), reads its `kind` field.
        let main_source = r#"
use TLang.Leaf
use helpers.DashTemplates

func main(): String {
    let inst = navItem("Home", "/");
    let kind = TLang.Leaf.get(inst, "kind");
    return kind;
}
"#;
        fs::write(project_root.join("Main.tlang"), main_source).unwrap();

        let project_root_canon = project_root.canonicalize().unwrap();
        let main_path = project_root.join("Main.tlang");

        // Step 1: compile to bytecode.
        let (compiled_in_memory, _written) =
            compile_to_bytecode_files(&main_path, &project_root_canon, None)
                .expect("compile should succeed");

        // The in-memory program should run (navItem is a data template → kind == "data").
        let r1 = run_main(&compiled_in_memory).expect("in-memory run should succeed");
        assert_eq!(
            r1.return_value,
            crate::runtime::Value::String("data".to_string()),
            "in-memory run: navItem kind should be 'data'"
        );

        // Step 2: read the bytecode back from disk and run again.
        // This is what the second `tlang both` invocation does.
        let compiled_from_disk =
            load_program_prefer_bytecode(&main_path, &project_root_canon, None)
                .expect("load from bytecode should succeed");

        let r2 = run_main(&compiled_from_disk).expect("bytecode run should succeed");
        assert_eq!(
            r2.return_value,
            crate::runtime::Value::String("data".to_string()),
            "bytecode run: navItem must survive encode/decode — \
             data templates from imported helpers are being lost during bytecode serialisation"
        );
    }
}
