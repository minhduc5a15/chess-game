package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessPiece;

public interface PlayerPanelListener {
    void onScoreUpdated(PieceColor color, int score);

    void onPieceCaptured(PieceColor capturerColor, ChessPiece capturedPiece);

    void onTurnChanged(PieceColor currentPlayerColor);
}
