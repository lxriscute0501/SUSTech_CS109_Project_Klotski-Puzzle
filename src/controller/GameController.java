package controller;

import model.Direction;
import model.GameState;
import model.MapModel;
import model.User;
import view.game.BoxComponent;
import view.game.GamePanel;

import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * It is a bridge to combine GamePanel(view) and MapMatrix(model) in one game.
 * You can design several methods about the game logic in this class.
 */
public class GameController {
    private final GamePanel view;
    private final MapModel model;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private int stepCount = 0;
    private User currentUser;
    private Stack<MoveRecord> moveHistory = new Stack<>();

    private record MoveRecord(int blockId, int fromRow, int fromCol, int toRow, int toCol) {}

    public GameController(GamePanel view, MapModel model, User user) {
        this.view = view;
        this.model = model;
        this.currentUser = user;
        this.view.setController(this);
    }


    public boolean moveBox(int row, int col, Direction direction) {
        int blockId = model.getId(row, col);
        if (blockId == 0) return false;

        // Calculate destination
        int toRow = row + direction.getRow();
        int toCol = col + direction.getCol();

        // Validate move
        if (!isMoveValid(blockId, row, col, toRow, toCol)) {
            return false;  // Message is now handled by the caller if needed
        }

        // Record the move history
        moveHistory.push(new MoveRecord(blockId, row, col, toRow, toCol));

        // Update positions
        updateModelPosition(blockId, row, col, toRow, toCol);
        updateViewPosition(row, col, toRow, toCol);

        checkWinCondition();
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
        stepCount --;
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

    private boolean hasSelection() {
        return selectedRow != -1 && selectedCol != -1;
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
            view.showVictoryMessage(stepCount);
            if (currentUser != null && !currentUser.isGuest()) {
                saveGame();
            }
        }
    }

    public void restartGame() {
        model.resetMap();
        view.initializeGame();
        selectedRow = -1;
        selectedCol = -1;
        stepCount = 0;
        moveHistory.clear();
        view.updateStepCount(stepCount);
        view.clearSelection();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void saveGame() {
        if (currentUser.isGuest()) {
            view.showErrorMessage("Guest can not save game!");
            return;
        }

        int[][] saveMap = model.getMatrix();
        List<String> gameData = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        gameData.add(String.valueOf(view.getSteps()));

        for (int[] line : saveMap) {
            sb.setLength(0);
            for (int value : line) {
                sb.append(value).append(" ");
            }
            gameData.add(sb.toString().trim());
        }

        String filePath = "data/" + currentUser.getUsername();
        File dir = new File(filePath);
        dir.mkdirs();

        try {
            Files.write(Path.of(filePath + "/data.txt"), gameData);
            view.showInfoMessage("Game saved successfully!");
        } catch (Exception e) {
            view.showErrorMessage("Game saved failed: " + e.getMessage());
        }
    }

    public void loadGame() {
        if (currentUser.isGuest()) {
            view.showErrorMessage("Guest can not load game!");
            return;
        }

        String filePath = "data/" + currentUser.getUsername() + "/data.txt";
        File saveFile = new File(filePath);

        if (!saveFile.exists()) {
            view.showErrorMessage("No saved game found for this user!");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));

            if (lines.isEmpty()) {
                view.showErrorMessage("Save file is empty!");
                return;
            }

            int savedStepCount = 0;
            try {
                savedStepCount = Integer.parseInt(lines.get(0));
            } catch (NumberFormatException e) {
                view.showErrorMessage("Invalid step count in save file!");
                return;
            }

            int[][] loadedMap = new int[model.getHeight()][model.getWidth()];
            for (int row = 1; row < lines.size() && row-1 < model.getHeight(); row++) {
                String[] values = lines.get(row).trim().split(" ");
                for (int col = 0; col < values.length && col < model.getWidth(); col++) {
                    try {
                        loadedMap[row-1][col] = Integer.parseInt(values[col]);
                    } catch (NumberFormatException e) {
                        loadedMap[row-1][col] = 0;
                    }
                }
            }

            model.setMatrix(loadedMap);
            view.setSteps(savedStepCount);
            moveHistory.clear();
            selectedRow = -1;
            selectedCol = -1;

            view.rebuildGameView(loadedMap);
            view.updateStepCount(view.getSteps());
            view.clearSelection();
            view.showInfoMessage("Game loaded successfully!");
        } catch (Exception e) {
            view.showErrorMessage("Failed to load game: " + e.getMessage());
        }
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
}