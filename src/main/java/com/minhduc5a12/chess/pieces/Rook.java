package com.minhduc5a12.chess.pieces;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.utils.BoardUtils;

import java.util.ArrayList;
import java.util.List;

public class Rook extends ChessPiece {
    {
        this.pieceValue = 5;
    }

    public Rook(PieceColor color) {
        super(color, color.isWhite() ? "white_rook.png" : "black_rook.png");
    }

    @Override
    public List<ChessMove> generateValidMoves(ChessPosition start, ChessPieceMap pieceMap) {
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

            while (BoardUtils.isWithinBoard(newCol, newRow)) {
                ChessPosition endPos = new ChessPosition(newCol, newRow);
                if (!pieceMap.hasPiece(endPos)) {
                    moves.add(new ChessMove(start, endPos));
                } else {
                    if (pieceMap.getPiece(endPos).getColor() != getColor()) {
                        moves.add(new ChessMove(start, endPos));
                    }
                    break;
                }
                newCol += dCol;
                newRow += dRow;
            }
        }
        return moves;
    }

    @Override
    public String getPieceNotation() {
        return this.getColor().isWhite() ? "R" : "r";
    }
}