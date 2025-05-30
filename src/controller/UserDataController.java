package controller;

import model.MapModel;
import view.game.GamePanel;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
        gameData.add(String.valueOf(view.getSteps()));
        gameData.add(String.valueOf(controller.getActualTime()));
        gameData.add(String.valueOf(currentUser.getBestSteps()));
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
            if (!isAuto) {
                view.showInfoMessage("Game saved successfully!");
            }
        } catch (Exception e) {
            view.showErrorMessage("Game saved failed: " + e.getMessage());
        }
    }

    public void loadGame() {
        String filePath = "data/" + currentUser.getUsername() + "/data.txt";

        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            System.out.println("Loaded data: " + lines);

            if (lines.size() != 9) {
                view.showErrorMessage("Save file is corrupted!");
                return;
            }

            String level = lines.get(0);
            int savedStepCount = Integer.parseInt(lines.get(1));
            long savedTimeLeft = 300 - Long.parseLong(lines.get(2));

            if ((!Objects.equals(level, "Easy") && !Objects.equals(level, "Medium") && !Objects.equals(level, "Hard")) || savedTimeLeft < 0) {
                view.showErrorMessage("Save file is corrupted!");
                return;
            }

            model.setLevel(level);
            view.setSteps(savedStepCount);
            view.setTimeLabelString("Time Left: " + formatTime(savedTimeLeft));

            int[][] loadedMap = new int[4][5];
            for (int i = 0; i < 4; i++)
            {
                String[] value1 = lines.get(5 + i).split(" ");

                if (value1.length != 5) {
                    view.showErrorMessage("Save file is corrupted!");
                    return;
                }

                for (int j = 0; j < 5; j++) {
                    loadedMap[i][j] = Integer.parseInt(value1[j]);
                }
            }

            model.setMatrix(loadedMap);
            view.rebuildGameView(loadedMap);
            view.setTimeLabelString("Time Left: " + formatTime(savedTimeLeft));
            view.showInfoMessage("Loaded level: " + level);

            controller.startGameTimer(savedTimeLeft);

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