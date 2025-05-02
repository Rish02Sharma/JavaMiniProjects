package org.example.designPatterns.behavioural.state.vendingMachine;

public class VendingMachine {
    private final State noMoneyState;
    private final State hasMoneyState;
    private final State dispensingState;
    private final State soldOutState;

    private State currentState;
    private int itemCount;

    public VendingMachine(int itemCount) {
        this.itemCount = itemCount;
        noMoneyState = new NoMoneyState(this);
        hasMoneyState = new HasMoneyState(this);
        dispensingState = new DispensingState(this);
        soldOutState = new SoldOutState(this);

        currentState = itemCount > 0 ? noMoneyState : soldOutState;
    }

    public void insertMoney() {
        currentState.insertMoney();
    }

    public void selectItem() {
        currentState.selectItem();
    }

    public void dispense() {
        currentState.dispense();
    }

    void releaseItem() {
        System.out.println("Item released.");
        if (itemCount > 0) itemCount--;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setState(State state) {
        currentState = state;
    }

    public State getNoMoneyState() {
        return noMoneyState;
    }

    public State getHasMoneyState() {
        return hasMoneyState;
    }

    public State getDispensingState() {
        return dispensingState;
    }

    public State getSoldOutState() {
        return soldOutState;
    }
}

