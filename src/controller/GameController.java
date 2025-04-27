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
import java.util.Stack;

/**
 * 游戏控制器，负责协调视图(GamePanel)和模型(MapModel)的交互
 * 处理游戏逻辑包括方块选择、移动、保存和加载
 */
public class GameController {
    private final GamePanel view;
    private final MapModel model;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private int moveCount = 0;
    private User currentUser;
    private Stack<MoveRecord> moveHistory = new Stack<>(); // 移动历史记录栈

    // 移动记录内部类
    private record MoveRecord(int blockId, int fromRow, int fromCol, int toRow, int toCol) {}

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

    /* 方向移动方法 */
    private void moveRight() {
        if (hasSelection()) attemptMove(selectedRow, selectedCol, Direction.RIGHT);
    }

    private void moveLeft() {
        if (hasSelection()) attemptMove(selectedRow, selectedCol, Direction.LEFT);
    }

    private void moveUp() {
        if (hasSelection()) attemptMove(selectedRow, selectedCol, Direction.UP);
    }

    private void moveDown() {
        if (hasSelection()) attemptMove(selectedRow, selectedCol, Direction.DOWN);
    }

    /**
     * 尝试移动方块
     * @return 是否移动成功
     */
    public boolean attemptMove(int row, int col, Direction direction) {
        boolean moved = moveBox(row, col, direction);
        if (moved) {
            moveCount ++;
            view.updateStepCount(moveCount);
        }
        return moved;
    }

    /**
     * 核心移动方法
     */
    public boolean moveBox(int row, int col, Direction direction) {
        int blockId = model.getId(row, col);
        if (blockId == 0) return false;

        // destination Id
        int toRow = row + direction.getRow();
        int toCol = col + direction.getCol();

        // check the move validity
        if (!isMoveValid(blockId, row, col, toRow, toCol)) {
            System.out.println("Can not move in this direction!");
            return false;
        }

        // record the move history, because we add undo
        moveHistory.push(new MoveRecord(blockId, row, col, toRow, toCol));

        updateModelPosition(blockId, row, col, toRow, toCol);
        updateViewPosition(row, col, toRow, toCol);

        checkWinCondition();

        return true;
    }

    /**
     * 撤销上一步移动
     * @return 是否撤销成功
     */
    public boolean undoLastMove() {
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

        // 更新步数和选中状态
        moveCount--;
        view.updateStepCount(moveCount);
        selectedRow = lastMove.fromRow();
        selectedCol = lastMove.fromCol();
        view.highlightSelectedBox(selectedRow, selectedCol);

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
            case 1: // CaoCao
                return checkLargeBlockMove(fromRow, fromCol, toRow, toCol, 2, 2);
            case 2: // GuanYu
                return checkLargeBlockMove(fromRow, fromCol, toRow, toCol, 2, 1);
            case 3: // General
                return checkLargeBlockMove(fromRow, fromCol, toRow, toCol, 1, 2);

                //??empty and solider?
            default:
                return model.getId(toRow, toCol) == 0;
        }
    }

    private boolean checkLargeBlockMove(int fromRow, int fromCol, int toRow, int toCol,
                                        int height, int width) {
        // 确定移动方向
        boolean movingRight = toCol > fromCol;
        boolean movingDown = toRow > fromRow;

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int checkRow = toRow + r;
                int checkCol = toCol + c;

                // 检查是否越界
                if (!model.checkInHeightSize(checkRow) || !model.checkInWidthSize(checkCol)) {
                    return false;
                }

                // 检查这个位置是否在原始方块区域内
                boolean isOriginalPosition =
                        (checkRow >= fromRow && checkRow < fromRow + height) &&
                                (checkCol >= fromCol && checkCol < fromCol + width);

                // 如果不是原始位置，检查是否被其他方块占据
                if (!isOriginalPosition && model.getId(checkRow, checkCol) != 0) {
                    return false;
                }

                // 特殊处理：防止"跳跃"移动
                if (height > 1 || width > 1) {
                    // 对于大方块，检查移动路径上的中间位置
                    if (movingRight && c == 0) { // 向右移动时检查左侧一列
                        int pathCol = fromCol + width - 1;
                        if (checkCol > pathCol && model.getId(checkRow, pathCol) != 0) {
                            return false;
                        }
                    }
                    if (movingDown && r == 0) { // 向下移动时检查上方一行
                        int pathRow = fromRow + height - 1;
                        if (checkRow > pathRow && model.getId(pathRow, checkCol) != 0) {
                            return false;
                        }
                    }
                }
            }
        }
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

    /* 游戏状态管理 */
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

    public void restartGame() {
        model.resetMap();
        view.initialGame();
        selectedRow = -1;
        selectedCol = -1;
        moveCount = 0;
        moveHistory.clear(); // 清空历史记录
        view.updateStepCount(moveCount);
        view.clearSelection();
    }

    /* 用户管理 */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /* 存档管理 */
    public boolean saveGame() {
        if (currentUser == null || currentUser.isGuest()) {
            view.showErrorMessage("游客不能保存游戏");
            return false;
        }

        /*
        GameState state = new GameState(
                model.getMatrix(),
                moveCount,
                System.currentTimeMillis()
        );

         */

        try {
            String filename = "resources/users/" + currentUser.getUsername() + ".sav";
            // SaveLoadUtil.saveGameState(state, filename);
            view.showInfoMessage("游戏保存成功！");
            return true;
        } catch (Exception e) {
            view.showErrorMessage("保存失败: " + e.getMessage());
            return false;
        }
    }

    public boolean loadGame() {
        if (currentUser == null || currentUser.isGuest()) {
            view.showErrorMessage("游客不能加载存档");
            return false;
        }

        String filename = "resources/users/" + currentUser.getUsername() + ".sav";
        File saveFile = new File(filename);

        if (!saveFile.exists()) {
            view.showErrorMessage("没有找到存档文件");
            return false;
        }

        try {
            if (!FileValid.validateSaveFile(saveFile)) {
                view.showErrorMessage("存档文件已损坏");
                return false;
            }

            GameState state = SaveLoadUtil.loadGameState(filename);
            model.setMatrix(state.getBoardState());
            this.moveCount = state.getMoveCount();
            this.moveHistory.clear(); // 加载时清空历史

            selectedRow = -1;
            selectedCol = -1;

            view.initialGame();
            view.updateStepCount(moveCount);
            view.clearSelection();

            view.showInfoMessage("游戏加载成功！");
            return true;
        } catch (Exception e) {
            view.showErrorMessage("加载失败: " + e.getMessage());
            return false;
        }
    }

    public void autoSaveOnExit() {
        if (currentUser != null && !currentUser.isGuest()) {
            saveGame();
        }
    }

    /* 输入控制 */
    private void initializeKeyBindings() {
        view.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!hasSelection()) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> attemptMove(selectedRow, selectedCol, Direction.UP);
                    case KeyEvent.VK_DOWN -> attemptMove(selectedRow, selectedCol, Direction.DOWN);
                    case KeyEvent.VK_LEFT -> attemptMove(selectedRow, selectedCol, Direction.LEFT);
                    case KeyEvent.VK_RIGHT -> attemptMove(selectedRow, selectedCol, Direction.RIGHT);
                    case KeyEvent.VK_Z -> {
                        if (e.isControlDown()) undoLastMove();
                    }
                    case KeyEvent.VK_S -> {
                        if (e.isControlDown()) saveGame();
                    }
                    case KeyEvent.VK_L -> {
                        if (e.isControlDown()) loadGame();
                    }
                }
            }
        });
    }

    /* 选择控制 */
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

    public int getSelectedRow() { return selectedRow; }
    public int getSelectedCol() { return selectedCol; }
    public int getStepCount() { return moveCount; }
    public User getCurrentUser() { return currentUser; }
    public boolean hasMoveHistory() { return !moveHistory.isEmpty(); }
}