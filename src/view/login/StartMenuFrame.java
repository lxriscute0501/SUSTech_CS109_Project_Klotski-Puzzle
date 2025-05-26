package view.login;

import model.MapModel;
import controller.User;
import view.FrameUtil;
import view.game.BackgroundPanel;
import view.game.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class StartMenuFrame extends JFrame {
    private User user;
    private boolean isGuest;

    public StartMenuFrame(int width, int height, boolean isGuest, User user) {
        this.isGuest = isGuest;
        this.user = user;

        setTitle("Klotski Puzzle - Start Menu");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/UI/startBG.png");
        this.setContentPane(backgroundPanel);

        // Use GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // spacing between rows
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title label
        JLabel titleLabel = new JLabel("Klotski Puzzle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // Start Button
        JButton startBtn = FrameUtil.createImageButton("/Buttons/startNew.png", "Start new game", 150, 75);
        startBtn.addActionListener(e -> {
            LevelFrame levelFrame = new LevelFrame(600, 400, isGuest, user);
            levelFrame.setVisible(true);
            this.dispose();
        });
        gbc.gridy = 1;
        add(startBtn, gbc);

        // Load Button
        JButton loadBtn = FrameUtil.createImageButton("/Buttons/loadGame.png", "Load Game", 150, 75);
        loadBtn.addActionListener(e -> {
            String filePath = "data/" + user.getUsername() + "/data.txt";
            File saveFile = new File(filePath);

            if (!saveFile.exists() || !saveFile.canRead()) {
                JOptionPane.showMessageDialog(this, "Game cannot load!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                List<String> lines = Files.readAllLines(Path.of(filePath));
                String level = lines.getFirst();
                if (!Objects.equals(level, "Easy") && !Objects.equals(level, "Medium") && !Objects.equals(level, "Hard")) {
                    JOptionPane.showMessageDialog(this, "Game cannot load!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int[][] loadedMap = new int[4][5];
                for (int i = 0; i < 4; i++) {
                    String[] value1 = lines.get(5 + i).split(" ");

                    if (value1.length != 5) {
                        JOptionPane.showMessageDialog(this, "Game cannot load!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    for (int j = 0; j < 5; j++) {
                        loadedMap[i][j] = Integer.parseInt(value1[j]);
                    }
                }

                MapModel mapModel = new MapModel(loadedMap, level);
                GameFrame gameFrame = new GameFrame(900, 600, mapModel, isGuest, user);
                gameFrame.loadHistoryGame();
                gameFrame.setVisible(true);
                gameFrame.requestFocus();
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load game data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        if (isGuest) {
            loadBtn.setEnabled(false);
            loadBtn.setToolTipText("Guest cannot load saved games.");
        }
        gbc.gridy = 2;
        add(loadBtn, gbc);

        // Exit Button
        JButton exitBtn = FrameUtil.createImageButton("/Buttons/exitNew.png", "Exit", 150, 75);
        exitBtn.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame(600, 400);
            loginFrame.setVisible(true);
            this.dispose();
        });
        gbc.gridy = 3;
        add(exitBtn, gbc);
    }
}
