package view.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public abstract class ListenerPanel extends JPanel {
    public ListenerPanel() {
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        this.setFocusable(true);
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT -> doMoveRight();
                case KeyEvent.VK_LEFT -> doMoveLeft();
                case KeyEvent.VK_UP -> doMoveUp();
                case KeyEvent.VK_DOWN -> doMoveDown();
            }
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        switch (e.getID()) {
            case MouseEvent.MOUSE_CLICKED -> {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    doLeftClick(e.getPoint());
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    doRightClick(e.getPoint());
                }
            }
            case MouseEvent.MOUSE_PRESSED -> doMousePressed(e.getPoint());
            case MouseEvent.MOUSE_RELEASED -> doMouseReleased(e.getPoint());
            case MouseEvent.MOUSE_ENTERED -> doMouseEntered();
            case MouseEvent.MOUSE_EXITED -> doMouseExited();
            case MouseEvent.MOUSE_DRAGGED -> doMouseDragged(e.getPoint());
            case MouseEvent.MOUSE_MOVED -> doMouseMoved(e.getPoint());
        }
    }

    // Keyboard control methods
    public abstract void doMoveRight();
    public abstract void doMoveLeft();
    public abstract void doMoveUp();
    public abstract void doMoveDown();

    // Mouse control methods
    public abstract void doLeftClick(Point point);
    public abstract void doRightClick(Point point);
    public abstract void doMousePressed(Point point);
    public abstract void doMouseReleased(Point point);
    public abstract void doMouseEntered();
    public abstract void doMouseExited();
    public abstract void doMouseDragged(Point point);
    public abstract void doMouseMoved(Point point);
}