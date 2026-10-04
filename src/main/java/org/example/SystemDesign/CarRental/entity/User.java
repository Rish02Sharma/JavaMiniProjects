package org.example.SystemDesign.CarRental.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {
    UUID id;
    String name;

    public User(String name){
        this.id = UUID.randomUUID();
        this.name = name;
    }
}
