package com.minhduc5a12.chess.pieces;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.utils.BoardUtils;

import java.util.ArrayList;
import java.util.List;

public class King extends ChessPiece {
    public King(PieceColor color) {
        super(color, color == PieceColor.WHITE ? "white_king.png" : "black_king.png");
        setPieceValue(0);
    }

    @Override
    public List<ChessMove> generateValidMoves(ChessPosition start, ChessPieceMap pieceMap) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = start.row();
        int startCol = start.col();
        PieceColor color = getColor();

        int[][] offsets = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] offset : offsets) {
            int newCol = startCol + offset[0];
            int newRow = startRow + offset[1];
            if (newCol >= 0 && newCol <= 7 && newRow >= 0 && newRow <= 7) {
                ChessPosition pos = new ChessPosition(newCol, newRow);
                if (!pieceMap.hasPiece(pos) || (pieceMap.hasPiece(pos) && pieceMap.getPiece(pos).getColor() != color)) {
                    moves.add(new ChessMove(start, pos));
                }
            }
        }

        List<ChessMove> validMoves = new ArrayList<>();

        if (canCastleKingside(start, pieceMap)) {
            moves.add(new ChessMove(start, new ChessPosition(6, start.row())));
        }

        if (canCastleQueenside(start, pieceMap)) {
            moves.add(new ChessMove(start, new ChessPosition(2, start.row())));
        }

        for (ChessMove move : moves) {
            ChessPieceMap tempMap = BoardUtils.simulateMove(move, pieceMap);
            if (!BoardUtils.isKingInCheck(color, tempMap)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    public boolean canCastleKingside(ChessPosition start, ChessPieceMap pieceMap) {
        if (BoardUtils.isKingInCheck(getColor(), pieceMap)) {
            return false;
        }
        int row = start.row();
        ChessPosition rookPos = new ChessPosition(7, row);
        ChessPiece rook = pieceMap.getPiece(rookPos);

        if (!(rook instanceof Rook) || rook.hasMoved()) {
            return false;
        }

        for (int col = start.col() + 1; col < 7; col++) {
            if (pieceMap.hasPiece(new ChessPosition(col, row))) {
                return false;
            }
        }

        for (int col = start.col(); col <= start.col() + 2; col++) {
            ChessPosition pos = new ChessPosition(col, row);
            ChessPieceMap tempMap = BoardUtils.simulateMove(new ChessMove(start, pos), pieceMap);
            if (BoardUtils.isKingInCheck(getColor(), tempMap)) {
                return false;
            }
        }
        return true;
    }

    public boolean canCastleQueenside(ChessPosition start, ChessPieceMap pieceMap) {
        if (BoardUtils.isKingInCheck(getColor(), pieceMap)) {
            return false;
        }
        int row = start.row();
        ChessPosition rookPos = new ChessPosition(0, row); // a1 hoặc a8
        ChessPiece rook = pieceMap.getPiece(rookPos);

        if (!(rook instanceof Rook) || rook.hasMoved()) {
            return false;
        }

        for (int col = start.col() - 1; col > 0; col--) {
            if (pieceMap.hasPiece(new ChessPosition(col, row))) {
                return false;
            }
        }

        for (int col = start.col(); col >= start.col() - 2; col--) {
            ChessPosition pos = new ChessPosition(col, row);
            ChessPieceMap tempMap = BoardUtils.simulateMove(new ChessMove(start, pos), pieceMap);
            if (BoardUtils.isKingInCheck(getColor(), tempMap)) {
                return false;
            }
        }
        return true;
    }
}