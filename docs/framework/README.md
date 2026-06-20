# TLang Framework Architecture

This section documents the architecture for using TLang as a multi-project
code generation platform — for companies that want to generate complete
production projects from high-level declarations.

## Documents

| File | Content |
|---|---|
| [01-layers.md](./01-layers.md) | The four-layer model and the responsibility of each layer |
| [02-project-model.md](./02-project-model.md) | Technology-agnostic vocabulary for describing a project (`set` declarations) |
| [03-technology-implementations.md](./03-technology-implementations.md) | How abstract `set` bases are extended for specific technologies |
| [04-adapter-contract.md](./04-adapter-contract.md) | The adapter contract: one package per technology, a fixed set of hooks |

## The core idea

A company writes its **stack once** (Layer 3). Every project after that is a
short set of `set` declarations (Layer 4) that describe the domain — entities,
screens, security, deployment — without knowing how any of it is generated.

The stack reads the declarations and produces a complete, runnable project:
backend code, frontend pages, infrastructure config, CI/CD pipeline.

When the stack evolves (new framework version, updated design system, changed
CI conventions), regenerating all projects brings them in sync automatically.

## Key TLang concepts used

| Concept | Role |
|---|---|
| `set X { ... }` | Declares a named type or configuration |
| `set X : Y { ... }` | Extends a base type — the inheritance mechanism |
| `set X(p: T) { ... }` | Parameterised set — called like a constructor |
| `expose name` | Makes a function or set available to importing packages |
| `use Pkg as alias` | Imports a package — the adapter selection point |
| `func name(...): T { }` | Generation logic that reads sets and produces output |
| `Leaf.model()` / `Leaf.get()` | Runtime introspection of the declared set tree |
