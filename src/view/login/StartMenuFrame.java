package view.login;

import view.FrameUtil;
import view.game.GameFrame;
import javax.swing.*;
import java.awt.*;

/**
 * The middle frame, connecting LoginFrame && GameFrame.
 * Able to start new game and load game (only the lastest one, guest forbidden).
 */

public class StartMenuFrame extends JFrame {
    private JLabel titleLabel;

    private JButton startBtn;
    private JButton loadBtn;

    private final GameFrame gameFrame;
    private final boolean isGuest;

    public StartMenuFrame(int width, int height, GameFrame gameFrame, boolean isGuest) {
        this.gameFrame = gameFrame;
        this.isGuest = isGuest;

        this.setTitle("Klotski Puzzle - Start Menu");
        this.setLayout(null);
        this.setSize(width, height);

        // title label
        titleLabel = FrameUtil.createJLabel(this, new Point(200, 50), 200, 40, "Klotski Puzzle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // set start game button
        startBtn = FrameUtil.createButton(this, "Start New Game", new Point(200, 150), 200, 40);
        startBtn.addActionListener(e -> {
            gameFrame.startNewGame();
            enterGameFrame();
        });

        // set load game button (disabled for guests)
        loadBtn = FrameUtil.createButton(this, "Load Game", new Point(200, 200), 200, 40);
        loadBtn.addActionListener(e -> {
            if (gameFrame.loadGame()) {
                enterGameFrame();
            }
        });

        // disable load button for guest users
        if (isGuest) {
            loadBtn.setEnabled(false);
            loadBtn.setToolTipText("Guest cannot load saved games!");
        }

        gameFrame.setGuestMode(isGuest);

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }


    private void enterGameFrame() {
        gameFrame.setVisible(true);
        gameFrame.requestFocus();
        this.setVisible(false);
    }
}