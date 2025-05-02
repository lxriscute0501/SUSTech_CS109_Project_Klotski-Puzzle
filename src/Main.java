
import view.login.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(600,400);
            loginFrame.setVisible(true);
        });
    }
}
