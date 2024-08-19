package org.example.SuperMoneyAssignment.dto.service;

import org.example.SuperMoneyAssignment.dto.Song;
import org.example.SuperMoneyAssignment.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UserDataService {

    private static final UserDataService userDataService = new UserDataService();
    private UserDataService(){}

    public static UserDataService getInstance(){
        return userDataService;
    }

    private final SongsUniverseService songs = SongsUniverseService.getInstance();

    private final List<User> activeUsers = new ArrayList<>();

    public void addUser(String name, String[] playListArr, String[] friends){
        List<Song> playlist = new ArrayList<>();
        List<User> friendList = new ArrayList<>();

        for(String song: playListArr){
            playlist.add(songs.getSongByName(song));
        }

        for(String user: friends){
            User user1 = getUserByName(user);
            if(user1 == null) {
                user1 = addEmptyUser(user);
            }
            friendList.add(user1);
        }

        User user = findPreExistingUser(name);
        user.setName(name);
        user.setFriendList(friendList);
        user.setPlayList(playlist);
        this.activeUsers.add(user);
    }

    public User addEmptyUser(String name){
        User user = new User();
        user.setName(name);
        this.activeUsers.add(user);
        return user;
    }

    public User getUserByName(String name){
        AtomicReference<User> user = new AtomicReference<>();
        this.activeUsers.forEach(s -> {
            if(s.getName().equals(name))
                user.set(s);
        });
        return user.get();
    }

    private User findPreExistingUser(String user){
        AtomicReference<User> user1 = new AtomicReference<>();
        this.activeUsers.forEach(u -> {
            if(u.getName().equals(user)){
                user1.set(u);
            }
        });
        if(user1.get()==null) return new User();
        return user1.get();
    }

}
