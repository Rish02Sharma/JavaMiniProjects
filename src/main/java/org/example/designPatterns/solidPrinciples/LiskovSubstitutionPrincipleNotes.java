package org.example.designPatterns.solidPrinciples;

/**
 * ============================================================================
 * SOLID PRINCIPLES: Liskov Substitution Principle (LSP)
 * ============================================================================
 *
 * Definition (Barbara Liskov, 1987 / Robert C. Martin):
 * "Objects of a superclass should be replaceable with objects of its subclasses
 *  without affecting the correctness or expected behavior of the program."
 *
 * Formal Rule (Design by Contract):
 * 1. Preconditions cannot be strengthened in a subtype.
 * 2. Postconditions cannot be weakened in a subtype.
 * 3. Invariants of the supertype must be preserved in a subtype.
 * 4. Subtypes should NOT throw unexpected checked exceptions not thrown by the base type.
 *
 * Core Concept:
 * - "IS-A" in natural language does not always mean "IS-A" in OOP behavioral modeling
 *   (e.g., A Square is geometrically a Rectangle, an Ostrich is biologically a Bird,
 *   a Read-Only File is a File—but subclassing them naively breaks polymorphic contracts).
 * - Inheritance must model *behavioral compatibility*, not just shared properties.
 *
 * Common Smells / Red Flags:
 * 1. Subclass methods throwing UnsupportedOperationException or returning empty/null
 *    because "this subtype doesn't support that feature."
 * 2. Client code using `instanceof` or downcasting to check specific subtypes before
 *    invoking methods:
 *       if (account instanceof FixedDepositAccount) { ... }
 * 3. Overridden methods mutating fields in ways that violate the base class assumptions.
 */
public class LiskovSubstitutionPrincipleNotes {

    // ========================================================================
    // 1. CLASSIC VIOLATION: The Bank Account / Withdrawal Problem
    // ========================================================================
    /**
     * Why this violates LSP:
     * - The base class establishes a behavioral contract: every `Account` can withdraw funds.
     * - `FixedDepositAccount` extends `Account`, but throws a RuntimeException on withdraw()
     *   because fixed deposits are locked.
     * - Any polymorphic client processing a list of `Account` objects will crash unexpectedly.
     */
    static class AccountBad {
        protected double balance;

        public AccountBad(double balance) {
            this.balance = balance;
        }

        public void deposit(double amount) {
            this.balance += amount;
            System.out.println("Deposited: " + amount + ", New Balance: " + balance);
        }

        public void withdraw(double amount) {
            if (balance >= amount) {
                this.balance -= amount;
                System.out.println("Withdrawn: " + amount + ", New Balance: " + balance);
            } else {
                throw new IllegalArgumentException("Insufficient funds.");
            }
        }

        public double getBalance() { return balance; }
    }

    static class SavingsAccountBad extends AccountBad {
        public SavingsAccountBad(double balance) { super(balance); }
    }

    static class FixedDepositAccountBad extends AccountBad {
        public FixedDepositAccountBad(double balance) { super(balance); }

        @Override
        public void withdraw(double amount) {
            // VIOLATION: Breaks the contract of the base class!
            throw new UnsupportedOperationException("Withdrawals are locked for Fixed Deposits!");
        }
    }

    // Client demonstration showing how polymorphic substitution fails
    static class BankingClientBad {
        public static void processMonthlyWithdrawal(AccountBad account, double amount) {
            // Client assumes ALL Account instances can safely withdraw
            account.withdraw(amount); // CRASHES if account is FixedDepositAccountBad!
        }
    }


    // ========================================================================
    // 2. REFACTORED / LSP-COMPLIANT EXAMPLE (Contract Segregation)
    // ========================================================================
    /**
     * Solution:
     * Segregate the hierarchy so that contracts are strictly honored by all subtypes.
     * Only accounts that genuinely support withdrawals implement `WithdrawableAccount`.
     */

    // Base contract: All accounts support balance inspection and deposits
    interface Account {
        double getBalance();
        void deposit(double amount);
    }

    // Specialized behavioral contract for accounts allowing on-demand withdrawals
    interface WithdrawableAccount extends Account {
        void withdraw(double amount);
    }

    // Concrete implementation 1: Supports both deposit and withdrawal
    static class SavingsAccount implements WithdrawableAccount {
        private double balance;

        public SavingsAccount(double balance) { this.balance = balance; }

        @Override
        public void deposit(double amount) { this.balance += amount; }

        @Override
        public void withdraw(double amount) {
            if (balance >= amount) {
                this.balance -= amount;
            } else {
                throw new IllegalArgumentException("Insufficient funds in savings.");
            }
        }

        @Override
        public double getBalance() { return balance; }
    }

    // Concrete implementation 2: Supports deposit and lock-in only (honors Account contract 100%)
    static class FixedDepositAccount implements Account {
        private double balance;

        public FixedDepositAccount(double balance) { this.balance = balance; }

        @Override
        public void deposit(double amount) { this.balance += amount; }

        @Override
        public double getBalance() { return balance; }
    }

    // Client demonstration: Fully substitutable and type-safe
    static class BankingClientGood {
        // Accepts ANY WithdrawableAccount without fear of unexpected runtime exceptions
        public static void processWithdrawal(WithdrawableAccount account, double amount) {
            account.withdraw(amount); // Safe by design - compiler guarantees compatibility
        }

        // Accepts ANY Account for generic operations like statements or deposits
        public static void printAccountSummary(Account account) {
            System.out.println("Current Account Balance: " + account.getBalance());
        }
    }


    // ========================================================================
    // 3. CLASSIC GEOMETRY VIOLATION: Rectangle vs. Square
    // ========================================================================
    /**
     * Classic Uncle Bob Example:
     * Setting width on a Square modifies height as a hidden side-effect,
     * breaking Rectangle's invariant: area == width * height (when set independently).
     */
    interface Shape {
        int getArea();
    }

    static class Rectangle implements Shape {
        private final int width;
        private final int height;

        public Rectangle(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() { return width; }
        public int getHeight() { return height; }

        @Override
        public int getArea() { return width * height; }
    }

    static class Square implements Shape {
        private final int side;

        public Square(int side) {
            this.side = side;
        }

        public int getSide() { return side; }

        @Override
        public int getArea() { return side * side; }
    }
}
