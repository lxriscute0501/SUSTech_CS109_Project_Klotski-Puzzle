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
 * The third frame, connecting StartMenuFrame && GameFrame.
 * Able to choose different game levels.
 */

public class LevelFrame extends JFrame {
    private final boolean isGuest;
    private final User user;
    private int[][] exitLocations;

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
        easyBtn.addActionListener(e -> startGameWithLevel(1));

        JButton mediumBtn = FrameUtil.createButton(this, "Medium", new Point(200, 180), 200, 40);
        mediumBtn.addActionListener(e -> startGameWithLevel(2));

        JButton hardBtn = FrameUtil.createButton(this, "Hard", new Point(200, 240), 200, 40);
        hardBtn.addActionListener(e -> startGameWithLevel(3));

        JButton backBtn = FrameUtil.createButton(this, "Return", new Point(200, 300), 200, 40);
        backBtn.addActionListener(e -> {
            StartMenuFrame startMenu = new StartMenuFrame(600, 400, isGuest, user);
            startMenu.setVisible(true);
            this.dispose();
        });

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void startGameWithLevel(int difficulty) {
        int[][] mapData = generateMap(difficulty);

        String level = "";
        if (difficulty == 1) level = "Easy"; else if (difficulty == 2) level = "Medium"; else if (difficulty == 3) level = "Hard";

        MapModel model = new MapModel(mapData, level, exitLocations);
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

        // if no map in the folder, return the default map
        int mapCount = getMapCount(basePath);
        if (mapCount == 0) return getDefaultMap();

        Random random = new Random();
        int selectedMap = 1;
        //  int selectedMap = random.nextInt(mapCount) + 1;
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

            if (hasExits && lines.size() >= 8) {
                int[][] exits = new int[4][2];
                for (int i = 4; i < 8; i++) {
                    String[] coord = lines.get(i).split("\\s+");
                    if (coord.length >= 2) {
                        try {
                            exits[i-4][0] = Integer.parseInt(coord[0]);
                            exits[i-4][1] = Integer.parseInt(coord[1]);
                        } catch (NumberFormatException e) {
                            exits[i-4][0] = -1;
                            exits[i-4][1] = -1;
                        }
                    }
                }
                this.exitLocations = exits;
            }

            return map;
        } catch (Exception e) {
            System.err.println("Map loading failed: " + filePath + ", Error: " + e.getMessage());
            return getDefaultMap();
        }
    }

    public int[][] getExitLocations() {
        return exitLocations;
    }

    public String exitLocationToString() {
        StringBuilder sb = new StringBuilder();
        for (int[] exit : exitLocations) {
            sb.append("(").append(exit[0]).append(",").append(exit[1]).append(") ");
        }
        return sb.toString().trim();
    }

    // defensive programming, if level resources have been changed, we can still load the default map
    private int[][] getDefaultMap() {
        return new int[4][5];
    }
}