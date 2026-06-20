// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use crate::runtime::Value;

use super::helpers::run_conformity;

// -----------------------------------------------------------------------
// Template include attrs
// -----------------------------------------------------------------------

/// `conformity/tmpl_include_attrs/Main.tlang`
///
/// Demonstrates the `<[ call ]>` (`tmplInclude`) mechanism:
///
/// - A `lang [kotlin] dataClass(className, attrs)` template has an `impl`
///   block whose body contains `<[ renderAttrs(attrs) ]>`.
/// - At template instantiation time the runtime calls the helper function
///   `renderAttrs(attrs: List)`, which loops over the list and builds a
///   `var …` template fragment string for each attribute.
/// - The fragment is expanded inline into the template body before the
///   Kotlin code generator processes it.
/// - The final generated Kotlin class must contain every declared field.
#[test]
fn conformity_tmpl_include_attrs() {
    let run = run_conformity("tmpl_include_attrs");

    let generated = match &run.return_value {
        Value::String(s) => s.clone(),
        other => panic!("expected String return value, got {other:?}"),
    };

    // Class header
    assert!(
        generated.contains("class UserProfile {"),
        "expected class header:\n{generated}"
    );

    // All four attributes must be present as Kotlin var declarations.
    assert!(
        generated.contains("var id: Long"),
        "expected `var id: Long`:\n{generated}"
    );
    assert!(
        generated.contains("var name: String"),
        "expected `var name: String`:\n{generated}"
    );
    assert!(
        generated.contains("var email: String"),
        "expected `var email: String`:\n{generated}"
    );
    assert!(
        generated.contains("var active: Boolean"),
        "expected `var active: Boolean`:\n{generated}"
    );

    // Closing brace
    assert!(
        generated.ends_with('}'),
        "expected closing brace at end:\n{generated}"
    );

    // stdout should be the generated source followed by a newline.
    assert_eq!(
        run.output,
        format!("{generated}\n"),
        "stdout should be generated source followed by a newline"
    );
}

// -----------------------------------------------------------------------
// Set lead-template composition
// -----------------------------------------------------------------------

/// `conformity/set_lead_template/Main.tlang`
///
/// Demonstrates the `lead` attr + `<[ attrs() ]>` composition mechanism:
///
/// - A set body may declare a `lead: &someTemplate` attr.  The lead template
///   provides the *outer* structure (e.g. a Kotlin class wrapper) and uses
///   `<[ attrs() ]>` at the point where all the non-lead attrs' inner bodies
///   should be inlined.
/// - `generateAll()` detects the lead attr, pre-renders every non-lead attr
///   and extracts each one's inner body fragment, concatenates them, then
///   instantiates the lead template with `<[ attrs() ]>` expanding to the
///   combined fragment.
/// - The single combined output is written to the `>>` path as one file.
/// - Inheritance works: a child set that extends a parent with a `lead`
///   inherits it and any new non-lead attrs it introduces are also injected
///   inside `<[ attrs() ]>`.
///
/// Checks exercised:
/// - `main()` returns the content of the written BasicRepo file (read via
///   `File.read`), which must be a single Kotlin class containing both
///   `save` and `update` methods — no manual result-list scanning needed.
/// - The AuditedRepo written file (printed to stdout) must contain `save`,
///   `update`, and `audit` all inside a single class — proving that
///   inherited lead composition works across multiple parents.
#[test]
fn conformity_set_lead_template() {
    let run = run_conformity("set_lead_template");

    // main() returns File.read("…/DocumentRepo.kt") — the full generated file.
    let basic_code = match &run.return_value {
        Value::String(s) => s.clone(),
        other => panic!("expected String return value, got {other:?}"),
    };

    // BasicRepo: single class with both save and update inside it.
    assert!(
        basic_code.contains("class DocumentRepo"),
        "expected class DocumentRepo in BasicRepo output:\n{basic_code}"
    );
    assert!(
        basic_code.contains("fun save("),
        "expected save method in BasicRepo output:\n{basic_code}"
    );
    assert!(
        basic_code.contains("fun update("),
        "expected update method in BasicRepo output:\n{basic_code}"
    );

    // AuditedRepo: stdout holds File.read("…/AuditedDocumentRepo.kt").
    // It must be a single class containing all three methods.
    assert!(
        run.output.contains("class AuditedDocumentRepo"),
        "expected AuditedDocumentRepo class in stdout:\n{}",
        run.output
    );
    assert!(
        run.output.contains("fun save("),
        "expected save method in AuditedRepo output:\n{}",
        run.output
    );
    assert!(
        run.output.contains("fun update("),
        "expected update method in AuditedRepo output:\n{}",
        run.output
    );
    assert!(
        run.output.contains("fun audit("),
        "expected audit method in AuditedRepo output:\n{}",
        run.output
    );
}

// -----------------------------------------------------------------------
// Template comment generation
// -----------------------------------------------------------------------

/// `conformity/tmpl_comment/Main.tlang`
///
/// Verifies that the `comment` directive inside a `lang [kotlin]` template
/// body is correctly serialised as a `// text` line comment in the
/// generated Kotlin output.
///
/// Checks exercised:
/// - Plain `comment text` at the top level of an `impl` body.
/// - `comment ${param}` with template-parameter interpolation.
/// - `comment` before a `func` node (verifies ordering is preserved).
/// - The main helper asserts all five expected comment strings are present
///   and returns `"ok"` when they all pass.
#[test]
fn conformity_tmpl_comment() {
    let run = run_conformity("tmpl_comment");

    assert_eq!(
        run.return_value,
        Value::String("ok".to_string()),
        "tmpl_comment should return \"ok\"; got {:?}\noutput:\n{}",
        run.return_value,
        run.output,
    );

    let generated = match &run.return_value {
        _ => {
            // Extract the generated source from stdout (first block before
            // the assertion line).
            run.output
                .lines()
                .take_while(|l| !l.starts_with("all comment"))
                .collect::<Vec<_>>()
                .join("\n")
        }
    };

    // Every comment directive must appear as a Kotlin line comment.
    assert!(
        run.output.contains("// Primary identifier"),
        "expected '// Primary identifier' in output:\n{}",
        run.output
    );
    assert!(
        run.output.contains("// Display name of the entity"),
        "expected '// Display name of the entity' in output:\n{}",
        run.output
    );
    assert!(
        run.output.contains("// Whether the record is active"),
        "expected '// Whether the record is active' in output:\n{}",
        run.output
    );
    assert!(
        run.output.contains("// Returns the string representation"),
        "expected '// Returns the string representation' in output:\n{}",
        run.output
    );
    assert!(
        run.output.contains("// User — generated data class"),
        "expected '// User — generated data class' in output:\n{}",
        run.output
    );

    // Comments must appear before their associated declaration.
    let pid = run.output.find("// Primary identifier").unwrap();
    let var_id = run.output.find("var id:").unwrap();
    assert!(
        pid < var_id,
        "'// Primary identifier' should appear before 'var id:'"
    );

    let _ = generated;
}

// -----------------------------------------------------------------------
// Template language validation error test
// -----------------------------------------------------------------------

/// `conformity/tmpl_unknown_lang_error/Main.tlang`
///
/// Using `lang [kotlin]` without a `use <Package> as kotlin` alias must
/// produce a compile error naming the unknown language.
#[test]
fn conformity_tmpl_unknown_lang_error() {
    use crate::parser::parse_domain_model;
    use crate::runtime::compile_from_domain_model;

    let source = include_str!("../../conformity/tmpl_unknown_lang_error/Main.tlang");
    let model = parse_domain_model(source).expect("should parse");
    let err = compile_from_domain_model(&model).expect_err("should fail to compile");
    assert!(
        err.0.contains("unknown template language"),
        "expected 'unknown template language' in error, got: {}",
        err.0
    );
    assert!(
        err.0.contains("kotlin"),
        "expected 'kotlin' in error, got: {}",
        err.0
    );
}

// -----------------------------------------------------------------------
// else_if_program
// -----------------------------------------------------------------------

/// `conformity/else_if_program/Main.tlang`
///
/// Exercises `else if` chains in helper functions:
/// - Linear `else if` chain (letter grade from numeric score).
/// - Three-way `else if` (arithmetic sign).
/// - `else if` with compound `&&` conditions.
#[test]
fn conformity_else_if_program() {
    let run = run_conformity("else_if_program");

    let expected_output = concat!(
        "A\n",
        "B\n",
        "C\n",
        "D\n",
        "F\n",
        "positive\n",
        "negative\n",
        "zero\n",
        "single digit\n",
        "double digit\n",
        "negative\n",
        "large\n",
    );

    assert_eq!(
        run.output, expected_output,
        "else_if_program output mismatch"
    );
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "else_if_program should return \"done\""
    );
}

// -----------------------------------------------------------------------
// expression_if_match_program
// -----------------------------------------------------------------------

/// `conformity/expression_if_match_program/Main.tlang`
///
/// Verifies expression-oriented control flow:
/// - assignment from expression `if`
/// - expression `if` with `else if` chain
/// - assignment from expression `match`
/// - nested/inline expression composition (`if` + `match`)
/// - `return` using expression `if` and expression `match`
#[test]
fn conformity_expression_if_match_program() {
    let run = run_conformity("expression_if_match_program");

    let expected_output = concat!(
        "negative\n",
        "zero\n",
        "positive\n",
        "one\n",
        "other\n",
        "enabled-one\n",
        "enabled-many\n",
        "disabled\n",
    );

    assert_eq!(
        run.output, expected_output,
        "expression_if_match_program output mismatch"
    );
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "expression_if_match_program should return \"done\""
    );
}

// -----------------------------------------------------------------------
// tmpl_ref_multi_impl
// -----------------------------------------------------------------------

/// `conformity/tmpl_ref_multi_impl/Main.tlang`
///
/// Tests that a model can be impl'd multiple times, each instance pointing
/// via a body `&ref` attribute to a different template-generating function.
/// Also verifies that template calls return their instance directly so the
/// fragile `Leaf.get(model, "name_1")` pattern is never needed.
#[test]
fn conformity_tmpl_ref_multi_impl() {
    let run = run_conformity("tmpl_ref_multi_impl");

    // Each strategy description must appear in stdout.
    assert!(
        run.output.contains("Save scoped to a userId"),
        "expected user strategy description in output:\n{}",
        run.output
    );
    assert!(
        run.output.contains("Save scoped to a groupId"),
        "expected group strategy description in output:\n{}",
        run.output
    );
    assert!(
        run.output.contains("Save with no ownership (public)"),
        "expected public strategy description in output:\n{}",
        run.output
    );

    // Each generated class must appear in stdout.
    assert!(
        run.output.contains("class UserScopedDocumentSaver"),
        "expected UserScopedDocumentSaver class:\n{}",
        run.output
    );
    assert!(
        run.output.contains("class GroupScopedDocumentSaver"),
        "expected GroupScopedDocumentSaver class:\n{}",
        run.output
    );
    assert!(
        run.output.contains("class PublicDocumentSaver"),
        "expected PublicDocumentSaver class:\n{}",
        run.output
    );

    // Each variant generates a distinct save signature.
    assert!(
        run.output
            .contains("fun save(record: Document, userId: String)"),
        "expected user-scoped save signature:\n{}",
        run.output
    );
    assert!(
        run.output
            .contains("fun save(record: Document, groupId: String)"),
        "expected group-scoped save signature:\n{}",
        run.output
    );
    assert!(
        run.output.contains("fun save(record: Document): Document"),
        "expected public save signature:\n{}",
        run.output
    );

    // Return value is the three generated classes joined with `---`.
    let ret = match &run.return_value {
        Value::String(s) => s.clone(),
        other => panic!("expected String return, got {other:?}"),
    };
    assert!(
        ret.contains("UserScopedDocumentSaver"),
        "return value missing user class"
    );
    assert!(
        ret.contains("GroupScopedDocumentSaver"),
        "return value missing group class"
    );
    assert!(
        ret.contains("PublicDocumentSaver"),
        "return value missing public class"
    );
    assert!(
        ret.contains("\n---\n"),
        "return value should use --- as separator"
    );
}

// -----------------------------------------------------------------------
// tmpl_funcdef_multi_impl
// -----------------------------------------------------------------------

/// `conformity/tmpl_funcdef_multi_impl/Main.tlang`
///
/// Tests that a single `set` model with a FuncDef implementable parameter
/// acts as an interface: multiple instances each bind a different function
/// via `&ref` syntax, dispatching to a distinct template on `.generate(...)`.
/// Also exercises Map-based config-driven strategy selection.
#[test]
fn conformity_tmpl_funcdef_multi_impl() {
    let run = run_conformity("tmpl_funcdef_multi_impl");

    // All three instances share the same body label.
    assert_eq!(
        run.output.matches("save-strategy\n").count(),
        3,
        "expected the label `save-strategy` printed three times:\n{}",
        run.output
    );

    // Each generated class must be present.
    assert!(
        run.output.contains("class UserScopedDocumentSaver"),
        "expected UserScopedDocumentSaver:\n{}",
        run.output
    );
    assert!(
        run.output.contains("class GroupScopedDocumentSaver"),
        "expected GroupScopedDocumentSaver:\n{}",
        run.output
    );
    assert!(
        run.output.contains("class PublicDocumentSaver"),
        "expected PublicDocumentSaver:\n{}",
        run.output
    );

    // Config-driven dispatch produces the correct class and record type.
    assert!(
        run.output.contains("class GroupScopedInvoiceSaver"),
        "expected config-driven GroupScopedInvoiceSaver:\n{}",
        run.output
    );
    assert!(
        run.output.contains("record: Invoice"),
        "expected Invoice record type in config-driven output:\n{}",
        run.output
    );

    // Each variant generates a distinct save signature.
    assert!(
        run.output
            .contains("fun save(record: Document, userId: String)"),
        "expected user-scoped save signature:\n{}",
        run.output
    );
    assert!(
        run.output
            .contains("fun save(record: Document, groupId: String)"),
        "expected group-scoped save signature:\n{}",
        run.output
    );
    assert!(
        run.output.contains("fun save(record: Document): Document"),
        "expected public save signature:\n{}",
        run.output
    );

    // Return value contains all four sections joined with `---`.
    let ret = match &run.return_value {
        Value::String(s) => s.clone(),
        other => panic!("expected String return, got {other:?}"),
    };
    assert_eq!(
        ret.matches("\n---\n").count(),
        3,
        "expected three --- separators in return value"
    );
    assert!(
        ret.contains("UserScopedDocumentSaver"),
        "return value missing user class"
    );
    assert!(
        ret.contains("GroupScopedDocumentSaver"),
        "return value missing group class"
    );
    assert!(
        ret.contains("PublicDocumentSaver"),
        "return value missing public class"
    );
    assert!(
        ret.contains("GroupScopedInvoiceSaver"),
        "return value missing config-driven class"
    );
}

// -----------------------------------------------------------------------
// top_set_tmpl_ref
// -----------------------------------------------------------------------

/// `conformity/top_set_tmpl_ref/Main.tlang`
///
/// Tests the following language features together:
///
/// 1. **Template refs in body attrs** — `attr: &templateName` where
///    `templateName` is a `lang [...]` template (not a helper function).
///    Calling `inst.attr()` instantiates the template with the instance's
///    impl values and returns a `Leaf`.
///
/// 2. **`:` inheritance syntax** — `set Child : Parent` (braces optional
///    when nothing is surcharged).  Child sets inherit all parent body attrs
///    and constructor params, surcharing only what they redeclare.
///
/// 3. **`inst.attrs()`** — returns `List[BoundAttr]` in canonical
///    declaration order (parent first, child overrides in-place).
///    Each element is callable as `op()` and exposes `op.name: String`.
///
/// 4. **`inst.generateAll()`** — shortcut that iterates all body attrs,
///    generates each, and returns `List[Map{name, code}]`.
///
/// Hierarchy (`:`  = inherits from):
///   `set CrudRepository`                (save: &saveWithUser, update: &updateWithUser)
///     `set UserCrudRepository`          : CrudRepository  — no surcharges (no braces)
///     `set PublicCrudRepository`        : CrudRepository  — surcharges save + update
///     `set GroupCrudRepository`         : CrudRepository  — surcharges save + update
///       `set CachedGroupCrudRepository` : GroupCrudRepository — surcharges save only
#[test]
fn conformity_top_set_tmpl_ref() {
    let run = run_conformity("top_set_tmpl_ref");

    // ── Direct template-ref calls: UserCrudRepository ─────────────────────

    // save via saveWithUser template
    assert!(
        run.output
            .contains("fun save(record: Document, userId: String)"),
        "expected user save signature in output:\n{}",
        run.output
    );

    // update via updateWithUser template
    assert!(
        run.output
            .contains("fun update(record: Document, userId: String)"),
        "expected user update signature in output:\n{}",
        run.output
    );

    // ── Direct template-ref calls: PublicCrudRepository ───────────────────

    // save via savePublic template (no ownership arg)
    assert!(
        run.output.contains("fun save(record: Document): Document"),
        "expected public save signature (no userId/groupId) in output:\n{}",
        run.output
    );

    // ── CachedGroupCrudRepository: surcharged save, inherited update ──────

    // save must use saveWithGroupCached (has cache.invalidate)
    assert!(
        run.output.contains("cache.invalidate(groupId)"),
        "expected cache.invalidate in output (surcharged save):\n{}",
        run.output
    );

    // update must use updateWithGroup (inherited from GroupCrudRepository)
    assert!(
        run.output
            .contains("fun update(record: Document, groupId: String)"),
        "expected group update signature in output:\n{}",
        run.output
    );

    // ── Bound callable iteration via attrs() ──────────────────────────────
    // attrs() returns List[BoundAttr]; each op is callable and has op.name.

    assert!(
        run.output
            .contains("--- iterating attrs of CachedGroupCrudRepository ---"),
        "expected iteration section header:\n{}",
        run.output
    );

    let iter_section = run
        .output
        .split("--- iterating attrs of CachedGroupCrudRepository ---")
        .nth(1)
        .unwrap_or("");

    // op.name should print before the generated code for each attr.
    assert!(
        iter_section.contains("save\n"),
        "expected op.name 'save' printed in iteration section:\n{}",
        iter_section
    );
    assert!(
        iter_section.contains("update\n"),
        "expected op.name 'update' printed in iteration section:\n{}",
        iter_section
    );

    // op() should dispatch to the correct (surcharged / inherited) template.
    assert!(
        iter_section.contains("cache.invalidate"),
        "op() for 'save' should use the surcharged cached template:\n{}",
        iter_section
    );
    assert!(
        iter_section.contains("fun update(record: Document, groupId: String)"),
        "op() for 'update' should use the inherited group update template:\n{}",
        iter_section
    );

    // ── generateAll() shortcut ────────────────────────────────────────────
    // Returns List[Map{name, code}] — verifies PublicCrudRepository output.

    assert!(
        run.output
            .contains("--- generateAll on PublicCrudRepository ---"),
        "expected generateAll section header:\n{}",
        run.output
    );

    let gen_section = run
        .output
        .split("--- generateAll on PublicCrudRepository ---")
        .nth(1)
        .unwrap_or("");

    // generateAll entries print name then code for save and update.
    assert!(
        gen_section.contains("save\n"),
        "expected 'save' entry name in generateAll output:\n{}",
        gen_section
    );
    assert!(
        gen_section.contains("update\n"),
        "expected 'update' entry name in generateAll output:\n{}",
        gen_section
    );
    assert!(
        gen_section.contains("fun save(record: Document): Document"),
        "expected public save in generateAll output:\n{}",
        gen_section
    );
    assert!(
        gen_section.contains("fun update(record: Document): Document"),
        "expected public update in generateAll output:\n{}",
        gen_section
    );

    // ── attrs() canonical order: save before update ───────────────────────

    assert!(
        run.output.contains("--- user repo attr names ---"),
        "expected user attr names section:\n{}",
        run.output
    );

    let names_section = run
        .output
        .split("--- user repo attr names ---")
        .nth(1)
        .unwrap_or("");

    let save_pos = names_section.find("save\n");
    let update_pos = names_section.find("update\n");

    assert!(
        save_pos.is_some() && update_pos.is_some(),
        "both 'save' and 'update' should appear in attrs() names output:\n{}",
        names_section
    );
    assert!(
        save_pos.unwrap() < update_pos.unwrap(),
        "'save' must appear before 'update' (canonical parent-first order):\n{}",
        names_section
    );

    // ── Return value is the generated user-save Kotlin source ─────────────

    let ret = match &run.return_value {
        Value::String(s) => s.clone(),
        other => panic!("expected String return, got {other:?}"),
    };

    assert!(
        ret.contains("class UserDocumentRepo"),
        "return value should contain the UserDocumentRepo class:\n{ret}"
    );
    assert!(
        ret.contains("fun save(record: Document, userId: String)"),
        "return value should contain the user save signature:\n{ret}"
    );
}

// -----------------------------------------------------------------------
// set_multi_parent_write
// -----------------------------------------------------------------------

/// `conformity/set_multi_parent_write/Main.tlang`
///
/// Tests:
///
/// 1. **Multiple `:` parents** — `set Combo : A : B : C` inherits attrs
///    from all parents left-to-right; later parent wins on name collision;
///    child surcharges override everything.
///
/// 2. **`>> "path"`** — always-overwrite output.  `generateAll()` on an
///    instance writes the combined generated output to the interpolated path.
///    `${paramName}` placeholders are replaced with impl values.
///
/// 3. **`>>? "path"`** — write-once scaffold output.  File is skipped if
///    it already exists, preserving developer edits.
///
/// Hierarchy:
///   `set WithUser`   (save: &saveWithUser, update: &updateWithUser)
///   `set WithGroup`  (save: &saveWithGroup, update: &updateWithGroup)
///   `set WithAudit`  (audit: &auditLog)
///   `set FullRepo   : WithUser : WithAudit`              (3 attrs: save, update, audit)
///   `set CachedRepo : WithUser : WithGroup : WithAudit`  (save surcharged to cached)
///   `set ScaffoldRepo : WithUser`  (>>? write-once path)
#[test]
fn conformity_set_multi_parent_write() {
    use std::fs;
    use std::path::Path;

    // Clean up any leftover output from a previous run so >> overwrite and
    // >>? skip-if-exists logic are both exercised from a known state.
    let out_dir = Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("target")
        .join("tlang_test")
        .join("multi_parent");
    let _ = fs::remove_dir_all(&out_dir);

    let run = run_conformity("set_multi_parent_write");

    // ── FullRepo: canonical attr order [save, update, audit] ─────────────

    assert!(
        run.output.contains("--- FullRepo attrs ---"),
        "expected FullRepo attrs section:\n{}",
        run.output
    );

    let full_attrs_section = run
        .output
        .split("--- FullRepo attrs ---")
        .nth(1)
        .unwrap_or("");

    let save_pos = full_attrs_section.find("save\n");
    let update_pos = full_attrs_section.find("update\n");
    let audit_pos = full_attrs_section.find("audit\n");

    assert!(
        save_pos.is_some() && update_pos.is_some() && audit_pos.is_some(),
        "expected save, update, audit in FullRepo attrs:\n{full_attrs_section}"
    );
    assert!(
        save_pos.unwrap() < update_pos.unwrap(),
        "save must come before update:\n{full_attrs_section}"
    );
    assert!(
        update_pos.unwrap() < audit_pos.unwrap(),
        "update must come before audit:\n{full_attrs_section}"
    );

    // ── FullRepo: correct templates dispatched ────────────────────────────

    // save → saveWithUser (user-scoped)
    assert!(
        run.output
            .contains("fun save(record: Document, userId: String)"),
        "expected user-scoped save in FullRepo output:\n{}",
        run.output
    );

    // audit → auditLog
    assert!(
        run.output.contains("fun audit(record: Document)"),
        "expected auditLog method in FullRepo output:\n{}",
        run.output
    );

    // ── FullRepo generateAll() entries ────────────────────────────────────

    assert!(
        run.output.contains("--- FullRepo generateAll ---"),
        "expected FullRepo generateAll section:\n{}",
        run.output
    );

    let full_gen_section = run
        .output
        .split("--- FullRepo generateAll ---")
        .nth(1)
        .unwrap_or("");

    assert!(
        full_gen_section.contains("save\n"),
        "expected save entry in FullRepo generateAll:\n{full_gen_section}"
    );
    assert!(
        full_gen_section.contains("update\n"),
        "expected update entry in FullRepo generateAll:\n{full_gen_section}"
    );
    assert!(
        full_gen_section.contains("audit\n"),
        "expected audit entry in FullRepo generateAll:\n{full_gen_section}"
    );

    // ── >> file written by generateAll() ─────────────────────────────────

    let full_file = out_dir.join("FullDocumentRepo.kt");
    assert!(
        full_file.exists(),
        "expected >> file to be written at {}",
        full_file.display()
    );

    let full_content = fs::read_to_string(&full_file).expect("read FullDocumentRepo.kt");

    assert!(
        full_content.contains("class FullDocumentRepo"),
        "expected class FullDocumentRepo in written file:\n{full_content}"
    );
    assert!(
        full_content.contains("fun save"),
        "expected save method in written file:\n{full_content}"
    );
    assert!(
        full_content.contains("fun update"),
        "expected update method in written file:\n{full_content}"
    );
    assert!(
        full_content.contains("fun audit"),
        "expected audit method in written file:\n{full_content}"
    );

    // ── CachedRepo: three parents, child surcharges save ─────────────────

    assert!(
        run.output.contains("--- CachedRepo attrs ---"),
        "expected CachedRepo attrs section:\n{}",
        run.output
    );

    // save → saveWithGroupCached (child wins over both WithUser and WithGroup)
    assert!(
        run.output.contains("cache.invalidate(groupId)"),
        "expected cache.invalidate in CachedRepo save:\n{}",
        run.output
    );

    // update → updateWithGroup (WithGroup is declared after WithUser — last parent wins)
    assert!(
        run.output
            .contains("fun update(record: Document, groupId: String)"),
        "expected group-scoped update in CachedRepo output (last parent wins):\n{}",
        run.output
    );

    // >> file for CachedRepo
    let cached_file = out_dir.join("CachedDocumentRepo.kt");
    assert!(
        cached_file.exists(),
        "expected >> file for CachedDocumentRepo at {}",
        cached_file.display()
    );

    let cached_content = fs::read_to_string(&cached_file).expect("read CachedDocumentRepo.kt");

    assert!(
        cached_content.contains("cache.invalidate"),
        "CachedDocumentRepo.kt should contain cache.invalidate:\n{cached_content}"
    );

    // ── >>? write-once: file written on first run ─────────────────────────

    let scaffold_file = out_dir.join("scaffold").join("ScaffoldDocumentRepo.kt");
    assert!(
        scaffold_file.exists(),
        "expected >>? scaffold file at {}",
        scaffold_file.display()
    );

    let scaffold_content =
        fs::read_to_string(&scaffold_file).expect("read ScaffoldDocumentRepo.kt");

    assert!(
        scaffold_content.contains("ScaffoldDocumentRepo"),
        "scaffold file should contain class name:\n{scaffold_content}"
    );

    // ── >>? write-once: pre-existing file is NOT overwritten ─────────────

    // Write a sentinel into the scaffold file, run again, verify it survives.
    let sentinel = "// SENTINEL_DO_NOT_OVERWRITE\n";
    let sentinel_content = format!("{sentinel}{scaffold_content}");
    fs::write(&scaffold_file, &sentinel_content).expect("write sentinel");

    let run2 = run_conformity("set_multi_parent_write");
    let _ = run2; // output checked below

    let after_content =
        fs::read_to_string(&scaffold_file).expect("re-read scaffold file after second run");

    assert!(
        after_content.contains("SENTINEL_DO_NOT_OVERWRITE"),
        ">>? must not overwrite an existing file — sentinel was erased:\n{after_content}"
    );

    // ── Return value is the FullRepo user-save generated code ─────────────

    let ret = match &run.return_value {
        Value::String(s) => s.clone(),
        other => panic!("expected String return, got {other:?}"),
    };

    assert!(
        ret.contains("class FullDocumentRepo"),
        "return value should be FullDocumentRepo save class:\n{ret}"
    );
    assert!(
        ret.contains("fun save(record: Document, userId: String)"),
        "return value should contain user save signature:\n{ret}"
    );
}
