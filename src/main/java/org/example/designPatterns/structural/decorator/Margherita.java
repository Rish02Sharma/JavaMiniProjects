package org.example.designPatterns.structural.decorator;

public class Margherita implements Pizza {
    public String getDescription() {
        return "Margherita";
    }

    public double getCost() {
        return 100.0;
    }
}

