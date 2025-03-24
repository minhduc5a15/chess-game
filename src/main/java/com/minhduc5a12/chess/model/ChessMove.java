package com.minhduc5a12.chess.model;

public record ChessMove(ChessPosition start, ChessPosition end) {

    @Override
    public String toString() {
        return "Move{" + start.toString() + ", " + end.toString() + "}";
    }

    public ChessMove(int startX, int startY, int endX, int endY) {
        this(new ChessPosition(startX, startY), new ChessPosition(endX, endY));
    }

    public int startCol() {
        return start.col();
    }

    public int startRow() {
        return start.row();
    }

    public int endCol() {
        return end.col();
    }

    public int endRow() {
        return end.row();
    }

    public int startMatrixCol() {
        return start.matrixCol();
    }

    public int startMatrixRow() {
        return start.matrixRow();
    }

    public int endMatrixCol() {
        return end.matrixCol();
    }

    public int endMatrixRow() {
        return end.matrixRow();
    }
}
