package view.game;

import controller.GameController;
import model.Direction;
import model.MapModel;
import view.FrameUtil;
import view.login.LoginFrame;
import view.login.StartMenuFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {
    private GameController controller;

    private JButton restartBtn;
    private JButton saveBtn;
    private JButton upBtn;
    private JButton downBtn;
    private JButton leftBtn;
    private JButton rightBtn;
    private JButton undoBtn;

    private JLabel stepLabel;
    private GamePanel gamePanel;
    private LoginFrame loginFrame;
    private StartMenuFrame startMenuFrame;
    private boolean isGuest = false;


    public GameFrame(int width, int height, MapModel mapModel) {
        this.setTitle("Klotski Puzzle");
        this.setLayout(null);
        this.setSize(width, height);

        // create the main game panel
        gamePanel = new GamePanel(mapModel);
        gamePanel.setLocation(30, height / 2 - gamePanel.getHeight() / 2);
        this.add(gamePanel);

        this.controller = new GameController(gamePanel, mapModel);

        initializeUIComponents();

        // set location and close operation
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initializeUIComponents() {

        // step counter label
        this.stepLabel = FrameUtil.createJLabel(this, "Steps: 0",
                new Font("serif", Font.BOLD, 22),
                new Point(gamePanel.getWidth() + 80, 70), 180, 50);
        gamePanel.setStepLabel(stepLabel);

        // restart game button
        this.restartBtn = FrameUtil.createButton(this, "Restart",
                new Point(gamePanel.getWidth() + 80, 150), 120, 40);
        this.restartBtn.addActionListener(e -> {
            startNewGame();
            gamePanel.requestFocusInWindow();
        });

        // save game button (disabled for guests)
        this.saveBtn = FrameUtil.createButton(this, "Save Game",
                new Point(gamePanel.getWidth() + 80, 210), 120, 40);
        this.saveBtn.addActionListener(e -> {
            if (controller.saveGame()) {
                JOptionPane.showMessageDialog(this,
                        "Game saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // undo button
        this.undoBtn = FrameUtil.createButton(this, "Undo",
                new Point(gamePanel.getWidth() + 80, 330), 120, 40);
        this.undoBtn.addActionListener(e -> {
            if (gamePanel.undoLastMove()) {
                gamePanel.requestFocusInWindow();
            }
        });
        // at the first step it can not use
        this.undoBtn.setEnabled(false);
        this.undoBtn.setToolTipText("undo the last move");
        gamePanel.requestFocusInWindow();

        // direction button
        this.upBtn = FrameUtil.createButton(this, "⬆️",
                new Point(gamePanel.getWidth() + 80, 330), 40, 40);
        this.downBtn = FrameUtil.createButton(this, "⬇️",
                new Point(gamePanel.getWidth() + 80, 380), 40, 40);
        this.leftBtn = FrameUtil.createButton(this, "⬅️",
                new Point(gamePanel.getWidth() + 40, 355), 40, 40);
        this.rightBtn = FrameUtil.createButton(this, "➡️️",
                new Point(gamePanel.getWidth() + 120, 355), 40, 40);

        // set direction button action
        upBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.UP));
        downBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.DOWN));
        leftBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.LEFT));
        rightBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.RIGHT));

    }

    public void setGuestMode(boolean isGuest) {
        this.isGuest = isGuest;
        this.saveBtn.setEnabled(!isGuest);
        if (isGuest) {
            this.saveBtn.setToolTipText("Guest users cannot save games");
        }
    }

    public void startNewGame() {
        controller.restartGame();
        gamePanel.initialGame();
        gamePanel.requestFocusInWindow();
    }

    public boolean loadGame() {
        if (isGuest) {
            JOptionPane.showMessageDialog(this,
                    "Guest users cannot load saved games",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        boolean success = controller.loadGame();
        if (success) gamePanel.updateStepCount(controller.getStepCount());
        return success;
    }

    public void updateUndoButtonState(boolean enabled) {
        undoBtn.setEnabled(enabled);
    }

    public GameController getController() {
        return controller;
    }
}