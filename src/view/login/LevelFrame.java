package view.login;

import model.MapModel;
import controller.User;
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

        this.setTitle("Klotski Puzzle - Game Level Choose");
        this.setLayout(null);
        this.setSize(width, height);
        this.setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Level Selection", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBounds(0, 30, this.getWidth(), 40);
        this.add(titleLabel);

        JLabel subheadingLabel = new JLabel("Pick the level you want to play", SwingConstants.CENTER);
        subheadingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subheadingLabel.setBounds(0, 70, this.getWidth(), 25);
        this.add(subheadingLabel);


        JButton easyBtn = createImageButton("/Buttons/easy.png", "Easy");
        easyBtn.setBounds(200, 120, 200, 40);
        easyBtn.addActionListener(e -> startGameWithLevel(1));
        this.add(easyBtn);

        JButton mediumBtn = createImageButton("/Buttons/medium.png", "Medium");
        mediumBtn.setBounds(200, 180, 200, 40);
        mediumBtn.addActionListener(e -> startGameWithLevel(2));
        this.add(mediumBtn);

        JButton hardBtn = createImageButton("/Buttons/hard.png", "Hard");
        hardBtn.setBounds(200, 240, 200, 40);
        hardBtn.addActionListener(e -> startGameWithLevel(3));
        this.add(hardBtn);

        JButton backBtn = createImageButton("/Buttons/back.png", "Return");
        backBtn.setBounds(200, 300, 200, 40);
        backBtn.addActionListener(e -> {
            StartMenuFrame startMenu = new StartMenuFrame(600, 400, isGuest, user);
            startMenu.setVisible(true);
            this.dispose();
        });
        this.add(backBtn);



        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void startGameWithLevel(int difficulty) {
        int[][] mapData = generateMap(difficulty);

        String level = "";
        if (difficulty == 1) level = "Easy"; else if (difficulty == 2) level = "Medium"; else if (difficulty == 3) level = "Hard";

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
            case 1: difficultyFolder = "easy"; break;
            case 2: difficultyFolder = "medium"; break;
            case 3: difficultyFolder = "hard"; break;
            default: difficultyFolder = "medium";
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

            boolean hasExits = lines.size() > 4;
            int[][] map = new int[4][5];

            for (int i = 0; i < 4 && i < lines.size(); i++)
            {
                String[] values = lines.get(i).split("\\s+");
                for (int j = 0; j < 5 && j < values.length; j++)
                {
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

    // defensive programming, if level resources have been changed, we can still load the default map
    private int[][] getDefaultMap() {
        return new int[4][5];
    }

    private JButton createImageButton(String resourcePath, String toolTipText) {
        java.net.URL imageUrl = getClass().getResource(resourcePath);
        if (imageUrl == null) {
            System.err.println("Could not load image at: " + resourcePath);
            JButton fallback = new JButton(toolTipText);
            fallback.setToolTipText(toolTipText);
            return fallback;
        }

        ImageIcon originalIcon = new ImageIcon(imageUrl);
        int width = 150;
        int height = 75;
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