package view.game;

import controller.GameController;
import model.Direction;
import model.MapModel;
import model.User;
import view.FrameUtil;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private GameController controller;

    private JButton restartBtn;
    private JButton saveBtn;
    private JButton upBtn;
    private JButton downBtn;
    private JButton leftBtn;
    private JButton rightBtn;
    private JButton undoBtn;

    private JLabel usernameLabel;
    private JLabel bestStepCount;
    private JLabel winConditionLabel;
    private JLabel stepLabel;
    private GamePanel gamePanel;
    private User currentUser;
    private boolean isGuest;

    public GameFrame(int width, int height, MapModel mapModel, boolean isGuest, User user) {
        this.setTitle("Klotski Puzzle");
        this.setLayout(null);
        this.setSize(width, height);
        this.currentUser = user;
        this.isGuest = isGuest;

        // create the main game panel
        gamePanel = new GamePanel(mapModel);
        gamePanel.setLocation(30, height / 2 - gamePanel.getHeight() / 2);
        this.add(gamePanel);

        this.controller = new GameController(gamePanel, mapModel, user);

        initializeUIComponents();

        this.setLocationRelativeTo(null);
        if (isGuest) this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        else {
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    try {
                        controller.saveGame(true);
                        System.exit(0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.exit(0);
                    }
                }
            });
        }
    }

    private void initializeUIComponents() {

        this.usernameLabel = FrameUtil.createJLabel(this, "Username: " + currentUser.getUsername(),
                new Font("serif", Font.BOLD, 22),
                new Point(gamePanel.getWidth() + 80, 20), 300, 50);


        String bestMoveString;
        if (currentUser.getBestMoveCount() < Integer.MAX_VALUE)
            bestMoveString = String.format(" " + currentUser.getBestMoveCount());
        else bestMoveString = " Null";

        this.bestStepCount = FrameUtil.createJLabel(this, "Best Record:" + bestMoveString,
                new Font("serif", Font.BOLD, 22),
                new Point(gamePanel.getWidth() + 80, 60), 300, 50);


        this.winConditionLabel = FrameUtil.createJLabel(this, "Exit: " + controller.exitLocation(),
                new Font("serif", Font.BOLD, 22),
                new Point(gamePanel.getWidth() + 80, 100), 400, 50);

        this.stepLabel = FrameUtil.createJLabel(this, "Steps: 0",
                new Font("serif", Font.BOLD, 25),
                new Point(gamePanel.getWidth() + 80, 150), 300, 50);
        gamePanel.setStepLabel(stepLabel);

        // restart game button
        this.restartBtn = FrameUtil.createButton(this, "Restart",
                new Point(gamePanel.getWidth() + 80, 200), 120, 40);
        this.restartBtn.addActionListener(e -> {
            startNewGame();
            gamePanel.requestFocusInWindow();
        });

        // save game button (disabled for guests)
        this.saveBtn = FrameUtil.createButton(this, "Save Game",
                new Point(gamePanel.getWidth() + 80, 250), 120, 40);

        this.saveBtn.addActionListener(e -> {
            controller.saveGame(false);
        });

        // forbid guest to click save button
        if (isGuest) {
            saveBtn.setEnabled(false);
            saveBtn.setToolTipText("Guest cannot save games.");
        }

        // undo button
        this.undoBtn = FrameUtil.createButton(this, "Undo",
                new Point(gamePanel.getWidth() + 80, 320), 120, 40);
        this.undoBtn.addActionListener(e -> {
            if (gamePanel.undoLastMove()) {
                gamePanel.requestFocusInWindow();
            }
        });

        gamePanel.requestFocusInWindow();

        // direction button
        this.upBtn = FrameUtil.createButton(this, "⬆",
                new Point(gamePanel.getWidth() + 80, 400), 40, 40);
        this.downBtn = FrameUtil.createButton(this, "⬇",
                new Point(gamePanel.getWidth() + 80, 450), 40, 40);
        this.leftBtn = FrameUtil.createButton(this, "⬅",
                new Point(gamePanel.getWidth() + 40, 425), 40, 40);
        this.rightBtn = FrameUtil.createButton(this, "➡",
                new Point(gamePanel.getWidth() + 120, 425), 40, 40);

        // set direction button action
        upBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.UP));
        downBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.DOWN));
        leftBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.LEFT));
        rightBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.RIGHT));

    }

    public void startNewGame() {
        controller.restartGame();
        gamePanel.initializeGame();
        gamePanel.requestFocusInWindow();
    }

    public void loadHistoryGame() {
        controller.loadGame();
        gamePanel.requestFocusInWindow();
    }

}