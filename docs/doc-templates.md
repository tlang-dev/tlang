# Doc Templates

Doc templates (`doc`) are a template type in TLang for generating structured
documents from a single source definition. A single `doc` block can render to
multiple output formats such as Markdown (`md`) and HTML.

---

## Syntax

```tlang
doc [md, html] myDoc(param1: String, param2: String) {
  # Heading with ${param1}

  Some paragraph text referencing ${param2}.

  [code "rust"
    fn main() {
        println!("Hello, ${param1}!");
    }
  ]
}
```

The general structure is:

```
doc [<lang>, <lang>, ...] <name>(<params>) {
  <body>
}
```

- **`doc`** — the keyword that declares a doc template.
- **`[<lang>, ...]`** — a comma-separated list of output formats (e.g. `md`,
  `html`).
- **`<name>`** — the template name, used to call it from helper code.
- **`(<params>)`** — optional parameter list, same syntax as other TLang
  templates: `(name: Type, ...)`.
- **`{ <body> }`** — the document body, composed of doc elements (see below).

---

## Calling a Doc Template

Doc templates are called from helper code like any other function. The first
argument may optionally be the target language name (as a string) to select
which format to render:

```tlang
helper {
  func main(): String {
    return myDoc("md", "World", "TLang")
  }
}

doc [md, html] myDoc(greeting: String, project: String) {
  # Hello ${greeting}

  Welcome to ${project}.
}
```

If the first argument matches one of the declared languages (case-insensitive),
it is consumed as the format selector and the remaining arguments are passed as
template parameters. If the first argument does not match a language name, the
first declared language is used as the default and all arguments are treated as
template parameters.

---

## Document Elements

All elements appear inside the `{ }` body of a `doc` template. Elements are
separated by blank lines (blank lines between elements are ignored by the
parser).

### Headings

Standard Markdown-style headings using `#` characters:

```
# Level 1 heading
## Level 2 heading
### Level 3 heading
#### Level 4 heading
```

For levels beyond 6, or to specify a level dynamically:

```
#(4) Level 4 heading
#(7) Level 7 heading
```

**Markdown output:** `## Heading text\n\n`
**HTML output:** `<h2>Heading text</h2>\n`

---

### Plain Text / Paragraphs

Any line that does not start with `#` or `[` is treated as plain text.
Consecutive plain-text lines are merged into a single paragraph node.

```
This is a paragraph.
This line is merged into the same paragraph.

This is a separate paragraph because of the blank line.
```

**Markdown output:** `This is a paragraph.\nThis line is merged into the same paragraph.\n\n`
**HTML output:** `<p>This is a paragraph.\nThis line is merged...</p>\n`

---

### Sections

A named section groups content and adds semantic structure:

```
[section "intro"
  This is the introduction.

  ## Sub-heading inside section
]
```

Sections can be nested. The content inside a section is parsed recursively and
supports all other element types.

**Markdown output:** The section content is rendered inline (no wrapper).
**HTML output:** `<section id="intro">\n...\n</section>\n`

---

### Code Blocks

A fenced code block with a language hint:

```
[code "rust"
  fn main() {
      println!("Hello!");
  }
]
```

The language string is used for syntax-highlighting hints in the output.

**Markdown output:**
``````
```rust
fn main() {
    println!("Hello!");
}
```
``````

**HTML output:** `<pre><code class="language-rust">fn main() ...</code></pre>\n`

---

### Images

An inline image element:

```
[img "path/to/image.png" "Alt text"]
```

The second argument (alt text) is optional:

```
[img "path/to/image.png"]
```

**Markdown output:** `![Alt text](path/to/image.png)\n`
**HTML output:** `<img src="path/to/image.png" alt="Alt text" />\n`

---

### Links

A hyperlink:

```
[link "https://example.com" "Click here"]
```

**Markdown output:** `[Click here](https://example.com)`
**HTML output:** `<a href="https://example.com">Click here</a>`

---

### Spans (Inline Emphasis)

Inline text with emphasis formatting:

```
[span "important text"]
```

**Markdown output:** `*important text*`
**HTML output:** `<em>important text</em>`

---

### Lists

An unordered (bulleted) list:

```
[list "unordered"
  - Item one
  - Item two
  - Item three
]
```

An ordered (numbered) list:

```
[list "ordered"
  - First item
  - Second item
  - Third item
]
```

The order type is `"unordered"` or `"ordered"` (case-insensitive).

**Markdown output (unordered):**
```
- Item one
- Item two
- Item three
```

**Markdown output (ordered):**
```
1. First item
2. Second item
3. Third item
```

**HTML output (unordered):**
```html
<ul>
<li>Item one</li>
<li>Item two</li>
</ul>
```

**HTML output (ordered):**
```html
<ol>
<li>First item</li>
<li>Second item</li>
</ol>
```

---

### Tables

A table with header row:

```
[table "Name" | "Age" | "City"]
```

The headers are `"quoted strings"` separated by `|`. Currently the table
element only defines the header row structure.

**Markdown output:**
```
| Name | Age | City |
| --- | --- | --- |
```

**HTML output:**
```html
<table><thead><tr><th>Name</th><th>Age</th><th>City</th></tr></thead></table>
```

---

### Includes

Include another template by calling it inline:

```
[include myOtherTemplate(arg1, arg2)]
```

The call expression is emitted verbatim into the output at that position. This
is useful for composing doc templates from smaller pieces.

**All formats output:** The call expression text as-is (template expansion
happens at the call site).

---

### As-Is (Verbatim)

Emit content verbatim without any processing:

```
[asis
  <div class="custom">
    Raw HTML or Markdown that bypasses the parser.
  </div>
]
```

No interpolation, no transformation — the content is included exactly as
written.

**All formats output:** The content verbatim.

---

## Parameter Interpolation

Inside any text value (headings, paragraphs, code, link text, etc.) you can
interpolate template parameters using `${paramName}`:

```
doc [md] greet(name: String, lang: String) {
  # Hello, ${name}!

  This document was generated for the ${lang} language.

  [code "${lang}"
    // ${lang} example
    print("${name}")
  ]
}
```

Interpolation works in:
- Heading titles
- Plain text / paragraphs
- Image `src` and `alt` attributes
- Link `src` and `text` attributes
- Span content
- List item text
- Table header text
- Code block content

---

## Complete Example

```tlang
helper {
  func main(): String {
    return readme("md", "MyProject", "1.0.0")
  }
}

doc [md, html] readme(project: String, version: String) {
  # ${project}

  Version: ${version}

  A brief description of ${project}.

  [section "getting-started"
    ## Getting Started

    Install ${project} using your package manager.

    [code "bash"
      npm install ${project}
    ]
  ]

  [section "usage"
    ## Usage

    [code "js"
      const ${project} = require("${project}")
      ${project}.run()
    ]
  ]

  ## Features

  [list "unordered"
    - Fast and lightweight
    - Multi-format output
    - Easy to use
  ]

  ## Links

  [link "https://github.com/example/${project}" "View on GitHub"]

  [img "https://img.shields.io/badge/version-${version}-blue" "Version badge"]
}
```

Running `main()` produces a Markdown document with all sections, code blocks,
lists, and links rendered appropriately for the `md` format.

---

## Format Support

| Element    | Markdown (`md`) | HTML |
|------------|----------------|------|
| Heading    | `## text` | `<h2>text</h2>` |
| Text       | `text\n\n` | `<p>text</p>` |
| Section    | content inline | `<section id="name">` |
| Code       | ` ```lang\ncode\n``` ` | `<pre><code class="language-lang">` |
| Image      | `![alt](src)` | `<img src="..." alt="...">` |
| Link       | `[text](src)` | `<a href="src">text</a>` |
| Span       | `*text*` | `<em>text</em>` |
| List       | `- item` / `1. item` | `<ul>/<ol>` |
| Table      | `| h1 | h2 |` | `<table><thead>...` |
| Include    | call text | call text |
| AsIs       | verbatim | verbatim |

---

## Adding New Languages

The `doc` template type currently has built-in renderers for `md`/`markdown`
and `html`. For unrecognised format strings, the Markdown renderer is used as
a fallback. Additional rendering backends can be added to
`tlang/src/tmpl_doc_tree.rs` in the `render_doc_tree` function.