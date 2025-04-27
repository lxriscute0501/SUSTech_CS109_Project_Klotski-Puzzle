package model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the state of a game that can be saved and loaded.
 * Includes the board state, move count, and timestamp.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int ROWS = 4;  // 华容道标准行数
    private static final int COLS = 5;  // 华容道标准列数

    private final int[][] boardState;
    private final int moveCount;
    private final long timestamp;
    private final String username;  // 关联的用户名
    private final int level;        // 当前关卡

    /**
     * Constructs a new GameState
     * @param boardState the current board state (4x5 matrix)
     * @param moveCount the current move count
     * @param timestamp the save timestamp
     * @param username the associated username
     * @param level the current level
     * @throws IllegalArgumentException if boardState is invalid
     */
    public GameState(int[][] boardState, int moveCount, long timestamp, String username, int level) {
        validateBoardState(boardState);

        this.boardState = deepCopyBoard(boardState);
        this.moveCount = moveCount;
        this.timestamp = timestamp;
        this.username = username;
        this.level = level;
    }

    /**
     * Validates the board state
     * @param boardState the board to validate
     * @throws IllegalArgumentException if board is invalid
     */
    private void validateBoardState(int[][] boardState) {
        if (boardState == null) {
            throw new IllegalArgumentException("Board state cannot be null");
        }
        if (boardState.length != ROWS || boardState[0].length != COLS) {
            throw new IllegalArgumentException("Invalid board dimensions");
        }

        // 检查方块ID是否有效
        for (int[] row : boardState) {
            for (int cell : row) {
                if (cell < 0 || cell > 4) {  // 假设有效ID是0-4
                    throw new IllegalArgumentException("Invalid block ID in board state");
                }
            }
        }
    }

    /**
     * Creates a deep copy of the board state
     * @param original the original board
     * @return a deep copy
     */
    private int[][] deepCopyBoard(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }

    // Getters
    public int[][] getBoardState() {
        return deepCopyBoard(boardState);  // 返回副本保护内部状态
    }

    public int getMoveCount() {
        return moveCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return moveCount == gameState.moveCount &&
                timestamp == gameState.timestamp &&
                level == gameState.level &&
                Arrays.deepEquals(boardState, gameState.boardState) &&
                Objects.equals(username, gameState.username);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(moveCount, timestamp, username, level);
        result = 31 * result + Arrays.deepHashCode(boardState);
        return result;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "username='" + username + '\'' +
                ", level=" + level +
                ", moves=" + moveCount +
                ", saved=" + new java.util.Date(timestamp) +
                '}';
    }

    /**
     * Builder class for GameState
     */
    public static class Builder {
        private int[][] boardState;
        private int moveCount;
        private long timestamp = System.currentTimeMillis();
        private String username;
        private int level = 1;

        public Builder boardState(int[][] boardState) {
            this.boardState = boardState;
            return this;
        }

        public Builder moveCount(int moveCount) {
            this.moveCount = moveCount;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public GameState build() {
            return new GameState(boardState, moveCount, timestamp, username, level);
        }
    }
}