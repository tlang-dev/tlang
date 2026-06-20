# Use Cases

TLang is not just a code generator — it is a structural consistency tool.
The core idea: write the pattern once, enforce it everywhere, forever.
Below are the situations where this changes how teams work.

## Save AI Tokens at Scale

The standard AI-assisted development workflow has a hidden cost problem.
Ask an AI to generate a Kotlin entity class: it reasons about your schema,
your conventions, your annotations, and produces the file. Multiply that
by 200 entities across 10 services and the cost compounds — not just in
API tokens, but in time, review cycles, and subtle inconsistencies between
what the model produced on Monday versus Friday.
TLang changes the economics. The AI writes the template once.
TLang generates everything else.

```tlang
      // AI writes this template once — then never touches the 200 output files
      lang [kotlin] entity(pkg: String, name: String, table: String) {
          pkg ${pkg}
          annotation Entity
          annotation Table("${table}")
          annotation Cache(region = "${name}Cache", expire = 3600)
          impl ${name}(
              val id: Long,
              val name: String,
              val createdAt: LocalDateTime = LocalDateTime.now()
          )
      }
    
```

One template call per entity instead of one AI prompt per entity.
The AI reasoning happens once; TLang enforces the result at any scale.
A project with 200 entities costs the same as a project with 2.

## Fix Vulnerabilities Everywhere at Once

A CVE is disclosed. Your authentication middleware logs full request headers,
which can contain session tokens. You need to redact them before logging.
Your codebase has 40 microservices, each with its own copy of the middleware.
Without TLang: 40 pull requests, 40 code reviews, 40 deployments, and a two-week
window where some services are patched and some are not.
With TLang: one template change, one generation run, one pull request.

```tlang
      lang [kotlin] loggingFilter(pkg: String, service: String) {
          pkg ${pkg}
          annotation Component
          impl ${service}LoggingFilter : OncePerRequestFilter() {
              func doFilterInternal(req, res, chain) {
                  // Redact sensitive headers before logging
                  let safe = req.headers.filter { it.key !in SENSITIVE_HEADERS }
                  log.info("${service} request: {}", safe)
                  chain.doFilter(req, res)
              }
          }
      }
    
```

Update the template. Regenerate. Every service gets the fix in one commit.
Security patches, dependency updates, logging changes, audit trail additions —
anything that must be applied uniformly across your fleet becomes a single operation.

## Keep All Services Consistent

Microservice architectures drift. One team adds structured logging. Another adds
distributed tracing. A third changes the health-check contract. Over months,
services that started identical become subtly incompatible.
TLang makes consistency the path of least resistance. When all services share
the same templates, they share the same structure by construction:

```tlang
      set InventoryService  { name: "Inventory",  port: "8081", team: "platform" }
      set CatalogService    { name: "Catalog",    port: "8082", team: "catalog"  }
      set OrderService      { name: "Order",      port: "8083", team: "checkout" }
      set NotificationService { name: "Notification", port: "8084", team: "platform" }
    
```

Every service gets the same health check, the same metrics endpoint, the same
error response format, the same logging structure. When one improves, all improve.
The template is the source of truth — not the individual service repositories.

## Generate Multiple Formats from One Source

Documentation and code tend to diverge. API docs go stale. READMEs fall behind.
Architecture diagrams describe a system that no longer exists.
The doc template solves this by generating every format from a single source.
When the template changes, every output format changes together:

```tlang
      doc [md, html] apiReference(service: String, baseUrl: String) {
        # ${service} API Reference

        Base URL: ${baseUrl}

        [section "authentication"
          ## Authentication

          All requests require a Bearer token in the Authorization header.

          [code "bash"
            curl -H "Authorization: Bearer $TOKEN" ${baseUrl}/api/v1/items
          ]
        ]

        [section "endpoints"
          ## Endpoints

          [list "unordered"
            - GET /health — liveness probe, returns 200 OK
            - GET /api/v1/items — list all items
            - POST /api/v1/items — create an item
            - GET /api/v1/items/:id — get item by ID
            - DELETE /api/v1/items/:id — delete item by ID
          ]
        ]
      }

      func main(): String {
          File.write("docs/api.md",   apiReference("md",   "Inventory", "https://api.example.com"), true)
          File.write("docs/api.html", apiReference("html", "Inventory", "https://api.example.com"), true)
          return "ok"
      }
    
```

Markdown for GitHub, HTML for the developer portal — both generated, both always current.
Apply the same approach to: README files, changelogs, onboarding guides, architecture
decision records, runbooks, and any content that lives in more than one place.

## Structural Refactoring at Zero Cost

Renaming a field across 200 files is a mechanical task — exactly what AI is bad at
reliably and exactly what TLang is built for. When your model lives in set declarations
and your structure lives in templates, a rename is:

1. Update the set value (or the template parameter that references it)
2. Run tlang compile-run
3. All 200 output files are updated

The same applies to structural changes: switching from REST to gRPC, changing your
ORM annotations, adopting a new base class, restructuring your package layout.
These are template changes, not file-by-file edits.

## Adopt New Technologies Everywhere

Your team decides to add distributed tracing to every service using OpenTelemetry.
The change is not complex — it is repetitive. Every service needs the same dependency,
the same tracer configuration, the same span wrapping on HTTP handlers.
Without TLang: 40 services, 40 PRs, 6 weeks, and several services left behind.
With TLang: update the service template, add the tracing annotation, regenerate.

```tlang
      lang [kotlin] httpHandler(pkg: String, name: String, path: String) {
          pkg ${pkg}
          import io.opentelemetry.instrumentation.annotations.WithSpan
          annotation RestController
          annotation RequestMapping("${path}")
          impl ${name}Controller {
              annotation GetMapping
              annotation WithSpan("${name}.list")
              func list(): ResponseEntity
          }
      }
    
```

The template guarantees that every handler gets tracing. Handlers added in the future
automatically inherit it. The convention is encoded in the template — not in a wiki page
that developers have to remember to read.

## Enforce Design Systems Automatically

Design tokens — colours, spacing, typography, border radii — tend to multiply.
One team uses #7c5cfc for accent colour, another hard-codes #8b5cf6 in a component.
Over time the UI fragments.
TLang style templates make design tokens the single source of truth:

```tlang
      style [css] DesignSystem(accent: String, radius: String) {
          :root {
              --accent: ${accent},
              --accent-hover: "color-mix(in srgb, ${accent} 80%, white)",
              --radius: ${radius}
          }
      }

      func main(): String {
          File.write("packages/design/tokens.css",
              DesignSystem("css", "#7c5cfc", "8px"), true)
          return "ok"
      }
    
```

Change the accent colour: regenerate. Every component that uses var(--accent) updates.
Parameterise per-brand to generate white-label variants from the same template.

