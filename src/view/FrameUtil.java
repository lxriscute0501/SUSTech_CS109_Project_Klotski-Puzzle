package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * This class is to create basic JComponent.
 */
public class FrameUtil {
    public static JLabel createJLabel(JFrame frame, Point location, int width, int height, String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setSize(width, height);
        jLabel.setLocation(location);
        frame.add(jLabel);
        return jLabel;
    }

    public static JLabel createJLabel(JFrame frame, String name, Font font, Point location, int width, int height) {
        JLabel label = new JLabel(name);
        label.setFont(font);
        label.setLocation(location);
        label.setSize(width, height);
        frame.add(label);
        return label;
    }

    public static JTextField createJTextField(JFrame frame, Point location, int width, int height) {
        JTextField jTextField = new JTextField();
        jTextField.setSize(width, height);
        jTextField.setLocation(location);
        frame.add(jTextField);
        return jTextField;
    }

    public static JPasswordField createJPasswordField(JFrame frame, Point location, int width, int height) {
        JPasswordField jPasswordField = new JPasswordField();
        jPasswordField.setSize(width, height);
        jPasswordField.setLocation(location);
        frame.add(jPasswordField);
        return jPasswordField;
    }

    public static JButton createButton(JFrame frame, String name, Point location, int width, int height) {
        JButton button = new JButton(name);
        button.setLocation(location);
        button.setSize(width, height);
        frame.add(button);
        return button;
    }

    public static JButton createImageButton(String resourcePath, String toolTipText, int width, int height) {
        java.net.URL imageUrl = FrameUtil.class.getResource(resourcePath);
        if (imageUrl == null) {
            System.err.println("Could not load image at: " + resourcePath);
            JButton fallback = new JButton(toolTipText);
            fallback.setToolTipText(toolTipText);
            return fallback;
        }

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

    public static ImageIcon loadIcon(String path, int width, int height) {
        java.net.URL iconURL = FrameUtil.class.getResource(path);
        if (iconURL == null) {
            System.err.println("Icon not found: " + path);
            return new ImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        }

        ImageIcon icon = new ImageIcon(iconURL);
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}