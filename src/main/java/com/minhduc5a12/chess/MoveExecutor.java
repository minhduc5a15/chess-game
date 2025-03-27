package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;

public interface MoveExecutor {
    boolean executeMove(ChessMove move);
    boolean performCastling(boolean isKingside, PieceColor color);
}