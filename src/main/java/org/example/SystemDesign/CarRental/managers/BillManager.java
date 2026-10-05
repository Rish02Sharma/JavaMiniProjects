package org.example.SystemDesign.CarRental.managers;

import org.example.SystemDesign.CarRental.entity.Bill;
import org.example.SystemDesign.CarRental.strategy.BillingStrategy;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BillManager {

    private BillingStrategy billingStrategy;

    // store all bills here
    private final Map<Integer, Bill> bills = new ConcurrentHashMap<>();

    public BillManager(BillingStrategy billingStrategy) {
        this.billingStrategy = billingStrategy;
    }

    public Bill generateBill(org.example.SystemDesign.CarRental.entity.Reservation reservation) {

        Bill bill = billingStrategy.generateBill(reservation);
        bills.put(bill.getBillId(), bill);
        return bill;
    }

    public Optional<Bill> getBill(int billId) {
        return Optional.ofNullable(bills.get(billId));
    }

    public void setBillingStrategy(BillingStrategy billingStrategy) {
        this.billingStrategy = billingStrategy;
    }
}


