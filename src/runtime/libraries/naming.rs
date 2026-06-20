// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use super::super::{RuntimeError, Value};
use super::expect_string;

pub fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Naming.toCamelCase" => {
            let s = expect_string(&args[0], "toCamelCase argument")?;
            Ok(Value::String(to_camel_case(&s)))
        }
        "TLang.Naming.toPascalCase" => {
            let s = expect_string(&args[0], "toPascalCase argument")?;
            Ok(Value::String(to_pascal_case(&s)))
        }
        "TLang.Naming.toSnakeCase" => {
            let s = expect_string(&args[0], "toSnakeCase argument")?;
            Ok(Value::String(to_snake_case(&s)))
        }
        "TLang.Naming.toScreamingSnake" => {
            let s = expect_string(&args[0], "toScreamingSnake argument")?;
            Ok(Value::String(to_screaming_snake(&s)))
        }
        "TLang.Naming.toKebabCase" => {
            let s = expect_string(&args[0], "toKebabCase argument")?;
            Ok(Value::String(to_kebab_case(&s)))
        }
        "TLang.Naming.toDotCase" => {
            let s = expect_string(&args[0], "toDotCase argument")?;
            Ok(Value::String(to_dot_case(&s)))
        }
        "TLang.Naming.toTitleCase" => {
            let s = expect_string(&args[0], "toTitleCase argument")?;
            Ok(Value::String(to_title_case(&s)))
        }
        "TLang.Naming.capitalize" => {
            let s = expect_string(&args[0], "capitalize argument")?;
            Ok(Value::String(capitalize(&s)))
        }
        "TLang.Naming.decapitalize" => {
            let s = expect_string(&args[0], "decapitalize argument")?;
            Ok(Value::String(decapitalize(&s)))
        }
        "TLang.Naming.pluralize" => {
            let s = expect_string(&args[0], "pluralize argument")?;
            Ok(Value::String(pluralize(&s)))
        }
        "TLang.Naming.singularize" => {
            let s = expect_string(&args[0], "singularize argument")?;
            Ok(Value::String(singularize(&s)))
        }
        "TLang.Naming.words" => {
            let s = expect_string(&args[0], "words argument")?;
            let words = split_words(&s).into_iter().map(Value::String).collect();
            Ok(Value::List(words))
        }
        _ => Err(RuntimeError(format!(
            "Unknown TLang.Naming target: {target}"
        ))),
    }
}

/// Split a string into lowercase words, handling snake_case, kebab-case,
/// dot.case, space separated, camelCase, and PascalCase.
fn split_words(s: &str) -> Vec<String> {
    // First split on explicit separators: _, -, space, .
    // Then further split each chunk on camelCase boundaries.
    let mut words: Vec<String> = Vec::new();

    for chunk in s.split(['_', '-', ' ', '.']) {
        if chunk.is_empty() {
            continue;
        }
        // Split on camelCase / PascalCase boundaries within the chunk.
        words.extend(split_camel(chunk));
    }

    words
}

/// Split a single chunk (no separators) on camelCase boundaries.
/// Returns lowercase words.
fn split_camel(s: &str) -> Vec<String> {
    let chars: Vec<char> = s.chars().collect();
    let len = chars.len();
    let mut words: Vec<String> = Vec::new();
    let mut start = 0;

    let mut i = 1;
    while i < len {
        let prev = chars[i - 1];
        let curr = chars[i];
        let next = if i + 1 < len {
            Some(chars[i + 1])
        } else {
            None
        };

        // Boundary: lowercase → uppercase  (e.g. myField: split before F)
        let boundary_lower_to_upper = prev.is_lowercase() && curr.is_uppercase();

        // Boundary: uppercase run → uppercase+lowercase  (e.g. XMLParser: split before P)
        // Only when previous char is uppercase and next is lowercase.
        let boundary_acronym = prev.is_uppercase()
            && curr.is_uppercase()
            && next.map(|n| n.is_lowercase()).unwrap_or(false);

        if boundary_lower_to_upper || boundary_acronym {
            let word: String = chars[start..i].iter().collect::<String>().to_lowercase();
            if !word.is_empty() {
                words.push(word);
            }
            start = i;
        }

        i += 1;
    }

    // Push the last segment
    let word: String = chars[start..].iter().collect::<String>().to_lowercase();
    if !word.is_empty() {
        words.push(word);
    }

    words
}

fn capitalize_word(w: &str) -> String {
    let mut chars = w.chars();
    match chars.next() {
        None => String::new(),
        Some(first) => first.to_uppercase().collect::<String>() + chars.as_str(),
    }
}

fn to_camel_case(s: &str) -> String {
    let words = split_words(s);
    let mut result = String::new();
    for (i, word) in words.iter().enumerate() {
        if i == 0 {
            result.push_str(word);
        } else {
            result.push_str(&capitalize_word(word));
        }
    }
    result
}

fn to_pascal_case(s: &str) -> String {
    split_words(s).iter().map(|w| capitalize_word(w)).collect()
}

fn to_snake_case(s: &str) -> String {
    split_words(s).join("_")
}

fn to_screaming_snake(s: &str) -> String {
    split_words(s).join("_").to_uppercase()
}

fn to_kebab_case(s: &str) -> String {
    split_words(s).join("-")
}

fn to_dot_case(s: &str) -> String {
    split_words(s).join(".")
}

fn to_title_case(s: &str) -> String {
    split_words(s)
        .iter()
        .map(|w| capitalize_word(w))
        .collect::<Vec<_>>()
        .join(" ")
}

fn capitalize(s: &str) -> String {
    let mut chars = s.chars();
    match chars.next() {
        None => String::new(),
        Some(first) => first.to_uppercase().collect::<String>() + chars.as_str(),
    }
}

fn decapitalize(s: &str) -> String {
    let mut chars = s.chars();
    match chars.next() {
        None => String::new(),
        Some(first) => first.to_lowercase().collect::<String>() + chars.as_str(),
    }
}

fn pluralize(s: &str) -> String {
    if s.is_empty() {
        return s.to_string();
    }

    let lower = s.to_lowercase();

    // Determine the suffix transformation on the lowercase version, then
    // rebuild preserving original case up to the unchanged prefix.
    let (prefix_len, suffix) = get_plural_suffix(&lower);

    let mut result: String = s.chars().take(prefix_len).collect();
    result.push_str(suffix);
    result
}

/// Returns (number of original chars to keep, suffix to append).
fn get_plural_suffix(lower: &str) -> (usize, &'static str) {
    if lower.ends_with("fe") {
        return (lower.len() - 2, "ves");
    }
    if lower.ends_with('f') && !lower.ends_with("ff") {
        return (lower.len() - 1, "ves");
    }
    if lower.ends_with("ch")
        || lower.ends_with("sh")
        || lower.ends_with('s')
        || lower.ends_with('x')
        || lower.ends_with('z')
    {
        return (lower.len(), "es");
    }
    if let Some(without_y) = lower.strip_suffix('y') {
        let last_char = without_y.chars().last().unwrap_or('a');
        if !"aeiou".contains(last_char) {
            return (lower.len() - 1, "ies");
        }
    }
    (lower.len(), "s")
}

fn singularize(s: &str) -> String {
    if s.is_empty() {
        return s.to_string();
    }

    let lower = s.to_lowercase();

    if lower.len() > 3 && lower.ends_with("ies") {
        // consonant + ies → consonant + y
        let prefix_len = lower.len() - 3;
        let mut result: String = s.chars().take(prefix_len).collect();
        result.push('y');
        return result;
    }

    if lower.ends_with("ves") {
        let prefix_len = lower.len() - 3;
        let mut result: String = s.chars().take(prefix_len).collect();
        result.push('f');
        return result;
    }

    for ending in &["sses", "xes", "zes", "ches", "shes", "ses"] {
        if lower.ends_with(ending) {
            let prefix_len = lower.len() - 2; // remove "es"
            let result: String = s.chars().take(prefix_len).collect();
            return result;
        }
    }

    if lower.ends_with('s') && !lower.ends_with("ss") {
        let prefix_len = lower.len() - 1;
        return s.chars().take(prefix_len).collect();
    }

    s.to_string()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_split_words() {
        assert_eq!(split_words("my_field"), vec!["my", "field"]);
        assert_eq!(split_words("myField"), vec!["my", "field"]);
        assert_eq!(split_words("MyField"), vec!["my", "field"]);
        assert_eq!(split_words("MY_FIELD"), vec!["my", "field"]);
        assert_eq!(split_words("my-field"), vec!["my", "field"]);
        assert_eq!(split_words("my.field"), vec!["my", "field"]);
        assert_eq!(split_words("my field"), vec!["my", "field"]);
        assert_eq!(split_words("XMLParser"), vec!["xml", "parser"]);
    }

    #[test]
    fn test_to_camel_case() {
        assert_eq!(to_camel_case("my_field"), "myField");
        assert_eq!(to_camel_case("MyField"), "myField");
        assert_eq!(to_camel_case("MY_FIELD"), "myField");
    }

    #[test]
    fn test_to_pascal_case() {
        assert_eq!(to_pascal_case("my_field"), "MyField");
        assert_eq!(to_pascal_case("myField"), "MyField");
    }

    #[test]
    fn test_to_snake_case() {
        assert_eq!(to_snake_case("myField"), "my_field");
        assert_eq!(to_snake_case("MyField"), "my_field");
    }

    #[test]
    fn test_to_screaming_snake() {
        assert_eq!(to_screaming_snake("myField"), "MY_FIELD");
    }

    #[test]
    fn test_to_kebab_case() {
        assert_eq!(to_kebab_case("myField"), "my-field");
    }

    #[test]
    fn test_to_dot_case() {
        assert_eq!(to_dot_case("myField"), "my.field");
    }

    #[test]
    fn test_to_title_case() {
        assert_eq!(to_title_case("myField"), "My Field");
    }

    #[test]
    fn test_capitalize() {
        assert_eq!(capitalize("hello"), "Hello");
        assert_eq!(capitalize("Hello"), "Hello");
    }

    #[test]
    fn test_decapitalize() {
        assert_eq!(decapitalize("Hello"), "hello");
        assert_eq!(decapitalize("hello"), "hello");
    }

    #[test]
    fn test_pluralize() {
        assert_eq!(pluralize("box"), "boxes");
        assert_eq!(pluralize("church"), "churches");
        assert_eq!(pluralize("quiz"), "quizes");
        assert_eq!(pluralize("baby"), "babies");
        assert_eq!(pluralize("leaf"), "leaves");
        assert_eq!(pluralize("knife"), "knives");
        assert_eq!(pluralize("cat"), "cats");
        assert_eq!(pluralize("bus"), "buses");
    }

    #[test]
    fn test_singularize() {
        assert_eq!(singularize("boxes"), "box");
        assert_eq!(singularize("churches"), "church");
        assert_eq!(singularize("babies"), "baby");
        assert_eq!(singularize("leaves"), "leaf");
        assert_eq!(singularize("cats"), "cat");
    }
}
