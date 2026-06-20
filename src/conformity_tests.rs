// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Conformity tests — each test loads a real `.tlang` program from the
//! `conformity/` directory tree, compiles it, runs `main`, and asserts
//! both the printed output and the return value.
//!
//! The programs live under `conformity/<program>/Main.tlang` (relative to
//! the crate root, i.e. next to `Cargo.toml`).  Using `env!("CARGO_MANIFEST_DIR")`
//! means the paths are always resolved correctly regardless of the working
//! directory from which `cargo test` is invoked.

#[cfg(test)]
mod helpers;
#[cfg(test)]
mod basics;
#[cfg(test)]
mod models;
#[cfg(test)]
mod templates;
#[cfg(test)]
mod codegen;
#[cfg(test)]
mod libraries;
#[cfg(test)]
mod imports;
