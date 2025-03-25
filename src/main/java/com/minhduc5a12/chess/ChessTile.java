package com.minhduc5a12.chess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

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

        if (isLeftClickSelected) {
            g.setColor(new Color(56, 72, 79, 160));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        if (piece != null) {
            drawPiece(g);
        }
        if (isValidMove) {
            g.setColor(new Color(255, 255, 255, 100));
            int circleX = (DEFAULT_TILE_SIZE - CIRCLE_SIZE) / 2;
            int circleY = (DEFAULT_TILE_SIZE - CIRCLE_SIZE) / 2;
            g.fillOval(circleX, circleY, CIRCLE_SIZE, CIRCLE_SIZE);
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
                    // Chọn ô có quân cờ
                    if (piece != null) {
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
}