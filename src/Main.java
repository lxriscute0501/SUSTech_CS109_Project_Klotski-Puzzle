
import model.MapModel;
import view.game.GameFrame;
import view.login.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(300,200);
            loginFrame.setVisible(true);

            // CaoCao : 1; GuanYu : 2; General : 3; Soldier : 4; Empty : 5
            MapModel mapModel = new MapModel(new int[][]{
                    {1, 1, 3, 3, 5},
                    {1, 1, 3, 3, 5},
                    {2, 3, 3, 5, 5},
                    {2, 3, 3, 0, 0}
            });
            GameFrame gameFrame = new GameFrame(600, 450, mapModel);
            gameFrame.setVisible(false);
            loginFrame.setGameFrame(gameFrame);
        });
    }
}

