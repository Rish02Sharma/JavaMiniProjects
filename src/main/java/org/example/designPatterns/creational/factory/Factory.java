package org.example.designPatterns.creational.factory;

/**
 * ==============================================================================
 * PATTERN: FACTORY METHOD (Creational)
 * ==============================================================================
 *
 * 🎯 Intent:
 * Defines an interface for creating a single object, but lets subclasses decide
 * which class to instantiate. Factory Method lets a class defer instantiation
 * to subclasses.
 *
 * 📦 Key Traits:
 * 1. Creates one type of product (e.g., Button).
 * 2. Uses inheritance: The Creator class defines the abstract factory method,
 *    and Concrete Creators override it to return a specific Concrete Product.
 * 3. Open/Closed Principle (OCP): New product types can be introduced without
 *    breaking existing client code.
 * 4. Single Responsibility Principle (SRP): Product creation logic is decoupled
 *    from product consumption / core business logic.
 *
 * 👥 Roles in this Implementation:
 * - Product:           {@link Button}
 * - Concrete Products: {@link WindowsButton}, {@link LinuxButton}
 * - Creator:           {@link Dialog}
 * - Concrete Creators: {@link WindowsDialog}, {@link LinuxDialog}
 * - Client:            {@link #main(String[])} / {@link #renderDialog(Dialog)}
 * ==============================================================================
 */
public class Factory {

    // ==========================================================================
    // 1. PRODUCT INTERFACE
    // ==========================================================================
    public interface Button {
        void render();
        void onClick();
    }

    // ==========================================================================
    // 2. CONCRETE PRODUCTS
    // ==========================================================================
    public static class WindowsButton implements Button {
        @Override
        public void render() {
            System.out.println("[Windows Button] Rendering native Windows Fluent UI button with square borders.");
        }

        @Override
        public void onClick() {
            System.out.println("[Windows Button] Click event dispatched via Windows message loop (WM_COMMAND).");
        }
    }

    public static class LinuxButton implements Button {
        @Override
        public void render() {
            System.out.println("[Linux Button] Rendering native Linux GTK/GNOME flat button with rounded corners.");
        }

        @Override
        public void onClick() {
            System.out.println("[Linux Button] Click event dispatched via Linux X11/Wayland event queue.");
        }
    }

    // ==========================================================================
    // 3. CREATOR (Abstract Class)
    // ==========================================================================
    public static abstract class Dialog {

        /**
         * Factory Method: Subclasses override this method to instantiate the concrete product.
         */
        public abstract Button createButton();

        /**
         * Core business logic:
         * Note that the creator's primary job is NOT just object creation. It contains
         * core business workflows that rely on the product returned by the factory method.
         */
        public void renderWindow() {
            System.out.println("Dialog: Initializing window frame and title bar...");
            Button okButton = createButton(); // Defer instantiation to subclass
            okButton.render();
            okButton.onClick();
            System.out.println("Dialog: Window successfully rendered and ready for user input.\n");
        }
    }

    // ==========================================================================
    // 4. CONCRETE CREATORS
    // ==========================================================================
    public static class WindowsDialog extends Dialog {
        @Override
        public Button createButton() {
            return new WindowsButton();
        }
    }

    public static class LinuxDialog extends Dialog {
        @Override
        public Button createButton() {
            return new LinuxButton();
        }
    }

    // ==========================================================================
    // 5. CLIENT DEMONSTRATION
    // ==========================================================================
    public static void renderDialog(Dialog dialog) {
        // Client interacts with the abstract Creator, remaining decoupled from concrete products
        dialog.renderWindow();
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       FACTORY METHOD PATTERN DEMONSTRATION       ");
        System.out.println("==================================================\n");

        // Scenario 1: Windows environment
        System.out.println("--- Scenario 1: Windows Environment ---");
        Dialog windowsDialog = new WindowsDialog();
        renderDialog(windowsDialog);

        // Scenario 2: Linux environment
        System.out.println("--- Scenario 2: Linux Environment ---");
        Dialog linuxDialog = new LinuxDialog();
        renderDialog(linuxDialog);
    }
}
