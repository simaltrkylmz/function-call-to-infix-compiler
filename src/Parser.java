// The Parser class takes the tokens from the Lexer and builds the mathematical expression.
class Parser {

    private Lexer lexer;
    private Token currentToken;

    Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken(); // We get the first token immediately.
    }

    /* This helper class holds the result of a parsed expression.
       It stores the text (like "5 + 3") and its precedence level.
       I needed precedence to decide if I should put parenthesis or not. */
    static class Expression {
        String text;
        int precedence;

        Expression(String text, int precedence) {
            this.text = text;
            this.precedence = precedence;
        }
    }

    // This method checks if the current token matches the expected type.
    // If it matches, it moves to the next token.
    private void cont(TokenTypes type) {
        if (currentToken.type == type) {
            currentToken = lexer.nextToken();
        } else {
            // I throw an exception if the token is not what we expected.
            throw new RuntimeException("Expected " + type + " but found " + currentToken.type);
        }
    }

    // This is the main entry point we will call from Main class.
    // It ensures that ALL input is consumed.
    public Expression parse() {
        Expression root = parseExpression(); // Get the result

        // Check if there is garbage left at the end (like extra parenthesis)
        if (currentToken.type != TokenTypes.EOF) {
            throw new RuntimeException("Unexpected token at the end: " + currentToken.value);
        }

        return root;
    }

    // This is the main method to start parsing. It checks if we have a function or a number.
    public Expression parseExpression() {
        if (currentToken.type == TokenTypes.FUNCTION) {
            return parseFunction(); // If it's a function, we go to parseFunction method.
        }
        else if (currentToken.type == TokenTypes.NUMBER) {
            String num = currentToken.value;
            cont(TokenTypes.NUMBER);
            // I used AI for that part. It said that I should give numbers very high precedence so that they won't get any parenthesis.
            return new Expression(num, 100);
        }
        else {
            throw new RuntimeException("Expected function or number");
        }
    }

    // This method handles function calls.
    private Expression parseFunction() {
        String functionName = currentToken.value;
        cont(TokenTypes.FUNCTION);
        cont(TokenTypes.LPAREN); // We expect a left parenthesis after function name.

        // This is for tern function. It is special because it has 3 arguments, the rest has 2.
        if (functionName.equals("tern")) {
            Expression x = parseExpression(); // First operand.
            cont(TokenTypes.COMMA); // There must be a comma.
            Expression y = parseExpression(); // Second operand.
            cont(TokenTypes.COMMA); // There must be a comma.
            Expression z = parseExpression(); // Third operand.
            cont(TokenTypes.RPAREN); // There must be a right parenthesis.

            int myPrec = getPrecedence(functionName); // Tern operator's precedence is 1.

            //Ternary is right-associative. That means a?b:c?d:e is this: a?b:(c?d:e).
            String xText = x.text;
            if (x.precedence <= myPrec) { // If x has lower or equal precedence, it needs parenthesis.
                xText = "(" + xText + ")";
            }

            String yText = y.text;
            if (y.precedence < myPrec) { // Middle part needs parentheses if lower precedence.
                yText = "(" + yText + ")";
            }

            String zText = z.text;
            //Right side doesn't need parentheses if equal (right-associative). I asked AI about the equality in associativity. And used it throughout the code.
            if (z.precedence < myPrec) {
                zText = "(" + zText + ")";
            }

            String result = xText + "?" + yText + ":" + zText; // tern(x,y,z) becomes x ? y : z
            return new Expression(result, myPrec);
        }

        // This is for other functions. They have 2 arguments.
        Expression left = parseExpression(); // Left operand.
        cont(TokenTypes.COMMA); // There must be a comma.
        Expression right = parseExpression(); // Right operand.
        cont(TokenTypes.RPAREN); //There must be a right parenthesis.

        String operator = getOperator(functionName); // It finds the operator.
        int myPrecedence = getPrecedence(functionName); // It gets its precedence.
        boolean rightAssociative = isRightAssociative(functionName); // It checks if the operator is right associative.

        String leftText = left.text;
        String rightText = right.text;

        /* This is the logic for parenthesis. If the child expression has lower precedence, it needs parenthesis.
           But if precedences are equal, we check associativity. */

        if (rightAssociative) { // This is for right associative functions.
            if (left.precedence <= myPrecedence) { // If left one's precedence is equal to right one, it needs parenthesis.
                leftText = "(" + leftText + ")";
            }
            if (right.precedence < myPrecedence) { // The right side doesn't need parenthesis if their precedences are equal.
                rightText = "(" + rightText + ")";
            }
        }
        else { // For left associative operators:
            if (left.precedence < myPrecedence) { // Left doesn't need parenthesis if equal.
                leftText = "(" + leftText + ")";
            }
            if (right.precedence <= myPrecedence) { // The right side needs parenthesis if equal. sub(5, sub(2,1)) -> 5-(2-1).
                rightText = "(" + rightText + ")";
            }
        }

        String result = leftText + operator  + rightText;
        return new Expression(result, myPrecedence);
    }

    // This method gets the operator symbol for each function.
    private String getOperator(String name) {
        return switch (name) {
            case "add" -> "+";
            case "sub" -> "-";
            case "mul" -> "*";
            case "div" -> "/";
            case "mod" -> "%";
            case "pow" -> "^";
            default -> throw new RuntimeException("Unknown function: " + name);
        };
    }

    // This method gets the precedence levels based on what's in the assignment.
    private int getPrecedence(String name) {
        if (name.equals("tern")) return 1;
        if (name.equals("add") || name.equals("sub")) return 2;
        if (name.equals("mul") || name.equals("div") || name.equals("mod")) return 3;
        if (name.equals("pow")) return 4;
        return 0;
    }

    // This method looks at the right associative functions. Due to the assignment, only '^' and '?:' are right-associative.
    private boolean isRightAssociative(String name) {
        return name.equals("pow") || name.equals("tern");
    }
}