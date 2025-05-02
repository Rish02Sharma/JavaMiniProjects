package org.example.designPatterns.creational.factory;

public class AbstractFactory {
    interface GUIFactory {
        Button createButton();
        Checkbox createCheckbox();
    }

    class WindowsFactory implements GUIFactory {
        public Button createButton() { return new WindowsButton(); }
        public Checkbox createCheckbox() { return new WindowsCheckbox(); }
    }

    class MacFactory implements GUIFactory {
        public Button createButton() { return new MacButton(); }
        public Checkbox createCheckbox() { return new MacCheckbox(); }
    }

    interface Button {

    }

    class WindowsButton implements Button {

    }

    class MacButton implements Button {

    }

    interface Checkbox{

    }

    class WindowsCheckbox implements Checkbox{

    }

    class MacCheckbox implements Checkbox{

    }

}
