package org.example.SystemDesign.parking_lot.LookupStrategy;

import org.example.SystemDesign.parking_lot.Entity.ParkingSpot;

import java.util.List;

public interface ParkingSpotLookupStrategy {

    ParkingSpot selectSpot(List<ParkingSpot> spots);
}


