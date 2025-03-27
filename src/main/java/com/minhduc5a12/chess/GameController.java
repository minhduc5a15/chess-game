package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.pieces.*;
import com.minhduc5a12.chess.utils.BoardUtils;
import com.minhduc5a12.chess.utils.SoundPlayer;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameController extends BoardManager implements MoveExecutor {
    private boolean gameEnded;
    private JFrame frame;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public GameController() {
        super();
        this.gameEnded = false;
        setupInitialPosition();
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public boolean executeMove(ChessMove move) {
        ChessPiece piece = getPiece(move.start());
        if (piece == null || gameEnded) {
            SoundPlayer.playMoveIllegal();
            logger.debug("No piece found at start position or game ended: {}", move.start().toChessNotation());
            return false;
        }
        if (!BoardUtils.isMoveValidUnderCheck(move, getChessPieceMap())) {
            if (move.start().equals(move.end())) {
                setCurrentLeftClickedTile(null);
                return false;
            }
            SoundPlayer.playMoveIllegal();
            setCurrentLeftClickedTile(null);

            logger.debug("Invalid move under check: {}", move);
            return false;
        }

        // Kiểm tra có ăn quân không
        boolean isCapture = getPiece(move.end()) != null;

        // Thực thi nước đi
        ChessTile startTile = getTiles()[move.start().toMatrixCoords()[0]][move.start().toMatrixCoords()[1]];
        ChessTile endTile = getTiles()[move.end().toMatrixCoords()[0]][move.end().toMatrixCoords()[1]];

        removePiece(move.end());
        removePiece(move.start());
        setPiece(move.end(), piece);
        setLastMove(move);

        startTile.repaint();
        endTile.repaint();

        switchTurn();

        // Kiểm tra chiếu sau khi đổi lượt
        boolean isCheck = BoardUtils.isKingInCheck(currentPlayerColor, getChessPieceMap());

        // Phát âm thanh theo ưu tiên: ăn quân > chiếu > di chuyển thường
        if (isCapture) {
            SoundPlayer.playCaptureSound();
        } else if (isCheck) {
            SoundPlayer.playMoveCheckSound();
        } else {
            SoundPlayer.playMoveSound();
        }

        logger.debug("Executed move: {} to {}", move.start().toChessNotation(), move.end().toChessNotation());

        executor.submit(() -> {
            if (BoardUtils.isCheckmate(currentPlayerColor, getChessPieceMap())) {
                gameEnded = true;
                SwingUtilities.invokeLater(this::showGameOverDialog);
            }
        });

        return true;
    }

    @Override
    public boolean performCastling(boolean isKingside, PieceColor color) {
        ChessPosition kingPos = getChessPieceMap().getKingPosition(color);
        if (kingPos == null) {
            logger.debug("King not found for color: {}", color);
            return false;
        }

        ChessPiece king = getPiece(kingPos);
        if (!(king instanceof King kingPiece) || king.hasMoved()) {
            logger.debug("King has moved or not found at {}", kingPos.toChessNotation());
            return false;
        }

        boolean canCastle = isKingside ? kingPiece.canCastleKingside(kingPos, getChessPieceMap()) : kingPiece.canCastleQueenside(kingPos, getChessPieceMap());

        if (!canCastle) {
            logger.debug("Cannot castle {} for {}", isKingside ? "kingside" : "queenside", color);
            return false;
        }

        int kingRow = (color.isWhite()) ? 0 : 7;
        int rookCol = isKingside ? 7 : 0;

        ChessPosition rookPos = new ChessPosition(rookCol, kingRow);
        ChessPiece rook = getPiece(rookPos);

        int kingTargetCol = isKingside ? 6 : 2;
        int rookTargetCol = isKingside ? 5 : 3;

        ChessTile kingStartTile = getTiles()[kingPos.toMatrixCoords()[0]][kingPos.toMatrixCoords()[1]];
        ChessTile kingEndTile = getTiles()[kingRow][kingTargetCol];
        ChessTile rookStartTile = getTiles()[rookPos.toMatrixCoords()[0]][rookPos.toMatrixCoords()[1]];
        ChessTile rookEndTile = getTiles()[kingRow][rookTargetCol];

        removePiece(kingPos);
        removePiece(rookPos);
        setPiece(new ChessPosition(kingTargetCol, kingRow), king);
        setPiece(new ChessPosition(rookTargetCol, kingRow), rook);
        king.setHasMoved(true);
        rook.setHasMoved(true);
        kingStartTile.repaint();
        kingEndTile.repaint();
        rookStartTile.repaint();
        rookEndTile.repaint();
        logger.debug("Castling performed: {} for {}", isKingside ? "Kingside" : "Queenside", color);
        return true;
    }

    @Override
    public boolean movePiece(ChessMove move) {
        ChessPiece piece = getPiece(move.start());
        if (piece instanceof King && Math.abs(move.end().col() - move.start().col()) == 2) {
            boolean isKingside = move.end().col() > move.start().col();
            if (performCastling(isKingside, piece.getColor())) {
                SoundPlayer.playCastleSound();
                switchTurn();
                executor.submit(() -> {
                    if (BoardUtils.isCheckmate(currentPlayerColor, getChessPieceMap())) {
                        gameEnded = true;
                        SwingUtilities.invokeLater(this::showGameOverDialog);
                    }
                });
                return true;
            } else {
                SoundPlayer.playMoveIllegal();
                return false;
            }
        }
        return executeMove(move);
    }

    private void showGameOverDialog() {
        String winner = (currentPlayerColor.isWhite()) ? "Đen" : "Trắng";
        GameOverDialog dialog = new GameOverDialog(frame, "Chiếu hết! Người chơi " + winner + " thắng!");
        dialog.setVisible(true);
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void shutdown() {
        executor.shutdown();
        SoundPlayer.shutdown();
    }
}