package view.game;

import controller.GameController;
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
    private JButton backToMenuBtn;
    private JButton saveBtn;
    private JLabel stepLabel;
    private GamePanel gamePanel;
    private LoginFrame loginFrame;
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
        // step counter
        this.stepLabel = FrameUtil.createJLabel(this, "Steps: 0",
                new Font("serif", Font.BOLD, 22),
                new Point(gamePanel.getWidth() + 80, 70), 180, 50);
        gamePanel.setStepLabel(stepLabel);

        // restart game
        this.restartBtn = FrameUtil.createButton(this, "Restart",
                new Point(gamePanel.getWidth() + 80, 150), 120, 40);
        this.restartBtn.addActionListener(e -> {
            startNewGame();
            gamePanel.requestFocusInWindow(); // enable key listener
        });

        // save game (disabled for guests)
        this.saveBtn = FrameUtil.createButton(this, "Save Game",
                new Point(gamePanel.getWidth() + 80, 210), 120, 40);
        this.saveBtn.addActionListener(e -> {
            if (controller.saveGame()) {
                JOptionPane.showMessageDialog(this,
                        "Game saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            gamePanel.requestFocusInWindow();
        });

        // back to menu button
        // save game before back??
        this.backToMenuBtn = FrameUtil.createButton(this, "Back to Menu",
                new Point(gamePanel.getWidth() + 80, 270), 120, 40);
        this.backToMenuBtn.addActionListener(e -> {
            this.setVisible(false);
            new StartMenuFrame(this, isGuest).setVisible(true);
        });
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

    public LoginFrame getLoginFrame() {
        return loginFrame;
    }

    public void setLoginFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
    }

    public GameController getController() {
        return controller;
    }
}