package view.login;

import view.FrameUtil;
import view.game.GameFrame;
import javax.swing.*;
import java.awt.*;

public class StartMenuFrame extends JFrame {
    private final GameFrame gameFrame;
    private final boolean isGuest;

    public StartMenuFrame(GameFrame gameFrame, boolean isGuest) {
        this.gameFrame = gameFrame;
        this.isGuest = isGuest;
        initializeUI();
    }

    private void initializeUI() {
        this.setTitle("Klotski Puzzle - Start Menu");
        this.setLayout(null);
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // title label
        JLabel titleLabel = FrameUtil.createJLabel(this, new Point(100, 30), 200, 40, "Klotski Puzzle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // set start game button
        JButton startBtn = FrameUtil.createButton(this, "Start New Game", new Point(100, 100), 200, 40);
        startBtn.addActionListener(e -> {
            gameFrame.startNewGame();
            this.setVisible(false);
            gameFrame.setVisible(true);
        });

        // set load game button (disabled for guests)
        JButton loadBtn = FrameUtil.createButton(this, "Load Game", new Point(100, 160), 200, 40);
        loadBtn.addActionListener(e -> {
            if (gameFrame.loadGame()) {
                this.setVisible(false);
                gameFrame.setVisible(true);
            }
        });

        // disable load button for guest users
        if (isGuest) {
            loadBtn.setEnabled(false);
            loadBtn.setToolTipText("Guest cannot load saved games!");
        }

        // set back button
        JButton backBtn = FrameUtil.createButton(this, "Back to Login", new Point(100, 220), 200, 40);
        backBtn.addActionListener(e -> {
            this.dispose();
            gameFrame.getLoginFrame().setVisible(true);
        });
    }
}