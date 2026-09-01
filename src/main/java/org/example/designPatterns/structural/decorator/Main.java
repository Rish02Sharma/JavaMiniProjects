package org.example.designPatterns.structural.decorator;

public class Main {
    public static void main(String[] args) {
        // Just like we have defined margherita here we can define more classes that implements pizza base like OTC or Pan crust
        Pizza myPizza = new Margherita();                         // Base pizza

        // We can add more decorator classes and have their individual cost
        myPizza = new Cheese(myPizza);                            // Add cheese
        myPizza = new Olives(myPizza);                            // Add olives
        myPizza = new Jalapenos(myPizza);                         // Add jalapenos

        System.out.println("Pizza: " + myPizza.getDescription());
        System.out.println("Cost: ₹" + myPizza.getCost());
    }
}

