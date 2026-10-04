package org.example.SystemDesign.CarRental.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Reservation {
    UUID id;
    LocalDateTime bookingStart;
    LocalDateTime bookingEnd;
    UUID vehicleId;
    UUID userId;
    ReservationType reservationType;
    ReservationStatus reservationStatus;

    public enum ReservationType{
        HOURLY,
        DAILY
    }

    public enum ReservationStatus{
        SCHEDULED,
        IN_USE,
        COMPLETED,
        CANCELLED
    }

    public Reservation(UUID vehicleId,
                       UUID userId,
                       LocalDateTime dateBookedFrom,
                       LocalDateTime dateBookedTo,
                       ReservationType reservationType) {

        this.id = UUID.randomUUID();
        this.vehicleId = vehicleId;
        this.userId = userId;
        this.bookingStart = dateBookedFrom;
        this.bookingEnd = dateBookedTo;
        this.reservationType = reservationType;
        this.reservationStatus = ReservationStatus.SCHEDULED;
    }
}
