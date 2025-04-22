package view.login;

import view.FrameUtil;
import view.game.GameFrame;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class LoginFrame extends JFrame {
    private JTextField username;
    private JPasswordField password; // 改为密码字段
    private JButton submitBtn;
    private JButton resetBtn;
    private JButton setupBtn; // 新增设置密码按钮
    private GameFrame gameFrame;
    private static final String CONFIG_FILE = "user.config";
    private Properties userProps = new Properties();

    public LoginFrame(int width, int height) {
        this.setTitle("Login Frame");
        this.setLayout(null);
        this.setSize(width, height);

        // 加载用户配置
        loadUserConfig();

        JLabel userLabel = FrameUtil.createJLabel(this, new Point(50, 20), 70, 40, "username:");
        JLabel passLabel = FrameUtil.createJLabel(this, new Point(50, 80), 70, 40, "password:");
        username = FrameUtil.createJTextField(this, new Point(120, 20), 120, 40);
        password = new JPasswordField(); // 使用JPasswordField
        password.setBounds(120, 80, 120, 40);
        this.add(password);

        submitBtn = FrameUtil.createButton(this, "Login", new Point(40, 140), 80, 40);
        resetBtn = FrameUtil.createButton(this, "Reset", new Point(130, 140), 80, 40);
        setupBtn = FrameUtil.createButton(this, "Setup", new Point(220, 140), 80, 40);

        submitBtn.addActionListener(e -> {
            String inputUser = username.getText();
            String inputPass = new String(password.getPassword());

            // 检查是否是首次使用
            if (!userProps.containsKey("password")) {
                JOptionPane.showMessageDialog(this,
                        "Please setup password first!",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 验证登录信息
            if (inputUser.equals(userProps.getProperty("username", "admin")) &&
                    inputPass.equals(userProps.getProperty("password"))) {
                if (this.gameFrame != null) {
                    this.gameFrame.setVisible(true);
                    this.setVisible(false);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        resetBtn.addActionListener(e -> {
            username.setText("");
            password.setText("");
        });

        setupBtn.addActionListener(e -> {
            showPasswordSetupDialog();
        });

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void loadUserConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            userProps.load(input);
        } catch (IOException e) {
            // 文件不存在，使用默认配置
            userProps.setProperty("username", "admin");
        }
    }

    private void saveUserConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            userProps.store(output, "User Configuration");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to save configuration!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPasswordSetupDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JTextField newUserField = new JTextField(userProps.getProperty("username", "admin"));
        JPasswordField newPassField = new JPasswordField();
        JPasswordField confirmPassField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(newUserField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPassField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPassField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Setup Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newUser = newUserField.getText();
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (newUser.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username and password cannot be empty!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                userProps.setProperty("username", newUser);
                userProps.setProperty("password", newPass);
                saveUserConfig();
                JOptionPane.showMessageDialog(this,
                        "Password setup successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }


    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

}