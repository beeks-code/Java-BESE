package OOP;
public class basicClass {
    public static void main(String[] args) {
        Student std1 = new Student();
        std1.name = "Beeks";
        std1.age = 21;
        std1.height = 1.61f; // in meter
        std1.details();
    }
}

class Student {
    int age;
    String name, school = "BMSS";
    float height;

    void details() {
        System.out.printf(
            "%s who studies in %s is %.1f cm tall and %d yrs old",
            name, school, height * 100, age
        );
    }
}
