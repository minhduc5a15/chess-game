package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.pieces.ChessPiece;

public interface MoveExecutor {
    boolean executeMove(ChessMove move);
    boolean performCastling(boolean isKingside, PieceColor color);
    boolean performEnPassant(ChessMove move);
    ChessPiece promotePawn(ChessPosition position, PieceColor color);
}