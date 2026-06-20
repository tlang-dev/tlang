// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use crate::runtime::Value;

use super::helpers::run_conformity;

// -----------------------------------------------------------------------
// Set model
// -----------------------------------------------------------------------

/// `conformity/set_model/Main.tlang`
///
/// Exercises the `model { set ... }` declaration and the `TLang.Leaf`
/// library for inspecting the compiled model tree at runtime:
///
/// - `Leaf.model()` returns a Leaf containing one key per model node.
/// - `Leaf.get(entity, "name")` → the entity name string.
/// - `Leaf.get(entity, "kind")` → `"set"`.
/// - `Leaf.get(entity, "ext")` → the parent entity name (when present).
/// - `Leaf.get(entity, "attrs")` → a Leaf keyed by attribute name.
/// - `Leaf.get(attr_leaf, "value")` → the attribute type string.
///
/// Three entities are declared: `User`, `Product`, and `Order ext User`.
#[test]
fn conformity_set_model() {
    let run = run_conformity("set_model");

    let expected_output = concat!(
        "User\n",     // Leaf.get(user, "name")
        "set\n",      // Leaf.get(user, "kind")
        "String\n",   // User.name attribute type
        "Number\n",   // User.age attribute type
        "Product\n",  // Leaf.get(product, "name")
        "Number\n",   // Product.price attribute type
        "Order\n",    // Leaf.get(order, "name")
        "User\n",     // Leaf.get(order, "ext")
        "String\n",   // Order.orderId attribute type
        "String[]\n", // Order.items attribute type (array)
    );

    assert_eq!(run.output, expected_output, "set model output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "set_model should return \"done\""
    );
}

// -----------------------------------------------------------------------
// Set instance
// -----------------------------------------------------------------------

/// `conformity/set_instance/Main.tlang`
///
/// Exercises the full `set` entity instantiation feature:
///
/// - Static attribute access: `Config.version`, `Config.maxRetries`, etc.
///   including integer (`3`) and boolean (`false`) and array (`["stable","v1"]`) body attrs.
/// - Positional instantiation: `User("Alice", "admin")`
/// - Reading positional-param values from an instance: `alice.name`, `alice.role`
/// - Reading a body attribute from an instance: `alice.label`
/// - Named instantiation: `User(name: "Carol", role: "guest")`
/// - Ref attribute dispatch (positional): `Greeter("Hello")` → `greeter.greetUser("World")`
/// - FuncDef param dispatch (positional): `Formatter("shout")` → `fmt.format("hello")`
#[test]
fn conformity_set_instance() {
    let run = run_conformity("set_instance");

    let expected_output = concat!(
        "1.0.0\n",         // Config.version
        "3\n",             // Config.maxRetries  (Int body attr)
        "false\n",         // Config.debugMode   (Bool body attr)
        "Alice\n",         // alice.name (positional)
        "admin\n",         // alice.role (positional)
        "Bob\n",           // bob.name (positional)
        "user-entity\n",   // alice.label (body attr)
        "Carol\n",         // carol.name (named instantiation)
        "Hello, World!\n", // greeter.greetUser("World") → buildGreeting
        "Hi, TLang!\n",    // greeter2.greetUser("TLang")
        "hello!!!\n",      // fmt.format("hello") → shout
    );

    assert_eq!(run.output, expected_output, "set_instance output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "set_instance should return \"done\""
    );
}

/// Positional set-entity instantiation: `Name(v1, v2, v3)` syntax.
///
/// - Verifies that `EntityName(arg1, arg2)` binds args positionally to declared params.
/// - Verifies that model body attrs support `Int`, `Bool`, and array `[...]` literals.
/// - Verifies that passing another set instance as a positional arg works.
#[test]
fn conformity_set_instance_positional() {
    let src = concat!(
        "use TLang.Terminal\n",
        "set Point(x: Int, y: Int) {\n",
        "    kind: \"point\",\n",
        "    defaultScale: 1,\n",
        "    visible: true,\n",
        "    coords: [0, 0]\n",
        "}\n",
        "func main(): String {\n",
        "    let p = Point(3, 7)\n",
        "    Terminal.println(p.x)\n",
        "    Terminal.println(p.y)\n",
        "    Terminal.println(p.kind)\n",
        "    Terminal.println(p.defaultScale)\n",
        "    Terminal.println(p.visible)\n",
        "    return \"done\"\n",
        "}\n",
    );
    let model = crate::parser::parse_domain_model(src).expect("parses");
    let compiled = crate::runtime::compile_from_domain_model(&model).expect("compiles");
    let run = crate::runtime::run_main(&compiled).expect("runs");
    assert_eq!(
        run.output, "3\n7\npoint\n1\ntrue\n",
        "positional instantiation output mismatch"
    );
    assert_eq!(run.return_value, Value::String("done".to_string()));
}

/// Positional instantiation: wrong argument count produces a clear error.
#[test]
fn conformity_set_instance_positional_wrong_arg_count() {
    let src = concat!(
        "use TLang.Terminal\n",
        "set Pair(a: String, b: String) {}\n",
        "func main(): String {\n",
        "    let p = Pair(\"only-one\")\n",
        "    Terminal.println(p.a)\n",
        "    return \"done\"\n",
        "}\n",
    );
    let model = crate::parser::parse_domain_model(src).expect("parses");
    let compiled = crate::runtime::compile_from_domain_model(&model).expect("compiles");
    let err =
        crate::runtime::run_main(&compiled).expect_err("wrong positional arg count must fail");
    assert!(
        err.0.contains("Pair") && (err.0.contains("2") || err.0.contains("expects")),
        "error should mention entity name and expected count; got: {}",
        err.0
    );
}

/// Entity with `List<Field>` constructor param and typed `Type` instances.
///
/// Verifies:
/// - `Entity("Name", "Desc", [Field("x", Email()), Field("y", Number())])` works
/// - `entity.fields` returns the list of `Field` instances
/// - `field.name` returns the positional string arg
/// - `field.type` returns the `Type` set instance (its body attr `kind` is accessible)
/// - Body attr defaults (`nullable: false`, `insertable: true`) are readable on `Field`
#[test]
fn conformity_entity_with_list_of_field() {
    let src = concat!(
        "use TLang.Terminal\n",
        "use TLang.List\n",
        "set Type {}\n",
        "set Email    : Type { kind: \"Email\" }\n",
        "set Number   : Type { kind: \"Number\" }\n",
        "set Field(name: String, type: Type) {\n",
        "    kind:       \"field\",\n",
        "    nullable:   false,\n",
        "    insertable: true\n",
        "}\n",
        "set Entity(name: String, description: String, fields: List<Field>) {\n",
        "    kind: \"entity\"\n",
        "}\n",
        "func main(): String {\n",
        "    let emailField  = Field(\"email\",  Email())\n",
        "    let ageField    = Field(\"age\",    Number())\n",
        "    let user = Entity(\"User\", \"App user\", [emailField, ageField])\n",
        "\n",
        "    // entity-level\n",
        "    Terminal.println(user.name)\n",
        "    Terminal.println(user.description)\n",
        "    Terminal.println(user.kind)\n",
        "\n",
        "    // iterate fields\n",
        "    let fieldList = user.fields\n",
        "    let first  = List.get(fieldList, 0)\n",
        "    let second = List.get(fieldList, 1)\n",
        "    Terminal.println(first.name)\n",
        "    Terminal.println(first.nullable)\n",
        "    Terminal.println(first.insertable)\n",
        "    Terminal.println(second.name)\n",
        "\n",
        "    // field type body attr\n",
        "    let emailType = first.type\n",
        "    Terminal.println(emailType.kind)\n",
        "\n",
        "    return \"done\"\n",
        "}\n",
    );
    let model = crate::parser::parse_domain_model(src).expect("parses");
    let compiled = crate::runtime::compile_from_domain_model(&model).expect("compiles");
    let run = crate::runtime::run_main(&compiled).expect("runs");
    assert_eq!(
        run.output,
        concat!(
            "User\n",
            "App user\n",
            "entity\n",
            "email\n",
            "false\n",
            "true\n",
            "age\n",
            "Email\n",
        ),
        "entity/field output mismatch"
    );
    assert_eq!(run.return_value, Value::String("done".to_string()));
}

/// Error: instantiating with a missing required constructor param.
#[test]
fn conformity_set_instance_missing_param_error() {
    let src = concat!(
        "use TLang.Terminal\n",
        "set Widget(title: String, color: String) {\n",
        "    label: \"widget\"\n",
        "}\n",
        "func main(): String {\n",
        "    let w = Widget(title: \"My Widget\")\n",
        "    Terminal.println(w.label)\n",
        "}\n",
    );
    let model = crate::parser::parse_domain_model(src).expect("parses");
    let err = crate::runtime::compile_from_domain_model(&model)
        .expect_err("missing required constructor param must fail at compile time");
    assert!(
        err.0.contains("color"),
        "error should mention the missing param `color`; got: {}",
        err.0
    );
}

/// Error: providing a key that is not a declared constructor param.
#[test]
fn conformity_set_instance_unknown_param_error() {
    let src = concat!(
        "use TLang.Terminal\n",
        "set Widget(title: String) {\n",
        "    label: \"widget\"\n",
        "}\n",
        "func main(): String {\n",
        "    let w = Widget(title: \"ok\", extra: \"bad\")\n",
        "    Terminal.println(w.label)\n",
        "}\n",
    );
    let model = crate::parser::parse_domain_model(src).expect("parses");
    let err = crate::runtime::compile_from_domain_model(&model)
        .expect_err("unknown constructor param must fail at compile time");
    assert!(
        err.0.contains("extra"),
        "error should mention the unknown key `extra`; got: {}",
        err.0
    );
}

/// Error: accessing a Ref attribute on the static entity (not an instance).
#[test]
fn conformity_set_instance_static_ref_access_error() {
    let src = concat!(
        "use TLang.Terminal\n",
        "set Service(id: String) {\n",
        "    compute: &myCalc\n",
        "}\n",
        "func myCalc(id: String): String {\n",
        "    return \"result-\" + id\n",
        "}\n",
        "func main(): String {\n",
        "    let r = Service.compute\n",
        "    Terminal.println(r)\n",
        "}\n",
    );
    let model = crate::parser::parse_domain_model(src).expect("parses");
    let compiled =
        crate::runtime::compile_from_domain_model(&model).expect("compiles without type error");
    let err = crate::runtime::run_main(&compiled)
        .expect_err("static ref access must fail at runtime");
    assert!(
        err.0.contains("ref function") || err.0.contains("static"),
        "error should mention static ref restriction; got: {}",
        err.0
    );
}

// -----------------------------------------------------------------------
// Let model
// -----------------------------------------------------------------------

/// `conformity/let_model/Main.tlang`
///
/// Exercises the `model { let ... }` declaration.  Each `let` node in the
/// model is accessible via `Leaf.model()` as a Leaf with three fields:
///
/// - `"name"` — the variable name as a string.
/// - `"type"` — the declared type annotation as a string.
/// - `"value"` — the raw value text as a string.
///
/// Three constants are declared: `seed: Number = 42`, `enabled: Bool = false`,
/// `max: Number = 100`.
#[test]
fn conformity_let_model() {
    let run = run_conformity("let_model");

    let expected_output = concat!(
        "seed\n",    // seed name
        "Number\n",  // seed type
        "42\n",      // seed value
        "enabled\n", // enabled name
        "Bool\n",    // enabled type
        "false\n",   // enabled value
        "max\n",     // max name
        "Number\n",  // max type
        "100\n",     // max value
    );

    assert_eq!(run.output, expected_output, "let model output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "let_model should return \"done\""
    );
}
