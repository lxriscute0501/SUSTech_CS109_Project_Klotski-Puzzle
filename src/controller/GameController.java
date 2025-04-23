package controller;

import model.Direction;
import model.MapModel;
import view.game.BoxComponent;
import view.game.GamePanel;

/**
 * It is a bridge to combine GamePanel(view) and MapMatrix(model) in one game.
 * You can design several methods about the game logic in this class.
 */
public class GameController {
    private final GamePanel view;
    private final MapModel model;

    public GameController(GamePanel view, MapModel model) {
        this.view = view;
        this.model = model;
        view.setController(this);
    }

    public void restartGame() {
        System.out.println("Do restart game here");

    }

    public boolean doMove(int row, int col, Direction direction) {

        BoxComponent box = view.getSelectedBox();
        if (box == null) return false;

        int oldRow = box.getRow();
        int oldCol = box.getCol();
        int newRow = oldRow + direction.getRow();
        int newCol = oldCol + direction.getCol();

        int widthInGrid = box.getWidth() / view.getGRID_SIZE();
        int heightInGrid = box.getHeight() / view.getGRID_SIZE();

        for (int i = 0; i < heightInGrid; i++) {
            for (int j = 0; j < widthInGrid; j++) {
                int checkRow = newRow + i;
                int checkCol = newCol + j;

                if (!model.checkInHeightSize(checkRow) || !model.checkInWidthSize(checkCol)) {
                    return false; // Out of bounds
                }

                if (model.getId(checkRow, checkCol) != 0 &&
                        (checkRow < oldRow || checkRow >= oldRow + heightInGrid ||
                                checkCol < oldCol || checkCol >= oldCol + widthInGrid)) {
                    return false;
                }
            }
        }

        for (int i = 0; i < heightInGrid; i++) {
            for (int j = 0; j < widthInGrid; j++) {
                model.setId(oldRow + i, oldCol + j, 0);
            }
        }

        for (int i = 0; i < heightInGrid; i++) {
            for (int j = 0; j < widthInGrid; j++) {
                model.setId(newRow + i, newCol + j, 1); // or use a box ID if you track them
            }
        }

        for (int i = 0; i < heightInGrid; i++) {
            for (int j = 0; j < widthInGrid; j++) {
                model.setId(newRow + i, newCol + j, 1); // or use a unique ID per block
            }
        }

        box.setRow(newRow);
        box.setCol(newCol);
        box.setLocation(newCol * view.getGRID_SIZE() + 2, newRow * view.getGRID_SIZE() + 2);
        box.repaint();

        if (box.getRow() == model.getExitRow() && box.getCol() == model.getExitCol()) {
            System.out.println("Congratulations! You have moved Cao Cao to the exit!");

            return true;
        }
        return true;
    }

    //todo: add other methods such as loadGame, saveGame...

}
