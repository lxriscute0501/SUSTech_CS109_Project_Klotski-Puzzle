package controller;

import model.MapModel;
import model.User;
import view.game.GamePanel;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.Timer;

public class UserDataController {
    private final GameController controller;
    private final GamePanel view;
    private final MapModel model;
    private final User currentUser;
    private Timer autoSaveTimer;

    public UserDataController(GameController controller, GamePanel view, MapModel model, User user) {
        this.controller = controller;
        this.view = view;
        this.model = model;
        this.currentUser = user;
        this.view.setUserData(this);
    }

    public void saveGame(boolean isAuto) {
        if (currentUser.isGuest()) {
            if (!isAuto) view.showErrorMessage("Guest can not save game!");
            return;
        }

        int[][] saveMap = model.getMatrix();
        List<String> gameData = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        gameData.add(model.getLevel());

        // Existing data (shifted down by 1 line)
        gameData.add(String.valueOf(view.getSteps()));
        gameData.add(String.valueOf(controller.getActualTime()));
        gameData.add(String.valueOf(currentUser.getBestTime()));
        gameData.add(String.valueOf(currentUser.getBestMoveCount()));

        for (int[] line : saveMap) {
            sb.setLength(0);
            for (int value : line) {
                sb.append(value).append(" ");
            }
            gameData.add(sb.toString().trim());
        }

        String filePath = "data/" + currentUser.getUsername();
        File dir = new File(filePath);
        dir.mkdirs();

        try {
            Files.write(Path.of(filePath + "/data.txt"), gameData);
            if (!isAuto) view.showInfoMessage("Game saved successfully!");
        } catch (Exception e) {
            view.showErrorMessage("Game saved failed: " + e.getMessage());
        }
    }

    public void loadGame() {
        if (currentUser.isGuest()) {
            view.showErrorMessage("Guest can not load game!");
            return;
        }

        String filePath = "data/" + currentUser.getUsername() + "/data.txt";
        File saveFile = new File(filePath);

        if (!saveFile.exists()) {
            view.showErrorMessage("No saved game found for this user!");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));

            if (lines.size() < 5) {
                view.showErrorMessage("Save file is corrupted!");
                return;
            }

            String level = lines.get(0);
            model.setLevel(level);

            int savedStepCount = Integer.parseInt(lines.get(1));
            long savedTimeLeft = 300 - Long.parseLong(lines.get(2));
            long bestTime = Long.parseLong(lines.get(3));
            int bestSteps = Integer.parseInt(lines.get(4));

            int[][] loadedMap = new int[model.getHeight()][model.getWidth()];
            for (int row = 5; row < lines.size() && row - 5 < model.getHeight(); row++) {
                String[] values = lines.get(row).trim().split(" ");
                for (int col = 0; col < values.length && col < model.getWidth(); col++) {
                    try {
                        loadedMap[row - 5][col] = Integer.parseInt(values[col]);
                    } catch (NumberFormatException e) {
                        loadedMap[row - 5][col] = 0;
                    }
                }
            }

            model.setMatrix(loadedMap);
            view.setSteps(savedStepCount);
            currentUser.updateBestTime(bestTime);
            currentUser.updateBestMoveCount(bestSteps);

            view.rebuildGameView(loadedMap);
            view.updateStepCount(view.getSteps());
            view.clearSelection();
            view.setTimeLabelString("Time Left: " + formatTime(savedTimeLeft));

            // Show level in success message (optional)
            view.showInfoMessage("Game loaded successfully! Level: " + level);

        } catch (Exception e) {
            view.showErrorMessage("Failed to load game: " + e.getMessage());
        }
    }

    public void setupAutoSave(int intervalMinutes) {
        autoSaveTimer = new Timer(intervalMinutes * 60 * 1000, e -> {
            if (currentUser != null && !currentUser.isGuest()) {
                saveGame(true);
                System.out.println("Auto-save successfully！Time: " + new Date());
            }
        });
        autoSaveTimer.start();
    }

    private String formatTime(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}