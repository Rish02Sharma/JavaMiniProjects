package org.example.JavaPractice.modern;

import java.util.*;

/**
 * =============================================================================================
 *          MASTERCLASS: MODERN JAVA (JAVA 17 - JAVA 21 LTS FEATURES)
 * =============================================================================================
 *
 * This file serves as an end-to-end, runnable guide and masterclass covering the modern Java
 * language evolution from Java 11 through Java 21 LTS:
 *
 * TABLE OF CONTENTS:
 *  1. Records (Java 16): Canonical, Compact Constructors & Immutability Semantics
 *  2. Sealed Classes & Interfaces (Java 17): Algebraic Data Types (Sum Types)
 *  3. Pattern Matching for instanceof (Java 16) & Flow Scoping Rules
 *  4. Pattern Matching for switch (Java 21 JEP 441): Type Patterns, Guarded Patterns (when), null handling
 *  5. Record Patterns & Nested Deconstruction (Java 21 JEP 440)
 *  6. Text Blocks & String Formatting (Java 15): Indentation, Escaping & .formatted()
 *  7. Sequenced Collections (Java 21 JEP 431): Unified Sequence Access & Reversed Views
 *  8. Local-Variable Type Inference (var): Dos, Don'ts & Diamond Operator Pitfalls
 *  9. Real-World Domain Modeling: Building a Modern Pure Functional Domain Pipeline
 * =============================================================================================
 */
public class ModernJavaFeatures {

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("       MODERN JAVA MASTERCLASS (JAVA 17 THROUGH JAVA 21 LTS)       ");
        System.out.println("===================================================================\n");

        section1_RecordsAndCompactConstructors();
        section2_SealedClassesAndInterfaces();
        section3_PatternMatchingInstanceOf();
        section4_PatternMatchingSwitch();
        section5_RecordPatternsAndDeconstruction();
        section6_TextBlocksAndFormatting();
        section7_SequencedCollectionsOverview();
        section8_VarBestPracticesAndPitfalls();
        section9_RealWorldDomainModeling();

        System.out.println("\n===================================================================");
        System.out.println("           ALL MODERN JAVA (17 - 21) LESSONS COMPLETE!             ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: RECORDS & COMPACT CONSTRUCTORS (JAVA 16)
    // =========================================================================================
    private static void section1_RecordsAndCompactConstructors() {
        printHeader("1. RECORDS & COMPACT CONSTRUCTORS (JAVA 16)");
        /*
         * WHAT IS A RECORD?
         * A Record is a specialized, transparent, immutable carrier for data.
         * The compiler automatically generates:
         *  - private final fields for every component
         *  - Canonical constructor
         *  - Public accessors: `name()` and `price()` (Note: NO 'get' prefix!)
         *  - equals() and hashCode() based on all components
         *  - toString() with component names and values
         *  - Implicit superclass: java.lang.Record (cannot extend other classes!)
         *
         * COMPACT CONSTRUCTORS:
         * A constructor syntax with NO parameter list:
         *   Point {
         *       // Parameters x and y are implicitly available!
         *       // Used for validation and normalization BEFORE assignment.
         *   }
         */

        record Product(String sku, String name, double price) {
            // Compact Constructor for validation and normalization:
            public Product {
                Objects.requireNonNull(sku, "SKU cannot be null");
                Objects.requireNonNull(name, "Product name cannot be null");
                if (price < 0.0) {
                    throw new IllegalArgumentException("Price cannot be negative: " + price);
                }
                sku = sku.trim().toUpperCase(); // Normalization
            }

            // Custom instance method:
            public double priceWithTax(double taxRate) {
                return price + (price * taxRate);
            }
        }

        Product p = new Product("  sku-101  ", "Wireless Mouse", 29.99);
        System.out.println("Record toString():     " + p);
        System.out.println("Normalized SKU:        " + p.sku()); // SKU-101
        System.out.println("Price with 18% Tax:    $" + String.format("%.2f", p.priceWithTax(0.18)));
    }

    // =========================================================================================
    // SECTION 2: SEALED CLASSES & INTERFACES (JAVA 17)
    // =========================================================================================
    /*
     * WHAT ARE SEALED TYPES?
     * Prior to Java 17, inheritance was all-or-nothing: either `final` (no subclasses)
     * or completely open (any class anywhere could extend it).
     *
     * Sealed classes and interfaces allow a class or interface to restrict EXACTLY which
     * subclasses are permitted to extend or implement it!
     *
     * Subclasses MUST specify one of three modifiers:
     *  1. `final`: No further subclasses permitted.
     *  2. `sealed`: Permits its own specific list of subclasses.
     *  3. `non-sealed`: Opens the hierarchy back up for arbitrary extension.
     */
    public sealed interface PaymentMethod
            permits CreditCard, Upi, CryptoPayment {}

    public record CreditCard(String cardNumber, String cvv, String expiry) implements PaymentMethod {}
    public record Upi(String vpaId) implements PaymentMethod {}
    public final class CryptoPayment implements PaymentMethod {
        private final String walletAddress;
        public CryptoPayment(String walletAddress) { this.walletAddress = walletAddress; }
        public String walletAddress() { return walletAddress; }
    }

    private static void section2_SealedClassesAndInterfaces() {
        printHeader("2. SEALED CLASSES & INTERFACES (JAVA 17)");

        PaymentMethod method = new Upi("merchant@upi");
        System.out.println("Sealed hierarchy instance: " + method.getClass().getSimpleName());
        System.out.println("Permitted subtypes of PaymentMethod: "
                + Arrays.toString(PaymentMethod.class.getPermittedSubclasses()));
    }

    // =========================================================================================
    // SECTION 3: PATTERN MATCHING FOR instanceof (JAVA 16)
    // =========================================================================================
    private static void section3_PatternMatchingInstanceOf() {
        printHeader("3. PATTERN MATCHING FOR instanceof & FLOW SCOPING (JAVA 16)");
        /*
         * THE OLD WAY:
         *   if (obj instanceof String) {
         *       String s = (String) obj; // Redundant manual cast!
         *       System.out.println(s.toUpperCase());
         *   }
         *
         * THE MODERN WAY:
         *   if (obj instanceof String s) {
         *       System.out.println(s.toUpperCase()); // Cast is automated!
         *   }
         *
         * FLOW SCOPING RULES:
         * The variable `s` is ONLY in scope where the pattern is guaranteed to have matched:
         *  - `if (obj instanceof String s && s.length() > 5)` -> Valid!
         *  - `if (obj instanceof String s || s.length() > 5)`  -> COMPILE ERROR!
         *  - If statement with early return:
         *    if (!(obj instanceof String s)) return;
         *    // 's' IS IN SCOPE HERE because if it weren't a String, the method would have returned!
         */

        Object payload = "Antigravity Java 21";

        if (payload instanceof String s && s.startsWith("Anti")) {
            System.out.println("Matched and flow-scoped: " + s.toUpperCase() + " (length: " + s.length() + ")");
        }
    }

    // =========================================================================================
    // SECTION 4: PATTERN MATCHING FOR switch (JAVA 21 JEP 441)
    // =========================================================================================
    private static void section4_PatternMatchingSwitch() {
        printHeader("4. PATTERN MATCHING FOR switch & GUARD CLAUSES (JAVA 21)");
        /*
         * JAVA 21 switch ENHANCEMENTS:
         *  1. Type Patterns: Switch on any Object type, matching on class types.
         *  2. Guarded Patterns (`when` clause): Add boolean expressions to refine matches.
         *  3. Null Handling: `case null` is now explicitly supported (no more automatic NPE)!
         *  4. Exhaustiveness Checking: When switching over a sealed hierarchy, the compiler
         *     verifies all subtypes are handled, so NO `default` case is needed!
         */

        List<Object> items = Arrays.asList(42, "Modern Java 21", "Short", 3.14159, null, new Upi("user@okaxis"));

        for (Object item : items) {
            String description = switch (item) {
                case null -> "Received a NULL reference!";
                case Integer i -> "Integer number: " + (i * 2);
                case String s when s.length() > 10 -> "Long String (" + s.length() + " chars): " + s;
                case String s -> "Short String: " + s;
                case Double d -> "Floating-point double: " + d;
                case PaymentMethod pm -> "PaymentMethod subtype: " + pm.getClass().getSimpleName();
                default -> "Unknown Object: " + item;
            };
            System.out.println("   " + description);
        }
    }

    // =========================================================================================
    // SECTION 5: RECORD PATTERNS & DECONSTRUCTION (JAVA 21 JEP 440)
    // =========================================================================================
    public record Point(int x, int y) {}
    public sealed interface Shape permits Circle, Rectangle {}
    public record Circle(Point center, int radius) implements Shape {}
    public record Rectangle(Point topLeft, Point bottomRight) implements Shape {}

    private static void section5_RecordPatternsAndDeconstruction() {
        printHeader("5. RECORD PATTERNS & NESTED DECONSTRUCTION (JAVA 21)");
        /*
         * WHAT ARE RECORD PATTERNS?
         * Java 21 allows DECONSTRUCTING a record into its constituent components directly
         * within an `instanceof` or `switch` pattern!
         *
         * Nested Deconstruction:
         * Instead of:
         *   Circle c = (Circle) shape;
         *   Point p = c.center();
         *   int x = p.x();
         *
         * You write:
         *   case Circle(Point(int x, int y), int r) -> ...
         */

        Shape shape1 = new Circle(new Point(10, 20), 5);
        Shape shape2 = new Rectangle(new Point(0, 10), new Point(20, 0));

        printShapeDetails(shape1);
        printShapeDetails(shape2);
    }

    public static void printShapeDetails(Shape shape) {
        // Exhaustive switch with nested record pattern deconstruction:
        String details = switch (shape) {
            case Circle(Point(int x, int y), int radius) ->
                    String.format("Circle centered at (%d, %d) with radius %d (Area: %.2f)",
                            x, y, radius, Math.PI * radius * radius);
            case Rectangle(Point(int x1, int y1), Point(int x2, int y2)) ->
                    String.format("Rectangle from (%d, %d) to (%d, %d) with Area %d",
                            x1, y1, x2, y2, Math.abs(x2 - x1) * Math.abs(y1 - y2));
        };
        System.out.println("   " + details);
    }

    // =========================================================================================
    // SECTION 6: TEXT BLOCKS & MODERN STRING FORMATTING (JAVA 15)
    // =========================================================================================
    private static void section6_TextBlocksAndFormatting() {
        printHeader("6. TEXT BLOCKS & MODERN STRING FORMATTING (JAVA 15)");
        /*
         * TEXT BLOCKS ("""):
         *  - Multi-line string literals enclosed in triple quotes.
         *  - Strips INCIDENTAL indentation automatically based on leftmost non-whitespace line.
         *  - Escape Sequences:
         *    - `\` at the end of a line: Suppresses newline (keeps text on a single line).
         *    - `\s`: Explicitly preserves trailing whitespace.
         *  - `.formatted(args)`: Modern instance method equivalent to String.format().
         */

        String htmlTemplate = """
                <html>
                    <body>
                        <h1>Welcome, %s!</h1>
                        <p>Status: Active</p>
                    </body>
                </html>
                """.formatted("Developer");

        System.out.println("Formatted HTML Text Block:\n" + htmlTemplate);

        // Escape sequence demonstration:
        String singleLineBrokenUp = """
                This is a very long string that is split across \
                multiple source code lines for readability, \
                but compiles to a single continuous string!""";

        System.out.println("Single continuous line via \\: " + singleLineBrokenUp);
    }

    // =========================================================================================
    // SECTION 7: SEQUENCED COLLECTIONS OVERVIEW (JAVA 21)
    // =========================================================================================
    private static void section7_SequencedCollectionsOverview() {
        printHeader("7. SEQUENCED COLLECTIONS OVERVIEW (JAVA 21 JEP 431)");

        // Unified encounter order API:
        SequencedCollection<String> list = new ArrayList<>(List.of("B", "C"));
        list.addFirst("A");
        list.addLast("D");

        System.out.println("SequencedCollection: " + list);
        System.out.println("First element:       " + list.getFirst());
        System.out.println("Last element:        " + list.getLast());
        System.out.println("Reversed View:       " + list.reversed());
    }

    // =========================================================================================
    // SECTION 8: LOCAL-VARIABLE TYPE INFERENCE (var) DOS & DON'TS
    // =========================================================================================
    private static void section8_VarBestPracticesAndPitfalls() {
        printHeader("8. LOCAL-VARIABLE TYPE INFERENCE (var) DOS & DON'TS");
        /*
         * RULES FOR `var`:
         *  1. `var` is NOT dynamic typing! Java remains 100% statically typed.
         *     The compiler infers the exact type at compile time.
         *  2. ONLY allowed for local variables with initializers!
         *     Cannot be used for: method parameters, return types, fields, or uninitialized variables.
         *
         * DOS:
         *  - Use when the type is obvious from the constructor:
         *      var customers = new ArrayList<Customer>();
         *  - Use for complex generic chains:
         *      var entry = map.entrySet().iterator().next();
         *  - Use in try-with-resources:
         *      try (var stream = Files.newInputStream(path)) { ... }
         *
         * DON'TS:
         *  - DO NOT combine `var` with the diamond operator `<>` without explicit type!
         *      var list = new ArrayList<>(); // INFERRED AS ArrayList<Object>!
         *  - DO NOT use when it obscures critical business types:
         *      var result = process(); // What is result? Reader has no idea!
         */

        var message = "Inferred as java.lang.String";
        var numberList = new ArrayList<Integer>(List.of(1, 2, 3));
        System.out.println("var String: " + message.getClass().getSimpleName());
        System.out.println("var List:   " + numberList);
    }

    // =========================================================================================
    // SECTION 9: REAL-WORLD MODERN DOMAIN PIPELINE
    // =========================================================================================
    public sealed interface OrderEvent permits OrderCreated, OrderShipped, OrderCancelled {}
    public record OrderCreated(String orderId, double totalAmount) implements OrderEvent {}
    public record OrderShipped(String orderId, String trackingNumber) implements OrderEvent {}
    public record OrderCancelled(String orderId, String reason) implements OrderEvent {}

    private static void section9_RealWorldDomainModeling() {
        printHeader("9. REAL-WORLD DOMAIN MODELING (SEALED ADT + PATTERN MATCHING)");

        List<OrderEvent> eventLog = List.of(
                new OrderCreated("ORD-101", 250.00),
                new OrderShipped("ORD-101", "TRK-987654321"),
                new OrderCreated("ORD-102", 89.50),
                new OrderCancelled("ORD-102", "Customer requested cancellation")
        );

        System.out.println("Processing Event Log with Pattern Matching:");
        for (OrderEvent event : eventLog) {
            String auditLog = switch (event) {
                case OrderCreated(String id, double amount) ->
                        String.format("   [EVENT: CREATED]   Order %s initiated with total $%.2f", id, amount);
                case OrderShipped(String id, String tracking) ->
                        String.format("   [EVENT: SHIPPED]   Order %s dispatched via Tracking #%s", id, tracking);
                case OrderCancelled(String id, String reason) ->
                        String.format("   [EVENT: CANCELLED] Order %s aborted! Reason: '%s'", id, reason);
            };
            System.out.println(auditLog);
        }
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}

