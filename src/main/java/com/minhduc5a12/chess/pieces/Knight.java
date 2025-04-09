package com.minhduc5a12.chess.pieces;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class Knight extends ChessPiece {
    public Knight(PieceColor color) {
        super(color, color.isWhite() ? "white_knight.png" : "black_knight.png");
        this.pieceValue = 3;
    }

    @Override
    public List<ChessMove> generateValidMoves(ChessPosition start, ChessPieceMap pieceMap) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = start.row();
        int startCol = start.col();

        // 8 nước đi chữ L: (±1, ±2) hoặc (±2, ±1)
        int[][] offsets = {
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };

        for (int[] offset : offsets) {
            int newCol = startCol + offset[0];
            int newRow = startRow + offset[1];
            if (newCol >= 0 && newCol <= 7 && newRow >= 0 && newRow <= 7) {
                ChessPosition pos = new ChessPosition(newCol, newRow);
                if (!pieceMap.hasPiece(pos) ||
                        (pieceMap.hasPiece(pos) && pieceMap.getPiece(pos).getColor() != getColor())) {
                    moves.add(new ChessMove(start, pos));
                }
            }
        }

        return moves;
    }

    @Override
    public String getPieceNotation() {
        return this.getColor().isWhite() ? "N" : "n";
    }
}