// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::path::Path;

use crate::{
    load_program_with_manifest,
    runtime::{Value, compile_from_domain_model, run_main, run_main_with_args},
    try_load_manifest,
};

use super::helpers::{conformity_path, run_conformity};

// -----------------------------------------------------------------------
// cross_file_import
// -----------------------------------------------------------------------

/// `conformity/cross_file_import/Main.tlang`
///
/// Verifies that a file can import another file in the same directory with
/// `use Utils` (no `.tlang` suffix) and call functions that are marked
/// `expose` in that file.
#[test]
fn conformity_cross_file_import() {
    let run = run_conformity("cross_file_import");

    let expected_output = concat!("7\n", "Hello, TLang!\n",);

    assert_eq!(
        run.output, expected_output,
        "cross_file_import output mismatch"
    );
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "cross_file_import should return \"done\""
    );
}

/// Calling a function from a dependency that exists but is NOT exposed
/// must produce a compile error with a helpful message pointing the user
/// to the missing `expose` declaration.
#[test]
fn conformity_cross_file_import_unexposed_error() {
    use std::fs;

    // Build a temporary program that tries to call `secret()` which is
    // defined in Utils.tlang but not listed in its `expose` directives.
    let root = Path::new(env!("CARGO_MANIFEST_DIR"));
    let utils_path = root
        .join("conformity")
        .join("cross_file_import")
        .join("Utils.tlang");

    // Write a temporary Main.tlang that calls the private `secret()`.
    let tmp_dir = root.join("conformity").join("cross_file_import_err_tmp");
    fs::create_dir_all(&tmp_dir).expect("create tmp dir");

    // Copy Utils.tlang into the temp dir so the relative `use Utils` resolves.
    fs::copy(&utils_path, tmp_dir.join("Utils.tlang")).expect("copy Utils");

    let bad_main = concat!(
        "use TLang.Terminal\n",
        "use Utils\n",
        "\n",
        "func main(): String {\n",
        "    let s = secret()\n",
        "    Terminal.println(s)\n",
        "    return \"done\"\n",
        "}\n",
    );
    let main_path = tmp_dir.join("Main.tlang");
    fs::write(&main_path, bad_main).expect("write bad Main");

    let model = load_program_with_manifest(&main_path, None)
        .expect("load should succeed — error is a compile error, not a load error");

    let result = compile_from_domain_model(&model);

    // Clean up temp dir.
    fs::remove_dir_all(&tmp_dir).ok();

    let err = result.expect_err("calling an unexposed function must fail at compile time");
    let msg = err.0;
    assert!(
        msg.contains("secret") && msg.contains("Utils"),
        "error should mention the function name and the source file; got: {msg}"
    );
    assert!(
        msg.contains("expose") || msg.contains("not exposed"),
        "error should hint at adding an `expose` declaration; got: {msg}"
    );
}

// -----------------------------------------------------------------------
// subfolder_import
// -----------------------------------------------------------------------

/// `conformity/subfolder_import/Main.tlang`
///
/// Verifies that a file can import a file one directory level deep with
/// `use helpers.MathUtils` and call functions that are marked `expose`
/// in that file.
#[test]
fn conformity_subfolder_import() {
    let run = run_conformity("subfolder_import");

    let expected_output = concat!(
        "10\n", // double(5)
        "16\n", // square(4)
    );

    assert_eq!(
        run.output, expected_output,
        "subfolder_import output mismatch"
    );
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "subfolder_import should return \"done\""
    );
}

/// Calling a function from a subfolder dependency that exists but is NOT
/// exposed must produce a compile error with a helpful message.
#[test]
fn conformity_subfolder_import_unexposed_error() {
    use std::fs;

    let root = Path::new(env!("CARGO_MANIFEST_DIR"));
    let utils_path = root
        .join("conformity")
        .join("subfolder_import")
        .join("helpers")
        .join("MathUtils.tlang");

    let tmp_dir = root.join("conformity").join("subfolder_import_err_tmp");
    let tmp_helpers = tmp_dir.join("helpers");
    fs::create_dir_all(&tmp_helpers).expect("create tmp helpers dir");

    fs::copy(&utils_path, tmp_helpers.join("MathUtils.tlang")).expect("copy MathUtils");

    let bad_main = concat!(
        "use TLang.Terminal\n",
        "use helpers.MathUtils\n",
        "\n",
        "func main(): String {\n",
        "    let r = internalMultiply(3, 4)\n",
        "    Terminal.println(r)\n",
        "    return \"done\"\n",
        "}\n",
    );
    let main_path = tmp_dir.join("Main.tlang");
    fs::write(&main_path, bad_main).expect("write bad Main");

    let model = load_program_with_manifest(&main_path, None)
        .expect("load should succeed — error is a compile error, not a load error");

    let result = compile_from_domain_model(&model);

    fs::remove_dir_all(&tmp_dir).ok();

    let err = result.expect_err("calling an unexposed function must fail at compile time");
    let msg = err.0;
    assert!(
        msg.contains("internalMultiply") && msg.contains("MathUtils"),
        "error should mention the function name and the source file; got: {msg}"
    );
    assert!(
        msg.contains("expose") || msg.contains("not exposed"),
        "error should hint at adding an `expose` declaration; got: {msg}"
    );
}

/// Attempting to import a path that is more than one directory level deep
/// (three or more segments) must produce a clear load error.
#[test]
fn conformity_subfolder_import_too_deep_error() {
    use std::fs;

    let root = Path::new(env!("CARGO_MANIFEST_DIR"));
    let tmp_dir = root.join("conformity").join("subfolder_import_deep_tmp");
    let nested = tmp_dir.join("a").join("b");
    fs::create_dir_all(&nested).expect("create nested dirs");

    fs::write(
        nested.join("Deep.tlang"),
        "expose foo\nhelper {\n    func foo(): Int { return 1 }\n}\n",
    )
    .expect("write deep file");

    let bad_main = concat!(
        "use TLang.Terminal\n",
        "use a.b.Deep\n",
        "\n",
        "func main(): String {\n",
        "    Terminal.println(foo())\n",
        "    return \"done\"\n",
        "}\n",
    );
    let main_path = tmp_dir.join("Main.tlang");
    fs::write(&main_path, bad_main).expect("write deep-import Main");

    let result = load_program_with_manifest(&main_path, None);

    fs::remove_dir_all(&tmp_dir).ok();

    let err = result.expect_err("a too-deep import must fail at load time");
    let msg = err.to_string();
    assert!(
        msg.contains("a.b.Deep") || msg.contains("too deep"),
        "error should mention the offending path or explain the depth limit; got: {msg}"
    );
    assert!(
        msg.contains("FolderName.FileName") || msg.contains("one directory"),
        "error should show the correct import form; got: {msg}"
    );
}

// -----------------------------------------------------------------------
// main_signatures
// -----------------------------------------------------------------------

/// `conformity/main_signatures/Main.tlang`
///
/// Verifies `func main(args: String[]): String` — the entry point receives
/// a list of strings and returns a primitive value.  When called with no
/// args the helper returns "no args".
#[test]
fn conformity_main_signatures_no_args() {
    let run = run_conformity("main_signatures");

    assert_eq!(run.output, "no args\n", "main_signatures output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("no args".to_string()),
        "main_signatures should return \"no args\""
    );
}

/// When `main` is called with arguments, the helper returns "received args".
#[test]
fn conformity_main_signatures_with_args() {
    let path = conformity_path("main_signatures");
    let program_dir = path.parent().expect("has parent");
    let manifest = try_load_manifest(program_dir).unwrap_or_else(|e| panic!("manifest: {e}"));
    let model = load_program_with_manifest(&path, manifest.as_ref())
        .unwrap_or_else(|e| panic!("load: {e}"));
    let compiled = compile_from_domain_model(&model).unwrap_or_else(|e| panic!("compile: {e}"));

    let run = run_main_with_args(&compiled, vec!["hello".to_string(), "world".to_string()])
        .unwrap_or_else(|e| panic!("run: {e}"));

    assert_eq!(
        run.output, "received args\n",
        "main_signatures with-args output mismatch"
    );
    assert_eq!(
        run.return_value,
        Value::String("received args".to_string()),
        "main_signatures should return \"received args\" when args supplied"
    );
}

/// A program with no `main` function must fail at runtime with a clear
/// message telling the user what to add.
#[test]
fn conformity_main_signatures_no_main_error() {
    let src = concat!(
        "func helper_only(): Int {\n",
        "    return 42\n",
        "}\n",
    );
    let model = crate::parser::parse_domain_model(src).expect("parses");
    let compiled = compile_from_domain_model(&model).expect("compiles without main");
    let err = run_main(&compiled).expect_err("missing main must fail at runtime");
    let msg = err.0;
    assert!(
        msg.contains("main"),
        "error should mention `main`; got: {msg}"
    );
    assert!(
        msg.contains("func main"),
        "error should show the correct form; got: {msg}"
    );
}

/// A `main` with a wrong parameter type must fail at compile time.
#[test]
fn conformity_main_signatures_wrong_param_type_error() {
    let src = concat!(
        "func main(args: Int) {\n",
        "    return 0\n",
        "}\n",
    );
    let model = crate::parser::parse_domain_model(src).expect("parses");
    let err = compile_from_domain_model(&model)
        .expect_err("wrong param type must fail at compile time");
    let msg = err.0;
    assert!(
        msg.contains("String[]") || msg.contains("String["),
        "error should tell the user to use String[]; got: {msg}"
    );
}

/// A `main` with more than one parameter must fail at compile time.
#[test]
fn conformity_main_signatures_too_many_params_error() {
    let src = concat!(
        "func main(a: String[], b: String[]) {\n",
        "    return 0\n",
        "}\n",
    );
    let model = crate::parser::parse_domain_model(src).expect("parses");
    let err = compile_from_domain_model(&model)
        .expect_err("too many params must fail at compile time");
    let msg = err.0;
    assert!(
        msg.contains("main") && (msg.contains("parameter") || msg.contains("1")),
        "error should mention `main` and the parameter limit; got: {msg}"
    );
}

/// A `main` that declares a non-primitive return type must fail at compile time.
#[test]
fn conformity_main_signatures_wrong_return_type_error() {
    let src = concat!(
        "func main(): List {\n",
        "    return 42\n",
        "}\n",
    );
    let model = crate::parser::parse_domain_model(src).expect("parses");
    let err = compile_from_domain_model(&model)
        .expect_err("non-primitive return type must fail at compile time");
    let msg = err.0;
    assert!(
        msg.contains("main") && msg.contains("return"),
        "error should mention `main` and the return type; got: {msg}"
    );
    assert!(
        msg.contains("primitive") || msg.contains("String") || msg.contains("Int"),
        "error should list the allowed return types; got: {msg}"
    );
}

// -----------------------------------------------------------------------
// set_ext_override
// -----------------------------------------------------------------------

/// `conformity/set_ext_override/Main.tlang`
///
/// Tests that a child `set` entity declared with `ext` inherits all body
/// attrs and constructor params from its parent, and that any attr
/// (including a `&ref` function attribute) redeclared in the child
/// surcharges (replaces) the inherited version.
///
/// Scenarios:
/// - Base entity uses its own label and its own `&ref` for `generate`.
/// - `JsonSerializer ext Serializer` inherits `label: "serializer"` and
///   surcharges only `generate` → a different template-generating function.
/// - `XmlSerializer ext Serializer` surcharges both `label` and `generate`.
/// - Inherited constructor params (`pkg`, `className`) are prepended to the
///   ref call automatically, even though the child declares no params itself.
#[test]
fn conformity_set_ext_override() {
    let run = run_conformity("set_ext_override");

    // ── Label inheritance and surcharging ─────────────────────────────────

    // Base: own label
    assert!(
        run.output.contains("serializer\n"),
        "expected base label `serializer` in output:\n{}",
        run.output
    );

    // JsonSerializer inherits label from Serializer — should NOT print
    // anything other than "serializer" for its label.
    let label_count = run.output.matches("serializer\n").count();
    assert!(
        label_count >= 2,
        "expected `serializer` label at least twice (base + json inherit):\n{}",
        run.output
    );

    // XmlSerializer surcharges label.
    assert!(
        run.output.contains("xml-serializer\n"),
        "expected surcharging label `xml-serializer` for XmlSerializer:\n{}",
        run.output
    );

    // ── Template dispatch ─────────────────────────────────────────────────

    // Base → serializerBase template (has `serialize` method)
    assert!(
        run.output.contains("fun serialize(value: String)"),
        "expected base serialize method in output:\n{}",
        run.output
    );

    // JsonSerializer → serializerJson template (has `toJson` method)
    assert!(
        run.output.contains("fun toJson(value: String)"),
        "expected json toJson method in output:\n{}",
        run.output
    );

    // XmlSerializer → serializerXml template (has `toXml` method)
    assert!(
        run.output.contains("fun toXml(value: String)"),
        "expected xml toXml method in output:\n{}",
        run.output
    );

    // ── Class names confirm correct param inheritance ──────────────────────

    assert!(
        run.output.contains("class DefaultSerializer"),
        "expected DefaultSerializer class:\n{}",
        run.output
    );
    assert!(
        run.output.contains("class JsonSerializer"),
        "expected JsonSerializer class:\n{}",
        run.output
    );
    assert!(
        run.output.contains("class XmlSerializer"),
        "expected XmlSerializer class:\n{}",
        run.output
    );

    // ── Return value contains all three classes joined with --- ───────────

    let ret = match &run.return_value {
        Value::String(s) => s.clone(),
        other => panic!("expected String return, got {other:?}"),
    };
    assert_eq!(
        ret.matches("\n---\n").count(),
        2,
        "expected two --- separators in return value"
    );
    assert!(
        ret.contains("DefaultSerializer"),
        "return value missing DefaultSerializer"
    );
    assert!(
        ret.contains("JsonSerializer"),
        "return value missing JsonSerializer"
    );
    assert!(
        ret.contains("XmlSerializer"),
        "return value missing XmlSerializer"
    );
    // Each class carries its distinct method — confirming the right template
    // was dispatched for each strategy.
    assert!(ret.contains("fun serialize"), "return missing serialize");
    assert!(ret.contains("fun toJson"), "return missing toJson");
    assert!(ret.contains("fun toXml"), "return missing toXml");
}
