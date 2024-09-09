package org.example.SuperMoneyAssignment.dto;

import java.util.List;

public class User {
    private String name;
    private List<Song> playList;
    private List<User> friendList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getPlayList() {
        return this.playList;
    }

    public void setPlayList(List<Song> playList) {
        this.playList = playList;
    }

    public List<User> getFriendList() {
        return this.friendList;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }
}
