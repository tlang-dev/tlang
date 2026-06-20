# Adapter Contract (Option 1)

## The problem

Every abstract component (Postgres, Minio, JWT, Kubernetes…) must produce
completely different output depending on the backend technology:

```
           quarkus/kotlin  spring/java  spring/kotlin  django/python  rails/ruby
Postgres        ✓              ✓              ✓              ✓            ✓
Minio           ✓              ✓              ✓              ✓            ✓
Redis           ✓              ✓              ✓              ✓            ✓
JWT             ✓              ✓              ✓              ✓            ✓
K8s manifests   ✓              ✓              ✓              ✓            ✓
```

The naive approach — one package per cell — creates N×M packages and collapses
under its own weight. The **adapter contract** solves this by inverting the
organisation: one package per *column* (one per technology), each implementing
the same fixed interface.

---

## The solution: one adapter per technology pair

One adapter package exists for each `backend:language` combination:

```
adapters/
  quarkus-kotlin/     ← knows how to generate everything for Quarkus + Kotlin
  spring-java/        ← knows how to generate everything for Spring + Java
  spring-kotlin/      ← knows how to generate everything for Spring + Kotlin
  django-python/      ← knows how to generate everything for Django + Python
  rails-ruby/         ← knows how to generate everything for Rails + Ruby
  express-typescript/ ← knows how to generate everything for Express + TypeScript
```

Each adapter package fulfils the same **adapter contract** — a fixed set of
`expose`d functions. The company stack imports exactly one adapter and calls
the contract functions. Swapping the technology means changing one `use`
statement.

---

## The contract declaration

The contract is a `set` that documents what every adapter must expose, plus
`func` accessors that tooling can query at runtime.

```tlang
// contract/AdapterContract.tlang
expose adapterHooks
expose infraHooks
expose allHooks

set AdapterContract {
    kind: "contract",
    pattern: "adapter",
    adapter_hooks: "generateEntity generateCrud generateScreen generateSecurity generatePipeline",
    infra_hooks:   "generateDatabase generateStorage generateCache generateQueue generateEmail"
}

func adapterHooks(): List {
    return L.of(
        "generateEntity",
        "generateCrud",
        "generateScreen",
        "generateSecurity",
        "generatePipeline"
    )
}

func infraHooks(): List {
    return L.of(
        "generateDatabase",
        "generateStorage",
        "generateCache",
        "generateQueue",
        "generateEmail"
    )
}

func allHooks(): List {
    return L.concat(adapterHooks(), infraHooks())
}
```

---

## What each hook must do

Every adapter must expose the following functions. The signature is fixed;
the implementation is technology-specific.

| Hook | Input | Output |
|---|---|---|
| `generateEntity` | entity set name, project set name | Entity class + repository + service files |
| `generateCrud` | crud set name, project set name | REST resource / controller file |
| `generateScreen` | screen set name, project set name | Frontend page file |
| `generateSecurity` | security set name, project set name | Auth config + middleware files |
| `generatePipeline` | pipeline set name, project set name | CI/CD workflow files |
| `generateDatabase` | database set name, project set name | Datasource config + docker service |
| `generateStorage` | storage set name, project set name | Storage service + docker service |
| `generateCache` | cache set name, project set name | Cache config + docker service |
| `generateQueue` | queue set name, project set name | Queue config + docker service |
| `generateEmail` | email set name, project set name | Email service stub |

---

## Implementing the contract — Quarkus/Kotlin adapter

```tlang
// adapters/quarkus-kotlin/DatabaseAdapter.tlang
expose generateDatabase
use TLang.File
use TLang.Leaf

func generateDatabase(dbName: String, projectName: String): String {
    let kind = Leaf.get(Leaf.get(Leaf.model(), dbName), "kind")
    if (Str.equals(kind, "postgres")) {
        return generatePostgres(dbName, projectName)
    }
    if (Str.equals(kind, "mongo")) {
        return generateMongo(dbName, projectName)
    }
    return "ok"
}

func generatePostgres(dbName: String, projectName: String): String {
    // Reads Postgres set attributes, writes application.yml datasource block
    // and docker-compose postgres service — Quarkus conventions
    let schema  = Leaf.get(Leaf.get(Leaf.get(Leaf.model(), dbName), "attrs"), "schema")
    let poolMax = Leaf.get(Leaf.get(Leaf.get(Leaf.model(), dbName), "attrs"), "poolMax")
    // ... generates quarkus.datasource.* yaml block
    return "ok"
}
```

```tlang
// adapters/quarkus-kotlin/EntityAdapter.tlang
expose generateEntity
use TLangAPI.entity.Entity
use KotlinGen

func generateEntity(entityName: String, projectName: String): String {
    // Reads Entity set, generates Kotlin @Entity class + PanacheRepository
    // using Quarkus/Panache conventions
    return "ok"
}
```

---

## Implementing the contract — Spring/Java adapter

The same hooks, different output:

```tlang
// adapters/spring-java/DatabaseAdapter.tlang
expose generateDatabase

func generateDatabase(dbName: String, projectName: String): String {
    let kind = Leaf.get(Leaf.get(Leaf.model(), dbName), "kind")
    if (Str.equals(kind, "postgres")) {
        return generatePostgres(dbName, projectName)
    }
    return "ok"
}

func generatePostgres(dbName: String, projectName: String): String {
    // Same Postgres set, different output:
    // generates spring.datasource.* in application.properties
    // and @Configuration DataSource bean — Spring conventions
    return "ok"
}
```

```tlang
// adapters/spring-java/EntityAdapter.tlang
expose generateEntity

func generateEntity(entityName: String, projectName: String): String {
    // Reads the same Entity set, generates Java @Entity class
    // + JpaRepository interface — Spring Data JPA conventions
    return "ok"
}
```

---

## Adapter self-declaration

Every adapter package must declare a named set that registers itself in the
Forge model. This is what `Forge.verifyAdapter()` uses to confirm the adapter
is properly wired before any files are written.

```tlang
// adapters/quarkus-kotlin/Main.tlang

expose QuarkusKotlin      // ← must be exposed so Leaf.model() sees it

set QuarkusKotlin {
    kind:                     "adapter",
    // List every hook the adapter implements.
    // Empty string = not yet implemented (will not fail verifyAdapter, but
    // missing hooks produce a runtime error when actually called).
    generateEntity:           "impl",
    generateEnum:             "impl",
    generateEvent:            "impl",
    generateCrud:             "impl",
    generateEndpoint:         "impl",
    generateWebhook:          "impl",
    generateSecurityProvider: "impl",
    generateRole:             "impl",
    generateRoles:            "impl",
    generateDatabase:         "impl",
    generateStorage:          "impl",
    generateCache:            "impl",
    generateQueue:            "impl",
    generateEmail:            "impl",
    generateScreen:           "",
    generateForm:             "",
    generateNav:              ""
}
```

---

## Declaring the adapter in the project

Projects reference adapters as **type references** (unquoted), not strings.
Using the unquoted form lets tooling and future compile-time checks verify the
named set exists and is registered as an adapter.

```tlang
set AcmeProject : ForgeProject {
    name:        "acme-backend",
    package:     "com.acme.backend",
    codeAdapter: QuarkusKotlin,    ← type reference — NOT "QuarkusKotlin"
    data:        AcmeData,
    api:         AcmeApi,
    infra:       AcmeInfra,
    security:    AcmeSecurity
}
```

`Forge.verifyAdapter(adapterName)` is called at the start of generation — it
reads `codeAdapter` from the project set and confirms the named set exists in
the model with `kind` starting with `"adapter"`.

---

## The company stack wires one adapter

```tlang
// acme-stack/Stack.tlang
use adapters.QuarkusKotlin as adapter   // ← the only line that names a technology
use acme.security.KeycloakSetup
use TLang.List as L

func generate(projectName: String): String {
    let entities = projectEntities(projectName)
    for (entity in entities) {
        adapter.generateEntity(entity, projectName)
    }

    let cruds = projectCruds(projectName)
    for (crud in cruds) {
        adapter.generateCrud(crud, projectName)
    }

    // Security is overridden at Layer 3 — Keycloak replaces the adapter default
    KeycloakSetup.generate(projectName)

    adapter.generateDatabase(projectDb(projectName), projectName)
    adapter.generatePipeline(projectPipeline(projectName), projectName)

    return "ok"
}
```

To switch the entire company to Spring/Java:

```tlang
use adapters.SpringJava as adapter   // ← one change, everything follows
```

---

## Adding a new technology

To support a new stack (e.g., Ktor/Kotlin), create one new adapter package:

```
adapters/ktor-kotlin/
  DatabaseAdapter.tlang    ← expose generateDatabase
  EntityAdapter.tlang      ← expose generateEntity
  CrudAdapter.tlang        ← expose generateCrud
  ScreenAdapter.tlang      ← expose generateScreen
  SecurityAdapter.tlang    ← expose generateSecurity
  PipelineAdapter.tlang    ← expose generatePipeline
  StorageAdapter.tlang     ← expose generateStorage
  ...
```

Each file exposes the hook functions from the contract. Nothing in Layer 1,
Layer 3, or Layer 4 changes. The company stack adds one `use` statement.

---

## Contract verification

The `AdapterContract` set is machine-readable. A verification function can
check at generation time that all hooks are present in the loaded adapter:

```tlang
func verifyAdapter(adapterName: String): Bool {
    let hooks = allHooks()
    for (hook in hooks) {
        if (adapterExposes(adapterName, hook) == false) {
            Terminal.println("Adapter " + adapterName + " missing hook: " + hook)
            return false
        }
    }
    return true
}
```

This runs before generation starts and fails fast if an adapter is incomplete,
rather than producing broken output silently.
