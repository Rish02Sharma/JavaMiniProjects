package org.example.designPatterns.structural.decorator;

public class Jalapenos extends PizzaDecorator {
    public Jalapenos(Pizza pizza) {
        super(pizza);
    }

    public String getDescription() {
        return pizza.getDescription() + ", Jalapenos";
    }

    public double getCost() {
        return pizza.getCost() + 25.0;
    }
}

