package controller;

import model.Direction;
import model.GameState;
import model.MapModel;
import model.User;
import view.game.BoxComponent;
import view.game.GamePanel;
import util.SaveLoadUtil;
import util.FileValidator;

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
    private User currentUser; // 当前登录用户

    public GameController(GamePanel view, MapModel model) {
        this.view = view;
        this.model = model;
        this.view.setController(this);
        new SelectController(view, model, this);
        initializeKeyBindings();
    }

    /**
     * Sets the current logged-in user
     * @param user the user object
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Saves the current game state
     * @return true if save was successful
     */
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

    /**
     * Loads the saved game state
     * @return true if load was successful
     */
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
            // Validate the save file first
            if (!FileValidator.validateSaveFile(saveFile)) {
                view.showErrorMessage("Save file is corrupted or invalid.");
                return false;
            }

            GameState state = SaveLoadUtil.loadGameState(filename);

            // Update model and view
            model.setMatrix(state.getBoardState());
            this.moveCount = state.getMoveCount();

            // Reset selection
            selectedRow = -1;
            selectedCol = -1;

            // Update view
            view.resetView();
            view.updateMoveCount(moveCount);
            view.clearSelection();

            view.showInfoMessage("Game loaded successfully!");
            return true;
        } catch (Exception e) {
            view.showErrorMessage("Failed to load game: " + e.getMessage());
            return false;
        }
    }

    /**
     * Auto-saves the game when exiting
     */
    public void autoSaveOnExit() {
        if (currentUser != null && !currentUser.isGuest()) {
            saveGame();
        }
    }

    /**
     * Initializes keyboard bindings for movement
     */
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

    /**
     * Selects a block at the specified position
     * @param row the row of the block
     * @param col the column of the block
     */
    public void selectBlock(int row, int col) {
        if (model.getId(row, col) != 0) { // Only select non-empty blocks
            selectedRow = row;
            selectedCol = col;
            view.highlightSelectedBox(row, col);
        } else {
            selectedRow = -1;
            selectedCol = -1;
            view.clearSelection();
        }
    }

    /**
     * Attempts to move a block in the specified direction
     * @param row the row of the block
     * @param col the column of the block
     * @param direction the direction to move
     * @return true if the move was successful
     */
    public boolean attemptMove(int row, int col, Direction direction) {
        if (doMove(row, col, direction)) {
            moveCount++;
            view.updateMoveCount(moveCount);
            return true;
        }
        return false;
    }

    public void restartGame() {
        model.resetMap();
        view.resetView();
        selectedRow = -1;
        selectedCol = -1;
        moveCount = 0;
        view.updateMoveCount(moveCount);
        view.clearSelection();
        System.out.println("Game restarted");
    }

    /**
     * Performs the actual movement logic
     * @param row the row of the block
     * @param col the column of the block
     * @param direction the direction to move
     * @return true if the move was successful
     */
    public boolean doMove(int row, int col, Direction direction) {
        int blockId = model.getId(row, col);
        if (blockId == 0) return false; // Can't move empty space

        // Calculate new position
        int nextRow = row + direction.getRow();
        int nextCol = col + direction.getCol();

        // Check boundaries
        if (!model.checkInHeightSize(nextRow) || !model.checkInWidthSize(nextCol)) {
            return false;
        }

        // Check if target position is empty
        if (model.getId(nextRow, nextCol) != 0) {
            return false;
        }

        // Perform the move
        model.getMatrix()[row][col] = 0;
        model.getMatrix()[nextRow][nextCol] = blockId;

        // Update the view
        BoxComponent box = view.getBoxAt(row, col);
        if (box != null) {
            box.setRow(nextRow);
            box.setCol(nextCol);
            box.setLocation(
                    box.getCol() * view.getGRID_SIZE() + 2,
                    box.getRow() * view.getGRID_SIZE() + 2
            );
            box.repaint();

            // Update selection to new position
            selectedRow = nextRow;
            selectedCol = nextCol;
            view.highlightSelectedBox(nextRow, nextCol);
        }

        // Check for win condition (Cao Cao block at exit)
        checkWinCondition();

        return true;
    }

    /**
     * Checks if the game has been won (Cao Cao block at exit)
     */
    private void checkWinCondition() {
        // 检查Cao Cao方块(2x2, ID=1)是否到达出口位置
        // 假设出口是底部中间位置(3,1)-(3,2)

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
            // Auto-save on victory if user is logged in
            if (currentUser != null && !currentUser.isGuest()) {
                saveGame();
            }
        }
    }

    // Getters for selected position
    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}