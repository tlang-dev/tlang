// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Binary bytecode encoder and decoder for `.tlangc` files.
//!
//! A [`CompiledProgram`] can be serialised to a compact binary format and
//! loaded back without re-parsing or re-compiling the source. This is used
//! by [`crate::loader::load_program_prefer_bytecode`] to speed up repeated
//! invocations of the same program.
//!
//! # Format
//!
//! The file begins with a 4-byte magic header (`TLC` + version byte). All
//! discriminant bytes for `Stmt`, `Expr`, `Op`, `Value` and model node
//! variants are defined in the `bc` submodule as named constants, making the
//! format self-documenting and easy to version.
//!
//! # Key types
//!
//! - [`BcWriter`] — streaming encoder; call `encode_function`, `encode_template`,
//!   etc., then [`BcWriter::finalize`] to produce the byte buffer.
//! - [`BcReader`] — streaming decoder; mirrors every `BcWriter` method.

use super::*;

/// Named constants for every discriminant byte used in the `.tlangc` binary
/// format.  All [`BcWriter`] encode methods and [`BcReader`] decode methods
/// must reference these constants instead of inline hex literals so that:
///
/// - The format is self-documenting at the call site.
/// - Future format bumps require changes in exactly one place.
/// - Mismatches between encoder and decoder are caught by the reader's
///   exhaustive `match` arms rather than silently producing bad output.
mod bc {
    /// File magic bytes: ASCII `"TLC"` followed by format version `5`.
    /// Version 2 added the `exposes` list (for `tlang exec`) after imports.
    /// Version 3 adds the `exec` declaration (`!>`) to model set entities.
    /// Version 4 changes `Ref.currying` from `Vec<Vec<String>>` to
    /// `Vec<Vec<RefArg>>`, enabling `this`, `_`, typed literals, and nested
    /// `&ref` args inside model ref currying lists.
    /// Version 5 adds optional javadoc (`/** */`) string at the end of each
    /// encoded function, enabling `tlang exec --list` to display doc summaries.
    pub const MAGIC: [u8; 4] = [0x54, 0x4C, 0x43, 0x05];

    /// Discriminants for [`super::super::Stmt`] variants.
    pub mod stmt {
        pub const LET: u8 = 0x01;
        pub const CALL: u8 = 0x02;
        pub const IF: u8 = 0x03;
        pub const FOR_IN: u8 = 0x04;
        pub const FOR_RANGE: u8 = 0x05;
        pub const RETURN: u8 = 0x06;
        pub const MATCH: u8 = 0x07;
        pub const FOR_IN_DESTRUCTURE: u8 = 0x08;
    }

    /// Discriminants for [`super::super::Expr`] / [`super::super::Value`] literal variants.
    pub mod expr {
        pub const LIT_UNIT: u8 = 0x10;
        pub const LIT_INT: u8 = 0x11;
        pub const LIT_BOOL: u8 = 0x12;
        pub const LIT_STRING: u8 = 0x13;
        pub const LIT_LIST: u8 = 0x14;
        pub const LIT_LEAF: u8 = 0x15;
        pub const VAR: u8 = 0x16;
        pub const OP: u8 = 0x17;
        pub const CALL: u8 = 0x18;
        pub const LIT_FLOAT: u8 = 0x1A;
        pub const LIT_MAP: u8 = 0x1B;
        pub const SET_NEW: u8 = 0x1C;
        pub const IS_TYPE: u8 = 0x1D;
        pub const IF: u8 = 0x1E;
        pub const MATCH_EXPR: u8 = 0x1F;
        pub const LAMBDA: u8 = 0x20;
        /// List expression with arbitrary sub-expressions: `[expr, expr, ...]`.
        /// Unlike `LIT_LIST`, items may be any expression (calls, variables, etc.).
        pub const LIST_EXPR: u8 = 0x21;
    }

    /// Discriminants for [`super::super::ModelNodeTree`] variants.
    pub mod model_node {
        pub const ASSIGN_VAR: u8 = 0x01;
        pub const SET_ENTITY: u8 = 0x02;
    }

    /// Discriminants for [`super::super::ModelValueTypeTree`] variants.
    pub mod model_val {
        pub const TYPE: u8 = 0x00;
        pub const ARRAY: u8 = 0x01;
        pub const FUNC_DEF: u8 = 0x02;
        pub const REF: u8 = 0x03;
        pub const IMPL: u8 = 0x04;
        pub const IMPL_ARRAY: u8 = 0x05;
        pub const STRING_LITERAL: u8 = 0x06;
        pub const GENERIC: u8 = 0x07;
        pub const INT_LITERAL: u8 = 0x08;
        pub const BOOL_LITERAL: u8 = 0x09;
        pub const ARRAY_LITERAL: u8 = 0x0A;
    }

    /// Discriminants for [`super::super::TemplateContent`] variants.
    pub mod tmpl_content {
        pub const FULL: u8 = 0;
        pub const SPECIALIZED: u8 = 1;
    }

    /// Discriminants for [`super::super::WriteMode`] variants (inside set-entity
    /// output declarations).
    pub mod write_mode {
        pub const ALWAYS_WRITE: u8 = 0;
        pub const WRITE_ONCE: u8 = 1;
    }

    /// Discriminants for [`super::super::crate::model_tree::RefArg`] variants,
    /// used inside the `currying` list of a `REF`-typed model value.
    pub mod ref_arg {
        /// `this` — resolves to the current set instance.
        pub const THIS: u8 = 0x00;
        /// `_` — a hole the caller must fill in.
        pub const HOLE: u8 = 0x01;
        /// An inline string literal.
        pub const STR: u8 = 0x02;
        /// An inline integer literal (i64, big-endian).
        pub const INT: u8 = 0x03;
        /// An inline boolean literal (0 = false, 1 = true).
        pub const BOOL: u8 = 0x04;
        /// A nested function / variable reference (path of strings).
        pub const REF: u8 = 0x05;
        /// A constructor parameter reference (bare identifier like `leader`).
        /// The stored payload is the parameter name as a string.
        pub const IMPL_PARAM: u8 = 0x06;
    }
}

// ---------------------------------------------------------------------------
// Binary bytecode helpers: op / type-annotation codec
// ---------------------------------------------------------------------------

fn op_to_u8(op: Op) -> u8 {
    match op {
        Op::Add => 0,
        Op::Sub => 1,
        Op::Mul => 2,
        Op::Div => 3,
        Op::Mod => 4,
        Op::Eq => 5,
        Op::Ne => 6,
        Op::Lt => 7,
        Op::Gt => 8,
        Op::Lte => 9,
        Op::Gte => 10,
        Op::And => 11,
        Op::Or => 12,
        Op::NullCoalesce => 13,
    }
}

fn u8_to_op(v: u8) -> Result<Op, String> {
    Ok(match v {
        0 => Op::Add,
        1 => Op::Sub,
        2 => Op::Mul,
        3 => Op::Div,
        4 => Op::Mod,
        5 => Op::Eq,
        6 => Op::Ne,
        7 => Op::Lt,
        8 => Op::Gt,
        9 => Op::Lte,
        10 => Op::Gte,
        11 => Op::And,
        12 => Op::Or,
        13 => Op::NullCoalesce,
        _ => return Err(format!("unknown op code {v}")),
    })
}

fn type_annotation_to_u8(ty: &TypeAnnotation) -> u8 {
    match ty {
        TypeAnnotation::Int => 0,
        TypeAnnotation::Bool => 1,
        TypeAnnotation::String => 2,
        TypeAnnotation::List => 3,
        TypeAnnotation::Unit => 4,
        TypeAnnotation::Float => 5,
        TypeAnnotation::Map => 6,
        TypeAnnotation::Leaf => 7,
        TypeAnnotation::TmplLang => 8,
        TypeAnnotation::TmplDoc => 9,
        TypeAnnotation::TmplStyle => 10,
        TypeAnnotation::TmplData => 11,
        TypeAnnotation::TmplCmd => 12,
        TypeAnnotation::Func => 13,
    }
}

fn u8_to_type_annotation(v: u8) -> Result<TypeAnnotation, String> {
    Ok(match v {
        0 => TypeAnnotation::Int,
        1 => TypeAnnotation::Bool,
        2 => TypeAnnotation::String,
        3 => TypeAnnotation::List,
        4 => TypeAnnotation::Unit,
        5 => TypeAnnotation::Float,
        6 => TypeAnnotation::Map,
        7 => TypeAnnotation::Leaf,
        8 => TypeAnnotation::TmplLang,
        9 => TypeAnnotation::TmplDoc,
        10 => TypeAnnotation::TmplStyle,
        11 => TypeAnnotation::TmplData,
        12 => TypeAnnotation::TmplCmd,
        13 => TypeAnnotation::Func,
        _ => return Err(format!("unknown TypeAnnotation code {v}")),
    })
}

// ---------------------------------------------------------------------------
// BcWriter — builds the binary bytecode blob
// ---------------------------------------------------------------------------

pub(super) struct BcWriter {
    /// Instruction / operand bytes (everything after the header).
    body: Vec<u8>,
    /// Interning map: string → index into `pool`.
    strings: HashMap<String, u16>,
    /// Ordered constant-pool entries.
    pool: Vec<String>,
}

impl BcWriter {
    pub(super) fn new() -> Self {
        Self {
            body: Vec::new(),
            strings: HashMap::new(),
            pool: Vec::new(),
        }
    }

    // ---- primitives --------------------------------------------------------

    fn intern(&mut self, s: &str) -> u16 {
        if let Some(&idx) = self.strings.get(s) {
            return idx;
        }
        let idx = self.pool.len() as u16;
        self.pool.push(s.to_string());
        self.strings.insert(s.to_string(), idx);
        idx
    }

    fn write_u8(&mut self, v: u8) {
        self.body.push(v);
    }

    pub(super) fn write_u16_be(&mut self, v: u16) {
        self.body.extend_from_slice(&v.to_be_bytes());
    }

    fn write_u32_be(&mut self, v: u32) {
        self.body.extend_from_slice(&v.to_be_bytes());
    }

    fn write_i64_be(&mut self, v: i64) {
        self.body.extend_from_slice(&v.to_be_bytes());
    }

    pub(super) fn write_str_ref(&mut self, s: &str) {
        let idx = self.intern(s);
        self.write_u16_be(idx);
    }

    /// Finalize: prepend magic + constant pool in front of `body`.
    pub(super) fn finalize(self) -> Vec<u8> {
        let mut out = Vec::new();
        // Magic: "TLC" + version 1
        out.extend_from_slice(&bc::MAGIC);
        // Pool count (u32)
        out.extend_from_slice(&(self.pool.len() as u32).to_be_bytes());
        // Pool entries: len:u16, utf8_bytes[len]
        for s in &self.pool {
            let bytes = s.as_bytes();
            out.extend_from_slice(&(bytes.len() as u16).to_be_bytes());
            out.extend_from_slice(bytes);
        }
        // Body
        out.extend_from_slice(&self.body);
        out
    }

    // ---- encoding methods --------------------------------------------------

    pub(super) fn encode_block(&mut self, stmts: &[Stmt]) {
        self.write_u16_be(stmts.len() as u16);
        for stmt in stmts {
            self.encode_stmt(stmt);
        }
    }

    pub(super) fn encode_stmt(&mut self, stmt: &Stmt) {
        match stmt {
            Stmt::Let { name, expr } => {
                self.write_u8(bc::stmt::LET);
                self.write_str_ref(name);
                self.encode_expr(expr);
            }
            Stmt::Call(call) => {
                self.write_u8(bc::stmt::CALL);
                self.encode_call(call);
            }
            Stmt::If {
                condition,
                then_body,
                else_body,
            } => {
                self.write_u8(bc::stmt::IF);
                self.encode_expr(condition);
                self.encode_block(then_body);
                match else_body {
                    Some(body) => {
                        self.write_u8(1);
                        self.encode_block(body);
                    }
                    None => self.write_u8(0),
                }
            }
            Stmt::ForIn {
                var,
                iterable,
                body,
            } => {
                self.write_u8(bc::stmt::FOR_IN);
                self.write_str_ref(var);
                self.encode_expr(iterable);
                self.encode_block(body);
            }
            Stmt::ForInDestructure {
                vars,
                iterable,
                body,
            } => {
                self.write_u8(bc::stmt::FOR_IN_DESTRUCTURE);
                self.write_u16_be(vars.len() as u16);
                for v in vars {
                    self.write_str_ref(v);
                }
                self.encode_expr(iterable);
                self.encode_block(body);
            }
            Stmt::ForRange {
                var,
                start,
                end,
                inclusive,
                body,
            } => {
                self.write_u8(bc::stmt::FOR_RANGE);
                self.write_str_ref(var);
                self.encode_expr(start);
                self.encode_expr(end);
                self.write_u8(if *inclusive { 1 } else { 0 });
                self.encode_block(body);
            }
            Stmt::Match {
                target,
                arms,
                default,
            } => {
                self.write_u8(bc::stmt::MATCH);
                self.encode_expr(target);
                self.write_u16_be(arms.len() as u16);
                for arm in arms {
                    self.encode_match_pattern(&arm.pattern);
                    self.encode_block(&arm.body);
                }
                match default {
                    Some(body) => {
                        self.write_u8(1);
                        self.encode_block(body);
                    }
                    None => self.write_u8(0),
                }
            }
            Stmt::Return(expr) => {
                self.write_u8(bc::stmt::RETURN);
                self.encode_expr(expr);
            }
        }
    }

    fn encode_expr(&mut self, expr: &Expr) {
        match expr {
            Expr::Literal(Value::Unit) => {
                self.write_u8(bc::expr::LIT_UNIT);
            }
            Expr::Literal(Value::Int(v)) => {
                self.write_u8(bc::expr::LIT_INT);
                self.write_i64_be(*v);
            }
            Expr::Literal(Value::Bool(b)) => {
                self.write_u8(bc::expr::LIT_BOOL);
                self.write_u8(if *b { 1 } else { 0 });
            }
            Expr::Literal(Value::String(s)) => {
                self.write_u8(bc::expr::LIT_STRING);
                self.write_str_ref(s);
            }
            Expr::Literal(Value::List(items)) => {
                self.write_u8(bc::expr::LIT_LIST);
                self.write_u16_be(items.len() as u16);
                for item in items {
                    self.encode_value_as_literal_expr(item);
                }
            }
            // ListExpr is handled just above this match block (see encode_expr).
            Expr::ListExpr(items) => {
                self.write_u8(bc::expr::LIST_EXPR);
                self.write_u16_be(items.len() as u16);
                for item in items {
                    self.encode_expr(item);
                }
            }
            Expr::Literal(Value::Leaf(obj)) => {
                self.write_u8(bc::expr::LIT_LEAF);
                self.write_u16_be(obj.fields.len() as u16);
                for (key, val) in &obj.fields {
                    self.write_str_ref(key);
                    self.encode_value_as_literal_expr(val);
                }
            }
            Expr::Literal(Value::Float(v)) => {
                // f64 literal stored as its raw IEEE 754 bits.
                self.write_u8(bc::expr::LIT_FLOAT);
                self.write_i64_be(v.to_bits() as i64);
            }
            Expr::Literal(Value::Map(map)) => {
                // Map literal: key-value pairs, keys are strings.
                self.write_u8(bc::expr::LIT_MAP);
                self.write_u16_be(map.len() as u16);
                for (key, val) in map {
                    self.write_str_ref(key);
                    self.encode_value_as_literal_expr(val);
                }
            }
            Expr::Literal(Value::SetInstance(_)) => {
                // SetInstance is a runtime-only value and cannot appear as a
                // source-level literal — this branch should never be reached.
                unreachable!("cannot encode SetInstance as a bytecode literal");
            }
            Expr::Literal(Value::BoundAttr(_)) => {
                // BoundAttr is a runtime-only value and cannot appear as a
                // source-level literal — this branch should never be reached.
                unreachable!("cannot encode BoundAttr as a bytecode literal");
            }
            Expr::Literal(Value::StringBuilder(_)) => {
                // StringBuilder is a runtime-only value and cannot appear as a
                // source-level literal — this branch should never be reached.
                unreachable!("cannot encode StringBuilder as a bytecode literal");
            }
            Expr::Literal(Value::Lambda(_)) => {
                // Lambda is a runtime-only value; lambdas in source are encoded
                // via Expr::Lambda, not as a Literal — this branch is unreachable.
                unreachable!("cannot encode Lambda as a bytecode literal");
            }
            Expr::Literal(Value::PdfDoc(_)) => {
                unreachable!("cannot encode PdfDoc as a bytecode literal");
            }
            Expr::Lambda { params, body } => {
                self.write_u8(bc::expr::LAMBDA);
                self.write_u16_be(params.len() as u16);
                for p in params {
                    self.write_str_ref(p);
                }
                match body {
                    LambdaBody::Expr(expr) => {
                        self.write_u8(0); // body kind = expr
                        self.encode_expr(expr);
                    }
                    LambdaBody::Block(stmts) => {
                        self.write_u8(1); // body kind = block
                        self.write_u16_be(stmts.len() as u16);
                        for stmt in stmts {
                            self.encode_stmt(stmt);
                        }
                    }
                }
            }
            Expr::Var(name) => {
                self.write_u8(bc::expr::VAR);
                self.write_str_ref(name);
            }
            Expr::Op(op, lhs, rhs) => {
                self.write_u8(bc::expr::OP);
                self.write_u8(op_to_u8(*op));
                self.encode_expr(lhs);
                self.encode_expr(rhs);
            }
            Expr::Call(call) => {
                self.write_u8(bc::expr::CALL);
                self.encode_call(call);
            }
            Expr::If {
                condition,
                then_expr,
                else_expr,
            } => {
                self.write_u8(bc::expr::IF);
                self.encode_expr(condition);
                self.encode_expr(then_expr);
                self.encode_expr(else_expr);
            }
            Expr::MatchExpr {
                target,
                arms,
                default,
            } => {
                self.write_u8(bc::expr::MATCH_EXPR);
                self.encode_expr(target);
                self.write_u16_be(arms.len() as u16);
                for (pattern, rhs) in arms {
                    self.encode_match_pattern(pattern);
                    self.encode_expr(rhs);
                }
                self.encode_expr(default);
            }
            Expr::SetNew(sn) => {
                // Set-entity instantiation.
                self.write_u8(bc::expr::SET_NEW);
                self.write_str_ref(&sn.entity);
                self.write_u16_be(sn.fields.len() as u16);
                for (key, expr) in &sn.fields {
                    self.write_str_ref(key);
                    self.encode_expr(expr);
                }
            }
            Expr::IsType {
                expr,
                type_name,
                negated,
            } => {
                self.write_u8(bc::expr::IS_TYPE);
                self.encode_expr(expr);
                self.write_str_ref(type_name);
                self.write_u8(if *negated { 1 } else { 0 });
            }
        }
    }

    fn encode_value_as_literal_expr(&mut self, val: &Value) {
        self.encode_expr(&Expr::Literal(val.clone()));
    }

    fn encode_call(&mut self, call: &CallExpr) {
        self.write_str_ref(&call.target);
        self.write_u16_be(call.args.len() as u16);
        for arg in &call.args {
            self.encode_expr(arg);
        }
    }

    fn encode_match_pattern(&mut self, pattern: &MatchPattern) {
        match pattern {
            MatchPattern::Value(expr) => {
                self.write_u8(0);
                self.encode_expr(expr);
            }
            MatchPattern::Condition(expr) => {
                self.write_u8(1);
                self.encode_expr(expr);
            }
            MatchPattern::IsType { type_name, negated } => {
                self.write_u8(2);
                self.write_str_ref(type_name);
                self.write_u8(if *negated { 1 } else { 0 });
            }
        }
    }

    pub(super) fn encode_function(&mut self, f: &Function) {
        self.write_str_ref(&f.name);
        self.write_u16_be(f.params.len() as u16);
        for (name, ty) in &f.params {
            self.write_str_ref(name);
            match ty {
                Some(t) => {
                    self.write_u8(1);
                    self.write_u8(type_annotation_to_u8(t));
                }
                None => self.write_u8(0),
            }
        }
        match &f.return_type {
            Some(t) => {
                self.write_u8(1);
                self.write_u8(type_annotation_to_u8(t));
            }
            None => self.write_u8(0),
        }
        self.encode_block(&f.body);
        match &f.doc {
            Some(doc) => {
                self.write_u8(1);
                self.write_str_ref(doc);
            }
            None => self.write_u8(0),
        }
    }

    pub(super) fn encode_template(&mut self, tmpl: &TemplateFunction) {
        self.write_str_ref(&tmpl.lang);
        self.write_str_ref(&tmpl.name);
        self.write_u16_be(tmpl.params.len() as u16);
        for p in &tmpl.params {
            self.write_str_ref(&p.name);
            self.write_str_ref(&p.ty);
        }
        match &tmpl.content {
            TemplateContent::Full(s) => {
                self.write_u8(bc::tmpl_content::FULL);
                self.write_str_ref(s);
            }
            TemplateContent::Specialized(s) => {
                self.write_u8(bc::tmpl_content::SPECIALIZED);
                self.write_str_ref(s);
            }
        }
    }

    pub(super) fn encode_data_template(&mut self, dt: &DataTemplateFunction) {
        self.write_u16_be(dt.langs.len() as u16);
        for lang in &dt.langs {
            self.write_str_ref(lang);
        }
        self.write_str_ref(&dt.name);
        self.write_u16_be(dt.params.len() as u16);
        for p in &dt.params {
            self.write_str_ref(&p.name);
            self.write_str_ref(&p.ty);
        }
        self.write_str_ref(&dt.content);
    }

    pub(super) fn encode_cmd_template(&mut self, ct: &CmdTemplateFunction) {
        self.write_u16_be(ct.langs.len() as u16);
        for lang in &ct.langs {
            self.write_str_ref(lang);
        }
        self.write_str_ref(&ct.name);
        self.write_u16_be(ct.params.len() as u16);
        for p in &ct.params {
            self.write_str_ref(&p.name);
            self.write_str_ref(&p.ty);
        }
        self.write_str_ref(&ct.content);
    }

    pub(super) fn encode_style_template(&mut self, st: &StyleTemplateFunction) {
        self.write_u16_be(st.langs.len() as u16);
        for lang in &st.langs {
            self.write_str_ref(lang);
        }
        self.write_str_ref(&st.name);
        self.write_u16_be(st.params.len() as u16);
        for p in &st.params {
            self.write_str_ref(&p.name);
            self.write_str_ref(&p.ty);
        }
        self.write_str_ref(&st.content);
    }

    pub(super) fn encode_doc_template(&mut self, dt: &DocTemplateFunction) {
        self.write_u16_be(dt.langs.len() as u16);
        for lang in &dt.langs {
            self.write_str_ref(lang);
        }
        self.write_str_ref(&dt.name);
        self.write_u16_be(dt.params.len() as u16);
        for p in &dt.params {
            self.write_str_ref(&p.name);
            self.write_str_ref(&p.ty);
        }
        self.write_str_ref(&dt.content);
    }

    pub(super) fn encode_raw_template(&mut self, rt: &RawTemplateFunction) {
        let variant_byte: u8 = match rt.variant {
            RawVariant::AsIs => 0,
            RawVariant::Replaced => 1,
        };
        self.write_u8(variant_byte);
        self.write_str_ref(&rt.name);
        self.write_u16_be(rt.params.len() as u16);
        for p in &rt.params {
            self.write_str_ref(&p.name);
            self.write_str_ref(&p.ty);
        }
        self.write_str_ref(&rt.content);
    }

    pub(super) fn encode_model(&mut self, model: &ModelBlockTree) {
        self.write_u16_be(model.nodes.len() as u16);
        for node in &model.nodes {
            match node {
                ModelNodeTree::AssignVar(av) => {
                    self.write_u8(bc::model_node::ASSIGN_VAR);
                    self.write_str_ref(&av.name);
                    match &av.ty {
                        Some(ty) => {
                            self.write_u8(1);
                            self.encode_model_value_type(ty);
                        }
                        None => self.write_u8(0),
                    }
                    self.write_str_ref(&av.value);
                    self.encode_tree_context(&av.context);
                }
                ModelNodeTree::SetEntity(se) => {
                    self.write_u8(bc::model_node::SET_ENTITY);
                    self.write_str_ref(&se.name);
                    // exts: count + list of parent names
                    self.write_u16_be(se.exts.len() as u16);
                    for ext in &se.exts {
                        self.write_str_ref(ext);
                    }
                    // output decl: flag + mode + path
                    match &se.output {
                        Some(o) => {
                            self.write_u8(1);
                            self.write_u8(match o.mode {
                                crate::model_tree::WriteMode::AlwaysWrite => {
                                    bc::write_mode::ALWAYS_WRITE
                                }
                                crate::model_tree::WriteMode::WriteOnce => {
                                    bc::write_mode::WRITE_ONCE
                                }
                            });
                            self.write_str_ref(&o.path);
                        }
                        None => self.write_u8(0),
                    }
                    // exec decl: flag + executor name
                    match &se.exec {
                        Some(e) => {
                            self.write_u8(1);
                            self.write_str_ref(&e.executor);
                        }
                        None => self.write_u8(0),
                    }
                    self.write_u16_be(se.params.len() as u16);
                    for p in &se.params {
                        self.encode_model_set_attribute(p);
                    }
                    self.write_u16_be(se.attrs.len() as u16);
                    for a in &se.attrs {
                        self.encode_model_set_attribute(a);
                    }
                    self.encode_tree_context(&se.context);
                }
            }
        }
    }

    fn encode_model_set_attribute(&mut self, attr: &ModelSetAttributeTree) {
        match &attr.attr {
            Some(a) => {
                self.write_u8(1);
                self.write_str_ref(a);
            }
            None => self.write_u8(0),
        }
        self.encode_model_value_type(&attr.value);
        self.encode_tree_context(&attr.context);
    }

    fn encode_model_value_type(&mut self, ty: &ModelValueTypeTree) {
        match ty {
            ModelValueTypeTree::Type(s) => {
                self.write_u8(bc::model_val::TYPE);
                self.write_str_ref(s);
            }
            ModelValueTypeTree::Array(s) => {
                self.write_u8(bc::model_val::ARRAY);
                self.write_str_ref(s);
            }
            ModelValueTypeTree::Generic { name, params } => {
                self.write_u8(bc::model_val::GENERIC);
                self.write_str_ref(name);
                self.write_u16_be(params.len() as u16);
                for p in params {
                    self.write_str_ref(p);
                }
            }
            ModelValueTypeTree::FuncDef {
                param_types,
                ret_types,
            } => {
                self.write_u8(bc::model_val::FUNC_DEF);
                self.write_u16_be(param_types.len() as u16);
                for p in param_types {
                    self.write_str_ref(p);
                }
                self.write_u16_be(ret_types.len() as u16);
                for r in ret_types {
                    self.write_str_ref(r);
                }
            }
            ModelValueTypeTree::Ref { path, currying } => {
                use crate::model_tree::RefArg;
                self.write_u8(bc::model_val::REF);
                self.write_u16_be(path.len() as u16);
                for p in path {
                    self.write_str_ref(p);
                }
                self.write_u16_be(currying.len() as u16);
                for curry in currying {
                    self.write_u16_be(curry.len() as u16);
                    for item in curry {
                        match item {
                            RefArg::This => {
                                self.write_u8(bc::ref_arg::THIS);
                            }
                            RefArg::Hole => {
                                self.write_u8(bc::ref_arg::HOLE);
                            }
                            RefArg::Str(s) => {
                                self.write_u8(bc::ref_arg::STR);
                                self.write_str_ref(s);
                            }
                            RefArg::Int(n) => {
                                self.write_u8(bc::ref_arg::INT);
                                self.write_i64_be(*n);
                            }
                            RefArg::Bool(b) => {
                                self.write_u8(bc::ref_arg::BOOL);
                                self.write_u8(if *b { 1 } else { 0 });
                            }
                            RefArg::Ref(ref_path) => {
                                self.write_u8(bc::ref_arg::REF);
                                self.write_u16_be(ref_path.len() as u16);
                                for seg in ref_path {
                                    self.write_str_ref(seg);
                                }
                            }
                            RefArg::ImplParam(name) => {
                                self.write_u8(bc::ref_arg::IMPL_PARAM);
                                self.write_str_ref(name);
                            }
                        }
                    }
                }
            }
            ModelValueTypeTree::Impl { attrs } => {
                self.write_u8(bc::model_val::IMPL);
                self.write_u16_be(attrs.len() as u16);
                for a in attrs {
                    self.encode_model_set_attribute(a);
                }
            }
            ModelValueTypeTree::ImplArray => {
                self.write_u8(bc::model_val::IMPL_ARRAY);
            }
            ModelValueTypeTree::StringLiteral(s) => {
                self.write_u8(bc::model_val::STRING_LITERAL);
                self.write_str_ref(s);
            }
            ModelValueTypeTree::IntLiteral(n) => {
                self.write_u8(bc::model_val::INT_LITERAL);
                self.write_i64_be(*n);
            }
            ModelValueTypeTree::BoolLiteral(b) => {
                self.write_u8(bc::model_val::BOOL_LITERAL);
                self.write_u8(if *b { 1 } else { 0 });
            }
            ModelValueTypeTree::ArrayLiteral(items) => {
                self.write_u8(bc::model_val::ARRAY_LITERAL);
                self.write_u16_be(items.len() as u16);
                for item in items {
                    self.encode_model_value_type(item);
                }
            }
        }
    }

    fn encode_tree_context(&mut self, ctx: &TreeContext) {
        match &ctx.file {
            Some(f) => {
                self.write_u8(1);
                self.write_str_ref(f);
            }
            None => self.write_u8(0),
        }
        match ctx.line {
            Some(l) => {
                self.write_u8(1);
                self.write_u32_be(l as u32);
            }
            None => self.write_u8(0),
        }
        match ctx.position {
            Some(p) => {
                self.write_u8(1);
                self.write_u32_be(p as u32);
            }
            None => self.write_u8(0),
        }
    }
}

// ---------------------------------------------------------------------------
// BcReader — parses a binary bytecode blob
// ---------------------------------------------------------------------------

pub(super) struct BcReader {
    data: Vec<u8>,
    pos: usize,
    pool: Vec<String>,
}

impl BcReader {
    pub(super) fn new(data: &[u8]) -> Result<Self, String> {
        // Detect stale JSON-format bytecode files.
        if data.first() == Some(&b'{') {
            return Err(
                "old JSON bytecode format detected — please recompile from source".to_string(),
            );
        }
        if data.len() < 4 {
            return Err("bytecode file too short".to_string());
        }
        if data[..4] != bc::MAGIC {
            // Version mismatch (e.g. old v1 file) gives the same helpful hint.
            return Err(format!(
                "invalid or outdated bytecode (magic: {:02X} {:02X} {:02X} {:02X}) — \
                 please recompile from source (`tlang compile`)",
                data[0], data[1], data[2], data[3]
            ));
        }
        let mut r = BcReader {
            data: data.to_vec(),
            pos: 4,
            pool: Vec::new(),
        };
        // Read constant pool
        let pool_count = r.read_u32_be()? as usize;
        let mut pool = Vec::with_capacity(pool_count);
        for _ in 0..pool_count {
            let len = r.read_u16_be()? as usize;
            r.ensure(len)?;
            let s = String::from_utf8(r.data[r.pos..r.pos + len].to_vec())
                .map_err(|_| "invalid UTF-8 in constant pool entry".to_string())?;
            r.pos += len;
            pool.push(s);
        }
        r.pool = pool;
        Ok(r)
    }

    // ---- primitives --------------------------------------------------------

    fn ensure(&self, n: usize) -> Result<(), String> {
        if self.pos + n > self.data.len() {
            Err(format!(
                "unexpected end of bytecode at offset {} (need {n} bytes, {} remaining)",
                self.pos,
                self.data.len().saturating_sub(self.pos),
            ))
        } else {
            Ok(())
        }
    }

    fn read_u8(&mut self) -> Result<u8, String> {
        self.ensure(1)?;
        let v = self.data[self.pos];
        self.pos += 1;
        Ok(v)
    }

    pub(super) fn read_u16_be(&mut self) -> Result<u16, String> {
        self.ensure(2)?;
        let v = u16::from_be_bytes([self.data[self.pos], self.data[self.pos + 1]]);
        self.pos += 2;
        Ok(v)
    }

    fn read_u32_be(&mut self) -> Result<u32, String> {
        self.ensure(4)?;
        let v = u32::from_be_bytes([
            self.data[self.pos],
            self.data[self.pos + 1],
            self.data[self.pos + 2],
            self.data[self.pos + 3],
        ]);
        self.pos += 4;
        Ok(v)
    }

    fn read_i64_be(&mut self) -> Result<i64, String> {
        self.ensure(8)?;
        let v = i64::from_be_bytes([
            self.data[self.pos],
            self.data[self.pos + 1],
            self.data[self.pos + 2],
            self.data[self.pos + 3],
            self.data[self.pos + 4],
            self.data[self.pos + 5],
            self.data[self.pos + 6],
            self.data[self.pos + 7],
        ]);
        self.pos += 8;
        Ok(v)
    }

    /// Read a `str_ref` (u16 pool index) and return the interned string.
    pub(super) fn read_str(&mut self) -> Result<String, String> {
        let idx = self.read_u16_be()? as usize;
        self.pool.get(idx).cloned().ok_or_else(|| {
            format!(
                "pool index {idx} out of range (pool size {})",
                self.pool.len()
            )
        })
    }

    // ---- decoding methods --------------------------------------------------

    fn decode_block(&mut self) -> Result<Vec<Stmt>, String> {
        let count = self.read_u16_be()? as usize;
        let mut stmts = Vec::with_capacity(count);
        for _ in 0..count {
            stmts.push(self.decode_stmt()?);
        }
        Ok(stmts)
    }

    pub(super) fn decode_stmt(&mut self) -> Result<Stmt, String> {
        let opcode = self.read_u8()?;
        match opcode {
            bc::stmt::LET => {
                let name = self.read_str()?;
                let expr = self.decode_expr()?;
                Ok(Stmt::Let { name, expr })
            }
            bc::stmt::CALL => {
                let call = self.decode_call()?;
                Ok(Stmt::Call(call))
            }
            bc::stmt::IF => {
                let condition = self.decode_expr()?;
                let then_body = self.decode_block()?;
                let has_else = self.read_u8()?;
                let else_body = if has_else != 0 {
                    Some(self.decode_block()?)
                } else {
                    None
                };
                Ok(Stmt::If {
                    condition,
                    then_body,
                    else_body,
                })
            }
            bc::stmt::FOR_IN => {
                let var = self.read_str()?;
                let iterable = self.decode_expr()?;
                let body = self.decode_block()?;
                Ok(Stmt::ForIn {
                    var,
                    iterable,
                    body,
                })
            }
            bc::stmt::FOR_IN_DESTRUCTURE => {
                let count = self.read_u16_be()? as usize;
                let mut vars = Vec::with_capacity(count);
                for _ in 0..count {
                    vars.push(self.read_str()?);
                }
                let iterable = self.decode_expr()?;
                let body = self.decode_block()?;
                Ok(Stmt::ForInDestructure { vars, iterable, body })
            }
            bc::stmt::FOR_RANGE => {
                let var = self.read_str()?;
                let start = self.decode_expr()?;
                let end = self.decode_expr()?;
                let inclusive = self.read_u8()? != 0;
                let body = self.decode_block()?;
                Ok(Stmt::ForRange {
                    var,
                    start,
                    end,
                    inclusive,
                    body,
                })
            }
            bc::stmt::MATCH => {
                let target = self.decode_expr()?;
                let arm_count = self.read_u16_be()? as usize;
                let mut arms = Vec::with_capacity(arm_count);
                for _ in 0..arm_count {
                    let pattern = self.decode_match_pattern()?;
                    let body = self.decode_block()?;
                    arms.push(MatchArm { pattern, body });
                }
                let default = if self.read_u8()? != 0 {
                    Some(self.decode_block()?)
                } else {
                    None
                };
                Ok(Stmt::Match {
                    target,
                    arms,
                    default,
                })
            }
            bc::stmt::RETURN => {
                let expr = self.decode_expr()?;
                Ok(Stmt::Return(expr))
            }
            _ => Err(format!("unknown stmt opcode 0x{opcode:02X}")),
        }
    }

    fn decode_expr(&mut self) -> Result<Expr, String> {
        let opcode = self.read_u8()?;
        match opcode {
            bc::expr::LIT_UNIT => Ok(Expr::Literal(Value::Unit)),
            bc::expr::LIT_INT => Ok(Expr::Literal(Value::Int(self.read_i64_be()?))),
            bc::expr::LIT_BOOL => Ok(Expr::Literal(Value::Bool(self.read_u8()? != 0))),
            bc::expr::LIT_STRING => {
                let s = self.read_str()?;
                Ok(Expr::Literal(Value::String(s)))
            }
            bc::expr::LIT_LIST => {
                let count = self.read_u16_be()? as usize;
                let mut items = Vec::with_capacity(count);
                for _ in 0..count {
                    match self.decode_expr()? {
                        Expr::Literal(v) => items.push(v),
                        _ => return Err("expected literal inside LIT_LIST".to_string()),
                    }
                }
                Ok(Expr::Literal(Value::List(items)))
            }
            bc::expr::LIST_EXPR => {
                let count = self.read_u16_be()? as usize;
                let mut items = Vec::with_capacity(count);
                for _ in 0..count {
                    items.push(self.decode_expr()?);
                }
                Ok(Expr::ListExpr(items))
            }
            bc::expr::LIT_LEAF => {
                let field_count = self.read_u16_be()? as usize;
                let mut fields = std::collections::BTreeMap::new();
                for _ in 0..field_count {
                    let key = self.read_str()?;
                    match self.decode_expr()? {
                        Expr::Literal(v) => {
                            fields.insert(key, v);
                        }
                        _ => return Err("expected literal inside LIT_LEAF field".to_string()),
                    }
                }
                Ok(Expr::Literal(Value::Leaf(LeafObject { fields })))
            }
            bc::expr::VAR => {
                let name = self.read_str()?;
                Ok(Expr::Var(name))
            }
            bc::expr::OP => {
                let op = u8_to_op(self.read_u8()?)?;
                let lhs = self.decode_expr()?;
                let rhs = self.decode_expr()?;
                Ok(Expr::Op(op, Box::new(lhs), Box::new(rhs)))
            }
            bc::expr::CALL => {
                let call = self.decode_call()?;
                Ok(Expr::Call(call))
            }
            bc::expr::IF => {
                let condition = self.decode_expr()?;
                let then_expr = self.decode_expr()?;
                let else_expr = self.decode_expr()?;
                Ok(Expr::If {
                    condition: Box::new(condition),
                    then_expr: Box::new(then_expr),
                    else_expr: Box::new(else_expr),
                })
            }
            bc::expr::MATCH_EXPR => {
                let target = self.decode_expr()?;
                let arm_count = self.read_u16_be()? as usize;
                let mut arms = Vec::with_capacity(arm_count);
                for _ in 0..arm_count {
                    let pattern = self.decode_match_pattern()?;
                    let rhs = self.decode_expr()?;
                    arms.push((pattern, rhs));
                }
                let default = self.decode_expr()?;
                Ok(Expr::MatchExpr {
                    target: Box::new(target),
                    arms,
                    default: Box::new(default),
                })
            }
            bc::expr::LIT_FLOAT => {
                // f64 literal
                let mut buf = [0u8; 8];
                for b in &mut buf {
                    *b = self.read_u8()?;
                }
                Ok(Expr::Literal(Value::Float(f64::from_bits(
                    u64::from_be_bytes(buf),
                ))))
            }
            bc::expr::LIT_MAP => {
                // Map literal
                let field_count = self.read_u16_be()? as usize;
                let mut map = std::collections::BTreeMap::new();
                for _ in 0..field_count {
                    let key = self.read_str()?;
                    match self.decode_expr()? {
                        Expr::Literal(v) => {
                            map.insert(key, v);
                        }
                        _ => return Err("expected literal inside LIT_MAP field".to_string()),
                    }
                }
                Ok(Expr::Literal(Value::Map(map)))
            }
            bc::expr::SET_NEW => {
                // set-entity instantiation
                let entity = self.read_str()?;
                let field_count = self.read_u16_be()? as usize;
                let mut fields = Vec::with_capacity(field_count);
                for _ in 0..field_count {
                    let key = self.read_str()?;
                    let expr = self.decode_expr()?;
                    fields.push((key, Box::new(expr)));
                }
                Ok(Expr::SetNew(SetNewExpr { entity, fields }))
            }
            bc::expr::IS_TYPE => {
                let expr = self.decode_expr()?;
                let type_name = self.read_str()?;
                let negated = self.read_u8()? != 0;
                Ok(Expr::IsType {
                    expr: Box::new(expr),
                    type_name,
                    negated,
                })
            }
            bc::expr::LAMBDA => {
                let param_count = self.read_u16_be()? as usize;
                let mut params = Vec::with_capacity(param_count);
                for _ in 0..param_count {
                    params.push(self.read_str()?);
                }
                let body_kind = self.read_u8()?;
                let body = match body_kind {
                    0 => {
                        // expr body
                        let expr = self.decode_expr()?;
                        LambdaBody::Expr(Box::new(expr))
                    }
                    1 => {
                        // block body
                        let stmt_count = self.read_u16_be()? as usize;
                        let mut stmts = Vec::with_capacity(stmt_count);
                        for _ in 0..stmt_count {
                            stmts.push(self.decode_stmt()?);
                        }
                        LambdaBody::Block(stmts)
                    }
                    other => {
                        return Err(format!("unknown lambda body kind {other}"));
                    }
                };
                Ok(Expr::Lambda { params, body })
            }
            _ => Err(format!("unknown expr opcode 0x{opcode:02X}")),
        }
    }

    fn decode_call(&mut self) -> Result<CallExpr, String> {
        let target = self.read_str()?;
        let argc = self.read_u16_be()? as usize;
        let mut args = Vec::with_capacity(argc);
        for _ in 0..argc {
            args.push(self.decode_expr()?);
        }
        Ok(CallExpr { target, args, offset: 0 })
    }

    fn decode_match_pattern(&mut self) -> Result<MatchPattern, String> {
        let tag = self.read_u8()?;
        match tag {
            0 => Ok(MatchPattern::Value(self.decode_expr()?)),
            1 => Ok(MatchPattern::Condition(self.decode_expr()?)),
            2 => {
                let type_name = self.read_str()?;
                let negated = self.read_u8()? != 0;
                Ok(MatchPattern::IsType { type_name, negated })
            }
            _ => Err(format!("unknown match pattern tag 0x{tag:02X}")),
        }
    }

    pub(super) fn decode_function(&mut self) -> Result<Function, String> {
        let name = self.read_str()?;
        let param_count = self.read_u16_be()? as usize;
        let mut params = Vec::with_capacity(param_count);
        for _ in 0..param_count {
            let pname = self.read_str()?;
            let has_type = self.read_u8()?;
            let ty = if has_type != 0 {
                Some(u8_to_type_annotation(self.read_u8()?)?)
            } else {
                None
            };
            params.push((pname, ty));
        }
        let has_return = self.read_u8()?;
        let return_type = if has_return != 0 {
            Some(u8_to_type_annotation(self.read_u8()?)?)
        } else {
            None
        };
        let body = self.decode_block()?;
        let has_doc = self.read_u8()?;
        let doc = if has_doc != 0 {
            Some(self.read_str()?)
        } else {
            None
        };
        Ok(Function {
            name,
            params,
            return_type,
            body,
            offset: 0,
            source_file: None,
            source_path: None,
            doc,
        })
    }

    pub(super) fn decode_template(&mut self) -> Result<TemplateFunction, String> {
        let lang = self.read_str()?;
        let name = self.read_str()?;
        let param_count = self.read_u16_be()? as usize;
        let mut params = Vec::with_capacity(param_count);
        for _ in 0..param_count {
            let pname = self.read_str()?;
            let ty = self.read_str()?;
            params.push(TemplateParam { name: pname, ty });
        }
        let content_kind = self.read_u8()?;
        let content_str = self.read_str()?;
        let content = match content_kind {
            bc::tmpl_content::FULL => TemplateContent::Full(content_str),
            bc::tmpl_content::SPECIALIZED => TemplateContent::Specialized(content_str),
            _ => return Err(format!("unknown template content kind {content_kind}")),
        };
        Ok(TemplateFunction {
            lang,
            name,
            params,
            content,
        })
    }

    pub(super) fn decode_data_template(&mut self) -> Result<DataTemplateFunction, String> {
        let langs_count = self.read_u16_be()? as usize;
        let mut langs = Vec::with_capacity(langs_count);
        for _ in 0..langs_count {
            langs.push(self.read_str()?);
        }
        let name = self.read_str()?;
        let param_count = self.read_u16_be()? as usize;
        let mut params = Vec::with_capacity(param_count);
        for _ in 0..param_count {
            let pname = self.read_str()?;
            let ty = self.read_str()?;
            params.push(TemplateParam { name: pname, ty });
        }
        let content = self.read_str()?;
        Ok(DataTemplateFunction {
            langs,
            name,
            params,
            content,
        })
    }

    pub(super) fn decode_cmd_template(&mut self) -> Result<CmdTemplateFunction, String> {
        let langs_count = self.read_u16_be()? as usize;
        let mut langs = Vec::with_capacity(langs_count);
        for _ in 0..langs_count {
            langs.push(self.read_str()?);
        }
        let name = self.read_str()?;
        let param_count = self.read_u16_be()? as usize;
        let mut params = Vec::with_capacity(param_count);
        for _ in 0..param_count {
            let pname = self.read_str()?;
            let ty = self.read_str()?;
            params.push(TemplateParam { name: pname, ty });
        }
        let content = self.read_str()?;
        Ok(CmdTemplateFunction {
            langs,
            name,
            params,
            content,
        })
    }

    pub(super) fn decode_style_template(&mut self) -> Result<StyleTemplateFunction, String> {
        let langs_count = self.read_u16_be()? as usize;
        let mut langs = Vec::with_capacity(langs_count);
        for _ in 0..langs_count {
            langs.push(self.read_str()?);
        }
        let name = self.read_str()?;
        let param_count = self.read_u16_be()? as usize;
        let mut params = Vec::with_capacity(param_count);
        for _ in 0..param_count {
            let pname = self.read_str()?;
            let ty = self.read_str()?;
            params.push(TemplateParam { name: pname, ty });
        }
        let content = self.read_str()?;
        Ok(StyleTemplateFunction {
            langs,
            name,
            params,
            content,
        })
    }

    pub(super) fn decode_doc_template(&mut self) -> Result<DocTemplateFunction, String> {
        let langs_count = self.read_u16_be()? as usize;
        let mut langs = Vec::with_capacity(langs_count);
        for _ in 0..langs_count {
            langs.push(self.read_str()?);
        }
        let name = self.read_str()?;
        let param_count = self.read_u16_be()? as usize;
        let mut params = Vec::with_capacity(param_count);
        for _ in 0..param_count {
            let pname = self.read_str()?;
            let ty = self.read_str()?;
            params.push(TemplateParam { name: pname, ty });
        }
        let content = self.read_str()?;
        Ok(DocTemplateFunction {
            langs,
            name,
            params,
            content,
        })
    }

    pub(super) fn decode_raw_template(&mut self) -> Result<RawTemplateFunction, String> {
        let variant_byte = self.read_u8()?;
        let variant = match variant_byte {
            0 => RawVariant::AsIs,
            1 => RawVariant::Replaced,
            other => return Err(format!("unknown raw template variant byte 0x{other:02X}")),
        };
        let name = self.read_str()?;
        let param_count = self.read_u16_be()? as usize;
        let mut params = Vec::with_capacity(param_count);
        for _ in 0..param_count {
            let pname = self.read_str()?;
            let ty = self.read_str()?;
            params.push(TemplateParam { name: pname, ty });
        }
        let content = self.read_str()?;
        Ok(RawTemplateFunction {
            variant,
            name,
            params,
            content,
        })
    }

    pub(super) fn decode_model(&mut self) -> Result<ModelBlockTree, String> {
        let node_count = self.read_u16_be()? as usize;
        let mut nodes = Vec::with_capacity(node_count);
        for _ in 0..node_count {
            let kind = self.read_u8()?;
            match kind {
                bc::model_node::ASSIGN_VAR => {
                    let name = self.read_str()?;
                    let has_ty = self.read_u8()?;
                    let ty = if has_ty != 0 {
                        Some(self.decode_model_value_type()?)
                    } else {
                        None
                    };
                    let value = self.read_str()?;
                    let context = self.decode_tree_context()?;
                    nodes.push(ModelNodeTree::AssignVar(ModelAssignVarTree {
                        name,
                        ty,
                        value,
                        context,
                    }));
                }
                bc::model_node::SET_ENTITY => {
                    let name = self.read_str()?;
                    let ext_count = self.read_u16_be()? as usize;
                    let mut exts = Vec::with_capacity(ext_count);
                    for _ in 0..ext_count {
                        exts.push(self.read_str()?);
                    }
                    let has_output = self.read_u8()?;
                    let output = if has_output != 0 {
                        let mode_byte = self.read_u8()?;
                        let mode = if mode_byte == 0 {
                            crate::model_tree::WriteMode::AlwaysWrite
                        } else {
                            crate::model_tree::WriteMode::WriteOnce
                        };
                        let path = self.read_str()?;
                        Some(crate::model_tree::OutputDecl { path, mode })
                    } else {
                        None
                    };
                    let has_exec = self.read_u8()?;
                    let exec = if has_exec != 0 {
                        let executor = self.read_str()?;
                        Some(crate::model_tree::ExecDecl { executor })
                    } else {
                        None
                    };
                    let param_count = self.read_u16_be()? as usize;
                    let mut params = Vec::with_capacity(param_count);
                    for _ in 0..param_count {
                        params.push(self.decode_model_set_attribute()?);
                    }
                    let attr_count = self.read_u16_be()? as usize;
                    let mut attrs = Vec::with_capacity(attr_count);
                    for _ in 0..attr_count {
                        attrs.push(self.decode_model_set_attribute()?);
                    }
                    let context = self.decode_tree_context()?;
                    nodes.push(ModelNodeTree::SetEntity(ModelSetEntityTree {
                        name,
                        exts,
                        params,
                        attrs,
                        output,
                        exec,
                        context,
                    }));
                }
                _ => return Err(format!("unknown model node kind 0x{kind:02X}")),
            }
        }
        Ok(ModelBlockTree { nodes })
    }

    fn decode_model_set_attribute(&mut self) -> Result<ModelSetAttributeTree, String> {
        let has_attr = self.read_u8()?;
        let attr = if has_attr != 0 {
            Some(self.read_str()?)
        } else {
            None
        };
        let value = self.decode_model_value_type()?;
        let context = self.decode_tree_context()?;
        Ok(ModelSetAttributeTree {
            attr,
            value,
            context,
        })
    }

    fn decode_model_value_type(&mut self) -> Result<ModelValueTypeTree, String> {
        let kind = self.read_u8()?;
        match kind {
            bc::model_val::TYPE => Ok(ModelValueTypeTree::Type(self.read_str()?)),
            bc::model_val::ARRAY => Ok(ModelValueTypeTree::Array(self.read_str()?)),
            bc::model_val::FUNC_DEF => {
                let param_count = self.read_u16_be()? as usize;
                let mut param_types = Vec::with_capacity(param_count);
                for _ in 0..param_count {
                    param_types.push(self.read_str()?);
                }
                let ret_count = self.read_u16_be()? as usize;
                let mut ret_types = Vec::with_capacity(ret_count);
                for _ in 0..ret_count {
                    ret_types.push(self.read_str()?);
                }
                Ok(ModelValueTypeTree::FuncDef {
                    param_types,
                    ret_types,
                })
            }
            bc::model_val::REF => {
                use crate::model_tree::RefArg;
                let path_count = self.read_u16_be()? as usize;
                let mut path = Vec::with_capacity(path_count);
                for _ in 0..path_count {
                    path.push(self.read_str()?);
                }
                let curry_count = self.read_u16_be()? as usize;
                let mut currying = Vec::with_capacity(curry_count);
                for _ in 0..curry_count {
                    let item_count = self.read_u16_be()? as usize;
                    let mut items = Vec::with_capacity(item_count);
                    for _ in 0..item_count {
                        let kind = self.read_u8()?;
                        let arg = match kind {
                            bc::ref_arg::THIS => RefArg::This,
                            bc::ref_arg::HOLE => RefArg::Hole,
                            bc::ref_arg::STR => RefArg::Str(self.read_str()?),
                            bc::ref_arg::INT => RefArg::Int(self.read_i64_be()?),
                            bc::ref_arg::BOOL => {
                                let b = self.read_u8()?;
                                RefArg::Bool(b != 0)
                            }
                            bc::ref_arg::REF => {
                                let seg_count = self.read_u16_be()? as usize;
                                let mut ref_path = Vec::with_capacity(seg_count);
                                for _ in 0..seg_count {
                                    ref_path.push(self.read_str()?);
                                }
                                RefArg::Ref(ref_path)
                            }
                            bc::ref_arg::IMPL_PARAM => RefArg::ImplParam(self.read_str()?),
                            other => {
                                return Err(format!(
                                    "unknown RefArg kind 0x{other:02X} in bytecode"
                                ));
                            }
                        };
                        items.push(arg);
                    }
                    currying.push(items);
                }
                Ok(ModelValueTypeTree::Ref { path, currying })
            }
            bc::model_val::IMPL => {
                let attr_count = self.read_u16_be()? as usize;
                let mut attrs = Vec::with_capacity(attr_count);
                for _ in 0..attr_count {
                    attrs.push(self.decode_model_set_attribute()?);
                }
                Ok(ModelValueTypeTree::Impl { attrs })
            }
            bc::model_val::IMPL_ARRAY => Ok(ModelValueTypeTree::ImplArray),
            bc::model_val::STRING_LITERAL => {
                Ok(ModelValueTypeTree::StringLiteral(self.read_str()?))
            }
            bc::model_val::INT_LITERAL => Ok(ModelValueTypeTree::IntLiteral(self.read_i64_be()?)),
            bc::model_val::BOOL_LITERAL => {
                let b = self.read_u8()?;
                Ok(ModelValueTypeTree::BoolLiteral(b != 0))
            }
            bc::model_val::ARRAY_LITERAL => {
                let count = self.read_u16_be()? as usize;
                let mut items = Vec::with_capacity(count);
                for _ in 0..count {
                    items.push(self.decode_model_value_type()?);
                }
                Ok(ModelValueTypeTree::ArrayLiteral(items))
            }
            bc::model_val::GENERIC => {
                let name = self.read_str()?;
                let param_count = self.read_u16_be()? as usize;
                let mut params = Vec::with_capacity(param_count);
                for _ in 0..param_count {
                    params.push(self.read_str()?);
                }
                Ok(ModelValueTypeTree::Generic { name, params })
            }
            _ => Err(format!("unknown ModelValueType kind 0x{kind:02X}")),
        }
    }

    fn decode_tree_context(&mut self) -> Result<TreeContext, String> {
        let has_file = self.read_u8()?;
        let file = if has_file != 0 {
            Some(self.read_str()?)
        } else {
            None
        };
        let has_line = self.read_u8()?;
        let line = if has_line != 0 {
            Some(self.read_u32_be()? as usize)
        } else {
            None
        };
        let has_pos = self.read_u8()?;
        let position = if has_pos != 0 {
            Some(self.read_u32_be()? as usize)
        } else {
            None
        };
        Ok(TreeContext {
            file,
            line,
            position,
        })
    }
}
