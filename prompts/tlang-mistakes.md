# TLang — Common Mistakes

> Topic file. Load `tlang-index.md` first for routing + cheat-sheets.

---

## Project & Manifest

| Mistake | Fix |
|---------|-----|
| `manifest.yml` created inside a subfolder | One manifest only, at the project root. Subfolders are not separate projects. |
| `output/` created inside a subfolder | One `output/` only, at the project root. |
| `main:` field includes `.tlang` extension | Use the stem only: `main: KotlinCodegen`, not `main: KotlinCodegen.tlang`. |
| Added `manifest.yml` or `file://` entry for a sibling/subfolder import | Not needed. `use FileName` or `use Folder.FileName` resolves on disk automatically — no manifest entry required. |
| `use a.b.DeepFile` (two levels deep) | Max depth is one subfolder. Restructure or use a manifest `file://` alias. |
| A subfolder `.tlang` file is invisible to the build | Nothing `use`s it. Add `use Folder.FileName` in the file that needs it. |

---

## Imports & Language Tags

| Mistake | Fix |
|---------|-----|
| `lang [kotlin]` — unknown language `kotlin` | Add `use KotlinGen as kotlin` at the top of the file. |
| `data [html]` — unknown template language error | `data` needs no generator import. Use `data [html]` not `lang [html]`. |
| `doc [md]` — unknown template language error | `doc` needs no generator import. Use `doc [md]` not `lang [md]`. |
| `style [css]` — unknown template language error | `style` needs no generator import. Use `style [css]` not `lang [css]`. |
| Importing a non-main file from a manifest-backed package | Only the `main:` file is importable. Target the package alias in your `use` statement. |
| Calling a non-exposed symbol from another package | Add the symbol to `expose` in the generator's main file. |

---

## Templates — `lang`

| Mistake | Fix |
|---------|-----|
| `${name}` substituted but you wanted it literal | Write `$${name}` — `$$` produces a literal `$` after substitution. |
| Line starting with `impl`, `func`, `return`, etc. mis-parsed as TLang directive | Prefix with `raw:` — e.g. `raw: return someValue`. |
| `<[ … ]>` treated as an include when you want it literal | Prefix the line with `raw:`. |

---

## Templates — `data`

| Mistake | Fix |
|---------|-----|
| `data` body keywords (`impl`, `func`) treated as directives | They are tag/key names inside `data`, not directives. Only the top-level `data` keyword has special meaning. |
| Nested HTML elements not producing children | Write child elements as positional nested blocs: `body { div { … } }`. Use `<[ renderFn(list) ]>` for dynamic children. |

---

## Templates — `raw`

| Mistake | Fix |
|---------|-----|
| `raw [AsIs]` — `${param}` is still written literally when you wanted substitution | Switch to `raw [Replaced]`. `AsIs` never substitutes. |
| `raw [Replaced]` — `${…}` appears in the output when you wanted it literal | Switch to `raw [AsIs]`. `Replaced` always substitutes `${…}`. |

---

## Templates — `style`

| Mistake | Fix |
|---------|-----|
| CSS at-rules (`@media`, `@keyframes`) not processed | At-rules are passed through verbatim as selector strings. Use `raw [Replaced]` for full at-rule bodies. |
| SCSS/LESS nesting not working | SCSS/LESS output is currently identical to CSS. Use `raw [Replaced]` for nesting/variables. |

---

## Helper Expressions

| Mistake | Fix |
|---------|-----|
| `main` function missing a return type | Add `: String` (or the appropriate type) after the parameter list. |
| Expression `if` missing `else` | Expression-form `if` always requires an `else` branch. Statement-form does not. |
| Expression `match` missing `default` | `default` is mandatory in expression `match`. Add `default => <value>`. |
| `?.` causing unexpected `null` | `?.` returns `null` when any step is `null`. Add `?? fallback` to provide a default: `val?.field ?? "default"`. |

---

## File I/O

| Mistake | Fix |
|---------|-----|
| Output file at wrong path (e.g. `MyProject/output/…`) | Remove the project directory name. Write `"output/Main.kt"` — the runtime already `cd`s to the project root. |
| File silently skipped / not written | `File.write` skips existing files by default. Pass `true` as the third arg to always overwrite. |
| User edits erased on re-run | You are calling `File.write(path, content, true)` on a user-owned file. Remove the `true` flag. |
| Fully generated file edited by user and then overwritten | Add a `DO NOT EDIT` banner at the top of fully-generated files so users know not to edit them. |

---

## CLI

| Mistake | Fix |
|---------|-----|
| Running before compiling | Use `tlang both` instead of `tlang run` on a fresh project. |
| `tlang package` fails — no manifest found | `package` requires a `manifest.yml` in the target directory. |
| `--in-memory` flag not accepted | `--in-memory` is only valid with `both` / `compile-run`, not `compile` or `run` alone. |

---

## Set Attribute Values — Type References vs Strings

| Mistake | Fix |
|---------|-----|
| `entity: "PackageEntity"` — quoted string for a set/type reference | Use unquoted identifier: `entity: PackageEntity`. Both resolve to the same string at runtime, but unquoted marks it as a type reference for future compile-time checking. |
| `type: "StringType"` — quoted Forge type name | `type: StringType` (unquoted). Forge type names (`StringType`, `LongType`, `BoolType`, `UuidType`, etc.) are set references, not plain strings. |
| `codeAdapter: "QuarkusKotlin"` — adapter declared as a quoted string | `codeAdapter: QuarkusKotlin` (unquoted). Adapters must be referenced as types so `Forge.verifyAdapter` can find them in the model at generation time. |
| Adapter package has no self-declaration set | Add `expose MyAdapter` and `set MyAdapter { kind: "adapter", generateX: "impl", ... }` to the adapter's main file. Without this, `Forge.verifyAdapter` cannot confirm the adapter is registered and will report an error. |
| `data: "MarketData"`, `infra: "MarketInfra"` — generator sub-sets as strings | Use unquoted: `data: MarketData`, `infra: MarketInfra`. Sub-generator attributes in `ForgeProject` are set references, not arbitrary strings. |

**Rule**: If the value names a TLang `set`, write it **unquoted** (type reference). If the value is plain data — a URL, a class name, a space-separated list, a technology hint like `"quarkus"` — keep it **quoted** (string literal).

---

## lead / spec Composition

| Mistake | Fix |
|---------|-----|
| Multiple class wrappers in `generateAll()` output | All top-level templates emit their own outer wrapper. Add a `lead` attr owning the wrapper; mark others `spec`. |
| `<[ attrs() ]>` expands to empty string | `<[ attrs() ]>` only populates when called via `generateAll()`. Direct `inst.lead()` calls expand it to empty. |
| Child set attrs not appearing in output | Child `set` must declare `spec` attrs; `generateAll()` on the child includes them automatically. |