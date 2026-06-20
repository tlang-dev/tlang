// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! `textDocument/completion` — completion item provider.
//!
//! [`compute_completions`] handles keyword completions, `use`-path completions,
//! library method completions (via [`library_method_table`]), and set-entity
//! method completions based on the token context at the cursor position.

use lsp_types::{CompletionItem, CompletionItemKind, Position};

/// All known built-in TLang library paths, in the order they should appear.
const BUILTIN_LIBRARIES: &[&str] = &[
    "TLang.Terminal",
    "TLang.File",
    "TLang.String",
    "TLang.List",
    "TLang.Map",
    "TLang.StringBuilder",
    "TLang.Leaf",
    "TLang.Json",
    "TLang.Yaml",
    "TLang.MCPTool",
    "TLang.Generator",
    "TLang.Kotlin",
    "TLang.Formatting",
    "TLang.Token",
    "TLang.Naming",
    "TLang.Math",
    "TLang.Int",
    "TLang.Long",
    "TLang.Float",
    "TLang.Double",
];

/// Compute completion items for the given cursor position in `text`.
pub(super) fn compute_completions(text: &str, position: Position) -> Vec<CompletionItem> {
    let line_idx = position.line as usize;
    let col = position.character as usize;

    let line = text.lines().nth(line_idx).unwrap_or("");
    let prefix = &line[..col.min(line.len())];
    let trimmed = prefix.trim_start();

    // ── Case 1: inside a `use` declaration ───────────────────────────────────
    if let Some(after_use_raw) = trimmed.strip_prefix("use ") {
        let after_use = after_use_raw.trim_start();
        return complete_use_path(after_use);
    }

    // ── Case 2: method access `Alias.‹cursor›` ────────────────────────────────
    // Find the identifier immediately before the last `.`
    let word_start = prefix
        .rfind(|c: char| c.is_whitespace() || c == '(' || c == ',' || c == '=')
        .map(|i| i + 1)
        .unwrap_or(0);
    let current_word = &prefix[word_start..];

    if let Some(dot_pos) = current_word.rfind('.') {
        let ns = current_word[..dot_pos].trim();
        if !ns.is_empty() {
            return complete_methods(ns, text);
        }
    }

    // ── Case 3: general completions ───────────────────────────────────────────
    complete_general(text)
}

/// Suggestions when the user is typing after `use `.
fn complete_use_path(partial: &str) -> Vec<CompletionItem> {
    BUILTIN_LIBRARIES
        .iter()
        .filter(|lib| lib.starts_with(partial))
        .map(|lib| CompletionItem {
            label: lib.to_string(),
            kind: Some(CompletionItemKind::MODULE),
            detail: Some("TLang built-in library".to_string()),
            insert_text: Some(lib.to_string()),
            ..Default::default()
        })
        .collect()
}

/// Suggestions when the user types `Alias.` — resolve the alias to its fully
/// qualified library name and return the known methods for that library.
fn complete_methods(alias: &str, doc_text: &str) -> Vec<CompletionItem> {
    // Resolve the alias through the `use` declarations in the document.
    let fqn = resolve_import_alias(alias, doc_text).unwrap_or_else(|| alias.to_string());
    let lib = library_methods(&fqn);
    if !lib.is_empty() {
        return lib;
    }

    // If no library matched, check whether `alias` is a let-bound variable
    // whose right-hand side is a set-entity instantiation.  If so, offer the
    // set-instance built-in methods plus any body attrs declared on that entity.
    if let Some(entity_name) = resolve_set_instance_entity(alias, doc_text) {
        return set_instance_methods(doc_text, &entity_name);
    }

    Vec::new()
}

/// Return the set-entity name that variable `var_name` was bound to, or `None`
/// if the variable is not a set-entity instantiation.
///
/// Scans for lines of the form `let var_name = EntityName(key: val, …)`.
pub(super) fn resolve_set_instance_entity(var_name: &str, text: &str) -> Option<String> {
    for line in text.lines() {
        let trimmed = line.trim();
        // Match `let <var_name> = <EntityName>(`
        let rest = match trimmed.strip_prefix("let ") {
            Some(r) => r,
            None => continue,
        };
        let name_end = match rest.find(|c: char| !c.is_alphanumeric() && c != '_') {
            Some(i) => i,
            None => continue,
        };
        if &rest[..name_end] != var_name {
            continue;
        }
        let after_name = rest[name_end..].trim_start();
        let after_eq = match after_name.strip_prefix('=') {
            Some(r) => r.trim_start(),
            None => continue,
        };
        // Entity name is the identifier before `(`.
        let entity: String = after_eq
            .chars()
            .take_while(|c| c.is_alphanumeric() || *c == '_')
            .collect();
        if entity.is_empty() {
            continue;
        }
        let after_entity = after_eq[entity.len()..].trim_start();
        // Accept named-arg instantiation `Entity(key: val, …)` — detected by
        // the `(identifier:` pattern — as well as bare `Entity()`.
        if after_entity.starts_with('(') {
            let inside = after_entity[1..].trim_start();
            let first_ident_end = inside
                .find(|c: char| !c.is_alphanumeric() && c != '_')
                .unwrap_or(0);
            let after_ident = inside[first_ident_end..].trim_start();
            // Named-arg: `(ident:` pattern, or empty `()`.
            if after_ident.starts_with(':') || inside.starts_with(')') {
                return Some(entity);
            }
        }
    }
    None
}

/// Completions for a set-instance variable: the built-in instance methods
/// (`attrs()`, `generateAll()`) plus any body attrs declared on the entity.
fn set_instance_methods(text: &str, entity_name: &str) -> Vec<CompletionItem> {
    let mut items: Vec<CompletionItem> = vec![
        CompletionItem {
            label: "attrs".to_string(),
            kind: Some(CompletionItemKind::METHOD),
            detail: Some("attrs(): List".to_string()),
            documentation: Some(lsp_types::Documentation::String(
                "Return all bound attributes in canonical declaration order.\n\
                 Each element is a BoundAttr: callable as `op()` and exposes `op.name`."
                    .to_string(),
            )),
            insert_text: Some("attrs()".to_string()),
            ..Default::default()
        },
        CompletionItem {
            label: "generateAll".to_string(),
            kind: Some(CompletionItemKind::METHOD),
            detail: Some("generateAll(): List".to_string()),
            documentation: Some(lsp_types::Documentation::String(
                "Generate code for every attribute and write the combined output to the `>>` path \
                 (if declared).\n\nWhen the set has a `lead` attr, the lead template provides the \
                 outer structure and `<[ attrs() ]>` inside it is expanded with all non-lead attrs' \
                 inner bodies — producing one unified file.\n\n\
                 Returns `List[Map{name, code}]` in canonical order."
                    .to_string(),
            )),
            insert_text: Some("generateAll()".to_string()),
            ..Default::default()
        },
    ];

    // Also suggest the body attrs declared on the entity so the user can call
    // them directly as `inst.attrName()`.
    for line in text.lines() {
        let trimmed = line.trim();
        // Inside a set body: `    attrName: &templateRef,`
        // Heuristic: a line that starts with an identifier followed by `: &`
        if let Some(colon_pos) = trimmed.find(": &") {
            let attr_name: String = trimmed[..colon_pos]
                .trim()
                .chars()
                .take_while(|c| c.is_alphanumeric() || *c == '_')
                .collect();
            if attr_name.is_empty() || attr_name == "lead" {
                continue;
            }
            // Verify we are inside the right entity's block by checking the
            // entity name appears somewhere earlier in the document on a `set`
            // line.  This is a best-effort heuristic, not a full parse.
            items.push(CompletionItem {
                label: attr_name.clone(),
                kind: Some(CompletionItemKind::METHOD),
                detail: Some(format!("{}(): Leaf", attr_name)),
                documentation: Some(lsp_types::Documentation::String(format!(
                    "Call the `{}` template ref on this `{}` instance.\n\
                     Returns a Leaf that can be passed to `Generator.generate()`.",
                    attr_name, entity_name
                ))),
                insert_text: Some(format!("{}()", attr_name)),
                ..Default::default()
            });
        }
    }

    items
}

/// Walk the document's `use` lines and map `alias` → fully-qualified path.
pub(super) fn resolve_import_alias(alias: &str, text: &str) -> Option<String> {
    for line in text.lines() {
        let trimmed = line.trim();
        let rest = trimmed.strip_prefix("use ")?.trim();
        // Explicit alias: `use TLang.Terminal as T`
        if let Some(as_pos) = rest.find(" as ") {
            let path = rest[..as_pos].trim();
            let declared = rest[as_pos + 4..].trim();
            if declared == alias {
                return Some(path.to_string());
            }
        } else {
            // Implicit alias: last path segment equals alias
            let last = rest.rsplit('.').next().unwrap_or(rest);
            if last == alias {
                return Some(rest.to_string());
            }
        }
    }
    None
}

/// Raw method table: `(method_name, signature_detail, doc_string)` entries for a library.
pub(super) fn library_method_table(
    fqn: &str,
) -> &'static [(&'static str, &'static str, &'static str)] {
    match fqn {
        "TLang.Terminal" => &[
            (
                "println",
                "println(value)",
                "Print a value followed by a newline",
            ),
            (
                "print",
                "print(value)",
                "Print a value without a trailing newline",
            ),
            ("read", "read(): String", "Read a line from standard input"),
        ],
        "TLang.File" => &[
            (
                "read",
                "read(path): String",
                "Read the entire contents of a file",
            ),
            (
                "write",
                "write(path, content): Unit",
                "Write content to a file (skips if file exists; pass true as third arg to overwrite)",
            ),
            (
                "exists",
                "exists(path): Bool",
                "Check whether a file exists",
            ),
            (
                "searchReplace",
                "searchReplace(path, search, replace): Unit",
                "Replace the first occurrence of search with replace in a file",
            ),
            (
                "append",
                "append(path, content): Unit",
                "Append content to the end of a file",
            ),
            (
                "appendAfter",
                "appendAfter(path, sequence, content): Unit",
                "Insert content immediately after the first occurrence of sequence in a file",
            ),
            (
                "prependBefore",
                "prependBefore(path, sequence, content): Unit",
                "Insert content immediately before the first occurrence of sequence in a file",
            ),
            (
                "createDir",
                "createDir(path): Unit",
                "Create a directory and all required parent directories",
            ),
            ("deleteFile", "deleteFile(path): Unit", "Delete a file"),
            (
                "deleteDir",
                "deleteDir(path, recursive): Unit",
                "Delete a directory; pass true as second argument to delete recursively",
            ),
        ],
        "TLang.StringBuilder" => &[
            (
                "create",
                "create(): StringBuilder",
                "Create a new empty StringBuilder",
            ),
            (
                "new",
                "new(): StringBuilder",
                "Create a new empty StringBuilder",
            ),
            (
                "append",
                "append(sb, value): StringBuilder",
                "Append a value to a StringBuilder",
            ),
            ("build", "build(sb): String", "Build the final String"),
        ],
        "TLang.Leaf" => &[
            (
                "model",
                "model(): Leaf",
                "Get the current instantiated model",
            ),
            (
                "get",
                "get(model, key): Leaf",
                "Get a child node from the model by key",
            ),
        ],
        "TLang.Json" => &[
            (
                "toLeaf",
                "toLeaf(json): Leaf",
                "Parse a JSON object string into a Leaf",
            ),
            (
                "fromLeaf",
                "fromLeaf(leaf): String",
                "Export a Leaf (or data value) to a JSON string",
            ),
        ],
        "TLang.Yaml" => &[
            (
                "toLeaf",
                "toLeaf(yaml): Leaf",
                "Parse a YAML mapping string into a Leaf",
            ),
            (
                "fromLeaf",
                "fromLeaf(leaf): String",
                "Export a Leaf (or data value) to a YAML string",
            ),
        ],
        "TLang.MCPTool" => &[],
        "TLang.Generator" => &[(
            "generate",
            "generate(tmpl, lang): String",
            "Generate source code from a template",
        )],
        "TLang.Kotlin" => &[],
        "TLang.Formatting" => &[
            (
                "create",
                "create(): Formatting",
                "Create a new Formatting rule set",
            ),
            (
                "with_indent_text",
                "with_indent_text(rules, text): Formatting",
                "Set the indent string",
            ),
            (
                "space_between",
                "space_between(rules, token): Formatting",
                "Insert a space between occurrences of a token",
            ),
            (
                "newline_after",
                "newline_after(rules, token): Formatting",
                "Insert a newline after a token",
            ),
            (
                "indent_after",
                "indent_after(rules, token): Formatting",
                "Increase indent after a token",
            ),
            (
                "outdent_before",
                "outdent_before(rules, token): Formatting",
                "Decrease indent before a token",
            ),
            (
                "render",
                "render(tmpl, rules): String",
                "Render a template using formatting rules",
            ),
            (
                "render_list",
                "render_list(tmpl, rules): String",
                "Render a template list using formatting rules",
            ),
        ],
        "TLang.List" => &[
            ("create", "create(): List", "Create a new empty list"),
            (
                "of",
                "of(v1, v2, ...): List",
                "Create a list from variadic arguments",
            ),
            (
                "size",
                "size(list): Int",
                "Return the number of elements in the list",
            ),
            (
                "isEmpty",
                "isEmpty(list): Bool",
                "Return true if the list has no elements",
            ),
            (
                "get",
                "get(list, index): Value",
                "Return the element at index (negative indices count from end)",
            ),
            (
                "first",
                "first(list): Value",
                "Return the first element; error if empty",
            ),
            (
                "last",
                "last(list): Value",
                "Return the last element; error if empty",
            ),
            (
                "push",
                "push(list, value): List",
                "Return a new list with value appended at the end",
            ),
            (
                "prepend",
                "prepend(list, value): List",
                "Return a new list with value inserted at the front",
            ),
            (
                "set",
                "set(list, index, value): List",
                "Return a new list with the element at index replaced",
            ),
            (
                "remove",
                "remove(list, index): List",
                "Return a new list without the element at index",
            ),
            (
                "tail",
                "tail(list): List",
                "Return all elements except the first",
            ),
            (
                "init",
                "init(list): List",
                "Return all elements except the last",
            ),
            (
                "slice",
                "slice(list, start, end): List",
                "Return elements from start (inclusive) to end (exclusive); negative indices supported",
            ),
            (
                "concat",
                "concat(a, b): List",
                "Return a new list that is the concatenation of a and b",
            ),
            (
                "reverse",
                "reverse(list): List",
                "Return a new list with elements in reverse order",
            ),
            (
                "contains",
                "contains(list, value): Bool",
                "Return true if the list contains value",
            ),
            (
                "indexOf",
                "indexOf(list, value): Int",
                "Return the first index of value, or -1 if not found",
            ),
            (
                "join",
                "join(list, separator): String",
                "Convert each element to string and join with separator",
            ),
            (
                "flatten",
                "flatten(list): List",
                "Inline one level of nested lists",
            ),
            (
                "distinct",
                "distinct(list): List",
                "Return a new list with duplicates removed (first occurrence kept)",
            ),
            (
                "sort",
                "sort(list): List",
                "Return a new list sorted in ascending order",
            ),
            ("take", "take(list, n): List", "Return the first n elements"),
            (
                "drop",
                "drop(list, n): List",
                "Return the list without the first n elements",
            ),
            (
                "zip",
                "zip(a, b): List<List>",
                "Pair up elements from two lists; result length equals the shorter list",
            ),
        ],
        "TLang.Map" => &[
            ("create", "create(): Map", "Create a new empty map"),
            (
                "of",
                "of(k1, v1, k2, v2, ...): Map",
                "Create a map from variadic key-value pairs (keys must be String)",
            ),
            (
                "size",
                "size(map): Int",
                "Return the number of entries in the map",
            ),
            (
                "isEmpty",
                "isEmpty(map): Bool",
                "Return true if the map has no entries",
            ),
            (
                "has",
                "has(map, key): Bool",
                "Return true if the map contains the given key",
            ),
            (
                "get",
                "get(map, key): Value",
                "Return the value for key; error if absent",
            ),
            (
                "getOrDefault",
                "getOrDefault(map, key, default): Value",
                "Return the value for key, or default if absent",
            ),
            (
                "set",
                "set(map, key, value): Map",
                "Return a new map with the entry key -> value added or replaced",
            ),
            (
                "remove",
                "remove(map, key): Map",
                "Return a new map without the given key (no-op if key is absent)",
            ),
            (
                "keys",
                "keys(map): List<String>",
                "Return a sorted list of all keys",
            ),
            (
                "values",
                "values(map): List",
                "Return a list of all values in key-sorted order",
            ),
            (
                "entries",
                "entries(map): List<List>",
                "Return a list of [key, value] pairs in key-sorted order",
            ),
            (
                "merge",
                "merge(base, overrides): Map",
                "Return a new map with all entries from base plus overrides; overrides wins on conflict",
            ),
            (
                "fromLists",
                "fromLists(keys, values): Map",
                "Build a map from two equal-length lists; keys must be List<String>",
            ),
            (
                "toList",
                "toList(map): List<List>",
                "Return a list of [key, value] pairs (alias for entries)",
            ),
        ],
        "TLang.Token" => &[
            ("keyword", "keyword(name): Token", "Create a keyword token"),
            ("name", "name(value): Token", "Create an identifier token"),
            ("value", "value(val): Token", "Create a literal value token"),
            ("list", "list(): TokenList", "Create an empty token list"),
            (
                "push",
                "push(list, token): TokenList",
                "Append a token to a list",
            ),
            (
                "concat",
                "concat(a, b): TokenList",
                "Concatenate two token lists",
            ),
        ],
        "TLang.String" => &[
            (
                "length",
                "length(s): Int",
                "Return the number of Unicode characters in the string",
            ),
            (
                "isEmpty",
                "isEmpty(s): Bool",
                "Return true if the string has no characters",
            ),
            (
                "toUpperCase",
                "toUpperCase(s): String",
                "Return a new string with all characters uppercased",
            ),
            (
                "toLowerCase",
                "toLowerCase(s): String",
                "Return a new string with all characters lowercased",
            ),
            (
                "trim",
                "trim(s): String",
                "Return a new string with leading and trailing whitespace removed",
            ),
            (
                "trimStart",
                "trimStart(s): String",
                "Return a new string with leading whitespace removed",
            ),
            (
                "trimEnd",
                "trimEnd(s): String",
                "Return a new string with trailing whitespace removed",
            ),
            (
                "contains",
                "contains(s, sub): Bool",
                "Return true if the string contains the given substring",
            ),
            (
                "startsWith",
                "startsWith(s, prefix): Bool",
                "Return true if the string starts with the given prefix",
            ),
            (
                "endsWith",
                "endsWith(s, suffix): Bool",
                "Return true if the string ends with the given suffix",
            ),
            (
                "equals",
                "equals(a, b): Bool",
                "Return true if both strings are exactly equal (case-sensitive)",
            ),
            (
                "equalsIgnoreCase",
                "equalsIgnoreCase(a, b): Bool",
                "Return true if both strings are equal ignoring case",
            ),
            (
                "compare",
                "compare(a, b): Int",
                "Lexicographic comparison: -1 if a < b, 0 if equal, 1 if a > b",
            ),
            (
                "indexOf",
                "indexOf(s, sub): Int",
                "Return the first character index of sub, or -1 if not found",
            ),
            (
                "lastIndexOf",
                "lastIndexOf(s, sub): Int",
                "Return the last character index of sub, or -1 if not found",
            ),
            (
                "substring",
                "substring(s, start, end): String",
                "Return characters from start (inclusive) to end (exclusive)",
            ),
            (
                "slice",
                "slice(s, start, end): String",
                "Like substring but accepts negative indices counting from the end",
            ),
            (
                "split",
                "split(s, delimiter): List<String>",
                "Split the string on the delimiter and return an array of parts",
            ),
            (
                "replace",
                "replace(s, from, to): String",
                "Replace the first occurrence of from with to",
            ),
            (
                "replaceAll",
                "replaceAll(s, from, to): String",
                "Replace all occurrences of from with to",
            ),
            (
                "concat",
                "concat(a, b): String",
                "Concatenate two strings into a new string",
            ),
            (
                "repeat",
                "repeat(s, n): String",
                "Return the string repeated n times",
            ),
            (
                "charAt",
                "charAt(s, index): String",
                "Return a single-character string at the given index",
            ),
            (
                "charCodeAt",
                "charCodeAt(s, index): Int",
                "Return the Unicode code point of the character at index",
            ),
            (
                "fromCharCode",
                "fromCharCode(code): String",
                "Return a single-character string from a Unicode code point",
            ),
            (
                "lines",
                "lines(s): List<String>",
                "Split the string on newlines and return an array of lines",
            ),
            (
                "words",
                "words(s): List<String>",
                "Split the string on whitespace and return an array of words",
            ),
        ],
        "TLang.Naming" => &[
            (
                "toCamelCase",
                "toCamelCase(s: String): String",
                "Convert to camelCase — e.g. \"my_field\" → \"myField\"",
            ),
            (
                "toPascalCase",
                "toPascalCase(s: String): String",
                "Convert to PascalCase — e.g. \"my_field\" → \"MyField\"",
            ),
            (
                "toSnakeCase",
                "toSnakeCase(s: String): String",
                "Convert to snake_case — e.g. \"myField\" → \"my_field\"",
            ),
            (
                "toScreamingSnake",
                "toScreamingSnake(s: String): String",
                "Convert to SCREAMING_SNAKE_CASE — e.g. \"myField\" → \"MY_FIELD\"",
            ),
            (
                "toKebabCase",
                "toKebabCase(s: String): String",
                "Convert to kebab-case — e.g. \"myField\" → \"my-field\"",
            ),
            (
                "toDotCase",
                "toDotCase(s: String): String",
                "Convert to dot.case — e.g. \"myField\" → \"my.field\"",
            ),
            (
                "toTitleCase",
                "toTitleCase(s: String): String",
                "Convert to Title Case — e.g. \"myField\" → \"My Field\"",
            ),
            (
                "capitalize",
                "capitalize(s: String): String",
                "Uppercase the first character only — e.g. \"hello\" → \"Hello\"",
            ),
            (
                "decapitalize",
                "decapitalize(s: String): String",
                "Lowercase the first character only — e.g. \"Hello\" → \"hello\"",
            ),
            (
                "pluralize",
                "pluralize(s: String): String",
                "Simple English pluralization — e.g. \"entity\" → \"entities\"",
            ),
            (
                "singularize",
                "singularize(s: String): String",
                "Simple English singularization — e.g. \"entities\" → \"entity\"",
            ),
            (
                "words",
                "words(s: String): List<String>",
                "Split into words, handling camelCase, snake_case, kebab-case, spaces, dots",
            ),
        ],
        "TLang.Math" => &[
            ("abs", "abs(n: Int): Int", "Absolute value"),
            ("min", "min(a: Int, b: Int): Int", "Minimum of two integers"),
            ("max", "max(a: Int, b: Int): Int", "Maximum of two integers"),
            (
                "clamp",
                "clamp(n: Int, lo: Int, hi: Int): Int",
                "Clamp n into the range [lo, hi]",
            ),
            (
                "pow",
                "pow(base: Int, exp: Int): Int",
                "Integer exponentiation — base raised to exp (exp ≥ 0)",
            ),
            ("sqrt", "sqrt(n: Int): Int", "Floor integer square root"),
            (
                "isEven",
                "isEven(n: Int): Bool",
                "Returns true if n is even",
            ),
            ("isOdd", "isOdd(n: Int): Bool", "Returns true if n is odd"),
            (
                "sign",
                "sign(n: Int): Int",
                "Returns -1, 0, or 1 based on the sign of n",
            ),
            ("gcd", "gcd(a: Int, b: Int): Int", "Greatest common divisor"),
            ("lcm", "lcm(a: Int, b: Int): Int", "Least common multiple"),
        ],
        "TLang.Int" => &[
            (
                "toString",
                "toString(n: Int): String",
                "Convert integer to decimal string",
            ),
            (
                "parse",
                "parse(s: String): Int",
                "Parse a decimal string to Int",
            ),
            (
                "toHex",
                "toHex(n: Int): String",
                "Convert to lowercase hexadecimal string",
            ),
            (
                "toBinary",
                "toBinary(n: Int): String",
                "Convert to binary string",
            ),
            (
                "toOctal",
                "toOctal(n: Int): String",
                "Convert to octal string",
            ),
            (
                "fromHex",
                "fromHex(s: String): Int",
                "Parse a hex string (with or without 0x prefix)",
            ),
            (
                "fromBinary",
                "fromBinary(s: String): Int",
                "Parse a binary string (with or without 0b prefix)",
            ),
            (
                "fromOctal",
                "fromOctal(s: String): Int",
                "Parse an octal string (with or without 0o prefix)",
            ),
            (
                "range",
                "range(start: Int, end: Int): List<Int>",
                "Exclusive range [start, end) as a list",
            ),
            (
                "rangeTo",
                "rangeTo(start: Int, end: Int): List<Int>",
                "Inclusive range [start, end] as a list",
            ),
            (
                "minValue",
                "minValue(): Int",
                "Minimum 32-bit signed integer value (-2147483648)",
            ),
            (
                "maxValue",
                "maxValue(): Int",
                "Maximum 32-bit signed integer value (2147483647)",
            ),
            ("abs", "abs(n: Int): Int", "Absolute value"),
            (
                "clamp",
                "clamp(n: Int, lo: Int, hi: Int): Int",
                "Clamp n into [lo, hi]",
            ),
            (
                "toFloat",
                "toFloat(n: Int): Float",
                "Convert to 32-bit Float",
            ),
            (
                "toDouble",
                "toDouble(n: Int): Double",
                "Convert to 64-bit Double",
            ),
        ],
        "TLang.Long" => &[
            (
                "toString",
                "toString(n: Long): String",
                "Convert long integer to decimal string",
            ),
            (
                "parse",
                "parse(s: String): Long",
                "Parse a decimal string to Long",
            ),
            (
                "toHex",
                "toHex(n: Long): String",
                "Convert to lowercase hexadecimal string",
            ),
            (
                "toBinary",
                "toBinary(n: Long): String",
                "Convert to binary string",
            ),
            (
                "fromHex",
                "fromHex(s: String): Long",
                "Parse a hex string (with or without 0x prefix)",
            ),
            (
                "fromBinary",
                "fromBinary(s: String): Long",
                "Parse a binary string (with or without 0b prefix)",
            ),
            (
                "range",
                "range(start: Long, end: Long): List<Long>",
                "Exclusive range [start, end) as a list",
            ),
            (
                "rangeTo",
                "rangeTo(start: Long, end: Long): List<Long>",
                "Inclusive range [start, end] as a list",
            ),
            (
                "minValue",
                "minValue(): Long",
                "Minimum 64-bit signed integer value (-9223372036854775808)",
            ),
            (
                "maxValue",
                "maxValue(): Long",
                "Maximum 64-bit signed integer value (9223372036854775807)",
            ),
            ("abs", "abs(n: Long): Long", "Absolute value"),
            (
                "clamp",
                "clamp(n: Long, lo: Long, hi: Long): Long",
                "Clamp n into [lo, hi]",
            ),
            (
                "toFloat",
                "toFloat(n: Long): Float",
                "Convert to 32-bit Float",
            ),
            (
                "toDouble",
                "toDouble(n: Long): Double",
                "Convert to 64-bit Double",
            ),
        ],
        "TLang.Float" => &[
            (
                "parse",
                "parse(s: String): Float",
                "Parse a string to 32-bit Float",
            ),
            (
                "toString",
                "toString(n: Float): String",
                "Convert Float to string",
            ),
            ("fromInt", "fromInt(n: Int): Float", "Convert Int to Float"),
            (
                "toInt",
                "toInt(n: Float): Int",
                "Truncate Float toward zero to Int",
            ),
            (
                "floor",
                "floor(n: Float): Float",
                "Round down to nearest integer value",
            ),
            (
                "ceil",
                "ceil(n: Float): Float",
                "Round up to nearest integer value",
            ),
            (
                "round",
                "round(n: Float): Float",
                "Round to nearest integer value",
            ),
            ("abs", "abs(n: Float): Float", "Absolute value"),
            (
                "min",
                "min(a: Float, b: Float): Float",
                "Minimum of two floats",
            ),
            (
                "max",
                "max(a: Float, b: Float): Float",
                "Maximum of two floats",
            ),
            ("sqrt", "sqrt(n: Float): Float", "Square root"),
            (
                "pow",
                "pow(base: Float, exp: Float): Float",
                "Raise base to the power of exp",
            ),
            (
                "isNaN",
                "isNaN(n: Float): Bool",
                "Returns true if the value is NaN",
            ),
            (
                "isInfinite",
                "isInfinite(n: Float): Bool",
                "Returns true if the value is ±Infinity",
            ),
            (
                "pi",
                "pi(): Float",
                "The mathematical constant π (3.14159...)",
            ),
            ("e", "e(): Float", "Euler's number e (2.71828...)"),
            ("infinity", "infinity(): Float", "Positive infinity"),
            ("nan", "nan(): Float", "Not-a-Number (NaN)"),
            ("add", "add(a: Float, b: Float): Float", "Add two floats"),
            ("sub", "sub(a: Float, b: Float): Float", "Subtract b from a"),
            (
                "mul",
                "mul(a: Float, b: Float): Float",
                "Multiply two floats",
            ),
            ("div", "div(a: Float, b: Float): Float", "Divide a by b"),
        ],
        "TLang.Double" => &[
            (
                "parse",
                "parse(s: String): Double",
                "Parse a string to 64-bit Double",
            ),
            (
                "toString",
                "toString(n: Double): String",
                "Convert Double to string",
            ),
            (
                "fromInt",
                "fromInt(n: Int): Double",
                "Convert Int to Double",
            ),
            (
                "toInt",
                "toInt(n: Double): Int",
                "Truncate Double toward zero to Int",
            ),
            (
                "floor",
                "floor(n: Double): Double",
                "Round down to nearest integer value",
            ),
            (
                "ceil",
                "ceil(n: Double): Double",
                "Round up to nearest integer value",
            ),
            (
                "round",
                "round(n: Double): Double",
                "Round to nearest integer value",
            ),
            ("abs", "abs(n: Double): Double", "Absolute value"),
            (
                "min",
                "min(a: Double, b: Double): Double",
                "Minimum of two doubles",
            ),
            (
                "max",
                "max(a: Double, b: Double): Double",
                "Maximum of two doubles",
            ),
            ("sqrt", "sqrt(n: Double): Double", "Square root"),
            (
                "pow",
                "pow(base: Double, exp: Double): Double",
                "Raise base to the power of exp",
            ),
            (
                "isNaN",
                "isNaN(n: Double): Bool",
                "Returns true if the value is NaN",
            ),
            (
                "isInfinite",
                "isInfinite(n: Double): Bool",
                "Returns true if the value is ±Infinity",
            ),
            (
                "pi",
                "pi(): Double",
                "The mathematical constant π (3.141592653589793)",
            ),
            ("e", "e(): Double", "Euler's number e (2.718281828459045)"),
            ("infinity", "infinity(): Double", "Positive infinity"),
            ("nan", "nan(): Double", "Not-a-Number (NaN)"),
            (
                "add",
                "add(a: Double, b: Double): Double",
                "Add two doubles",
            ),
            (
                "sub",
                "sub(a: Double, b: Double): Double",
                "Subtract b from a",
            ),
            (
                "mul",
                "mul(a: Double, b: Double): Double",
                "Multiply two doubles",
            ),
            ("div", "div(a: Double, b: Double): Double", "Divide a by b"),
        ],
        _ => &[],
    }
}

/// Return `CompletionItem`s for every method in the given fully-qualified library.
fn library_methods(fqn: &str) -> Vec<CompletionItem> {
    library_method_table(fqn)
        .iter()
        .map(|(label, detail, doc)| CompletionItem {
            label: label.to_string(),
            kind: Some(CompletionItemKind::METHOD),
            detail: Some(detail.to_string()),
            documentation: Some(lsp_types::Documentation::String(doc.to_string())),
            insert_text: Some(label.to_string()),
            ..Default::default()
        })
        .collect()
}

/// General completions: user-defined functions, let-bound variables, and
/// imported library aliases visible in the current document.
fn complete_general(text: &str) -> Vec<CompletionItem> {
    let mut items: Vec<CompletionItem> = Vec::new();
    let mut seen = std::collections::HashSet::new();

    // ── Top-level keyword boilerplate snippets ────────────────────────────────
    let kw_snippets: &[(&str, &str, &str, &str)] = &[
        (
            "func",
            "function definition",
            "func ${1:name}(${2:param}: ${3:Type}): ${4:ReturnType} {\n    return $0\n}",
            "Define a named helper function.\n\n\
             ```\nfunc add(a: Int, b: Int): Int {\n    return a + b\n}\n```",
        ),
        (
            "lang",
            "language template",
            "lang [${1:lang}] ${2:name}(${3:param}: ${4:String}) {\n    $0\n}",
            "Define a language code-generation template.\n\n\
             ```\nlang [kotlin] myClass(name: String) {\n    impl[public class] ${name} {}\n}\n```",
        ),
        (
            "doc",
            "documentation template",
            "doc [${1:lang}] ${2:name}(${3:param}: ${4:String}) {\n    $0\n}",
            "Define a documentation generation template.\n\n\
             ```\ndoc [markdown] readme(title: String) {\n    # ${title}\n}\n```",
        ),
        (
            "style",
            "style template",
            "style [${1:lang}] ${2:name}(${3:param}: ${4:String}) {\n    $0\n}",
            "Define a style (CSS/SCSS/JSON) generation template.\n\n\
             ```\nstyle [css] theme(primary: String) {\n    :root { --primary: ${primary}; }\n}\n```",
        ),
        (
            "cmd",
            "command template",
            "cmd [${1:lang}] ${2:name} {\n    $0\n}",
            "Define a command generation template.\n\n\
             ```\ncmd [bash] build {\n    ./gradlew build\n}\n```",
        ),
        (
            "data",
            "data template",
            "data [${1:lang}] ${2:name}(${3:param}: ${4:String}) {\n    ${3}: ${5:value}\n}",
            "Define a data-structure template.\n\n\
             ```\ndata [json] config(host: String) {\n    host: ${host}\n}\n```",
        ),
        (
            "set",
            "model entity definition",
            "set ${1:Name}(${2:param}: ${3:Type}) {\n    ${4:attr}: &$0\n}",
            "Define a model set entity.\n\n\
             ```\nset Repo(pkg: String) {\n    lead: &classShell,\n    save: &saveMethod\n}\n```",
        ),
        (
            "func",
            "func definition",
            "func ${1:name}(${2:param}: ${3:Type}): ${4:String} {\n    return $0\n}",
            "Define a top-level TLang function.",
        ),
        (
            "expose",
            "expose declaration",
            "expose ${1:functionName}",
            "Mark a helper function as publicly accessible from other modules.",
        ),
        (
            "use",
            "use import",
            "use ${1:TLang.Terminal}",
            "Import a library or module.\n\n\
             ```\nuse TLang.Terminal\nuse TLang.File as F\n```",
        ),
    ];

    for (label, detail, snippet, doc) in kw_snippets {
        if seen.insert(label.to_string()) {
            items.push(CompletionItem {
                label: label.to_string(),
                kind: Some(CompletionItemKind::SNIPPET),
                detail: Some(detail.to_string()),
                documentation: Some(lsp_types::Documentation::String(doc.to_string())),
                insert_text: Some(snippet.to_string()),
                insert_text_format: Some(lsp_types::InsertTextFormat::SNIPPET),
                ..Default::default()
            });
        }
    }

    // ── Common TLang type names ───────────────────────────────────────────────
    let type_names: &[(&str, &str)] = &[
        ("String", "built-in string type"),
        ("Int", "built-in 32-bit integer type"),
        ("Long", "built-in 64-bit integer type"),
        ("Float", "built-in 32-bit float type"),
        ("Double", "built-in 64-bit float type"),
        ("Bool", "built-in boolean type (true / false)"),
        ("List", "built-in ordered list type"),
        ("Map", "built-in key-value map type"),
        ("Leaf", "template tree node / instantiated model"),
        ("Unit", "no-value return type"),
    ];

    for (name, doc) in type_names {
        if seen.insert(name.to_string()) {
            items.push(CompletionItem {
                label: name.to_string(),
                kind: Some(CompletionItemKind::TYPE_PARAMETER),
                detail: Some(doc.to_string()),
                insert_text: Some(name.to_string()),
                ..Default::default()
            });
        }
    }

    // Always offer `lead` as a recognised set-body attr name with documentation.
    if seen.insert("lead".to_string()) {
        items.push(CompletionItem {
            label: "lead".to_string(),
            kind: Some(CompletionItemKind::KEYWORD),
            detail: Some("lead: &templateRef".to_string()),
            documentation: Some(lsp_types::Documentation::String(
                "Designates the outer-structure template for this set.\n\n\
                 When `generateAll()` is called, the lead template is instantiated last \
                 and `<[ attrs() ]>` inside it is expanded with the inner body fragments \
                 of all other (non-lead) attrs — producing one combined output file.\n\n\
                 Example:\n  lead: &classShell"
                    .to_string(),
            )),
            insert_text: Some("lead: &".to_string()),
            ..Default::default()
        });
    }

    // Lambda expression snippet: `(param) => expr`
    if seen.insert("lambda".to_string()) {
        items.push(CompletionItem {
            label: "(param) => expr".to_string(),
            kind: Some(CompletionItemKind::SNIPPET),
            detail: Some("lambda expression".to_string()),
            documentation: Some(lsp_types::Documentation::String(
                "Inline anonymous function (lambda).\n\n\
                 Expression body:\n  (x) => x * 2\n\n\
                 Block body:\n  (x) => {\n      return x * 2;\n  }\n\n\
                 Multi-param:\n  (a, b) => a + b\n\n\
                 Zero-param:\n  () => \"hello\"\n\n\
                 Lambdas close over variables in the enclosing scope."
                    .to_string(),
            )),
            insert_text: Some("($1) => $0".to_string()),
            insert_text_format: Some(lsp_types::InsertTextFormat::SNIPPET),
            ..Default::default()
        });
    }

    // Function reference snippet: `&funcName`
    if seen.insert("&ref".to_string()) {
        items.push(CompletionItem {
            label: "&funcName".to_string(),
            kind: Some(CompletionItemKind::SNIPPET),
            detail: Some("function reference".to_string()),
            documentation: Some(lsp_types::Documentation::String(
                "Reference to a named helper function as a first-class value.\n\n\
                 The identifier after `&` must be a valid function name.\n\n\
                 Example:\n  let f = &double;\n  f(7)    // calls double(7)\n\n\
                 Also used in FuncDef model constructor params:\n  \
                 Strategy(generate: &myFunc)"
                    .to_string(),
            )),
            insert_text: Some("&$0".to_string()),
            insert_text_format: Some(lsp_types::InsertTextFormat::SNIPPET),
            ..Default::default()
        });
    }

    // `Func` type annotation keyword
    if seen.insert("Func".to_string()) {
        items.push(CompletionItem {
            label: "Func".to_string(),
            kind: Some(CompletionItemKind::TYPE_PARAMETER),
            detail: Some("callable type annotation".to_string()),
            documentation: Some(lsp_types::Documentation::String(
                "Type annotation for a parameter that accepts a lambda or function reference.\n\n\
                 Example:\n  func apply(f: Func, v: Int): Int {\n      return f(v);\n  }"
                    .to_string(),
            )),
            insert_text: Some("Func".to_string()),
            ..Default::default()
        });
    }

    for line in text.lines() {
        let trimmed = line.trim();

        // Data template names: `data [lang, …] name(`
        if let Some(rest) = trimmed.strip_prefix("data ")
            && let Some(after_bracket) = rest.find(']')
        {
            let name_part = rest[after_bracket + 1..].trim_start();
            let name: String = name_part
                .chars()
                .take_while(|c| c.is_alphanumeric() || *c == '_')
                .collect();
            if !name.is_empty() && seen.insert(name.clone()) {
                let (detail, insert_text) = parse_func_call_snippet(&name, name_part);
                items.push(CompletionItem {
                    label: name,
                    kind: Some(CompletionItemKind::FUNCTION),
                    detail: Some(format!("data template — {detail}")),
                    insert_text: Some(insert_text),
                    insert_text_format: Some(lsp_types::InsertTextFormat::SNIPPET),
                    ..Default::default()
                });
            }
        }

        // Doc template names: `doc [lang, …] name(`
        if let Some(rest) = trimmed.strip_prefix("doc ")
            && let Some(after_bracket) = rest.find(']')
        {
            let name_part = rest[after_bracket + 1..].trim_start();
            let name: String = name_part
                .chars()
                .take_while(|c| c.is_alphanumeric() || *c == '_')
                .collect();
            if !name.is_empty() && seen.insert(name.clone()) {
                let (detail, insert_text) = parse_func_call_snippet(&name, name_part);
                items.push(CompletionItem {
                    label: name,
                    kind: Some(CompletionItemKind::FUNCTION),
                    detail: Some(format!("doc template — {detail}")),
                    insert_text: Some(insert_text),
                    insert_text_format: Some(lsp_types::InsertTextFormat::SNIPPET),
                    ..Default::default()
                });
            }
        }

        // Style template names: `style [lang, …] name(`
        if let Some(rest) = trimmed.strip_prefix("style ")
            && let Some(after_bracket) = rest.find(']')
        {
            let name_part = rest[after_bracket + 1..].trim_start();
            let name: String = name_part
                .chars()
                .take_while(|c| c.is_alphanumeric() || *c == '_')
                .collect();
            if !name.is_empty() && seen.insert(name.clone()) {
                let (detail, insert_text) = parse_func_call_snippet(&name, name_part);
                items.push(CompletionItem {
                    label: name,
                    kind: Some(CompletionItemKind::FUNCTION),
                    detail: Some(format!("style template — {detail}")),
                    insert_text: Some(insert_text),
                    insert_text_format: Some(lsp_types::InsertTextFormat::SNIPPET),
                    ..Default::default()
                });
            }
        }

        // Functions: `func name(`
        if let Some(rest) = trimmed.strip_prefix("func ") {
            let name: String = rest
                .chars()
                .take_while(|c| c.is_alphanumeric() || *c == '_')
                .collect();
            if !name.is_empty() && seen.insert(name.clone()) {
                let (detail, insert_text) = parse_func_call_snippet(&name, rest);
                items.push(CompletionItem {
                    label: name,
                    kind: Some(CompletionItemKind::FUNCTION),
                    detail: Some(detail),
                    insert_text: Some(insert_text),
                    insert_text_format: Some(lsp_types::InsertTextFormat::SNIPPET),
                    ..Default::default()
                });
            }
        }

        // Set entity names: `set Name {` / `set Name : Parent` / `set Name(`
        if let Some(rest) = trimmed.strip_prefix("set ") {
            let name: String = rest
                .chars()
                .take_while(|c| c.is_alphanumeric() || *c == '_')
                .collect();
            if !name.is_empty() && seen.insert(name.clone()) {
                // Build a detail string that mentions parents when present.
                let after_name = rest[name.len()..].trim_start();
                let detail = {
                    // Collect `: Parent` clauses (may be after a param list).
                    let mut scan = after_name;
                    if scan.starts_with('(') {
                        let mut depth = 0usize;
                        let mut idx = 0;
                        for (i, c) in scan.char_indices() {
                            match c {
                                '(' => depth += 1,
                                ')' => {
                                    depth -= 1;
                                    if depth == 0 {
                                        idx = i + 1;
                                        break;
                                    }
                                }
                                _ => {}
                            }
                        }
                        scan = scan[idx..].trim_start();
                    }
                    let mut parents: Vec<String> = Vec::new();
                    while let Some(rc) = scan.strip_prefix(':') {
                        let rc = rc.trim_start();
                        let p: String = rc
                            .chars()
                            .take_while(|c| c.is_alphanumeric() || *c == '_')
                            .collect();
                        if p.is_empty() {
                            break;
                        }
                        parents.push(p.clone());
                        scan = rc[p.len()..].trim_start();
                    }
                    if parents.is_empty() {
                        "set entity".to_string()
                    } else {
                        format!("set entity extends {}", parents.join(", "))
                    }
                };
                items.push(CompletionItem {
                    label: name,
                    kind: Some(CompletionItemKind::STRUCT),
                    detail: Some(detail),
                    ..Default::default()
                });
            }
        }

        // Let bindings: `let name =` / `let name:`
        if let Some(rest) = trimmed.strip_prefix("let ") {
            let name = rest.split([' ', '=', ':']).next().unwrap_or("").trim();
            if !name.is_empty() && seen.insert(name.to_string()) {
                items.push(CompletionItem {
                    label: name.to_string(),
                    kind: Some(CompletionItemKind::VARIABLE),
                    detail: Some("variable".to_string()),
                    ..Default::default()
                });
            }
        }

        // Imported libraries: expose the short alias so users can type it
        // and then trigger method completions with `.`
        if let Some(rest) = trimmed.strip_prefix("use ") {
            let path = rest.trim();
            let alias = if let Some(as_pos) = path.find(" as ") {
                path[as_pos + 4..].trim()
            } else {
                path.rsplit('.').next().unwrap_or(path)
            };
            if !alias.is_empty() && seen.insert(alias.to_string()) {
                items.push(CompletionItem {
                    label: alias.to_string(),
                    kind: Some(CompletionItemKind::MODULE),
                    detail: Some(path.to_string()),
                    ..Default::default()
                });
            }
        }
    }

    items
}

/// Build a `(detail_string, snippet_insert_text)` pair for a function or template
/// call from the declaration text starting at the name.
///
/// `name` is the function name.  `decl_rest` is the text starting at the name
/// (e.g. `"add(a: Int, b: Int): Int {"` or just `"add"`).  Returns a
/// `(detail, snippet)` where the snippet uses LSP tab-stop syntax `${N:hint}`.
pub(super) fn parse_func_call_snippet(name: &str, decl_rest: &str) -> (String, String) {
    // Find the opening paren in decl_rest.
    let Some(open_rel) = decl_rest.find('(') else {
        return (format!("func {name}()"), format!("{name}($0)"));
    };
    let after_open = &decl_rest[open_rel + 1..];
    let Some(close_rel) = after_open.find(')') else {
        return (format!("func {name}()"), format!("{name}($0)"));
    };
    let params_str = &after_open[..close_rel];

    // Parse "name: Type, name: Type, …" into individual param strings.
    let params: Vec<&str> = if params_str.trim().is_empty() {
        vec![]
    } else {
        params_str.split(',').map(|p| p.trim()).collect()
    };

    // Build snippet tab-stop list.
    let snippet_params: String = params
        .iter()
        .enumerate()
        .map(|(i, p)| {
            let hint = p.split(':').next().unwrap_or(p).trim();
            format!("${{{}:{}}}", i + 1, hint)
        })
        .collect::<Vec<_>>()
        .join(", ");

    // Extract return type if present (`: ReturnType` after `)`).
    let after_close = &after_open[close_rel + 1..];
    let ret_type = after_close
        .trim()
        .strip_prefix(':')
        .map(|s| {
            s.trim()
                .split(|c: char| !c.is_alphanumeric() && c != '_')
                .next()
                .unwrap_or("")
        })
        .unwrap_or("");

    let detail = if ret_type.is_empty() {
        format!("{name}({params_str})")
    } else {
        format!("{name}({params_str}): {ret_type}")
    };
    let snippet = format!("{name}({snippet_params})");
    (detail, snippet)
}
