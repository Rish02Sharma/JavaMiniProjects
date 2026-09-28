package org.example.SystemDesign.EventBooking.MovieBooking.entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class User {
    long id;
    String name;

    public User(long id, String name){
        this.id = id;
        this.name = name;
    }
}
