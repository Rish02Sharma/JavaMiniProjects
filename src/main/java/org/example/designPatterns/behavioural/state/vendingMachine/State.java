package org.example.designPatterns.behavioural.state.vendingMachine;

public interface State {
    void insertMoney();
    void selectItem();
    void dispense();
}

