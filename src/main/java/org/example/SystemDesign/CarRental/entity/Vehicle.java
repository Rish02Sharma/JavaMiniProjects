package org.example.SystemDesign.CarRental.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Vehicle {
    UUID id;
    String vehicleNumber;
    long dailyRentalCost;
    long hourlyRentalCost;
    VehicleType vehicleType;
    VehicleStatus vehicleStatus;

    public enum VehicleStatus{
        AVAILABLE,
        BOOKED,
        MAINTENANCE
    }

    public enum VehicleType{
        FOUR_WHEELER,
        TWO_WHEELER
    }
}
