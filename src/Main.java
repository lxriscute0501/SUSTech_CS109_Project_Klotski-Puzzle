
import model.MapModel;
import view.game.GameFrame;
import view.login.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MapModel mapModel = new MapModel(new int[4][5]);
            GameFrame gameFrame = new GameFrame(900, 600, mapModel);
            gameFrame.setVisible(false);
            LoginFrame loginFrame = new LoginFrame(600,400, gameFrame);
            loginFrame.setVisible(true);
        });
    }
}
