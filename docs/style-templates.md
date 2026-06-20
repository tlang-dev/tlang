# Style Templates

A `style` template generates structured style output — CSS, SCSS, LESS,
JSON style objects, or JavaScript/TypeScript style constants — from a
single parameterised definition.

---

## Syntax

```tlang
style [lang1, lang2, ...] TemplateName(param1: Type, param2: Type) {
    selectorOrName [modifier1, modifier2] {
        property: value,
        property: value
    }
    ...
}
```

### Header

| Part | Description |
|------|-------------|
| `style` | Keyword that opens a style block. |
| `[lang, ...]` | Comma-separated list of output formats this template supports (e.g. `css`, `scss`, `json`). |
| `TemplateName` | Identifier used to call the template from helper code. |
| `(params)` | Optional parameter list. Each parameter may have an optional type annotation (`name: Type`). |

### Body — Style Structs

The body contains one or more **style structs**, each representing a CSS-like
rule:

```
selectorName [modifier1, modifier2] {
    property: value,
    ...
}
```

All three parts are optional:

- **Selector / name** — the rule target (e.g. `.button`, `h1`, `#header`,
  a bare identifier, or a `${param}` interpolation).  If omitted the block
  has no selector.
- **Modifier list `[...]`** — zero or more pseudo-class or pseudo-element
  hints (e.g. `hover`, `focus`).  In CSS output they are appended to the
  selector separated by `:` (e.g. `.link:hover`).
- **Attribute block `{ ... }`** — required body containing comma-separated
  `property: value` pairs.

### Attribute Values

| Value form | Example | Notes |
|-----------|---------|-------|
| Bare identifier | `blue`, `bold`, `auto` | Used verbatim. |
| Quoted string | `"14px solid red"` | Quotes are stripped in CSS output. |
| Number | `14`, `3.14`, `-1` | Output without quotes. |
| Boolean | `true`, `false` | Output as `true` / `false`. |
| Interpolation | `${color}` | Replaced with the runtime argument value. |
| Array | `[left, center, right]` | Items joined with `, ` in CSS; JSON array otherwise. |
| Include | `<[ myHelper() ]>` | Expanded to the return value of the include call. |

An attribute may be anonymous (value only) or named (`property: value`).
Named attributes produce `property: value;` declarations in CSS output.

---

## Output Formats

Pass the format as the first string argument when calling the template (see
**Calling a Style Template** below).  If no format argument is given, the
first format in the declaration list is used.

| Key(s) | Output |
|--------|--------|
| `css` | Standard CSS rule blocks with `selector { prop: value; }` syntax. |
| `scss` | SCSS — identical to CSS output (nesting and variables are not yet pre-processed). |
| `less` | LESS — identical to CSS output. |
| `json` | JSON object: `{ "selector": { "property": "value" } }`. |
| `js`, `ts`, `javascript`, `typescript` | JavaScript/TypeScript object literal: `{ selector: { property: "value" } }`. |

Any other format key falls back to CSS syntax.

---

## Calling a Style Template

Style templates are called like ordinary helper functions.  The **first
argument** is optionally the output format string; remaining arguments are
the declared template parameters.

```tlang
helper {
    func main(): String {
        // Use the first declared format (css) with no extra params.
        let plain = ButtonStyle()

        // Explicitly request JSON output.
        let asJson = ButtonStyle("json")

        // Pass parameters.
        let themed = ThemedButton("css", "#3498db", "14px")

        return themed
    }
}
```

If the first argument matches one of the declared format strings
(case-insensitive), it is consumed as the format selector and the remaining
arguments are bound to the template parameters in order.  Otherwise the
first declared format is used and all arguments are bound to parameters.

---

## Examples

### Basic CSS button

```tlang
style [css, scss] Button() {
    .btn {
        display: inline-block,
        padding: 8px 16px,
        border-radius: 4px,
        cursor: pointer
    }
    .btn [hover] {
        opacity: 0.9
    }
    .btn [focus] {
        outline: 2px solid currentColor
    }
}
```

Rendered with `"css"`:

```css
.btn {
    display: inline-block;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
}
.btn:hover {
    opacity: 0.9;
}
.btn:focus {
    outline: 2px solid currentColor;
}
```

---

### Parameterised theme colours

```tlang
style [css, json] Theme(primary: String, secondary: String, radius: String) {
    :root {
        --color-primary: ${primary},
        --color-secondary: ${secondary},
        --border-radius: ${radius}
    }
    .card {
        border-radius: ${radius},
        border: 1px solid ${secondary}
    }
    .card [hover] {
        border-color: ${primary}
    }
}
```

Call from helper code:

```tlang
let css = Theme("css", "#1a73e8", "#e8f0fe", "8px")
let tokens = Theme("json", "#1a73e8", "#e8f0fe", "8px")
```

CSS output:

```css
:root {
    --color-primary: #1a73e8;
    --color-secondary: #e8f0fe;
    --border-radius: 8px;
}
.card {
    border-radius: 8px;
    border: 1px solid #e8f0fe;
}
.card:hover {
    border-color: #1a73e8;
}
```

JSON output:

```json
{
  ":root": {
    "--color-primary": "#1a73e8",
    "--color-secondary": "#e8f0fe",
    "--border-radius": "8px"
  },
  ".card": {
    "border-radius": "8px",
    "border": "1px solid #e8f0fe"
  },
  ".card:hover": {
    "border-color": "#1a73e8"
  }
}
```

---

### JavaScript style object

```tlang
style [js, ts] TypographyTokens() {
    heading {
        fontFamily: "Inter, sans-serif",
        fontWeight: 700,
        lineHeight: 1.2
    }
    body {
        fontFamily: "Inter, sans-serif",
        fontWeight: 400,
        lineHeight: 1.6
    }
}
```

JS output:

```js
{
  heading: {
    fontFamily: "Inter, sans-serif",
    fontWeight: 700,
    lineHeight: "1.2",
  },
  body: {
    fontFamily: "Inter, sans-serif",
    fontWeight: 400,
    lineHeight: "1.6",
  }
}
```

---

### Include directives

Include expressions (`<[ call() ]>`) inside an attribute list are rendered
as inline comments in CSS / JS output and as `null` values in JSON output.
They are intended as integration points for injecting content from helper
functions at runtime (e.g. computed values, vendor-prefixed expansions):

```tlang
style [css] Animations() {
    .fade-in {
        <[ vendorPrefix("animation", "fadeIn 0.3s ease") ]>,
        opacity: 1
    }
}
```

---

## LSP Support

The LSP provides the following features for `style` blocks:

| Feature | Behaviour |
|---------|-----------|
| **Semantic tokens** | `style` keyword highlighted as a keyword; `[lang, ...]` list as a namespace; template name as a function; parameter names as parameters; parameter types as types; `${...}` interpolations in the body as variables; `<[...]>` includes as macros. |
| **Document symbols** | Each `style [...]  Name(...)` block appears in the Outline panel with kind *Function* and container label `style template [langs]`. |
| **Completions** | Template names defined with `style [...]` are offered as callable function completions in helper blocks. |
| **Go-to-definition** | Template names at call sites navigate to the `style` block header line. |
| **Hover** | Hovering the template name at a call site shows the signature. |

---

## Notes and Limitations

- The style body is parsed as a sequence of named structs with attribute
  lists.  CSS nesting, at-rules (`@media`, `@keyframes`, etc.) and
  variable declarations are not given special semantics — they are treated
  as raw selector strings or property values and passed through verbatim.
- Number literals without units (e.g. `14`) are stored as numbers; CSS
  values that include units (e.g. `14px`, `2em`, `100%`) are stored as
  strings.
- Boolean values (`true` / `false`) are useful for JSON / JS output but
  are not standard CSS — they are emitted verbatim in CSS mode.
- SCSS / LESS output is currently identical to CSS output.  Language-
  specific pre-processing (nesting, variables, mixins) is not yet
  implemented; if you need those features, embed raw SCSS/LESS content in
  a `raw [Replaced]` block instead.