package controller;

import model.Direction;
import model.MapModel;
import model.User;
import view.game.BoxComponent;
import view.game.GamePanel;

import java.util.Stack;
import javax.swing.*;

/**
 * It is a bridge to combine GamePanel(view) and MapMatrix(model) in one game.
 * You can design several methods about the game logic in this class.
 */

public class GameController {
    private final GamePanel view;
    private final MapModel model;

    private final int winx = 0;
    private final int winy = 0;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private User currentUser;
    private Stack<MoveRecord> moveHistory = new Stack<>();

    private long startTime;
    private Timer gameTimer;

    private UserDataController userDataController;
    private static final long GAME_DURATION = 5 * 60 * 1000;

    private record MoveRecord(int blockId, int fromRow, int fromCol, int toRow, int toCol) {}

    public GameController(GamePanel view, MapModel model, User user) {
        this.view = view;
        this.model = model;
        this.currentUser = user;
        this.view.setController(this);
        this.userDataController = new UserDataController(this, view, model, user);

        startGameTimer();
        userDataController.setupAutoSave(1);
    }


    public boolean moveBox(int row, int col, Direction direction) {
        int blockId = model.getId(row, col);
        if (blockId == 0) return false;

        int toRow = row + direction.getRow();
        int toCol = col + direction.getCol();

        if (!isMoveValid(blockId, row, col, toRow, toCol)) return false;

        moveHistory.push(new MoveRecord(blockId, row, col, toRow, toCol));

        updateModelPosition(blockId, row, col, toRow, toCol);
        updateViewPosition(row, col, toRow, toCol);

        return true;
    }

        /**
        * check the move validity : boundary detection && collision detection
        */
        private boolean isMoveValid(int blockId, int fromRow, int fromCol, int toRow, int toCol) {
            // boundary
            if (!model.checkInHeightSize(toRow) || !model.checkInWidthSize(toCol)) return false;

            // collision
            switch (blockId) {
                case 1: // CaoCao (2x2)
                    return checkLargeBlockMove(fromRow, fromCol, toRow, toCol, 2, 2);
                case 2: // GuanYu (2x1)
                    return checkLargeBlockMove(fromRow, fromCol, toRow, toCol, 2, 1);
                case 3: // General (1x2)
                    return checkLargeBlockMove(fromRow, fromCol, toRow, toCol, 1, 2);
                default: // solider (1x1)
                    return model.getId(toRow, toCol) == 0;
            }
        }

        private boolean checkLargeBlockMove(int fromRow, int fromCol, int toRow, int toCol, int height, int width) {
            // Check all cells the block would occupy
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    int checkRow = toRow + r;
                    int checkCol = toCol + c;

                    // Check boundaries
                    if (!model.checkInHeightSize(checkRow) || !model.checkInWidthSize(checkCol)) return false;

                    // Check if this is part of the original block position
                    boolean isOriginal = (checkRow >= fromRow && checkRow < fromRow + height) &&
                        (checkCol >= fromCol && checkCol < fromCol + width);

                    // If not original position and not empty, can't move here
                    if (!isOriginal && model.getId(checkRow, checkCol) != 0) return false;
                }
            }
            return true;
        }


    public boolean undoMove() {

        if (moveHistory.isEmpty()) {
            System.out.println("Can not undo!");
            return false;
        }

        MoveRecord lastMove = moveHistory.pop();

        // move in the opposite direction
        model.getMatrix()[lastMove.toRow()][lastMove.toCol()] = 0;
        model.getMatrix()[lastMove.fromRow()][lastMove.fromCol()] = lastMove.blockId();

        // update box vision
        BoxComponent box = view.getBoxAt(lastMove.toRow(), lastMove.toCol());
        if (box != null) {
            box.setRow(lastMove.fromRow());
            box.setCol(lastMove.fromCol());
            box.setLocation(
                    box.getCol() * view.getGRID_SIZE() + 2,
                    box.getRow() * view.getGRID_SIZE() + 2
            );
            box.repaint();
        }

        // update step count and selected box
        selectedRow = lastMove.fromRow();
        selectedCol = lastMove.fromCol();
        view.highlightSelectedBox(selectedRow, selectedCol);

        return true;
    }

    private void updateModelPosition(int blockId, int fromRow, int fromCol, int toRow, int toCol) {
        model.getMatrix()[fromRow][fromCol] = 0;

        // for large blocks, their Id is according to the up-left
        switch (blockId) {
            case 1:
                model.getMatrix()[fromRow + 1][fromCol] = 0;
                model.getMatrix()[fromRow][fromCol + 1] = 0;
                model.getMatrix()[fromRow + 1][fromCol + 1] = 0;

                model.getMatrix()[toRow][toCol] = blockId;
                model.getMatrix()[toRow + 1][toCol] = blockId;
                model.getMatrix()[toRow][toCol + 1] = blockId;
                model.getMatrix()[toRow + 1][toCol + 1] = blockId;
                break;

            case 2:
                model.getMatrix()[fromRow + 1][fromCol] = 0;
                model.getMatrix()[toRow][toCol] = blockId;
                model.getMatrix()[toRow + 1][toCol] = blockId;
                break;

            case 3:
                model.getMatrix()[fromRow][fromCol + 1] = 0;
                model.getMatrix()[toRow][toCol] = blockId;
                model.getMatrix()[toRow][toCol + 1] = blockId;
                break;

            default:
                model.getMatrix()[toRow][toCol] = blockId;
        }
    }

    private void updateViewPosition(int fromRow, int fromCol, int toRow, int toCol) {
        BoxComponent box = view.getBoxAt(fromRow, fromCol);
        if (box != null) {
            box.setRow(toRow);
            box.setCol(toCol);
            box.setLocation(
                    box.getCol() * view.getGRID_SIZE() + 2,
                    box.getRow() * view.getGRID_SIZE() + 2
            );
            box.repaint();

            selectedRow = toRow;
            selectedCol = toCol;
            view.highlightSelectedBox(toRow, toCol);
        }
    }

    public void checkWinCondition() {
        boolean win = true;
        for (int r = 0; r <= 1; r++) {
            for (int c = 0; c <= 1; c++) {
                if (model.getId(winx + r, winy + c) != 1) {
                    win = false;
                    break;
                }
            }
        }

        if (win) {
            stopGameTimer();
            long actualTime = getActualTime();
            String timeString = formatTime(actualTime);
            view.showVictoryMessage(view.getSteps(), timeString);

            if (currentUser != null && !currentUser.isGuest()) {
                currentUser.updateBestSteps(view.getSteps());
                currentUser.updateBestTime(actualTime);
                userDataController.saveGame(true);
            }
        }
    }

    private String formatTime(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void restartGame() {
        model.resetMap();
        view.initializeGame();
        selectedRow = -1;
        selectedCol = -1;
        moveHistory.clear();
        view.setSteps(0);
        view.clearSelection();
        view.setTimeLabelString("Time Left: 05:00");
        stopGameTimer();
        startGameTimer();

        // save game after restart
        if (currentUser != null && !currentUser.isGuest()) userDataController.saveGame(true);
    }

    public String exitLocation() {
        int winx1 = winx, winx2 = winx + 1;
        int winy1 = winy, winy2 = winy + 1;
        return String.format("(" + winx1 + ", " + winy1 + ") "
                + "(" + winx2 + ", " + winy1 + ") "
                + "(" + winx1 + ", " + winy2 + ") "
                + "(" + winx2 + ", " + winy2 + ")");
    }


    private void startGameTimer() {
        startTime = System.currentTimeMillis();

        gameTimer = new Timer(1000, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            long remaining = GAME_DURATION - elapsed;

            if (remaining <= 0) {
                stopGameTimer();
                view.setTimeLabelString("Time Left: 00:00");
                view.showErrorMessage("Time's up! Game over.");
                // view.setGameOverState(); // 假设你有这个方法来禁用交互
                return;
            }

            long minutes = remaining / 1000 / 60;
            long seconds = (remaining / 1000) % 60;
            String timeString = String.format("Time Left: %02d:%02d", minutes, seconds);
            view.setTimeLabelString(timeString);
        });

        gameTimer.start();
    }

    private void stopGameTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    private void restartGameTimer(long remainingSeconds) {
        stopGameTimer();

        startTime = System.currentTimeMillis() - (GAME_DURATION - remainingSeconds * 1000);

        gameTimer = new Timer(1000, e -> {
            long timeLeft = getTimeLeft();
            if (timeLeft <= 0) {
                stopGameTimer();
                view.setTimeLabelString("Time Left: 00:00");
                view.showErrorMessage("Time's up! Game Over.");
                // view.setGameOverState(); // 禁用控制
                return;
            }
            view.setTimeLabelString("Time Left: " + formatTime(timeLeft));
        });

        gameTimer.start();
    }

    public long getActualTime() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - startTime) / 1000;
    }

    public long getTimeLeft() {
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.max(0, GAME_DURATION - elapsed) / 1000;
    }

    public UserDataController getUserDataController() {
            return userDataController;
    }
}