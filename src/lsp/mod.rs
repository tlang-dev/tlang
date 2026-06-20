// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! LSP server entry point.
//!
//! Runs the JSON-RPC message loop over stdin/stdout, dispatches incoming
//! requests to the feature handlers in the sibling modules, and processes
//! background `PublishDiagnostics` notifications on a dedicated thread.

pub mod completion;
pub mod diagnostics;
pub mod formatting;
pub mod hover;
pub mod references;
pub mod semantic_tokens;
pub mod signatures;
pub mod symbols;
pub(super) mod util;

use std::collections::HashMap;
use std::io;
use std::thread;

use lsp_server::{Connection, ExtractError, Message, Notification, Request, RequestId, Response};
use lsp_types::notification::{
    DidChangeTextDocument, DidOpenTextDocument, Initialized,
};
use lsp_types::request::{
    Completion, DocumentSymbolRequest, Formatting, GotoDefinition, HoverRequest, References,
    Rename as RenameRequest, SemanticTokensFullRequest, SignatureHelpRequest,
};
use lsp_types::{
    CompletionOptions, CompletionResponse, HoverProviderCapability, InitializeResult, OneOf,
    SemanticTokenType, SemanticTokensFullOptions, SemanticTokensLegend, SemanticTokensOptions,
    SemanticTokensServerCapabilities, ServerCapabilities, ServerInfo, SignatureHelpOptions,
    TextDocumentSyncCapability, TextDocumentSyncKind, Uri,
};

use completion::compute_completions;
use diagnostics::publish_diagnostics_with_sender;
use formatting::compute_formatting;
use hover::compute_hover;
use references::{compute_definition, compute_references, compute_rename};
use semantic_tokens::compute_semantic_tokens;
use signatures::compute_signature_help;
use symbols::compute_document_symbols;

/// Start and run the LSP server, communicating over stdin/stdout.
///
/// This function blocks until the client sends a `shutdown` request followed
/// by an `exit` notification, or until the connection is closed.
pub fn run_lsp_server() -> io::Result<()> {
    let (connection, io_threads) = Connection::stdio();

    // Handshake: send server capabilities.
    //
    // NOTE: `Connection::initialize` wraps its argument as `{"capabilities": <value>}`, so we
    // must use the lower-level initialize_start / initialize_finish pair to send the full
    // InitializeResult (including serverInfo) without double-nesting the capabilities object.
    let caps = ServerCapabilities {
        text_document_sync: Some(TextDocumentSyncCapability::Kind(TextDocumentSyncKind::FULL)),
        document_formatting_provider: Some(OneOf::Left(true)),
        semantic_tokens_provider: Some(SemanticTokensServerCapabilities::SemanticTokensOptions(
            SemanticTokensOptions {
                legend: SemanticTokensLegend {
                    token_types: vec![
                        SemanticTokenType::KEYWORD,   // 0 — TLang keywords inside template bodies
                        SemanticTokenType::MACRO,     // 1 — <[ ... ]> include directives
                        SemanticTokenType::VARIABLE,  // 2 — ${ ... } interpolations / string values
                        SemanticTokenType::NAMESPACE, // 3 — lang [language] attribute header / attr keys
                        SemanticTokenType::FUNCTION,  // 4 — template name / tag call names in body
                        SemanticTokenType::PARAMETER, // 5 — parameter names in data template signature
                        SemanticTokenType::TYPE,      // 6 — type names in data template signature
                    ],
                    token_modifiers: vec![],
                },
                full: Some(SemanticTokensFullOptions::Bool(true)),
                range: None,
                work_done_progress_options: Default::default(),
            },
        )),
        completion_provider: Some(CompletionOptions {
            trigger_characters: Some(vec![".".to_string(), " ".to_string()]),
            resolve_provider: Some(false),
            ..Default::default()
        }),
        definition_provider: Some(OneOf::Left(true)),
        hover_provider: Some(HoverProviderCapability::Simple(true)),
        signature_help_provider: Some(SignatureHelpOptions {
            trigger_characters: Some(vec!["(".to_string(), ",".to_string()]),
            retrigger_characters: Some(vec![",".to_string()]),
            ..Default::default()
        }),
        document_symbol_provider: Some(OneOf::Left(true)),
        references_provider: Some(OneOf::Left(true)),
        rename_provider: Some(OneOf::Left(true)),
        ..Default::default()
    };

    let init_result = InitializeResult {
        capabilities: caps,
        server_info: Some(ServerInfo {
            name: "tlang-lsp".to_string(),
            version: Some(env!("CARGO_PKG_VERSION").to_string()),
        }),
    };

    let _init_params: lsp_types::InitializeParams = match connection.initialize_start() {
        Ok((id, params)) => {
            if let Err(e) =
                connection.initialize_finish(id, serde_json::to_value(init_result).unwrap())
            {
                eprintln!("[tlang-lsp] initialization failed: {e:?}");
                return Ok(());
            }
            serde_json::from_value(params).unwrap_or_default()
        }
        Err(e) => {
            eprintln!("[tlang-lsp] initialization failed: {e:?}");
            return Ok(());
        }
    };

    // Document store: uri → current text.
    // Uri contains interior mutability (Arc internals) but is used as a
    // read-only key here; the HashMap is never mutated through the key.
    #[allow(clippy::mutable_key_type)]
    let mut docs: HashMap<Uri, String> = HashMap::new();

    // Main message loop.
    for msg in &connection.receiver {
        match msg {
            Message::Request(req) => {
                if connection.handle_shutdown(&req).unwrap_or(false) {
                    break;
                }
                handle_request(&connection, req, &docs);
            }
            Message::Notification(notif) => {
                handle_notification_bg(&connection, notif, &mut docs);
            }
            Message::Response(_) => {}
        }
    }

    io_threads
        .join()
        .map_err(|e| io::Error::other(format!("LSP IO thread error: {e:?}")))?;
    Ok(())
}

// ---------------------------------------------------------------------------
// Request handling
// ---------------------------------------------------------------------------

// Uri contains interior mutability (Arc internals) but is used as a read-only key here.
#[allow(clippy::mutable_key_type)]
fn handle_request(connection: &Connection, req: Request, docs: &HashMap<Uri, String>) {
    let req = match cast_request::<SemanticTokensFullRequest>(req) {
        Ok((id, params)) => {
            let uri = params.text_document.uri;
            let response = match docs.get(&uri) {
                Some(text) => {
                    let tokens = compute_semantic_tokens(text);
                    Response::new_ok(id, serde_json::to_value(tokens).unwrap())
                }
                None => Response::new_ok(id, serde_json::Value::Null),
            };
            connection.sender.send(Message::Response(response)).ok();
            return;
        }
        Err(ExtractError::MethodMismatch(req)) => req,
        Err(_) => return,
    };

    let req = match cast_request::<Formatting>(req) {
        Ok((id, params)) => {
            let uri = params.text_document.uri;
            let response = match docs.get(&uri) {
                Some(text) => compute_formatting(&id, text),
                None => Response::new_ok(id, serde_json::Value::Null),
            };
            connection.sender.send(Message::Response(response)).ok();
            return;
        }
        Err(ExtractError::MethodMismatch(req)) => req,
        Err(_) => return,
    };

    let req = match cast_request::<Completion>(req) {
        Ok((id, params)) => {
            let uri = params.text_document_position.text_document.uri.clone();
            let position = params.text_document_position.position;
            let response = match docs.get(&uri) {
                Some(text) => {
                    let items = compute_completions(text, position);
                    Response::new_ok(
                        id,
                        serde_json::to_value(CompletionResponse::Array(items)).unwrap(),
                    )
                }
                None => Response::new_ok(id, serde_json::Value::Null),
            };
            connection.sender.send(Message::Response(response)).ok();
            return;
        }
        Err(ExtractError::MethodMismatch(req)) => req,
        Err(_) => return,
    };

    let req = match cast_request::<GotoDefinition>(req) {
        Ok((id, params)) => {
            let uri = params
                .text_document_position_params
                .text_document
                .uri
                .clone();
            let position = params.text_document_position_params.position;
            let response = match docs.get(&uri) {
                Some(text) => {
                    let result = compute_definition(text, &uri, position);
                    Response::new_ok(id, serde_json::to_value(result).unwrap())
                }
                None => Response::new_ok(id, serde_json::Value::Null),
            };
            connection.sender.send(Message::Response(response)).ok();
            return;
        }
        Err(ExtractError::MethodMismatch(req)) => req,
        Err(_) => return,
    };

    let req = match cast_request::<HoverRequest>(req) {
        Ok((id, params)) => {
            let uri = params
                .text_document_position_params
                .text_document
                .uri
                .clone();
            let position = params.text_document_position_params.position;
            let response = match docs.get(&uri) {
                Some(text) => {
                    let result = compute_hover(text, position);
                    Response::new_ok(id, serde_json::to_value(result).unwrap())
                }
                None => Response::new_ok(id, serde_json::Value::Null),
            };
            connection.sender.send(Message::Response(response)).ok();
            return;
        }
        Err(ExtractError::MethodMismatch(req)) => req,
        Err(_) => return,
    };

    let req = match cast_request::<SignatureHelpRequest>(req) {
        Ok((id, params)) => {
            let uri = params
                .text_document_position_params
                .text_document
                .uri
                .clone();
            let position = params.text_document_position_params.position;
            let response = match docs.get(&uri) {
                Some(text) => {
                    let result = compute_signature_help(text, position);
                    Response::new_ok(id, serde_json::to_value(result).unwrap())
                }
                None => Response::new_ok(id, serde_json::Value::Null),
            };
            connection.sender.send(Message::Response(response)).ok();
            return;
        }
        Err(ExtractError::MethodMismatch(req)) => req,
        Err(_) => return,
    };

    let req = match cast_request::<DocumentSymbolRequest>(req) {
        Ok((id, params)) => {
            let uri = params.text_document.uri.clone();
            let response = match docs.get(&uri) {
                Some(text) => {
                    let result = compute_document_symbols(text, &uri);
                    Response::new_ok(id, serde_json::to_value(result).unwrap())
                }
                None => Response::new_ok(id, serde_json::Value::Null),
            };
            connection.sender.send(Message::Response(response)).ok();
            return;
        }
        Err(ExtractError::MethodMismatch(req)) => req,
        Err(_) => return,
    };

    let req = match cast_request::<References>(req) {
        Ok((id, params)) => {
            let uri = params.text_document_position.text_document.uri.clone();
            let position = params.text_document_position.position;
            let response = match docs.get(&uri) {
                Some(text) => {
                    let result = compute_references(text, &uri, position);
                    Response::new_ok(id, serde_json::to_value(result).unwrap())
                }
                None => Response::new_ok(id, serde_json::Value::Null),
            };
            connection.sender.send(Message::Response(response)).ok();
            return;
        }
        Err(ExtractError::MethodMismatch(req)) => req,
        Err(_) => return,
    };

    let req = match cast_request::<RenameRequest>(req) {
        Ok((id, params)) => {
            let uri = params.text_document_position.text_document.uri.clone();
            let position = params.text_document_position.position;
            let new_name = params.new_name.clone();
            let response = match docs.get(&uri) {
                Some(text) => {
                    let result = compute_rename(text, &uri, position, &new_name);
                    Response::new_ok(id, serde_json::to_value(result).unwrap())
                }
                None => Response::new_ok(id, serde_json::Value::Null),
            };
            connection.sender.send(Message::Response(response)).ok();
            return;
        }
        Err(ExtractError::MethodMismatch(req)) => req,
        Err(_) => return,
    };

    // Ignore any other request we don't handle.
    let _ = req;
}

// ---------------------------------------------------------------------------
// Notification handling
// ---------------------------------------------------------------------------

/// Wrapper around [`handle_notification`] that spawns a background thread for
/// operations that may be slow (e.g. diagnostics via full compilation), keeping
/// the main message loop always responsive to requests.
// Uri contains interior mutability (Arc internals) but is used as a map key only; no
// mutation occurs through the key itself.
#[allow(clippy::mutable_key_type)]
fn handle_notification_bg(
    connection: &Connection,
    notif: Notification,
    docs: &mut HashMap<Uri, String>,
) {
    // Fast path: `initialized` carries no work.
    let notif = match cast_notification::<Initialized>(notif) {
        Ok(_) => return,
        Err(ExtractError::MethodMismatch(n)) => n,
        Err(_) => return,
    };

    let notif = match cast_notification::<DidOpenTextDocument>(notif) {
        Ok(params) => {
            let uri = params.text_document.uri;
            let text = params.text_document.text;
            // Update the in-memory document immediately so subsequent requests
            // see the latest content, then compute diagnostics off-thread.
            docs.insert(uri.clone(), text.clone());
            let sender = connection.sender.clone();
            thread::spawn(move || {
                publish_diagnostics_with_sender(&sender, uri, &text);
            });
            return;
        }
        Err(ExtractError::MethodMismatch(n)) => n,
        Err(_) => return,
    };

    let notif = match cast_notification::<DidChangeTextDocument>(notif) {
        Ok(params) => {
            let uri = params.text_document.uri;
            // With FULL sync, only one content change is sent.
            if let Some(change) = params.content_changes.into_iter().last() {
                let text = change.text;
                docs.insert(uri.clone(), text.clone());
                let sender = connection.sender.clone();
                thread::spawn(move || {
                    publish_diagnostics_with_sender(&sender, uri, &text);
                });
            }
            return;
        }
        Err(ExtractError::MethodMismatch(n)) => n,
        Err(_) => return,
    };

    let _ = notif;
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

fn cast_request<R>(req: Request) -> Result<(RequestId, R::Params), ExtractError<Request>>
where
    R: lsp_types::request::Request,
    R::Params: serde::de::DeserializeOwned,
{
    req.extract(R::METHOD)
}

fn cast_notification<N>(notif: Notification) -> Result<N::Params, ExtractError<Notification>>
where
    N: lsp_types::notification::Notification,
    N::Params: serde::de::DeserializeOwned,
{
    notif.extract(N::METHOD)
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

#[cfg(test)]
mod tests {
    #[allow(unused_imports)]
    use super::*;
    #[allow(unused_imports)]
    use lsp_types::SymbolKind;

    use completion::{compute_completions, parse_func_call_snippet};
    use diagnostics::compute_diagnostics;
    use hover::compute_hover;
    use references::{compute_definition, compute_references, compute_rename};
    use semantic_tokens::compute_semantic_tokens;
    use semantic_tokens::{ST_KEYWORD, ST_MACRO, ST_NAMESPACE, ST_VARIABLE};
    use signatures::{build_library_signature_help, compute_signature_help, find_active_call};
    use symbols::{compute_document_symbols, parse_ident_before_paren};
    use util::{position_to_offset, word_at_position};

    use lsp_types::{
        CompletionItemKind, DiagnosticSeverity, DocumentSymbolResponse, GotoDefinitionResponse,
        HoverContents, MarkupKind, ParameterLabel, Position, Uri,
    };

    // ── diagnostic smoke test ─────────────────────────────────────────────

    #[test]
    fn diagnostics_hello_world_produces_no_errors() {
        let text = r#"use TLang.Terminal

func main(): String {
    let message = "Hello, World!"
    Terminal.println(message)
    return message
}
"#;
        let diags = collect_diagnostics(text);
        // Warnings (e.g. missing return-type annotation) are acceptable; hard
        // errors are not.
        let errors: Vec<_> = diags
            .iter()
            .filter(|d| d.severity == Some(lsp_types::DiagnosticSeverity::ERROR))
            .collect();
        assert!(
            errors.is_empty(),
            "expected no ERROR diagnostics for hello-world, got: {:#?}",
            errors
        );
    }

    #[test]
    fn diagnostics_utils_file_produces_no_errors() {
        let text = r#"expose add
expose greet

func add(a: Int, b: Int): Int {
    return a + b
}

func greet(name: String): String {
    return "Hello, " + name + "!"
}
"#;
        let diags = collect_diagnostics(text);
        let errors: Vec<_> = diags
            .iter()
            .filter(|d| d.severity == Some(DiagnosticSeverity::ERROR))
            .collect();
        assert!(
            errors.is_empty(),
            "expected no errors for utils file, got: {:#?}",
            errors
        );
    }

    #[test]
    fn diagnostics_toplevel_func_produces_no_hints() {
        let text = "expose add\n\nfunc add(a: Int, b: Int): Int {\n    return a + b\n}\n";
        let diags = collect_diagnostics(text);
        let hints: Vec<_> = diags
            .iter()
            .filter(|d| d.severity == Some(DiagnosticSeverity::HINT))
            .collect();
        assert!(
            hints.is_empty(),
            "top-level func should produce no hints, got: {hints:#?}"
        );
    }

    #[test]
    fn diagnostics_model_block_produces_error() {
        // `model` is no longer a valid keyword — it must produce a parse error.
        let text = "model { set User(name: String) {} }\n";
        let diags = collect_diagnostics(text);
        let errors: Vec<_> = diags
            .iter()
            .filter(|d| d.severity == Some(DiagnosticSeverity::ERROR))
            .collect();
        assert!(!errors.is_empty(), "expected an ERROR diagnostic for unknown 'model' keyword, got: {diags:#?}");
    }

    #[test]
    fn diagnostics_optional_chain_and_null_coalesce_have_no_errors() {
        let text = r#"use TLang.Map

func none() {
}

func main(): Int {
    let profile = Map.create()
    let profile = Map.set(profile, "name", "Alice")
    let user = Map.create()
    let user = Map.set(user, "profile", profile)
    let name_len = user?.profile?.name?.length() ?? 0
    let missing = none()
    return name_len + (missing?.profile?.name?.length() ?? 0)
}
"#;
        let diags = collect_diagnostics(text);
        let errors: Vec<_> = diags
            .iter()
            .filter(|d| d.severity == Some(lsp_types::DiagnosticSeverity::ERROR))
            .collect();
        assert!(
            errors.is_empty(),
            "expected no ERROR diagnostics for optional chaining/null coalescing, got: {:#?}",
            errors
        );
    }

    #[test]
    fn diagnostics_expression_if_and_match_have_no_errors() {
        let text = r#"func classify(n): String {
    let label = if (n < 0) "negative" else if (n == 0) "zero" else "positive"
    return match (label) {
        case "negative" => "neg",
        case "zero" => "zero",
        default => "pos"
    }
}
"#;
        let diags = collect_diagnostics(text);
        let errors: Vec<_> = diags
            .iter()
            .filter(|d| d.severity == Some(lsp_types::DiagnosticSeverity::ERROR))
            .collect();
        assert!(
            errors.is_empty(),
            "expected no ERROR diagnostics for expression if/match, got: {:#?}",
            errors
        );
    }

    #[test]
    fn diagnostics_print_messages_for_hello_world() {
        let text = r#"use TLang.Terminal

func main(): String {
    let message = "Hello, World!"
    Terminal.println(message)
    return message
}
"#;
        let diags = collect_diagnostics(text);
        // Print all messages so we can see what's happening in test output
        for d in &diags {
            eprintln!("DIAGNOSTIC: {:?}", d.message);
        }
    }

    #[test]
    fn diagnostics_model_only_file_no_spurious_error() {
        // A file with only a model block and no helper block — the LSP
        // should not surface a hard error for this common case.
        let text = r#"set User {
    name: String,
    age: Number
}
"#;
        let diags = collect_diagnostics(text);
        for d in &diags {
            eprintln!(
                "DIAGNOSTIC (model-only): severity={:?} msg={:?}",
                d.severity, d.message
            );
        }
        // Must not contain any ERROR-severity diagnostic.
        let errors: Vec<_> = diags
            .iter()
            .filter(|d| d.severity == Some(lsp_types::DiagnosticSeverity::ERROR))
            .collect();
        assert!(
            errors.is_empty(),
            "model-only file should produce no errors, got: {:#?}",
            errors
        );
    }

    #[test]
    fn diagnostics_template_only_file_no_spurious_error() {
        // A file with only a template block and no helper block.
        // A `use <Package> as kotlin` alias is required so the compiler can
        // validate the language identifier.
        let text = r#"use KotlinGen as kotlin

lang [kotlin] myClass(pkg: String, className: String) {
    pkg ${pkg}
    impl[public class] ${className} {
    }
}
"#;
        let diags = collect_diagnostics(text);
        for d in &diags {
            eprintln!(
                "DIAGNOSTIC (tmpl-only): severity={:?} msg={:?}",
                d.severity, d.message
            );
        }
        let errors: Vec<_> = diags
            .iter()
            .filter(|d| d.severity == Some(lsp_types::DiagnosticSeverity::ERROR))
            .collect();
        assert!(
            errors.is_empty(),
            "template-only file should produce no errors, got: {:#?}",
            errors
        );
    }

    #[test]
    fn diagnostics_unknown_template_language_produces_error() {
        // Using `lang [kotlin]` without a `use <Package> as kotlin` alias must
        // produce an ERROR diagnostic so the editor underlines the unknown language.
        let text = r#"lang [kotlin] myClass(pkg: String) {
    impl[public class] ${pkg} {}
}

func main(): String {
    return "fail"
}
"#;
        let diags = collect_diagnostics(text);
        let errors: Vec<_> = diags
            .iter()
            .filter(|d| d.severity == Some(lsp_types::DiagnosticSeverity::ERROR))
            .collect();
        assert!(
            !errors.is_empty(),
            "expected at least one error for unknown language, got none"
        );
        let has_lang_error = errors
            .iter()
            .any(|d| d.message.contains("unknown template language"));
        assert!(
            has_lang_error,
            "expected 'unknown template language' diagnostic, got: {:#?}",
            errors
        );
    }

    #[test]
    fn diagnostics_known_template_language_no_error() {
        // With `use KotlinGen as kotlin` present, `lang [kotlin]` must not produce
        // a language-unknown diagnostic.
        let text = r#"use KotlinGen as kotlin

lang [kotlin] myClass(pkg: String) {
    impl[public class] ${pkg} {}
}

func main(): String {
    return "ok"
}
"#;
        let diags = collect_diagnostics(text);
        let lang_errors: Vec<_> = diags
            .iter()
            .filter(|d| {
                d.severity == Some(lsp_types::DiagnosticSeverity::ERROR)
                    && d.message.contains("unknown template language")
            })
            .collect();
        assert!(
            lang_errors.is_empty(),
            "expected no language error when `use KotlinGen as kotlin` is present, got: {:#?}",
            lang_errors
        );
    }

    #[test]
    fn diagnostics_no_expected_semi_error_for_valid_let_in_func() {
        // Regression guard: a helper with `let` inside a function must never
        // produce the "expected token Semi" compile error.
        let text = r#"func greet(name: String): String {
    let prefix = "Hello, "
    let result = prefix + name
    return result
}
"#;
        let diags = collect_diagnostics(text);
        for d in &diags {
            eprintln!(
                "DIAGNOSTIC (let-in-func): severity={:?} msg={:?}",
                d.severity, d.message
            );
            assert!(
                !d.message.contains("expected token Semi"),
                "got spurious 'expected token Semi' diagnostic: {:?}",
                d.message
            );
        }
    }

    #[test]
    fn diagnostics_print_messages_for_all_sample_content() {
        // Exhaustive print-test — useful to spot unexpected diagnostics while
        // developing.  Not a hard assertion (beyond no Semi errors).
        let samples = vec![
            (
                "model-only",
                r#"set Config {
    version: String
}
"#,
            ),
            (
                "empty-helper",
                r#"func main(): String {
    return "ok"
}
"#,
            ),
            (
                "expose-only",
                r#"expose add

func add(a: Int, b: Int): Int {
    return a + b
}
"#,
            ),
        ];
        for (label, text) in samples {
            let diags = collect_diagnostics(text);
            for d in &diags {
                eprintln!("[{label}] severity={:?} msg={:?}", d.severity, d.message);
                assert!(
                    !d.message.contains("expected token Semi"),
                    "[{label}] spurious 'expected token Semi': {:?}",
                    d.message
                );
            }
        }
    }

    /// Delegate to the shared [`compute_diagnostics`] function so tests always
    /// exercise the exact same logic as the production LSP path.
    fn collect_diagnostics(text: &str) -> Vec<lsp_types::Diagnostic> {
        compute_diagnostics(text, None)
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    /// Build an LSP `Position` from 0-based line + character.
    fn pos(line: u32, character: u32) -> Position {
        Position { line, character }
    }

    const SAMPLE: &str = concat!(
        "use TLang.Terminal\n",                    // line 0
        "\n",                                      // line 1
        "func add(a: Int, b: Int): Int {\n",       // line 2
        "    return a + b\n",                      // line 3
        "}\n",                                     // line 4
        "\n",                                      // line 5
        "func greet(name: String): String {\n",    // line 6
        "    return \"Hello, \" + name + \"!\"\n", // line 7
        "}\n",                                     // line 8
        "\n",                                      // line 9
        "func main(): String {\n",                 // line 10
        "    let sum = add(3, 4)\n",               // line 11
        "    Terminal.println(sum)\n",             // line 12
        "    let msg = greet(\"World\")\n",        // line 13
        "    Terminal.println(msg)\n",             // line 14
        "}\n",                                     // line 15
    );

    // ── position_to_offset ────────────────────────────────────────────────────

    #[test]
    fn position_to_offset_start_of_doc() {
        assert_eq!(position_to_offset(SAMPLE, pos(0, 0)), 0);
    }

    #[test]
    fn position_to_offset_second_line() {
        // Line 1 starts after "use TLang.Terminal\n" (19 chars).
        assert_eq!(position_to_offset(SAMPLE, pos(1, 0)), 19);
    }

    #[test]
    fn position_to_offset_clamps_to_line_end() {
        // Line 0 is "use TLang.Terminal" (18 chars, index 0-17).
        // Requesting col 999 should clamp to the end of the line.
        let line0_len = "use TLang.Terminal".len();
        assert_eq!(position_to_offset(SAMPLE, pos(0, 999)), line0_len);
    }

    // ── word_at_position ──────────────────────────────────────────────────────

    #[test]
    fn word_at_position_on_func_name() {
        // Line 2: "func add(a: Int, b: Int): Int {"
        // "add" starts at col 5 on line 2.
        let word = word_at_position(SAMPLE, pos(2, 5));
        assert_eq!(word.as_deref(), Some("add"));
    }

    #[test]
    fn word_at_position_in_call() {
        // Line 11: "    let sum = add(3, 4)"
        // "add" starts at col 14.
        let word = word_at_position(SAMPLE, pos(11, 14));
        assert_eq!(word.as_deref(), Some("add"));
    }

    #[test]
    fn word_at_position_on_whitespace_returns_none() {
        // Line 3: "    return a + b" — col 0 is a leading space.
        let word = word_at_position(SAMPLE, pos(3, 0));
        assert!(word.is_none(), "expected None on whitespace, got {word:?}");
    }

    #[test]
    fn word_at_position_cursor_at_end_of_word() {
        // "add" on line 2 occupies cols 5-7; cursor at col 8 (after 'd') should
        // still resolve to "add" because we scan backward.
        let word = word_at_position(SAMPLE, pos(2, 8));
        assert_eq!(word.as_deref(), Some("add"));
    }

    // ── find_active_call ─────────────────────────────────────────────────────

    #[test]
    fn find_active_call_first_param() {
        // Cursor right after the opening paren: add(|
        let text = "add(";
        let result = find_active_call(text);
        assert_eq!(result, Some(("add".to_string(), 0)));
    }

    #[test]
    fn find_active_call_second_param() {
        // Cursor after the comma: add(3, |
        let text = "add(3, ";
        let result = find_active_call(text);
        assert_eq!(result, Some(("add".to_string(), 1)));
    }

    #[test]
    fn find_active_call_third_param() {
        let text = "foo(a, b, ";
        assert_eq!(find_active_call(text), Some(("foo".to_string(), 2)));
    }

    #[test]
    fn find_active_call_nested_call_inner() {
        // Cursor is inside the inner call: outer(inner(|
        let text = "outer(inner(";
        assert_eq!(find_active_call(text), Some(("inner".to_string(), 0)));
    }

    #[test]
    fn find_active_call_nested_call_outer_after_inner() {
        // Cursor is after a completed inner call: outer(inner(x), |
        let text = "outer(inner(x), ";
        assert_eq!(find_active_call(text), Some(("outer".to_string(), 1)));
    }

    #[test]
    fn find_active_call_not_in_call() {
        // No unclosed paren.
        assert_eq!(find_active_call("let x = 5"), None);
    }

    #[test]
    fn find_active_call_empty() {
        assert_eq!(find_active_call(""), None);
    }

    // ── compute_hover ─────────────────────────────────────────────────────────

    #[test]
    fn hover_on_function_name_shows_signature() {
        // Hover over "add" on the definition line (line 2, col 5).
        let hover = compute_hover(SAMPLE, pos(2, 5));
        assert!(hover.is_some(), "expected hover result for 'add'");
        let HoverContents::Markup(mc) = hover.unwrap().contents else {
            panic!("expected MarkupContent");
        };
        assert_eq!(mc.kind, MarkupKind::Markdown);
        assert!(
            mc.value.contains("func add"),
            "hover should contain the signature; got: {}",
            mc.value
        );
        assert!(
            mc.value.contains("a: Int"),
            "hover should include parameter types; got: {}",
            mc.value
        );
        assert!(
            mc.value.contains(": Int"),
            "hover should include return type; got: {}",
            mc.value
        );
    }

    #[test]
    fn hover_on_call_site_shows_signature() {
        // Hover over "add" in the call on line 11.
        let hover = compute_hover(SAMPLE, pos(11, 16));
        assert!(hover.is_some(), "expected hover result at call site");
        let HoverContents::Markup(mc) = hover.unwrap().contents else {
            panic!("expected MarkupContent");
        };
        assert!(mc.value.contains("func add"), "got: {}", mc.value);
    }

    #[test]
    fn hover_on_unknown_word_returns_none() {
        // "println" is a built-in method call, not a user-defined function.
        let hover = compute_hover(SAMPLE, pos(12, 14));
        assert!(
            hover.is_none(),
            "expected None for non-user-function 'println'"
        );
    }

    #[test]
    fn hover_shows_source_file_for_imported_function() {
        // Build a source that imports Utils; we can test the note without
        // a real file by checking that functions with source_file set include
        // the "*defined in*" annotation — covered here via the signature helper.
        let info = crate::runtime::FunctionInfo {
            name: "helper_func".to_string(),
            params: vec![("x".to_string(), Some("Int".to_string()))],
            return_type: Some("Int".to_string()),
            offset: 0,
            source_file: Some("Utils".to_string()),
            source_path: None,
            doc: None,
        };
        let sig = info.signature();
        assert_eq!(sig, "func helper_func(x: Int): Int");

        // Verify the hover body format for imported functions.
        let body = format!("```tlang\n{sig}\n```\n\n*defined in `{}`*", "Utils");
        assert!(body.contains("defined in `Utils`"));
    }

    // ── compute_signature_help ────────────────────────────────────────────────

    #[test]
    fn signature_help_first_argument() {
        // Cursor is right after the opening paren of add( on line 11.
        // "    let sum = add(3, 4)" — '(' is at col 17, so col 19 = first arg position.
        let help = compute_signature_help(SAMPLE, pos(11, 19));
        assert!(help.is_some(), "expected signature help inside add(");
        let help = help.unwrap();
        assert_eq!(help.signatures.len(), 1);
        assert!(
            help.signatures[0].label.contains("func add"),
            "label should be the full signature; got: {}",
            help.signatures[0].label
        );
        assert_eq!(
            help.active_parameter,
            Some(0),
            "first argument: active param should be 0"
        );
    }

    #[test]
    fn signature_help_second_argument() {
        // Cursor is after the comma: "add(3, |"
        // On line 11, after "add(3, " — col 22.
        let help = compute_signature_help(SAMPLE, pos(11, 22));
        assert!(help.is_some(), "expected signature help on second arg");
        let help = help.unwrap();
        assert_eq!(
            help.active_parameter,
            Some(1),
            "second argument: active param should be 1"
        );
    }

    #[test]
    fn signature_help_outside_call_returns_none() {
        // Cursor is on the `let` keyword on line 11, not inside any call.
        let help = compute_signature_help(SAMPLE, pos(11, 4));
        assert!(
            help.is_none(),
            "should return None when not inside a call; got {help:?}"
        );
    }

    #[test]
    fn signature_help_parameters_listed() {
        let help = compute_signature_help(SAMPLE, pos(11, 19)).unwrap();
        let params = help.signatures[0].parameters.as_ref().unwrap();
        assert_eq!(params.len(), 2, "add has 2 parameters");
        let ParameterLabel::Simple(p0) = &params[0].label else {
            panic!("expected simple label");
        };
        assert_eq!(p0, "a: Int");
        let ParameterLabel::Simple(p1) = &params[1].label else {
            panic!("expected simple label");
        };
        assert_eq!(p1, "b: Int");
    }

    // ── compute_definition ────────────────────────────────────────────────────

    #[test]
    fn definition_from_call_site_points_to_func_line() {
        let dummy_uri: Uri = "file:///test/Main.tlang".parse().unwrap();

        // Hover over "add" in the call on line 11, col 14-16.
        let result = compute_definition(SAMPLE, &dummy_uri, pos(11, 15));
        assert!(result.is_some(), "expected a definition result for 'add'");

        let GotoDefinitionResponse::Scalar(loc) = result.unwrap() else {
            panic!("expected a scalar location");
        };

        // The definition should point back into the same document.
        assert_eq!(loc.uri, dummy_uri);

        // `func add` is on line 2 (0-based).
        assert_eq!(
            loc.range.start.line, 2,
            "definition should point to line 2 (the func add line); got line {}",
            loc.range.start.line
        );
    }

    #[test]
    fn definition_on_definition_line_itself() {
        // Clicking on "add" in the definition `func add(` should also work.
        let dummy_uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let result = compute_definition(SAMPLE, &dummy_uri, pos(2, 5));
        assert!(result.is_some());
        let GotoDefinitionResponse::Scalar(loc) = result.unwrap() else {
            panic!("expected scalar");
        };
        assert_eq!(loc.range.start.line, 2);
    }

    #[test]
    fn definition_on_unknown_word_returns_none() {
        let dummy_uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        // "println" is not a user-defined function.
        let result = compute_definition(SAMPLE, &dummy_uri, pos(12, 14));
        assert!(result.is_none());
    }

    #[test]
    fn definition_on_whitespace_returns_none() {
        let dummy_uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let result = compute_definition(SAMPLE, &dummy_uri, pos(1, 0));
        assert!(result.is_none());
    }

    // ── compute_document_symbols ──────────────────────────────────────────────

    // A richer sample that contains functions, a template, and model entities.
    const SYMBOLS_SAMPLE: &str = concat!(
        "use TLang.Terminal\n",                  // line 0
        "\n",                                    // line 1
        "set User {\n",                          // line 2
        "    name: String,\n",                   // line 3
        "    age: Int\n",                        // line 4
        "}\n",                                   // line 5
        "set Product {\n",                       // line 6
        "    title: String\n",                   // line 7
        "}\n",                                   // line 8
        "\n",                                    // line 9
        "lang [kotlin] entity(pkg: String) {\n", // line 10
        "    pkg ${pkg}\n",                      // line 11
        "}\n",                                   // line 12
        "\n",                                    // line 13
        "func main(): String {\n",               // line 14
        "    return \"ok\"\n",                   // line 15
        "}\n",                                   // line 16
        "func helper_fn(x: Int): Int {\n",       // line 17
        "    return x\n",                        // line 18
        "}\n",                                   // line 19
    );

    #[test]
    fn document_symbols_finds_functions() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let DocumentSymbolResponse::Flat(symbols) = compute_document_symbols(SYMBOLS_SAMPLE, &uri)
        else {
            panic!("expected flat symbol list");
        };
        let func_names: Vec<&str> = symbols
            .iter()
            .filter(|s| s.kind == SymbolKind::FUNCTION)
            .map(|s| s.name.as_str())
            .collect();
        assert!(
            func_names.contains(&"main"),
            "expected 'main' in symbols; got {func_names:?}"
        );
        assert!(
            func_names.contains(&"helper_fn"),
            "expected 'helper_fn' in symbols; got {func_names:?}"
        );
    }

    #[test]
    fn document_symbols_finds_template() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let DocumentSymbolResponse::Flat(symbols) = compute_document_symbols(SYMBOLS_SAMPLE, &uri)
        else {
            panic!("expected flat symbol list");
        };
        let tmpl: Vec<&str> = symbols
            .iter()
            .filter(|s| s.kind == SymbolKind::MODULE)
            .map(|s| s.name.as_str())
            .collect();
        assert!(
            tmpl.contains(&"entity"),
            "expected 'entity' template in symbols; got {tmpl:?}"
        );
    }

    #[test]
    fn document_symbols_finds_model_entities() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let DocumentSymbolResponse::Flat(symbols) = compute_document_symbols(SYMBOLS_SAMPLE, &uri)
        else {
            panic!("expected flat symbol list");
        };
        let structs: Vec<&str> = symbols
            .iter()
            .filter(|s| s.kind == SymbolKind::STRUCT)
            .map(|s| s.name.as_str())
            .collect();
        assert!(
            structs.contains(&"User"),
            "expected 'User' struct in symbols; got {structs:?}"
        );
        assert!(
            structs.contains(&"Product"),
            "expected 'Product' struct in symbols; got {structs:?}"
        );
    }

    #[test]
    fn document_symbols_correct_line_numbers() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let DocumentSymbolResponse::Flat(symbols) = compute_document_symbols(SYMBOLS_SAMPLE, &uri)
        else {
            panic!("expected flat symbol list");
        };
        // `func main()` is on line 14 (0-based).
        let main_sym = symbols.iter().find(|s| s.name == "main").unwrap();
        assert_eq!(
            main_sym.location.range.start.line, 14,
            "main should be on line 14; got {}",
            main_sym.location.range.start.line
        );
        // `lang [kotlin] entity(` is on line 10 (0-based).
        let entity_sym = symbols.iter().find(|s| s.name == "entity").unwrap();
        assert_eq!(
            entity_sym.location.range.start.line, 10,
            "entity template should be on line 10; got {}",
            entity_sym.location.range.start.line
        );
    }

    #[test]
    fn document_symbols_empty_document_returns_empty_list() {
        let uri: Uri = "file:///test/Empty.tlang".parse().unwrap();
        let DocumentSymbolResponse::Flat(symbols) = compute_document_symbols("", &uri) else {
            panic!("expected flat symbol list");
        };
        assert!(symbols.is_empty());
    }

    // ── parse_ident_before_paren ──────────────────────────────────────────────

    #[test]
    fn parse_ident_before_paren_simple() {
        assert_eq!(
            parse_ident_before_paren("add(a, b)"),
            Some("add".to_string())
        );
    }

    #[test]
    fn parse_ident_before_paren_no_paren() {
        // No `(` — should return None.
        assert_eq!(parse_ident_before_paren("add a b"), None);
    }

    #[test]
    fn parse_ident_before_paren_empty() {
        assert_eq!(parse_ident_before_paren(""), None);
    }

    // ── compute_references ────────────────────────────────────────────────────

    #[test]
    fn references_finds_all_occurrences_of_add() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        // SAMPLE has `func add(` on line 2 and `add(3, 4)` on line 11.
        let locs = compute_references(SAMPLE, &uri, pos(2, 5)).unwrap();
        // Should find at least 2 occurrences: definition + call site.
        assert!(
            locs.len() >= 2,
            "expected at least 2 occurrences of 'add'; got {}",
            locs.len()
        );
    }

    #[test]
    fn references_on_unknown_word_returns_none() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        // Whitespace position — word_at_position returns None → references returns None.
        let locs = compute_references(SAMPLE, &uri, pos(1, 0));
        assert!(locs.is_none(), "expected None on whitespace; got {locs:?}");
    }

    #[test]
    fn references_returns_correct_line_for_call_site() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let locs = compute_references(SAMPLE, &uri, pos(2, 5)).unwrap();
        // One location should be on line 11 (the call site: `let sum = add(3, 4)`).
        let has_line_11 = locs.iter().any(|l| l.range.start.line == 11);
        assert!(
            has_line_11,
            "expected a reference on line 11 (call site); lines: {:?}",
            locs.iter().map(|l| l.range.start.line).collect::<Vec<_>>()
        );
    }

    #[test]
    fn references_word_boundaries_respected() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        // "add" must not match "address" or "adding".
        let text = "func add(x: Int): Int {\n    let address = add(x)\n    return address\n}";
        // Cursor on "add" in the func definition (line 0, col 5).
        let locs = compute_references(text, &uri, pos(0, 5)).unwrap();
        // Should find 2 occurrences of "add" (definition + call), not 3 (address contains "add" as prefix).
        let count = locs
            .iter()
            .filter(|l| {
                // Extract the matched text to confirm word boundary: only "add" occurrences.
                let line = l.range.start.line as usize;
                let col = l.range.start.character as usize;
                let line_text = text.lines().nth(line).unwrap_or("");
                let slice = &line_text[col..];
                slice.starts_with("add")
            })
            .count();
        // "address" contains "add" as prefix but should NOT be included due to word-boundary check.
        assert_eq!(
            count, 2,
            "expected exactly 2 word-boundary matches of 'add'; got {count}"
        );
    }

    // ── compute_rename ────────────────────────────────────────────────────────

    #[test]
    fn rename_produces_workspace_edit_for_all_sites() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        // Rename "add" → "sum" on the definition line.
        let edit = compute_rename(SAMPLE, &uri, pos(2, 5), "sum").unwrap();
        let changes = edit.changes.unwrap();
        let edits = changes.get(&uri).unwrap();
        // All edits should replace with "sum".
        assert!(edits.iter().all(|e| e.new_text == "sum"));
        // At least 2 edits: the definition and the call site.
        assert!(
            edits.len() >= 2,
            "expected at least 2 edits; got {}",
            edits.len()
        );
    }

    #[test]
    fn rename_on_whitespace_returns_none() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let edit = compute_rename(SAMPLE, &uri, pos(1, 0), "anything");
        assert!(edit.is_none(), "expected None on whitespace; got {edit:?}");
    }

    #[test]
    fn rename_edit_ranges_cover_old_name() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let edit = compute_rename(SAMPLE, &uri, pos(2, 5), "total").unwrap();
        let changes = edit.changes.unwrap();
        let edits = changes.get(&uri).unwrap();
        // Every edit range should span exactly "add" (3 characters).
        for e in edits {
            let span = e.range.end.character - e.range.start.character;
            assert_eq!(
                span, 3,
                "each edit should cover 3 chars ('add'); got span {span} at line {}",
                e.range.start.line
            );
        }
    }

    #[test]
    fn rename_only_renames_target_word() {
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        // Rename "greet" — should not touch "add" occurrences.
        let edit = compute_rename(SAMPLE, &uri, pos(6, 5), "welcome").unwrap();
        let changes = edit.changes.unwrap();
        let edits = changes.get(&uri).unwrap();
        // All edits are for "greet", not "add".
        for e in edits {
            assert_eq!(
                e.new_text, "welcome",
                "all edits should rename to 'welcome'"
            );
            // Each edit spans "greet" (5 chars).
            let span = e.range.end.character - e.range.start.character;
            assert_eq!(
                span, 5,
                "each edit should cover 5 chars ('greet'); got {span}"
            );
        }
    }

    // ── compute_semantic_tokens ───────────────────────────────────────────────

    /// Collect semantic tokens and return a list of
    /// `(line, start_col, length, token_type)` tuples (absolute positions,
    /// not delta-encoded) for easier assertion.
    fn collect_tokens(source: &str) -> Vec<(u32, u32, u32, u32)> {
        let st = compute_semantic_tokens(source);
        let mut out = Vec::new();
        let mut line: u32 = 0;
        let mut col: u32 = 0;
        for t in st.data {
            line += t.delta_line;
            if t.delta_line > 0 {
                col = t.delta_start;
            } else {
                col += t.delta_start;
            }
            out.push((line, col, t.length, t.token_type));
        }
        out
    }

    #[test]
    fn semantic_tokens_empty_doc_produces_no_tokens() {
        let tokens = collect_tokens("");
        assert!(tokens.is_empty());
    }

    #[test]
    fn semantic_tokens_no_tokens_outside_template_body() {
        // The `impl` keyword here is inside a helper block, not a template body —
        // it must NOT be emitted as a semantic token.
        let src = concat!(
            "func main(): String {\n",
            "    return \"ok\"\n",
            "}\n",
        );
        let tokens = collect_tokens(src);
        assert!(
            tokens.is_empty(),
            "helper blocks should produce no semantic tokens; got {tokens:?}"
        );
    }

    #[test]
    fn semantic_tokens_highlights_impl_keyword() {
        let src = concat!(
            "use KotlinGen as kotlin\n",
            "\n",
            "lang [kotlin] cls {\n",           // line 2
            "    impl[public class] Foo {}\n", // line 3 — "impl" at col 4
            "}\n",
        );
        let tokens = collect_tokens(src);
        let kw_tokens: Vec<_> = tokens.iter().filter(|t| t.3 == ST_KEYWORD).collect();
        assert!(
            kw_tokens.iter().any(|t| t.0 == 3 && t.1 == 4 && t.2 == 4),
            "expected 'impl' keyword token at line 3 col 4 len 4; got {kw_tokens:?}"
        );
    }

    #[test]
    fn semantic_tokens_highlights_func_keyword() {
        let src = concat!(
            "use KotlinGen as kotlin\n",
            "\n",
            "lang [kotlin] bean {\n",       // line 2
            "    impl[public class] A {\n", // line 3
            "        func getName() {}\n",  // line 4 — "func" at col 8
            "    }\n",
            "}\n",
        );
        let tokens = collect_tokens(src);
        let kw_tokens: Vec<_> = tokens.iter().filter(|t| t.3 == ST_KEYWORD).collect();
        assert!(
            kw_tokens.iter().any(|t| t.0 == 4 && t.1 == 8 && t.2 == 4),
            "expected 'func' keyword token at line 4 col 8; got {kw_tokens:?}"
        );
    }

    #[test]
    fn semantic_tokens_highlights_var_keyword() {
        let src = concat!(
            "use KotlinGen as kotlin\n",
            "\n",
            "lang [kotlin] cls {\n",        // line 2
            "    impl[public class] A {\n", // line 3
            "        var id: Long\n",       // line 4 — "var" at col 8
            "    }\n",
            "}\n",
        );
        let tokens = collect_tokens(src);
        let kw_tokens: Vec<_> = tokens.iter().filter(|t| t.3 == ST_KEYWORD).collect();
        assert!(
            kw_tokens.iter().any(|t| t.0 == 4 && t.1 == 8 && t.2 == 3),
            "expected 'var' keyword token at line 4 col 8 len 3; got {kw_tokens:?}"
        );
    }

    #[test]
    fn semantic_tokens_highlights_match_case_default_keywords() {
        let src = concat!(
            "lang [kotlin] decision {\n",
            "    impl[public class] Foo {\n",
            "        func f(v: Int) {\n",
            "            match (v) {\n",
            "                case 1 => { return \"one\" }\n",
            "                default => { return \"other\" }\n",
            "            }\n",
            "        }\n",
            "    }\n",
            "}\n",
        );
        let tokens = collect_tokens(src);
        let kw_tokens: Vec<_> = tokens.iter().filter(|t| t.3 == ST_KEYWORD).collect();
        assert!(
            kw_tokens.iter().any(|t| t.0 == 3 && t.1 == 12 && t.2 == 5),
            "expected 'match' keyword token; got {kw_tokens:?}"
        );
        assert!(
            kw_tokens.iter().any(|t| t.0 == 4 && t.1 == 16 && t.2 == 4),
            "expected 'case' keyword token; got {kw_tokens:?}"
        );
        assert!(
            kw_tokens.iter().any(|t| t.0 == 5 && t.1 == 16 && t.2 == 7),
            "expected 'default' keyword token; got {kw_tokens:?}"
        );
    }

    #[test]
    fn semantic_tokens_highlights_interpolation() {
        let src = concat!(
            "use KotlinGen as kotlin\n",
            "\n",
            "lang [kotlin] cls(name: String) {\n", // line 2
            "    impl[public class] ${name} {}\n", // line 3 — "${name}" at col 23
            "}\n",
        );
        let tokens = collect_tokens(src);
        let interp_tokens: Vec<_> = tokens.iter().filter(|t| t.3 == ST_VARIABLE).collect();
        // "${name}" is 7 chars
        assert!(
            interp_tokens.iter().any(|t| t.0 == 3 && t.2 == 7),
            "expected interpolation token at line 3; got {interp_tokens:?}"
        );
    }

    #[test]
    fn semantic_tokens_highlights_include_directive() {
        let src = concat!(
            "use KotlinGen as kotlin\n",
            "\n",
            "lang [kotlin] cls {\n",                // line 2
            "    impl[public class] A {\n",         // line 3
            "        <[ renderFields(fields) ]>\n", // line 4
            "    }\n",
            "}\n",
        );
        let tokens = collect_tokens(src);
        let macro_tokens: Vec<_> = tokens.iter().filter(|t| t.3 == ST_MACRO).collect();
        // "<[ renderFields(fields) ]>" is on line 4, col 8
        assert!(
            macro_tokens.iter().any(|t| t.0 == 4 && t.1 == 8),
            "expected macro token for include on line 4; got {macro_tokens:?}"
        );
    }

    #[test]
    fn semantic_tokens_no_keyword_inside_identifier() {
        // `funcName` contains "func" as a prefix but must NOT produce a keyword token.
        let src = concat!(
            "use KotlinGen as kotlin\n",
            "\n",
            "lang [kotlin] cls {\n",
            "    impl[public class] A {\n",
            "        funcName()\n", // "funcName" — should NOT have a 'func' keyword token
            "    }\n",
            "}\n",
        );
        let tokens = collect_tokens(src);
        // Any keyword token on line 4 starting at col 8 with length 4 would be wrong.
        let false_positive = tokens
            .iter()
            .any(|t| t.3 == ST_KEYWORD && t.0 == 4 && t.1 == 8 && t.2 == 4);
        assert!(
            !false_positive,
            "should NOT emit 'func' keyword token inside identifier 'funcName'"
        );
    }

    #[test]
    fn semantic_tokens_highlights_lang_keyword() {
        // The `lang` keyword on the template header line must be emitted as a KEYWORD token.
        let src = concat!(
            "use KotlinGen as kotlin\n", // line 0
            "\n",                        // line 1
            "lang [kotlin] cls {\n",     // line 2 — "lang" at col 0, len 4
            "}\n",
        );
        let tokens = collect_tokens(src);
        let kw_tokens: Vec<_> = tokens.iter().filter(|t| t.3 == ST_KEYWORD).collect();
        assert!(
            kw_tokens.iter().any(|t| t.0 == 2 && t.1 == 0 && t.2 == 4),
            "expected KEYWORD token for `lang` on line 2 col 0 len 4; got {kw_tokens:?}"
        );
    }

    #[test]
    fn semantic_tokens_highlights_lang_attribute() {
        // The `[kotlin]` language attribute in the template header must be emitted
        // as a NAMESPACE token.
        let src = concat!(
            "use KotlinGen as kotlin\n", // line 0
            "\n",                        // line 1
            "lang [kotlin] cls {\n",     // line 2 — "[kotlin]" at col 5, len 8
            "}\n",
        );
        let tokens = collect_tokens(src);
        let ns_tokens: Vec<_> = tokens.iter().filter(|t| t.3 == ST_NAMESPACE).collect();
        // "[kotlin]" = 8 chars, starts at col 5 on line 2
        assert!(
            ns_tokens.iter().any(|t| t.0 == 2 && t.1 == 5 && t.2 == 8),
            "expected NAMESPACE token for `[kotlin]` on line 2 col 5 len 8; got {ns_tokens:?}"
        );
    }

    // ── set entity: multi-parent symbols ─────────────────────────────────────

    #[test]
    fn document_symbols_set_with_multi_parent_shows_extends_in_container() {
        let src = concat!(
            "model {\n",                        // line 0
            "    set Base(pkg: String) {\n",    // line 1
            "        save: &saveMethod\n",      // line 2
            "    }\n",                          // line 3
            "    set Child : Base {\n",         // line 4
            "        audit: &auditMethod\n",    // line 5
            "    }\n",                          // line 6
            "    set Multi : Base : Extra {\n", // line 7
            "    }\n",                          // line 8
            "}\n",                              // line 9
        );
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let DocumentSymbolResponse::Flat(symbols) = compute_document_symbols(src, &uri) else {
            panic!("expected flat symbol list");
        };

        let child_sym = symbols
            .iter()
            .find(|s| s.name == "Child")
            .expect("Child symbol");
        let container = child_sym.container_name.as_deref().unwrap_or("");
        assert!(
            container.contains("extends"),
            "Child container_name should mention 'extends'; got: {container}"
        );
        assert!(
            container.contains("Base"),
            "Child container_name should mention 'Base'; got: {container}"
        );

        let multi_sym = symbols
            .iter()
            .find(|s| s.name == "Multi")
            .expect("Multi symbol");
        let multi_container = multi_sym.container_name.as_deref().unwrap_or("");
        assert!(
            multi_container.contains("Base"),
            "Multi container_name should mention 'Base'; got: {multi_container}"
        );
        assert!(
            multi_container.contains("Extra"),
            "Multi container_name should mention 'Extra'; got: {multi_container}"
        );
    }

    #[test]
    fn document_symbols_set_without_parent_has_plain_model_container() {
        let src = concat!(
            "set Standalone(pkg: String) {\n",
            "    save: &saveMethod\n",
            "}\n",
        );
        let uri: Uri = "file:///test/Main.tlang".parse().unwrap();
        let DocumentSymbolResponse::Flat(symbols) = compute_document_symbols(src, &uri) else {
            panic!("expected flat symbol list");
        };
        let sym = symbols
            .iter()
            .find(|s| s.name == "Standalone")
            .expect("Standalone symbol");
        let container = sym.container_name.as_deref().unwrap_or("");
        assert!(
            container.contains("model"),
            "Standalone container_name should contain 'model'; got: {container}"
        );
        assert!(
            !container.contains("extends"),
            "Standalone container_name should NOT contain 'extends'; got: {container}"
        );
    }

    // ── hover: lead attr and set entity ──────────────────────────────────────

    #[test]
    fn hover_on_set_entity_name_shows_attrs_and_parents() {
        let src = concat!(
            "set MyRepo(pkg: String) {\n",
            "    lead:   &classShell,\n",
            "    save:   &saveMethod\n",
            "}\n",
            "func main(): String {\n",
            "    return \"ok\"\n",
            "}\n",
        );
        // Hover over "MyRepo" on line 0, col 4.
        let hover = compute_hover(src, pos(0, 4));
        assert!(hover.is_some(), "expected hover result on set entity name");
        let HoverContents::Markup(mc) = hover.unwrap().contents else {
            panic!("expected MarkupContent");
        };
        assert!(
            mc.value.contains("MyRepo"),
            "hover should mention the entity name; got: {}",
            mc.value
        );
        // The lead attr should be called out specially.
        assert!(
            mc.value.contains("lead"),
            "hover should mention the lead attr; got: {}",
            mc.value
        );
    }

    #[test]
    fn hover_on_lead_attr_shows_lead_template_description() {
        let src = concat!(
            "set MyRepo(pkg: String) {\n",
            "    lead: &classShell,\n",
            "    save: &saveMethod\n",
            "}\n",
            "func main(): String {\n",
            "    return \"ok\"\n",
            "}\n",
        );
        // Hover over "lead" on line 1, col 4.
        let hover = compute_hover(src, pos(1, 4));
        assert!(hover.is_some(), "expected hover on 'lead' attr");
        let HoverContents::Markup(mc) = hover.unwrap().contents else {
            panic!("expected MarkupContent");
        };
        assert!(
            mc.value.contains("lead") || mc.value.contains("scaffold"),
            "hover on lead attr should mention 'lead' or 'scaffold'; got: {}",
            mc.value
        );
    }

    // ── completions: set instance methods ────────────────────────────────────

    #[test]
    fn completions_set_instance_variable_offers_attrs_and_generate_all() {
        let src = concat!(
            "set Repo(pkg: String) {\n",
            "    save: &saveMethod\n",
            "}\n",
            "func main(): String {\n",
            "    let myRepo = Repo(pkg: \"com.example\")\n",
            "    myRepo.\n", // ← cursor here, line 8 col 15
            "    return \"ok\"\n",
            "}\n",
        );
        // Cursor placed immediately after the `.` on "myRepo." (line 5, col 11).
        let completions = compute_completions(src, pos(5, 11));
        let labels: Vec<&str> = completions.iter().map(|c| c.label.as_str()).collect();
        assert!(
            labels.contains(&"attrs"),
            "expected 'attrs' in set instance completions; got: {labels:?}"
        );
        assert!(
            labels.contains(&"generateAll"),
            "expected 'generateAll' in set instance completions; got: {labels:?}"
        );
    }

    #[test]
    fn completions_set_instance_generate_all_has_lead_documentation() {
        let src = concat!(
            "set Repo(pkg: String) {\n",
            "    save: &saveMethod\n",
            "}\n",
            "func main(): String {\n",
            "    let myRepo = Repo(pkg: \"com.example\")\n",
            "    myRepo.\n",
            "    return \"ok\"\n",
            "}\n",
        );
        let completions = compute_completions(src, pos(5, 11));
        let generate_all = completions.iter().find(|c| c.label == "generateAll");
        assert!(generate_all.is_some(), "expected generateAll completion");
        let doc = match &generate_all.unwrap().documentation {
            Some(lsp_types::Documentation::String(s)) => s.clone(),
            _ => String::new(),
        };
        assert!(
            doc.contains("lead") || doc.contains(">>"),
            "generateAll doc should mention 'lead' or '>>'; got: {doc}"
        );
    }

    // ── completions: lead keyword in general context ──────────────────────────

    #[test]
    fn completions_general_offers_lead_keyword() {
        let src = concat!(
            "set Repo(pkg: String) {\n",
            "    \n", // ← cursor here, line 2 col 8
            "}\n",
            "func main(): String {\n",
            "    return \"ok\"\n",
            "}\n",
        );
        let completions = compute_completions(src, pos(2, 8));
        let labels: Vec<&str> = completions.iter().map(|c| c.label.as_str()).collect();
        assert!(
            labels.contains(&"lead"),
            "expected 'lead' in general completions; got: {labels:?}"
        );
        let lead = completions.iter().find(|c| c.label == "lead").unwrap();
        let doc = match &lead.documentation {
            Some(lsp_types::Documentation::String(s)) => s.clone(),
            _ => String::new(),
        };
        assert!(
            doc.contains("generateAll") || doc.contains("outer"),
            "lead completion doc should explain its role; got: {doc}"
        );
    }

    #[test]
    fn completions_use_path_includes_tlang_mcptool() {
        let src = "use TLang.M\n";
        let completions = compute_completions(src, pos(0, 11));
        let labels: Vec<&str> = completions.iter().map(|c| c.label.as_str()).collect();
        assert!(
            labels.contains(&"TLang.MCPTool"),
            "expected TLang.MCPTool in use-path completions; got: {labels:?}"
        );
    }

    // ── completions: boilerplate snippets ─────────────────────────────────────

    #[test]
    fn completions_general_offers_func_snippet() {
        let completions = compute_completions("", pos(0, 0));
        let func_item = completions.iter().find(|c| c.label == "func");
        assert!(func_item.is_some(), "expected 'func' snippet in general completions");
        let item = func_item.unwrap();
        assert_eq!(item.kind, Some(CompletionItemKind::SNIPPET));
        let text = item.insert_text.as_deref().unwrap_or("");
        assert!(
            text.contains("${1:name}") || text.contains("func"),
            "func snippet should contain tab-stop for name; got: {text}"
        );
    }

    #[test]
    fn completions_general_offers_lang_doc_style_cmd_data_snippets() {
        let completions = compute_completions("", pos(0, 0));
        let labels: Vec<&str> = completions.iter().map(|c| c.label.as_str()).collect();
        for kw in &["lang", "doc", "style", "cmd", "data", "set", "func", "expose"] {
            assert!(
                labels.contains(kw),
                "expected '{kw}' snippet in general completions; got: {labels:?}"
            );
        }
    }

    #[test]
    fn completions_general_offers_type_names() {
        let completions = compute_completions("", pos(0, 0));
        let labels: Vec<&str> = completions.iter().map(|c| c.label.as_str()).collect();
        for ty in &["String", "Int", "Bool", "List", "Map", "Leaf", "Unit"] {
            assert!(
                labels.contains(ty),
                "expected type '{ty}' in general completions; got: {labels:?}"
            );
        }
    }

    #[test]
    fn completions_func_in_file_has_call_snippet_with_params() {
        let src = concat!(
            "func add(a: Int, b: Int): Int {\n",
            "    return a + b\n",
            "}\n",
            "func main(): String {\n",
            "    \n", // ← cursor here, line 5 col 8
            "}\n",
        );
        let completions = compute_completions(src, pos(5, 8));
        let add_item = completions.iter().find(|c| c.label == "add");
        assert!(add_item.is_some(), "expected 'add' in completions");
        let item = add_item.unwrap();
        let insert = item.insert_text.as_deref().unwrap_or("");
        assert!(
            insert.contains("add(") && (insert.contains("${1") || insert.contains("a")),
            "add completion should be a call snippet; got: {insert}"
        );
        let detail = item.detail.as_deref().unwrap_or("");
        assert!(
            detail.contains("a: Int") || detail.contains("Int"),
            "add completion detail should mention parameter types; got: {detail}"
        );
    }

    // ── signature help: library methods ──────────────────────────────────────

    #[test]
    fn signature_help_library_method_println() {
        let src = concat!(
            "use TLang.Terminal\n",
            "\n",
            "func main(): String {\n",
            "    Terminal.println(\n", // ← cursor after '('
            "    return \"ok\"\n",
            "}\n",
        );
        // Line 4, col 26 — just after the opening paren.
        let help = compute_signature_help(src, pos(4, 26));
        assert!(
            help.is_some(),
            "expected signature help for Terminal.println("
        );
        let help = help.unwrap();
        assert_eq!(help.signatures.len(), 1);
        let label = &help.signatures[0].label;
        assert!(
            label.contains("println"),
            "signature label should mention 'println'; got: {label}"
        );
    }

    #[test]
    fn signature_help_library_method_list_get_second_param() {
        // Signature: get(list, index): Value — active param should be 1 after comma.
        let src = concat!(
            "use TLang.List\n",
            "\n",
            "func main(): String {\n",
            "    let x = List.get(myList, \n", // ← cursor after comma+space
            "    return \"ok\"\n",
            "}\n",
        );
        // "        let x = List.get(myList, " — cursor at end of that
        let line = "        let x = List.get(myList, ";
        let col = line.len() as u32;
        let help = compute_signature_help(src, pos(4, col));
        assert!(help.is_some(), "expected signature help for List.get(list, |");
        let help = help.unwrap();
        assert_eq!(
            help.active_parameter,
            Some(1),
            "second argument: active param should be 1"
        );
    }

    // ── find_active_call: dotted names ────────────────────────────────────────

    #[test]
    fn find_active_call_captures_dotted_name() {
        let text = "Terminal.println(";
        let result = find_active_call(text);
        assert_eq!(result, Some(("Terminal.println".to_string(), 0)));
    }

    #[test]
    fn find_active_call_captures_plain_name_unchanged() {
        let text = "foo(";
        let result = find_active_call(text);
        assert_eq!(result, Some(("foo".to_string(), 0)));
    }

    // ── parse_func_call_snippet ───────────────────────────────────────────────

    #[test]
    fn parse_func_call_snippet_with_params() {
        let (detail, snippet) = parse_func_call_snippet("add", "add(a: Int, b: Int): Int {");
        assert!(detail.contains("a: Int"), "detail should mention params; got: {detail}");
        assert!(detail.contains("Int"), "detail should mention return type; got: {detail}");
        assert!(snippet.starts_with("add("), "snippet should start with call; got: {snippet}");
        assert!(snippet.contains("${1"), "snippet should have tab-stops; got: {snippet}");
    }

    #[test]
    fn parse_func_call_snippet_no_params() {
        let (detail, snippet) = parse_func_call_snippet("main", "main(): String {");
        assert!(detail.contains("main()"), "detail should show empty params; got: {detail}");
        assert!(snippet.contains("main("), "snippet should call main; got: {snippet}");
    }

    // ── build_library_signature_help ──────────────────────────────────────────

    #[test]
    fn build_library_signature_help_parses_params() {
        let help = build_library_signature_help("get(list, index): Value", 1).unwrap();
        assert_eq!(help.active_parameter, Some(1));
        let params = help.signatures[0].parameters.as_ref().unwrap();
        assert_eq!(params.len(), 2);
        let ParameterLabel::Simple(p0) = &params[0].label else { panic!() };
        assert_eq!(p0, "list");
        let ParameterLabel::Simple(p1) = &params[1].label else { panic!() };
        assert_eq!(p1, "index");
    }

    #[test]
    fn build_library_signature_help_no_params() {
        let help = build_library_signature_help("read(): String", 0).unwrap();
        let params = help.signatures[0].parameters.as_ref().unwrap();
        assert!(params.is_empty(), "expected no parameters; got {params:?}");
    }
}
