package com.minhduc5a12.chess.pieces;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMatrix;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class King extends ChessPiece {
    public King(PieceColor color) {
        super(color, color == PieceColor.WHITE ? "white_king.png" : "black_king.png");
        setPieceValue(0);
    }

    @Override
    public List<ChessMove> generateValidMoves(ChessPosition start, ChessMatrix chessMatrix) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = start.row();
        int startCol = start.col();

        // 8 hướng xung quanh
        int[][] offsets = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] offset : offsets) {
            int newCol = startCol + offset[0];
            int newRow = startRow + offset[1];
            if (newCol >= 0 && newCol <= 7 && newRow >= 0 && newRow <= 7) {
                ChessPosition pos = new ChessPosition(newCol, newRow);
                if (!chessMatrix.hasPiece(pos) || (chessMatrix.hasPiece(pos) && chessMatrix.getPiece(pos).getColor() != getColor())) {
                    moves.add(new ChessMove(start, pos));
                }
            }
        }

        return moves;
    }
}