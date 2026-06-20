# Language Specification

TLang is a compiled, statically-typed template language. Source files use the .tlang
extension and compile to bytecode before execution. The compiler caches bytecode —
unchanged files are not recompiled on subsequent runs.

## File Structure

Every .tlang file contains an optional header followed by body declarations.
The header holds expose and use statements. The body holds functions, templates, and sets:

```tlang
      // Header — declarations only
      expose myFunction          // export this symbol
      use TLang.Terminal         // import standard library module
      use utils.MathHelpers      // import from utils/MathHelpers.tlang
      use Formatter              // import from Formatter.tlang in same directory

      // Body — implementations
      func myFunction(): String {
          return "hello"
      }
    
```

## Import Paths

Import paths map directly to filesystem paths relative to the project root.

- use TLang.X — standard library module
- use folder.File — file at folder/File.tlang
- use File — file at File.tlang in the same directory

Only symbols declared with expose in the target file can be imported. Unexposed
functions and templates are private to their file.

## Comments

TLang supports single-line comments:

```tlang
      // This is a comment
      let x = 42 // inline comment
    
```

Comments are not permitted inside template block bodies (lang, style, doc) or
inside set declarations.

## Types

TLang is statically typed. Type annotations follow the name with a colon:

- String — UTF-8 text. Literal: "hello". Concatenation with +
- Int — 64-bit signed integer. Literals: 42, -7, 1_000_000
- Float — 64-bit double precision. Literals: 3.14, -0.5, 1.0e6
- Bool — true or false. Operators: && || !
- List — ordered sequence. Constructed with List.of(a, b, c)
- Map — string-keyed dictionary. Constructed with Map.of("key", value, ...)

String concatenation uses the + operator. Int and Float arithmetic use + - * /.
Comparison operators: == != < > <= >=.

## Variables

Variables are declared with let. Bindings are lexically scoped and immutable.
To update a value, rebind the name with a new let in the same scope:

```tlang
      let name = "Alice"
      let count = 0
      let items = List.of("a", "b", "c")

      // Rebind — shadows the previous binding
      let count = count + 1
    
```

## Functions

Functions are top-level declarations. Parameters require explicit type annotations.
The return type is declared after the parameter list:

```tlang
      func greet(name: String): String {
          return "Hello, " + name + "!"
      }

      func add(a: Int, b: Int): Int {
          return a + b
      }

      func isPositive(n: Int): Bool {
          return n > 0
      }

      // Functions can call each other freely
      func greetMany(names: List): String {
          let sb = StringBuilder.create()
          for (name in names) {
              StringBuilder.append(sb, greet(name) + "\n")
          }
          return StringBuilder.build(sb)
      }
    
```

## Control Flow

```tlang
      // if / else if / else
      func classify(n: Int): String {
          if (n < 0) {
              return "negative"
          } else if (n == 0) {
              return "zero"
          } else {
              return "positive"
          }
      }

      // for-in loop over a list
      func join(items: List, sep: String): String {
          let result = ""
          for (item in items) {
              let result = result + item + sep
          }
          return result
      }

      // for-range loop (inclusive on both ends)
      func sumTo(n: Int): Int {
          let total = 0
          for (i in 1..n) {
              let total = total + i
          }
          return total
      }
    
```

## Template Blocks

Template blocks are the primary output mechanism. Each template specifies a
keyword, a list of target formats, a name, and parameters:

```tlang
      lang [kotlin] entity(pkg: String, name: String) {
          pkg ${pkg}
          impl[data class] ${name}(
              val id: Long,
              val name: String
          )
      }
    
```

Inside a template body, ${paramName} is replaced with the argument value at call time.
The first positional argument to a multi-format template selects the output format:

```tlang
      doc [md, html] readme(project: String) {
        # ${project}

        Welcome to ${project}.
      }

      func main(): String {
          let md   = readme("md",   "MyLib")
          let html = readme("html", "MyLib")
          return "ok"
      }
    
```

Available template types:

- lang [target] — generate code in any target language (kotlin, java, ts, html, yaml, rust...)
- doc [md, html] — generate structured documents in multiple formats
- style [css] — generate CSS stylesheets with design tokens and component rules
- data [json, yaml] — generate structured data or configuration files
- cmd [bash, sql] — generate shell scripts or SQL migrations
- raw [text] — generate verbatim text with ${} substitution only

## Doc Template Elements

Inside a doc template body, the following elements are available:

- # Heading — H1 heading (## for H2, ### for H3)
- Paragraph — plain text lines become paragraphs
- [section "id" ... ] — named section, renders as HTML section or Markdown heading group
- [code "lang" ... ] — fenced code block with language hint
- [list "unordered" - item ... ] — bullet list
- [list "ordered" - item ... ] — numbered list
- [link "url" "text"] — hyperlink
- [img "url" "alt"] — image
- [asis ... ] — raw content block; ${params} are still substituted

## Style Template Rules

Inside a style template body, each rule is a selector followed by a block of
property: value pairs separated by commas. Property values that contain spaces
must be quoted strings:

```tlang
      style [css] Tokens() {
          :root {
              --bg: "#0d0d0d",
              --text: "#e8e8e8"
          }
          .card {
              background: "var(--bg)",
              border-radius: 12px,
              padding: "1.5rem 2rem"
          }
          .card [hover] {
              border-color: "var(--accent)"
          }
      }
    
```

Pseudo-class modifiers — [hover], [focus], [active], [visited] — generate the
corresponding CSS pseudo-class selectors (:hover, :focus, etc.).
Comments (//) are not permitted inside style block bodies.

## Set Declarations

A set declares a named record of key-value pairs. Sets form the model that
templates iterate over. All values are strings:

```tlang
      set User {
          pkg: "com.example.model",
          name: "User",
          table: "users"
      }

      set Product {
          pkg: "com.example.model",
          name: "Product",
          table: "products"
      }
    
```

At runtime, Generator.models() returns a List of all sets defined in the project.
Each set is a Map — values are retrieved with model.get("key"):

```tlang
      for (model in Generator.models()) {
          let name = model.get("name")
          let pkg  = model.get("pkg")
          entity(pkg, name)
      }
    
```

## Standard Library

All standard library modules are imported with use TLang.ModuleName.
TLang.Terminal — console output:
println(msg), print(msg)
TLang.File — file I/O (sandboxed to the project directory):
write(path, content, overwrite), read(path): String
TLang.String — string utilities:
split(s, sep), trim(s), replace(s, from, to), upper(s), lower(s),
contains(s, sub), startsWith(s, prefix), endsWith(s, suffix), len(s), indexOf(s, sub)
TLang.List — list operations:
of(...), create(), push(list, item), pop(list), get(list, i),
len(list), map(list, fn), filter(list, fn)
TLang.Map — map operations:
of(k, v, ...), create(), set(map, k, v), get(map, k), keys(map), values(map)
TLang.Math — numeric utilities:
abs(n), min(a, b), max(a, b), floor(n), ceil(n), round(n), sqrt(n), pow(a, b)
TLang.StringBuilder — efficient string accumulation:
create(): StringBuilder, append(sb, str), build(sb): String
TLang.Generator — model iteration:
models(): List of all set declarations in the project
TLang.Leaf — model tree access:
model(name): Map, get(map, key): String

