package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user of the Klotski Puzzle game.
 * This class stores basic user information and game progress.
 */

public class User implements Serializable {
    private final String username;
    private String password;
    private final boolean isGuest;
    private int highestLevel;
    private int bestSteps;
    private long bestTime = Long.MAX_VALUE;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isGuest = false;
        this.highestLevel = 0;
        this.bestSteps = Integer.MAX_VALUE;
    }

    public User(String username) {
        this.username = username;
        this.password = null;
        this.isGuest = true;
        this.highestLevel = 0;
        this.bestSteps = Integer.MAX_VALUE;
    }

    public void updateBestSteps(int moveCount) {
        if (moveCount < bestSteps) bestSteps = moveCount;
    }

    public void updateBestTime(long newTime) {
        if (newTime < bestTime) bestTime = newTime;
    }

    public String getUsername() {
        return username;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public boolean hasBestSteps() {
        return bestSteps != Integer.MAX_VALUE;
    }

    public boolean hasBestTime() {
        return bestTime != Long.MAX_VALUE;
    }

    public int getBestSteps() {
        return bestSteps;
    }

    public long getBestTime() {
        return bestTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isGuest == user.isGuest &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, isGuest);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", isGuest=" + isGuest +
                ", highestLevelCompleted=" + highestLevel +
                ", bestMoveCount=" + (bestSteps == Integer.MAX_VALUE ? "N/A" : bestSteps) +
                '}';
    }
}