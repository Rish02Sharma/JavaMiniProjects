package org.example.SystemDesign.EventBooking.MovieBooking;

import org.example.SystemDesign.EventBooking.MovieBooking.controller.BookingController;
import org.example.SystemDesign.EventBooking.MovieBooking.controller.TheaterController;
import org.example.SystemDesign.EventBooking.MovieBooking.entity.*;
import org.example.SystemDesign.EventBooking.MovieBooking.service.BookingServiceImpl;
import org.example.SystemDesign.EventBooking.MovieBooking.service.TheaterServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class BookMyShowApp {
    private TheaterController theatreController;
    private BookingController bookingController;

    public static void main(String[] args) {
        BookMyShowApp app = new BookMyShowApp();
        app.initialize();
        app.userFlow();
    }

    void initialize(){
        theatreController = new TheaterController(new TheaterServiceImpl());
        bookingController = new BookingController(new BookingServiceImpl());

        /*
         * 1. Create Movies
         */
        Movie baahubali = new Movie("BAAHUBALI", LocalDateTime.now());
        Movie avengers = new Movie("AVENGERS", LocalDateTime.now());

        // create a Theater and Shows and seats
        Theater theaterINOX = new Theater("Inox GT", Theater.City.JAIPUR);
        Theater theaterPVR = new Theater("PVR CP", Theater.City.DELHI);

        Show morningIX = new Show(baahubali, LocalDateTime.of(2026, 9, 28, 9, 30), createSeats());
        Show afternoonIX = new Show(baahubali, LocalDateTime.of(2026, 9, 28, 15, 30), createSeats());
        Show nightIX = new Show(baahubali, LocalDateTime.of(2026, 9, 28, 21, 30), createSeats());

        Show morningIX2 = new Show(avengers, LocalDateTime.of(2026, 9, 28, 9, 30), createSeats());
        Show afternoonIX2 = new Show(avengers, LocalDateTime.of(2026, 9, 28, 15, 30), createSeats());
        Show nightIX2 = new Show(avengers, LocalDateTime.of(2026, 9, 28, 21, 30), createSeats());

        theaterINOX.setShowList(List.of(morningIX, afternoonIX, nightIX, morningIX2, afternoonIX2, nightIX2));

        Show morningPv = new Show(baahubali, LocalDateTime.of(2026, 9, 28, 9, 30), createSeats());
        Show noonPv = new Show(baahubali, LocalDateTime.of(2026, 9, 28, 15, 30), createSeats());
        Show nightPv = new Show(baahubali, LocalDateTime.of(2026, 9, 28, 21, 30), createSeats());

        Show morningPv2 = new Show(avengers, LocalDateTime.of(2026, 9, 28, 9, 30), createSeats());
        Show noonPv2 = new Show(avengers, LocalDateTime.of(2026, 9, 28, 15, 30), createSeats());
        Show nightPv2 = new Show(avengers, LocalDateTime.of(2026, 9, 28, 21, 30), createSeats());

        theaterPVR.setShowList(List.of(morningPv, noonPv, nightPv, morningPv2, noonPv2, nightPv2));

        theatreController.addTheaters(theaterINOX);
        theatreController.addTheaters(theaterPVR);
    }

    void userFlow(){
        User user = new User(1, "Rishabh");

        System.out.println("User logged in: " + user.getName());

        Theater.City selectedCity = Theater.City.JAIPUR;
        LocalDateTime selectedDate = LocalDateTime.now();

        List<Movie> movieList = theatreController.getMoviesList(selectedCity, selectedDate);
        System.out.println("Movies available:");
        movieList.forEach(m -> System.out.println(" - " + m.getName()));

        // 3. User selects movie
        Movie selectedMovie = movieList.getFirst(); //selecting first movie
        System.out.println("Selected Movie: " + selectedMovie.getName());

        // 4. Show theatres and show times in city
        List<Theater> theatres = theatreController.getTheaterListForMovie(selectedCity, selectedMovie, selectedDate);
        System.out.println("Theatres available:");
        theatres.forEach(t -> System.out.println(" - " + t.getName()));

        // 6. User selects theatre
        Theater selectedTheatre = theatres.get(0);
        System.out.println("Selected Theatre: " + selectedTheatre.getName());

        // 7. Show running shows for movie + date + theatre
        List<Show> shows =
                theatreController.getShowsForATheaterForMovie(
                        selectedTheatre,
                        selectedMovie,
                        selectedDate
                );

        System.out.println("Shows available:");
        shows.forEach(s ->
                System.out.println(" - " + s.getTime())
        );

        // 8. User selects show
        Show selectedShow = shows.get(0);
        System.out.println("Selected Show Time: " + selectedShow.getTime());

        // 9. User selects seats
        List<Seats> selectedSeats = List.of(new Seats(1, Seats.SeatType.PREMIUM), new Seats(2, Seats.SeatType.PREMIUM), new Seats(45, Seats.SeatType.LUXURY));
        System.out.println("Selected Seats: " + selectedSeats);

        // 10. Booking + Payment
        Booking booking =
                bookingController.createBooking(
                        user,
                        selectedShow,
                        selectedSeats
                );

        System.out.println("BOOKING SUCCESSFUL");
        System.out.println("Booking ID: " + booking.getId());

    }

    private List<Seats> createSeats() {
        List<Seats> seats = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            seats.add(new Seats(i, Seats.SeatType.Normal));
        }

        for (int i = 20; i <= 60; i++) {
            seats.add(new Seats(i, Seats.SeatType.PREMIUM));
        }

        for (int i = 60; i <= 100; i++) {
            seats.add(new Seats(i, Seats.SeatType.LUXURY));
        }
        return seats;
    }
}
