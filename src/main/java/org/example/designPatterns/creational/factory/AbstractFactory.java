package org.example.designPatterns.creational.factory;

/**
 * ==============================================================================
 * PATTERN: ABSTRACT FACTORY (Creational)
 * ==============================================================================
 *
 * 🎯 Intent:
 * Provides an interface for creating families of related or dependent objects
 * without specifying their concrete classes.
 *
 * 📦 Key Traits:
 * 1. Creates families of related products (e.g., Button + Checkbox).
 * 2. Uses composition: The client is supplied with a concrete factory and delegates
 *    creation of the product family through the factory's interface.
 * 3. Enforces consistency: Guarantees that products from the same family (e.g.,
 *    WindowsButton + WindowsCheckbox) are always used together, preventing
 *    incompatible UI mixing (e.g., WindowsButton with MacCheckbox).
 * 4. Open/Closed Principle (OCP): New product families (e.g., LinuxFactory)
 *    can be added without altering existing client code.
 *
 * 👥 Roles in this Implementation:
 * - Abstract Factory:  {@link GUIFactory}
 * - Concrete Factory:  {@link WindowsFactory}, {@link MacFactory}
 * - Abstract Products: {@link Button}, {@link Checkbox}
 * - Concrete Products: {@link WindowsButton}, {@link WindowsCheckbox},
 *                      {@link MacButton}, {@link MacCheckbox}
 * - Client:            {@link Application}, {@link #main(String[])}
 * ==============================================================================
 */
public class AbstractFactory {

    // ==========================================================================
    // 1. ABSTRACT PRODUCTS
    // ==========================================================================
    public interface Button {
        void render();
        void onClick();
    }

    public interface Checkbox {
        void render();
        void toggle();
        boolean isChecked();
    }

    // ==========================================================================
    // 2. CONCRETE PRODUCTS: WINDOWS FAMILY
    // ==========================================================================
    public static class WindowsButton implements Button {
        @Override
        public void render() {
            System.out.println("[Windows Button] Rendering native Windows Fluent UI button with square borders.");
        }

        @Override
        public void onClick() {
            System.out.println("[Windows Button] Click event triggered: Handled by Windows OS event subsystem.");
        }
    }

    public static class WindowsCheckbox implements Checkbox {
        private boolean checked;

        public WindowsCheckbox() {
            this(false);
        }

        public WindowsCheckbox(boolean initialChecked) {
            this.checked = initialChecked;
        }

        @Override
        public void render() {
            System.out.println("[Windows Checkbox] Rendering square checkbox [checked=" + checked + "] with Windows theme.");
        }

        @Override
        public void toggle() {
            this.checked = !this.checked;
            System.out.println("[Windows Checkbox] Toggled state to: " + (checked ? "CHECKED" : "UNCHECKED"));
        }

        @Override
        public boolean isChecked() {
            return checked;
        }
    }

    // ==========================================================================
    // 3. CONCRETE PRODUCTS: MAC FAMILY
    // ==========================================================================
    public static class MacButton implements Button {
        @Override
        public void render() {
            System.out.println("[Mac Button] Rendering rounded Aqua-style button with soft shadow gradient.");
        }

        @Override
        public void onClick() {
            System.out.println("[Mac Button] Click event triggered: Triggering macOS haptic feedback pulse.");
        }
    }

    public static class MacCheckbox implements Checkbox {
        private boolean checked;

        public MacCheckbox() {
            this(false);
        }

        public MacCheckbox(boolean initialChecked) {
            this.checked = initialChecked;
        }

        @Override
        public void render() {
            System.out.println("[Mac Checkbox] Rendering pill-shaped checkbox [checked=" + checked + "] with macOS blue accent.");
        }

        @Override
        public void toggle() {
            this.checked = !this.checked;
            System.out.println("[Mac Checkbox] Toggled state to: " + (checked ? "CHECKED" : "UNCHECKED"));
        }

        @Override
        public boolean isChecked() {
            return checked;
        }
    }

    // ==========================================================================
    // 4. ABSTRACT FACTORY INTERFACE
    // ==========================================================================
    public interface GUIFactory {
        Button createButton();
        Checkbox createCheckbox();
    }

    // ==========================================================================
    // 5. CONCRETE FACTORIES
    // ==========================================================================
    public static class WindowsFactory implements GUIFactory {
        @Override
        public Button createButton() {
            return new WindowsButton();
        }

        @Override
        public Checkbox createCheckbox() {
            return new WindowsCheckbox();
        }
    }

    public static class MacFactory implements GUIFactory {
        @Override
        public Button createButton() {
            return new MacButton();
        }

        @Override
        public Checkbox createCheckbox() {
            return new MacCheckbox();
        }
    }

    // ==========================================================================
    // 6. CLIENT CODE (Application)
    // ==========================================================================
    public static class Application {
        private final Button button;
        private final Checkbox checkbox;

        /**
         * The client receives a factory instance through composition / dependency injection.
         * The client never directly instantiates concrete UI components.
         */
        public Application(GUIFactory factory) {
            this.button = factory.createButton();
            this.checkbox = factory.createCheckbox();
        }

        public void renderUI() {
            System.out.println("Application: Rendering UI component suite...");
            button.render();
            checkbox.render();
            System.out.println("Application: UI component suite rendered successfully.\n");
        }

        public void simulateUserInteraction() {
            System.out.println("Application: Simulating user interactions...");
            button.onClick();
            checkbox.toggle();
            System.out.println();
        }

        public Button getButton() {
            return button;
        }

        public Checkbox getCheckbox() {
            return checkbox;
        }
    }

    // ==========================================================================
    // 7. DEMONSTRATION ENTRY POINT
    // ==========================================================================
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("     ABSTRACT FACTORY PATTERN DEMONSTRATION       ");
        System.out.println("==================================================\n");

        // Scenario 1: Windows Client Configuration
        System.out.println("--- Scenario 1: Configuring Application for Windows ---");
        GUIFactory windowsFactory = new WindowsFactory();
        Application windowsApp = new Application(windowsFactory);
        windowsApp.renderUI();
        windowsApp.simulateUserInteraction();

        // Scenario 2: Mac Client Configuration
        System.out.println("--- Scenario 2: Configuring Application for macOS ---");
        GUIFactory macFactory = new MacFactory();
        Application macApp = new Application(macFactory);
        macApp.renderUI();
        macApp.simulateUserInteraction();
    }
}
