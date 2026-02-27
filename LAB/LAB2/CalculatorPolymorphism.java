interface Calculator {
    double add(double a, double b);
    double subtract(double a, double b);
    double multiply(double a, double b);
    double divide(double a, double b);
}

class BasicCalculator implements Calculator {
    @Override
    public double add(double a, double b) { return a + b; }

    @Override
    public double subtract(double a, double b) { return a - b; }

    @Override
    public double multiply(double a, double b) { return a * b; }

    @Override
    public double divide(double a, double b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        return a / b;
    }

    public String getType() { return "Basic Calculator"; }
}

class ScientificCalculator extends BasicCalculator {
    public double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    public double squareRoot(double n) {
        if (n < 0) throw new ArithmeticException("Cannot take square root of negative number");
        return Math.sqrt(n);
    }

    public double logarithm(double n) {
        if (n <= 0) throw new ArithmeticException("Logarithm undefined for non-positive numbers");
        return Math.log10(n);
    }

    public double sine(double degrees) { return Math.sin(Math.toRadians(degrees)); }
    public double cosine(double degrees) { return Math.cos(Math.toRadians(degrees)); }

    @Override
    public String getType() { return "Scientific Calculator"; }
}

public class CalculatorPolymorphism {
    static void runBasicOps(Calculator calc, double a, double b) {
        System.out.printf("  %s + %s = %.4f%n", a, b, calc.add(a, b));
        System.out.printf("  %s - %s = %.4f%n", a, b, calc.subtract(a, b));
        System.out.printf("  %s x %s = %.4f%n", a, b, calc.multiply(a, b));
        System.out.printf("  %s / %s = %.4f%n", a, b, calc.divide(a, b));
    }

    public static void main(String[] args) {
        BasicCalculator basic = new BasicCalculator();
        ScientificCalculator scientific = new ScientificCalculator();

        // Polymorphic usage via interface
        Calculator[] calcs = { basic, scientific };

        for (Calculator calc : calcs) {
            System.out.println("\n=== " + ((BasicCalculator) calc).getType() + " ===");
            runBasicOps(calc, 10, 4);
        }

        System.out.println("\n=== Scientific Extended Operations ===");
        System.out.printf("  2^10      = %.4f%n", scientific.power(2, 10));
        System.out.printf("  sqrt(144) = %.4f%n", scientific.squareRoot(144));
        System.out.printf("  log(1000) = %.4f%n", scientific.logarithm(1000));
        System.out.printf("  sin(90°)  = %.4f%n", scientific.sine(90));
        System.out.printf("  cos(0°)   = %.4f%n", scientific.cosine(0));
    }
}
