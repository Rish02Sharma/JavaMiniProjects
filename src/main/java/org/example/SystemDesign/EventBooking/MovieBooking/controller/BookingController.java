package org.example.SystemDesign.EventBooking.MovieBooking.controller;

import lombok.AllArgsConstructor;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Booking;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Seats;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Show;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.User;
import org.example.SystemDesign.EventBooking.MovieBooking.service.BookingService;

import java.util.List;

@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    public Booking createBooking(User user, Show selectedShow, List<Seats> selectedSeats) {
        return bookingService.createBooking(user, selectedShow, selectedSeats);
    }
}
