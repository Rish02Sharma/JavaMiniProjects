package org.example.SystemDesign.EventBooking.MovieBooking.service;

import org.example.SystemDesign.EventBooking.MovieBooking.entity.Movie;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Show;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.Theater;

import java.time.LocalDateTime;
import java.util.*;

public class TheaterServiceImpl implements TheaterService {

    private final Map<Theater.City, List<Theater>> cityTheatres = new HashMap<>();

    public void addMovie(Movie movie) {
    }

    @Override
    public List<Movie> getMoviesList(Theater.City city, LocalDateTime time) {
        Set<Movie> movies = new HashSet<>();
        List<Theater> theaters = cityTheatres.getOrDefault(city, List.of());

        for(Theater theater: theaters){
            for(Show show: theater.getShowList()){
                movies.add(show.getMovie());
            }
        }

        return movies.stream().toList();
    }

    @Override
    public void addTheaters(Theater theater) {
        cityTheatres
                .computeIfAbsent(theater.getCity(), c -> new ArrayList<>())
                .add(theater);
    }

    @Override
    public List<Theater> getTheaterListForMovie(Theater.City city, Movie movie, LocalDateTime dateTime) {

        List<Theater> theatres = cityTheatres.getOrDefault(city, List.of());

        return theatres.stream()
                .filter(t -> t.getShowList().stream()
                        .anyMatch(s -> s.getTime().isAfter(dateTime) && s.getMovie().equals(movie)))
                .toList();
    }

    @Override
    public void addShowToTheater(Show show, Theater theater) {

    }

    @Override
    public List<Show> getShowsForATheaterForMovie(Theater theater, Movie movie, LocalDateTime time) {
        List<Show> result = new ArrayList<>();

        for (Show show : theater.getShowList()) {
                if (show.getMovie().equals(movie) && show.getTime().isAfter(time)) {
                    result.add(show);
                }

        }
        return result;
    }
}
