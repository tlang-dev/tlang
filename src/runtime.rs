// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang compiler and runtime executor.
//!
//! Translates a [`crate::ast::DomainModel`] into a [`CompiledProgram`] and
//! executes it, producing a [`RunResult`].
//!
//! See [`docs/architecture.md`](../../docs/architecture.md) for the full pipeline.
//!
//! # Submodules
//!
//! | Submodule | Contents |
//! |-----------|----------|
//! | [`encoding`] | Binary encoder/decoder for `.tlangc` bytecode |
//! | [`helper_parser`] | Lexer + parser for `{ … }` helper code blocks |
//! | [`type_checker`] | Static type-checker over compiled IR |
//! | `libraries/` | Built-in `TLang.*` library implementations |

use std::cell::RefCell;
use std::collections::{BTreeMap, HashMap};
use std::rc::Rc;

mod libraries;

use crate::ast::{DomainBlock, TemplateBlock, TemplateContent, TemplateParam};
use crate::model_tree::{
    ModelAssignVarTree, ModelBlockTree, ModelNodeTree, ModelSetAttributeTree, ModelSetEntityTree,
    ModelValueTypeTree, parse_model_block_tree, parse_model_content_tree,
};
use crate::tree_context::TreeContext;
use libraries::{ImportResolver, call_builtin};

const TEMPLATE_INSTANCE_TYPE: &str = "TemplateInstance";
const MCP_TOOL_INTERNAL_MODEL: &str = r#"
{
    set MCPTool(name: String, description: String, run: (String):(String)) {
    }
}
"#;

#[derive(Debug, Clone)]
pub enum Value {
    Int(i64),
    Float(f64),
    Bool(bool),
    String(String),
    Leaf(LeafObject),
    List(Vec<Value>),
    Map(std::collections::BTreeMap<String, Value>),
    Unit,
    /// A runtime instance of a `set` entity, created via
    /// `let x = EntityName(param: value, ...)` in a helper block.
    SetInstance(SetInstanceObject),
    /// A bound callable returned by `inst.attrs()`.
    /// Calling `op()` dispatches the underlying ref; `op.name` returns the
    /// attribute name as a String.
    BoundAttr(BoundAttrObject),
    /// A mutable string accumulator.  Unlike other `Value` variants this uses
    /// reference semantics (`Rc<RefCell<String>>`): calling `sb.append(text)`
    /// mutates the shared buffer in-place so no `let`-rebinding is needed.
    StringBuilder(Rc<RefCell<std::string::String>>),
    /// An anonymous function (lambda) created with `(params) => expr` or
    /// `(params) => { block }` syntax.  Captures the lexical environment at
    /// the point of definition.
    Lambda(Rc<LambdaObject>),
    /// A mutable PDF document state.  Uses reference semantics so that
    /// `TLang.Pdf.*` mutating calls can share the same document across bindings.
    PdfDoc(Rc<std::cell::RefCell<crate::pdf_lib::PdfState>>),
}

impl PartialEq for Value {
    fn eq(&self, other: &Self) -> bool {
        match (self, other) {
            (Value::Int(a), Value::Int(b)) => a == b,
            // Float: use bit-level equality so NaN != NaN (consistent with IEEE 754).
            (Value::Float(a), Value::Float(b)) => a.to_bits() == b.to_bits(),
            (Value::Bool(a), Value::Bool(b)) => a == b,
            (Value::String(a), Value::String(b)) => a == b,
            (Value::Leaf(a), Value::Leaf(b)) => a == b,
            (Value::List(a), Value::List(b)) => a == b,
            (Value::Map(a), Value::Map(b)) => a == b,
            (Value::Unit, Value::Unit) => true,
            (Value::SetInstance(a), Value::SetInstance(b)) => a == b,
            (Value::BoundAttr(a), Value::BoundAttr(b)) => {
                a.instance == b.instance && a.attr_name == b.attr_name
            }
            // Two StringBuilders are equal only when they share the same allocation.
            (Value::StringBuilder(a), Value::StringBuilder(b)) => Rc::ptr_eq(a, b),
            // Two Lambdas are equal only when they share the same allocation.
            (Value::Lambda(a), Value::Lambda(b)) => Rc::ptr_eq(a, b),
            // Two PdfDocs are equal only when they share the same allocation.
            (Value::PdfDoc(a), Value::PdfDoc(b)) => Rc::ptr_eq(a, b),
            _ => false,
        }
    }
}

impl Eq for Value {}

/// A runtime instance of a `set` model entity.
/// A bound callable value returned by `inst.attrs()`.
///
/// - Calling `op()` dispatches the underlying ref on `instance` for `attr_name`.
/// - Accessing `op.name` returns the attribute name as a `Value::String`.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct BoundAttrObject {
    pub instance: SetInstanceObject,
    pub attr_name: String,
}

/// An anonymous function (lambda / closure) created with `(params) => expr`
/// or `(params) => { block }` syntax inside a helper block.
///
/// The `captured` map holds a snapshot of the enclosing scope's bindings at
/// the point the lambda was defined, implementing lexical (static) scoping.
#[derive(Debug, Clone)]
pub struct LambdaObject {
    /// Parameter names declared between `(` and `)`.
    pub(crate) params: Vec<String>,
    /// Body: either a single expression or a `{ … }` block.
    pub(crate) body: LambdaBody,
    /// Snapshot of the enclosing frame's bindings at definition time.
    pub(crate) captured: HashMap<String, Value>,
}

/// The body of a [`LambdaObject`]: either an expression or a statement block.
#[derive(Debug, Clone)]
pub(crate) enum LambdaBody {
    Expr(Box<Expr>),
    Block(Vec<Stmt>),
}

/// A runtime instance of a `set` model entity.
///
/// Created by evaluating `EntityName(param1: val1, param2: val2)`
/// in helper code.  `entity_name` identifies which `set` entity in the model
/// block this is an instance of.  `impls` holds the values supplied for the
/// mandatory constructor parameters declared in the `(…)` section of the `set`
/// declaration, keyed by parameter name.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct SetInstanceObject {
    /// Name of the `set` entity this is an instance of.
    pub entity_name: String,
    /// Values provided for the mandatory `(…)` params at instantiation.
    pub impls: std::collections::BTreeMap<String, Value>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct LeafObject {
    pub(crate) fields: BTreeMap<String, Value>,
}

impl LeafObject {
    pub(crate) fn new(fields: BTreeMap<String, Value>) -> Self {
        Self { fields }
    }

    pub(crate) fn get(&self, key: &str) -> Option<&Value> {
        self.fields.get(key)
    }
}

/// An error produced during compilation (parsing helper blocks, type-checking,
/// or translating a [`crate::ast::DomainModel`] into a [`CompiledProgram`]).
///
/// The inner `String` contains a human-readable description of the problem
/// suitable for display to the developer.
#[derive(Debug)]
pub struct CompileError(pub String);

impl std::fmt::Display for CompileError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Compilation error: {}", self.0)
    }
}

impl std::error::Error for CompileError {}

/// An error produced at runtime while executing a compiled TLang program.
///
/// The inner `String` contains a human-readable description of the problem,
/// including context such as function name and argument values where available.
#[derive(Debug)]
pub struct RuntimeError(pub String);

impl std::fmt::Display for RuntimeError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Runtime error: {}", self.0)
    }
}

impl std::error::Error for RuntimeError {}

/// A type annotation attached to a function parameter or return position.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum TypeAnnotation {
    Int,
    Float,
    Bool,
    String,
    List,
    Map,
    Unit,
    Leaf,
    TmplLang,
    TmplDoc,
    TmplStyle,
    TmplData,
    TmplCmd,
    /// A callable value: lambda or function reference.
    /// Parameters typed `Func` accept both `(params) => body` lambdas and
    /// `&funcName` function references.
    Func,
}

impl std::fmt::Display for TypeAnnotation {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            TypeAnnotation::Int => write!(f, "Int"),
            TypeAnnotation::Float => write!(f, "Float"),
            TypeAnnotation::Bool => write!(f, "Bool"),
            TypeAnnotation::String => write!(f, "String"),
            TypeAnnotation::List => write!(f, "List"),
            TypeAnnotation::Map => write!(f, "Map"),
            TypeAnnotation::Unit => write!(f, "Unit"),
            TypeAnnotation::Leaf => write!(f, "Leaf"),
            TypeAnnotation::TmplLang => write!(f, "TmplLang"),
            TypeAnnotation::TmplDoc => write!(f, "TmplDoc"),
            TypeAnnotation::TmplStyle => write!(f, "TmplStyle"),
            TypeAnnotation::TmplData => write!(f, "TmplData"),
            TypeAnnotation::TmplCmd => write!(f, "TmplCmd"),
            TypeAnnotation::Func => write!(f, "Func"),
        }
    }
}

/// The fully compiled, self-contained representation of a TLang program.
///
/// A `CompiledProgram` holds every artifact produced by the compiler:
///
/// - **`functions`** — compiled helper functions (AST + resolved types).
/// - **`model`** — the static model tree (`model { … }` block), used by
///   `TLang.Leaf`, set-entity instantiation, and template generation.
/// - **`templates`** — language-specific template functions (`tmpl`).
/// - **`data_templates`** — structured data templates (`data`).
/// - **`imports`** — resolved import aliases for `TLang.*` built-in libraries.
///
/// A `CompiledProgram` can be serialized to a compact binary `.tlangc`
/// bytecode file via [`CompiledProgram::encode`] and loaded back with
/// [`CompiledProgram::decode`].
#[derive(Debug, Default)]
pub struct CompiledProgram {
    functions: HashMap<String, Function>,
    /// Top-level statements (e.g., `let` bindings) in helper blocks.
    top_level_stmts: Vec<Stmt>,
    model: ModelBlockTree,
    templates: HashMap<String, TemplateFunction>,
    data_templates: HashMap<String, DataTemplateFunction>,
    cmd_templates: HashMap<String, CmdTemplateFunction>,
    raw_templates: HashMap<String, RawTemplateFunction>,
    doc_templates: HashMap<String, DocTemplateFunction>,
    style_templates: HashMap<String, StyleTemplateFunction>,
    imports: ImportResolver,
    /// Names explicitly declared with `expose` in the main entry-point file.
    /// Only functions in this list may be invoked via `tlang exec <name>`.
    pub exposes: Vec<String>,
    /// Names of test blocks declared with `test name { … }`.
    /// The corresponding compiled function is stored in `functions` under
    /// `"__test__{name}"`.
    pub test_names: Vec<String>,
}

// ---------------------------------------------------------------------------
// Public function introspection API (used by the LSP)
// ---------------------------------------------------------------------------

/// Metadata about a compiled helper function, used by LSP features such as
/// go-to-definition, hover, and signature help.
#[derive(Debug, Clone)]
pub struct FunctionInfo {
    /// Function name as it appears in the source.
    pub name: String,
    /// Parameter list: each entry is `(name, optional_type_as_string)`.
    pub params: Vec<(String, Option<String>)>,
    /// Optional declared return type as a string (e.g. `"String"`).
    pub return_type: Option<String>,
    /// Byte offset of the `func` keyword in the source document.
    pub offset: usize,
    /// File stem of the dependency that provides this function, or `None` for
    /// functions defined in the main entry-point file.
    pub source_file: Option<String>,
    /// Full filesystem path of the file containing this function, or `None`
    /// for functions defined in the main entry-point file.
    pub source_path: Option<String>,
    /// Cleaned content of the `/** ... */` doc-comment preceding this
    /// function, if present.
    pub doc: Option<String>,
}

impl FunctionInfo {
    /// Format the function signature as it would appear in TLang source.
    ///
    /// Example: `func greet(name: String): String`
    pub fn signature(&self) -> String {
        let params = self
            .params
            .iter()
            .map(|(n, t)| match t {
                Some(ty) => format!("{n}: {ty}"),
                None => n.clone(),
            })
            .collect::<Vec<_>>()
            .join(", ");
        let ret = match &self.return_type {
            Some(ty) => format!(": {ty}"),
            None => String::new(),
        };
        format!("func {}({}){}", self.name, params, ret)
    }
}

impl CompiledProgram {
    /// Return metadata for every compiled helper function.
    /// Return the list of exposed function names (declared with `expose` in the source).
    pub fn exposed_names(&self) -> &[String] {
        &self.exposes
    }

    /// Return `true` if a helper function with the given name exists in this program.
    pub fn has_function(&self, name: &str) -> bool {
        self.functions.contains_key(name)
    }

    pub fn function_infos(&self) -> Vec<FunctionInfo> {
        self.functions
            .values()
            .map(|f| FunctionInfo {
                name: f.name.clone(),
                params: f
                    .params
                    .iter()
                    .map(|(n, t)| (n.clone(), t.as_ref().map(|a| a.to_string())))
                    .collect(),
                return_type: f.return_type.as_ref().map(|t| t.to_string()),
                offset: f.offset,
                source_file: f.source_file.clone(),
                source_path: f.source_path.clone(),
                doc: f.doc.clone(),
            })
            .collect()
    }
}

/// Collect semantic warnings (e.g. missing type annotations) for a compiled
/// program.  These are non-fatal and intended for IDE tooling (LSP warning
/// diagnostics).
pub fn collect_semantic_warnings(program: &CompiledProgram) -> Vec<String> {
    type_checker::TypeChecker::new(&program.functions)
        .collect_warnings()
        .into_iter()
        .map(|w| w.0)
        .collect()
}

/// The result of executing a compiled TLang program via [`run_main`].
pub struct RunResult {
    /// All text written to the program's output stream during execution
    /// (e.g. via `TLang.Terminal.println`).
    pub output: String,
    /// The value returned by the `main` helper function.
    pub return_value: Value,
    /// The static model tree extracted from the program, available for
    /// post-execution introspection (e.g. by the LSP or template engine).
    pub model_tree: ModelBlockTree,
}

// Manually implement Debug for RunResult to avoid requiring Debug on all
// transitively referenced types.
impl std::fmt::Debug for RunResult {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        f.debug_struct("RunResult")
            .field("output", &self.output)
            .field("return_value", &self.return_value)
            .finish()
    }
}

#[derive(Debug, Clone)]
struct Function {
    name: String,
    /// Each parameter is `(name, optional_type_annotation)`.
    params: Vec<(String, Option<TypeAnnotation>)>,
    /// Optional declared return type.
    return_type: Option<TypeAnnotation>,
    body: Vec<Stmt>,
    /// Byte offset of the `func` keyword in the source document.
    offset: usize,
    /// File stem of the dependency that provides this function (`None` for
    /// functions defined in the main entry-point file).
    source_file: Option<String>,
    /// Full filesystem path of the file containing this function (`None` for
    /// the main entry-point file).
    source_path: Option<String>,
    /// Cleaned content of the `/** ... */` doc-comment immediately preceding
    /// this function declaration, if any.
    doc: Option<String>,
}

#[derive(Debug, Clone)]
struct TemplateFunction {
    lang: String,
    name: String,
    params: Vec<TemplateParam>,
    content: TemplateContent,
}

#[derive(Debug, Clone)]
struct DataTemplateFunction {
    langs: Vec<String>,
    name: String,
    params: Vec<TemplateParam>,
    content: String,
}

#[derive(Debug, Clone)]
struct CmdTemplateFunction {
    langs: Vec<String>,
    name: String,
    params: Vec<TemplateParam>,
    content: String,
}

/// Which processing mode a `raw [...]` block uses.
#[derive(Debug, Clone, PartialEq, Eq)]
enum RawVariant {
    /// Body is emitted entirely verbatim — no `${param}` substitution.
    AsIs,
    /// Body is emitted with `${param}` references replaced by argument values;
    /// no other processing is applied.
    Replaced,
}

#[derive(Debug, Clone)]
struct RawTemplateFunction {
    variant: RawVariant,
    name: String,
    params: Vec<TemplateParam>,
    /// Raw body text **including** the surrounding `{ }`.
    content: String,
}

#[derive(Debug, Clone)]
struct DocTemplateFunction {
    langs: Vec<String>,
    name: String,
    params: Vec<TemplateParam>,
    content: String,
}

#[derive(Debug, Clone)]
struct StyleTemplateFunction {
    langs: Vec<String>,
    name: String,
    params: Vec<TemplateParam>,
    content: String,
}

#[derive(Debug, Clone)]
enum Stmt {
    Let {
        name: String,
        expr: Expr,
    },
    Call(CallExpr),
    /// `if ( cond ) { then_body } else? { else_body }`
    If {
        condition: Expr,
        then_body: Vec<Stmt>,
        else_body: Option<Vec<Stmt>>,
    },
    /// `for ( var in collection ) { body }`
    ForIn {
        var: String,
        iterable: Expr,
        body: Vec<Stmt>,
    },
    /// `for ( [a, b, c] in collection ) { body }` — destructuring for loop.
    ///
    /// Each item in the collection is expected to be a `Value::List`; its
    /// elements are bound to `vars[0]`, `vars[1]`, … in turn.  Used with
    /// `TLang.List.zip` and `TLang.List.enumerate`.
    ForInDestructure {
        vars: Vec<String>,
        iterable: Expr,
        body: Vec<Stmt>,
    },
    /// `for ( var start to|until end ) { body }`
    ///
    /// `to` is inclusive of `end`; `until` is exclusive.
    ForRange {
        var: String,
        start: Expr,
        end: Expr,
        inclusive: bool,
        body: Vec<Stmt>,
    },
    Match {
        target: Expr,
        arms: Vec<MatchArm>,
        default: Option<Vec<Stmt>>,
    },
    Return(Expr),
}

#[derive(Debug, Clone)]
enum Expr {
    Literal(Value),
    Var(String),
    Op(Op, Box<Expr>, Box<Expr>),
    Call(CallExpr),
    If {
        condition: Box<Expr>,
        then_expr: Box<Expr>,
        else_expr: Box<Expr>,
    },
    MatchExpr {
        target: Box<Expr>,
        arms: Vec<(MatchPattern, Expr)>,
        default: Box<Expr>,
    },
    /// Set-entity instantiation: `EntityName(param1: val1, param2: val2)`.
    ///
    /// Parsed from helper code when the opening `(` is immediately followed by
    /// a bare `identifier:` named-argument pattern.  At evaluation time every
    /// field expression is evaluated and the results are stored in a
    /// [`SetInstanceObject`].
    SetNew(SetNewExpr),
    /// Runtime type/entity-instance check (`x is Type`, `x !is Type`).
    IsType {
        expr: Box<Expr>,
        type_name: String,
        negated: bool,
    },
    /// Lambda expression: `(params) => expr` or `(params) => { block }`.
    ///
    /// At evaluation time the current frame is captured (closed over) and a
    /// [`Value::Lambda`] is returned.  The lambda can be stored in a `let`
    /// binding, passed as an argument, or called immediately.
    Lambda {
        params: Vec<String>,
        body: LambdaBody,
    },
    /// A list expression whose items are arbitrary expressions, e.g.
    /// `[Field("x", Email()), someVar, computeItem()]`.
    /// Evaluated at runtime by evaluating each element in order.
    ListExpr(Vec<Expr>),
}

#[derive(Debug, Clone)]
struct CallExpr {
    target: String,
    args: Vec<Expr>,
    /// Byte offset of the call expression in the original source document.
    /// Used for precise error reporting in the type checker.
    offset: usize,
}

/// A set-entity instantiation expression.
///
/// `entity` is the name of the `set` entity declared in the `model {}` block.
/// `fields` is the list of `(param_name, value_expr)` named-argument pairs
/// supplied by the caller.
#[derive(Debug, Clone)]
struct SetNewExpr {
    entity: String,
    fields: Vec<(String, Box<Expr>)>,
}

#[derive(Debug, Clone)]
struct MatchArm {
    pattern: MatchPattern,
    body: Vec<Stmt>,
}

#[derive(Debug, Clone)]
enum MatchPattern {
    Value(Expr),
    Condition(Expr),
    IsType { type_name: String, negated: bool },
}

#[derive(Debug, Clone, Copy)]
enum Op {
    // arithmetic
    Add,
    Sub,
    Mul,
    Div,
    Mod,
    // comparison
    Eq,
    Ne,
    Lt,
    Gt,
    Lte,
    Gte,
    // logical
    And,
    Or,
    // null handling
    NullCoalesce,
}

pub fn compile_from_domain_model(
    model: &crate::ast::DomainModel,
) -> Result<CompiledProgram, CompileError> {
    // Collect and merge ALL model blocks from the body.  The loader prepends
    // dependency blocks before the consumer's blocks, so a naïve `find_map`
    // would only see the first dependency's model and silently ignore every
    // subsequent model block (including the consumer's own).  We fold all
    // blocks together so that every `set` declaration — regardless of which
    // file it came from — is visible to `Leaf.model()` at runtime.
    let mut static_model = {
        let mut merged = crate::model_tree::ModelBlockTree::default();
        for block in &model.body {
            if let DomainBlock::Model(mb) = block {
                let tree = parse_model_block_tree(mb)
                    .map_err(|e| CompileError(format!("invalid model block: {e}")))?;
                merged.nodes.extend(tree.nodes);
            }
        }
        merged
    };
    inject_internal_model_nodes(model, &mut static_model)?;

    // Collect ALL helper blocks — dependencies are prepended by the loader so
    // the consumer's block comes last.  We merge with last-wins semantics so
    // that a consumer can override a dependency's `main()` (or any other
    // function) simply by re-declaring it.
    let helper_blocks_meta: Vec<(usize, &str, Option<&str>, Option<&str>, Option<&str>)> = model
        .body
        .iter()
        .filter_map(|block| match block {
            DomainBlock::Helper(helper) => Some((
                helper.content_start,
                helper.content.as_str(),
                helper.source_file.as_deref(),
                helper.source_path.as_deref(),
                helper.package_name.as_deref(),
            )),
            _ => None,
        })
        .collect();

    validate_template_languages(model)?;

    // Library projects (no `main:` entry point, no helper block) are valid —
    // they expose only templates and model definitions for consumers to import.
    // We still produce a full CompiledProgram so that `tlang compile` can
    // verify the project parses and type-checks cleanly without requiring a
    // runnable `main()` function.

    fn inject_internal_model_nodes(
        model: &crate::ast::DomainModel,
        static_model: &mut ModelBlockTree,
    ) -> Result<(), CompileError> {
        let uses_mcp_tool = model
            .header
            .uses
            .iter()
            .any(|u| u.path.len() == 2 && u.path[0] == "TLang" && u.path[1] == "MCPTool");
        if !uses_mcp_tool {
            return Ok(());
        }

        let already_declared = static_model.nodes.iter().any(|node| {
            matches!(
                node,
                ModelNodeTree::SetEntity(set) if set.name == "MCPTool"
            )
        });
        if already_declared {
            return Ok(());
        }

        let tree = parse_model_content_tree(MCP_TOOL_INTERNAL_MODEL).map_err(|e| {
            CompileError(format!(
                "internal error: invalid internal MCPTool model: {e}"
            ))
        })?;
        static_model.nodes.extend(tree.nodes);
        Ok(())
    }

    let mut compiled = merge_helper_blocks_with_source_with_offsets(&helper_blocks_meta)?;
    let templates = collect_templates(model)?;
    for name in templates.keys() {
        if compiled.functions.contains_key(name) {
            return Err(CompileError(format!(
                "template `{name}` conflicts with helper function name"
            )));
        }
    }
    let data_templates = collect_data_templates(model)?;
    for name in data_templates.keys() {
        if compiled.functions.contains_key(name) {
            return Err(CompileError(format!(
                "data template `{name}` conflicts with helper function name"
            )));
        }
        if templates.contains_key(name) {
            return Err(CompileError(format!(
                "data template `{name}` conflicts with a lang template name"
            )));
        }
    }
    let cmd_templates = collect_cmd_templates(model)?;
    for name in cmd_templates.keys() {
        if compiled.functions.contains_key(name) {
            return Err(CompileError(format!(
                "cmd template `{name}` conflicts with helper function name"
            )));
        }
        if templates.contains_key(name) {
            return Err(CompileError(format!(
                "cmd template `{name}` conflicts with a lang template name"
            )));
        }
        if data_templates.contains_key(name) {
            return Err(CompileError(format!(
                "cmd template `{name}` conflicts with a data template name"
            )));
        }
    }
    let raw_templates = collect_raw_templates(model)?;
    for name in raw_templates.keys() {
        if compiled.functions.contains_key(name) {
            return Err(CompileError(format!(
                "raw template `{name}` conflicts with helper function name"
            )));
        }
        if templates.contains_key(name) {
            return Err(CompileError(format!(
                "raw template `{name}` conflicts with a lang template name"
            )));
        }
        if data_templates.contains_key(name) {
            return Err(CompileError(format!(
                "raw template `{name}` conflicts with a data template name"
            )));
        }
    }
    let doc_templates = collect_doc_templates(model)?;
    let style_templates = collect_style_templates(model)?;
    for name in doc_templates.keys() {
        if compiled.functions.contains_key(name) {
            return Err(CompileError(format!(
                "doc template `{name}` conflicts with helper function name"
            )));
        }
        if templates.contains_key(name) {
            return Err(CompileError(format!(
                "doc template `{name}` conflicts with a lang template name"
            )));
        }
        if data_templates.contains_key(name) {
            return Err(CompileError(format!(
                "doc template `{name}` conflicts with a data template name"
            )));
        }
        if raw_templates.contains_key(name) {
            return Err(CompileError(format!(
                "doc template `{name}` conflicts with a raw template name"
            )));
        }
    }
    // Conflict checks for style templates
    for name in style_templates.keys() {
        if compiled.functions.contains_key(name) {
            return Err(CompileError(format!(
                "style template `{name}` conflicts with helper function name"
            )));
        }
        if templates.contains_key(name) {
            return Err(CompileError(format!(
                "style template `{name}` conflicts with a lang template name"
            )));
        }
        if data_templates.contains_key(name) {
            return Err(CompileError(format!(
                "style template `{name}` conflicts with a data template name"
            )));
        }
        if raw_templates.contains_key(name) {
            return Err(CompileError(format!(
                "style template `{name}` conflicts with a raw template name"
            )));
        }
        if doc_templates.contains_key(name) {
            return Err(CompileError(format!(
                "style template `{name}` conflicts with a doc template name"
            )));
        }
    }
    compiled.model = static_model;
    validate_model_type_refs(&compiled.model)?;
    compiled.templates = templates;
    compiled.data_templates = data_templates;
    compiled.cmd_templates = cmd_templates;
    compiled.raw_templates = raw_templates;
    compiled.doc_templates = doc_templates;
    compiled.style_templates = style_templates;
    compiled.imports = ImportResolver::from_uses(&model.header.uses);
    compiled.exposes = model.header.exposes.clone();

    // Compile test blocks into synthetic functions named `__test__{name}`.
    for block in &model.body {
        if let DomainBlock::Test(test_block) = block {
            let fn_name = format!("__test__{}", test_block.name);
            let inner = test_block
                .content
                .trim()
                .strip_prefix('{')
                .and_then(|s| s.strip_suffix('}'))
                .unwrap_or("");
            let wrapped = format!("{{\nfunc {fn_name}() {{\n{inner}\n}}\n}}");
            let mut block_fns: HashMap<String, Function> = HashMap::new();
            let mut block_top: Vec<Stmt> = Vec::new();
            let mut parser = HelperParser::new(&wrapped)
                .map_err(|e| CompileError(format!("test `{}`: parse error: {e}", test_block.name)))?;
            parser
                .parse_functions_into(&mut block_fns, &mut block_top)
                .map_err(|e| CompileError(format!("test `{}`: compile error: {e}", test_block.name)))?;
            compiled.functions.extend(block_fns);
            compiled.test_names.push(test_block.name.clone());
        }
    }

    // Ensure no `let` binding shadows an import alias.
    let alias_names: std::collections::HashSet<String> = model
        .header
        .uses
        .iter()
        .filter_map(|u| u.alias.clone())
        .collect();
    if !alias_names.is_empty() {
        for (func_name, func) in &compiled.functions {
            check_no_alias_bindings(&func.body, &alias_names, func_name)?;
        }
    }

    // Validate `main` function signature if present.
    if let Some(main_fn) = compiled.functions.get("main") {
        // At most one parameter.
        if main_fn.params.len() > 1 {
            return Err(CompileError(format!(
                "`main` must have at most 1 parameter (String[]), but {} were declared",
                main_fn.params.len()
            )));
        }
        // If a parameter is declared it must be typed as List (String[]).
        if let Some((param_name, param_ty)) = main_fn.params.first() {
            match param_ty {
                Some(TypeAnnotation::List) => {} // ok — String[] maps to List
                Some(other) => {
                    return Err(CompileError(format!(
                        "`main` parameter `{param_name}` must be typed `String[]`, \
                         found `{other}`"
                    )));
                }
                None => {
                    return Err(CompileError(format!(
                        "`main` parameter `{param_name}` must be typed `String[]`"
                    )));
                }
            }
        }
        // Return type must be declared.
        if main_fn.return_type.is_none() {
            return Err(CompileError(format!(
                "function `main` has no declared return type; \
                 expected one of: String, Int, Bool, Unit \
                 (at_offset: {})",
                main_fn.offset
            )));
        }
        // Return type, if declared, must be a primitive scalar.
        if let Some(ret) = &main_fn.return_type {
            match ret {
                TypeAnnotation::Int
                | TypeAnnotation::Bool
                | TypeAnnotation::String
                | TypeAnnotation::Unit => {}
                other => {
                    return Err(CompileError(format!(
                        "`main` return type must be a primitive (String, Int, Bool, or Unit), \
                         found `{other}`"
                    )));
                }
            }
        }
    }

    // Collect all template names and model entity names so the type-checker can
    // accept calls to them (e.g. `service(inst)` for lang templates, `Point()`
    // for set-entity positional instantiations).
    let mut known_callables: std::collections::HashSet<String> = compiled
        .templates
        .keys()
        .chain(compiled.data_templates.keys())
        .chain(compiled.cmd_templates.keys())
        .chain(compiled.raw_templates.keys())
        .chain(compiled.doc_templates.keys())
        .chain(compiled.style_templates.keys())
        .cloned()
        .collect();
    for node in &compiled.model.nodes {
        if let crate::model_tree::ModelNodeTree::SetEntity(set) = node {
            known_callables.insert(set.name.clone());
        }
    }

    // Run type-checking with dep_exposes so visibility is enforced.
    type_checker::TypeChecker::new(&compiled.functions)
        .with_dep_exposes(&model.header.dep_exposes)
        .with_known_callables(known_callables)
        .check_all()?;

    // Validate all set-entity instantiation expressions at compile time:
    // every entity must exist in the model, every required param must be
    // supplied, and no unknown params may be provided.
    check_set_instantiations(&compiled)?;

    Ok(compiled)
}

/// Walk every function body in `program` and validate each `SetNew` expression:
/// - the named entity exists in the model,
/// - every mandatory constructor param is supplied,
/// - no unrecognised param names are provided.
///
/// Errors are surfaced as [`CompileError`]s so they appear both in `tlang compile`
/// output and in the LSP diagnostics panel.
fn check_set_instantiations(program: &CompiledProgram) -> Result<(), CompileError> {
    for (func_name, func) in &program.functions {
        check_set_new_in_stmts(&func.body, &program.model, func_name)?;
    }
    Ok(())
}

fn check_set_new_in_stmts(
    stmts: &[Stmt],
    model: &crate::model_tree::ModelBlockTree,
    func_name: &str,
) -> Result<(), CompileError> {
    for stmt in stmts {
        match stmt {
            Stmt::Let { expr, .. } => check_set_new_in_expr(expr, model, func_name)?,
            Stmt::Call(call) => {
                for arg in &call.args {
                    check_set_new_in_expr(arg, model, func_name)?;
                }
            }
            Stmt::Return(expr) => check_set_new_in_expr(expr, model, func_name)?,
            Stmt::If {
                condition,
                then_body,
                else_body,
            } => {
                check_set_new_in_expr(condition, model, func_name)?;
                check_set_new_in_stmts(then_body, model, func_name)?;
                if let Some(e) = else_body {
                    check_set_new_in_stmts(e, model, func_name)?;
                }
            }
            Stmt::ForIn { iterable, body, .. } => {
                check_set_new_in_expr(iterable, model, func_name)?;
                check_set_new_in_stmts(body, model, func_name)?;
            }
            Stmt::ForInDestructure { iterable, body, .. } => {
                check_set_new_in_expr(iterable, model, func_name)?;
                check_set_new_in_stmts(body, model, func_name)?;
            }
            Stmt::ForRange {
                start, end, body, ..
            } => {
                check_set_new_in_expr(start, model, func_name)?;
                check_set_new_in_expr(end, model, func_name)?;
                check_set_new_in_stmts(body, model, func_name)?;
            }
            Stmt::Match {
                target,
                arms,
                default,
            } => {
                check_set_new_in_expr(target, model, func_name)?;
                for arm in arms {
                    check_set_new_in_stmts(&arm.body, model, func_name)?;
                }
                if let Some(d) = default {
                    check_set_new_in_stmts(d, model, func_name)?;
                }
            }
        }
    }
    Ok(())
}

fn check_set_new_in_expr(
    expr: &Expr,
    model: &crate::model_tree::ModelBlockTree,
    func_name: &str,
) -> Result<(), CompileError> {
    match expr {
        Expr::SetNew(sn) => {
            // Entity must be declared in the model.
            if find_set_entity(model, &sn.entity).is_none() {
                let simple = resolve_entity_name(&sn.entity);
                return Err(CompileError(format!(
                    "in `{func_name}`: unknown set entity `{}`; \
                     declare it with `set {simple}(…) {{…}}` in a `model {{}}` block",
                    sn.entity
                )));
            }
            let all_params = collect_effective_params(model, &sn.entity);
            // Every required param must be provided.
            for param in &all_params {
                if let Some(param_name) = &param.attr {
                    if !sn.fields.iter().any(|(k, _)| k == param_name) {
                        return Err(CompileError(format!(
                            "in `{func_name}`: set entity `{}` requires `{}: …` at instantiation",
                            sn.entity, param_name
                        )));
                    }
                }
            }
            // No unrecognised params may be supplied.
            for (key, field_expr) in &sn.fields {
                if !all_params
                    .iter()
                    .any(|p| p.attr.as_deref() == Some(key.as_str()))
                {
                    return Err(CompileError(format!(
                        "in `{func_name}`: `{key}` is not a constructor param of \
                         set entity `{}`; only params declared in the `(…)` section \
                         may be provided at instantiation",
                        sn.entity
                    )));
                }
                // Recurse into the value expression.
                check_set_new_in_expr(field_expr, model, func_name)?;
            }
        }
        Expr::Op(_, left, right) => {
            check_set_new_in_expr(left, model, func_name)?;
            check_set_new_in_expr(right, model, func_name)?;
        }
        Expr::Call(call) => {
            for arg in &call.args {
                check_set_new_in_expr(arg, model, func_name)?;
            }
        }
        Expr::If {
            condition,
            then_expr,
            else_expr,
        } => {
            check_set_new_in_expr(condition, model, func_name)?;
            check_set_new_in_expr(then_expr, model, func_name)?;
            check_set_new_in_expr(else_expr, model, func_name)?;
        }
        Expr::MatchExpr {
            target,
            arms,
            default,
        } => {
            check_set_new_in_expr(target, model, func_name)?;
            for (_, rhs) in arms {
                check_set_new_in_expr(rhs, model, func_name)?;
            }
            check_set_new_in_expr(default, model, func_name)?;
        }
        Expr::IsType { expr, .. } => {
            check_set_new_in_expr(expr, model, func_name)?;
        }
        Expr::Lambda { body, .. } => match body {
            LambdaBody::Expr(e) => check_set_new_in_expr(e, model, func_name)?,
            LambdaBody::Block(stmts) => check_set_new_in_stmts(stmts, model, func_name)?,
        },
        Expr::ListExpr(items) => {
            for item in items {
                check_set_new_in_expr(item, model, func_name)?;
            }
        }
        Expr::Literal(_) | Expr::Var(_) => {}
    }
    Ok(())
}

/// Walk a block and return a compile error if any `let` binding uses an import alias name.
fn check_no_alias_bindings(
    block: &[Stmt],
    alias_names: &std::collections::HashSet<String>,
    func_name: &str,
) -> Result<(), CompileError> {
    for stmt in block {
        match stmt {
            Stmt::Let { name, .. } if alias_names.contains(name) => {
                return Err(CompileError(format!(
                    "in `{func_name}`: variable `{name}` shadows an import alias; \
                     choose a different name"
                )));
            }
            Stmt::Let { .. } => {}
            Stmt::If {
                then_body,
                else_body,
                ..
            } => {
                check_no_alias_bindings(then_body, alias_names, func_name)?;
                if let Some(els) = else_body {
                    check_no_alias_bindings(els, alias_names, func_name)?;
                }
            }
            Stmt::ForIn { body, .. }
            | Stmt::ForInDestructure { body, .. }
            | Stmt::ForRange { body, .. } => {
                check_no_alias_bindings(body, alias_names, func_name)?;
            }
            _ => {}
        }
    }
    Ok(())
}

/// Compile a single helper block.  Duplicate function names within the same
/// block are still treated as an error (use [`merge_helper_blocks_with_source_with_offsets`]
/// when combining multiple blocks from different files).
pub fn compile_helper_block(helper_content: &str) -> Result<CompiledProgram, CompileError> {
    let mut parser = HelperParser::new(helper_content)?;
    let program = parser.parse_program()?;
    type_checker::TypeChecker::new(&program.functions).check_all()?;
    Ok(program)
}

/// Merge one or more helper block source strings into a single
/// [`CompiledProgram`].
///
/// Each string is expected to be the raw content of a `helper { … }` block
/// (i.e. wrapped in `{…}`).  Blocks are processed in order; when the same
/// function name appears in more than one block the **later** definition wins,
/// allowing a consumer file to override utility functions supplied by a
/// dependency.
///
/// After merging, the combined function set is passed through the type
/// checker.
/// Merge helper blocks that carry source-file metadata and document offsets.
/// Used by `compile_from_domain_model` so that function byte offsets are
/// relative to the original source document (enabling LSP go-to-definition).
fn collect_doc_templates(
    model: &crate::ast::DomainModel,
) -> Result<HashMap<String, DocTemplateFunction>, CompileError> {
    let mut doc_templates = HashMap::new();
    for block in &model.body {
        let crate::ast::DomainBlock::Doc(crate::ast::DocTemplateBlock {
            langs,
            name,
            params,
            content,
            ..
        }) = block
        else {
            continue;
        };
        if doc_templates.contains_key(name) {
            return Err(CompileError(format!(
                "duplicate doc template definition `{name}`"
            )));
        }
        doc_templates.insert(
            name.clone(),
            DocTemplateFunction {
                langs: langs.clone(),
                name: name.clone(),
                params: params.clone(),
                content: content.clone(),
            },
        );
    }
    Ok(doc_templates)
}

fn collect_style_templates(
    model: &crate::ast::DomainModel,
) -> Result<HashMap<String, StyleTemplateFunction>, CompileError> {
    let mut style_templates = HashMap::new();
    for block in &model.body {
        let crate::ast::DomainBlock::Style(crate::ast::StyleTemplateBlock {
            langs,
            name,
            params,
            content,
            ..
        }) = block
        else {
            continue;
        };
        if style_templates.contains_key(name) {
            return Err(CompileError(format!(
                "duplicate style template definition `{name}`"
            )));
        }
        style_templates.insert(
            name.clone(),
            StyleTemplateFunction {
                langs: langs.clone(),
                name: name.clone(),
                params: params.clone(),
                content: content.clone(),
            },
        );
    }
    Ok(style_templates)
}

fn merge_helper_blocks_with_source_with_offsets(
    blocks: &[(usize, &str, Option<&str>, Option<&str>, Option<&str>)],
) -> Result<CompiledProgram, CompileError> {
    let mut functions = HashMap::new();
    let mut top_level_stmts = Vec::new();
    for (base_offset, content, source_file, source_path, package_name) in blocks {
        let mut parser = HelperParser::new_with_base(*base_offset, content)?;
        let mut block_fns: HashMap<String, Function> = HashMap::new();
        let mut block_top_level: Vec<Stmt> = Vec::new();
        parser.parse_functions_into(&mut block_fns, &mut block_top_level)?;
        // Tag each function with its source file/path if coming from a dependency.
        for func in block_fns.values_mut() {
            if func.source_file.is_none() {
                func.source_file = source_file.map(|s| s.to_string());
            }
            if func.source_path.is_none() {
                func.source_path = source_path.map(|s| s.to_string());
            }
        }
        // For manifest-backed packages, also register every function under its
        // qualified name ("PackageName.funcName") so multiple adapters can
        // coexist in the table and TLang.Call.invoke can reach them by name.
        if let Some(pkg) = package_name {
            for (bare_name, func) in &block_fns {
                let qualified = format!("{pkg}.{bare_name}");
                functions.insert(qualified, func.clone());
            }
        }
        // Last-wins: later blocks (consumer) override earlier (dependency).
        functions.extend(block_fns);
        // Collect top-level statements from all blocks
        top_level_stmts.extend(block_top_level);
    }
    Ok(CompiledProgram {
        functions,
        top_level_stmts,
        model: ModelBlockTree::default(),
        templates: HashMap::new(),
        data_templates: HashMap::new(),
        cmd_templates: HashMap::new(),
        raw_templates: HashMap::new(),
        doc_templates: HashMap::new(),
        style_templates: HashMap::new(),
        imports: ImportResolver::default(),
        exposes: Vec::new(),
        test_names: Vec::new(),
    })
}

/// Options that control how a compiled TLang program is executed.
///
/// Pass this to [`run_main_with_options`] for full control, or use the
/// convenience wrappers [`run_main`] and [`run_main_with_args`] which apply
/// sensible defaults.
#[derive(Debug, Clone, Default)]
pub struct RunOptions {
    /// Arguments forwarded to the program's `main(args: String[])` function.
    pub args: Vec<Value>,
    /// When `true`, treat `>>?` (write-once / scaffold) output paths as `>>`
    /// (always-overwrite).  Use this after a template change to regenerate
    /// scaffold files that already exist on disk.
    pub force_regen: bool,
}

/// Run the compiled program's `main` function with default options.
pub fn run_main(program: &CompiledProgram) -> Result<RunResult, RuntimeError> {
    run_main_with_options(program, RunOptions::default())
}

/// Run the compiled program's `main` function, forwarding `args` to it.
///
/// Equivalent to `run_main_with_options(program, RunOptions { args, ..Default::default() })`.
pub fn run_main_with_args(
    program: &CompiledProgram,
    raw_args: Vec<String>,
) -> Result<RunResult, RuntimeError> {
    let args = vec![Value::List(
        raw_args.into_iter().map(Value::String).collect(),
    )];
    run_main_with_options(
        program,
        RunOptions {
            args,
            force_regen: false,
        },
    )
}

/// Run the compiled program's `main` function with full [`RunOptions`] control.
pub fn run_main_with_options(
    program: &CompiledProgram,
    options: RunOptions,
) -> Result<RunResult, RuntimeError> {
    let mut runtime = Runtime::new_with_options(program, &options);
    // Evaluate top-level statements (e.g., let bindings) before running main
    runtime.eval_top_level_stmts()?;
    // If `main` declares a single `String[]` / `List` parameter, supply an
    // empty list so callers that just do `run_main` don't need to know about it.
    let main_args = if options.args.is_empty() {
        if let Some(f) = program.functions.get("main") {
            if f.params.len() == 1 {
                vec![Value::List(Vec::new())]
            } else {
                Vec::new()
            }
        } else {
            Vec::new()
        }
    } else {
        options.args
    };
    let return_value = runtime.call_user_function("main", main_args)?;
    Ok(RunResult {
        output: runtime.output,
        return_value,
        model_tree: runtime.model,
    })
}

/// The result of running a single `test` block.
#[derive(Debug)]
pub struct TestBlockResult {
    pub name: String,
    pub passed: bool,
    pub failures: Vec<String>,
}

/// Run all `test` blocks in a compiled program.
///
/// Each test block runs to completion regardless of how many assertions fail.
/// All failures are collected and returned together so the caller can report
/// the full picture without stopping at the first failure.
pub fn run_tests(program: &CompiledProgram) -> Vec<TestBlockResult> {
    let mut results = Vec::new();
    for name in &program.test_names {
        let fn_name = format!("__test__{name}");
        libraries::assert::begin_test();
        let mut runtime = Runtime::new_with_options(program, &RunOptions::default());
        let _ = runtime.eval_top_level_stmts();
        let call_result = runtime.call_user_function(&fn_name, vec![]);
        let failures = libraries::assert::end_test();
        let mut all_failures = failures;
        if let Err(RuntimeError(msg)) = call_result {
            all_failures.push(format!("runtime error: {msg}"));
        }
        let passed = all_failures.is_empty();
        results.push(TestBlockResult {
            name: name.clone(),
            passed,
            failures: all_failures,
        });
    }
    results
}

/// Run a named function that has been declared with `expose` in the source.
///
/// This is the runtime backing for `tlang exec <funcName> [args…]`.
///
/// # Errors
///
/// Returns an error if:
/// - `function` is not in the program's `exposes` list.
/// - The function is not found in the compiled program.
/// - The number of CLI args does not match the function's declared parameter count.
/// - A runtime error occurs during execution.
pub fn run_exposed_function(
    program: &CompiledProgram,
    function: &str,
    raw_args: Vec<String>,
    force_regen: bool,
) -> Result<RunResult, RuntimeError> {
    // Guard: the function must be explicitly exposed.
    if !program.exposes.is_empty() && !program.exposes.contains(&function.to_string()) {
        let exposed = if program.exposes.is_empty() {
            "(none)".to_string()
        } else {
            program.exposes.join(", ")
        };
        return Err(RuntimeError(format!(
            "function `{function}` is not exposed — only exposed functions can be invoked \
             via `tlang exec`.\n\
             Exposed functions: {exposed}\n\
             Add `expose {function}` to the top of your .tlang file to make it callable."
        )));
    }

    // Look up the function to determine its arity before building args.
    let func = program.functions.get(function).ok_or_else(|| {
        RuntimeError(format!(
            "function `{function}` not found in the compiled program"
        ))
    })?;

    // Build the argument list.  If the function has exactly one parameter and
    // its type annotation is List (String[]), wrap all raw args into a single
    // list — mirroring how `main(args: String[])` is handled.  Otherwise pass
    // each raw arg as a separate Value::String positional argument.
    let args: Vec<Value> =
        if func.params.len() == 1 && matches!(func.params[0].1, Some(TypeAnnotation::List)) {
            vec![Value::List(
                raw_args.into_iter().map(Value::String).collect(),
            )]
        } else {
            raw_args.into_iter().map(Value::String).collect()
        };

    let options = RunOptions {
        args: args.clone(),
        force_regen,
    };
    let mut runtime = Runtime::new_with_options(program, &options);
    let return_value = runtime.call_user_function(function, args)?;
    Ok(RunResult {
        output: runtime.output,
        return_value,
        model_tree: runtime.model,
    })
}

/// Run `main` with a list of string arguments.
///
/// Each argument is passed as a `Value::String`.  This mirrors the common
/// pattern where `main` accepts a `List` of CLI arguments:
///
/// ```tlang

/// Compile a standalone helper-block source string and invoke a named
/// function with the given arguments in a fresh runtime.  Built-in library
/// functions (TLang.Leaf.get, TLang.StringBuilder.*,
/// etc.) are fully available.  Model access returns an empty model.
#[allow(dead_code)]
pub(crate) fn call_in_helper(
    helper_content: &str,
    function_name: &str,
    args: Vec<Value>,
) -> Result<Value, RuntimeError> {
    // compile_helper_block expects the content to be wrapped in `{ … }` braces.
    let wrapped = format!("{{\n{helper_content}\n}}");
    let program = compile_helper_block(&wrapped).map_err(|e| RuntimeError(e.0))?;
    let mut runtime = Runtime::new(&program);
    runtime.call_user_function(function_name, args)
}

/// Load a proper TLang source file (with `expose`/`use` header and `helper { }`
/// blocks), compile it, and call `function_name` with `args`.
///
/// Unlike [`call_in_helper`] — which wraps raw function definitions in `{ }` —
/// this function uses the full file loader so that `expose`, `use`, and
/// `helper { }` directives are all handled correctly.
pub fn call_in_file(
    file_path: &std::path::Path,
    function_name: &str,
    args: Vec<Value>,
) -> Result<Value, RuntimeError> {
    use crate::loader::load_program_with_manifest;
    let project_root = file_path.parent().unwrap_or(std::path::Path::new("."));
    let manifest = crate::manifest::try_load_manifest(project_root)
        .map_err(|e| RuntimeError(format!("manifest load error: {e}")))?;
    let model = load_program_with_manifest(file_path, manifest.as_ref())
        .map_err(|e| RuntimeError(format!("load error: {e}")))?;
    let program = compile_from_domain_model(&model).map_err(|e| RuntimeError(e.0))?;
    let mut runtime = Runtime::new(&program);
    runtime.call_user_function(function_name, args)
}

impl CompiledProgram {
    pub fn model_tree(&self) -> &ModelBlockTree {
        &self.model
    }

    /// Serialize this compiled program to a binary bytecode blob.
    pub(crate) fn encode(&self) -> Vec<u8> {
        let mut w = BcWriter::new();

        // Imports
        let aliases = self.imports.aliases();
        let mut aliases_vec: Vec<(&String, &String)> = aliases.iter().collect();
        aliases_vec.sort_by_key(|(k, _)| k.as_str());
        w.write_u16_be(aliases_vec.len() as u16);
        for (alias, path) in &aliases_vec {
            w.write_str_ref(alias);
            w.write_str_ref(path);
        }

        // Exposes
        w.write_u16_be(self.exposes.len() as u16);
        for name in &self.exposes {
            w.write_str_ref(name);
        }

        // Top-level statements
        w.write_u16_be(self.top_level_stmts.len() as u16);
        for stmt in &self.top_level_stmts {
            w.encode_stmt(stmt);
        }

        // Functions
        let mut funcs: Vec<&Function> = self.functions.values().collect();
        funcs.sort_by_key(|f| f.name.as_str());
        w.write_u16_be(funcs.len() as u16);
        for func in &funcs {
            w.encode_function(func);
        }

        // Templates
        let mut tmpls: Vec<&TemplateFunction> = self.templates.values().collect();
        tmpls.sort_by_key(|t| t.name.as_str());
        w.write_u16_be(tmpls.len() as u16);
        for tmpl in &tmpls {
            w.encode_template(tmpl);
        }

        // Data templates
        let mut data_tmpls: Vec<&DataTemplateFunction> = self.data_templates.values().collect();
        data_tmpls.sort_by_key(|t| t.name.as_str());
        w.write_u16_be(data_tmpls.len() as u16);
        for dt in &data_tmpls {
            w.encode_data_template(dt);
        }

        // Cmd templates
        let mut cmd_tmpls: Vec<&CmdTemplateFunction> = self.cmd_templates.values().collect();
        cmd_tmpls.sort_by_key(|t| t.name.as_str());
        w.write_u16_be(cmd_tmpls.len() as u16);
        for ct in &cmd_tmpls {
            w.encode_cmd_template(ct);
        }

        // Raw templates
        let mut raw_tmpls: Vec<&RawTemplateFunction> = self.raw_templates.values().collect();
        raw_tmpls.sort_by_key(|t| t.name.as_str());
        w.write_u16_be(raw_tmpls.len() as u16);
        for rt in &raw_tmpls {
            w.encode_raw_template(rt);
        }

        // Doc templates
        let mut doc_tmpls: Vec<&DocTemplateFunction> = self.doc_templates.values().collect();
        doc_tmpls.sort_by_key(|t| t.name.as_str());
        w.write_u16_be(doc_tmpls.len() as u16);
        for dt in &doc_tmpls {
            w.encode_doc_template(dt);
        }

        // Style templates
        let mut style_tmpls: Vec<&StyleTemplateFunction> = self.style_templates.values().collect();
        style_tmpls.sort_by_key(|t| t.name.as_str());
        w.write_u16_be(style_tmpls.len() as u16);
        for st in &style_tmpls {
            w.encode_style_template(st);
        }

        // Model
        w.encode_model(&self.model);

        w.finalize()
    }

    /// Deserialize a compiled program from a binary bytecode blob.
    pub(crate) fn decode(data: &[u8]) -> Result<Self, String> {
        let mut r = BcReader::new(data)?;

        // Imports
        let imports_count = r.read_u16_be()? as usize;
        let mut aliases = HashMap::new();
        for _ in 0..imports_count {
            let alias = r.read_str()?;
            let path = r.read_str()?;
            aliases.insert(alias, path);
        }

        // Exposes
        let exposes_count = r.read_u16_be()? as usize;
        let mut exposes = Vec::with_capacity(exposes_count);
        for _ in 0..exposes_count {
            exposes.push(r.read_str()?);
        }

        // Top-level statements
        let top_level_count = r.read_u16_be()? as usize;
        let mut top_level_stmts = Vec::with_capacity(top_level_count);
        for _ in 0..top_level_count {
            top_level_stmts.push(r.decode_stmt()?);
        }

        // Functions
        let funcs_count = r.read_u16_be()? as usize;
        let mut functions = HashMap::new();
        for _ in 0..funcs_count {
            let func = r.decode_function()?;
            functions.insert(func.name.clone(), func);
        }

        // Templates
        let tmpls_count = r.read_u16_be()? as usize;
        let mut templates = HashMap::new();
        for _ in 0..tmpls_count {
            let tmpl = r.decode_template()?;
            templates.insert(tmpl.name.clone(), tmpl);
        }

        // Data templates
        let data_tmpls_count = r.read_u16_be()? as usize;
        let mut data_templates = HashMap::new();
        for _ in 0..data_tmpls_count {
            let dt = r.decode_data_template()?;
            data_templates.insert(dt.name.clone(), dt);
        }

        // Cmd templates
        let cmd_tmpls_count = r.read_u16_be()? as usize;
        let mut cmd_templates = HashMap::new();
        for _ in 0..cmd_tmpls_count {
            let ct = r.decode_cmd_template()?;
            cmd_templates.insert(ct.name.clone(), ct);
        }

        // Raw templates
        let raw_tmpls_count = r.read_u16_be()? as usize;
        let mut raw_templates = HashMap::new();
        for _ in 0..raw_tmpls_count {
            let rt = r.decode_raw_template()?;
            raw_templates.insert(rt.name.clone(), rt);
        }

        // Doc templates
        let doc_tmpls_count = r.read_u16_be()? as usize;
        let mut doc_templates = HashMap::new();
        for _ in 0..doc_tmpls_count {
            let dt = r.decode_doc_template()?;
            doc_templates.insert(dt.name.clone(), dt);
        }

        // Style templates
        let style_tmpls_count = r.read_u16_be()? as usize;
        let mut style_templates = HashMap::new();
        for _ in 0..style_tmpls_count {
            let st = r.decode_style_template()?;
            style_templates.insert(st.name.clone(), st);
        }

        // Model
        let model = r.decode_model()?;

        Ok(CompiledProgram {
            functions,
            top_level_stmts,
            model,
            templates,
            data_templates,
            cmd_templates,
            raw_templates,
            doc_templates,
            style_templates,
            imports: libraries::ImportResolver::from_aliases(aliases),
            exposes,
            test_names: Vec::new(),
        })
    }
}

mod encoding;
use encoding::{BcWriter, BcReader};

struct Runtime<'a> {
    program: &'a CompiledProgram,
    output: String,
    model: ModelBlockTree,
    template_calls: HashMap<String, usize>,
    /// Pre-rendered inner-body fragments set by `generateAll()` before it
    /// instantiates a `lead` template.  Inside the lead template, a
    /// `<[ attrs() ]>` include expands to this string.  Cleared after the
    /// lead template finishes instantiating.
    set_attrs_context: Option<String>,
    /// When `true`, `>>?` (write-once / scaffold) output paths are treated as
    /// `>>` (always-overwrite).  Set via [`RunOptions::force_regen`].
    force_regen: bool,
    /// Global frame for top-level let bindings, accessible to all functions.
    global_frame: Frame,
}

impl<'a> Runtime<'a> {
    fn new(program: &'a CompiledProgram) -> Self {
        Self {
            program,
            output: String::new(),
            model: program.model.clone(),
            template_calls: HashMap::new(),
            set_attrs_context: None,
            force_regen: false,
            global_frame: Frame {
                bindings: HashMap::new(),
            },
        }
    }

    fn new_with_options(program: &'a CompiledProgram, options: &RunOptions) -> Self {
        Self {
            force_regen: options.force_regen,
            ..Self::new(program)
        }
    }

    fn call_user_function(&mut self, name: &str, args: Vec<Value>) -> Result<Value, RuntimeError> {
        // Primary lookup: try the fully-qualified name as-is.
        // Fallback: if the name contains dots (e.g. "TLangUI.layout.Layout.attr"),
        // try just the last segment ("attr") so that cross-package helper calls
        // work even though functions are stored under their simple names in the
        // flat merged function table.
        let resolved_name: String = if !self.program.functions.contains_key(name) {
            if let Some(simple) = name.rsplit('.').next() {
                if self.program.functions.contains_key(simple) {
                    simple.to_string()
                } else {
                    name.to_string()
                }
            } else {
                name.to_string()
            }
        } else {
            name.to_string()
        };
        let lookup_name = resolved_name.as_str();
        let function = self
            .program
            .functions
            .get(lookup_name)
            .ok_or_else(|| {
                if name == "main" {
                    RuntimeError(
                        "no `main` function found — add `func main(): String { … }` to your helper block"
                            .to_string(),
                    )
                } else {
                    RuntimeError(format!("function `{name}` not found"))
                }
            })?
            .clone();

        if function.params.len() != args.len() {
            return Err(RuntimeError(format!(
                "function `{name}` expects {} args, got {}",
                function.params.len(),
                args.len()
            )));
        }

        let mut bindings = HashMap::new();
        for ((param, _type_ann), value) in function.params.iter().zip(args) {
            bindings.insert(param.clone(), value);
        }
        let mut frame = Frame { bindings };

        self.execute_block(&function.body, &mut frame)
    }

    /// Call a lambda (closure) with the given arguments.
    pub(crate) fn call_lambda(
        &mut self,
        lambda: &LambdaObject,
        args: Vec<Value>,
    ) -> Result<Value, RuntimeError> {
        if lambda.params.len() != args.len() {
            return Err(RuntimeError(format!(
                "lambda expects {} args, got {}",
                lambda.params.len(),
                args.len()
            )));
        }
        // Start from captured scope, then bind params on top.
        let mut bindings = lambda.captured.clone();
        for (param, value) in lambda.params.iter().zip(args) {
            bindings.insert(param.clone(), value);
        }
        let mut frame = Frame { bindings };
        match &lambda.body {
            LambdaBody::Expr(expr) => {
                let body = expr.as_ref().clone();
                self.eval_expr(&body, &mut frame)
            }
            LambdaBody::Block(stmts) => {
                let stmts = stmts.clone();
                self.execute_block(&stmts, &mut frame)
            }
        }
    }

    /// Evaluate top-level statements (e.g., let bindings) and populate the global frame.
    fn eval_top_level_stmts(&mut self) -> Result<(), RuntimeError> {
        // Use a temporary frame for evaluation, then merge into global_frame
        let mut temp_frame = Frame {
            bindings: HashMap::new(),
        };
        for stmt in &self.program.top_level_stmts {
            match stmt {
                Stmt::Let { name, expr } => {
                    let value = self.eval_expr(expr, &mut temp_frame)?;
                    temp_frame.bindings.insert(name.clone(), value);
                }
                Stmt::Call(call) => {
                    let _ = self.eval_call(call, &mut temp_frame)?;
                }
                // For other statement types, we only support let and call at the top level
                _ => {
                    return Err(RuntimeError(format!(
                        "top-level statement type not supported: {:?}",
                        stmt
                    )));
                }
            }
        }
        // Merge the temporary frame into the global frame
        self.global_frame.bindings.extend(temp_frame.bindings);
        Ok(())
    }

    fn execute_block(&mut self, block: &[Stmt], frame: &mut Frame) -> Result<Value, RuntimeError> {
        for stmt in block {
            match stmt {
                Stmt::Let { name, expr } => {
                    let value = self.eval_expr(expr, frame)?;
                    frame.bindings.insert(name.clone(), value);
                }
                Stmt::Call(call) => {
                    let _ = self.eval_call(call, frame)?;
                }
                Stmt::If {
                    condition,
                    then_body,
                    else_body,
                } => {
                    let cond = self.eval_expr(condition, frame)?;
                    let branch = match cond {
                        Value::Bool(true) => Some(then_body.as_slice()),
                        Value::Bool(false) => else_body.as_deref(),
                        _ => return Err(RuntimeError("if condition must be bool".to_string())),
                    };
                    if let Some(branch_body) = branch {
                        let nested = self.execute_block(branch_body, frame)?;
                        if !matches!(nested, Value::Unit) {
                            return Ok(nested);
                        }
                    }
                }
                Stmt::ForIn {
                    var,
                    iterable,
                    body,
                } => {
                    let collection = self.eval_expr(iterable, frame)?;
                    let items = match collection {
                        Value::List(items) => items,
                        other => {
                            return Err(RuntimeError(format!(
                                "for-in requires a list, got {}",
                                value_type_name(&other)
                            )));
                        }
                    };
                    for item in items {
                        frame.bindings.insert(var.clone(), item);
                        let result = self.execute_block(body, frame)?;
                        if !matches!(result, Value::Unit) {
                            return Ok(result);
                        }
                    }
                }
                Stmt::ForInDestructure {
                    vars,
                    iterable,
                    body,
                } => {
                    let collection = self.eval_expr(iterable, frame)?;
                    let items = match collection {
                        Value::List(items) => items,
                        other => {
                            return Err(RuntimeError(format!(
                                "for-in destructure requires a list, got {}",
                                value_type_name(&other)
                            )));
                        }
                    };
                    for item in items {
                        let sub = match item {
                            Value::List(sub) => sub,
                            other => vec![other],
                        };
                        for (i, var) in vars.iter().enumerate() {
                            let val = sub.get(i).cloned().unwrap_or(Value::Unit);
                            frame.bindings.insert(var.clone(), val);
                        }
                        let result = self.execute_block(body, frame)?;
                        if !matches!(result, Value::Unit) {
                            return Ok(result);
                        }
                    }
                }
                Stmt::ForRange {
                    var,
                    start,
                    end,
                    inclusive,
                    body,
                } => {
                    let start_val = self.eval_expr(start, frame)?;
                    let end_val = self.eval_expr(end, frame)?;
                    let (start_int, end_int) = match (start_val, end_val) {
                        (Value::Int(s), Value::Int(e)) => (s, e),
                        _ => {
                            return Err(RuntimeError(
                                "for range requires integer bounds".to_string(),
                            ));
                        }
                    };
                    let end_bound = if *inclusive { end_int + 1 } else { end_int };
                    for i in start_int..end_bound {
                        frame.bindings.insert(var.clone(), Value::Int(i));
                        let result = self.execute_block(body, frame)?;
                        if !matches!(result, Value::Unit) {
                            return Ok(result);
                        }
                    }
                }
                Stmt::Match {
                    target,
                    arms,
                    default,
                } => {
                    let target_value = self.eval_expr(target, frame)?;
                    let mut matched = false;
                    for arm in arms {
                        if self.match_arm_matches(&target_value, arm, frame)? {
                            matched = true;
                            let nested = self.execute_block(&arm.body, frame)?;
                            if !matches!(nested, Value::Unit) {
                                return Ok(nested);
                            }
                            break;
                        }
                    }
                    if !matched && let Some(default_body) = default {
                        let nested = self.execute_block(default_body, frame)?;
                        if !matches!(nested, Value::Unit) {
                            return Ok(nested);
                        }
                    }
                }
                Stmt::Return(expr) => {
                    return self.eval_expr(expr, frame);
                }
            }
        }
        Ok(Value::Unit)
    }

    fn eval_expr(&mut self, expr: &Expr, frame: &mut Frame) -> Result<Value, RuntimeError> {
        if let Expr::ListExpr(items) = expr {
            let mut vals = Vec::with_capacity(items.len());
            for item in items {
                vals.push(self.eval_expr(item, frame)?);
            }
            return Ok(Value::List(vals));
        }
        match expr {
            Expr::Literal(v) => Ok(v.clone()),
            Expr::Var(name) => self.eval_var_path(name, frame),
            Expr::Op(op, left, right) => {
                if matches!(op, Op::NullCoalesce) {
                    let left = self.eval_expr(left, frame)?;
                    if !matches!(left, Value::Unit) {
                        return Ok(left);
                    }
                    let right = self.eval_expr(right, frame)?;
                    return Ok(right);
                }
                let left = self.eval_expr(left, frame)?;
                let right = self.eval_expr(right, frame)?;
                self.eval_op(*op, left, right)
            }
            Expr::Call(call) => self.eval_call(call, frame),
            Expr::If {
                condition,
                then_expr,
                else_expr,
            } => {
                let condition = self.eval_expr(condition, frame)?;
                match condition {
                    Value::Bool(true) => self.eval_expr(then_expr, frame),
                    Value::Bool(false) => self.eval_expr(else_expr, frame),
                    _ => Err(RuntimeError("if condition must be bool".to_string())),
                }
            }
            Expr::MatchExpr {
                target,
                arms,
                default,
            } => {
                let target_value = self.eval_expr(target, frame)?;
                for (pattern, rhs) in arms {
                    if self.match_pattern_matches(&target_value, pattern, frame)? {
                        return self.eval_expr(rhs, frame);
                    }
                }
                self.eval_expr(default, frame)
            }
            Expr::SetNew(sn) => self.eval_set_new(sn, frame),
            Expr::IsType {
                expr,
                type_name,
                negated,
            } => {
                let value = self.eval_expr(expr, frame)?;
                let is_match = value_matches_type_name(&value, type_name);
                Ok(Value::Bool(if *negated { !is_match } else { is_match }))
            }
            Expr::Lambda { params, body } => {
                // Capture the current frame to build a closure.
                let captured = frame.bindings.clone();
                Ok(Value::Lambda(Rc::new(LambdaObject {
                    params: params.clone(),
                    body: body.clone(),
                    captured,
                })))
            }
            // Handled by the early `if let` above the match — unreachable here.
            Expr::ListExpr(_) => unreachable!("ListExpr handled before match"),
        }
    }

    fn match_arm_matches(
        &mut self,
        target_value: &Value,
        arm: &MatchArm,
        frame: &mut Frame,
    ) -> Result<bool, RuntimeError> {
        self.match_pattern_matches(target_value, &arm.pattern, frame)
    }

    fn match_pattern_matches(
        &mut self,
        target_value: &Value,
        pattern: &MatchPattern,
        frame: &mut Frame,
    ) -> Result<bool, RuntimeError> {
        match pattern {
            MatchPattern::Value(expr) => {
                let case_value = self.eval_expr(expr, frame)?;
                Ok(case_value == *target_value)
            }
            MatchPattern::IsType { type_name, negated } => {
                let is_match = value_matches_type_name(target_value, type_name);
                Ok(if *negated { !is_match } else { is_match })
            }
            MatchPattern::Condition(expr) => {
                let prev = frame.bindings.insert("_".to_string(), target_value.clone());
                let cond_value = self.eval_expr(expr, frame);
                match prev {
                    Some(old) => {
                        frame.bindings.insert("_".to_string(), old);
                    }
                    None => {
                        frame.bindings.remove("_");
                    }
                }
                match cond_value? {
                    Value::Bool(b) => Ok(b),
                    _ => Err(RuntimeError(
                        "match case condition must be bool".to_string(),
                    )),
                }
            }
        }
    }

    /// Evaluate a set-entity instantiation expression.
    ///
    /// Validates that all mandatory constructor params declared in the `(…)`
    /// section of the `set` declaration are provided, then builds and returns a
    /// [`Value::SetInstance`].
    fn eval_set_new(&mut self, sn: &SetNewExpr, frame: &mut Frame) -> Result<Value, RuntimeError> {
        // Look up the entity in the static model.
        let _entity = self
            .find_set_entity(&sn.entity)
            .ok_or_else(|| {
                RuntimeError(format!(
                    "set entity `{}` not found in model block",
                    sn.entity
                ))
            })?
            .clone();

        // Collect all params including those inherited via `ext`.
        let all_params = collect_effective_params(&self.program.model, &sn.entity);

        // Validate that every mandatory constructor param has been supplied.
        for param in &all_params {
            if let Some(param_name) = &param.attr {
                let provided = sn.fields.iter().any(|(k, _)| k == param_name);
                if !provided {
                    return Err(RuntimeError(format!(
                        "set entity `{}` requires `{}: …` at instantiation",
                        sn.entity, param_name
                    )));
                }
            }
        }

        // Reject any impl fields that are not declared as constructor params
        // in this entity or any of its ancestors.
        for (key, _) in &sn.fields {
            let declared = all_params
                .iter()
                .any(|p| p.attr.as_deref() == Some(key.as_str()));
            if !declared {
                return Err(RuntimeError(format!(
                    "`{}` is not a constructor param of set entity `{}` or any of its ancestors; \
                     only params declared in the `(…)` section may be provided at instantiation",
                    key, sn.entity
                )));
            }
        }

        // Evaluate each impl field value.
        let mut impls = std::collections::BTreeMap::new();
        for (key, expr) in &sn.fields {
            let val = self.eval_expr(expr, frame)?;
            impls.insert(key.clone(), val);
        }

        Ok(Value::SetInstance(SetInstanceObject {
            entity_name: resolve_entity_name(&sn.entity).to_string(),
            impls,
        }))
    }

    /// Read a non-callable attribute from a [`SetInstanceObject`].
    ///
    /// Lookup order:
    /// 1. Impl values supplied at instantiation (constructor params).
    /// 2. Body attributes on the entity — primitive and string-literal values
    ///    are returned directly; `Ref` and `Impl` attributes are not readable
    ///    as plain values (use `instance.attr()` for `Ref` attrs).
    fn eval_set_instance_attr(
        &self,
        inst: &SetInstanceObject,
        attr_name: &str,
    ) -> Result<Value, RuntimeError> {
        // 1. Constructor param value.
        if let Some(v) = inst.impls.get(attr_name) {
            return Ok(v.clone());
        }

        // 2. Body attribute from the static entity definition, walking the
        //    `ext` chain so inherited attrs are visible and child attrs
        //    (surcharges) take precedence over parent attrs with the same name.
        let effective_attrs = collect_effective_attrs(&self.program.model, &inst.entity_name);

        for attr in &effective_attrs {
            if attr.attr.as_deref() != Some(attr_name) {
                continue;
            }
            return match &attr.value {
                crate::model_tree::ModelValueTypeTree::Ref { .. } => Err(RuntimeError(format!(
                    "`{attr_name}` on `{}` is a ref function — call it as `instance.{attr_name}()`",
                    inst.entity_name
                ))),
                crate::model_tree::ModelValueTypeTree::Impl { .. }
                | crate::model_tree::ModelValueTypeTree::ImplArray => Err(RuntimeError(format!(
                    "`{attr_name}` on `{}` is an `impl` placeholder with no value",
                    inst.entity_name
                ))),
                crate::model_tree::ModelValueTypeTree::Generic { name, params } => {
                    Err(RuntimeError(format!(
                        "`{attr_name}` on `{}` is a generic type annotation `{}<{}>` with no runtime value — use a constructor parameter instead",
                        inst.entity_name,
                        name,
                        params.join(", ")
                    )))
                }
                other => model_value_type_to_value(other),
            };
        }

        Err(RuntimeError(format!(
            "attribute `{attr_name}` not found on set entity `{}`",
            inst.entity_name
        )))
    }

    fn eval_member_access(&self, receiver: Value, name: &str) -> Result<Value, RuntimeError> {
        match receiver {
            Value::SetInstance(inst) => self.eval_set_instance_attr(&inst, name),
            Value::BoundAttr(bound) => {
                if name == "name" {
                    Ok(Value::String(bound.attr_name))
                } else {
                    Err(RuntimeError(format!(
                        "bound attr `{}` has no property `{name}`; \
                         use `op.name` to get the attribute name or `op()` to call it",
                        bound.attr_name
                    )))
                }
            }
            Value::Map(map) => map
                .get(name)
                .cloned()
                .ok_or_else(|| RuntimeError(format!("map has no key `{name}`"))),
            Value::Leaf(leaf) => leaf
                .get(name)
                .cloned()
                .ok_or_else(|| RuntimeError(format!("leaf has no key `{name}`"))),
            other => Err(RuntimeError(format!(
                "cannot access `{name}` on value of type `{}`",
                value_type_name(&other)
            ))),
        }
    }

    fn eval_chain_from_value(
        &self,
        mut current: Value,
        segments: &[ChainSegment],
    ) -> Result<Value, RuntimeError> {
        for seg in segments {
            if matches!(current, Value::Unit) {
                if seg.optional {
                    return Ok(Value::Unit);
                }
                return Err(RuntimeError(format!(
                    "cannot access `{}` on null value",
                    seg.name
                )));
            }
            current = self.eval_member_access(current, &seg.name)?;
        }
        Ok(current)
    }

    fn eval_var_path(&self, path: &str, frame: &Frame) -> Result<Value, RuntimeError> {
        let (base, segments) = parse_chain_path(path);

        if segments.is_empty() {
            // First check the local frame, then the global frame
            if let Some(value) = frame.bindings.get(path).cloned() {
                return Ok(value);
            }
            return self
                .global_frame
                .bindings
                .get(path)
                .cloned()
                .ok_or_else(|| RuntimeError(format!("variable `{path}` not found")));
        }

        // First check the local frame, then the global frame
        if let Some(bound) = frame.bindings.get(&base).cloned() {
            return self.eval_chain_from_value(bound, &segments);
        }
        if let Some(bound) = self.global_frame.bindings.get(&base).cloned() {
            return self.eval_chain_from_value(bound, &segments);
        }

        let Some(first) = segments.first() else {
            return Err(RuntimeError(format!("variable `{path}` not found")));
        };
        let current = match resolve_model_attr_checked(&self.model, &base, &first.name) {
            Ok(Some(v)) => v,
            Ok(None) => {
                return Err(RuntimeError(format!("variable `{path}` not found")));
            }
            Err(e) => return Err(e),
        };

        if matches!(current, Value::Unit) && first.optional {
            return Ok(Value::Unit);
        }

        self.eval_chain_from_value(current, &segments[1..])
    }

    fn eval_op(&self, op: Op, left: Value, right: Value) -> Result<Value, RuntimeError> {
        match op {
            // equality — works for any Value type
            Op::Eq => Ok(Value::Bool(left == right)),
            Op::Ne => Ok(Value::Bool(left != right)),
            // arithmetic
            Op::Add => match (left, right) {
                (Value::Int(a), Value::Int(b)) => Ok(Value::Int(a + b)),
                (Value::String(a), Value::String(b)) => Ok(Value::String(a + &b)),
                (Value::String(a), Value::Int(b)) => Ok(Value::String(a + &b.to_string())),
                _ => Err(RuntimeError(
                    "+ expects int or string arguments".to_string(),
                )),
            },
            Op::Sub => match (left, right) {
                (Value::Int(a), Value::Int(b)) => Ok(Value::Int(a - b)),
                _ => Err(RuntimeError("- expects int arguments".to_string())),
            },
            Op::Mul => match (left, right) {
                (Value::Int(a), Value::Int(b)) => Ok(Value::Int(a * b)),
                _ => Err(RuntimeError("* expects int arguments".to_string())),
            },
            Op::Div => match (left, right) {
                (Value::Int(_), Value::Int(0)) => Err(RuntimeError("division by zero".to_string())),
                (Value::Int(a), Value::Int(b)) => Ok(Value::Int(a / b)),
                _ => Err(RuntimeError("/ expects int arguments".to_string())),
            },
            Op::Mod => match (left, right) {
                (Value::Int(_), Value::Int(0)) => Err(RuntimeError("modulo by zero".to_string())),
                (Value::Int(a), Value::Int(b)) => Ok(Value::Int(a % b)),
                _ => Err(RuntimeError("% expects int arguments".to_string())),
            },
            // comparison
            Op::Lt => match (left, right) {
                (Value::Int(a), Value::Int(b)) => Ok(Value::Bool(a < b)),
                _ => Err(RuntimeError("< expects int arguments".to_string())),
            },
            Op::Gt => match (left, right) {
                (Value::Int(a), Value::Int(b)) => Ok(Value::Bool(a > b)),
                _ => Err(RuntimeError("> expects int arguments".to_string())),
            },
            Op::Lte => match (left, right) {
                (Value::Int(a), Value::Int(b)) => Ok(Value::Bool(a <= b)),
                _ => Err(RuntimeError("<= expects int arguments".to_string())),
            },
            Op::Gte => match (left, right) {
                (Value::Int(a), Value::Int(b)) => Ok(Value::Bool(a >= b)),
                _ => Err(RuntimeError(">= expects int arguments".to_string())),
            },
            // logical — short-circuit semantics already applied at eval time;
            // by the time we reach here both sides are already evaluated, so
            // we just combine the booleans.
            Op::And => match (left, right) {
                (Value::Bool(a), Value::Bool(b)) => Ok(Value::Bool(a && b)),
                _ => Err(RuntimeError("&& expects bool arguments".to_string())),
            },
            Op::Or => match (left, right) {
                (Value::Bool(a), Value::Bool(b)) => Ok(Value::Bool(a || b)),
                _ => Err(RuntimeError("|| expects bool arguments".to_string())),
            },
            Op::NullCoalesce => unreachable!("handled with short-circuit in eval_expr"),
        }
    }

    fn eval_call(&mut self, call: &CallExpr, frame: &mut Frame) -> Result<Value, RuntimeError> {
        let mut args = Vec::with_capacity(call.args.len());
        for arg in &call.args {
            args.push(self.eval_expr(arg, frame)?);
        }

        // BoundAttr direct call: `op()` where `op` is a local variable bound to
        // a BoundAttr value.  Must be checked before the dotted-path method
        // dispatch so that `op()` is handled here rather than falling through to
        // the function-name lookup (which would fail with a confusing error).
        if !call.target.contains('.')
            && let Some(Value::BoundAttr(bound)) = frame.bindings.get(&call.target).cloned()
        {
            return self.eval_set_instance_call(
                &bound.instance.clone(),
                &bound.attr_name.clone(),
                args,
            );
        }

        // Lambda direct call: `fn(args)` where `fn` is a local variable bound
        // to a Lambda value.  Dispatch to call_lambda before other checks.
        if !call.target.contains('.') {
            if let Some(val) = frame.bindings.get(&call.target).cloned() {
                match val {
                    Value::Lambda(lambda) => {
                        return self.call_lambda(&lambda, args);
                    }
                    // Function-reference: `&funcName` produces `Value::String`.
                    // When stored in a variable (e.g. `let f = &myFunc`) it can
                    // be called like a lambda: `f(x)`.
                    Value::String(ref func_name)
                        if self.program.functions.contains_key(func_name.as_str()) =>
                    {
                        let func_name = func_name.clone();
                        return self.call_user_function(&func_name, args);
                    }
                    _ => {}
                }
            }
        }

        // Method-call syntax on a local variable, including optional chaining:
        // `receiver.method(args)` and `receiver?.method(args)`.
        let (call_base, call_segments) = parse_chain_path(&call.target);
        if !call_segments.is_empty()
            && let Some(base_receiver) = frame.bindings.get(&call_base).cloned()
        {
            let (receiver_chain, method_seg) = call_segments.split_at(call_segments.len() - 1);
            let method_seg = &method_seg[0];
            let receiver_val = self.eval_chain_from_value(base_receiver, receiver_chain)?;
            if matches!(receiver_val, Value::Unit) {
                if method_seg.optional {
                    return Ok(Value::Unit);
                }
                return Err(RuntimeError(format!(
                    "cannot call `{}` on null value",
                    method_seg.name
                )));
            }

            let method = method_seg.name.as_str();
            match &receiver_val {
                // SetInstance — delegate to our specialised handler.
                Value::SetInstance(inst) => {
                    let inst = inst.clone();
                    return self.eval_set_instance_call(&inst, method, args);
                }
                // BoundAttr — property access only (`.name`).
                // Calling a BoundAttr as `op()` is handled below (no-dot path).
                Value::BoundAttr(bound) => {
                    let bound = bound.clone();
                    match method {
                        "name" if args.is_empty() => {
                            return Ok(Value::String(bound.attr_name.clone()));
                        }
                        _ => {
                            return Err(RuntimeError(format!(
                                "bound attr `{}` has no property `{method}`; \
                                     use `op.name` to get the attribute name or `op()` to call it",
                                bound.attr_name
                            )));
                        }
                    }
                }
                // List / Map / String / Float — route to the relevant
                // TLang built-in library.
                Value::List(_) => {
                    let lib_target = format!("TLang.List.{method}");
                    let mut lib_args = vec![receiver_val];
                    lib_args.extend(args.clone());
                    if let Some(v) = call_builtin(self, &lib_target, &lib_args)? {
                        return Ok(v);
                    }
                    return Err(RuntimeError(format!(
                        "`{method}` is not a known method of `TLang.List`"
                    )));
                }
                Value::Map(_) => {
                    let lib_target = format!("TLang.Map.{method}");
                    let mut lib_args = vec![receiver_val];
                    lib_args.extend(args.clone());
                    if let Some(v) = call_builtin(self, &lib_target, &lib_args)? {
                        return Ok(v);
                    }
                    return Err(RuntimeError(format!(
                        "`{method}` is not a known method of `TLang.Map`"
                    )));
                }
                Value::String(_) => {
                    let lib_target = format!("TLang.String.{method}");
                    let mut lib_args = vec![receiver_val];
                    lib_args.extend(args.clone());
                    if let Some(v) = call_builtin(self, &lib_target, &lib_args)? {
                        return Ok(v);
                    }
                    // Fall through — might be a user function with a dotted name.
                }
                Value::Float(_) => {
                    let lib_target = format!("TLang.Double.{method}");
                    let mut lib_args = vec![receiver_val];
                    lib_args.extend(args.clone());
                    if let Some(v) = call_builtin(self, &lib_target, &lib_args)? {
                        return Ok(v);
                    }
                    // Fall through.
                }
                Value::StringBuilder(buf) => {
                    let buf = buf.clone();
                    match method {
                        "append" => {
                            for arg in &args {
                                buf.borrow_mut().push_str(&value_to_string(arg));
                            }
                            return Ok(Value::Unit);
                        }
                        "build" | "toString" => {
                            return Ok(Value::String(buf.borrow().clone()));
                        }
                        _ => {
                            return Err(RuntimeError(format!(
                                "StringBuilder has no method `{method}`; \
                                 use `append(text)` or `build()`"
                            )));
                        }
                    }
                }
                _ => {}
            }
        }

        let target = self.program.imports.resolve_call_target(&call.target);

        if let Some(value) = call_builtin(self, &target, &args)? {
            return Ok(value);
        }

        if let Some(template) = self.program.templates.get(&target).cloned() {
            let leaf = self.instantiate_template(&template, args)?;
            return Ok(leaf);
        }

        if let Some(data_template) = self.program.data_templates.get(&target).cloned() {
            let leaf = self.instantiate_data_template(&data_template, args)?;
            return Ok(leaf);
        }

        if let Some(cmd_template) = self.program.cmd_templates.get(&target).cloned() {
            let leaf = self.instantiate_cmd_template(&cmd_template, args)?;
            return Ok(leaf);
        }

        if let Some(raw_template) = self.program.raw_templates.get(&target).cloned() {
            let result = self.instantiate_raw_template(&raw_template, args)?;
            return Ok(result);
        }

        if let Some(doc_template) = self.program.doc_templates.get(&target).cloned() {
            let (lang, real_args) = if !args.is_empty() {
                if let Value::String(ref s) = args[0] {
                    if doc_template.langs.iter().any(|l| l.eq_ignore_ascii_case(s)) {
                        (s.clone(), args[1..].to_vec())
                    } else {
                        (
                            doc_template.langs.first().cloned().unwrap_or_default(),
                            args.clone(),
                        )
                    }
                } else {
                    (
                        doc_template.langs.first().cloned().unwrap_or_default(),
                        args.clone(),
                    )
                }
            } else {
                (
                    doc_template.langs.first().cloned().unwrap_or_default(),
                    args.clone(),
                )
            };
            let result = self.instantiate_doc_template(&doc_template, &lang, real_args)?;
            return Ok(result);
        }

        if let Some(style_template) = self.program.style_templates.get(&target).cloned() {
            let (lang, real_args) = if !args.is_empty() {
                if let Value::String(ref s) = args[0] {
                    if style_template
                        .langs
                        .iter()
                        .any(|l| l.eq_ignore_ascii_case(s))
                    {
                        (s.clone(), args[1..].to_vec())
                    } else {
                        (
                            style_template.langs.first().cloned().unwrap_or_default(),
                            args.clone(),
                        )
                    }
                } else {
                    (
                        style_template.langs.first().cloned().unwrap_or_default(),
                        args.clone(),
                    )
                }
            } else {
                (
                    style_template.langs.first().cloned().unwrap_or_default(),
                    args.clone(),
                )
            };
            let result = self.instantiate_style_template(&style_template, &lang, real_args)?;
            return Ok(result);
        }

        // Positional set-entity instantiation: EntityName(arg1, arg2, ...)
        // Takes lower priority than all builtins, templates, and user functions.
        // Only activates when there is no user function with the same name.
        if !target.contains('.') && !self.program.functions.contains_key(target.as_str()) {
            if self.find_set_entity(&target).is_some() {
                let all_params = collect_effective_params(&self.program.model, &target);
                if all_params.len() != args.len() {
                    return Err(RuntimeError(format!(
                        "set entity `{target}` expects {} positional argument(s), got {}",
                        all_params.len(),
                        args.len()
                    )));
                }
                let mut impls = std::collections::BTreeMap::new();
                for (param, val) in all_params.iter().zip(args.iter()) {
                    if let Some(name) = &param.attr {
                        impls.insert(name.clone(), val.clone());
                    }
                }
                return Ok(Value::SetInstance(SetInstanceObject {
                    entity_name: target.clone(),
                    impls,
                }));
            }
        }

        self.call_user_function(&target, args)
    }

    /// Handle a method call `instance.method(args)` on a [`SetInstanceObject`].
    ///
    /// - **`Ref` attribute** (`attr: &myFunc()`): calls the referenced helper
    ///   function *or* instantiates a template of the same name, prepending
    ///   any curried arguments stored in the `Ref` before `args`.
    /// - **`FuncDef` param** (`attr: (String):(Bool)`): the impl value must be
    ///   a `String` holding the name of a helper function to delegate to.
    /// - **`"attrs"`** (built-in method): returns `List[String]` of body attr
    ///   names in canonical declaration order (top ancestor first).
    /// - **`"callAttr"`** (built-in method): calls a body attr by name.
    /// - All other attribute types produce an error — they are not callable.
    fn eval_set_instance_call(
        &mut self,
        inst: &SetInstanceObject,
        method: &str,
        args: Vec<Value>,
    ) -> Result<Value, RuntimeError> {
        // ── Built-in instance methods ────────────────────────────────────────

        // `inst.attrs()` → List[BoundAttr] in canonical declaration order.
        // Each element is callable as `op()` and exposes `op.name: String`.
        if method == "attrs" {
            let canonical =
                collect_effective_attrs_canonical_order(&self.program.model, &inst.entity_name);
            let list = canonical
                .into_iter()
                .filter_map(|a| {
                    a.attr.map(|name| {
                        Value::BoundAttr(BoundAttrObject {
                            instance: inst.clone(),
                            attr_name: name,
                        })
                    })
                })
                .collect();
            return Ok(Value::List(list));
        }

        // `inst.generateAll()` → List[Map{name: String, code: String}] in canonical order.
        // Iterates every body attr, invokes each template ref, generates the output, and
        // returns the results as an ordered list of {name, code} maps.
        // If the set declares `>> path` or `>>? path`, the combined output is written to
        // that file automatically (always-write vs. write-once).
        //
        // Lead-template composition:
        //   If the canonical attrs contain one named `lead`, it is treated as the outer
        //   scaffold template.  All other (non-lead) attrs are rendered first; their inner
        //   body fragments are extracted and concatenated.  The combined fragment string is
        //   stored in `self.set_attrs_context` so that `<[ attrs() ]>` inside the lead
        //   template expands to it.  The lead template produces the single combined output
        //   for the `>>` path; non-lead attrs still appear individually in the returned
        //   List for inspection, but the written file is exclusively the lead output.
        if method == "generateAll" && args.is_empty() {
            let canonical =
                collect_effective_attrs_canonical_order(&self.program.model, &inst.entity_name);
            let output_decl = collect_effective_output(&self.program.model, &inst.entity_name);
            let exec_decl = collect_effective_exec(&self.program.model, &inst.entity_name);

            let has_lead = canonical.iter().any(|a| a.attr.as_deref() == Some("lead"));

            let mut results = Vec::new();
            let inst_clone = inst.clone();

            if has_lead {
                // ── Lead-template path ────────────────────────────────────────
                // 1. Render every non-lead attr and collect their inner bodies.
                let mut inner_bodies: Vec<String> = Vec::new();
                for attr in &canonical {
                    if attr.attr.as_deref() == Some("lead") {
                        continue;
                    }
                    if let Some(name) = &attr.attr {
                        let leaf = self.eval_set_instance_call(&inst_clone, name, vec![])?;
                        // Extract the inner body fragment (strips outer `{ … }`).
                        let fragment = inline_value_as_template_fragment(self, &leaf, None)?;
                        inner_bodies.push(fragment);

                        // Also collect into results for the returned List.
                        let code_val = call_builtin(
                            self,
                            "TLang.Generator.generate",
                            std::slice::from_ref(&leaf),
                        )
                        .ok()
                        .flatten();
                        let code = match code_val {
                            Some(Value::String(s)) => s,
                            Some(other) => value_to_string(&other),
                            None => String::new(),
                        };
                        let mut entry = std::collections::BTreeMap::new();
                        entry.insert("name".to_string(), Value::String(name.clone()));
                        entry.insert("code".to_string(), Value::String(code));
                        results.push(Value::Map(entry));
                    }
                }

                // 2. Install the combined fragment as the attrs context so that
                //    `<[ attrs() ]>` inside the lead template expands to it.
                self.set_attrs_context = Some(inner_bodies.join("\n"));

                // 3. Instantiate the lead template with the context active.
                let lead_leaf = self.eval_set_instance_call(&inst_clone, "lead", vec![])?;

                // 4. Clear the context immediately after instantiation.
                self.set_attrs_context = None;

                // 5. Generate the full output from the lead leaf.
                let lead_code_val = call_builtin(self, "TLang.Generator.generate", &[lead_leaf])?
                    .ok_or_else(|| {
                    RuntimeError("Generator.generate returned nothing for lead attr".to_string())
                })?;
                let lead_code = match lead_code_val {
                    Value::String(s) => s,
                    other => value_to_string(&other),
                };

                // 6. Prepend the lead entry to results.
                let mut lead_entry = std::collections::BTreeMap::new();
                lead_entry.insert("name".to_string(), Value::String("lead".to_string()));
                lead_entry.insert("code".to_string(), Value::String(lead_code.clone()));
                results.insert(0, Value::Map(lead_entry));

                // 7. Write the lead output to the >> path (if declared).
                if let Some(decl) = output_decl {
                    let path = interpolate_output_path(&decl.path, &inst_clone.impls);
                    write_generated_output(&path, &lead_code, &decl.mode, self.force_regen)
                        .map_err(|e| {
                            RuntimeError(format!(
                                "failed to write generated output to `{path}`: {e}"
                            ))
                        })?;
                }
                // 8. Execute via !> executor (if declared).
                if let Some(decl) = exec_decl {
                    exec_generated_output(&lead_code, &decl.executor).map_err(|e| {
                        RuntimeError(format!(
                            "failed to execute generated output via `{}`: {e}",
                            decl.executor
                        ))
                    })?;
                }
            } else {
                // ── Normal (non-lead) path — existing behaviour ───────────────
                for attr in canonical {
                    if let Some(name) = attr.attr {
                        let leaf = self.eval_set_instance_call(&inst_clone, &name, vec![])?;
                        let code_val = call_builtin(self, "TLang.Generator.generate", &[leaf])?
                            .ok_or_else(|| {
                                RuntimeError(format!(
                                    "Generator.generate returned nothing for attr `{name}`"
                                ))
                            })?;
                        let code = match code_val {
                            Value::String(s) => s,
                            other => value_to_string(&other),
                        };
                        let mut entry = std::collections::BTreeMap::new();
                        entry.insert("name".to_string(), Value::String(name));
                        entry.insert("code".to_string(), Value::String(code));
                        results.push(Value::Map(entry));
                    }
                }

                // Write to file / execute if the set declares an output or exec decl.
                let combined: String = results
                    .iter()
                    .filter_map(|v| {
                        if let Value::Map(m) = v {
                            m.get("code").and_then(|c| {
                                if let Value::String(s) = c {
                                    Some(s.as_str())
                                } else {
                                    None
                                }
                            })
                        } else {
                            None
                        }
                    })
                    .collect::<Vec<_>>()
                    .join("\n\n");

                if let Some(decl) = output_decl {
                    let path = interpolate_output_path(&decl.path, &inst_clone.impls);
                    write_generated_output(&path, &combined, &decl.mode, self.force_regen)
                        .map_err(|e| {
                            RuntimeError(format!(
                                "failed to write generated output to `{path}`: {e}"
                            ))
                        })?;
                }
                if let Some(decl) = exec_decl {
                    exec_generated_output(&combined, &decl.executor).map_err(|e| {
                        RuntimeError(format!(
                            "failed to execute generated output via `{}`: {e}",
                            decl.executor
                        ))
                    })?;
                }
            }

            return Ok(Value::List(results));
        }

        let _entity = self
            .find_set_entity(&inst.entity_name)
            .ok_or_else(|| {
                RuntimeError(format!(
                    "set entity `{}` not found in model",
                    inst.entity_name
                ))
            })?
            .clone();

        // Search body attrs first, then constructor params — walking the full
        // `ext` chain so inherited attrs and surcharges are both visible.
        // Child attrs/params shadow parent ones with the same name.
        let mut effective_all = collect_effective_attrs(&self.program.model, &inst.entity_name);
        effective_all.extend(collect_effective_params(
            &self.program.model,
            &inst.entity_name,
        ));

        for attr in &effective_all {
            if attr.attr.as_deref() != Some(method) {
                continue;
            }
            match &attr.value {
                // Ref attr: `attr: &myFunc(currying...)` — call the referenced function.
                //
                // Calling convention:
                //   1. Static currying values embedded in the Ref (from the model
                //      declaration, e.g. `&myFunc("constant")`).
                //   2. The instance's impl values, in the order they were declared
                //      as constructor params.  This lets the ref function receive
                //      the instance's context (e.g. `prefix`) automatically.
                //   3. The explicit call-site arguments passed by the caller.
                crate::model_tree::ModelValueTypeTree::Ref { path, currying } => {
                    use crate::model_tree::RefArg;
                    let func_name = path.join(".");

                    // Flatten all currying groups into a single arg list.
                    let flat_args: Vec<&RefArg> = currying.iter().flatten().collect();

                    let call_args: Vec<Value> = if flat_args.is_empty() {
                        // ── Legacy implicit mode ────────────────────────────────
                        // No explicit currying args: append impl param values in
                        // declaration order, then the caller-supplied args.
                        let mut v: Vec<Value> = Vec::new();
                        let effective_params =
                            collect_effective_params(&self.program.model, &inst.entity_name);
                        for param in &effective_params {
                            if let Some(param_name) = &param.attr
                                && let Some(impl_val) = inst.impls.get(param_name)
                            {
                                v.push(impl_val.clone());
                            }
                        }
                        v.extend(args);
                        v
                    } else {
                        // ── Explicit mode ───────────────────────────────────────
                        // The currying list fully specifies the argument list.
                        // Every `_` (Hole) is replaced by the next caller-supplied
                        // arg in left-to-right order.

                        // Count how many holes the caller must fill.
                        let hole_count = flat_args
                            .iter()
                            .filter(|a| matches!(a, RefArg::Hole))
                            .count();

                        if hole_count != args.len() {
                            return Err(RuntimeError(format!(
                                "attribute `{method}` on `{}` has {} caller placeholder(s) (`_`) \
                                 but was called with {} argument(s) — \
                                 provide exactly one argument per `_` in left-to-right order",
                                inst.entity_name,
                                hole_count,
                                args.len()
                            )));
                        }

                        let mut hole_iter = args.into_iter();
                        let mut v: Vec<Value> = Vec::with_capacity(flat_args.len());

                        for ref_arg in &flat_args {
                            let val = match ref_arg {
                                // `this` → the current set instance.
                                RefArg::This => Value::SetInstance(inst.clone()),
                                // `_` → the next caller-supplied argument.
                                RefArg::Hole => hole_iter.next().unwrap(), // count already verified
                                // Typed literals.
                                RefArg::Str(s) => Value::String(s.clone()),
                                RefArg::Int(n) => Value::Int(*n),
                                RefArg::Bool(b) => Value::Bool(*b),
                                // Nested `&func` or `&pkg.fn` → pass the function
                                // name as a string so FuncDef params can dispatch it.
                                RefArg::Ref(ref_path) => Value::String(ref_path.join(".")),
                                // Constructor parameter reference: look up the impl
                                // value stored for `name`.  When the stored value is a
                                // template-function reference string (e.g.
                                // `"Template.leader"` set via `impl leader: &Template.leader`),
                                // automatically call that template with `this` as its
                                // sole argument to produce a Leaf — allowing `LangTmpl`
                                // constructor params to be passed directly to
                                // `Generator.generate`.
                                RefArg::ImplParam(name) => match inst.impls.get(name) {
                                    Some(Value::String(func_name)) => {
                                        let func_name = func_name.clone();
                                        // Resolve module-qualified names like
                                        // "Template.leader" → "leader" for flat
                                        // imports, passing through alias imports
                                        // (e.g. "kotlin.generate" → "KotlinGen.generate").
                                        let resolved =
                                            self.program.imports.resolve_call_target(&func_name);
                                        // Also try the bare suffix as a fallback for
                                        // flat local imports whose `use Module` entry
                                        // was stripped from the alias resolver (e.g.
                                        // "Template.leader" → try "leader" when
                                        // "Template.leader" is not in the template map).
                                        let bare =
                                            resolved.rsplit_once('.').map(|(_, s)| s.to_string());
                                        let tmpl_opt =
                                            self.program.templates.get(&resolved).cloned().or_else(
                                                || {
                                                    bare.as_deref()
                                                        .and_then(|b| self.program.templates.get(b))
                                                        .cloned()
                                                },
                                            );
                                        if let Some(tmpl) = tmpl_opt {
                                            self.instantiate_template(
                                                &tmpl,
                                                vec![Value::SetInstance(inst.clone())],
                                            )?
                                        } else {
                                            Value::String(resolved)
                                        }
                                    }
                                    Some(other) => other.clone(),
                                    None => {
                                        return Err(RuntimeError(format!(
                                            "constructor parameter `{name}` not found on \
                                             instance of `{}`; only parameters declared in \
                                             the `(…)` section may be used as currying \
                                             arguments",
                                            inst.entity_name
                                        )));
                                    }
                                },
                            };
                            v.push(val);
                        }
                        v
                    };

                    // Dispatch: template ref takes priority over helper function.
                    // If `func_name` matches a `lang [...]` template, instantiate
                    // it directly (returns a Leaf).  Otherwise fall back to calling
                    // the helper function by name.
                    if let Some(template) = self.program.templates.get(&func_name).cloned() {
                        return self.instantiate_template(&template, call_args);
                    }
                    if let Some(raw_template) = self.program.raw_templates.get(&func_name).cloned()
                    {
                        return self.instantiate_raw_template(&raw_template, call_args);
                    }
                    return self.call_user_function(&func_name, call_args);
                }
                // FuncDef param: impl must hold the function name as a String or a Lambda.
                crate::model_tree::ModelValueTypeTree::FuncDef { .. } => {
                    let impl_val = inst.impls.get(method).cloned();
                    match impl_val {
                        Some(Value::String(func_name)) => {
                            return self.call_user_function(&func_name, args);
                        }
                        Some(Value::Lambda(lambda)) => {
                            return self.call_lambda(&lambda, args);
                        }
                        _ => {}
                    }
                    return Err(RuntimeError(format!(
                        "constructor param `{method}` on `{}` is a function-definition type; \
                         supply the name of a helper function (using `&funcName`) or a lambda \
                         expression at instantiation",
                        inst.entity_name
                    )));
                }
                // Plain value — cannot be called.
                other => {
                    return Err(RuntimeError(format!(
                        "attribute `{method}` on `{}` (type {:?}) is not callable",
                        inst.entity_name, other
                    )));
                }
            }
        }

        Err(RuntimeError(format!(
            "attribute `{method}` not found on set entity `{}`",
            inst.entity_name
        )))
    }

    fn instantiate_data_template(
        &mut self,
        template: &DataTemplateFunction,
        args: Vec<Value>,
    ) -> Result<Value, RuntimeError> {
        if template.params.len() != args.len() {
            return Err(RuntimeError(format!(
                "data template `{}` expects {} arguments, got {}",
                template.name,
                template.params.len(),
                args.len()
            )));
        }

        // Build param → value maps.
        let args_map: HashMap<String, String> = template
            .params
            .iter()
            .zip(args.iter())
            .map(|(p, v)| (p.name.clone(), value_to_string(v)))
            .collect();

        let param_values: HashMap<String, Value> = template
            .params
            .iter()
            .zip(args.iter())
            .map(|(p, v)| (p.name.clone(), v.clone()))
            .collect();

        // Expand any <[ include ]> calls inside the content.
        let expanded = expand_includes(self, &template.content, &param_values)?;

        // Parse and build the data leaf.
        let tree = crate::tmpl_data_tree::parse_tmpl_data_block_tree(
            &template.langs,
            &template.name,
            &template.params,
            &expanded,
        )
        .map_err(|e| RuntimeError(format!("data template `{}`: {}", template.name, e)))?;

        let leaf = libraries::tmpl_data_leaf::build_data_instance_leaf(&tree, &args_map);

        // Record the call in the model tree so serialisers can discover it.
        let call_count = self
            .template_calls
            .entry(template.name.clone())
            .or_insert(0);
        *call_count += 1;
        let instance_name = format!("{}_{}", template.name, *call_count);

        self.model
            .nodes
            .push(crate::model_tree::ModelNodeTree::AssignVar(
                crate::model_tree::ModelAssignVarTree {
                    name: instance_name.clone(),
                    ty: Some(crate::model_tree::ModelValueTypeTree::Type(
                        "TmplDataInstance".to_string(),
                    )),
                    value: format!("data [{}] {}", template.langs.join(", "), template.name),
                    context: crate::tree_context::TreeContext::default(),
                },
            ));

        Ok(leaf)
    }

    fn instantiate_cmd_template(
        &mut self,
        template: &CmdTemplateFunction,
        args: Vec<Value>,
    ) -> Result<Value, RuntimeError> {
        if template.params.len() != args.len() {
            return Err(RuntimeError(format!(
                "cmd template `{}` expects {} arguments, got {}",
                template.name,
                template.params.len(),
                args.len()
            )));
        }

        let args_map: HashMap<String, String> = template
            .params
            .iter()
            .zip(args.iter())
            .map(|(p, v)| (p.name.clone(), value_to_string(v)))
            .collect();

        let tree = crate::tmpl_cmd_tree::parse_tmpl_cmd_block_tree(
            &template.langs,
            &template.name,
            &template.params,
            &template.content,
        )
        .map_err(|e| RuntimeError(format!("cmd template `{}`: {}", template.name, e)))?;

        Ok(libraries::tmpl_cmd_leaf::build_cmd_instance_leaf(&tree, &args_map))
    }

    fn instantiate_style_template(
        &mut self,
        template: &StyleTemplateFunction,
        lang: &str,
        args: Vec<Value>,
    ) -> Result<Value, RuntimeError> {
        if !template.langs.iter().any(|l| l.eq_ignore_ascii_case(lang)) {
            return Err(RuntimeError(format!(
                "style template `{}` does not support language `{lang}` — supported: {}",
                template.name,
                template.langs.join(", ")
            )));
        }
        if template.params.len() != args.len() {
            return Err(RuntimeError(format!(
                "style template `{}` expects {} arguments, got {}",
                template.name,
                template.params.len(),
                args.len()
            )));
        }
        let args_map: HashMap<String, String> = template
            .params
            .iter()
            .zip(args.iter())
            .map(|(p, v)| (p.name.clone(), value_to_string(v)))
            .collect();

        let tree = crate::tmpl_style_tree::parse_style_block_tree(
            &template.langs,
            &template.name,
            &template.params,
            &template.content,
        )
        .map_err(|e| RuntimeError(format!("style template `{}`: {}", template.name, e)))?;

        let structs_value = crate::tmpl_style_tree::style_structs_to_value(&tree, &args_map);

        let mut fields = std::collections::BTreeMap::new();
        fields.insert("kind".to_string(), Value::String("style".to_string()));
        fields.insert("name".to_string(), Value::String(template.name.clone()));
        fields.insert("lang".to_string(), Value::String(lang.to_string()));
        fields.insert("structs".to_string(), structs_value);
        Ok(Value::Leaf(LeafObject::new(fields)))
    }

    fn instantiate_doc_template(
        &mut self,
        template: &DocTemplateFunction,
        lang: &str,
        args: Vec<Value>,
    ) -> Result<Value, RuntimeError> {
        if !template.langs.iter().any(|l| l.eq_ignore_ascii_case(lang)) {
            return Err(RuntimeError(format!(
                "doc template `{}` does not support language `{lang}` — supported: {}",
                template.name,
                template.langs.join(", ")
            )));
        }
        if template.params.len() != args.len() {
            return Err(RuntimeError(format!(
                "doc template `{}` expects {} arguments, got {}",
                template.name,
                template.params.len(),
                args.len()
            )));
        }
        let args_map: HashMap<String, String> = template
            .params
            .iter()
            .zip(args.iter())
            .map(|(p, v)| (p.name.clone(), value_to_string(v)))
            .collect();

        let tree = crate::tmpl_doc_tree::parse_doc_block_tree(
            &template.langs,
            &template.name,
            &template.params,
            &template.content,
        )
        .map_err(|e| RuntimeError(format!("doc template `{}`: {}", template.name, e)))?;

        // Serialize the doc node tree to a Value so external generators can
        // traverse it.  All ${} substitutions are resolved here.
        let nodes_value = crate::tmpl_doc_tree::doc_nodes_to_value(&tree.nodes, &args_map);

        // Build a Leaf that mirrors the lang-template leaf shape so that
        // Generator.generate(docLeaf) works identically.
        let mut fields = std::collections::BTreeMap::new();
        fields.insert("kind".to_string(), Value::String("doc".to_string()));
        fields.insert("name".to_string(), Value::String(template.name.clone()));
        fields.insert("lang".to_string(), Value::String(lang.to_string()));
        fields.insert("nodes".to_string(), nodes_value);
        Ok(Value::Leaf(LeafObject::new(fields)))
    }

    fn instantiate_raw_template(
        &mut self,
        template: &RawTemplateFunction,
        args: Vec<Value>,
    ) -> Result<Value, RuntimeError> {
        if template.params.len() != args.len() {
            return Err(RuntimeError(format!(
                "raw template `{}` expects {} arguments, got {}",
                template.name,
                template.params.len(),
                args.len()
            )));
        }

        // Strip the surrounding `{ }` from the balanced_block content.
        let body = template.content.as_str();
        let inner = if body.starts_with('{') && body.ends_with('}') {
            &body[1..body.len() - 1]
        } else {
            body
        };

        let output = match template.variant {
            RawVariant::AsIs => {
                // Emit verbatim — trim only a single leading/trailing newline
                // that the grammar naturally adds from the braces placement.
                let trimmed = inner.strip_prefix('\n').unwrap_or(inner);
                let trimmed = trimmed.strip_suffix('\n').unwrap_or(trimmed);
                trimmed.to_string()
            }
            RawVariant::Replaced => {
                // Build param → value string map.
                let args_map: HashMap<String, String> = template
                    .params
                    .iter()
                    .zip(args.iter())
                    .map(|(p, v)| (p.name.clone(), value_to_string(v)))
                    .collect();

                // Replace every `${paramName}` occurrence.
                let trimmed = inner.strip_prefix('\n').unwrap_or(inner);
                let trimmed = trimmed.strip_suffix('\n').unwrap_or(trimmed);
                let mut result = trimmed.to_string();
                for (param_name, param_value) in &args_map {
                    let placeholder = format!("${{{param_name}}}");
                    result = result.replace(&placeholder, param_value);
                }
                result
            }
        };

        Ok(Value::String(output))
    }

    fn instantiate_template(
        &mut self,
        template: &TemplateFunction,
        args: Vec<Value>,
    ) -> Result<Value, RuntimeError> {
        if template.params.len() != args.len() {
            return Err(RuntimeError(format!(
                "template `{}` expects {} arguments, got {}",
                template.name,
                template.params.len(),
                args.len()
            )));
        }

        let call_count = self
            .template_calls
            .entry(template.name.clone())
            .or_insert(0);
        *call_count += 1;
        let instance_name = format!("{}_{}", template.name, *call_count);

        let args_text = template
            .params
            .iter()
            .zip(args.iter())
            .map(|(param, value)| format!("{}={}", param.name, value_to_string(value)))
            .collect::<Vec<_>>()
            .join(", ");
        let content = match &template.content {
            TemplateContent::Full(body) => body.trim().to_string(),
            TemplateContent::Specialized(body) => body.trim().to_string(),
        };

        // Build a param→value map so include calls can look up template args.
        let param_values: HashMap<String, Value> = template
            .params
            .iter()
            .zip(args.iter())
            .map(|(p, v)| (p.name.clone(), v.clone()))
            .collect();

        let content = expand_includes(self, &content, &param_values)?;

        let instance_value = format!(
            "lang [{}] {}({}) {}",
            template.lang, template.name, args_text, content
        );

        self.model
            .nodes
            .push(ModelNodeTree::AssignVar(ModelAssignVarTree {
                name: instance_name.clone(),
                ty: Some(ModelValueTypeTree::Type(TEMPLATE_INSTANCE_TYPE.to_string())),
                value: instance_value.clone(),
                context: TreeContext::default(),
            }));

        // Return a Leaf representing this instance so callers can pass it
        // directly to Generator.generate without going through Leaf.model().

        // Build string args map for ${param} substitution in the tree.
        // Flattened dotted paths are also added so that interpolations like
        // `${compositor.entity.pkg}` in pkg/body directives resolve correctly.
        let mut args_map: HashMap<String, String> = HashMap::new();
        for (param, value) in template.params.iter().zip(args.iter()) {
            flatten_value_to_string_map(&param.name, value, &mut args_map);
        }

        let mut fields = BTreeMap::new();
        fields.insert("kind".to_string(), Value::String("assign".to_string()));
        fields.insert("name".to_string(), Value::String(instance_name));
        fields.insert(
            "type".to_string(),
            Value::String(TEMPLATE_INSTANCE_TYPE.to_string()),
        );
        fields.insert("value".to_string(), Value::String(instance_value));
        fields.insert("lang".to_string(), Value::String(template.lang.clone()));

        // Pre-populate the parsed tree so generators can use TLang.Leaf.get(tmpl, "package")
        // and TLang.Leaf.get(tmpl, "nodes") without any proprietary parse step.
        match libraries::tmpl_leaf::build_tmpl_tree_fields(&content, &template.lang, &args_map) {
            Ok((pkg, nodes)) => {
                fields.insert("package".to_string(), Value::String(pkg));
                fields.insert("nodes".to_string(), Value::List(nodes));
            }
            Err(_) => {
                fields.insert("package".to_string(), Value::String(String::new()));
                fields.insert("nodes".to_string(), Value::List(Vec::new()));
            }
        }

        Ok(Value::Leaf(LeafObject::new(fields)))
    }
}

/// Validate that every `lang [xxx]` template block has a corresponding
/// `use <Package> as xxx` import in scope.  The alias of the `use` directive
/// is what registers a language identifier — there are no longer any built-in
/// `TLang.Xxx` language imports.
///
/// Files that declare `expose` are generator/library packages — they define
/// the language implementation and are exempt from this check.
///
/// Returns a `CompileError` naming the first unregistered language together
/// with an `(at_offset: N)` tag so the LSP can underline the right spot.
fn validate_template_languages(model: &crate::ast::DomainModel) -> Result<(), CompileError> {
    // Generator/library files (those with `expose` directives) ARE the language
    // implementation — they don't need to import a generator for themselves.
    if !model.header.exposes.is_empty() {
        return Ok(());
    }

    // Every `use X as alias` registers `alias` (lowercased) as a valid language.
    let known_languages: std::collections::HashSet<String> = model
        .header
        .uses
        .iter()
        .filter_map(|u| u.alias.as_deref().map(|a| a.to_lowercase()))
        .collect();

    for block in &model.body {
        let crate::ast::DomainBlock::Template(tmpl) = block else {
            continue;
        };
        let lang = tmpl.lang.trim().to_lowercase();
        if !known_languages.contains(&lang) {
            return Err(CompileError(format!(
                "unknown template language `{lang}` in template `{name}` — \
                 import a generator package with `use <Package> as {lang}` \
                 (at_offset: {offset})",
                lang = tmpl.lang,
                name = tmpl.name,
                offset = tmpl.lang_offset,
            )));
        }
    }
    Ok(())
}

/// Checks that every unquoted type reference in model set declarations
/// resolves to a declared set.
///
/// In TLang model blocks two kinds of attribute values look similar but mean
/// different things:
///
/// ```tlang
/// set Project {
///     name:        "pkgmarket",    // StringLiteral — opaque data, never validated
///     codeAdapter: QuarkusKotlin, // Type          — set reference, MUST be declared
/// }
/// ```
///
/// `StringLiteral` values (`"..."`) are plain data and are always accepted.
/// `Type` values (unquoted identifiers) are set references; this function
/// verifies each one names a set that exists in the merged model.
///
/// Built-in scalar / collection type names (`String`, `Int`, `Number`, …) are
/// whitelisted so that type-annotation style attrs like `id: Number` continue
/// to work without requiring a `set Number { … }` declaration.
///
/// Set extension parents (the `: Parent` clause) are also validated.  They
/// may be dotted (`Forge.ForgeProject`); only the final segment is checked
/// against the known-sets map since the prefix is an import alias.
fn validate_model_type_refs(model: &ModelBlockTree) -> Result<(), CompileError> {
    use crate::model_tree::{ModelNodeTree, ModelValueTypeTree};

    // Build the set of all declared set names from the fully-merged model.
    let known: std::collections::HashSet<&str> = model
        .nodes
        .iter()
        .filter_map(|n| match n {
            ModelNodeTree::SetEntity(s) => Some(s.name.as_str()),
            ModelNodeTree::AssignVar(_) => None,
        })
        .collect();

    // Scalar primitive / built-in names that may appear as type annotations
    // without being declared as named sets.
    const PRIMITIVES: &[&str] = &[
        "String", "Int", "Bool", "Float", "Long", "Double", "Short", "Char",
        "Byte", "Number", "List", "Map", "Set", "Unit", "Any", "Nothing",
        "Null", "Type", "FuncDef",
    ];

    let is_known = |name: &str| known.contains(name) || PRIMITIVES.contains(&name);

    for node in &model.nodes {
        let set = match node {
            ModelNodeTree::SetEntity(s) => s,
            ModelNodeTree::AssignVar(_) => continue,
        };

        // ── Validate parent set references (`exts`) ───────────────────────
        // Exts may be dotted (e.g. "Forge.ForgeProject"); only the last
        // segment is the set name — the prefix is an import alias resolved
        // by the loader before compilation.
        for ext in &set.exts {
            let set_name = ext.rsplit('.').next().unwrap_or(ext.as_str());
            if !is_known(set_name) {
                return Err(CompileError(format!(
                    "set `{}` extends unknown type `{ext}` — \
                     check that the module is imported and the type name is spelled correctly",
                    set.name,
                )));
            }
        }

        // ── Validate unquoted type references in attribute values ─────────
        // Only `Type(name)` variants are set references and require
        // validation.  `StringLiteral` values are opaque data and are
        // intentionally not checked here.
        for attr_tree in &set.attrs {
            if let ModelValueTypeTree::Type(ref_name) = &attr_tree.value {
                if !is_known(ref_name) {
                    let key = attr_tree.attr.as_deref().unwrap_or("<unnamed>");
                    return Err(CompileError(format!(
                        "set `{}` attribute `{key}` references unknown type `{ref_name}`\n\
                         hint: if this is a plain string value use `{key}: \"{ref_name}\"` (quoted),\n\
                         or declare `set {ref_name} {{ ... }}` if it is a type reference",
                        set.name,
                    )));
                }
            }
        }
    }

    Ok(())
}

fn collect_templates(
    model: &crate::ast::DomainModel,
) -> Result<HashMap<String, TemplateFunction>, CompileError> {
    let mut templates = HashMap::new();
    for block in &model.body {
        let DomainBlock::Template(TemplateBlock {
            lang,
            name,
            params,
            content,
            ..
        }) = block
        else {
            continue;
        };
        if templates.contains_key(name) {
            return Err(CompileError(format!(
                "duplicate template definition `{name}`"
            )));
        }
        templates.insert(
            name.clone(),
            TemplateFunction {
                lang: lang.clone(),
                name: name.clone(),
                params: params.clone(),
                content: content.clone(),
            },
        );
    }
    Ok(templates)
}

fn collect_data_templates(
    model: &crate::ast::DomainModel,
) -> Result<HashMap<String, DataTemplateFunction>, CompileError> {
    let mut data_templates = HashMap::new();
    for block in &model.body {
        let crate::ast::DomainBlock::Data(crate::ast::DataTemplateBlock {
            langs,
            name,
            params,
            content,
            ..
        }) = block
        else {
            continue;
        };
        if data_templates.contains_key(name) {
            return Err(CompileError(format!(
                "duplicate data template definition `{name}`"
            )));
        }
        data_templates.insert(
            name.clone(),
            DataTemplateFunction {
                langs: langs.clone(),
                name: name.clone(),
                params: params.clone(),
                content: content.clone(),
            },
        );
    }
    Ok(data_templates)
}

fn collect_cmd_templates(
    model: &crate::ast::DomainModel,
) -> Result<HashMap<String, CmdTemplateFunction>, CompileError> {
    let mut cmd_templates = HashMap::new();
    for block in &model.body {
        let crate::ast::DomainBlock::Cmd(crate::ast::CmdTemplateBlock {
            langs,
            name,
            params,
            content,
            ..
        }) = block
        else {
            continue;
        };
        if cmd_templates.contains_key(name) {
            return Err(CompileError(format!(
                "duplicate cmd template definition `{name}`"
            )));
        }
        cmd_templates.insert(
            name.clone(),
            CmdTemplateFunction {
                langs: langs.clone(),
                name: name.clone(),
                params: params.clone(),
                content: content.clone(),
            },
        );
    }
    Ok(cmd_templates)
}

fn collect_raw_templates(
    model: &crate::ast::DomainModel,
) -> Result<HashMap<String, RawTemplateFunction>, CompileError> {
    let mut raw_templates = HashMap::new();
    for block in &model.body {
        let crate::ast::DomainBlock::Raw(crate::ast::RawTemplateBlock {
            variant,
            name,
            params,
            content,
            ..
        }) = block
        else {
            continue;
        };
        if raw_templates.contains_key(name) {
            return Err(CompileError(format!(
                "duplicate raw template definition `{name}`"
            )));
        }
        let raw_variant = match variant.as_str() {
            "AsIs" => RawVariant::AsIs,
            "Replaced" => RawVariant::Replaced,
            other => {
                return Err(CompileError(format!(
                    "unknown raw template variant `{other}` in `{name}` — expected `AsIs` or `Replaced`"
                )));
            }
        };
        raw_templates.insert(
            name.clone(),
            RawTemplateFunction {
                variant: raw_variant,
                name: name.clone(),
                params: params.clone(),
                content: content.clone(),
            },
        );
    }
    Ok(raw_templates)
}

/// Resolve `entity.attr` from the model tree, returning the attribute's string value.
/// Resolve an entity name that may include a module/alias prefix.
///
/// Entity names in the model are always simple identifiers (e.g. `MyCompositor`).
/// When the user writes `compositor.MyCompositor(...)` the path is
/// `compositor.MyCompositor` where `compositor` is a `use … as compositor` alias.
/// Stripping the prefix and using only the last segment gives the model name.
fn resolve_entity_name(entity_name: &str) -> &str {
    match entity_name.rfind('.') {
        Some(pos) => &entity_name[pos + 1..],
        None => entity_name,
    }
}

/// Look up a `set` entity by name in the Runtime's live model tree.
///
/// Accepts both plain names (`MyCompositor`) and alias-prefixed names
/// (`compositor.MyCompositor`) — the prefix is stripped before lookup because
/// all entities are stored under their simple names in the merged model.
///
/// Returns a reference to the [`ModelSetEntityTree`] if found, or `None`.
fn find_set_entity<'m>(
    model: &'m crate::model_tree::ModelBlockTree,
    entity_name: &str,
) -> Option<&'m crate::model_tree::ModelSetEntityTree> {
    use crate::model_tree::ModelNodeTree;
    let simple = resolve_entity_name(entity_name);
    for node in &model.nodes {
        if let ModelNodeTree::SetEntity(entity) = node
            && entity.name == simple
        {
            return Some(entity);
        }
    }
    None
}

/// Collect the effective body attrs for an entity, walking the `ext` chain.
///
/// Parent attrs are collected first; child attrs are then overlaid on top,
/// so a child attr with the same name as a parent attr silently replaces
/// (surcharges) it.  This applies recursively, so a grandchild surcharges
/// both parent and grandparent attrs.
/// Collect the effective output declaration for an entity, walking the parent
/// chain.  The child's own output decl takes priority; if none, parents are
/// searched right-to-left (last parent wins on collision, consistent with
/// the attr surcharging rule).
fn collect_effective_output(
    model: &crate::model_tree::ModelBlockTree,
    entity_name: &str,
) -> Option<crate::model_tree::OutputDecl> {
    let entity = find_set_entity(model, entity_name)?;
    // Child's own declaration wins.
    if entity.output.is_some() {
        return entity.output.clone();
    }
    // Walk parents right-to-left so the last-declared parent wins.
    for parent_name in entity.exts.iter().rev() {
        if let Some(decl) = collect_effective_output(model, parent_name) {
            return Some(decl);
        }
    }
    None
}

/// Walk the `ext` chain to find the effective `!>` exec declaration for a set
/// entity, mirroring the `collect_effective_output` logic for `>>`.
fn collect_effective_exec(
    model: &crate::model_tree::ModelBlockTree,
    entity_name: &str,
) -> Option<crate::model_tree::ExecDecl> {
    let entity = find_set_entity(model, entity_name)?;
    if entity.exec.is_some() {
        return entity.exec.clone();
    }
    for parent_name in entity.exts.iter().rev() {
        if let Some(decl) = collect_effective_exec(model, parent_name) {
            return Some(decl);
        }
    }
    None
}

/// Execute `content` through `executor` (e.g. `bash`, `sh`, `python`).
///
/// The content is passed as the `-c` / `-Command` argument so the executor
/// receives the entire rendered text as a single command string.  On Windows,
/// `cmd` uses `/C` and `powershell` uses `-Command`; everything else uses `-c`
/// (the POSIX convention).
///
/// Stdout and stderr are inherited from the parent process so the user sees
/// command output directly in the terminal.  A non-zero exit code is surfaced
/// as a `RuntimeError`.
fn exec_generated_output(content: &str, executor: &str) -> std::io::Result<()> {
    #[cfg(windows)]
    let (prog, flag) = match executor.to_ascii_lowercase().as_str() {
        "cmd" | "cmd.exe" => ("cmd.exe", "/C"),
        "powershell" | "pwsh" => (executor, "-Command"),
        _ => (executor, "-c"),
    };
    #[cfg(not(windows))]
    let (prog, flag) = (executor, "-c");

    let status = std::process::Command::new(prog)
        .arg(flag)
        .arg(content)
        .status()?;

    if !status.success() {
        let code = status.code().unwrap_or(-1);
        return Err(std::io::Error::other(format!(
            "executor `{executor}` exited with code {code}"
        )));
    }
    Ok(())
}

/// Interpolate `${paramName}` placeholders in an output path template using
/// the instance's impl values.
fn interpolate_output_path(
    path: &str,
    impls: &std::collections::BTreeMap<String, Value>,
) -> String {
    let mut result = path.to_string();
    for (key, val) in impls {
        result = result.replace(&format!("${{{key}}}"), &value_to_string(val));
    }
    result
}

/// Write `content` to `path`, creating parent directories as needed.
/// Respects [`WriteMode`]: `AlwaysWrite` overwrites; `WriteOnce` skips if the
/// file already exists.
/// Write `content` to `path`, respecting the declared [`WriteMode`].
///
/// - [`WriteMode::AlwaysWrite`] (`>>`) — always overwrites.
/// - [`WriteMode::WriteOnce`] (`>>?`) — skips if the file already exists,
///   **unless** `force_regen` is `true`, in which case it overwrites anyway.
///   Pass `force_regen = true` (via `--force-regen` on the CLI) to regenerate
///   scaffold files after a template change.
fn write_generated_output(
    path: &str,
    content: &str,
    mode: &crate::model_tree::WriteMode,
    force_regen: bool,
) -> std::io::Result<()> {
    let p = std::path::Path::new(path);
    if matches!(mode, crate::model_tree::WriteMode::WriteOnce) && p.exists() && !force_regen {
        return Ok(());
    }
    if let Some(parent) = p.parent() {
        std::fs::create_dir_all(parent)?;
    }
    std::fs::write(p, content)
}

/// Collect effective attrs with **canonical ordering**: attribute names appear
/// in the order they were *first* declared (starting from the topmost ancestor),
/// with child-set values replacing parent-set values *in place* for the same
/// name.  New attrs introduced by a child are appended after any inherited ones.
///
/// This differs from [`collect_effective_attrs`] which appends overriding child
/// attrs at the end (breaking the original order).  Use this function whenever
/// you need stable, top-down declaration order — e.g. for `inst.attrs()`.
/// Collect effective attrs with **canonical ordering**: attribute names appear
/// in the order they were *first* declared (starting from the topmost ancestor),
/// with child-set values replacing parent-set values *in place* for the same
/// name.  New attrs introduced by a child are appended after any inherited ones.
///
/// Multiple parents are processed left-to-right; a later parent's value wins
/// on name collision (same last-wins rule as single-parent surcharging).
fn collect_effective_attrs_canonical_order(
    model: &crate::model_tree::ModelBlockTree,
    entity_name: &str,
) -> Vec<crate::model_tree::ModelSetAttributeTree> {
    let entity = match find_set_entity(model, entity_name) {
        Some(e) => e,
        None => return Vec::new(),
    };

    let mut result: Vec<crate::model_tree::ModelSetAttributeTree> = Vec::new();

    // Process parents left-to-right; later parent overlays earlier one in-place.
    for parent_name in &entity.exts {
        let parent_attrs = collect_effective_attrs_canonical_order(model, parent_name);
        for attr in parent_attrs {
            if let Some(name) = &attr.attr
                && let Some(existing) = result
                    .iter_mut()
                    .find(|a| a.attr.as_deref() == Some(name.as_str()))
            {
                *existing = attr.clone();
                continue;
            }
            result.push(attr);
        }
    }

    // Overlay child's own attrs: replace in-place if name exists, else append.
    for attr in &entity.attrs {
        if let Some(name) = &attr.attr
            && let Some(existing) = result
                .iter_mut()
                .find(|a| a.attr.as_deref() == Some(name.as_str()))
        {
            *existing = attr.clone();
            continue;
        }
        result.push(attr.clone());
    }

    result
}

/// Collect effective attrs walking the full parent chain.
/// Multiple parents are processed left-to-right; last parent wins on collision.
/// Child attrs always surcharge parents.
fn collect_effective_attrs(
    model: &crate::model_tree::ModelBlockTree,
    entity_name: &str,
) -> Vec<crate::model_tree::ModelSetAttributeTree> {
    let entity = match find_set_entity(model, entity_name) {
        Some(e) => e,
        None => return Vec::new(),
    };

    let mut result: Vec<crate::model_tree::ModelSetAttributeTree> = Vec::new();

    // Process parents left-to-right.
    for parent_name in &entity.exts {
        let parent_attrs = collect_effective_attrs(model, parent_name);
        for attr in parent_attrs {
            if let Some(name) = &attr.attr {
                result.retain(|a| a.attr.as_deref() != Some(name.as_str()));
            }
            result.push(attr);
        }
    }

    // Overlay child attrs: remove same-named, push child's version.
    for attr in &entity.attrs {
        if let Some(name) = &attr.attr {
            result.retain(|a| a.attr.as_deref() != Some(name.as_str()));
        }
        result.push(attr.clone());
    }

    result
}

/// Collect the effective constructor params for an entity, walking all parents.
/// Multiple parents are processed left-to-right; same-named params are merged
/// (last wins).  The stable declaration order is preserved so that Ref
/// calling-convention arg-prepending produces a predictable argument list.
fn collect_effective_params(
    model: &crate::model_tree::ModelBlockTree,
    entity_name: &str,
) -> Vec<crate::model_tree::ModelSetAttributeTree> {
    let entity = match find_set_entity(model, entity_name) {
        Some(e) => e,
        None => return Vec::new(),
    };

    let mut result: Vec<crate::model_tree::ModelSetAttributeTree> = Vec::new();

    // Process parents left-to-right.
    for parent_name in &entity.exts {
        let parent_params = collect_effective_params(model, parent_name);
        for param in parent_params {
            if let Some(name) = &param.attr {
                result.retain(|p| p.attr.as_deref() != Some(name.as_str()));
            }
            result.push(param);
        }
    }

    // Overlay child params.
    for param in &entity.params {
        if let Some(name) = &param.attr {
            result.retain(|p| p.attr.as_deref() != Some(name.as_str()));
        }
        result.push(param.clone());
    }

    result
}

/// Static model attribute access for use inside `eval_expr`.
///
/// Returns:
/// - `Ok(Some(v))` — attribute found and has a readable static value.
/// - `Ok(None)` — entity or attribute not found.
/// - `Err(…)` — attribute exists but **cannot** be accessed statically
///   (e.g. `Ref` functions and `impl` placeholders require a set instance).
fn model_value_type_to_value(
    vt: &crate::model_tree::ModelValueTypeTree,
) -> Result<Value, RuntimeError> {
    use crate::model_tree::ModelValueTypeTree;
    match vt {
        ModelValueTypeTree::StringLiteral(s) => Ok(Value::String(s.clone())),
        ModelValueTypeTree::Type(s) => Ok(Value::String(s.clone())),
        ModelValueTypeTree::IntLiteral(n) => Ok(Value::Int(*n)),
        ModelValueTypeTree::BoolLiteral(b) => Ok(Value::Bool(*b)),
        ModelValueTypeTree::ArrayLiteral(items) => {
            let vals: Result<Vec<Value>, RuntimeError> =
                items.iter().map(model_value_type_to_value).collect();
            Ok(Value::List(vals?))
        }
        ModelValueTypeTree::Ref { .. } => Err(RuntimeError(
            "ref function attribute cannot be read as a plain value — call it as `instance.attr()`"
                .to_string(),
        )),
        ModelValueTypeTree::Impl { .. } | ModelValueTypeTree::ImplArray => Err(RuntimeError(
            "`impl` placeholder has no static value — instantiate the entity first".to_string(),
        )),
        other => Ok(Value::String(format!("{other:?}"))),
    }
}

fn resolve_model_attr_checked(
    model: &crate::model_tree::ModelBlockTree,
    entity_name: &str,
    attr_name: &str,
) -> Result<Option<Value>, RuntimeError> {
    use crate::model_tree::{ModelNodeTree, ModelValueTypeTree};
    for node in &model.nodes {
        if let ModelNodeTree::SetEntity(entity) = node
            && entity.name == entity_name
        {
            for attr in &entity.attrs {
                if attr.attr.as_deref() == Some(attr_name) {
                    return match &attr.value {
                        ModelValueTypeTree::Ref { .. } => Err(RuntimeError(format!(
                            "`{entity_name}.{attr_name}` is a ref function and cannot be \
                                 accessed on the static entity — instantiate first with \
                                 `let inst = {entity_name}(…)` then call `inst.{attr_name}()`"
                        ))),
                        ModelValueTypeTree::Impl { .. } | ModelValueTypeTree::ImplArray => {
                            Err(RuntimeError(format!(
                                "`{entity_name}.{attr_name}` is an `impl` placeholder with no \
                                 static value — instantiate first with `let inst = {entity_name}(…)`"
                            )))
                        }
                        other => model_value_type_to_value(other).map(Some),
                    };
                }
            }
        }
    }
    Ok(None)
}

/// Format a `f64` for display: if the value has no fractional part, append
/// `.0` so it is clearly distinguishable from an integer.
pub(crate) fn display_float(v: f64) -> String {
    if v.fract() == 0.0 && v.is_finite() {
        format!("{v:.1}")
    } else {
        v.to_string()
    }
}

/// Scan `body` for `<[ … ]>` include expressions, evaluate each one against
/// the running `runtime` using the supplied `param_values` as the argument
/// bindings, and replace the `<[…]>` span with the `String` result of the
/// call.  If the called function returns a non-String value it is coerced via
/// `value_to_string`.
fn expand_includes(
    runtime: &mut Runtime<'_>,
    body: &str,
    param_values: &HashMap<String, Value>,
) -> Result<String, RuntimeError> {
    let mut result = String::with_capacity(body.len());
    let mut remaining = body;

    while let Some(start) = remaining.find("<[") {
        // Copy everything before the include verbatim.
        result.push_str(&remaining[..start]);

        let after_open = &remaining[start + 2..];
        let close = after_open.find("]>").ok_or_else(|| {
            RuntimeError("unterminated `<[` include in template body".to_string())
        })?;
        let call_expr = after_open[..close].trim();

        // Evaluate the call expression and inline its result.
        let expanded = eval_include_call(runtime, call_expr, param_values)?;
        result.push_str(&expanded);

        remaining = &after_open[close + 2..];
    }

    // Append any trailing content after the last include.
    result.push_str(remaining);
    Ok(result)
}

/// Parse and evaluate a single include call expression of the form
/// `funcName(arg1, arg2, …)` or `ns.func(arg1, …)`.
/// Each arg is either a template-param name (looked up in `param_values`)
/// or a string / numeric literal.
fn eval_include_call(
    runtime: &mut Runtime<'_>,
    call_expr: &str,
    param_values: &HashMap<String, Value>,
) -> Result<String, RuntimeError> {
    // Split into function name and argument list.
    let paren_pos = call_expr.find('(').ok_or_else(|| {
        RuntimeError(format!(
            "include expression `{call_expr}` is not a function call"
        ))
    })?;
    let func_name = call_expr[..paren_pos].trim();
    let args_src = call_expr[paren_pos + 1..].trim_end_matches(')').trim();

    // Special built-in include: `<[ attrs() ]>`
    // Expands to the inner body fragments of all non-lead attrs collected by
    // `generateAll()` before it instantiated this (lead) template.
    // Returns an empty string when called outside of a lead-template context
    // (e.g. a direct `inst.lead()` call with no attrs pre-rendered).
    if func_name == "attrs" && args_src.trim().is_empty() {
        return Ok(runtime.set_attrs_context.clone().unwrap_or_default());
    }

    // Parse comma-separated arguments, respecting nested brackets/parens.
    let raw_args = split_call_args(args_src);
    let mut evaled_args: Vec<Value> = Vec::with_capacity(raw_args.len());
    for raw in raw_args {
        let raw = raw.trim();
        if raw.is_empty() {
            continue;
        }
        // Check if it is a known template param name.
        if let Some(val) = param_values.get(raw) {
            evaled_args.push(val.clone());
        } else if (raw.starts_with('"') && raw.ends_with('"'))
            || (raw.starts_with('\'') && raw.ends_with('\''))
        {
            // String literal — strip quotes.
            let inner = raw[1..raw.len() - 1].to_string();
            evaled_args.push(Value::String(inner));
        } else if let Ok(n) = raw.parse::<i64>() {
            evaled_args.push(Value::Int(n));
        } else if let Ok(f) = raw.parse::<f64>() {
            evaled_args.push(Value::Float(f));
        } else {
            // Fall back: treat as a string literal.
            evaled_args.push(Value::String(raw.to_string()));
        }
    }

    let result = runtime.call_user_function(func_name, evaled_args)?;
    inline_value_as_template_fragment(runtime, &result, Some(param_values))
}

/// Convert a value returned by an include call into a plain text fragment
/// that can be spliced into a template body.
///
/// Supported return types:
/// * `Value::String(s)` — if `s` is a known template-function name **and**
///   `param_values` is provided, the template is called with its declared
///   params looked up by name from `param_values`, and the resulting
///   `TemplateInstance` body is inlined.  When the template cannot be
///   resolved this way the string is used verbatim (e.g. it is just a plain
///   text value).
/// * `Value::Leaf` — if it carries `type == "TemplateInstance"` its body
///   content is extracted (with `${param}` substitution applied) and the
///   outer `{ … }` braces stripped.  Any other leaf is coerced with
///   `value_to_string`.
/// * `Value::List` — each element is processed recursively and the results
///   are concatenated.
/// * anything else — coerced with `value_to_string`.
fn inline_value_as_template_fragment(
    runtime: &mut Runtime<'_>,
    value: &Value,
    param_values: Option<&std::collections::HashMap<String, Value>>,
) -> Result<String, RuntimeError> {
    match value {
        Value::String(s) => {
            // When param_values are in scope (i.e. we are expanding a template
            // body) and the string looks like a template-function reference
            // stored via `impl imports: [&Template.import1, …]`, call that
            // template with its declared params resolved from param_values so
            // that each entry in a `List<LangTmpl>` is properly instantiated
            // and inlined rather than emitted as a literal string.
            if let Some(pv) = param_values {
                // Resolve module-qualified names like "Template.import1" → "import1"
                // for flat imports, passing through alias imports unchanged.
                let resolved = runtime.program.imports.resolve_call_target(s.as_str());
                // Also try bare suffix for flat local imports (e.g. "Template.import1"
                // → "import1") when the full resolved name isn't in the template map.
                let bare = resolved.rsplit_once('.').map(|(_, b)| b.to_string());
                let tmpl_opt = runtime
                    .program
                    .templates
                    .get(resolved.as_str())
                    .cloned()
                    .or_else(|| {
                        bare.as_deref()
                            .and_then(|b| runtime.program.templates.get(b))
                            .cloned()
                    });
                if let Some(tmpl) = tmpl_opt {
                    let args: Vec<Value> = tmpl
                        .params
                        .iter()
                        .filter_map(|p| pv.get(&p.name).cloned())
                        .collect();
                    // Only auto-call when every declared parameter is satisfied.
                    if args.len() == tmpl.params.len() {
                        let leaf = runtime.instantiate_template(&tmpl, args)?;
                        return inline_value_as_template_fragment(runtime, &leaf, Some(pv));
                    }
                }
            }
            Ok(s.clone())
        }
        Value::Leaf(leaf) => {
            // Check for a TemplateInstance produced by calling a template.
            let is_tmpl_instance = matches!(
                leaf.get("type"),
                Some(Value::String(t)) if t == TEMPLATE_INSTANCE_TYPE
            );
            if is_tmpl_instance && let Some(Value::String(instance_str)) = leaf.get("value") {
                return extract_tmpl_instance_body(instance_str).ok_or_else(|| {
                    RuntimeError(format!(
                        "failed to extract body from template instance: {instance_str}"
                    ))
                });
            }
            Ok(value_to_string(value))
        }
        Value::List(items) => {
            let mut buf = String::new();
            for item in items {
                buf.push_str(&inline_value_as_template_fragment(
                    runtime,
                    item,
                    param_values,
                )?);
            }
            Ok(buf)
        }
        _ => Ok(value_to_string(value)),
    }
}

/// Parse a template-instance string of the form
/// `lang [l] name(param1=val1, param2=val2) { body }`,
/// apply `${paramN}` substitution to the body, strip the outer `{ … }`,
/// and return the inner content.
///
/// Returns `None` if the string does not match the expected format.
fn extract_tmpl_instance_body(instance_str: &str) -> Option<String> {
    let (args, body_with_braces) = parse_template_instance_header(instance_str)?;

    // Strip the outer `{ … }`.
    let body_trimmed = body_with_braces.trim();
    let inner = if body_trimmed.starts_with('{') && body_trimmed.ends_with('}') {
        &body_trimmed[1..body_trimmed.len() - 1]
    } else {
        body_trimmed
    };

    // Apply ${param} substitution.
    let mut result = inner.to_string();
    for (key, val) in &args {
        result = result.replace(&format!("${{{key}}}"), val);
    }
    Some(result)
}

/// Parse the header of a template-instance string:
/// `lang [l] name(param1=val1, …) rest`
///
/// Returns `(args_map, rest_after_closing_paren)` or `None` on parse failure.
/// Argument values are extracted with a depth-aware comma split so that
/// values containing nested `[…]` or `{…}` (e.g. serialised lists) are
/// handled correctly.
fn parse_template_instance_header(s: &str) -> Option<(HashMap<String, String>, &str)> {
    let s = s.trim();
    // Skip "lang"
    let s = s.strip_prefix("lang")?.trim_start();
    // Skip "[lang_id]"
    let bracket_end = s.find(']')?;
    let s = s[bracket_end + 1..].trim_start();
    // Skip template name up to "("
    let paren_start = s.find('(')?;
    let s = &s[paren_start + 1..];
    // Find matching ")" with depth tracking.
    let mut depth = 1usize;
    let mut paren_end = None;
    for (i, ch) in s.char_indices() {
        match ch {
            '(' | '[' | '{' => depth += 1,
            ')' => {
                depth -= 1;
                if depth == 0 {
                    paren_end = Some(i);
                    break;
                }
            }
            ']' | '}' => {
                depth = depth.saturating_sub(1);
            }
            _ => {}
        }
    }
    let paren_end = paren_end?;
    let args_str = &s[..paren_end];
    let rest = s[paren_end + 1..].trim_start();
    Some((parse_args_depth_aware(args_str), rest))
}

/// Split `name=value, …` pairs respecting nested `(`, `[`, `{` delimiters,
/// and return a `HashMap<String, String>`.
fn parse_args_depth_aware(args_str: &str) -> HashMap<String, String> {
    let mut map = HashMap::new();
    let parts = split_call_args(args_str); // reuse depth-aware splitter
    for part in parts {
        let part = part.trim();
        if part.is_empty() {
            continue;
        }
        if let Some((k, v)) = part.split_once('=') {
            map.insert(k.trim().to_string(), v.trim().to_string());
        }
    }
    map
}

/// Split a comma-separated argument string while respecting nested `(`, `[`,
/// and `{` delimiters.
fn split_call_args(args_src: &str) -> Vec<&str> {
    let mut parts = Vec::new();
    let mut depth = 0usize;
    let mut last = 0usize;
    for (i, ch) in args_src.char_indices() {
        match ch {
            '(' | '[' | '{' => depth += 1,
            ')' | ']' | '}' => depth = depth.saturating_sub(1),
            ',' if depth == 0 => {
                parts.push(&args_src[last..i]);
                last = i + 1;
            }
            _ => {}
        }
    }
    if last < args_src.len() {
        parts.push(&args_src[last..]);
    }
    parts
}

/// Recursively insert `value` into `map` under the key `prefix`, and also
/// under every dotted sub-path for `SetInstance`, `Leaf`, and `Map` values.
///
/// For example, given `prefix = "compositor"` and a `SetInstance` that
/// contains `entity: SetInstance { pkg: "dev.tlang" }`, this inserts:
///   - `"compositor"`         → serialised whole value
///   - `"compositor.entity"`  → serialised entity value
///   - `"compositor.entity.pkg"` → `"dev.tlang"`
///
/// This allows `${compositor.entity.pkg}` interpolations in template `pkg`
/// directives (and elsewhere) to resolve correctly via the ordinary `substitute`
/// function without requiring a separate runtime-value lookup.
fn flatten_value_to_string_map(prefix: &str, value: &Value, map: &mut HashMap<String, String>) {
    map.insert(prefix.to_string(), value_to_string(value));
    match value {
        Value::SetInstance(inst) => {
            for (k, v) in &inst.impls {
                flatten_value_to_string_map(&format!("{prefix}.{k}"), v, map);
            }
        }
        Value::Leaf(leaf) => {
            for (k, v) in leaf.fields.iter() {
                flatten_value_to_string_map(&format!("{prefix}.{k}"), v, map);
            }
        }
        Value::Map(m) => {
            for (k, v) in m {
                flatten_value_to_string_map(&format!("{prefix}.{k}"), v, map);
            }
        }
        _ => {}
    }
}

pub(crate) fn value_to_string(value: &Value) -> String {
    match value {
        Value::Int(v) => v.to_string(),
        Value::Float(v) => display_float(*v),
        Value::Bool(v) => v.to_string(),
        Value::String(v) => v.clone(),
        Value::StringBuilder(buf) => buf.borrow().clone(),
        Value::Leaf(leaf) => {
            let fields = leaf
                .fields
                .iter()
                .map(|(key, value)| format!("{key}: {}", value_to_string(value)))
                .collect::<Vec<_>>()
                .join(", ");
            format!("{{{fields}}}")
        }
        Value::List(items) => {
            let inner = items
                .iter()
                .map(value_to_string)
                .collect::<Vec<_>>()
                .join(", ");
            format!("[{inner}]")
        }
        Value::Map(map) => {
            let entries = map
                .iter()
                .map(|(k, v)| format!("{k}: {}", value_to_string(v)))
                .collect::<Vec<_>>()
                .join(", ");
            format!("{{{entries}}}")
        }
        Value::Unit => String::new(),
        Value::SetInstance(inst) => {
            let fields: Vec<String> = inst
                .impls
                .iter()
                .map(|(k, v)| format!("{k}: {}", value_to_string(v)))
                .collect();
            format!("{}({})", inst.entity_name, fields.join(", "))
        }
        Value::BoundAttr(bound) => {
            format!("{}::{}", bound.instance.entity_name, bound.attr_name)
        }
        Value::StringBuilder(buf) => buf.borrow().clone(),
        Value::Lambda(lambda) => {
            format!("({}) => <lambda>", lambda.params.join(", "))
        }
        Value::PdfDoc(_) => "<PdfDoc>".to_string(),
    }
}

pub(crate) fn value_type_name(value: &Value) -> &'static str {
    match value {
        Value::Int(_) => "Int",
        Value::Float(_) => "Float",
        Value::Bool(_) => "Bool",
        Value::String(_) => "String",
        Value::Leaf(_) => "Leaf",
        Value::List(_) => "List",
        Value::Map(_) => "Map",
        Value::Unit => "Unit",
        Value::SetInstance(_) => "SetInstance",
        Value::BoundAttr(_) => "BoundAttr",
        Value::StringBuilder(_) => "StringBuilder",
        Value::Lambda(_) => "Func",
        Value::PdfDoc(_) => "PdfDoc",
    }
}

fn value_matches_type_name(value: &Value, type_name: &str) -> bool {
    let normalized = type_name.trim();
    match normalized {
        "Int" | "Long" | "Number" => matches!(value, Value::Int(_)),
        "Float" | "Double" => matches!(value, Value::Float(_)),
        "Bool" | "Boolean" => matches!(value, Value::Bool(_)),
        "String" => matches!(value, Value::String(_)),
        "Leaf" => matches!(value, Value::Leaf(_)),
        "List" => matches!(value, Value::List(_)),
        "Map" => matches!(value, Value::Map(_)),
        "Unit" | "Void" => matches!(value, Value::Unit),
        "SetInstance" => matches!(value, Value::SetInstance(_)),
        "BoundAttr" => matches!(value, Value::BoundAttr(_)),
        "StringBuilder" => matches!(value, Value::StringBuilder(_)),
        "PdfDoc" => matches!(value, Value::PdfDoc(_)),
        "Func" | "Function" | "Callable" => {
            // Only lambda values are semantically `Func`; `&funcName` strings
            // are callable at runtime but reported as `String` via `typeof`.
            matches!(value, Value::Lambda(_))
        }
        _ => matches!(value, Value::SetInstance(inst) if inst.entity_name == normalized),
    }
}

/// A single call-frame on the runtime's value stack.
///
/// Each function invocation pushes a new `Frame`; variable bindings
/// introduced by `let` statements or function parameters are stored here
/// and discarded when the frame is popped at the end of the call.
struct Frame {
    bindings: HashMap<String, Value>,
}

impl<'a> Runtime<'a> {
    /// Look up a `set` entity by name in the runtime's live model tree.
    fn find_set_entity(&self, entity_name: &str) -> Option<&crate::model_tree::ModelSetEntityTree> {
        find_set_entity(&self.model, entity_name)
    }
}

mod helper_parser;
use helper_parser::{HelperParser, ChainSegment, parse_chain_path};
mod type_checker;

#[cfg(test)]
mod tests {
    use std::{
        env, fs,
        time::{SystemTime, UNIX_EPOCH},
    };

    use crate::model_tree::{ModelNodeTree, ModelValueTypeTree};
    use crate::{load_program, parse_domain_model};

    use super::{CompiledProgram, Value, compile_from_domain_model, run_main};

    #[test]
    fn runs_hello_world_from_helper() {
        let model = parse_domain_model(
            r#"
            use TLang.Terminal

            func main(): String {
                let message = "Hello world";
                Terminal.println(message);
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.output, "Hello world\n");
        assert_eq!(run.return_value, Value::Unit);
    }

    #[test]
    fn supports_fully_qualified_terminal_calls() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                TLang.Terminal.println("Hello again");
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.output, "Hello again\n");
        assert_eq!(run.return_value, Value::Unit);
    }

    #[test]
    fn supports_file_library_read_and_write() {
        let path = temp_file_path("file-library");
        let escaped_path = tlang_string_literal(&path);
        let model = parse_domain_model(&format!(
            r#"
            use TLang.File

            func main(): String {{
                File.write("{escaped_path}", "Hello from file");
                return File.read("{escaped_path}");
            }}
        "#
        ))
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(
            run.return_value,
            Value::String("Hello from file".to_string())
        );
        assert_eq!(
            fs::read_to_string(&path).expect("file should exist"),
            "Hello from file"
        );

        let _ = fs::remove_file(path);
    }

    #[test]
    fn supports_string_builder_library_with_shared_ref_appends() {
        // TLang.StringBuilder.append appends in-place and returns the SAME
        // Rc<RefCell<String>> reference.  All aliases (`builder`, `once`,
        // `twice`) therefore share the same underlying buffer, so every
        // `build` call reflects the final accumulated string.
        let model = parse_domain_model(
            r#"
            use TLang.StringBuilder
            use TLang.Terminal

            func main(): String {
                let builder = StringBuilder.create();
                let once = StringBuilder.append(builder, "Hello");
                let twice = StringBuilder.append(once, " World");
                Terminal.println(StringBuilder.build(once));
                Terminal.println(StringBuilder.build(twice));
                return StringBuilder.build(twice);
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        // All three names share the same buffer; after both appends the buffer
        // holds "Hello World", so both println calls print "Hello World".
        assert_eq!(run.output, "Hello World\nHello World\n");
        assert_eq!(run.return_value, Value::String("Hello World".to_string()));
    }

    #[test]
    fn supports_leaf_library_browsing_instantiated_model() {
        let model = parse_domain_model(
            r#"
            use TLang.Leaf
            use RustGen as rust
            use TLang.Terminal

            func main(): String {
                service("User");
                let model = Leaf.model();
                let service_leaf = Leaf.get(model, "service_1");
                let value = Leaf.get(service_leaf, "value");
                Terminal.println(value);
                return value;
            }

            lang [rust] service(name: String) {
                impl Service {
                    name: String
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        let expected = "lang [rust] service(name=User) {\n                impl Service {\n                    name: String\n                }\n            }";
        assert_eq!(run.output, format!("{expected}\n"));
        assert_eq!(run.return_value, Value::String(expected.to_string()));
    }

    #[test]
    fn supports_generator_library_with_tlang_java_generator() {
        let model = parse_domain_model(
            r#"
            use TLang.Generator
            use JavaGen as java
            use TLang.Leaf
            use TLang.StringBuilder
            expose generate_java

            func generate_java(tmpl) {
                let name = Leaf.get(tmpl, "name");
                let source = Leaf.get(tmpl, "value");
                let builder = StringBuilder.create();
                let with_prefix = StringBuilder.append(builder, "public class ");
                let with_name = StringBuilder.append(with_prefix, name);
                let with_suffix = StringBuilder.append(with_name, " {} // ");
                let with_source = StringBuilder.append(with_suffix, source);
                return StringBuilder.build(with_source);
            }

            func main(): String {
                service("User");
                let model = Leaf.model();
                let tmpl = Leaf.get(model, "service_1");
                return Generator.generate(tmpl, "java");
            }

            lang [java] service(name: String) {
                impl Service {
                    name: String
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        let Value::String(generated) = run.return_value else {
            panic!("expected generated java code as string");
        };
        let class_idx = generated
            .find("public class service_1 {}")
            .expect("generated class header should be present");
        let source_idx = generated
            .find("lang [java] service(name=User)")
            .expect("generated template source should be present");
        assert!(class_idx < source_idx);
    }

    #[test]
    fn supports_generator_with_tokens_and_formatting_rules() {
        let model = parse_domain_model(
            r#"
            use TLang.Formatting
            use TLang.Generator
            use JavaGen as java
            use TLang.Leaf
            use TLang.Token

            func generate_java(tmpl, rules) {
                let public_kw = Token.keyword("public", "public");
                let final_kw = Token.keyword("final", "final");
                let class_kw = Token.keyword("class", "class");
                let class_name = Token.keyword("name", Leaf.get(tmpl, "name"));
                let open_brace = Token.keyword("open_brace", "{");
                let close_brace = Token.keyword("close_brace", "}");
                return Formatting.render(
                    rules,
                    public_kw,
                    final_kw,
                    class_kw,
                    class_name,
                    open_brace,
                    close_brace
                );
            }

            func main(): String {
                service("User");
                let model = Leaf.model();
                let tmpl = Leaf.get(model, "service_1");
                let rules = Formatting.create();
                let rules = Formatting.space_between(rules, "public", "final");
                let rules = Formatting.space_between(rules, "final", "class");
                let rules = Formatting.space_between(rules, "class", "name");
                let rules = Formatting.space_between(rules, "name", "open_brace");
                let rules = Formatting.newline_after(rules, "open_brace");
                let rules = Formatting.indent_after(rules, "open_brace");
                let rules = Formatting.outdent_before(rules, "close_brace");
                return Generator.generate(tmpl, "java", rules);
            }

            lang [java] service(name: String) {
                impl Service {
                    name: String
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(
            run.return_value,
            Value::String("public final class service_1 {\n}".to_string())
        );
    }

    #[test]
    fn runs_java_generator_example_from_generators_folder() {
        let root = std::path::Path::new(env!("CARGO_MANIFEST_DIR"));
        let example_path = root.join("generators").join("java").join("Example.tlang");
        let model = load_program(&example_path).expect("example should load");

        let compiled = compile_from_domain_model(&model).expect("example should compile");
        let run = run_main(&compiled).expect("example should run");

        assert_eq!(
            run.return_value,
            Value::String(
                "package com.example.demo;\n\npublic final class Application {\n    private static final String VERSION = \"1.0.0\";\n    public static void main(String[] args) {\n        System.out.println(\"Starting application v\" + VERSION);\n    }\n}"
                    .to_string()
            )
        );
        assert!(run.output.contains("public final class Application"));
        assert!(run.output.contains("public interface Serializable"));
        assert!(run.output.contains("public abstract class Auditable"));
        assert!(run.output.contains("public enum Role"));
    }

    #[test]
    fn supports_calls_math_and_if_then() {
        // Updated to use the ANTLR4-conformant `if (cond) { }` syntax with
        // infix `==` instead of the old prefix `eq(a, b)` + `then` keyword.
        let model = parse_domain_model(
            r#"
            func double(value) {
                return value * 2;
            }

            func main(): String {
                let start = 21;
                let result = double(start);
                if (result == 42) {
                    TLang.Terminal.println("ok");
                }
                return result;
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.output, "ok\n");
        assert_eq!(run.return_value, Value::Int(42));
    }

    #[test]
    fn supports_optional_chain_and_null_coalesce_for_nested_properties() {
        let model = parse_domain_model(
            r#"
            use TLang.Map

            func none() {
            }

            func main(): String {
                let address = Map.create();
                let address = Map.set(address, "city", "Paris");
                let user = Map.create();
                let user = Map.set(user, "address", address);

                let city = user?.address?.city ?? "Unknown";
                let missing = none();
                let fallback = missing?.address?.city ?? "Anonymous";
                return city + ":" + fallback;
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(
            run.return_value,
            Value::String("Paris:Anonymous".to_string())
        );
    }

    #[test]
    fn supports_optional_chain_with_method_call() {
        let model = parse_domain_model(
            r#"
            use TLang.Map

            func none() {
            }

            func main(): Int {
                let profile = Map.create();
                let profile = Map.set(profile, "name", "Alice");
                let user = Map.create();
                let user = Map.set(user, "profile", profile);

                let present_len = user?.profile?.name?.length() ?? 0;
                let missing = none();
                let missing_len = missing?.profile?.name?.length() ?? 0;
                return present_len + missing_len;
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.return_value, Value::Int(5));
    }

    #[test]
    fn null_coalesce_has_lower_precedence_than_arithmetic() {
        let model = parse_domain_model(
            r#"
            func none() {
            }

            func main(): Int {
                let value = none() ?? 1 + 2 * 3;
                return value;
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.return_value, Value::Int(7));
    }

    #[test]
    fn supports_expression_if_in_let_and_return() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let n = 0;
                let label = if (n < 0) "negative" else if (n == 0) "zero" else "positive";
                return if (label == "zero") "ok" else "bad";
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.return_value, Value::String("ok".to_string()));
    }

    #[test]
    fn supports_expression_match_in_let_and_return() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let n = 2;
                let label = match (n) {
                    case 1 => "one",
                    case 2 => "two",
                    default => "other",
                };
                return match (label) {
                    case "two" => "ok",
                    default => "bad",
                };
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.return_value, Value::String("ok".to_string()));
    }

    #[test]
    fn bytecode_roundtrip_supports_expression_if_and_match() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let category = if (2 > 1) "small" else "large";
                return match (category) {
                    case "small" => "ok",
                    default => "bad",
                };
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let bytes = compiled.encode();
        let restored = CompiledProgram::decode(&bytes).expect("bytecode should decode");
        let run = run_main(&restored).expect("decoded program should run");

        assert_eq!(run.return_value, Value::String("ok".to_string()));
    }

    #[test]
    fn if_else_executes_else_branch_when_condition_is_false() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let x = 10;
                if (x > 20) {
                    TLang.Terminal.println("big");
                } else {
                    TLang.Terminal.println("small");
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "small\n");
    }

    #[test]
    fn if_else_executes_then_branch_when_condition_is_true() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let x = 30;
                if (x > 20) {
                    TLang.Terminal.println("big");
                } else {
                    TLang.Terminal.println("small");
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "big\n");
    }

    #[test]
    fn if_else_can_return_value_from_branch() {
        let model = parse_domain_model(
            r#"
            func classify(n) {
                if (n >= 0) {
                    return 1;
                } else {
                    return -1;
                }
            }

            func main(): String {
                return classify(-5);
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(-1));
    }

    #[test]
    fn if_supports_compound_boolean_with_and() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let a = 5;
                let b = 10;
                if ((a < 10) && (b > 5)) {
                    TLang.Terminal.println("yes");
                } else {
                    TLang.Terminal.println("no");
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "yes\n");
    }

    #[test]
    fn if_supports_compound_boolean_with_or() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let a = 5;
                let b = 3;
                if ((a > 10) || (b > 2)) {
                    TLang.Terminal.println("yes");
                } else {
                    TLang.Terminal.println("no");
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "yes\n");
    }

    #[test]
    fn if_supports_not_equal_operator() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let x = 5;
                if (x != 10) {
                    TLang.Terminal.println("different");
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "different\n");
    }

    #[test]
    fn if_supports_is_and_not_is_operators() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let name = "Alice";
                if (name is String) {
                    TLang.Terminal.println("string");
                }
                if (name !is Int) {
                    TLang.Terminal.println("not-int");
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "string\nnot-int\n");
    }

    #[test]
    fn match_supports_value_type_and_condition_cases() {
        let model = parse_domain_model(
            r#"
            func classify(value): String {
                match(value) {
                    case "myVal" => return "value";
                    case is String => return "string";
                    case _ != "thisVal" and _ != "thatVal" => return "other";
                    default => return "default";
                }
            }

            func main(): String {
                TLang.Terminal.println(classify("myVal"));
                TLang.Terminal.println(classify("zzz"));
                TLang.Terminal.println(classify("thisVal"));
                return "done";
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "value\nstring\nstring\n");
        assert_eq!(run.return_value, Value::String("done".to_string()));
    }

    #[test]
    fn match_uses_default_when_no_case_matches() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let value = 42;
                match(value) {
                    case "myVal" => return "value";
                    case is String => return "string";
                    case _ != 42 and _ != 100 => return "other";
                    default => return "default";
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::String("default".to_string()));
    }

    #[test]
    fn match_condition_supports_or_keyword_alias() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let value = "thisVal";
                match(value) {
                    case _ == "thisVal" or _ == "thatVal" => return "picked";
                    default => return "default";
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::String("picked".to_string()));
    }

    #[test]
    fn match_condition_supports_and_keyword_alias() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let value = "ok";
                match(value) {
                    case _ == "ok" and _ != "no" => return "picked";
                    default => return "default";
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::String("picked".to_string()));
    }

    #[test]
    fn match_case_is_supports_set_entity_instances() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let p = Person(name: "Alice");
                match(p) {
                    case is Person => TLang.Terminal.println("person");
                    default => TLang.Terminal.println("other");
                }
                return "done";
            }

            set Person(name: String) {
                name: "unknown"
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "person\n");
    }

    #[test]
    fn for_in_iterates_over_list_literal() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                for (item in ["a", "b", "c"]) {
                    TLang.Terminal.println(item);
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "a\nb\nc\n");
    }

    #[test]
    fn for_in_iterates_over_list_variable() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let names = ["Alice", "Bob"];
                let result = "";
                for (name in names) {
                    let result = result + name;
                }
                TLang.Terminal.println(result);
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "AliceBob\n");
    }

    #[test]
    fn for_to_range_is_inclusive() {
        // `for (i 1 to 5)` should iterate i = 1, 2, 3, 4, 5
        let model = parse_domain_model(
            r#"
            func main(): Int {
                let sum = 0;
                for (i 1 to 5) {
                    let sum = sum + i;
                }
                return sum;
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(15)); // 1+2+3+4+5
    }

    #[test]
    fn for_until_range_is_exclusive() {
        // `for (i 0 until 5)` should iterate i = 0, 1, 2, 3, 4  (5 iterations)
        let model = parse_domain_model(
            r#"
            func main(): Int {
                let count = 0;
                for (i 0 until 5) {
                    let count = count + 1;
                }
                return count;
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(5));
    }

    #[test]
    fn for_in_with_conditional_body() {
        // Filter-like pattern: print only even numbers from a list
        let model = parse_domain_model(
            r#"
            func main(): String {
                for (n in [1, 2, 3, 4, 5, 6]) {
                    if (n % 2 == 0) {
                        TLang.Terminal.println(n);
                    }
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "2\n4\n6\n");
    }

    #[test]
    fn for_range_prints_each_iteration() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                for (i 1 to 3) {
                    TLang.Terminal.println(i);
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "1\n2\n3\n");
    }

    #[test]
    fn infix_arithmetic_operators_work() {
        let model = parse_domain_model(
            r#"
            func main(): Int {
                let a = 10 + 5;
                let b = 10 - 3;
                let c = 4 * 3;
                let d = 10 / 2;
                let e = 10 % 3;
                return e;
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(1)); // 10 % 3 == 1
    }

    #[test]
    fn infix_string_concatenation_with_plus() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let greeting = "Hello, " + "World!";
                return greeting;
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::String("Hello, World!".to_string()));
    }

    #[test]
    fn comparison_operators_lte_gte() {
        let model = parse_domain_model(
            r#"
            func check(n) {
                if (n <= 5) {
                    return 0;
                }
                if (n >= 10) {
                    return 2;
                }
                return 1;
            }

            func main(): String {
                return check(12);
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(2));
    }

    #[test]
    fn function_returns_shadowed_variable_value() {
        let model = parse_domain_model(
            r#"
            func mutate(v) {
                let v = v + 1;
                return v;
            }

            func main(): String {
                let original = 5;
                let updated = mutate(original);
                return updated;
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.return_value, Value::Int(6));
    }

    #[test]
    fn immutable_variables_keep_original_value() {
        let model = parse_domain_model(
            r#"
            func mutate(v) {
                let v = v + 1;
                return v;
            }

            func main(): Int {
                let original = 5;
                let updated = mutate(original);
                return original;
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("helper should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.return_value, Value::Int(5));
    }

    #[test]
    fn compile_from_domain_model_keeps_static_model_tree() {
        let model = parse_domain_model(
            r#"
            func main(): Int {
                return 1;
            }

            let root: Number = 42
            set Service(name: String) {
                id: Number
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");

        assert_eq!(compiled.model_tree().nodes.len(), 2);
        assert!(matches!(
            compiled.model_tree().nodes[0],
            ModelNodeTree::AssignVar(_)
        ));
        assert!(matches!(
            compiled.model_tree().nodes[1],
            ModelNodeTree::SetEntity(_)
        ));
    }

    #[test]
    fn tmpl_call_instantiates_template_model_nodes() {
        let model = parse_domain_model(
            r#"
            use RustGen as rust

            func main(): String {
                service("User");
                service("Admin");
            }

            lang [rust] service(name: String) {
                impl Service {
                    name: String
                }
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.model_tree.nodes.len(), 2);
        match &run.model_tree.nodes[0] {
            ModelNodeTree::AssignVar(assign) => {
                assert_eq!(assign.name, "service_1");
                assert_eq!(
                    assign.ty,
                    Some(ModelValueTypeTree::Type("TemplateInstance".to_string()))
                );
                assert!(assign.value.contains("lang [rust] service(name=User)"));
            }
            _ => panic!("expected template instance assignment"),
        }
        match &run.model_tree.nodes[1] {
            ModelNodeTree::AssignVar(assign) => {
                assert_eq!(assign.name, "service_2");
                assert!(assign.value.contains("lang [rust] service(name=Admin)"));
            }
            _ => panic!("expected template instance assignment"),
        }
    }

    #[test]
    fn tmpl_call_preserves_static_model_nodes() {
        let model = parse_domain_model(
            r#"
            use GoGen as go

            func main(): String {
                service("User");
            }

            let seed: Number = 42

            lang [go] service(name: String) {
                func build() {}
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.model_tree.nodes.len(), 2);
        assert!(matches!(
            run.model_tree.nodes[0],
            ModelNodeTree::AssignVar(_)
        ));
        match &run.model_tree.nodes[1] {
            ModelNodeTree::AssignVar(assign) => {
                assert_eq!(assign.name, "service_1");
                assert!(assign.value.contains("lang [go] service(name=User)"));
            }
            _ => panic!("expected template instance assignment"),
        }
    }

    #[test]
    fn tmpl_call_requires_expected_arguments() {
        let model = parse_domain_model(
            r#"
            use RustGen as rust

            func main(): String {
                service();
            }

            lang [rust] service(name: String) {
                func build() {}
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let err = run_main(&compiled).expect_err("main should fail");

        assert!(
            err.to_string()
                .contains("template `service` expects 1 arguments, got 0")
        );
    }

    fn temp_file_path(prefix: &str) -> String {
        let unique = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .expect("time should be after epoch")
            .as_nanos();
        env::temp_dir()
            .join(format!("tlang-{prefix}-{unique}.txt"))
            .display()
            .to_string()
    }

    fn tlang_string_literal(value: &str) -> String {
        value.replace('\\', "\\\\").replace('"', "\\\"")
    }

    #[test]
    fn model_block_supports_string_literal_values() {
        let model = parse_domain_model(
            r#"
            use TLang.Leaf
            use TLang.Terminal

            func main(): String {
                let model = Leaf.model();
                let entity = Leaf.get(model, "MyEntity");
                let attrs = Leaf.get(entity, "attrs");
                let pkg_attr = Leaf.get(attrs, "package");
                let pkg = Leaf.get(pkg_attr, "value");
                Terminal.println(pkg);
                return pkg;
            }

            set MyEntity {
                package: "com.example.myapp",
                class_name: "MyEntity"
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(
            run.return_value,
            Value::String("com.example.myapp".to_string())
        );
        assert_eq!(run.output, "com.example.myapp\n");
    }

    #[test]
    fn model_string_literal_unescapes_escape_sequences() {
        let model = parse_domain_model(
            r#"
            use TLang.Leaf
            use TLang.Terminal

            func main(): String {
                let model = Leaf.model();
                let entity = Leaf.get(model, "Cfg");
                let attrs = Leaf.get(entity, "attrs");
                let body_attr = Leaf.get(attrs, "body");
                let body = Leaf.get(body_attr, "value");
                Terminal.println(body);
                return body;
            }

            set Cfg {
                body: "line1\nline2\n"
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(
            run.return_value,
            Value::String("line1\nline2\n".to_string())
        );
    }

    #[test]
    fn generates_kotlin_class_from_model_entity() {
        // Demonstrates the template-DSL approach: the class structure is
        // represented with a `lang [kotlin]` template; the model supplies
        // the instance parameters (package, class name, …).
        let model = parse_domain_model(
            r#"
            use TLang.Generator
            use KotlinGen as kotlin
            use TLang.Leaf
            use TLang.Terminal

            lang [kotlin] serviceClass(pkg: String, className: String, extendsClass: String, annotation: String) {
                pkg ${pkg}
                impl[public class] ${className} {
                }
            }

            func main(): String {
                let model = Leaf.model();
                let entity = Leaf.get(model, "Service");
                let attrs = Leaf.get(entity, "attrs");
                let pkg = Leaf.get(Leaf.get(attrs, "package"), "value");
                let cls = Leaf.get(Leaf.get(attrs, "class_name"), "value");
                let ext = Leaf.get(Leaf.get(attrs, "extends"), "value");
                let ann = Leaf.get(Leaf.get(attrs, "annotation"), "value");
                serviceClass(pkg, cls, ext, ann);
                let model2 = Leaf.model();
                let instance = Leaf.get(model2, "serviceClass_1");
                let generated = Generator.generate(instance, "kotlin");
                Terminal.println(generated);
                return generated;
            }

            set Service {
                package:    "com.example.service",
                class_name: "UserService",
                extends:    "BaseService",
                annotation: "@Service"
            }
        "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        let expected =
            "package com.example.service\n\n@Service\nclass UserService : BaseService {\n}";
        assert_eq!(run.return_value, Value::String(expected.to_string()));
        assert_eq!(run.output, format!("{expected}\n"));
    }

    #[test]
    fn generates_kotlin_jpa_entity_for_bookmark() {
        // The Bookmark JPA entity is now represented as a lang [kotlin] template.
        // The class structure (annotated fields) is described with template DSL
        // nodes; the model supplies the instance parameters (package, class name,
        // extends, annotation).  The Kotlin generator walks the template tree
        // and emits proper Kotlin source.
        let source = r#"
            use TLang.Generator
            use KotlinGen as kotlin
            use TLang.Leaf
            use TLang.Terminal

            lang [kotlin] bookmark(pkg: String, className: String, extendsClass: String, annotation: String) {
                pkg ${pkg}
                impl[public class] ${className} {
                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    var id: Long? = null
                    @Column(unique = true, nullable = false)
                    var code: String = UUID.randomUUID().toString()
                    @ManyToOne(fetch = FetchType.LAZY)
                    @JoinColumn(name = "user_id", nullable = false)
                    lateinit var user: User
                    @Column(nullable = false, length = 300)
                    lateinit var title: String
                    @Column(nullable = false, length = 2000)
                    lateinit var url: String
                    @Column(length = 1000)
                    var description: String? = null
                    @Column(name = "create_date", nullable = false)
                    var createdDate: LocalDateTime? = null
                    @Column
                    var favorite: Boolean = false
                }
            }

            func main(): String {
                let model = Leaf.model();
                let entity = Leaf.get(model, "Bookmark");
                let attrs = Leaf.get(entity, "attrs");
                let pkg = Leaf.get(Leaf.get(attrs, "package"), "value");
                let cls = Leaf.get(Leaf.get(attrs, "class_name"), "value");
                let ext = Leaf.get(Leaf.get(attrs, "extends"), "value");
                let ann = Leaf.get(Leaf.get(attrs, "annotation"), "value");
                bookmark(pkg, cls, ext, ann);
                let model2 = Leaf.model();
                let instance = Leaf.get(model2, "bookmark_1");
                let generated = Generator.generate(instance, "kotlin");
                Terminal.println(generated);
                return generated;
            }

            set Bookmark {
                package:    "com.colistor.web.bookmark",
                class_name: "Bookmark",
                extends:    "PanacheEntityBase",
                annotation: "@Entity"
            }
        "#;

        let model = parse_domain_model(source).expect("domain model should parse");
        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        // Verify key structural elements are present in the generated output.
        // The template-based generator does not preserve blank lines between field
        // groups (template items are whitespace-separated), so we use contains
        // checks rather than a full string equality assertion.
        let generated = match &run.return_value {
            Value::String(s) => s.clone(),
            other => panic!("expected String return value, got {other:?}"),
        };
        assert!(
            generated.starts_with("package com.colistor.web.bookmark"),
            "package declaration missing:\n{generated}"
        );
        assert!(
            generated.contains("@Entity\nclass Bookmark : PanacheEntityBase {"),
            "class header missing:\n{generated}"
        );
        assert!(generated.contains("@Id"), "missing @Id:\n{generated}");
        assert!(
            generated.contains("@GeneratedValue(strategy = GenerationType.IDENTITY)"),
            "missing @GeneratedValue:\n{generated}"
        );
        assert!(
            generated.contains("var id: Long? = null"),
            "missing id field:\n{generated}"
        );
        assert!(
            generated.contains("var code: String = UUID.randomUUID().toString()"),
            "missing code field:\n{generated}"
        );
        assert!(
            generated.contains("@JoinColumn(name = \"user_id\", nullable = false)"),
            "missing @JoinColumn:\n{generated}"
        );
        assert!(
            generated.contains("lateinit var user: User"),
            "missing user field:\n{generated}"
        );
        assert!(
            generated.contains("lateinit var title: String"),
            "missing title field:\n{generated}"
        );
        assert!(
            generated.contains("var description: String? = null"),
            "missing description field:\n{generated}"
        );
        assert!(
            generated.contains("var createdDate: LocalDateTime? = null"),
            "missing createdDate field:\n{generated}"
        );
        assert!(
            generated.contains("var favorite: Boolean = false"),
            "missing favorite field:\n{generated}"
        );
        assert!(
            generated.ends_with('}'),
            "expected closing brace at end:\n{generated}"
        );
        assert_eq!(
            run.output,
            format!("{generated}\n"),
            "stdout should be generated source + newline"
        );
    }

    #[test]
    fn runs_kotlin_generator_example_from_generators_folder() {
        let root = std::path::Path::new(env!("CARGO_MANIFEST_DIR"));
        let example_path = root.join("generators").join("kotlin").join("Example.tlang");
        let model = load_program(&example_path).expect("example should load");

        let compiled = compile_from_domain_model(&model).expect("example should compile");
        let run = run_main(&compiled).expect("example should run");

        let generated = match &run.return_value {
            Value::String(s) => s.clone(),
            _ => panic!("expected string return value"),
        };

        // Verify the full class structure.
        assert!(generated.starts_with("package com.colistor.web.bookmark"));
        assert!(generated.contains("@Entity"));
        assert!(generated.contains("class Bookmark : PanacheEntityBase {"));
        // The Example.tlang includes all Bookmark fields in the body.
        assert!(generated.contains("var id: Long? = null"));
        assert!(generated.contains("var code: String = UUID.randomUUID().toString()"));
        assert!(generated.contains("lateinit var user: User"));
        assert!(generated.contains("lateinit var title: String"));
        assert!(generated.contains("var description: String? = null"));
        assert!(generated.contains("var favorite: Boolean = false"));
        assert!(generated.ends_with('}'));
        assert_eq!(run.output, format!("{generated}\n"));
    }

    #[test]
    fn model_entity_attribute_access_via_dot_notation() {
        let model = parse_domain_model(
            r#"
            use TLang.Terminal

            func main(): String {
                let pkg = HelloWorld.package;
                Terminal.println(pkg);
                return pkg;
            }

            set HelloWorld {
                package: "com.example"
            }
            "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.return_value, Value::String("com.example".to_string()));
        assert_eq!(run.output, "com.example\n");
    }

    #[test]
    fn model_entity_attribute_used_directly_in_template_call() {
        let model = parse_domain_model(
            r#"
            use TLang.Terminal

            func main(): String {
                let greeting = MyConfig.message;
                Terminal.println(greeting);
                return greeting;
            }

            set MyConfig {
                message: "Hello, World!"
            }
            "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        assert_eq!(run.return_value, Value::String("Hello, World!".to_string()));
    }

    #[test]
    fn template_call_returns_leaf_for_direct_generate() {
        // Generator.generate(helloWorld(pkg)) — 1-arg form where the lang is
        // derived from the `lang [kotlin]` declaration on the template.
        let model = parse_domain_model(
            r#"
            use TLang.Generator
            use KotlinGen as kotlin
            use TLang.Terminal

            lang [kotlin] simpleClass(pkg: String) {
                pkg ${pkg}
                impl[public class] Main {
                }
            }

            func main(): String {
                let output = Generator.generate(simpleClass("com.example"));
                Terminal.println(output);
                return output;
            }

            "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        let expected = "package com.example

class Main {
}";
        assert_eq!(run.return_value, Value::String(expected.to_string()));
    }

    #[test]
    fn alias_clash_check_rejects_let_binding_named_after_alias() {
        let model = parse_domain_model(
            r#"
            use KotlinGen.KotlinGenerator as kotlin

            func main(): String {
                let kotlin = "oops";
            }

            "#,
        )
        .expect("domain model should parse");

        let err = compile_from_domain_model(&model)
            .expect_err("should fail when let shadows an import alias");
        assert!(
            err.0.contains("kotlin") && err.0.contains("shadows"),
            "unexpected error: {}",
            err.0
        );
    }

    #[test]
    fn init_example_works_end_to_end_with_new_syntax() {
        // Reproduces the init-template helper using the new shorthand:
        //   - HelloWorld.package for model attribute access
        //   - Generator.generate(helloWorld(pkg)) 1-arg form
        //   - use KotlinGen.KotlinGenerator as kotlin  (lang [kotlin] matches)
        let model = parse_domain_model(
            r#"
            use TLang.Generator
            use KotlinGen as kotlin
            use TLang.File

            lang [kotlin] helloWorld(pkg: String) {
                pkg ${pkg}
                impl[public class] Main {
                    fun main(args: Array<String>) {
                    }
                }
            }

            func main(): String {
                let pkg    = HelloWorld.package;
                let output = Generator.generate(helloWorld(pkg));
                return output;
            }

            set HelloWorld {
                package: "com.example"
            }
            "#,
        )
        .expect("domain model should parse");

        let compiled = compile_from_domain_model(&model).expect("domain should compile");
        let run = run_main(&compiled).expect("main should run");

        let generated = match &run.return_value {
            Value::String(s) => s.clone(),
            other => panic!("expected String, got {other:?}"),
        };
        assert!(
            generated.contains("com.example"),
            "missing package: {generated}"
        );
        assert!(
            generated.contains("class Main"),
            "missing class: {generated}"
        );
    }

    // -----------------------------------------------------------------------
    // SetInstance — unit tests
    // -----------------------------------------------------------------------

    fn run_set_instance_program(src: &str) -> super::RunResult {
        let model = parse_domain_model(src).expect("domain model should parse");
        let compiled = compile_from_domain_model(&model).expect("domain model should compile");
        run_main(&compiled).expect("main should run")
    }

    /// Basic instantiation: reading impl values back from the instance.
    #[test]
    fn set_instance_reads_impl_values() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set Person(name: String, age: String) {
                kind: "person"
            }
            func main(): String {
                let p = Person(name: "Alice", age: "30")
                Terminal.println(p.name)
                Terminal.println(p.age)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "Alice\n30\n");
        assert_eq!(run.return_value, Value::String("ok".to_string()));
    }

    /// Instance can read a body attribute (not from impls).
    #[test]
    fn set_instance_reads_body_attr() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set Widget(id: String) {
                category: "ui-widget"
            }
            func main(): String {
                let w = Widget(id: "btn-1")
                Terminal.println(w.category)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "ui-widget\n");
        assert_eq!(run.return_value, Value::String("ok".to_string()));
    }

    /// Static attribute access still works for StringLiteral attrs.
    #[test]
    fn set_static_attr_access() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set AppConfig {
                version: "2.0",
                env: "production"
            }
            func main(): String {
                let v = AppConfig.version
                let e = AppConfig.env
                Terminal.println(v)
                Terminal.println(e)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "2.0\nproduction\n");
    }

    /// Ref attribute: calling `instance.refAttr(args)` dispatches to the
    /// referenced function with impl values prepended.
    #[test]
    fn set_instance_ref_dispatch() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set Greeter(prefix: String) {
                greet: &doGreet
            }
            func doGreet(prefix: String, name: String): String {
                return prefix + " " + name
            }
            func main(): String {
                let g = Greeter(prefix: "Hello")
                let msg = g.greet("World")
                Terminal.println(msg)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "Hello World\n");
    }

    /// FuncDef constructor param: the impl value is the function name string;
    /// calling `instance.method(args)` delegates to that function.
    #[test]
    fn set_instance_func_def_dispatch() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set Processor(transform: (String):(String)) {
                label: "processor"
            }
            func upper(s: String): String {
                return s + "_UPPER"
            }
            func main(): String {
                let proc = Processor(transform: "upper")
                let result = proc.transform("hello")
                Terminal.println(result)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "hello_UPPER\n");
    }

    /// Multiple instances of the same entity are independent.
    #[test]
    fn set_instance_multiple_instances_are_independent() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set Tag(name: String) {
                type: "tag"
            }
            func main(): String {
                let t1 = Tag(name: "rust")
                let t2 = Tag(name: "tlang")
                Terminal.println(t1.name)
                Terminal.println(t2.name)
                Terminal.println(t1.type)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "rust\ntlang\ntag\n");
    }

    // ── Explicit-currying tests ────────────────────────────────────────────────

    /// `this` in currying → the current set instance is passed as the first arg.
    #[test]
    fn ref_currying_this_passes_instance() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set Report(title: String) {
                render: &doRender(this)
            }
            func doRender(r: Report): String {
                return r.title
            }
            func main(): String {
                let rep = Report(title: "Q1 Summary")
                let text = rep.render()
                Terminal.println(text)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "Q1 Summary\n");
    }

    /// `_` holes are filled left-to-right from caller-supplied arguments.
    /// Uses `this` to forward the instance and `_` for the caller-supplied name.
    #[test]
    fn ref_currying_holes_filled_by_caller() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set Greeter2(prefix: String) {
                greet: &doGreetHole(this, _, "!")
            }
            func doGreetHole(g: Greeter2, name: String, suffix: String): String {
                return g.prefix + " " + name + suffix
            }
            func main(): String {
                let g = Greeter2(prefix: "Hello")
                let msg = g.greet("World")
                Terminal.println(msg)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "Hello World!\n");
    }

    /// Typed literals (String, Int, Bool) in explicit currying are forwarded correctly.
    /// Verifies that each arg kind reaches the function with its proper Value type.
    #[test]
    fn ref_currying_typed_literals() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set Config2 {
                add: &addOffset(_, 10),
                flag: &isActive(false),
                greet: &prefixGreet("Hello", _)
            }
            func addOffset(base: Int, offset: Int): Int {
                return base + offset
            }
            func isActive(flag: Bool): Bool {
                return flag
            }
            func prefixGreet(prefix: String, name: String): String {
                return prefix + " " + name
            }
            func main(): String {
                let c = Config2()
                let sum = c.add(5)
                let active = c.flag()
                let msg = c.greet("World")
                Terminal.println(sum)
                Terminal.println(active)
                Terminal.println(msg)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "15\nfalse\nHello World\n");
    }

    /// Nested `&ref` in currying passes the function name as a string.
    #[test]
    fn ref_currying_nested_ref_passes_func_name() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set Dispatcher {
                dispatch: &doDispatch(&myHandler)
            }
            func myHandler(x: String): String {
                return "handled:" + x
            }
            func doDispatch(handlerName: String): String {
                return handlerName
            }
            func main(): String {
                let d = Dispatcher()
                let name = d.dispatch()
                Terminal.println(name)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "myHandler\n");
    }

    /// Wrong number of caller args for holes → runtime error with clear message.
    #[test]
    fn ref_currying_wrong_hole_count_errors() {
        let model = parse_domain_model(
            r#"
            set Svc2 {
                run: &doRun(_, _)
            }
            func doRun(a: String, b: String): String { return a + b }
            func main(): String {
                let s = Svc2()
                return s.run("only-one")
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let err = run_main(&compiled).expect_err("hole-count mismatch must fail");
        assert!(
            err.0.contains("placeholder") || err.0.contains("argument"),
            "error should mention the placeholder/argument mismatch; got: {}",
            err.0
        );
    }

    /// `this` with explicit holes: instance + caller arg forwarded to function.
    #[test]
    fn ref_currying_this_and_holes_combined() {
        let run = run_set_instance_program(
            r#"
            use TLang.Terminal
            set Printer(prefix: String) {
                print: &doPrint(this, _)
            }
            func doPrint(p: Printer, suffix: String): String {
                return p.prefix + suffix
            }
            func main(): String {
                let p = Printer(prefix: "Hello")
                let out = p.print(" World")
                Terminal.println(out)
                return "ok"
            }
            "#,
        );
        assert_eq!(run.output, "Hello World\n");
    }

    /// Missing mandatory constructor param → compile error mentioning the param name.
    #[test]
    fn set_instance_missing_param_errors() {
        let model = parse_domain_model(
            r#"
            set Box(width: String, height: String) { }
            func main(): String {
                let b = Box(width: "10")
                return b.width
            }
            "#,
        )
        .expect("should parse");
        let err =
            compile_from_domain_model(&model).expect_err("missing param must fail at compile time");
        assert!(
            err.0.contains("height"),
            "error should name the missing param; got: {}",
            err.0
        );
    }

    /// Supplying an unknown param key → compile error mentioning the key name.
    #[test]
    fn set_instance_unknown_param_errors() {
        let model = parse_domain_model(
            r#"
            set Box(width: String) { }
            func main(): String {
                let b = Box(width: "10", depth: "5")
                return b.width
            }
            "#,
        )
        .expect("should parse");
        let err =
            compile_from_domain_model(&model).expect_err("unknown param must fail at compile time");
        assert!(
            err.0.contains("depth"),
            "error should name the unknown key; got: {}",
            err.0
        );
    }

    /// Accessing a Ref attr on the static entity (not an instance) → runtime error.
    #[test]
    fn set_static_ref_access_errors() {
        let model = parse_domain_model(
            r#"
            set Svc(id: String) {
                compute: &myFn
            }
            func myFn(id: String): String { return id }
            func main(): String {
                let r = Svc.compute
                return r
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let err = run_main(&compiled).expect_err("static ref access must fail");
        assert!(
            err.0.contains("ref function") || err.0.contains("static"),
            "error should mention restriction; got: {}",
            err.0
        );
    }

    /// Calling a non-existent set entity → compile error.
    #[test]
    fn set_instance_unknown_entity_errors() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let x = Ghost(name: "boo")
                return "unreachable"
            }
            "#,
        )
        .expect("should parse");
        let err = compile_from_domain_model(&model)
            .expect_err("unknown entity must fail at compile time");
        assert!(
            err.0.contains("Ghost"),
            "error should mention the entity name; got: {}",
            err.0
        );
    }

    // ── TLang.Shell integration tests ─────────────────────────────────────────

    #[test]
    #[cfg(unix)]
    fn shell_run_returns_stdout() {
        let model = parse_domain_model(
            r#"
            use TLang.Shell
            use TLang.Terminal

            func main(): String {
                let out = Shell.run("echo tlang-shell-test");
                Terminal.println(out);
                return out;
            }
            "#,
        )
        .expect("should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let result = run_main(&compiled).expect("should run");

        assert_eq!(result.output.trim(), "tlang-shell-test");
        assert_eq!(
            result.return_value,
            Value::String("tlang-shell-test".to_string())
        );
    }

    #[test]
    #[cfg(unix)]
    fn shell_capture_returns_map_with_exit_code() {
        let model = parse_domain_model(
            r#"
            use TLang.Shell

            func main(): String {
                let result = Shell.capture("echo captured; exit 0");
                return result.exitCode;
            }
            "#,
        )
        .expect("should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let result = run_main(&compiled).expect("should run");

        assert_eq!(result.return_value, Value::Int(0));
    }

    #[test]
    #[cfg(unix)]
    fn shell_capture_nonzero_exit_does_not_fail() {
        let model = parse_domain_model(
            r#"
            use TLang.Shell

            func main(): String {
                let result = Shell.capture("exit 42");
                return result.success;
            }
            "#,
        )
        .expect("should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let result = run_main(&compiled).expect("should run");

        assert_eq!(result.return_value, Value::Bool(false));
    }

    #[test]
    #[cfg(unix)]
    fn shell_run_fails_on_nonzero_exit() {
        let model = parse_domain_model(
            r#"
            use TLang.Shell

            func main(): String {
                let out = Shell.run("exit 1");
                return out;
            }
            "#,
        )
        .expect("should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let err = run_main(&compiled).expect_err("Shell.run should fail on non-zero exit");
        assert!(
            err.0.contains("exited with code 1"),
            "error should mention exit code; got: {}",
            err.0
        );
    }

    #[test]
    fn shell_env_returns_known_variable() {
        let model = parse_domain_model(
            r#"
            use TLang.Shell

            func main(): String {
                return Shell.env("PATH");
            }
            "#,
        )
        .expect("should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let result = run_main(&compiled).expect("should run");

        match result.return_value {
            Value::String(v) => assert!(!v.is_empty(), "PATH should not be empty"),
            other => panic!("expected String return, got {other:?}"),
        }
    }

    #[test]
    fn shell_env_returns_empty_for_unset_variable() {
        let model = parse_domain_model(
            r#"
            use TLang.Shell

            func main(): String {
                return Shell.env("__TLANG_DEFINITELY_NOT_SET_XYZ__");
            }
            "#,
        )
        .expect("should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let result = run_main(&compiled).expect("should run");

        assert_eq!(result.return_value, Value::String(String::new()));
    }

    // ── !> exec decl bytecode roundtrip ───────────────────────────────────────

    #[test]
    fn exec_decl_survives_bytecode_roundtrip() {
        let model = parse_domain_model(
            r#"
            set Deploy(host: String) {
                host: "prod"
            } !> bash
            func main(): String {
                return "ok";
            }
            "#,
        )
        .expect("should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");

        // Verify exec decl is present before encoding.
        let deploy = compiled
            .model_tree()
            .nodes
            .iter()
            .find_map(|n| {
                if let ModelNodeTree::SetEntity(e) = n {
                    if e.name == "Deploy" { Some(e) } else { None }
                } else {
                    None
                }
            })
            .expect("Deploy set should exist");
        assert_eq!(
            deploy.exec.as_ref().map(|e| e.executor.as_str()),
            Some("bash"),
            "exec decl should be bash before roundtrip"
        );

        // Roundtrip through bytecode.
        let bytes = compiled.encode();
        let restored = CompiledProgram::decode(&bytes).expect("decode should succeed");

        let deploy_after = restored
            .model_tree()
            .nodes
            .iter()
            .find_map(|n| {
                if let ModelNodeTree::SetEntity(e) = n {
                    if e.name == "Deploy" { Some(e) } else { None }
                } else {
                    None
                }
            })
            .expect("Deploy set should exist after roundtrip");
        assert_eq!(
            deploy_after.exec.as_ref().map(|e| e.executor.as_str()),
            Some("bash"),
            "exec decl should survive bytecode roundtrip"
        );
    }

    #[test]
    fn exec_and_output_decls_survive_bytecode_roundtrip() {
        let model = parse_domain_model(
            r#"
            set Deploy(host: String) >> "deploy.sh" {
                host: "prod"
            } !> bash
            func main(): String {
                return "ok";
            }
            "#,
        )
        .expect("should parse");

        let compiled = compile_from_domain_model(&model).expect("should compile");
        let bytes = compiled.encode();
        let restored = CompiledProgram::decode(&bytes).expect("decode should succeed");

        let deploy = restored
            .model_tree()
            .nodes
            .iter()
            .find_map(|n| {
                if let ModelNodeTree::SetEntity(e) = n {
                    if e.name == "Deploy" { Some(e) } else { None }
                } else {
                    None
                }
            })
            .expect("Deploy set should exist after roundtrip");

        assert_eq!(
            deploy.output.as_ref().map(|o| o.path.as_str()),
            Some("deploy.sh"),
            "output path should survive roundtrip"
        );
        assert_eq!(
            deploy.exec.as_ref().map(|e| e.executor.as_str()),
            Some("bash"),
            "exec decl should survive roundtrip alongside output"
        );
    }

    // ── Lambda tests ────────────────────────────────────────────────────────

    /// Lambda stored in a `let` binding and called as a function.
    #[test]
    fn lambda_let_and_call() {
        let model = parse_domain_model(
            r#"
            func main(): Int {
                let double = (x) => x * 2;
                return double(21);
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(42));
    }

    /// Lambda with a block body using an explicit `return`.
    #[test]
    fn lambda_block_body() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let greet = (name) => {
                    return "Hello, " + name;
                };
                return greet("World");
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::String("Hello, World".to_string()));
    }

    /// Lambda passed as an argument to a helper function.
    #[test]
    fn lambda_passed_as_argument() {
        let model = parse_domain_model(
            r#"
            func apply(f: Func, v: Int): Int {
                return f(v);
            }
            func main(): Int {
                return apply((x) => x + 10, 5);
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(15));
    }

    /// Lambda with multiple parameters.
    #[test]
    fn lambda_multi_params() {
        let model = parse_domain_model(
            r#"
            func main(): Int {
                let add = (a, b) => a + b;
                return add(3, 4);
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(7));
    }

    /// Zero-parameter lambda.
    #[test]
    fn lambda_no_params() {
        let model = parse_domain_model(
            r#"
            func main(): String {
                let greeting = () => "hi";
                return greeting();
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::String("hi".to_string()));
    }

    /// `&funcName` function reference can be called when stored in a variable.
    #[test]
    fn func_ref_variable_call() {
        let model = parse_domain_model(
            r#"
            func double(x: Int): Int {
                return x * 2;
            }
            func main(): Int {
                let f = &double;
                return f(7);
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(14));
    }

    /// Lambda captures a variable from the enclosing scope.
    #[test]
    fn lambda_captures_enclosing_scope() {
        let model = parse_domain_model(
            r#"
            func main(): Int {
                let base = 100;
                let add_base = (n) => n + base;
                return add_base(42);
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(142));
    }

    /// Lambda survives a bytecode encode/decode roundtrip.
    #[test]
    fn lambda_bytecode_roundtrip() {
        let model = parse_domain_model(
            r#"
            func main(): Int {
                let triple = (x) => x * 3;
                return triple(5);
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let bytes = compiled.encode();
        let restored = CompiledProgram::decode(&bytes).expect("decode should succeed");
        let run = run_main(&restored).expect("should run after roundtrip");
        assert_eq!(run.return_value, Value::Int(15));
    }

    /// FuncDef model param accepts a lambda at instantiation.
    #[test]
    fn funcdef_param_accepts_lambda() {
        let model = parse_domain_model(
            r#"
            set Transformer(transform: (Int):(Int))
            func main(): Int {
                let t = Transformer(transform: (x) => x * 2);
                return t.transform(6);
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(12));
    }

    #[test]
    fn tlang_mcptool_use_injects_internal_set_model() {
        let model = parse_domain_model(
            r#"
            use TLang.MCPTool
            func run(args_json: String): String {
                return args_json;
            }
            func main(): String {
                let tool = MCPTool(
                    name: "echo",
                    description: "echo tool",
                    run: &run
                );
                return tool.run("{\"ok\":true}");
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::String("{\"ok\":true}".to_string()));
    }

    #[test]
    fn list_map_transforms_each_element() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            use TLang.Terminal
            func main(): String {
                let nums = List.of(1, 2, 3)
                let result = List.map(nums, (x) => x + 10)
                Terminal.println(List.join(result, ","))
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "11,12,13\n");
    }

    #[test]
    fn list_filter_keeps_matching_elements() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            use TLang.Terminal
            func main(): String {
                let nums = List.of(1, 2, 3, 4, 5)
                let result = List.filter(nums, (x) => x > 2)
                Terminal.println(List.join(result, ","))
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "3,4,5\n");
    }

    #[test]
    fn list_reduce_folds_to_sum() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            func main(): Int {
                let nums = List.of(1, 2, 3, 4)
                return List.reduce(nums, 0, (acc, x) => acc + x)
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(10));
    }

    #[test]
    fn list_any_returns_true_when_match_found() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            func main(): Bool {
                let nums = List.of(1, 2, 3)
                return List.any(nums, (x) => x == 2)
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Bool(true));
    }

    #[test]
    fn list_any_returns_false_when_no_match() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            func main(): Bool {
                let nums = List.of(1, 2, 3)
                return List.any(nums, (x) => x > 10)
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Bool(false));
    }

    #[test]
    fn list_all_returns_true_when_all_match() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            func main(): Bool {
                let nums = List.of(2, 4, 6)
                return List.all(nums, (x) => x > 0)
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Bool(true));
    }

    #[test]
    fn list_all_returns_false_when_one_fails() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            func main(): Bool {
                let nums = List.of(1, 2, 3)
                return List.all(nums, (x) => x > 1)
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Bool(false));
    }

    #[test]
    fn list_find_returns_first_matching_element() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            func main(): Int {
                let nums = List.of(1, 2, 3, 4)
                return List.find(nums, (x) => x > 2)
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(3));
    }

    #[test]
    fn list_find_returns_unit_when_no_match() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            use TLang.Terminal
            func main(): String {
                let nums = List.of(1, 2, 3)
                let found = List.find(nums, (x) => x > 10)
                Terminal.println(found ?? "none")
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "none\n");
    }

    #[test]
    fn list_flat_map_maps_and_flattens() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            use TLang.Terminal
            func main(): String {
                let nums = List.of(1, 2, 3)
                let result = List.flatMap(nums, (x) => List.of(x, x))
                Terminal.println(List.join(result, ","))
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "1,1,2,2,3,3\n");
    }

    #[test]
    fn list_sort_by_sorts_by_key() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            use TLang.Terminal
            func main(): String {
                let nums = List.of(3, 1, 2)
                let result = List.sortBy(nums, (x) => x)
                Terminal.println(List.join(result, ","))
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "1,2,3\n");
    }

    #[test]
    fn list_count_counts_matching_elements() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            func main(): Int {
                let nums = List.of(1, 2, 3, 4, 5)
                return List.count(nums, (x) => x > 2)
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(3));
    }

    #[test]
    fn list_for_each_executes_side_effects() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            use TLang.Terminal
            func main(): String {
                let items = List.of("a", "b", "c")
                List.forEach(items, (x) => Terminal.println(x))
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "a\nb\nc\n");
    }

    #[test]
    fn list_map_using_dot_method_syntax() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            use TLang.Terminal
            func main(): String {
                let nums = List.of(10, 20, 30)
                let result = nums.map((x) => x + 1)
                Terminal.println(List.join(result, ","))
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "11,21,31\n");
    }

    #[test]
    fn list_group_by_groups_elements_by_key() {
        let model = parse_domain_model(
            r#"
            use TLang.List
            use TLang.Map
            use TLang.Terminal
            func main(): String {
                let words = List.of("ant", "bee", "arc", "bat")
                let grouped = List.groupBy(words, (w) => w.substring(0, 1))
                let a_group = Map.get(grouped, "a")
                Terminal.println(List.join(a_group, ","))
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "ant,arc\n");
    }

    #[test]
    fn top_level_let_binding_in_helper() {
        let model = parse_domain_model(
            r#"
            let x = 5
            func main(): Int {
                return x
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(5));
    }

    #[test]
    fn top_level_let_binding_with_expression() {
        let model = parse_domain_model(
            r#"
            let x = 2 + 3
            func main(): Int {
                return x * 2
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.return_value, Value::Int(10));
    }

    #[test]
    fn top_level_let_binding_string() {
        let model = parse_domain_model(
            r#"
            use TLang.Terminal
            let greeting = "Hello"
            func main(): String {
                Terminal.println(greeting)
            }
            "#,
        )
        .expect("should parse");
        let compiled = compile_from_domain_model(&model).expect("should compile");
        let run = run_main(&compiled).expect("should run");
        assert_eq!(run.output, "Hello\n");
    }

    // ── validate_model_type_refs ──────────────────────────────────────────
    //
    // These tests cover the compile-time type-reference validation added to
    // `compile_from_domain_model`.  The key invariant: unquoted identifiers
    // in set attributes / extensions are TYPE REFERENCES and must resolve to
    // a declared set; quoted strings are opaque data and are never checked.

    #[test]
    fn type_ref_validation_accepts_known_set_extension() {
        // `Child : Base` — Base is declared, should compile cleanly.
        let model = parse_domain_model(
            r#"
            set Base { kind: "base" }
            set Child : Base { name: "child" }
            func main(): String { return "ok"; }
            "#,
        )
        .expect("should parse");
        compile_from_domain_model(&model).expect("should compile: Child extends declared set Base");
    }

    #[test]
    fn type_ref_validation_rejects_unknown_ext() {
        // `Child : UnknownParent` — UnknownParent is not declared, must fail.
        let model = parse_domain_model(
            r#"
            set Child : UnknownParent { name: "child" }
            func main(): String { return "ok"; }
            "#,
        )
        .expect("should parse");
        let err =
            compile_from_domain_model(&model).expect_err("should fail: UnknownParent not declared");
        let msg = err.to_string();
        assert!(
            msg.contains("UnknownParent"),
            "error should name the unknown type, got: {msg}"
        );
        assert!(
            msg.contains("Child"),
            "error should name the declaring set, got: {msg}"
        );
    }

    #[test]
    fn type_ref_validation_accepts_dotted_ext_when_leaf_is_known() {
        // `set Child : Module.ForgeProject` — the last segment "ForgeProject"
        // must be declared; the prefix "Module" is an import alias.
        let model = parse_domain_model(
            r#"
            set ForgeProject { kind: "forge-project" }
            set Child : Module.ForgeProject { name: "test" }
            func main(): String { return "ok"; }
            "#,
        )
        .expect("should parse");
        compile_from_domain_model(&model)
            .expect("should compile: ForgeProject is declared (dotted alias resolved correctly)");
    }

    #[test]
    fn type_ref_validation_rejects_dotted_ext_when_leaf_is_unknown() {
        let model = parse_domain_model(
            r#"
            set Child : Module.GhostType { name: "test" }
            func main(): String { return "ok"; }
            "#,
        )
        .expect("should parse");
        let err = compile_from_domain_model(&model)
            .expect_err("should fail: GhostType not declared");
        assert!(
            err.to_string().contains("GhostType"),
            "error should name the unknown type: {err}"
        );
    }

    #[test]
    fn type_ref_validation_accepts_known_attr_type_ref() {
        // `codeAdapter: AdapterBase` — AdapterBase is declared, should pass.
        let model = parse_domain_model(
            r#"
            set AdapterBase { kind: "adapter" }
            set Project { codeAdapter: AdapterBase }
            func main(): String { return "ok"; }
            "#,
        )
        .expect("should parse");
        compile_from_domain_model(&model)
            .expect("should compile: AdapterBase is declared");
    }

    #[test]
    fn type_ref_validation_rejects_unknown_attr_type_ref() {
        // `codeAdapter: UnknownAdapter` — not declared, must fail with a
        // message that names both the attribute and the unknown type.
        let model = parse_domain_model(
            r#"
            set Project { codeAdapter: UnknownAdapter }
            func main(): String { return "ok"; }
            "#,
        )
        .expect("should parse");
        let err = compile_from_domain_model(&model)
            .expect_err("should fail: UnknownAdapter not declared");
        let msg = err.to_string();
        assert!(
            msg.contains("UnknownAdapter"),
            "error should name the unknown type, got: {msg}"
        );
        assert!(
            msg.contains("codeAdapter"),
            "error should name the attribute, got: {msg}"
        );
        assert!(
            msg.contains("hint"),
            "error should include a hint for the user, got: {msg}"
        );
    }

    #[test]
    fn type_ref_validation_quoted_string_is_not_validated() {
        // `codeAdapter: "UnknownAdapterQuoted"` — quoted string, never
        // treated as a type reference regardless of content.
        let model = parse_domain_model(
            r#"
            set Project { codeAdapter: "UnknownAdapterQuoted" }
            func main(): String { return "ok"; }
            "#,
        )
        .expect("should parse");
        compile_from_domain_model(&model)
            .expect("should compile: quoted string is opaque data, not a type reference");
    }

    #[test]
    fn type_ref_validation_primitive_types_are_whitelisted() {
        // Primitive / built-in type names may appear as attr values (type
        // annotations) without a `set` declaration.
        let model = parse_domain_model(
            r#"
            set Field { id: Number, label: String, active: Bool, score: Float }
            func main(): String { return "ok"; }
            "#,
        )
        .expect("should parse");
        compile_from_domain_model(&model)
            .expect("should compile: Number/String/Bool/Float are whitelisted primitives");
    }

    #[test]
    fn type_ref_validation_multiple_attrs_reports_first_unknown() {
        // When several attrs are bad, the error points to the first one found.
        let model = parse_domain_model(
            r#"
            set Project {
                known: "ok",
                broken: GhostSet
            }
            func main(): String { return "ok"; }
            "#,
        )
        .expect("should parse");
        let err = compile_from_domain_model(&model).expect_err("should fail");
        assert!(
            err.to_string().contains("GhostSet"),
            "error should name the bad type ref: {err}"
        );
    }

    #[test]
    fn type_ref_validation_cross_set_reference_works() {
        // One set references another set declared later in the same model block.
        // All sets are known after the merge pass, so ordering doesn't matter.
        let model = parse_domain_model(
            r#"
            set Project { adapter: MyAdapter }
            set MyAdapter { kind: "adapter" }
            func main(): String { return "ok"; }
            "#,
        )
        .expect("should parse");
        compile_from_domain_model(&model)
            .expect("should compile: forward reference within same model is valid");
    }
}
