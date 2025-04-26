package controller;

import model.Direction;
import model.MapModel;
import view.game.GamePanel;
import java.awt.*;

/**
 * control block selecting && moving
 */
public class SelectController {
    private final GamePanel view;
    private final MapModel model;
    private final GameController gameController;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private Point pressedPoint;

    public SelectController(GamePanel view, MapModel model, GameController gameController) {
        this.view = view;
        this.model = model;
        this.gameController = gameController;
        setupControlCallbacks();
    }

    private void setupControlCallbacks() {
        // 设置键盘控制回调
        view.setMoveCallbacks(
                this::moveRight,
                this::moveLeft,
                this::moveUp,
                this::moveDown
        );

        // 设置鼠标控制回调
        view.setMouseCallbacks(
                this::handleLeftClick,
                this::handleRightClick,
                this::handleMousePressed,
                this::handleMouseReleased
        );
    }

    // keyboard control
    private void moveRight() {
        attemptMoveIfSelected(Direction.RIGHT);
    }

    private void moveLeft() {
        attemptMoveIfSelected(Direction.LEFT);
    }

    private void moveUp() {
        attemptMoveIfSelected(Direction.UP);
    }

    private void moveDown() {
        attemptMoveIfSelected(Direction.DOWN);
    }

    private void attemptMoveIfSelected(Direction direction) {
        if (isBlockSelected()) {
            gameController.attemptMove(selectedRow, selectedCol, direction);
            updateSelectionAfterMove(direction);
        }
    }

    // mouse control
    private void handleLeftClick(Point point) {
        int[] gridPos = convertToGridPosition(point);
        if (gridPos == null) return;

        int row = gridPos[0], col = gridPos[1];

        // 点击已选方块则取消选择
        if (row == selectedRow && col == selectedCol) {
            clearSelection();
            return;
        }

        // 点击新方块则选择它
        if (model.getId(row, col) != 0) {
            selectBlock(row, col);
        } else {
            clearSelection();
        }
    }

    private void handleRightClick(Point point) {
        clearSelection(); // 右键取消选择
    }

    private void handleMousePressed(Point point) {
        pressedPoint = point;
    }

    private void handleMouseReleased(Point point) {
        if (pressedPoint != null && isDragOperation(pressedPoint, point)) {
            handleDragMovement(pressedPoint, point);
        }
        pressedPoint = null;
    }

    // 辅助方法
    private int[] convertToGridPosition(Point point) {
        int gridSize = view.getGRID_SIZE();
        int col = point.x / gridSize;
        int row = point.y / gridSize;

        if (!model.checkInHeightSize(row) || !model.checkInWidthSize(col)) {
            return null;
        }
        return new int[]{row, col};
    }

    private boolean isDragOperation(Point start, Point end) {
        return start.distance(end) > view.getGRID_SIZE() / 2;
    }

    private void handleDragMovement(Point start, Point end) {
        if (!isBlockSelected()) return;

        int[] startGrid = convertToGridPosition(start);
        if (startGrid == null || startGrid[0] != selectedRow || startGrid[1] != selectedCol) {
            return;
        }

        int dx = end.x - start.x;
        int dy = end.y - start.y;
        Direction direction = getDragDirection(dx, dy);

        if (direction != null) {
            gameController.attemptMove(selectedRow, selectedCol, direction);
            updateSelectionAfterMove(direction);
        }
    }

    private Direction getDragDirection(int dx, int dy) {
        int gridSize = view.getGRID_SIZE();
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > gridSize/2 ? Direction.RIGHT : Direction.LEFT;
        } else {
            return dy > gridSize/2 ? Direction.DOWN : Direction.UP;
        }
    }

    private void updateSelectionAfterMove(Direction direction) {
        selectedRow += direction.getRow();
        selectedCol += direction.getCol();
        view.highlightSelectedBox(selectedRow, selectedCol);
    }

    private void selectBlock(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        view.highlightSelectedBox(row, col);
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        view.clearSelection();
    }

    private boolean isBlockSelected() {
        return selectedRow != -1 && selectedCol != -1;
    }
}