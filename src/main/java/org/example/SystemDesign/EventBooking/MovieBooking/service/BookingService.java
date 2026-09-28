package org.example.SystemDesign.EventBooking.MovieBooking.service;

import org.example.SystemDesign.EventBooking.MovieBooking.entity.Booking;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Seats;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Show;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.User;

import java.util.List;

public interface BookingService {
    public Booking createBooking(User user, Show show, List<Seats> seats);
    public Booking getBooking(long bookingId);
    public List<Booking> getBookingsForUser(User user);

}
