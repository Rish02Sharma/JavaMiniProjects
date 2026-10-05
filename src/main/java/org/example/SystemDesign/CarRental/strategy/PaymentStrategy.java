package org.example.SystemDesign.CarRental.strategy;

import org.example.SystemDesign.CarRental.entity.Bill;
import org.example.SystemDesign.CarRental.entity.Payment;

public interface PaymentStrategy {

    Payment processPayment(Bill bill, double paymentAmount);
}
