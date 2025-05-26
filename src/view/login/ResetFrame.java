package view.login;

import controller.User;
import controller.UserManager;
import javax.swing.*;
import java.awt.*;

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
        RegisterFrame.styleInputField(usernameField);
        this.add(usernameField);

        JLabel oldPasswordLabel = new JLabel("Old Password:");
        oldPasswordLabel.setBounds(50, 70, 100, 30);
        this.add(oldPasswordLabel);

        oldPasswordField = new JPasswordField();
        oldPasswordField.setBounds(200, 70, 150, 30);
        RegisterFrame.styleInputField(oldPasswordField);
        this.add(oldPasswordField);

        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setBounds(50, 110, 100, 30);
        this.add(newPasswordLabel);

        newPasswordField = new JPasswordField();
        newPasswordField.setBounds(200, 110, 150, 30);
        RegisterFrame.styleInputField(newPasswordField);
        this.add(newPasswordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setBounds(50, 150, 150, 30);
        this.add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(200, 150, 150, 30);
        RegisterFrame.styleInputField(confirmPasswordField);
        this.add(confirmPasswordField);

        submitBtn = createImageButton("/images/buttons/submit.png", "Submit");
        submitBtn.setBounds(80, 200, 120, 60);
        submitBtn.addActionListener(e -> handlePasswordReset());
        this.add(submitBtn);

        cancelBtn = createImageButton("/images/buttons/cancel.png", "Cancel");
        cancelBtn.setBounds(220, 200, 120, 60);
        cancelBtn.addActionListener(e -> this.dispose());
        this.add(cancelBtn);

        this.setLocationRelativeTo(parentFrame);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private void handlePasswordReset() {
        String username = usernameField.getText();
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showErrorDialog("All fields must be filled!", "Error", "/images/buttons/back.png");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showErrorDialog("New passwords do not match!", "Error", "/images/buttons/back.png");
            return;
        }

        User user = UserManager.loadUser(username, oldPassword);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or old password!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        user.setPassword(newPassword);
        if (UserManager.saveUser(user)) {
            JOptionPane.showMessageDialog(this,
                    "Password changed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            showErrorDialog("Failed to save new password. Please try again.", "Error", "/images/buttons/back.png");
        }
    }

    private JButton createImageButton(String resourcePath, String toolTipText) {
        java.net.URL imageUrl = getClass().getResource(resourcePath);
        if (imageUrl == null) {
            System.err.println("Could not load image at: " + resourcePath);
            JButton fallback = new JButton(toolTipText);
            fallback.setToolTipText(toolTipText);
            fallback.setPreferredSize(new Dimension(100, 50));
            return fallback;
        }

        int width = 120;
        int height = 60;

        ImageIcon originalIcon = new ImageIcon(imageUrl);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);
        button.setPreferredSize(new Dimension(width, height));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setToolTipText(toolTipText);
        return button;
    }

    private void showErrorDialog(String message, String title, String buttonImagePath) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel messageLabel = new JLabel("<html><body style='text-align:center;'>" + message.replace("\n", "<br>") + "</body></html>", SwingConstants.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        dialog.add(messageLabel, BorderLayout.CENTER);

        JButton backButton = createImageButton(buttonImagePath, "Back");
        backButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        buttonPanel.add(backButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}