package view.game;

import controller.GameController;
import model.MapModel;
import view.FrameUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {

    private GameController controller;
    private JButton restartBtn;
    private JButton loadBtn;
    private JButton saveBtn;
    private JLabel stepLabel;
    private GamePanel gamePanel;
    private int stepCount = 0;

    public GameFrame(int width, int height, MapModel mapModel) {
        this.setTitle("Welcome to Klotski Puzzle!");
        this.setLayout(null);
        this.setSize(width, height);

        // create the main game panel
        gamePanel = new GamePanel(mapModel);
        gamePanel.setLocation(30, height / 2 - gamePanel.getHeight() / 2);
        this.add(gamePanel);

        // connect the map model && vision graph
        this.controller = new GameController(gamePanel, mapModel);

        this.restartBtn = FrameUtil.createButton(this, "Restart", new Point(gamePanel.getWidth() + 80, 120), 80, 50);
        this.loadBtn = FrameUtil.createButton(this, "Load", new Point(gamePanel.getWidth() + 80, 210), 80, 50);
        this.saveBtn = FrameUtil.createButton(this, "Save", new Point(gamePanel.getWidth() + 80, 300), 80, 50);
        this.stepLabel = FrameUtil.createJLabel(this, "Start", new Font("serif", Font.ITALIC, 22), new Point(gamePanel.getWidth() + 80, 70), 180, 50);


        gamePanel.setStepLabel(stepLabel);

        // add ActionListener for step count
        gamePanel.addStepListener(e -> {
            stepCount ++;
            updateStepLabel();
        });

        this.restartBtn.addActionListener(e -> {
            controller.restartGame();
            resetStepCount();
            gamePanel.requestFocusInWindow(); // enable key listener
        });

        // ??what is load?
        this.loadBtn.addActionListener(e -> {
            String string = JOptionPane.showInputDialog(this, "Input path:");
            System.out.println("Save at:" + string);
            // 这里可以添加加载步数的逻辑
            gamePanel.requestFocusInWindow(); // enable key listener
        });

        //todo: add other button here
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void updateStepLabel() {
        stepLabel.setText("Steps: " + stepCount);
    }

    private void resetStepCount() {
        stepCount = 0;
        updateStepLabel();
    }

}
