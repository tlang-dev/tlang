# TLang Specifications (Draft)

This directory contains the evolving language specification for TLang.

## Scope

This is an initial draft focused on documenting currently implemented behavior with practical examples.

## Documents

- [Core Language Specification](./core-language.md)
  - Program structure
  - Header directives (`use`, `expose`)
  - `model` block (`let`, `set`, `ext`)
  - `helper` block (`func`, expressions, control flow)
- [Template Block Specification](./template-blocks.md)
  - `lang [<language>] <name>(...) { ... }`
  - Full template blocks and specialized `spec` blocks
  - Interpolated identifiers and template examples
- [Manifest Specification](./manifest.md)
  - Project metadata and identification
  - Dependency declaration and resolution
  - Entry point configuration
  - Package publishing format

## Status

Draft, work in progress.

## Additional Documentation

- [CLI Reference](../cli/reference.md) - Complete command-line interface documentation
- [TLang Index](../../prompts/tlang-index.md) - Quick routing index and cheat-sheets
- [Templates](../../prompts/tlang-templates.md) - All template types: lang/data/doc/style/raw
- [Helpers](../../prompts/tlang-helpers.md) - Functions, expressions, builtins, model block
- [Patterns](../../prompts/tlang-patterns.md) - Best practices, lead/spec, file I/O, checklist
- [Mistakes](../../prompts/tlang-mistakes.md) - Common mistakes and fixes
- [Contributing](../../contributing/CONTRIBUTING.md) - How to contribute to TLang development
