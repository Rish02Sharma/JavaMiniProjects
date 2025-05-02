package org.example.designPatterns.behavioural.state.vendingMachine;

public class HasMoneyState implements State {
    private final VendingMachine machine;

    public HasMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    public void insertMoney() {
        System.out.println("Money already inserted.");
    }

    public void selectItem() {
        System.out.println("Item selected.");
        machine.setState(machine.getDispensingState());
    }

    public void dispense() {
        System.out.println("Select item first.");
    }
}

