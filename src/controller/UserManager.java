package controller;

import model.User;
import java.io.*;
import java.util.Properties;

/**
 * Manages user data using Properties stored in user.config
 */
public class UserManager {
    private static final String CONFIG_FILE = "user.config";
    private static Properties props = new Properties();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
        } catch (IOException e) {
            System.out.println("No existing config. A new one will be created.");
        }
    }

    private static void saveConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            props.store(output, "User Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User loadUser(String username, String password) {
        String storedPassword = props.getProperty(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            return new User(username, password);
        }
        return null;
    }

    public static void saveUser(User user) {
        props.setProperty("user." + user.getUsername() + ".password", user.getPassword());
        saveConfig();
    }

    public static boolean userExists(String username) {
        return props.containsKey("user." + username + ".password");
    }
}
