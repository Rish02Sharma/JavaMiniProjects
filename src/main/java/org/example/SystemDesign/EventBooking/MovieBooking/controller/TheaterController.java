package org.example.SystemDesign.EventBooking.MovieBooking.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Movie;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Show;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Theater;
import org.example.SystemDesign.EventBooking.MovieBooking.service.TheaterService;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    public void addMovie(Movie movie){
        theaterService.addMovie(movie);
    }

    public List<Movie> getMoviesList(Theater.City city, LocalDateTime dateTime){
        return theaterService.getMoviesList(city, dateTime);
    }

    public void addTheaters(Theater theater){
        theaterService.addTheaters(theater);
    }

    public List<Theater> getTheaterListForMovie(Theater.City city, Movie movie, LocalDateTime dateTime){
        return theaterService.getTheaterListForMovie(city, movie, dateTime);
    }

    public void addShows(Show show, Theater theater){
        theaterService.addShowToTheater(show, theater);
    }

    public List<Show> getShowsForATheaterForMovie(Theater theater, Movie movie, LocalDateTime time){
        return theaterService.getShowsForATheaterForMovie(theater, movie, time);
    }


}
