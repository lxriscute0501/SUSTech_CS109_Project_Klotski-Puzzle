package controller;

import model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Manages user data: load, save, and cache current user.
 */
public class UserManager {
    private static final String DATA_FOLDER = "data";
    private static final String USER_DATA_FILE = "user_data.ser";

    /**
     * Loads a user from serialized file if exists, or creates a new one.
     */
    public static User loadUser(String username, String password) {
        String filePath = getUserDataFilePath(username);
        File file = new File(filePath);

        if (!file.exists()) {
            return new User(username, password);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            User user = (User) ois.readObject();
            if (user.getPassword().equals(password)) {
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Saves current user object to disk.
     */
    public static void saveUser(User user) {
        String filePath = getUserDataFilePath(user.getUsername());

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getUserDataFilePath(String username) {
        String userDir = DATA_FOLDER + "/" + username;
        new File(userDir).mkdirs();
        return userDir + "/" + USER_DATA_FILE;
    }
}