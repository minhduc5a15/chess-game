package com.minhduc5a12.chess.model;

import com.minhduc5a12.chess.constants.GameConstants;

public record ChessPosition(int col, int row) {
    public ChessPosition {
        if (col < 0 || col > (GameConstants.Board.BOARD_SIZE - 1) || row < 0 || row > (GameConstants.Board.BOARD_SIZE - 1)) {
            throw new IllegalArgumentException("Invalid position: (" + col + ", " + row + ")");
        }
    }

    @Override
    public String toString() {
        return "ChessPosition{" + col + ", " + row + "}";
    }

    public String toChessNotation() {
        char file = (char) ('a' + col);
        int rank = row + 1;
        return "" + file + rank;
    }

    public static ChessPosition toChessPosition(String chessNotation) {
        if (chessNotation == null || chessNotation.length() != 2) {
            throw new IllegalArgumentException("Invalid chess notation: " + chessNotation);
        }
        char file = chessNotation.charAt(0);
        int rank = Character.getNumericValue(chessNotation.charAt(1));
        return new ChessPosition(file - 'a', rank - 1);
    }

    public int[] toMatrixCoords() {
        return new int[]{GameConstants.Board.BOARD_SIZE - row - 1, col};
    }

    public int matrixCol() {
        return col;
    }

    public int matrixRow() {
        return GameConstants.Board.BOARD_SIZE - row - 1;
    }
}
