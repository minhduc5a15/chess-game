package com.minhduc5a12.chess.pieces;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessPosition;

import java.util.HashMap;
import java.util.Map;

public class ChessPieceMap {
    private final Map<ChessPosition, ChessPiece> pieceMap;

    public ChessPieceMap() {
        this.pieceMap = new HashMap<>();
    }

    public ChessPiece getPiece(ChessPosition position) {
        return pieceMap.get(position);
    }

    public ChessPiece getPiece(String chessNotation) {
        return getPiece(ChessPosition.toChessPosition(chessNotation));
    }

    public void setPiece(ChessPosition position, ChessPiece piece) {
        pieceMap.put(position, piece);
    }

    public void setPiece(int x, int y, ChessPiece piece) {
        setPiece(new ChessPosition(x, y), piece);
    }

    public void removePiece(ChessPosition position) {
        pieceMap.remove(position);
    }

    public boolean hasPiece(ChessPosition position) {
        return pieceMap.containsKey(position) && pieceMap.get(position) != null;
    }

    public Map<ChessPosition, ChessPiece> getPieceMap() {
        return pieceMap;
    }

    public void clear() {
        pieceMap.clear();
    }

    public ChessPosition getKingPosition(PieceColor color) {
        for (Map.Entry<ChessPosition, ChessPiece> entry : pieceMap.entrySet()) {
            ChessPiece piece = entry.getValue();
            if (piece instanceof King && piece.getColor() == color) {
                return entry.getKey();
            }
        }
        return null;
    }
}