// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Pest-based parser for `.tlang` source files.
//!
//! Converts raw source text into a [`crate::ast::DomainModel`] using the
//! grammar defined in `src/tlang.pest`. The parser handles:
//!
//! - `expose` / `use` header declarations
//! - `{ … }` helper code blocks
//! - `model { … }` model blocks
//! - `lang [target] Name(params) { … }` template blocks
//! - `data`, `raw`, `doc`, `style`, `cmd` template variants
//! - Top-level `func` declarations (wrapped into helper blocks)
//! - `/** … */` javadoc comments attached to top-level functions
//!
//! # Entry points
//!
//! - [`parse_domain_model`] — parse a source string (file name reported as `<input>`).
//! - [`parse_domain_model_in_file`] — parse with an explicit file name for error messages.

use pest::Parser;
use pest::iterators::Pair;
use pest_derive::Parser;

use crate::ast::{
    CmdTemplateBlock, DataTemplateBlock, DocTemplateBlock, DomainBlock, DomainHeader, DomainModel,
    DomainUse, HelperBlock, ModelBlock, RawTemplateBlock, StyleTemplateBlock, TemplateBlock,
    TemplateContent, TemplateParam, TestBlock,
};
use crate::error_checker::{format_pest_error, format_with_context, offset_to_line_position};
use crate::model_tree::parse_model_block_tree;
use crate::tmpl_cmd_tree::parse_tmpl_cmd_block_tree;
use crate::tmpl_data_tree::parse_tmpl_data_block_tree;
use crate::tmpl_tree::parse_tmpl_block_tree;

#[derive(Parser)]
#[grammar = "tlang.pest"]
struct TLangParser;

/// An error produced by the TLang parser.
///
/// The inner `String` contains a human-readable message with file name,
/// line number, and a context excerpt pointing to the offending token.
#[derive(Debug)]
pub struct ParseError(pub String);

impl std::fmt::Display for ParseError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.0)
    }
}

impl std::error::Error for ParseError {}

/// Parse a TLang source string into a [`DomainModel`].
///
/// The file name is reported as `<input>` in error messages.
/// Prefer [`parse_domain_model_in_file`] when a real path is available.
pub fn parse_domain_model(input: &str) -> Result<DomainModel, ParseError> {
    parse_domain_model_in_file("<input>", input)
}

/// Parse a TLang source string from a named file into a [`DomainModel`].
///
/// `file` is used only for error messages (e.g. `"Main.tlang"`).
///
/// The parser recognises all top-level constructs:
/// - `expose` / `use` header declarations
/// - `{ … }` helper blocks and top-level `func` / `let` declarations
/// - `model { … }` / top-level `set` declarations
/// - `lang`, `data`, `raw`, `doc`, `style`, `cmd` template blocks
/// - `/** … */` javadoc comments attached to top-level functions
pub fn parse_domain_model_in_file(file: &str, input: &str) -> Result<DomainModel, ParseError> {
    let mut pairs = TLangParser::parse(Rule::file, input)
        .map_err(|e| ParseError(format_pest_error(e, file)))?;
    let parsed_file = pairs
        .next()
        .ok_or_else(|| ParseError("unable to parse input".to_string()))?;

    let mut header = DomainHeader::default();
    let mut body = Vec::new();

    for pair in parsed_file.into_inner() {
        match pair.as_rule() {
            Rule::domain_header => parse_header(pair, &mut header),
            Rule::data_block => body.push(DomainBlock::Data(parse_data_block(pair, file, input)?)),
            Rule::cmd_block => body.push(DomainBlock::Cmd(parse_cmd_block(pair, file, input)?)),
            Rule::raw_block => body.push(DomainBlock::Raw(parse_raw_block(pair, file, input)?)),
            Rule::doc_block => body.push(DomainBlock::Doc(parse_doc_block(pair, file, input)?)),
            Rule::style_block => {
                body.push(DomainBlock::Style(parse_style_block(pair, file, input)?))
            }
            Rule::toplevel_doc_func => {
                // `/** ... */` javadoc immediately preceding a top-level func.
                // Include the doc comment in the helper content so lex_with_offsets
                // emits a DocComment token and parse_program attaches it to the func.
                let start = pair.as_span().start();
                let mut inner = pair.into_inner();
                let doc_raw = inner.next().map(|p| p.as_str()).unwrap_or("");
                let func_raw = inner.next().map(|p| p.as_str()).unwrap_or("");
                let content_start = start.saturating_sub(2);
                body.push(DomainBlock::Helper(HelperBlock {
                    content: format!("{{\n{doc_raw}\n{func_raw}\n}}"),
                    content_start,
                    source_file: None,
                    source_path: None,
                    package_name: None,
                }));
            }
            Rule::toplevel_func => {
                let raw = pair.as_str();
                let start = pair.as_span().start();
                // Wrap in braces so the helper parser sees a valid block.
                // Subtract 2 for the "{\n" prefix so token offsets map back to
                // the actual source positions (best-effort; may be off by 2 at
                // the very start of a file, which is harmless for runtime use).
                let content_start = start.saturating_sub(2);
                body.push(DomainBlock::Helper(HelperBlock {
                    content: format!("{{\n{raw}\n}}"),
                    content_start,
                    source_file: None,
                    source_path: None,
                    package_name: None,
                }));
            }
            Rule::toplevel_set => {
                let raw = pair.as_str().trim_end();
                let model_start = pair.as_span().start();
                let model = ModelBlock {
                    content: format!("{{\n{raw}\n}}"),
                };
                parse_model_block_tree(&model).map_err(|e| {
                    let (line, position) = offset_to_line_position(input, model_start);
                    ParseError(format_with_context(
                        &format!("invalid model tree: {e}"),
                        file,
                        line,
                        position,
                    ))
                })?;
                body.push(DomainBlock::Model(model));
            }
            Rule::toplevel_let => {
                let raw = pair.as_str().trim_end();
                let start = pair.as_span().start();
                // Typed let (e.g. `let seed: Number = 42`) goes to the model tree;
                // untyped let (e.g. `let x = 5`) becomes a helper-block runtime variable.
                if let_has_type_annotation(raw) {
                    let model = ModelBlock {
                        content: format!("{{\n{raw}\n}}"),
                    };
                    parse_model_block_tree(&model).map_err(|e| {
                        let (line, position) = offset_to_line_position(input, start);
                        ParseError(format_with_context(
                            &format!("invalid model tree: {e}"),
                            file,
                            line,
                            position,
                        ))
                    })?;
                    body.push(DomainBlock::Model(model));
                } else {
                    let content_start = start.saturating_sub(2);
                    body.push(DomainBlock::Helper(HelperBlock {
                        content: format!("{{\n{raw}\n}}"),
                        content_start,
                        source_file: None,
                        source_path: None,
                        package_name: None,
                    }));
                }
            }
            Rule::tmpl_block => body.push(DomainBlock::Template(parse_template_block(
                pair, file, input,
            )?)),
            Rule::test_block => body.push(DomainBlock::Test(parse_test_block(pair)?)),
            Rule::EOI => {}
            _ => {}
        }
    }

    Ok(DomainModel { header, body })
}

fn parse_header(pair: Pair<'_, Rule>, header: &mut DomainHeader) {
    for item in pair.into_inner() {
        match item.as_rule() {
            Rule::domain_expose => {
                let expose_offset = item.as_span().start();
                if let Some(name) = item.into_inner().find(|p| p.as_rule() == Rule::ident) {
                    header.exposes.push(name.as_str().to_string());
                    header.expose_offsets.push(expose_offset);
                }
            }
            Rule::domain_use => {
                let mut path = Vec::new();
                let mut alias = None;
                let offset = item.as_span().start();

                for part in item.into_inner() {
                    match part.as_rule() {
                        Rule::use_path => {
                            path = part
                                .into_inner()
                                .filter(|p| p.as_rule() == Rule::ident)
                                .map(|p| p.as_str().to_string())
                                .collect();
                        }
                        Rule::domain_use_alias => {
                            alias = part
                                .into_inner()
                                .find(|p| p.as_rule() == Rule::ident)
                                .map(|p| p.as_str().to_string());
                        }
                        _ => {}
                    }
                }

                header.uses.push(DomainUse {
                    path,
                    alias,
                    offset,
                });
            }
            _ => {}
        }
    }
}

fn parse_data_block(
    pair: Pair<'_, Rule>,
    file: &str,
    source: &str,
) -> Result<DataTemplateBlock, ParseError> {
    let data_start = pair.as_span().start();
    let mut langs: Vec<String> = Vec::new();
    let mut lang_offset: usize = 0;
    let mut name: Option<String> = None;
    let mut params: Vec<TemplateParam> = Vec::new();
    let mut content: Option<String> = None;

    for item in pair.into_inner() {
        match item.as_rule() {
            Rule::data_langs => {
                for lang_id in item.into_inner() {
                    if lang_id.as_rule() == Rule::tmpl_id {
                        if langs.is_empty() {
                            lang_offset = lang_id.as_span().start();
                        }
                        langs.push(lang_id.as_str().to_string());
                    }
                }
            }
            Rule::tmpl_id if name.is_none() => {
                name = Some(item.as_str().to_string());
            }
            Rule::tmpl_params => {
                for param in item.into_inner() {
                    if param.as_rule() == Rule::tmpl_param {
                        let mut inner = param.into_inner();
                        let param_name = inner
                            .next()
                            .ok_or_else(|| {
                                ParseError("invalid data template parameter".to_string())
                            })?
                            .as_str()
                            .to_string();
                        let param_type = inner
                            .next()
                            .map(|n| n.as_str().to_string())
                            .unwrap_or_default();
                        params.push(TemplateParam {
                            name: param_name,
                            ty: param_type,
                        });
                    }
                }
            }
            Rule::balanced_block => {
                content = Some(item.as_str().to_string());
            }
            _ => {}
        }
    }

    let (line, position) = offset_to_line_position(source, data_start);
    if langs.is_empty() {
        return Err(ParseError(format_with_context(
            "data template requires at least one language",
            file,
            line,
            position,
        )));
    }
    let name = name.ok_or_else(|| {
        ParseError(format_with_context(
            "data template name is required",
            file,
            line,
            position,
        ))
    })?;
    let content = content.ok_or_else(|| {
        ParseError(format_with_context(
            "data template content is required",
            file,
            line,
            position,
        ))
    })?;

    parse_tmpl_data_block_tree(&langs, &name, &params, &content).map_err(|e| {
        ParseError(format_with_context(
            &format!("invalid data template tree: {e}"),
            file,
            line,
            position,
        ))
    })?;

    Ok(DataTemplateBlock {
        langs,
        name,
        params,
        content,
        lang_offset,
        data_start,
    })
}

fn parse_cmd_block(
    pair: Pair<'_, Rule>,
    file: &str,
    source: &str,
) -> Result<CmdTemplateBlock, ParseError> {
    let cmd_start = pair.as_span().start();
    let mut langs: Vec<String> = Vec::new();
    let mut lang_offset: usize = 0;
    let mut name: Option<String> = None;
    let mut params: Vec<TemplateParam> = Vec::new();
    let mut content: Option<String> = None;

    for item in pair.into_inner() {
        match item.as_rule() {
            Rule::cmd_langs => {
                for lang_id in item.into_inner() {
                    if lang_id.as_rule() == Rule::ident {
                        if langs.is_empty() {
                            lang_offset = lang_id.as_span().start();
                        }
                        langs.push(lang_id.as_str().to_string());
                    }
                }
            }
            Rule::ident if name.is_none() => {
                name = Some(item.as_str().to_string());
            }
            Rule::tmpl_params => {
                for param in item.into_inner() {
                    if param.as_rule() == Rule::tmpl_param {
                        let mut inner = param.into_inner();
                        let param_name = inner
                            .next()
                            .ok_or_else(|| {
                                ParseError("invalid cmd template parameter".to_string())
                            })?
                            .as_str()
                            .to_string();
                        let param_type = inner
                            .next()
                            .map(|n| n.as_str().to_string())
                            .unwrap_or_default();
                        params.push(TemplateParam {
                            name: param_name,
                            ty: param_type,
                        });
                    }
                }
            }
            Rule::balanced_block => {
                content = Some(item.as_str().to_string());
            }
            _ => {}
        }
    }

    let (line, position) = offset_to_line_position(source, cmd_start);
    if langs.is_empty() {
        return Err(ParseError(format_with_context(
            "cmd template requires at least one language",
            file,
            line,
            position,
        )));
    }
    let name = name.ok_or_else(|| {
        ParseError(format_with_context(
            "cmd template name is required",
            file,
            line,
            position,
        ))
    })?;
    let content = content.ok_or_else(|| {
        ParseError(format_with_context(
            "cmd template content is required",
            file,
            line,
            position,
        ))
    })?;

    parse_tmpl_cmd_block_tree(&langs, &name, &params, &content).map_err(|e| {
        ParseError(format_with_context(
            &format!("invalid cmd template: {e}"),
            file,
            line,
            position,
        ))
    })?;

    Ok(CmdTemplateBlock {
        langs,
        name,
        params,
        content,
        lang_offset,
        cmd_start,
    })
}

fn parse_raw_block(
    pair: Pair<'_, Rule>,
    file: &str,
    source: &str,
) -> Result<RawTemplateBlock, ParseError> {
    let raw_start = pair.as_span().start();
    let mut variant: Option<String> = None;
    let mut variant_offset: usize = 0;
    let mut name: Option<String> = None;
    let mut params: Vec<TemplateParam> = Vec::new();
    let mut content: Option<String> = None;

    for item in pair.into_inner() {
        match item.as_rule() {
            Rule::raw_variant => {
                variant_offset = item.as_span().start();
                if let Some(v) = item.into_inner().find(|p| p.as_rule() == Rule::ident) {
                    variant = Some(v.as_str().to_string());
                }
            }
            Rule::ident if name.is_none() => {
                name = Some(item.as_str().to_string());
            }
            Rule::tmpl_params => {
                for param in item.into_inner() {
                    if param.as_rule() == Rule::tmpl_param {
                        let mut inner = param.into_inner();
                        let param_name = inner
                            .next()
                            .ok_or_else(|| {
                                ParseError("invalid raw template parameter".to_string())
                            })?
                            .as_str()
                            .to_string();
                        let param_type = inner
                            .next()
                            .map(|n| n.as_str().to_string())
                            .unwrap_or_default();
                        params.push(TemplateParam {
                            name: param_name,
                            ty: param_type,
                        });
                    }
                }
            }
            Rule::balanced_block => {
                content = Some(item.as_str().to_string());
            }
            _ => {}
        }
    }

    let (line, position) = offset_to_line_position(source, raw_start);
    let variant = variant.ok_or_else(|| {
        ParseError(format_with_context(
            "raw template requires a variant: [AsIs] or [Replaced]",
            file,
            line,
            position,
        ))
    })?;

    match variant.as_str() {
        "AsIs" | "Replaced" => {}
        other => {
            return Err(ParseError(format_with_context(
                &format!("unknown raw template variant `{other}` — expected `AsIs` or `Replaced`"),
                file,
                line,
                position,
            )));
        }
    }

    let name = name.ok_or_else(|| {
        ParseError(format_with_context(
            "raw template name is required",
            file,
            line,
            position,
        ))
    })?;
    let content = content.ok_or_else(|| {
        ParseError(format_with_context(
            "raw template content is required",
            file,
            line,
            position,
        ))
    })?;

    Ok(RawTemplateBlock {
        variant,
        name,
        params,
        content,
        variant_offset,
        raw_start,
    })
}

fn parse_doc_block(
    pair: Pair<'_, Rule>,
    file: &str,
    source: &str,
) -> Result<DocTemplateBlock, ParseError> {
    let doc_start = pair.as_span().start();
    let mut langs: Vec<String> = Vec::new();
    let mut lang_offset: usize = 0;
    let mut name: Option<String> = None;
    let mut params: Vec<TemplateParam> = Vec::new();
    let mut content: Option<String> = None;

    for item in pair.into_inner() {
        match item.as_rule() {
            Rule::doc_langs => {
                for lang_id in item.into_inner() {
                    if lang_id.as_rule() == Rule::ident {
                        if langs.is_empty() {
                            lang_offset = lang_id.as_span().start();
                        }
                        langs.push(lang_id.as_str().to_string());
                    }
                }
            }
            Rule::ident if name.is_none() => {
                name = Some(item.as_str().to_string());
            }
            Rule::tmpl_params => {
                for param in item.into_inner() {
                    if param.as_rule() == Rule::tmpl_param {
                        let mut inner = param.into_inner();
                        let param_name = inner
                            .next()
                            .ok_or_else(|| {
                                ParseError("invalid doc template parameter".to_string())
                            })?
                            .as_str()
                            .to_string();
                        let param_type = inner
                            .next()
                            .map(|n| n.as_str().to_string())
                            .unwrap_or_default();
                        params.push(TemplateParam {
                            name: param_name,
                            ty: param_type,
                        });
                    }
                }
            }
            Rule::balanced_block => {
                content = Some(item.as_str().to_string());
            }
            _ => {}
        }
    }

    let (line, position) = offset_to_line_position(source, doc_start);
    if langs.is_empty() {
        return Err(ParseError(format_with_context(
            "doc template requires at least one language",
            file,
            line,
            position,
        )));
    }
    let name = name.ok_or_else(|| {
        ParseError(format_with_context(
            "doc template name is required",
            file,
            line,
            position,
        ))
    })?;
    let content = content.ok_or_else(|| {
        ParseError(format_with_context(
            "doc template content is required",
            file,
            line,
            position,
        ))
    })?;

    Ok(DocTemplateBlock {
        langs,
        name,
        params,
        content,
        lang_offset,
        doc_start,
    })
}

fn parse_style_block(
    pair: Pair<'_, Rule>,
    file: &str,
    source: &str,
) -> Result<StyleTemplateBlock, ParseError> {
    let style_start = pair.as_span().start();
    let mut langs: Vec<String> = Vec::new();
    let mut lang_offset: usize = 0;
    let mut name: Option<String> = None;
    let mut params: Vec<TemplateParam> = Vec::new();
    let mut content: Option<String> = None;

    for item in pair.into_inner() {
        match item.as_rule() {
            Rule::style_langs => {
                for lang_id in item.into_inner() {
                    if lang_id.as_rule() == Rule::ident {
                        if langs.is_empty() {
                            lang_offset = lang_id.as_span().start();
                        }
                        langs.push(lang_id.as_str().to_string());
                    }
                }
            }
            Rule::ident if name.is_none() => {
                name = Some(item.as_str().to_string());
            }
            Rule::tmpl_params => {
                for param in item.into_inner() {
                    if param.as_rule() == Rule::tmpl_param {
                        let mut inner = param.into_inner();
                        let param_name = inner
                            .next()
                            .ok_or_else(|| {
                                ParseError("invalid style template parameter".to_string())
                            })?
                            .as_str()
                            .to_string();
                        let param_type = inner
                            .next()
                            .map(|n| n.as_str().to_string())
                            .unwrap_or_default();
                        params.push(TemplateParam {
                            name: param_name,
                            ty: param_type,
                        });
                    }
                }
            }
            Rule::balanced_block => {
                content = Some(item.as_str().to_string());
            }
            _ => {}
        }
    }

    let (line, position) = offset_to_line_position(source, style_start);
    if langs.is_empty() {
        return Err(ParseError(format_with_context(
            "style template requires at least one language",
            file,
            line,
            position,
        )));
    }
    let name = name.ok_or_else(|| {
        ParseError(format_with_context(
            "style template name is required",
            file,
            line,
            position,
        ))
    })?;
    let content = content.ok_or_else(|| {
        ParseError(format_with_context(
            "style template content is required",
            file,
            line,
            position,
        ))
    })?;

    Ok(StyleTemplateBlock {
        langs,
        name,
        params,
        content,
        lang_offset,
        style_start,
    })
}

fn parse_template_block(
    pair: Pair<'_, Rule>,
    file: &str,
    source: &str,
) -> Result<TemplateBlock, ParseError> {
    let tmpl_start = pair.as_span().start();
    let mut lang = None;
    let mut lang_offset: usize = 0;
    let mut name = None;
    let mut params = Vec::new();
    let mut content = None;

    for item in pair.into_inner() {
        match item.as_rule() {
            Rule::tmpl_id => {
                let value = item.as_str().to_string();
                if lang.is_none() {
                    lang_offset = item.as_span().start();
                    lang = Some(value);
                } else if name.is_none() {
                    name = Some(value);
                }
            }
            Rule::tmpl_params => {
                for param in item.into_inner() {
                    if param.as_rule() == Rule::tmpl_param {
                        let mut inner = param.into_inner();
                        let param_name = inner
                            .next()
                            .ok_or_else(|| ParseError("invalid template parameter".to_string()))?
                            .as_str()
                            .to_string();
                        // Type annotation is optional — consume it if present.
                        let param_type = inner
                            .next()
                            .map(|n| n.as_str().to_string())
                            .unwrap_or_default();
                        params.push(TemplateParam {
                            name: param_name,
                            ty: param_type,
                        });
                    }
                }
            }
            Rule::tmpl_content => {
                for content_item in item.into_inner() {
                    match content_item.as_rule() {
                        Rule::balanced_block => {
                            content = Some(TemplateContent::Full(content_item.as_str().to_string()))
                        }
                        Rule::tmpl_specialized => {
                            let specialized_start = content_item.as_span().start();
                            let block = content_item
                                .into_inner()
                                .find(|p| p.as_rule() == Rule::balanced_block)
                                .ok_or_else(|| {
                                    let (line, position) =
                                        offset_to_line_position(source, specialized_start);
                                    ParseError(format_with_context(
                                        "specialized template has no body",
                                        file,
                                        line,
                                        position,
                                    ))
                                })?;
                            content =
                                Some(TemplateContent::Specialized(block.as_str().to_string()));
                        }
                        _ => {}
                    }
                }
            }
            _ => {}
        }
    }

    let (line, position) = offset_to_line_position(source, tmpl_start);
    let lang = lang.ok_or_else(|| {
        ParseError(format_with_context(
            "template language is required",
            file,
            line,
            position,
        ))
    })?;
    let name = name.ok_or_else(|| {
        ParseError(format_with_context(
            "template name is required",
            file,
            line,
            position,
        ))
    })?;
    let content = content.ok_or_else(|| {
        ParseError(format_with_context(
            "template content is required",
            file,
            line,
            position,
        ))
    })?;

    parse_tmpl_block_tree(&lang, &name, &params, &content).map_err(|e| {
        ParseError(format_with_context(
            &format!("invalid tmpl tree: {e}"),
            file,
            line,
            position,
        ))
    })?;

    Ok(TemplateBlock {
        lang,
        name,
        params,
        content,
        lang_offset,
        tmpl_start,
    })
}

fn parse_test_block(pair: Pair<'_, Rule>) -> Result<TestBlock, ParseError> {
    let mut name: Option<String> = None;
    let mut content: Option<String> = None;

    for item in pair.into_inner() {
        match item.as_rule() {
            Rule::ident if name.is_none() => {
                name = Some(item.as_str().to_string());
            }
            Rule::balanced_block => {
                content = Some(item.as_str().to_string());
            }
            _ => {}
        }
    }

    Ok(TestBlock {
        name: name.unwrap_or_default(),
        content: content.unwrap_or_else(|| "{}".to_string()),
    })
}

#[cfg(test)]
mod tests {
    use pretty_assertions::assert_eq;

    use crate::ast::{DomainBlock, TemplateContent};

    use super::{parse_domain_model, parse_domain_model_in_file};

    #[test]
    fn parses_domain_model_with_all_block_types() {
        let input = r#"
            expose Api
            use core.helpers as helpers
            use core.models

            func trim(v: String) {
                let a = v
            }

            lang [rust] service(name: String, retries: i32) {
                func build() {}
            }

            lang [go] handler spec {
                include <[some.call()]>
            }

            set Service(name: String) {
                value: Type
            }
        "#;

        let parsed = parse_domain_model(input).expect("input should parse");

        assert_eq!(parsed.header.exposes, vec!["Api"]);
        assert_eq!(parsed.header.uses.len(), 2);
        assert_eq!(parsed.header.uses[0].path, vec!["core", "helpers"]);
        assert_eq!(parsed.header.uses[0].alias.as_deref(), Some("helpers"));

        assert_eq!(parsed.body.len(), 4);

        match &parsed.body[1] {
            DomainBlock::Template(tmpl) => {
                assert_eq!(tmpl.lang, "rust");
                assert_eq!(tmpl.name, "service");
                assert_eq!(tmpl.params.len(), 2);
                assert_eq!(tmpl.params[0].name, "name");
                assert_eq!(tmpl.params[0].ty, "String");
                match &tmpl.content {
                    TemplateContent::Full(content) => {
                        assert!(content.contains("func build"));
                    }
                    _ => panic!("expected full template"),
                }
            }
            _ => panic!("expected template block"),
        }

        match &parsed.body[2] {
            DomainBlock::Template(tmpl) => match &tmpl.content {
                TemplateContent::Specialized(content) => {
                    assert!(content.contains("include <[some.call()]>"));
                }
                _ => panic!("expected specialized template"),
            },
            _ => panic!("expected template block"),
        }
    }

    #[test]
    fn fails_when_block_is_unclosed() {
        let input = r#"
            lang [rust] broken {
                func build() {}
        "#;

        let err = super::parse_domain_model_in_file("/tmp/broken.tlang", input)
            .expect_err("input should be invalid");
        let msg = err.to_string();
        assert!(msg.contains("file: /tmp/broken.tlang"));
        assert!(msg.contains("line:"));
        assert!(msg.contains("position:"));
    }

    #[test]
    fn parses_header_only_program() {
        let input = r#"
            expose One
            expose Two
            use a.b
            use c as d
        "#;

        let parsed = parse_domain_model(input).expect("header-only input should parse");
        assert_eq!(parsed.header.exposes, vec!["One", "Two"]);
        assert_eq!(parsed.header.uses.len(), 2);
        assert_eq!(parsed.body.len(), 0);
    }

    #[test]
    fn parses_cmd_block_bare_name() {
        let input = r#"
            cmd [bash] runScript(script: String) {
                bash
            }
        "#;

        let parsed = parse_domain_model(input).expect("input should parse");
        assert_eq!(parsed.body.len(), 1);

        match &parsed.body[0] {
            DomainBlock::Cmd(cmd) => {
                assert_eq!(cmd.langs, vec!["bash"]);
                assert_eq!(cmd.name, "runScript");
                assert_eq!(cmd.params.len(), 1);
                assert_eq!(cmd.params[0].name, "script");
                assert_eq!(cmd.params[0].ty, "String");
                assert!(cmd.content.contains("bash"));
            }
            _ => panic!("expected cmd block"),
        }
    }

    #[test]
    fn parses_cmd_block_call_with_named_args() {
        let input = r#"
            cmd [http] fetchUser(host: String, userId: String) {
                GET(url: ${host}, path: "/users/${userId}")
            }
        "#;

        let parsed = parse_domain_model(input).expect("input should parse");
        assert_eq!(parsed.body.len(), 1);

        match &parsed.body[0] {
            DomainBlock::Cmd(cmd) => {
                assert_eq!(cmd.langs, vec!["http"]);
                assert_eq!(cmd.name, "fetchUser");
                assert_eq!(cmd.params.len(), 2);
                assert!(cmd.content.contains("GET"));
            }
            _ => panic!("expected cmd block"),
        }
    }

    #[test]
    fn parses_cmd_block_multiple_langs() {
        let input = r#"
            cmd [bash, zsh] deploy(host: String) {
                ssh(host: ${host}, command: runDeploy)
            }
        "#;

        let parsed = parse_domain_model(input).expect("input should parse");

        match &parsed.body[0] {
            DomainBlock::Cmd(cmd) => {
                assert_eq!(cmd.langs, vec!["bash", "zsh"]);
                assert_eq!(cmd.name, "deploy");
            }
            _ => panic!("expected cmd block"),
        }
    }

    #[test]
    fn parses_cmd_block_sql_style() {
        let input = r#"
            cmd [sql] findByEmail(email: String) {
                SELECT(table: users, where: "email = ${email}")
            }
        "#;

        let parsed = parse_domain_model(input).expect("input should parse");

        match &parsed.body[0] {
            DomainBlock::Cmd(cmd) => {
                assert_eq!(cmd.langs, vec!["sql"]);
                assert_eq!(cmd.name, "findByEmail");
                assert!(cmd.content.contains("SELECT"));
            }
            _ => panic!("expected cmd block"),
        }
    }

    #[test]
    fn parses_cmd_block_no_params() {
        let input = r#"
            cmd [bash] ping {
                bash
            }
        "#;

        let parsed = parse_domain_model(input).expect("input should parse");

        match &parsed.body[0] {
            DomainBlock::Cmd(cmd) => {
                assert_eq!(cmd.name, "ping");
                assert!(cmd.params.is_empty());
            }
            _ => panic!("expected cmd block"),
        }
    }

    #[test]
    fn rejects_cmd_block_with_invalid_body() {
        let input = r#"
            cmd [bash] broken(x: String) {
                bash extra-garbage-here
            }
        "#;

        let err = parse_domain_model_in_file("/tmp/broken.tlang", input)
            .expect_err("input should be invalid");
        let msg = err.to_string();
        assert!(msg.contains("invalid cmd template"));
        assert!(msg.contains("file: /tmp/broken.tlang"));
    }

    #[test]
    fn reports_context_for_invalid_model_tree() {
        // A `set` block with an unrecognised statement should produce an
        // "invalid model tree" error with full source context.
        let input = "set Broken {\n    nope Something\n}\n";

        let err = super::parse_domain_model_in_file("/tmp/model.tlang", input)
            .expect_err("input should be invalid");
        let msg = err.to_string();
        assert!(msg.contains("invalid model tree"), "msg: {msg}");
        assert!(msg.contains("file: /tmp/model.tlang"), "msg: {msg}");
        assert!(msg.contains("line:"), "msg: {msg}");
        assert!(msg.contains("position:"), "msg: {msg}");
    }
}

/// Return `true` if the `let` declaration text has a type annotation between
/// the name and the `=`, e.g. `let seed: Number = 42` → `true`,
/// `let x = 5` → `false`.
fn let_has_type_annotation(let_text: &str) -> bool {
    let tail = let_text.trim().strip_prefix("let").unwrap_or("").trim();
    if let Some(before_eq) = tail.split('=').next() {
        before_eq.contains(':')
    } else {
        false
    }
}
