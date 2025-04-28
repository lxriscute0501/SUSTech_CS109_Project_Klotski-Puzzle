package util;

import model.GameState;
import java.io.*;
import java.nio.file.*;

/**
 * Utility class for saving and loading objects and game states.
 */
public class SaveLoadUtil {
    private static final String SAVE_DIR = "resources/users/";
    private static final String TEMP_SUFFIX = ".tmp";

    /**
     * Saves any serializable object to file
     * @param object the object to save
     * @param filename the target filename
     * @throws IOException if saving fails
     */
    public static void saveObject(Serializable object, String filename) throws IOException {
        if (object == null || filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Invalid arguments for saveObject");
        }

        // Ensure directory exists
        Path dirPath = Paths.get(SAVE_DIR);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Save to temp file first
        Path tempPath = Paths.get(filename + TEMP_SUFFIX);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(tempPath))) {
            oos.writeObject(object);
        }

        // Then atomically rename to target file
        Path targetPath = Paths.get(filename);
        Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Loads any serializable object from file
     * @param filename the file to load from
     * @return the loaded object
     * @throws IOException if file operations fail
     * @throws ClassNotFoundException if serialization fails
     */
    public static Object loadObject(String filename) throws IOException, ClassNotFoundException {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(filename)))) {
            return ois.readObject();
        }
    }

    /**
     * Saves a game state to file
     * @param state the game state to save
     * @param filename the target filename
     * @throws IOException if saving fails
     */
    public static void saveGameState(GameState state, String filename) throws IOException {
        saveObject(state, filename); // Reuse the generic save method
    }

    /**
     * Loads a game state from file
     * @param filename the file to load from
     * @return the loaded GameState
     * @throws IOException if file operations fail
     * @throws ClassNotFoundException if serialization fails
     */
    public static GameState loadGameState(String filename) throws IOException, ClassNotFoundException {
        Object obj = loadObject(filename);
        if (!(obj instanceof GameState)) {
            throw new IOException("Invalid game state format");
        }
        return (GameState) obj;
    }

    /**
     * Deletes a saved file
     * @param filename the file to delete
     * @return true if successfully deleted
     */
    public static boolean deleteSaveFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }

        try {
            return Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets the save file path for a user
     * @param username the username
     * @return full path to the save file
     */
    public static String getUserSavePath(String username) {
        return SAVE_DIR + username + ".sav";
    }

    /**
     * Gets the user data file path
     * @param username the username
     * @return full path to the user file
     */
    public static String getUserDataPath(String username) {
        return SAVE_DIR + username + ".user";
    }

    /**
     * Checks if a user has a saved game
     * @param username the username to check
     * @return true if save exists
     */
    public static boolean hasSavedGame(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return Files.exists(Paths.get(getUserSavePath(username)));
    }
}