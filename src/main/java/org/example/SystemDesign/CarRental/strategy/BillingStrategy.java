package org.example.SystemDesign.CarRental.strategy;

import org.example.SystemDesign.CarRental.entity.Bill;
import org.example.SystemDesign.CarRental.entity.Reservation;

public interface BillingStrategy {

    Bill generateBill(Reservation reservation);
}
