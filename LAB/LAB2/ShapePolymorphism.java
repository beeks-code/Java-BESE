abstract class Shape {
    abstract double calculateArea();
    abstract void display();
}

class Circle extends Shape {
    private double radius;
    Circle(double radius) { this.radius = radius; }

    @Override
    double calculateArea() { return Math.PI * radius * radius; }

    @Override
    void display() {
        System.out.printf("Circle    | Radius: %5.2f          | Area: %8.4f%n",
                radius, calculateArea());
    }
}

class Rectangle extends Shape {
    private double width, height;
    Rectangle(double width, double height) { this.width = width; this.height = height; }

    @Override
    double calculateArea() { return width * height; }

    @Override
    void display() {
        System.out.printf("Rectangle | Width: %5.2f, H: %5.2f | Area: %8.4f%n",
                width, height, calculateArea());
    }
}

class Triangle extends Shape {
    private double base, height;
    Triangle(double base, double height) { this.base = base; this.height = height; }

    @Override
    double calculateArea() { return 0.5 * base * height; }

    @Override
    void display() {
        System.out.printf("Triangle  | Base: %5.2f, H: %5.2f  | Area: %8.4f%n",
                base, height, calculateArea());
    }
}

public class ShapePolymorphism {
    public static void main(String[] args) {
        // Polymorphic array of Shape objects
        Shape[] shapes = {
            new Circle(5.0),
            new Rectangle(4.0, 6.0),
            new Triangle(3.0, 8.0),
            new Circle(2.5),
            new Rectangle(10.0, 3.0)
        };

        System.out.println("=== Shape Polymorphism Demo ===");
        System.out.println("-".repeat(55));

        double totalArea = 0;
        for (Shape shape : shapes) {
            shape.display();          // Polymorphic method call
            totalArea += shape.calculateArea();
        }

        System.out.println("-".repeat(55));
        System.out.printf("Total Area of all shapes: %.4f%n", totalArea);
    }
}
