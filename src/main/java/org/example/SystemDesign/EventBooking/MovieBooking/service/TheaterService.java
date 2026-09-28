package org.example.SystemDesign.EventBooking.MovieBooking.service;

import org.example.SystemDesign.EventBooking.MovieBooking.entity.Movie;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Show;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Theater;

import java.time.LocalDateTime;
import java.util.List;

public interface TheaterService {
    public void addMovie(Movie movie);

    List<Movie> getMoviesList(Theater.City city, LocalDateTime time);

    void addTheaters(Theater theater);

    List<Theater> getTheaterListForMovie(Theater.City city, Movie movie, LocalDateTime dateTime);

    void addShowToTheater(Show show, Theater theater);

    List<Show> getShowsForATheaterForMovie(Theater theater, Movie movie, LocalDateTime time);
}
