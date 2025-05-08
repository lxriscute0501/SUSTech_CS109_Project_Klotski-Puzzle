package view.login;

import model.MapModel;
import model.User;
import view.FrameUtil;
import view.game.GameFrame;
import javax.swing.*;
import java.awt.*;

/**
 * The second frame, connecting LoginFrame && LevelFrame.
 * Able to start new game and load game (only the latest one, guest forbidden).
 */

public class StartMenuFrame extends JFrame {
    private JLabel titleLabel;

    private JButton startBtn;
    private JButton loadBtn;
    private JButton exitBtn;

    private final User user;
    private final boolean isGuest;

    public StartMenuFrame(int width, int height, boolean isGuest, User user) {
        this.isGuest = isGuest;
        this.user = user;

        this.setTitle("Klotski Puzzle - Start Menu");
        this.setLayout(null);
        this.setSize(width, height);

        titleLabel = FrameUtil.createJLabel(this, new Point(200, 50), 200, 40, "Klotski Puzzle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        startBtn = FrameUtil.createButton(this, "Start New Game", new Point(200, 150), 200, 40);
        startBtn.addActionListener(e -> {
            LevelFrame levelFrame = new LevelFrame(600, 400, isGuest, user);
            levelFrame.setVisible(true);
            this.setVisible(false);
        });

        loadBtn = FrameUtil.createButton(this, "Load Game", new Point(200, 200), 200, 40);
        loadBtn.addActionListener(e -> {
            MapModel mapModel = new MapModel(new int[4][5]);
            GameFrame gameFrame = new GameFrame(900, 600, mapModel, isGuest, user);
            gameFrame.loadHistoryGame();
            gameFrame.setVisible(true);
            gameFrame.requestFocus();
            this.setVisible(false);
        });

        exitBtn = FrameUtil.createButton(this, "Exit", new Point(200, 250), 200, 40);
        exitBtn.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame(600, 400);
            loginFrame.setVisible(true);
            this.dispose();
        });

        // disable load button for guest users
        if (isGuest) {
            loadBtn.setEnabled(false);
            loadBtn.setToolTipText("Guest cannot load saved games.");
        }

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}