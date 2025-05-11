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

        // saved data
        gameData.add(model.getLevel());
        gameData.add(String.valueOf(view.getSteps()));
        gameData.add(String.valueOf(controller.getActualTime()));
        gameData.add(String.valueOf(currentUser.getBestMoveCount()));
        gameData.add(String.valueOf(currentUser.getBestTime()));

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
        String filePath = "data/" + currentUser.getUsername() + "/data.txt";

        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            System.out.println("Loaded data: " + lines);

            if (lines.size() < 5) {
                view.showErrorMessage("Save file is corrupted!");
                return;
            }

            String level = lines.get(0);
            int savedStepCount = Integer.parseInt(lines.get(1));
            long savedTimeLeft = 300 - Long.parseLong(lines.get(2));
            int bestSteps = Integer.parseInt(lines.get(3));
            long bestTime = Long.parseLong(lines.get(4));

            model.setLevel(level);
            view.updateLevelLabel(level);
            view.updateBestTimeLabel(bestTime);
            view.updateBestStepsLabel(bestSteps);
            view.setSteps(savedStepCount);

            int[][] loadedMap = new int[model.getHeight()][model.getWidth()];
            for (int row = 5; row < lines.size(); row++) {
                String[] values = lines.get(row).split(" ");
                for (int col = 0; col < values.length; col++) {
                    loadedMap[row - 5][col] = Integer.parseInt(values[col]);
                }
            }

            model.setMatrix(loadedMap);
            view.rebuildGameView(loadedMap);
            view.setTimeLabelString("Time Left: " + formatTime(savedTimeLeft));
            view.showInfoMessage("Loaded level: " + level);

        } catch (Exception e) {
            view.showErrorMessage("Load failed: " + e.getMessage());
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