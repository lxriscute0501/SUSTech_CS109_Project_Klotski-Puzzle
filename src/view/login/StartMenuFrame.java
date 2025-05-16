package view.login;

import model.MapModel;
import model.User;
import view.FrameUtil;
import view.game.GameFrame;

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
                String level = lines.getFirst();

                int[][] loadedMap = new int[4][5];
                for (int i = 0; i < 4; i++)
                {
                    String[] value1 = lines.get(5 + i).split(" ");
                    for (int j = 0; j < 5; j++) {
                        loadedMap[i][j] = Integer.parseInt(value1[j]);
                    }
                }

                int[][] exit = new int[4][2];
                for (int i = 0; i < 4; i++)
                {
                    String[] value2 = lines.get(9 + i).split(" ");
                    for (int j = 0; j < 2; j++) {
                        exit[i][j] = Integer.parseInt(value2[j]);
                    }
                }

                MapModel mapModel = new MapModel(loadedMap, level, exit);
                GameFrame gameFrame = new GameFrame(900, 600, mapModel, isGuest, user);
                gameFrame.loadHistoryGame();
                gameFrame.setVisible(true);
                gameFrame.requestFocus();
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load game data.", "Error", JOptionPane.ERROR_MESSAGE);
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