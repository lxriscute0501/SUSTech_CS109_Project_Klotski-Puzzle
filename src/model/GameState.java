package model;

import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int[][] boardState;
    private final int moveCount;
    private final long timestamp;

    public GameState(int[][] boardState, int moveCount, long timestamp) {
        this.boardState = boardState;
        this.moveCount = moveCount;
        this.timestamp = timestamp;
    }

    public int[][] getBoardState() {
        return boardState;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public long getTimestamp() {
        return timestamp;
    }
}