# Core Language Specification (Draft)

## 1. Program Structure

A TLang source file consists of:

1. **Header directives** (`use`, `expose`), and
2. Zero or more domain blocks (`helper`, `model`, `lang [...]`).

Example:

```tlang
use TLang.Terminal
expose main

model {
    let appName: String = "Demo"
}

helper {
    func main(): String {
        Terminal.println(appName);
        return "ok";
    }
}
```

---

## 2. Header Directives

### 2.1 `use`

Imports a path:

```tlang
use TLang.Terminal
use my.shared.module
```

Alias form:

```tlang
use some.long.path as short
```

### 2.2 `expose`

Declares exported symbols:

```tlang
expose main
expose generate_service
```

---

## 3. `model` Block

The `model` block declares static model nodes.

### 3.1 Variable assignment (`let`)

Syntax (type optional):

```tlang
let name = value
let name: Type = value
```

Example:

```tlang
model {
    let seed: Number = 42
    let enabled: Bool = false
    let package: String = "com.example.demo"
}
```

### 3.2 Entity declaration (`set`)

A `set` entity declares a named type in the model.  It can carry:

- **Body attributes** — static key/value pairs that describe the entity.
- **Constructor parameters** — typed slots that must be filled when creating an instance.

#### Basic syntax (body attrs only)

```tlang
set EntityName {
    attr: "value",
    count: 5,
    enabled: true,
    tags: ["stable", "v1"]
}
```

#### Body attribute value types

| Syntax | Type |
|--------|------|
| `"some string"` | String literal |
| `42` / `-10` | Integer literal |
| `true` / `false` | Boolean literal |
| `["a", "b"]` | Array literal (elements may be any literal type) |
| `SomeName` | Type reference (stored as a string) |
| `SomeName[]` | Array type reference |
| `List<Item>` | Generic type reference (e.g. `List<Field>`) |
| `&funcName` | Reference to a helper function (callable on instances) |
| `(String):(Bool)` | FuncDef — a function-valued constructor param |

#### Constructor parameters

Parameters declare the positional arguments required to create an instance.
Each parameter has a name and a type annotation:

```tlang
set EntityName(param1: String, param2: Int, param3: List<Item>) {
    label: "my-entity",
    count: 5
}
```

Supported parameter types: `String`, `Int`, `Bool`, `List`, `Map`, `Leaf`,
any custom type name, generic types like `List<Item>`, and function types
like `(String):(String)`.

#### Inheritance

```tlang
set Order : User {
    orderId: String,
    items: String[]
}
```

A child entity inherits all body attrs and constructor params from its
parent(s).  Child attrs/params with the same name silently override the
parent's value.

#### Full example

```tlang
model {
    set Config {
        version: "1.0.0",
        maxRetries: 3,
        debugMode: false,
        supportedLangs: ["kotlin", "java"]
    }

    set User(name: String, role: String) {
        label: "user-entity"
    }

    set Order : User {
        orderId: String,
        items: String[]
    }
}
```

---

## 4. `helper` Block

The `helper` block contains executable functions.

### 4.1 Function declarations

Examples:

```tlang
helper {
    func add(a, b) {
        return a + b;
    }

    func main(): String {
        let result = add(3, 7);
        return "done";
    }
}
```

### 4.2 Local bindings and immutability

`let` creates immutable bindings; rebinding the same name shadows prior value.

```tlang
func increment_twice(n) {
    let x = n + 1;
    let x = x + 1;
    return x;
}
```

### 4.3 Control flow

#### `if` / `else if` / `else`

```tlang
func classify(n) {
    if (n < 0) {
        return "negative";
    }
    if (n == 0) {
        return "zero";
    }
    return "positive";
}
```

#### `for` over list

```tlang
func print_words() {
    for (word in ["alpha", "beta", "gamma"]) {
        TLang.Terminal.println(word);
    }
}
```

#### `for` range (`to` inclusive, `until` exclusive)

```tlang
func show_ranges() {
    // inclusive range: 1, 2, 3
    for (i 1 to 3) {
        TLang.Terminal.println(i);
    }

    // exclusive range: 0, 1, 2
    for (j 0 until 3) {
        TLang.Terminal.println(j);
    }
}
```

---

## 5. `data` Block

The `data` block declares structured data templates for generating HTML, JSON, YAML, TOML, XML, or other hierarchical formats.

### Syntax

```tlang
data [lang1, lang2, ...] templateName(params) {
    // structured data content
}
```

### Example

```tlang
data [html, json] userCard(name: String, email: String) {
    div(class: "user-card") {
        h1: "${name}",
        p(class: "email"): "${email}"
    }
}
```

### Key Features

- Multiple language targets can be specified (comma-separated)
- Parameters work the same as `lang` templates
- Content uses a hierarchical bloc syntax
- No generator import required (unlike `lang` templates)

---

## 6. Expressions

### Literals

| Type | Syntax | Example |
|------|--------|---------|
| String | `"..."` | `"hello"` |
| Number (Integer) | `[0-9]+` | `42`, `-10` |
| Number (Float) | `[0-9]+\.[0-9]+` | `3.14`, `-0.5` |
| Boolean | `true` / `false` | `true` |
| List | `[...]` | `[1, 2, 3]`, `["a", "b"]` |
| Map | `{key: value, ...}` | `{"name": "Alice", "age": 30}` |

### Operators

#### Arithmetic

| Operator | Description | Example |
|----------|-------------|---------|
| `+` | Addition | `a + b` |
| `-` | Subtraction | `a - b` |
| `*` | Multiplication | `a * b` |
| `/` | Division | `a / b` |
| `%` | Modulo | `a % b` |

#### Comparison

| Operator | Description | Example |
|----------|-------------|---------|
| `==` | Equal | `a == b` |
| `!=` | Not equal | `a != b` |
| `<` | Less than | `a < b` |
| `>` | Greater than | `a > b` |
| `<=` | Less than or equal | `a <= b` |
| `>=` | Greater than or equal | `a >= b` |

#### Logical

| Operator | Description | Example |
|----------|-------------|---------|
| `&&` | Logical AND | `a && b` |
| `\|\|` | Logical OR | `a \|\| b` |
| `??` | Null-coalescing fallback | `userName ?? "Anonymous"` |

#### Optional Chaining

| Syntax | Description | Example |
|--------|-------------|---------|
| `?.` | Safe member / method access on nullable values | `user?.profile?.name` |

Semantics:
- `a?.b` evaluates to `null` when `a` is `null`; otherwise it evaluates `a.b`.
- `a?.m()` evaluates to `null` when `a` is `null`; otherwise it calls `a.m()`.
- `a ?? b` returns `a` when `a` is non-null, otherwise `b`.

In the current runtime, `null` maps to TLang's unit/nullish value.

### Operator Precedence

From highest to lowest:
1. Parentheses `()`
2. Multiplication `*`, Division `/`, Modulo `%`
3. Addition `+`, Subtraction `-`
4. Comparison `==`, `!=`, `<`, `>`, `<=`, `>=`
5. Logical AND `&&`
6. Logical OR `\|\|`
7. Null-coalescing `??`

Member/method chaining (`.` and `?.`) is part of primary/postfix expression parsing and is applied before infix operators above.

### Expression `if`

`if` can be used as an expression anywhere an expression is accepted:

```tlang
let label = if (n < 0) "negative" else "positive"
let bucket = if (score >= 90) "A" else if (score >= 80) "B" else "C"
return if (ok) "done" else "retry"
```

Rules:
- Expression-form `if` requires an `else` branch.
- The expression result is the selected branch value.
- `else if` chains are supported as nested expression `if`.

### Expression `match`

`match` can also be used as an expression:

```tlang
let kind = match (token) {
  case "+" => "plus",
  case "-" => "minus",
  default => "other"
}

return match (status) {
  case 200 => "ok",
  case 404 => "missing",
  default => "error"
}
```

Rules:
- Each `case` arm in expression-form `match` maps to a value expression.
- A `default` arm is required.
- Expression `match` is valid in assignments, returns, and nested inline expressions.

---

### Lambda Functions

Helper blocks support inline anonymous functions (lambdas) using the `=>` arrow syntax.

#### Expression-body lambda

```tlang
let double = (x) => x * 2;
let result = double(21);  // 42
```

#### Block-body lambda

```tlang
let greet = (name) => {
    return "Hello, " + name;
};
return greet("World");  // "Hello, World"
```

#### Multi-parameter lambda

```tlang
let add = (a, b) => a + b;
return add(3, 4);  // 7
```

#### Zero-parameter lambda

```tlang
let greeting = () => "hi";
return greeting();  // "hi"
```

#### Lambda captures enclosing scope

Lambdas close over the variables in scope when they are created:

```tlang
let base = 100;
let add_base = (n) => n + base;
return add_base(42);  // 142
```

#### Passing lambdas as arguments

A lambda can be passed to any helper function whose parameter is typed `Func`:

```tlang
func apply(f: Func, v: Int): Int {
    return f(v);
}

func main(): Int {
    return apply((x) => x + 10, 5);  // 15
}
```

---

### Function References (`&`)

The `&` prefix creates a reference to a named helper function as a first-class
value.  This is the original callable-value mechanism and is fully preserved.

```tlang
func double(x: Int): Int {
    return x * 2;
}

func main(): Int {
    let f = &double;   // f holds a reference to `double`
    return f(7);       // 14
}
```

`&funcName` is resolved to the string `"funcName"` at parse time, so the
identifier must be valid.  When a variable holding a function-reference string
is called, the runtime looks up the function by name.

#### Using `&` with FuncDef model parameters

Model `set` entities can declare constructor parameters typed as function
definitions `(ParamTypes):(ReturnTypes)`.  Both `&funcName` references and
lambda expressions are accepted:

```tlang
model {
    set SaveStrategy(generate: (String, String, String):(String)) {
        label: "save-strategy"
    }
}

helper {
    func generateWith(pkg: String, cls: String, rec: String): String {
        // ... build and return code string
    }

    func main(): String {
        // Using &ref syntax
        let s1 = SaveStrategy(impl generate: &generateWith)
        let code1 = s1.generate("com.example", "MySaver", "Document")

        // Using an inline lambda
        let s2 = SaveStrategy(impl generate: (p, c, r) => "stub")
        let code2 = s2.generate("com.example", "StubSaver", "Document")

        return code1
    }
}
```

Rules:
- `&funcName` and lambda expressions are interchangeable wherever a `Func`-typed
  value is expected.
- `&funcName` is validated at parse time (must be a valid identifier); lambdas
  are validated at runtime.
- Lambdas capture variables from the enclosing scope at the point of creation.
- Lambda parameters are untyped; the callee is responsible for correct usage.

---

## 7. Set Entity Instantiation

### 7.1 Positional syntax (preferred)

When a `set` entity declares constructor parameters, create an instance by
passing values in declaration order:

```tlang
let alice = User("Alice", "admin")
let p     = Point(3, 7)
```

- Arguments are matched **positionally** to the declared params.
- The number of arguments must exactly match the number of params.
- Any TLang value (string, int, bool, list, another set instance, …) is accepted.

### 7.2 Named syntax (`impl` keyword)

The original named-argument syntax is also supported for backward compatibility:

```tlang
let alice = User(impl name: "Alice", impl role: "admin")
```

Named and positional calls cannot be mixed in the same instantiation.

### 7.3 Reading values from an instance

After instantiation, access constructor-param values and body attrs with
dot notation:

```tlang
let alice = User("Alice", "admin")
Terminal.println(alice.name)   // "Alice"  — constructor param
Terminal.println(alice.label)  // "user-entity"  — body attr
```

### 7.4 Calling ref attributes

Body attrs typed as `&funcName` are callable on instances and prepend the
instance's own impl values as leading arguments:

```tlang
model {
    set Greeter(prefix: String) {
        greetUser: &buildGreeting
    }
}
helper {
    func buildGreeting(prefix: String, name: String): String {
        return prefix + ", " + name + "!"
    }
    func main(): String {
        let g = Greeter("Hello")
        return g.greetUser("World")  // "Hello, World!"
    }
}
```

### 7.5 Zero-param entities

A `set` with no constructor params can be instantiated with an empty call:

```tlang
set Tag {}

let t = Tag()
```

---

## 8. Built-in Libraries

### TLang.Terminal

Provides console output functionality.

**Functions:**
- `println(message)`: Print a message to stdout with a newline

**Example:**
```tlang
use TLang.Terminal

helper {
    func main() {
        Terminal.println("Hello, World!");
        return "done";
    }
}
```

### TLang.File

Provides file I/O operations.

**Functions:**
- `write(path: String, content: String)`: Write content to a file (only if file doesn't exist)
- `write(path: String, content: String, overwrite: Bool)`: Write content to a file (overwrite if `true`)
- `exists(path: String): Bool`: Check if a file exists

**Example:**
```tlang
use TLang.File
use TLang.Generator

helper {
    func main(): String {
        let output = Generator.generate(myTemplate("arg"));
        File.write("output/Generated.kt", output, true);
        return "done";
    }
}
```

### TLang.Generator

Provides code generation functionality.

**Functions:**
- `generate(templateInstance)`: Generate code from a template instance
- `generate(templateInstance, language: String)`: Generate code in a specific language

**Example:**
```tlang
use TLang.Generator
use TLang.Leaf

helper {
    func main(): String {
        myTemplate("arg");
        let model = Leaf.model();
        let instance = Leaf.get(model, "myTemplate_1");
        return Generator.generate(instance);
    }
}
```

### TLang.MCPTool

Internal library used to declare MCP tools from TLang code.

Import it with:

```tlang
use TLang.MCPTool
```

When imported, the runtime provides an internal model type:

```tlang
set MCPTool(name: String, description: String, run: (String):(String)) {}
```

- `run` is a function-valued constructor parameter (FuncDef).
- Instances implement it with `impl run: &someFunc` (or any compatible callable value).

End-to-end example:

```tlang
use TLang.MCPTool
expose provideEchoTool

helper {
    func echoImpl(argsJson: String): String {
        return "echo:" + argsJson;
    }

    func provideEchoTool(): MCPTool {
        return MCPTool(
            impl name: "echo_tool",
            impl description: "Echo dynamic MCP tool",
            impl run: &echoImpl
        );
    }
}
```

MCP server behavior with project context:
- Call `set_project_context(projectPath)` at runtime.
- The server reloads the active project.
- Exposed main-file functions returning `MCPTool` are discovered.
- Implementations inside returned `MCPTool` instances are registered as dynamic MCP tools.
- Tool-list refresh is announced via `notifications/tools/list_changed`.

### TLang.Leaf

Provides access to the model tree and template instances.

**Functions:**
- `model(): Leaf`: Get the root model leaf
- `get(leaf: Leaf, key: String): Leaf`: Get a child leaf by key
- `type(leaf: Leaf): String`: Get the type of a leaf
- `value(leaf: Leaf): Value`: Get the value of a leaf

**Example:**
```tlang
use TLang.Leaf

helper {
    func main(): String {
        let model = Leaf.model();
        let user = Leaf.get(model, "User");
        let name = Leaf.get(user, "name");
        return Leaf.value(name);
    }
}
```

### TLang.List

Provides list manipulation functions.

**Functions:**
- `create(): List`: Create an empty list
- `of(...items)`: Create a list with initial items
- `push(list: List, item)`: Add an item to the end of a list
- `length(list: List): Int`: Get the length of a list
- `get(list: List, index: Int)`: Get an item by index

**Example:**
```tlang
use TLang.List

helper {
    func main(): String {
        let list = List.of("a", "b", "c");
        let list = List.push(list, "d");
        let length = List.length(list);
        return "Length: " + length;
    }
}
```

---

## 9. Built-in Library Import Style

Built-ins are imported via `use TLang.<Library>` and called by short name.

```tlang
use TLang.Terminal

helper {
    func main() {
        Terminal.println("Hello, World!");
        return "done";
    }
}
```

---

## 10. Types

TLang has the following built-in types:

### Primitive Types

| Type | Description | Literal Example |
|------|-------------|-----------------|
| `Int` | 64-bit signed integer | `42`, `-10` |
| `Float` | 64-bit floating point | `3.14`, `-0.5` |
| `Bool` | Boolean | `true`, `false` |
| `String` | UTF-8 string | `"hello"` |
| `Unit` | No value (like void) | (no literal) |

### Collection Types

| Type | Description | Literal Example |
|------|-------------|-----------------|
| `List` | Ordered collection | `[1, 2, 3]` |
| `Map` | Key-value pairs | `{"key": "value"}` |

### Special Types

| Type | Description |
|------|-------------|
| `Leaf` | Model or template instance |
| `SetInstance` | Instance of a `set` entity from model block |

---

## 11. Type Annotations

Function parameters and return types can have type annotations.

### Syntax

```tlang
func functionName(param1: Type, param2: Type): ReturnType {
    // body
}
```

### Example

```tlang
helper {
    func add(a: Int, b: Int): Int {
        return a + b;
    }

    func greet(name: String): String {
        return "Hello, " + name;
    }

    func process(items: List): List {
        // process items
        return items;
    }
}
```

### Supported Type Annotations

| Annotation | TLang Type | Description |
|------------|------------|-------------|
| `Int` | Int | 64-bit integer |
| `Float` | Float | 64-bit float |
| `Bool` | Bool | Boolean |
| `String` | String | String |
| `List` | List | List of any type |
| `Map` | Map | Map/dictionary |
| `Leaf` | Leaf | Model or template instance |
| `TmplLang` | Template | Language template |
| `TmplDoc` | Template | Data template |
| `Func` | Func | Lambda or function reference (also accepted as `Function` or `Callable`) |

---

## 12. Comments

### Single-line Comments

```tlang
// This is a single-line comment
let x = 42;  // Comments can appear after code
```

### Block Comments

TLang does not currently support block comments (`/* ... */`). Use single-line comments for multi-line comments:

```tlang
// This is a multi-line comment
// that spans multiple lines
// Each line must start with //
```

---

## 13. Escaping

### String Escaping

Use `\` to escape special characters in strings:

```tlang
let message = "Hello \"World\"";  // Contains: Hello "World"
let path = "C:\\Users\\Name";      // Contains: C:\Users\Name
```

### Template Escaping

Use `$$` to emit a literal `$` in template bodies:

```tlang
lang [kotlin] example() {
    // Generates: return "Hello, ${name}!"
    return "Hello, $${name}!"
}
```

Use `raw:` prefix to emit a line verbatim without any interpretation:

```tlang
lang [kotlin] example() {
    raw: return someValue  // Not treated as a TLang return statement
}
```
