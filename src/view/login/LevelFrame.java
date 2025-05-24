package view.login;

import model.MapModel;
import controller.User;
import view.FrameUtil;
import view.game.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelFrame extends JFrame {
    private final boolean isGuest;
    private final User user;

    public LevelFrame(int width, int height, boolean isGuest, User user) {
        this.isGuest = isGuest;
        this.user = user;

        setTitle("Klotski Puzzle - Game Level Choose");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Use GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // General GBC setup for all components
        gbc.insets = new Insets(0, 0, 0, 0); // vertical spacing
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Row 0 - Title label
        JLabel titleLabel = new JLabel("Level Selection", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // Row 1 - Subheading label
        JLabel subheadingLabel = new JLabel("Pick the level you want to play", SwingConstants.CENTER);
        subheadingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        add(subheadingLabel, gbc);

        // Button size constants
        int btnWidth = 150;
        int btnHeight = 75;

        // Helper method to create and add buttons
        gbc.gridy = 2;
        JButton easyBtn = FrameUtil.createImageButton("/Buttons/easy.png", "Easy", btnWidth, btnHeight);
        easyBtn.addActionListener(e -> startGameWithLevel(1));
        add(easyBtn, gbc);

        gbc.gridy = 3;
        JButton mediumBtn = FrameUtil.createImageButton("/Buttons/medium.png", "Medium", btnWidth, btnHeight);
        mediumBtn.addActionListener(e -> startGameWithLevel(2));
        add(mediumBtn, gbc);

        gbc.gridy = 4;
        JButton hardBtn = FrameUtil.createImageButton("/Buttons/hard.png", "Hard", btnWidth, btnHeight);
        hardBtn.addActionListener(e -> startGameWithLevel(3));
        add(hardBtn, gbc);

        gbc.gridy = 5;
        JButton backBtn = FrameUtil.createImageButton("/Buttons/back.png", "Return", btnWidth, btnHeight);
        backBtn.addActionListener(e -> {
            StartMenuFrame startMenu = new StartMenuFrame(600, 400, isGuest, user);
            startMenu.setVisible(true);
            this.dispose();
        });
        add(backBtn, gbc);
    }

    private void startGameWithLevel(int difficulty) {
        int[][] mapData = generateMap(difficulty);

        String level = "";
        if (difficulty == 1) level = "Easy";
        else if (difficulty == 2) level = "Medium";
        else if (difficulty == 3) level = "Hard";

        MapModel model = new MapModel(mapData, level);
        model.setLevel(level);

        GameFrame gameFrame = new GameFrame(900, 600, model, isGuest, user);
        gameFrame.startNewGame();
        gameFrame.setVisible(true);
        gameFrame.requestFocus();
        this.dispose();
    }

    private int[][] generateMap(int difficulty) {
        String difficultyFolder;
        switch (difficulty) {
            case 1:
                difficultyFolder = "easy";
                break;
            case 2:
                difficultyFolder = "medium";
                break;
            case 3:
                difficultyFolder = "hard";
                break;
            default:
                difficultyFolder = "medium";
        }

        String basePath = "resources/levels/" + difficultyFolder + "/";

        int mapCount = getMapCount(basePath);
        if (mapCount == 0) return getDefaultMap();

        Random random = new Random();
        int selectedMap = random.nextInt(mapCount) + 1;
        String filePath = basePath + selectedMap + ".txt";

        return loadMapFromFile(filePath);
    }

    private int getMapCount(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
            return files != null ? files.length : 0;
        }
        return 0;
    }

    private int[][] loadMapFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }

            if (lines.isEmpty()) return getDefaultMap();

            int[][] map = new int[4][5];
            for (int i = 0; i < 4 && i < lines.size(); i++) {
                String[] values = lines.get(i).split("\\s+");
                for (int j = 0; j < 5 && j < values.length; j++) {
                    try {
                        map[i][j] = Integer.parseInt(values[j]);
                    } catch (NumberFormatException e) {
                        map[i][j] = 0;
                    }
                }
            }

            return map;
        } catch (Exception e) {
            System.err.println("Map loading failed: " + filePath + ", Error: " + e.getMessage());
            return getDefaultMap();
        }
    }

    private int[][] getDefaultMap() {
        return new int[4][5];
    }
}
