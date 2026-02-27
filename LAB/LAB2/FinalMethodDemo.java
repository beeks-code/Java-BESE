class Vehicle {
    private String brand;

    Vehicle(String brand) {
        this.brand = brand;
    }

    // Regular method — CAN be overridden
    public String describe() {
        return "Vehicle: " + brand;
    }

    // FINAL method — CANNOT be overridden by any subclass
    public final String getVehicleType() {
        return "This is a Vehicle (final method - cannot be overridden)";
    }

    // Final method with important logic
    public final void startEngine() {
        System.out.println("[" + brand + "] Running mandatory safety checks...");
        performStartSequence(); // Can delegate to overridable method
        System.out.println("[" + brand + "] Engine started successfully.");
    }

    // This CAN be overridden — it's the "hook" method
    protected void performStartSequence() {
        System.out.println("  Generic engine start sequence.");
    }
}

class Car extends Vehicle {
    Car(String brand) {
        super(brand);
    }

    @Override
    public String describe() {
        return "Car — " + super.describe();
    }

    // Overrides the hook, but NOT the final startEngine()
    @Override
    protected void performStartSequence() {
        System.out.println("  Car-specific: Checking fuel, turning ignition key...");
    }

    /*
     * UNCOMMENTING THE BELOW CAUSES A COMPILE ERROR:
     * error: getVehicleType() in Car cannot override getVehicleType() in Vehicle
     *        overridden method is final
     *
     * @Override
     * public final String getVehicleType() {
     *     return "This is a Car";
     * }
     */
}

class ElectricCar extends Car {
    ElectricCar(String brand) {
        super(brand);
    }

    @Override
    public String describe() {
        return "Electric " + super.describe();
    }

    @Override
    protected void performStartSequence() {
        System.out.println("  Electric: Checking battery, activating motor silently...");
    }
}

public class FinalMethodDemo {
    public static void main(String[] args) {
        Vehicle v = new Vehicle("GenericBrand");
        Car car = new Car("Toyota");
        ElectricCar ev = new ElectricCar("Tesla");

        Vehicle[] vehicles = { v, car, ev };

        for (Vehicle vehicle : vehicles) {
            System.out.println("--- " + vehicle.describe() + " ---");
            System.out.println(vehicle.getVehicleType()); // Always Vehicle's version
            vehicle.startEngine();  // Final method, but delegates to overridden hook
            System.out.println();
        }

        System.out.println("Key point: getVehicleType() always calls Vehicle's version");
        System.out.println("even when called on Car or ElectricCar references.");
    }
}
