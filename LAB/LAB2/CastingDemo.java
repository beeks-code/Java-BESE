class Animal {
    protected String name;

    Animal(String name) { this.name = name; }

    void makeSound() {
        System.out.println(name + " makes a generic sound.");
    }

    void eat() {
        System.out.println(name + " is eating.");
    }

    @Override
    public String toString() { return "Animal[" + name + "]"; }
}

class Dog extends Animal {
    private String breed;

    Dog(String name, String breed) {
        super(name);
        this.breed = breed;
    }

    @Override
    void makeSound() { System.out.println(name + " says: Woof! Woof!"); }

    void fetch() { System.out.println(name + " the " + breed + " fetches the ball!"); }

    @Override
    public String toString() { return "Dog[" + name + ", " + breed + "]"; }
}

class Cat extends Animal {
    private boolean isIndoor;

    Cat(String name, boolean isIndoor) {
        super(name);
        this.isIndoor = isIndoor;
    }

    @Override
    void makeSound() { System.out.println(name + " says: Meow~"); }

    void purr() { System.out.println(name + " purrs contentedly..."); }

    @Override
    public String toString() { return "Cat[" + name + ", indoor=" + isIndoor + "]"; }
}

class Bird extends Animal {
    private double wingspan;

    Bird(String name, double wingspan) {
        super(name);
        this.wingspan = wingspan;
    }

    @Override
    void makeSound() { System.out.println(name + " says: Tweet! Tweet!"); }

    void fly() { System.out.printf("%s flies with %.1fcm wingspan!%n", name, wingspan); }

    @Override
    public String toString() { return "Bird[" + name + ", wingspan=" + wingspan + "cm]"; }
}

public class CastingDemo {
    public static void main(String[] args) {

        // =============================================
        // 1. UPCASTING — subclass → superclass (implicit, safe)
        // =============================================
        System.out.println("====== UPCASTING ======");
        Dog dog = new Dog("Rex", "German Shepherd");
        Animal animal = dog; // Upcasting — implicit, no cast needed

        System.out.println("Original Dog reference: " + dog);
        System.out.println("Upcast Animal reference: " + animal);
        animal.makeSound(); // Polymorphism — Dog's version is called
        animal.eat();
        // animal.fetch(); // ← COMPILE ERROR: fetch() not in Animal

        // =============================================
        // 2. DOWNCASTING — superclass ref → subclass (explicit, requires instanceof check)
        // =============================================
        System.out.println("\n====== DOWNCASTING ======");
        Animal animalRef = new Dog("Buddy", "Labrador"); // Upcast during creation
        System.out.println("animalRef holds: " + animalRef);

        // Safe downcast with instanceof check
        if (animalRef instanceof Dog) {
            Dog downcastDog = (Dog) animalRef; // Explicit downcast
            System.out.println("Successfully downcast to: " + downcastDog);
            downcastDog.fetch(); // Now we can call Dog-specific method
        }

        // Unsafe downcast example — would throw ClassCastException
        Animal cat = new Cat("Whiskers", true);
        System.out.println("\nAttempting unsafe downcast of Cat as Dog:");
        try {
            Dog wrongCast = (Dog) cat; // ClassCastException at runtime
        } catch (ClassCastException e) {
            System.out.println("ClassCastException caught: " + e.getMessage());
        }

        // Modern Java: pattern matching instanceof (Java 16+)
        System.out.println("\nModern instanceof pattern matching:");
        if (cat instanceof Cat c) {
            c.purr(); // c is already typed as Cat
        }

        // =============================================
        // 3. POLYMORPHIC ARRAY
        // =============================================
        System.out.println("\n====== POLYMORPHIC ARRAY ======");
        Animal[] animals = {
            new Dog("Rex", "German Shepherd"),
            new Cat("Luna", true),
            new Bird("Tweety", 25.0),
            new Dog("Max", "Poodle"),
            new Cat("Shadow", false),
            new Bird("Eagle", 180.0)
        };

        System.out.println("--- Polymorphic makeSound() calls ---");
        for (Animal a : animals) {
            a.makeSound(); // Each calls its OWN version
        }

        System.out.println("\n--- Type-specific methods via downcasting ---");
        for (Animal a : animals) {
            System.out.print(a.name + ": ");
            if (a instanceof Dog d) {
                d.fetch();
            } else if (a instanceof Cat c) {
                c.purr();
            } else if (a instanceof Bird b) {
                b.fly();
            }
        }
    }
}
