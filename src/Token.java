//Firstly, I declared the names of the token types using enum class in Java.
enum TokenTypes {
    FUNCTION, //token type for function
    NUMBER, //token type for number
    LPAREN, //token type for left parenthesis
    RPAREN, //token type for right parenthesis
    COMMA, //token type for comma
    EOF, //token type for end of function (input is over)
}

/* The Token class represents a single token in the input.
  It contains the token type and its string value. */
public class Token {
    TokenTypes type;
    String value;

    Token(TokenTypes type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return type + "('" + value + "')";
    }
}
 /* Then, I created the Lexer class for Lexical analyzer for my compiler project.
    It converts input string into tokens. */
class Lexer{
    private String input;
    private int pos=0;
    Lexer(String input) {
        this.input = input;
    }
     private char currentChar() {
         if (pos >= input.length()) {
             return '\0'; // checks if the input is over
         }
         return input.charAt(pos);
     }

    private void positionIncreasing(){ // Method for increasing current position.
        pos+=1;
    }
    private char nextChar() { // It looks at the next char without changing the current position
        if (pos+1>=input.length()) { // AI gave me that idea because for some symbols, next char is important:
            return '\0';             // like, after "." there should be a digit-sequence etc.
        }
        return input.charAt(pos+1);
    }

    private boolean isDigit(char a){ // Method for checking if the current char is a digit.
        if (a<='9' && a>='0')
                return true;
        else return false;
    }
    private boolean isLetter(char a){ // Method for checking if the current char is a letter.
        if((a>='a' && a<='z') || (a>='A' && a<='Z'))
            return true;
        else return false;
    }
    private boolean isWhiteSpace(char a){ // Method for checking the whitespace
        if (a==' ' || a == '\t' || a == '\n' || a == '\r') //I used AI for the parts \t, \n and \r
            return true;                                   // Because I thought that only ' ' was enough.
        else return false;
    }
    private void skipWhitespace() {
        while (pos < input.length() && isWhiteSpace(input.charAt(pos))) {
            pos++;
        } }
    private void skipBlockComment() { // Method for skipping block comments.
        if (currentChar() == '/' && nextChar() == '*') {
            positionIncreasing();
            positionIncreasing(); // We have to increase position twice.
        }
        while (pos < input.length()) { // It should continue until finding "*/", and while doing that, position should increase.
            if (currentChar() == '*' && nextChar() == '/') {
                positionIncreasing();
                positionIncreasing();
                return;
            }
            positionIncreasing();
        }
    }
    /* I checked if the input is a number or not according to this grammar in the website:
    <number> ::= <optional-sign> <digit-sequence> <optional-fraction> <optional-exponent>*/
    // This method checks numbers
     private Token readNumber() {
         StringBuilder number = new StringBuilder();

         char ch=currentChar();

         // First, we look at the optional sign
         if(ch=='+' || ch=='-') {
             number.append(ch);
             positionIncreasing(); //if we find the sign, we add it to our variable "number" and we increase the position.
            ch=currentChar();
         }

         if(isDigit(ch)==false) { //If there is no digit after that, it throws an exception.
             throw new RuntimeException("Invalid number");
         } // I used AI for the throwing an exception idea. And I used that idea throughout my code when there's an error.


         while (isDigit(ch)){
             number.append(ch);
             positionIncreasing();
             ch=currentChar();
         }
         if(ch=='.') {          // Then, we check if there is "." After that, we check if there are digit/digits. (it must be)
             number.append(ch); // We add the "." to our current number variable.
             positionIncreasing(); // We increase the value of position
             ch = currentChar();
             if (isDigit(ch) == false) { // If there isn't a digit after ".", we throw an exception.
                 throw new RuntimeException("Digit is expected after '.'");
             }
             while (isDigit(ch) == true) { // We continue adding digits to our number variable using while loop.
                 number.append(ch);
                 positionIncreasing();
                 ch = currentChar();
             }
         }
             // Lastly, for numbers, we check if there is an exponent.
             if(ch=='e' || ch=='E'){
                 number.append('e');
                 positionIncreasing();
                 ch=currentChar();
                 if(ch=='+' || ch=='-'){ // There can be optional sign after "e".
                     number.append(ch);
                     positionIncreasing();
                     ch=currentChar();
                 }
                 if(isDigit(ch)==false) {
                     throw new RuntimeException("Digit is expected after e ");
                 }
                 while(isDigit(ch)==true){ // Again, we continue adding digits to our number variable using while loop.
                     number.append(ch);
                     positionIncreasing();
                     ch=currentChar();
                 }
             } // Our number token is done.
         return new Token(TokenTypes.NUMBER, number.toString());
     }
        private Token readFunction(){
         StringBuilder name = new StringBuilder();
        char ch=currentChar();

        while (isLetter(ch)==true) { // It will continue as long as we have a letter.
            name.append(ch); // It adds the letter to our name variable.
            positionIncreasing(); // It increases the position's value.
            ch = currentChar(); // It gets the new char.
        }
            String functionName = name.toString(); // It changes name's type into String.

            // Now we will check if our current String variable is one of our functions.
            if(functionName.equals("add") || functionName.equals("sub") ||
                    functionName.equals("mul") || functionName.equals("div") ||
                    functionName.equals("mod") || functionName.equals("pow") ||
                    functionName.equals("tern")){
                return new Token(TokenTypes.FUNCTION, functionName);
            }
           else throw new RuntimeException("Invalid function name");


    }

// This method returns the next token from the input, and also skips whitespace and block comments.
 public Token nextToken() {
     // Firstly, we look at the whitespaces.
     while (true) {
         skipWhitespace(); // It skips whitespaces continuously.

         if (currentChar() == '/' && nextChar() == '*') { // It checks if there is a block comment.
             skipBlockComment();
             continue; // After that, it continues.
         }
         break;
     }
     if(pos>=input.length()) {
         return new Token(TokenTypes.EOF, "");
     }
     char ch=currentChar(); // We check our current char.
     if(isDigit(ch)==true) { // If it's a digit, we call the method readNumber to check if it's a proper number.
         return readNumber();
     }
     if ((ch == '+' || ch == '-') && isDigit(nextChar())) { // If it's a signed number, again we call the method readNumber to check.
         return readNumber();
     }
     if (isLetter(ch)) { // If it's a letter, we call the method readFunction to check if it's one of our functions.
         return readFunction();
     }
     // After these, we check our single character tokens.
     if (ch == '(') {
         positionIncreasing();
         return new Token(TokenTypes.LPAREN, "(");
     }

     if (ch == ')') {
         positionIncreasing();
         return new Token(TokenTypes.RPAREN, ")");
     }

     if (ch == ',') {
         positionIncreasing();
         return new Token(TokenTypes.COMMA, ",");
     }

     // If it's none of them, we're throwing an exception. And we're done.
     throw new RuntimeException("Unexpected character: '" + ch + "'");
 }

     }