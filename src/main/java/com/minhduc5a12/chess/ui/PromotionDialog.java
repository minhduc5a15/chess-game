package com.minhduc5a12.chess.ui;

import com.minhduc5a12.chess.constants.PieceColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

public class PromotionDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(PromotionDialog.class);
    private String selectedPiece = "Queen";
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();


    public PromotionDialog(Frame parent, PieceColor color) {
        super(parent, "Pawn Promotion", true);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 30));
        setResizable(false);
        setUndecorated(true);

        JPanel roundedPanel = getJPanel();

        JLabel messageLabel = new JLabel("Promote pawn to:", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setForeground(Color.WHITE);
        roundedPanel.add(messageLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton queenButton = createStyledButton("Queen", "images/" + (color == PieceColor.WHITE ? "white_queen.png" : "black_queen.png"));
        queenButton.addActionListener(e -> onPieceSelected("Queen"));
        buttonPanel.add(queenButton);

        JButton rookButton = createStyledButton("Rook", "images/" + (color == PieceColor.WHITE ? "white_rook.png" : "black_rook.png"));
        rookButton.addActionListener(e -> onPieceSelected("Rook"));
        buttonPanel.add(rookButton);

        JButton bishopButton = createStyledButton("Bishop", "images/" + (color == PieceColor.WHITE ? "white_bishop.png" : "black_bishop.png"));
        bishopButton.addActionListener(e -> onPieceSelected("Bishop"));
        buttonPanel.add(bishopButton);

        JButton knightButton = createStyledButton("Knight", "images/" + (color == PieceColor.WHITE ? "white_knight.png" : "black_knight.png"));
        knightButton.addActionListener(e -> onPieceSelected("Knight"));
        buttonPanel.add(knightButton);

        roundedPanel.add(buttonPanel, BorderLayout.CENTER);
        add(roundedPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
    }

    private static JPanel getJPanel() {
        JPanel roundedPanel = new JPanel() {
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
        roundedPanel.setLayout(new BorderLayout(10, 10));
        roundedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return roundedPanel;
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(50, 50, 50));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(70, 70, 70));
                } else {
                    g2d.setColor(new Color(60, 60, 60));
                }
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2d.setColor(Color.WHITE);
                FontMetrics metrics = g2d.getFontMetrics();
                int textX = (getWidth() - metrics.stringWidth(getText())) / 2;
                int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(80, 40);
            }
        };
        ImageIcon cachedIcon = iconCache.get(iconPath.substring("images/".length()));
        if (cachedIcon != null) {
            button.setIcon(cachedIcon);
        } else {
            logger.warn("Icon not found in cache for: {}", iconPath);
        }
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void onPieceSelected(String piece) {
        this.selectedPiece = piece;
        dispose();
    }

    public String getSelectedPiece() {
        return selectedPiece;
    }
}