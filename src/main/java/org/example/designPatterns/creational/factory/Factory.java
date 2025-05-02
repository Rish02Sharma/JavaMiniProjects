package org.example.designPatterns.creational.factory;


/*
* ✅ Purpose:
Provides an interface for creating a single object, but lets subclasses decide which class to instantiate.

📦 Key Traits:
Creates one type of object.
Uses inheritance to vary the product being created.
Common in frameworks and plugin-based designs.
* */


public class Factory {
    abstract class Dialog {
        abstract Button createButton(); // Factory Method
    }

    class WindowsDialog extends Dialog {
        Button createButton() {
            return new WindowsButton();
        }
    }

    class LinuxDialog extends Dialog {
        Button createButton() {
            return new LinuxButton();
        }
    }

    interface Button {

    }

    class WindowsButton implements Button {

    }

    class LinuxButton implements Button {

    }

}
