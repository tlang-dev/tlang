# TLang — Quick Index

TLang is a custom DSL for code generation. Programs declare templates and
models; the runtime generates source files from them. It is not TypeScript,
Kotlin, or any mainstream language — read the relevant topic file before
writing any TLang code.

---

## Load which file for what task?

| Task | Load |
|------|------|
| Project setup, manifest, imports, CLI, packaging | `tlang-project.md` |
| Writing `lang [alias]` code generation templates | `tlang-lang.md` |
| Writing `data`/`doc`/`style`/`raw` templates | `tlang-templates.md` |
| Helper language — functions, `if`/`match`, `?.`, lambdas, model | `tlang-helpers.md` |
| Standard libraries — Generator, File, List, Leaf, MCPTool | `tlang-builtins.md` |
| Best practices — file structure, lead/spec, I/O patterns, checklist | `tlang-patterns.md` |
| Common mistakes and fixes | `tlang-mistakes.md` |

For ordinary code generation tasks load `tlang-lang.md` + `tlang-helpers.md`.
Add `tlang-builtins.md` when you need `File`, `List`, `Leaf`, or `MCPTool`.

---

## Template type cheat-sheet

| Keyword | Generates | Generator import? |
|---------|-----------|-------------------|
| `lang [alias]` | Source code — Kotlin, Java, Go, TS, … | **Yes** — `use Gen as alias` |
| `data [fmt]` | Hierarchical data — HTML, JSON, YAML, XML, TOML | No |
| `doc [fmt]` | Documents — Markdown, HTML | No |
| `style [fmt]` | Styles — CSS, SCSS, LESS, JSON tokens, JS/TS objects | No |
| `raw [AsIs]` | Verbatim text, `${…}` **not** substituted | No |
| `raw [Replaced]` | Verbatim text with `${param}` substitution | No |

Only `lang` needs a generator import. All other template types are built-in.

---

## Expression quick-ref

| Feature | Example |
|---------|---------|
| Expression `if` (needs `else`) | `let x = if (a > 0) "pos" else "non-pos"` |
| Expression `match` (needs `default`) | `let s = match (v) { case 1 => "one", default => "?" }` |
| Optional chain | `user?.profile?.name` → `null` if any step is `null` |
| Null-coalesce | `user?.name ?? "anon"` |
| Lambda | `let f = (x) => x * 2` |
| Function ref | `let f = &myFunc` |

---

## Escaping quick-ref (`lang` template bodies)

| Goal | Write |
|------|-------|
| Literal `$` in output | `$$` |
| Literal `${name}` — no substitution | `$${name}` |
| Emit a TLang keyword verbatim (`return`, `impl`, …) | `raw: return …` |
| Emit literal `<[ … ]>` | `raw: <[ … ]>` |
| Suppress all substitution on a line | `raw: …` |

---

## File write modes

| Mode | Call | When to use |
|------|------|-------------|
| Scaffold once | `File.write(path, content)` | User-editable boilerplate — never overwritten |
| Always overwrite | `File.write(path, content, true)` | Fully generated — add `DO NOT EDIT` banner |
| Conditional | `File.exists(path)` → branch | Vary behaviour on first vs subsequent runs |

Paths are **relative to the project root** (directory of `manifest.yml`).
Never include the project directory name in the path.