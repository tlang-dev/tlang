// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! MCP (Model Context Protocol) server for TLang.
//!
//! Implements JSON-RPC 2.0 over stdio following the MCP 2024-11-05 spec.
//! Run with `tlang mcp-server`.
//!
//! Exposed capabilities:
//!   • tools   — mirrors the TLang CLI (init, compile, run, both, package, clean)
//!   • resources — `tlang://guide` serves the embedded onboarding guide
//!   • prompts — a ready-made system prompt for AI assistants working with TLang

use serde_json::{Value, json};
use std::{
    fs,
    io::{BufRead, Write},
    path::{Path, PathBuf},
    sync::{Mutex, OnceLock},
};

use tlang::loader::load_program_with_manifest;
use tlang::manifest::{resolve_main, try_load_manifest};
use tlang::runtime::{
    Value as RuntimeValue, call_in_file, compile_from_domain_model, run_exposed_function,
};

// ─── embedded guide ──────────────────────────────────────────────────────────

const TLANG_INDEX: &str = include_str!("../prompts/tlang-index.md");
const TLANG_PROJECT: &str = include_str!("../prompts/tlang-project.md");
const TLANG_LANG: &str = include_str!("../prompts/tlang-lang.md");
const TLANG_TEMPLATES: &str = include_str!("../prompts/tlang-templates.md");
const TLANG_HELPERS: &str = include_str!("../prompts/tlang-helpers.md");
const TLANG_BUILTINS: &str = include_str!("../prompts/tlang-builtins.md");
const TLANG_PATTERNS: &str = include_str!("../prompts/tlang-patterns.md");
const TLANG_MISTAKES: &str = include_str!("../prompts/tlang-mistakes.md");
const MAX_LIST_FILES_RESULTS: usize = 200;
const MAX_READ_FILE_LINES: usize = 200;
const MAX_READ_FILE_BYTES: usize = 16_000;
const MAX_SEARCH_RESULTS: usize = 50;
const MAX_SEARCH_LINE_CHARS: usize = 200;
const MAX_PATCH_EDITS: usize = 20;

#[derive(Debug, Clone)]
struct DynamicTool {
    name: String,
    description: String,
    impl_function: String,
}

#[derive(Debug, Clone)]
struct ActiveProjectContext {
    project_root: PathBuf,
    main_file: PathBuf,
    dynamic_tools: Vec<DynamicTool>,
}

#[derive(Debug, Default)]
struct ServerState {
    active_project: Option<ActiveProjectContext>,
    pending_notifications: Vec<Value>,
}

fn server_state() -> &'static Mutex<ServerState> {
    static STATE: OnceLock<Mutex<ServerState>> = OnceLock::new();
    STATE.get_or_init(|| Mutex::new(ServerState::default()))
}

fn queue_tools_changed_notification(state: &mut ServerState) {
    state.pending_notifications.push(json!({
        "jsonrpc": "2.0",
        "method": "notifications/tools/list_changed",
        "params": {}
    }));
}

// ─── entry point ─────────────────────────────────────────────────────────────

/// Start the MCP stdio server.  Blocks until stdin is closed.
pub fn run_mcp_server() -> Result<(), String> {
    let stdin = std::io::stdin();
    let stdout = std::io::stdout();
    let mut out = stdout.lock();

    for line in stdin.lock().lines() {
        let line = line.map_err(|e| format!("stdin read error: {e}"))?;
        let trimmed = line.trim();
        if trimmed.is_empty() {
            continue;
        }

        match serde_json::from_str::<Value>(trimmed) {
            Ok(msg) => {
                if let Some(response) = handle_message(&msg) {
                    let serialized =
                        serde_json::to_string(&response).expect("response serialization failed");
                    writeln!(out, "{serialized}")
                        .map_err(|e| format!("stdout write error: {e}"))?;
                    for notification in drain_pending_notifications() {
                        let serialized = serde_json::to_string(&notification)
                            .expect("notification serialization failed");
                        writeln!(out, "{serialized}")
                            .map_err(|e| format!("stdout write error: {e}"))?;
                    }
                    out.flush()
                        .map_err(|e| format!("stdout flush error: {e}"))?;
                }
            }
            Err(e) => {
                // Respond with a parse error so the client knows something went wrong.
                let err_response =
                    json_rpc_error(Value::Null, -32700, &format!("Parse error: {e}"), None);
                let serialized = serde_json::to_string(&err_response).unwrap();
                writeln!(out, "{serialized}").ok();
                out.flush().ok();
            }
        }
    }

    Ok(())
}

fn drain_pending_notifications() -> Vec<Value> {
    let mut guard = server_state()
        .lock()
        .expect("server state lock should not be poisoned");
    std::mem::take(&mut guard.pending_notifications)
}

// ─── dispatcher ──────────────────────────────────────────────────────────────

/// Route an incoming JSON-RPC message.  Returns `None` for notifications
/// (which must not be replied to).
fn handle_message(msg: &Value) -> Option<Value> {
    let id = msg.get("id").cloned().unwrap_or(Value::Null);
    let method = msg.get("method").and_then(Value::as_str).unwrap_or("");
    let params = msg.get("params").cloned().unwrap_or(Value::Null);

    // Notifications have no `id` — do not reply.
    msg.get("id")?;

    let result = match method {
        "initialize" => handle_initialize(&params),
        "ping" => Ok(json!({})),
        "tools/list" => handle_tools_list(),
        "tools/call" => handle_tools_call(&params),
        "resources/list" => handle_resources_list(),
        "resources/read" => handle_resources_read(&params),
        "prompts/list" => handle_prompts_list(),
        "prompts/get" => handle_prompts_get(&params),
        _ => Err(json_rpc_error_payload(
            -32601,
            &format!("Method not found: {method}"),
            None,
        )),
    };

    Some(match result {
        Ok(value) => json_rpc_success(id, value),
        Err(err_payload) => json!({
            "jsonrpc": "2.0",
            "id": id,
            "error": err_payload,
        }),
    })
}

// ─── JSON-RPC helpers ─────────────────────────────────────────────────────────

fn json_rpc_success(id: Value, result: Value) -> Value {
    json!({
        "jsonrpc": "2.0",
        "id": id,
        "result": result,
    })
}

fn json_rpc_error(id: Value, code: i64, message: &str, data: Option<Value>) -> Value {
    json!({
        "jsonrpc": "2.0",
        "id": id,
        "error": json_rpc_error_payload(code, message, data),
    })
}

fn json_rpc_error_payload(code: i64, message: &str, data: Option<Value>) -> Value {
    match data {
        Some(d) => json!({ "code": code, "message": message, "data": d }),
        None => json!({ "code": code, "message": message }),
    }
}

// ─── initialize ──────────────────────────────────────────────────────────────

fn handle_initialize(_params: &Value) -> Result<Value, Value> {
    Ok(json!({
        "protocolVersion": "2024-11-05",
        "capabilities": {
            "tools": { "listChanged": true },
            "resources": {},
            "prompts": {}
        },
        "serverInfo": {
            "name": "tlang-mcp",
            "version": env!("CARGO_PKG_VERSION")
        }
    }))
}

// ─── tools ───────────────────────────────────────────────────────────────────

fn tool_list() -> Value {
    json!([
        {
            "name": "tlang_init",
            "description": "Scaffold a new TLang project (manifest.yml, Main.tlang, output/, prompts/).",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Directory to create the project in. Defaults to current directory."
                    }
                }
            }
        },
        {
            "name": "tlang_compile",
            "description": "Compile a project to bytecode. Pass live=true to watch for changes.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Project directory or .tlang file."
                    },
                    "live": {
                        "type": "boolean",
                        "description": "Watch for changes and recompile automatically."
                    }
                }
            }
        },
        {
            "name": "tlang_run",
            "description": "Run a compiled project (bytecode must exist in target/tlang/).",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Project directory or .tlang file."
                    }
                }
            }
        },
        {
            "name": "tlang_both",
            "description": "Compile then run in one step. Pass in_memory=true to skip bytecode files.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Project directory or .tlang file."
                    },
                    "in_memory": {
                        "type": "boolean",
                        "description": "Compile in memory without writing bytecode files."
                    }
                }
            }
        },
        {
            "name": "tlang_package",
            "description": "Package a generator into a .tbag archive and publish to local tbox.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Generator project directory."
                    }
                }
            }
        },
        {
            "name": "tlang_clean",
            "description": "Delete compiled bytecode (target/tlang/).",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Project directory."
                    }
                }
            }
        },
        {
            "name": "set_project_context",
            "description": "Set the active project path for dynamic tool discovery.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "projectPath": {
                        "type": "string",
                        "description": "Path to the active TLang project directory."
                    }
                },
                "required": ["projectPath"]
            }
        },
        {
            "name": "tlang_index",
            "description": "Return the routing index + cheat-sheets. Load first on every TLang task.",
            "inputSchema": {
                "type": "object",
                "properties": {}
            }
        },
        {
            "name": "tlang_topic",
            "description": "Return a focused topic file. Topics: lang|project|templates|helpers|builtins|patterns|mistakes.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "name": {
                        "type": "string",
                        "description": "lang | project | templates | helpers | builtins | patterns | mistakes",
                        "enum": ["lang", "project", "templates", "helpers", "builtins", "patterns", "mistakes"]
                    }
                },
                "required": ["name"]
            }
        },
        {
            "name": "list_files",
            "description": "List files/dirs under a path. Caps large listings.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Repo-relative directory path. Defaults to root."
                    },
                    "recursive": {
                        "type": "boolean",
                        "description": "Walk subdirectories recursively."
                    },
                    "limit": {
                        "type": "integer",
                        "description": "Max entries to return (default 100, max 200)."
                    }
                }
            }
        },
        {
            "name": "read_file",
            "description": "Read a file, optionally by line range.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Repo-relative path to the file."
                    },
                    "startLine": {
                        "type": "integer",
                        "description": "1-based start line."
                    },
                    "endLine": {
                        "type": "integer",
                        "description": "1-based inclusive end line."
                    }
                },
                "required": ["path"]
            }
        },
        {
            "name": "search_code",
            "description": "Search files for a string. Returns file:line matches.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "query": {
                        "type": "string",
                        "description": "String to search for."
                    },
                    "path": {
                        "type": "string",
                        "description": "Optional file or directory scope."
                    },
                    "limit": {
                        "type": "integer",
                        "description": "Max matches (default 20, max 50)."
                    }
                },
                "required": ["query"]
            }
        },
        {
            "name": "edit_file",
            "description": "Targeted find/replace edit. Rejects ambiguous matches unless replaceAll=true.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Repo-relative path to the file."
                    },
                    "find": {
                        "type": "string",
                        "description": "Exact text to find."
                    },
                    "replace": {
                        "type": "string",
                        "description": "Replacement text."
                    },
                    "replaceAll": {
                        "type": "boolean",
                        "description": "Replace every match instead of requiring uniqueness."
                    },
                    "expectedMatches": {
                        "type": "integer",
                        "description": "Safety check: file must have exactly this many matches."
                    }
                },
                "required": ["path", "find", "replace"]
            }
        },
        {
            "name": "apply_patch",
            "description": "Apply a sequence of replace/delete/insert edits to a file.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Repo-relative path to the file."
                    },
                    "edits": {
                        "type": "array",
                        "description": "Sequential edit operations (replace/delete/insert_before/insert_after)."
                    }
                },
                "required": ["path", "edits"]
            }
        },
        {
            "name": "create_file",
            "description": "Create a new file. Fails if already exists unless overwrite=true.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Repo-relative path. Parent dirs created automatically."
                    },
                    "content": {
                        "type": "string",
                        "description": "Text content to write."
                    },
                    "overwrite": {
                        "type": "boolean",
                        "description": "Overwrite if the file already exists. Defaults to false."
                    }
                },
                "required": ["path", "content"]
            }
        },
        {
            "name": "move_file",
            "description": "Move or rename a file. References in other files are NOT updated automatically.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "source": {
                        "type": "string",
                        "description": "Repo-relative source path."
                    },
                    "destination": {
                        "type": "string",
                        "description": "Repo-relative destination path. Parent dirs created automatically."
                    }
                },
                "required": ["source", "destination"]
            }
        },
        {
            "name": "rename_symbol",
            "description": "Rename an identifier across files (text, not AST). Use dry_run to preview.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "symbol": {
                        "type": "string",
                        "description": "Symbol name to find (word-boundary matched)."
                    },
                    "new_name": {
                        "type": "string",
                        "description": "Replacement name."
                    },
                    "path": {
                        "type": "string",
                        "description": "Optional file or directory scope."
                    },
                    "dry_run": {
                        "type": "boolean",
                        "description": "Report matches without modifying. Defaults to false."
                    }
                },
                "required": ["symbol", "new_name"]
            }
        },
        {
            "name": "create_module",
            "description": "Scaffold a .tlang file with boilerplate (lang block, helper, model).",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "name": {
                        "type": "string",
                        "description": "PascalCase module name; creates <Name>.tlang in repo root."
                    },
                    "path": {
                        "type": "string",
                        "description": "Explicit repo-relative path ending in .tlang. Alternative to name."
                    },
                    "lang": {
                        "type": "string",
                        "description": "Language tag for a starter lang [] block (e.g. kotlin)."
                    },
                    "template_name": {
                        "type": "string",
                        "description": "Name for the starter template block."
                    },
                    "with_model": {
                        "type": "boolean",
                        "description": "Include an empty model {} block. Defaults to true."
                    },
                    "with_helper": {
                        "type": "boolean",
                        "description": "Include a helper {} block with a stub main. Defaults to true."
                    }
                }
            }
        },
        {
            "name": "add_leaf_to_template",
            "description": "Add a leaf attribute to a named model set entity.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Repo-relative path to the .tlang file."
                    },
                    "set_name": {
                        "type": "string",
                        "description": "Name of the set entity to extend."
                    },
                    "leaf_name": {
                        "type": "string",
                        "description": "Attribute key for the new leaf."
                    },
                    "leaf_ref": {
                        "type": "string",
                        "description": "Reference name written as &leaf_ref. Defaults to leaf_name."
                    }
                },
                "required": ["path", "set_name", "leaf_name"]
            }
        },
        {
            "name": "move_method",
            "description": "Move a func between TLang helper blocks. Call references are NOT updated.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "source_path": {
                        "type": "string",
                        "description": "Repo-relative path of the source .tlang file."
                    },
                    "dest_path": {
                        "type": "string",
                        "description": "Repo-relative path of the destination .tlang file."
                    },
                    "func_name": {
                        "type": "string",
                        "description": "Name of the func to move."
                    }
                },
                "required": ["source_path", "dest_path", "func_name"]
            }
        }
    ])
}

fn handle_tools_list() -> Result<Value, Value> {
    let mut tools = tool_list().as_array().cloned().unwrap_or_default();
    tools.extend(dynamic_tool_descriptors());
    Ok(json!({ "tools": tools }))
}

fn handle_tools_call(params: &Value) -> Result<Value, Value> {
    let name = params
        .get("name")
        .and_then(Value::as_str)
        .ok_or_else(|| json_rpc_error_payload(-32602, "Missing required parameter: name", None))?;

    let args = params.get("arguments").cloned().unwrap_or(json!({}));

    match name {
        "tlang_init" => call_cli_tool("init", &args, &[]),
        "tlang_compile" => {
            let mut flags = vec![];
            if args.get("live").and_then(Value::as_bool).unwrap_or(false) {
                flags.push("--live");
            }
            call_cli_tool("compile", &args, &flags)
        }
        "tlang_run" => call_cli_tool("run", &args, &[]),
        "tlang_both" => {
            let mut flags = vec![];
            if args
                .get("in_memory")
                .and_then(Value::as_bool)
                .unwrap_or(false)
            {
                flags.push("--in-memory");
            }
            call_cli_tool("both", &args, &flags)
        }
        "tlang_package" => call_cli_tool("package", &args, &[]),
        "tlang_clean" => call_cli_tool("clean", &args, &[]),
        "set_project_context" => handle_set_project_context(&args),
        "tlang_index" => Ok(tool_content_text(TLANG_INDEX)),
        "tlang_topic" => {
            let topic = args.get("name").and_then(Value::as_str).ok_or_else(|| {
                json_rpc_error_payload(-32602, "Missing required parameter: name", None)
            })?;
            match topic {
                "lang" => Ok(tool_content_text(TLANG_LANG)),
                "project" => Ok(tool_content_text(TLANG_PROJECT)),
                "templates" => Ok(tool_content_text(TLANG_TEMPLATES)),
                "helpers" => Ok(tool_content_text(TLANG_HELPERS)),
                "builtins" => Ok(tool_content_text(TLANG_BUILTINS)),
                "patterns" => Ok(tool_content_text(TLANG_PATTERNS)),
                "mistakes" => Ok(tool_content_text(TLANG_MISTAKES)),
                other => Err(json_rpc_error_payload(
                    -32602,
                    &format!(
                        "Unknown topic '{other}'. Valid topics: lang, project, templates, helpers, builtins, patterns, mistakes"
                    ),
                    None,
                )),
            }
        }
        "list_files" => handle_list_files(&args),
        "read_file" => handle_read_file(&args),
        "search_code" => handle_search_code(&args),
        "edit_file" => handle_edit_file(&args),
        "apply_patch" => handle_apply_patch(&args),
        "create_file" => handle_create_file(&args),
        "move_file" => handle_move_file(&args),
        "rename_symbol" => handle_rename_symbol(&args),
        "create_module" => handle_create_module(&args),
        "add_leaf_to_template" => handle_add_leaf_to_template(&args),
        "move_method" => handle_move_method(&args),
        _ => handle_dynamic_tool_call(name, &args).map_err(|err| {
            json_rpc_error_payload(-32602, &format!("Unknown tool: {name}"), Some(json!(err)))
        }),
    }
}

/// Build a `tools/call` success response with a single text content item.
fn tool_content_text(text: &str) -> Value {
    json!({
        "content": [
            { "type": "text", "text": text }
        ]
    })
}

/// Build a `tools/call` error response (tool-level error, not protocol error).
fn tool_content_error(text: &str) -> Value {
    json!({
        "isError": true,
        "content": [
            { "type": "text", "text": text }
        ]
    })
}

fn dynamic_tool_descriptors() -> Vec<Value> {
    let guard = server_state()
        .lock()
        .expect("server state lock should not be poisoned");
    guard
        .active_project
        .as_ref()
        .map(|ctx| {
            ctx.dynamic_tools
                .iter()
                .map(|tool| {
                    json!({
                        "name": tool.name,
                        "description": tool.description,
                        "inputSchema": {
                            "type": "object",
                            "additionalProperties": true
                        }
                    })
                })
                .collect()
        })
        .unwrap_or_default()
}

fn handle_set_project_context(args: &Value) -> Result<Value, Value> {
    let project_path = args
        .get("projectPath")
        .and_then(Value::as_str)
        .ok_or_else(|| {
            json_rpc_error_payload(-32602, "Missing required parameter: projectPath", None)
        })?;

    let new_context = match build_active_project_context(project_path) {
        Ok(ctx) => ctx,
        Err(err) => return Ok(tool_content_error(&err)),
    };

    let mut state = server_state()
        .lock()
        .expect("server state lock should not be poisoned");
    let changed = match &state.active_project {
        Some(previous) => {
            previous.project_root != new_context.project_root
                || dynamic_tool_names(&previous.dynamic_tools)
                    != dynamic_tool_names(&new_context.dynamic_tools)
        }
        None => true,
    };
    state.active_project = Some(new_context.clone());
    if changed {
        queue_tools_changed_notification(&mut state);
    }

    let loaded = if new_context.dynamic_tools.is_empty() {
        "no dynamic tools discovered".to_string()
    } else {
        format!(
            "discovered tools: {}",
            new_context
                .dynamic_tools
                .iter()
                .map(|tool| tool.name.as_str())
                .collect::<Vec<_>>()
                .join(", ")
        )
    };
    Ok(tool_content_text(&format!(
        "Active project context set to {}\n{}",
        new_context.project_root.display(),
        loaded
    )))
}

fn dynamic_tool_names(tools: &[DynamicTool]) -> Vec<String> {
    let mut names: Vec<String> = tools.iter().map(|tool| tool.name.clone()).collect();
    names.sort();
    names
}

fn handle_dynamic_tool_call(name: &str, args: &Value) -> Result<Value, String> {
    let (main_file, impl_function) = {
        let guard = server_state()
            .lock()
            .expect("server state lock should not be poisoned");
        let Some(context) = &guard.active_project else {
            return Err(
                "Dynamic tools are unavailable. Call set_project_context(projectPath) first."
                    .to_string(),
            );
        };
        let Some(tool) = context.dynamic_tools.iter().find(|tool| tool.name == name) else {
            return Err("tool not found in active project context".to_string());
        };
        (context.main_file.clone(), tool.impl_function.clone())
    };

    let args_json = serde_json::to_string(args)
        .map_err(|err| format!("failed to serialize tool arguments: {err}"))?;
    let result = call_in_file(
        &main_file,
        &impl_function,
        vec![RuntimeValue::String(args_json)],
    )
    .map_err(|err| format!("dynamic tool execution failed: {err}"))?;
    Ok(tool_content_text(&runtime_value_to_text(&result)))
}

fn runtime_value_to_text(value: &RuntimeValue) -> String {
    match value {
        RuntimeValue::String(s) => s.clone(),
        RuntimeValue::Int(v) => v.to_string(),
        RuntimeValue::Float(v) => v.to_string(),
        RuntimeValue::Bool(v) => v.to_string(),
        RuntimeValue::Unit => String::new(),
        other => format!("{other:?}"),
    }
}

fn build_active_project_context(project_path: &str) -> Result<ActiveProjectContext, String> {
    let project_root = canonicalize_project_path(project_path)?;
    let manifest = try_load_manifest(&project_root)
        .map_err(|err| format!("failed to load manifest from project path: {err}"))?;
    let main_file = resolve_main(&project_root, manifest.as_ref());
    if !main_file.exists() {
        return Err(format!(
            "main entry file not found: {}",
            main_file.display()
        ));
    }
    let domain_model = load_program_with_manifest(&main_file, manifest.as_ref())
        .map_err(|err| format!("failed to load project program: {err}"))?;
    let compiled = compile_from_domain_model(&domain_model)
        .map_err(|err| format!("failed to compile project: {err}"))?;
    let dynamic_tools = discover_dynamic_tools(&compiled);
    Ok(ActiveProjectContext {
        project_root,
        main_file,
        dynamic_tools,
    })
}

fn canonicalize_project_path(project_path: &str) -> Result<PathBuf, String> {
    if project_path.trim().is_empty() {
        return Err("projectPath must not be empty".to_string());
    }
    let raw = Path::new(project_path);
    let candidate = if raw.is_absolute() {
        raw.to_path_buf()
    } else {
        std::env::current_dir()
            .map_err(|err| format!("failed to read current directory: {err}"))?
            .join(raw)
    };
    if !candidate.exists() {
        return Err(format!("project path not found: {}", candidate.display()));
    }
    if !candidate.is_dir() {
        return Err(format!(
            "project path must be a directory: {}",
            candidate.display()
        ));
    }
    candidate.canonicalize().map_err(|err| {
        format!(
            "failed to resolve project path {}: {err}",
            candidate.display()
        )
    })
}

fn discover_dynamic_tools(compiled: &tlang::runtime::CompiledProgram) -> Vec<DynamicTool> {
    let callable_params = mcp_tool_callable_params(compiled);
    if callable_params.is_empty() {
        return Vec::new();
    }

    let exposed = compiled.exposed_names();
    let mut names_seen = std::collections::HashSet::new();
    let mut tools = Vec::new();
    for info in compiled.function_infos() {
        if info.source_file.is_some() {
            continue;
        }
        if !exposed.contains(&info.name) {
            continue;
        }
        if !info.params.is_empty() {
            continue;
        }

        let run = match run_exposed_function(compiled, &info.name, Vec::new(), false) {
            Ok(result) => result,
            Err(_) => continue,
        };
        let RuntimeValue::SetInstance(inst) = run.return_value else {
            continue;
        };
        if inst.entity_name != "MCPTool" {
            continue;
        }
        let Some(base_name) = instance_string_impl(&inst, "name") else {
            continue;
        };
        if base_name.trim().is_empty() {
            continue;
        }
        let description = instance_string_impl(&inst, "description").unwrap_or_else(|| {
            format!(
                "Dynamic MCP tool from exposed function provider '{}'",
                info.name
            )
        });

        for param in &callable_params {
            let Some(RuntimeValue::String(func_name)) = inst.impls.get(param) else {
                continue;
            };
            if func_name.trim().is_empty() {
                continue;
            }

            let tool_name = if callable_params.len() == 1 {
                base_name.clone()
            } else {
                format!("{}_{}", base_name, param)
            };
            if !names_seen.insert(tool_name.clone()) {
                continue;
            }
            tools.push(DynamicTool {
                name: tool_name,
                description: description.clone(),
                impl_function: func_name.clone(),
            });
        }
    }
    tools
}

fn mcp_tool_callable_params(compiled: &tlang::runtime::CompiledProgram) -> Vec<String> {
    let mut params = Vec::new();
    for node in &compiled.model_tree().nodes {
        let tlang::model_tree::ModelNodeTree::SetEntity(entity) = node else {
            continue;
        };
        if entity.name != "MCPTool" {
            continue;
        }
        for param in &entity.params {
            if matches!(
                param.value,
                tlang::model_tree::ModelValueTypeTree::FuncDef { .. }
            ) && let Some(name) = &param.attr
            {
                params.push(name.clone());
            }
        }
    }
    params.sort();
    params.dedup();
    params
}

fn instance_string_impl(inst: &tlang::runtime::SetInstanceObject, name: &str) -> Option<String> {
    match inst.impls.get(name) {
        Some(RuntimeValue::String(value)) => Some(value.clone()),
        _ => None,
    }
}

/// Locate the `tlang` binary (prefer the current executable so the MCP server
/// and the CLI are always the same binary / version).
fn tlang_exe() -> std::path::PathBuf {
    std::env::current_exe().unwrap_or_else(|_| std::path::PathBuf::from("tlang"))
}

/// Spawn the `tlang` CLI as a subprocess, capture stdout+stderr, and return
/// a `tools/call` response value.
///
/// `subcommand` — the CLI sub-command (e.g. `"compile"`, `"init"`)
/// `args`       — the parsed JSON arguments object from the MCP call
/// `extra_flags`— additional CLI flags to prepend (e.g. `["--in-memory"]`)
fn call_cli_tool(subcommand: &str, args: &Value, extra_flags: &[&str]) -> Result<Value, Value> {
    let exe = tlang_exe();
    let mut cmd = std::process::Command::new(&exe);
    cmd.arg(subcommand);

    // Append extra flags before the path argument so the CLI parser is happy.
    for flag in extra_flags {
        cmd.arg(flag);
    }

    // If a `path` argument was supplied, resolve it to an absolute path
    // (relative paths are anchored to the MCP server's CWD, which is
    // unpredictable when launched from a Flatpak host like Zed).
    // We then:
    //   1. Pass the absolute path via `-p <abs>` so the CLI resolves it
    //      correctly regardless of where the subprocess starts.
    //   2. For commands that operate on an existing project (everything except
    //      `init`), set the subprocess CWD to that directory so that any
    //      File.write("<relative>") call inside a TLang program is anchored
    //      to the project root, not to some system directory.
    //      (`init` is excluded because the directory may not exist yet.)
    if let Some(path_str) = args.get("path").and_then(Value::as_str) {
        let raw = std::path::Path::new(path_str);
        let abs = if raw.is_absolute() {
            raw.to_path_buf()
        } else {
            std::env::current_dir()
                .unwrap_or_else(|_| std::path::PathBuf::from("/"))
                .join(raw)
        };
        cmd.arg("-p").arg(&abs);

        // For project-operating commands, pin the subprocess CWD so that
        // relative File.write paths always resolve against the project root.
        // Skip this for `init` (the directory may not exist yet).
        if subcommand != "init" {
            if abs.is_dir() {
                cmd.current_dir(&abs);
            } else {
                // Give a clear error immediately rather than a confusing
                // "manifest not found" from deep inside the CLI.
                return Ok(tool_content_error(&format!(
                    "Not a directory: {}. Run tlang_init to scaffold first.",
                    abs.display()
                )));
            }
        }
    }

    let output = cmd.output().map_err(|e| {
        json_rpc_error_payload(
            -32000,
            &format!("Failed to spawn tlang subprocess: {e}"),
            None,
        )
    })?;

    let stdout = String::from_utf8_lossy(&output.stdout);
    let stderr = String::from_utf8_lossy(&output.stderr);

    let mut combined = String::new();
    if !stdout.is_empty() {
        combined.push_str(stdout.trim_end());
    }
    if !stderr.is_empty() {
        if !combined.is_empty() {
            combined.push('\n');
        }
        combined.push_str(stderr.trim_end());
    }

    if output.status.success() {
        Ok(tool_content_text(&combined))
    } else {
        let exit_code = output
            .status
            .code()
            .map(|c| format!(" (exit {})", c))
            .unwrap_or_default();
        let message = format!("tlang {subcommand} failed{exit_code}:\n{combined}");
        Ok(tool_content_error(&message))
    }
}
fn handle_list_files(args: &Value) -> Result<Value, Value> {
    let resolved = match resolve_repo_path(args.get("path").and_then(Value::as_str)) {
        Ok(path) => path,
        Err(message) => return Ok(tool_content_error(&message)),
    };

    if !resolved.is_dir() {
        return Ok(tool_content_error(&format!(
            "Path is not a directory: {}",
            display_repo_path(&resolved)
        )));
    }

    let recursive = args
        .get("recursive")
        .and_then(Value::as_bool)
        .unwrap_or(false);
    let limit = match read_limit_arg(args, "limit", 100, MAX_LIST_FILES_RESULTS) {
        Ok(limit) => limit,
        Err(message) => return Ok(tool_content_error(&message)),
    };

    let mut stack = vec![resolved.clone()];
    let mut entries = Vec::new();
    let mut truncated = false;

    while let Some(dir) = stack.pop() {
        let mut children = match read_dir_sorted(&dir) {
            Ok(children) => children,
            Err(message) => return Ok(tool_content_error(&message)),
        };

        let mut child_dirs = Vec::new();
        for entry in children.drain(..) {
            let path = entry.path();
            let file_type = match entry.file_type() {
                Ok(file_type) => file_type,
                Err(err) => {
                    return Ok(tool_content_error(&format!(
                        "Failed to read file type for {}: {err}",
                        path.display()
                    )));
                }
            };

            let mut display = display_repo_path(&path);
            if file_type.is_dir() {
                display.push('/');
                child_dirs.push(path.clone());
            }
            entries.push(display);

            if entries.len() >= limit {
                truncated = true;
                break;
            }
        }

        if truncated || !recursive {
            break;
        }

        child_dirs.reverse();
        for child in child_dirs {
            if should_skip_recursive_dir(&child) {
                continue;
            }
            stack.push(child);
        }
    }

    if entries.is_empty() {
        return Ok(tool_content_text("(empty directory)"));
    }

    let mut text = entries.join("\n");
    if truncated {
        text.push_str(&format!(
            "\n... truncated after {limit} entr{}",
            if limit == 1 { "y" } else { "ies" }
        ));
    }
    Ok(tool_content_text(&text))
}

fn handle_read_file(args: &Value) -> Result<Value, Value> {
    let path = match required_string_arg(args, "path") {
        Ok(path) => path,
        Err(message) => return Ok(tool_content_error(&message)),
    };
    let resolved = match resolve_repo_path(Some(path)) {
        Ok(path) => path,
        Err(message) => return Ok(tool_content_error(&message)),
    };

    if !resolved.is_file() {
        return Ok(tool_content_error(&format!(
            "Path is not a file: {}",
            display_repo_path(&resolved)
        )));
    }

    let start_line = match read_positive_usize_arg(args, "startLine") {
        Ok(Some(value)) => value,
        Ok(None) => 1,
        Err(message) => return Ok(tool_content_error(&message)),
    };
    let end_line = match read_positive_usize_arg(args, "endLine") {
        Ok(value) => value,
        Err(message) => return Ok(tool_content_error(&message)),
    };

    if let Some(end_line) = end_line {
        if end_line < start_line {
            return Ok(tool_content_error(
                "endLine must be greater than or equal to startLine",
            ));
        }
    }

    let contents = match read_utf8_file(&resolved) {
        Ok(contents) => contents,
        Err(message) => return Ok(tool_content_error(&message)),
    };

    if contents.is_empty() {
        return Ok(tool_content_text(&format!(
            "{}: empty file",
            display_repo_path(&resolved)
        )));
    }

    let lines: Vec<&str> = contents.lines().collect();
    if start_line > lines.len() {
        return Ok(tool_content_error(&format!(
            "startLine {} is beyond the end of {} ({} lines)",
            start_line,
            display_repo_path(&resolved),
            lines.len()
        )));
    }

    let requested_end = end_line.unwrap_or(lines.len()).min(lines.len());
    let requested_slice = &lines[start_line - 1..requested_end];

    let mut rendered = Vec::new();
    let mut used_bytes = 0usize;
    let mut last_line = start_line.saturating_sub(1);
    let mut truncated = false;

    for (index, line) in requested_slice.iter().enumerate() {
        if rendered.len() >= MAX_READ_FILE_LINES {
            truncated = true;
            break;
        }

        let line_number = start_line + index;
        let rendered_line = format!("{line_number}: {line}");
        let additional_bytes = rendered_line.len() + 1;
        if !rendered.is_empty() && used_bytes + additional_bytes > MAX_READ_FILE_BYTES {
            truncated = true;
            break;
        }

        used_bytes += additional_bytes;
        last_line = line_number;
        rendered.push(rendered_line);
    }

    if rendered.is_empty() {
        return Ok(tool_content_error(
            "Requested range is too large to return compactly; narrow the line range",
        ));
    }

    let mut text = format!(
        "{}:{}-{}\n{}",
        display_repo_path(&resolved),
        start_line,
        last_line,
        rendered.join("\n")
    );
    if truncated {
        text.push_str(&format!(
            "\n... truncated after {} line(s); narrow the requested range",
            rendered.len()
        ));
    }
    Ok(tool_content_text(&text))
}

fn handle_search_code(args: &Value) -> Result<Value, Value> {
    let query = match required_string_arg(args, "query") {
        Ok(query) if !query.is_empty() => query,
        Ok(_) => return Ok(tool_content_error("query must not be empty")),
        Err(message) => return Ok(tool_content_error(&message)),
    };

    let scope = match resolve_repo_path(args.get("path").and_then(Value::as_str)) {
        Ok(path) => path,
        Err(message) => return Ok(tool_content_error(&message)),
    };
    let limit = match read_limit_arg(args, "limit", 20, MAX_SEARCH_RESULTS) {
        Ok(limit) => limit,
        Err(message) => return Ok(tool_content_error(&message)),
    };

    let mut files = Vec::new();
    if scope.is_dir() {
        if let Err(message) = collect_search_files(&scope, &mut files) {
            return Ok(tool_content_error(&message));
        }
    } else if scope.is_file() {
        files.push(scope);
    } else {
        return Ok(tool_content_error(&format!(
            "Path is neither a file nor a directory: {}",
            display_repo_path(&scope)
        )));
    }

    let mut matches = Vec::new();
    let mut truncated = false;
    for file in files {
        let contents = match read_utf8_file_if_possible(&file) {
            Ok(Some(contents)) => contents,
            Ok(None) => continue,
            Err(message) => return Ok(tool_content_error(&message)),
        };

        for (index, line) in contents.lines().enumerate() {
            if line.contains(query) {
                matches.push(format!(
                    "{}:{}:{}",
                    display_repo_path(&file),
                    index + 1,
                    truncate_line(line, MAX_SEARCH_LINE_CHARS)
                ));
                if matches.len() >= limit {
                    truncated = true;
                    break;
                }
            }
        }

        if truncated {
            break;
        }
    }

    if matches.is_empty() {
        return Ok(tool_content_text("(no matches)"));
    }

    let mut text = matches.join("\n");
    if truncated {
        text.push_str(&format!(
            "\n... truncated after {limit} match{}",
            if limit == 1 { "" } else { "es" }
        ));
    }
    Ok(tool_content_text(&text))
}

fn handle_edit_file(args: &Value) -> Result<Value, Value> {
    let path = match required_string_arg(args, "path") {
        Ok(path) => path,
        Err(message) => return Ok(tool_content_error(&message)),
    };
    let find = match required_string_arg(args, "find") {
        Ok(find) if !find.is_empty() => find,
        Ok(_) => return Ok(tool_content_error("find must not be empty")),
        Err(message) => return Ok(tool_content_error(&message)),
    };
    let replace = match required_string_arg(args, "replace") {
        Ok(replace) => replace,
        Err(message) => return Ok(tool_content_error(&message)),
    };
    let replace_all = args
        .get("replaceAll")
        .and_then(Value::as_bool)
        .unwrap_or(false);
    let expected_matches = match read_positive_usize_arg(args, "expectedMatches") {
        Ok(value) => value,
        Err(message) => return Ok(tool_content_error(&message)),
    };

    let resolved = match resolve_repo_path(Some(path)) {
        Ok(path) => path,
        Err(message) => return Ok(tool_content_error(&message)),
    };
    if !resolved.is_file() {
        return Ok(tool_content_error(&format!(
            "Path is not a file: {}",
            display_repo_path(&resolved)
        )));
    }

    let mut contents = match read_utf8_file(&resolved) {
        Ok(contents) => contents,
        Err(message) => return Ok(tool_content_error(&message)),
    };

    let replaced = match replace_text(&mut contents, find, replace, replace_all, expected_matches) {
        Ok(replaced) => replaced,
        Err(message) => return Ok(tool_content_error(&message)),
    };

    if let Err(message) = fs::write(&resolved, contents) {
        return Ok(tool_content_error(&format!(
            "Failed to write {}: {message}",
            display_repo_path(&resolved)
        )));
    }

    Ok(tool_content_text(&format!(
        "Updated {} (replaced {} occurrence{})",
        display_repo_path(&resolved),
        replaced,
        if replaced == 1 { "" } else { "s" }
    )))
}

fn handle_apply_patch(args: &Value) -> Result<Value, Value> {
    let path = match required_string_arg(args, "path") {
        Ok(path) => path,
        Err(message) => return Ok(tool_content_error(&message)),
    };
    let resolved = match resolve_repo_path(Some(path)) {
        Ok(path) => path,
        Err(message) => return Ok(tool_content_error(&message)),
    };
    if !resolved.is_file() {
        return Ok(tool_content_error(&format!(
            "Path is not a file: {}",
            display_repo_path(&resolved)
        )));
    }

    let edits = match args.get("edits").and_then(Value::as_array) {
        Some(edits) if !edits.is_empty() => edits,
        Some(_) => {
            return Ok(tool_content_error(
                "edits must contain at least one operation",
            ));
        }
        None => return Ok(tool_content_error("Missing required parameter: edits")),
    };
    if edits.len() > MAX_PATCH_EDITS {
        return Ok(tool_content_error(&format!(
            "Too many edits: {} (max {MAX_PATCH_EDITS})",
            edits.len()
        )));
    }

    let mut contents = match read_utf8_file(&resolved) {
        Ok(contents) => contents,
        Err(message) => return Ok(tool_content_error(&message)),
    };

    let mut summaries = Vec::new();
    for (index, edit) in edits.iter().enumerate() {
        let action = edit
            .get("action")
            .or_else(|| edit.get("type"))
            .and_then(Value::as_str);
        let Some(action) = action else {
            return Ok(tool_content_error(&format!(
                "Edit {} is missing an action",
                index + 1
            )));
        };

        match action {
            "replace" => {
                let find = match required_string_arg(edit, "find") {
                    Ok(find) if !find.is_empty() => find,
                    Ok(_) => return Ok(tool_content_error("find must not be empty")),
                    Err(message) => return Ok(tool_content_error(&message)),
                };
                let replace = match required_string_arg(edit, "replace") {
                    Ok(replace) => replace,
                    Err(message) => return Ok(tool_content_error(&message)),
                };
                let replace_all = edit
                    .get("replaceAll")
                    .and_then(Value::as_bool)
                    .unwrap_or(false);
                let expected_matches = match read_positive_usize_arg(edit, "expectedMatches") {
                    Ok(value) => value,
                    Err(message) => return Ok(tool_content_error(&message)),
                };
                let replaced =
                    match replace_text(&mut contents, find, replace, replace_all, expected_matches)
                    {
                        Ok(replaced) => replaced,
                        Err(message) => return Ok(tool_content_error(&message)),
                    };
                summaries.push(format!(
                    "{}. replace ({replaced} occurrence{})",
                    index + 1,
                    if replaced == 1 { "" } else { "s" }
                ));
            }
            "delete" => {
                let find = match required_string_arg(edit, "find") {
                    Ok(find) if !find.is_empty() => find,
                    Ok(_) => return Ok(tool_content_error("find must not be empty")),
                    Err(message) => return Ok(tool_content_error(&message)),
                };
                let replace_all = edit
                    .get("replaceAll")
                    .and_then(Value::as_bool)
                    .unwrap_or(false);
                let expected_matches = match read_positive_usize_arg(edit, "expectedMatches") {
                    Ok(value) => value,
                    Err(message) => return Ok(tool_content_error(&message)),
                };
                let removed =
                    match replace_text(&mut contents, find, "", replace_all, expected_matches) {
                        Ok(removed) => removed,
                        Err(message) => return Ok(tool_content_error(&message)),
                    };
                summaries.push(format!(
                    "{}. delete ({removed} occurrence{})",
                    index + 1,
                    if removed == 1 { "" } else { "s" }
                ));
            }
            "insert_before" | "insert_after" => {
                let find = match required_string_arg(edit, "find") {
                    Ok(find) if !find.is_empty() => find,
                    Ok(_) => return Ok(tool_content_error("find must not be empty")),
                    Err(message) => return Ok(tool_content_error(&message)),
                };
                let content = match required_string_arg(edit, "content") {
                    Ok(content) => content,
                    Err(message) => return Ok(tool_content_error(&message)),
                };
                if let Err(message) =
                    insert_text(&mut contents, find, content, action == "insert_before")
                {
                    return Ok(tool_content_error(&message));
                }
                summaries.push(format!("{}. {}", index + 1, action));
            }
            other => {
                return Ok(tool_content_error(&format!(
                    "Unsupported patch action: {other}. Supported actions: replace, delete, insert_before, insert_after"
                )));
            }
        }
    }

    if let Err(message) = fs::write(&resolved, contents) {
        return Ok(tool_content_error(&format!(
            "Failed to write {}: {message}",
            display_repo_path(&resolved)
        )));
    }

    Ok(tool_content_text(&format!(
        "Updated {}\n{}",
        display_repo_path(&resolved),
        summaries.join("\n")
    )))
}

// ─── new tools ───────────────────────────────────────────────────────────────

fn handle_create_file(args: &Value) -> Result<Value, Value> {
    let path = match required_string_arg(args, "path") {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    let content = match args.get("content").and_then(Value::as_str) {
        Some(c) => c,
        None => return Ok(tool_content_error("Missing required parameter: content")),
    };
    let overwrite = args
        .get("overwrite")
        .and_then(Value::as_bool)
        .unwrap_or(false);

    let resolved = match resolve_new_repo_path(path) {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };

    if resolved.exists() {
        if resolved.is_dir() {
            return Ok(tool_content_error(&format!(
                "Path is a directory, not a file: {}",
                display_repo_path(&resolved)
            )));
        }
        if !overwrite {
            return Ok(tool_content_error(&format!(
                "File already exists: {}. Set overwrite=true to replace it.",
                display_repo_path(&resolved)
            )));
        }
    }

    if let Some(parent) = resolved.parent() {
        if let Err(e) = fs::create_dir_all(parent) {
            return Ok(tool_content_error(&format!(
                "Failed to create parent directories: {e}"
            )));
        }
    }

    let byte_count = content.len();
    if let Err(e) = fs::write(&resolved, content) {
        return Ok(tool_content_error(&format!(
            "Failed to write {}: {e}",
            display_repo_path(&resolved)
        )));
    }

    let _ = byte_count;
    Ok(tool_content_text(&format!(
        "Created {}",
        display_repo_path(&resolved)
    )))
}

fn handle_move_file(args: &Value) -> Result<Value, Value> {
    let source_str = match required_string_arg(args, "source") {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    let dest_str = match required_string_arg(args, "destination") {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };

    let source = match resolve_repo_path(Some(source_str)) {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    if !source.is_file() {
        return Ok(tool_content_error(&format!(
            "Source is not a file: {}",
            display_repo_path(&source)
        )));
    }

    let dest = match resolve_new_repo_path(dest_str) {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    if dest.exists() {
        return Ok(tool_content_error(&format!(
            "Destination already exists: {}",
            display_repo_path(&dest)
        )));
    }

    if let Some(parent) = dest.parent() {
        if let Err(e) = fs::create_dir_all(parent) {
            return Ok(tool_content_error(&format!(
                "Failed to create destination parent directories: {e}"
            )));
        }
    }

    // Try atomic rename first; fall back to copy+delete across filesystem boundaries.
    if let Err(rename_err) = fs::rename(&source, &dest) {
        match fs::copy(&source, &dest) {
            Ok(_) => {
                if let Err(del_err) = fs::remove_file(&source) {
                    return Ok(tool_content_error(&format!(
                        "Copied {} to {} but failed to remove source: {del_err}",
                        display_repo_path(&source),
                        display_repo_path(&dest)
                    )));
                }
            }
            Err(copy_err) => {
                return Ok(tool_content_error(&format!(
                    "Failed to move {} to {}: rename failed ({rename_err}), copy fallback also failed ({copy_err})",
                    display_repo_path(&source),
                    display_repo_path(&dest)
                )));
            }
        }
    }

    Ok(tool_content_text(&format!(
        "Moved {} → {}",
        display_repo_path(&source),
        display_repo_path(&dest)
    )))
}

fn handle_rename_symbol(args: &Value) -> Result<Value, Value> {
    let symbol = match required_string_arg(args, "symbol") {
        Ok(s) if !s.is_empty() => s,
        Ok(_) => return Ok(tool_content_error("symbol must not be empty")),
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    let new_name = match required_string_arg(args, "new_name") {
        Ok(s) if !s.is_empty() => s,
        Ok(_) => return Ok(tool_content_error("new_name must not be empty")),
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    let dry_run = args
        .get("dry_run")
        .and_then(Value::as_bool)
        .unwrap_or(false);

    let scope = match resolve_repo_path(args.get("path").and_then(Value::as_str)) {
        Ok(path) => path,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };

    let mut files = Vec::new();
    if scope.is_dir() {
        if let Err(msg) = collect_search_files(&scope, &mut files) {
            return Ok(tool_content_error(&msg));
        }
    } else if scope.is_file() {
        files.push(scope);
    } else {
        return Ok(tool_content_error(&format!(
            "Path is neither a file nor a directory: {}",
            display_repo_path(&scope)
        )));
    }

    let mut summaries = Vec::new();
    let mut total_replacements = 0usize;

    for file in &files {
        let contents = match read_utf8_file_if_possible(file) {
            Ok(Some(c)) => c,
            Ok(None) => continue,
            Err(msg) => return Ok(tool_content_error(&msg)),
        };

        let count = count_word_boundary_matches(&contents, symbol);
        if count == 0 {
            continue;
        }

        total_replacements += count;
        summaries.push(format!(
            "{}: {count} occurrence{}",
            display_repo_path(file),
            if count == 1 { "" } else { "s" }
        ));

        if !dry_run {
            let updated = word_boundary_replace(&contents, symbol, new_name);
            if let Err(e) = fs::write(file, &updated) {
                return Ok(tool_content_error(&format!(
                    "Failed to write {}: {e}",
                    display_repo_path(file)
                )));
            }
        }
    }

    if total_replacements == 0 {
        return Ok(tool_content_text(&format!(
            "No word-boundary occurrences of '{}' found",
            symbol
        )));
    }

    let header = if dry_run {
        format!(
            "[dry-run] Would rename '{}' → '{}': {} occurrence{} across {} file{}",
            symbol,
            new_name,
            total_replacements,
            if total_replacements == 1 { "" } else { "s" },
            summaries.len(),
            if summaries.len() == 1 { "" } else { "s" }
        )
    } else {
        format!(
            "Renamed '{}' → '{}': {} occurrence{} across {} file{}",
            symbol,
            new_name,
            total_replacements,
            if total_replacements == 1 { "" } else { "s" },
            summaries.len(),
            if summaries.len() == 1 { "" } else { "s" }
        )
    };

    Ok(tool_content_text(&format!(
        "{}\n{}",
        header,
        summaries.join("\n")
    )))
}

fn handle_create_module(args: &Value) -> Result<Value, Value> {
    // Accept either `name` (auto-generates <Name>.tlang in repo root) or explicit `path`.
    let path_str: String = if let Some(name) = args.get("name").and_then(Value::as_str) {
        format!("{name}.tlang")
    } else {
        match required_string_arg(args, "path") {
            Ok(p) => p.to_string(),
            Err(msg) => {
                return Ok(tool_content_error(&format!(
                    "{msg} (provide 'name' or 'path')"
                )));
            }
        }
    };

    if !path_str.ends_with(".tlang") {
        return Ok(tool_content_error(
            "path must end with .tlang (e.g. src/MyModule.tlang)",
        ));
    }

    let resolved = match resolve_new_repo_path(&path_str) {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };

    if resolved.exists() {
        return Ok(tool_content_error(&format!(
            "File already exists: {}",
            display_repo_path(&resolved)
        )));
    }

    let lang = args.get("lang").and_then(Value::as_str);
    let template_name = args
        .get("template_name")
        .and_then(Value::as_str)
        .unwrap_or("myTemplate");
    let with_model = args
        .get("with_model")
        .and_then(Value::as_bool)
        .unwrap_or(true);
    let with_helper = args
        .get("with_helper")
        .and_then(Value::as_bool)
        .unwrap_or(true);

    let mut content = String::new();

    // Optional lang [] template block
    if let Some(lang_tag) = lang {
        content.push_str(&format!(
            "lang [{lang_tag}] {template_name}() {{\n    impl[] MyClass {{\n    }}\n}}\n"
        ));
    }

    // Helper block with stub main
    if with_helper {
        if !content.is_empty() {
            content.push('\n');
        }
        content.push_str("helper {\n    func main(): String {\n        return \"\";\n    }\n}\n");
    }

    // Model block
    if with_model {
        if !content.is_empty() {
            content.push('\n');
        }
        content.push_str("model {\n}\n");
    }

    if content.is_empty() {
        content.push('\n');
    }

    if let Some(parent) = resolved.parent() {
        if let Err(e) = fs::create_dir_all(parent) {
            return Ok(tool_content_error(&format!(
                "Failed to create parent directories: {e}"
            )));
        }
    }

    let byte_count = content.len();
    if let Err(e) = fs::write(&resolved, &content) {
        return Ok(tool_content_error(&format!(
            "Failed to write {}: {e}",
            display_repo_path(&resolved)
        )));
    }

    let _ = byte_count;
    Ok(tool_content_text(&format!(
        "Created {}",
        display_repo_path(&resolved)
    )))
}

fn handle_add_leaf_to_template(args: &Value) -> Result<Value, Value> {
    let path = match required_string_arg(args, "path") {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    let set_name = match required_string_arg(args, "set_name") {
        Ok(s) if !s.is_empty() => s,
        Ok(_) => return Ok(tool_content_error("set_name must not be empty")),
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    let leaf_name = match required_string_arg(args, "leaf_name") {
        Ok(s) if !s.is_empty() => s,
        Ok(_) => return Ok(tool_content_error("leaf_name must not be empty")),
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    // leaf_ref defaults to leaf_name; the `&` prefix is always written by this tool.
    let leaf_ref = args
        .get("leaf_ref")
        .and_then(Value::as_str)
        .unwrap_or(leaf_name);

    let resolved = match resolve_repo_path(Some(path)) {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    if !resolved.is_file() {
        return Ok(tool_content_error(&format!(
            "Path is not a file: {}",
            display_repo_path(&resolved)
        )));
    }

    let mut contents = match read_utf8_file(&resolved) {
        Ok(c) => c,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };

    // Locate `set <set_name>` with word boundaries.
    let needle = format!("set {set_name}");
    let set_pos = match find_word_boundary_match(&contents, &needle) {
        Some(pos) => pos,
        None => {
            return Ok(tool_content_error(&format!(
                "set '{set_name}' not found in {}",
                display_repo_path(&resolved)
            )));
        }
    };

    // Find the opening `{` of the set body (may be preceded by params and output decl).
    let after_set = &contents[set_pos + needle.len()..];
    let brace_offset = match after_set.find('{') {
        Some(pos) => pos,
        None => {
            return Ok(tool_content_error(&format!(
                "No opening brace found for set '{set_name}'"
            )));
        }
    };
    let body_open = set_pos + needle.len() + brace_offset;

    // Find the matching closing brace using brace counting.
    let body_close = match find_matching_close_brace(&contents, body_open) {
        Some(pos) => pos,
        None => {
            return Ok(tool_content_error(&format!(
                "No matching closing brace for set '{set_name}'"
            )));
        }
    };

    // Determine indentation from existing body content.
    let body = &contents[body_open + 1..body_close];
    let indent = detect_indent(body);

    // Build the new attribute line.
    let new_attr_line = format!("{indent}{leaf_name}: &{leaf_ref}");

    // If existing attributes don't end with a newline before the `}`, add one.
    let trimmed_body = body.trim_end();
    let body_has_content = !trimmed_body.is_empty();

    let insertion = if body_has_content {
        format!("\n{new_attr_line}")
    } else {
        format!("{new_attr_line}\n")
    };

    contents.insert_str(body_close, &insertion);

    if let Err(e) = fs::write(&resolved, &contents) {
        return Ok(tool_content_error(&format!(
            "Failed to write {}: {e}",
            display_repo_path(&resolved)
        )));
    }

    Ok(tool_content_text(&format!(
        "Updated {}: added '{leaf_name}: &{leaf_ref}' to set '{set_name}'",
        display_repo_path(&resolved)
    )))
}

fn handle_move_method(args: &Value) -> Result<Value, Value> {
    let source_path = match required_string_arg(args, "source_path") {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    let dest_path = match required_string_arg(args, "dest_path") {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    let func_name = match required_string_arg(args, "func_name") {
        Ok(s) if !s.is_empty() => s,
        Ok(_) => return Ok(tool_content_error("func_name must not be empty")),
        Err(msg) => return Ok(tool_content_error(&msg)),
    };

    let source = match resolve_repo_path(Some(source_path)) {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    if !source.is_file() {
        return Ok(tool_content_error(&format!(
            "Source is not a file: {}",
            display_repo_path(&source)
        )));
    }

    let dest = match resolve_repo_path(Some(dest_path)) {
        Ok(p) => p,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };
    if !dest.is_file() {
        return Ok(tool_content_error(&format!(
            "Destination is not a file: {}",
            display_repo_path(&dest)
        )));
    }

    if source == dest {
        return Ok(tool_content_error(
            "source_path and dest_path must be different files",
        ));
    }

    let source_contents = match read_utf8_file(&source) {
        Ok(c) => c,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };

    // Extract the complete func block (from line-start to end of closing-brace line).
    let func_text = match extract_func(&source_contents, func_name) {
        Ok(t) => t,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };

    // Remove the func from source content.
    let stripped_source = strip_func(&source_contents, &func_text);

    let dest_contents = match read_utf8_file(&dest) {
        Ok(c) => c,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };

    // Insert the func before the closing brace of the destination helper block.
    let updated_dest = match insert_func_into_helper(&dest_contents, &func_text) {
        Ok(s) => s,
        Err(msg) => return Ok(tool_content_error(&msg)),
    };

    // Write source first; on dest failure, restore source to keep consistent state.
    if let Err(e) = fs::write(&source, &stripped_source) {
        return Ok(tool_content_error(&format!(
            "Failed to write source {}: {e}",
            display_repo_path(&source)
        )));
    }
    if let Err(e) = fs::write(&dest, &updated_dest) {
        // Attempt to restore the original source so the repo stays consistent.
        let restore_note = match fs::write(&source, &source_contents) {
            Ok(_) => "source restored".to_string(),
            Err(re) => {
                format!("WARNING: source restore also failed ({re}) — repo may be inconsistent")
            }
        };
        return Ok(tool_content_error(&format!(
            "Failed to write destination {} ({restore_note}): {e}",
            display_repo_path(&dest)
        )));
    }

    Ok(tool_content_text(&format!(
        "Moved func '{func_name}': {} → {}",
        display_repo_path(&source),
        display_repo_path(&dest)
    )))
}

fn required_string_arg<'a>(args: &'a Value, name: &str) -> Result<&'a str, String> {
    args.get(name)
        .and_then(Value::as_str)
        .ok_or_else(|| format!("Missing required parameter: {name}"))
}

fn read_positive_usize_arg(args: &Value, name: &str) -> Result<Option<usize>, String> {
    match args.get(name) {
        None => Ok(None),
        Some(value) => {
            let value = value
                .as_u64()
                .ok_or_else(|| format!("{name} must be a positive integer"))?;
            if value == 0 {
                return Err(format!("{name} must be greater than 0"));
            }
            usize::try_from(value)
                .map(Some)
                .map_err(|_| format!("{name} is too large"))
        }
    }
}

fn read_limit_arg(
    args: &Value,
    name: &str,
    default_value: usize,
    max_value: usize,
) -> Result<usize, String> {
    let value = match read_positive_usize_arg(args, name)? {
        Some(value) => value,
        None => return Ok(default_value),
    };
    Ok(value.min(max_value))
}

fn resolve_repo_path(path: Option<&str>) -> Result<PathBuf, String> {
    let root = std::env::current_dir()
        .map_err(|err| format!("Failed to read current directory: {err}"))?
        .canonicalize()
        .map_err(|err| format!("Failed to resolve repository root: {err}"))?;

    let requested = path.unwrap_or(".");
    let candidate = if Path::new(requested).is_absolute() {
        PathBuf::from(requested)
    } else {
        root.join(requested)
    };

    if !candidate.exists() {
        return Err(format!("Path not found: {}", candidate.display()));
    }

    let canonical = candidate
        .canonicalize()
        .map_err(|err| format!("Failed to resolve path {}: {err}", candidate.display()))?;

    if !canonical.starts_with(&root) {
        return Err(format!(
            "Path is outside the repository root: {}",
            candidate.display()
        ));
    }

    Ok(canonical)
}

fn display_repo_path(path: &Path) -> String {
    let root = match std::env::current_dir().and_then(|path| path.canonicalize()) {
        Ok(root) => root,
        Err(_) => return path.display().to_string(),
    };

    match path.strip_prefix(&root) {
        Ok(relative) if relative.as_os_str().is_empty() => ".".to_string(),
        Ok(relative) => relative.display().to_string(),
        Err(_) => path.display().to_string(),
    }
}

fn read_dir_sorted(dir: &Path) -> Result<Vec<fs::DirEntry>, String> {
    let mut entries = fs::read_dir(dir)
        .map_err(|err| format!("Failed to read directory {}: {err}", display_repo_path(dir)))?
        .collect::<Result<Vec<_>, _>>()
        .map_err(|err| format!("Failed to read directory {}: {err}", display_repo_path(dir)))?;
    entries.sort_by_key(|entry| entry.file_name());
    Ok(entries)
}

fn should_skip_recursive_dir(path: &Path) -> bool {
    matches!(
        path.file_name().and_then(|name| name.to_str()),
        Some(".git" | "target")
    )
}

fn read_utf8_file(path: &Path) -> Result<String, String> {
    let bytes = fs::read(path)
        .map_err(|err| format!("Failed to read {}: {err}", display_repo_path(path)))?;
    String::from_utf8(bytes)
        .map_err(|_| format!("{} is not valid UTF-8 text", display_repo_path(path)))
}

fn read_utf8_file_if_possible(path: &Path) -> Result<Option<String>, String> {
    let bytes = fs::read(path)
        .map_err(|err| format!("Failed to read {}: {err}", display_repo_path(path)))?;
    Ok(String::from_utf8(bytes).ok())
}

fn collect_search_files(dir: &Path, files: &mut Vec<PathBuf>) -> Result<(), String> {
    for entry in read_dir_sorted(dir)? {
        let path = entry.path();
        let file_type = entry
            .file_type()
            .map_err(|err| format!("Failed to read file type for {}: {err}", path.display()))?;
        if file_type.is_dir() {
            if should_skip_recursive_dir(&path) {
                continue;
            }
            collect_search_files(&path, files)?;
        } else if file_type.is_file() {
            files.push(path);
        }
    }
    Ok(())
}

fn truncate_line(line: &str, max_chars: usize) -> String {
    if line.chars().count() <= max_chars {
        return line.to_string();
    }
    let truncated: String = line.chars().take(max_chars).collect();
    format!("{truncated}…")
}

fn replace_text(
    contents: &mut String,
    find: &str,
    replace: &str,
    replace_all: bool,
    expected_matches: Option<usize>,
) -> Result<usize, String> {
    if find.is_empty() {
        return Err("find must not be empty".to_string());
    }

    let matches = contents.matches(find).count();
    if matches == 0 {
        return Err("No matches found for the requested text".to_string());
    }
    if let Some(expected_matches) = expected_matches {
        if matches != expected_matches {
            return Err(format!(
                "Expected {expected_matches} match(es) but found {matches}"
            ));
        }
    }
    if !replace_all && matches != 1 {
        return Err(format!(
            "Found {matches} matches for the requested text; refine the search or set replaceAll=true"
        ));
    }

    let replaced = if replace_all { matches } else { 1 };
    *contents = contents.replacen(find, replace, replaced);
    Ok(replaced)
}

fn insert_text(
    contents: &mut String,
    find: &str,
    content: &str,
    insert_before: bool,
) -> Result<(), String> {
    if find.is_empty() {
        return Err("find must not be empty".to_string());
    }

    let occurrences: Vec<usize> = contents
        .match_indices(find)
        .map(|(index, _)| index)
        .collect();
    if occurrences.is_empty() {
        return Err("No matches found for the requested text".to_string());
    }
    if occurrences.len() != 1 {
        return Err(format!(
            "Found {} matches for the requested text; patch anchors must be unique",
            occurrences.len()
        ));
    }

    let index = occurrences[0];
    let insert_at = if insert_before {
        index
    } else {
        index + find.len()
    };
    contents.insert_str(insert_at, content);
    Ok(())
}

// ─── new helper utilities ─────────────────────────────────────────────────────

/// Resolve a repository path that may not yet exist.
/// Validates the resolved path is inside the repository root.
fn resolve_new_repo_path(path: &str) -> Result<PathBuf, String> {
    let root = std::env::current_dir()
        .map_err(|err| format!("Failed to read current directory: {err}"))?
        .canonicalize()
        .map_err(|err| format!("Failed to resolve repository root: {err}"))?;

    let candidate = if Path::new(path).is_absolute() {
        PathBuf::from(path)
    } else {
        root.join(path)
    };

    // If the path already exists, delegate to the standard resolver.
    if candidate.exists() {
        let canonical = candidate
            .canonicalize()
            .map_err(|err| format!("Failed to resolve path: {err}"))?;
        if !canonical.starts_with(&root) {
            return Err(format!(
                "Path is outside the repository root: {}",
                candidate.display()
            ));
        }
        return Ok(canonical);
    }

    // Walk up to the longest existing ancestor, canonicalize it, then re-append
    // the non-existing suffix so we can still validate the root boundary.
    let mut ancestor = candidate.clone();
    let mut suffix: Vec<std::ffi::OsString> = Vec::new();
    loop {
        if ancestor.exists() {
            break;
        }
        if let Some(name) = ancestor.file_name() {
            suffix.push(name.to_os_string());
        }
        if !ancestor.pop() {
            return Err(format!("Cannot resolve path: {}", candidate.display()));
        }
    }
    let mut canonical = ancestor
        .canonicalize()
        .map_err(|err| format!("Failed to resolve ancestor path: {err}"))?;
    suffix.reverse();
    for component in suffix {
        canonical.push(component);
    }
    if !canonical.starts_with(&root) {
        return Err(format!(
            "Path is outside the repository root: {}",
            candidate.display()
        ));
    }
    Ok(canonical)
}

/// Return true if `c` is an identifier word character (alphanumeric or `_`).
fn is_word_char(c: char) -> bool {
    c.is_alphanumeric() || c == '_'
}

/// Return the byte position of the first occurrence of `needle` in `haystack`
/// that is surrounded by word boundaries (non-word-char or start/end of string).
fn find_word_boundary_match(haystack: &str, needle: &str) -> Option<usize> {
    if needle.is_empty() {
        return None;
    }
    let mut start = 0;
    while let Some(pos) = haystack[start..].find(needle) {
        let abs_pos = start + pos;
        let before_ok = haystack[..abs_pos]
            .chars()
            .next_back()
            .map_or(true, |c| !is_word_char(c));
        let after_end = abs_pos + needle.len();
        let after_ok = haystack[after_end..]
            .chars()
            .next()
            .map_or(true, |c| !is_word_char(c));
        if before_ok && after_ok {
            return Some(abs_pos);
        }
        start = abs_pos + 1;
    }
    None
}

/// Count word-boundary occurrences of `needle` in `haystack`.
fn count_word_boundary_matches(haystack: &str, needle: &str) -> usize {
    if needle.is_empty() {
        return 0;
    }
    let mut count = 0;
    let mut start = 0;
    while let Some(pos) = haystack[start..].find(needle) {
        let abs_pos = start + pos;
        let before_ok = haystack[..abs_pos]
            .chars()
            .next_back()
            .map_or(true, |c| !is_word_char(c));
        let after_end = abs_pos + needle.len();
        let after_ok = haystack[after_end..]
            .chars()
            .next()
            .map_or(true, |c| !is_word_char(c));
        if before_ok && after_ok {
            count += 1;
        }
        start = abs_pos + 1;
    }
    count
}

/// Replace all word-boundary occurrences of `needle` in `haystack` with
/// `replacement`.
fn word_boundary_replace(haystack: &str, needle: &str, replacement: &str) -> String {
    if needle.is_empty() {
        return haystack.to_string();
    }
    let mut result = String::with_capacity(haystack.len());
    let mut start = 0;
    while let Some(pos) = haystack[start..].find(needle) {
        let abs_pos = start + pos;
        let before_ok = haystack[..abs_pos]
            .chars()
            .next_back()
            .map_or(true, |c| !is_word_char(c));
        let after_end = abs_pos + needle.len();
        let after_ok = haystack[after_end..]
            .chars()
            .next()
            .map_or(true, |c| !is_word_char(c));
        if before_ok && after_ok {
            result.push_str(&haystack[start..abs_pos]);
            result.push_str(replacement);
            start = after_end;
        } else {
            result.push_str(&haystack[start..abs_pos + 1]);
            start = abs_pos + 1;
        }
    }
    result.push_str(&haystack[start..]);
    result
}

/// Find the position of the closing `}` that matches the opening `{` at
/// `open_pos` in `text`, respecting string literals and nested braces.
fn find_matching_close_brace(text: &str, open_pos: usize) -> Option<usize> {
    let bytes = text.as_bytes();
    if open_pos >= bytes.len() || bytes[open_pos] != b'{' {
        return None;
    }
    let mut depth = 0usize;
    let mut in_string = false;
    let mut escape_next = false;
    let mut i = open_pos;
    while i < bytes.len() {
        let b = bytes[i];
        if escape_next {
            escape_next = false;
            i += 1;
            continue;
        }
        if in_string {
            if b == b'\\' {
                escape_next = true;
            } else if b == b'"' {
                in_string = false;
            }
        } else {
            match b {
                b'"' => in_string = true,
                b'{' => depth += 1,
                b'}' => {
                    depth -= 1;
                    if depth == 0 {
                        return Some(i);
                    }
                }
                _ => {}
            }
        }
        i += 1;
    }
    None
}

/// Detect the indentation used in `body` by inspecting the first non-empty line.
/// Falls back to four spaces.
fn detect_indent(body: &str) -> String {
    for line in body.lines() {
        let trimmed = line.trim_start();
        if !trimmed.is_empty() {
            let leading = &line[..line.len() - trimmed.len()];
            if !leading.is_empty() {
                return leading.to_string();
            }
        }
    }
    "    ".to_string()
}

/// Extract the complete `func <name> { ... }` block from `contents`, starting
/// from the beginning of the line containing `func <name>` and ending at the
/// end of the line containing the matching closing brace (inclusive of newline).
fn extract_func(contents: &str, func_name: &str) -> Result<String, String> {
    let needle = format!("func {func_name}");
    let func_pos = match find_word_boundary_match(contents, &needle) {
        Some(pos) => pos,
        None => return Err(format!("func '{func_name}' not found")),
    };

    // Go back to the start of the line that contains `func_pos`.
    let line_start = contents[..func_pos].rfind('\n').map(|p| p + 1).unwrap_or(0);

    // Find the opening `{` that begins the function body.
    let after_sig = &contents[func_pos + needle.len()..];
    let brace_offset = match after_sig.find('{') {
        Some(pos) => pos,
        None => return Err(format!("No opening brace found for func '{func_name}'")),
    };
    let body_open = func_pos + needle.len() + brace_offset;

    let body_close = match find_matching_close_brace(contents, body_open) {
        Some(pos) => pos,
        None => {
            return Err(format!(
                "No matching closing brace found for func '{func_name}'"
            ));
        }
    };

    // Include to (and including) the newline after the closing brace.
    let end_pos = contents[body_close..]
        .find('\n')
        .map(|p| body_close + p + 1)
        .unwrap_or(body_close + 1);
    let end_pos = end_pos.min(contents.len());

    Ok(contents[line_start..end_pos].to_string())
}

/// Remove `func_text` from `contents` and clean up surrounding blank lines.
fn strip_func(contents: &str, func_text: &str) -> String {
    if let Some(pos) = contents.find(func_text) {
        let before = &contents[..pos];
        let after = &contents[pos + func_text.len()..];
        let before_clean = before.trim_end_matches('\n');
        let after_clean = after.trim_start_matches('\n');
        if before_clean.is_empty() {
            after_clean.to_string()
        } else if after_clean.is_empty() {
            format!("{before_clean}\n")
        } else {
            format!("{before_clean}\n\n{after_clean}")
        }
    } else {
        contents.to_string()
    }
}

/// Insert `func_text` just before the closing `}` of the first `helper { … }`
/// block found in `contents`.
fn insert_func_into_helper(contents: &str, func_text: &str) -> Result<String, String> {
    let helper_pos = match find_word_boundary_match(contents, "helper") {
        Some(pos) => pos,
        None => {
            return Err(
                "No 'helper' block found in destination file — cannot insert func".to_string(),
            );
        }
    };

    let after_helper = &contents[helper_pos + "helper".len()..];
    let brace_offset = match after_helper.find('{') {
        Some(pos) => pos,
        None => return Err("No opening brace found for helper block".to_string()),
    };
    let helper_body_open = helper_pos + "helper".len() + brace_offset;

    let helper_close = match find_matching_close_brace(contents, helper_body_open) {
        Some(pos) => pos,
        None => return Err("No matching closing brace found for helper block".to_string()),
    };

    // Find the start of the line that contains the helper closing brace.
    let close_line_start = contents[..helper_close]
        .rfind('\n')
        .map(|p| p + 1)
        .unwrap_or(0);

    let before = contents[..close_line_start].trim_end_matches('\n');
    let after = &contents[close_line_start..];
    let func_trimmed = func_text.trim_end_matches('\n');

    Ok(format!("{before}\n\n{func_trimmed}\n{after}"))
}

fn handle_resources_list() -> Result<Value, Value> {
    Ok(json!({
        "resources": [
            {
                "uri": "tlang://index",
                "name": "TLang Index",
                "description": "Routing index + cheat-sheets. Load first.",
                "mimeType": "text/markdown"
            },
            {
                "uri": "tlang://topic/lang",
                "name": "TLang lang Templates",
                "description": "lang [alias] code-gen templates: directives, spec, escaping.",
                "mimeType": "text/markdown"
            },
            {
                "uri": "tlang://topic/project",
                "name": "TLang Project",
                "description": "Manifest, imports, CLI, packaging.",
                "mimeType": "text/markdown"
            },
            {
                "uri": "tlang://topic/templates",
                "name": "TLang Templates",
                "description": "data/doc/style/raw template types.",
                "mimeType": "text/markdown"
            },
            {
                "uri": "tlang://topic/helpers",
                "name": "TLang Helpers",
                "description": "Functions, if/match, ?./??, lambdas, types, model block.",
                "mimeType": "text/markdown"
            },
            {
                "uri": "tlang://topic/builtins",
                "name": "TLang Builtins",
                "description": "Generator, File, Terminal, List, Leaf, MCPTool libraries.",
                "mimeType": "text/markdown"
            },
            {
                "uri": "tlang://topic/patterns",
                "name": "TLang Patterns",
                "description": "Best practices, lead/spec, file I/O, checklist.",
                "mimeType": "text/markdown"
            },
            {
                "uri": "tlang://topic/mistakes",
                "name": "TLang Mistakes",
                "description": "Common mistakes and fixes.",
                "mimeType": "text/markdown"
            }
        ]
    }))
}

fn handle_resources_read(params: &Value) -> Result<Value, Value> {
    let uri = params
        .get("uri")
        .and_then(Value::as_str)
        .ok_or_else(|| json_rpc_error_payload(-32602, "Missing required parameter: uri", None))?;

    let (content_text, resolved_uri) = match uri {
        "tlang://index" => (TLANG_INDEX, uri),
        "tlang://topic/lang" => (TLANG_LANG, uri),
        "tlang://topic/project" => (TLANG_PROJECT, uri),
        "tlang://topic/templates" => (TLANG_TEMPLATES, uri),
        "tlang://topic/helpers" => (TLANG_HELPERS, uri),
        "tlang://topic/builtins" => (TLANG_BUILTINS, uri),
        "tlang://topic/patterns" => (TLANG_PATTERNS, uri),
        "tlang://topic/mistakes" => (TLANG_MISTAKES, uri),
        _ => {
            return Err(json_rpc_error_payload(
                -32002,
                &format!("Unknown resource URI: {uri}"),
                None,
            ));
        }
    };
    Ok(json!({
        "contents": [
            {
                "uri": resolved_uri,
                "mimeType": "text/markdown",
                "text": content_text
            }
        ]
    }))
}

// ─── prompts ─────────────────────────────────────────────────────────────────

fn handle_prompts_list() -> Result<Value, Value> {
    Ok(json!({
        "prompts": [
            {
                "name": "tlang_system",
                "description": "System prompt orienting an AI to TLang.",
                "arguments": []
            },
            {
                "name": "tlang_patterns",
                "description": "System prompt directing an AI to load TLang patterns.",
                "arguments": []
            },
            {
                "name": "tlang_new_project",
                "description": "Guided prompt to create a new TLang project.",
                "arguments": [
                    {
                        "name": "target_language",
                        "description": "Target language to generate (e.g. Kotlin, Java, TypeScript).",
                        "required": true
                    },
                    {
                        "name": "project_name",
                        "description": "PascalCase project name.",
                        "required": false
                    }
                ]
            },
            {
                "name": "tlang_debug",
                "description": "Prompt to diagnose and fix a TLang error.",
                "arguments": [
                    {
                        "name": "error_message",
                        "description": "Error output from tlang compile or tlang run.",
                        "required": true
                    }
                ]
            }
        ]
    }))
}

fn handle_prompts_get(params: &Value) -> Result<Value, Value> {
    let name = params
        .get("name")
        .and_then(Value::as_str)
        .ok_or_else(|| json_rpc_error_payload(-32602, "Missing required parameter: name", None))?;

    let arguments = params.get("arguments").cloned().unwrap_or(json!({}));

    match name {
        "tlang_system" => Ok(prompt_tlang_system()),
        "tlang_patterns" => Ok(prompt_tlang_patterns()),
        "tlang_new_project" => Ok(prompt_tlang_new_project(&arguments)),
        "tlang_debug" => Ok(prompt_tlang_debug(&arguments)),
        _ => Err(json_rpc_error_payload(
            -32001,
            &format!("Unknown prompt: {name}"),
            None,
        )),
    }
}

fn prompt_tlang_system() -> Value {
    let system_text = "You are an expert TLang developer.\n\
         TLang is a custom DSL for code generation — it is NOT TypeScript, Kotlin, or any \
         mainstream language.\n\
         \n\
         Before doing anything else, call `tlang_index` to orient yourself. \
         It is small and routes you to the right topic file(s). \
         Then call `tlang_topic(name)` for only the topic(s) you need \
         (project | templates | helpers | patterns | mistakes).";

    json!({
        "description": "System prompt orienting an AI assistant to TLang.",
        "messages": [
            {
                "role": "user",
                "content": {
                    "type": "text",
                    "text": system_text
                }
            }
        ]
    })
}

fn prompt_tlang_patterns() -> Value {
    let text = "You are an expert TLang developer.\n\
         Call `tlang_index` first, then `tlang_topic(\"patterns\")` to load \
         the full best-practices reference before writing any TLang code.";
    json!({
        "description": "System prompt directing an AI to load TLang patterns before writing code.",
        "messages": [
            {
                "role": "user",
                "content": {
                    "type": "text",
                    "text": text
                }
            }
        ]
    })
}

fn prompt_tlang_new_project(arguments: &Value) -> Value {
    let target_language = arguments
        .get("target_language")
        .and_then(Value::as_str)
        .unwrap_or("Kotlin");
    let project_name = arguments
        .get("project_name")
        .and_then(Value::as_str)
        .unwrap_or("MyProject");

    let text = format!(
        "Create a new TLang project named `{project_name}` that generates `{target_language}` source code.\n\
         \n\
         Steps to follow:\n\
         1. Call `tlang_init` with `path` set to the desired directory (e.g. `\"{project_name}\"`).\n\
         2. Update the scaffolded `manifest.yml`:\n\
            - Set `name`, `project`, and `organisation` appropriately.\n\
            - Add a dependency on the `{target_language}` generator if one is available \
              in the local tbox (e.g. `TLangGen/{target_language}Gen/{target_language} 1.0.0:alpha:1 {target_language}Gen`).\n\
         3. Edit `Main.tlang`:\n\
            - Import `TLang.Generator` and `TLang.File`.\n\
            - Import the generator alias: `use {target_language}Gen as {}`.\n\
            - Write a `lang [{}]` template block for the code you want to generate.\n\
            - Write a `helper {{ func main(): String {{ … }} }}` block that calls \
              `Generator.generate(…)` and writes the result with `File.write(…)`.\n\
            - Add a `model {{ … }}` block if you need structured data.\n\
         4. Make sure the generator `.tbag` is published: call `tlang_package` on \
            the generator project first if needed.\n\
         5. Call `tlang_both` with the project path to compile and run.\n\
         6. Check the generated output in the `output/` directory.\n\
         \n\
         Call `tlang_index` then `tlang_topic(\"templates\")` for full syntax details.",
        target_language.to_lowercase(),
        target_language.to_lowercase(),
    );

    json!({
        "description": format!("Guide to create a new TLang project generating {target_language}."),
        "messages": [
            {
                "role": "user",
                "content": {
                    "type": "text",
                    "text": text
                }
            }
        ]
    })
}

fn prompt_tlang_debug(arguments: &Value) -> Value {
    let error_message = arguments
        .get("error_message")
        .and_then(Value::as_str)
        .unwrap_or("<no error message provided>");

    let text = format!(
        "I encountered the following error while working with a TLang project:\n\
         \n\
         ```\n\
         {error_message}\n\
         ```\n\
         \n\
         Please help me diagnose and fix this issue.\n\
         \n\
         Checklist to consider:\n\
         \n\
         - **Missing return type**: does `main` (or any helper function) declare a return type \
           (e.g. `func main(): String`)?\n\
         - **Unknown language tag**: is the generator alias imported with \
           `use <Package> as <alias>` before using `lang [alias]`?\n\
         - **Dependency not packaged**: has the generator `.tbag` been built and published to \
           the local tbox with `tlang_package`? Check `~/.tlang/tbox/…`.\n\
         - **Non-exposed symbol**: is the called symbol listed under `expose` in the \
           generator's main file?\n\
         - **Wrong import path**: for manifest-backed packages, only the `main:` file is \
           importable — adjust the `use` statement to the package alias.\n\
         - **Stale bytecode**: try `tlang_clean` then `tlang_both` to force a full recompile.\n\
         - **Path mismatch**: confirm the `path` argument points to the correct project root \
           (the directory containing `manifest.yml`).\n\
         \n\
         Call `tlang_index` then the relevant `tlang_topic(...)` to review syntax rules if needed.",
    );

    json!({
        "description": "Prompt to diagnose and fix a TLang error.",
        "messages": [
            {
                "role": "user",
                "content": {
                    "type": "text",
                    "text": text
                }
            }
        ]
    })
}

// ─── tests ────────────────────────────────────────────────────────────────────

#[cfg(test)]
mod tests {
    use super::*;
    use std::{
        sync::{Mutex, OnceLock},
        time::{SystemTime, UNIX_EPOCH},
    };

    fn req(id: u64, method: &str, params: Value) -> Value {
        json!({ "jsonrpc": "2.0", "id": id, "method": method, "params": params })
    }

    fn notification(method: &str) -> Value {
        json!({ "jsonrpc": "2.0", "method": method })
    }

    fn cwd_lock() -> &'static Mutex<()> {
        static LOCK: OnceLock<Mutex<()>> = OnceLock::new();
        LOCK.get_or_init(|| Mutex::new(()))
    }

    struct TempRepo {
        path: PathBuf,
    }

    impl TempRepo {
        fn new() -> Self {
            let unique = SystemTime::now()
                .duration_since(UNIX_EPOCH)
                .expect("system time before unix epoch")
                .as_nanos();
            let path = std::env::temp_dir()
                .join(format!("tlang-mcp-test-{}-{unique}", std::process::id()));
            fs::create_dir_all(&path).expect("create temp repo");
            Self { path }
        }

        fn write(&self, relative: &str, contents: &str) {
            let path = self.path.join(relative);
            if let Some(parent) = path.parent() {
                fs::create_dir_all(parent).expect("create parent dir");
            }
            fs::write(path, contents).expect("write test file");
        }
    }

    impl Drop for TempRepo {
        fn drop(&mut self) {
            let _ = fs::remove_dir_all(&self.path);
        }
    }

    struct CurrentDirGuard {
        original: PathBuf,
    }

    impl CurrentDirGuard {
        fn change_to(path: &Path) -> Self {
            let original = std::env::current_dir().expect("read current dir");
            std::env::set_current_dir(path).expect("change current dir");
            Self { original }
        }
    }

    impl Drop for CurrentDirGuard {
        fn drop(&mut self) {
            let _ = std::env::set_current_dir(&self.original);
        }
    }

    fn in_temp_repo(test: impl FnOnce(&TempRepo)) {
        let _lock = cwd_lock().lock().expect("lock current directory");
        reset_server_state();
        let repo = TempRepo::new();
        let _guard = CurrentDirGuard::change_to(&repo.path);
        test(&repo);
        reset_server_state();
    }

    fn reset_server_state() {
        let mut guard = server_state()
            .lock()
            .expect("server state lock should not be poisoned");
        *guard = ServerState::default();
    }

    #[test]
    fn initialize_returns_capabilities() {
        let msg = req(1, "initialize", json!({ "protocolVersion": "2024-11-05" }));
        let resp = handle_message(&msg).expect("expected a response");
        let result = resp.get("result").expect("expected result");
        assert!(result.get("capabilities").is_some());
        assert!(result.get("serverInfo").is_some());
        assert_eq!(result["protocolVersion"], "2024-11-05");
    }

    #[test]
    fn notification_returns_none() {
        let msg = notification("notifications/initialized");
        assert!(handle_message(&msg).is_none());
    }

    #[test]
    fn tools_list_contains_expected_tools() {
        let msg = req(2, "tools/list", json!({}));
        let resp = handle_message(&msg).expect("expected a response");
        let tools = resp["result"]["tools"].as_array().expect("expected array");
        let names: Vec<&str> = tools.iter().filter_map(|t| t["name"].as_str()).collect();
        for expected in &[
            "tlang_init",
            "tlang_compile",
            "tlang_run",
            "tlang_both",
            "tlang_package",
            "tlang_clean",
            "tlang_index",
            "tlang_topic",
            "list_files",
            "read_file",
            "search_code",
            "edit_file",
            "apply_patch",
            "set_project_context",
        ] {
            assert!(names.contains(expected), "missing tool: {expected}");
        }
    }

    #[test]
    fn list_files_returns_recursive_entries_and_truncation_notice() {
        in_temp_repo(|repo| {
            repo.write(
                "src/main.tlang",
                "helper { func main(): String { return \"ok\"; } }",
            );
            repo.write(
                "src/lib.tlang",
                "helper { func lib(): String { return \"ok\"; } }",
            );
            repo.write("README.md", "# test");

            let msg = req(
                20,
                "tools/call",
                json!({
                    "name": "list_files",
                    "arguments": { "path": ".", "recursive": true, "limit": 2 }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(text.contains("README.md") || text.contains("src/"));
            assert!(text.contains("truncated after 2 entries"));
        });
    }

    #[test]
    fn read_file_supports_line_ranges() {
        in_temp_repo(|repo| {
            repo.write("src/demo.tlang", "first\nsecond\nthird\nfourth\n");

            let msg = req(
                21,
                "tools/call",
                json!({
                    "name": "read_file",
                    "arguments": { "path": "src/demo.tlang", "startLine": 2, "endLine": 3 }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(text.contains("src/demo.tlang:2-3"));
            assert!(text.contains("2: second"));
            assert!(text.contains("3: third"));
            assert!(!text.contains("1: first"));
        });
    }

    #[test]
    fn search_code_returns_compact_matches() {
        in_temp_repo(|repo| {
            repo.write(
                "src/a.tlang",
                "helper { func main(): String { return greet(); } }",
            );
            repo.write(
                "src/b.tlang",
                "helper { func greet(): String { return \"hi\"; } }",
            );

            let msg = req(
                22,
                "tools/call",
                json!({
                    "name": "search_code",
                    "arguments": { "query": "greet", "path": "src", "limit": 5 }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(text.contains("src/a.tlang:1:"));
            assert!(text.contains("src/b.tlang:1:"));
        });
    }

    #[test]
    fn edit_file_updates_a_unique_match() {
        in_temp_repo(|repo| {
            repo.write("Main.tlang", "return greet();\n");

            let msg = req(
                23,
                "tools/call",
                json!({
                    "name": "edit_file",
                    "arguments": {
                        "path": "Main.tlang",
                        "find": "greet()",
                        "replace": "hello()"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            assert_eq!(
                fs::read_to_string(repo.path.join("Main.tlang")).expect("read updated file"),
                "return hello();\n"
            );
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(text.contains("Updated Main.tlang"));
        });
    }

    #[test]
    fn apply_patch_supports_multiple_structured_edits() {
        in_temp_repo(|repo| {
            repo.write(
                "Main.tlang",
                "helper {\n    func main(): String {\n        return greet();\n    }\n}\n",
            );

            let msg = req(
                24,
                "tools/call",
                json!({
                    "name": "apply_patch",
                    "arguments": {
                        "path": "Main.tlang",
                        "edits": [
                            { "action": "replace", "find": "greet()", "replace": "hello()" },
                            { "action": "insert_before", "find": "    }\n}\n", "content": "        let name = \"TLang\";\n" }
                        ]
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            let updated =
                fs::read_to_string(repo.path.join("Main.tlang")).expect("read updated file");
            assert!(updated.contains("return hello();"));
            assert!(updated.contains("let name = \"TLang\";"));
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(text.contains("1. replace"));
            assert!(text.contains("2. insert_before"));
        });
    }

    #[test]
    fn tools_call_index_returns_text() {
        let msg = req(
            3,
            "tools/call",
            json!({ "name": "tlang_index", "arguments": {} }),
        );
        let resp = handle_message(&msg).expect("expected a response");
        let text = resp["result"]["content"][0]["text"]
            .as_str()
            .expect("expected string");
        assert!(text.contains("TLang"), "index missing TLang mention");
        assert!(
            text.contains("tlang-templates"),
            "index missing template routing"
        );
    }

    #[test]
    fn tools_call_topic_templates_returns_text() {
        let msg = req(
            3,
            "tools/call",
            json!({ "name": "tlang_topic", "arguments": { "name": "templates" } }),
        );
        let resp = handle_message(&msg).expect("expected a response");
        let text = resp["result"]["content"][0]["text"]
            .as_str()
            .expect("expected string");
        assert!(
            text.contains("lang"),
            "templates topic missing lang mention"
        );
        assert!(text.contains("doc"), "templates topic missing doc mention");
        assert!(
            text.contains("style"),
            "templates topic missing style mention"
        );
    }

    #[test]
    fn tools_call_topic_patterns_returns_text() {
        let msg = req(
            3,
            "tools/call",
            json!({ "name": "tlang_topic", "arguments": { "name": "patterns" } }),
        );
        let resp = handle_message(&msg).expect("expected a response");
        let text = resp["result"]["content"][0]["text"]
            .as_str()
            .expect("expected string");
        assert!(text.contains("spec"), "patterns topic missing spec mention");
        assert!(
            text.contains("File.write"),
            "patterns topic missing File.write mention"
        );
    }

    #[test]
    fn tools_call_topic_unknown_returns_error() {
        let msg = req(
            3,
            "tools/call",
            json!({ "name": "tlang_topic", "arguments": { "name": "nonexistent" } }),
        );
        let resp = handle_message(&msg).expect("expected a response");
        assert!(
            resp["error"].is_object(),
            "expected error for unknown topic"
        );
    }

    #[test]
    fn resources_list_contains_guide() {
        let msg = req(4, "resources/list", json!({}));
        let resp = handle_message(&msg).expect("expected a response");
        let resources = resp["result"]["resources"]
            .as_array()
            .expect("expected array");
        let uris: Vec<&str> = resources.iter().filter_map(|r| r["uri"].as_str()).collect();
        assert!(
            uris.contains(&"tlang://index"),
            "tlang://index resource missing"
        );
        assert!(
            uris.contains(&"tlang://topic/templates"),
            "tlang://topic/templates resource missing"
        );
        assert!(
            uris.contains(&"tlang://topic/patterns"),
            "tlang://topic/patterns resource missing"
        );
    }

    #[test]
    fn resources_read_patterns_returns_markdown() {
        let msg = req(
            5,
            "resources/read",
            json!({ "uri": "tlang://topic/patterns" }),
        );
        let resp = handle_message(&msg).expect("expected a response");
        let contents = resp["result"]["contents"]
            .as_array()
            .expect("expected array");
        assert!(!contents.is_empty());
        assert_eq!(
            contents[0]["mimeType"].as_str().unwrap_or(""),
            "text/markdown"
        );
        let text = contents[0]["text"].as_str().expect("expected text");
        assert!(text.contains("spec"));
    }

    #[test]
    fn resources_read_index_returns_markdown() {
        let msg = req(5, "resources/read", json!({ "uri": "tlang://index" }));
        let resp = handle_message(&msg).expect("expected a response");
        let contents = resp["result"]["contents"]
            .as_array()
            .expect("expected array");
        assert!(!contents.is_empty());
        let mime = contents[0]["mimeType"].as_str().unwrap_or("");
        assert_eq!(mime, "text/markdown");
        let text = contents[0]["text"].as_str().expect("expected text");
        assert!(text.contains("TLang"));
    }

    #[test]
    fn resources_read_unknown_returns_error() {
        let msg = req(6, "resources/read", json!({ "uri": "tlang://nonexistent" }));
        let resp = handle_message(&msg).expect("expected a response");
        assert!(
            resp.get("error").is_some(),
            "expected error for unknown resource"
        );
    }

    #[test]
    fn prompts_list_contains_expected_prompts() {
        let msg = req(7, "prompts/list", json!({}));
        let resp = handle_message(&msg).expect("expected a response");
        let prompts = resp["result"]["prompts"]
            .as_array()
            .expect("expected array");
        let names: Vec<&str> = prompts.iter().filter_map(|p| p["name"].as_str()).collect();
        for expected in &[
            "tlang_system",
            "tlang_patterns",
            "tlang_new_project",
            "tlang_debug",
        ] {
            assert!(names.contains(expected), "missing prompt: {expected}");
        }
    }

    #[test]
    fn prompts_get_patterns_returns_messages() {
        let msg = req(
            8,
            "prompts/get",
            json!({ "name": "tlang_patterns", "arguments": {} }),
        );
        let resp = handle_message(&msg).expect("expected a response");
        let messages = resp["result"]["messages"]
            .as_array()
            .expect("expected messages");
        assert!(!messages.is_empty());
        let text = messages[0]["content"]["text"].as_str().unwrap_or("");
        assert!(text.contains("tlang_topic"));
    }

    #[test]
    fn prompts_get_system_returns_messages() {
        let msg = req(
            8,
            "prompts/get",
            json!({ "name": "tlang_system", "arguments": {} }),
        );
        let resp = handle_message(&msg).expect("expected a response");
        let messages = resp["result"]["messages"]
            .as_array()
            .expect("expected messages");
        assert!(!messages.is_empty());
        let text = messages[0]["content"]["text"].as_str().unwrap_or("");
        assert!(text.contains("TLang"));
    }

    #[test]
    fn prompts_get_new_project_interpolates_language() {
        let msg = req(
            9,
            "prompts/get",
            json!({
                "name": "tlang_new_project",
                "arguments": { "target_language": "TypeScript", "project_name": "TsGen" }
            }),
        );
        let resp = handle_message(&msg).expect("expected a response");
        let messages = resp["result"]["messages"]
            .as_array()
            .expect("expected messages");
        let text = messages[0]["content"]["text"].as_str().unwrap_or("");
        assert!(text.contains("TypeScript"));
        assert!(text.contains("TsGen"));
    }

    #[test]
    fn prompts_get_debug_interpolates_error() {
        let msg = req(
            10,
            "prompts/get",
            json!({
                "name": "tlang_debug",
                "arguments": { "error_message": "missing return type" }
            }),
        );
        let resp = handle_message(&msg).expect("expected a response");
        let messages = resp["result"]["messages"]
            .as_array()
            .expect("expected messages");
        let text = messages[0]["content"]["text"].as_str().unwrap_or("");
        assert!(text.contains("missing return type"));
    }

    #[test]
    fn unknown_method_returns_error() {
        let msg = req(11, "nonexistent/method", json!({}));
        let resp = handle_message(&msg).expect("expected a response");
        assert!(resp.get("error").is_some());
        let code = resp["error"]["code"].as_i64().unwrap_or(0);
        assert_eq!(code, -32601);
    }

    #[test]
    fn unknown_tool_returns_error() {
        let msg = req(
            12,
            "tools/call",
            json!({ "name": "does_not_exist", "arguments": {} }),
        );
        let resp = handle_message(&msg).expect("expected a response");
        assert!(resp.get("error").is_some());
    }

    #[test]
    fn ping_returns_empty_result() {
        let msg = req(13, "ping", json!({}));
        let resp = handle_message(&msg).expect("expected a response");
        assert!(resp.get("result").is_some());
        assert!(resp.get("error").is_none());
    }

    // ─── tools/list includes new tools ────────────────────────────────────────

    #[test]
    fn tools_list_contains_new_tools() {
        let msg = req(30, "tools/list", json!({}));
        let resp = handle_message(&msg).expect("expected a response");
        let tools = resp["result"]["tools"].as_array().expect("expected array");
        let names: Vec<&str> = tools.iter().filter_map(|t| t["name"].as_str()).collect();
        for expected in &[
            "create_file",
            "move_file",
            "rename_symbol",
            "create_module",
            "add_leaf_to_template",
            "move_method",
        ] {
            assert!(names.contains(expected), "missing tool: {expected}");
        }
    }

    #[test]
    fn set_project_context_handles_invalid_path() {
        in_temp_repo(|repo| {
            let missing = repo.path.join("does-not-exist");
            let msg = req(
                31,
                "tools/call",
                json!({
                    "name": "set_project_context",
                    "arguments": {
                        "projectPath": missing.to_string_lossy().to_string()
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected a response");
            let result = &resp["result"];
            assert_eq!(result["isError"], true);
            let text = result["content"][0]["text"]
                .as_str()
                .expect("expected error text");
            assert!(text.contains("project path not found"));
        });
    }

    #[test]
    fn set_project_context_discovers_and_reloads_dynamic_tools() {
        in_temp_repo(|repo| {
            let project_a = repo.path.join("project-a");
            let project_b = repo.path.join("project-b");
            fs::create_dir_all(&project_a).expect("create project-a");
            fs::create_dir_all(&project_b).expect("create project-b");

            fs::write(
                project_a.join("Main.tlang"),
                r#"
use TLang.MCPTool
expose provide_tool

func run_alpha(args_json: String): String {
    return "alpha:" + args_json;
}

func provide_tool(): MCPTool {
    return MCPTool(
        name: "alpha_tool",
        description: "alpha dynamic tool",
        run: &run_alpha
    );
}
"#,
            )
            .expect("write project-a Main.tlang");

            fs::write(
                project_b.join("Main.tlang"),
                r#"
use TLang.MCPTool
expose provide_tool

func run_beta(args_json: String): String {
    return "beta:" + args_json;
}

func provide_tool(): MCPTool {
    return MCPTool(
        name: "beta_tool",
        description: "beta dynamic tool",
        run: &run_beta
    );
}
"#,
            )
            .expect("write project-b Main.tlang");

            let set_a = req(
                32,
                "tools/call",
                json!({
                    "name": "set_project_context",
                    "arguments": { "projectPath": project_a.to_string_lossy().to_string() }
                }),
            );
            let set_a_resp = handle_message(&set_a).expect("expected response");
            let set_a_text = set_a_resp["result"]["content"][0]["text"]
                .as_str()
                .unwrap_or("");
            assert!(
                set_a_text.contains("alpha_tool"),
                "expected alpha tool in response text, got: {set_a_text}"
            );

            let tools_a = req(33, "tools/list", json!({}));
            let tools_a_resp = handle_message(&tools_a).expect("expected response");
            let tools_a = tools_a_resp["result"]["tools"]
                .as_array()
                .expect("expected tools list");
            let names_a: Vec<&str> = tools_a.iter().filter_map(|t| t["name"].as_str()).collect();
            assert!(names_a.contains(&"alpha_tool"));
            assert!(!names_a.contains(&"beta_tool"));

            let call_alpha = req(
                34,
                "tools/call",
                json!({
                    "name": "alpha_tool",
                    "arguments": { "value": 1 }
                }),
            );
            let call_alpha_resp = handle_message(&call_alpha).expect("expected response");
            let call_alpha_text = call_alpha_resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected response text");
            assert!(call_alpha_text.contains(r#"alpha:{"value":1}"#));

            let set_b = req(
                35,
                "tools/call",
                json!({
                    "name": "set_project_context",
                    "arguments": { "projectPath": project_b.to_string_lossy().to_string() }
                }),
            );
            let set_b_resp = handle_message(&set_b).expect("expected response");
            assert!(
                set_b_resp["result"]["content"][0]["text"]
                    .as_str()
                    .unwrap_or("")
                    .contains("beta_tool")
            );

            let tools_b = req(36, "tools/list", json!({}));
            let tools_b_resp = handle_message(&tools_b).expect("expected response");
            let tools_b = tools_b_resp["result"]["tools"]
                .as_array()
                .expect("expected tools list");
            let names_b: Vec<&str> = tools_b.iter().filter_map(|t| t["name"].as_str()).collect();
            assert!(names_b.contains(&"beta_tool"));
            assert!(!names_b.contains(&"alpha_tool"));
        });
    }

    // ─── create_file tests ────────────────────────────────────────────────────

    #[test]
    fn create_file_creates_new_file() {
        in_temp_repo(|repo| {
            let msg = req(
                40,
                "tools/call",
                json!({
                    "name": "create_file",
                    "arguments": {
                        "path": "NewFile.tlang",
                        "content": "// hello"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            assert!(resp.get("error").is_none());
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(text.contains("Created"), "expected Created in response");
            let content =
                fs::read_to_string(repo.path.join("NewFile.tlang")).expect("file must exist");
            assert_eq!(content, "// hello");
        });
    }

    #[test]
    fn create_file_rejects_overwrite_without_flag() {
        in_temp_repo(|repo| {
            repo.write("Existing.tlang", "old content");
            let msg = req(
                41,
                "tools/call",
                json!({
                    "name": "create_file",
                    "arguments": {
                        "path": "Existing.tlang",
                        "content": "new content"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(
                text.contains("already exists"),
                "expected already-exists error: {text}"
            );
            // Original file must be unchanged.
            let content =
                fs::read_to_string(repo.path.join("Existing.tlang")).expect("file must exist");
            assert_eq!(content, "old content");
        });
    }

    #[test]
    fn create_file_overwrites_with_flag() {
        in_temp_repo(|repo| {
            repo.write("Existing.tlang", "old content");
            let msg = req(
                42,
                "tools/call",
                json!({
                    "name": "create_file",
                    "arguments": {
                        "path": "Existing.tlang",
                        "content": "new content",
                        "overwrite": true
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            assert!(resp.get("error").is_none());
            let content =
                fs::read_to_string(repo.path.join("Existing.tlang")).expect("file must exist");
            assert_eq!(content, "new content");
        });
    }

    #[test]
    fn create_file_creates_parent_directories() {
        in_temp_repo(|repo| {
            let msg = req(
                43,
                "tools/call",
                json!({
                    "name": "create_file",
                    "arguments": {
                        "path": "sub/dir/NewFile.tlang",
                        "content": "content"
                    }
                }),
            );
            handle_message(&msg).expect("expected response");
            assert!(repo.path.join("sub/dir/NewFile.tlang").exists());
        });
    }

    // ─── move_file tests ──────────────────────────────────────────────────────

    #[test]
    fn move_file_renames_file() {
        in_temp_repo(|repo| {
            repo.write("OldName.tlang", "content");
            let msg = req(
                50,
                "tools/call",
                json!({
                    "name": "move_file",
                    "arguments": {
                        "source": "OldName.tlang",
                        "destination": "NewName.tlang"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            assert!(resp.get("error").is_none());
            assert!(!repo.path.join("OldName.tlang").exists());
            assert!(repo.path.join("NewName.tlang").exists());
        });
    }

    #[test]
    fn move_file_rejects_overwrite_without_flag() {
        in_temp_repo(|repo| {
            repo.write("A.tlang", "content A");
            repo.write("B.tlang", "content B");
            let msg = req(
                51,
                "tools/call",
                json!({
                    "name": "move_file",
                    "arguments": {
                        "source": "A.tlang",
                        "destination": "B.tlang"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(
                text.contains("already exists"),
                "expected already-exists error: {text}"
            );
        });
    }

    // ─── rename_symbol tests ──────────────────────────────────────────────────

    #[test]
    fn rename_symbol_updates_references_across_files() {
        in_temp_repo(|repo| {
            repo.write(
                "Defn.tlang",
                "set TodoEntity() >> \"out\" { title: &title }\n",
            );
            repo.write(
                "Ref.tlang",
                "// uses TodoEntity here\nset Other() >> \"out\" { ref: &TodoEntity }\n",
            );

            let msg = req(
                60,
                "tools/call",
                json!({
                    "name": "rename_symbol",
                    "arguments": {
                        "symbol": "TodoEntity",
                        "new_name": "TaskEntity"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            assert!(resp.get("error").is_none());

            let defn = fs::read_to_string(repo.path.join("Defn.tlang")).expect("read Defn.tlang");
            assert!(defn.contains("TaskEntity"), "expected rename in definition");
            assert!(
                !defn.contains("TodoEntity"),
                "old name still present in definition"
            );

            let refr = fs::read_to_string(repo.path.join("Ref.tlang")).expect("read Ref.tlang");
            assert!(refr.contains("TaskEntity"), "expected rename in reference");
        });
    }

    #[test]
    fn rename_symbol_does_not_partial_match() {
        in_temp_repo(|repo| {
            repo.write(
                "File.tlang",
                "set Todo() >> \"out\" {}\nset TodoEntity() >> \"out\" {}\n",
            );

            let msg = req(
                61,
                "tools/call",
                json!({
                    "name": "rename_symbol",
                    "arguments": {
                        "symbol": "Todo",
                        "new_name": "Task"
                    }
                }),
            );
            handle_message(&msg).expect("expected response");

            let content =
                fs::read_to_string(repo.path.join("File.tlang")).expect("read File.tlang");
            assert!(
                content.contains("TodoEntity"),
                "TodoEntity should not be renamed when renaming Todo"
            );
            assert!(content.contains("Task()"), "Todo should be renamed to Task");
        });
    }

    // ─── create_module tests ──────────────────────────────────────────────────

    #[test]
    fn create_module_creates_tlang_file_with_boilerplate() {
        in_temp_repo(|repo| {
            let msg = req(
                70,
                "tools/call",
                json!({
                    "name": "create_module",
                    "arguments": {
                        "name": "MyModule"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            assert!(resp.get("error").is_none());
            let path = repo.path.join("MyModule.tlang");
            assert!(path.exists(), "module file must be created");
            let content = fs::read_to_string(&path).expect("read module file");
            assert!(
                content.contains("model"),
                "boilerplate must include model block"
            );
            assert!(
                content.contains("helper"),
                "boilerplate must include helper block"
            );
        });
    }

    #[test]
    fn create_module_rejects_existing_file_without_overwrite() {
        in_temp_repo(|repo| {
            repo.write("Existing.tlang", "old");
            let msg = req(
                71,
                "tools/call",
                json!({
                    "name": "create_module",
                    "arguments": {
                        "name": "Existing"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(
                text.contains("already exists"),
                "expected already-exists error: {text}"
            );
        });
    }

    // ─── add_leaf_to_template tests ───────────────────────────────────────────

    #[test]
    fn add_leaf_inserts_new_attribute_into_set_entity() {
        in_temp_repo(|repo| {
            repo.write(
                "Model.tlang",
                "model {\n    set TodoEntity() >> \"out\" {\n        title: &title\n    }\n}\n",
            );

            let msg = req(
                80,
                "tools/call",
                json!({
                    "name": "add_leaf_to_template",
                    "arguments": {
                        "path": "Model.tlang",
                        "set_name": "TodoEntity",
                        "leaf_name": "description",
                        "leaf_ref": "description"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            assert!(resp.get("error").is_none());
            let content =
                fs::read_to_string(repo.path.join("Model.tlang")).expect("read model file");
            assert!(
                content.contains("description: &description"),
                "new leaf must be present: {content}"
            );
        });
    }

    #[test]
    fn add_leaf_errors_when_set_not_found() {
        in_temp_repo(|repo| {
            repo.write("Model.tlang", "model {\n    set Other() >> \"out\" {}\n}\n");

            let msg = req(
                81,
                "tools/call",
                json!({
                    "name": "add_leaf_to_template",
                    "arguments": {
                        "path": "Model.tlang",
                        "set_name": "Missing",
                        "leaf_name": "x",
                        "leaf_ref": "x"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(
                text.contains("not found") || text.contains("Missing"),
                "expected not-found error: {text}"
            );
        });
    }

    // ─── move_method tests ────────────────────────────────────────────────────

    #[test]
    fn move_method_moves_func_between_files() {
        in_temp_repo(|repo| {
            repo.write(
                "Src.tlang",
                "helper {\n    func greet(): String {\n        return \"hello\";\n    }\n    func main(): String {\n        return \"main\";\n    }\n}\n",
            );
            repo.write(
                "Dst.tlang",
                "helper {\n    func other(): String {\n        return \"other\";\n    }\n}\n",
            );

            let msg = req(
                90,
                "tools/call",
                json!({
                    "name": "move_method",
                    "arguments": {
                        "func_name": "greet",
                        "source_path": "Src.tlang",
                        "dest_path": "Dst.tlang"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            assert!(resp.get("error").is_none());

            let src = fs::read_to_string(repo.path.join("Src.tlang")).expect("read Src.tlang");
            assert!(
                !src.contains("func greet"),
                "func must be removed from source"
            );

            let dst = fs::read_to_string(repo.path.join("Dst.tlang")).expect("read Dst.tlang");
            assert!(
                dst.contains("func greet"),
                "func must be present in destination"
            );
        });
    }

    #[test]
    fn move_method_errors_when_func_not_found() {
        in_temp_repo(|repo| {
            repo.write(
                "Src.tlang",
                "helper {\n    func main(): String { return \"x\"; }\n}\n",
            );
            repo.write(
                "Dst.tlang",
                "helper {\n    func other(): String { return \"y\"; }\n}\n",
            );

            let msg = req(
                91,
                "tools/call",
                json!({
                    "name": "move_method",
                    "arguments": {
                        "func_name": "missing",
                        "source_path": "Src.tlang",
                        "dest_path": "Dst.tlang"
                    }
                }),
            );
            let resp = handle_message(&msg).expect("expected response");
            let text = resp["result"]["content"][0]["text"]
                .as_str()
                .expect("expected string");
            assert!(
                text.contains("not found") || text.contains("missing"),
                "expected not-found error: {text}"
            );
        });
    }

    // ─── helper function unit tests ───────────────────────────────────────────

    #[test]
    fn word_boundary_replace_avoids_partial_matches() {
        let result = word_boundary_replace("set Todo() set TodoEntity()", "Todo", "Task");
        assert_eq!(result, "set Task() set TodoEntity()");
    }

    #[test]
    fn find_matching_close_brace_handles_nested() {
        let text = "{ outer { inner } }";
        let close = find_matching_close_brace(text, 0);
        assert_eq!(close, Some(text.len() - 1));
    }

    #[test]
    fn find_matching_close_brace_handles_string_literals() {
        let text = "{ \"this } is a string\" }";
        let close = find_matching_close_brace(text, 0);
        assert_eq!(close, Some(text.len() - 1));
    }

    #[test]
    fn extract_func_and_strip_func_roundtrip() {
        let src = "helper {\n    func foo(): String {\n        return \"x\";\n    }\n    func bar(): String {\n        return \"y\";\n    }\n}\n";
        let func_text = extract_func(src, "foo").expect("extract foo");
        assert!(func_text.contains("func foo"));
        let stripped = strip_func(src, &func_text);
        assert!(!stripped.contains("func foo"));
        assert!(stripped.contains("func bar"));
    }

    #[test]
    fn insert_func_into_helper_inserts_before_close_brace() {
        let src = "helper {\n    func bar(): String { return \"y\"; }\n}\n";
        let func_text = "    func foo(): String { return \"x\"; }\n";
        let result = insert_func_into_helper(src, func_text).expect("insert");
        assert!(result.contains("func foo"));
        assert!(result.contains("func bar"));
        // foo must appear before the final `}`
        let foo_pos = result.find("func foo").unwrap();
        let close_pos = result.rfind('}').unwrap();
        assert!(foo_pos < close_pos);
    }
}
