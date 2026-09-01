package org.example.LLD.ParkingLot.parking_lot.parkinglot;

import org.example.LLD.ParkingLot.parking_lot.Entity.Vehicle;
import org.example.LLD.ParkingLot.parking_lot.Ticket;

public class EntranceGate {

    public Ticket enter(ParkingBuilding building, Vehicle vehicle) {
        return building.allocate(vehicle);
    }
}


