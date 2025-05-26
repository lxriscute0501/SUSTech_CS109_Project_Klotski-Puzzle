package controller;

import java.io.*;

public class UserManager {
    private static final String USER_DATA_DIR = "userdata/";

    public static boolean userExists(String username) {
        File userFile = new File(USER_DATA_DIR + username + ".dat");
        return userFile.exists();
    }

    public static User loadUser(String username, String password) {
        File userFile = new File(USER_DATA_DIR + username + ".dat");
        if (userFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
                User user = (User) ois.readObject();
                if (user.getPassword().equals(password)) {
                    return user;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean saveUser(User user) {
        File dir = new File(USER_DATA_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(USER_DATA_DIR + user.getUsername() + ".dat"))) {
            oos.writeObject(user);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}