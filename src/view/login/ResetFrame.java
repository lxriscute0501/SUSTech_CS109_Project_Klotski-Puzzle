package view.login;

import javax.swing.*;
import java.util.Properties;

/**
 * Reset the password of one user, including username, old password, new password, confirm new password.
 */

public class ResetFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    private JButton submitBtn;
    private JButton cancelBtn;

    private LoginFrame parentFrame;

    public ResetFrame(LoginFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.setTitle("Reset Password");
        this.setLayout(null);
        this.setSize(400, 300);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 30, 100, 30);
        this.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(200, 30, 150, 30);
        this.add(usernameField);

        JLabel oldPasswordLabel = new JLabel("Old Password:");
        oldPasswordLabel.setBounds(50, 70, 100, 30);
        this.add(oldPasswordLabel);

        oldPasswordField = new JPasswordField();
        oldPasswordField.setBounds(200, 70, 150, 30);
        this.add(oldPasswordField);

        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setBounds(50, 110, 100, 30);
        this.add(newPasswordLabel);

        newPasswordField = new JPasswordField();
        newPasswordField.setBounds(200, 110, 150, 30);
        this.add(newPasswordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setBounds(50, 150, 150, 30);
        this.add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(200, 150, 150, 30);
        this.add(confirmPasswordField);

        submitBtn = new JButton("Submit");
        submitBtn.setBounds(80, 200, 100, 30);
        submitBtn.addActionListener(e -> handlePasswordReset());
        this.add(submitBtn);

        cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(220, 200, 100, 30);
        cancelBtn.addActionListener(e -> this.dispose());
        this.add(cancelBtn);

        this.setLocationRelativeTo(parentFrame);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /** 3 cases:
     * 1. empty fields
     * 2. new password != confirm password
     * 3. wrong username or old password
     * Note. old password = new password is allowed
     */
    private void handlePasswordReset() {
        String username = usernameField.getText();
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields must be filled!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "New passwords do not match!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Properties userProperty = parentFrame.getUserProperty();
        String storedPassword = userProperty.getProperty(username);

        if (storedPassword == null || !storedPassword.equals(oldPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or old password!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // reset username and password
        userProperty.setProperty(username, newPassword);
        parentFrame.saveUserConfig();

        JOptionPane.showMessageDialog(this,
                "Password changed successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        this.dispose();
    }
}