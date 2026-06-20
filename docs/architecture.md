# TLang Architecture

This document describes the internal structure of the TLang compiler and runtime,
intended for contributors who want to understand how the pieces fit together.

---

## High-level pipeline

```
  .tlang source files
        │
        ▼
  ┌─────────────┐
  │   parser    │  pest grammar → DomainModel (AST)
  └──────┬──────┘
         │
         ▼
  ┌─────────────┐
  │   loader    │  resolves `use` imports, merges dependency blocks
  └──────┬──────┘
         │
         ▼
  ┌──────────────────────────────────────────┐
  │              runtime: compiler           │
  │  • validates model types & templates     │
  │  • helper_parser: source → IR (Stmt/Expr)│
  │  • type_checker: static analysis         │
  │  • CompiledProgram (in-memory IR)        │
  └──────┬───────────────────────────────────┘
         │              │
         │ (optional)   │
         ▼              ▼
  ┌────────────┐  ┌─────────────┐
  │  encoding  │  │   runtime:  │
  │  BcWriter  │  │  executor   │
  │  → .tlangc │  │  Runtime<'a>│
  └────────────┘  └──────┬──────┘
                         │
                         ▼
                   RunResult
                   (output text + return value + model tree)
```

---

## Source modules

### `src/ast.rs`

The **top-level AST** produced by the parser. Contains:

- `DomainModel` — the root of every parsed `.tlang` file.
- `DomainHeader` — `expose` and `use` declarations at the top of the file.
- `DomainBlock` — an enum over all block kinds: `Helper`, `Template`, `Data`, `Cmd`, `Model`, `Raw`, `Doc`, `Style`.
- Individual block types (`HelperBlock`, `TemplateBlock`, `ModelBlock`, …) each carry their raw content string plus source-position fields used by the LSP.

The AST is serialisable (`serde`) so it can be cached as `.tlangc` bytecode.

### `src/tree_context.rs`

A tiny `TreeContext` struct that records `(file, line, position)` for nodes inside the model and template parse trees. Used for precise error reporting.

### `src/model_tree.rs`

The **parsed model block AST** — the result of parsing the `model { … }` section of a `.tlang` file. Key types:

- `ModelBlockTree` — root of the model tree (list of `ModelNodeTree`).
- `ModelNodeTree` — either an `AssignVar` (a typed constant) or a `SetEntity` (a `set` declaration).
- `ModelSetEntityTree` — a `set` declaration with its name, parent extensions (`exts`), constructor params, attributes, and optional `>>` output / `!>` exec declarations.
- `ModelValueTypeTree` — describes the type of a model attribute: simple type, array, function ref, impl, etc.

### `src/tmpl_tree.rs` (and `tmpl_data_tree`, `tmpl_doc_tree`, `tmpl_style_tree`, `tmpl_cmd_tree`)

Each `tmpl_*_tree` module provides the **parsed AST for a specific template block kind**:

| Module | Block keyword | Purpose |
|--------|--------------|---------|
| `tmpl_tree` | `lang [kotlin] Name(…) { … }` | Code generation templates |
| `tmpl_data_tree` | `data [html, json] Name(…) { … }` | Structured data output |
| `tmpl_doc_tree` | `doc [md] Name(…) { … }` | Documentation output |
| `tmpl_style_tree` | `style [css] Name(…) { … }` | Style output |
| `tmpl_cmd_tree` | `cmd [bash] Name(…) { … }` | Executable command descriptions |

---

### `src/parser.rs`

The **Pest-based parser** that converts raw `.tlang` source text into a `DomainModel`. It uses the grammar at `src/tlang.pest`.

Entry points:
- `parse_domain_model(input)` — parses a string (used in tests and the LSP).
- `parse_domain_model_in_file(file, input)` — same, with a filename for error messages.

The parser calls into the `*_tree` modules to convert raw grammar pairs into their typed AST nodes.

### `src/loader.rs`

The **dependency resolver and multi-file loader**. Starting from the entry-point file, it:

1. Parses the file's `use` declarations.
2. Resolves each `use` path — either a sibling file, a manifest dependency, or a built-in `TLang.*` library.
3. Loads and parses each dependency recursively (cycle detection via a `visited` set).
4. Prepends dependency `DomainBlock`s before the consumer's blocks so the compiler sees dependencies first.

Key functions:
- `load_program(path)` — full load + compile pipeline from a file path.
- `load_program_with_manifest(path, manifest)` — same, with an explicit manifest for package resolution.
- `compile_to_bytecode_files(path)` — load, compile, and write `.tlangc` files.

### `src/manifest.rs`

Parses the `manifest.yml` project file and resolves dependency paths. Supports:
- Local file dependencies (`path:`).
- Registry dependencies (TLang package registry).
- `.tbag` package archives.
- Semantic version specifications (`^`, `~`, `=`, `>=`).

### `src/formatter.rs`

Re-serialises a `DomainModel` AST back into canonical TLang source text. Used by the LSP to implement document formatting (`textDocument/formatting`). Helper and template block bodies are reproduced verbatim since they contain arbitrary target-language code.

### `src/error_checker.rs`

Semantic validation over a `DomainModel`, producing structured `TLangError` diagnostics that the LSP translates into `Diagnostic` objects. Also provides utility functions used by other modules:
- `offset_to_line_position` / `location_from_offset` — byte offset ↔ `(line, col)` conversion.
- `format_pest_error` / `format_with_context` — human-readable error formatting.

---

### `src/runtime.rs`

The **compiler and executor** — the largest module. After Phase 2 refactoring it owns:

- All **public types**: `Value`, `CompiledProgram`, `RunResult`, `CompileError`, `RuntimeError`, `TypeAnnotation`, `FunctionInfo`, `RunOptions`.
- All **IR types** (private to the module): `Stmt`, `Expr`, `Op`, `Function`, `Frame`, and the various template function types.
- The **compiler**: `compile_from_domain_model` — translates a `DomainModel` into a `CompiledProgram`. Calls into `helper_parser` to compile helper code blocks, validates set instantiations, and collects templates.
- The **runner**: `run_main`, `run_main_with_args`, `run_main_with_options`, `run_exposed_function` — execute a `CompiledProgram` using the `Runtime<'a>` executor.
- The **executor** (`struct Runtime<'a>`): interprets the IR, evaluates expressions, manages call frames, expands templates, and writes generated output files.
- The **code-generation helpers**: template collection, output path interpolation, file writing, include expansion, template fragment inlining.

#### Submodules

| File | Purpose |
|------|---------|
| `runtime/encoding.rs` | Binary bytecode encoder (`BcWriter`) and decoder (`BcReader`) for `.tlangc` files. Also contains the `bc` constants module (discriminant bytes for all IR variants). |
| `runtime/helper_parser.rs` | Lexer and recursive-descent parser for TLang helper code blocks. Converts raw `{ … }` block text into `Vec<Stmt>`. Separate from the Pest-based file parser because helper blocks use a simpler, embeddable grammar. |
| `runtime/type_checker.rs` | Optional static type checker that runs over compiled `Function` IR to emit warnings about type mismatches and missing annotations. Used by the LSP for warning diagnostics. |
| `runtime/libraries/` | Built-in standard library implementations: `TLang.String`, `TLang.List`, `TLang.Map`, `TLang.File`, `TLang.Terminal`, `TLang.Generator`, `TLang.Math`, etc. Each library is a Rust module that handles calls to its namespace from the executor. |

---

### `src/lsp.rs` → `src/lsp/`

Full **Language Server Protocol** implementation using the `lsp-server` crate. The server loop (`run_lsp_server`) reads JSON-RPC messages over stdin/stdout and dispatches to feature handlers:

| File | LSP features |
|------|-------------|
| `mod.rs` | Server loop, request dispatch, notification handling |
| `completion.rs` | `textDocument/completion` — keyword, method, library, and `use`-path completions; `library_method_table` |
| `hover.rs` | `textDocument/hover` — type info and doc-comments for functions, model attrs, library methods |
| `signatures.rs` | `textDocument/signatureHelp` — parameter hints while typing a call |
| `references.rs` | `textDocument/references`, `textDocument/rename`, `textDocument/definition` |
| `symbols.rs` | `textDocument/documentSymbol` — outline of functions, templates, model entities |
| `semantic_tokens.rs` | `textDocument/semanticTokens` — token-based syntax highlighting |
| `diagnostics.rs` | `textDocument/publishDiagnostics` — parse and semantic errors |
| `formatting.rs` | `textDocument/formatting` — canonical source formatting |
| `util.rs` | Shared utilities: offset ↔ position conversion, word extraction, URI helpers |

### `src/mcp.rs`

A **Model Context Protocol** server that exposes TLang operations to AI agents over stdio JSON-RPC 2.0. Tools include file reading, code editing, symbol search, compilation, and project scaffolding. Used by the Claude Code MCP integration.

### `src/tbag.rs`

The **`.tbag` package archive format** — a zip file containing a `manifest.yml` and all `.tlang` source files. Functions: `pack`, `extract`, `push` (upload to registry), `find_tbag`, `read_manifest_from_tbag`.

### `src/bytecode.rs`

High-level **`.tlangc` file I/O** — reads and writes the bytecode cache files that `compile_to_bytecode_files` produces. Distinct from `runtime/encoding.rs`, which implements the binary encoding of the IR itself.

---

## Key data flow: `tlang run Main.tlang`

```
1. main() → cli::run(Compile { path: "Main.tlang" })
2. cli::build::compile_target() → loader::load_program_with_manifest()
3.   loader: parse Main.tlang → DomainModel
4.   loader: resolve `use` declarations recursively
5.   loader: merge dependency blocks → full DomainModel
6. runtime::compile_from_domain_model(model)
7.   helper_parser: parse { … } blocks → Vec<Stmt>
8.   type_checker: warn on type issues
9.   → CompiledProgram
10. runtime::run_main_with_options(program, options)
11.   Runtime<'a>::call_user_function("main", [])
12.   evaluate Stmt/Expr IR, expand templates, write output files
13.   → RunResult { output, return_value, model_tree }
14. print output to stdout
```

## Key data flow: `tlang lsp-server`

```
1. main() → cli::run(LspServer) → lsp::run_lsp_server()
2. JSON-RPC loop over stdin/stdout
3. textDocument/didOpen → parse + diagnose → publishDiagnostics
4. textDocument/completion → compute_completions() → CompletionList
5. textDocument/hover → compute_hover() → Hover
6. … (each feature is a pure function over the document text)
```
