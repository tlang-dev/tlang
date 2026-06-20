// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Static type-checker for compiled TLang helper IR.
//!
//! Runs over the `Stmt`/`Expr` IR of compiled helper functions and emits
//! non-fatal [`TypeWarning`]s for potential type mismatches. These are
//! surfaced by the LSP as warning diagnostics but never block execution.
//!
//! The type checker uses a lightweight [`Type`] enum that collapses
//! `TypeAnnotation` variants into broader categories and adds `Unknown` for
//! expressions whose type cannot be determined statically (e.g. calls into
//! built-in libraries with unresolved return types).
//!
//! Entry point: [`TypeChecker::new`] + [`TypeChecker::collect_warnings`], called
//! from [`super::collect_semantic_warnings`].

use super::*;

/// Runtime type used during type-checking — mirrors `TypeAnnotation` but also carries
/// `Unknown` for cases where the type cannot be statically determined (e.g. calls to
/// built-in library functions whose signatures are not declared in the helper source).
#[derive(Debug, Clone, PartialEq, Eq)]
enum Type {
    Int,
    Bool,
    Str,
    List,
    StringBuilder,
    Unit,
    Unknown,
}

impl std::fmt::Display for Type {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Type::Int => write!(f, "Int"),
            Type::Bool => write!(f, "Bool"),
            Type::Str => write!(f, "String"),
            Type::List => write!(f, "List"),
            Type::StringBuilder => write!(f, "StringBuilder"),
            Type::Unit => write!(f, "Unit"),
            Type::Unknown => write!(f, "Unknown"),
        }
    }
}

impl From<&TypeAnnotation> for Type {
    fn from(ann: &TypeAnnotation) -> Self {
        match ann {
            TypeAnnotation::Int => Type::Int,
            TypeAnnotation::Float => Type::Unknown,
            TypeAnnotation::Bool => Type::Bool,
            TypeAnnotation::String => Type::Str,
            TypeAnnotation::List => Type::List,
            TypeAnnotation::Map => Type::Unknown,
            TypeAnnotation::Unit => Type::Unit,
            TypeAnnotation::Leaf => Type::Unknown,
            TypeAnnotation::TmplLang => Type::Unknown,
            TypeAnnotation::TmplDoc => Type::Unknown,
            TypeAnnotation::TmplStyle => Type::Unknown,
            TypeAnnotation::TmplData => Type::Unknown,
            TypeAnnotation::TmplCmd => Type::Unknown,
            TypeAnnotation::Func => Type::Unknown,
        }
    }
}

fn from_value(v: &Value) -> Type {
    match v {
        Value::Int(_) => Type::Int,
        Value::Float(_) => Type::Unknown,
        Value::Bool(_) => Type::Bool,
        Value::String(_) => Type::Str,
        Value::List(_) => Type::List,
        Value::Map(_) => Type::Unknown,
        Value::Unit => Type::Unit,
        Value::Leaf(_) => Type::Unknown,
        Value::SetInstance(_) => Type::Unknown,
        Value::BoundAttr(_) => Type::Unknown,
        Value::StringBuilder(_) => Type::StringBuilder,
        Value::Lambda(_) => Type::Unknown,
        Value::PdfDoc(_) => Type::Unknown,
    }
}

/// Returns `true` if `actual` is compatible with `expected` (i.e. they are the same,
/// or `actual` is `Unknown` which means we could not determine the type statically).
fn compatible(expected: &Type, actual: &Type) -> bool {
    actual == &Type::Unknown || expected == actual
}

pub(super) struct SemanticWarning(pub String);

pub(super) struct TypeChecker<'a> {
    functions: &'a HashMap<String, Function>,
    /// Maps dependency file stem → list of explicitly exposed symbol names.
    /// When a dependency has at least one `expose` declaration, only those
    /// symbols may be called from the current file.
    dep_exposes: Option<&'a std::collections::HashMap<String, Vec<String>>>,
    /// Names that are callable but not in `functions` — e.g. template names
    /// (`lang [kotlin] service(...)`) that are valid call targets from helper blocks.
    known_callables: std::collections::HashSet<String>,
}

impl<'a> TypeChecker<'a> {
    pub(super) fn new(functions: &'a HashMap<String, Function>) -> Self {
        TypeChecker {
            functions,
            dep_exposes: None,
            known_callables: std::collections::HashSet::new(),
        }
    }

    pub(super) fn with_dep_exposes(
        mut self,
        dep_exposes: &'a std::collections::HashMap<String, Vec<String>>,
    ) -> Self {
        self.dep_exposes = Some(dep_exposes);
        self
    }

    pub(super) fn with_known_callables(
        mut self,
        callables: std::collections::HashSet<String>,
    ) -> Self {
        self.known_callables = callables;
        self
    }

    /// Collect non-fatal warnings (e.g. missing type annotations).
    pub(super) fn collect_warnings(&self) -> Vec<SemanticWarning> {
        let mut warnings = Vec::new();
        for func in self.functions.values() {
            // Warn when any parameter has no type annotation.
            for (param, ty) in &func.params {
                if ty.is_none() {
                    warnings.push(SemanticWarning(format!(
                        "parameter `{param}` in `{}` has no type annotation",
                        func.name
                    )));
                }
            }
        }
        warnings
    }

    pub(super) fn check_all(&self) -> Result<(), CompileError> {
        for (key, func) in self.functions.iter() {
            // Skip qualified-alias entries (e.g. "KotlinGen.generate") that
            // were registered in Step 1 to enable runtime dispatch.  Their
            // bodies are identical to the bare-name entry and are already
            // checked under the bare key — type-checking the duplicate would
            // produce spurious visibility errors because `func.name` is bare
            // while the key is qualified, causing the caller-stem lookup to
            // return the wrong function when names collide across packages.
            if !key.contains('.') {
                self.check_function(func)?;
            }
        }
        Ok(())
    }

    // ── function ────────────────────────────────────────────────────────────

    fn check_function(&self, func: &Function) -> Result<(), CompileError> {
        let mut env: HashMap<String, Type> = HashMap::new();
        for (param, type_ann) in &func.params {
            if let Some(ann) = type_ann {
                env.insert(param.clone(), Type::from(ann));
            }
        }
        let expected_return = func.return_type.as_ref().map(Type::from);
        self.check_block(&func.body, &mut env, expected_return.as_ref(), &func.name)?;
        Ok(())
    }

    // ── block / statement ───────────────────────────────────────────────────

    fn check_block(
        &self,
        block: &[Stmt],
        env: &mut HashMap<String, Type>,
        expected_return: Option<&Type>,
        func_name: &str,
    ) -> Result<(), CompileError> {
        for stmt in block {
            self.check_stmt(stmt, env, expected_return, func_name)?;
        }
        Ok(())
    }

    fn check_stmt(
        &self,
        stmt: &Stmt,
        env: &mut HashMap<String, Type>,
        expected_return: Option<&Type>,
        func_name: &str,
    ) -> Result<(), CompileError> {
        match stmt {
            Stmt::Let { name, expr } => {
                // Shadowing a previous binding is intentional in TLang —
                // `let s = s + more` creates a fresh immutable `s` whose
                // value is the concatenation.  Always update `env` so the
                // inferred type of the new binding is correct.
                let t = self.infer_expr(expr, env, func_name)?;
                env.insert(name.clone(), t);
            }

            Stmt::Call(call) => {
                // Validate argument types even for standalone call statements.
                self.infer_call(call, env, func_name)?;
            }

            Stmt::Return(expr) => {
                let actual = self.infer_expr(expr, env, func_name)?;
                if let Some(expected) = expected_return
                    && !compatible(expected, &actual)
                {
                    return Err(CompileError(format!(
                        "type error in `{func_name}`: \
                             return type is declared as `{expected}` \
                             but the `return` expression has type `{actual}`"
                    )));
                }
            }

            Stmt::If {
                condition,
                then_body,
                else_body,
            } => {
                let cond_type = self.infer_expr(condition, env, func_name)?;
                if !compatible(&Type::Bool, &cond_type) {
                    return Err(CompileError(format!(
                        "type error in `{func_name}`: \
                         `if` condition must be `Bool`, found `{cond_type}`"
                    )));
                }
                let mut then_env = env.clone();
                self.check_block(then_body, &mut then_env, expected_return, func_name)?;
                if let Some(else_b) = else_body {
                    let mut else_env = env.clone();
                    self.check_block(else_b, &mut else_env, expected_return, func_name)?;
                }
            }

            Stmt::ForIn {
                var,
                iterable,
                body,
            } => {
                let iter_type = self.infer_expr(iterable, env, func_name)?;
                if !compatible(&Type::List, &iter_type) {
                    return Err(CompileError(format!(
                        "type error in `{func_name}`: \
                         `for-in` requires a `List`, found `{iter_type}`"
                    )));
                }
                let mut body_env = env.clone();
                body_env.insert(var.clone(), Type::Unknown);
                self.check_block(body, &mut body_env, expected_return, func_name)?;
            }

            Stmt::ForInDestructure {
                vars,
                iterable,
                body,
            } => {
                let iter_type = self.infer_expr(iterable, env, func_name)?;
                if !compatible(&Type::List, &iter_type) {
                    return Err(CompileError(format!(
                        "type error in `{func_name}`: \
                         `for-in` destructure requires a `List`, found `{iter_type}`"
                    )));
                }
                let mut body_env = env.clone();
                for var in vars {
                    body_env.insert(var.clone(), Type::Unknown);
                }
                self.check_block(body, &mut body_env, expected_return, func_name)?;
            }

            Stmt::ForRange {
                var,
                start,
                end,
                body,
                ..
            } => {
                let start_type = self.infer_expr(start, env, func_name)?;
                let end_type = self.infer_expr(end, env, func_name)?;
                if !compatible(&Type::Int, &start_type) {
                    return Err(CompileError(format!(
                        "type error in `{func_name}`: \
                         `for` range start must be `Int`, found `{start_type}`"
                    )));
                }
                if !compatible(&Type::Int, &end_type) {
                    return Err(CompileError(format!(
                        "type error in `{func_name}`: \
                         `for` range end must be `Int`, found `{end_type}`"
                    )));
                }
                let mut body_env = env.clone();
                body_env.insert(var.clone(), Type::Int);
                self.check_block(body, &mut body_env, expected_return, func_name)?;
            }

            Stmt::Match {
                target,
                arms,
                default,
            } => {
                let target_type = self.infer_expr(target, env, func_name)?;
                for arm in arms {
                    match &arm.pattern {
                        MatchPattern::Value(expr) => {
                            let _ = self.infer_expr(expr, env, func_name)?;
                        }
                        MatchPattern::Condition(expr) => {
                            let mut cond_env = env.clone();
                            cond_env.insert("_".to_string(), target_type.clone());
                            let cond_type = self.infer_expr(expr, &cond_env, func_name)?;
                            if !compatible(&Type::Bool, &cond_type) {
                                return Err(CompileError(format!(
                                    "type error in `{func_name}`: \
                                     `match` case condition must be `Bool`, found `{cond_type}`"
                                )));
                            }
                        }
                        MatchPattern::IsType { .. } => {}
                    }
                    let mut arm_env = env.clone();
                    self.check_block(&arm.body, &mut arm_env, expected_return, func_name)?;
                }
                if let Some(default_body) = default {
                    let mut default_env = env.clone();
                    self.check_block(
                        default_body,
                        &mut default_env,
                        expected_return,
                        func_name,
                    )?;
                }
            }
        }
        Ok(())
    }

    // ── expression inference ────────────────────────────────────────────────

    fn infer_expr(
        &self,
        expr: &Expr,
        env: &HashMap<String, Type>,
        func_name: &str,
    ) -> Result<Type, CompileError> {
        match expr {
            Expr::Literal(v) => Ok(from_value(v)),
            Expr::Var(name) => Ok(env.get(name).cloned().unwrap_or(Type::Unknown)),
            Expr::Op(op, left, right) => {
                let lt = self.infer_expr(left, env, func_name)?;
                let rt = self.infer_expr(right, env, func_name)?;
                self.infer_op(*op, &lt, &rt, func_name)
            }
            Expr::Call(call) => self.infer_call(call, env, func_name),
            Expr::If {
                condition,
                then_expr,
                else_expr,
            } => {
                let cond_type = self.infer_expr(condition, env, func_name)?;
                if !compatible(&Type::Bool, &cond_type) {
                    return Err(CompileError(format!(
                        "type error in `{func_name}`: `if` condition must be `Bool`, found `{cond_type}`"
                    )));
                }
                let then_type = self.infer_expr(then_expr, env, func_name)?;
                let else_type = self.infer_expr(else_expr, env, func_name)?;
                if compatible(&then_type, &else_type) {
                    Ok(then_type)
                } else if compatible(&else_type, &then_type) {
                    Ok(else_type)
                } else {
                    Ok(Type::Unknown)
                }
            }
            Expr::MatchExpr {
                target,
                arms,
                default,
            } => {
                let target_type = self.infer_expr(target, env, func_name)?;
                let mut inferred_types = Vec::with_capacity(arms.len() + 1);
                for (pattern, rhs) in arms {
                    match pattern {
                        MatchPattern::Value(case_expr) => {
                            let case_type = self.infer_expr(case_expr, env, func_name)?;
                            if !compatible(&target_type, &case_type)
                                && !compatible(&case_type, &target_type)
                            {
                                return Err(CompileError(format!(
                                    "type error in `{func_name}`: \
                                     `match` case value type `{case_type}` does not match target type `{target_type}`"
                                )));
                            }
                        }
                        MatchPattern::Condition(cond_expr) => {
                            let mut cond_env = env.clone();
                            cond_env.insert("_".to_string(), target_type.clone());
                            let cond_type = self.infer_expr(cond_expr, &cond_env, func_name)?;
                            if !compatible(&Type::Bool, &cond_type) {
                                return Err(CompileError(format!(
                                    "type error in `{func_name}`: \
                                     `match` case condition must be `Bool`, found `{cond_type}`"
                                )));
                            }
                        }
                        MatchPattern::IsType { .. } => {}
                    }
                    inferred_types.push(self.infer_expr(rhs, env, func_name)?);
                }
                inferred_types.push(self.infer_expr(default, env, func_name)?);
                let mut acc = inferred_types[0].clone();
                for ty in inferred_types.iter().skip(1) {
                    if compatible(&acc, ty) {
                        continue;
                    }
                    if compatible(ty, &acc) {
                        acc = ty.clone();
                    } else {
                        return Ok(Type::Unknown);
                    }
                }
                Ok(acc)
            }
            // SetNew produces a SetInstance at runtime — not a statically-known
            // type, so we return Unknown to avoid false type errors.
            Expr::SetNew(_) => Ok(Type::Unknown),
            Expr::IsType { expr, .. } => {
                let _ = self.infer_expr(expr, env, func_name)?;
                Ok(Type::Bool)
            }
            // Lambdas produce a callable value; type is Unknown statically.
            Expr::Lambda { .. } => Ok(Type::Unknown),
            // List expressions with arbitrary items always produce a List.
            Expr::ListExpr(_) => Ok(Type::List),
        }
    }

    fn infer_call(
        &self,
        call: &CallExpr,
        env: &HashMap<String, Type>,
        func_name: &str,
    ) -> Result<Type, CompileError> {
        // Enforce dep_exposes visibility: if the callee comes from a dependency
        // file that has at least one `expose` declaration, only explicitly
        // exposed symbols may be called — but only for cross-file calls.
        // Internal calls within the same source file are always allowed.
        if let Some(callee_fn) = self.functions.get(&call.target)
            && let Some(callee_stem) = &callee_fn.source_file
        {
            // Look up the caller's source file so we can skip the check
            // for intra-package calls (same file calling same file).
            let caller_stem = self
                .functions
                .get(func_name)
                .and_then(|f| f.source_file.as_deref());
            let is_internal = caller_stem == Some(callee_stem.as_str());
            // The exposed list uses bare function names ("generate").
            // call.target may be qualified ("KotlinGen.generate") when the
            // caller used an alias (e.g. `use KotlinGen as kotlin`).
            // Extract the bare suffix for the expose check.
            let exposed_name = call.target.rsplit('.').next().unwrap_or(&call.target);
            if !is_internal
                && let Some(exposed_list) = self.dep_exposes.and_then(|d| d.get(callee_stem))
                && !exposed_list.iter().any(|e| e == exposed_name)
            {
                return Err(CompileError(format!(
                    "`{}` is not exposed by `{callee_stem}` — \
                                 add `expose {}` to that file to make it accessible",
                    exposed_name, exposed_name
                )));
            }
        }

        if let Some(callee) = self.functions.get(&call.target) {
            // Validate argument count (runtime already checks this, but give a
            // compile-time message that mentions types).
            if callee.params.len() == call.args.len() {
                for ((param_name, param_type), arg) in
                    callee.params.iter().zip(call.args.iter())
                {
                    if let Some(expected_ann) = param_type {
                        let expected = Type::from(expected_ann);
                        let actual = self.infer_expr(arg, env, func_name)?;
                        if !compatible(&expected, &actual) {
                            return Err(CompileError(format!(
                                "type error in `{func_name}`: \
                                 argument `{param_name}` passed to `{}` \
                                 must be `{expected}`, found `{actual}`",
                                call.target,
                            )));
                        }
                    }
                }
            }
            Ok(callee
                .return_type
                .as_ref()
                .map(Type::from)
                .unwrap_or(Type::Unknown))
        } else {
            // Dotted calls (method calls, TLang builtins, alias-qualified calls) and
            // lambda-variable calls cannot be statically resolved here — skip them.
            // Plain-name calls to unknown functions are an error unless they are
            // a known template name, a scope variable (lambda), or have a dot.
            if !call.target.contains('.')
                && !env.contains_key(&call.target)
                && !self.known_callables.contains(&call.target)
            {
                return Err(CompileError(format!(
                    "function `{}` is not defined — it must be declared in this file or an imported file (at_offset: {})",
                    call.target, call.offset
                )));
            }
            Ok(Type::Unknown)
        }
    }

    fn infer_op(
        &self,
        op: Op,
        lt: &Type,
        rt: &Type,
        func_name: &str,
    ) -> Result<Type, CompileError> {
        match op {
            Op::Add => match (lt, rt) {
                (Type::Int, Type::Int) => Ok(Type::Int),
                (Type::Str, _) | (_, Type::Str) => Ok(Type::Str),
                (Type::Unknown, _) | (_, Type::Unknown) => Ok(Type::Unknown),
                _ => Err(CompileError(format!(
                    "type error in `{func_name}`: \
                     `+` requires `Int` or `String` operands, found `{lt}` and `{rt}`"
                ))),
            },
            Op::Sub | Op::Mul | Op::Div | Op::Mod => match (lt, rt) {
                (Type::Int, Type::Int) => Ok(Type::Int),
                (Type::Unknown, _) | (_, Type::Unknown) => Ok(Type::Int),
                _ => Err(CompileError(format!(
                    "type error in `{func_name}`: \
                     arithmetic operator requires `Int` operands, found `{lt}` and `{rt}`"
                ))),
            },
            Op::Eq | Op::Ne => Ok(Type::Bool),
            Op::Lt | Op::Gt | Op::Lte | Op::Gte => match (lt, rt) {
                (Type::Int, Type::Int) => Ok(Type::Bool),
                (Type::Unknown, _) | (_, Type::Unknown) => Ok(Type::Bool),
                _ => Err(CompileError(format!(
                    "type error in `{func_name}`: \
                     comparison operator requires `Int` operands, found `{lt}` and `{rt}`"
                ))),
            },
            Op::And | Op::Or => match (lt, rt) {
                (Type::Bool, Type::Bool) => Ok(Type::Bool),
                (Type::Unknown, _) | (_, Type::Unknown) => Ok(Type::Bool),
                _ => Err(CompileError(format!(
                    "type error in `{func_name}`: \
                     logical operator requires `Bool` operands, found `{lt}` and `{rt}`"
                ))),
            },
            Op::NullCoalesce => match (lt, rt) {
                (Type::Unit, other) => Ok(other.clone()),
                (other, Type::Unit) => Ok(other.clone()),
                (Type::Unknown, other) => Ok(other.clone()),
                (other, Type::Unknown) => Ok(other.clone()),
                (left, right) if left == right => Ok(left.clone()),
                _ => Ok(Type::Unknown),
            },
        }
    }
}

// ── unit tests for the type checker ─────────────────────────────────────────

#[cfg(test)]
mod tests {
    use super::super::compile_helper_block;

    fn check(src: &str) -> Result<(), super::super::CompileError> {
        compile_helper_block(&format!("{{{src}}}")).map(|_| ())
    }

    #[test]
    fn accepts_typed_params_and_return() {
        check(
            r#"
            func add(a: Int, b: Int): Int {
                return a + b;
            }
            "#,
        )
        .unwrap();
    }

    #[test]
    fn accepts_untyped_params() {
        check(
            r#"
            func greet(name) {
                return name;
            }
            "#,
        )
        .unwrap();
    }

    #[test]
    fn rejects_wrong_return_type() {
        let err = check(
            r#"
            func bad(): Int {
                return "hello";
            }
            "#,
        )
        .unwrap_err();
        assert!(
            err.0.contains("return type is declared as `Int`"),
            "unexpected error: {err}"
        );
        assert!(
            err.0.contains("has type `String`"),
            "unexpected error: {err}"
        );
    }

    #[test]
    fn rejects_wrong_argument_type() {
        let err = check(
            r#"
            func double(n: Int): Int {
                return n + n;
            }
            func caller() {
                let x = double("oops");
            }
            "#,
        )
        .unwrap_err();
        assert!(
            err.0.contains("argument `n` passed to `double`"),
            "unexpected error: {err}"
        );
    }

    #[test]
    fn rejects_non_bool_if_condition() {
        let err = check(
            r#"
            func bad() {
                if (42) {
                    let x = 1;
                }
            }
            "#,
        )
        .unwrap_err();
        assert!(
            err.0.contains("`if` condition must be `Bool`"),
            "unexpected error: {err}"
        );
    }

    #[test]
    fn rejects_non_int_for_range() {
        let err = check(
            r#"
            func bad() {
                for (i "start" to 10) {
                    let x = i;
                }
            }
            "#,
        )
        .unwrap_err();
        assert!(
            err.0.contains("`for` range start must be `Int`"),
            "unexpected error: {err}"
        );
    }
}
