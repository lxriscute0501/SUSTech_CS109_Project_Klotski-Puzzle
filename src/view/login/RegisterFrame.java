package view.login;

import view.FrameUtil;
import javax.swing.*;
import java.awt.*;
import java.util.Properties;

/**
 * Register a new account, including username, password, and confirm password.
 */

public class RegisterFrame extends JFrame {
    private final LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.setTitle("Register Frame");
        this.setLayout(null);
        this.setSize(400, 250);

        // create username label && input box
        JLabel usernameLabel = FrameUtil.createJLabel(this, new Point(50, 30), 100, 40, "username:");
        JTextField usernameField = FrameUtil.createJTextField(this, new Point(160, 30), 180, 40);

        // create password label && input box
        JLabel passwordLabel = FrameUtil.createJLabel(this, new Point(50, 80), 100, 40, "password:");
        JPasswordField passwordField = FrameUtil.createJPasswordField(this, new Point(160, 80), 180, 40);
        passwordField.setBounds(160, 80, 180, 40);
        this.add(passwordField);

        // create confirm password label && input box
        JLabel confirmLabel = FrameUtil.createJLabel(this, new Point(50, 130), 100, 40, "confirm:");
        JPasswordField confirmField = FrameUtil.createJPasswordField(this, new Point(160, 130), 180, 40);
        confirmField.setBounds(160, 130, 180, 40);
        this.add(confirmField);

        // create register button
        JButton registerBtn = FrameUtil.createButton(this, "Register", new Point(150, 180), 100, 40);
        registerBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            /** 3 cases:
             * 1. empty username or password or both
             * 2. username already exists
             * 3. confirm password != new password
             */

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username and password cannot be empty!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            else if (loginFrame.getUserProperty().containsKey(username)) {
                JOptionPane.showMessageDialog(this,
                        "Username already exists! Please choose a different one.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            else if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                // set new username and password
                Properties props = loginFrame.getUserProperty();
                props.setProperty(username, password);
                loginFrame.saveUserConfig();

                JOptionPane.showMessageDialog(this,
                        "Registration successful!",
                        username + ", welcome!",
                        JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            }
        });

        this.setLocationRelativeTo(loginFrame);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}