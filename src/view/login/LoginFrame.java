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
    private JPasswordField password; // display as dots
    private JButton submitBtn;
    private JButton resetBtn;
    private JButton registerBtn;
    private JButton guestBtn;
    private GameFrame gameFrame;
    private static final String CONFIG_FILE = "user.config";
    private Properties userProperty = new Properties();
    private boolean isGuest = false;

    public LoginFrame(int width, int height) {
        this.setTitle("Login Frame");
        this.setLayout(null);
        this.setSize(width, height);

        // load username && password
        loadUserConfig();

        // create username label && input box
        JLabel usernameLabel = FrameUtil.createJLabel(this, new Point(50, 20), 70, 40, "username:");
        username = FrameUtil.createJTextField(this, new Point(120, 20), 120, 40);

        // create password label && input box
        JLabel passwordLabel = FrameUtil.createJLabel(this, new Point(50, 80), 70, 40, "password:");
        password = new JPasswordField();
        password.setBounds(120, 80, 120, 40);

        // password do not use JTextField, so we need to invoke .add() specifically
        this.add(password);

        submitBtn = FrameUtil.createButton(this, "Login", new Point(40, 140), 80, 40);
        resetBtn = FrameUtil.createButton(this, "Reset", new Point(130, 140), 80, 40);
        registerBtn = FrameUtil.createButton(this, "Register", new Point(220, 140), 80, 40);
        guestBtn = FrameUtil.createButton(this, "Guest", new Point(310, 140), 80, 40);

        submitBtn.addActionListener(e -> {
            String inputUsername = username.getText();
            String inputPassword = new String(password.getPassword());

            // check whether password has been set
            if (!userProperty.containsKey("password")) {
                JOptionPane.showMessageDialog(this,
                        "Please register first!",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // verify username and password
            if (inputUsername.equals(userProperty.getProperty("username", "admin")) &&
                    inputPassword.equals(userProperty.getProperty("password"))) {
                // Correct! false means not guest
                enterGame(false);
            } else {
                // Wrong! show error message
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

        registerBtn.addActionListener(e -> {
            RegisterFrame registerFrame = new RegisterFrame(this);
            registerFrame.setVisible(true);
        });

        guestBtn.addActionListener(e -> {
            isGuest = true;
            enterGame(true);
            JOptionPane.showMessageDialog(this,
                    "You are entering as guest.\nGame progress will not be saved.",
                    "Guest Mode",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // set game location int the center
        this.setLocationRelativeTo(null);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void enterGame(boolean isGuest) {
        if (this.gameFrame != null) {
            //this.gameFrame.setGuestMode(isGuest);
            this.gameFrame.setVisible(true);
            this.setVisible(false);
        }
    }

    protected void loadUserConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            userProperty.load(input);
        } catch (IOException e) {
            // no file, use the initial username "admin"
            userProperty.setProperty("username", "admin");
        }
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

    protected Properties getUserProperty() {
        return userProperty;
    }

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }
}