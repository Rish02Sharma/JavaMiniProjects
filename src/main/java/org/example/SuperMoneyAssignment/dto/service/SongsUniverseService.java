package org.example.SuperMoneyAssignment.dto.service;

import org.example.SuperMoneyAssignment.dto.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SongsUniverseService {

    private static final SongsUniverseService songsUniverseService = new SongsUniverseService();
    private SongsUniverseService(){}

    public static SongsUniverseService getInstance(){
        return songsUniverseService;
    }


    private final List<Song> songs = new ArrayList<>();

    public void addSong(String name, String singer, String genre, int temp){
        Song song = new Song();
        song.setName(name);
        song.setGenre(genre);
        song.setSinger(singer);
        song.setTempo(temp);
        this.songs.add(song);
    }

    public Song getSongByName(String name){
        AtomicReference<Song> song = new AtomicReference<>();
        this.songs.forEach(s -> {
            if(s.getName().equals(name))
                song.set(s);
        });
        return song.get();
    }

    public List<Song> getAllSongs(){
        return this.songs;
    }

}
