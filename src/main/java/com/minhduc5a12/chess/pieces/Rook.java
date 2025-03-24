package com.minhduc5a12.chess.pieces;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMatrix;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class Rook extends ChessPiece {
    public Rook(PieceColor color) {
        super(color, color == PieceColor.WHITE ? "white_rook.png" : "black_rook.png");
        setPieceValue(5);
    }

    @Override
    public List<ChessMove> generateValidMoves(ChessPosition start, ChessMatrix chessMatrix) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = start.row();
        int startCol = start.col();

        // 4 hướng: lên, xuống, trái, phải
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : directions) {
            int dCol = dir[0];
            int dRow = dir[1];
            int newCol = startCol + dCol;
            int newRow = startRow + dRow;

            while (newCol >= 0 && newCol <= 7 && newRow >= 0 && newRow <= 7) {
                ChessPosition pos = new ChessPosition(newCol, newRow);
                if (!chessMatrix.hasPiece(pos)) {
                    moves.add(new ChessMove(start, pos));
                } else {
                    if (chessMatrix.getPiece(pos).getColor() != getColor()) {
                        moves.add(new ChessMove(start, pos));
                    }
                    break;
                }
                newCol += dCol;
                newRow += dRow;
            }
        }

        return moves;
    }
}