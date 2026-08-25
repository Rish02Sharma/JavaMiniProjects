package org.example.designPatterns.solidPrinciples;

/**
 * ============================================================================
 * SOLID PRINCIPLES: Dependency Inversion Principle (DIP)
 * ============================================================================
 *
 * Definition (Robert C. Martin / Uncle Bob):
 * 1. "High-level modules should not depend on low-level modules.
 *     Both should depend on abstractions."
 * 2. "Abstractions should not depend on details.
 *     Details (concrete implementations) should depend on abstractions."
 *
 * Core Concepts & Distinctions:
 * - DIP (Principle): High-level architectural guideline stating software should
 *   depend on interfaces/abstract classes rather than concrete implementations.
 * - IoC (Inversion of Control - Pattern): Inverting the control flow so a framework
 *   or container controls object creation and lifecycle rather than the class itself.
 * - DI (Dependency Injection - Technique): The mechanism of supplying dependencies
 *   to a dependent object from the outside (Constructor, Setter, or Field injection).
 *
 * Common Smells / Red Flags:
 * 1. Direct use of the `new` keyword inside high-level business services to instantiate
 *    low-level I/O, database, client, or messaging classes.
 * 2. Inability to unit test business logic in isolation without spinning up
 *    actual databases, external APIs, or disk storage (no mockability).
 * 3. Fragile architecture: A change to a database driver or third-party SDK forces
 *    changes directly inside the core domain logic.
 */
public class DependencyInversionPrincipleNotes {

    // ========================================================================
    // 1. VIOLATION EXAMPLE: Tightly Coupled Notification Flow
    // ========================================================================
    /**
     * Why this violates DIP:
     * - `OrderServiceBad` (High-level business workflow) directly instantiates and
     *   depends on `MySqlOrderRepositoryBad` and `SmsGatewayBad` (Low-level details).
     * - Cannot unit test `OrderServiceBad` without an active MySQL DB and SMS Gateway.
     * - Switching persistence to PostgreSQL or notification to Email/WhatsApp requires
     *   editing `OrderServiceBad`.
     */
    static class MySqlOrderRepositoryBad {
        public void insertOrder(String orderId, double amount) {
            System.out.println("SQL: INSERT INTO orders VALUES ('" + orderId + "', " + amount + ");");
        }
    }

    static class SmsGatewayBad {
        public void sendSms(String phoneNumber, String message) {
            System.out.println("SMS sent to " + phoneNumber + ": " + message);
        }
    }

    static class OrderServiceBad {
        // VIOLATION: Hardcoded dependencies on low-level concrete classes
        private final MySqlOrderRepositoryBad repository = new MySqlOrderRepositoryBad();
        private final SmsGatewayBad smsGateway = new SmsGatewayBad();

        public void checkout(String orderId, double amount, String customerPhone) {
            // High-level business logic mixed with concrete dependencies
            repository.insertOrder(orderId, amount);
            smsGateway.sendSms(customerPhone, "Your order " + orderId + " is placed successfully.");
        }
    }


    // ========================================================================
    // 2. REFACTORED / DIP-COMPLIANT EXAMPLE: Decoupled via Abstractions
    // ========================================================================
    /**
     * Solution:
     * 1. Define abstractions (interfaces) for persistence and messaging.
     * 2. High-level service accepts abstractions via Constructor Injection.
     * 3. Low-level adapters implement the abstractions.
     */

    // --- Abstractions (Contracts) ---

    interface OrderRepository {
        void saveOrder(Order order);
        Order findById(String orderId);
    }

    interface NotificationChannel {
        void send(String recipient, String message);
    }

    // --- Domain Entity ---

    static class Order {
        private final String orderId;
        private final double amount;
        private final String customerContact;

        public Order(String orderId, double amount, String customerContact) {
            this.orderId = orderId;
            this.amount = amount;
            this.customerContact = customerContact;
        }

        public String getOrderId() { return orderId; }
        public double getAmount() { return amount; }
        public String getCustomerContact() { return customerContact; }
    }

    // --- Low-Level Concrete Implementations (Details) ---

    static class PostgresOrderRepository implements OrderRepository {
        @Override
        public void saveOrder(Order order) {
            System.out.println("PostgreSQL: Persisting order " + order.getOrderId() + " with amount " + order.getAmount());
        }

        @Override
        public Order findById(String orderId) {
            return new Order(orderId, 250.0, "+919876543210");
        }
    }

    static class MongoDbOrderRepository implements OrderRepository {
        @Override
        public void saveOrder(Order order) {
            System.out.println("MongoDB: db.orders.insertOne({ id: '" + order.getOrderId() + "' })");
        }

        @Override
        public Order findById(String orderId) {
            return new Order(orderId, 500.0, "user@domain.com");
        }
    }

    static class TwilioSmsChannel implements NotificationChannel {
        @Override
        public void send(String recipient, String message) {
            System.out.println("Twilio SMS to " + recipient + ": " + message);
        }
    }

    static class SendGridEmailChannel implements NotificationChannel {
        @Override
        public void send(String recipient, String message) {
            System.out.println("SendGrid Email to " + recipient + ": " + message);
        }
    }

    // --- High-Level Module (Depends ONLY on Abstractions) ---

    static class OrderService {
        private final OrderRepository orderRepository;
        private final NotificationChannel notificationChannel;

        // Dependency Injection via Constructor
        public OrderService(OrderRepository orderRepository, NotificationChannel notificationChannel) {
            this.orderRepository = orderRepository;
            this.notificationChannel = notificationChannel;
        }

        public void checkout(Order order) {
            // Business workflow executes cleanly through abstractions
            orderRepository.saveOrder(order);
            notificationChannel.send(order.getCustomerContact(),
                    "Order " + order.getOrderId() + " confirmed. Total: INR " + order.getAmount());
        }
    }


    // ========================================================================
    // 3. UNIT TESTING & MOCKABILITY DEMO (The Real Power of DIP)
    // ========================================================================
    /**
     * Demonstrates how DIP enables fast, in-memory unit tests without external infrastructure.
     */
    static class MockOrderRepository implements OrderRepository {
        private boolean saveCalled = false;

        @Override
        public void saveOrder(Order order) { this.saveCalled = true; }

        @Override
        public Order findById(String orderId) { return null; }

        public boolean isSaveCalled() { return saveCalled; }
    }

    static class MockNotificationChannel implements NotificationChannel {
        private String lastMessage = null;

        @Override
        public void send(String recipient, String message) { this.lastMessage = message; }

        public String getLastMessage() { return lastMessage; }
    }

    // Client / Composition Root Setup
    public static void main(String[] args) {
        // Production Wiring
        OrderRepository prodRepo = new PostgresOrderRepository();
        NotificationChannel prodNotifier = new TwilioSmsChannel();
        OrderService prodService = new OrderService(prodRepo, prodNotifier);

        Order order = new Order("ORD-9021", 1499.00, "+919876543210");
        prodService.checkout(order);

        // Test Wiring (Fast, zero network or DB dependencies)
        MockOrderRepository mockRepo = new MockOrderRepository();
        MockNotificationChannel mockNotifier = new MockNotificationChannel();
        OrderService testService = new OrderService(mockRepo, mockNotifier);

        testService.checkout(order);
        System.out.println("Test Verification -> Save Invoked: " + mockRepo.isSaveCalled());
        System.out.println("Test Verification -> Notification Content: " + mockNotifier.getLastMessage());
    }
}
