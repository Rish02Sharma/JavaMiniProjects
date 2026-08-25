package org.example.designPatterns.solidPrinciples;

/**
 * ============================================================================
 * SOLID PRINCIPLES: Open/Closed Principle (OCP)
 * ============================================================================
 *
 * Definition (Bertrand Meyer / Robert C. Martin):
 * "Software entities (classes, modules, functions, etc.) should be open for
 *  extension, but closed for modification."
 *
 * Core Concept:
 * - Open for extension: You should be able to extend the behavior of a module
 *   when requirements change or new features are introduced.
 * - Closed for modification: Extending behavior should NOT require modifying
 *   existing, tested source code.
 *
 * How to achieve OCP:
 * - Abstractions (Interfaces / Abstract classes) define the contract.
 * - Polymorphism allows swapping or adding new concrete behaviors at runtime.
 * - Design patterns enabling OCP: Strategy, Factory Method, Decorator.
 *
 * Common Smells / Red Flags:
 * 1. Cascading `if-else` or `switch` blocks checking type/format strings.
 * 2. Constantly editing an existing service class whenever a new provider,
 *    storage format, or payment method is added.
 * 3. High regression risk: modifying existing code forces re-testing of
 *    previously stable features.
 */
public class OpenClosePrincipleNotes {

    // ========================================================================
    // 1. VIOLATION EXAMPLE
    // ========================================================================
    /**
     * Why this violates OCP:
     * 1. Every time a new persistence medium is introduced (e.g., Redis, S3, Kafka),
     *    we must modify this existing class to add a new method.
     * 2. Modifying this class risks breaking existing logic and requires
     *    re-testing already deployed functionality.
     * 3. It also couples data representation directly to multiple persistence mechanisms.
     */
    static class UserPersistenceBad {
        private final int id;
        private final String name;
        private final String address;

        public UserPersistenceBad(int id, String name, String address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }

        // Hardcoded MySQL persistence
        public void saveToMySQL() {
            System.out.println("Executing INSERT INTO users VALUES (" + id + ", '" + name + "') in MySQL");
        }

        // Added later: forced modification to existing class
        public void saveToCSVFile() {
            System.out.println("Writing " + id + "," + name + "," + address + " to CSV file");
        }

        // Added later: another modification to existing class
        public void saveToMongoDB() {
            System.out.println("Inserting document {id: " + id + ", name: '" + name + "'} into MongoDB");
        }

        // If we want to add Redis caching tomorrow -> We MUST modify this class again!
    }


    // ========================================================================
    // 2. REFACTORED / OCP-COMPLIANT EXAMPLE
    // ========================================================================

    /**
     * Step 1: Define the Domain Model (Closed for unnecessary changes).
     */
    static class User {
        private final int id;
        private final String name;
        private final String address;

        public User(int id, String name, String address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getAddress() { return address; }
    }

    /**
     * Step 2: Define an Abstraction (Contract).
     * Follows Java naming conventions: UpperCamelCase for interfaces.
     */
    interface DataExporter {
        void export(User user);
    }

    /**
     * Step 3: Implement concrete strategies for each target.
     * Adding a new exporter requires ONLY creating a new class (Extension).
     * Existing exporter classes remain untouched (Closed for modification).
     */
    static class MySqlDataExporter implements DataExporter {
        @Override
        public void export(User user) {
            System.out.println("Exporting user [" + user.getName() + "] to MySQL database table.");
        }
    }

    static class CsvFileDataExporter implements DataExporter {
        @Override
        public void export(User user) {
            System.out.println("Exporting user [" + user.getName() + "] to users.csv file.");
        }
    }

    static class MongoDbDataExporter implements DataExporter {
        @Override
        public void export(User user) {
            System.out.println("Exporting user [" + user.getName() + "] to MongoDB collection.");
        }
    }

    /**
     * Example of future extension:
     * Added AWS S3 storage without modifying ANY of the classes above!
     */
    static class S3DataExporter implements DataExporter {
        @Override
        public void export(User user) {
            System.out.println("Uploading user [" + user.getName() + "] payload to AWS S3 bucket.");
        }
    }

    /**
     * Step 4: High-Level Client / Context.
     * This coordinator executes exports via abstraction without knowing the concrete storage type.
     */
    static class UserExportService {
        private final DataExporter exporter;

        // Dependency injection via constructor
        public UserExportService(DataExporter exporter) {
            this.exporter = exporter;
        }

        public void processExport(User user) {
            // Delegated execution via polymorphism
            exporter.export(user);
        }
    }
}