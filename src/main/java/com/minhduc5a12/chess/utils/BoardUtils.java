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
import java.util.Map;

public class BoardUtils {
    private static final Logger logger = LoggerFactory.getLogger(BoardUtils.class);
    public static final int BOARD_SIZE = 8;

    public static boolean isWithinBoard(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    public static boolean isKingInCheck(PieceColor color, ChessPieceMap pieceMap) {
        ChessPosition kingPosition = pieceMap.getKingPosition(color);
        if (kingPosition == null) return false;

        PieceColor opponentColor = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        ChessPosition opponentKingPos = pieceMap.getKingPosition(opponentColor);

        if (opponentKingPos != null && Math.abs(kingPosition.col() - opponentKingPos.col()) <= 1 && Math.abs(kingPosition.row() - opponentKingPos.row()) <= 1) {
            return true;
        }

        for (Map.Entry<ChessPosition, ChessPiece> enemyEntry : pieceMap.getPieceMap().entrySet()) {
            ChessPosition enemyPosition = enemyEntry.getKey();
            ChessPiece enemyPiece = enemyEntry.getValue();

            if (enemyPiece.getColor() != opponentColor || enemyPiece instanceof King) {
                continue;
            }

            List<ChessMove> possibleAttacks = enemyPiece.generateValidMoves(enemyPosition, pieceMap);
            for (ChessMove move : possibleAttacks) {
                if (move.end().equals(kingPosition)) {
                    logger.info("King of {} in check by {} at {}", color, enemyPiece.getClass().getSimpleName(), enemyPosition.toChessNotation());
                    return true;
                }
            }
        }
        return false;
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

    public static ChessPieceMap simulateMove(ChessMove move, ChessPieceMap pieceMap) {
        ChessPieceMap tempMap = new ChessPieceMap();
        for (Map.Entry<ChessPosition, ChessPiece> entry : pieceMap.getPieceMap().entrySet()) {
            ChessPosition pos = entry.getKey();
            ChessPiece piece = entry.getValue();
            tempMap.setPiece(pos, piece);
        }

        ChessPiece piece = tempMap.getPiece(move.start());
        tempMap.removePiece(move.end());
        tempMap.removePiece(move.start());
        tempMap.setPiece(move.end(), piece);

        return tempMap;
    }
}
