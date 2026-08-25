package org.example.designPatterns.solidPrinciples;

/**
 * ============================================================================
 * SOLID PRINCIPLES: Interface Segregation Principle (ISP)
 * ============================================================================
 *
 * Definition (Robert C. Martin / Uncle Bob):
 * "Clients should not be forced to depend upon interfaces that they do not use."
 *
 * Core Concept:
 * - Many client-specific, fine-grained interfaces are better than one general-purpose,
 *   "fat" interface.
 * - Classes should only know about methods that are relevant to their operations.
 * - Prevents "Interface Pollution" and minimizes coupling across unrelated domains.
 *
 * ISP vs SRP:
 * - SRP is about the cohesion of a CLASS (one reason to change).
 * - ISP is about the design of an INTERFACE (no unnecessary baggage for clients).
 *
 * Common Smells / Red Flags:
 * 1. "Fat" or "Polluted" interfaces packed with dozens of methods spanning multiple roles.
 * 2. Classes implementing an interface with empty method bodies ({}), returning null/default,
 *    or throwing `UnsupportedOperationException`.
 * 3. Changes to one method in an interface trigger recompilation and re-deployment of
 *    unrelated client classes that never invoke that method.
 */
public class InterfaceSegregationPrincipleNotes {

    // ========================================================================
    // 1. VIOLATION EXAMPLE: The "Fat" Multi-Function Printer Interface
    // ========================================================================
    /**
     * Why this violates ISP:
     * - `MultiFunctionDeviceBad` bunches together printing, scanning, faxing, and stapling.
     * - A basic office printer or mobile scanner only needs a subset of these features.
     * - Simple implementations are forced to provide dummy/throwaway implementations
     *   for methods they physically cannot perform.
     */
    interface MultiFunctionDeviceBad {
        void print(String document);
        void scan(String document);
        void fax(String document);
        void stapleDocument();
    }

    // High-end Enterprise Machine: Uses everything (looks fine here)
    static class EnterpriseLaserPrinterBad implements MultiFunctionDeviceBad {
        @Override
        public void print(String document) { System.out.println("Printing: " + document); }
        @Override
        public void scan(String document) { System.out.println("Scanning: " + document); }
        @Override
        public void fax(String document) { System.out.println("Faxing: " + document); }
        @Override
        public void stapleDocument() { System.out.println("Stapling printed packet."); }
    }

    // Basic Home Printer: Forced to implement Scan, Fax, and Staple
    static class BasicHomePrinterBad implements MultiFunctionDeviceBad {
        @Override
        public void print(String document) {
            System.out.println("Printing from home printer: " + document);
        }

        @Override
        public void scan(String document) {
            // VIOLATION: Hardware does not have a scanner!
            throw new UnsupportedOperationException("Scanner not supported on this model.");
        }

        @Override
        public void fax(String document) {
            // VIOLATION: Dummy empty implementation
        }

        @Override
        public void stapleDocument() {
            // VIOLATION: No staple finisher hardware
            throw new UnsupportedOperationException("Stapler not attached.");
        }
    }


    // ========================================================================
    // 2. REFACTORED / ISP-COMPLIANT EXAMPLE: Fine-Grained Role Interfaces
    // ========================================================================
    /**
     * Solution:
     * Break the fat interface into single-purpose, cohesive role interfaces.
     * Classes compose only the behaviors they actually support.
     */

    // Role Interface 1: Printing capability
    interface Printable {
        void print(String document);
    }

    // Role Interface 2: Scanning capability
    interface Scannable {
        void scan(String document);
    }

    // Role Interface 3: Faxing capability
    interface Faxable {
        void fax(String document);
    }

    // Role Interface 4: Finishing/Stapling capability
    interface Stapleable {
        void staple();
    }

    // Device 1: Basic home printer implements ONLY Printable
    static class BasicDeskjetPrinter implements Printable {
        @Override
        public void print(String document) {
            System.out.println("Printing document: " + document);
        }
    }

    // Device 2: Standalone handheld scanner implements ONLY Scannable
    static class HandheldScanner implements Scannable {
        @Override
        public void scan(String document) {
            System.out.println("Scanning barcode/document: " + document);
        }
    }

    // Device 3: Enterprise office hub composes all relevant interfaces cleanly
    static class AllInOneOfficePrinter implements Printable, Scannable, Faxable, Stapleable {
        @Override
        public void print(String document) { System.out.println("Enterprise printing: " + document); }
        @Override
        public void scan(String document) { System.out.println("Enterprise scanning: " + document); }
        @Override
        public void fax(String document) { System.out.println("Enterprise faxing: " + document); }
        @Override
        public void staple() { System.out.println("Finishing: packet stapled."); }
    }


    // ========================================================================
    // 3. REAL-WORLD BACKEND USE CASE: User & Role Permissions
    // ========================================================================
    /**
     * Separate read-only operations from write/admin operations so restricted
     * clients/services do not have visibility into destructive actions.
     */
    interface UserReader {
        String fetchUserProfile(String userId);
    }

    interface UserWriter {
        void updateUserProfile(String userId, String payload);
        void deleteUser(String userId);
    }

    // Read-only reporting service only depends on UserReader
    static class AnalyticsService {
        private final UserReader userReader;

        public AnalyticsService(UserReader userReader) {
            this.userReader = userReader; // Cannot call deleteUser even by mistake
        }

        public void generateReport(String userId) {
            String data = userReader.fetchUserProfile(userId);
            System.out.println("Generating metric report for: " + data);
        }
    }

    // Full service implements both interfaces
    static class UserService implements UserReader, UserWriter {
        @Override
        public String fetchUserProfile(String userId) { return "User Profile Data"; }
        @Override
        public void updateUserProfile(String userId, String payload) { System.out.println("Updated user."); }
        @Override
        public void deleteUser(String userId) { System.out.println("Deleted user."); }
    }
}