package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPiece;
import com.minhduc5a12.chess.model.ChessPosition;

public interface MoveExecutor {
    boolean executeMove(ChessMove move);

    boolean performCastling(boolean isKingside, PieceColor color);

    boolean performEnPassant(ChessMove move);

    ChessPiece promotePawn(ChessPosition position, PieceColor color);
}