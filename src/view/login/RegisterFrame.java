package view.login;

import view.FrameUtil;
import javax.swing.*;
import java.awt.*;
import java.util.Properties;
import javax.swing.text.JTextComponent;


public class RegisterFrame extends JFrame {
    private final LoginFrame loginFrame;

    public static void styleInputField(JTextComponent field) {
        field.setOpaque(false);
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        field.setCaretColor(Color.BLACK);
    }

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.setTitle("Register Frame");
        this.setLayout(null);
        this.setSize(400, 300);

        JLabel usernameLabel = FrameUtil.createJLabel(this, new Point(50, 30), 100, 40, "username:");
        JTextField usernameField = FrameUtil.createJTextField(this, new Point(160, 30), 180, 40);
        RegisterFrame.styleInputField(usernameField);

        JLabel passwordLabel = FrameUtil.createJLabel(this, new Point(50, 80), 100, 40, "password:");
        JPasswordField passwordField = FrameUtil.createJPasswordField(this, new Point(160, 80), 180, 40);
        RegisterFrame.styleInputField(passwordField);


        JLabel confirmLabel = FrameUtil.createJLabel(this, new Point(50, 130), 100, 40, "confirm:");
        JPasswordField confirmField = FrameUtil.createJPasswordField(this, new Point(160, 130), 180, 40);
        RegisterFrame.styleInputField(confirmField);

        JButton registerBtn = createImageButton("/Buttons/register.png", "Register");
        int x = (this.getWidth() - 140) / 2;
        registerBtn.setBounds(x, 180, 140, 50);
        this.add(registerBtn);


        registerBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                showErrorDialog("Username and password cannot be empty!", "Error", "/Buttons/back.png");
            } else if (loginFrame.getUserProperty().containsKey(username)) {
                showErrorDialog("Username already exists! Please choose a different one.", "Error", "/Buttons/back.png");
            } else if (!password.equals(confirm)) {
                showErrorDialog("Passwords do not match!", "Error", "/Buttons/back.png");
            } else {
                Properties props = loginFrame.getUserProperty();
                props.setProperty(username, password);
                loginFrame.saveUserConfig();

                JOptionPane.showMessageDialog(this,
                        "Registration successful!",
                        username + ", welcome!", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            }
        });

        this.setLocationRelativeTo(loginFrame);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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

        int width = 100;
        int height = 50;

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
