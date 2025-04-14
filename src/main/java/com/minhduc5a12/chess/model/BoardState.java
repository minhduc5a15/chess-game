package com.minhduc5a12.chess.model;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.pieces.ChessPieceMap;

import java.io.Serializable;

public class BoardState implements Serializable {
    private final ChessPieceMap chessPieceMap;
    private ChessMove lastMove;
    protected PieceColor currentPlayerColor = PieceColor.WHITE;
    protected int halfmoveClock = 0;
    protected int fullmoveNumber = 1;

    public BoardState(ChessPieceMap chessPieceMap) {
        this.chessPieceMap = chessPieceMap;
    }
}