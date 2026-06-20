# The Four-Layer Model

TLang project generation is organised into four layers. Each layer has a single
responsibility. Higher layers depend on lower ones; lower layers know nothing
about higher ones.

```
┌─────────────────────────────────────────────────────────────┐
│  Layer 4 — Project Wiring                                   │
│  Per project. Declares what the project is.                 │
├─────────────────────────────────────────────────────────────┤
│  Layer 3 — Company Stack                                    │
│  Per company. Assembles and constrains Layer 2.             │
├─────────────────────────────────────────────────────────────┤
│  Layer 2 — Framework Adapters                               │
│  Per technology. Implements abstract components.            │
├─────────────────────────────────────────────────────────────┤
│  Layer 1 — Language Primitives                              │
│  Universal. KotlinGen, HtmlGen, StyleGen, …                 │
└─────────────────────────────────────────────────────────────┘
```

---

## Layer 1 — Language Primitives

**Owned by:** TLang core
**Scope:** Universal
**Changes:** Rarely

Language primitives generate raw output in a specific language or format.
They carry no knowledge of frameworks, business concepts, or project structure.

Examples: `KotlinGen`, `HtmlGen`, `StyleGen`, `YamlGen`, `SqlGen`

A primitive knows how to render a Kotlin class, an HTML element, a CSS rule.
It does not know what a `User` entity is or what a `Dashboard` screen means.

---

## Layer 2 — Framework Adapters

**Owned by:** Community, vendors, or the company
**Scope:** One technology stack
**Changes:** When the framework evolves

A framework adapter maps abstract project components (see
[02-project-model.md](./02-project-model.md)) onto a concrete technology.
One adapter package exists per `backend:language` pair.

```
adapters/
  quarkus-kotlin/
  spring-java/
  spring-kotlin/
  django-python/
  rails-ruby/
  express-typescript/
```

Each adapter fulfils the same **adapter contract** — a fixed set of `expose`d
functions that the company stack calls. The contract is documented in
[04-adapter-contract.md](./04-adapter-contract.md).

An adapter imports Layer 1 primitives to do the actual rendering. It does not
know which company is using it or what business domain the project covers.

---

## Layer 3 — Company Stack

**Owned by:** The company
**Scope:** All projects within the company
**Changes:** When company conventions evolve

A company stack wraps one framework adapter and applies company-specific
conventions on top:

- Which framework adapter to use (one import, one decision)
- The company's authentication model (Keycloak, custom JWT, SAML…)
- The company's design system components
- The company's Kubernetes conventions (resource limits, label taxonomy…)
- The company's CI/CD pipeline template
- Approved infrastructure services and their default configuration

The company stack is a TLang package published to a private PkgMarket
instance. It is the primary investment a company makes in TLang. Once stable,
individual projects never touch framework or infrastructure details.

A company stack selects an adapter and delegates to it, overriding only what
the company's conventions require:

```tlang
use QuarkusKotlinAdapter as adapter
use acme.security.KeycloakSetup
use acme.design.Components

func generate(project: String): String {
    adapter.generateInfrastructure(project)
    adapter.generateEntities(project)
    KeycloakSetup.generate(project)     // overrides adapter's default auth
    adapter.generateScreens(project, Components)  // injects design system
    return "ok"
}
```

---

## Layer 4 — Project Wiring

**Owned by:** The project team
**Scope:** One project
**Changes:** As the project evolves

The project wiring declares *what the project is*, not *how to generate it*.
It uses the abstract component vocabulary (see
[02-project-model.md](./02-project-model.md)) and hands off to the company
stack.

```tlang
use AcmeStack as stack

set UserService : ProjectBase {
    name:      "UserService",
    package:   "com.acme.users",
    outputDir: "output",
    backend:   "quarkus",
    language:  "kotlin"
}

func main(): String {
    return stack.generate("UserService")
}
```

`main()` is the only imperative code a project team writes. Everything else is
a `set` declaration that the stack reads and interprets.

---

## Responsibility summary

| Layer | Who writes it | What it knows | What it must not know |
|---|---|---|---|
| 1 — Primitives | TLang core | Language syntax | Frameworks, business concepts |
| 2 — Framework Adapters | Community / vendors | One tech stack | Company conventions, business domain |
| 3 — Company Stack | The company | Company conventions + chosen adapter | Individual project domains |
| 4 — Project Wiring | Project team | This project's domain | How anything is generated |

---

## Token economy

The layering is the token economy. An AI working on Layer 4 only needs the
project declaration in context — typically 50–150 lines. It never needs to
read Layer 1 or 2 source unless the task is about those layers.

An AI working on Layer 3 reads the company stack files and the adapter
contract, not individual project declarations.

Narrow context per task is the direct result of clean layer separation.
