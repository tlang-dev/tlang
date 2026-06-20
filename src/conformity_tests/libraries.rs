// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use crate::runtime::Value;

use super::helpers::run_conformity;

// -----------------------------------------------------------------------
// String library
// -----------------------------------------------------------------------

/// `conformity/string_lib/Main.tlang`
///
/// Exercises every `TLang.String` method:
/// - `length`, `isEmpty`
/// - `toUpperCase`, `toLowerCase`
/// - `trim`
/// - `contains`, `startsWith`, `endsWith`
/// - `equals`, `equalsIgnoreCase`
/// - `compare` (returns -1 / 0 / 1)
/// - `indexOf`, `lastIndexOf`
/// - `substring`, `slice` (including negative indices)
/// - `split` (result iterated with for-in)
/// - `replace`, `replaceAll`
/// - `concat`, `repeat`
/// - `charAt`, `charCodeAt`, `fromCharCode`
/// - `lines`, `words` (results iterated with for-in)
///
/// Also verifies immutability: every call returns a new value and the
/// source string passed as argument is never modified.
#[test]
fn conformity_string_lib() {
    let run = run_conformity("string_lib");

    let expected_output = concat!(
        // length
        "5\n", // length("hello")
        "0\n", // length("")
        // isEmpty
        "true\n",  // isEmpty("")
        "false\n", // isEmpty("x")
        // toUpperCase / toLowerCase
        "HELLO\n", // toUpperCase("hello")
        "world\n", // toLowerCase("WORLD")
        // trim
        "hi\n", // trim("  hi  ")
        // contains
        "true\n",  // contains("foobar", "oob")
        "false\n", // contains("foobar", "xyz")
        // startsWith / endsWith
        "true\n",  // startsWith("foobar", "foo")
        "true\n",  // endsWith("foobar", "bar")
        "false\n", // startsWith("foobar", "bar") → false
        // equals / equalsIgnoreCase
        "true\n",  // equals("abc", "abc")
        "false\n", // equals("abc", "ABC")
        "true\n",  // equalsIgnoreCase("Hello", "hElLo")
        // compare
        "-1\n", // compare("apple", "banana")
        "0\n",  // compare("same", "same")
        "1\n",  // compare("zoo", "ant")
        // indexOf / lastIndexOf
        "6\n",  // indexOf("hello world", "world")
        "-1\n", // indexOf("hello", "xyz")
        "4\n",  // lastIndexOf("abcabc", "bc")
        "-1\n", // lastIndexOf("hello", "xyz")
        // substring
        "ell\n",   // substring("hello", 1, 4)
        "hello\n", // substring("hello", 0, 5)
        // slice
        "ell\n", // slice("hello", 1, 4)
        "ll\n",  // slice("hello", -3, -1)
        // split ("a,b,c" → iterate a, b, c)
        "a\n", "b\n", "c\n",     // split with no separator match → single element
        "alone\n", // replace / replaceAll
        "Xbbaa\n", // replace first "aa"
        "XbbX\n",  // replaceAll "aa"
        // replace with no match is identity
        "hello\n",  // concat
        "foobar\n", // concat("foo", "bar")
        "bar\n",    // concat("", "bar")
        "foo\n",    // concat("foo", "")
        // repeat
        "ababab\n", // repeat("ab", 3)
        "x\n",      // repeat("x", 1)
        "\n",       // repeat("x", 0) → empty string → blank line
        // charAt
        "h\n", // charAt("hello", 0)
        "o\n", // charAt("hello", 4)
        // charCodeAt
        "65\n", // charCodeAt("A", 0)
        "97\n", // charCodeAt("a", 0)
        // fromCharCode
        "A\n", // fromCharCode(65)
        "a\n", // fromCharCode(97)
        // lines("x\ny\nz") → iterate x, y, z
        "x\n", "y\n", "z\n",
        // words("  hello   world  ") → iterate hello, world
        "hello\n", "world\n",
    );

    assert_eq!(run.output, expected_output, "string_lib output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "string_lib should return \"done\""
    );
}

// -----------------------------------------------------------------------
// List library
// -----------------------------------------------------------------------

/// `conformity/list_lib/Main.tlang`
///
/// Exercises every `TLang.List` method:
/// - `create`, `of`, `size`, `isEmpty`
/// - `get` (positive and negative indices)
/// - `first`, `last`
/// - `push`, `prepend` — immutability verified (original size unchanged)
/// - `set`, `remove`
/// - `tail`, `init`
/// - `slice` (positive and negative indices)
/// - `concat`, `reverse`
/// - `contains`, `indexOf`
/// - `join`
/// - `flatten`, `distinct`
/// - `sort` (Int and String)
/// - `take`, `drop`
/// - `zip`
/// - for-in to confirm the original list is intact throughout
#[test]
fn conformity_list_lib() {
    let run = run_conformity("list_lib");

    let expected_output = concat!(
        // create / isEmpty / size on empty list
        "true\n", // isEmpty(create())
        "0\n",    // size(create())
        // of
        "false\n", // isEmpty(of(3,1,4,1,5))
        "5\n",     // size(of(3,1,4,1,5))
        // get positive and negative
        "3\n", // get(nums, 0)
        "4\n", // get(nums, 2)
        "5\n", // get(nums, -1)
        "1\n", // get(nums, -2)
        // first / last
        "3\n", // first(nums)
        "5\n", // last(nums)
        // push — new list has 6 elements, last = 9; original still 5
        "6\n", // size(pushed)
        "9\n", // last(pushed)
        "5\n", // size(nums) — unchanged
        // prepend — new list has 6 elements, first = 0; original still 5
        "6\n", // size(prepended)
        "0\n", // first(prepended)
        "5\n", // size(nums) — unchanged
        // set — updated[1] = 99; original nums[1] still 1
        "99\n", // get(updated, 1)
        "1\n",  // get(nums, 1) — unchanged
        // remove — removes index 2 (value 4); size drops to 4; new[2] = 1
        "4\n", // size(removed)
        "1\n", // get(removed, 2)
        // tail — all but first; size = 4, first = 1
        "4\n", // size(tail(nums))
        "1\n", // first(tail(nums))
        // tail of single-element list is empty
        "0\n", // size(tail(of(42)))
        // init — all but last; size = 4, last = 1
        "4\n", // size(init(nums))
        "1\n", // last(init(nums))
        // slice [1, 4) → [1, 4, 1]; size=3, [0]=1, [2]=1
        "3\n", // size(slice(nums, 1, 4))
        "1\n", // get(sl, 0)
        "1\n", // get(sl, 2)
        // negative-index slice [-3,-1) → indices 2..4 → [4,1]; size=2, [0]=4
        "2\n", // size(slice(nums, -3, -1))
        "4\n", // get(sl2, 0)
        // concat [1,2] ++ [3,4] → size=4, join="1,2,3,4"
        "4\n",
        "1,2,3,4\n",
        // reverse [1,2] → [2,1]; original a=[1,2] size=2
        "2\n", // get(rev, 0)
        "1\n", // get(rev, 1)
        "2\n", // size(a) — original unchanged
        // contains
        "true\n",  // contains(nums, 4)
        "false\n", // contains(nums, 7)
        // indexOf
        "2\n",  // indexOf(nums, 4)  → index 2
        "-1\n", // indexOf(nums, 7)
        // join
        "hello world\n", // join(["hello","world"], " ")
        "1-2-3\n",       // join([1,2,3], "-")
        // flatten: [[1,2], 3, [4]] → [1,2,3,4]
        "4\n",
        "1,2,3,4\n",
        // distinct: [1,2,1,3,2] → [1,2,3]
        "3\n",
        "1,2,3\n",
        // sort ints
        "1,1,2,3,4,5,9\n",
        // sort strings
        "apple,banana,cherry\n",
        // take
        "3,1,4\n", // take(nums, 3)
        "0\n",     // size(take(nums, 0))
        "5\n",     // size(take(nums, 100)) → clamped to 5
        // drop
        "1,5\n", // drop(nums, 3)
        "5\n",   // size(drop(nums, 0))
        "0\n",   // size(drop(nums, 100)) → clamped to empty
        // zip
        "3\n",   // size(zip([1,2,3], ["a","b","c"]))
        "1:a\n", // join(get(zipped, 0), ":")
        "3:c\n", // join(get(zipped, 2), ":")
        // zip stops at shorter list
        "1\n", // size(zip([1,2,3], ["x"]))
        // for-in verifying original nums = [3,1,4,1,5]
        "3\n",
        "1\n",
        "4\n",
        "1\n",
        "5\n",
    );

    assert_eq!(run.output, expected_output, "list_lib output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "list_lib should return \"done\""
    );
}

// -----------------------------------------------------------------------
// Map library
// -----------------------------------------------------------------------

/// `conformity/map_lib/Main.tlang`
///
/// Exercises every `TLang.Map` method:
/// - `create`, `of`, `size`, `isEmpty`
/// - `has`, `get`, `getOrDefault`
/// - `set` — immutability verified (original size unchanged, key absent)
/// - `remove` — missing key is a no-op
/// - `keys`, `values` (BTreeMap order = alphabetical by key)
/// - `entries` (list of [key, value] pairs)
/// - `merge` (overrides win; originals unchanged)
/// - `fromLists`
/// - `toList` (alias for entries)
/// - for-in over `keys` result to confirm all keys are present
#[test]
fn conformity_map_lib() {
    let run = run_conformity("map_lib");

    let expected_output = concat!(
        // create / isEmpty / size
        "true\n", // isEmpty(create())
        "0\n",    // size(create())
        // of("name","alice","age","30") — BTreeMap: age < name
        "false\n", // isEmpty(m)
        "2\n",     // size(m)
        // has
        "true\n",  // has(m, "name")
        "false\n", // has(m, "email")
        // get
        "alice\n", // get(m, "name")
        "30\n",    // get(m, "age")
        // getOrDefault
        "alice\n", // getOrDefault(m, "name", "unknown")
        "none\n",  // getOrDefault(m, "email", "none")
        // set — m2 has 3 entries; m still has 2; email present in m2, absent in m
        "3\n",                 // size(m2)
        "2\n",                 // size(m)
        "alice@example.com\n", // get(m2, "email")
        "false\n",             // has(m, "email")
        // overwrite existing key — m3.name = "bob"; m.name still "alice"
        "bob\n",
        "alice\n",
        // remove — m4 = m2 without "age": size 2, no "age"; m2 still 3
        "2\n",     // size(m4)
        "false\n", // has(m4, "age")
        "3\n",     // size(m2) — unchanged
        // remove missing key is no-op — m5 = m without "ghost": size 2
        "2\n",
        // keys (alphabetical: "age" < "name")
        "2\n",
        "age,name\n",
        // values (key-sorted: age→"30", name→"alice")
        "2\n",
        "30,alice\n",
        // entries — 2 pairs: [age,30] and [name,alice]
        "2\n",
        "age=30\n",
        "name=alice\n",
        // merge: base={x:1,y:2}, over={y:99,z:3} → {x:1,y:99,z:3}; size=3
        "3\n",
        "1\n",  // get(merged, "x")
        "99\n", // get(merged, "y") — override wins
        "3\n",  // get(merged, "z")
        // base and over are unchanged
        "2\n", // size(base)
        "2\n", // size(over)
        // fromLists(["a","b","c"], ["1","2","3"]) → {a:1,b:2,c:3}
        "3\n",
        "1\n", // get(built, "a")
        "2\n", // get(built, "b")
        "3\n", // get(built, "c")
        // toList (alias for entries) — 3 pairs
        "3\n",
        // for-in over keys(built) = ["a","b","c"]
        "a\n",
        "b\n",
        "c\n",
    );

    assert_eq!(run.output, expected_output, "map_lib output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "map_lib should return \"done\""
    );
}

// -----------------------------------------------------------------------

/// `conformity/method_call_syntax/Main.tlang`
///
/// Verifies that `receiver.method(args)` syntax works as an equivalent to
/// `Alias.method(receiver, args)` for List, Map, and String values.
#[test]
fn conformity_method_call_syntax() {
    let run = run_conformity("method_call_syntax");

    let expected_output = concat!(
        // --- List method-call syntax ---
        "5\n",         // nums.size()
        "false\n",     // nums.isEmpty()
        "1\n",         // nums.first()
        "5\n",         // nums.last()
        "true\n",      // nums.contains(3)
        "false\n",     // nums.contains(9)
        "2\n",         // nums.indexOf(3)
        "1,2,3,4,5\n", // nums.join(",")
        // push
        "6\n", // pushed.size()
        "6\n", // pushed.last()
        // reverse
        "5\n", // reversed.first()
        "1\n", // reversed.last()
        // slice(1,4) → [2,3,4]
        "3\n",     // sliced.size()
        "2-3-4\n", // sliced.join("-")
        // take(3) → [1,2,3]
        "1,2,3\n", // taken.join(",")
        // drop(2) → [3,4,5]
        "3,4,5\n", // dropped.join(",")
        // concat [1,2] ++ [3,4]
        "1,2,3,4\n", // cat.join(",")
        // --- String method-call syntax ---
        "13\n",            // greeting.length()
        "HELLO, WORLD!\n", // greeting.toUpperCase()
        "hello, world!\n", // greeting.toLowerCase()
        "true\n",          // greeting.contains("World")
        "true\n",          // greeting.startsWith("Hello")
        "true\n",          // greeting.endsWith("!")
        "7\n",             // greeting.indexOf("World")
        "World\n",         // greeting.substring(7, 12)
        "trim me\n",       // padded.trim()
        "3\n",             // parts.size()
        "a|b|c\n",         // parts.join("|")
        // --- Map method-call syntax ---
        // M.of("name","Alice","age","30") — BTreeMap: age < name
        "2\n",     // m.size()
        "false\n", // m.isEmpty()
        "true\n",  // m.has("name")
        "false\n", // m.has("missing")
        "Alice\n", // m.get("name")
        "30\n",    // m.get("age")
        // set → adds "city"
        "3\n",     // m2.size()
        "Paris\n", // m2.get("city")
        // remove "age"
        "1\n",     // m3.size()
        "false\n", // m3.has("age")
        // keys of m (age, name) — 2 entries
        "2\n", // keys.size()
    );

    assert_eq!(
        run.output, expected_output,
        "method_call_syntax output mismatch"
    );
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "method_call_syntax should return \"done\""
    );
}

// -----------------------------------------------------------------------

/// `conformity/naming_lib/Main.tlang`
#[test]
fn conformity_naming_lib() {
    let run = run_conformity("naming_lib");

    let expected_output = concat!(
        // toCamelCase
        "myFieldName\n", // from snake
        "myFieldName\n", // from PascalCase
        "myFieldName\n", // from SCREAMING_SNAKE
        "myFieldName\n", // from kebab
        "myFieldName\n", // from spaces
        // toPascalCase
        "MyFieldName\n",
        "MyFieldName\n",
        "MyFieldName\n",
        "MyFieldName\n",
        // toSnakeCase
        "my_field_name\n",
        "my_field_name\n",
        "my_field_name\n",
        "my_field_name\n",
        // toScreamingSnake
        "MY_FIELD_NAME\n",
        "MY_FIELD_NAME\n",
        "MY_FIELD_NAME\n",
        // toKebabCase
        "my-field-name\n",
        "my-field-name\n",
        "my-field-name\n",
        // toDotCase
        "my.field.name\n",
        "my.field.name\n",
        // toTitleCase
        "My Field Name\n",
        "My Field Name\n",
        // capitalize / decapitalize
        "Hello\n",
        "World\n",
        "hello\n",
        "world\n",
        // pluralize
        "entities\n",
        "services\n",
        "buses\n",
        "classes\n",
        "leaves\n",
        // singularize
        "entity\n",
        "service\n",
        "bus\n",
        "class\n",
        "leaf\n",
        // words (List prints as [a, b, c])
        "[my, field, name]\n",
        "[my, field, name]\n",
        "[my, field, name]\n",
    );

    assert_eq!(run.output, expected_output, "naming_lib output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "naming_lib should return \"done\""
    );
}

// -----------------------------------------------------------------------

/// `conformity/math_lib/Main.tlang`
#[test]
fn conformity_math_lib() {
    let run = run_conformity("math_lib");

    let expected_output = concat!(
        // abs
        "5\n", "5\n", "0\n", // min
        "3\n", "3\n", "-1\n", // max
        "7\n", "7\n", "1\n", // clamp
        "5\n", "0\n", "10\n", "0\n", "10\n", // pow
        "1024\n", "27\n", "1\n", "0\n", "7\n", // sqrt
        "3\n", "4\n", "1\n", "0\n", "10\n", // isEven
        "true\n", "false\n", "true\n", // isOdd
        "false\n", "true\n", "false\n", // sign
        "1\n", "-1\n", "0\n", // gcd
        "4\n", "25\n", "1\n", "5\n", "5\n", // lcm
        "12\n", "15\n", "0\n", "7\n",
    );

    assert_eq!(run.output, expected_output, "math_lib output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "math_lib should return \"done\""
    );
}

// -----------------------------------------------------------------------

/// `conformity/int_lib/Main.tlang`
#[test]
fn conformity_int_lib() {
    let run = run_conformity("int_lib");

    let expected_output = concat!(
        // Int.toString
        "0\n",
        "42\n",
        "-99\n",
        // Int.parse
        "0\n",
        "123\n",
        "-7\n",
        // Int.toHex / fromHex
        "ff\n",
        "0\n",
        "10\n",
        "255\n",
        "255\n",
        "16\n",
        // Int.toBinary / fromBinary
        "1010\n",
        "0\n",
        "11111111\n",
        "10\n",
        "255\n",
        // Int.toOctal / fromOctal
        "10\n",
        "10\n",
        "377\n",
        "8\n",
        "255\n",
        // Int.range (exclusive)
        "[0, 1, 2, 3, 4]\n",
        "[]\n",
        "[]\n",
        // Int.rangeTo (inclusive)
        "[1, 2, 3, 4]\n",
        "[5]\n",
        // Int.minValue / maxValue
        "-2147483648\n",
        "2147483647\n",
        // Int.abs
        "10\n",
        "10\n",
        "0\n",
        // Int.clamp
        "5\n",
        "0\n",
        "10\n",
        // Int.toFloat / toDouble
        "3.0\n",
        "7.0\n",
        // Long.minValue / maxValue
        "-9223372036854775808\n",
        "9223372036854775807\n",
        // Long.toHex / toBinary
        "ff\n",
        "1000\n",
        // Long.range
        "[10, 11, 12]\n",
        // Long.abs / clamp
        "999\n",
        "50\n",
        // Long.toDouble
        "42.0\n",
    );

    assert_eq!(run.output, expected_output, "int_lib output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "int_lib should return \"done\""
    );
}

// -----------------------------------------------------------------------

/// `conformity/float_lib/Main.tlang`
#[test]
fn conformity_float_lib() {
    let run = run_conformity("float_lib");

    let expected_output = concat!(
        // Double.fromInt / toInt
        "42.0\n",
        "-7.0\n",
        "42\n",
        // Double.parse / toString
        "3.141592653589793\n",
        "-2.5\n",
        // Double.floor / ceil / round on 3.7
        "3.0\n",
        "4.0\n",
        "4.0\n",
        // Double.floor / ceil / round on 3.2
        "3.0\n",
        "4.0\n",
        "3.0\n",
        // Double.abs
        "4.5\n",
        "3.7\n",
        // Double.min / max
        "3.2\n",
        "3.7\n",
        // Double.sqrt
        "2.0\n",
        "3.0\n",
        // Double.pow
        "1024.0\n",
        "1.4142135623730951\n",
        // Double.add / sub / mul / div (1.5 and 2.5)
        "4.0\n",
        "1.0\n",
        "3.75\n",
        "1.6666666666666667\n",
        // Double.pi / e
        "3.141592653589793\n",
        "2.718281828459045\n",
        // Double.isNaN / isInfinite
        "true\n",
        "false\n",
        "true\n",
        "false\n",
        // Float.pi / e (f32 precision widened to f64)
        "3.1415927410125732\n",
        "2.7182817459106445\n",
        // Float.fromInt / toInt
        "5.0\n",
        "5\n",
        // Float.floor / ceil / round on 3.7
        "3.0\n",
        "4.0\n",
        "4.0\n",
        // Float.sqrt(4)
        "2.0\n",
        // Float.isNaN / isInfinite
        "true\n",
        "false\n",
        "true\n",
        "false\n",
    );

    assert_eq!(run.output, expected_output, "float_lib output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "float_lib should return \"done\""
    );
}
