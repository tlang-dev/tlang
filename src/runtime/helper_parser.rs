// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Lexer and recursive-descent parser for TLang helper code blocks.
//!
//! Helper blocks (`{ … }` sections in `.tlang` files) contain a small
//! imperative language: `let` bindings, `if`/`for`/`match` statements,
//! function definitions, and expressions. This module converts the raw block
//! text into a `Vec<Stmt>` IR tree that the [`super`] executor evaluates.
//!
//! This parser is separate from the Pest-based file parser ([`crate::parser`])
//! because helper blocks use a simpler, embeddable grammar that is processed
//! inline during compilation rather than upfront.
//!
//! # Pipeline
//!
//! ```text
//! raw block text
//!   → lex_with_offsets()  → Vec<(Token, byte_offset)>
//!   → HelperParser::parse_program()  → Vec<Stmt>
//! ```
//!
//! # Key types
//!
//! - [`Token`] — the lexical token enum.
//! - [`HelperParser`] — stateful recursive-descent parser; call
//!   [`HelperParser::parse_program`] to parse a full helper block.
//! - [`lex_with_offsets`] — tokenises a helper block string, attaching the
//!   byte offset of each token for precise error reporting.

use super::*;

#[derive(Debug, Clone, PartialEq, Eq)]
pub(super) enum Token {
    Ident(String),
    Number(i64),
    String(String),
    LParen,
    RParen,
    LBrace,
    RBrace,
    LBracket,
    RBracket,
    Comma,
    Semi,
    Assign,
    FatArrow,
    Dot,
    QuestionDot,
    QuestionQuestion,
    Colon,
    // operators
    Bang,
    EqEq,
    BangEq,
    Lt,
    Gt,
    LtEq,
    GtEq,
    Amp,
    AmpAmp,
    PipePipe,
    Plus,
    PlusAssign,
    Minus,
    Star,
    Slash,
    Percent,
    /// A `/** ... */` doc-comment block, with content already cleaned.
    DocComment(String),
}

fn token_display(token: &Token) -> String {
    match token {
        Token::Ident(s) => s.clone(),
        Token::Number(n) => n.to_string(),
        Token::String(s) => format!("\"{s}\""),
        Token::LParen => "(".to_string(),
        Token::RParen => ")".to_string(),
        Token::LBrace => "{".to_string(),
        Token::RBrace => "}".to_string(),
        Token::LBracket => "[".to_string(),
        Token::RBracket => "]".to_string(),
        Token::Comma => ",".to_string(),
        Token::Semi => ";".to_string(),
        Token::Assign => "=".to_string(),
        Token::FatArrow => "=>".to_string(),
        Token::Dot => ".".to_string(),
        Token::QuestionDot => "?.".to_string(),
        Token::QuestionQuestion => "??".to_string(),
        Token::Colon => ":".to_string(),
        Token::Bang => "!".to_string(),
        Token::Amp => "&".to_string(),
        Token::Lt => "<".to_string(),
        Token::Gt => ">".to_string(),
        Token::LtEq => "<=".to_string(),
        Token::GtEq => ">=".to_string(),
        Token::EqEq => "==".to_string(),
        Token::BangEq => "!=".to_string(),
        Token::AmpAmp => "&&".to_string(),
        Token::PipePipe => "||".to_string(),
        Token::Plus => "+".to_string(),
        Token::PlusAssign => "+=".to_string(),
        Token::Minus => "-".to_string(),
        Token::Star => "*".to_string(),
        Token::Slash => "/".to_string(),
        Token::Percent => "%".to_string(),
        Token::DocComment(s) => format!("/** {s} */"),
    }
}

pub(super) struct HelperParser {
    tokens: Vec<Token>,
    /// Byte offset of each token within the original input string.
    /// Parallel to `tokens` — always the same length.
    offsets: Vec<usize>,
    pos: usize,
    /// Base offset added to all token offsets (used when the helper content
    /// was extracted from a larger source document).
    base_offset: usize,
}

impl HelperParser {
    pub(super) fn new(input: &str) -> Result<Self, CompileError> {
        let (tokens, offsets) = lex_with_offsets(input)?;
        Ok(Self {
            tokens,
            offsets,
            pos: 0,
            base_offset: 0,
        })
    }

    pub(super) fn new_with_base(base_offset: usize, input: &str) -> Result<Self, CompileError> {
        let (tokens, offsets) = lex_with_offsets(input)?;
        Ok(Self {
            tokens,
            offsets,
            pos: 0,
            base_offset,
        })
    }

    /// Return the absolute byte offset of the current token in the original
    /// source document (base_offset + local token offset).
    fn current_abs_offset(&self) -> usize {
        self.offsets
            .get(self.pos)
            .copied()
            .unwrap_or(0)
            .saturating_add(self.base_offset)
    }

    pub(super) fn parse_program(&mut self) -> Result<CompiledProgram, CompileError> {
        self.expect_token(Token::LBrace)?;
        let mut functions = HashMap::new();
        let mut top_level_stmts = Vec::new();
        let mut pending_doc: Option<String> = None;
        while !self.check(&Token::RBrace) {
            if let Some(Token::DocComment(doc)) = self.peek().cloned() {
                self.pos += 1;
                pending_doc = Some(doc);
            } else if self.peek_keyword("func") {
                let function = self.parse_function(None, None, pending_doc.take())?;
                if functions.insert(function.name.clone(), function).is_some() {
                    return Err(CompileError("duplicate function definition".to_string()));
                }
            } else if self.peek_keyword("let") {
                pending_doc = None;
                let stmt = self.parse_stmt()?;
                top_level_stmts.push(stmt);
            } else {
                return Err(CompileError(format!(
                    "expected `func` or `let`, got {:?}",
                    self.peek()
                )));
            }
        }
        self.expect_token(Token::RBrace)?;
        if self.pos != self.tokens.len() {
            return Err(CompileError("unexpected trailing tokens".to_string()));
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

    /// Parse all function definitions from this helper block and insert them
    /// Parse all function definitions from this helper block and insert them into `dest`.
    /// Duplicate function names within the same helper block are an error.
    /// Across blocks (different files), last-wins override semantics apply.
    pub(super) fn parse_functions_into(
        &mut self,
        dest: &mut HashMap<String, Function>,
        top_level_dest: &mut Vec<Stmt>,
    ) -> Result<(), CompileError> {
        // Track names defined within this single block to catch same-file duplicates.
        let mut block_names: std::collections::HashSet<String> = std::collections::HashSet::new();
        let mut pending_doc: Option<String> = None;
        self.expect_token(Token::LBrace)?;
        while !self.check(&Token::RBrace) {
            if let Some(Token::DocComment(doc)) = self.peek().cloned() {
                self.pos += 1;
                pending_doc = Some(doc);
            } else if self.peek_keyword("func") {
                let function = self.parse_function(None, None, pending_doc.take())?;
                if !block_names.insert(function.name.clone()) {
                    return Err(CompileError(format!(
                        "duplicate function `{}` in this file — each function name must be unique (at_offset: {})",
                        function.name, function.offset
                    )));
                }
                dest.insert(function.name.clone(), function);
            } else if self.peek_keyword("let") {
                pending_doc = None;
                let stmt = self.parse_stmt()?;
                top_level_dest.push(stmt);
            } else {
                return Err(CompileError(format!(
                    "expected `func` or `let`, got {:?}",
                    self.peek()
                )));
            }
        }
        self.expect_token(Token::RBrace)?;
        if self.pos != self.tokens.len() {
            return Err(CompileError("unexpected trailing tokens".to_string()));
        }
        Ok(())
    }

    pub(super) fn parse_function(
        &mut self,
        source_file: Option<&str>,
        source_path: Option<&str>,
        doc: Option<String>,
    ) -> Result<Function, CompileError> {
        // Record the offset of the `func` keyword before consuming it.
        let func_offset = self.current_abs_offset();
        self.expect_keyword("func")?;
        let name = self.expect_ident()?;
        self.expect_token(Token::LParen)?;
        let mut params = Vec::new();
        if !self.check(&Token::RParen) {
            loop {
                let param_name = self.expect_ident()?;
                let type_ann = if self.consume(&Token::Colon) {
                    Some(self.parse_type_annotation()?)
                } else {
                    None
                };
                params.push((param_name, type_ann));
                if self.consume(&Token::Comma) {
                    continue;
                }
                break;
            }
        }
        self.expect_token(Token::RParen)?;
        let return_type = if self.consume(&Token::Colon) {
            Some(self.parse_type_annotation()?)
        } else {
            None
        };
        let body = self.parse_block()?;
        Ok(Function {
            name,
            params,
            return_type,
            body,
            offset: func_offset,
            source_file: source_file.map(|s| s.to_string()),
            source_path: source_path.map(|s| s.to_string()),
            doc,
        })
    }

    fn parse_type_annotation(&mut self) -> Result<TypeAnnotation, CompileError> {
        let type_name = self.expect_ident()?;
        // Consume optional `<T>` / `<A, B>` generic parameters (e.g. `List<String>`).
        // TLang does not use the type parameters at runtime, so we just skip them.
        if self.check(&Token::Lt) {
            self.pos += 1; // consume `<`
            let mut depth = 1usize;
            while depth > 0 {
                match self.tokens.get(self.pos) {
                    None => {
                        return Err(CompileError(
                            "unclosed `<` in type annotation".to_string(),
                        ))
                    }
                    Some(Token::Lt) => {
                        depth += 1;
                        self.pos += 1;
                    }
                    Some(Token::Gt) => {
                        depth -= 1;
                        self.pos += 1;
                    }
                    _ => {
                        self.pos += 1;
                    }
                }
            }
        }
        // Consume optional `[]` suffix (array types like `String[]`, `Int[]`).
        // We treat them all as `List` at the type-annotation level.
        if self.check(&Token::LBracket) {
            self.pos += 1;
            self.expect_token(Token::RBracket)?;
            return Ok(TypeAnnotation::List);
        }
        match type_name.as_str() {
            "Int" | "Long" | "Number" => Ok(TypeAnnotation::Int),
            "Float" | "Double" => Ok(TypeAnnotation::Float),
            "Bool" | "Boolean" => Ok(TypeAnnotation::Bool),
            "String" => Ok(TypeAnnotation::String),
            "List" => Ok(TypeAnnotation::List),
            "Map" => Ok(TypeAnnotation::Map),
            "Unit" | "Void" => Ok(TypeAnnotation::Unit),
            "Leaf" => Ok(TypeAnnotation::Leaf),
            "TmplLang" => Ok(TypeAnnotation::TmplLang),
            "TmplDoc" => Ok(TypeAnnotation::TmplDoc),
            "TmplStyle" => Ok(TypeAnnotation::TmplStyle),
            "TmplData" => Ok(TypeAnnotation::TmplData),
            "TmplCmd" => Ok(TypeAnnotation::TmplCmd),
            // Callable / lambda type annotation.
            "Func" | "Function" | "Callable" => Ok(TypeAnnotation::Func),
            // Any other identifier (e.g. a user-defined type name) is accepted
            // but treated as Unknown by the type-checker.
            _ => Ok(TypeAnnotation::String),
        }
    }

    fn parse_block(&mut self) -> Result<Vec<Stmt>, CompileError> {
        self.expect_token(Token::LBrace)?;
        let mut body = Vec::new();
        while !self.check(&Token::RBrace) {
            body.push(self.parse_stmt()?);
        }
        self.expect_token(Token::RBrace)?;
        Ok(body)
    }

    fn parse_stmt(&mut self) -> Result<Stmt, CompileError> {
        // x += expr  — desugar to  let x = x + expr
        if matches!(self.peek(), Some(Token::Ident(_)))
            && matches!(self.tokens.get(self.pos + 1), Some(Token::PlusAssign))
        {
            let name = self.expect_ident()?;
            self.expect_token(Token::PlusAssign)?;
            let rhs = self.parse_expr()?;
            self.consume(&Token::Semi);
            let expr = Expr::Op(Op::Add, Box::new(Expr::Var(name.clone())), Box::new(rhs));
            return Ok(Stmt::Let { name, expr });
        }

        if self.peek_keyword("let") {
            self.expect_keyword("let")?;
            let name = self.expect_ident()?;
            self.expect_token(Token::Assign)?;
            let expr = self.parse_expr()?;
            self.consume(&Token::Semi);
            return Ok(Stmt::Let { name, expr });
        }

        // if ( cond ) { then } else? { else }
        if self.peek_keyword("if") {
            self.expect_keyword("if")?;
            self.expect_token(Token::LParen)?;
            let condition = self.parse_expr()?;
            self.expect_token(Token::RParen)?;
            let then_body = self.parse_block()?;
            let else_body = if self.peek_keyword("else") {
                self.expect_keyword("else")?;
                if self.peek_keyword("if") {
                    // `else if` — parse the nested if statement and wrap it in
                    // a single-element block so the AST stays uniform: every
                    // else branch is always a `Vec<Stmt>`.
                    let nested_if = self.parse_stmt()?;
                    Some(vec![nested_if])
                } else {
                    Some(self.parse_block()?)
                }
            } else {
                None
            };
            return Ok(Stmt::If {
                condition,
                then_body,
                else_body,
            });
        }

        if self.peek_keyword("match") {
            self.expect_keyword("match")?;
            self.expect_token(Token::LParen)?;
            let target = self.parse_expr()?;
            self.expect_token(Token::RParen)?;
            self.expect_token(Token::LBrace)?;
            let mut arms = Vec::new();
            let mut default = None;

            while !self.check(&Token::RBrace) {
                if self.peek_keyword("case") {
                    self.expect_keyword("case")?;
                    let pattern = self.parse_match_pattern()?;
                    self.expect_token(Token::FatArrow)?;
                    let body = self.parse_match_arm_body()?;
                    arms.push(MatchArm { pattern, body });
                    continue;
                }
                if self.peek_keyword("default") {
                    self.expect_keyword("default")?;
                    self.expect_token(Token::FatArrow)?;
                    default = Some(self.parse_match_arm_body()?);
                    continue;
                }
                return Err(CompileError(format!(
                    "expected `case` or `default` in match block, got {:?}",
                    self.peek()
                )));
            }

            self.expect_token(Token::RBrace)?;
            return Ok(Stmt::Match {
                target,
                arms,
                default,
            });
        }

        // for ( var in collection ) { body }
        // for ( var start to|until end ) { body }
        // for ( [a, b, c] in collection ) { body }  — destructuring
        if self.peek_keyword("for") {
            self.expect_keyword("for")?;
            self.expect_token(Token::LParen)?;

            // Destructuring: for ( [a, b, c] in collection )
            if self.check(&Token::LBracket) {
                self.pos += 1;
                let mut vars = Vec::new();
                if !self.check(&Token::RBracket) {
                    loop {
                        vars.push(self.expect_ident()?);
                        if !self.consume(&Token::Comma) {
                            break;
                        }
                        if self.check(&Token::RBracket) {
                            break;
                        }
                    }
                }
                self.expect_token(Token::RBracket)?;
                self.expect_keyword("in")?;
                let iterable = self.parse_expr()?;
                self.expect_token(Token::RParen)?;
                let body = self.parse_block()?;
                return Ok(Stmt::ForInDestructure { vars, iterable, body });
            }

            let var = self.expect_ident()?;

            // Check what comes next: if it's `in` immediately → ForIn with no start.
            // Otherwise parse start expression, then expect `in`/`to`/`until`.
            if self.peek_keyword("in") {
                self.expect_keyword("in")?;
                let iterable = self.parse_expr()?;
                self.expect_token(Token::RParen)?;
                let body = self.parse_block()?;
                return Ok(Stmt::ForIn {
                    var,
                    iterable,
                    body,
                });
            }

            // start expression is present
            let start = self.parse_expr()?;
            let kind = if self.peek_keyword("to") {
                self.expect_keyword("to")?;
                "to"
            } else if self.peek_keyword("until") {
                self.expect_keyword("until")?;
                "until"
            } else if self.peek_keyword("in") {
                // `for ( var start in collection )` — start is an index hint,
                // treat as plain ForIn (start unused, matches grammar's start=operation?)
                self.expect_keyword("in")?;
                let iterable = self.parse_expr()?;
                self.expect_token(Token::RParen)?;
                let body = self.parse_block()?;
                return Ok(Stmt::ForIn {
                    var,
                    iterable,
                    body,
                });
            } else {
                return Err(CompileError(format!(
                    "expected `in`, `to`, or `until` in for loop, got {:?}",
                    self.peek()
                )));
            };
            let end = self.parse_expr()?;
            self.expect_token(Token::RParen)?;
            let body = self.parse_block()?;
            let inclusive = kind == "to";
            return Ok(Stmt::ForRange {
                var,
                start,
                end,
                inclusive,
                body,
            });
        }

        if self.peek_keyword("return") {
            self.expect_keyword("return")?;
            let expr = self.parse_expr()?;
            self.consume(&Token::Semi);
            return Ok(Stmt::Return(expr));
        }

        if matches!(self.peek(), Some(Token::Ident(_))) {
            let expr = self.parse_expr()?;
            self.consume(&Token::Semi);
            if let Expr::Call(call) = expr {
                return Ok(Stmt::Call(call));
            }
            return Err(CompileError(
                "only function calls are allowed as expression statements".to_string(),
            ));
        }

        // List literal as expression statement is not allowed, but a list can
        // appear on the right-hand side of a let binding.
        Err(CompileError(format!(
            "unknown statement starting at token {:?}",
            self.peek()
        )))
    }

    fn parse_match_pattern(&mut self) -> Result<MatchPattern, CompileError> {
        if self.peek_keyword("is") {
            self.expect_keyword("is")?;
            let type_name = self.expect_ident()?;
            return Ok(MatchPattern::IsType {
                type_name,
                negated: false,
            });
        }
        if self.check(&Token::Bang) && self.peek_next_keyword("is") {
            self.expect_token(Token::Bang)?;
            self.expect_keyword("is")?;
            let type_name = self.expect_ident()?;
            return Ok(MatchPattern::IsType {
                type_name,
                negated: true,
            });
        }

        let expr = self.parse_expr()?;
        if expr_uses_match_target_placeholder(&expr) {
            Ok(MatchPattern::Condition(expr))
        } else {
            Ok(MatchPattern::Value(expr))
        }
    }

    fn parse_match_arm_body(&mut self) -> Result<Vec<Stmt>, CompileError> {
        if self.check(&Token::LBrace) {
            return self.parse_block();
        }
        Ok(vec![self.parse_stmt()?])
    }

    /// Parse an expression using precedence-climbing (Pratt-style).
    ///
    /// Operator precedence (lowest → highest):
    ///   1. `??`              — null coalescing
    ///   2. `||`              — logical or
    ///   3. `&&`              — logical and
    ///   4. `==` `!=` `<` `>` `<=` `>=` — comparison (non-associative in intent, left-assoc here)
    ///   5. `+` `-`           — additive, left-associative
    ///   6. `*` `/` `%`       — multiplicative, left-associative
    fn parse_expr(&mut self) -> Result<Expr, CompileError> {
        self.parse_if_expr()
    }

    fn parse_if_expr(&mut self) -> Result<Expr, CompileError> {
        if self.peek_keyword("if") {
            self.expect_keyword("if")?;
            self.expect_token(Token::LParen)?;
            let condition = self.parse_expr()?;
            self.expect_token(Token::RParen)?;
            let then_expr = self.parse_expr()?;
            self.expect_keyword("else")?;
            let else_expr = self.parse_if_expr()?;
            return Ok(Expr::If {
                condition: Box::new(condition),
                then_expr: Box::new(then_expr),
                else_expr: Box::new(else_expr),
            });
        }
        self.parse_match_expr()
    }

    fn parse_match_expr(&mut self) -> Result<Expr, CompileError> {
        if self.peek_keyword("match") {
            self.expect_keyword("match")?;
            self.expect_token(Token::LParen)?;
            let target = self.parse_expr()?;
            self.expect_token(Token::RParen)?;
            self.expect_token(Token::LBrace)?;

            let mut arms: Vec<(MatchPattern, Expr)> = Vec::new();
            let mut default: Option<Expr> = None;
            loop {
                if self.check(&Token::RBrace) {
                    break;
                }
                if self.peek_keyword("case") {
                    self.expect_keyword("case")?;
                    let pattern = self.parse_match_pattern()?;
                    self.expect_token(Token::FatArrow)?;
                    let rhs = self.parse_expr()?;
                    arms.push((pattern, rhs));
                } else if self.peek_keyword("default") {
                    self.expect_keyword("default")?;
                    self.expect_token(Token::FatArrow)?;
                    default = Some(self.parse_expr()?);
                } else {
                    return Err(CompileError(
                        "expected `case` or `default` in match expression".to_string(),
                    ));
                }

                if self.consume(&Token::Comma) {
                    continue;
                }
                if self.check(&Token::RBrace) {
                    break;
                }
                return Err(CompileError(
                    "expected `,` or `}` after match expression arm".to_string(),
                ));
            }
            self.expect_token(Token::RBrace)?;

            let default = default.ok_or_else(|| {
                CompileError("match expression requires a `default` arm".to_string())
            })?;

            return Ok(Expr::MatchExpr {
                target: Box::new(target),
                arms,
                default: Box::new(default),
            });
        }

        self.parse_null_coalesce()
    }

    fn parse_null_coalesce(&mut self) -> Result<Expr, CompileError> {
        self.parse_or().and_then(|mut lhs| {
            while self.consume(&Token::QuestionQuestion) {
                let rhs = self.parse_or()?;
                lhs = Expr::Op(Op::NullCoalesce, Box::new(lhs), Box::new(rhs));
            }
            Ok(lhs)
        })
    }

    fn parse_or(&mut self) -> Result<Expr, CompileError> {
        let mut lhs = self.parse_and()?;
        while self.check(&Token::PipePipe) || self.peek_keyword("or") {
            if self.check(&Token::PipePipe) {
                self.pos += 1;
            } else {
                self.expect_keyword("or")?;
            }
            let rhs = self.parse_and()?;
            lhs = Expr::Op(Op::Or, Box::new(lhs), Box::new(rhs));
        }
        Ok(lhs)
    }

    fn parse_and(&mut self) -> Result<Expr, CompileError> {
        let mut lhs = self.parse_comparison()?;
        while self.check(&Token::AmpAmp) || self.peek_keyword("and") {
            if self.check(&Token::AmpAmp) {
                self.pos += 1;
            } else {
                self.expect_keyword("and")?;
            }
            let rhs = self.parse_comparison()?;
            lhs = Expr::Op(Op::And, Box::new(lhs), Box::new(rhs));
        }
        Ok(lhs)
    }

    fn parse_comparison(&mut self) -> Result<Expr, CompileError> {
        let mut lhs = self.parse_addition()?;
        loop {
            if self.peek_keyword("is") {
                self.expect_keyword("is")?;
                let type_name = self.expect_ident()?;
                lhs = Expr::IsType {
                    expr: Box::new(lhs),
                    type_name,
                    negated: false,
                };
                continue;
            }
            if self.check(&Token::Bang) && self.peek_next_keyword("is") {
                self.expect_token(Token::Bang)?;
                self.expect_keyword("is")?;
                let type_name = self.expect_ident()?;
                lhs = Expr::IsType {
                    expr: Box::new(lhs),
                    type_name,
                    negated: true,
                };
                continue;
            }
            let op = match self.peek() {
                Some(Token::EqEq) => Op::Eq,
                Some(Token::BangEq) => Op::Ne,
                Some(Token::Lt) => Op::Lt,
                Some(Token::Gt) => Op::Gt,
                Some(Token::LtEq) => Op::Lte,
                Some(Token::GtEq) => Op::Gte,
                _ => break,
            };
            self.pos += 1;
            let rhs = self.parse_addition()?;
            lhs = Expr::Op(op, Box::new(lhs), Box::new(rhs));
        }
        Ok(lhs)
    }

    fn parse_addition(&mut self) -> Result<Expr, CompileError> {
        let mut lhs = self.parse_multiplication()?;
        loop {
            let op = match self.peek() {
                Some(Token::Plus) => Op::Add,
                Some(Token::Minus) => Op::Sub,
                _ => break,
            };
            self.pos += 1;
            let rhs = self.parse_multiplication()?;
            lhs = Expr::Op(op, Box::new(lhs), Box::new(rhs));
        }
        Ok(lhs)
    }

    fn parse_multiplication(&mut self) -> Result<Expr, CompileError> {
        let mut lhs = self.parse_unary_expr()?;
        loop {
            let op = match self.peek() {
                Some(Token::Star) => Op::Mul,
                Some(Token::Slash) => Op::Div,
                Some(Token::Percent) => Op::Mod,
                _ => break,
            };
            self.pos += 1;
            let rhs = self.parse_unary_expr()?;
            lhs = Expr::Op(op, Box::new(lhs), Box::new(rhs));
        }
        Ok(lhs)
    }

    /// Parse a primary/unary expression (no infix operators consumed here).
    fn parse_unary_expr(&mut self) -> Result<Expr, CompileError> {
        // Function reference: `&funcName` — syntactic sugar for the string literal
        // `"funcName"`.  Used primarily with FuncDef constructor params so that
        // `SaveStrategy(generate: &generateWithUserId)` is equivalent to
        // `SaveStrategy(generate: "generateWithUserId")` but is validated
        // at the call site (the name must be a valid identifier) and is
        // consistent with the `&ref` syntax already accepted in model body attrs.
        if self.consume(&Token::Amp) {
            // Support dotted module-qualified paths like `&Template.leader` in
            // addition to bare identifiers like `&myFunc`.  The full path is
            // joined with `.` and stored as a string literal exactly as a bare
            // `&name` would be.
            let (path, _) = self.parse_path()?;
            return Ok(Expr::Literal(Value::String(path)));
        }

        // Integer literal
        if let Some(Token::Number(v)) = self.peek() {
            let out = Expr::Literal(Value::Int(*v));
            self.pos += 1;
            return Ok(out);
        }

        // Negative integer literal: `-N`
        if self.check(&Token::Minus)
            && let Some(Token::Number(v)) = self.tokens.get(self.pos + 1)
        {
            let neg = -v;
            self.pos += 2;
            return Ok(Expr::Literal(Value::Int(neg)));
        }
        // Otherwise treat `-` as a subtraction operator (will be picked up
        // by the infix loop in parse_expr).

        // String literal
        if let Some(Token::String(v)) = self.peek() {
            let out = Expr::Literal(Value::String(v.clone()));
            self.pos += 1;
            return Ok(out);
        }

        // Bool literals
        if self.peek_keyword("true") {
            self.expect_keyword("true")?;
            return Ok(Expr::Literal(Value::Bool(true)));
        }
        if self.peek_keyword("false") {
            self.expect_keyword("false")?;
            return Ok(Expr::Literal(Value::Bool(false)));
        }

        // List expression: `[expr, expr, ...]`
        // Items may be any expression — literals, variables, calls, set
        // instantiations, etc.  If every item happens to be a literal it is
        // folded into the cheaper `Expr::Literal(Value::List)` at parse time;
        // otherwise an `Expr::ListExpr` is produced so each element is
        // evaluated at runtime.
        if self.check(&Token::LBracket) {
            self.pos += 1;
            let mut items = Vec::new();
            if !self.check(&Token::RBracket) {
                loop {
                    items.push(self.parse_expr()?);
                    if self.consume(&Token::Comma) {
                        continue;
                    }
                    break;
                }
            }
            self.expect_token(Token::RBracket)?;
            // Fold into a plain list literal when all items are compile-time constants.
            let all_literal = items.iter().all(|e| matches!(e, Expr::Literal(_)));
            if all_literal {
                let vals = items
                    .into_iter()
                    .map(|e| match e {
                        Expr::Literal(v) => v,
                        _ => unreachable!(),
                    })
                    .collect();
                return Ok(Expr::Literal(Value::List(vals)));
            }
            return Ok(Expr::ListExpr(items));
        }

        // Lambda expression: `(params) => expr` or `(params) => { block }`.
        // Uses lookahead to distinguish from a parenthesised expression `(expr)`.
        if self.check(&Token::LParen) && self.lookahead_is_lambda() {
            return self.parse_lambda();
        }

        // Parenthesised expression: `( expr )`
        if self.check(&Token::LParen) {
            self.pos += 1;
            let inner = self.parse_expr()?;
            self.expect_token(Token::RParen)?;
            return Ok(inner);
        }

        // Prefix/named operator calls kept for backwards compatibility:
        // `add(a, b)`, `eq(a, b)`, etc.  These are indistinguishable from
        // normal function calls at the identifier stage, so we fall through to
        // the path/call parsing below.

        // Identifier, dotted path, or function call / set-entity instantiation
        let call_offset = self.current_abs_offset();
        let (path, _has_dot) = self.parse_path()?;
        if self.consume(&Token::LParen) {
            // Set-entity instantiation: `EntityName(key: val, key2: val2, ...)`.
            // Detected via lookahead: the first token after `(` is an identifier
            // immediately followed by `:`.  This is unambiguous because `:` is not
            // a valid infix operator in TLang expressions.
            if self.lookahead_is_named_arg() {
                let mut fields: Vec<(String, Box<Expr>)> = Vec::new();
                if !self.check(&Token::RParen) {
                    loop {
                        if self.peek_keyword("impl") {
                            return Err(CompileError(
                                "`impl` is not valid in a leaf instantiation — remove the `impl` prefix from the argument".to_string()
                            ));
                        }
                        let key = self.expect_ident()?;
                        self.expect_token(Token::Colon)?;
                        let val = self.parse_expr()?;
                        fields.push((key, Box::new(val)));
                        if !self.consume(&Token::Comma) {
                            break;
                        }
                        // Allow trailing comma before closing `)`
                        if self.check(&Token::RParen) {
                            break;
                        }
                    }
                }
                self.expect_token(Token::RParen)?;
                return Ok(Expr::SetNew(SetNewExpr {
                    entity: path,
                    fields,
                }));
            }

            // Regular function / template call
            let mut args = Vec::new();
            if !self.check(&Token::RParen) {
                loop {
                    args.push(self.parse_expr()?);
                    if self.consume(&Token::Comma) {
                        continue;
                    }
                    break;
                }
            }
            self.expect_token(Token::RParen)?;
            return Ok(Expr::Call(CallExpr { target: path, args, offset: call_offset }));
        }

        Ok(Expr::Var(path))
    }

    /// Returns `true` when the token stream at `self.pos` opens a named-argument
    /// set-entity instantiation (`key: val`).
    ///
    /// Also matches the illegal `impl key: val` form so the parser enters the
    /// named-arg branch and can emit a clear "impl is not valid here" error
    /// rather than a confusing parse failure.
    fn lookahead_is_named_arg(&self) -> bool {
        // key: val
        if matches!(
            (self.tokens.get(self.pos), self.tokens.get(self.pos + 1)),
            (Some(Token::Ident(_)), Some(Token::Colon))
        ) {
            return true;
        }
        // impl key: val — detected so we can error precisely inside the loop
        matches!(
            (self.tokens.get(self.pos), self.tokens.get(self.pos + 1), self.tokens.get(self.pos + 2)),
            (Some(Token::Ident(kw)), Some(Token::Ident(_)), Some(Token::Colon))
            if kw == "impl"
        )
    }

    /// Returns `true` when the token stream starting at `self.pos` matches the
    /// lambda lookahead pattern: `(ident, ident, ...) =>`.
    ///
    /// This distinguishes `(params) => body` from a parenthesised expression
    /// `(expr)`.  Only the outermost `(…)` depth-0 paren is inspected; nested
    /// parens are skipped.  A `=>` immediately after the closing `)` confirms
    /// the lambda form.
    fn lookahead_is_lambda(&self) -> bool {
        // We must be sitting on `(`.
        if !matches!(self.tokens.get(self.pos), Some(Token::LParen)) {
            return false;
        }
        let mut depth: usize = 0;
        let mut i = self.pos;
        while i < self.tokens.len() {
            match &self.tokens[i] {
                Token::LParen => depth += 1,
                Token::RParen => {
                    depth -= 1;
                    if depth == 0 {
                        // Check next token for `=>`.
                        return matches!(self.tokens.get(i + 1), Some(Token::FatArrow));
                    }
                }
                _ => {}
            }
            i += 1;
        }
        false
    }

    /// Parse a lambda expression: `(param, …) => expr` or `(param, …) => { stmts }`.
    ///
    /// Called after [`lookahead_is_lambda`] has confirmed the `=> ` pattern.
    fn parse_lambda(&mut self) -> Result<Expr, CompileError> {
        self.expect_token(Token::LParen)?;
        let mut params = Vec::new();
        if !self.check(&Token::RParen) {
            loop {
                let param = self.expect_ident()?;
                params.push(param);
                if self.consume(&Token::Comma) {
                    // Allow trailing comma before `)`
                    if self.check(&Token::RParen) {
                        break;
                    }
                    continue;
                }
                break;
            }
        }
        self.expect_token(Token::RParen)?;
        self.expect_token(Token::FatArrow)?;

        // Body: `{ stmts }` block or a single expression.
        let body = if self.check(&Token::LBrace) {
            let stmts = self.parse_block()?;
            LambdaBody::Block(stmts)
        } else {
            let expr = self.parse_expr()?;
            LambdaBody::Expr(Box::new(expr))
        };

        Ok(Expr::Lambda { params, body })
    }

    fn parse_path(&mut self) -> Result<(String, bool), CompileError> {
        let mut path = self.expect_ident()?;
        let mut has_dot = false;
        while self.check(&Token::Dot) || self.check(&Token::QuestionDot) {
            let optional = if self.consume(&Token::QuestionDot) {
                true
            } else {
                self.expect_token(Token::Dot)?;
                false
            };
            let part = self.expect_ident()?;
            if optional {
                path.push_str("?.");
            } else {
                path.push('.');
            }
            path.push_str(&part);
            has_dot = true;
        }
        Ok((path, has_dot))
    }

    /// Recognise an infix operator token at the current position.
    fn peek(&self) -> Option<&Token> {
        self.tokens.get(self.pos)
    }

    fn check(&self, token: &Token) -> bool {
        self.peek().map(|t| t == token).unwrap_or(false)
    }

    fn consume(&mut self, token: &Token) -> bool {
        if self.check(token) {
            self.pos += 1;
            true
        } else {
            false
        }
    }

    fn expect_token(&mut self, token: Token) -> Result<(), CompileError> {
        if self.consume(&token) {
            Ok(())
        } else {
            let offset = self.current_abs_offset();
            let found = self.peek().map(token_display).unwrap_or_else(|| "end of input".to_string());
            Err(CompileError(format!(
                "syntax error: expected `{}`, found `{}` (at_offset: {offset})",
                token_display(&token),
                found
            )))
        }
    }

    fn expect_ident(&mut self) -> Result<String, CompileError> {
        match self.peek().cloned() {
            Some(Token::Ident(v)) => {
                self.pos += 1;
                Ok(v)
            }
            other => Err(CompileError(format!("expected identifier, got {other:?}"))),
        }
    }

    fn expect_keyword(&mut self, keyword: &str) -> Result<(), CompileError> {
        match self.peek() {
            Some(Token::Ident(v)) if v == keyword => {
                self.pos += 1;
                Ok(())
            }
            other => Err(CompileError(format!(
                "expected keyword `{keyword}`, got {other:?}"
            ))),
        }
    }

    fn peek_keyword(&self, keyword: &str) -> bool {
        matches!(self.peek(), Some(Token::Ident(v)) if v == keyword)
    }

    fn peek_next_keyword(&self, keyword: &str) -> bool {
        matches!(self.tokens.get(self.pos + 1), Some(Token::Ident(v)) if v == keyword)
    }
}

#[derive(Debug, Clone)]
pub(super) struct ChainSegment {
    pub(super) optional: bool,
    pub(super) name: String,
}

pub(super) fn parse_chain_path(path: &str) -> (String, Vec<ChainSegment>) {
    let mut chars = path.chars().peekable();
    let mut base = String::new();
    while let Some(&ch) = chars.peek() {
        if ch == '.' || ch == '?' {
            break;
        }
        base.push(ch);
        chars.next();
    }

    let mut segments = Vec::new();
    while let Some(&ch) = chars.peek() {
        let optional = match ch {
            '.' => {
                chars.next();
                false
            }
            '?' => {
                chars.next();
                if chars.peek() == Some(&'.') {
                    chars.next();
                    true
                } else {
                    break;
                }
            }
            _ => break,
        };

        let mut name = String::new();
        while let Some(&c) = chars.peek() {
            if c == '.' || c == '?' {
                break;
            }
            name.push(c);
            chars.next();
        }
        if name.is_empty() {
            break;
        }
        segments.push(ChainSegment { optional, name });
    }

    (base, segments)
}

pub(super) fn expr_uses_match_target_placeholder(expr: &Expr) -> bool {
    match expr {
        Expr::Var(name) => name == "_",
        Expr::Op(_, left, right) => {
            expr_uses_match_target_placeholder(left) || expr_uses_match_target_placeholder(right)
        }
        Expr::Call(call) => call.args.iter().any(expr_uses_match_target_placeholder),
        Expr::If {
            condition,
            then_expr,
            else_expr,
        } => {
            expr_uses_match_target_placeholder(condition)
                || expr_uses_match_target_placeholder(then_expr)
                || expr_uses_match_target_placeholder(else_expr)
        }
        Expr::MatchExpr {
            target,
            arms,
            default,
        } => {
            expr_uses_match_target_placeholder(target)
                || arms.iter().any(|(pattern, rhs)| {
                    match_pattern_uses_match_target_placeholder(pattern)
                        || expr_uses_match_target_placeholder(rhs)
                })
                || expr_uses_match_target_placeholder(default)
        }
        Expr::SetNew(sn) => sn
            .fields
            .iter()
            .any(|(_, value)| expr_uses_match_target_placeholder(value)),
        Expr::IsType { expr, .. } => expr_uses_match_target_placeholder(expr),
        Expr::Lambda { body, .. } => match body {
            LambdaBody::Expr(expr) => expr_uses_match_target_placeholder(expr),
            LambdaBody::Block(_) => false,
        },
        Expr::ListExpr(items) => items.iter().any(expr_uses_match_target_placeholder),
        Expr::Literal(_) => false,
    }
}

fn match_pattern_uses_match_target_placeholder(pattern: &MatchPattern) -> bool {
    match pattern {
        MatchPattern::Value(expr) | MatchPattern::Condition(expr) => {
            expr_uses_match_target_placeholder(expr)
        }
        MatchPattern::IsType { .. } => false,
    }
}

/// Tokenise `input` and return `(tokens, offsets)` where `offsets[i]` is the
/// byte offset of `tokens[i]` within `input`.
/// Strip `/** ... */` formatting from a raw doc-comment body:
/// removes leading whitespace and optional `*` from each line,
/// and trims blank lines from the start and end.
fn clean_doc_comment(raw: &str) -> String {
    let cleaned: Vec<String> = raw
        .lines()
        .map(|line| {
            let s = line.trim();
            if let Some(rest) = s.strip_prefix('*') {
                rest.trim_start().to_string()
            } else {
                s.to_string()
            }
        })
        .collect();
    let start = cleaned.iter().position(|l| !l.is_empty()).unwrap_or(cleaned.len());
    let end = cleaned.iter().rposition(|l| !l.is_empty()).map(|i| i + 1).unwrap_or(0);
    if start >= end {
        return String::new();
    }
    cleaned[start..end].join("\n")
}

/// Tokenise a helper block string, returning tokens paired with their byte offsets.
///
/// The returned `(tokens, offsets)` vectors are parallel: `offsets[i]` is the
/// byte position of `tokens[i]` in `input`. Offsets are used by
/// [`HelperParser`] to produce diagnostics that map back to precise LSP
/// document positions.
///
/// # Comment handling
///
/// - `//` — single-line comment; consumed silently.
/// - `/* … */` — block comment; consumed silently.
/// - `/** … */` — doc-comment; emitted as a [`Token::DocComment`] with its
///   content cleaned by [`clean_doc_comment`]. An empty doc-comment (`/**/`)
///   is treated as a regular block comment and skipped.
pub(super) fn lex_with_offsets(input: &str) -> Result<(Vec<Token>, Vec<usize>), CompileError> {
    let mut tokens = Vec::new();
    let mut offsets = Vec::new();
    let mut chars = input.chars().peekable();
    let mut byte_pos: usize = 0;

    loop {
        // Skip whitespace and comments; emit DocComment tokens for /** ... */.
        while let Some(&ch) = chars.peek() {
            if ch == '/' {
                let mut tmp = input[byte_pos..].chars();
                tmp.next(); // the '/'
                let second = tmp.next();
                if second == Some('/') {
                    // Line comment: skip until end of line.
                    while let Some(&c) = chars.peek() {
                        let len = c.len_utf8();
                        chars.next();
                        byte_pos += len;
                        if c == '\n' {
                            break;
                        }
                    }
                    continue;
                } else if second == Some('*') {
                    let doc_start = byte_pos;
                    let third = tmp.next();
                    let is_doc = third == Some('*');
                    // Consume '/' and '*'.
                    chars.next(); byte_pos += 1;
                    chars.next(); byte_pos += 1;
                    if is_doc {
                        // Consume the second '*' of '/**'.
                        chars.next(); byte_pos += 1;
                        // '/**/' is an empty block comment, not a doc comment.
                        if chars.peek() == Some(&'/') {
                            chars.next(); byte_pos += 1;
                            continue;
                        }
                    }
                    // Read content until '*/'.
                    let mut content = String::new();
                    loop {
                        match chars.peek() {
                            None => break,
                            Some(&'*') => {
                                chars.next(); byte_pos += 1;
                                if chars.peek() == Some(&'/') {
                                    chars.next(); byte_pos += 1;
                                    break;
                                } else if is_doc {
                                    content.push('*');
                                }
                            }
                            Some(&c) => {
                                let len = c.len_utf8();
                                chars.next(); byte_pos += len;
                                if is_doc { content.push(c); }
                            }
                        }
                    }
                    if is_doc {
                        let cleaned = clean_doc_comment(&content);
                        if !cleaned.is_empty() {
                            tokens.push(Token::DocComment(cleaned));
                            offsets.push(doc_start);
                        }
                    }
                    continue;
                }
            }
            if ch.is_whitespace() {
                let len = ch.len_utf8();
                chars.next();
                byte_pos += len;
            } else {
                break;
            }
        }

        let ch = match chars.peek() {
            None => break,
            Some(&c) => c,
        };

        let tok_start = byte_pos;

        macro_rules! push {
            ($tok:expr) => {{
                tokens.push($tok);
                offsets.push(tok_start);
            }};
        }

        match ch {
            '{' => {
                chars.next();
                byte_pos += 1;
                push!(Token::LBrace);
            }
            '}' => {
                chars.next();
                byte_pos += 1;
                push!(Token::RBrace);
            }
            '(' => {
                chars.next();
                byte_pos += 1;
                push!(Token::LParen);
            }
            ')' => {
                chars.next();
                byte_pos += 1;
                push!(Token::RParen);
            }
            '[' => {
                chars.next();
                byte_pos += 1;
                push!(Token::LBracket);
            }
            ']' => {
                chars.next();
                byte_pos += 1;
                push!(Token::RBracket);
            }
            ',' => {
                chars.next();
                byte_pos += 1;
                push!(Token::Comma);
            }
            ';' => {
                chars.next();
                byte_pos += 1;
                push!(Token::Semi);
            }
            '.' => {
                chars.next();
                byte_pos += 1;
                push!(Token::Dot);
            }
            '?' => {
                chars.next();
                byte_pos += 1;
                if chars.peek() == Some(&'.') {
                    chars.next();
                    byte_pos += 1;
                    push!(Token::QuestionDot);
                } else if chars.peek() == Some(&'?') {
                    chars.next();
                    byte_pos += 1;
                    push!(Token::QuestionQuestion);
                } else {
                    return Err(CompileError(
                        "unexpected `?` — did you mean `?.` or `??`?".to_string(),
                    ));
                }
            }
            ':' => {
                chars.next();
                byte_pos += 1;
                push!(Token::Colon);
            }
            '+' => {
                chars.next();
                byte_pos += 1;
                if chars.peek() == Some(&'=') {
                    chars.next();
                    byte_pos += 1;
                    push!(Token::PlusAssign);
                } else {
                    push!(Token::Plus);
                }
            }
            '*' => {
                chars.next();
                byte_pos += 1;
                push!(Token::Star);
            }
            '/' => {
                chars.next();
                byte_pos += 1;
                push!(Token::Slash);
            }
            '%' => {
                chars.next();
                byte_pos += 1;
                push!(Token::Percent);
            }
            '-' => {
                chars.next();
                byte_pos += 1;
                push!(Token::Minus);
            }
            '=' => {
                chars.next();
                byte_pos += 1;
                if chars.peek() == Some(&'=') {
                    chars.next();
                    byte_pos += 1;
                    push!(Token::EqEq);
                } else if chars.peek() == Some(&'>') {
                    chars.next();
                    byte_pos += 1;
                    push!(Token::FatArrow);
                } else {
                    push!(Token::Assign);
                }
            }
            '!' => {
                chars.next();
                byte_pos += 1;
                if chars.peek() == Some(&'=') {
                    chars.next();
                    byte_pos += 1;
                    push!(Token::BangEq);
                } else {
                    push!(Token::Bang);
                }
            }
            '<' => {
                chars.next();
                byte_pos += 1;
                if chars.peek() == Some(&'=') {
                    chars.next();
                    byte_pos += 1;
                    push!(Token::LtEq);
                } else {
                    push!(Token::Lt);
                }
            }
            '>' => {
                chars.next();
                byte_pos += 1;
                if chars.peek() == Some(&'=') {
                    chars.next();
                    byte_pos += 1;
                    push!(Token::GtEq);
                } else {
                    push!(Token::Gt);
                }
            }
            '&' => {
                chars.next();
                byte_pos += 1;
                if chars.peek() == Some(&'&') {
                    chars.next();
                    byte_pos += 1;
                    push!(Token::AmpAmp);
                } else {
                    push!(Token::Amp);
                }
            }
            '|' => {
                chars.next();
                byte_pos += 1;
                if chars.peek() == Some(&'|') {
                    chars.next();
                    byte_pos += 1;
                    push!(Token::PipePipe);
                } else {
                    return Err(CompileError(
                        "unexpected `|` — did you mean `||`?".to_string(),
                    ));
                }
            }
            '"' => {
                chars.next();
                byte_pos += 1;
                let mut text = String::new();
                let mut terminated = false;
                let mut escaped = false;
                for c in chars.by_ref() {
                    byte_pos += c.len_utf8();
                    if escaped {
                        match c {
                            'n' => text.push('\n'),
                            't' => text.push('\t'),
                            '"' => text.push('"'),
                            '\\' => text.push('\\'),
                            _ => {
                                return Err(CompileError(format!("invalid string escape `\\{c}`")));
                            }
                        }
                        escaped = false;
                        continue;
                    }
                    if c == '\\' {
                        escaped = true;
                        continue;
                    }
                    if c == '"' {
                        terminated = true;
                        break;
                    }
                    text.push(c);
                }
                if escaped {
                    return Err(CompileError("unterminated string escape".to_string()));
                }
                if !terminated {
                    return Err(CompileError("unterminated string literal".to_string()));
                }
                push!(Token::String(text));
            }
            '0'..='9' => {
                let mut number = String::new();
                while let Some(&d) = chars.peek() {
                    if d.is_ascii_digit() {
                        number.push(d);
                        chars.next();
                        byte_pos += 1;
                    } else {
                        break;
                    }
                }
                let value = number
                    .parse::<i64>()
                    .map_err(|_| CompileError(format!("invalid integer literal `{number}`")))?;
                push!(Token::Number(value));
            }
            _ if ch.is_ascii_alphabetic() || ch == '_' => {
                let mut ident = String::new();
                while let Some(&c) = chars.peek() {
                    if c.is_ascii_alphanumeric() || c == '_' {
                        ident.push(c);
                        chars.next();
                        byte_pos += c.len_utf8();
                    } else {
                        break;
                    }
                }
                push!(Token::Ident(ident));
            }
            _ => {
                let context_start = byte_pos.saturating_sub(20);
                let context_end = (byte_pos + 20).min(input.len());
                let context = &input[context_start..context_end];
                return Err(CompileError(format!(
                    "unexpected character `{ch}` at byte offset {byte_pos}\nContext: {context:?}"
                )));
            }
        }
    }

    Ok((tokens, offsets))
}

/// Simpler tokeniser without byte-offset tracking.
///
/// This is the original implementation, superseded by [`lex_with_offsets`]
/// which adds the offset parallel vector required for LSP diagnostics.
/// Kept as a readable reference; the `_` prefix suppresses the unused warning.
fn _lex_impl(input: &str) -> Result<Vec<Token>, CompileError> {
    let mut chars = input.chars().peekable();
    let mut tokens = Vec::new();

    while let Some(&ch) = chars.peek() {
        match ch {
            ' ' | '\t' | '\n' | '\r' => {
                chars.next();
            }
            '/' => {
                chars.next();
                if chars.peek() == Some(&'/') {
                    chars.next();
                    // consume until end-of-line (single-line comment)
                    while let Some(&c) = chars.peek() {
                        chars.next();
                        if c == '\n' {
                            break;
                        }
                    }
                } else {
                    tokens.push(Token::Slash);
                }
            }
            '(' => {
                chars.next();
                tokens.push(Token::LParen);
            }
            ')' => {
                chars.next();
                tokens.push(Token::RParen);
            }
            '{' => {
                chars.next();
                tokens.push(Token::LBrace);
            }
            '}' => {
                chars.next();
                tokens.push(Token::RBrace);
            }
            '[' => {
                chars.next();
                tokens.push(Token::LBracket);
            }
            ']' => {
                chars.next();
                tokens.push(Token::RBracket);
            }
            ',' => {
                chars.next();
                tokens.push(Token::Comma);
            }
            ';' => {
                chars.next();
                tokens.push(Token::Semi);
            }
            '=' => {
                chars.next();
                if chars.peek() == Some(&'=') {
                    chars.next();
                    tokens.push(Token::EqEq);
                } else if chars.peek() == Some(&'>') {
                    chars.next();
                    tokens.push(Token::FatArrow);
                } else {
                    tokens.push(Token::Assign);
                }
            }
            '!' => {
                chars.next();
                if chars.peek() == Some(&'=') {
                    chars.next();
                    tokens.push(Token::BangEq);
                } else {
                    tokens.push(Token::Bang);
                }
            }
            '<' => {
                chars.next();
                if chars.peek() == Some(&'=') {
                    chars.next();
                    tokens.push(Token::LtEq);
                } else {
                    tokens.push(Token::Lt);
                }
            }
            '>' => {
                chars.next();
                if chars.peek() == Some(&'=') {
                    chars.next();
                    tokens.push(Token::GtEq);
                } else {
                    tokens.push(Token::Gt);
                }
            }
            '&' => {
                chars.next();
                if chars.peek() == Some(&'&') {
                    chars.next();
                    tokens.push(Token::AmpAmp);
                } else {
                    return Err(CompileError(
                        "unexpected `&` — did you mean `&&`?".to_string(),
                    ));
                }
            }
            '|' => {
                chars.next();
                if chars.peek() == Some(&'|') {
                    chars.next();
                    tokens.push(Token::PipePipe);
                } else {
                    return Err(CompileError(
                        "unexpected `|` — did you mean `||`?".to_string(),
                    ));
                }
            }
            '+' => {
                chars.next();
                if chars.peek() == Some(&'=') {
                    chars.next();
                    tokens.push(Token::PlusAssign);
                } else {
                    tokens.push(Token::Plus);
                }
            }
            '*' => {
                chars.next();
                tokens.push(Token::Star);
            }
            '%' => {
                chars.next();
                tokens.push(Token::Percent);
            }
            '.' => {
                chars.next();
                tokens.push(Token::Dot);
            }
            '?' => {
                chars.next();
                if chars.peek() == Some(&'.') {
                    chars.next();
                    tokens.push(Token::QuestionDot);
                } else if chars.peek() == Some(&'?') {
                    chars.next();
                    tokens.push(Token::QuestionQuestion);
                } else {
                    return Err(CompileError(
                        "unexpected `?` — did you mean `?.` or `??`?".to_string(),
                    ));
                }
            }
            ':' => {
                chars.next();
                tokens.push(Token::Colon);
            }
            '"' => {
                chars.next();
                let mut text = String::new();
                let mut escaped = false;
                let mut terminated = false;
                for c in chars.by_ref() {
                    if escaped {
                        match c {
                            'n' => text.push('\n'),
                            't' => text.push('\t'),
                            '"' => text.push('"'),
                            '\\' => text.push('\\'),
                            _ => {
                                return Err(CompileError(format!(
                                    "invalid string escape sequence `\\{c}`"
                                )));
                            }
                        }
                        escaped = false;
                        continue;
                    }
                    if c == '\\' {
                        escaped = true;
                        continue;
                    }
                    if c == '"' {
                        terminated = true;
                        break;
                    }
                    text.push(c);
                }
                if escaped {
                    return Err(CompileError(
                        "unterminated string escape sequence".to_string(),
                    ));
                }
                if !terminated {
                    return Err(CompileError("unterminated string literal".to_string()));
                }
                tokens.push(Token::String(text));
            }
            '-' => {
                chars.next();
                tokens.push(Token::Minus);
            }
            '0'..='9' => {
                let mut number = String::new();
                number.push(ch);
                chars.next();
                while let Some(next) = chars.peek() {
                    if next.is_ascii_digit() {
                        number.push(*next);
                        chars.next();
                    } else {
                        break;
                    }
                }
                let value = number
                    .parse::<i64>()
                    .map_err(|_| CompileError(format!("invalid integer literal `{number}`")))?;
                tokens.push(Token::Number(value));
            }
            _ if ch.is_ascii_alphabetic() || ch == '_' => {
                let mut ident = String::new();
                ident.push(ch);
                chars.next();
                while let Some(next) = chars.peek() {
                    if next.is_ascii_alphanumeric() || *next == '_' {
                        ident.push(*next);
                        chars.next();
                    } else {
                        break;
                    }
                }
                tokens.push(Token::Ident(ident));
            }
            _ => {
                // We can't easily get the exact position in the second lexer without tracking it,
                // so we'll just provide the character and a simple context message
                return Err(CompileError(format!(
                    "unexpected character `{ch}` (consider if this character should be inside a string literal)"
                )));
            }
        }
    }

    Ok(tokens)
}
