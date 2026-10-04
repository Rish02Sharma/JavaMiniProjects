package org.example.SystemDesign.CarRental.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Payment {
    UUID id;
    long amountPaid;
    Bill billId;
    LocalDateTime paymentDate;

    public Payment(long amountPaid, Bill billId, LocalDateTime paymentDate){
        this.id = UUID.randomUUID();
        this.amountPaid = amountPaid;
        this.billId = billId;
        this.paymentDate = paymentDate;
    }
}
