# Template Block Specification (Draft)

## 1. Template Block Form

A template block starts with:

```tlang
lang [<language>] <templateName>(<optionalParams>) { ... }
```

Examples:

```tlang
lang [java] service(name: String) {
    impl Service {
        name: String
    }
}
```

```tlang
lang [kotlin] entity(pkg: String, className: String) {
    pkg ${pkg}
    impl[public class] ${className} {
    }
}
```

---

## 2. Parameters

Template parameters are optional and may include type annotations.

```tlang
lang [rust] service(name: String, retries: i32) {
    impl Service {
        name: String
    }
}
```

---

## 3. Specialized Templates (`spec`)

A template may use the specialized form by appending `spec` before the block:

```tlang
lang [go] handler(name: String) spec {
    <[ buildBody(name) ]>
}
```

This variant stores specialized template content as a dedicated specialized template body.
The body contains a single `tmplContent` entry (an expression, attribute, `setAttr`, or
`param` statement) rather than the full pkg/use/impl structure of a regular template.

---

## 4. Identifiers and Interpolation

Template identifiers support interpolation with `${...}`.

```tlang
lang [kotlin] ${entityName}(pkg: String) {
    pkg ${pkg}
    impl[public class] ${entityName} {
    }
}
```

Backtick-escaped identifiers are also supported:

```tlang
lang [java] `my-template`(name: String) {
    impl Service {
        name: String
    }
}
```

---

## 5. Typical Runtime Usage Pattern

A common flow is:

1. Instantiate a template call,
2. Read the instantiated model leaf,
3. Generate output through `TLang.Generator`.

```tlang
use TLang.Generator
use TLang.Leaf

lang [java] service(name: String) {
    impl Service {
        name: String
    }
}

helper {
    func main(): String {
        service("UserService");
        let model = Leaf.model();
        let tmpl = Leaf.get(model, "service_1");
        return Generator.generate(tmpl, "java");
    }
}
```

---

## 6. Template Includes (`<[ ... ]>`)

### Syntax

An *include expression* may appear anywhere a template content statement is valid:

```
<[ helperCall(arg1, arg2) ]>
```

The delimiters `<[` and `]>` mark the boundary of the include.  Everything
between them is a single function-call expression.  Multiple arguments are
comma-separated and follow the same rules as regular helper-function calls.

### Semantics

When the runtime instantiates a template it scans the template body for every
`<[…]>` span and evaluates the enclosed call **at instantiation time** using
the current runtime context.  The result is inlined into the template body
before the instance is stored in the model or passed to a code generator.

Arguments may be:

| Argument form             | Resolution                                   |
|---------------------------|----------------------------------------------|
| Template parameter name   | Substituted with the bound argument value    |
| String literal `"…"`      | Used verbatim (quotes stripped)              |
| Integer / float literal   | Parsed and used as the corresponding type    |
| Anything else             | Treated as a plain string                    |

### Return-value handling

The called helper may return any of the following:

| Return type                        | Inline behaviour                                                                 |
|------------------------------------|----------------------------------------------------------------------------------|
| `String`                           | Inserted verbatim at the `<[…]>` site                                            |
| `List`                             | Each element is processed recursively; results are concatenated                  |
| `Leaf` with `type = "TemplateInstance"` | The instance body is extracted, `${param}` substitutions are applied, the outer `{ … }` braces are stripped, and the inner fragment is inlined |
| Any other value                    | Coerced to a string via `value_to_string`                                        |

### Example — generating fields from a list

```tlang
use TLang.Generator
use TLang.Leaf
use TLang.List

// Spec template that emits a single `var` declaration.
lang [kotlin] attrNode(attrDecl: String) spec {
    var ${attrDecl}
}

// Full template with an include that delegates field generation to a helper.
lang [kotlin] dataClass(className: String, attrs: List) {
    impl[public class] ${className} {
        <[ renderAttrs(attrs) ]>
    }
}

helper {
    // Build one attrNode TemplateInstance per attribute and return them as a List.
    // The runtime recognises each leaf as a TemplateInstance, extracts the
    // substituted `var …` fragment, and splices them all into the impl body.
    func renderAttrs(attrs: List): List {
        let nodes = List.create()
        for (attr in attrs) {
            let node = attrNode(attr)
            let nodes = List.push(nodes, node)
        }
        return nodes
    }

    func main(): String {
        let attrs = List.of("id: Long", "name: String", "email: String")
        dataClass("User", attrs)

        let model    = Leaf.model()
        let instance = Leaf.get(model, "dataClass_1")
        return Generator.generate(instance, "kotlin")
    }
}
```

The generated Kotlin output will contain:

```kotlin
public class User {
    var id: Long
    var name: String
    var email: String
}
```

### Includes in specialized templates

`<[…]>` is equally valid as the single content statement of a `spec` block:

```tlang
lang [kotlin] fieldSpec(fieldDecl: String) spec {
    <[ buildField(fieldDecl) ]>
}
```

### Includes as function arguments and attribute values

The grammar also permits `<[…]>` in positions where a `setAttr` value or array
element is expected (`tmplInclSetAttribute`, `tmplInclAttribute`,
`tmplArrayValue`).  This allows dynamic values to be injected directly into
named-parameter lists and structured attribute blocks:

```tlang
lang [kotlin] wrapper(content: String) {
    impl Wrapper {
        data = <[ computeData(content) ]>
    }
}
```

### Error handling

If the `<[…]>` span is not terminated (missing `]>`), the runtime raises an
error at instantiation time:

```
unterminated `<[` include in template body
```

If the enclosed expression is not a valid function call (no parentheses), the
runtime raises:

```
include expression `…` is not a function call
```
