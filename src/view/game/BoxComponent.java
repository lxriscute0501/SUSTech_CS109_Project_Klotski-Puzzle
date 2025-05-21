package view.game;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class BoxComponent extends JComponent {
    private Image image;
    private Color color;
    private int row;
    private int col;
    private boolean isSelected;

    public BoxComponent(Image image, int row, int col, Color color) {
        this.image = image;
        this.row = row;
        this.col = col;
        this.color = color;
        this.setOpaque(false);
        isSelected = false;
    }

    // highlight chosen block border
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

        Border border;
        if (isSelected) {
            border = BorderFactory.createLineBorder(Color.red, 3);
        } else {
            border = BorderFactory.createLineBorder(color, 1);
        }
        this.setBorder(border);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        this.repaint();
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
