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

        int frameWidth = this.getWidth();
        int buttonWidth = 140;
        int buttonHeight = 50;
        int centerX = (frameWidth - buttonWidth) / 2;


        int labelWidth = 300;
        int labelHeight = 50;
        int labelX = (width - labelWidth) / 2;

        titleLabel = FrameUtil.createJLabel(this, new Point(labelX, 50), labelWidth, labelHeight, "Klotski Puzzle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        startBtn = createImageButton("Buttons/startNew.png", "Start new game");
        startBtn.setBounds(centerX, 130, buttonWidth, buttonHeight);
        startBtn.addActionListener(e -> {
            LevelFrame levelFrame = new LevelFrame(600, 400, isGuest, user);
            levelFrame.setVisible(true);
            this.setVisible(false);
        });
        this.add(startBtn); // ✅ Important!

        loadBtn = createImageButton("Buttons/loadGame.png", "Load Game");
        loadBtn.setBounds(centerX, 200, buttonWidth, buttonHeight);
        loadBtn.addActionListener(e -> {
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
                for (int i = 0; i < 4; i++) {
                    String[] value1 = lines.get(5 + i).split(" ");
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
        this.add(loadBtn); // ✅ Important!

        exitBtn = createImageButton("Buttons/exitNew.png", "Exit");
        exitBtn.setBounds(centerX, 270, buttonWidth, buttonHeight);
        exitBtn.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame(600, 400);
            loginFrame.setVisible(true);
            this.dispose();
        });
        this.add(exitBtn);

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private JButton createImageButton(String resourcePath, String toolTipText) {
        // Use ClassLoader to load resource from the "resources" directory
        java.net.URL imageUrl = getClass().getClassLoader().getResource(resourcePath);
        if (imageUrl == null) {
            System.err.println("Could not load image at: " + resourcePath);
            JButton fallback = new JButton(toolTipText);
            fallback.setToolTipText(toolTipText);
            fallback.setPreferredSize(new Dimension(100, 50));
            return fallback;
        }

        int width = 150;
        int height = 75;

        ImageIcon originalIcon = new ImageIcon(imageUrl);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);
        button.setPreferredSize(new Dimension(width, height));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setToolTipText(toolTipText);
        return button;
    }
}
