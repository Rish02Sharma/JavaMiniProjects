package org.example.SystemDesign.CarRental.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Store {
    UUID id;
    City city;

    public enum City{
        JAIPUR, DELHI
    }

    public Store(City city){
        this.id = UUID.randomUUID();
        this.city = city;
    }

}
