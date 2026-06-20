// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang.Map — immutable string-keyed map library.
//!
//! The key type is always `String`.  Every function returns a *new* value;
//! no in-place mutation occurs.
//!
//! The underlying runtime type is `Value::Map(BTreeMap<String, Value>)`.

use std::collections::BTreeMap;

use super::super::{Runtime, RuntimeError, Value};

// ── dispatch ─────────────────────────────────────────────────────────────────

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Map.create" => create(args),
        "TLang.Map.of" => of(args),
        "TLang.Map.size" => size(args),
        "TLang.Map.isEmpty" => is_empty(args),
        "TLang.Map.has" => has(args),
        "TLang.Map.get" => get(args),
        "TLang.Map.getOrDefault" => get_or_default(args),
        "TLang.Map.set" => set(args),
        "TLang.Map.remove" => remove(args),
        "TLang.Map.keys" => keys(args),
        "TLang.Map.values" => values(args),
        "TLang.Map.entries" => entries(args),
        "TLang.Map.merge" => merge(args),
        "TLang.Map.fromLists" => from_lists(args),
        "TLang.Map.toList" => to_list(args),
        _ => Err(RuntimeError(format!(
            "unknown Map library function `{target}`"
        ))),
    }
}

/// Higher-order dispatch — requires a `Runtime` reference to call lambdas.
pub(crate) fn call_hof(
    runtime: &mut Runtime<'_>,
    target: &str,
    args: &[Value],
) -> Result<Option<Value>, RuntimeError> {
    match target {
        "TLang.Map.mapValues" => map_values(runtime, args).map(Some),
        "TLang.Map.filterKeys" => filter_keys(runtime, args).map(Some),
        "TLang.Map.filterValues" => filter_values(runtime, args).map(Some),
        "TLang.Map.forEach" => for_each(runtime, args).map(Some),
        "TLang.Map.reduce" => reduce(runtime, args).map(Some),
        _ => Ok(None),
    }
}

// ── helpers ───────────────────────────────────────────────────────────────────

fn check_arity(args: &[Value], expected: usize, name: &str) -> Result<(), RuntimeError> {
    if args.len() != expected {
        Err(RuntimeError(format!(
            "TLang.Map.{name} expects exactly {expected} argument(s), got {}",
            args.len()
        )))
    } else {
        Ok(())
    }
}

fn expect_map<'a>(
    value: &'a Value,
    ctx: &str,
) -> Result<&'a BTreeMap<String, Value>, RuntimeError> {
    match value {
        Value::Map(m) => Ok(m),
        other => Err(RuntimeError(format!(
            "{ctx}: expected Map, got {}",
            super::super::value_type_name(other)
        ))),
    }
}

fn str_arg(args: &[Value], index: usize, ctx: &str) -> Result<String, RuntimeError> {
    args.get(index)
        .ok_or_else(|| RuntimeError(format!("{ctx}: missing argument at index {index}")))
        .and_then(|v| match v {
            Value::String(s) => Ok(s.clone()),
            other => Err(RuntimeError(format!(
                "{ctx}: argument {index} must be String, got {}",
                super::super::value_type_name(other)
            ))),
        })
}

fn expect_list<'a>(value: &'a Value, ctx: &str) -> Result<&'a Vec<Value>, RuntimeError> {
    match value {
        Value::List(v) => Ok(v),
        other => Err(RuntimeError(format!(
            "{ctx}: expected List, got {}",
            super::super::value_type_name(other)
        ))),
    }
}

// ── implementations ───────────────────────────────────────────────────────────

/// `create() -> Map`  — empty map.
fn create(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 0, "create")?;
    Ok(Value::Map(BTreeMap::new()))
}

/// `of(k1, v1, k2, v2, …) -> Map`  — variadic map literal helper.
///   Arguments must come in (String key, value) pairs; an odd count is an error.
fn of(args: &[Value]) -> Result<Value, RuntimeError> {
    if !args.len().is_multiple_of(2) {
        return Err(RuntimeError(format!(
            "TLang.Map.of expects an even number of arguments (key-value pairs), got {}",
            args.len()
        )));
    }
    let mut map = BTreeMap::new();
    for chunk in args.chunks(2) {
        let key = match &chunk[0] {
            Value::String(s) => s.clone(),
            other => {
                return Err(RuntimeError(format!(
                    "TLang.Map.of: keys must be String, got {}",
                    super::super::value_type_name(other)
                )));
            }
        };
        map.insert(key, chunk[1].clone());
    }
    Ok(Value::Map(map))
}

/// `size(map) -> Int`  — number of entries.
fn size(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "size")?;
    let map = expect_map(&args[0], "TLang.Map.size")?;
    Ok(Value::Int(map.len() as i64))
}

/// `isEmpty(map) -> Bool`
fn is_empty(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "isEmpty")?;
    let map = expect_map(&args[0], "TLang.Map.isEmpty")?;
    Ok(Value::Bool(map.is_empty()))
}

/// `has(map, key) -> Bool`  — true if the map contains `key`.
fn has(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "has")?;
    let map = expect_map(&args[0], "TLang.Map.has")?;
    let key = str_arg(args, 1, "TLang.Map.has")?;
    Ok(Value::Bool(map.contains_key(&key)))
}

/// `get(map, key) -> Value`  — value for `key`; error if absent.
fn get(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "get")?;
    let map = expect_map(&args[0], "TLang.Map.get")?;
    let key = str_arg(args, 1, "TLang.Map.get")?;
    map.get(&key)
        .cloned()
        .ok_or_else(|| RuntimeError(format!("TLang.Map.get: key `{key}` not found")))
}

/// `getOrDefault(map, key, default) -> Value`  — value for `key`, or `default` if absent.
fn get_or_default(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 3, "getOrDefault")?;
    let map = expect_map(&args[0], "TLang.Map.getOrDefault")?;
    let key = str_arg(args, 1, "TLang.Map.getOrDefault")?;
    Ok(map.get(&key).cloned().unwrap_or_else(|| args[2].clone()))
}

/// `set(map, key, value) -> Map`  — new map with entry `key -> value` added or replaced.
fn set(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 3, "set")?;
    let map = expect_map(&args[0], "TLang.Map.set")?;
    let key = str_arg(args, 1, "TLang.Map.set")?;
    let mut new = map.clone();
    new.insert(key, args[2].clone());
    Ok(Value::Map(new))
}

/// `remove(map, key) -> Map`  — new map without `key` (no-op if key is absent).
fn remove(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "remove")?;
    let map = expect_map(&args[0], "TLang.Map.remove")?;
    let key = str_arg(args, 1, "TLang.Map.remove")?;
    let mut new = map.clone();
    new.remove(&key);
    Ok(Value::Map(new))
}

/// `keys(map) -> List<String>`  — sorted list of all keys.
fn keys(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "keys")?;
    let map = expect_map(&args[0], "TLang.Map.keys")?;
    let ks: Vec<Value> = map.keys().map(|k| Value::String(k.clone())).collect();
    Ok(Value::List(ks))
}

/// `values(map) -> List`  — list of values in key-sorted order.
fn values(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "values")?;
    let map = expect_map(&args[0], "TLang.Map.values")?;
    let vs: Vec<Value> = map.values().cloned().collect();
    Ok(Value::List(vs))
}

/// `entries(map) -> List<List>`
///   List of `[key, value]` pairs in key-sorted order.
fn entries(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "entries")?;
    let map = expect_map(&args[0], "TLang.Map.entries")?;
    let pairs: Vec<Value> = map
        .iter()
        .map(|(k, v)| Value::List(vec![Value::String(k.clone()), v.clone()]))
        .collect();
    Ok(Value::List(pairs))
}

/// `merge(base, overrides) -> Map`
///   New map containing all entries from `base` plus all entries from `overrides`.
///   When both maps share a key, `overrides` wins.
fn merge(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "merge")?;
    let base = expect_map(&args[0], "TLang.Map.merge")?;
    let over = expect_map(&args[1], "TLang.Map.merge")?;
    let mut new = base.clone();
    for (k, v) in over {
        new.insert(k.clone(), v.clone());
    }
    Ok(Value::Map(new))
}

/// `fromLists(keys, values) -> Map`
///   Build a map from two lists of equal length.
///   `keys` must be a `List<String>`.  Extra values are ignored; missing values error.
fn from_lists(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "fromLists")?;
    let ks = expect_list(&args[0], "TLang.Map.fromLists keys")?;
    let vs = expect_list(&args[1], "TLang.Map.fromLists values")?;
    if ks.len() != vs.len() {
        return Err(RuntimeError(format!(
            "TLang.Map.fromLists: keys length ({}) != values length ({})",
            ks.len(),
            vs.len()
        )));
    }
    let mut map = BTreeMap::new();
    for (k, v) in ks.iter().zip(vs.iter()) {
        let key = match k {
            Value::String(s) => s.clone(),
            other => {
                return Err(RuntimeError(format!(
                    "TLang.Map.fromLists: keys must be String, got {}",
                    super::super::value_type_name(other)
                )));
            }
        };
        map.insert(key, v.clone());
    }
    Ok(Value::Map(map))
}

/// `toList(map) -> List<List>`  — alias for `entries`; provided for discoverability.
fn to_list(args: &[Value]) -> Result<Value, RuntimeError> {
    entries(args)
}

// ── higher-order functions ────────────────────────────────────────────────────

use super::super::LambdaObject;

fn expect_lambda<'a>(
    args: &'a [Value],
    idx: usize,
    name: &str,
) -> Result<&'a LambdaObject, RuntimeError> {
    match args.get(idx) {
        Some(Value::Lambda(lam)) => Ok(lam),
        Some(other) => Err(RuntimeError(format!(
            "TLang.Map.{name}: argument {idx} must be a lambda, got {}",
            super::super::value_type_name(other)
        ))),
        None => Err(RuntimeError(format!(
            "TLang.Map.{name}: missing lambda argument at position {idx}"
        ))),
    }
}

/// `mapValues(map, fn) -> Map`  — transform each value via `fn(key, value) -> Value`.
fn map_values(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "mapValues")?;
    let map = expect_map(&args[0], "TLang.Map.mapValues")?.clone();
    let lam = expect_lambda(args, 1, "mapValues")?.clone();
    let mut out = BTreeMap::new();
    for (k, v) in map {
        let new_v = runtime.call_lambda(&lam, vec![Value::String(k.clone()), v])?;
        out.insert(k, new_v);
    }
    Ok(Value::Map(out))
}

/// `filterKeys(map, fn) -> Map`  — keep entries where `fn(key) -> Bool`.
fn filter_keys(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "filterKeys")?;
    let map = expect_map(&args[0], "TLang.Map.filterKeys")?.clone();
    let lam = expect_lambda(args, 1, "filterKeys")?.clone();
    let mut out = BTreeMap::new();
    for (k, v) in map {
        match runtime.call_lambda(&lam, vec![Value::String(k.clone())])? {
            Value::Bool(true) => {
                out.insert(k, v);
            }
            Value::Bool(false) => {}
            other => {
                return Err(RuntimeError(format!(
                    "TLang.Map.filterKeys: predicate must return Bool, got {}",
                    super::super::value_type_name(&other)
                )));
            }
        }
    }
    Ok(Value::Map(out))
}

/// `filterValues(map, fn) -> Map`  — keep entries where `fn(key, value) -> Bool`.
fn filter_values(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "filterValues")?;
    let map = expect_map(&args[0], "TLang.Map.filterValues")?.clone();
    let lam = expect_lambda(args, 1, "filterValues")?.clone();
    let mut out = BTreeMap::new();
    for (k, v) in map {
        match runtime.call_lambda(&lam, vec![Value::String(k.clone()), v.clone()])? {
            Value::Bool(true) => {
                out.insert(k, v);
            }
            Value::Bool(false) => {}
            other => {
                return Err(RuntimeError(format!(
                    "TLang.Map.filterValues: predicate must return Bool, got {}",
                    super::super::value_type_name(&other)
                )));
            }
        }
    }
    Ok(Value::Map(out))
}

/// `forEach(map, fn) -> Unit`  — call `fn(key, value)` for each entry.
fn for_each(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "forEach")?;
    let map = expect_map(&args[0], "TLang.Map.forEach")?.clone();
    let lam = expect_lambda(args, 1, "forEach")?.clone();
    for (k, v) in map {
        runtime.call_lambda(&lam, vec![Value::String(k), v])?;
    }
    Ok(Value::Unit)
}

/// `reduce(map, initial, fn) -> Value`  — fold entries with `fn(acc, key, value) -> acc`.
fn reduce(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 3, "reduce")?;
    let map = expect_map(&args[0], "TLang.Map.reduce")?.clone();
    let initial = args[1].clone();
    let lam = expect_lambda(args, 2, "reduce")?.clone();
    let mut acc = initial;
    for (k, v) in map {
        acc = runtime.call_lambda(&lam, vec![acc, Value::String(k), v])?;
    }
    Ok(acc)
}

// ── tests ─────────────────────────────────────────────────────────────────────

#[cfg(test)]
mod tests {
    use super::*;

    fn i(v: i64) -> Value {
        Value::Int(v)
    }
    fn s(v: &str) -> Value {
        Value::String(v.to_string())
    }
    fn b(v: bool) -> Value {
        Value::Bool(v)
    }
    fn empty_map() -> Value {
        Value::Map(BTreeMap::new())
    }
    fn map_of(pairs: &[(&str, Value)]) -> Value {
        let mut m = BTreeMap::new();
        for (k, v) in pairs {
            m.insert(k.to_string(), v.clone());
        }
        Value::Map(m)
    }
    fn lst(items: Vec<Value>) -> Value {
        Value::List(items)
    }

    // -- create ---------------------------------------------------------------
    #[test]
    fn test_create_empty() {
        assert_eq!(call("TLang.Map.create", &[]).unwrap(), empty_map());
    }

    #[test]
    fn test_create_wrong_arity() {
        assert!(call("TLang.Map.create", &[i(1)]).is_err());
    }

    // -- of -------------------------------------------------------------------
    #[test]
    fn test_of_pairs() {
        let result = call("TLang.Map.of", &[s("a"), i(1), s("b"), i(2)]).unwrap();
        assert_eq!(result, map_of(&[("a", i(1)), ("b", i(2))]));
    }

    #[test]
    fn test_of_empty() {
        assert_eq!(call("TLang.Map.of", &[]).unwrap(), empty_map());
    }

    #[test]
    fn test_of_odd_args_error() {
        assert!(call("TLang.Map.of", &[s("key")]).is_err());
    }

    #[test]
    fn test_of_non_string_key_error() {
        assert!(call("TLang.Map.of", &[i(1), s("val")]).is_err());
    }

    // -- size -----------------------------------------------------------------
    #[test]
    fn test_size_empty() {
        assert_eq!(call("TLang.Map.size", &[empty_map()]).unwrap(), i(0));
    }

    #[test]
    fn test_size_two() {
        let m = map_of(&[("a", i(1)), ("b", i(2))]);
        assert_eq!(call("TLang.Map.size", &[m]).unwrap(), i(2));
    }

    // -- isEmpty --------------------------------------------------------------
    #[test]
    fn test_is_empty_true() {
        assert_eq!(call("TLang.Map.isEmpty", &[empty_map()]).unwrap(), b(true));
    }

    #[test]
    fn test_is_empty_false() {
        let m = map_of(&[("k", i(1))]);
        assert_eq!(call("TLang.Map.isEmpty", &[m]).unwrap(), b(false));
    }

    // -- has ------------------------------------------------------------------
    #[test]
    fn test_has_existing_key() {
        let m = map_of(&[("name", s("alice"))]);
        assert_eq!(call("TLang.Map.has", &[m, s("name")]).unwrap(), b(true));
    }

    #[test]
    fn test_has_missing_key() {
        let m = map_of(&[("name", s("alice"))]);
        assert_eq!(call("TLang.Map.has", &[m, s("age")]).unwrap(), b(false));
    }

    // -- get ------------------------------------------------------------------
    #[test]
    fn test_get_existing() {
        let m = map_of(&[("x", i(42))]);
        assert_eq!(call("TLang.Map.get", &[m, s("x")]).unwrap(), i(42));
    }

    #[test]
    fn test_get_missing_error() {
        let m = map_of(&[("x", i(1))]);
        assert!(call("TLang.Map.get", &[m, s("y")]).is_err());
    }

    // -- getOrDefault ---------------------------------------------------------
    #[test]
    fn test_get_or_default_present() {
        let m = map_of(&[("k", i(7))]);
        assert_eq!(
            call("TLang.Map.getOrDefault", &[m, s("k"), i(0)]).unwrap(),
            i(7)
        );
    }

    #[test]
    fn test_get_or_default_absent() {
        assert_eq!(
            call("TLang.Map.getOrDefault", &[empty_map(), s("k"), i(99)]).unwrap(),
            i(99)
        );
    }

    // -- set ------------------------------------------------------------------
    #[test]
    fn test_set_new_key() {
        let m = map_of(&[("a", i(1))]);
        let result = call("TLang.Map.set", &[m, s("b"), i(2)]).unwrap();
        assert_eq!(result, map_of(&[("a", i(1)), ("b", i(2))]));
    }

    #[test]
    fn test_set_overwrites_key() {
        let m = map_of(&[("a", i(1))]);
        let result = call("TLang.Map.set", &[m, s("a"), i(99)]).unwrap();
        assert_eq!(result, map_of(&[("a", i(99))]));
    }

    #[test]
    fn test_set_does_not_mutate_original() {
        let original = map_of(&[("a", i(1))]);
        let _new = call("TLang.Map.set", &[original.clone(), s("b"), i(2)]).unwrap();
        // original must still have only one entry
        assert_eq!(original, map_of(&[("a", i(1))]));
    }

    // -- remove ---------------------------------------------------------------
    #[test]
    fn test_remove_existing() {
        let m = map_of(&[("a", i(1)), ("b", i(2))]);
        let result = call("TLang.Map.remove", &[m, s("a")]).unwrap();
        assert_eq!(result, map_of(&[("b", i(2))]));
    }

    #[test]
    fn test_remove_missing_is_noop() {
        let m = map_of(&[("a", i(1))]);
        let result = call("TLang.Map.remove", &[m.clone(), s("z")]).unwrap();
        assert_eq!(result, m);
    }

    // -- keys / values / entries ----------------------------------------------
    #[test]
    fn test_keys_sorted() {
        let m = map_of(&[("b", i(2)), ("a", i(1)), ("c", i(3))]);
        // BTreeMap is already sorted, so keys come out alphabetically
        assert_eq!(
            call("TLang.Map.keys", &[m]).unwrap(),
            lst(vec![s("a"), s("b"), s("c")])
        );
    }

    #[test]
    fn test_values_sorted_by_key() {
        let m = map_of(&[("b", i(2)), ("a", i(1))]);
        assert_eq!(
            call("TLang.Map.values", &[m]).unwrap(),
            lst(vec![i(1), i(2)])
        );
    }

    #[test]
    fn test_entries() {
        let m = map_of(&[("a", i(1)), ("b", i(2))]);
        assert_eq!(
            call("TLang.Map.entries", &[m]).unwrap(),
            lst(vec![lst(vec![s("a"), i(1)]), lst(vec![s("b"), i(2)]),])
        );
    }

    // -- merge ----------------------------------------------------------------
    #[test]
    fn test_merge_disjoint() {
        let a = map_of(&[("x", i(1))]);
        let b = map_of(&[("y", i(2))]);
        assert_eq!(
            call("TLang.Map.merge", &[a, b]).unwrap(),
            map_of(&[("x", i(1)), ("y", i(2))])
        );
    }

    #[test]
    fn test_merge_override_wins() {
        let base = map_of(&[("k", i(1))]);
        let over = map_of(&[("k", i(99))]);
        assert_eq!(
            call("TLang.Map.merge", &[base, over]).unwrap(),
            map_of(&[("k", i(99))])
        );
    }

    // -- fromLists ------------------------------------------------------------
    #[test]
    fn test_from_lists() {
        let ks = Value::List(vec![s("a"), s("b")]);
        let vs = Value::List(vec![i(1), i(2)]);
        assert_eq!(
            call("TLang.Map.fromLists", &[ks, vs]).unwrap(),
            map_of(&[("a", i(1)), ("b", i(2))])
        );
    }

    #[test]
    fn test_from_lists_length_mismatch_error() {
        let ks = Value::List(vec![s("a")]);
        let vs = Value::List(vec![i(1), i(2)]);
        assert!(call("TLang.Map.fromLists", &[ks, vs]).is_err());
    }

    #[test]
    fn test_from_lists_non_string_key_error() {
        let ks = Value::List(vec![i(1)]);
        let vs = Value::List(vec![s("v")]);
        assert!(call("TLang.Map.fromLists", &[ks, vs]).is_err());
    }

    // -- toList ---------------------------------------------------------------
    #[test]
    fn test_to_list_same_as_entries() {
        let m = map_of(&[("a", i(1))]);
        assert_eq!(
            call("TLang.Map.toList", &[m.clone()]).unwrap(),
            call("TLang.Map.entries", &[m]).unwrap()
        );
    }

    // -- arity / type errors --------------------------------------------------
    #[test]
    fn test_wrong_arity_size() {
        assert!(call("TLang.Map.size", &[]).is_err());
    }

    #[test]
    fn test_wrong_type_size() {
        assert!(call("TLang.Map.size", &[Value::Int(1)]).is_err());
    }

    // -- unknown method -------------------------------------------------------
    #[test]
    fn test_unknown_method() {
        assert!(call("TLang.Map.nonExistent", &[]).is_err());
    }
}
