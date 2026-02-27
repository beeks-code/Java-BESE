import java.util.Scanner;

public class DivisionException {
    public static double divide(double numerator, double denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        return numerator / denominator;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Division with Exception Handling ===");

        double[][] testCases = {
            {10, 2},
            {15, 0},   // Will throw exception
            {-20, 4},
            {0, 5},
            {7, 0}     // Will throw exception
        };

        for (double[] pair : testCases) {
            try {
                double result = divide(pair[0], pair[1]);
                System.out.printf("%.1f / %.1f = %.4f%n", pair[0], pair[1], result);
            } catch (ArithmeticException e) {
                System.out.printf("%.1f / %.1f → ERROR: %s%n", pair[0], pair[1], e.getMessage());
            }
        }

        // Interactive input
        System.out.println("\n--- Enter your own values (type 'quit' to exit) ---");
        while (scanner.hasNextLine()) {
            System.out.print("Numerator: ");
            String numStr = scanner.nextLine().trim();
            if (numStr.equalsIgnoreCase("quit")) break;

            System.out.print("Denominator: ");
            String denStr = scanner.nextLine().trim();
            if (denStr.equalsIgnoreCase("quit")) break;

            try {
                double num = Double.parseDouble(numStr);
                double den = Double.parseDouble(denStr);
                System.out.printf("Result: %.4f%n%n", divide(num, den));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numeric values.");
            } catch (ArithmeticException e) {
                System.out.println("Math Error: " + e.getMessage() + "\n");
            }
        }
        scanner.close();
    }
}
