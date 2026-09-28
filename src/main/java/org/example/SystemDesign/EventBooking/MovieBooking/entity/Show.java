package org.example.SystemDesign.EventBooking.MovieBooking.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
public class Show {
    long id;
    List<Seats> seatsList;
    Movie movie;
    LocalDateTime time;

    private final Map<Long, Seats.SeatStatus> seatStatusMap = new HashMap<>();
    private final Map<Long, ReentrantLock> seatLocks = new HashMap<>();

    public Show(Movie movie, LocalDateTime time, List<Seats> seatsList){
        this.id = new Random().nextLong();
        this.movie = movie;
        this.time = time;
        this.seatsList = seatsList;
    }

    public boolean lockSeats(List<Seats> seatIds) {
        List<Seats> sorted = new ArrayList<>(seatIds);
        List<Long> sortedList = sorted.stream().map()

        //sorting i am doing to avoid deadlock scenario
        Collections.sort(sorted);

        List<ReentrantLock> acquiredLocks = new ArrayList<>();

        try {
            // Phase 1: acquire all locks
            for (int seatId : sorted) {
                ReentrantLock lock = seatLocks.get(seatId);
                lock.lock();
                acquiredLocks.add(lock);
            }

            // Phase 2: validate availability
            for (int seatId : sorted) {
                if (seatStatusMap.get(seatId) != Seats.SeatStatus.AVAILABLE) {
                    return false;
                }
            }

            // Phase 3: mark LOCKED
            for (int seatId : sorted) {
                seatStatusMap.put(seatId, Seats.SeatStatus.LOCKED);
            }

            return true;

        } finally {
            // Phase 4: release locks
            for (ReentrantLock lock : acquiredLocks) {
                lock.unlock();
            }
        }
    }

    public void confirmSeats(List<Seats> seatIds) {
        for (Seats seat : seatIds) {
            seatStatusMap.put(seat.getId(), Seats.SeatStatus.BOOKED);
        }
    }

    public void releaseSeats(List<Seats> seatIds) {
        for (Seats seat : seatIds) {
            seatStatusMap.put(seat.getId(), Seats.SeatStatus.AVAILABLE);
        }
    }

}
