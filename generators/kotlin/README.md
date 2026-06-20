# TLangGen / Kotlin Generator

A TLang code generator that converts `lang [kotlin]` template blocks into
formatted Kotlin source files.

- **Package:** `TLangGen/KotlinGen/Kotlin`
- **Version:** 1.0.0-alpha
- **License:** Apache-2.0

---

## Installation

Add the dependency to your `manifest.yml`:

```yaml
dependencies:
  - TLangGen/KotlinGen/Kotlin:1.0.0
```

Then import it in your `.tlang` file:

```tlang
use KotlinCodegen as kotlin
```

---

## How it works

The generator operates in two stages:

1. **Template instantiation** — you write a `lang [kotlin]` template block and
   call it with a `set` model or plain arguments. The TLang runtime records the
   call and stores the result as a `TmplLang` value in the model.

2. **Code generation** — you retrieve the recorded instance from the model and
   pass it to `Generator.generate(instance, "kotlin")`. The generator walks the
   AST produced by the template and emits properly indented, spaced Kotlin source.

```tlang
use TLang.Generator
use TLang.Leaf
use KotlinCodegen as kotlin

lang [kotlin] myService(pkg: String) {
    pkg ${pkg}
    impl[open class] MyService {
        func doWork(): String {
            return "done"
        }
    }
}

func main(): String {
    myService("com.example")
    let model    = Leaf.model()
    let instance = Leaf.get(model, "myService_1")
    return Generator.generate(instance, "kotlin")
}
```

---

## Template reference

All constructs are written inside a `lang [kotlin] templateName(...) { }` block.

### Package declaration

```tlang
lang [kotlin] example(pkg: String) {
    pkg ${pkg}
    // ... rest of the file
}
```

Emits `package com.example` followed by a blank line. Omit the `pkg` line to
generate a file with no package header.

---

### Classes

Use `impl[modifier]` to declare any Kotlin class-like type.

| Template syntax | Kotlin output |
|---|---|
| `impl[class] Foo` | `class Foo` |
| `impl[data class] Point` | `data class Point` |
| `impl[open class] Base` | `open class Base` |
| `impl[abstract class] Base` | `abstract class Base` |
| `impl[sealed class] Result` | `sealed class Result` |
| `impl[value class] Wrapper` | `value class Wrapper` |
| `impl[annotation class] Route` | `annotation class Route` |
| `impl[private class] Helper` | `private class Helper` |
| `impl[internal class] Cache` | `internal class Cache` |
| `impl[private abstract class] Repo` | `private abstract class Repo` |
| `impl[interface] Loadable` | `interface Loadable` |
| `impl[sealed interface] Shape` | `sealed interface Shape` |
| `impl[fun interface] Action` | `fun interface Action` |
| `impl[enum class] Status` | `enum class Status` |
| `impl[object] Singleton` | `object Singleton` |
| `impl[companion object]` | `companion object` |

A class-level annotation is emitted on the line before the class declaration
when provided as a template parameter or written as a plain `@Annotation` line:

```tlang
lang [kotlin] entity(pkg: String, name: String) {
    pkg ${pkg}
    @Entity
    impl[open class] ${name} {
        // ...
    }
}
```

---

### Functions

```tlang
func greet(who: String): String {
    return "Hello, " + who
}
```

Add a modifier prefix in brackets to emit visibility or behavioural keywords:

| Template syntax | Kotlin output |
|---|---|
| `func[override] onStart()` | `override fun onStart()` |
| `func[private] helper(): Int` | `private fun helper(): Int` |
| `func[private suspend] load(): T` | `private suspend fun load(): T` |
| `func[inline] map(f: (T) -> R): R` | `inline fun map(f: (T) -> R): R` |
| `func[operator] plus(other: V): V` | `operator fun plus(other: V): V` |
| `func[open override] process()` | `open override fun process()` |

**Abstract / interface declarations** — a `func` with no body emits an abstract
signature:

```tlang
impl[abstract class] Repo {
    func[abstract] findAll(): List<Entity>
}
```

**Single-expression functions** — include the `= expr` tail directly in the
signature:

```tlang
func add(a: Int, b: Int): Int = a + b
```

Recognised modifiers (combinable with spaces):
`private` `protected` `internal` `override` `abstract` `open` `suspend`
`inline` `infix` `operator` `tailrec` `external` `crossinline` `noinline`
`reified`

---

### Properties

Use the TLang `var` keyword for simple declarations inside a class body:

```tlang
var id: Long? = null
var [val] name: String = ""
```

`var [val]` emits a Kotlin `val`.

For declarations that need explicit modifiers write the full line as a plain
expression and the generator will tokenise it automatically:

```tlang
private var count: Int = 0
private lateinit var user: User
override val size: Int
const val TAG: String = "MyClass"
protected open var title: String = ""
```

---

### Annotations

Prefix any line with `@` and it is emitted as an annotation on its own line:

```tlang
impl[open class] BookmarkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(nullable = false)
    lateinit var title: String
}
```

---

### Imports

Write `import fully.qualified.Name` anywhere in the template body:

```tlang
lang [kotlin] service(pkg: String) {
    pkg ${pkg}
    import jakarta.inject.Singleton
    import jakarta.transaction.Transactional
    impl[open class] MyService {
        // ...
    }
}
```

---

### Control flow

```tlang
for (item in items) {
    item.process()
}

while (queue.isNotEmpty()) {
    queue.poll().run()
}

do {
    val line = reader.readLine()
} while (line != null)

if (x > 0) {
    return x
} else if (x < 0) {
    return -x
} else {
    return 0
}
```

---

### Return and throw

```tlang
return result
throw IllegalArgumentException("bad input")
```

---

### Type aliases

```tlang
typealias Callback = (String) -> Unit
```

---

### Comments

```tlang
comment This is rendered as a Kotlin line comment
```

Emits: `// This is rendered as a Kotlin line comment`

---

### Raw output

```tlang
raw: val x: Map<String, List<Int>> = emptyMap()
```

Passes the text through the formatter unchanged — useful for complex generic
types or expressions that the template parser would otherwise misinterpret.

---

## `set` model pattern

The `set` block binds named attribute values to a template. The generator
retrieves the resulting instance from `Leaf.model()`:

```tlang
use TLang.Generator
use TLang.Leaf
use KotlinCodegen as kotlin

lang [kotlin] entity(pkg: String, className: String, extends: String, annotation: String) {
    pkg ${pkg}
    ${annotation}
    impl[open class] ${className} : ${extends} {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null
    }
}

set MyEntity {
    package:    "com.example.domain",
    class_name: "MyEntity",
    extends:    "PanacheEntityBase",
    annotation: "@Entity"
}

func generate(): String {
    let model    = Leaf.model()
    let entity   = Leaf.get(model, "MyEntity")
    let attrs    = Leaf.get(entity, "attrs")
    let pkg      = Leaf.get(Leaf.get(attrs, "package"),    "value")
    let cls      = Leaf.get(Leaf.get(attrs, "class_name"), "value")
    let ext      = Leaf.get(Leaf.get(attrs, "extends"),    "value")
    let ann      = Leaf.get(Leaf.get(attrs, "annotation"), "value")
    entity(pkg, cls, ext, ann)
    let model2   = Leaf.model()
    let instance = Leaf.get(model2, "entity_1")
    return Generator.generate(instance, "kotlin")
}
```

---

## Complete example

The following reproduces a JPA entity with several field types and a companion
object:

```tlang
use TLang.Generator
use TLang.Leaf
use KotlinCodegen as kotlin

lang [kotlin] bookmark(pkg: String, className: String, extends: String, annotation: String) {
    pkg ${pkg}
    impl[open class] ${className} : ${extends} {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null
        @Column(unique = true, nullable = false)
        var code: String = UUID.randomUUID().toString()
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        lateinit var user: User
        @Column(nullable = false, length = 300)
        lateinit var title: String
        @Column(nullable = false, length = 2000)
        lateinit var url: String
        @Column(length = 1000)
        var description: String? = null
        @Column(name = "create_date", nullable = false)
        var createdDate: LocalDateTime? = null
        @Column(name = "update_date")
        var updatedDate: LocalDateTime? = null
        @Column
        var favorite: Boolean = false
        impl[companion object] {
            func findByCode(code: String): ${className}? = find("code", code).firstResult()
        }
    }
}

set Bookmark {
    package:    "com.example.bookmark",
    class_name: "Bookmark",
    extends:    "PanacheEntityBase",
    annotation: "@Entity"
}

func main(): String {
    let model    = Leaf.model()
    let entity   = Leaf.get(model, "Bookmark")
    let attrs    = Leaf.get(entity, "attrs")
    let pkg      = Leaf.get(Leaf.get(attrs, "package"),    "value")
    let cls      = Leaf.get(Leaf.get(attrs, "class_name"), "value")
    let ext      = Leaf.get(Leaf.get(attrs, "extends"),    "value")
    let ann      = Leaf.get(Leaf.get(attrs, "annotation"), "value")
    bookmark(pkg, cls, ext, ann)
    let model2   = Leaf.model()
    let instance = Leaf.get(model2, "bookmark_1")
    return Generator.generate(instance, "kotlin")
}
```

Generated output:

```kotlin
package com.example.bookmark

@Entity
open class Bookmark : PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(unique = true, nullable = false)
    var code: String = UUID.randomUUID().toString()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User
    @Column(nullable = false, length = 300)
    lateinit var title: String
    @Column(nullable = false, length = 2000)
    lateinit var url: String
    @Column(length = 1000)
    var description: String? = null
    @Column(name = "create_date", nullable = false)
    var createdDate: LocalDateTime? = null
    @Column(name = "update_date")
    var updatedDate: LocalDateTime? = null
    @Column
    var favorite: Boolean = false
    companion object {
        fun findByCode(code: String): Bookmark? = find("code", code).firstResult()
    }
}
```

---

## Compatibility

This generator targets Kotlin and is compatible with any language-agnostic
generator (`language: '*'`). It is incompatible with generators that target
a different language.
