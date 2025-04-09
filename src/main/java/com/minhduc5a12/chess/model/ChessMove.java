package com.minhduc5a12.chess.model;

public record ChessMove(ChessPosition start, ChessPosition end) {

    @Override
    public String toString() {
        return "Move{" + start.toString() + ", " + end.toString() + "}";
    }

    public ChessMove(int startX, int startY, int endX, int endY) {
        this(new ChessPosition(startX, startY), new ChessPosition(endX, endY));
    }
}
