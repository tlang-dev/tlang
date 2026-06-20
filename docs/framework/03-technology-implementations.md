# Technology Implementations

This document shows how abstract components from
[02-project-model.md](./02-project-model.md) are given concrete form for
specific technologies.

Implementations live in Layer 2 (framework adapters) or Layer 3 (company
stacks). They are never written by project teams.

The pattern is always the same:
1. A concrete `set` extends an abstract base `set` and adds technology-specific
   attributes.
2. A `lang [target]` block (or `func`) reads those attributes and produces
   the generated output.

For how adapters are structured and selected, see
[04-adapter-contract.md](./04-adapter-contract.md).

---

## Infrastructure — Database

### Abstract base (Layer 2 contract)

```tlang
set Database {
    kind: "database",
    type: "relational"
}
```

### Postgres

```tlang
set Postgres : Database {
    kind:       "postgres",
    version:    "16",
    schema:     "public",
    poolMin:    "2",
    poolMax:    "10"
}
```

A project that wants Postgres declares:

```tlang
set MyDb : Postgres {
    schema: "myapp"
}
```

The adapter's `generateDatabase` hook reads the set and produces files.
The quarkus-kotlin adapter generates an `application.yml` datasource block;
the spring-java adapter generates `application.properties`; the django-python
adapter generates a `settings.py` `DATABASES` entry. Same `set`, different
output, because each is a different adapter. See
[04-adapter-contract.md](./04-adapter-contract.md).

### MongoDB

```tlang
set MongoDB : Database {
    kind:       "mongo",
    type:       "document",
    version:    "7",
    authSource: "admin"
}
```

### Company-level override (Layer 3)

A company that mandates Postgres 16 with fixed pool sizes creates a Layer 3
specialisation. Projects inherit it without setting anything:

```tlang
set AcmePostgres : Postgres {
    version:  "16",
    poolMin:  "5",
    poolMax:  "20"
}
```

Project teams declare `set MyDb : AcmePostgres { schema: "users" }` and
provide only what is unique to them.

---

## Infrastructure — Storage

### Abstract base

```tlang
set Storage {
    kind: "storage",
    type: "object"
}
```

### Minio

```tlang
set Minio : Storage {
    kind:      "minio",
    endpoint:  "",
    bucket:    "",
    accessKey: "",
    secretKey: ""
}
```

### S3

```tlang
set S3 : Storage {
    kind:   "s3",
    type:   "object",
    region: "",
    bucket: ""
}
```

---

## Security — Provider

### Abstract base

```tlang
set SecurityBase {
    kind:     "security",
    provider: "none"
}
```

### JWT

```tlang
set Jwt : SecurityBase {
    provider:  "jwt",
    expiry:    "3600",
    issuer:    "",
    algorithm: "RS256"
}
```

### OAuth2

```tlang
set OAuth2 : SecurityBase {
    provider:     "oauth2",
    serverUrl:    "",
    realm:        "",
    clientId:     "",
    clientSecret: ""
}
```

### Company override (Layer 3)

A company running Keycloak fixes the server URL and realm so projects only
supply the client ID:

```tlang
set AcmeKeycloak : OAuth2 {
    serverUrl: "https://auth.acme.com",
    realm:     "acme"
}
```

Project teams declare `set MySecurity : AcmeKeycloak { clientId: "my-service" }`.

---

## Deployment — Orchestration

### Abstract base

```tlang
set OrchestrationBase {
    kind: "orchestration",
    type: ""
}
```

### Kubernetes

```tlang
set Kubernetes : OrchestrationBase {
    type:          "kubernetes",
    namespace:     "",
    replicas:      "1",
    cpuRequest:    "250m",
    cpuLimit:      "1000m",
    memoryRequest: "256Mi",
    memoryLimit:   "512Mi",
    ingressHost:   "",
    tlsSecret:     ""
}
```

### Docker Compose

```tlang
set DockerCompose : OrchestrationBase {
    type:    "compose",
    version: "3.9",
    network: ""
}
```

### Company Kubernetes profile (Layer 3)

The company fixes resource defaults and adds its label conventions:

```tlang
set AcmeKubernetes : Kubernetes {
    cpuRequest:    "250m",
    cpuLimit:      "1000m",
    memoryRequest: "256Mi",
    memoryLimit:   "512Mi"
}
```

The adapter's `generateOrchestration` hook adds company-standard labels and
annotations (cost-centre, team, environment tags) on top of the base
Kubernetes output.

---

## Pipeline — CI/CD

### Abstract base

```tlang
set PipelineBase {
    kind:     "pipeline",
    provider: "none"
}
```

### GitHub Actions

```tlang
set GithubActions : PipelineBase {
    provider:      "github",
    defaultBranch: "main",
    registry:      ""
}
```

### Jenkins

```tlang
set Jenkins : PipelineBase {
    provider:   "jenkins",
    agentLabel: "",
    registry:   ""
}
```

---

## Frontend — Design system

The design system is a Layer 3 concern. The abstract `ScreenBase` and
`ComponentBase` sets from the project model are rendered using the company's
`data [html]` component library.

The adapter's `generateScreen` hook receives the screen set and the company
design system package name. It calls the correct component templates to build
the page — the project team never touches HTML.

When the design system updates its CSS classes, only the `data [html]`
component declarations change. Re-running generation across all projects
propagates the update automatically.

---

## How implementations are selected

The company stack reads the project set and resolves abstract bases to
concrete implementations:

```tlang
func resolveDatabase(project: String): String {
    let dbType = projectAttr(project, "database")
    if (Str.equals(dbType, "postgres")) { return "Postgres" }
    if (Str.equals(dbType, "mongo"))    { return "MongoDB" }
    return "Postgres"
}

func resolveOrchestration(project: String, env: String): String {
    if (Str.equals(env, "dev")) { return "DockerCompose" }
    return "AcmeKubernetes"
}
```

Project teams declare the abstract concepts. The company stack resolves them
to concrete `set` subtypes. The adapter then generates files from those
concrete sets. Projects never reference `Postgres`, `Minio`, `Kubernetes`, or
`GithubActions` directly — those are company stack details.
