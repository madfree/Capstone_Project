package com.madfree.capstoneproject.data;

public class User {

    private String userName;
    private int totalScore;
    private int gamesPlayed;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userName, int gamesPlayed, int totalScore) {
        this.userName = userName;
        this.gamesPlayed = gamesPlayed;
        this.totalScore = totalScore;
    }

    public String getUserName() {
        return userName;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

}
