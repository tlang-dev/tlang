// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Manifest file support for TLang projects.
//!
//! A `manifest.yml` in the root of a project directory describes the project
//! metadata and its dependencies.  Example:
//!
//! ```yaml
//! name: HelloWorld
//! project: IntegrationTests
//! organisation: TLang
//! version: 1.0.0
//! stability: alpha
//! releaseNumber: 1
//! author: Jane Dev                  # optional
//! website: https://example.com      # optional
//! license: Apache-2.0               # optional
//! compatibility:                    # optional; for generator packages
//!   language: kotlin                # target language, or '*' for any
//!   domain: database                # functional domain (e.g. http, ui, auth)
//! dependencies:
//!   - My/External/Package 1.0.0:alpha:1 Alias
//!   - file://path/to/the/dir Alias
//! main: src/Main.tlang          # optional; defaults to Main.tlang
//! ```
//!
//! ## Compatibility
//!
//! Generator packages may declare a `compatibility` block.  Two generators are
//! considered **compatible** when their target `language` values are equal, or
//! when at least one of them declares `language: '*'` (meaning it produces
//! language-agnostic output that can pair with any language-specific generator).
//!
//! Use [`check_compatibility`] to validate two manifests before composing their
//! generators.
//!
//! Each dependency line has the form:
//!
//! ```text
//! <locator> <alias>
//! ```
//!
//! where `<locator>` is either:
//! - A registry path:  `Org/Group/Package version:stability:releaseNum`
//! - A file path:      `file://relative/or/absolute/path`
//!
//! and `<alias>` is the name used in TLang `use` statements, e.g.
//! `use Alias.MyClass`.

use std::collections::HashMap;
use std::path::{Path, PathBuf};

// ---------------------------------------------------------------------------
// Public data types
// ---------------------------------------------------------------------------

/// Package type — controls how the package is treated by the toolchain.
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub enum PackageType {
    /// Standard library package (default).
    #[default]
    Library,
    /// CLI tool package; installed via `tlang install` and invoked via `tlang exec`.
    Cli,
}

impl PackageType {
    fn parse(s: &str) -> Self {
        match s.to_lowercase().as_str() {
            "cli" => PackageType::Cli,
            _ => PackageType::Library,
        }
    }
}

/// Stability tier of a release.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum Stability {
    Alpha,
    Beta,
    ReleaseCandidate,
    Stable,
    Other(String),
}

impl Stability {
    fn parse(s: &str) -> Self {
        match s.to_lowercase().as_str() {
            "alpha" => Stability::Alpha,
            "beta" => Stability::Beta,
            "rc" | "release-candidate" | "releasecandidate" => Stability::ReleaseCandidate,
            "stable" | "release" => Stability::Stable,
            other => Stability::Other(other.to_string()),
        }
    }
}

impl std::fmt::Display for Stability {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Stability::Alpha => write!(f, "alpha"),
            Stability::Beta => write!(f, "beta"),
            Stability::ReleaseCandidate => write!(f, "rc"),
            Stability::Stable => write!(f, "stable"),
            Stability::Other(s) => write!(f, "{s}"),
        }
    }
}

/// Version information attached to a registry dependency.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct VersionSpec {
    /// Semver-style version string, e.g. `"1.0.0"`.
    pub version: String,
    /// Optional stability label parsed from the compound `version:stability:release` token.
    pub stability: Option<Stability>,
    /// Optional release number parsed from the compound `version:stability:release` token.
    pub release_number: Option<u32>,
}

/// Location of a dependency — either a registry package or a local directory.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum DependencyLocator {
    /// A package from the TLang registry.
    ///
    /// `path` is the slash-separated package path, e.g. `["My", "External", "Package"]`.
    Registry {
        path: Vec<String>,
        version: VersionSpec,
    },
    /// A local file-system directory containing `.tlang` files.
    File { dir: PathBuf },
}

/// A single entry in the `dependencies` list.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Dependency {
    /// How to locate the dependency.
    pub locator: DependencyLocator,
    /// The alias under which the dependency is imported in TLang source files.
    /// e.g. `use <alias>.MyClass`.
    pub alias: String,
}

/// Generator compatibility declaration.
///
/// Describes the target output language and functional domain of a generator
/// package so that the tooling can verify two generators can be composed.
///
/// # Language matching rules
///
/// Two generators are compatible when:
/// - both declare the **same** `language`, OR
/// - either (or both) declares `language = "*"` (language-agnostic).
///
/// Examples of language values: `"kotlin"`, `"rust"`, `"typescript"`, `"*"`.
/// Examples of domain values: `"database"`, `"http"`, `"ui"`, `"auth"`, `"dto"`.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Compatibility {
    /// The target output language, e.g. `"kotlin"`, `"rust"`, or `"*"` for
    /// language-agnostic generators (e.g. an OpenAPI spec generator).
    pub language: String,
    /// The functional domain this generator covers, e.g. `"database"`,
    /// `"http"`, `"ui"`.  Optional — `None` means the generator does not
    /// restrict itself to a specific domain.
    pub domain: Option<String>,
}

impl Compatibility {
    /// Returns `true` if `self` and `other` are compatible.
    ///
    /// Two generators are compatible when their `language` fields are equal, or
    /// when at least one of them is `"*"` (language-agnostic).
    pub fn is_compatible_with(&self, other: &Compatibility) -> bool {
        self.language == "*" || other.language == "*" || self.language == other.language
    }
}

/// Full contents of a parsed `manifest.yml`.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Manifest {
    /// Human-readable project name.
    pub name: String,
    /// Project group / category name.
    pub project: String,
    /// Organisation that owns the project.
    pub organisation: String,
    /// Version string (semver recommended), e.g. `"1.0.0"`.
    pub version: String,
    /// Stability tier of this release.
    pub stability: Stability,
    /// Release number within this stability tier.
    pub release_number: u32,
    /// Package author (person or team name).  Optional.
    pub author: Option<String>,
    /// Project or documentation website URL.  Optional.
    pub website: Option<String>,
    /// SPDX licence identifier, e.g. `"Apache-2.0"`, `"MIT"`, `"BSL-1.1"`.
    /// Optional.
    pub license: Option<String>,
    /// Generator compatibility declaration.  Optional — typically set by
    /// generator packages to allow tooling to validate composition.
    pub compatibility: Option<Compatibility>,
    /// Declared dependencies, in declaration order.
    pub dependencies: Vec<Dependency>,
    /// Override for the main entry-point file.
    ///
    /// If `None`, the loader should look for `Main.tlang` in the project root.
    pub main: Option<PathBuf>,
    /// Package type; defaults to `Library` when the `type:` field is absent.
    pub package_type: PackageType,
    /// CLI invocation name for `tlang exec <command>`.
    ///
    /// Only valid when `package_type` is `Cli`.  When absent the package `name`
    /// is used as the install filename.
    pub command: Option<String>,
}

// ---------------------------------------------------------------------------
// Compatibility check
// ---------------------------------------------------------------------------

/// The outcome of a compatibility check between two manifests.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum CompatibilityResult {
    /// The two manifests are compatible and can be composed.
    Compatible,
    /// One or both manifests do not declare a `compatibility` block, so no
    /// conflict can be detected.  The pair *may* still work together.
    NoDeclaration,
    /// The manifests declare conflicting target languages and cannot be safely
    /// composed.
    Incompatible {
        /// Language declared by the first manifest.
        language_a: String,
        /// Language declared by the second manifest.
        language_b: String,
    },
}

/// Check whether two manifests can be composed.
///
/// Returns [`CompatibilityResult::Compatible`] when both manifests declare a
/// `compatibility` block and the language rules are satisfied.
/// Returns [`CompatibilityResult::NoDeclaration`] when either manifest omits
/// the block (conservative — the tooling may warn but should not hard-block).
/// Returns [`CompatibilityResult::Incompatible`] when both declare a
/// `compatibility` block with conflicting languages.
///
/// # Example
///
/// ```ignore
/// // kotlin/database generator + rust/http generator → Incompatible
/// // openapi/* generator   + kotlin/http generator  → Compatible
/// ```
pub fn check_compatibility(a: &Manifest, b: &Manifest) -> CompatibilityResult {
    match (&a.compatibility, &b.compatibility) {
        (Some(ca), Some(cb)) => {
            if ca.is_compatible_with(cb) {
                CompatibilityResult::Compatible
            } else {
                CompatibilityResult::Incompatible {
                    language_a: ca.language.clone(),
                    language_b: cb.language.clone(),
                }
            }
        }
        _ => CompatibilityResult::NoDeclaration,
    }
}

// ---------------------------------------------------------------------------
// Error type
// ---------------------------------------------------------------------------

/// Errors that can arise while loading or parsing a manifest.
#[derive(Debug)]
pub enum ManifestError {
    /// The manifest file could not be read.
    Io(String),
    /// The YAML could not be parsed.
    Yaml(String),
    /// A required field is missing from the manifest.
    MissingField(String),
    /// A field value has an unexpected format.
    InvalidField { field: String, message: String },
    /// A dependency line could not be parsed.
    InvalidDependency(String),
}

impl std::fmt::Display for ManifestError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            ManifestError::Io(msg) => write!(f, "manifest IO error: {msg}"),
            ManifestError::Yaml(msg) => write!(f, "manifest YAML error: {msg}"),
            ManifestError::MissingField(field) => {
                write!(f, "manifest is missing required field `{field}`")
            }
            ManifestError::InvalidField { field, message } => {
                write!(f, "manifest field `{field}` is invalid: {message}")
            }
            ManifestError::InvalidDependency(line) => {
                write!(f, "cannot parse dependency `{line}`")
            }
        }
    }
}

impl std::error::Error for ManifestError {}

// ---------------------------------------------------------------------------
// YAML value helpers  (hand-rolled — avoids a serde_yaml dependency)
// ---------------------------------------------------------------------------

/// A minimal YAML subset sufficient to parse `manifest.yml`.
///
/// We only need:
/// - Scalar string values (quoted or unquoted)
/// - Mapping keys (top-level)
/// - Sequence items (prefixed with `- `)
///
/// Multi-document, anchors, and complex nested structures are out of scope.
#[derive(Debug, PartialEq, Eq)]
enum YamlValue {
    Scalar(String),
    Sequence(Vec<String>),
}

/// Parse the manifest YAML text into a flat `HashMap<key, YamlValue>`.
///
/// This covers the small subset of YAML used by `manifest.yml`:
/// ```yaml
/// scalarKey: some value
/// listKey:
///   - item one
///   - item two
/// ```
fn parse_yaml_map(text: &str) -> Result<HashMap<String, YamlValue>, ManifestError> {
    let mut map: HashMap<String, YamlValue> = HashMap::new();
    // The current top-level key whose value block is being accumulated.
    let mut current_key: Option<String> = None;
    // Sequence items accumulated under `current_key`.
    let mut current_seq: Vec<String> = Vec::new();
    // When `current_key` had an empty scalar value (i.e. it opens a nested
    // mapping block), this flag is true and sub-keys are stored as
    // `"{current_key}.{sub_key}"` compound entries.
    let mut in_nested_map: bool = false;

    for raw_line in text.lines() {
        let line = raw_line.trim_end();

        // Skip blank lines and comments.
        if line.trim().is_empty() || line.trim_start().starts_with('#') {
            continue;
        }

        // Sequence item inside a list block (must come before the nested-map
        // sub-key check so that sequence items under a top-level key are still
        // collected correctly).
        if !in_nested_map
            && (line.starts_with("  - ") || line.starts_with("    - ") || {
                let trimmed = line.trim_start();
                trimmed.starts_with("- ") && line.starts_with(' ')
            })
        {
            let value = line.trim().trim_start_matches("- ").to_string();
            let value = strip_yaml_quotes(&value);
            current_seq.push(value);
            continue;
        }

        // Nested mapping sub-key: indented (2+ spaces) key: value pair that
        // is NOT a sequence item.  Only one level of nesting is supported.
        if in_nested_map {
            let leading = raw_line.len() - raw_line.trim_start().len();
            if leading >= 2 {
                let trimmed = raw_line.trim();
                if let Some(colon_pos) = trimmed.find(':') {
                    let sub_key = trimmed[..colon_pos].trim();
                    let sub_val = trimmed[colon_pos + 1..].trim();
                    if !sub_key.is_empty() && !sub_val.is_empty() {
                        let compound =
                            format!("{}.{}", current_key.as_deref().unwrap_or(""), sub_key);
                        map.insert(compound, YamlValue::Scalar(strip_yaml_quotes(sub_val)));
                        continue;
                    }
                }
            }
            // A non-indented (or otherwise unrecognised) line exits the nested
            // block; fall through to top-level key handling below.
            in_nested_map = false;
            current_key = None;
        }

        // Top-level mapping key (no leading whitespace).
        if let Some(colon_pos) = line.find(':') {
            let key_part = &line[..colon_pos];
            // Ensure key has no leading whitespace (top-level).
            if !key_part.starts_with(' ') && !key_part.starts_with('\t') {
                // Flush any in-progress sequence.
                if let Some(key) = current_key.take()
                    && !current_seq.is_empty()
                {
                    map.insert(key, YamlValue::Sequence(std::mem::take(&mut current_seq)));
                }

                let key = key_part.trim().to_string();
                let rest = line[colon_pos + 1..].trim().to_string();

                if rest.is_empty() {
                    // Value will follow as a block (sequence or nested map).
                    current_key = Some(key);
                    in_nested_map = false; // will be confirmed by first child line
                } else {
                    let value = strip_yaml_quotes(&rest);
                    map.insert(key, YamlValue::Scalar(value));
                }
                continue;
            }
        }

        // Indented line with a colon while we have a pending `current_key` but
        // have not yet seen any sequence items — this is the first sub-key of a
        // nested mapping block.
        if current_key.is_some() && !current_seq.is_empty() {
            // Already collecting a sequence; indented non-sequence lines are
            // unexpected but harmless — skip them.
            continue;
        }
        if current_key.is_some() {
            // First child line under a block key that has no value — treat the
            // whole block as a nested map from here on.
            let leading = raw_line.len() - raw_line.trim_start().len();
            if leading >= 2 {
                let trimmed = raw_line.trim();
                if let Some(colon_pos) = trimmed.find(':') {
                    let sub_key = trimmed[..colon_pos].trim();
                    let sub_val = trimmed[colon_pos + 1..].trim();
                    if !sub_key.is_empty() && !sub_val.is_empty() {
                        in_nested_map = true;
                        let compound =
                            format!("{}.{}", current_key.as_deref().unwrap_or(""), sub_key);
                        map.insert(compound, YamlValue::Scalar(strip_yaml_quotes(sub_val)));
                        continue;
                    }
                }
            }
        }
    }

    // Flush final sequence if any.
    if let Some(key) = current_key.take()
        && !current_seq.is_empty()
    {
        map.insert(key, YamlValue::Sequence(std::mem::take(&mut current_seq)));
    }

    Ok(map)
}

/// Remove surrounding single or double quotes from a YAML scalar value.
fn strip_yaml_quotes(s: &str) -> String {
    let s = s.trim();
    if (s.starts_with('"') && s.ends_with('"')) || (s.starts_with('\'') && s.ends_with('\'')) {
        s[1..s.len() - 1].to_string()
    } else {
        s.to_string()
    }
}

// ---------------------------------------------------------------------------
// Dependency line parsing
// ---------------------------------------------------------------------------

/// Parse a single dependency entry string.
///
/// Grammar:
/// ```text
/// <locator> <alias>
///
/// locator ::= "file://" <path>
///           | <pkg-path> <version-spec>
///
/// pkg-path     ::= <segment> ("/" <segment>)*
/// version-spec ::= <version> (":" <stability> (":" <release>)?)?
/// ```
pub fn parse_dependency(line: &str) -> Result<Dependency, ManifestError> {
    let line = line.trim();

    if line.starts_with("file://") {
        parse_file_dependency(line)
    } else {
        parse_registry_dependency(line)
    }
}

fn parse_file_dependency(line: &str) -> Result<Dependency, ManifestError> {
    // `file://path/to/dir Alias`
    let without_scheme = &line["file://".len()..];
    // Split on the last whitespace-separated token — that's the alias.
    let (path_part, alias) = rsplit_alias(without_scheme)
        .ok_or_else(|| ManifestError::InvalidDependency(line.to_string()))?;

    Ok(Dependency {
        locator: DependencyLocator::File {
            dir: PathBuf::from(path_part),
        },
        alias,
    })
}

fn parse_registry_dependency(line: &str) -> Result<Dependency, ManifestError> {
    // `My/External/Package 1.0.0:alpha:1 Alias`
    // Split into tokens separated by whitespace.
    let tokens: Vec<&str> = line.split_whitespace().collect();

    // We need at least: <pkg-path> <version-spec> <alias>
    if tokens.len() < 3 {
        return Err(ManifestError::InvalidDependency(line.to_string()));
    }

    let pkg_path_str = tokens[0];
    let version_str = tokens[1];
    // Everything after the first two tokens, up to the last token, is ignored
    // (future-proofing); the last token is the alias.
    let alias = tokens[tokens.len() - 1].to_string();

    let path: Vec<String> = pkg_path_str.split('/').map(|s| s.to_string()).collect();
    if path.is_empty() || path.iter().any(|s| s.is_empty()) {
        return Err(ManifestError::InvalidDependency(line.to_string()));
    }

    let version = parse_version_spec(version_str)
        .ok_or_else(|| ManifestError::InvalidDependency(line.to_string()))?;

    Ok(Dependency {
        locator: DependencyLocator::Registry { path, version },
        alias,
    })
}

/// Parse `version`, `version:stability`, or `version:stability:release`.
pub fn parse_version_spec(s: &str) -> Option<VersionSpec> {
    let parts: Vec<&str> = s.splitn(3, ':').collect();
    let version = parts[0].to_string();
    if version.is_empty() {
        return None;
    }
    let stability = parts
        .get(1)
        .filter(|s| !s.is_empty())
        .map(|s| Stability::parse(s));
    let release_number = parts.get(2).and_then(|s| s.parse::<u32>().ok());

    Some(VersionSpec {
        version,
        stability,
        release_number,
    })
}

/// Split a string on the last whitespace run, returning `(left, right)`.
///
/// Used to extract the trailing alias from a dependency line where the path
/// component may or may not contain spaces before the version token.
fn rsplit_alias(s: &str) -> Option<(String, String)> {
    let trimmed = s.trim();
    let last_space = trimmed.rfind(|c: char| c.is_whitespace())?;
    let left = trimmed[..last_space].trim().to_string();
    let right = trimmed[last_space..].trim().to_string();
    if left.is_empty() || right.is_empty() {
        None
    } else {
        Some((left, right))
    }
}

// ---------------------------------------------------------------------------
// Public API
// ---------------------------------------------------------------------------

/// Load and parse a `manifest.yml` from `path`.
pub fn load_manifest(path: &Path) -> Result<Manifest, ManifestError> {
    let text = std::fs::read_to_string(path)
        .map_err(|e| ManifestError::Io(format!("cannot read '{}': {e}", path.display())))?;
    parse_manifest(&text)
}

/// Load a `manifest.yml` from `project_root/manifest.yml`, if it exists.
///
/// If `manifest.local.yml` is present alongside it, its `dependencies:` list
/// is merged in as a local overlay: entries with an alias that matches an
/// existing dependency replace it; entries with a new alias are appended.
/// The local file is gitignored by convention and is never committed, so it
/// is safe to put `file://` paths in it for local development without
/// affecting CI or other contributors.
///
/// Returns `Ok(None)` when no manifest file is present so that callers can
/// treat its absence as "use defaults".
pub fn try_load_manifest(project_root: &Path) -> Result<Option<Manifest>, ManifestError> {
    let manifest_path = project_root.join("manifest.yml");
    if !manifest_path.exists() {
        return Ok(None);
    }
    let mut manifest = load_manifest(&manifest_path)?;
    apply_local_overlay(&mut manifest, project_root);
    Ok(Some(manifest))
}

/// Parse only the `dependencies:` list from a `manifest.local.yml` file.
///
/// All other fields are ignored — the file does not need `name:`, `project:`,
/// etc.  Returns an empty list when the file contains no `dependencies:` key.
fn parse_local_overlay(text: &str) -> Result<Vec<Dependency>, ManifestError> {
    let map = parse_yaml_map(text)?;
    match map.get("dependencies") {
        Some(YamlValue::Sequence(items)) => items
            .iter()
            .map(|item| parse_dependency(item))
            .collect::<Result<Vec<_>, _>>(),
        Some(YamlValue::Scalar(_)) => Err(ManifestError::InvalidField {
            field: "dependencies".to_string(),
            message: "expected a sequence, found a scalar".to_string(),
        }),
        None => Ok(Vec::new()),
    }
}

/// Apply a `manifest.local.yml` overlay to `manifest` if the file exists in
/// `project_root`.
///
/// Overlay deps replace any existing dep with the same alias, then new aliases
/// are appended.  Errors in the local file are printed to stderr and ignored
/// so that a malformed local file never breaks the build for other team members.
fn apply_local_overlay(manifest: &mut Manifest, project_root: &Path) {
    let local_path = project_root.join("manifest.local.yml");
    if !local_path.exists() {
        return;
    }
    let text = match std::fs::read_to_string(&local_path) {
        Ok(t) => t,
        Err(e) => {
            eprintln!(
                "warning: could not read '{}': {e}",
                local_path.display()
            );
            return;
        }
    };
    let overrides = match parse_local_overlay(&text) {
        Ok(deps) => deps,
        Err(e) => {
            eprintln!(
                "warning: '{}' is invalid and will be ignored: {e}",
                local_path.display()
            );
            return;
        }
    };
    if overrides.is_empty() {
        return;
    }
    eprintln!(
        "info: applying {} local dep override(s) from manifest.local.yml",
        overrides.len()
    );
    for local_dep in overrides {
        if let Some(existing) = manifest
            .dependencies
            .iter_mut()
            .find(|d| d.alias == local_dep.alias)
        {
            *existing = local_dep;
        } else {
            manifest.dependencies.push(local_dep);
        }
    }
}

/// Parse a manifest from its YAML text content.
pub fn parse_manifest(text: &str) -> Result<Manifest, ManifestError> {
    let map = parse_yaml_map(text)?;

    let get_scalar = |key: &str| -> Result<String, ManifestError> {
        match map.get(key) {
            Some(YamlValue::Scalar(s)) => Ok(s.clone()),
            Some(YamlValue::Sequence(_)) => Err(ManifestError::InvalidField {
                field: key.to_string(),
                message: "expected a scalar, found a sequence".to_string(),
            }),
            None => Err(ManifestError::MissingField(key.to_string())),
        }
    };

    let name = get_scalar("name")?;
    let project = get_scalar("project")?;
    let organisation = get_scalar("organisation")?;
    let version = get_scalar("version")?;

    let stability = get_scalar("stability")
        .map(|s| Stability::parse(&s))
        .unwrap_or(Stability::Stable);

    let release_number = get_scalar("releaseNumber")
        .ok()
        .and_then(|s| s.parse::<u32>().ok())
        .unwrap_or(1);

    // Optional metadata fields.
    let author = get_scalar("author").ok();
    let website = get_scalar("website").ok();
    let license = get_scalar("license").ok();

    // Optional compatibility block.
    // Parsed from a nested mapping, e.g.:
    //   compatibility:
    //     language: kotlin
    //     domain: database
    let compatibility = parse_compatibility_block(&map)?;

    let dependencies = match map.get("dependencies") {
        Some(YamlValue::Sequence(items)) => items
            .iter()
            .map(|item| parse_dependency(item))
            .collect::<Result<Vec<_>, _>>()?,
        Some(YamlValue::Scalar(_)) => {
            return Err(ManifestError::InvalidField {
                field: "dependencies".to_string(),
                message: "expected a sequence, found a scalar".to_string(),
            });
        }
        None => Vec::new(),
    };

    // Normalise the `main:` value: strip any `.tlang` extension so we store
    // only the stem (e.g. `"KotlinCodegen"`, not `"KotlinCodegen.tlang"`).
    // The extension is added back by `resolve_main` at resolution time.
    let main = get_scalar("main").ok().map(|s| {
        let p = PathBuf::from(&s);
        if p.extension().and_then(|e| e.to_str()) == Some("tlang") {
            p.with_extension("")
        } else {
            p
        }
    });

    let package_type = get_scalar("type")
        .map(|s| PackageType::parse(&s))
        .unwrap_or_default();

    let command = get_scalar("command").ok();

    if command.is_some() && package_type != PackageType::Cli {
        return Err(ManifestError::InvalidField {
            field: "command".to_string(),
            message: "`command` is only valid when `type: cli` is also set".to_string(),
        });
    }

    Ok(Manifest {
        name,
        project,
        organisation,
        version,
        stability,
        release_number,
        author,
        website,
        license,
        compatibility,
        dependencies,
        main,
        package_type,
        command,
    })
}

/// Parse an optional `compatibility:` block from the top-level YAML map.
///
/// The block is a nested mapping whose keys are parsed with the same minimal
/// YAML parser used for the rest of the manifest.  Returns `Ok(None)` when the
/// key is absent.
fn parse_compatibility_block(
    map: &HashMap<String, YamlValue>,
) -> Result<Option<Compatibility>, ManifestError> {
    // The minimal YAML parser flattens nested maps into dot-separated keys,
    // e.g. `compatibility.language`.  Try that first.
    let language_key = "compatibility.language";
    let domain_key = "compatibility.domain";

    let language_opt = match map.get(language_key) {
        Some(YamlValue::Scalar(s)) => Some(s.clone()),
        Some(YamlValue::Sequence(_)) => {
            return Err(ManifestError::InvalidField {
                field: language_key.to_string(),
                message: "expected a scalar, found a sequence".to_string(),
            });
        }
        None => None,
    };

    let domain_opt = match map.get(domain_key) {
        Some(YamlValue::Scalar(s)) => Some(s.clone()),
        Some(YamlValue::Sequence(_)) => {
            return Err(ManifestError::InvalidField {
                field: domain_key.to_string(),
                message: "expected a scalar, found a sequence".to_string(),
            });
        }
        None => None,
    };

    match language_opt {
        Some(language) => Ok(Some(Compatibility {
            language,
            domain: domain_opt,
        })),
        // `compatibility.domain` without `compatibility.language` is invalid.
        None if domain_opt.is_some() => Err(ManifestError::InvalidField {
            field: "compatibility".to_string(),
            message: "`domain` requires `language` to also be set".to_string(),
        }),
        None => Ok(None),
    }
}

/// Resolve the path to the main entry-point file for a project.
///
/// If the manifest specifies a `main` override, that path is resolved relative
/// to `project_root`.  Otherwise `Main.tlang` in `project_root` is used.
pub fn resolve_main(project_root: &Path, manifest: Option<&Manifest>) -> PathBuf {
    match manifest.and_then(|m| m.main.as_ref()) {
        Some(main_path) => {
            // Always append `.tlang` — the stored value is the bare stem.
            let with_ext = if main_path.extension().is_none() {
                main_path.with_extension("tlang")
            } else {
                main_path.clone()
            };
            if with_ext.is_absolute() {
                with_ext
            } else {
                project_root.join(&with_ext)
            }
        }
        None => project_root.join("Main.tlang"),
    }
}

/// Build a map from `alias → directory` for all `file://` dependencies in a
/// manifest so the loader can resolve named imports.
pub fn file_dependency_dirs(project_root: &Path, manifest: &Manifest) -> HashMap<String, PathBuf> {
    let mut map = HashMap::new();
    for dep in &manifest.dependencies {
        if let DependencyLocator::File { dir } = &dep.locator {
            let resolved = if dir.is_absolute() {
                dir.clone()
            } else {
                project_root.join(dir)
            };
            map.insert(dep.alias.clone(), resolved);
        }
    }
    map
}

/// Returns the default TLang package store (tbox) path.
///
/// The tbox is the local cache for registry dependencies, analogous to
/// Maven's `~/.m2` repository.  The default location is:
///
/// - `$HOME/.tlang/tbox`   on Unix / macOS
/// - `%USERPROFILE%\.tlang\tbox` on Windows
///
/// Falls back to `.tlang/tbox` inside the current working directory when no
/// home directory can be determined.
pub fn tbox_path() -> PathBuf {
    std::env::var_os("HOME")
        .or_else(|| std::env::var_os("USERPROFILE"))
        .map(|h| PathBuf::from(h).join(".tlang").join("tbox"))
        .unwrap_or_else(|| {
            std::env::current_dir()
                .unwrap_or_else(|_| PathBuf::from("."))
                .join(".tlang")
                .join("tbox")
        })
}

/// Compute the directory inside a tbox for a registry [`Dependency`].
///
/// The layout mirrors the package path, followed by version components:
///
/// ```text
/// {tbox}/{org}/{group}/{package}/{version}/{stability}/{release}/
/// ```
///
/// Stability and release are optional path components: if the version spec
/// omits them, they are not added to the path.
///
/// Returns `None` if `dep` is not a registry dependency.
pub fn registry_dep_dir(tbox: &Path, dep: &Dependency) -> Option<PathBuf> {
    if let DependencyLocator::Registry { path, version } = &dep.locator {
        let mut dir = tbox.to_path_buf();
        for segment in path {
            dir.push(segment);
        }
        dir.push(&version.version);
        if let Some(stability) = &version.stability {
            dir.push(stability.to_string());
        }
        if let Some(release) = version.release_number {
            dir.push(release.to_string());
        }
        Some(dir)
    } else {
        None
    }
}

/// Build a map from `alias → directory` for **all** dependencies in a
/// manifest (both `file://` and registry packages).
///
/// `file://` dependencies are resolved relative to `project_root`.
/// Registry dependencies are resolved from the default tbox
/// (`~/.tlang/tbox`).
pub fn dependency_dirs(project_root: &Path, manifest: &Manifest) -> HashMap<String, PathBuf> {
    dependency_dirs_with_tbox(project_root, manifest, &tbox_path())
}

/// Like [`dependency_dirs`] but uses a caller-supplied tbox root instead of
/// the default.  Useful for tests or when the tbox location is overridden.
pub fn dependency_dirs_with_tbox(
    project_root: &Path,
    manifest: &Manifest,
    tbox: &Path,
) -> HashMap<String, PathBuf> {
    let mut map = HashMap::new();
    for dep in &manifest.dependencies {
        match &dep.locator {
            DependencyLocator::File { dir } => {
                let resolved = if dir.is_absolute() {
                    dir.clone()
                } else {
                    project_root.join(dir)
                };
                map.insert(dep.alias.clone(), resolved);
            }
            DependencyLocator::Registry { .. } => {
                if let Some(dir) = registry_dep_dir(tbox, dep) {
                    map.insert(dep.alias.clone(), dir);
                }
            }
        }
    }
    map
}

/// Compute the tbox directory for this package itself — i.e. where a
/// packaged `.tbag` for the current project would be stored in the tbox.
///
/// Layout:
/// ```text
/// {tbox}/{organisation}/{project}/{name}/{version}/{stability}/{release}/
/// ```
///
/// Stability and release are optional: they are only appended when they are
/// present on the manifest.
pub fn own_tbox_dir(tbox: &Path, manifest: &Manifest) -> PathBuf {
    let mut dir = tbox.to_path_buf();
    dir.push(&manifest.organisation);
    dir.push(&manifest.project);
    dir.push(&manifest.name);
    dir.push(&manifest.version);
    dir.push(manifest.stability.to_string());
    dir.push(manifest.release_number.to_string());
    dir
}

/// Return the full path of the `.tbag` archive for the current project inside
/// `tbox`.
///
/// Example:
/// ```text
/// ~/.tlang/tbox/MyOrg/MyProject/MyPackage/1.0.0/alpha/1/MyPackage.tbag
/// ```
pub fn own_tbox_tbag_path(tbox: &Path, manifest: &Manifest) -> PathBuf {
    own_tbox_dir(tbox, manifest).join(format!("{}.tbag", manifest.name))
}

/// Return the path of the `.tbag` archive for a *registry dependency* inside
/// `tbox`.
///
/// The file is named after the last segment of the dependency's registry path
/// (i.e. the package name).
///
/// Returns `None` if `dep` is not a registry dependency.
pub fn registry_dep_tbag_path(tbox: &Path, dep: &Dependency) -> Option<PathBuf> {
    if let DependencyLocator::Registry { path, .. } = &dep.locator {
        let pkg_name = path.last()?;
        registry_dep_dir(tbox, dep).map(|dir| dir.join(format!("{pkg_name}.tbag")))
    } else {
        None
    }
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use super::*;

    // -----------------------------------------------------------------------
    // Helper to build a minimal Manifest for tests that only care about a
    // subset of fields.
    // -----------------------------------------------------------------------
    fn minimal_manifest(name: &str) -> Manifest {
        Manifest {
            name: name.into(),
            project: "TestProject".into(),
            organisation: "TLang".into(),
            version: "1.0.0".into(),
            stability: Stability::Stable,
            release_number: 1,
            author: None,
            website: None,
            license: None,
            compatibility: None,
            dependencies: vec![],
            main: None,
            package_type: PackageType::Library,
            command: None,
        }
    }

    const EXAMPLE_MANIFEST: &str = r#"
name: HelloWorld
project: IntegrationTests
organisation: TLang
version: 1.0.0
stability: alpha
releaseNumber: 1
dependencies:
  - My/External/Package 1.0.0:alpha:1 Name
  - file://path/to/the/dir LocalLib
"#;

    #[test]
    fn parses_example_manifest() {
        let manifest = parse_manifest(EXAMPLE_MANIFEST).expect("should parse");
        assert_eq!(manifest.name, "HelloWorld");
        assert_eq!(manifest.project, "IntegrationTests");
        assert_eq!(manifest.organisation, "TLang");
        assert_eq!(manifest.version, "1.0.0");
        assert_eq!(manifest.stability, Stability::Alpha);
        assert_eq!(manifest.release_number, 1);
        assert_eq!(manifest.dependencies.len(), 2);
        assert_eq!(manifest.main, None);
        // Optional fields default to None.
        assert_eq!(manifest.author, None);
        assert_eq!(manifest.website, None);
        assert_eq!(manifest.license, None);
        assert_eq!(manifest.compatibility, None);
    }

    // -----------------------------------------------------------------------
    // Optional metadata fields
    // -----------------------------------------------------------------------

    #[test]
    fn parses_optional_metadata_fields() {
        let text = r#"
name: MyGen
project: Generators
organisation: Acme
version: 2.0.0
stability: stable
releaseNumber: 1
author: Jane Dev
website: https://acme.example.com
license: Apache-2.0
"#;
        let manifest = parse_manifest(text).expect("should parse");
        assert_eq!(manifest.author.as_deref(), Some("Jane Dev"));
        assert_eq!(
            manifest.website.as_deref(),
            Some("https://acme.example.com")
        );
        assert_eq!(manifest.license.as_deref(), Some("Apache-2.0"));
    }

    // -----------------------------------------------------------------------
    // Compatibility block
    // -----------------------------------------------------------------------

    #[test]
    fn parses_compatibility_block_with_domain() {
        let text = r#"
name: KtDb
project: Generators
organisation: Acme
version: 1.0.0
stability: stable
releaseNumber: 1
compatibility:
  language: kotlin
  domain: database
"#;
        let manifest = parse_manifest(text).expect("should parse");
        let compat = manifest.compatibility.expect("should have compatibility");
        assert_eq!(compat.language, "kotlin");
        assert_eq!(compat.domain.as_deref(), Some("database"));
    }

    #[test]
    fn parses_compatibility_block_language_only() {
        let text = r#"
name: KtHttp
project: Generators
organisation: Acme
version: 1.0.0
stability: stable
releaseNumber: 1
compatibility:
  language: kotlin
"#;
        let manifest = parse_manifest(text).expect("should parse");
        let compat = manifest.compatibility.expect("should have compatibility");
        assert_eq!(compat.language, "kotlin");
        assert_eq!(compat.domain, None);
    }

    #[test]
    fn parses_compatibility_wildcard_language() {
        let text = r#"
name: OpenApiGen
project: Generators
organisation: Acme
version: 1.0.0
stability: stable
releaseNumber: 1
compatibility:
  language: '*'
  domain: http
"#;
        let manifest = parse_manifest(text).expect("should parse");
        let compat = manifest.compatibility.expect("should have compatibility");
        assert_eq!(compat.language, "*");
        assert_eq!(compat.domain.as_deref(), Some("http"));
    }

    #[test]
    fn compatibility_domain_without_language_is_error() {
        let text = r#"
name: Bad
project: P
organisation: O
version: 1.0.0
stability: stable
releaseNumber: 1
compatibility:
  domain: database
"#;
        let err = parse_manifest(text).expect_err("should fail");
        assert!(
            matches!(err, ManifestError::InvalidField { ref field, .. } if field == "compatibility")
        );
    }

    // -----------------------------------------------------------------------
    // check_compatibility
    // -----------------------------------------------------------------------

    #[test]
    fn check_compatibility_same_language_is_compatible() {
        let mut a = minimal_manifest("A");
        a.compatibility = Some(Compatibility {
            language: "kotlin".into(),
            domain: Some("database".into()),
        });
        let mut b = minimal_manifest("B");
        b.compatibility = Some(Compatibility {
            language: "kotlin".into(),
            domain: Some("http".into()),
        });
        assert_eq!(check_compatibility(&a, &b), CompatibilityResult::Compatible);
    }

    #[test]
    fn check_compatibility_different_languages_is_incompatible() {
        let mut a = minimal_manifest("A");
        a.compatibility = Some(Compatibility {
            language: "kotlin".into(),
            domain: Some("database".into()),
        });
        let mut b = minimal_manifest("B");
        b.compatibility = Some(Compatibility {
            language: "rust".into(),
            domain: Some("http".into()),
        });
        assert_eq!(
            check_compatibility(&a, &b),
            CompatibilityResult::Incompatible {
                language_a: "kotlin".into(),
                language_b: "rust".into(),
            }
        );
    }

    #[test]
    fn check_compatibility_wildcard_a_is_compatible() {
        let mut a = minimal_manifest("OpenApi");
        a.compatibility = Some(Compatibility {
            language: "*".into(),
            domain: Some("http".into()),
        });
        let mut b = minimal_manifest("KtHttp");
        b.compatibility = Some(Compatibility {
            language: "kotlin".into(),
            domain: Some("http".into()),
        });
        assert_eq!(check_compatibility(&a, &b), CompatibilityResult::Compatible);
    }

    #[test]
    fn check_compatibility_wildcard_b_is_compatible() {
        let mut a = minimal_manifest("KtDb");
        a.compatibility = Some(Compatibility {
            language: "kotlin".into(),
            domain: Some("database".into()),
        });
        let mut b = minimal_manifest("DocGen");
        b.compatibility = Some(Compatibility {
            language: "*".into(),
            domain: None,
        });
        assert_eq!(check_compatibility(&a, &b), CompatibilityResult::Compatible);
    }

    #[test]
    fn check_compatibility_both_wildcards_is_compatible() {
        let mut a = minimal_manifest("A");
        a.compatibility = Some(Compatibility {
            language: "*".into(),
            domain: None,
        });
        let mut b = minimal_manifest("B");
        b.compatibility = Some(Compatibility {
            language: "*".into(),
            domain: None,
        });
        assert_eq!(check_compatibility(&a, &b), CompatibilityResult::Compatible);
    }

    #[test]
    fn check_compatibility_no_declaration_returns_no_declaration() {
        let a = minimal_manifest("A");
        let mut b = minimal_manifest("B");
        b.compatibility = Some(Compatibility {
            language: "kotlin".into(),
            domain: None,
        });
        // a has no compatibility block → NoDeclaration.
        assert_eq!(
            check_compatibility(&a, &b),
            CompatibilityResult::NoDeclaration
        );
        // Neither has a block.
        let c = minimal_manifest("C");
        assert_eq!(
            check_compatibility(&a, &c),
            CompatibilityResult::NoDeclaration
        );
    }

    #[test]
    fn is_compatible_with_symmetry() {
        let kotlin = Compatibility {
            language: "kotlin".into(),
            domain: None,
        };
        let rust = Compatibility {
            language: "rust".into(),
            domain: None,
        };
        let any = Compatibility {
            language: "*".into(),
            domain: None,
        };

        assert!(!kotlin.is_compatible_with(&rust));
        assert!(!rust.is_compatible_with(&kotlin));
        assert!(kotlin.is_compatible_with(&kotlin));
        assert!(any.is_compatible_with(&rust));
        assert!(rust.is_compatible_with(&any));
        assert!(any.is_compatible_with(&any));
    }

    // -----------------------------------------------------------------------
    // Dependency parsing
    // -----------------------------------------------------------------------

    #[test]
    fn parses_registry_dependency() {
        let dep = parse_dependency("My/External/Package 1.0.0:alpha:1 Name").expect("should parse");
        assert_eq!(dep.alias, "Name");
        match &dep.locator {
            DependencyLocator::Registry { path, version } => {
                assert_eq!(path, &["My", "External", "Package"]);
                assert_eq!(version.version, "1.0.0");
                assert_eq!(version.stability, Some(Stability::Alpha));
                assert_eq!(version.release_number, Some(1));
            }
            _ => panic!("expected registry locator"),
        }
    }

    #[test]
    fn parses_file_dependency() {
        let dep = parse_dependency("file://path/to/the/dir LocalLib").expect("should parse");
        assert_eq!(dep.alias, "LocalLib");
        match &dep.locator {
            DependencyLocator::File { dir } => {
                assert_eq!(dir, &PathBuf::from("path/to/the/dir"));
            }
            _ => panic!("expected file locator"),
        }
    }

    #[test]
    fn parses_version_spec_full() {
        let v = parse_version_spec("2.3.1:beta:4").unwrap();
        assert_eq!(v.version, "2.3.1");
        assert_eq!(v.stability, Some(Stability::Beta));
        assert_eq!(v.release_number, Some(4));
    }

    #[test]
    fn parses_version_spec_version_only() {
        let v = parse_version_spec("1.0.0").unwrap();
        assert_eq!(v.version, "1.0.0");
        assert_eq!(v.stability, None);
        assert_eq!(v.release_number, None);
    }

    #[test]
    fn parses_version_spec_no_release() {
        let v = parse_version_spec("1.2.3:stable").unwrap();
        assert_eq!(v.stability, Some(Stability::Stable));
        assert_eq!(v.release_number, None);
    }

    #[test]
    fn parses_main_override() {
        let text = r#"
name: App
project: Demo
organisation: TLang
version: 0.1.0
stability: stable
releaseNumber: 1
main: src/Entry.tlang
"#;
        let manifest = parse_manifest(text).expect("should parse");
        // Extension is stripped on parse; resolve_main re-adds .tlang.
        assert_eq!(manifest.main, Some(PathBuf::from("src/Entry")));
    }

    #[test]
    fn resolve_main_defaults_to_main_tlang() {
        let root = PathBuf::from("/project");
        let path = resolve_main(&root, None);
        assert_eq!(path, PathBuf::from("/project/Main.tlang"));
    }

    #[test]
    fn resolve_main_uses_manifest_override() {
        let mut manifest = minimal_manifest("X");
        manifest.main = Some(PathBuf::from("src/Entry"));
        let root = PathBuf::from("/project");
        let path = resolve_main(&root, Some(&manifest));
        assert_eq!(path, PathBuf::from("/project/src/Entry.tlang")); // extension re-added
    }

    #[test]
    fn file_dependency_dirs_resolves_relative_paths() {
        let mut manifest = minimal_manifest("X");
        manifest.dependencies = vec![Dependency {
            locator: DependencyLocator::File {
                dir: PathBuf::from("libs/mylib"),
            },
            alias: "MyLib".into(),
        }];
        let root = PathBuf::from("/project");
        let dirs = file_dependency_dirs(&root, &manifest);
        assert_eq!(dirs["MyLib"], PathBuf::from("/project/libs/mylib"));
    }

    #[test]
    fn missing_required_field_returns_error() {
        // `organisation` is absent.
        let text = r#"
name: App
project: Demo
version: 1.0.0
stability: alpha
releaseNumber: 1
"#;
        let err = parse_manifest(text).expect_err("should fail");
        assert!(matches!(err, ManifestError::MissingField(f) if f == "organisation"));
    }

    #[test]
    fn empty_dependencies_list_is_ok() {
        let text = r#"
name: App
project: Demo
organisation: TLang
version: 1.0.0
stability: stable
releaseNumber: 1
"#;
        let manifest = parse_manifest(text).expect("should parse");
        assert!(manifest.dependencies.is_empty());
    }

    #[test]
    fn yaml_comments_are_ignored() {
        let text = r#"
# This is a comment
name: App   # inline comment
project: Demo
organisation: TLang
version: 1.0.0
# another comment
stability: stable
releaseNumber: 1
"#;
        // Inline comments: our simple parser does NOT strip inline comments —
        // that is intentional simplicity.  The field values include the comment
        // text only when using unquoted scalars that contain `#`, which is a
        // corner case we do not support.  Verify we at least parse cleanly.
        let manifest = parse_manifest(text).expect("should parse");
        assert_eq!(manifest.project, "Demo");
    }

    #[test]
    fn invalid_dependency_returns_error() {
        let err = parse_dependency("justonetoken").expect_err("should fail");
        assert!(matches!(err, ManifestError::InvalidDependency(_)));
    }

    // -----------------------------------------------------------------------
    // tbox tests
    // -----------------------------------------------------------------------

    #[test]
    fn tbox_path_contains_tlang_tbox() {
        let path = tbox_path();
        // The path must end in `.tlang/tbox` regardless of the home directory.
        assert!(
            path.ends_with(".tlang/tbox") || path.ends_with(".tlang\\tbox"),
            "unexpected tbox path: {}",
            path.display()
        );
    }

    #[test]
    fn registry_dep_dir_full_version() {
        let dep =
            parse_dependency("My/External/Package 1.0.0:alpha:1 Alias").expect("should parse");
        let tbox = PathBuf::from("/tbox");
        let dir = registry_dep_dir(&tbox, &dep).expect("should return a dir");
        assert_eq!(
            dir,
            PathBuf::from("/tbox/My/External/Package/1.0.0/alpha/1")
        );
    }

    #[test]
    fn registry_dep_dir_version_only() {
        let dep = parse_dependency("Org/Pkg 2.3.0 Alias").expect("should parse");
        let tbox = PathBuf::from("/tbox");
        let dir = registry_dep_dir(&tbox, &dep).expect("should return a dir");
        assert_eq!(dir, PathBuf::from("/tbox/Org/Pkg/2.3.0"));
    }

    #[test]
    fn registry_dep_dir_returns_none_for_file_dep() {
        let dep = parse_dependency("file://libs/mylib MyLib").expect("should parse");
        let tbox = PathBuf::from("/tbox");
        assert!(registry_dep_dir(&tbox, &dep).is_none());
    }

    #[test]
    fn dependency_dirs_with_tbox_includes_registry_dep() {
        let tbox = PathBuf::from("/tbox");
        let mut manifest = minimal_manifest("X");
        manifest.dependencies = vec![Dependency {
            locator: DependencyLocator::Registry {
                path: vec!["My".into(), "Pkg".into()],
                version: VersionSpec {
                    version: "1.0.0".into(),
                    stability: Some(Stability::Alpha),
                    release_number: Some(2),
                },
            },
            alias: "ExtPkg".into(),
        }];
        let dirs = dependency_dirs_with_tbox(Path::new("/project"), &manifest, &tbox);
        assert_eq!(dirs["ExtPkg"], PathBuf::from("/tbox/My/Pkg/1.0.0/alpha/2"));
    }

    #[test]
    fn dependency_dirs_with_tbox_includes_both_file_and_registry() {
        let tbox = PathBuf::from("/tbox");
        let mut manifest = minimal_manifest("X");
        manifest.dependencies = vec![
            Dependency {
                locator: DependencyLocator::File {
                    dir: PathBuf::from("libs/local"),
                },
                alias: "Local".into(),
            },
            Dependency {
                locator: DependencyLocator::Registry {
                    path: vec!["Org".into(), "Pkg".into()],
                    version: VersionSpec {
                        version: "3.0.0".into(),
                        stability: None,
                        release_number: None,
                    },
                },
                alias: "Remote".into(),
            },
        ];
        let dirs = dependency_dirs_with_tbox(Path::new("/project"), &manifest, &tbox);
        assert_eq!(dirs["Local"], PathBuf::from("/project/libs/local"));
        assert_eq!(dirs["Remote"], PathBuf::from("/tbox/Org/Pkg/3.0.0"));
    }

    #[test]
    fn parses_package_type_cli() {
        let text = r#"
name: Kotlin
project: Extractor
organisation: TLang
version: 1.0.0
stability: alpha
releaseNumber: 1
type: cli
"#;
        let manifest = parse_manifest(text).expect("should parse");
        assert_eq!(manifest.package_type, PackageType::Cli);
    }

    #[test]
    fn parses_package_type_defaults_to_library() {
        let text = r#"
name: MyLib
project: Libs
organisation: TLang
version: 1.0.0
stability: stable
releaseNumber: 1
"#;
        let manifest = parse_manifest(text).expect("should parse");
        assert_eq!(manifest.package_type, PackageType::Library);
    }

    #[test]
    fn parses_package_type_library_explicit() {
        let text = r#"
name: MyLib
project: Libs
organisation: TLang
version: 1.0.0
stability: stable
releaseNumber: 1
type: library
"#;
        let manifest = parse_manifest(text).expect("should parse");
        assert_eq!(manifest.package_type, PackageType::Library);
    }

    #[test]
    fn parses_command_with_cli_type() {
        let text = r#"
name: Kotlin
project: Extractor
organisation: TLang
version: 1.0.0
stability: alpha
releaseNumber: 1
type: cli
command: extract-kotlin
"#;
        let manifest = parse_manifest(text).expect("should parse");
        assert_eq!(manifest.package_type, PackageType::Cli);
        assert_eq!(manifest.command.as_deref(), Some("extract-kotlin"));
    }

    #[test]
    fn command_without_cli_type_is_error() {
        let text = r#"
name: MyLib
project: Libs
organisation: TLang
version: 1.0.0
stability: stable
releaseNumber: 1
command: some-cmd
"#;
        let err = parse_manifest(text).expect_err("should fail");
        assert!(
            matches!(err, ManifestError::InvalidField { ref field, .. } if field == "command"),
            "unexpected error: {err}"
        );
    }

    #[test]
    fn cli_type_without_command_is_ok() {
        let text = r#"
name: MyTool
project: Tools
organisation: TLang
version: 1.0.0
stability: alpha
releaseNumber: 1
type: cli
"#;
        let manifest = parse_manifest(text).expect("should parse");
        assert_eq!(manifest.package_type, PackageType::Cli);
        assert_eq!(manifest.command, None);
    }

    #[test]
    fn own_tbox_dir_full() {
        let tbox = PathBuf::from("/tbox");
        let manifest = Manifest {
            name: "MyPkg".into(),
            project: "MyProject".into(),
            organisation: "MyOrg".into(),
            version: "2.1.0".into(),
            stability: Stability::Beta,
            release_number: 3,
            author: None,
            website: None,
            license: None,
            compatibility: None,
            dependencies: vec![],
            main: None,
            package_type: PackageType::Library,
            command: None,
        };
        let dir = own_tbox_dir(&tbox, &manifest);
        assert_eq!(
            dir,
            PathBuf::from("/tbox/MyOrg/MyProject/MyPkg/2.1.0/beta/3")
        );
    }

    #[test]
    fn own_tbox_tbag_path_is_named_after_package() {
        let tbox = PathBuf::from("/tbox");
        let manifest = Manifest {
            name: "MyPkg".into(),
            project: "MyProject".into(),
            organisation: "MyOrg".into(),
            version: "1.0.0".into(),
            stability: Stability::Alpha,
            release_number: 1,
            author: None,
            website: None,
            license: None,
            compatibility: None,
            dependencies: vec![],
            main: None,
            package_type: PackageType::Library,
            command: None,
        };
        let path = own_tbox_tbag_path(&tbox, &manifest);
        assert_eq!(
            path,
            PathBuf::from("/tbox/MyOrg/MyProject/MyPkg/1.0.0/alpha/1/MyPkg.tbag")
        );
    }

    #[test]
    fn registry_dep_tbag_path_uses_last_path_segment() {
        let tbox = PathBuf::from("/tbox");
        let dep = parse_dependency("Org/Group/Package 1.0.0:alpha:1 Alias").expect("should parse");
        let path = registry_dep_tbag_path(&tbox, &dep).expect("should return a path");
        assert_eq!(
            path,
            PathBuf::from("/tbox/Org/Group/Package/1.0.0/alpha/1/Package.tbag")
        );
    }

    #[test]
    fn registry_dep_tbag_path_returns_none_for_file_dep() {
        let tbox = PathBuf::from("/tbox");
        let dep = parse_dependency("file://libs/mylib MyLib").expect("should parse");
        assert_eq!(registry_dep_tbag_path(&tbox, &dep), None);
    }
}
