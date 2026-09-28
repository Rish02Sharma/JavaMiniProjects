package org.example.SystemDesign.parking_lot.parkinglot;

import org.example.SystemDesign.parking_lot.Entity.Vehicle;
import org.example.SystemDesign.parking_lot.Ticket;

public class EntranceGate {

    public Ticket enter(ParkingBuilding building, Vehicle vehicle) {
        return building.allocate(vehicle);
    }
}


