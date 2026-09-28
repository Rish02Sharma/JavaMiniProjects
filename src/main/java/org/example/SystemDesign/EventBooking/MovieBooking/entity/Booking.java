package org.example.SystemDesign.EventBooking.MovieBooking.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.SystemDesign.EventBooking.MovieBooking.service.BookingService;

import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Booking {
    long id;
    Show show;
    List<Seats> seatsList;
    User user;
    Payment payment;

    public Booking(User user, Show show, List<Seats> seats, Payment payment){
        this.id = new Random().nextLong();
        this.user = user;
        this.show = show;
        this.seatsList = seats;
        this.payment = payment;
    }
}
