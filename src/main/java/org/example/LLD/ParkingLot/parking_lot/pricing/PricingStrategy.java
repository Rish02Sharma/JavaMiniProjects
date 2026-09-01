package org.example.LLD.ParkingLot.parking_lot.pricing;

import org.example.LLD.ParkingLot.parking_lot.Ticket;

public interface PricingStrategy {

    double calculate(Ticket ticket);
}

