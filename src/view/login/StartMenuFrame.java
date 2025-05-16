package view.login;

import model.MapModel;
import model.User;
import view.FrameUtil;
import view.game.GameFrame;
import view.game.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The second frame, connecting LoginFrame && LevelFrame.
 * Able to start new game and load game (only the latest one, guest forbidden).
 */

public class StartMenuFrame extends JFrame {
    private JLabel titleLabel;

    private JButton startBtn;
    private JButton loadBtn;
    private JButton exitBtn;

    private User user;
    private boolean isGuest;
    private GamePanel gamePanel;

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
            // check whether data.txt exists and is readable
            String filePath = "data/" + user.getUsername() + "/data.txt";
            File saveFile = new File(filePath);

            if (!saveFile.exists() || !saveFile.canRead()) {
                JOptionPane.showMessageDialog(this, "Game cannot load!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<String> lines = null;
            try {
                lines = Files.readAllLines(Path.of(filePath));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            try {
                int[][] loadedMap = new int[4][5];
                for (int row = 5; row < lines.size(); row++) {
                    String[] values = lines.get(row).split(" ");
                    for (int col = 0; col < values.length; col++) {
                        loadedMap[row - 5][col] = Integer.parseInt(values[col]);
                    }
                }

                MapModel mapModel = new MapModel(loadedMap);
                GameFrame gameFrame = new GameFrame(900, 600, mapModel, isGuest, user);
                gameFrame.loadHistoryGame();
                gameFrame.setVisible(true);
                gameFrame.requestFocus();
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load game data.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });


        // disable load button for guest users
        if (isGuest) {
            loadBtn.setEnabled(false);
            loadBtn.setToolTipText("Guest cannot load saved games.");
        }

        exitBtn = FrameUtil.createButton(this, "Exit", new Point(200, 250), 200, 40);
        exitBtn.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame(600, 400);
            loginFrame.setVisible(true);
            this.dispose();
        });

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}