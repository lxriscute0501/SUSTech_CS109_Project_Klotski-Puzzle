package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user of the Klotski Puzzle game.
 * This class stores basic user information and game progress.
 */
public class User implements Serializable {
    // private static final long serialVersionUID = 1L;

    private final String username;
    private String password;
    private final boolean isGuest;
    private int highestLevel;
    private int bestStepCount;
    private long bestTime = Long.MAX_VALUE;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isGuest = false;
        this.highestLevel = 0;
        this.bestStepCount = Integer.MAX_VALUE;
    }

    public User(String username) {
        this.username = username;
        this.password = null;
        this.isGuest = true;
        this.highestLevel = 0;
        this.bestStepCount = Integer.MAX_VALUE;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public int getHighestLevelCompleted() {
        return highestLevel;
    }

    public int getBestMoveCount() {
        return bestStepCount;
    }

    public long getBestTime() {
        return bestTime;
    }

    public void setPassword(String password) {
        if (!isGuest) this.password = password;
    }

    public void updateHighestLevel(int level) {
        if (level > highestLevel) this.highestLevel= level;
    }

    public void updateBestMoveCount(int moveCount) {
        if (moveCount < bestStepCount) this.bestStepCount = moveCount;
    }

    public void updateBestTime(long newTime) {
        if (newTime < bestTime) {
            bestTime = newTime;
        }
    }

    public String getBestStepCountString() {
        if (bestStepCount == Integer.MAX_VALUE) return " N/A";
        else return String.format(" " + bestStepCount);
    }

    public String getBestTimeString() {
        if (bestTime == Long.MAX_VALUE) return " N/A";
        else return String.format(" %02d:%02d", bestTime/60, bestTime%60);
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
                ", bestMoveCount=" + (bestStepCount == Integer.MAX_VALUE ? "N/A" : bestStepCount) +
                '}';
    }
}