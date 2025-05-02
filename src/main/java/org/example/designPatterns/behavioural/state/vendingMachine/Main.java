package org.example.designPatterns.behavioural.state.vendingMachine;

public class Main {
    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine(2);

        machine.insertMoney();
        machine.selectItem();
        machine.dispense();

        System.out.println("---");

        machine.insertMoney();
        machine.selectItem();
        machine.dispense();

        System.out.println("---");

        machine.insertMoney(); // Should show sold out
    }
}

