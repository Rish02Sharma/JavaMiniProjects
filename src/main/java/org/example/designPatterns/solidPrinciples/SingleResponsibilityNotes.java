package org.example.designPatterns.solidPrinciples;

/**
 * ============================================================================
 * SOLID PRINCIPLES: Single Responsibility Principle (SRP)
 * ============================================================================
 *
 * Definition (Robert C. Martin / Uncle Bob):
 * "A class should have one, and only one, reason to change."
 *
 * Core Concept:
 * - Each class should be responsible for a single part of the functionality.
 * - Cohesion: High cohesion means elements of a module belong together.
 * - Coupling: Low coupling avoids ripple effects when modifying requirements.
 *
 * Common Smells / Red Flags:
 * 1. "God Classes" / "Swiss Army Knives" that handle business logic, database,
 *    and UI formatting all in one place.
 * 2. Classes with multiple distinct actors (e.g., changes requested by Finance,
 *    DBAs, and Security all forcing edits to the same class).
 * 3. Bloated import statements across disparate domains (e.g., javax.sql.*,
 *    org.springframework.mail.*, and java.awt.* all in one entity class).
 */
public class SingleResponsibilityNotes {

    // ========================================================================
    // 1. VIOLATION EXAMPLE
    // ========================================================================
    /**
     * Why this violates SRP:
     * This class has at least THREE separate reasons to change:
     *   1. Core business logic / invoice calculations change (Finance team).
     *   2. Database schema, ORM, or query mechanism changes (DBA / Backend team).
     *   3. Notification/Email template changes (Product / Marketing team).
     */
    static class InvoiceBad {
        private final String txnId;
        private final double amount;
        private final String customerEmail;

        public InvoiceBad(String txnId, double amount, String customerEmail) {
            this.txnId = txnId;
            this.amount = amount;
            this.customerEmail = customerEmail;
        }

        // Responsibility 1: Business Logic / Calculation
        public double calculateTotalWithTax(double taxRate) {
            return this.amount + (this.amount * taxRate);
        }

        // Responsibility 2: Database Persistence
        public void saveToDatabase() {
            System.out.println("Executing SQL: INSERT INTO invoices VALUES ('"
                    + this.txnId + "', " + this.amount + ");");
        }

        // Responsibility 3: Notification / Communication
        public void sendEmailReceipt() {
            System.out.println("Connecting to SMTP server and sending receipt to "
                    + this.customerEmail);
        }
    }


    // ========================================================================
    // 2. REFACTORED / SRP-COMPLIANT EXAMPLE
    // ========================================================================
    /**
     * Solution:
     * Decompose the multi-responsibility class into dedicated, single-purpose classes.
     */

    // Responsibility 1: Pure Domain Model / Business Logic
    static class Invoice {
        private final String txnId;
        private final double amount;
        private final String customerEmail;

        public Invoice(String txnId, double amount, String customerEmail) {
            this.txnId = txnId;
            this.amount = amount;
            this.customerEmail = customerEmail;
        }

        public double calculateTotalWithTax(double taxRate) {
            return this.amount + (this.amount * taxRate);
        }

        public String getTxnId() { return txnId; }
        public double getAmount() { return amount; }
        public String getCustomerEmail() { return customerEmail; }
    }

    // Responsibility 2: Data Access & Persistence
    static class InvoiceRepository {
        public void save(Invoice invoice) {
            System.out.println("Saving invoice " + invoice.getTxnId() + " to DB.");
        }

        public Invoice findById(String txnId) {
            // DB lookup logic...
            return new Invoice(txnId, 100.0, "user@example.com");
        }
    }

    // Responsibility 3: Notification Dispatcher
    static class InvoiceEmailService {
        public void sendReceipt(Invoice invoice) {
            System.out.println("Sending receipt for " + invoice.getTxnId()
                    + " to " + invoice.getCustomerEmail());
        }
    }


    // ========================================================================
    // 3. AUTH / TOKEN GENERATION EXAMPLE (From your original draft)
    // ========================================================================

    /**
     * Domain entity representing a User.
     * Sole Responsibility: Maintain user identity and state.
     */
    static class User {
        private final String name;
        private final String mobileNumber;

        public User(String name, String mobileNumber) {
            this.name = name;
            this.mobileNumber = mobileNumber;
        }

        public String getName() { return name; }
        public String getMobileNumber() { return mobileNumber; }
    }

    /**
     * Security / Token Service.
     * Sole Responsibility: Token generation and hashing logic.
     * (If token signing algorithm changes, only this class changes—not User).
     */
    static class UserTokenService {
        public String generateAuthToken(User user) {
            // Generates hash/JWT based purely on token-generation rules
            return "TOKEN_" + Integer.toHexString((user.getName() + user.getMobileNumber()).hashCode());
        }
    }
}