package org.example.designPatterns.creational.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Factory and Abstract Factory Pattern Tests")
class FactoryPatternTest {

    @Nested
    @DisplayName("Factory Method Pattern Tests")
    class FactoryMethodTests {

        @Test
        @DisplayName("WindowsDialog should create a WindowsButton and execute lifecycle")
        void testWindowsDialogCreatesWindowsButton() {
            Factory.Dialog dialog = new Factory.WindowsDialog();
            Factory.Button button = dialog.createButton();

            assertNotNull(button, "Button created by WindowsDialog must not be null");
            assertInstanceOf(Factory.WindowsButton.class, button, "Button should be an instance of WindowsButton");

            // Verify lifecycle execution does not throw exceptions
            assertDoesNotThrow(dialog::renderWindow);
        }

        @Test
        @DisplayName("LinuxDialog should create a LinuxButton and execute lifecycle")
        void testLinuxDialogCreatesLinuxButton() {
            Factory.Dialog dialog = new Factory.LinuxDialog();
            Factory.Button button = dialog.createButton();

            assertNotNull(button, "Button created by LinuxDialog must not be null");
            assertInstanceOf(Factory.LinuxButton.class, button, "Button should be an instance of LinuxButton");

            // Verify lifecycle execution does not throw exceptions
            assertDoesNotThrow(dialog::renderWindow);
        }

        @Test
        @DisplayName("Factory main method should execute successfully")
        void testFactoryMainDemo() {
            assertDoesNotThrow(() -> Factory.main(new String[]{}));
        }
    }

    @Nested
    @DisplayName("Abstract Factory Pattern Tests")
    class AbstractFactoryTests {

        @Test
        @DisplayName("WindowsFactory should produce WindowsButton and WindowsCheckbox family")
        void testWindowsFactoryFamily() {
            AbstractFactory.GUIFactory factory = new AbstractFactory.WindowsFactory();
            AbstractFactory.Application app = new AbstractFactory.Application(factory);

            assertNotNull(app.getButton());
            assertNotNull(app.getCheckbox());
            assertInstanceOf(AbstractFactory.WindowsButton.class, app.getButton());
            assertInstanceOf(AbstractFactory.WindowsCheckbox.class, app.getCheckbox());

            // Check initial state & interaction
            assertFalse(app.getCheckbox().isChecked(), "Checkbox should initially be unchecked");
            assertDoesNotThrow(app::renderUI);
            assertDoesNotThrow(app::simulateUserInteraction);
            assertTrue(app.getCheckbox().isChecked(), "Checkbox should be checked after user interaction");
        }

        @Test
        @DisplayName("MacFactory should produce MacButton and MacCheckbox family")
        void testMacFactoryFamily() {
            AbstractFactory.GUIFactory factory = new AbstractFactory.MacFactory();
            AbstractFactory.Application app = new AbstractFactory.Application(factory);

            assertNotNull(app.getButton());
            assertNotNull(app.getCheckbox());
            assertInstanceOf(AbstractFactory.MacButton.class, app.getButton());
            assertInstanceOf(AbstractFactory.MacCheckbox.class, app.getCheckbox());

            // Check initial state & interaction
            assertFalse(app.getCheckbox().isChecked(), "Checkbox should initially be unchecked");
            assertDoesNotThrow(app::renderUI);
            assertDoesNotThrow(app::simulateUserInteraction);
            assertTrue(app.getCheckbox().isChecked(), "Checkbox should be checked after user interaction");
        }

        @Test
        @DisplayName("AbstractFactory main method should execute successfully")
        void testAbstractFactoryMainDemo() {
            assertDoesNotThrow(() -> AbstractFactory.main(new String[]{}));
        }
    }
}
