English | [Türkçe](README.tr.md)

# Function-Call to Infix Compiler

A small source-to-source compiler in Java. It translates nested function-call
expressions like `add(5, mul(3, 2))` into infix math (`5+3*2`) using as few
parentheses as possible.

## Features

- **Lexer** for signed numbers, fractions, exponents, whitespace and `/* block comments */`
- **Recursive descent parser**
- Operator **precedence and associativity**, including right-associative `^` and `?:`
- **Minimal parentheses** in the output
- Error messages for invalid input

## Supported functions

| Function | Output | Precedence |
|---|---|---|
| `tern(a, b, c)` | `a?b:c` | 1 (lowest) |
| `add`, `sub` | `+`, `-` | 2 |
| `mul`, `div`, `mod` | `*`, `/`, `%` | 3 |
| `pow` | `^` | 4 (highest) |

`^` and `?:` are right-associative; all other operators are left-associative.

## Examples

| Input | Output |
|---|---|
| `add(5, mul(3, 2))` | `5+3*2` |
| `add(5, mul(3, sub(10, pow(6, 4))))` | `5+3*(10-6^4)` |
| `mul(add(1, 2), 3)` | `(1+2)*3` |
| `sub(5, sub(2, 1))` | `5-(2-1)` |
| `pow(2, pow(3, 2))` | `2^3^2` |
| `pow(pow(2, 3), 2)` | `(2^3)^2` |
| `tern(1, 2, tern(3, 4, 5))` | `1?2:3?4:5` |
| `tern(1, tern(2, 3, 4), 5)` | `1?2?3:4:5` |
| `add(1, /* comment */ 2)` | `1+2` |
| `add(5,)` | `Error: Expected function or number` |
| `foo(1, 2)` | `Error: Invalid function name` |

## How it works

The lexer turns the input into tokens. A recursive descent parser reads them,
and every parsed piece carries its text and its precedence. When combining two
pieces, the parser adds parentheses only if the child binds weaker than its
parent, or ties in the wrong direction for the operator's associativity.

| File | Role |
|---|---|
| `src/Token.java` | Token types, `Token` class and the `Lexer` |
| `src/Parser.java` | Recursive descent parser and parenthesis logic |
| `src/Main.java` | Reads expressions (from a file or standard input) and prints the results |
| `src/input.txt` | Sample input |

## How to run

Requires Java 14 or newer (the code uses switch expressions).

```bash
javac -d out src/*.java
java -cp out Main src/input.txt
```

Without an argument, the program reads expressions from standard input, one per line.

## Background

Written for the *Formal Languages and Compilers* course at Brno University of
Technology (VUT). I wrote the lexer, the parser and the parenthesis logic
myself. AI tools were used only for small suggestions: whitespace escape
characters (`\t`, `\n`, `\r`), using `RuntimeException` for errors, and the
equality case in the associativity rules.

## Known limitations

- An unterminated block comment is skipped silently instead of reporting an error.
- A negative number as the left operand of `^` prints without parentheses
  (`pow(-2, 2)` → `-2^2`), which usually reads as -(2²) in standard math.
