package com.minhduc5a12.chess.pieces;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.*;
import com.minhduc5a12.chess.utils.BoardUtils;
import com.minhduc5a12.chess.utils.ImageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public abstract class ChessPiece {
    private static final Logger logger = LoggerFactory.getLogger(ChessPiece.class);
    private final PieceColor color;
    private final String imagePath;
    private final Image image;
    private int pieceValue = 0;
    private boolean hasMoved = false;

    public ChessPiece(PieceColor color, String imagePath) {
        this.color = color;
        this.imagePath = "images/" + imagePath;
        logger.debug("Loading image for piece: {}", this.imagePath);
        this.image = ImageLoader.getImage(this.imagePath, 95, 95);
        if (this.image == null) {
            logger.error("Failed to load image for piece: {}", this.imagePath);
        } else {
            logger.info("Image loaded successfully for piece: {}", this.imagePath);
        }
    }

    public Image getImage() {
        return image;
    }

    public PieceColor getColor() {
        return color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public void setPieceValue(int value) {
        this.pieceValue = value;
    }

    public int getPieceValue() {
        return pieceValue;
    }

    public abstract List<ChessMove> generateValidMoves(ChessPosition start, ChessPieceMap pieceMap);

    public boolean isValidMove(ChessMove move, ChessPieceMap pieceMap) {
        List<ChessMove> moves = generateValidMoves(move.start(), pieceMap);

        for (ChessMove chessMove: moves) {
            ChessPieceMap tempMap = BoardUtils.simulateMove(chessMove, pieceMap);
            if (!BoardUtils.isKingInCheck(this.getColor(), tempMap)) {
                if (chessMove.equals(move)) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getImagePath() {
        return imagePath;
    }
}