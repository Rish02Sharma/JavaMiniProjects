package org.example.designPatterns.behavioural.state.vendingMachine;

public class DispensingState implements State {
    private final VendingMachine machine;

    public DispensingState(VendingMachine machine) {
        this.machine = machine;
    }

    public void insertMoney() {
        System.out.println("Wait! Dispensing in progress.");
    }

    public void selectItem() {
        System.out.println("Already selected item.");
    }

    public void dispense() {
        System.out.println("Dispensing item...");
        machine.releaseItem();

        if (machine.getItemCount() > 0) {
            machine.setState(machine.getNoMoneyState());
        } else {
            machine.setState(machine.getSoldOutState());
        }
    }
}

