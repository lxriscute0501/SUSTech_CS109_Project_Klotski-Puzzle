package view.login;

import model.MapModel;
import model.User;
import view.FrameUtil;
import view.game.GameFrame;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 难度选择界面，提供不同难度级别选项
 */
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

        JLabel titleLabel = FrameUtil.createJLabel(this, new Point(200, 50), 200, 40, "Choose Game Level");
        titleLabel.setFont(new Font("serif", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton easyBtn = FrameUtil.createButton(this, "Easy", new Point(200, 120), 200, 40);
        easyBtn.addActionListener(e -> startGameWithDifficulty(1));

        JButton mediumBtn = FrameUtil.createButton(this, "Medium", new Point(200, 180), 200, 40);
        mediumBtn.addActionListener(e -> startGameWithDifficulty(2));

        JButton hardBtn = FrameUtil.createButton(this, "Hard", new Point(200, 240), 200, 40);
        hardBtn.addActionListener(e -> startGameWithDifficulty(3));

        JButton backBtn = FrameUtil.createButton(this, "Return", new Point(200, 300), 200, 40);
        backBtn.addActionListener(e -> {
            StartMenuFrame startMenu = new StartMenuFrame(600, 400, isGuest, user);
            startMenu.setVisible(true);
            this.dispose();
        });

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void startGameWithDifficulty(int difficulty) {
        int[][] mapData = generateMap(difficulty);
        MapModel mapModel = new MapModel(mapData);

        // 创建游戏主界面
        GameFrame gameFrame = new GameFrame(900, 600, mapModel, isGuest, user);
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

        // if no map in the folder, return the default one
        int mapCount = getMapCount(basePath);
        if (mapCount == 0) return getDefaultMap(difficulty);

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

            // default medium level
            if (lines.isEmpty()) return getDefaultMap(2);

            int rows = lines.size();
            int cols = lines.get(0).split("\\s+").length;
            int[][] map = new int[rows][cols];

            for (int i = 0; i < rows; i++) {
                String[] values = lines.get(i).split("\\s+");
                for (int j = 0; j < cols && j < values.length; j++) {
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
            return getDefaultMap(2);
        }
    }

    private int[][] getDefaultMap(int difficulty) {
        switch (difficulty) {
            case 1: return new int[][]{};
            case 2: return new int[][]{};
            case 3: return new int[][]{};
            default: return new int[4][5];
        }
    }
}