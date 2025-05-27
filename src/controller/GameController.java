package controller;

import model.*;
import view.game.BoxComponent;
import view.game.GamePanel;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import javax.swing.*;

/**
 * It is a bridge to combine GamePanel(view) and MapMatrix(model) in one game.
 * You can design several methods about the game logic in this class.
 */

public class GameController {
    private final GamePanel view;
    private final MapModel model;

    private final int winx = 1;
    private final int winy = 3;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private User currentUser;
    private Stack<MoveRecord> moveHistory = new Stack<>();

    private long startTime;
    private Timer gameTimer;

    private UserDataController userDataController;
    private static long GAME_DURATION = 5 * 60 * 1000;

    public enum Tool { HAMMER, OBSTACLE, NONE }
    private Tool currentTool = Tool.NONE;

    private FireworksEffect fireworks;
    private boolean isWinning = false;
    private long winTime;
    private final Random random = new Random();

    public static class MoveRecord {
        private final int blockId;
        private final int fromRow, fromCol;
        private final int toRow, toCol;
        private final boolean isValid;

        public MoveRecord(int blockId, int fromRow, int fromCol, int toRow, int toCol, boolean isValid) {
            this.blockId = blockId;
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
            this.isValid = isValid;
        }

        public int getBlockId() {
            return blockId;
        }

        public int getFromRow() {
            return fromRow;
        }

        public int getFromCol() {
            return fromCol;
        }

        public int getToRow() {
            return toRow;
        }

        public int getToCol() {
            return toCol;
        }

        public boolean isValid() {
            return isValid;
        }
    }


    public GameController(GamePanel view, MapModel model, User user) {
        this.view = view;
        this.model = model;
        this.currentUser = user;
        this.view.setController(this);
        this.userDataController = new UserDataController(this, view, model, user);

        startGameTimer(300);
        userDataController.setupAutoSave(1);
    }


    public boolean moveBox(int row, int col, Direction direction) {
        int blockId = model.getId(row, col);
        if (blockId == 0) return false;

        int toRow = row + direction.getRow();
        int toCol = col + direction.getCol();

        if (!isMoveValid(blockId, row, col, toRow, toCol)) return false;

        moveHistory.push(new MoveRecord(blockId, row, col, toRow, toCol, true));
        updateModelPosition(blockId, row, col, toRow, toCol);
        updateViewPosition(row, col, toRow, toCol);
        return true;
    }

    private boolean isMoveValid(int blockId, int fromRow, int fromCol, int toRow, int toCol) {

            if (model.getId(toRow, toCol) == -1) return false;
            if (!model.checkInHeightSize(toRow) || !model.checkInWidthSize(toCol)) return false;

            switch (blockId) {
                case 1:
                    return checkLargeBlockMove(fromRow, fromCol, toRow, toCol, 2, 2);
                case 2:
                    return checkLargeBlockMove(fromRow, fromCol, toRow, toCol, 2, 1);
                case 3:
                    return checkLargeBlockMove(fromRow, fromCol, toRow, toCol, 1, 2);
                default:
                    return model.getId(toRow, toCol) == 0;
            }
        }

    private boolean checkLargeBlockMove(int fromRow, int fromCol, int toRow, int toCol, int height, int width) {
        for (int r = 0; r < height; r++)
        {
            for (int c = 0; c < width; c++)
            {
                int checkRow = toRow + r;
                int checkCol = toCol + c;

                if (model.getId(checkRow, checkCol) == -1) return false;
                if (!model.checkInHeightSize(checkRow) || !model.checkInWidthSize(checkCol)) return false;

                boolean isOriginal = (checkRow >= fromRow && checkRow < fromRow + height) &&
                        (checkCol >= fromCol && checkCol < fromCol + width);

                if (!isOriginal && model.getId(checkRow, checkCol) != 0) return false;
            }
        }
        return true;
    }

    public boolean undoMove() {
        if (!moveHistory.isEmpty()) {
            MoveRecord lastMove = moveHistory.pop();
            int blockId = lastMove.getBlockId();

            if (model.isOccupied(lastMove.getFromRow(), lastMove.getFromCol())) {
                moveHistory.push(lastMove);
                System.out.println("Undo blocked: position is occupied!");
                return false;
            }

            BoxComponent box = view.getBoxAt(lastMove.getToRow(), lastMove.getToCol());
            if (box != null) {
                updateModelPosition(blockId,
                        lastMove.getToRow(), lastMove.getToCol(),
                        lastMove.getFromRow(), lastMove.getFromCol()
                );

                box.setRow(lastMove.getFromRow());
                box.setCol(lastMove.getFromCol());
                box.setLocation(
                        box.getCol() * view.getGRID_SIZE() + 2,
                        box.getRow() * view.getGRID_SIZE() + 2
                );
                box.repaint();

                selectedRow = lastMove.getFromRow();
                selectedCol = lastMove.getFromCol();
                view.highlightSelectedBox(selectedRow, selectedCol);
                return true;
            } else {
                moveHistory.push(lastMove);
                System.out.println("Undo failed: block already deleted.");
                return false;
            }
        }

        System.out.println("No valid moves to undo!");
        return false;
    }


    private void updateModelPosition(int blockId, int fromRow, int fromCol, int toRow, int toCol) {
        model.getMatrix()[fromRow][fromCol] = 0;

        // for large blocks, each id is according to the up-left
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
            moveWithAnimation(fromRow, fromCol, toRow, toCol, box);
        }
    }

    public void moveWithAnimation(int fromRow, int fromCol, int toRow, int toCol, BoxComponent box) {
        final int startX = fromCol * view.getGRID_SIZE() + 2;
        final int startY = fromRow * view.getGRID_SIZE() + 2;
        final int targetX = toCol * view.getGRID_SIZE() + 2;
        final int targetY = toRow * view.getGRID_SIZE() + 2;
        final int duration = 200;
        final long startTime = System.currentTimeMillis();

        Timer timer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / duration);
            float easedProgress = easeOutQuad(progress);

            int currentX = (int) (startX + (targetX - startX) * easedProgress);
            int currentY = (int) (startY + (targetY - startY) * easedProgress);

            box.setLocation(currentX, currentY);
            box.repaint();

            if (progress >= 1.0f) {
                ((Timer) e.getSource()).stop();
                box.setRow(toRow);
                box.setCol(toCol);
                selectedRow = toRow;
                selectedCol = toCol;
                view.highlightSelectedBox(toRow, toCol);
                view.repaint();
            }
        });

        timer.setInitialDelay(0);
        timer.start();
    }

    private float easeOutQuad(float t) {
        return t * (2 - t);
    }

    public void checkWinCondition() {
        boolean win = true;
        for (int i = 0; i <= 1; i++)
            for (int j = 0; j <= 1; j++)
            {
                if (model.getId(winx + i, winy + j) != 1) {
                    win = false;
                    break;
                }
            }

        if (win && !isWinning) {
            isWinning = true;
            winTime = System.currentTimeMillis();
            stopGameTimer();
            long actualTime = getActualTime();
            String timeString = formatTime(actualTime);

            // new SoundEffect().playEffect("resources/sound/win.wav");

            if (currentUser != null && !currentUser.isGuest()) {
                currentUser.updateBestSteps(view.getSteps());
                currentUser.updateBestTime(actualTime);
                UserManager.saveUser(currentUser);
                userDataController.saveGame(true);
            }

            SwingUtilities.invokeLater(() -> {
                view.showVictoryMessage(view.getSteps(), timeString);
            });
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
        startGameTimer(300);

        // save game after restart
        if (currentUser != null && !currentUser.isGuest()) userDataController.saveGame(true);
    }

    public void startGameTimer(long timeLeft) {
        startTime = System.currentTimeMillis();
        GAME_DURATION = timeLeft * 1000;

        gameTimer = new Timer(1000, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            long remaining = GAME_DURATION - elapsed;

            if (remaining <= 0) {
                stopGameTimer();
                view.setTimeLabelString("00:00");
                view.timeoutDialog();
                return;
            }

            long minutes = remaining / 1000 / 60;
            long seconds = (remaining / 1000) % 60;
            String timeString = String.format("Time Left: %02d:%02d", minutes, seconds);
            view.setTimeLabelString(timeString);
        });

        gameTimer.start();
    }

    public void stopGameTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    public long getActualTime() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - startTime + 5 * 60 * 1000 - GAME_DURATION) / 1000;
    }

    public boolean useTool(int row, int col) {
        switch (currentTool) {
            case HAMMER:
                if (model.getId(row, col) == 4) {
                    model.removeSoldier(row, col);
                    view.removeBoxAt(row, col);
                    return true;
                }
                break;
            case OBSTACLE:
                if (model.getId(row, col) == 0) {
                    model.setObstacle(row, col);
                    view.addObstacleAt(row, col);
                    return true;
                }
                break;
        }
        return false;
    }

    public void selectTool(Tool tool) {
        this.currentTool = tool;
    }

    public void setCurrentTool(Tool tool) {
        this.currentTool = tool;
    }

    public Tool getCurrentTool() {
        return currentTool;
    }

    public UserDataController getUserDataController() { return userDataController; }
}