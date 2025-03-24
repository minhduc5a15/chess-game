package com.minhduc5a12.chess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.pieces.ChessPiece;

public class ChessTile extends JPanel {
    private static final int DEFAULT_TILE_SIZE = 100;
    private static final Color LIGHT_COLOR = new Color(245, 245, 220);
    private static final Color DARK_COLOR = new Color(139, 69, 19);

    private final ChessPosition position;
    private ChessPiece piece;
    private final int tileSize;
    private final int pieceSize;

    public ChessTile(ChessPosition position, int tileSize) {
        this.position = position;
        this.tileSize = tileSize;
        this.pieceSize = (int) (tileSize * 0.95);
        setPreferredSize(new Dimension(tileSize, tileSize));
        setOpaque(false);
    }

    public ChessTile(ChessPosition position) {
        this(position, DEFAULT_TILE_SIZE);
    }

    public ChessTile(int col, int row) {
        this(new ChessPosition(col, row));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (piece != null) {
            drawPiece(g);
        }
    }

    private void drawPiece(Graphics g) {
        Image image = piece.getImage();
        if (image != null) {
            int offsetX = (tileSize - pieceSize) / 2;
            int offsetY = (tileSize - pieceSize) / 2;
            g.drawImage(image, offsetX, offsetY, pieceSize, pieceSize, null);
        } else {
            System.out.println("Warning: No image for piece at " + position.toChessNotation());
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

    public int getCol() {
        return position.col();
    }

    public int getRow() {
        return position.row();
    }
}