// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Parsed AST for `model { … }` blocks.
//!
//! A model block declares the static structure of a TLang program — the set
//! entities and typed constants that templates and helper code operate on.
//!
//! # Key types
//!
//! - [`ModelBlockTree`] — root of the model tree; a list of [`ModelNodeTree`]s.
//! - [`ModelSetEntityTree`] — a `set` declaration with params, attrs, and optional
//!   output (`>>`) / exec (`!>`) destinations.
//! - [`ModelValueTypeTree`] — the type of a model attribute (simple type, array,
//!   function ref, impl, generic, or a literal value).

use crate::ast::ModelBlock;
use crate::tree_context::TreeContext;

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ModelBlockTree {
    pub nodes: Vec<ModelNodeTree>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ModelNodeTree {
    AssignVar(ModelAssignVarTree),
    SetEntity(ModelSetEntityTree),
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ModelAssignVarTree {
    pub name: String,
    pub ty: Option<ModelValueTypeTree>,
    pub value: String,
    pub context: TreeContext,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ModelSetEntityTree {
    pub name: String,
    /// Parent set names, in declaration order.  Last parent wins on name collision.
    pub exts: Vec<String>,
    pub params: Vec<ModelSetAttributeTree>,
    pub attrs: Vec<ModelSetAttributeTree>,
    /// Optional output path declaration (`>>` or `>>?`).
    pub output: Option<OutputDecl>,
    /// Optional exec declaration (`!>`): execute the rendered output via a shell.
    pub exec: Option<ExecDecl>,
    pub context: TreeContext,
}

/// Describes where and how a set's generated output should be written.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct OutputDecl {
    /// Path template — may contain `${paramName}` placeholders that are
    /// interpolated with the instance's impl values at generation time.
    pub path: String,
    pub mode: WriteMode,
}

/// Controls whether an existing file is overwritten on each generation run.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum WriteMode {
    /// `>>` — always overwrite (TLang owns the file).
    AlwaysWrite,
    /// `>>?` — write only if the file does not yet exist (developer owns it).
    WriteOnce,
}

/// Describes a `!>` exec declaration on a `set` entity.
///
/// When a set declares `!> bash` (or any other executor), calling
/// `generateAll()` on an instance will execute the rendered command output
/// through the named shell/executor instead of (or in addition to) writing it
/// to a file with `>>`.
///
/// The executor name maps to a system binary: `bash`, `sh`, `python`,
/// `psql`, etc.  On Unix the command is invoked as `<executor> -c <output>`;
/// on Windows as `<executor> /C <output>` (for `cmd`) or
/// `<executor> -Command <output>` (for `powershell`).
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ExecDecl {
    /// The executor / shell to use, e.g. `bash`, `sh`, `python`, `psql`.
    pub executor: String,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ModelSetAttributeTree {
    pub attr: Option<String>,
    pub value: ModelValueTypeTree,
    pub context: TreeContext,
}

/// A single argument in a ref function's explicit currying list.
///
/// When an attribute is declared as `generate: &generateAll(this, "v", _, &cb, 5)`,
/// each item inside the parentheses is represented by one `RefArg`.
///
/// # Calling convention (explicit-currying mode)
/// When at least one `RefArg` is present the runtime uses *explicit mode*:
/// the argument list for the referenced function is built solely from the
/// currying args, substituting every [`RefArg::Hole`] (`_`) with the next
/// caller-supplied argument (left-to-right).  Constructor-param impl values
/// are **not** appended automatically in this mode.
///
/// When `currying` is empty the old *implicit mode* is preserved: impl
/// values are appended first, then caller-supplied args.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum RefArg {
    /// The `this` keyword — resolves to the current set instance at call time.
    /// Only meaningful inside a `model {}` block's `set` entity definition.
    This,
    /// An underscore `_` — a *hole* that the caller must fill in, left-to-right.
    ///
    /// The *n*-th `Hole` in the currying list is replaced by the *n*-th
    /// argument supplied by the caller.  Providing the wrong number of
    /// arguments is a runtime error.
    Hole,
    /// An inline string literal, e.g. `"hello"`.
    Str(String),
    /// An inline integer literal, e.g. `42` or `-1`.
    Int(i64),
    /// An inline boolean literal: `true` or `false`.
    Bool(bool),
    /// A nested function or variable reference, e.g. `&myFunc` or `&pkg.fn`.
    ///
    /// Resolved at call time to the named helper function; passed as its
    /// string name so `FuncDef`-typed parameters can dispatch to it.
    Ref(Vec<String>),
    /// A bare identifier naming a constructor parameter of the enclosing set
    /// entity, e.g. `leader` in `&myFunc(this, leader)`.
    ///
    /// At call time the runtime looks up the parameter value in
    /// `inst.impls[name]`.  If the stored value is a template-function
    /// reference string (e.g. `"Template.leader"`) it is automatically
    /// called with the current set instance to produce a `Leaf`.
    ImplParam(String),
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ModelValueTypeTree {
    Type(String),
    Array(String),
    /// A generic type with one or more type parameters, e.g. `List<Field>` or `Map<String, Int>`.
    Generic {
        name: String,
        params: Vec<String>,
    },
    FuncDef {
        param_types: Vec<String>,
        ret_types: Vec<String>,
    },
    Ref {
        path: Vec<String>,
        /// Explicit currying arguments.
        ///
        /// When non-empty the runtime is in *explicit mode*: args are built
        /// from these items only, with [`RefArg::Hole`] slots filled by the
        /// caller.  When empty the legacy implicit mode is used.
        currying: Vec<Vec<RefArg>>,
    },
    Impl {
        attrs: Vec<ModelSetAttributeTree>,
    },
    ImplArray,
    /// A quoted string literal value, e.g. `"com.example.package"`.
    /// The stored value is already unescaped (e.g. `\"` → `"`, `\n` → newline).
    StringLiteral(String),
    /// An integer literal value, e.g. `5` or `-10`.
    IntLiteral(i64),
    /// A boolean literal value: `true` or `false`.
    BoolLiteral(bool),
    /// An inline array literal, e.g. `[1, 2, 3]` or `["a", "b"]`.
    ArrayLiteral(Vec<ModelValueTypeTree>),
}

#[derive(Debug)]
pub struct ModelTreeError(pub String);

impl std::fmt::Display for ModelTreeError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.0)
    }
}

impl std::error::Error for ModelTreeError {}

pub fn parse_model_block_tree(model: &ModelBlock) -> Result<ModelBlockTree, ModelTreeError> {
    parse_model_content_tree(&model.content)
}

pub fn parse_model_content_tree(content: &str) -> Result<ModelBlockTree, ModelTreeError> {
    let inner = strip_outer_braces(content)?;
    let items = split_top_level_items(inner)?;
    let mut nodes = Vec::new();

    for item in items {
        let text = item.text.trim();
        if text.is_empty() {
            continue;
        }
        if text.starts_with("set ") || text == "set" {
            nodes.push(ModelNodeTree::SetEntity(parse_set_entity(
                text, item.start, inner,
            )?));
        } else if text.starts_with("let ") || text == "let" {
            nodes.push(ModelNodeTree::AssignVar(parse_assign_var(
                text, item.start, inner,
            )?));
        } else {
            return Err(ModelTreeError(format!("unknown model statement: {text}")));
        }
    }

    Ok(ModelBlockTree { nodes })
}

fn parse_assign_var(
    text: &str,
    start_offset: usize,
    source: &str,
) -> Result<ModelAssignVarTree, ModelTreeError> {
    let trimmed = text.trim();
    let tail = trimmed
        .strip_prefix("let")
        .ok_or_else(|| ModelTreeError("invalid assign statement".to_string()))?
        .trim();
    let (left, value) = tail
        .split_once('=')
        .ok_or_else(|| ModelTreeError("assign statement must contain `=`".to_string()))?;
    let value = value.trim();
    if value.is_empty() {
        return Err(ModelTreeError(
            "assign statement value is empty".to_string(),
        ));
    }

    let (name, ty) = if let Some((name, ty)) = left.split_once(':') {
        (name.trim(), Some(parse_value_type(ty.trim())?))
    } else {
        (left.trim(), None)
    };
    if !is_ident(name) {
        return Err(ModelTreeError(format!(
            "invalid assign variable name `{name}`"
        )));
    }

    Ok(ModelAssignVarTree {
        name: name.to_string(),
        ty,
        value: value.to_string(),
        context: TreeContext::from_offset(source, start_offset),
    })
}

fn parse_set_entity(
    text: &str,
    start_offset: usize,
    source: &str,
) -> Result<ModelSetEntityTree, ModelTreeError> {
    let bytes = text.as_bytes();
    let mut idx = 0usize;

    consume_keyword(bytes, &mut idx, "set")
        .ok_or_else(|| ModelTreeError("model set must start with `set`".to_string()))?;
    skip_ws(bytes, &mut idx);

    let name = read_ident(bytes, &mut idx)
        .ok_or_else(|| ModelTreeError("model set entity name is missing".to_string()))?;

    skip_ws(bytes, &mut idx);

    // Parse zero or more `: ParentName` clauses.  Last parent wins on collision.
    let mut exts = Vec::new();
    while bytes.get(idx) == Some(&b':') {
        idx += 1;
        skip_ws(bytes, &mut idx);
        let parent = read_obj_type(bytes, &mut idx)
            .ok_or_else(|| ModelTreeError("model set `:` parent type is missing".to_string()))?;
        exts.push(parent);
        skip_ws(bytes, &mut idx);
    }

    let mut params = Vec::new();
    if bytes.get(idx) == Some(&b'(') {
        let end = find_matching(bytes, idx, b'(', b')')
            .ok_or_else(|| ModelTreeError("unclosed model set parameter list".to_string()))?;
        let inner = &text[idx + 1..end];
        params = parse_attribute_list(inner, start_offset + idx + 1, source)?;
        idx = end + 1;
        skip_ws(bytes, &mut idx);
    }

    // Also allow `: ParentName` clauses AFTER the params list, e.g. `set Email(): Type`.
    while bytes.get(idx) == Some(&b':') {
        idx += 1;
        skip_ws(bytes, &mut idx);
        let parent = read_obj_type(bytes, &mut idx)
            .ok_or_else(|| ModelTreeError("model set `:` parent type is missing".to_string()))?;
        exts.push(parent);
        skip_ws(bytes, &mut idx);
    }

    // Parse optional `>> "path"` or `>>? "path"` output declaration.
    let output = if bytes.get(idx) == Some(&b'>') && bytes.get(idx + 1) == Some(&b'>') {
        let mode = if bytes.get(idx + 2) == Some(&b'?') {
            idx += 3;
            WriteMode::WriteOnce
        } else {
            idx += 2;
            WriteMode::AlwaysWrite
        };
        skip_ws(bytes, &mut idx);
        let path = read_quoted_string(bytes, &mut idx)?;
        skip_ws(bytes, &mut idx);
        Some(OutputDecl { path, mode })
    } else {
        None
    };

    let attrs = if bytes.get(idx) == Some(&b'{') {
        let end = find_matching(bytes, idx, b'{', b'}')
            .ok_or_else(|| ModelTreeError("unclosed model set body".to_string()))?;
        let body = &text[idx + 1..end];
        let result = parse_attribute_list(body, start_offset + idx + 1, source)?;
        idx = end + 1;
        skip_ws(bytes, &mut idx);
        result
    } else {
        // Braces are optional when there is nothing to surcharge.
        // `set Foo : Bar` and `set Foo : Bar {}` are equivalent.
        Vec::new()
    };
    // Parse optional `!> executor` exec declaration (after the body).
    // The executor is a plain identifier (e.g. bash, sh, python, psql).
    let exec = if bytes.get(idx) == Some(&b'!') && bytes.get(idx + 1) == Some(&b'>') {
        idx += 2;
        skip_ws(bytes, &mut idx);
        let executor = read_ident(bytes, &mut idx).ok_or_else(|| {
            ModelTreeError("expected an executor name (e.g. `bash`, `sh`) after `!>`".to_string())
        })?;
        skip_ws(bytes, &mut idx);
        Some(ExecDecl { executor })
    } else {
        None
    };

    if idx != bytes.len() {
        return Err(ModelTreeError(format!(
            "unexpected trailing model set content: {}",
            text[idx..].trim()
        )));
    }

    Ok(ModelSetEntityTree {
        name,
        exts,
        params,
        attrs,
        output,
        exec,
        context: TreeContext::from_offset(source, start_offset),
    })
}

/// Read a `"..."` quoted string literal from the byte slice starting at `*idx`,
/// advance `*idx` past the closing quote, and return the unescaped content.
fn read_quoted_string(bytes: &[u8], idx: &mut usize) -> Result<String, ModelTreeError> {
    if bytes.get(*idx) != Some(&b'"') {
        return Err(ModelTreeError(format!(
            "expected a quoted string after `>>` / `>>?`, got `{}`",
            bytes.get(*idx).map_or("end of input".to_string(), |b| {
                (char::from(*b)).to_string()
            })
        )));
    }
    let start = *idx;
    *idx += 1;
    while *idx < bytes.len() {
        if bytes[*idx] == b'\\' {
            *idx += 2; // skip escape sequence
        } else if bytes[*idx] == b'"' {
            *idx += 1;
            break;
        } else {
            *idx += 1;
        }
    }
    let s = std::str::from_utf8(&bytes[start..*idx])
        .map_err(|_| ModelTreeError("invalid UTF-8 in output path string".to_string()))?;
    parse_string_literal(s)
}

fn parse_attribute_list(
    inner: &str,
    start_offset: usize,
    source: &str,
) -> Result<Vec<ModelSetAttributeTree>, ModelTreeError> {
    let mut out = Vec::new();
    for part in split_top_level_commas_with_offsets(inner)? {
        let trimmed = part.text.trim();
        if trimmed.is_empty() {
            continue;
        }
        out.push(parse_attribute(trimmed, start_offset + part.start, source)?);
    }
    Ok(out)
}

fn parse_attribute(
    text: &str,
    start_offset: usize,
    source: &str,
) -> Result<ModelSetAttributeTree, ModelTreeError> {
    let mut attr = None;
    let mut value_text = text.trim();
    if let Some(pos) = find_top_level_colon(value_text) {
        let left = value_text[..pos].trim();
        if is_ident(left) {
            attr = Some(left.to_string());
            value_text = value_text[pos + 1..].trim();
        }
    }
    if value_text.is_empty() {
        return Err(ModelTreeError("model attribute value is empty".to_string()));
    }
    Ok(ModelSetAttributeTree {
        attr,
        value: parse_value_type(value_text)?,
        context: TreeContext::from_offset(source, start_offset),
    })
}

fn parse_string_literal(text: &str) -> Result<String, ModelTreeError> {
    // `text` must start and end with `"`.  Inner escape sequences recognised:
    //   \"  →  "
    //   \\  →  \
    //   \n  →  newline
    //   \t  →  tab
    // Any other `\x` is preserved as-is (`\x`).
    debug_assert!(text.starts_with('"'));
    let mut result = String::new();
    let mut chars = text[1..].chars().peekable();
    loop {
        match chars.next() {
            None => {
                return Err(ModelTreeError(
                    "unterminated string literal in model block".to_string(),
                ));
            }
            Some('"') => {
                // Ensure nothing follows the closing quote.
                let trailing: String = chars.collect();
                if !trailing.trim().is_empty() {
                    return Err(ModelTreeError(format!(
                        "unexpected content after string literal: {}",
                        trailing.trim()
                    )));
                }
                return Ok(result);
            }
            Some('\\') => match chars.next() {
                Some('n') => result.push('\n'),
                Some('t') => result.push('\t'),
                Some('\\') => result.push('\\'),
                Some('"') => result.push('"'),
                Some(c) => {
                    result.push('\\');
                    result.push(c);
                }
                None => {
                    return Err(ModelTreeError(
                        "unterminated escape sequence in model string literal".to_string(),
                    ));
                }
            },
            Some(c) => result.push(c),
        }
    }
}

fn parse_value_type(text: &str) -> Result<ModelValueTypeTree, ModelTreeError> {
    let trimmed = text.trim();

    // String literal: "..."
    if trimmed.starts_with('"') {
        let unescaped = parse_string_literal(trimmed)?;
        return Ok(ModelValueTypeTree::StringLiteral(unescaped));
    }

    // Boolean literals
    if trimmed == "true" {
        return Ok(ModelValueTypeTree::BoolLiteral(true));
    }
    if trimmed == "false" {
        return Ok(ModelValueTypeTree::BoolLiteral(false));
    }

    // Integer literal: optional leading `-` followed by digits
    {
        let num_text = if trimmed.starts_with('-') {
            &trimmed[1..]
        } else {
            trimmed
        };
        if !num_text.is_empty() && num_text.bytes().all(|b| b.is_ascii_digit()) {
            let n: i64 = trimmed
                .parse()
                .map_err(|_| ModelTreeError(format!("integer literal out of range: {trimmed}")))?;
            return Ok(ModelValueTypeTree::IntLiteral(n));
        }
    }

    // Array literal: `[item, item, ...]`
    if trimmed.starts_with('[') {
        let bytes = trimmed.as_bytes();
        let end = find_matching(bytes, 0, b'[', b']')
            .ok_or_else(|| ModelTreeError("unclosed array literal `[`".to_string()))?;
        if trimmed[end + 1..].trim().is_empty() {
            let inner = &trimmed[1..end];
            let items = if inner.trim().is_empty() {
                Vec::new()
            } else {
                split_top_level_commas(inner)?
                    .into_iter()
                    .map(|s| parse_value_type(s.trim()))
                    .collect::<Result<Vec<_>, _>>()?
            };
            return Ok(ModelValueTypeTree::ArrayLiteral(items));
        }
        return Err(ModelTreeError(format!(
            "unexpected content after array literal: {}",
            trimmed[end + 1..].trim()
        )));
    }

    if trimmed == "impl[]" {
        return Ok(ModelValueTypeTree::ImplArray);
    }

    if let Some(rest) = trimmed.strip_prefix("impl") {
        let rest = rest.trim();
        if rest.is_empty() {
            return Ok(ModelValueTypeTree::Impl { attrs: Vec::new() });
        }
        if !rest.starts_with('{') {
            return Err(ModelTreeError(format!(
                "invalid impl value type: {trimmed}"
            )));
        }
        let bytes = rest.as_bytes();
        let end = find_matching(bytes, 0, b'{', b'}')
            .ok_or_else(|| ModelTreeError("unclosed impl body".to_string()))?;
        if rest[end + 1..].trim().is_empty() {
            let attrs = parse_attribute_list(&rest[1..end], 1, rest)?;
            return Ok(ModelValueTypeTree::Impl { attrs });
        }
        return Err(ModelTreeError(format!(
            "invalid impl value type: {trimmed}"
        )));
    }

    if let Some(ref_type) = parse_ref_type(trimmed)? {
        return Ok(ref_type);
    }

    if let Some(func_type) = parse_func_def_type(trimmed)? {
        return Ok(func_type);
    }

    if let Some(array) = trimmed.strip_suffix("[]") {
        let array = array.trim();
        if is_ident(array) {
            return Ok(ModelValueTypeTree::Array(array.to_string()));
        }
    }

    if let Some(generic) = parse_generic_type(trimmed)? {
        return Ok(generic);
    }

    if is_ident(trimmed) {
        return Ok(ModelValueTypeTree::Type(trimmed.to_string()));
    }

    Err(ModelTreeError(format!(
        "unsupported model value type: {trimmed}"
    )))
}

/// Parse a generic type like `List<Field>` or `Map<String, Int>`.
/// Returns `None` if the text does not match the `Ident<...>` pattern.
fn parse_generic_type(text: &str) -> Result<Option<ModelValueTypeTree>, ModelTreeError> {
    let Some(angle_start) = text.find('<') else {
        return Ok(None);
    };
    let name = text[..angle_start].trim();
    if !is_ident(name) {
        return Ok(None);
    }
    if !text.ends_with('>') {
        return Err(ModelTreeError(format!("unclosed generic type: {text}")));
    }
    let inner = text[angle_start + 1..text.len() - 1].trim();
    if inner.is_empty() {
        return Err(ModelTreeError(format!(
            "generic type has no parameters: {text}"
        )));
    }
    let params: Vec<String> = inner.split(',').map(|p| p.trim().to_string()).collect();
    for param in &params {
        if param.is_empty() {
            return Err(ModelTreeError(format!(
                "empty type parameter in generic type: {text}"
            )));
        }
    }
    Ok(Some(ModelValueTypeTree::Generic {
        name: name.to_string(),
        params,
    }))
}

/// Parse one argument from a ref's explicit currying list.
///
/// Accepts:
/// - `this`           → [`RefArg::This`]
/// - `_`              → [`RefArg::Hole`]
/// - `true` / `false` → [`RefArg::Bool`]
/// - `"string"`       → [`RefArg::Str`]
/// - `42` / `-1`      → [`RefArg::Int`]
/// - `&func` / `&a.b` → [`RefArg::Ref`]
fn parse_ref_arg(text: &str) -> Result<RefArg, ModelTreeError> {
    let trimmed = text.trim();

    if trimmed == "this" {
        return Ok(RefArg::This);
    }

    if trimmed == "_" {
        return Ok(RefArg::Hole);
    }

    if trimmed == "true" {
        return Ok(RefArg::Bool(true));
    }
    if trimmed == "false" {
        return Ok(RefArg::Bool(false));
    }

    if trimmed.starts_with('"') {
        let s = parse_string_literal(trimmed)?;
        return Ok(RefArg::Str(s));
    }

    // Integer literal: optional leading `-` followed by digits.
    {
        let num_text = if trimmed.starts_with('-') {
            &trimmed[1..]
        } else {
            trimmed
        };
        if !num_text.is_empty() && num_text.bytes().all(|b| b.is_ascii_digit()) {
            let n: i64 = trimmed
                .parse()
                .map_err(|_| ModelTreeError(format!("integer literal out of range: {trimmed}")))?;
            return Ok(RefArg::Int(n));
        }
    }

    // Nested ref: `&func` or `&pkg.fn` (no nested currying).
    if let Some(rest) = trimmed.strip_prefix('&') {
        let rest = rest.trim();
        if rest.is_empty() {
            return Err(ModelTreeError(
                "a ref argument `&` must be followed by a function or variable name".to_string(),
            ));
        }
        let path: Vec<String> = rest.split('.').map(|s| s.trim().to_string()).collect();
        if path.iter().any(|s| s.is_empty()) {
            return Err(ModelTreeError(format!(
                "invalid ref argument path: `{trimmed}`"
            )));
        }
        return Ok(RefArg::Ref(path));
    }

    // Bare identifier — treated as a constructor parameter reference.
    // e.g. `leader` in `&generateLeadTemplate(this, leader)` refers to the
    // value supplied for the `leader` impl parameter at instantiation time.
    if is_ident(trimmed) {
        return Ok(RefArg::ImplParam(trimmed.to_string()));
    }

    Err(ModelTreeError(format!(
        "unsupported ref argument `{trimmed}` — expected `this`, `_`, a string literal, \
         an integer, a boolean (`true`/`false`), a function reference (`&name`), or a \
         constructor parameter name"
    )))
}

fn parse_ref_type(text: &str) -> Result<Option<ModelValueTypeTree>, ModelTreeError> {
    if !text.starts_with('&') {
        return Ok(None);
    }
    let bytes = text.as_bytes();
    let mut idx = 1usize;
    skip_ws(bytes, &mut idx);
    let mut path = Vec::new();
    let first = read_ident(bytes, &mut idx)
        .ok_or_else(|| ModelTreeError("invalid model reference type".to_string()))?;
    path.push(first);
    loop {
        skip_ws(bytes, &mut idx);
        if bytes.get(idx) != Some(&b'.') {
            break;
        }
        idx += 1;
        skip_ws(bytes, &mut idx);
        let next = read_ident(bytes, &mut idx)
            .ok_or_else(|| ModelTreeError("invalid model reference path".to_string()))?;
        path.push(next);
    }

    let mut currying = Vec::new();
    loop {
        skip_ws(bytes, &mut idx);
        if bytes.get(idx) != Some(&b'(') {
            break;
        }
        let end = find_matching(bytes, idx, b'(', b')')
            .ok_or_else(|| ModelTreeError("unclosed model reference currying".to_string()))?;
        let raw_args = split_top_level_commas(&text[idx + 1..end])?;
        let values = raw_args
            .into_iter()
            .map(|v| v.trim().to_string())
            .filter(|v| !v.is_empty())
            .map(|v| parse_ref_arg(&v))
            .collect::<Result<Vec<_>, _>>()?;
        currying.push(values);
        idx = end + 1;
    }

    skip_ws(bytes, &mut idx);
    if idx != bytes.len() {
        return Err(ModelTreeError(format!(
            "invalid model reference trailing content: {}",
            text[idx..].trim()
        )));
    }

    Ok(Some(ModelValueTypeTree::Ref { path, currying }))
}

fn parse_func_def_type(text: &str) -> Result<Option<ModelValueTypeTree>, ModelTreeError> {
    if !text.starts_with('(') {
        return Ok(None);
    }
    let bytes = text.as_bytes();
    let params_end = find_matching(bytes, 0, b'(', b')')
        .ok_or_else(|| ModelTreeError("unclosed function type params".to_string()))?;
    let param_types = split_top_level_commas(&text[1..params_end])?
        .into_iter()
        .map(|v| v.trim().to_string())
        .filter(|v| !v.is_empty())
        .collect::<Vec<_>>();

    let mut idx = params_end + 1;
    skip_ws(bytes, &mut idx);
    let mut ret_types = Vec::new();
    if bytes.get(idx) == Some(&b':') {
        idx += 1;
        skip_ws(bytes, &mut idx);
        if bytes.get(idx) != Some(&b'(') {
            return Err(ModelTreeError(
                "function type return tuple is missing".to_string(),
            ));
        }
        let ret_end = find_matching(bytes, idx, b'(', b')')
            .ok_or_else(|| ModelTreeError("unclosed function type returns".to_string()))?;
        ret_types = split_top_level_commas(&text[idx + 1..ret_end])?
            .into_iter()
            .map(|v| v.trim().to_string())
            .filter(|v| !v.is_empty())
            .collect();
        idx = ret_end + 1;
    }

    skip_ws(bytes, &mut idx);
    if idx != bytes.len() {
        return Ok(None);
    }

    Ok(Some(ModelValueTypeTree::FuncDef {
        param_types,
        ret_types,
    }))
}

fn strip_outer_braces(input: &str) -> Result<&str, ModelTreeError> {
    let trimmed = input.trim();
    if !trimmed.starts_with('{') || !trimmed.ends_with('}') {
        return Err(ModelTreeError(
            "model block content must be enclosed by braces".to_string(),
        ));
    }
    Ok(&trimmed[1..trimmed.len() - 1])
}

#[derive(Debug)]
struct ParsedSegment {
    text: String,
    start: usize,
}

fn split_top_level_items(input: &str) -> Result<Vec<ParsedSegment>, ModelTreeError> {
    let mut out = Vec::new();
    let mut idx = 0usize;
    while idx < input.len() {
        skip_ws_and_comments(input, &mut idx);
        if idx >= input.len() {
            break;
        }
        let start = idx;
        idx = read_item_end(input, idx)?;
        let raw = &input[start..idx];
        let item = raw.trim();
        if !item.is_empty() {
            let leading_ws = raw.len().saturating_sub(raw.trim_start().len());
            out.push(ParsedSegment {
                text: item.to_string(),
                start: start + leading_ws,
            });
        }
    }
    Ok(out)
}

fn split_top_level_commas(input: &str) -> Result<Vec<String>, ModelTreeError> {
    Ok(split_top_level_commas_with_offsets(input)?
        .into_iter()
        .map(|segment| segment.text)
        .collect())
}

fn split_top_level_commas_with_offsets(input: &str) -> Result<Vec<ParsedSegment>, ModelTreeError> {
    let bytes = input.as_bytes();
    let mut depth_paren = 0usize;
    let mut depth_brace = 0usize;
    let mut depth_bracket = 0usize;
    let mut in_string: Option<u8> = None;
    let mut start = 0usize;
    let mut out = Vec::new();
    let mut idx = 0usize;
    while idx < bytes.len() {
        let ch = bytes[idx];
        if let Some(quote) = in_string {
            if ch == b'\\' {
                if idx + 1 < bytes.len() {
                    idx += 2;
                } else {
                    idx += 1;
                }
                continue;
            }
            if ch == quote {
                in_string = None;
            }
            idx += 1;
            continue;
        }
        match ch {
            b'"' | b'\'' => in_string = Some(ch),
            b'(' => depth_paren += 1,
            b')' => depth_paren = depth_paren.saturating_sub(1),
            b'{' => depth_brace += 1,
            b'}' => depth_brace = depth_brace.saturating_sub(1),
            b'[' => depth_bracket += 1,
            b']' => depth_bracket = depth_bracket.saturating_sub(1),
            b',' if depth_paren == 0 && depth_brace == 0 && depth_bracket == 0 => {
                let raw = &input[start..idx];
                let segment = raw.trim();
                if !segment.is_empty() {
                    let leading_ws = raw.len().saturating_sub(raw.trim_start().len());
                    out.push(ParsedSegment {
                        text: segment.to_string(),
                        start: start + leading_ws,
                    });
                }
                start = idx + 1;
            }
            _ => {}
        }
        idx += 1;
    }
    if in_string.is_some() {
        return Err(ModelTreeError(
            "unclosed string delimiter while splitting by commas".to_string(),
        ));
    }
    if depth_paren > 0 {
        return Err(ModelTreeError(
            "unclosed `()` delimiter while splitting by commas".to_string(),
        ));
    }
    if depth_brace > 0 {
        return Err(ModelTreeError(
            "unclosed `{}` delimiter while splitting by commas".to_string(),
        ));
    }
    if depth_bracket > 0 {
        return Err(ModelTreeError(
            "unclosed `[]` delimiter while splitting by commas".to_string(),
        ));
    }
    let raw = &input[start..];
    let segment = raw.trim();
    if !segment.is_empty() {
        let leading_ws = raw.len().saturating_sub(raw.trim_start().len());
        out.push(ParsedSegment {
            text: segment.to_string(),
            start: start + leading_ws,
        });
    }
    Ok(out)
}

fn find_top_level_colon(input: &str) -> Option<usize> {
    let bytes = input.as_bytes();
    let mut depth_paren = 0usize;
    let mut depth_brace = 0usize;
    let mut depth_bracket = 0usize;
    let mut in_string: Option<u8> = None;

    let mut idx = 0usize;
    while idx < bytes.len() {
        let ch = bytes[idx];
        if let Some(quote) = in_string {
            if ch == b'\\' {
                if idx + 1 < bytes.len() {
                    idx += 2;
                } else {
                    idx += 1;
                }
                continue;
            }
            if ch == quote {
                in_string = None;
            }
            idx += 1;
            continue;
        }
        match ch {
            b'"' | b'\'' => in_string = Some(ch),
            b'(' => depth_paren += 1,
            b')' => depth_paren = depth_paren.saturating_sub(1),
            b'{' => depth_brace += 1,
            b'}' => depth_brace = depth_brace.saturating_sub(1),
            b'[' => depth_bracket += 1,
            b']' => depth_bracket = depth_bracket.saturating_sub(1),
            b':' if depth_paren == 0 && depth_brace == 0 && depth_bracket == 0 => {
                return Some(idx);
            }
            _ => {}
        }
        idx += 1;
    }
    None
}

fn read_item_end(input: &str, start: usize) -> Result<usize, ModelTreeError> {
    let bytes = input.as_bytes();
    let mut idx = start;
    let mut paren = 0usize;
    let mut bracket = 0usize;
    let mut in_string: Option<u8> = None;

    while idx < bytes.len() {
        let ch = bytes[idx];
        if let Some(quote) = in_string {
            if ch == b'\\' {
                if idx + 1 < bytes.len() {
                    idx += 2;
                } else {
                    idx += 1;
                }
                continue;
            }
            if ch == quote {
                in_string = None;
            }
            idx += 1;
            continue;
        }
        match ch {
            b'"' | b'\'' => {
                in_string = Some(ch);
                idx += 1;
            }
            b'(' => {
                paren += 1;
                idx += 1;
            }
            b')' => {
                paren = paren.saturating_sub(1);
                idx += 1;
            }
            b'[' => {
                bracket += 1;
                idx += 1;
            }
            b']' => {
                bracket = bracket.saturating_sub(1);
                idx += 1;
            }
            b'{' if paren == 0 && bracket == 0 => {
                idx = skip_balanced_braces(input, idx)?;
            }
            b';' if paren == 0 && bracket == 0 => return Ok(idx + 1),
            b'\n' if paren == 0 && bracket == 0 => {
                // Peek ahead past whitespace: if the next non-whitespace token
                // starts with `>` (for `>>` / `>>?`) the declaration continues
                // on the next line — do not split here.
                let mut peek = idx + 1;
                while peek < input.len()
                    && input.as_bytes()[peek].is_ascii_whitespace()
                    && input.as_bytes()[peek] != b'\n'
                {
                    peek += 1;
                }
                if input.as_bytes().get(peek) == Some(&b'>') {
                    idx += 1;
                    continue;
                }
                // `!>` — exec declaration — also continues on the next line.
                if input.as_bytes().get(peek) == Some(&b'!')
                    && input.as_bytes().get(peek + 1) == Some(&b'>')
                {
                    idx += 1;
                    continue;
                }
                return Ok(idx);
            }
            _ => idx += 1,
        }
    }
    Ok(input.len())
}

fn skip_balanced_braces(input: &str, start: usize) -> Result<usize, ModelTreeError> {
    let bytes = input.as_bytes();
    if bytes.get(start) != Some(&b'{') {
        return Err(ModelTreeError("expected opening brace".to_string()));
    }
    let mut idx = start;
    let mut depth = 0usize;
    let mut in_string: Option<u8> = None;
    while idx < bytes.len() {
        let ch = bytes[idx];
        if let Some(quote) = in_string {
            if ch == b'\\' {
                if idx + 1 < bytes.len() {
                    idx += 2;
                } else {
                    idx += 1;
                }
                continue;
            }
            if ch == quote {
                in_string = None;
            }
            idx += 1;
            continue;
        }
        match ch {
            b'"' | b'\'' => {
                in_string = Some(ch);
                idx += 1;
            }
            b'{' => {
                depth += 1;
                idx += 1;
            }
            b'}' => {
                depth = depth.saturating_sub(1);
                idx += 1;
                if depth == 0 {
                    return Ok(idx);
                }
            }
            _ => idx += 1,
        }
    }
    Err(ModelTreeError("unclosed brace block".to_string()))
}

fn skip_ws_and_comments(input: &str, idx: &mut usize) {
    let bytes = input.as_bytes();
    loop {
        while *idx < bytes.len() && bytes[*idx].is_ascii_whitespace() {
            *idx += 1;
        }
        if *idx + 1 < bytes.len() && bytes[*idx] == b'/' && bytes[*idx + 1] == b'/' {
            *idx += 2;
            while *idx < bytes.len() && bytes[*idx] != b'\n' {
                *idx += 1;
            }
            continue;
        }
        break;
    }
}

fn find_matching(bytes: &[u8], start: usize, open: u8, close: u8) -> Option<usize> {
    if bytes.get(start) != Some(&open) {
        return None;
    }
    let mut idx = start;
    let mut depth = 0usize;
    let mut in_string: Option<u8> = None;
    while idx < bytes.len() {
        let ch = bytes[idx];
        if let Some(quote) = in_string {
            if ch == b'\\' {
                idx = (idx + 2).min(bytes.len());
                continue;
            }
            if ch == quote {
                in_string = None;
            }
            idx += 1;
            continue;
        }
        match ch {
            b'"' | b'\'' => in_string = Some(ch),
            _ if ch == open => depth += 1,
            _ if ch == close => {
                depth = depth.saturating_sub(1);
                if depth == 0 {
                    return Some(idx);
                }
            }
            _ => {}
        }
        idx += 1;
    }
    None
}

fn consume_keyword(bytes: &[u8], idx: &mut usize, keyword: &str) -> Option<()> {
    let start = *idx;
    let kw = keyword.as_bytes();
    if bytes.get(start..start + kw.len()) != Some(kw) {
        return None;
    }
    let end = start + kw.len();
    if let Some(next) = bytes.get(end)
        && (next.is_ascii_alphanumeric() || *next == b'_' || *next == b'-')
    {
        return None;
    }
    *idx = end;
    Some(())
}

fn read_obj_type(bytes: &[u8], idx: &mut usize) -> Option<String> {
    let mut parts = Vec::new();
    let first = read_ident(bytes, idx)?;
    parts.push(first);
    if bytes.get(*idx) == Some(&b'.') {
        *idx += 1;
        let second = read_ident(bytes, idx)?;
        parts.push(second);
    }
    Some(parts.join("."))
}

fn read_ident(bytes: &[u8], idx: &mut usize) -> Option<String> {
    if *idx >= bytes.len() {
        return None;
    }
    let start = *idx;
    let first = bytes[*idx];
    if !(first.is_ascii_alphabetic() || first == b'_') {
        return None;
    }
    *idx += 1;
    while *idx < bytes.len() {
        let ch = bytes[*idx];
        if ch.is_ascii_alphanumeric() || ch == b'_' || ch == b'-' {
            *idx += 1;
        } else {
            break;
        }
    }
    Some(String::from_utf8_lossy(&bytes[start..*idx]).to_string())
}

fn is_ident(text: &str) -> bool {
    let mut chars = text.chars();
    let Some(first) = chars.next() else {
        return false;
    };
    if !(first.is_ascii_alphabetic() || first == '_') {
        return false;
    }
    chars.all(|c| c.is_ascii_alphanumeric() || c == '_' || c == '-')
}

fn skip_ws(bytes: &[u8], idx: &mut usize) {
    while *idx < bytes.len() && bytes[*idx].is_ascii_whitespace() {
        *idx += 1;
    }
}

#[cfg(test)]
#[cfg(test)]
mod value_type_tests {
    use super::*;

    #[test]
    fn parses_int_literal() {
        assert_eq!(
            parse_value_type("42").unwrap(),
            ModelValueTypeTree::IntLiteral(42)
        );
        assert_eq!(
            parse_value_type("-7").unwrap(),
            ModelValueTypeTree::IntLiteral(-7)
        );
        assert_eq!(
            parse_value_type("0").unwrap(),
            ModelValueTypeTree::IntLiteral(0)
        );
    }

    #[test]
    fn parses_bool_literal() {
        assert_eq!(
            parse_value_type("true").unwrap(),
            ModelValueTypeTree::BoolLiteral(true)
        );
        assert_eq!(
            parse_value_type("false").unwrap(),
            ModelValueTypeTree::BoolLiteral(false)
        );
    }

    #[test]
    fn parses_array_literal_of_ints() {
        assert_eq!(
            parse_value_type("[1, 2, 3]").unwrap(),
            ModelValueTypeTree::ArrayLiteral(vec![
                ModelValueTypeTree::IntLiteral(1),
                ModelValueTypeTree::IntLiteral(2),
                ModelValueTypeTree::IntLiteral(3),
            ])
        );
    }

    #[test]
    fn parses_array_literal_of_strings() {
        assert_eq!(
            parse_value_type(r#"["a", "b"]"#).unwrap(),
            ModelValueTypeTree::ArrayLiteral(vec![
                ModelValueTypeTree::StringLiteral("a".to_string()),
                ModelValueTypeTree::StringLiteral("b".to_string()),
            ])
        );
    }

    #[test]
    fn parses_empty_array_literal() {
        assert_eq!(
            parse_value_type("[]").unwrap(),
            ModelValueTypeTree::ArrayLiteral(vec![])
        );
    }

    #[test]
    fn parses_generic_type_list_field() {
        assert_eq!(
            parse_value_type("List<Field>").unwrap(),
            ModelValueTypeTree::Generic {
                name: "List".to_string(),
                params: vec!["Field".to_string()],
            }
        );
    }

    #[test]
    fn parses_ref_arg_this() {
        let vt = parse_value_type("&doRender(this)").unwrap();
        match vt {
            ModelValueTypeTree::Ref { path, currying } => {
                assert_eq!(path, vec!["doRender"]);
                assert_eq!(currying.len(), 1);
                assert_eq!(currying[0], vec![crate::model_tree::RefArg::This]);
            }
            _ => panic!("expected Ref"),
        }
    }

    #[test]
    fn parses_ref_arg_hole() {
        let vt = parse_value_type("&doRun(_)").unwrap();
        match vt {
            ModelValueTypeTree::Ref { path, currying } => {
                assert_eq!(path, vec!["doRun"]);
                assert_eq!(currying[0], vec![crate::model_tree::RefArg::Hole]);
            }
            _ => panic!("expected Ref"),
        }
    }

    #[test]
    fn parses_ref_arg_string_literal() {
        let vt = parse_value_type(r#"&greet("Hello")"#).unwrap();
        match vt {
            ModelValueTypeTree::Ref { currying, .. } => {
                assert_eq!(
                    currying[0],
                    vec![crate::model_tree::RefArg::Str("Hello".to_string())]
                );
            }
            _ => panic!("expected Ref"),
        }
    }

    #[test]
    fn parses_ref_arg_int_literal() {
        let vt = parse_value_type("&offset(42)").unwrap();
        match vt {
            ModelValueTypeTree::Ref { currying, .. } => {
                assert_eq!(currying[0], vec![crate::model_tree::RefArg::Int(42)]);
            }
            _ => panic!("expected Ref"),
        }
    }

    #[test]
    fn parses_ref_arg_negative_int_literal() {
        let vt = parse_value_type("&offset(-7)").unwrap();
        match vt {
            ModelValueTypeTree::Ref { currying, .. } => {
                assert_eq!(currying[0], vec![crate::model_tree::RefArg::Int(-7)]);
            }
            _ => panic!("expected Ref"),
        }
    }

    #[test]
    fn parses_ref_arg_bool_literals() {
        let vt_true = parse_value_type("&flag(true)").unwrap();
        let vt_false = parse_value_type("&flag(false)").unwrap();
        match vt_true {
            ModelValueTypeTree::Ref { currying, .. } => {
                assert_eq!(currying[0], vec![crate::model_tree::RefArg::Bool(true)]);
            }
            _ => panic!("expected Ref"),
        }
        match vt_false {
            ModelValueTypeTree::Ref { currying, .. } => {
                assert_eq!(currying[0], vec![crate::model_tree::RefArg::Bool(false)]);
            }
            _ => panic!("expected Ref"),
        }
    }

    #[test]
    fn parses_ref_arg_nested_ref() {
        let vt = parse_value_type("&dispatch(&myHandler)").unwrap();
        match vt {
            ModelValueTypeTree::Ref { currying, .. } => {
                assert_eq!(
                    currying[0],
                    vec![crate::model_tree::RefArg::Ref(vec![
                        "myHandler".to_string()
                    ])]
                );
            }
            _ => panic!("expected Ref"),
        }
    }

    #[test]
    fn parses_ref_arg_nested_ref_dotted_path() {
        let vt = parse_value_type("&dispatch(&pkg.handler)").unwrap();
        match vt {
            ModelValueTypeTree::Ref { currying, .. } => {
                assert_eq!(
                    currying[0],
                    vec![crate::model_tree::RefArg::Ref(vec![
                        "pkg".to_string(),
                        "handler".to_string()
                    ])]
                );
            }
            _ => panic!("expected Ref"),
        }
    }

    #[test]
    fn parses_ref_arg_mixed_list() {
        // &generateAll(this, "prefix", _, &cb, 5, _, true)
        let vt = parse_value_type(r#"&generateAll(this, "prefix", _, &cb, 5, _, true)"#).unwrap();
        match vt {
            ModelValueTypeTree::Ref { path, currying } => {
                use crate::model_tree::RefArg;
                assert_eq!(path, vec!["generateAll"]);
                assert_eq!(currying.len(), 1);
                assert_eq!(
                    currying[0],
                    vec![
                        RefArg::This,
                        RefArg::Str("prefix".to_string()),
                        RefArg::Hole,
                        RefArg::Ref(vec!["cb".to_string()]),
                        RefArg::Int(5),
                        RefArg::Hole,
                        RefArg::Bool(true),
                    ]
                );
            }
            _ => panic!("expected Ref"),
        }
    }

    #[test]
    fn parses_ref_arg_impl_param() {
        // Bare identifiers in currying args are now valid — they reference a
        // constructor parameter of the enclosing set entity (ImplParam).
        let vt = parse_value_type("&lookup(name)").unwrap();
        match vt {
            ModelValueTypeTree::Ref { path, currying } => {
                assert_eq!(path, vec!["lookup"]);
                assert_eq!(currying.len(), 1);
                assert_eq!(currying[0], vec![RefArg::ImplParam("name".to_string())]);
            }
            _ => panic!("expected Ref"),
        }
    }

    #[test]
    fn rejects_ref_arg_empty_nested_ref() {
        let err = parse_value_type("&lookup(&)").unwrap_err();
        assert!(
            err.0.contains("must be followed by") || err.0.contains("ref argument"),
            "expected helpful error, got: {}",
            err.0
        );
    }
}

#[cfg(test)]
mod tests {
    use super::{ModelNodeTree, ModelValueTypeTree, parse_model_content_tree};

    #[test]
    fn parses_model_tree_with_set_and_assign() {
        let tree = parse_model_content_tree(
            r#"
            {
                let seed: Number = 42
                set Service : core.Base : extra.Mixin(name: String, handler: impl[]) {
                    name: String,
                    tags: String[],
                    deps: &core.lookup(&name),
                    callback: (String, Number):(Bool),
                    config: impl { port: Number, flag: Bool }
                }
            }
            "#,
        )
        .expect("model should parse");

        assert_eq!(tree.nodes.len(), 2);
        match &tree.nodes[0] {
            ModelNodeTree::AssignVar(assign) => {
                assert_eq!(assign.name, "seed");
                assert!(matches!(assign.ty, Some(ModelValueTypeTree::Type(_))));
                assert_eq!(assign.context.file, None);
                assert_eq!(assign.context.line, Some(2));
                assert_eq!(assign.context.position, Some(17));
            }
            _ => panic!("expected assign node"),
        }

        match &tree.nodes[1] {
            ModelNodeTree::SetEntity(set) => {
                assert_eq!(set.name, "Service");
                assert_eq!(set.exts, vec!["core.Base", "extra.Mixin"]);
                assert_eq!(set.params.len(), 2);
                assert_eq!(set.attrs.len(), 5);
                assert!(matches!(set.attrs[1].value, ModelValueTypeTree::Array(_)));
                assert!(matches!(set.attrs[2].value, ModelValueTypeTree::Ref { .. }));
                assert!(matches!(
                    set.attrs[3].value,
                    ModelValueTypeTree::FuncDef { .. }
                ));
                assert!(matches!(
                    set.attrs[4].value,
                    ModelValueTypeTree::Impl { .. }
                ));
                assert_eq!(set.context.file, None);
                assert_eq!(set.context.line, Some(3));
                assert_eq!(set.context.position, Some(17));
                assert_eq!(set.attrs[0].context.line, Some(4));
                assert_eq!(set.attrs[0].context.position, Some(21));
            }
            _ => panic!("expected set node"),
        }
    }

    #[test]
    fn set_body_is_optional() {
        // Since optional braces were introduced, `set Service` with no body
        // is valid — it declares an empty set with no attrs.
        let tree = parse_model_content_tree("{ set Service }").expect("should parse");
        assert_eq!(tree.nodes.len(), 1);
        if let crate::model_tree::ModelNodeTree::SetEntity(e) = &tree.nodes[0] {
            assert_eq!(e.name, "Service");
            assert!(e.attrs.is_empty());
        } else {
            panic!("expected SetEntity node");
        }
    }

    // ── !> exec declaration tests ─────────────────────────────────────────────

    #[test]
    fn parses_exec_decl_bare() {
        let tree = parse_model_content_tree(
            r#"{
                set Deploy(host: String) {
                    host: String
                } !> bash
            }"#,
        )
        .expect("should parse");

        match &tree.nodes[0] {
            ModelNodeTree::SetEntity(set) => {
                assert_eq!(set.name, "Deploy");
                assert!(set.output.is_none(), "no >> should be present");
                let exec = set.exec.as_ref().expect("!> should be present");
                assert_eq!(exec.executor, "bash");
            }
            _ => panic!("expected SetEntity"),
        }
    }

    #[test]
    fn parses_exec_decl_sh_executor() {
        let tree = parse_model_content_tree("{ set RunScript !> sh }").expect("should parse");

        match &tree.nodes[0] {
            ModelNodeTree::SetEntity(set) => {
                let exec = set.exec.as_ref().expect("!> should be present");
                assert_eq!(exec.executor, "sh");
            }
            _ => panic!("expected SetEntity"),
        }
    }

    #[test]
    fn parses_exec_decl_python_executor() {
        let tree = parse_model_content_tree("{ set RunPy { script: String } !> python }")
            .expect("should parse");

        match &tree.nodes[0] {
            ModelNodeTree::SetEntity(set) => {
                let exec = set.exec.as_ref().expect("!> should be present");
                assert_eq!(exec.executor, "python");
            }
            _ => panic!("expected SetEntity"),
        }
    }

    #[test]
    fn parses_exec_decl_combined_with_output() {
        // A set may declare both >> (write to file) and !> (execute) at once.
        // >> comes before the body; !> comes after.
        let tree =
            parse_model_content_tree(r#"{ set Deploy >> "deploy.sh" { host: String } !> bash }"#)
                .expect("should parse");

        match &tree.nodes[0] {
            ModelNodeTree::SetEntity(set) => {
                let output = set.output.as_ref().expect(">> should be present");
                assert_eq!(output.path, "deploy.sh");
                assert!(matches!(
                    output.mode,
                    crate::model_tree::WriteMode::AlwaysWrite
                ));
                let exec = set.exec.as_ref().expect("!> should be present");
                assert_eq!(exec.executor, "bash");
            }
            _ => panic!("expected SetEntity"),
        }
    }

    #[test]
    fn parses_exec_decl_combined_with_write_once_output() {
        let tree =
            parse_model_content_tree(r#"{ set Init >>? "init.sh" { name: String } !> bash }"#)
                .expect("should parse");

        match &tree.nodes[0] {
            ModelNodeTree::SetEntity(set) => {
                let output = set.output.as_ref().expect(">>? should be present");
                assert!(matches!(
                    output.mode,
                    crate::model_tree::WriteMode::WriteOnce
                ));
                let exec = set.exec.as_ref().expect("!> should be present");
                assert_eq!(exec.executor, "bash");
            }
            _ => panic!("expected SetEntity"),
        }
    }

    #[test]
    fn no_exec_decl_when_absent() {
        let tree = parse_model_content_tree(r#"{ set Plain >> "out.txt" { value: String } }"#)
            .expect("should parse");
        // exec should be absent — no !> declared.

        match &tree.nodes[0] {
            ModelNodeTree::SetEntity(set) => {
                assert!(set.exec.is_none(), "exec should be None when !> is absent");
            }
            _ => panic!("expected SetEntity"),
        }
    }

    #[test]
    fn rejects_exec_decl_missing_executor() {
        let err = parse_model_content_tree("{ set Bad !> }").expect_err("should fail");
        assert!(
            err.0.contains("executor name") || err.0.contains("!>"),
            "got: {}",
            err.0
        );
    }

    #[test]
    fn exec_decl_roundtrips_through_formatter() {
        use crate::formatter::format_model;
        use crate::parser::parse_domain_model;

        let input = r#"
            set Deploy(host: String) {
                host: String
            } !> bash
        "#;

        let parsed = parse_domain_model(input).expect("should parse");
        let formatted = format_model(&parsed);
        assert!(
            formatted.contains("!> bash"),
            "formatted output should contain `!> bash`\ngot:\n{formatted}"
        );
    }

    #[test]
    fn exec_decl_with_output_roundtrips_through_formatter() {
        use crate::formatter::format_model;
        use crate::parser::parse_domain_model;

        let input = r#"
            set Deploy(host: String) >> "deploy.sh" {
                host: String
            } !> bash
        "#;

        let parsed = parse_domain_model(input).expect("should parse");
        let formatted = format_model(&parsed);
        assert!(
            formatted.contains(">> \"deploy.sh\""),
            "formatted output should contain `>> \"deploy.sh\"`\ngot:\n{formatted}"
        );
        assert!(
            formatted.contains("!> bash"),
            "formatted output should contain `!> bash`\ngot:\n{formatted}"
        );
    }
}
