# Manifest Specification

Every TLang project requires a `manifest.yml` file in its root directory. This document describes the complete manifest format and semantics.

## Overview

The manifest file serves multiple purposes:
1. **Project identification**: Name, version, and metadata
2. **Dependency management**: Declare external packages the project depends on
3. **Entry point configuration**: Specify the main file to execute
4. **Package publishing**: Information needed when this project is used as a dependency

## File Location

- **Must** be named `manifest.yml` (lowercase)
- **Must** be located in the project root directory
- **Must not** exist in subdirectories (subfolders are part of the same project)

## File Format

The manifest uses YAML 1.2 syntax. All keys are case-sensitive.

## Required Fields

### `name`

**Type:** String
**Required:** Yes

The package name in PascalCase. This is the short name used when this project is published as a dependency.

**Example:**
```yaml
name: HelloWorld
```

**Rules:**
- Must be a valid identifier (alphanumeric + underscore)
- Must use PascalCase (e.g., `MyPackage`, not `my_package` or `myPackage`)
- Must not contain spaces or special characters (except underscore)

---

### `project`

**Type:** String
**Required:** Yes

The logical project name or group. This is typically the repository name or a descriptive name for the project group.

**Example:**
```yaml
project: HelloWorldProject
```

**Rules:**
- Must be a valid identifier
- Must use PascalCase

---

### `organisation`

**Type:** String
**Required:** Yes

The organization or author name. This is used as the first component of the package path when published.

**Example:**
```yaml
organisation: MyOrg
```

**Rules:**
- Must be a valid identifier
- Must use PascalCase

---

### `version`

**Type:** String
**Required:** Yes

The semantic version of the project. Follows [Semantic Versioning 2.0.0](https://semver.org/) conventions.

**Example:**
```yaml
version: 1.0.0
```

**Rules:**
- Must be a valid semver string (MAJOR.MINOR.PATCH)
- May include pre-release identifiers (e.g., `1.0.0-alpha`)
- May include build metadata (e.g., `1.0.0+build.123`)

---

### `stability`

**Type:** String
**Required:** Yes

The stability tier of the release.

**Valid values:**
- `alpha`: Early development, likely to change, not production-ready
- `beta`: Feature-complete but may have bugs, not recommended for production
- `rc` or `release-candidate`: Release candidate, nearly production-ready
- `stable`: Production-ready, stable API

**Example:**
```yaml
stability: alpha
```

---

### `releaseNumber`

**Type:** Integer
**Required:** Yes

The release number within the stability tier. Incremented for each release at the same stability level.

**Example:**
```yaml
releaseNumber: 1
```

**Rules:**
- Must be a positive integer (>= 1)
- Resets to 1 when stability changes (e.g., from alpha to beta)

---

## Optional Fields

### `main`

**Type:** String
**Required:** No
**Default:** `Main`

The entry point file stem (without `.tlang` extension) for the project. This is the file that contains the `main` function to execute.

**Example:**
```yaml
main: src/MyMain
```

**Rules:**
- Must be a stem (no `.tlang` extension)
- Must not include directory prefix if it's in the project root
- Path is relative to the project root
- If not specified, defaults to `Main` (i.e., `Main.tlang`)

**Important:**
- When a project has a `main:` declared, only that file is accessible as the entry point
- Other files in the project are not importable from outside (main-only access rule)
- Only symbols marked with `expose` in the main file are accessible to consumers

---

### `dependencies`

**Type:** List of strings
**Required:** No
**Default:** Empty list

List of external packages this project depends on.

**Format:**
Each dependency is a single string with the format:
```
<locator> <alias>
```

Where:
- `<locator>`: Either a registry path or a file path
- `<alias>`: The name to use for importing this dependency

**Example:**
```yaml
dependencies:
  # Registry package
  - TLangGen/KotlinGen/Kotlin 1.0.0:alpha:1 KotlinGen
  
  # Local directory dependency
  - file://libs/my-local-lib LocalLib
  
  # Multiple dependencies
  - TLangGen/JavaGen/Java 1.0.0:beta:1 JavaGen
  - file://../shared-models SharedModels
```

---

#### Registry Package Locator

**Format:**
```
<Org>/<Project>/<Name> <version>:<stability>:<releaseNumber> <Alias>
```

**Components:**
- `<Org>`: Organization name (from the package's `organisation` field)
- `<Project>`: Project name (from the package's `project` field)
- `<Name>`: Package name (from the package's `name` field)
- `<version>`: Version string (from the package's `version` field)
- `<stability>`: Stability tier (from the package's `stability` field)
- `<releaseNumber>`: Release number (from the package's `releaseNumber` field)
- `<Alias>`: Import alias (user-defined)

**Example:**
```yaml
dependencies:
  - TLangGen/KotlinGen/Kotlin 1.0.0:alpha:1 KotlinGen
```

This declares a dependency on the Kotlin generator package, version 1.0.0, alpha stability, release 1, and makes it available for import as `KotlinGen`.

---

#### File Path Locator

**Format:**
```
file://<path> <Alias>
```

**Components:**
- `<path>`: Relative or absolute path to a directory containing `.tlang` files
- `<Alias>`: Import alias (user-defined)

**Example:**
```yaml
dependencies:
  - file://libs/my-local-lib LocalLib
  - file://../shared-code SharedCode
```

**Rules:**
- The path must point to a directory (not a file)
- The directory must contain at least one `.tlang` file
- The directory should contain its own `manifest.yml` (recommended)
- Relative paths are resolved relative to the project root

---

## Complete Example

```yaml
# Required fields
name: UserService
project: UserServiceGenerator
organisation: MyCompany
version: 2.1.0
stability: beta
releaseNumber: 3

# Optional: custom entry point
main: src/GeneratorMain

# Optional: dependencies
dependencies:
  # Registry packages
  - TLangGen/KotlinGen/Kotlin 1.0.0:alpha:1 KotlinGen
  - TLangGen/JavaGen/Java 1.0.0:beta:1 JavaGen
  
  # Local directory dependencies
  - file://libs/common CommonLib
  - file://../shared-models SharedModels
```

---

## Resolution Rules

### Dependency Resolution

1. **Registry packages** are resolved from the local tbox (`~/.tlang/tbox/`)
2. **File path dependencies** are resolved from the filesystem
3. Resolution order: registry first, then file paths

### Import Path Resolution

When a TLang file contains `use Some.Package`:

1. If `Some` matches a dependency alias in the manifest:
   - The import is resolved from that dependency
   - If the dependency has a `main:` declared, only that file is accessible (main-only rule)
   - Only `expose`d symbols from the main file are accessible

2. Otherwise, the path is resolved relative to the current file:
   - `use FileName` → `<current-dir>/FileName.tlang`
   - `use Folder.FileName` → `<current-dir>/Folder/FileName.tlang`

---

## Package Publishing

When a project is packaged with `tlang package`:

1. The manifest is read to determine package metadata
2. The package is published to:
   ```
   ~/.tlang/tbox/<organisation>/<project>/<name>/<version>/<stability>/<releaseNumber>/<name>.tbag
   ```

3. Consumers can then depend on it using the full path format

---

## Validation Rules

The manifest is validated when loaded. The following checks are performed:

1. **Required fields**: All required fields must be present
2. **Field types**: Each field must have the correct type
3. **Field values**: Each field must have a valid value (e.g., stability must be one of the valid values)
4. **Dependency format**: Each dependency string must be parseable
5. **Path validity**: File path dependencies must point to existing directories

---

## Common Errors

| Error | Cause | Fix |
|-------|-------|-----|
| `missing required field: name` | The `name` field is missing | Add `name: <value>` to the manifest |
| `invalid stability: 'dev'` | The stability value is not valid | Use one of: `alpha`, `beta`, `rc`, `stable` |
| `invalid dependency format: 'KotlinGen'` | Dependency string is malformed | Use format: `<locator> <alias>` |
| `dependency not found: TLangGen/KotlinGen/Kotlin 1.0.0:alpha:1` | Package not in tbox | Run `tlang package generators/kotlin` first |
| `directory not found: libs/my-lib` | File dependency path doesn't exist | Create the directory or fix the path |

---

## Best Practices

1. **Use semantic versioning**: Follow semver conventions for the `version` field
2. **Start with alpha**: New projects should start with `stability: alpha`
3. **Increment releaseNumber**: Bump the release number for each release at the same stability level
4. **Use descriptive names**: Choose clear, descriptive names for your package
5. **Document dependencies**: Add comments explaining why each dependency is needed
6. **Keep main file focused**: The main file should only contain `expose`d symbols and delegate to other files

---

## See Also

- [Core Language Specification](./core-language.md)
- [Template Block Specification](./template-blocks.md)
- [CLI Reference](../cli/reference.md)
- [TLang Index](../../prompts/tlang-index.md)
- [Templates Topic](../../prompts/tlang-templates.md)
- [Project Topic](../../prompts/tlang-project.md)
