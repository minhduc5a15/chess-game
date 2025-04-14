package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.GameConstants;
import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPiece;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.pieces.*;
import com.minhduc5a12.chess.utils.BoardUtils;
import com.minhduc5a12.chess.utils.ChessNotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardManager {
    protected static final Logger logger = LoggerFactory.getLogger(BoardManager.class);

    // Fields
    private final ChessTile[][] tiles = new ChessTile[GameConstants.Board.BOARD_SIZE][GameConstants.Board.BOARD_SIZE];
    private final ChessPieceMap chessPieceMap;
    private final Map<String, Integer> boardStateHistory;
    private final ChessNotationUtils notationUtils;
    private ChessTile currentLeftClickedTile;
    private List<ChessMove> currentValidMoves;
    private ChessMove lastMove;
    protected PieceColor currentPlayerColor = PieceColor.WHITE;
    protected int halfmoveClock = 0;
    protected int fullmoveNumber = 1;

    // Constructor
    public BoardManager() {
        this.chessPieceMap = new ChessPieceMap();
        this.boardStateHistory = new HashMap<>();
        this.notationUtils = new ChessNotationUtils();
        this.currentLeftClickedTile = null;
        this.currentValidMoves = new ArrayList<>();
        this.lastMove = null;
        initializeTiles();
    }

    // Initialize tiles array
    private void initializeTiles() {
        for (int row = 0; row < GameConstants.Board.BOARD_SIZE; row++) {
            for (int col = 0; col < GameConstants.Board.BOARD_SIZE; col++) {
                tiles[row][col] = new ChessTile(new ChessPosition(col, GameConstants.Board.BOARD_SIZE - row - 1), (ChessController) this);
            }
        }
    }

    // Getters
    public ChessPiece getPiece(ChessPosition position) {
        return chessPieceMap.getPiece(position);
    }

    public ChessPiece getPiece(String chessNotation) {
        return chessPieceMap.getPiece(chessNotation);
    }

    public PieceColor getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public ChessTile getCurrentLeftClickedTile() {
        return currentLeftClickedTile;
    }

    public ChessPieceMap getChessPieceMap() {
        return chessPieceMap;
    }

    public ChessTile[][] getTiles() {
        return tiles;
    }

    public ChessTile getTile(ChessPosition position) {
        return tiles[position.matrixRow()][position.matrixCol()];
    }

    public ChessMove getLastMove() {
        return lastMove;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public int getFullmoveNumber() {
        return fullmoveNumber;
    }

    public Map<String, Integer> getBoardStateHistory() {
        return boardStateHistory;
    }

    public ChessNotationUtils getNotationUtils() {
        return notationUtils;
    }

    public List<ChessMove> getCurrentValidMoves() {
        return currentValidMoves;
    }

    // Setters
    public void setPiece(ChessPosition position, ChessPiece piece) {
        chessPieceMap.setPiece(position, piece);
        getTile(position).setPiece(piece);
    }

    public void setPiece(int x, int y, ChessPiece piece) {
        setPiece(new ChessPosition(x, y), piece);
    }

    public void setCurrentLeftClickedTile(ChessTile tile) {
        if (currentLeftClickedTile != null) {
            currentLeftClickedTile.setLeftClickSelected(false);
            clearValidMoveHighlights();
        }
        currentLeftClickedTile = tile;
        if (tile != null) {
            tile.setLeftClickSelected(true);
            generateAndHighlightValidMoves(tile);
        }
    }

    public void setLastMove(ChessMove lastMove) {
        clearLastMoveHighlights();
        this.lastMove = lastMove;
        if (lastMove != null) {
            highlightLastMove();
            logger.info("Last move: {}", lastMove);
        }
    }

    public void setHalfmoveClock(int halfmoveClock) {
        this.halfmoveClock = halfmoveClock;
    }

    public void setFullmoveNumber(int fullmoveNumber) {
        this.fullmoveNumber = fullmoveNumber;
    }

    // Core methods
    public void setupInitialPosition() {
        clear();
        placeInitialPieces(PieceColor.WHITE, 0, 1);
        placeInitialPieces(PieceColor.BLACK, 7, 6);
    }

    public void switchTurn() {
        currentPlayerColor = currentPlayerColor.getOpponent();
        clearCurrentValidMoves();
        setCurrentLeftClickedTile(null);
        fullmoveNumber++;
        logger.debug("Switched turn to: {}", currentPlayerColor);
    }

    public void clear() {
        chessPieceMap.clear();
        for (ChessTile[] row : tiles) {
            for (ChessTile tile : row) {
                tile.setPiece(null);
            }
        }
        currentLeftClickedTile = null;
    }

    public void removePiece(ChessPosition position) {
        chessPieceMap.removePiece(position);
        getTile(position).setPiece(null);
    }

    public void updatePieceMovement(ChessMove move) {
        ChessPiece piece = getPiece(move.end());
        if (piece != null) {
            piece.setHasMoved(true);
        }
    }

    public void updateBoardStateHistory() {
        String partialFEN = notationUtils.getPartialFEN(this);
        boardStateHistory.put(partialFEN, boardStateHistory.getOrDefault(partialFEN, 0) + 1);
        logger.debug("Updated board state (partial FEN): {}, occurrences: {}", partialFEN, boardStateHistory.get(partialFEN));
    }

    public void repaintTiles(ChessTile... tiles) {
        for (ChessTile tile : tiles) {
            if (tile != null) tile.repaint();
        }
    }

    // Helper methods
    private void placeInitialPieces(PieceColor color, int backRow, int pawnRow) {
        setPiece(0, backRow, new Rook(color));
        setPiece(1, backRow, new Knight(color));
        setPiece(2, backRow, new Bishop(color));
        setPiece(3, backRow, new Queen(color));
        setPiece(4, backRow, new King(color));
        setPiece(5, backRow, new Bishop(color));
        setPiece(6, backRow, new Knight(color));
        setPiece(7, backRow, new Rook(color));
        for (int col = 0; col < 8; col++) {
            setPiece(col, pawnRow, new Pawn(color, this));
        }
    }

    private void generateAndHighlightValidMoves(ChessTile tile) {
        currentValidMoves = tile.getPiece().generateValidMoves(tile.getPosition(), chessPieceMap);
        for (ChessMove move : currentValidMoves) {
            if (BoardUtils.isMoveValidUnderCheck(move, chessPieceMap)) {
                ChessTile endTile = getTile(move.end());
                if (endTile != null) {
                    endTile.setValidMove(true);
                }
            }
        }
    }

    private void clearValidMoveHighlights() {
        for (ChessMove move : currentValidMoves) {
            ChessTile endTile = getTile(move.end());
            if (endTile != null) {
                endTile.setValidMove(false);
            }
        }
    }

    public void clearCurrentValidMoves() {
        if (!currentValidMoves.isEmpty()) {
            clearValidMoveHighlights();
            currentValidMoves.clear();
        }
    }

    private void clearLastMoveHighlights() {
        if (lastMove != null) {
            ChessTile startTile = getTile(lastMove.start());
            ChessTile endTile = getTile(lastMove.end());
            if (startTile != null) startTile.setLastMove(false);
            if (endTile != null) endTile.setLastMove(false);
        }
    }

    private void highlightLastMove() {
        ChessTile startTile = getTile(lastMove.start());
        ChessTile endTile = getTile(lastMove.end());
        if (startTile != null) startTile.setLastMove(true);
        if (endTile != null) endTile.setLastMove(true);
    }

}