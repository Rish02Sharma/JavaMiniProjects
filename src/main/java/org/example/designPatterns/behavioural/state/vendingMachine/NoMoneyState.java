package org.example.designPatterns.behavioural.state.vendingMachine;

public class NoMoneyState implements State {
    private final VendingMachine machine;

    public NoMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    public void insertMoney() {
        System.out.println("Money inserted.");
        machine.setState(machine.getHasMoneyState());
    }

    public void selectItem() {
        System.out.println("Insert money first.");
    }

    public void dispense() {
        System.out.println("Insert money first.");
    }
}

