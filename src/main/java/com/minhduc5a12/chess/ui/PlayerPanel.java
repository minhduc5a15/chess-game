package com.minhduc5a12.chess.ui;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;

public class PlayerPanel extends JPanel {
    private final PieceColor pieceColor;

    public PlayerPanel(String playerName, PieceColor pieceColor, String avatarPath) {
        this.pieceColor = pieceColor;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setPreferredSize(new Dimension(250, 800));

        Image avatar = ImageLoader.getImage(avatarPath, 120, 120);
        JLabel avatarLabel = new JLabel(new ImageIcon(avatar));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setPreferredSize(new Dimension(120, 120));
        avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 0)); // Viền vuông

        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Roboto", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel colorLabel = new JLabel("Color: " + (pieceColor.isWhite() ? "White" : "Black"), SwingConstants.CENTER);
        colorLabel.setFont(new Font("Roboto", Font.ITALIC, 18));
        colorLabel.setForeground(Color.LIGHT_GRAY);
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public PieceColor getPieceColor() {
        return pieceColor;
    }
}