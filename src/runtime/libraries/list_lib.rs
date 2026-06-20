// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! TLang.List — immutable list library.
//!
//! Every function returns a *new* value; no in-place mutation occurs.
//! The underlying runtime type is `Value::List(Vec<Value>)`.

use super::super::{Runtime, RuntimeError, Value, value_to_string};

// ── dispatch ─────────────────────────────────────────────────────────────────

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.List.create" => create(args),
        "TLang.List.of" => of(args),
        "TLang.List.size" => size(args),
        "TLang.List.isEmpty" => is_empty(args),
        "TLang.List.get" => get(args),
        "TLang.List.first" => first(args),
        "TLang.List.last" => last(args),
        "TLang.List.push" => push(args),
        "TLang.List.prepend" => prepend(args),
        "TLang.List.set" => set(args),
        "TLang.List.remove" => remove(args),
        "TLang.List.tail" => tail(args),
        "TLang.List.init" => init(args),
        "TLang.List.slice" => slice(args),
        "TLang.List.concat" => concat(args),
        "TLang.List.reverse" => reverse(args),
        "TLang.List.contains" => contains(args),
        "TLang.List.indexOf" => index_of(args),
        "TLang.List.join" => join(args),
        "TLang.List.flatten" => flatten(args),
        "TLang.List.distinct" => distinct(args),
        "TLang.List.sort" => sort(args),
        "TLang.List.take" => take(args),
        "TLang.List.drop" => drop(args),
        "TLang.List.zip" => zip(args),
        "TLang.List.enumerate" => enumerate(args),
        _ => Err(RuntimeError(format!(
            "unknown List library function `{target}`"
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
        "TLang.List.map" => map(runtime, args).map(Some),
        "TLang.List.filter" => filter(runtime, args).map(Some),
        "TLang.List.forEach" => for_each(runtime, args).map(Some),
        "TLang.List.reduce" => reduce(runtime, args).map(Some),
        "TLang.List.any" => any(runtime, args).map(Some),
        "TLang.List.all" => all(runtime, args).map(Some),
        "TLang.List.find" => find(runtime, args).map(Some),
        "TLang.List.flatMap" => flat_map(runtime, args).map(Some),
        "TLang.List.sortBy" => sort_by(runtime, args).map(Some),
        "TLang.List.groupBy" => group_by(runtime, args).map(Some),
        "TLang.List.count" => count_hof(runtime, args).map(Some),
        _ => Ok(None),
    }
}

// ── helpers ───────────────────────────────────────────────────────────────────

fn check_arity(args: &[Value], expected: usize, name: &str) -> Result<(), RuntimeError> {
    if args.len() != expected {
        Err(RuntimeError(format!(
            "TLang.List.{name} expects exactly {expected} argument(s), got {}",
            args.len()
        )))
    } else {
        Ok(())
    }
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

fn int_arg(args: &[Value], index: usize, ctx: &str) -> Result<i64, RuntimeError> {
    args.get(index)
        .ok_or_else(|| RuntimeError(format!("{ctx}: missing argument at index {index}")))
        .and_then(|v| match v {
            Value::Int(n) => Ok(*n),
            other => Err(RuntimeError(format!(
                "{ctx}: argument {index} must be Int, got {}",
                super::super::value_type_name(other)
            ))),
        })
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

fn normalise_index(idx: i64, len: usize, ctx: &str) -> Result<usize, RuntimeError> {
    let len_i = len as i64;
    let normalised = if idx < 0 { len_i + idx } else { idx };
    if normalised < 0 || normalised >= len_i {
        Err(RuntimeError(format!(
            "{ctx}: index {idx} out of bounds for list of length {len}"
        )))
    } else {
        Ok(normalised as usize)
    }
}

// ── implementations ───────────────────────────────────────────────────────────

/// `create() -> List`  — empty list.
fn create(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 0, "create")?;
    Ok(Value::List(vec![]))
}

/// `of(v1, v2, …) -> List`  — variadic list literal helper.
fn of(args: &[Value]) -> Result<Value, RuntimeError> {
    Ok(Value::List(args.to_vec()))
}

/// `size(list) -> Int`  — number of elements.
fn size(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "size")?;
    let list = expect_list(&args[0], "TLang.List.size")?;
    Ok(Value::Int(list.len() as i64))
}

/// `isEmpty(list) -> Bool`
fn is_empty(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "isEmpty")?;
    let list = expect_list(&args[0], "TLang.List.isEmpty")?;
    Ok(Value::Bool(list.is_empty()))
}

/// `get(list, index) -> Value`  — 0-based; negative indices count from the end.
fn get(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "get")?;
    let list = expect_list(&args[0], "TLang.List.get")?;
    let idx = int_arg(args, 1, "TLang.List.get")?;
    let i = normalise_index(idx, list.len(), "TLang.List.get")?;
    Ok(list[i].clone())
}

/// `first(list) -> Value`  — first element; error if empty.
fn first(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "first")?;
    let list = expect_list(&args[0], "TLang.List.first")?;
    list.first()
        .cloned()
        .ok_or_else(|| RuntimeError("TLang.List.first: list is empty".to_string()))
}

/// `last(list) -> Value`  — last element; error if empty.
fn last(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "last")?;
    let list = expect_list(&args[0], "TLang.List.last")?;
    list.last()
        .cloned()
        .ok_or_else(|| RuntimeError("TLang.List.last: list is empty".to_string()))
}

/// `push(list, value) -> List`  — new list with `value` appended at the end.
fn push(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "push")?;
    let list = expect_list(&args[0], "TLang.List.push")?;
    let mut new = list.clone();
    new.push(args[1].clone());
    Ok(Value::List(new))
}

/// `prepend(list, value) -> List`  — new list with `value` inserted at the front.
fn prepend(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "prepend")?;
    let list = expect_list(&args[0], "TLang.List.prepend")?;
    let mut new = vec![args[1].clone()];
    new.extend_from_slice(list);
    Ok(Value::List(new))
}

/// `set(list, index, value) -> List`  — new list with element at `index` replaced.
fn set(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 3, "set")?;
    let list = expect_list(&args[0], "TLang.List.set")?;
    let idx = int_arg(args, 1, "TLang.List.set")?;
    let i = normalise_index(idx, list.len(), "TLang.List.set")?;
    let mut new = list.clone();
    new[i] = args[2].clone();
    Ok(Value::List(new))
}

/// `remove(list, index) -> List`  — new list without the element at `index`.
fn remove(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "remove")?;
    let list = expect_list(&args[0], "TLang.List.remove")?;
    let idx = int_arg(args, 1, "TLang.List.remove")?;
    let i = normalise_index(idx, list.len(), "TLang.List.remove")?;
    let mut new = list.clone();
    new.remove(i);
    Ok(Value::List(new))
}

/// `tail(list) -> List`  — all elements except the first; empty if list has ≤ 1 element.
fn tail(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "tail")?;
    let list = expect_list(&args[0], "TLang.List.tail")?;
    Ok(Value::List(list.iter().skip(1).cloned().collect()))
}

/// `init(list) -> List`  — all elements except the last; empty if list has ≤ 1 element.
fn init(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "init")?;
    let list = expect_list(&args[0], "TLang.List.init")?;
    let n = list.len().saturating_sub(1);
    Ok(Value::List(list[..n].to_vec()))
}

/// `slice(list, start, end) -> List`
///   Elements from `start` (inclusive) to `end` (exclusive), both 0-based.
///   Negative indices count from the end.
///   Out-of-range boundaries are clamped rather than erroring.
fn slice(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 3, "slice")?;
    let list = expect_list(&args[0], "TLang.List.slice")?;
    let len = list.len() as i64;

    let mut start = int_arg(args, 1, "TLang.List.slice")?;
    let mut end = int_arg(args, 2, "TLang.List.slice")?;

    if start < 0 {
        start = (len + start).max(0);
    }
    if end < 0 {
        end = (len + end).max(0);
    }
    let start = (start.min(len)) as usize;
    let end = (end.min(len)) as usize;

    if start >= end {
        return Ok(Value::List(vec![]));
    }
    Ok(Value::List(list[start..end].to_vec()))
}

/// `concat(a, b) -> List`  — new list that is the concatenation of `a` and `b`.
fn concat(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "concat")?;
    let a = expect_list(&args[0], "TLang.List.concat")?;
    let b = expect_list(&args[1], "TLang.List.concat")?;
    let mut new = a.clone();
    new.extend_from_slice(b);
    Ok(Value::List(new))
}

/// `reverse(list) -> List`  — new list with elements in reverse order.
fn reverse(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "reverse")?;
    let list = expect_list(&args[0], "TLang.List.reverse")?;
    let mut new = list.clone();
    new.reverse();
    Ok(Value::List(new))
}

/// `contains(list, value) -> Bool`  — true if the list contains `value`.
fn contains(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "contains")?;
    let list = expect_list(&args[0], "TLang.List.contains")?;
    Ok(Value::Bool(list.contains(&args[1])))
}

/// `indexOf(list, value) -> Int`  — first index of `value`, or -1 if absent.
fn index_of(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "indexOf")?;
    let list = expect_list(&args[0], "TLang.List.indexOf")?;
    let idx = list
        .iter()
        .position(|v| v == &args[1])
        .map(|i| i as i64)
        .unwrap_or(-1);
    Ok(Value::Int(idx))
}

/// `join(list, separator) -> String`
///   Converts every element to its string representation and joins with `separator`.
fn join(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "join")?;
    let list = expect_list(&args[0], "TLang.List.join")?;
    let sep = str_arg(args, 1, "TLang.List.join")?;
    let result = list
        .iter()
        .map(value_to_string)
        .collect::<Vec<_>>()
        .join(&sep);
    Ok(Value::String(result))
}

/// `flatten(list) -> List`  — one-level deep flattening.
///   Every element that is itself a `List` is inlined; others are kept as-is.
fn flatten(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "flatten")?;
    let list = expect_list(&args[0], "TLang.List.flatten")?;
    let mut result: Vec<Value> = Vec::new();
    for item in list {
        match item {
            Value::List(inner) => result.extend_from_slice(inner),
            other => result.push(other.clone()),
        }
    }
    Ok(Value::List(result))
}

/// `distinct(list) -> List`  — new list with duplicates removed (first occurrence kept).
fn distinct(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "distinct")?;
    let list = expect_list(&args[0], "TLang.List.distinct")?;
    let mut seen: Vec<&Value> = Vec::new();
    let mut result: Vec<Value> = Vec::new();
    for item in list {
        if !seen.contains(&item) {
            seen.push(item);
            result.push(item.clone());
        }
    }
    Ok(Value::List(result))
}

/// `sort(list) -> List`  — new list sorted in ascending order.
///   All elements must be of the same primitive type (Int, Bool, or String).
///   Mixed-type or Leaf/Map lists return an error.
fn sort(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "sort")?;
    let list = expect_list(&args[0], "TLang.List.sort")?;
    let mut new = list.clone();
    // Determine sort key: all-Int, all-Bool, or all-String.
    new.sort_by(value_cmp);
    // Validate that we did not mix incompatible types (sort_by silently falls back
    // to Equal for incompatible pairs — do a post-check instead).
    if let Some(err) = list.windows(2).find_map(|w| {
        if !values_comparable(&w[0], &w[1]) {
            Some(RuntimeError(format!(
                "TLang.List.sort: cannot compare {} and {}",
                super::super::value_type_name(&w[0]),
                super::super::value_type_name(&w[1])
            )))
        } else {
            None
        }
    }) {
        return Err(err);
    }
    Ok(Value::List(new))
}

fn values_comparable(a: &Value, b: &Value) -> bool {
    matches!(
        (a, b),
        (Value::Int(_), Value::Int(_))
            | (Value::Bool(_), Value::Bool(_))
            | (Value::String(_), Value::String(_))
    )
}

fn value_cmp(a: &Value, b: &Value) -> std::cmp::Ordering {
    match (a, b) {
        (Value::Int(x), Value::Int(y)) => x.cmp(y),
        (Value::Bool(x), Value::Bool(y)) => x.cmp(y),
        (Value::String(x), Value::String(y)) => x.cmp(y),
        _ => std::cmp::Ordering::Equal,
    }
}

/// `take(list, n) -> List`  — first `n` elements; clamped to list length.
fn take(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "take")?;
    let list = expect_list(&args[0], "TLang.List.take")?;
    let n = int_arg(args, 1, "TLang.List.take")?;
    if n < 0 {
        return Err(RuntimeError(format!(
            "TLang.List.take: count must be >= 0, got {n}"
        )));
    }
    let n = (n as usize).min(list.len());
    Ok(Value::List(list[..n].to_vec()))
}

/// `drop(list, n) -> List`  — list without the first `n` elements; clamped to list length.
fn drop(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "drop")?;
    let list = expect_list(&args[0], "TLang.List.drop")?;
    let n = int_arg(args, 1, "TLang.List.drop")?;
    if n < 0 {
        return Err(RuntimeError(format!(
            "TLang.List.drop: count must be >= 0, got {n}"
        )));
    }
    let n = (n as usize).min(list.len());
    Ok(Value::List(list[n..].to_vec()))
}

/// `zip(a, b) -> List<List>`
///   Pairs up elements from two lists; result length equals the shorter list.
///   Each element is `[a_i, b_i]`.
fn enumerate(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 1, "enumerate")?;
    let list = expect_list(&args[0], "TLang.List.enumerate")?;
    let result = list
        .iter()
        .enumerate()
        .map(|(i, v)| Value::List(vec![Value::Int(i as i64), v.clone()]))
        .collect();
    Ok(Value::List(result))
}

fn zip(args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "zip")?;
    let a = expect_list(&args[0], "TLang.List.zip")?;
    let b = expect_list(&args[1], "TLang.List.zip")?;
    let result = a
        .iter()
        .zip(b.iter())
        .map(|(x, y)| Value::List(vec![x.clone(), y.clone()]))
        .collect();
    Ok(Value::List(result))
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
            "TLang.List.{name}: argument {idx} must be a lambda, got {}",
            super::super::value_type_name(other)
        ))),
        None => Err(RuntimeError(format!(
            "TLang.List.{name}: missing lambda argument at position {idx}"
        ))),
    }
}

/// `map(list, fn) -> List`  — transform each element via `fn(item) -> Value`.
fn map(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "map")?;
    let list = expect_list(&args[0], "TLang.List.map")?.clone();
    let lam = expect_lambda(args, 1, "map")?.clone();
    let mut out = Vec::with_capacity(list.len());
    for item in list {
        out.push(runtime.call_lambda(&lam, vec![item])?);
    }
    Ok(Value::List(out))
}

/// `filter(list, fn) -> List`  — keep elements where `fn(item) -> Bool`.
fn filter(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "filter")?;
    let list = expect_list(&args[0], "TLang.List.filter")?.clone();
    let lam = expect_lambda(args, 1, "filter")?.clone();
    let mut out = Vec::new();
    for item in list {
        match runtime.call_lambda(&lam, vec![item.clone()])? {
            Value::Bool(true) => out.push(item),
            Value::Bool(false) => {}
            other => {
                return Err(RuntimeError(format!(
                    "TLang.List.filter: predicate must return Bool, got {}",
                    super::super::value_type_name(&other)
                )));
            }
        }
    }
    Ok(Value::List(out))
}

/// `forEach(list, fn) -> Unit`  — call `fn(item)` for each element.
fn for_each(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "forEach")?;
    let list = expect_list(&args[0], "TLang.List.forEach")?.clone();
    let lam = expect_lambda(args, 1, "forEach")?.clone();
    for item in list {
        runtime.call_lambda(&lam, vec![item])?;
    }
    Ok(Value::Unit)
}

/// `reduce(list, initial, fn) -> Value`  — fold left with `fn(acc, item) -> acc`.
fn reduce(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 3, "reduce")?;
    let list = expect_list(&args[0], "TLang.List.reduce")?.clone();
    let initial = args[1].clone();
    let lam = expect_lambda(args, 2, "reduce")?.clone();
    let mut acc = initial;
    for item in list {
        acc = runtime.call_lambda(&lam, vec![acc, item])?;
    }
    Ok(acc)
}

/// `any(list, fn) -> Bool`  — true if `fn(item)` is true for at least one element.
fn any(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "any")?;
    let list = expect_list(&args[0], "TLang.List.any")?.clone();
    let lam = expect_lambda(args, 1, "any")?.clone();
    for item in list {
        match runtime.call_lambda(&lam, vec![item])? {
            Value::Bool(true) => return Ok(Value::Bool(true)),
            Value::Bool(false) => {}
            other => {
                return Err(RuntimeError(format!(
                    "TLang.List.any: predicate must return Bool, got {}",
                    super::super::value_type_name(&other)
                )));
            }
        }
    }
    Ok(Value::Bool(false))
}

/// `all(list, fn) -> Bool`  — true if `fn(item)` is true for every element.
fn all(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "all")?;
    let list = expect_list(&args[0], "TLang.List.all")?.clone();
    let lam = expect_lambda(args, 1, "all")?.clone();
    for item in list {
        match runtime.call_lambda(&lam, vec![item])? {
            Value::Bool(false) => return Ok(Value::Bool(false)),
            Value::Bool(true) => {}
            other => {
                return Err(RuntimeError(format!(
                    "TLang.List.all: predicate must return Bool, got {}",
                    super::super::value_type_name(&other)
                )));
            }
        }
    }
    Ok(Value::Bool(true))
}

/// `find(list, fn) -> Value?`  — first element where `fn(item)` is true, or Unit.
fn find(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "find")?;
    let list = expect_list(&args[0], "TLang.List.find")?.clone();
    let lam = expect_lambda(args, 1, "find")?.clone();
    for item in list {
        match runtime.call_lambda(&lam, vec![item.clone()])? {
            Value::Bool(true) => return Ok(item),
            Value::Bool(false) => {}
            other => {
                return Err(RuntimeError(format!(
                    "TLang.List.find: predicate must return Bool, got {}",
                    super::super::value_type_name(&other)
                )));
            }
        }
    }
    Ok(Value::Unit)
}

/// `flatMap(list, fn) -> List`  — map each element to a list, then flatten one level.
fn flat_map(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "flatMap")?;
    let list = expect_list(&args[0], "TLang.List.flatMap")?.clone();
    let lam = expect_lambda(args, 1, "flatMap")?.clone();
    let mut out = Vec::new();
    for item in list {
        match runtime.call_lambda(&lam, vec![item])? {
            Value::List(sub) => out.extend(sub),
            other => out.push(other),
        }
    }
    Ok(Value::List(out))
}

/// `sortBy(list, fn) -> List`  — sort by key returned by `fn(item) -> String|Int`.
fn sort_by(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "sortBy")?;
    let list = expect_list(&args[0], "TLang.List.sortBy")?.clone();
    let lam = expect_lambda(args, 1, "sortBy")?.clone();
    // Collect (key, item) pairs, sort by key, then extract items.
    let mut keyed: Vec<(Value, Value)> = Vec::with_capacity(list.len());
    for item in list {
        let key = runtime.call_lambda(&lam, vec![item.clone()])?;
        keyed.push((key, item));
    }
    keyed.sort_by(|(a, _), (b, _)| value_cmp(a, b));
    Ok(Value::List(keyed.into_iter().map(|(_, v)| v).collect()))
}

/// `groupBy(list, fn) -> Map`  — group elements by string key from `fn(item) -> String`.
fn group_by(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    use std::collections::BTreeMap;
    check_arity(args, 2, "groupBy")?;
    let list = expect_list(&args[0], "TLang.List.groupBy")?.clone();
    let lam = expect_lambda(args, 1, "groupBy")?.clone();
    let mut map: BTreeMap<String, Value> = BTreeMap::new();
    for item in list {
        let key = runtime.call_lambda(&lam, vec![item.clone()])?;
        let key_str = match key {
            Value::String(s) => s,
            other => value_to_string(&other),
        };
        match map.get_mut(&key_str) {
            Some(Value::List(bucket)) => bucket.push(item),
            Some(_) => unreachable!(),
            None => {
                map.insert(key_str, Value::List(vec![item]));
            }
        }
    }
    Ok(Value::Map(map))
}

/// `count(list, fn) -> Int`  — count elements where `fn(item) -> Bool`.
fn count_hof(runtime: &mut Runtime<'_>, args: &[Value]) -> Result<Value, RuntimeError> {
    check_arity(args, 2, "count")?;
    let list = expect_list(&args[0], "TLang.List.count")?.clone();
    let lam = expect_lambda(args, 1, "count")?.clone();
    let mut n: i64 = 0;
    for item in list {
        match runtime.call_lambda(&lam, vec![item])? {
            Value::Bool(true) => n += 1,
            Value::Bool(false) => {}
            other => {
                return Err(RuntimeError(format!(
                    "TLang.List.count: predicate must return Bool, got {}",
                    super::super::value_type_name(&other)
                )));
            }
        }
    }
    Ok(Value::Int(n))
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
    fn lst(items: Vec<Value>) -> Value {
        Value::List(items)
    }

    // -- create ---------------------------------------------------------------
    #[test]
    fn test_create_empty() {
        assert_eq!(call("TLang.List.create", &[]).unwrap(), lst(vec![]));
    }

    #[test]
    fn test_create_wrong_arity() {
        assert!(call("TLang.List.create", &[i(1)]).is_err());
    }

    // -- of -------------------------------------------------------------------
    #[test]
    fn test_of_zero() {
        assert_eq!(call("TLang.List.of", &[]).unwrap(), lst(vec![]));
    }

    #[test]
    fn test_of_mixed() {
        let result = call("TLang.List.of", &[i(1), s("hi"), b(true)]).unwrap();
        assert_eq!(result, lst(vec![i(1), s("hi"), b(true)]));
    }

    // -- size -----------------------------------------------------------------
    #[test]
    fn test_size() {
        assert_eq!(
            call("TLang.List.size", &[lst(vec![i(1), i(2)])]).unwrap(),
            i(2)
        );
    }

    #[test]
    fn test_size_empty() {
        assert_eq!(call("TLang.List.size", &[lst(vec![])]).unwrap(), i(0));
    }

    // -- isEmpty --------------------------------------------------------------
    #[test]
    fn test_is_empty_true() {
        assert_eq!(call("TLang.List.isEmpty", &[lst(vec![])]).unwrap(), b(true));
    }

    #[test]
    fn test_is_empty_false() {
        assert_eq!(
            call("TLang.List.isEmpty", &[lst(vec![i(1)])]).unwrap(),
            b(false)
        );
    }

    // -- get ------------------------------------------------------------------
    #[test]
    fn test_get_positive() {
        let l = lst(vec![i(10), i(20), i(30)]);
        assert_eq!(call("TLang.List.get", &[l, i(1)]).unwrap(), i(20));
    }

    #[test]
    fn test_get_negative() {
        let l = lst(vec![i(10), i(20), i(30)]);
        assert_eq!(call("TLang.List.get", &[l, i(-1)]).unwrap(), i(30));
    }

    #[test]
    fn test_get_out_of_bounds() {
        assert!(call("TLang.List.get", &[lst(vec![i(1)]), i(5)]).is_err());
    }

    // -- first / last ---------------------------------------------------------
    #[test]
    fn test_first() {
        assert_eq!(
            call("TLang.List.first", &[lst(vec![i(1), i(2)])]).unwrap(),
            i(1)
        );
    }

    #[test]
    fn test_last() {
        assert_eq!(
            call("TLang.List.last", &[lst(vec![i(1), i(2)])]).unwrap(),
            i(2)
        );
    }

    #[test]
    fn test_first_empty_error() {
        assert!(call("TLang.List.first", &[lst(vec![])]).is_err());
    }

    #[test]
    fn test_last_empty_error() {
        assert!(call("TLang.List.last", &[lst(vec![])]).is_err());
    }

    // -- push / prepend -------------------------------------------------------
    #[test]
    fn test_push() {
        let l = lst(vec![i(1), i(2)]);
        assert_eq!(
            call("TLang.List.push", &[l, i(3)]).unwrap(),
            lst(vec![i(1), i(2), i(3)])
        );
    }

    #[test]
    fn test_push_does_not_mutate_original() {
        let original = lst(vec![i(1)]);
        let new = call("TLang.List.push", &[original.clone(), i(2)]).unwrap();
        assert_eq!(original, lst(vec![i(1)]));
        assert_eq!(new, lst(vec![i(1), i(2)]));
    }

    #[test]
    fn test_prepend() {
        let l = lst(vec![i(2), i(3)]);
        assert_eq!(
            call("TLang.List.prepend", &[l, i(1)]).unwrap(),
            lst(vec![i(1), i(2), i(3)])
        );
    }

    // -- set ------------------------------------------------------------------
    #[test]
    fn test_set() {
        let l = lst(vec![i(1), i(2), i(3)]);
        assert_eq!(
            call("TLang.List.set", &[l, i(1), i(99)]).unwrap(),
            lst(vec![i(1), i(99), i(3)])
        );
    }

    #[test]
    fn test_set_negative_index() {
        let l = lst(vec![i(1), i(2), i(3)]);
        assert_eq!(
            call("TLang.List.set", &[l, i(-1), i(99)]).unwrap(),
            lst(vec![i(1), i(2), i(99)])
        );
    }

    // -- remove ---------------------------------------------------------------
    #[test]
    fn test_remove() {
        let l = lst(vec![i(1), i(2), i(3)]);
        assert_eq!(
            call("TLang.List.remove", &[l, i(1)]).unwrap(),
            lst(vec![i(1), i(3)])
        );
    }

    // -- tail / init ----------------------------------------------------------
    #[test]
    fn test_tail() {
        assert_eq!(
            call("TLang.List.tail", &[lst(vec![i(1), i(2), i(3)])]).unwrap(),
            lst(vec![i(2), i(3)])
        );
    }

    #[test]
    fn test_tail_single() {
        assert_eq!(
            call("TLang.List.tail", &[lst(vec![i(1)])]).unwrap(),
            lst(vec![])
        );
    }

    #[test]
    fn test_init() {
        assert_eq!(
            call("TLang.List.init", &[lst(vec![i(1), i(2), i(3)])]).unwrap(),
            lst(vec![i(1), i(2)])
        );
    }

    // -- slice ----------------------------------------------------------------
    #[test]
    fn test_slice_basic() {
        let l = lst(vec![i(0), i(1), i(2), i(3), i(4)]);
        assert_eq!(
            call("TLang.List.slice", &[l, i(1), i(4)]).unwrap(),
            lst(vec![i(1), i(2), i(3)])
        );
    }

    #[test]
    fn test_slice_negative() {
        let l = lst(vec![i(0), i(1), i(2), i(3), i(4)]);
        assert_eq!(
            call("TLang.List.slice", &[l, i(-3), i(-1)]).unwrap(),
            lst(vec![i(2), i(3)])
        );
    }

    #[test]
    fn test_slice_clamped() {
        let l = lst(vec![i(1), i(2)]);
        assert_eq!(
            call("TLang.List.slice", &[l, i(0), i(100)]).unwrap(),
            lst(vec![i(1), i(2)])
        );
    }

    // -- concat ---------------------------------------------------------------
    #[test]
    fn test_concat() {
        let a = lst(vec![i(1), i(2)]);
        let b = lst(vec![i(3), i(4)]);
        assert_eq!(
            call("TLang.List.concat", &[a, b]).unwrap(),
            lst(vec![i(1), i(2), i(3), i(4)])
        );
    }

    // -- reverse --------------------------------------------------------------
    #[test]
    fn test_reverse() {
        let l = lst(vec![i(1), i(2), i(3)]);
        assert_eq!(
            call("TLang.List.reverse", &[l]).unwrap(),
            lst(vec![i(3), i(2), i(1)])
        );
    }

    // -- contains / indexOf ---------------------------------------------------
    #[test]
    fn test_contains_true() {
        let l = lst(vec![i(1), i(2), i(3)]);
        assert_eq!(call("TLang.List.contains", &[l, i(2)]).unwrap(), b(true));
    }

    #[test]
    fn test_contains_false() {
        let l = lst(vec![i(1), i(2)]);
        assert_eq!(call("TLang.List.contains", &[l, i(9)]).unwrap(), b(false));
    }

    #[test]
    fn test_index_of_found() {
        let l = lst(vec![s("a"), s("b"), s("c")]);
        assert_eq!(call("TLang.List.indexOf", &[l, s("b")]).unwrap(), i(1));
    }

    #[test]
    fn test_index_of_not_found() {
        let l = lst(vec![s("a"), s("b")]);
        assert_eq!(call("TLang.List.indexOf", &[l, s("z")]).unwrap(), i(-1));
    }

    // -- join -----------------------------------------------------------------
    #[test]
    fn test_join() {
        let l = lst(vec![s("a"), s("b"), s("c")]);
        assert_eq!(
            call("TLang.List.join", &[l, s(", ")]).unwrap(),
            s("a, b, c")
        );
    }

    #[test]
    fn test_join_mixed_types() {
        let l = lst(vec![i(1), i(2), i(3)]);
        assert_eq!(call("TLang.List.join", &[l, s("-")]).unwrap(), s("1-2-3"));
    }

    // -- flatten --------------------------------------------------------------
    #[test]
    fn test_flatten() {
        let l = lst(vec![lst(vec![i(1), i(2)]), i(3), lst(vec![i(4)])]);
        assert_eq!(
            call("TLang.List.flatten", &[l]).unwrap(),
            lst(vec![i(1), i(2), i(3), i(4)])
        );
    }

    // -- distinct -------------------------------------------------------------
    #[test]
    fn test_distinct() {
        let l = lst(vec![i(1), i(2), i(1), i(3), i(2)]);
        assert_eq!(
            call("TLang.List.distinct", &[l]).unwrap(),
            lst(vec![i(1), i(2), i(3)])
        );
    }

    // -- sort -----------------------------------------------------------------
    #[test]
    fn test_sort_ints() {
        let l = lst(vec![i(3), i(1), i(4), i(1), i(5)]);
        assert_eq!(
            call("TLang.List.sort", &[l]).unwrap(),
            lst(vec![i(1), i(1), i(3), i(4), i(5)])
        );
    }

    #[test]
    fn test_sort_strings() {
        let l = lst(vec![s("banana"), s("apple"), s("cherry")]);
        assert_eq!(
            call("TLang.List.sort", &[l]).unwrap(),
            lst(vec![s("apple"), s("banana"), s("cherry")])
        );
    }

    // -- take / drop ----------------------------------------------------------
    #[test]
    fn test_take() {
        let l = lst(vec![i(1), i(2), i(3), i(4)]);
        assert_eq!(
            call("TLang.List.take", &[l, i(2)]).unwrap(),
            lst(vec![i(1), i(2)])
        );
    }

    #[test]
    fn test_take_more_than_size() {
        let l = lst(vec![i(1), i(2)]);
        assert_eq!(
            call("TLang.List.take", &[l, i(100)]).unwrap(),
            lst(vec![i(1), i(2)])
        );
    }

    #[test]
    fn test_drop() {
        let l = lst(vec![i(1), i(2), i(3), i(4)]);
        assert_eq!(
            call("TLang.List.drop", &[l, i(2)]).unwrap(),
            lst(vec![i(3), i(4)])
        );
    }

    #[test]
    fn test_drop_more_than_size() {
        let l = lst(vec![i(1), i(2)]);
        assert_eq!(call("TLang.List.drop", &[l, i(100)]).unwrap(), lst(vec![]));
    }

    // -- zip ------------------------------------------------------------------
    #[test]
    fn test_zip_equal_length() {
        let a = lst(vec![i(1), i(2), i(3)]);
        let b = lst(vec![s("a"), s("b"), s("c")]);
        assert_eq!(
            call("TLang.List.zip", &[a, b]).unwrap(),
            lst(vec![
                lst(vec![i(1), s("a")]),
                lst(vec![i(2), s("b")]),
                lst(vec![i(3), s("c")]),
            ])
        );
    }

    #[test]
    fn test_zip_shorter_second() {
        let a = lst(vec![i(1), i(2), i(3)]);
        let b = lst(vec![s("x")]);
        assert_eq!(
            call("TLang.List.zip", &[a, b]).unwrap(),
            lst(vec![lst(vec![i(1), s("x")])])
        );
    }

    // -- arity errors ---------------------------------------------------------
    #[test]
    fn test_wrong_arity_size() {
        assert!(call("TLang.List.size", &[]).is_err());
    }

    #[test]
    fn test_wrong_type_size() {
        assert!(call("TLang.List.size", &[Value::Int(1)]).is_err());
    }

    // -- unknown method -------------------------------------------------------
    #[test]
    fn test_unknown_method() {
        assert!(call("TLang.List.nonExistent", &[]).is_err());
    }
}
