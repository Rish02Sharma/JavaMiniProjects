package org.example.SystemDesign.EventBooking.MovieBooking.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Theater {
    long id;
    String name;
    City city;
    List<Show> showList;

    public Theater(String name, City city){
        this.name = name;
        this.city = city;
    }

    public Theater(String name, City city, List<Show> shows){
        this.name = name;
        this.city = city;
        this.showList=shows;
    }

    public enum City{
        DELHI, BANGALORE, GURUGRAM, JAIPUR
    }
}
