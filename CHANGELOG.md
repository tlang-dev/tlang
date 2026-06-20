# Changelog

All notable changes to TLang are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versions follow [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Added
- Apache 2.0 license and full OSS governance (LICENSE, CODE_OF_CONDUCT, SECURITY)
- SPDX license headers across all source files

---

## [0.1.0] — Initial Release

### Added
- TLang compiler: parse → AST → bytecode → multi-target code emission
- Built-in language generators: Kotlin, Java, TypeScript, HTML, JSON, YAML, TOML, XML
- Language Server Protocol (LSP) support for editor integration
- Model Context Protocol (MCP) server for AI agent integration
- Package system: `.tbag` archive format, `tlang pull` / `tlang push`
- Forge framework for full-stack project scaffolding
- Conformity test suite covering core language features
- Example projects: Kotlin/Quarkus, Java/Spring Boot, TypeScript, HTMX dashboards
- Zed editor plugin with syntax highlighting and LSP

[Unreleased]: https://github.com/joel-f/tlang/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/joel-f/tlang/releases/tag/v0.1.0
