package controller;

import model.Direction;
import model.GameState;
import model.MapModel;
import model.User;
import view.game.BoxComponent;
import view.game.GamePanel;
import util.SaveLoadUtil;
import util.FileValid;

import java.awt.event.KeyEvent;
import java.io.File;

/**
 * A bridge to combine GamePanel(view) and MapMatrix(model) in one game.
 * Handles game logic including block selection, movement, save and load.
 */
public class GameController {
    private final GamePanel view;
    private final MapModel model;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private int moveCount = 0;
    private User currentUser;

    public GameController(GamePanel view, MapModel model) {
        this.view = view;
        this.model = model;
        this.view.setController(this);
        setupCallbacks();
        initializeKeyBindings();
    }

    private void setupCallbacks() {
        view.setMoveCallbacks(
                this::moveRight,
                this::moveLeft,
                this::moveUp,
                this::moveDown
        );
    }

    private void moveRight() {
        if (selectedRow != -1 && selectedCol != -1) {
            attemptMove(selectedRow, selectedCol, Direction.RIGHT);
        }
    }

    private void moveLeft() {
        if (selectedRow != -1 && selectedCol != -1) {
            attemptMove(selectedRow, selectedCol, Direction.LEFT);
        }
    }

    private void moveUp() {
        if (selectedRow != -1 && selectedCol != -1) {
            attemptMove(selectedRow, selectedCol, Direction.UP);
        }
    }

    private void moveDown() {
        if (selectedRow != -1 && selectedCol != -1) {
            attemptMove(selectedRow, selectedCol, Direction.DOWN);
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean saveGame() {
        if (currentUser == null || currentUser.isGuest()) {
            view.showErrorMessage("Guest users cannot save games.");
            return false;
        }

        GameState state = new GameState(
                model.getMatrix(),
                moveCount,
                System.currentTimeMillis()
        );

        try {
            String filename = "resources/users/" + currentUser.getUsername() + ".sav";
            SaveLoadUtil.saveGameState(state, filename);
            view.showInfoMessage("Game saved successfully!");
            return true;
        } catch (Exception e) {
            view.showErrorMessage("Failed to save game: " + e.getMessage());
            return false;
        }
    }

    public boolean loadGame() {
        if (currentUser == null || currentUser.isGuest()) {
            view.showErrorMessage("Guest users cannot load saved games.");
            return false;
        }

        String filename = "resources/users/" + currentUser.getUsername() + ".sav";
        File saveFile = new File(filename);

        if (!saveFile.exists()) {
            view.showErrorMessage("No saved game found.");
            return false;
        }

        try {
            if (!FileValid.validateSaveFile(saveFile)) {
                view.showErrorMessage("Save file is corrupted or invalid.");
                return false;
            }

            GameState state = SaveLoadUtil.loadGameState(filename);
            model.setMatrix(state.getBoardState());
            this.moveCount = state.getMoveCount();

            selectedRow = -1;
            selectedCol = -1;

            view.initialGame();
            view.updateStepCount(moveCount);
            view.clearSelection();

            view.showInfoMessage("Game loaded successfully!");
            return true;
        } catch (Exception e) {
            view.showErrorMessage("Failed to load game: " + e.getMessage());
            return false;
        }
    }

    public void autoSaveOnExit() {
        if (currentUser != null && !currentUser.isGuest()) {
            saveGame();
        }
    }

    private void initializeKeyBindings() {
        view.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (selectedRow == -1 || selectedCol == -1) return;

                Direction direction = null;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        direction = Direction.UP;
                        break;
                    case KeyEvent.VK_DOWN:
                        direction = Direction.DOWN;
                        break;
                    case KeyEvent.VK_LEFT:
                        direction = Direction.LEFT;
                        break;
                    case KeyEvent.VK_RIGHT:
                        direction = Direction.RIGHT;
                        break;
                    case KeyEvent.VK_S:
                        if (e.isControlDown()) {
                            saveGame();
                        }
                        break;
                    case KeyEvent.VK_L:
                        if (e.isControlDown()) {
                            loadGame();
                        }
                        break;
                }

                if (direction != null) {
                    attemptMove(selectedRow, selectedCol, direction);
                }
            }
        });
    }

    public void selectBlock(int row, int col) {
        if (model.getId(row, col) != 0) {
            selectedRow = row;
            selectedCol = col;
            view.highlightSelectedBox(row, col);
        } else {
            selectedRow = -1;
            selectedCol = -1;
            view.clearSelection();
        }
    }

    public boolean attemptMove(int row, int col, Direction direction) {
        if (doMove(row, col, direction)) {
            moveCount++;
            view.updateStepCount(moveCount);
            return true;
        }
        return false;
    }

    public void restartGame() {
        model.resetMap();
        view.initialGame();
        selectedRow = -1;
        selectedCol = -1;
        moveCount = 0;
        view.updateStepCount(moveCount);
        view.clearSelection();
    }

    public boolean doMove(int row, int col, Direction direction) {
        int blockId = model.getId(row, col);
        if (blockId == 0) return false;

        int nextRow = row + direction.getRow();
        int nextCol = col + direction.getCol();

        if (!model.checkInHeightSize(nextRow) || !model.checkInWidthSize(nextCol)) {
            return false;
        }

        if (model.getId(nextRow, nextCol) != 0) {
            return false;
        }

        model.getMatrix()[row][col] = 0;
        model.getMatrix()[nextRow][nextCol] = blockId;

        BoxComponent box = view.getBoxAt(row, col);
        if (box != null) {
            box.setRow(nextRow);
            box.setCol(nextCol);
            box.setLocation(
                    box.getCol() * view.getGRID_SIZE() + 2,
                    box.getRow() * view.getGRID_SIZE() + 2
            );
            box.repaint();

            selectedRow = nextRow;
            selectedCol = nextCol;
            view.highlightSelectedBox(nextRow, nextCol);
        }

        checkWinCondition();
        return true;
    }

    private void checkWinCondition() {
        boolean caoAtExit = true;
        for (int r = 3; r <= 3; r++) {
            for (int c = 1; c <= 2; c++) {
                if (model.getId(r, c) != 1) {
                    caoAtExit = false;
                    break;
                }
            }
        }

        if (caoAtExit) {
            view.showVictoryMessage(moveCount);
            if (currentUser != null && !currentUser.isGuest()) {
                saveGame();
            }
        }
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public int getStepCount() {
        return moveCount;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}