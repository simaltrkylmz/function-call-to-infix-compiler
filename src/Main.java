import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = null;

        // If we got a filename as argument, read from file
        if (args.length > 0) {
            try {
                File file = new File(args[0]);
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + args[0]);
                return;
            }
        } else {
            // Otherwise read from standard input
            scanner = new Scanner(System.in);
        }

        // Process each line
        int line = 1;
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            // Skip empty lines and comments
            if (input.isEmpty() || input.startsWith("//")) {
                continue;
            }

            // Try to parse and transform the expression
            try {
                System.out.println("Input: " + input);

                Lexer lexer = new Lexer(input);
                Parser parser = new Parser(lexer);
                Parser.Expression result = parser.parse();

                System.out.println("Output: " + result.text);
                System.out.println();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println();
            }

            line++;
        }

        scanner.close();
    }
}