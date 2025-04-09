package com.minhduc5a12.chess.ui;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;

public class PlayerPanel extends JPanel {
    private final static int FRAME_WIDTH = 250;
    private final static int FRAME_HEIGHT = 800;

    public PlayerPanel(String playerName, PieceColor pieceColor, String avatarPath) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(true);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        Image avatar = ImageLoader.getImage(avatarPath, 120, 120);
        JLabel avatarLabel = new JLabel(new ImageIcon(avatar));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setPreferredSize(new Dimension(120, 120));
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel colorLabel = new JLabel("Color: " + (pieceColor.isWhite() ? "White" : "Black"), SwingConstants.CENTER);
        colorLabel.setFont(new Font("Georgia", Font.ITALIC, 18));
        colorLabel.setForeground(new Color(245, 245, 220));
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(avatarLabel);
        add(Box.createVerticalStrut(25));
        add(nameLabel);
        add(Box.createVerticalStrut(15));
        add(colorLabel);
        add(Box.createVerticalGlue());

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradient = new GradientPaint(0, 0, new Color(139, 69, 19), getWidth(), getHeight(), new Color(92, 51, 23));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}