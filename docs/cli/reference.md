# TLang CLI Reference

The `tlang` command-line interface is the primary way to compile, run, and package TLang programs.

## Installation

The `tlang` CLI is built from this repository. After building with `cargo build --release`, the binary is available at `target/release/tlang`.

Add it to your PATH or run it directly:

```bash
tlang --version
```

## Command Syntax

```
tlang <command> [options] [-p <path>] [target]
```

- `<command>`: Required. One of: `compile`, `run`, `both`, `clean`, `init`, `package`, `lsp-server`, `mcp-server`, `--help`, `--version`
- `[options]`: Optional flags specific to each command
- `[-p <path>]`: Optional. Set the project root directory (overrides positional target)
- `[target]`: Optional positional path to a project directory or `.tlang` file

If both `-p` and a positional target are omitted, the current working directory is used.

---

## Commands

### `compile`

Compile a TLang program to bytecode without running it.

**Synopsis:**
```bash
tlang compile [options] [-p <path>] [target]
```

**Options:**
- `--live`: Watch `.tlang` source files and automatically recompile on changes

**Description:**
Compiles all `.tlang` files in the project and writes bytecode (`.tlangc`) files to `target/tlang/`. If bytecode is already up-to-date (source files haven't changed), compilation is skipped.

**Examples:**
```bash
# Compile current directory
tlang compile

# Compile a specific project
tlang compile my-project

# Compile with live reload
tlang compile --live

# Compile with explicit path
tlang compile -p /path/to/project
```

**Output:**
- Creates `target/tlang/` directory if it doesn't exist
- Writes one `.tlangc` file per `.tlang` source file

---

### `run`

Run a compiled TLang program.

**Synopsis:**
```bash
tlang run [-p <path>] [target]
```

**Description:**
Loads and executes a compiled TLang program from its bytecode (`.tlangc`) files. The program must have been compiled first with `tlang compile`.

**Examples:**
```bash
# Run current directory
tlang run

# Run a specific project
tlang run my-project

# Run with explicit path
tlang run -p /path/to/project
```

**Requirements:**
- Bytecode files must exist in `target/tlang/`
- If bytecode is stale (source files have changed), use `tlang both` instead

---

### `both` (alias: `compile-run`)

Compile then run a TLang program in one step.

**Synopsis:**
```bash
tlang both [options] [-p <path>] [target]
tlang compile-run [options] [-p <path>] [target]
```

**Options:**
- `--in-memory`: Compile and run without writing bytecode files to disk

**Description:**
Compiles the program (if needed) and immediately runs it. This is the most common command for development.

**Examples:**
```bash
# Compile and run current directory
tlang both

# Compile and run without writing bytecode to disk
tlang both --in-memory

# Compile and run a specific project
tlang compile-run my-project
```

**Note:** The `--in-memory` flag is only valid with `both` and `compile-run`.

---

### `clean`

Delete compiled bytecode files.

**Synopsis:**
```bash
tlang clean [-p <path>] [target]
```

**Description:**
Deletes all `.tlangc` bytecode files under `target/tlang/`.

**Examples:**
```bash
# Clean current directory
tlang clean

# Clean a specific project
tlang clean my-project
```

**What it removes:**
- All files in `target/tlang/` directory
- Does NOT remove `target/` directory itself
- Does NOT remove source files or other build artifacts

---

### `init`

Scaffold a new TLang project.

**Synopsis:**
```bash
tlang init [directory]
```

**Description:**
Creates a new TLang project directory with the following structure:

```
<directory>/
├── manifest.yml          # Project manifest
├── Main.tlang            # Entry point with hello-world example
└── output/               # Directory for generated files
```

The generated `Main.tlang` includes a working example that uses the Kotlin generator (if available in your local tbox).

**Examples:**
```bash
# Create a new project in current directory
tlang init

# Create a new project in a specific directory
tlang init my-project

# Create a new project with explicit path
tlang init /path/to/my-project
```

**Requirements:**
- The directory must not already exist (or must be empty)
- To run the generated example, you need the Kotlin generator in your local tbox:
  ```bash
  tlang package generators/kotlin
  ```

---

### `package`

Compile, bundle, and publish a generator to the local tbox.

**Synopsis:**
```bash
tlang package [-p <path>] [target]
```

**Description:**
Packages a TLang generator project into a `.tbag` archive and publishes it to the local tbox store (`~/.tlang/tbox/`).

**What it does:**
1. Compiles the project (skipped if bytecode is up-to-date)
2. Bundles source files and compiled bytecode into a `.tbag` (ZIP) archive
3. Copies the archive to the appropriate location in `~/.tlang/tbox/`

**Examples:**
```bash
# Package current directory
tlang package

# Package a specific generator
tlang package generators/kotlin

# Package with explicit path
tlang package -p /path/to/generator
```

**Requirements:**
- The project must have a valid `manifest.yml`
- The manifest must declare a `main:` entry point
- The main file should use `expose` to declare public symbols

**Output:**
Prints the tbox path where the `.tbag` was published, e.g.:
```
~/.tlang/tbox/TLangGen/KotlinGen/Kotlin/1.0.0/alpha/1/Kotlin.tbag
```

---

### `lsp-server`

Start the TLang Language Server Protocol (LSP) server.

**Synopsis:**
```bash
tlang lsp-server
```

**Description:**
Starts the LSP server for TLang, communicating over stdin/stdout. This is typically launched by your editor/IDE, not directly by users.

**Features:**
- Syntax highlighting
- Code completion
- Diagnostic messages
- Go-to-definition
- Hover information
- Document formatting
- Semantic tokens

**Communication:**
- Uses stdio for communication with the client
- Implements the LSP 3.17 specification
- Blocks until the client sends a `shutdown` request followed by `exit` notification

**Examples:**
```bash
# Start the LSP server (typically done by your editor)
tlang lsp-server
```

**Note:** This command does not accept a target path or any options.

---

### `lsp-stop`

Stop a running TLang LSP server.

**Synopsis:**
```bash
tlang lsp-stop
```

**Description:**
Sends a shutdown request to a running LSP server instance.

---

### `lsp-restart`

Stop (if running) then start the TLang LSP server.

**Synopsis:**
```bash
tlang lsp-restart
```

**Description:**
Convenience command that stops any running LSP server and starts a new one.

---

### `mcp-server` (alias: `mcp`)

Start the TLang Model Context Protocol (MCP) server.

**Synopsis:**
```bash
tlang mcp-server
tlang mcp
```

**Description:**
Starts the MCP server for TLang, enabling AI assistants to interact with TLang projects. Communicates over stdin/stdout using the MCP protocol.

**Features:**
- Read TLang source files
- Execute TLang code
- Access TLang templates and models
- List and read resources
- Runtime project-context switching through MCP tool `set_project_context(projectPath)`
- Dynamic MCP tool discovery from exposed `MCPTool` providers in the active project

**Examples:**
```bash
# Start the MCP server
tlang mcp-server

# Alias
tlang mcp
```

Use MCP `tools/call` with `set_project_context` to change the active project at runtime:

```json
{
  "name": "set_project_context",
  "arguments": {
    "projectPath": "/absolute/path/to/project"
  }
}
```

After changing context, dynamic tools are rebuilt and the server emits `notifications/tools/list_changed`.

**Note:** The CLI command itself does not accept a target path or any options.

---

### `--help` (alias: `-h`, `help`)

Show help message.

**Synopsis:**
```bash
tlang --help
tlang -h
tlang help
```

**Description:**
Prints the usage message with all available commands and options.

---

### `--version` (alias: `-V`, `version`)

Show version information.

**Synopsis:**
```bash
tlang --version
tlang -V
tlang version
```

**Description:**
Prints the TLang version string.

---

## Exit Codes

| Code | Meaning |
|------|---------|
| 0 | Success |
| 1 | General error (invalid command, missing arguments, etc.) |
| 2 | Compilation error |
| 3 | Runtime error |
| 4 | I/O error (file not found, permission denied, etc.) |

---

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `TLANG_TBOX` | Override the tbox directory path | `~/.tlang/tbox` |
| `TLANG_LOG` | Enable verbose logging (set to any value) | (disabled) |

---

## Configuration Files

### `manifest.yml`

Every TLang project requires a `manifest.yml` file in its root directory. See the [Manifest Specification](../specs/manifest.md) for details.

### `.tlangignore`

Files matching patterns in `.tlangignore` are excluded from compilation. The format follows `.gitignore` conventions.

---

## Examples

### Complete workflow

```bash
# Initialize a new project
tlang init my-project
cd my-project

# Package the Kotlin generator (one-time setup)
tlang package generators/kotlin

# Compile and run
tlang both

# Or compile once, then run multiple times
tlang compile
tlang run

# Clean up
tlang clean
```

### Working with multiple projects

```bash
# Project A
tlang both -p /path/to/project-a

# Project B
tlang both -p /path/to/project-b
```

### Development with live reload

```bash
# In one terminal: compile with live reload
tlang compile --live

# In another terminal: run when needed
tlang run
```

---

## See Also

- [Core Language Specification](../specs/core-language.md)
- [Template Block Specification](../specs/template-blocks.md)
- [TLang Index](../../prompts/tlang-index.md)
- [Templates Reference](../../prompts/tlang-templates.md)
- [Helpers Reference](../../prompts/tlang-helpers.md)
- [Patterns & Best Practices](../../prompts/tlang-patterns.md)
- [Common Mistakes](../../prompts/tlang-mistakes.md)
