package view.game;

import controller.GameController;
import model.Direction;
import model.MapModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * It is the subclass of ListenerPanel, so that it should implement those four methods: move left, up, down ,right.
 * The class contains a grids, which is the corresponding GUI view of the matrix variable in MapMatrix.
 */
public class GamePanel extends ListenerPanel {
    private List<BoxComponent> boxes;
    private MapModel model;
    private GameController controller;
    private JLabel stepLabel;
    private int steps;
    private final int GRID_SIZE = 50;
    private BoxComponent selectedBox;
    private List<ActionListener> stepListeners = new ArrayList<>();

    // Callbacks for movement
    private Runnable doMoveRight;
    private Runnable doMoveLeft;
    private Runnable doMoveUp;
    private Runnable doMoveDown;

    // Mouse callbacks
    private Consumer<Point> doLeftClick;
    private Consumer<Point> doRightClick;
    private Consumer<Point> doMousePressed;
    private Consumer<Point> doMouseReleased;

    public GamePanel(MapModel model) {
        boxes = new ArrayList<>();
        this.setVisible(true);
        this.setFocusable(true);
        this.setLayout(null);
        this.setSize(model.getWidth() * GRID_SIZE + 4, model.getHeight() * GRID_SIZE + 4);
        this.model = model;
        this.selectedBox = null;
        initialGame();
    }

    // add step listener
    public void addStepListener(ActionListener listener) {
        stepListeners.add(listener);
    }

    // trigger step event
    private void triggerStepEvent() {
        for (ActionListener listener : stepListeners) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "step"));
        }
    }

    public void initialGame() {
        this.steps = 0;
        initializeBoxes();
    }

    private void initializeBoxes() {
        this.removeAll();
        boxes.clear();

        //copy a map
        int[][] map = new int[model.getHeight()][model.getWidth()];
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[0].length; j++)
                map[i][j] = model.getId(i, j);

        //build component
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                BoxComponent box = null;
                if (map[i][j] == 1) {
                    // CaoCao: 2 * 2
                    box = new BoxComponent(Color.GREEN, i, j);
                    box.setSize(GRID_SIZE * 2, GRID_SIZE * 2);
                    map[i][j] = 0;
                    map[i + 1][j] = 0;
                    map[i][j + 1] = 0;
                    map[i + 1][j + 1] = 0;
                } else if (map[i][j] == 2) {
                    // GuanYu: 2 * 1
                    box = new BoxComponent(Color.PINK, i, j);
                    box.setSize(GRID_SIZE, GRID_SIZE * 2);
                    map[i][j] = 0;
                    map[i + 1][j] = 0;
                } else if (map[i][j] == 3) {
                    // General: 1 * 2
                    box = new BoxComponent(Color.BLUE, i, j);
                    box.setSize(GRID_SIZE * 2, GRID_SIZE);
                    map[i][j] = 0;
                    map[i][j + 1] = 0;
                } else if (map[i][j] == 4) {
                    // Soldier: 1 * 1
                    box = new BoxComponent(Color.ORANGE, i, j);
                    box.setSize(GRID_SIZE, GRID_SIZE);
                    map[i][j] = 0;
                }

                if (box != null) {
                    box.setLocation(j * GRID_SIZE + 2, i * GRID_SIZE + 2);
                    boxes.add(box);
                    this.add(box);
                }
            }
        }
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
        this.setBorder(border);
    }

    @Override
    public void doMouseClick(Point point) {
        Component component = this.getComponentAt(point);
        if (component instanceof BoxComponent clickedComponent) {
            if (selectedBox == null) {
                selectedBox = clickedComponent;
                selectedBox.setSelected(true);
            } else if (selectedBox != clickedComponent) {
                selectedBox.setSelected(false);
                clickedComponent.setSelected(true);
                selectedBox = clickedComponent;
            } else {
                clickedComponent.setSelected(false);
                selectedBox = null;
            }
        }
    }

    @Override
    public void doMoveRight() {
        if (doMoveRight != null) {
            doMoveRight.run();
        }
    }

    @Override
    public void doMoveLeft() {
        if (doMoveLeft != null) {
            doMoveLeft.run();
        }
    }

    @Override
    public void doMoveUp() {
        if (doMoveUp != null) {
            doMoveUp.run();
        }
    }

    @Override
    public void doMoveDown() {
        if (doMoveDown != null) {
            doMoveDown.run();
        }
    }

    @Override
    public void doLeftClick(Point point) {

    }

    @Override
    public void doRightClick(Point point) {

    }

    @Override
    public void doMousePressed(Point point) {

    }

    @Override
    public void doMouseReleased(Point point) {

    }

    @Override
    public void doMouseEntered() {

    }

    @Override
    public void doMouseExited() {

    }

    @Override
    public void doMouseDragged(Point point) {

    }

    @Override
    public void doMouseMoved(Point point) {

    }

    public void afterMove() {
        this.steps ++;
        updateStepLabel();
        triggerStepEvent();
    }

    private void updateStepLabel() {
        if (stepLabel != null) {
            stepLabel.setText(String.format("Steps: %d", this.steps));
        }
    }

    public void setStepLabel(JLabel stepLabel) {
        this.stepLabel = stepLabel;
        updateStepLabel();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public BoxComponent getSelectedBox() {
        return selectedBox;
    }

    public int getGRID_SIZE() {
        return GRID_SIZE;
    }

    public void setSteps(int steps) {
        this.steps = steps;
        updateStepLabel();
    }

    public int getSteps() {
        return steps;
    }

    public void highlightSelectedBox(int row, int col) {
        clearSelection();
        BoxComponent box = getBoxAt(row, col);
        if (box != null) {
            box.setSelected(true);
            box.repaint();
        }
    }

    public void clearSelection() {
        for (Component comp : getComponents()) {
            if (comp instanceof BoxComponent) {
                ((BoxComponent) comp).setSelected(false);
                comp.repaint();
            }
        }
    }

    public void updateStepCount(int count) {
        this.steps = count;
        updateStepLabel();
    }

    public BoxComponent getBoxAt(int row, int col) {
        for (Component comp : getComponents()) {
            if (comp instanceof BoxComponent) {
                BoxComponent box = (BoxComponent) comp;
                if (box.getRow() == row && box.getCol() == col) {
                    return box;
                }
            }
        }
        return null;
    }

    public void showVictoryMessage(int moveCount) {
        JOptionPane.showMessageDialog(this,
                "Congratulations! You won in " + moveCount + " moves!",
                "Victory",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setMoveCallbacks(Runnable right, Runnable left, Runnable up, Runnable down) {
        this.doMoveRight = right;
        this.doMoveLeft = left;
        this.doMoveUp = up;
        this.doMoveDown = down;
    }

    public void setMouseCallbacks(
            Consumer<Point> leftClick,
            Consumer<Point> rightClick,
            Consumer<Point> mousePressed,
            Consumer<Point> mouseReleased
    ) {
        this.doLeftClick = leftClick;
        this.doRightClick = rightClick;
        this.doMousePressed = mousePressed;
        this.doMouseReleased = mouseReleased;
    }
}