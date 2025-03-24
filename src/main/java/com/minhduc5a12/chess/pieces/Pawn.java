package com.minhduc5a12.chess.pieces;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMatrix;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends ChessPiece {
    public Pawn(PieceColor color) {
        super(color, color == PieceColor.WHITE ? "white_pawn.png" : "black_pawn.png");
        setPieceValue(1);
    }

    @Override
    public List<ChessMove> generateValidMoves(ChessPosition start, ChessMatrix chessMatrix) {
        List<ChessMove> moves = new ArrayList<>();
        int direction = (getColor() == PieceColor.WHITE) ? 1 : -1;
        int startRow = start.row();
        int startCol = start.col();

        int newRow = startRow + direction;
        if (newRow >= 0 && newRow <= 7) {
            ChessPosition forward = new ChessPosition(startCol, newRow);
            if (!chessMatrix.hasPiece(forward)) {
                moves.add(new ChessMove(start, forward));
                if (!hasMoved() && ((getColor() == PieceColor.WHITE && startRow == 1) || (getColor() == PieceColor.BLACK && startRow == 6))) {
                    ChessPosition twoForward = new ChessPosition(startCol, startRow + 2 * direction);
                    if (!chessMatrix.hasPiece(twoForward)) {
                        moves.add(new ChessMove(start, twoForward));
                    }
                }
            }
        }

        int[] captureCols = {startCol - 1, startCol + 1};
        for (int col : captureCols) {
            if (col >= 0 && col <= 7 && newRow >= 0 && newRow <= 7) {
                ChessPosition capturePos = new ChessPosition(col, newRow);
                if (chessMatrix.hasPiece(capturePos) && chessMatrix.getPiece(capturePos).getColor() != getColor()) {
                    moves.add(new ChessMove(start, capturePos));
                }
            }
        }

        return moves;
    }
}