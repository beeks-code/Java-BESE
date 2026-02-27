abstract class BankAccount {
    protected String accountHolder;
    protected double balance;

    BankAccount(String accountHolder, double initialBalance) {
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }

    void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.printf("Deposited: $%.2f%n", amount);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    abstract void withdraw(double amount);

    void displayBalance() {
        System.out.printf("Account Holder: %s | Balance: $%.2f%n", accountHolder, balance);
    }
}

class SavingsAccount extends BankAccount {
    private static final double MIN_BALANCE = 500.0;

    SavingsAccount(String holder, double initialBalance) {
        super(holder, initialBalance);
    }

    @Override
    void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount.");
        } else if (balance - amount < MIN_BALANCE) {
            System.out.printf("Cannot withdraw. Minimum balance of $%.2f required.%n", MIN_BALANCE);
        } else {
            balance -= amount;
            System.out.printf("[Savings] Withdrawn: $%.2f%n", amount);
        }
    }
}

class CheckingAccount extends BankAccount {
    private double overdraftLimit;

    CheckingAccount(String holder, double initialBalance, double overdraftLimit) {
        super(holder, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount.");
        } else if (balance - amount < -overdraftLimit) {
            System.out.printf("Overdraft limit of $%.2f exceeded.%n", overdraftLimit);
        } else {
            balance -= amount;
            System.out.printf("[Checking] Withdrawn: $%.2f%n", amount);
        }
    }
}

public class BankAccountDemo {
    public static void main(String[] args) {
        System.out.println("=== Savings Account ===");
        BankAccount savings = new SavingsAccount("Alice", 1000.0);
        savings.displayBalance();
        savings.deposit(200.0);
        savings.withdraw(800.0);   // Should fail - min balance
        savings.withdraw(500.0);   // Should succeed
        savings.displayBalance();

        System.out.println("\n=== Checking Account ===");
        BankAccount checking = new CheckingAccount("Bob", 500.0, 200.0);
        checking.displayBalance();
        checking.withdraw(600.0);  // Uses overdraft
        checking.displayBalance();
        checking.withdraw(200.0);  // Exceeds overdraft
    }
}
