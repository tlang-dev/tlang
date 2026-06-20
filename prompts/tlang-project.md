# TLang — Project Setup

> Topic file. Load `tlang-index.md` first for routing + cheat-sheets.

---

## Prerequisites

- `tlang` CLI on PATH.
- External generators must be packaged into the local tbox before use:

```
tlang package generators/kotlin
```

Installs to `~/.tlang/tbox/<Org>/<Project>/<Name>/<ver>/<stab>/<rel>/<Name>.tbag`.
Only needed once (or when the generator changes).

---

## Create a Project

```
tlang init my-project        # scaffold: manifest.yml + Main.tlang + output/
```

Or create manually: a directory with at least `manifest.yml` + one `.tlang` file.

---

## manifest.yml

Every project has **exactly one** `manifest.yml` at the project root.

```yaml
name: HelloWorld              # PascalCase — package name
project: HelloWorldProject    # PascalCase — logical group
organisation: MyOrg           # PascalCase
version: 1.0.0
stability: alpha              # alpha | beta | stable
releaseNumber: 1
# main: KotlinCodegen         # optional — stem of entry-point (no .tlang extension)

dependencies:
  # Registry package:  <Org/Project/Name> <ver:stab:rel> <Alias>
  - TLangGen/KotlinGen/Kotlin 1.0.0:alpha:1 KotlinGen
  # Local dir package: file://<relative-path> <Alias>
  - file://libs/my-local-lib LocalLib
```

**Rules:**
- `name`, `project`, `organisation` — PascalCase.
- `stability` — `alpha` | `beta` | `stable`.
- `main:` is a **stem** — no `.tlang` extension, no directory prefix.
- **Never** create `manifest.yml` inside a subfolder. Subfolders are not separate projects.
- **Never** create `output/` inside a subfolder. One `output/` at the project root only.

---

## Source Files (`.tlang`)

A `.tlang` file contains header directives then domain blocks:

| Block | Purpose |
|-------|---------|
| `use …` | Import built-ins, local files, or manifest aliases |
| `expose sym` | Publish a symbol as part of the package public API |
| `helper { … }` | Functions, including the `main` entry point |
| `model { … }` | Static named data instances |
| `lang [alias] name(…) { … }` | Code-generation template |
| `data [fmt] name(…) { … }` | Hierarchical data template |
| `doc [fmt] name(…) { … }` | Document template |
| `style [fmt] name(…) { … }` | Style template |
| `raw [AsIs\|Replaced] name(…) { … }` | Verbatim text template |

---

## Imports (`use`)

### 1. Built-in libraries — no manifest entry needed

```
use TLang.Generator   # Generator.generate(tmpl) → String
use TLang.File        # File.write / File.exists
use TLang.Terminal    # Terminal.println
use TLang.List        # List.create / List.of / List.push / List.get / List.length
use TLang.Leaf        # Leaf.model / Leaf.get / Leaf.type / Leaf.value
use TLang.MCPTool     # MCP tool registration
```

### 2. Local file imports — no manifest entry needed

```
use EntityTemplate              # same dir  → EntityTemplate.tlang
use helpers.RenderHelpers       # one level → helpers/RenderHelpers.tlang
use models.DomainModels         # one level → models/DomainModels.tlang
```

- Max depth: **one subfolder level**. `use a.b.Deep` is rejected.
- Resolved relative to the importing file; merged into the same build automatically.
- **No manifest entry, no `file://`, no sub-manifest needed.**

### 3. Manifest alias imports — requires manifest entry

```
use KotlinGen as kotlin         # alias becomes the lang tag for lang [kotlin]
```

Only the package's `main:` file is importable; only `expose`d symbols are callable.

---

## Project Layout

```
my-project/
├── manifest.yml              # ONE manifest — project root only
├── Main.tlang                # entry point (only use + main)
├── templates/                # lang / data / doc / style / raw blocks
│   ├── EntityTemplate.tlang
│   └── ServiceTemplate.tlang
├── helpers/                  # helper functions
│   └── RenderHelpers.tlang
├── models/                   # model blocks
│   └── DomainModels.tlang
├── output/                   # ONE output dir — project root only
└── target/tlang/
    ├── Main.tlangc           # compiled bytecode
    └── MyGen.tbag            # packaged generator
```

---

## Packaging a Generator

A generator is a TLang project with `main:` in its manifest and `expose` in its
entry-point file.

```yaml
# manifest.yml
name: Kotlin
project: KotlinGen
organisation: TLangGen
version: 1.0.0
stability: alpha
releaseNumber: 1
main: KotlinCodegen
```

```
// KotlinCodegen.tlang
expose generate

helper {
    func generate(tmpl): String { … }
}
```

```
tlang package generators/kotlin
```

Compiles, bundles into `target/tlang/Kotlin.tbag`, copies to tbox.

---

## Using a Packaged Generator

```yaml
# manifest.yml
dependencies:
  - TLangGen/KotlinGen/Kotlin 1.0.0:alpha:1 KotlinGen
```

```
// Main.tlang
use KotlinGen as kotlin

lang [kotlin] myClass(pkg: String) { … }
```

---

## Set Attribute Values — Type References vs Plain Strings

TLang set attributes accept two kinds of values. Choosing the right one matters for correctness and future compile-time checking.

### Unquoted identifiers — type references

Use an **unquoted** identifier when the value names another TLang `set`:

```
set MarketProject : Forge.ForgeProject {
    codeAdapter: QuarkusKotlin,     ← type reference: names a set in the model
    data:        MarketData,        ← type reference: names a DataGenerator set
    infra:       MarketInfra,       ← type reference: names an InfraGenerator set
    security:    MarketSecurity     ← type reference: names a SecurityGenerator set
}

set PackageField : Forge.Field {
    type: StringType                ← type reference: names a Forge type set
}

set PackageCrud : Forge.Crud {
    entity: PackageEntity           ← type reference: names an Entity set
}
```

Both `"QuarkusKotlin"` (quoted) and `QuarkusKotlin` (unquoted) produce the same `String` value at runtime. The unquoted form is the correct choice because it signals a type reference — tooling and future compile-time checks use this distinction to verify the named set exists and satisfies the required contract.

### Quoted strings — plain data

Use a **quoted** string for everything that is not a set name:

```
set MarketProject : Forge.ForgeProject {
    name:      "pkgmarket",              ← plain string: data value
    package:   "dev.tlang.market",       ← plain string: Java package
    outputDir: "output",                 ← plain string: directory path
    backend:   "quarkus",               ← plain string: technology hint
    language:  "kotlin"                  ← plain string: technology hint
}

set PackageCrud : Forge.Crud {
    name:       "PackageResource",       ← plain string: generated class name
    operations: "create read update delete list",  ← plain string: space-separated list
    path:       "/api/packages",         ← plain string: URL path
    secured:    "true"                   ← plain string: boolean flag
}
```

### Quick rule

> If the value **is the name of a `set`**, write it **unquoted**.  
> If the value **is data** (URL, class name, tech hint, flag, list), write it **quoted**.

---

## CLI Reference

| Task | Command |
|------|---------|
| Scaffold project | `tlang init [dir]` |
| Compile | `tlang compile [dir]` |
| Run | `tlang run [dir]` |
| Compile + run | `tlang both [dir]` |
| Compile + run (no disk write) | `tlang both --in-memory [dir]` |
| Package generator | `tlang package [dir]` |
| Help | `tlang --help` |
| Version | `tlang --version` |

`[dir]` defaults to the current directory when omitted.
`--in-memory` is only valid with `both` / `compile-run`.