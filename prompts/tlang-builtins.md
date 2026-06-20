# TLang — Built-in Libraries

> Topic file. Load `tlang-index.md` first for routing + cheat-sheets.

---

## `TLang.Generator`

```
use TLang.Generator

Generator.generate(tmplCall)              // → String
Generator.generate(tmplCall, "kotlin")    // → String, explicit lang
```

---

## `TLang.File`

```
use TLang.File

File.write(path, content)           // scaffold-once — skips if file exists
File.write(path, content, true)     // always overwrite
File.exists(path): Bool
```

Paths are **relative to the project root**. Never include the project directory
name. Parent directories are created automatically.

| Intent | Call | Third arg |
|--------|------|-----------|
| Fully generated (regenerate every run) | `File.write(p, c, true)` | `true` |
| Boilerplate (write once, user edits) | `File.write(p, c)` | _(omit)_ |

---

## `TLang.Terminal`

```
use TLang.Terminal

Terminal.println(message)
```

---

## `TLang.List`

```
use TLang.List

let a = List.of("x", "y", "z")         // fixed list
let b = List.create()                   // empty list
let b = List.push(b, "item")           // append (returns new list)
let n = List.size(a)                    // Int
let v = List.get(a, 0)                  // item at index
```

Use `List.of` for small fixed lists; `List.create` + `List.push` for dynamic
lists built in loops.

### Higher-order functions

All HOFs accept a lambda `(param) => expr` or a named function reference `&myFunc`.
All HOFs also work with dot-method syntax: `myList.map((x) => x + 1)`.

| Call | Returns | Description |
|------|---------|-------------|
| `List.map(list, (x) => expr)` | `List` | Transform each element |
| `List.filter(list, (x) => bool)` | `List` | Keep elements where predicate is true |
| `List.forEach(list, (x) => expr)` | `Unit` | Call side-effect for each element |
| `List.reduce(list, init, (acc, x) => expr)` | `Value` | Fold left |
| `List.any(list, (x) => bool)` | `Bool` | True if any element matches |
| `List.all(list, (x) => bool)` | `Bool` | True if every element matches |
| `List.find(list, (x) => bool)` | `Value?` | First match, or `Unit` |
| `List.flatMap(list, (x) => List)` | `List` | Map then flatten one level |
| `List.sortBy(list, (x) => key)` | `List` | Sort by derived key (`Int` or `String`) |
| `List.groupBy(list, (x) => String)` | `Map` | Group into map of lists by key |
| `List.count(list, (x) => bool)` | `Int` | Count matching elements |

```
use TLang.List

let doubled = List.map(List.of(1, 2, 3), (x) => x * 2)          // [2, 4, 6]
let evens   = List.filter(List.of(1, 2, 3, 4), (x) => x > 2)    // [3, 4]
let sum     = List.reduce(List.of(1, 2, 3), 0, (acc, x) => acc + x)  // 6
let found   = List.find(List.of(1, 2, 3), (x) => x > 1)         // 2
let grouped = List.groupBy(List.of("ant", "bee", "arc"), (w) => w.substring(0, 1))
              // Map{ "a": ["ant","arc"], "b": ["bee"] }

// dot-method chaining:
let result = myList.filter((x) => x > 0).map((x) => x * 2)
```

---

## `TLang.Map`

```
use TLang.Map

let m  = Map.of("key", "value", "k2", "v2")   // create from pairs
let m2 = Map.create()                          // empty map
let v  = Map.get(m, "key")                     // value or error
let v  = Map.getOrDefault(m, "key", "default")
let m3 = Map.set(m, "key", "newValue")         // returns new map
let ks = Map.keys(m)                           // List<String>
```

### Higher-order functions

| Call | Returns | Description |
|------|---------|-------------|
| `Map.mapValues(map, (k, v) => expr)` | `Map` | Transform each value |
| `Map.filterKeys(map, (k) => bool)` | `Map` | Keep entries by key predicate |
| `Map.filterValues(map, (k, v) => bool)` | `Map` | Keep entries by key+value predicate |
| `Map.forEach(map, (k, v) => expr)` | `Unit` | Side-effect for each entry |
| `Map.reduce(map, init, (acc, k, v) => expr)` | `Value` | Fold over entries |

```
let upper = Map.mapValues(myMap, (k, v) => v.toUpperCase())
let sub   = Map.filterKeys(myMap, (k) => k.startsWith("a"))
```

---

## `TLang.Shell`

```
use TLang.Shell

Shell.run("git status")                       // → String (stdout), raises on non-zero exit
Shell.runIn("./gradlew build", "output")      // → String, in given directory
Shell.capture("git log --oneline -5")         // → Map{ stdout, stderr, exitCode, success }
Shell.captureIn("./gradlew test", "output")   // → Map, in given directory
Shell.stream("./gradlew quarkusDev")          // → Int (exit code), live output to terminal
Shell.streamIn("bash start-dev.sh", "output") // → Int, live output, in given directory
Shell.env("HOME")                             // → String env var value, or ""
Shell.which("java")                           // → String absolute path, or ""
```

| Function | Returns | Buffers output? | Fails on non-zero? |
|----------|---------|-----------------|-------------------|
| `Shell.run` | `String` (stdout) | Yes — waits for exit | Yes |
| `Shell.runIn` | `String` (stdout) | Yes — waits for exit | Yes |
| `Shell.capture` | `Map` | Yes — waits for exit | No |
| `Shell.captureIn` | `Map` | Yes — waits for exit | No |
| `Shell.stream` | `Int` (exit code) | **No — live to terminal** | No |
| `Shell.streamIn` | `Int` (exit code) | **No — live to terminal** | No |

Use `Shell.stream` / `Shell.streamIn` for long-running processes such as
`./gradlew quarkusDev`, `npm run dev`, or any server that should print logs
continuously. `Shell.run` is best for quick commands where you need to capture
and process the output as a string.

```
use TLang.Shell

// Live Quarkus logs printed straight to the terminal:
let code = Shell.streamIn("bash start-dev.sh", "output")

// Capture output for further processing:
let result = Shell.capture("git log --oneline -5")
let lines  = result.stdout.split("\n")
```

---

## `TLang.Leaf`

```
use TLang.Leaf

let root   = Leaf.model()
let entity = Leaf.get(root, "User")
let attrs  = Leaf.get(entity, "attrs")
let pkg    = Leaf.get(Leaf.get(attrs, "package"), "value")
let kind   = Leaf.type(entity)
let val    = Leaf.value(entity)
```

Prefer direct dot-access (`User.package`) when the entity name is known at
write time. Fall back to `Leaf.get` only when iterating dynamically.

---

## `TLang.MCPTool`

```
use TLang.MCPTool
expose provideMyTool

helper {
    func runImpl(argsJson: String): String {
        return "result:" + argsJson
    }

    func provideMyTool(): MCPTool {
        return MCPTool(
            impl name:        "my_tool",
            impl description: "Does something useful",
            impl run:         &runImpl
        )
    }
}
```

Functions returning `MCPTool` that are `expose`d are discovered by the MCP
server and registered as dynamic tools.