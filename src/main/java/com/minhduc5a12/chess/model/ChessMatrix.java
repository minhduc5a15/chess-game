package com.minhduc5a12.chess.model;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.pieces.*;

import java.util.HashMap;
import java.util.Map;

public class ChessMatrix {
    private final Map<ChessPosition, ChessPiece> chessPieceMap;

    public ChessMatrix() {
        this.chessPieceMap = new HashMap<>();
    }

    public boolean hasPiece(ChessPosition position) {
        if (!chessPieceMap.containsKey(position)) return false;
        ChessPiece piece = chessPieceMap.get(position);
        return piece != null;
    }

    public void setupInitialPosition() {
        clear();

        // Đặt quân trắng
        setPiece(0, 0, new Rook(PieceColor.WHITE));    // Xe trắng a1
        setPiece(1, 0, new Knight(PieceColor.WHITE));  // Mã trắng b1
        setPiece(2, 0, new Bishop(PieceColor.WHITE));  // Tượng trắng c1
        setPiece(3, 0, new Queen(PieceColor.WHITE));   // Hậu trắng d1
        setPiece(4, 0, new King(PieceColor.WHITE));    // Vua trắng e1
        setPiece(5, 0, new Bishop(PieceColor.WHITE));  // Tượng trắng f1
        setPiece(6, 0, new Knight(PieceColor.WHITE));  // Mã trắng g1
        setPiece(7, 0, new Rook(PieceColor.WHITE));    // Xe trắng h1
        for (int col = 0; col < 8; col++) {
            setPiece(col, 1, new Pawn(PieceColor.WHITE)); // Tốt trắng hàng 2
        }

        // Đặt quân đen
        setPiece(0, 7, new Rook(PieceColor.BLACK));    // Xe đen a8
        setPiece(1, 7, new Knight(PieceColor.BLACK));  // Mã đen b8
        setPiece(2, 7, new Bishop(PieceColor.BLACK));  // Tượng đen c8
        setPiece(3, 7, new Queen(PieceColor.BLACK));   // Hậu đen d8
        setPiece(4, 7, new King(PieceColor.BLACK));    // Vua đen e8
        setPiece(5, 7, new Bishop(PieceColor.BLACK));  // Tượng đen f8
        setPiece(6, 7, new Knight(PieceColor.BLACK));  // Mã đen g8
        setPiece(7, 7, new Rook(PieceColor.BLACK));    // Xe đen h8
        for (int col = 0; col < 8; col++) {
            setPiece(col, 6, new Pawn(PieceColor.BLACK)); // Tốt đen hàng 7
        }
    }

    public void clear() {
        this.chessPieceMap.clear();
    }

    public ChessPiece getPiece(ChessPosition position) {
        return chessPieceMap.get(position);
    }

    public void setPiece(ChessPosition position, ChessPiece piece) {
        chessPieceMap.put(position, piece);
    }

    public void setPiece(int x, int y, ChessPiece piece) {
        setPiece(new ChessPosition(x, y), piece);
    }

    public void removePiece(ChessPosition position) {
        chessPieceMap.remove(position);
    }

    public Map<ChessPosition, ChessPiece> getChessPieceMap() {
        return chessPieceMap;
    }

    public int countPiece(Class<? extends ChessPiece> pieceClass) {
        int count = 0;
        for (ChessPiece piece : chessPieceMap.values()) {
            if (pieceClass.isInstance(piece)) {
                count++;
            }
        }
        return count;
    }

    public void print() {
        for (Map.Entry<ChessPosition, ChessPiece> entry : chessPieceMap.entrySet()) {
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();
            System.out.println("Position: " + position + " Piece: " + piece);
        }
    }

    public int getDescartesRow(ChessPosition position) {
        return position.row();
    }

    public int getDescartesCol(ChessPosition position) {
        return position.col();
    }
}