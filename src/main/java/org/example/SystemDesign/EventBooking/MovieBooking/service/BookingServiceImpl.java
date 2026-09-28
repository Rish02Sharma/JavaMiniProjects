package org.example.SystemDesign.EventBooking.MovieBooking.service;

import org.example.SystemDesign.EventBooking.MovieBooking.entity.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BookingServiceImpl implements BookingService{

    private final Map<Long, Booking> bookings = new HashMap<>();

    @Override
    public Booking createBooking(User user, Show show, List<Seats> seats) {

        if (!show.lockSeats(seats)) {
            throw new RuntimeException("Seat unavailable");
        }

        //simulated payment flow here, we can invoke Pay method of Payment Controller
        Payment payment = new Payment(new Random().nextLong(), Payment.Status.COMPLETED);

        if (payment.getPaymentStatus() == Payment.Status.COMPLETED) {
            show.confirmSeats(seats);
            Booking booking =  new Booking(user, show, seats, payment);
            bookings.put(booking.getId(), booking);
            return booking;
        } else {
            show.releaseSeats(seats);
            throw new RuntimeException("Payment failed");
        }
    }

    public Booking getBooking(long bookingId) {
        return bookings.get(bookingId);
    }

    public List<Booking> getBookingsForUser(User user) {
        return bookings.values()
                .stream()
                .filter(b -> b.getUser().equals(user))
                .toList();
    }
}
