# TLang — Template Language for Structural Code Generation

[![CI](https://github.com/joel-f/tlang/actions/workflows/build.yml/badge.svg)](https://github.com/joel-f/tlang/actions/workflows/build.yml)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Crates.io](https://img.shields.io/crates/v/tlang.svg)](https://crates.io/crates/tlang)

> **Generate once. Enforce everywhere. Pay less.**

---

## The Problem with Agentic AI at Scale

Agentic AI is transforming software development — but it comes with a growing cost problem.

As AI agents are tasked with generating code across an entire repository, the bill climbs fast. Every file, every service, every entity requires the model to reason from scratch, consume tokens, and produce output that is — at best — *probably* correct. Multiply that across dozens of services, hundreds of entities, or thousands of files, and the cost becomes difficult to justify.

The instinctive response is to switch to cheaper, less capable models. But this trades one problem for another: **less capable models make more mistakes**. A cheaper model that generates subtly wrong code across 200 files is far more expensive to fix than the tokens you saved.

There is a better approach.

---

## The TLang Approach: AI Writes the Template, Templates Do the Work

TLang separates the *creative* step from the *mechanical* step.

```
┌─────────────────────────────────────────────────────┐
│  AI Agent (expensive, called once)                  │
│  → Understands your domain                         │
│  → Writes a precise, reusable TLang template        │
└────────────────────────┬────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│  TLang Runtime (deterministic, called many times)   │
│  → Applies the template to every entity/service     │
│  → Produces structurally identical, error-free code │
│  → No tokens consumed                               │
└─────────────────────────────────────────────────────┘
```

**The AI writes the template once.** The template is then applied to every similar structure in your codebase — deterministically, consistently, and at zero additional AI cost.

You get the intelligence of a high-capability model where it matters (design, structure, intent), and the reliability of a compiler where it matters most (repetitive generation across the repo).

---

## Why Templates Beat Cheaper Models

When you reduce cost by switching to a less capable AI model, you are gambling on consistency. A smaller model:

- May follow your conventions most of the time, but not always
- Cannot guarantee structural parity between a `UserService` and an `OrderService`
- Will introduce subtle drift that accumulates into technical debt
- Requires human review of every output to catch the cases it got wrong

A TLang template, once authored and validated, **cannot produce structurally inconsistent output**. It is not probabilistic. The tenth entity it generates is identical in structure to the first. The hundredth is identical to the tenth.

| Approach | Cost | Consistency | Scales with repo size? |
|---|---|---|---|
| High-capability AI for every file | Very high | High but not guaranteed | No — cost grows linearly |
| Low-capability AI for every file | Lower | Poor | No — errors grow linearly |
| AI writes template + TLang generates | Low | Guaranteed | Yes — cost is fixed |

---

## How It Works

### 1. Define Your Domain Model

Describe your entities, services, or data structures in a `model` block:

```tlang
model {
    set User {
        name: String,
        age: Number,
        email: String
    }

    set Order ext User {
        orderId: String,
        items: String[]
    }
}
```

### 2. Write a Template (or have your AI write it)

A `lang` block defines the structural pattern for a target language. The AI writes this once:

```tlang
lang [kotlin] entity(pkg: String, className: String, attrs: List) {
    pkg ${pkg}
    impl[public data class] ${className} {
        <[ renderFields(attrs) ]>
    }
}
```

The template captures the *intent* — the structural rules your team agreed on — and makes them permanent.

### 3. Apply It Everywhere

```tlang
helper {
    func main(): String {
        let output = entity("com.example", "User", User.attrs);
        File.write("output/User.kt", Generator.generate(output));

        let output = entity("com.example", "Order", Order.attrs);
        File.write("output/Order.kt", Generator.generate(output));

        return "done";
    }
}
```

Every generated file is structurally identical. No drift. No surprises.

---

## Packages and Generators

TLang ships with a generator package system. Generators (like the Kotlin generator) are first-class TLang packages, published to a local package store called the **tbox**.

### Publishing a Generator

```bash
tlang package generators/kotlin
```

This compiles the generator, bundles it into a `.tbag` archive, and publishes it to `~/.tlang/tbox`:

```
Packaging Kotlin v1.0.0 (TLangGen/KotlinGen)

  [1/3] Compile   — up to date, skipping
  [2/3] Packing…
        → generators/kotlin/target/tlang/Kotlin.tbag
  [3/3] Publishing to tbox…
        → ~/.tlang/tbox/TLangGen/KotlinGen/Kotlin/1.0.0/alpha/1/Kotlin.tbag

Done in 95ms.
Add to a project manifest:  TLangGen/KotlinGen/Kotlin 1.0.0:alpha:1 KotlinGen
```

### Consuming a Generator

Declare it in your `manifest.yml`:

```yaml
dependencies:
  - TLangGen/KotlinGen/Kotlin 1.0.0:alpha:1 KotlinGen
```

Then use it in your TLang source:

```tlang
use KotlinGen as kotlin
```

---

## Getting Started

### Start a New Project

```bash
tlang init my-project
cd my-project
tlang run
```

This scaffolds a complete hello-world project wired to the Kotlin generator from your local tbox.

### CLI Reference

```bash
tlang <command> [target]
```

| Command | Description |
|---|---|
| `compile` | Compile a TLang program (skips when up to date; supports `--live`) |
| `run` | Run a TLang program |
| `both` | Compile then run |
| `clean` | Delete compiled bytecode under `target/tlang` |
| `init [dir]` | Scaffold a new project |
| `package [dir]` | Compile, bundle, and publish to tbox |
| `lsp-server` | Start the language server |
| `mcp-server` | Start the MCP server over stdio |
| `--help` | Show help |
| `--version` | Show version |

`target` is an optional path to a project directory or a `.tlang` file. Defaults to the current directory.

### MCP Server Tools

Run the server with:

```bash
tlang mcp-server
```

Alongside the existing `tlang_*` project tools, the MCP server now exposes compact repository tools for token-efficient coding workflows:

| Tool | Purpose |
|---|---|
| `list_files` | List repository files/directories under a path, with optional recursion and capped output |
| `read_file` | Read a UTF-8 file by path, optionally restricted with `startLine` / `endLine` |
| `search_code` | Search repository text with compact `path:line:text` matches and result limits |
| `edit_file` | Apply a narrow find/replace edit without regenerating the whole file |
| `apply_patch` | Apply multiple structured edits (`replace`, `delete`, `insert_before`, `insert_after`) to one file |
| `create_file` | Create a new file at a path with provided content; rejects overwrites unless `overwrite: true` |
| `move_file` | Move or rename a file (`source` → `destination`); rejects if destination exists. Note: references in other files are **not** updated automatically — use `rename_symbol` or `search_code` + `edit_file` afterwards |
| `rename_symbol` | Rename an identifier across all repository source files using word-boundary matching; supports `dry_run` preview |
| `create_module` | Scaffold a new `.tlang` source file with standard boilerplate (helper + model blocks); accepts a short `name` or an explicit `path` |
| `add_leaf_to_template` | Insert a `leaf_name: &leaf_ref` attribute into a named `set` entity inside a model block |
| `move_method` | Move a `func` definition from one TLang file's `helper` block to another by name; cross-file call references are **not** updated automatically |
| `set_project_context` | Set the active project root and reload dynamic MCP tools discovered from exposed `MCPTool` providers |

These repository tools only operate on paths inside the current working tree and intentionally keep responses small. Large reads/listings are truncated with a clear notice so callers can narrow the next request.

#### Dynamic MCP tools from project context

Call `set_project_context` at runtime to select the active TLang project:

```json
{
  "name": "set_project_context",
  "arguments": { "projectPath": "/absolute/path/to/project" }
}
```

When the context changes, the server recompiles the project, finds exposed main-file functions returning `MCPTool`, and registers the callable implementations from those returned instances as dynamic MCP tools.

---

## The Core Idea, Restated

AI is a powerful but expensive tool for reasoning about structure and intent. Once that reasoning is captured in a template, AI is no longer needed to reproduce it. TLang is the layer that captures that reasoning, makes it executable, and applies it without limits — across every file, every service, every entity in your codebase.

**Write the template once. Generate everything. Forever.**