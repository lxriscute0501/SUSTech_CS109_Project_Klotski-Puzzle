package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user of the Klotski Puzzle game.
 * This class stores basic user information and game progress.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private String password;
    private final boolean isGuest;
    private int highestLevelCompleted;
    private int bestMoveCount;

    /**
     * Constructor for registered users
     * @param username the username
     * @param password the password (should be hashed in real application)
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isGuest = false;
        this.highestLevelCompleted = 0;
        this.bestMoveCount = Integer.MAX_VALUE;
    }

    /**
     * Constructor for guest users
     * @param username the guest username
     */
    public User(String username) {
        this.username = username;
        this.password = null;
        this.isGuest = true;
        this.highestLevelCompleted = 0;
        this.bestMoveCount = Integer.MAX_VALUE;
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
        return highestLevelCompleted;
    }

    public int getBestMoveCount() {
        return bestMoveCount;
    }

    public void setPassword(String password) {
        if (!isGuest) {
            this.password = password;
        }
    }

    public void updateHighestLevelCompleted(int level) {
        if (level > highestLevelCompleted) {
            this.highestLevelCompleted = level;
        }
    }

    public void updateBestMoveCount(int moveCount) {
        if (moveCount < bestMoveCount) {
            this.bestMoveCount = moveCount;
        }
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
                ", highestLevelCompleted=" + highestLevelCompleted +
                ", bestMoveCount=" + (bestMoveCount == Integer.MAX_VALUE ? "N/A" : bestMoveCount) +
                '}';
    }
}