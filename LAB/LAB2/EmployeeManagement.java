abstract class Employee {
    protected String name;
    protected int id;
    protected double hourlyRate;

    Employee(String name, int id, double hourlyRate) {
        this.name = name;
        this.id = id;
        this.hourlyRate = hourlyRate;
    }

    abstract double calculateSalary();

    void displayInfo() {
        System.out.printf("ID: %d | Name: %-15s | Type: %-10s | Hourly Rate: $%.2f | Weekly Salary: $%.2f%n",
                id, name, getType(), hourlyRate, calculateSalary());
    }

    abstract String getType();
}

class FullTimeEmployee extends Employee {
    private static final int HOURS_PER_WEEK = 40;

    FullTimeEmployee(String name, int id, double hourlyRate) {
        super(name, id, hourlyRate);
    }

    @Override
    double calculateSalary() {
        return HOURS_PER_WEEK * hourlyRate;
    }

    @Override
    String getType() { return "Full-Time"; }
}

class PartTimeEmployee extends Employee {
    private static final int HOURS_PER_WEEK = 20;

    PartTimeEmployee(String name, int id, double hourlyRate) {
        super(name, id, hourlyRate);
    }

    @Override
    double calculateSalary() {
        return HOURS_PER_WEEK * hourlyRate;
    }

    @Override
    String getType() { return "Part-Time"; }
}

public class EmployeeManagement {
    public static void main(String[] args) {
        Employee[] employees = {
            new FullTimeEmployee("Alice Johnson", 1001, 25.00),
            new FullTimeEmployee("Bob Smith",    1002, 30.00),
            new PartTimeEmployee("Carol White",  1003, 18.00),
            new PartTimeEmployee("David Brown",  1004, 22.00),
        };

        System.out.println("=== Employee Management System ===");
        System.out.println("Full-Time: 40 hrs/week | Part-Time: 20 hrs/week");
        System.out.println("-".repeat(75));

        double totalPayroll = 0;
        for (Employee e : employees) {
            e.displayInfo();
            totalPayroll += e.calculateSalary();
        }

        System.out.println("-".repeat(75));
        System.out.printf("Total Weekly Payroll: $%.2f%n", totalPayroll);
    }
}
