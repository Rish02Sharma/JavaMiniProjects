package org.example.SystemDesign.EventBooking.MovieBooking.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Movie {
   long id;
   String name;
   LocalDateTime releaseDate;

   public Movie(String name, LocalDateTime releaseDate){
      this.name = name;
      this.releaseDate=releaseDate;
   }
}
