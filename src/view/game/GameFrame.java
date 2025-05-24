package view.game;

import controller.GameController;
import controller.UserDataController;
import model.Direction;
import model.MapModel;
import controller.User;
import view.FrameUtil;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private GameController controller;
    private UserDataController userData;

    private JButton restartBtn;
    private JButton saveBtn;
    private JButton upBtn;
    private JButton downBtn;
    private JButton leftBtn;
    private JButton rightBtn;
    private JButton undoBtn;

    private GamePanel gamePanel;
    private User currentUser;
    private boolean isGuest;
    private String level;
    private MapModel map;
    private JButton hammerBtn;
    private JButton obstacleBtn;

    public GameFrame(int width, int height, MapModel mapModel, boolean isGuest, User user) {
        this.setTitle("Klotski Puzzle");
        this.setLayout(null);
        this.setSize(width, height);
        this.currentUser = user;
        this.isGuest = isGuest;
        this.map = mapModel;
        this.level = map.getLevel();

        // create the main game panel
        gamePanel = new GamePanel(mapModel);
        gamePanel.setLocation(30, height / 2 - gamePanel.getHeight() / 2);
        this.add(gamePanel);
        this.controller = new GameController(gamePanel, mapModel, user);
        this.userData = controller.getUserDataController();

        initializeUIComponents();
        addExitIndicator();

        this.setLocationRelativeTo(null);
        if (isGuest) this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        else {
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    try {
                        userData.saveGame(true);
                        System.exit(0);
                    } catch (Exception ex) {
                        System.exit(0);
                    }
                }
            });
        }
    }

    // draw exit image
    private void addExitIndicator() {
        JPanel exitPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setColor(new Color(144, 238, 144, 235));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(44, 103, 16));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "➡";
                int textWidth = fm.stringWidth(text);
                g2d.drawString(text, (getWidth() - textWidth) / 2, 60);
            }
        };

        exitPanel.setBounds(gamePanel.getX() + gamePanel.getWidth(),
                gamePanel.getY() + gamePanel.getHeight()/2 - 50, 30, 100);
        this.add(exitPanel);
    }

    private void initializeUIComponents() {
        int rightPanelX = gamePanel.getWidth() + 120;

        JLabel timeLabel = FrameUtil.createJLabel(this, "Time Left: 05:00",
                new Font("serif", Font.BOLD, 22),
                new Point(rightPanelX, 20), 300, 50);
        gamePanel.setTimeLabel(timeLabel);

        JLabel levelLabel = FrameUtil.createJLabel(this, "Level: " + this.level,
                new Font("serif", Font.BOLD, 22),
                new Point(rightPanelX + 200, 20), 300, 50);

        JLabel usernameLabel = FrameUtil.createJLabel(this, "Username: " + currentUser.getUsername(),
                new Font("serif", Font.BOLD, 22),
                new Point(rightPanelX + 100, 60), 300, 50);

        JLabel bestStepsLabel = FrameUtil.createJLabel(this,
                "Best Steps: " + (currentUser.hasBestSteps() ? currentUser.getBestSteps() : "N/A"),
                new Font("serif", Font.BOLD, 22),
                new Point(rightPanelX + 100, 100), 300, 50);

        JLabel bestTimeLabel = FrameUtil.createJLabel(this,
                "Best Time: " + (currentUser.hasBestTime() ? currentUser.getBestTime() : "N/A"),
                new Font("serif", Font.BOLD, 22),
                new Point(rightPanelX + 100, 140), 300, 50);

        JLabel stepLabel = FrameUtil.createJLabel(this, "Steps: 0",
                new Font("serif", Font.BOLD, 25),
                new Point(rightPanelX + 100, 220), 300, 50);
        gamePanel.setStepLabel(stepLabel);

        // restart game button
        this.restartBtn = FrameUtil.createButton(this, "Restart",
                new Point(rightPanelX + 100, 260), 120, 40);
        this.restartBtn.addActionListener(e -> {
            startNewGame();
            gamePanel.requestFocusInWindow();
        });

        // save game button (disabled for guests)
        this.saveBtn = FrameUtil.createButton(this, "Save Game",
                new Point(rightPanelX + 100, 300), 120, 40);

        this.saveBtn.addActionListener(e -> {
            userData.saveGame(false);
        });

        // forbid guest to click save button
        if (isGuest) {
            saveBtn.setEnabled(false);
            saveBtn.setToolTipText("Guest cannot save games.");
        }

        // undo button
        this.undoBtn = FrameUtil.createButton(this, "Undo",
                new Point(rightPanelX + 100, 340), 120, 40);
        this.undoBtn.addActionListener(e -> {
            if (gamePanel.undoLastMove()) {
                gamePanel.requestFocusInWindow();
            }
        });

        // direction button
        this.upBtn = FrameUtil.createButton(this, "⬆",
                new Point(rightPanelX + 100, 400), 40, 40);
        this.downBtn = FrameUtil.createButton(this, "⬇",
                new Point(rightPanelX + 100, 450), 40, 40);
        this.leftBtn = FrameUtil.createButton(this, "⬅",
                new Point(rightPanelX + 60, 425), 40, 40);
        this.rightBtn = FrameUtil.createButton(this, "➡",
                new Point(rightPanelX + 140, 425), 40, 40);

        // set direction button action
        upBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.UP));
        downBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.DOWN));
        leftBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.LEFT));
        rightBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.RIGHT));

        this.hammerBtn = FrameUtil.createButton(this, "🔨",
                new Point(rightPanelX + 50, 500), 50, 50);
        this.obstacleBtn = FrameUtil.createButton(this, "🚧",
                new Point(rightPanelX + 150, 500), 50, 50);

        hammerBtn.addActionListener(e -> controller.selectTool(GameController.Tool.HAMMER));
        obstacleBtn.addActionListener(e -> controller.selectTool(GameController.Tool.OBSTACLE));
    }

    public void startNewGame() {
        controller.restartGame();
        gamePanel.initializeGame();
        gamePanel.requestFocusInWindow();
    }

    public void loadHistoryGame() {
        userData.loadGame();
        gamePanel.requestFocusInWindow();
    }
}