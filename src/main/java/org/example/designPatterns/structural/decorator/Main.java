package org.example.designPatterns.structural.decorator;

public class Main {
    public static void main(String[] args) {
        Pizza myPizza = new Margherita();                         // Base pizza
        myPizza = new Cheese(myPizza);                            // Add cheese
        myPizza = new Olives(myPizza);                            // Add olives
        myPizza = new Jalapenos(myPizza);                         // Add jalapenos

        System.out.println("Pizza: " + myPizza.getDescription());
        System.out.println("Cost: ₹" + myPizza.getCost());
    }
}

