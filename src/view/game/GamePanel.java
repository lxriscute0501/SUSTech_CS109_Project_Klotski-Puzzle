package view.game;

import controller.GameController;
import controller.UserDataController;
import model.Direction;
import model.MapModel;
import model.SoundEffect;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * It is the subclass of ListenerPanel, so that it should implement those four methods: move left, up, down ,right.
 * The class contains a grids, which is the corresponding GUI view of the matrix variable in MapMatrix.
 */

public class GamePanel extends ListenerPanel {
    private List<BoxComponent> boxes;
    private MapModel model;
    private GameController controller;
    private UserDataController userData;
    private BoxComponent selectedBox;
    private List<ActionListener> stepListeners = new ArrayList<>();

    private JLabel timeLabel;
    private JLabel stepLabel;
    private int steps;

    private final int GRID_SIZE = 50;

    private Image image1;
    private Image image2;
    private Image image3_zy, image3_hz, image3_zf, image3_mc;
    private Image image4;

    public GamePanel(MapModel model) {
        boxes = new ArrayList<>();
        this.setVisible(true);
        this.setFocusable(true);
        this.setLayout(null);
        this.setSize(model.getWidth() * GRID_SIZE + 4, model.getHeight() * GRID_SIZE + 4);

        this.model = model;
        this.selectedBox = null;

        addBlockPicture();
        initializeGame();

        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private void addBlockPicture() {
        this.image1 = new ImageIcon(getClass().getResource("/images/blocks/CaoCao.png")).getImage();
        this.image2 = new ImageIcon(getClass().getResource("/images/blocks/GuanYu.png")).getImage();
        this.image3_hz = new ImageIcon(getClass().getResource("/images/blocks/HuangZhong.png")).getImage();
        this.image3_mc = new ImageIcon(getClass().getResource("/images/blocks/MaChao.png")).getImage();
        this.image3_zf = new ImageIcon(getClass().getResource("/images/blocks/ZhangFei.png")).getImage();
        this.image3_zy = new ImageIcon(getClass().getResource("/images/blocks/ZhaoYun.png")).getImage();
        this.image4 = new ImageIcon(getClass().getResource("/images/blocks/Soldier.png")).getImage();
    }

    public void initializeGame() {
        this.steps = 0;
        this.removeAll();
        boxes.clear();

        int[][] map = new int[model.getHeight()][model.getWidth()];
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[0].length; j++)
                map[i][j] = model.getId(i, j);

        int general = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                BoxComponent box = null;
                if (map[i][j] == 1) {
                    box = new BoxComponent(this.image1, i, j, Color.GREEN);
                    box.setSize(GRID_SIZE * 2, GRID_SIZE * 2);
                    map[i][j] = 0; map[i + 1][j] = 0; map[i][j + 1] = 0; map[i + 1][j + 1] = 0;
                } else if (map[i][j] == 2) {
                    box = new BoxComponent(this.image2, i, j, Color.PINK);
                    box.setSize(GRID_SIZE, GRID_SIZE * 2);
                    map[i][j] = 0; map[i + 1][j] = 0;
                } else if (map[i][j] == 3) {
                    switch (general) {
                        case 0: box = new BoxComponent(this.image3_hz, i, j, Color.BLUE); break;
                        case 1: box = new BoxComponent(this.image3_mc, i, j, Color.BLUE); break;
                        case 2: box = new BoxComponent(this.image3_zf, i, j, Color.BLUE); break;
                        case 3: box = new BoxComponent(this.image3_zy, i, j, Color.BLUE); break;
                        default: box = new BoxComponent(this.image3_zy, i, j, Color.BLUE);
                    }
                    box.setSize(GRID_SIZE * 2, GRID_SIZE);
                    map[i][j] = 0; map[i][j + 1] = 0;
                    general ++;
                } else if (map[i][j] == 4) {
                    box = new BoxComponent(this.image4, i, j, Color.ORANGE);
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

    public void moveSelectedBox(Direction direction) {
        if (selectedBox == null) {
            showInfoMessage("Please choose one box first.");
            return;
        }

        if (controller != null) {
            boolean moved = controller.moveBox(
                    selectedBox.getRow(),
                    selectedBox.getCol(),
                    direction
            );

            // update box location
            if (moved) {
                new SoundEffect().playEffect("resources/sound/click.wav");
                selectedBox.setLocation(
                        selectedBox.getCol() * GRID_SIZE + 2,
                        selectedBox.getRow() * GRID_SIZE + 2
                );

                System.out.println(direction);
                afterMove();
                repaint();
                controller.checkWinCondition();
            }
        }
    }

    public void afterMove() {
        this.steps ++;
        updateStepLabel();
        for (ActionListener listener : stepListeners) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "step"));
        }
    }

    private void updateBoxPositions() {
        for (BoxComponent box : boxes) {
            box.setLocation(box.getCol() * GRID_SIZE + 2, box.getRow() * GRID_SIZE + 2);
        }
        this.repaint();
    }

    public void rebuildGameView(int[][] mapData) {
        this.removeAll();
        boxes.clear();

        int general = 0;
        for (int i = 0; i < mapData.length; i++) {
            for (int j = 0; j < mapData[0].length; j++) {
                BoxComponent box = null;
                if (mapData[i][j] == 1) {
                    box = new BoxComponent(this.image1, i, j, Color.GREEN);
                    box.setSize(GRID_SIZE * 2, GRID_SIZE * 2);
                    mapData[i][j] = 0; mapData[i + 1][j] = 0; mapData[i][j + 1] = 0; mapData[i + 1][j + 1] = 0;
                } else if (mapData[i][j] == 2) {
                    box = new BoxComponent(this.image2, i, j, Color.PINK);
                    box.setSize(GRID_SIZE, GRID_SIZE * 2);
                    mapData[i][j] = 0; mapData[i + 1][j] = 0;
                } else if (mapData[i][j] == 3) {
                    switch (general) {
                        case 0: box = new BoxComponent(this.image3_hz, i, j, Color.BLUE); break;
                        case 1: box = new BoxComponent(this.image3_mc, i, j, Color.BLUE); break;
                        case 2: box = new BoxComponent(this.image3_zf, i, j, Color.BLUE); break;
                        case 3: box = new BoxComponent(this.image3_zy, i, j, Color.BLUE); break;
                        default: box = new BoxComponent(this.image3_zy, i, j, Color.BLUE);
                    }
                    box.setSize(GRID_SIZE * 2, GRID_SIZE);
                    mapData[i][j] = 0; mapData[i][j + 1] = 0;
                    general ++;
                } else if (mapData[i][j] == 4) {
                    box = new BoxComponent(this.image4, i, j, Color.ORANGE);
                    box.setSize(GRID_SIZE, GRID_SIZE);
                    mapData[i][j] = 0;
                }

                if (box != null) {
                    box.setLocation(j * GRID_SIZE + 2, i * GRID_SIZE + 2);
                    boxes.add(box);
                    this.add(box);
                }
            }
        }
        this.revalidate();
        this.repaint();
    }

    public boolean undoLastMove() {
        if (controller == null) {
            showInfoMessage("Controller not initialized.");
            return false;
        }

        boolean success = controller.undoMove();

        if (success) {
            steps--;
            updateStepLabel();
            updateBoxPositions();
            clearSelection();
            selectedBox = null;
        }

        return success;
    }

    public void setTimeLabel(JLabel timeLabel) {
        this.timeLabel = timeLabel;
    }

    public void setTimeLabelString(String label) {
        this.timeLabel.setText(label);
    }

    public void setStepLabel(JLabel stepLabel) {
        this.stepLabel = stepLabel;
        updateStepLabel();
    }

    private void updateStepLabel() {
        if (stepLabel != null) {
            stepLabel.setText(String.format("Steps: %d", this.steps));
        }
    }

    public void setSteps(int steps) {
        this.steps = steps;
        updateStepLabel();
    }

    public int getSteps() { return steps; }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void setUserData(UserDataController userData) {
        this.userData = userData;
    }

    public int getGRID_SIZE() { return GRID_SIZE; }

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

    public void addObstacleAt(int row, int col) {
        ObstacleComponent obstacle = new ObstacleComponent(row, col);
        obstacle.setSize(GRID_SIZE, GRID_SIZE);
        obstacle.setLocation(col * GRID_SIZE + 2, row * GRID_SIZE + 2);
        add(obstacle);
        repaint();
    }

    public void removeBoxAt(int row, int col) {
        Component comp = getBoxAt(row, col);
        if (comp != null) {
            remove(comp);
            repaint();
        }
    }

    public void showVictoryMessage(int stepCount, String timeUsed) {
        String message = String.format("Victory! Congratulations!\n" + "Steps: %d\n" + "Time used: %s", stepCount, timeUsed);
        JOptionPane.showMessageDialog(this, message, "Victory!", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
        this.setBorder(border);
    }

    // Implement the abstract methods from ListenerPanel
    @Override
    public void doMoveRight() { moveSelectedBox(Direction.RIGHT); }

    @Override
    public void doMoveLeft() { moveSelectedBox(Direction.LEFT); }

    @Override
    public void doMoveUp() { moveSelectedBox(Direction.UP); }

    @Override
    public void doMoveDown() { moveSelectedBox(Direction.DOWN); }

    @Override
    public void doLeftClick(Point point) {
        int col = point.x / GRID_SIZE;
        int row = point.y / GRID_SIZE;

        if (controller.getCurrentTool() != GameController.Tool.NONE) {
            if (controller.useTool(row, col)) {
                controller.setCurrentTool(GameController.Tool.NONE);
                return;
            }
        }

        Component component = this.getComponentAt(point);
        if (component instanceof BoxComponent clickedComponent) {
            if (selectedBox == null) {
                // no box selected, choose clicked box
                selectedBox = clickedComponent;
                selectedBox.setSelected(true);
            } else if (selectedBox != clickedComponent) {
                // different box selected, switch clicked box
                selectedBox.setSelected(false);
                clickedComponent.setSelected(true);
                selectedBox = clickedComponent;
            }
            // box has been selected, do nothing
        } else {
            // click the empty place, cancel selecting
            if (selectedBox != null) {
                selectedBox.setSelected(false);
                selectedBox = null;
            }
        }
        this.requestFocusInWindow();
    }

    @Override
    public void doRightClick(Point point) {
        Component component = this.getComponentAt(point);
        if (component instanceof BoxComponent clickedComponent) {
            // click the selected box, cancel it
            if (selectedBox != null && selectedBox == clickedComponent) {
                clickedComponent.setSelected(false);
                selectedBox = null;
            }
        }
    }
}