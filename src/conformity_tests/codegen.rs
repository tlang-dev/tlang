// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use crate::runtime::Value;

use super::helpers::{run_conformity, run_kotlin_extractor};

// -----------------------------------------------------------------------
// Kotlin code generation
// -----------------------------------------------------------------------

/// `conformity/kotlin_codegen/Main.tlang`
///
/// Verifies end-to-end Kotlin code generation via a `file://` manifest
/// dependency on `generators/kotlin/KotlinGenerator.tlang`.
///
/// The generated class (`UserService`) showcases the most common Kotlin
/// features:
///
/// - Package declaration
/// - Class extending a base class with an annotation (`@Component`)
/// - Mutable (`var`) and immutable (`val`) variable declarations
/// - A function with a typed parameter and a return type
/// - A function with `if / else if / else` branching
/// - A function with a `for` loop and an accumulator variable
/// - Cross-function calls within the same class body
///
/// The test also exercises the helper-block **last-wins override**
/// behaviour: `Main.tlang` re-declares `main()` to target its own model
/// entity, overriding the `main()` supplied by `KotlinGenerator`.
#[test]
fn conformity_kotlin_codegen() {
    let run = run_conformity("kotlin_codegen");

    let generated = match &run.return_value {
        Value::String(s) => s.clone(),
        other => panic!("expected String return value, got {other:?}"),
    };

    // ── structural checks ─────────────────────────────────────────────

    // Package declaration at the very top.
    assert!(
        generated.starts_with("package com.example.demo"),
        "expected package declaration at top:\n{generated}"
    );

    // Class header with annotation, extends, and opening brace.
    assert!(
        generated.contains("@Component\nclass UserService : BaseService {"),
        "expected class header:\n{generated}"
    );

    // ── variable declarations ─────────────────────────────────────────
    assert!(
        generated.contains("var name: String = \"default\""),
        "expected mutable var:\n{generated}"
    );
    assert!(
        generated.contains("val maxRetries: Int = 3"),
        "expected immutable val:\n{generated}"
    );
    assert!(
        generated.contains("var count: Int = 0"),
        "expected counter var:\n{generated}"
    );

    // ── function with typed param and return type ─────────────────────
    assert!(
        generated.contains("fun greet(who: String): String {"),
        "expected greet function signature:\n{generated}"
    );
    assert!(
        generated.contains("return \"Hello, \" + who + \"!\""),
        "expected greet return statement:\n{generated}"
    );

    // ── function with if / else-if / else ─────────────────────────────
    assert!(
        generated.contains("fun classify(n: Int): String {"),
        "expected classify function signature:\n{generated}"
    );
    assert!(
        generated.contains("return \"negative\""),
        "expected negative branch:\n{generated}"
    );
    assert!(
        generated.contains("return \"zero\""),
        "expected zero branch:\n{generated}"
    );
    assert!(
        generated.contains("return \"positive\""),
        "expected positive branch:\n{generated}"
    );

    // ── function with for loop ────────────────────────────────────────
    assert!(
        generated.contains("fun sumTo(limit: Int): Int {"),
        "expected sumTo function signature:\n{generated}"
    );
    assert!(
        generated.contains("for (i in 1..limit) {"),
        "expected for loop:\n{generated}"
    );
    assert!(
        generated.contains("total = total + i"),
        "expected loop accumulator:\n{generated}"
    );

    // ── cross-function call ───────────────────────────────────────────
    assert!(
        generated.contains("fun report(who: String, limit: Int): String {"),
        "expected report function signature:\n{generated}"
    );
    assert!(
        generated.contains("val greeting = greet(who)"),
        "expected call to greet:\n{generated}"
    );
    assert!(
        generated.contains("val sum = sumTo(limit)"),
        "expected call to sumTo:\n{generated}"
    );

    // ── closing brace ─────────────────────────────────────────────────
    assert!(
        generated.ends_with('}'),
        "expected closing brace at end:\n{generated}"
    );

    // ── stdout should be the generated source + newline ───────────────
    assert_eq!(
        run.output,
        format!("{generated}\n"),
        "stdout should be generated source followed by a newline"
    );
}

// -----------------------------------------------------------------------
// Kotlin Extractor tests
// -----------------------------------------------------------------------

/// Simple class with a package, a property, and a method.
#[test]
fn extractor_kotlin_simple_class() {
    let src = r#"
package com.example

class Greeter {
    var name: String = "World"
    fun greet(): String {
        return "Hello"
    }
}
"#;
    let out = run_kotlin_extractor(src);
    assert!(
        out.contains("lang [kotlin] Greeter"),
        "missing lang header:\n{out}"
    );
    assert!(
        out.contains("impl[class] ${className}"),
        "missing impl[class]:\n{out}"
    );
    assert!(out.contains("pkg ${pkg}"), "missing pkg line:\n{out}");
    assert!(out.contains("var name"), "missing var name:\n{out}");
    assert!(
        out.contains("func greet(): String {"),
        "missing func greet:\n{out}"
    );
    assert!(out.contains("return"), "missing return:\n{out}");
}

/// Class with a single class-level annotation.
#[test]
fn extractor_kotlin_annotated_class() {
    let src = r#"
package com.example

@Entity
class User {
    var id: Long = 0
    var name: String = ""
}
"#;
    let out = run_kotlin_extractor(src);
    assert!(
        out.contains("@Entity"),
        "missing @Entity annotation:\n{out}"
    );
    assert!(
        out.contains("impl[class] ${className}"),
        "missing impl[class]:\n{out}"
    );
    assert!(out.contains("var id"), "missing var id:\n{out}");
    assert!(out.contains("var name"), "missing var name:\n{out}");
}

/// Data class with the `data` modifier.
#[test]
fn extractor_kotlin_data_class() {
    let src = r#"
package com.example

data class Point {
    var x: Double = 0.0
    var y: Double = 0.0
}
"#;
    let out = run_kotlin_extractor(src);
    assert!(
        out.contains("impl[data class] ${className}"),
        "missing impl[data class]:\n{out}"
    );
    assert!(out.contains("var x"), "missing var x:\n{out}");
}

/// Class that extends a base class.
#[test]
fn extractor_kotlin_class_with_extends() {
    let src = r#"
package com.example

class UserService : BaseService {
    fun findById(id: Long): String {
        return id.toString()
    }
}
"#;
    let out = run_kotlin_extractor(src);
    assert!(
        out.contains("impl[class] ${className} : BaseService"),
        "missing extends:\n{out}"
    );
    assert!(
        out.contains("func findById(id: Long): String {"),
        "missing func findById:\n{out}"
    );
}

/// Multiple members with member-level annotations.
#[test]
fn extractor_kotlin_member_annotations() {
    let src = r#"
package com.example

@Entity
class User {
    @Id
    var id: Long = 0
    @Column
    var name: String = ""
}
"#;
    let out = run_kotlin_extractor(src);
    assert!(out.contains("@Entity"), "missing class @Entity:\n{out}");
    assert!(out.contains("@Id"), "missing @Id:\n{out}");
    assert!(out.contains("@Column"), "missing @Column:\n{out}");
    assert!(out.contains("var id"), "missing var id:\n{out}");
    assert!(out.contains("var name"), "missing var name:\n{out}");
}

// -----------------------------------------------------------------------
// Set inheritance via Leaf.get
// -----------------------------------------------------------------------

/// `conformity/leaf_inherit/Main.tlang`
///
/// Proves that `Leaf.get(childAttrs, key)` returns attrs inherited from
/// parent sets — children do not need to redeclare attrs that exist in a
/// parent.  Also exercises `Leaf.has` as a safe existence check.
///
/// Three inheritance scenarios:
///
/// - **Single**: `Child : Base` — `Leaf.get(childAttrs, "kind")` returns
///   the Base value even though Child never declares `kind`.
/// - **Override**: `Override : Base` — child value wins when both parent
///   and child declare the same attr key.
/// - **Multi-level**: `GrandChild : Child : Base` — `kind` resolves
///   through two hops without redeclaration at any level.
///
/// `Leaf.has` checks:
/// - Returns `true` for an own attr and for an inherited attr.
/// - Returns `false` for a key that is absent in both child and all parents.
#[test]
fn conformity_leaf_inherit() {
    let run = run_conformity("leaf_inherit");

    let expected_output = concat!(
        "animal\n", // Base.kind  (own)
        "animal\n", // Child.kind (inherited from Base)
        "brown\n",  // Child.color (inherited from Base)
        "woof\n",   // Child.sound (own)
        "animal\n", // Override.kind  (inherited)
        "black\n",  // Override.color (overrides Base "brown")
        "4\n",      // Override.legs  (inherited)
        "animal\n", // GrandChild.kind  (2-hop: GrandChild→Child→Base)
        "woof\n",   // GrandChild.sound (1-hop: GrandChild→Child)
        "rex\n",    // GrandChild.name  (own)
        "true\n",   // Leaf.has(childAttrs, "sound")   — own attr
        "true\n",   // Leaf.has(grandAttrs, "sound")   — inherited attr
        "false\n",  // Leaf.has(childAttrs, "wings")   — absent
    );

    assert_eq!(run.output, expected_output, "leaf_inherit output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "leaf_inherit should return \"done\""
    );
}
