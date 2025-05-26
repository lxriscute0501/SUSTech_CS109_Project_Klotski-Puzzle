package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FireworksEffect extends JPanel {  // 改为继承JPanel
    private ArrayList<Particle> particles = new ArrayList<>();
    private Random random = new Random();
    private Timer animationTimer;
    private int width;
    private int height;

    public FireworksEffect(int width, int height) {
        this.width = width;
        this.height = height;
        setBackground(new Color(0, 0, 0, 0)); // 透明背景
        setPreferredSize(new Dimension(width, height));
        createInitialFireworks();

        // 设置动画定时器
        animationTimer = new Timer(16, e -> {
            updateParticles();
            repaint();
        });
        animationTimer.start();
    }

    private void createInitialFireworks() {
        for (int i = 0; i < 8; i++) {
            createFirework(random.nextInt(width), random.nextInt(height / 2));
        }
    }

    private void createFirework(int x, int y) {
        Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        for (int i = 0; i < 80; i++) {
            particles.add(new Particle(x, y, color));
        }
    }

    private void updateParticles() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update();
            if (p.isDead()) {
                particles.remove(i);
            }
        }

        // 随机添加新烟花
        if (random.nextInt(20) == 0) {
            createFirework(random.nextInt(width), random.nextInt(height / 2));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // 绘制半透明背景
        g2d.setColor(new Color(6, 253, 253, 255));
        g2d.fillRect(0, 0, width, height);

        // 绘制所有粒子
        for (Particle p : particles) {
            p.draw(g2d);
        }

        g2d.dispose();
    }

    private class Particle {
        double x, y;
        double vx, vy;
        Color color;
        int life;
        int maxLife = 60 + random.nextInt(40);

        Particle(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 2 + random.nextDouble() * 5;
            this.vx = Math.cos(angle) * speed;
            this.vy = Math.sin(angle) * speed;
            this.life = 0;
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.1; // 重力效果
            life++;
        }

        boolean isDead() {
            return life >= maxLife || x < 0 || x > width || y < 0 || y > height;
        }

        void draw(Graphics g) {
            float alpha = 1f - (float) life / maxLife;
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255)));
            int size = 3 + (int)(alpha * 5);
            g.fillOval((int)x, (int)y, size, size);
        }
    }
}