package com.minhduc5a12.chess.pieces;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMatrix;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends ChessPiece {
    public Bishop(PieceColor color) {
        super(color, color == PieceColor.WHITE ? "white_bishop.png" : "black_bishop.png");
        setPieceValue(3);
    }

    @Override
    public List<ChessMove> generateValidMoves(ChessPosition start, ChessMatrix chessMatrix) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = start.row();
        int startCol = start.col();

        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
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