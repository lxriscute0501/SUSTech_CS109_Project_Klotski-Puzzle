package model;

import util.SaveLoadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages user accounts without using HashMap
 */
public class UserManager {
    private static final String USERS_DIR = "resources/users/";
    private static final List<User> loadedUsers = new ArrayList<>();

    /**
     * Registers a new user
     */
    public static User registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        // Check if user already exists
        if (findUser(username) != null) {
            return null;
        }

        // Create user directory if not exists
        new File(USERS_DIR).mkdirs();

        User newUser = new User(username, password);
        saveUser(newUser);
        loadedUsers.add(newUser);
        return newUser;
    }

    /**
     * Logs in a user
     */
    public static User loginUser(String username, String password) {
        User user = findUser(username);
        if (user == null) {
            // Try loading from file if not in memory
            user = loadUserFromFile(username);
            if (user != null) {
                loadedUsers.add(user);
            }
        }

        if (user == null || user.isGuest()) {
            return null;
        }

        // Simple password check (in real app, compare hashes)
        if (password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    /**
     * Creates a guest user
     */
    public static User createGuestUser() {
        String guestName = "Guest_" + System.currentTimeMillis();
        User guest = new User(guestName);
        loadedUsers.add(guest);
        return guest;
    }

    /**
     * Saves user data to file
     */
    public static void saveUser(User user) {
        if (user == null || user.isGuest()) {
            return; // Don't save guest users
        }

        String filename = USERS_DIR + user.getUsername() + ".user";
        SaveLoadUtil.saveObject(user, filename);
    }

    /**
     * Finds a user in the loaded users list
     */
    private static User findUser(String username) {
        for (User user : loadedUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Loads a user from file
     */
    private static User loadUserFromFile(String username) {
        String filename = USERS_DIR + username + ".user";
        if (!new File(filename).exists()) {
            return null;
        }

        return (User) SaveLoadUtil.loadObject(filename);
    }

    /**
     * Checks if a user exists
     */
    public static boolean userExists(String username) {
        if (findUser(username) != null) {
            return true;
        }

        String filename = USERS_DIR + username + ".user";
        return new File(filename).exists();
    }
}