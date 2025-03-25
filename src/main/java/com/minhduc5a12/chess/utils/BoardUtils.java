package com.minhduc5a12.chess.utils;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.pieces.ChessPiece;
import com.minhduc5a12.chess.pieces.ChessPieceMap;
import com.minhduc5a12.chess.pieces.King;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BoardUtils {
    private static final Logger logger = LoggerFactory.getLogger(BoardUtils.class);
    public static final int BOARD_SIZE = 8;

    public static boolean isWithinBoard(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    public static boolean isKingInCheck(PieceColor color, ChessPieceMap pieceMap) {
        ChessPosition kingPosition = null;
        for (ChessPosition pos : pieceMap.getPieceMap().keySet()) {
            ChessPiece piece = pieceMap.getPiece(pos);
            if (piece instanceof King && piece.getColor() == color) {
                kingPosition = pos;
                break;
            }
        }

        if (kingPosition == null) {
            logger.warn("King not found for color: {}", color);
            return false;
        }
        PieceColor opponentColor = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        for (ChessPosition pos : pieceMap.getPieceMap().keySet()) {
            ChessPiece piece = pieceMap.getPiece(pos);
            if (piece.getColor() == opponentColor) {
                List<ChessMove> opponentMoves = piece.generateValidMoves(pos, pieceMap);
                for (ChessMove move : opponentMoves) {
                    if (move.end().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isCheckmate(PieceColor color, ChessPieceMap pieceMap) {
        if (!isKingInCheck(color, pieceMap)) {
            return false;
        }

        for (ChessPosition pos : pieceMap.getPieceMap().keySet()) {
            ChessPiece piece = pieceMap.getPiece(pos);
            if (piece.getColor() == color) {
                List<ChessMove> moves = piece.generateValidMoves(pos, pieceMap);
                for (ChessMove move : moves) {
                    ChessPieceMap tempMap = simulateMove(move, pieceMap);
                    if (!isKingInCheck(color, tempMap)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isMoveValidUnderCheck(ChessMove move, ChessPieceMap pieceMap) {
        ChessPiece piece = pieceMap.getPiece(move.start());
        if (piece == null) return false;

        PieceColor color = piece.getColor();

        if (!isKingInCheck(color, pieceMap)) {
            return piece.isValidMove(move, pieceMap);
        }

        ChessPieceMap tempMap = simulateMove(move, pieceMap);
        return !isKingInCheck(color, tempMap);
    }

    private static ChessPieceMap simulateMove(ChessMove move, ChessPieceMap originalMap) {
        ChessPieceMap tempMap = new ChessPieceMap();
        for (ChessPosition pos : originalMap.getPieceMap().keySet()) {
            tempMap.setPiece(pos, originalMap.getPiece(pos));
        }

        ChessPiece piece = tempMap.getPiece(move.start());
        tempMap.removePiece(move.end());
        tempMap.removePiece(move.start());
        tempMap.setPiece(move.end(), piece);

        return tempMap;
    }
}
