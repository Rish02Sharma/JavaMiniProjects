package org.example.LLD.ParkingLot.parking_lot.LookupStrategy;

import org.example.LLD.ParkingLot.parking_lot.Entity.ParkingSpot;

import java.util.List;

public interface ParkingSpotLookupStrategy {

    ParkingSpot selectSpot(List<ParkingSpot> spots);
}


