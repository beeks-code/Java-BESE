// Abstract base class
abstract class Shape {
    abstract double calculateArea();
    abstract void display();
}

class Circle extends Shape {
    private double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double calculateArea() {
        return Math.PI * radius * radius;
    }

    @Override
    void display() {
        System.out.printf("Circle | Radius: %.2f | Area: %.2f%n", radius, calculateArea());
    }
}

class Rectangle extends Shape {
    private double width, height;

    Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    double calculateArea() {
        return width * height;
    }

    @Override
    void display() {
        System.out.printf("Rectangle | Width: %.2f, Height: %.2f | Area: %.2f%n",
                width, height, calculateArea());
    }
}

public class Shape {
    public static void main(String[] args) {
        Shape circle = new Circle(5.0);
        Shape rectangle = new Rectangle(4.0, 6.0);

        circle.display();
        rectangle.display();
    }
}
