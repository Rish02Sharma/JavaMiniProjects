package org.example.SystemDesign.EventBooking.MovieBooking.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
public class Seats {
    long id;
    SeatType type;
    SeatStatus seatStatus;

    public enum SeatType{
        Normal, PREMIUM, LUXURY
    }

    public enum SeatStatus {
        AVAILABLE, LOCKED, BOOKED
    }

    public Seats(long id, SeatType type){
        this.id = id;
        this.type = type;
        this.seatStatus = SeatStatus.AVAILABLE;
    }
}
