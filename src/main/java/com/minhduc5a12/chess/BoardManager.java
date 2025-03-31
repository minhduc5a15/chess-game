package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
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
    private ChessTile currentLeftClickedTile;
    private List<ChessMove> currentValidMoves;
    protected PieceColor currentPlayerColor = PieceColor.WHITE;
    private final ChessTile[][] tiles = new ChessTile[8][8];
    private final ChessPieceMap chessPieceMap;
    private ChessMove lastMove;
    protected int halfmoveClock = 0;
    protected int fullmoveNumber = 1;

    private final Map<String, Integer> boardStateHistory;
    private final ChessNotationUtils notationUtils;

    public BoardManager() {
        this.chessPieceMap = new ChessPieceMap();
        this.boardStateHistory = new HashMap<>();
        this.notationUtils = new ChessNotationUtils();
        this.currentLeftClickedTile = null;
        this.currentValidMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                tiles[row][col] = new ChessTile(new ChessPosition(col, 7 - row), (GameController) this);
            }
        }
        this.lastMove = null;
    }

    public ChessPiece getPiece(ChessPosition position) {
        return chessPieceMap.getPiece(position);
    }

    public ChessPiece getPiece(String chessNotation) {
        return chessPieceMap.getPiece(chessNotation);
    }

    public void setPiece(ChessPosition position, ChessPiece piece) {
        chessPieceMap.setPiece(position, piece);
        getTile(position).setPiece(piece);
    }

    public void setPiece(int x, int y, ChessPiece piece) {
        setPiece(new ChessPosition(x, y), piece);
    }

    public void clear() {
        chessPieceMap.clear();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                tiles[row][col].setPiece(null);
            }
        }
        this.currentLeftClickedTile = null;
    }

    public void removePiece(ChessPosition position) {
        chessPieceMap.removePiece(position);
        int[] matrixCoords = position.toMatrixCoords();
        int row = matrixCoords[0], col = matrixCoords[1];
        tiles[row][col].setPiece(null);
    }

    public void setupInitialPosition() {
        clear();

        // Đặt quân trắng
        setPiece(0, 0, new Rook(PieceColor.WHITE));    // Xe trắng a1
        setPiece(1, 0, new Knight(PieceColor.WHITE));  // Mã trắng b1
        setPiece(2, 0, new Bishop(PieceColor.WHITE));  // Tượng trắng c1
        setPiece(3, 0, new Queen(PieceColor.WHITE));   // Hậu trắng d1
        setPiece(4, 0, new King(PieceColor.WHITE));    // Vua trắng e1
        setPiece(5, 0, new Bishop(PieceColor.WHITE));  // Tượng trắng f1
        setPiece(6, 0, new Knight(PieceColor.WHITE));  // Mã trắng g1
        setPiece(7, 0, new Rook(PieceColor.WHITE));    // Xe trắng h1
        for (int col = 0; col < 8; col++) {
            setPiece(col, 1, new Pawn(PieceColor.WHITE, this)); // Tốt trắng hàng 2
        }

        // Đặt quân đen
        setPiece(0, 7, new Rook(PieceColor.BLACK));    // Xe đen a8
        setPiece(1, 7, new Knight(PieceColor.BLACK));  // Mã đen b8
        setPiece(2, 7, new Bishop(PieceColor.BLACK));  // Tượng đen c8
        setPiece(3, 7, new Queen(PieceColor.BLACK));   // Hậu đen d8
        setPiece(4, 7, new King(PieceColor.BLACK));    // Vua đen e8
        setPiece(5, 7, new Bishop(PieceColor.BLACK));  // Tượng đen f8
        setPiece(6, 7, new Knight(PieceColor.BLACK));  // Mã đen g8
        setPiece(7, 7, new Rook(PieceColor.BLACK));    // Xe đen h8
        for (int col = 0; col < 8; col++) {
            setPiece(col, 6, new Pawn(PieceColor.BLACK, this)); // Tốt đen hàng 7
        }
    }

    public void switchTurn() {
        currentPlayerColor = currentPlayerColor.getOpponent();
        clearCurrentValidMoves();
        setCurrentLeftClickedTile(null);
        fullmoveNumber++;
        logger.debug("Switched turn to: {}", currentPlayerColor);
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

    public void clearCurrentValidMoves() {
        if (currentValidMoves.isEmpty()) return;
        for (ChessMove move : currentValidMoves) {
            ChessTile endTile = getTile(move.end());
            if (endTile != null) {
                endTile.setValidMove(false);
            }
        }
        currentValidMoves.clear();
    }

    public ChessTile[][] getTiles() {
        return tiles;
    }

    public void setCurrentLeftClickedTile(ChessTile tile) {
        if (this.currentLeftClickedTile != null) {
            this.currentLeftClickedTile.setLeftClickSelected(false);
            for (ChessMove move : currentValidMoves) {
                ChessTile endTile = getTile(move.end());
                if (endTile != null) {
                    endTile.setValidMove(false);
                }
            }
        }
        this.currentLeftClickedTile = tile;
        if (tile != null) {
            tile.setLeftClickSelected(true);
            this.currentValidMoves = tile.getPiece().generateValidMoves(tile.getPosition(), this.chessPieceMap);
            for (ChessMove move : currentValidMoves) {
                if (!BoardUtils.isMoveValidUnderCheck(move, this.chessPieceMap)) continue;
                ChessTile endTile = getTile(move.end());
                if (endTile != null) {
                    endTile.setValidMove(true);
                }
            }
        }
    }

    public ChessMove getLastMove() {
        return lastMove;
    }

    public void setLastMove(ChessMove lastMove) {
        if (this.lastMove != null) {
            ChessTile startTile = getTile(this.lastMove.start());
            ChessTile endTile = getTile(this.lastMove.end());
            if (startTile != null) startTile.setLastMove(false);
            if (endTile != null) endTile.setLastMove(false);
        }
        this.lastMove = lastMove;
        if (lastMove != null) {
            ChessTile startTile = getTile(lastMove.start());
            ChessTile endTile = getTile(lastMove.end());
            if (startTile != null) startTile.setLastMove(true);
            if (endTile != null) endTile.setLastMove(true);
        }
        logger.info("Last move: {}", lastMove);
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public int getFullmoveNumber() {
        return fullmoveNumber;
    }

    public ChessTile getTile(ChessPosition position) {
        return tiles[position.matrixRow()][position.matrixCol()];
    }

    public Map<String, Integer> getBoardStateHistory() {
        return boardStateHistory;
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

    public ChessNotationUtils getNotationUtils() {
        return notationUtils;
    }
}