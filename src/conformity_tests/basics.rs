// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use crate::runtime::Value;

use super::helpers::{run_conformity};

// -----------------------------------------------------------------------
// HelloWorld
// -----------------------------------------------------------------------

/// `conformity/hello_world/Main.tlang`
///
/// The simplest possible program: bind a string to a variable, print it,
/// and return it.  Verifies that:
/// - `Terminal.println` emits the string followed by a newline.
/// - `return` propagates a `String` value back to the caller.
#[test]
fn conformity_hello_world() {
    let run = run_conformity("hello_world");

    assert_eq!(
        run.output, "Hello, World!\n",
        "stdout should be the greeting followed by a newline"
    );
    assert_eq!(
        run.return_value,
        Value::String("Hello, World!".to_string()),
        "main should return the greeting string"
    );
}

// -----------------------------------------------------------------------
// If program
// -----------------------------------------------------------------------

/// `conformity/if_program/Main.tlang`
///
/// Exercises every branch-related feature:
/// - Simple `if` without `else` (early return pattern).
/// - `if / else` with `&&` (logical AND of two comparisons).
/// - `if / else` with `||` (logical OR).
/// - `%` (modulo) inside an `if` condition.
/// - All six comparison operators: `<`, `==`, `>=`, `<=`, `>`, `!=` (via
///   `describe_parity` using `%` and `==`).
#[test]
fn conformity_if_program() {
    let run = run_conformity("if_program");

    let expected_output = concat!(
        "negative\n",
        "zero\n",
        "positive\n",
        "in range\n",
        "out of range\n",
        "at least one is big\n",
        "both are small\n",
        "even\n",
        "odd\n",
    );

    assert_eq!(run.output, expected_output, "if program output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "if program should return \"done\""
    );
}

// -----------------------------------------------------------------------
// For program
// -----------------------------------------------------------------------

/// `conformity/for_program/Main.tlang`
///
/// Exercises all three `for` forms:
/// - `for ( var in list )` — iterate a list variable.
/// - `for ( var in [...] )` — iterate an inline list literal.
/// - `for ( var start to end )` — inclusive range (1..=5, sum = 15).
/// - `for ( var start until end )` — exclusive range (0..5, count = 5).
/// - Nested `for` + `if` (print only even numbers from a list).
#[test]
fn conformity_for_program() {
    let run = run_conformity("for_program");

    let expected_output = concat!(
        "150\n",   // sum_list([10, 20, 30, 40, 50])
        "alpha\n", // for-in literal strings
        "beta\n", "gamma\n", "15\n", // sum_to(5) = 1+2+3+4+5
        "5\n",  // count_until(5) = 5 iterations
        "2\n",  // print_evens([1..6]): even numbers
        "4\n", "6\n",
    );

    assert_eq!(run.output, expected_output, "for program output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "for program should return \"done\""
    );
}

// -----------------------------------------------------------------------
// Function calls
// -----------------------------------------------------------------------

/// `conformity/function_calls/Main.tlang`
///
/// Exercises every calling convention:
///
/// - Call with direct integer literals:      `add(3, 7)`
/// - Call with variables:                    `add(x, y)`
/// - Call with a mix of variable + literal:  `multiply(x, 3)`
/// - Nested calls (return value as argument):`add(multiply(2,5), add(3,4))`
/// - Call with a string literal:             `greet("World")`
/// - Call with a string variable:            `greet(who)`
/// - Return value used in `if` condition:    `is_even(8)` / `is_even(7)`
/// - Return value of call used as argument in another call:
///                                           `max(multiply(3,4), add(10,1))`
/// - Multi-step accumulation through a helper:`apply_twice(10, 5)`
#[test]
fn conformity_function_calls() {
    let run = run_conformity("function_calls");

    let expected_output = concat!(
        "10\n",            // add(3, 7)             = 10
        "10\n",            // add(x=6, y=4)         = 10
        "18\n",            // multiply(x=6, 3)      = 18
        "17\n",            // add(multiply(2,5), add(3,4)) = add(10,7) = 17
        "Hello, World!\n", // greet("World")
        "Hello, TLang!\n", // greet("TLang")
        "8 is even\n",     // is_even(8) = true  → then branch
        "7 is odd\n",      // is_even(7) = false → else branch
        "12\n",            // max(multiply(3,4)=12, add(10,1)=11) = 12
        "20\n",            // apply_twice(10, 5) = 10+5+5 = 20
    );

    assert_eq!(
        run.output, expected_output,
        "function calls output mismatch"
    );
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "function_calls should return \"done\""
    );
}

/// Arithmetic conformity test — verifies:
/// - All five arithmetic operators: `+  -  *  /  %`
/// - Correct operator precedence (`*/%` before `+-`)
/// - Parentheses overriding default precedence
/// - Deeply nested bracket expressions
/// - Unary negation via `0 - expr`
/// - Function-call results used as operands in larger expressions
/// - Complex combinations of all of the above
#[test]
fn conformity_arithmetic() {
    let run = run_conformity("arithmetic");

    let expected_output = concat!(
        // Basic operators
        "5\n",  // 2 + 3
        "6\n",  // 10 - 4
        "21\n", // 3 * 7
        "5\n",  // 20 / 4
        "2\n",  // 17 % 5
        // Operator precedence
        "7\n",  // 1 + 2*3          (not 9)
        "4\n",  // 10 - 2*3         (not 24)
        "26\n", // 2*3 + 4*5
        "8\n",  // 20/4 + 3
        "80\n", // 100 - 50/5*2
        "12\n", // 10 + 17%5
        "1\n",  // 3*7 % 4
        // Parentheses override precedence
        "9\n",  // (1+2)*3
        "56\n", // (10-2)*(3+4)
        "3\n",  // ((2+3)*(4-1))/5
        "46\n", // 2*(3+4*5)
        "4\n",  // (6+2)/(3-1)
        // Deeply nested
        "90\n", // (1 + 2*(3 + 4*(5-2)) - 1) * (2+1)
        "14\n", // (3+4)*(2+(5-1)*3)/(1+6)
        "80\n", // ((4+6)*(3+2)-(8/2+6))*(1+1)
        // Negation
        "-5\n", // 0-5
        "-5\n", // 0-(3+2)
        "-7\n", // 0-(2*4-1)
        // Function calls as operands
        "16\n", // square(4)
        "27\n", // cube(3)
        "14\n", // double(7)
        "9\n",  // half(18)
        "10\n", // square(3)+1
        "30\n", // double(5)*3
        "9\n",  // square(2+1)
        "18\n", // double(square(3))
        "17\n", // cube(2)+square(3)
        "7\n",  // abs_diff(10,3)
        "7\n",  // abs_diff(3,10)
        "5\n",  // clamp(5,1,10)
        "1\n",  // clamp(0,1,10)
        "10\n", // clamp(15,1,10)
        "25\n", // sum_of_squares(3,4)
        // Functions combined with arithmetic
        "25\n", // square(3)+square(4)
        "12\n", // (square(5)-1)/(double(3)-4)
        "44\n", // cube(2)+double(3)*(square(4)-10)
        // Very complex
        "10\n", // ((square(3)+1)*(cube(2)-double(2)))/abs_diff(10,4)+square(2)
        "45\n", // sum_of_squares(3,4)*2 - clamp(square(3)+1,5,20) + half(cube(2)+2)
    );

    assert_eq!(run.output, expected_output, "arithmetic output mismatch");
    assert_eq!(
        run.return_value,
        Value::String("done".to_string()),
        "arithmetic should return \"done\""
    );
}

// -----------------------------------------------------------------------
// Hash in string
// -----------------------------------------------------------------------

/// `conformity/hash_in_string/Main.tlang`
///
/// Tests that hash characters (#) inside string literals are handled
/// correctly and don't interfere with parsing.
#[test]
fn conformity_hash_in_string() {
    let run = run_conformity("hash_in_string");

    assert_eq!(
        run.output, "color: #ff0000; /* ── box drawing ── */\n",
        "stdout should contain the string with hash characters"
    );
    assert_eq!(
        run.return_value,
        Value::String("color: #ff0000; /* ── box drawing ── */".to_string()),
        "main should return the string with hash characters"
    );
}
