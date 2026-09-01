package org.example.LLD.ParkingLot.parking_lot;

import org.example.LLD.ParkingLot.parking_lot.Entity.Vehicle;
import org.example.LLD.ParkingLot.parking_lot.Entity.ParkingSpot;
import org.example.LLD.ParkingLot.parking_lot.parkinglot.ParkingLevel;

import java.time.LocalDateTime;

public class Ticket {

    private final Vehicle vehicle;
    private final ParkingLevel level;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;

    public Ticket(Vehicle vehicle,
                  ParkingLevel level,
                  ParkingSpot spot) {
        this.vehicle = vehicle;
        this.level = level;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingLevel getLevel() {
        return level;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }
}


