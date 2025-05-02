package org.example.designPatterns.behavioural.state.vendingMachine;

public class SoldOutState implements State {
    private final VendingMachine machine;

    public SoldOutState(VendingMachine machine) {
        this.machine = machine;
    }

    public void insertMoney() {
        System.out.println("Machine is sold out.");
    }

    public void selectItem() {
        System.out.println("Machine is sold out.");
    }

    public void dispense() {
        System.out.println("Machine is sold out.");
    }
}

