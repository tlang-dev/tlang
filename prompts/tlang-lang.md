# TLang — `lang` Templates

> Topic file. Load `tlang-index.md` first for routing + cheat-sheets.

---

## `lang` — Code Generation Templates

Generates target-language source code. Requires an imported generator alias.

```
use KotlinGen as kotlin

lang [kotlin] myClass(pkg: String, cls: String) {
    pkg ${pkg}
    impl[public class] ${cls} {
        fun greet(): String {
            return "Hello!"
        }
    }
}
```

### Directives inside `lang` bodies

| Directive | Purpose |
|-----------|---------|
| `pkg ${pkg}` | Package declaration for the generated file |
| `impl[modifier] Name { … }` | Class / interface / object |
| `func name(params): Type { … }` | Method |
| `var name: Type = value` | Field or variable |
| `if / else / for / while / do` | Control-flow nodes |
| `return expr` | Return statement |
| `comment: text` | Emits a `//` line comment |
| `raw: text` | Emits text verbatim — no substitution, no keyword parsing |
| `<[ call() ]>` | Inline include — splices another template's output here |

`spec` modifier — marks a template as an inner fragment (no outer wrapper):

```
lang [kotlin] saveMethod(pkg: String, cls: String, rec: String) spec {
    fun save(record: ${rec}): ${rec} {
        return repository.save(record)
    }
}
```

### Escaping inside `lang` bodies

| Goal | Write |
|------|-------|
| Literal `$` | `$$` |
| Literal `${name}` — no substitution | `$${name}` |
| Line starting with a TLang keyword verbatim | `raw: return …` |
| Literal `<[ … ]>` without include processing | `raw: <[ … ]>` |
| Suppress all substitution on a line | `raw: …` |

`$$` is resolved *after* `${param}` substitution, so `$${name}` is safe even
when `name` is a declared parameter.