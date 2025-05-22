package view.game;

import javax.swing.*;
import java.awt.*;

public class ObstacleComponent extends JComponent {
    private final int row, col;

    public ObstacleComponent(int row, int col) {
        this.row = row;
        this.col = col;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(new Color(100, 100, 100, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0, 0, getWidth(), getHeight());
        g2d.drawLine(getWidth(), 0, 0, getHeight());
    }
}