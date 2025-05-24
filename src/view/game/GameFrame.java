package view.game;

import controller.GameController;
import controller.UserDataController;
import model.Direction;
import model.MapModel;
import controller.User;
import view.FrameUtil;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private Clip backgroundClip;
    private GameController controller;
    private UserDataController userData;

    private JButton restartBtn;
    private JButton saveBtn;
    private JButton upBtn;
    private JButton downBtn;
    private JButton leftBtn;
    private JButton rightBtn;
    private JButton undoBtn;
    private JButton soundBtn;
    private JButton hammerBtn;
    private JButton obstacleBtn;
    private JLabel timeLabel;
    private JLabel levelLabel;
    private JLabel stepLabel;

    private boolean isSoundOn = true;
    private GamePanel gamePanel;
    private User currentUser;
    private boolean isGuest;
    private String level;
    private MapModel map;

    public GameFrame(int width, int height, MapModel mapModel, boolean isGuest, User user) {
        this.setTitle("Klotski Puzzle");
        this.setSize(width, height);
        this.setLayout(null);

        this.currentUser = user;
        this.isGuest = isGuest;
        this.map = mapModel;
        this.level = map.getLevel();



        gamePanel = new GamePanel(mapModel);
        gamePanel.setLocation(width/2-gamePanel.getWidth()/2, height / 2 - gamePanel.getHeight() / 2-100);
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
        playBackgroundMusic();
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
        int x=0;
        int y=0;

        // Time Label
        JLabel timeLabel = FrameUtil.createJLabel(this, "Time Left 05:00",
                new Font("Arial", Font.PLAIN, 22),
                new Point(x+30, y+20),
                200, 100);
        gamePanel.setTimeLabel(timeLabel);

        // Level Label
        JLabel levelLabel = FrameUtil.createJLabel(this, "",
                new Font("Arial", Font.PLAIN, 22),
                new Point(x+45, y+100),
                200, 100);
        levelLabel.setText("<html><div style='text-align: center;'>"
                + "<b>Level</b><br>"
                + "<hr style='width: 80%; margin: 2px 0;'>"
                + "<span style='font-size: 16pt;'>" + this.level + "</span>"
                + "</div></html>");

        this.add(levelLabel);

        // Steps Label
        JLabel stepLabel = FrameUtil.createJLabel(this, "Steps: 0",
                new Font("Arial", Font.PLAIN, 25),
                new Point(x+40, y+180),
                200, 100);
        gamePanel.setStepLabel(stepLabel);

        this.add(stepLabel);

        // Username Label
        JLabel usernameLabel = FrameUtil.createJLabel(this,
                "<html><div style='text-align: center;'>"
                        + "<b>Username</b><br>"
                        + "<hr style='width: 80%; margin: 2px 0;'>"
                        + "<span style='font-size: 16pt;'>" + currentUser.getUsername() + "</span>"
                        + "</div></html>",
                new Font("Arial", Font.PLAIN, 22),
                new Point(x+750, y+20),
                200, 100);
        this.add(usernameLabel);

        // Best Steps Label
        JLabel bestStepsLabel = FrameUtil.createJLabel(this,
                "<html><div style='text-align: center;'>"
                        + "<b>Best Steps</b><br>"
                        + "<hr style='width: 80%; margin: 2px 0;'>"
                        + "<span style='font-size: 16pt;'>"
                        + (currentUser.hasBestSteps() ? currentUser.getBestSteps() : "N/A")
                        + "</span></div></html>",
                new Font("Arial", Font.PLAIN, 22),
                new Point(x+750, 100),
                200, 100);
        this.add(bestStepsLabel);

        // Best Time Label
        JLabel bestTimeLabel = FrameUtil.createJLabel(this,
                "<html><div style='text-align: center;'>"
                        + "<b>Best Time</b><br>"
                        + "<hr style='width: 80%; margin: 2px 0;'>"
                        + "<span style='font-size: 16pt;'>"
                        + (currentUser.hasBestTime() ? currentUser.getBestTime() : "N/A")
                        + "</span></div></html>",
                new Font("Arial", Font.PLAIN, 22),
                new Point(x+752, y+180),
                200, 100);
        this.add(bestTimeLabel);

        // Buttons size
        int btnWidth = 180;
        int btnHeight = 90;

        restartBtn = FrameUtil.createImageButton("/Buttons/restart.png", "Restart", btnWidth, btnHeight);
        restartBtn.setBounds(x+710, y+470, btnWidth, btnHeight);
        restartBtn.addActionListener(e -> {
            startNewGame();
            gamePanel.requestFocusInWindow();
        });
        this.add(restartBtn);

        saveBtn = FrameUtil.createImageButton("/Buttons/saveGame.png", "Save Game", btnWidth, btnHeight);
        saveBtn.setBounds(x+710, y+420, btnWidth, btnHeight);
        saveBtn.addActionListener(e -> userData.saveGame(false));
        if (isGuest) {
            saveBtn.setEnabled(false);
            saveBtn.setToolTipText("Guest cannot save games.");
        }
        this.add(saveBtn);

        undoBtn = FrameUtil.createImageButton("/Buttons/undo.png", "Undo", btnWidth, btnHeight);
        undoBtn.setBounds(x+710, y+370, btnWidth, btnHeight);
        undoBtn.addActionListener(e -> {
            if (gamePanel.undoLastMove()) {
                gamePanel.requestFocusInWindow();
            }
        });
        this.add(undoBtn);


        upBtn = FrameUtil.createImageButton("/Buttons/up.png", "Move Up", 150, 100);
        upBtn.setBounds(x+375, y+325, 150, 100);
        upBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.UP));
        this.add(upBtn);

        downBtn = FrameUtil.createImageButton("/Buttons/down.png", "Move Down", 150, 100);
        downBtn.setBounds(x+375, y+405, 150, 100);
        downBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.DOWN));
        this.add(downBtn);

        leftBtn = FrameUtil.createImageButton("/Buttons/left.png", "Move Left", 150, 100);
        leftBtn.setBounds(x+335, 365, 150, 100);
        leftBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.LEFT));
        this.add(leftBtn);

        rightBtn = FrameUtil.createImageButton("/Buttons/right.png", "Move Right", 150, 100);
        rightBtn.setBounds(x + 415, 365, 150, 100);
        rightBtn.addActionListener(e -> gamePanel.moveSelectedBox(Direction.RIGHT));
        this.add(rightBtn);

        // Sound Toggle Button (larger size)
        int soundBtnWidth = 150;
        int soundBtnHeight = 100;
        soundBtn = FrameUtil.createImageButton("/Buttons/soundOn.png", "Toggle Sound", soundBtnWidth, soundBtnHeight);
        soundBtn.setBounds(x+ 0,y+ 450, soundBtnWidth, soundBtnHeight);
        soundBtn.addActionListener(e -> toggleSound());
        this.add(soundBtn);



        // Tool Buttons
        // Tool Buttons
        hammerBtn = FrameUtil.createImageButton( "/Buttons/hammer.png", "hammer",soundBtnWidth, soundBtnHeight);
        hammerBtn.setBounds(x +0, y+300, soundBtnWidth, soundBtnHeight);
        hammerBtn.addActionListener(e -> controller.selectTool(GameController.Tool.HAMMER));
        this.add(hammerBtn);

        obstacleBtn = FrameUtil.createImageButton( "/Buttons/obstacle.png","obstacle",soundBtnWidth,soundBtnHeight);
        obstacleBtn.setBounds(x +0, y+375, soundBtnWidth, soundBtnHeight);
        obstacleBtn.addActionListener(e -> controller.selectTool(GameController.Tool.OBSTACLE));
        this.add(obstacleBtn);
    }

    private void toggleSound() {
        if (isSoundOn) {
            stopBackgroundMusic();
            soundBtn.setIcon(FrameUtil.loadIcon("/Buttons/mute.png", 200, 100));
        } else {
            playBackgroundMusic();
            soundBtn.setIcon(FrameUtil.loadIcon("/Buttons/soundOn.png", 200, 100));
        }
        isSoundOn = !isSoundOn;
        gamePanel.requestFocusInWindow();
    }

    private void playBackgroundMusic() {
        try {
            if (backgroundClip != null && backgroundClip.isRunning()) {
                backgroundClip.stop();
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    getClass().getResource("/Sounds/bgm.wav"));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
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
