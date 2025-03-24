package com.minhduc5a12.chess.model;

public record ChessPosition(int col, int row) {
    public ChessPosition {
        if (col < 0 || col > 7 || row < 0 || row > 7) {
            throw new IllegalArgumentException("Invalid position: (" + col + ", " + row + ")");
        }
    }

    @Override
    public String toString() {
        return "Position{" + col + ", " + row + "}";
    }

    public String toChessNotation() {
        char file = (char) ('a' + col);
        int rank = row + 1;
        return "" + file + rank;
    }

    public int[] toMatrixCoords() {
        return new int[]{7 - row, col};
    }

    public int matrixCol() {
        return col;
    }

    public int matrixRow() {
        return 7 - row;
    }
}
