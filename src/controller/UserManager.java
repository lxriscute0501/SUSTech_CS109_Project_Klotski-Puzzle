package controller;

import model.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Manages user authentication using user.config file
 */
public class UserManager {
    private static final String DATA_FOLDER = "data";
    private static final String CONFIG_FILE = "user.config";

    public static User loadUser(String username, String password) {
        Path configPath = Paths.get(DATA_FOLDER, CONFIG_FILE);

        if (!Files.exists(configPath)) {
            return null; // 配置文件不存在
        }

        try {
            List<String> lines = Files.readAllLines(configPath);
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 2 &&
                        parts[0].trim().equals(username.trim()) &&
                        parts[1].trim().equals(password.trim())) {
                    return new User(username, password); // 验证成功返回用户对象
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // 用户名或密码不匹配
    }

    public static void saveUser(User user) {
        // 现在这个方法不需要实现，因为所有用户信息都存储在user.config中
        // 如果需要添加新用户，需要直接编辑user.config文件
    }
}