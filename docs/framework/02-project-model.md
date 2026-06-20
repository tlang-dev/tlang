# Abstract Project Model

This document defines the vocabulary for describing a project in TLang.
All concepts here are **technology-agnostic**. They express *what a project
is*, not *how it is built*.

Framework adapters (Layer 2) and company stacks (Layer 3) implement these
abstractions for specific technologies. Project teams (Layer 4) use them
without knowing the implementation.

Each concept is a TLang `set` declaration. Subtypes extend a base `set`
using `: ParentType`. Accessors read their attributes at generation time
via `Leaf.model()` and `Leaf.get()`.

---

## Project

The root `set` a project team declares. Every generated project starts here.

```tlang
set ProjectBase {
    kind:      "project",
    name:      "",
    package:   "",
    outputDir: "output",
    backend:   "",
    frontend:  "",
    language:  ""
}
```

A project declares itself as a subtype:

```tlang
set UserService : ProjectBase {
    name:      "UserService",
    package:   "com.acme.users",
    outputDir: "output",
    backend:   "quarkus",
    language:  "kotlin"
}
```

---

## Data Model

### Entity

A persisted domain object with identity. Fields are declared as a list of
`Field` sets.

```tlang
set Entity(name: String, description: String, fields: List) {
    kind: "entity"
}
```

Usage:

```tlang
let user = Entity("User", "Application user", [
    Field("email",    Email()),
    Field("password", Password()),
    Field("role",     StringType())
])
```

### Field

A typed slot on an entity.

```tlang
set Field(name: String, type: Type) {
    kind:       "field",
    nullable:   false,
    insertable: true,
    updatable:  true
}
```

### Type

The base type marker. Concrete types extend it.

```tlang
set Type {}

set StringType : Type {}
set Email      : Type {}
set Password   : Type {}
set Number     : Type {}
set Date       : Type {}
set DateTime   : Type {}
set UUID       : Type {}
set PKey       : Type {}
set FKey       : Type {}
```

### Enum

A fixed set of named values.

```tlang
set EnumType : Type {
    kind:   "enum",
    values: ""       // space-separated list of value names
}
```

### Value

A typed object without its own identity — owned entirely by an entity
(address, money amount, coordinates…).

```tlang
set ValueBase {
    kind:   "value",
    fields: ""
}
```

### DomainEvent

A named occurrence in the domain. Triggers downstream processing without
direct coupling — notifications, audit logs, projections.

```tlang
set DomainEventBase {
    kind:    "event",
    payload: ""     // space-separated field names
}
```

---

## API

### CrudSpec

Requests automatic CRUD generation for a named entity. The framework adapter
derives the full resource from the entity definition — no repetition.

```tlang
set CrudBase {
    kind:   "crud",
    entity: ""      // name of the target Entity set
}
```

### Endpoint

A custom API endpoint not derivable from entity CRUD.

```tlang
set EndpointBase {
    kind:   "endpoint",
    method: "GET",
    path:   ""
}
```

### Webhook

An inbound HTTP call from an external system. Emits a `DomainEvent`.

```tlang
set WebhookBase {
    kind:  "webhook",
    path:  "",
    event: ""       // name of the DomainEvent set it emits
}
```

---

## Frontend

### Screen

A full navigable page. May be bound to an entity (for auto-generation) or
standalone.

```tlang
set ScreenBase {
    kind:   "screen",
    route:  "",
    entity: "",     // optional: entity this screen is bound to
    type:   ""      // "listing" | "detail" | "form" | "dashboard" | "custom"
}
```

### Component

A reusable UI element composed into screens.

```tlang
set ComponentBase {
    kind: "component",
    type: ""        // "card" | "table" | "chart" | "stat" | "custom"
}
```

### Form

A data-entry form. Derived from an entity or declared manually.

```tlang
set FormBase {
    kind:   "form",
    entity: "",     // optional: derive fields from entity
    action: ""      // "create" | "edit" | "custom"
}
```

### Navigation

The routing structure — declares routes and their hierarchy.

```tlang
set NavBase {
    kind:  "nav",
    items: ""       // space-separated list of route paths
}
```

---

## Security

### SecurityBase

The authentication mechanism. Concrete types (JWT, OAuth2, SAML) extend this.

```tlang
set SecurityBase {
    kind:     "security",
    provider: ""    // "jwt" | "oauth2" | "saml" | "basic" | "none"
}
```

### Role

A named category of user within the system.

```tlang
set RoleBase {
    kind: "role",
    name: ""
}
```

### Permission

A rule granting a role access to a resource or screen.

```tlang
set PermissionBase {
    kind:     "permission",
    role:     "",       // name of a Role set
    resource: "",       // name of an Endpoint, CrudSpec, or Screen set
    actions:  ""        // space-separated: "read write delete admin"
}
```

---

## Infrastructure

All infrastructure base sets declare only their **category**. Concrete
implementations (Postgres, Minio, Redis…) extend them in Layer 2 or 3.
Project teams declare the concrete type; the adapter knows how to wire it.

### Database

```tlang
set Database {
    kind: "database",
    type: "relational"  // "relational" | "document" | "graph"
}
```

### Storage

```tlang
set Storage {
    kind: "storage",
    type: "object"      // "object" | "filesystem"
}
```

### Cache

```tlang
set Cache {
    kind: "cache",
    type: "distributed" // "in-memory" | "distributed"
}
```

### Queue

```tlang
set Queue {
    kind: "queue",
    type: "broker"      // "in-process" | "broker"
}
```

### EmailService

```tlang
set EmailService {
    kind: "email",
    type: "smtp"        // "smtp" | "api"
}
```

---

## Deployment

### Environment

A named deployment target. Infrastructure config may vary per environment.

```tlang
set EnvironmentBase {
    kind:     "environment",
    name:     "",       // "dev" | "staging" | "production"
    replicas: "1",
    domain:   ""
}
```

### ContainerBase

```tlang
set ContainerBase {
    kind:      "container",
    baseImage: ""
}
```

### OrchestrationBase

The deployment target. Concrete types (Kubernetes, DockerCompose) extend this.

```tlang
set OrchestrationBase {
    kind: "orchestration",
    type: ""            // "kubernetes" | "compose" | "ecs" | "none"
}
```

### MonitoringBase

```tlang
set MonitoringBase {
    kind: "monitoring",
    type: "none"        // "prometheus" | "datadog" | "cloudwatch" | "none"
}
```

---

## Pipeline

### PipelineBase

```tlang
set PipelineBase {
    kind:     "pipeline",
    provider: ""        // "github" | "gitlab" | "jenkins" | "none"
}
```

### Stage

```tlang
set StageBase {
    kind:        "stage",
    name:        "",    // "build" | "test" | "publish" | "deploy"
    environment: ""     // optional: name of an Environment set
}
```

### PipelineNotification

```tlang
set PipelineNotificationBase {
    kind:    "notification",
    channel: "",        // "slack" | "email"
    on:      "failure"  // "failure" | "success" | "always"
}
```

---

## Derivation rules

Many components are automatically derived from others. Framework adapters
apply these rules unless the project explicitly overrides.

| If declared | Derived automatically |
|---|---|
| `Entity` | `CrudBase`, `FormBase(action: "create")`, `FormBase(action: "edit")` |
| `CrudBase` | `ScreenBase(type: "listing")`, `ScreenBase(type: "detail")` |
| `RoleBase` | `PermissionBase` entries for all `CrudBase` resources |
| `EnvironmentBase` | `StageBase(name: "deploy")` in the pipeline |
| `Entity` with `DomainEventBase` | async handler stub |

The project team only declares what differs from the derived defaults.
