package util;

import model.GameState;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Utility class for validating game save files.
 */
public class FileValid {

    /**
     * Validates a game save file
     * @param file the save file to validate
     * @return true if the file is valid, false otherwise
     */
    public static boolean validateSaveFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }

        // Check basic file properties
        if (file.length() == 0) {
            return false;
        }

        // Check if the file can be deserialized into a GameState
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            Object obj = ois.readObject();
            if (!(obj instanceof GameState)) {
                return false;
            }

            GameState state = (GameState) obj;
            return validateGameState(state);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates the content of a GameState object
     * @param state the GameState to validate
     * @return true if the state is valid, false otherwise
     */
    private static boolean validateGameState(GameState state) {
        if (state == null) {
            return false;
        }

        // Validate board state
        int[][] board = state.getBoardState();
        if (board == null || board.length != 4 || board[0].length != 5) {
            return false;
        }

        // Validate move count
        if (state.getMoveCount() < 0) {
            return false;
        }

        // Validate timestamp (basic check)
        if (state.getTimestamp() <= 0) {
            return false;
        }

        // Additional validation can be added here
        // For example, check if the board contains valid block IDs

        return true;
    }

    /**
     * Checks if a file has valid game save extension
     * @param file the file to check
     * @return true if has valid extension
     */
    public static boolean hasValidExtension(File file) {
        return file != null && file.getName().toLowerCase().endsWith(".sav");
    }

    /**
     * Checks if a file is likely a valid game save file
     * @param file the file to check
     * @return true if likely valid
     */
    public static boolean isLikelyValidSave(File file) {
        return file != null &&
                file.exists() &&
                file.isFile() &&
                file.length() > 0 &&
                hasValidExtension(file);
    }
}