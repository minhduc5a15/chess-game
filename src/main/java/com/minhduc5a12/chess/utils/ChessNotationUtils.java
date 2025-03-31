package com.minhduc5a12.chess.utils;

import com.minhduc5a12.chess.BoardManager;
import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.pieces.ChessPiece;
import com.minhduc5a12.chess.pieces.ChessPieceMap;
import com.minhduc5a12.chess.pieces.Pawn;
import com.minhduc5a12.chess.pieces.Rook;

public class ChessNotationUtils {
    public String getFEN(BoardManager boardManager) {
        ChessPieceMap pieceMap = boardManager.getChessPieceMap();
        StringBuilder fen = new StringBuilder();

        // 1. Vị trí quân cờ
        for (int row = 7; row >= 0; row--) {
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(col, row);
                ChessPiece piece = pieceMap.getPiece(position);

                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(getPieceNotation(piece));
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (row > 0) {
                fen.append("/");
            }
        }

        // 2. Lượt đi
        fen.append(" ");
        fen.append(boardManager.getCurrentPlayerColor().isWhite() ? "w" : "b");

        // 3. Quyền nhập thành
        fen.append(" ");
        StringBuilder castling = new StringBuilder();
        boolean whiteKingMoved = pieceMap.getPiece(pieceMap.getKingPosition(PieceColor.WHITE)) != null && pieceMap.getPiece(pieceMap.getKingPosition(PieceColor.WHITE)).hasMoved();
        boolean blackKingMoved = pieceMap.getPiece(pieceMap.getKingPosition(PieceColor.BLACK)) != null && pieceMap.getPiece(pieceMap.getKingPosition(PieceColor.BLACK)).hasMoved();

        ChessPiece whiteRookKingSide = pieceMap.getPiece(new ChessPosition(7, 0));
        ChessPiece whiteRookQueenSide = pieceMap.getPiece(new ChessPosition(0, 0));
        ChessPiece blackRookKingSide = pieceMap.getPiece(new ChessPosition(7, 7));
        ChessPiece blackRookQueenSide = pieceMap.getPiece(new ChessPosition(0, 7));

        if (!whiteKingMoved) {
            if (whiteRookKingSide instanceof Rook && !whiteRookKingSide.hasMoved()) {
                castling.append("K");
            }
            if (whiteRookQueenSide instanceof Rook && !whiteRookQueenSide.hasMoved()) {
                castling.append("Q");
            }
        }
        if (!blackKingMoved) {
            if (blackRookKingSide instanceof Rook && !blackRookKingSide.hasMoved()) {
                castling.append("k");
            }
            if (blackRookQueenSide instanceof Rook && !blackRookQueenSide.hasMoved()) {
                castling.append("q");
            }
        }
        fen.append(!castling.isEmpty() ? castling.toString() : "-");

        // 4. Mục tiêu en passant
        fen.append(" ");
        ChessMove lastMove = boardManager.getLastMove();
        if (lastMove != null && pieceMap.getPiece(lastMove.end()) instanceof Pawn && Math.abs(lastMove.start().row() - lastMove.end().row()) == 2) {
            int enPassantRow = (lastMove.start().row() + lastMove.end().row()) / 2;
            ChessPosition enPassantTarget = new ChessPosition(lastMove.end().col(), enPassantRow);
            fen.append(enPassantTarget.toChessNotation());
        } else {
            fen.append("-");
        }

        // 5. Đồng hồ nửa nước
        fen.append(" ");
        fen.append(boardManager.getHalfmoveClock());

        // 6. Số nước đi đầy đủ
        fen.append(" ");
        fen.append(boardManager.getFullmoveNumber());

        return fen.toString();
    }

    public String getPartialFEN(BoardManager boardManager) {
        String fullFen = getFEN(boardManager);
        String[] parts = fullFen.split(" ");

        return String.join(" ", parts[0], parts[1], parts[2], parts[3]);
    }

    private char getPieceNotation(ChessPiece piece) {
        String className = piece.getClass().getSimpleName();
        char notation = switch (className) {
            case "Pawn" -> 'p';
            case "Rook" -> 'r';
            case "Knight" -> 'n';
            case "Bishop" -> 'b';
            case "Queen" -> 'q';
            case "King" -> 'k';
            default -> throw new IllegalArgumentException("Unknown piece type: " + className);
        };
        return piece.getColor() == PieceColor.WHITE ? Character.toUpperCase(notation) : notation;
    }
}