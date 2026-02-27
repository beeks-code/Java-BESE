public class Student {
    // Private data members — encapsulated
    private String name;
    private int rollNumber;
    private double[] marks;
    private static final int MAX_SUBJECTS = 5;

    public Student(String name, int rollNumber) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.marks = new double[MAX_SUBJECTS];
    }

    // --- Getters ---
    public String getName() { return name; }
    public int getRollNumber() { return rollNumber; }
    public double getMark(int subject) {
        if (subject < 0 || subject >= MAX_SUBJECTS)
            throw new IndexOutOfBoundsException("Subject index out of range.");
        return marks[subject];
    }
    public double getAverage() {
        double sum = 0;
        for (double m : marks) sum += m;
        return sum / MAX_SUBJECTS;
    }
    public String getGrade() {
        double avg = getAverage();
        if (avg >= 90) return "A+";
        if (avg >= 80) return "A";
        if (avg >= 70) return "B";
        if (avg >= 60) return "C";
        return "F";
    }

    // --- Setters with validation ---
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }

    public void setMark(int subject, double mark) {
        if (subject < 0 || subject >= MAX_SUBJECTS)
            throw new IndexOutOfBoundsException("Subject index out of range.");
        if (mark < 0 || mark > 100)
            throw new IllegalArgumentException("Mark must be between 0 and 100.");
        marks[subject] = mark;
    }

    public void displayInfo() {
        System.out.println("-------------------------------");
        System.out.println("Name       : " + name);
        System.out.println("Roll No.   : " + rollNumber);
        System.out.print("Marks      : ");
        for (int i = 0; i < MAX_SUBJECTS; i++)
            System.out.printf("S%d:%.1f  ", i + 1, marks[i]);
        System.out.println();
        System.out.printf("Average    : %.2f%n", getAverage());
        System.out.println("Grade      : " + getGrade());
    }

    public static void main(String[] args) {
        Student s1 = new Student("Alice Johnson", 101);
        s1.setMark(0, 92); s1.setMark(1, 87); s1.setMark(2, 95);
        s1.setMark(3, 88); s1.setMark(4, 91);
        s1.displayInfo();

        Student s2 = new Student("Bob Smith", 102);
        s2.setMark(0, 65); s2.setMark(1, 70); s2.setMark(2, 55);
        s2.setMark(3, 72); s2.setMark(4, 60);
        s2.displayInfo();

        // Test encapsulation protection
        try {
            s1.setMark(0, 150); // Invalid
        } catch (IllegalArgumentException e) {
            System.out.println("\nEncapsulation caught: " + e.getMessage());
        }
    }
}
