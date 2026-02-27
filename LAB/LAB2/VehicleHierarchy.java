class Vehicle {
    protected String brand;
    protected String model;
    protected int year;
    protected double speed;

    Vehicle(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.speed = 0;
    }

    void accelerate(double amount) {
        speed += amount;
        System.out.printf("%s %s accelerates to %.1f km/h%n", brand, model, speed);
    }

    void brake(double amount) {
        speed = Math.max(0, speed - amount);
        System.out.printf("%s %s slows down to %.1f km/h%n", brand, model, speed);
    }

    void displayInfo() {
        System.out.printf("Vehicle: %d %s %s | Speed: %.1f km/h%n", year, brand, model, speed);
    }
}

class Car extends Vehicle {
    private int numDoors;
    private double trunkCapacity; // in liters

    Car(String brand, String model, int year, int numDoors, double trunkCapacity) {
        super(brand, model, year);
        this.numDoors = numDoors;
        this.trunkCapacity = trunkCapacity;
    }

    void openTrunk() {
        System.out.printf("%s %s trunk opened (%.1fL capacity)%n", brand, model, trunkCapacity);
    }

    @Override
    void displayInfo() {
        super.displayInfo();
        System.out.printf("  Type: Car | Doors: %d | Trunk: %.1fL%n", numDoors, trunkCapacity);
    }
}

class Motorcycle extends Vehicle {
    private String type; // sport, cruiser, off-road
    private boolean hasSidecar;

    Motorcycle(String brand, String model, int year, String type, boolean hasSidecar) {
        super(brand, model, year);
        this.type = type;
        this.hasSidecar = hasSidecar;
    }

    void doWheelie() {
        if (speed > 30) {
            System.out.printf("%s %s does a wheelie!%n", brand, model);
        } else {
            System.out.println("Need more speed for a wheelie!");
        }
    }

    @Override
    void displayInfo() {
        super.displayInfo();
        System.out.printf("  Type: Motorcycle | Style: %s | Sidecar: %s%n",
                type, hasSidecar ? "Yes" : "No");
    }
}

public class VehicleHierarchy {
    public static void main(String[] args) {
        System.out.println("=== Car ===");
        Car car = new Car("Toyota", "Camry", 2023, 4, 428.0);
        car.displayInfo();
        car.accelerate(60);
        car.openTrunk();
        car.brake(20);

        System.out.println("\n=== Motorcycle ===");
        Motorcycle moto = new Motorcycle("Harley-Davidson", "Sportster", 2022, "cruiser", false);
        moto.displayInfo();
        moto.accelerate(40);
        moto.doWheelie();
        moto.accelerate(10);
        moto.doWheelie();
    }
}
