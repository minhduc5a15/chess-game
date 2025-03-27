package com.minhduc5a12.chess;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.*;

import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.pieces.ChessPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChessTile extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(ChessTile.class);
    private static final int DEFAULT_TILE_SIZE = 100;
    private static final int CIRCLE_SIZE = 30;

    private final ChessPosition position;
    private ChessPiece piece;
    private final int tileSize;
    private final int pieceSize;
    private boolean isLeftClickSelected;
    private boolean isValidMove;
    private final GameController gameController;

    public ChessTile(ChessPosition position, int tileSize, GameController gameController) {
        this.position = position;
        this.tileSize = tileSize;
        this.pieceSize = 95;
        this.isLeftClickSelected = this.isValidMove = false;
        this.gameController = gameController;
        setPreferredSize(new Dimension(tileSize, tileSize));
        setOpaque(false);
        addMouseListener(new ChessTileMouseListener());
        addMouseMotionListener(new ChessTileMouseMotionListener());
    }

    public ChessTile(ChessPosition position, GameController gameController) {
        this(position, DEFAULT_TILE_SIZE, gameController);
    }

    public ChessTile(int col, int row, GameController gameController) {
        this(new ChessPosition(col, row), gameController);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (isLeftClickSelected) {
            g2d.setColor(new Color(56, 72, 79, 160));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        if (piece != null) {
            drawPiece(g);
        }
        if (isValidMove) {
            if (piece != null) {
                g2d.setColor(new Color(222, 47, 31, 150));
                g2d.setStroke(new BasicStroke(9));
                int circleX = (DEFAULT_TILE_SIZE - 90) / 2;
                int circleY = (DEFAULT_TILE_SIZE - 90) / 2;
                g2d.drawOval(circleX, circleY, 90, 90);
                g2d.setStroke(new BasicStroke(1));
            } else {
                g2d.setColor(new Color(255, 255, 255, 100));
                int circleX = (DEFAULT_TILE_SIZE - CIRCLE_SIZE) / 2;
                int circleY = (DEFAULT_TILE_SIZE - CIRCLE_SIZE) / 2;
                g2d.fillOval(circleX, circleY, CIRCLE_SIZE, CIRCLE_SIZE);
            }
        }
    }

    private void drawPiece(Graphics g) {
        Image image = piece.getImage();
        if (image != null) {
            int offsetX = (tileSize - pieceSize) / 2;
            int offsetY = (tileSize - pieceSize) / 2;
            g.drawImage(image, offsetX, offsetY, pieceSize, pieceSize, null);
        } else {
            logger.warn("No image for piece at position: {}", position.toChessNotation());
        }
    }

    public ChessPosition getPosition() {
        return position;
    }

    public String getChessNotation() {
        return position.toChessNotation();
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public void setPiece(ChessPiece piece) {
        this.piece = piece;
        repaint();
    }

    public void setLeftClickSelected(boolean leftClickSelected) {
        this.isLeftClickSelected = leftClickSelected;
        repaint();
    }

    public void setValidMove(boolean validMove) {
        this.isValidMove = validMove;
        repaint();
    }

    public boolean isLeftClickSelected() {
        return isLeftClickSelected;
    }

    public boolean isValidMove() {
        return isValidMove;
    }

    public int getCol() {
        return position.col();
    }

    public int getRow() {
        return position.row();
    }

    private class ChessTileMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                ChessTile selectedTile = gameController.getCurrentLeftClickedTile();
                if (selectedTile == null) {
                    // Chọn ô có quân cờ, chỉ khi quân cờ thuộc về người chơi hiện tại
                    if (piece != null && piece.getColor() == gameController.getCurrentPlayerColor()) {
                        gameController.setCurrentLeftClickedTile(ChessTile.this);
                        logger.debug("Selected piece at: {}", position.toChessNotation());
                    }
                } else {
                    // Di chuyển quân bằng ChessMove
                    ChessMove move = new ChessMove(selectedTile.getPosition(), position);
                    if (gameController.movePiece(move)) {
                        logger.debug("Moved piece from {} to {}", move.start().toChessNotation(), move.end().toChessNotation());
                        gameController.setCurrentLeftClickedTile(null);
                    }
                }
            }
        }
    }

    private class ChessTileMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            if (piece != null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }

            if (isValidMove) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }

            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}