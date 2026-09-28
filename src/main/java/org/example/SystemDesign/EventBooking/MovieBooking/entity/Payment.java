package org.example.SystemDesign.EventBooking.MovieBooking.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {
    long id;
    Status paymentStatus;

    public enum Status{
        PENDING, STARTED, COMPLETED
    }

    public Payment(long id, Status paymentStatus){
        this.id = id;
        this.paymentStatus = paymentStatus;
    }
}
