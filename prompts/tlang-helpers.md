# TLang — Helpers, Expressions & Builtins

> Topic file. Load `tlang-index.md` first for routing + cheat-sheets.

---

## The `helper` Block

Contains all executable functions. The `main` function is the program entry
point and **must** declare a return type.

```
use TLang.Generator
use TLang.File

helper {
    func main(): String {
        let output = Generator.generate(myTemplate("com.example"))
        File.write("output/Main.kt", output, true)
        return output
    }
}
```

### Function syntax

```
func name(param1: Type, param2: Type): ReturnType {
    // body
}
```

Type annotations are optional on parameters; return type is required on `main`
and recommended everywhere.

### `let` bindings

`let` creates an immutable binding. Rebind the same name to shadow it:

```
let x = n + 1
let x = x + 1   // shadows previous x
```

---

## Control Flow

### Statement `if` / `else if` / `else`

```
if (n < 0) {
    return "neg"
} else if (n == 0) {
    return "zero"
} else {
    return "pos"
}
```

### Expression `if` — requires `else`

```
let label = if (n < 0) "neg" else if (n == 0) "zero" else "pos"
return if (ok) "done" else "retry"
```

### `for` over a list

```
for (item in myList) {
    Terminal.println(item)
}
```

### `for` range

```
for (i 1 to 3)    { … }   // inclusive: 1 2 3
for (j 0 until 3) { … }   // exclusive: 0 1 2
```

---

## `match`

### Statement form

```
match (status) {
    case 200 { Terminal.println("ok") }
    case 404 { Terminal.println("missing") }
    default  { Terminal.println("other") }
}
```

### Expression form — requires `default`

```
let mime = match (ext) {
    case "ts"   => "text/typescript",
    case "json" => "application/json",
    default     => "text/plain"
}

return match (status) {
    case 200 => "ok",
    case 404 => "missing",
    default  => "error"
}
```

- Arms end with `,`; last arm may omit it.
- `default` is mandatory in expression form.
- Expression `match` is valid in assignments, `return`, and nested expressions.

---

## Operators

### Arithmetic / Comparison / Logical

| Op | Description |
|----|-------------|
| `+` `-` `*` `/` `%` | Arithmetic |
| `==` `!=` `<` `>` `<=` `>=` | Comparison |
| `&&` `\|\|` | Logical AND / OR |

### Optional chaining `?.` and null-coalescing `??`

```
let name = user?.profile?.displayName          // null if any step is null
let disp = user?.profile?.displayName ?? "Anon"
let port = config?.server?.port ?? 8080
```

| Operator | Meaning |
|----------|---------|
| `a?.b` | `null` if `a` is null, else `a.b` |
| `a?.m()` | `null` if `a` is null, else `a.m()` |
| `a ?? b` | `a` if non-null, else `b` |

---

## Lambdas and Function References

### Lambda

```
let double  = (x) => x * 2
let greet   = (name) => { return "Hello, " + name }
let add     = (a, b) => a + b
let hello   = () => "hi"
```

Lambdas close over enclosing scope:

```
let base = 100
let addBase = (n) => n + base
addBase(42)   // 142
```

Pass as argument (parameter typed `Func`):

```
func apply(f: Func, v: Int): Int { return f(v) }
apply((x) => x + 10, 5)   // 15
```

### Function reference (`&`)

```
func double(x: Int): Int { return x * 2 }

let f = &double
f(7)   // 14
```

`&funcName` and lambdas are interchangeable wherever a `Func`-typed value is
expected.

---

## Types

### Primitives

| Type | Example |
|------|---------|
| `Int` | `42`, `-10` |
| `Float` | `3.14`, `-0.5` |
| `Bool` | `true`, `false` |
| `String` | `"hello"` |
| `Unit` | (no literal — like void) |

### Collections

| Type | Literal |
|------|---------|
| `List` | `[1, 2, 3]`, `["a", "b"]` |
| `Map` | `{"key": "value"}` |

### Special

| Type | Description |
|------|-------------|
| `Leaf` | Model node or template instance |
| `Func` | Lambda or function reference (also `Function`, `Callable`) |

---

## `model` Block

Declares static named data instances. Accessed in helpers via dot notation.

### `let` — scalar value

```
model {
    let pkg:     String = "com.example"
    let debug:   Bool   = false
    let version: String = "1.0.0"
}
```

Access: `let p = pkg` (direct name) or `let p = Leaf.value(Leaf.get(Leaf.model(), "pkg"))`.

### `set` — entity with attributes

```
model {
    set User {
        package:    "com.example",
        class_name: "User",
        extends:    "BaseEntity"
    }
}
```

Direct dot access (preferred when name is known):

```
let p = User.package
let c = User.class_name
```

Leaf API (use when entity name is dynamic):

```
let model  = Leaf.model()
let entity = Leaf.get(model, "User")
let attrs  = Leaf.get(entity, "attrs")
let pkg    = Leaf.get(Leaf.get(attrs, "package"), "value")
```

### `set` with inheritance (`ext`)

```
model {
    set Order ext User {
        orderId: String,
        items:   String[]
    }
}
```

### `set` with constructor params + `>>` write path

```
model {
    set BasicRepo(pkg: String, className: String, recordType: String)
            >> "output/${className}.kt" {
        lead:   &classShell,
        save:   &saveMethod
    }
}
```

Instantiate with `impl` keyword:

```
let repo = BasicRepo(
    impl pkg:        "com.example",
    impl className:  "DocumentRepo",
    impl recordType: "Document"
)
repo.generateAll()
```

### `set` with FuncDef parameter

```
model {
    set SaveStrategy(generate: (String, String):(String)) {
        label: "save-strategy"
    }
}

helper {
    func myGen(pkg: String, cls: String): String { … }

    func main(): String {
        let s = SaveStrategy(impl generate: &myGen)
        return s.generate("com.example", "User")
    }
}
```

