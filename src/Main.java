
import model.MapModel;
import view.game.GameFrame;
import view.login.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(600,400);
            loginFrame.setVisible(true);

            /**
             * CaoCao : 1
             * GuanYu : 2
             * General : 3
             * Soldier : 4
             * Empty : 0
             */
            MapModel mapModel = new MapModel(new int[][]{
                    {1, 1, 3, 3, 4},
                    {1, 1, 3, 3, 4},
                    {2, 3, 3, 4, 4},
                    {2, 3, 3, 0, 0}
            });
            GameFrame gameFrame = new GameFrame(500, 400, mapModel);
            gameFrame.setVisible(false);
            loginFrame.setGameFrame(gameFrame);
        });
    }
}

