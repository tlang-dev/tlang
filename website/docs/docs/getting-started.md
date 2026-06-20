# Getting Started with TLang

TLang is a compiled template language for structural code generation.
Write a template once — generate every file, across every entity, in milliseconds.
Add a new entity: every template that touches your model picks it up automatically.

## Installation

TLang requires Rust 1.85 or later. Build from source:

```bash
      git clone https://github.com/joel-f/tlang
      cd tlang
      cargo build --release
      cargo install --path .
    
```

Verify:

```bash
      tlang --version
    
```

## Your First Program

Create Main.tlang:

```tlang
      use TLang.Terminal

      func main(): String {
          Terminal.println("Hello, TLang!")
          return "ok"
      }
    
```

Run it:

```bash
      tlang compile-run Main.tlang
    
```

TLang compiles to bytecode on the first run and caches it. Subsequent runs reuse the
cache and skip compilation — only recompiling when source files change.

## Your First Template

A lang template generates code in a target language. Each call to the template
produces one rendered instance:

```tlang
      use TLang.Terminal

      lang [kotlin] entity(pkg: String, name: String) {
          pkg ${pkg}
          impl[data class] ${name}(
              val id: Long,
              val name: String
          )
      }

      func main(): String {
          entity("com.example.model", "User")
          entity("com.example.model", "Product")
          Terminal.println("Generated 2 entities.")
          return "ok"
      }
    
```

The ${pkg} and ${name} placeholders are substituted with the argument values at call time.
Call the template once per entity — TLang handles the rest.

## Binding Templates to Models

Hard-coding calls to entity(...) does not scale. Use set declarations to define your
model data, then iterate over all sets in a loop:

```tlang
      use TLang.File
      use TLang.Generator

      lang [kotlin] entity(pkg: String, name: String) {
          pkg ${pkg}
          impl[data class] ${name}(
              val id: Long,
              val name: String
          )
      }

      set User {
          pkg: "com.example.model",
          name: "User"
      }

      set Product {
          pkg: "com.example.model",
          name: "Product"
      }

      set Order {
          pkg: "com.example.model",
          name: "Order"
      }

      func main(): String {
          for (model in Generator.models()) {
              let code = entity(model.get("pkg"), model.get("name"))
              File.write("src/" + model.get("name") + ".kt", code, true)
          }
          return "ok"
      }
    
```

Adding a new entity is one new set block. Every template that iterates Generator.models()
automatically includes it — no other changes required.

## Generating Documents

The doc template generates structured documents in multiple output formats from a
single definition. Format is selected at call time:

```tlang
      use TLang.File

      doc [md, html] apiDoc(service: String) {
        # ${service} API Reference

        This document is generated from a single template.
        Update the template — every output format updates together.

        [section "endpoints"
          ## Endpoints

          [list "unordered"
            - GET /health — liveness probe
            - GET /api/v1/items — list all items
            - POST /api/v1/items — create a new item
            - DELETE /api/v1/items/:id — remove an item
          ]
        ]
      }

      func main(): String {
          File.write("docs/api.md",   apiDoc("md",   "Inventory"), true)
          File.write("docs/api.html", apiDoc("html", "Inventory"), true)
          return "ok"
      }
    
```

## Generating Stylesheets

The style template generates CSS rules. Property values that contain spaces must be
quoted. Pseudo-classes use the [hover], [focus], [active] modifier syntax:

```tlang
      use TLang.File

      style [css] Theme(accent: String) {
          :root {
              --accent: ${accent},
              --bg: "#0d0d0d",
              --text: "#e8e8e8"
          }
          body {
              background: "var(--bg)",
              color: "var(--text)",
              font-family: "system-ui, sans-serif",
              margin: 0
          }
          .btn {
              background: "var(--accent)",
              padding: "0.65em 1.4em",
              border-radius: 8px
          }
          .btn [hover] {
              opacity: "0.85"
          }
      }

      func main(): String {
          File.write("assets/theme.css", Theme("css", "#7c5cfc"), true)
          return "ok"
      }
    
```

## Multi-file Projects

Split your project into modules. Use expose to export a symbol; use to import it:

```tlang
      // models/User.tlang
      expose userEntity

      lang [kotlin] userEntity(pkg: String) {
          pkg ${pkg}
          impl[data class] User(
              val id: Long,
              val name: String,
              val email: String
          )
      }
    
```

```tlang
      // Main.tlang
      use TLang.File
      use models.User

      func main(): String {
          File.write("src/User.kt", userEntity("com.example"), true)
          return "ok"
      }
    
```

For multi-file projects, add a manifest.yml at the project root:

```yaml
      name: MyProject
      project: MyApp
      organisation: MyOrg
      version: 1.0.0
      stability: stable
      releaseNumber: 1
      main: Main
    
```

Run from the project directory:

```bash
      tlang compile-run Main.tlang
    
```

## Next Steps

- Language Specification — full syntax reference for all language constructs
- Generators — detailed guide to every template type with examples
- Use Cases — how teams use TLang to save AI tokens and enforce consistency

[Language Specification](language-spec.html)[Generators](generators.html)[Use Cases](use-cases.html)