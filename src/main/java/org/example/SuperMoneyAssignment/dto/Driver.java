package org.example.SuperMoneyAssignment.dto;

import org.example.SuperMoneyAssignment.dto.service.SongsUniverseService;
import org.example.SuperMoneyAssignment.dto.service.UserDataService;
import org.example.SuperMoneyAssignment.dto.service.RecommendationService;

import java.util.Map;
import java.util.TreeMap;

public class Driver {
    private SongsUniverseService songs = SongsUniverseService.getInstance();
    private UserDataService users = UserDataService.getInstance();
    private RecommendationService recommendationService = new RecommendationService();

    public static void main(String[] args) {
        Driver driver = new Driver();
        driver.songs.addSong("song1", "AB", "Folk", 60);
        driver.songs.addSong("song2", "DEF", "Rock", 70);
        driver.songs.addSong("song3", "AB", "Country", 55);
        driver.songs.addSong("song4", "XYZ", "Rock", 60);
        driver.songs.addSong("song5", "XYZ", "Rock", 75);
        driver.songs.addSong("song6", "AB", "Country", 60);
        driver.songs.addSong("song7", "DEF", "Indie", 55);

        driver.users.addUser("A", new String[]{"song1", "song2", "song3"}, new String[]{"B", "C"});
        driver.users.addUser("B", new String[]{"song6", "song7", "song3"}, new String[]{"A", "D"});
        driver.users.addUser("C", new String[]{"song4", "song3", "song6"}, new String[]{"A", "D"});
        driver.users.addUser("D", new String[]{"song7", "song3", "song1", "song2"}, new String[]{"B", "C"});

        User user = driver.users.getUserByName("A");

        TreeMap<String,Float> map = driver.recommendationService.recommendedSongs(user);
        for (Map.Entry<String, Float> entry: map.entrySet()){
            System.out.println(entry.getKey() + "(UAS =" +entry.getValue() +")");
        }
    }

}