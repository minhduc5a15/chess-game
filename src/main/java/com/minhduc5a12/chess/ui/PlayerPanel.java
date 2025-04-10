package com.minhduc5a12.chess.ui;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.pieces.ChessPiece;
import com.minhduc5a12.chess.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.util.TreeMap;

public class PlayerPanel extends JPanel {
    private final static int FRAME_WIDTH = 250;
    private final static int FRAME_HEIGHT = 800;
    private final static int AVATAR_WIDTH = 120;
    private final static int AVATAR_HEIGHT = 120;
    private final static int CAPTURED_PIECE_SIZE = 20;

    private final TreeMap<ChessPiece, Integer> capturedPieces;
    private final JPanel capturedPiecesPanel;
    private final JLabel scoreLabel;
    private boolean isActiveTurn;

    public PlayerPanel(String playerName, PieceColor pieceColor, String avatarPath) {
        this.isActiveTurn = pieceColor.isWhite();
        capturedPieces = new TreeMap<>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(true);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        Image avatar = ImageLoader.getImage(avatarPath, AVATAR_WIDTH, AVATAR_HEIGHT);
        JLabel avatarLabel = new JLabel(new ImageIcon(avatar));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setPreferredSize(new Dimension(AVATAR_WIDTH, AVATAR_HEIGHT));
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        capturedPiecesPanel = new JPanel();
        capturedPiecesPanel.setMaximumSize(new Dimension(FRAME_WIDTH, 2 * CAPTURED_PIECE_SIZE + 5));
        capturedPiecesPanel.setPreferredSize(new Dimension(FRAME_WIDTH, 2 * CAPTURED_PIECE_SIZE + 5));
        capturedPiecesPanel.setOpaque(false);
        capturedPiecesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, -4, 0));
        capturedPiecesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("Georgia", Font.PLAIN, 13));
        scoreLabel.setForeground(Color.WHITE);
        capturedPiecesPanel.add(scoreLabel);

        add(Box.createVerticalGlue());
        add(avatarLabel);
        add(Box.createVerticalStrut(25));
        add(nameLabel);
        add(Box.createVerticalStrut(5));
        add(capturedPiecesPanel);
        add(Box.createVerticalGlue());

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public void addCapturedPiece(ChessPiece piece) {
        capturedPieces.merge(piece, 1, Integer::sum);
        updateCapturedPiecesDisplay();
    }

    private void updateCapturedPiecesDisplay() {
        capturedPiecesPanel.removeAll();

        for (var entry : capturedPieces.entrySet()) {
            ChessPiece piece = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                Image pieceImage = piece.getImage();
                if (pieceImage != null) {
                    Image scaledImage = pieceImage.getScaledInstance(CAPTURED_PIECE_SIZE, CAPTURED_PIECE_SIZE, Image.SCALE_SMOOTH);
                    JLabel pieceLabel = new JLabel(new ImageIcon(scaledImage));
                    capturedPiecesPanel.add(pieceLabel);
                }
            }
        }

        capturedPiecesPanel.add(scoreLabel);
        capturedPiecesPanel.revalidate();
        capturedPiecesPanel.repaint();
    }

    public void updateScore(int score) {
        String scoreText;
        if (score > 0) {
            scoreText = "+" + score;
        } else {
            scoreText = "";
        }
        scoreLabel.setText(scoreText);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradient = new GradientPaint(0, 0, new Color(89, 45, 13), getWidth(), getHeight(), new Color(138, 66, 17));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        if (isActiveTurn) {
            g2d.setColor(new Color(255, 255, 0, 100));
            g2d.setStroke(new BasicStroke(5));
            g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
        }
        g2d.dispose();
    }

    public void setActiveTurn(boolean isActiveTurn) {
        this.isActiveTurn = isActiveTurn;
        repaint();
    }
}