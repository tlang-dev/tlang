# TLang — Data, Doc, Style & Raw Templates

> Topic file. Load `tlang-index.md` first for routing + cheat-sheets.
> For `lang [alias]` code-generation templates, load `tlang-lang.md` instead.

---

## `data` — Hierarchical Data Templates

Generates HTML, JSON, YAML, TOML, XML. No generator import needed.

```
data [html, json] userCard(name: String, email: String) {
  div(class: "user-card") {
    h1: "${name}",
    p(class: "email"): "${email}"
  }
}
```

### Bloc syntax

```
tagName(inlineAttr: value, …) {
  bodyAttr: value,
  nestedTag { … },
  <[ include() ]>
}
```

All three parts (tag name, inline attrs, body) are optional.

### Value types

| Form | Example |
|------|---------|
| String | `"${appName}"` |
| Number | `8080`, `3.14` |
| Boolean | `true`, `false` |
| Array | `["auth", "logging"]` |
| Nested bloc | `spec { replicas: 3 }` |
| Include | `<[ renderRows(rows) ]>` |

### Examples

```
// HTML
data [html] page(title: String) {
  html {
    head { title: "${title}" },
    body {
      div(class: "container") { h1: "${title}" }
    }
  }
}

// JSON / YAML
data [json, yaml] config(app: String, port: Int) {
  {
    name: "${app}",
    port: "${port}",
    features: ["auth", "logging"]
  }
}

// XML
data [xml] manifest(app: String, ver: String) {
  manifest(version: "${ver}") {
    application(name: "${app}") {
      <[ renderActivities(activities) ]>
    }
  }
}
```

**Rules:**
- No generator import needed — `data [html]` does not require `use HtmlGen as html`.
- `$$` works inside strings for a literal `$`.
- `raw:` is not applicable (body is already treated as data, not code).

---

## `doc` — Document Templates

Generates Markdown or HTML documents. No generator import needed.

```
doc [md, html] readme(project: String, version: String) {
  # ${project}

  Version: ${version}

  [section "usage"
    ## Usage

    [code "bash"
      npm install ${project}
    ]
  ]

  [list "unordered"
    - Fast
    - Multi-format output
  ]

  [link "https://github.com/example/${project}" "View on GitHub"]
}
```

### Calling

```
helper {
  func main(): String {
    return readme("md", "MyLib", "2.0.0")  // "md" selects format
  }
}
```

First arg optionally selects the format (case-insensitive). If it doesn't
match a declared format, the first declared format is used and all args are
parameters.

### Body elements

| Element | Syntax | md output | html output |
|---------|--------|-----------|-------------|
| Heading | `# text` … `######` | `## text\n\n` | `<h2>text</h2>` |
| Heading (dynamic) | `#(4) text` | `#### text\n\n` | `<h4>text</h4>` |
| Paragraph | bare text lines | `text\n\n` | `<p>text</p>` |
| Code block | `[code "lang"\n  …\n]` | ` ```lang\n…\n``` ` | `<pre><code class="language-lang">…</code></pre>` |
| Section | `[section "id"\n  …\n]` | content inline | `<section id="id">…</section>` |
| Image | `[img "src" "alt"]` | `![alt](src)` | `<img src="src" alt="alt" />` |
| Link | `[link "href" "text"]` | `[text](href)` | `<a href="href">text</a>` |
| Span | `[span "text"]` | `*text*` | `<em>text</em>` |
| Unordered list | `[list "unordered"\n  - item\n]` | `- item\n` | `<ul><li>item</li></ul>` |
| Ordered list | `[list "ordered"\n  - item\n]` | `1. item\n` | `<ol><li>item</li></ol>` |
| Table | `[table "H1" \| "H2"]` | `\| H1 \| H2 \|\n\| --- \| --- \|` | `<table><thead>…</thead></table>` |
| Include | `[include myTmpl(args)]` | call text | call text |
| Verbatim | `[asis\n  content\n]` | verbatim | verbatim |

`${param}` interpolation works in all text values (headings, paragraphs,
code, links, images, spans, list items, table headers).

**Formats:** `md` / `markdown`, `html`. Unknown format falls back to `md`.

---

## `raw` — Verbatim Text Templates

Embeds arbitrary text with no reformatting, no keyword parsing, no LSP
normalisation. Two modes:

| Mode | Syntax | `${param}` substituted? |
|------|--------|------------------------|
| `AsIs` | `raw [AsIs] name() { … }` | **No** — written literally |
| `Replaced` | `raw [Replaced] name(p: T) { … }` | **Yes** — replaced at call time |

```
raw [AsIs] dockerfileHeader() {
# Auto-generated — DO NOT EDIT
# Template syntax example: ${placeholder} (written as-is, not substituted)
}

raw [Replaced] dockerfileBody(image: String, port: String) {
FROM ${image}
WORKDIR /app
COPY build/libs/app.jar app.jar
EXPOSE ${port}
ENTRYPOINT ["java", "-jar", "app.jar"]
}
```

```
helper {
  func main(): String {
    return dockerfileHeader() + "\n" + dockerfileBody("eclipse-temurin:21", "8080")
  }
}
```

**Use `raw` when:**
- Content conflicts with TLang syntax (Dockerfiles, shell scripts, `.gitignore`, etc.).
- You need `${…}` to appear literally in the output → use `AsIs`.
- You need `${param}` substitution but nothing else → use `Replaced`.

---

## `style` — Style Templates

Generates CSS, SCSS, LESS, JSON design tokens, or JS/TS style objects.
No generator import needed.

```
style [css, json] Theme(primary: String, radius: String) {
    :root {
        --color-primary: ${primary},
        --border-radius: ${radius}
    }
    .card [hover] {
        border-color: ${primary}
    }
    .btn [focus] {
        outline: 2px solid ${primary}
    }
}
```

### Struct shape

```
selectorOrName [modifier, …] { prop: value, … }
```

All three parts are optional. Modifiers become `:modifier` pseudo-class
suffixes in CSS output — `.card [hover]` → `.card:hover { … }`.

### Attribute value forms

| Form | Example | CSS output |
|------|---------|------------|
| Bare identifier | `bold`, `auto`, `none` | Used verbatim |
| Quoted string | `"14px solid red"` | Quotes stripped |
| Number | `14`, `3.14` | No quotes |
| Boolean | `true`, `false` | Verbatim (useful for JSON/JS) |
| Interpolation | `${color}` | Replaced at runtime |
| Array | `[left, center, right]` | Joined with `, ` in CSS |
| Include | `<[ vendorPrefix("…") ]>` | Expanded at runtime |

### Output formats

| Key(s) | Output style |
|--------|-------------|
| `css` | `selector { prop: value; }` |
| `scss`, `less` | Identical to CSS (nesting not pre-processed) |
| `json` | `{ "selector": { "prop": "value" } }` |
| `js`, `ts`, `javascript`, `typescript` | JS/TS object literal |
| anything else | Falls back to CSS |

### Calling

```
helper {
  func main(): String {
    let css    = Theme("css",  "#1a73e8", "8px")
    let tokens = Theme("json", "#1a73e8", "8px")
    return css
  }
}
```

First arg optionally selects the format (case-insensitive). If it doesn't
match a declared format, the first declared format is used and all args are
parameters.

### Notes

- CSS at-rules (`@media`, `@keyframes`) are passed through verbatim as
  selector strings — they are not given special semantics.
- SCSS/LESS nesting and variables are not pre-processed. Use `raw [Replaced]`
  if you need full at-rule bodies without style-struct parsing.
- Number literals without units (`14`) are stored as numbers; values with
  units (`14px`, `2em`) are stored as strings.

---

## Template Type Decision Table

| You are generating… | Use |
|---------------------|-----|
| Kotlin / Java / Go / TS / any source code | `lang [alias]` + generator import |
| HTML, JSON, YAML, XML, TOML | `data [fmt]` |
| Markdown or HTML documents | `doc [fmt]` |
| CSS, SCSS, LESS, design tokens, JS/TS style objects | `style [fmt]` |
| Verbatim text, `${…}` must appear literally | `raw [AsIs]` |
| Verbatim text with `${param}` substitution | `raw [Replaced]` |