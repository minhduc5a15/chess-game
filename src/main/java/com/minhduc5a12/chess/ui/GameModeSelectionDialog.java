package com.minhduc5a12.chess.ui;

import com.minhduc5a12.chess.constants.GameMode;
import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

public class GameModeSelectionDialog extends JDialog {
    private int selectedMode = GameMode.PLAYER_VS_AI; // Mặc định
    private PieceColor selectedColor = PieceColor.WHITE;
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 600;

    public GameModeSelectionDialog(Frame parent) {
        super(parent, "Chọn chế độ chơi", true);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 30));
        setResizable(false);
        setUndecorated(true);

        JPanel roundedPanel = createRoundedPanel();
        roundedPanel.setLayout(new BorderLayout(10, 10));
        roundedPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        roundedPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        JLabel messageLabel = new JLabel("Chọn chế độ chơi:", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        messageLabel.setForeground(Color.WHITE);
        roundedPanel.add(messageLabel, BorderLayout.NORTH);

        JPanel buttonPanel = createButtonPanel();
        roundedPanel.add(buttonPanel, BorderLayout.CENTER);
        add(roundedPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
    }

    private JPanel createRoundedPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 40, 40), getWidth(), getHeight(), new Color(20, 20, 20));
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.setColor(new Color(80, 80, 80));
                g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2d.dispose();
            }
        };
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton pvpButton = createStyledButton("Người vs Người", "images/handshake.png");
        pvpButton.addActionListener(this::onPlayerVsPlayerSelected);
        JButton pvaiButton = createStyledButton("Người vs Máy", null);
        pvaiButton.addActionListener(this::onPlayerVsAISelected);
        JButton aivaiButton = createStyledButton("Máy vs Máy", null);
        aivaiButton.addActionListener(this::onAIVsAISelected);

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(pvpButton);
        pvpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(Box.createVerticalStrut(30));
        buttonPanel.add(pvaiButton);
        pvaiButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(Box.createVerticalStrut(30));
        buttonPanel.add(aivaiButton);
        aivaiButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(Box.createVerticalGlue());

        return buttonPanel;
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isPressed() ? new Color(50, 50, 50) :
                        getModel().isRollover() ? new Color(70, 70, 70) : new Color(60, 60, 60));
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2d.setColor(Color.WHITE);
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 50);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };

        if (iconPath != null) {
            try {
                java.net.URL imageUrl = ImageLoader.class.getClassLoader().getResource(iconPath);
                if (imageUrl == null) throw new IOException("Cannot find image: " + iconPath);
                button.setIcon(new ImageIcon(imageUrl));
            } catch (Exception e) {
                System.err.println("Failed to load icon: " + iconPath);
            }
        }

        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void onPlayerVsPlayerSelected(ActionEvent e) {
        selectedMode = GameMode.PLAYER_VS_PLAYER;
        dispose();
    }

    private void onPlayerVsAISelected(ActionEvent e) {
        selectedMode = GameMode.PLAYER_VS_AI;
        showColorSelectionDialog();
    }

    private void onAIVsAISelected(ActionEvent e) {
        selectedMode = GameMode.AI_VS_AI;
        dispose();
    }

    private void showColorSelectionDialog() {
        ColorSelectionDialog colorDialog = new ColorSelectionDialog(null); // Dùng null thay vì parentFrame
        colorDialog.setVisible(true);
        selectedColor = colorDialog.getSelectedColor();
        dispose();
    }

    public int getSelectedMode() {
        return selectedMode;
    }

    public PieceColor getSelectedColor() {
        return selectedColor;
    }
}