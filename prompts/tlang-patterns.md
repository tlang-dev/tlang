# TLang — Patterns & Best Practices

> Topic file. Load `tlang-index.md` first for routing + cheat-sheets.

---

## 1. Compose with `<[ call ]>` Includes

The most powerful TLang technique. A `spec` sub-template produces a fragment;
the parent splices it in at the `<[ call ]>` site.

```
// Bad — fixed number of fields, breaks for variable data
lang [kotlin] dataClass(cls: String, f1: String, f2: String) {
    impl[public class] ${cls} {
        var ${f1}
        var ${f2}
    }
}

// Good — composable
lang [kotlin] field(decl: String) spec {
    var ${decl}
}

lang [kotlin] dataClass(cls: String, fields: List) {
    impl[public class] ${cls} {
        <[ renderFields(fields) ]>
    }
}

helper {
    func renderFields(fields: List): List {
        let nodes = List.create()
        for (f in fields) {
            let nodes = List.push(nodes, field(f))
        }
        return nodes
    }
}
```

**Rules:**
- Mark fragment templates `spec` — they produce inner content, not complete files.
- Name list-building helpers with a `render` prefix (`renderFields`, `renderMethods`).
- Keep each `spec` template focused on one element.

---

## 2. Split Code Across Files

`Main.tlang` should only contain `use` imports and `main`. Everything else
goes in focused files.

```
my-generator/
├── manifest.yml            # ONE manifest — project root only
├── Main.tlang              # use + main only
├── templates/
│   ├── EntityTemplate.tlang
│   └── ServiceTemplate.tlang
├── helpers/
│   └── RenderHelpers.tlang
├── models/
│   └── DomainModels.tlang
└── output/                 # ONE output dir — project root only
```

### Local imports — no manifest entry needed

```
use EntityTemplate              // same dir  → EntityTemplate.tlang
use helpers.RenderHelpers       // one level → helpers/RenderHelpers.tlang
use models.DomainModels         // one level → models/DomainModels.tlang
```

- Max depth: **one subfolder level**. `use a.b.Deep` is rejected.
- No `manifest.yml`, no `file://`, no sub-manifest needed in subfolders.
- Use PascalCase filenames matching what they expose.

### Exposing symbols

```
// helpers/RenderHelpers.tlang
expose renderFields
expose renderMethods

helper {
    func renderFields(fields: List): List { … }   // public
    func internalUtil(): String { … }              // private — not exposed
}
```

Only expose what other files need.

---

## 3. Keep Templates Thin

Templates should contain **only target-language structure**. All decisions
belong in helper functions.

```
// Bad — logic leaking into template
lang [kotlin] entity(cls: String, pkg: String) {
    pkg ${pkg}
    impl[public class] ${cls}Repository { … }   // caller must append "Repository"
}

// Good — logic in helper, template is declarative
lang [kotlin] entity(cls: String, pkg: String) {
    pkg ${pkg}
    impl[public class] ${cls} { … }
}

helper {
    func generateRepo(base: String, pkg: String): String {
        let name = base + "Repository"
        return Generator.generate(entity(name, pkg))
    }
}
```

---

## 4. Use `model` for All Static Data

Don't hardcode strings in helpers. Put domain data in `model` blocks.

```
model {
    set User {
        package:    "com.example",
        class_name: "User",
        extends:    "BaseEntity"
    }
    set Product {
        package:    "com.example",
        class_name: "Product",
        extends:    "BaseEntity"
    }
}

helper {
    func main(): String {
        let pkg = User.package        // direct dot access — preferred
        let cls = User.class_name
        …
    }
}
```

Use `Leaf.get` only when the entity name is dynamic (e.g. iterating all
entities). Prefer direct dot access when the name is known at write time.

Move large model blocks to `models/DomainModels.tlang` and `use models.DomainModels`.

---

## 5. One Output File per Artefact — Choose the Right Write Mode

| Intent | Call | Third arg |
|--------|------|-----------|
| Fully generated — regenerate every run | `File.write(path, content, true)` | `true` |
| Boilerplate — write once, user edits | `File.write(path, content)` | _(omit)_ |

### Always-overwrite (add `DO NOT EDIT` banner)

```
helper {
    func header(): String {
        return "// GENERATED FILE — DO NOT EDIT\n" +
               "// Regenerated automatically. Manual changes will be lost.\n\n"
    }
    func main(): String {
        File.write("output/User.kt",    header() + generateEntity("User"),   true)
        File.write("output/UserRepo.kt", header() + generateRepo("User"),    true)
        return "done"
    }
}
```

### Scaffold-once

```
File.write("output/Application.kt", generateMain())   // skips if already exists
```

### Conditional

```
if (File.exists("output/config.yml")) {
    Terminal.println("Config present — skipping")
} else {
    File.write("output/config.yml", defaultConfig())
}
```

**Path rules:**
- Always relative to the project root (directory of `manifest.yml`).
- Never include the project directory name: `"output/User.kt"` not `"my-project/output/User.kt"`.
- Parent directories are created automatically.

---

## 6. Use Expression Forms for Concise Helpers

Prefer expression `if` / `match` over verbose statement chains.

```
// Verbose
func sign(n): String {
    if (n < 0) { return "neg" }
    return "pos"
}

// Concise
func sign(n): String {
    return if (n < 0) "neg" else "pos"
}

let grade = if (score >= 90) "A" else if (score >= 80) "B" else "C"

let mime = match (ext) {
    case "ts"   => "text/typescript",
    case "json" => "application/json",
    default     => "text/plain"
}
```

Use `?.` / `??` instead of manual null checks:

```
let name = user?.profile?.displayName ?? "Anonymous"
let port = config?.server?.port ?? 8080
```

---

## 7. Name Templates After What They Generate

| ✗ Vague | ✓ Clear |
|---------|---------|
| `template1` | `entityClass` |
| `gen(x)` | `repositoryInterface` |
| `block(data)` | `serviceMethod` |
| `render(node)` | `fieldDeclaration` |

Template names appear in auto-generated model keys (`entityClass_1`, …).
Clear names make debugging much easier.

---

## 8. Lists

```
// Small, fixed — prefer List.of
let imports = List.of("java.util.List", "java.util.UUID")

// Dynamic — List.create + List.push
func collectFields(attrs): List {
    let result = List.create()
    for (attr in attrs) {
        let result = List.push(result, attr)
    }
    return result
}
```

Never pass a bare string where a `List` is expected.

---

## 9. Package Generators for Reuse

```yaml
# manifest.yml
name: KotlinGen
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

Consumers: one manifest line + `use KotlinGen as kotlin`.

---

## 10. Lead + Spec: Combine Templates into One File

When a `set` groups templates that must land in a **single output file**, use
a `lead` attr for the outer wrapper and `spec` templates for inner fragments.

### The problem without lead

Every attr generates a self-contained output → multiple class wrappers →
invalid code.

### Solution

```
// Lead — owns the outer class wrapper
lang [kotlin] classShell(pkg: String, cls: String, rec: String) {
    pkg ${pkg}
    impl[public class] ${cls} {
        <[ attrs() ]>
    }
}

// Spec — inner fragment only
lang [kotlin] saveMethod(pkg: String, cls: String, rec: String) spec {
    fun save(r: ${rec}): ${rec} { return repository.save(r) }
}

lang [kotlin] updateMethod(pkg: String, cls: String, rec: String) spec {
    fun update(r: ${rec}): ${rec} { return repository.update(r) }
}

model {
    set BasicRepo(pkg: String, cls: String, rec: String)
            >> "output/${cls}.kt" {
        lead:   &classShell,
        save:   &saveMethod,
        update: &updateMethod
    }
}

helper {
    func main(): String {
        let repo = BasicRepo(
            impl pkg: "com.example",
            impl cls: "DocumentRepo",
            impl rec: "Document"
        )
        repo.generateAll()
        return "done"
    }
}
```

`generateAll()` renders every non-lead attr, concatenates the fragments,
injects them into `<[ attrs() ]>`, and writes to the `>>` path.

### Inheritance

```
model {
    set AuditedRepo : BasicRepo >> "output/audited/${cls}.kt" {
        audit: &auditMethod     // adds to lead + save + update
    }
}
```

### Rules

| Rule | Detail |
|------|--------|
| Lead attr name | Must be exactly `lead` |
| Lead template | Owns file-level wrapper (pkg, class header, closing brace) |
| Non-lead templates | Declare `spec` — inner fragments only |
| Params | All templates in the set must accept the same constructor params |
| Write path | Declare `>>` on the set so `generateAll()` knows where to write |

### When to use

| Scenario | Recommendation |
|----------|----------------|
| All attrs belong in one file | `lead` + `spec` |
| Each attr is its own standalone file | No `lead` — separate attrs |
| Outer structure shared across a parent chain | `lead` on parent; children add `spec` attrs |

---

## 11. Quick Checklist

- [ ] `Main.tlang` contains only `use` imports and `main` — no templates or models?
- [ ] Templates split into focused files under `templates/`?
- [ ] Helper utilities in `helpers/`? Domain data in `models/`?
- [ ] Each `spec` template does exactly one thing?
- [ ] When a set combines templates into one file: `lead` attr + `spec` templates + `<[ attrs() ]>` + `generateAll()`?
- [ ] `File.write(path, content, true)` + `DO NOT EDIT` banner for fully-generated files?
- [ ] `File.write(path, content)` (no third arg) for user-editable scaffold files?
- [ ] `File.write` paths relative to project root — no project directory name prefix?
- [ ] Only `expose` what external callers need — internal helpers stay private?
- [ ] Target-language `${…}` escaped as `$${…}` inside `lang` bodies?
- [ ] Lines that must start with a TLang keyword but be emitted verbatim prefixed with `raw:`?
- [ ] Using `data [fmt]` for HTML/JSON/YAML/XML instead of `lang`?
- [ ] Using `doc [fmt]` for documents instead of string concatenation?
- [ ] Using `style [fmt]` for CSS/tokens instead of string concatenation?
- [ ] Using `raw [AsIs]` or `raw [Replaced]` for verbatim content instead of `lang`?
- [ ] Expression `if` / `match` used where statement chains would be wordier?
- [ ] `?.` / `??` used instead of manual null checks on optional fields?