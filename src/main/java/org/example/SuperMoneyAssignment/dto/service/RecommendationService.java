package org.example.SuperMoneyAssignment.dto.service;

import org.example.SuperMoneyAssignment.dto.Song;
import org.example.SuperMoneyAssignment.dto.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RecommendationService {
    private final SongsUniverseService songService = SongsUniverseService.getInstance();

    public TreeMap<String, Float> recommendedSongs(User user){
        Set<String> playlistSet = new HashSet<>();
        Set<String> attributes = new HashSet<>();
        int playListSize = user.getPlayList().size();
        user.getPlayList().forEach(song -> {
            playlistSet.add(song.getName());
            attributes.add(song.getGenre());
            attributes.add(song.getSinger());
            attributes.add(song.getTempo()+"");
        });


        List<Song> allSongs = songService.getAllSongs();
        TreeMap<String, Float> recommendationList = new TreeMap<>();
        allSongs.forEach(song -> {
            if(!playlistSet.contains(song.getName())){
                float ups = calculateUPS(attributes, song, playListSize);
                float ufps = calculateUFPS(user.getFriendList(), song);
                recommendationList.put(song.getName(), (ups+ufps));
            }
        });
        return recommendationList;
    }


    private float calculateUPS(Set<String> attributes, Song song, int sizeOfPlaylist){
        int si=0;
        if(attributes.contains(song.getGenre())) si++;
        if(attributes.contains(song.getSinger())) si++;
        if(attributes.contains(song.getTempo()+"")) si++;

        return (float) si /sizeOfPlaylist;
    }

    private float calculateUFPS(List<User> friends, Song song){
        AtomicInteger friendsWithSong= new AtomicInteger();
        friends.forEach(friend->{
            friend.getPlayList().forEach(s-> {
                if(s.getName().equals(song.getName())) friendsWithSong.getAndIncrement();
            });
        });
        return (float) friendsWithSong.get() /friends.size();
    }
}
