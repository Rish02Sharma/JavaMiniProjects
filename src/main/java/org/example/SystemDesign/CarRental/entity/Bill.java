package org.example.SystemDesign.CarRental.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Bill {
    UUID id;
    Reservation reservation;
    double totalBillAmount;
    boolean billPaid;

    public Bill(Reservation reservation, double totalBillAmount, boolean billPaid){
        this.id = UUID.randomUUID();
        this.reservation = reservation;
        this.totalBillAmount = totalBillAmount;
        this.billPaid = billPaid;
    }
}
