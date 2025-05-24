package view.login;

import controller.UserManager;
import controller.User;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;

/**
 * The initial frame, containing login, reset, register and guest button.
 * Connect RegisterFrame with register button, and StartMenuFrame with login && guest button.
 */
public class LoginFrame extends JFrame {
    private JTextField username;
    private JPasswordField password; // display as dots

    private JButton loginBtn;
    private JButton resetBtn;
    private JButton registerBtn;
    private JButton guestBtn;

    private StartMenuFrame startMenuFrame;

    private static final String CONFIG_FILE = "user.config";
    private Properties userProperty = new Properties();

    public LoginFrame(int width, int height) {
        this.setTitle("Login Frame");
        this.setLayout(new GridBagLayout());
        this.setSize(width, height);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        loadUserConfig();
        drawButton();

        loginBtn.addActionListener(e -> {
            String inputUsername = username.getText();
            String inputPassword = new String(password.getPassword());

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                warningDialog("Please enter both username and password!",
                        "Input Required", "/Buttons/back.png");
                return;
            }

            User user = UserManager.loadUser(inputUsername, inputPassword);
            if (user != null) {
                enterStartMenuFrame(user, false);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerBtn.addActionListener(e -> {
            RegisterFrame registerFrame = new RegisterFrame(this);
            registerFrame.setVisible(true);
        });

        resetBtn.addActionListener(e -> {
            ResetFrame resetFrame = new ResetFrame(this);
            resetFrame.setVisible(true);
        });

        guestBtn.addActionListener(e -> {
            // Create custom dialog
            JDialog dialog = new JDialog(this, "Guest Mode", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(this);

            // Message label
            JLabel message = new JLabel("<html>You are entering as guest.<br>Game progress will not be saved.</html>", SwingConstants.CENTER);
            message.setFont(new Font("Arial", Font.PLAIN, 14));
            dialog.add(message, BorderLayout.CENTER);

            // Buttons Panel
            JPanel GuestButtonPanel = new JPanel();
            JButton continueBtn = createImageButton("/Buttons/next.png", "Continue as Guest");
            JButton cancelBtn = createImageButton("/Buttons/cancel.png", "Cancel");

            GuestButtonPanel.add(continueBtn);
            GuestButtonPanel.add(cancelBtn);
            dialog.add(GuestButtonPanel, BorderLayout.SOUTH);

            // Button Actions
            continueBtn.addActionListener(ev -> {
                dialog.dispose();
                User guest = new User("Guest");
                enterStartMenuFrame(guest, true);
            });

            cancelBtn.addActionListener(ev -> {
                dialog.dispose(); //  close the dialog
            });

            dialog.setVisible(true);
        });


        // set login frame in the center
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void drawButton() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Welcome to the Game!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subheadingLabel = new JLabel("Please log in to continue", SwingConstants.CENTER);
        subheadingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(subheadingLabel, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy++;

        JLabel usernameLabel = new JLabel("Username:");
        this.add(usernameLabel, gbc);

        // Username Field
        gbc.gridx = 1;
        username = new JTextField(15);
        RegisterFrame.styleInputField(username);
        this.add(username, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        this.add(passwordLabel, gbc);

        gbc.gridx = 1;
        password = new JPasswordField(15);
        RegisterFrame.styleInputField(password);
        this.add(password, gbc);


        // Buttons Row
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginBtn = createImageButton("/Buttons/Login.png", "Login");
        resetBtn = createImageButton("/Buttons/reset.png", "Reset");
        registerBtn = createImageButton("/Buttons/register.png", "Register");
        guestBtn = createImageButton("/Buttons/guest.png", "Guest");

        buttonPanel.add(loginBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(registerBtn);
        buttonPanel.add(guestBtn);

        this.add(buttonPanel, gbc);
    }

    private JButton createImageButton(String resourcePath, String toolTipText) {
        java.net.URL imageUrl = getClass().getResource(resourcePath);
        if (imageUrl == null) {
            System.err.println("Could not load image at: " + resourcePath);
            JButton fallback = new JButton(toolTipText);
            fallback.setToolTipText(toolTipText);
            return fallback;
        }

        ImageIcon originalIcon = new ImageIcon(imageUrl);
        int desiredWidth = 120;
        int desiredHeight = 60;

        Image scaledImage = originalIcon.getImage().getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);
        button.setPreferredSize(new Dimension(desiredWidth, desiredHeight));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setToolTipText(toolTipText);
        return button;
    }

    private void warningDialog(String message, String title, String buttonImagePath) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout(10, 10)); // with hgap and vgap

        // Message label with padding and multiline support
        JLabel messageLabel = new JLabel("<html><body style='text-align:center;'>" + message.replace("\n", "<br>") + "</body></html>", SwingConstants.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        dialog.add(messageLabel, BorderLayout.CENTER);

        // Load button icon and scale it to match your other buttons (e.g. 80x40)
        java.net.URL imageUrl = getClass().getResource(buttonImagePath);
        JButton okButton;
        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 40, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            okButton = new JButton(scaledIcon);
            okButton.setContentAreaFilled(false);
            okButton.setBorderPainted(false);
            okButton.setFocusPainted(false);
        } else {
            okButton = new JButton("OK");
        }

        okButton.setPreferredSize(new Dimension(80, 40));
        okButton.setToolTipText("OK");
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void enterStartMenuFrame(User newUser, boolean isGuest) {
        this.startMenuFrame = new StartMenuFrame(600, 400, isGuest, newUser);
        this.startMenuFrame.setVisible(true);
        this.setVisible(false);
    }

    protected void saveUserConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            // store configuration to file
            userProperty.store(output, "User Configuration");
        } catch (IOException e) {
            // invalid (i.e. empty) configuration
            JOptionPane.showMessageDialog(this,
                    "Failed to save configuration!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void loadUserConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            userProperty.load(input);
        } catch (IOException e) {
            // if no file, use the initial username "admin"
            userProperty.setProperty("username", "admin");
            saveUserConfig();
        }
    }

    protected Properties getUserProperty() {
        return userProperty;
    }
}