package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.pieces.*;
import com.minhduc5a12.chess.ui.GameOverDialog;
import com.minhduc5a12.chess.ui.PromotionDialog;
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

        boolean isCapture = getPiece(move.end()) != null;
        boolean isPawnMove = piece instanceof Pawn;

        ChessTile startTile = getTile(move.start());
        ChessTile endTile = getTile(move.end());


        if (piece instanceof Pawn && (move.end().row() == 7 || move.end().row() == 0)) {
            piece = promotePawn(move.end(), piece.getColor());
            SoundPlayer.playMoveSound();
        }
        removePiece(move.end());
        setPiece(move.end(), piece);
        removePiece(move.start());

        setLastMove(move);

        startTile.repaint();
        endTile.repaint();

        if (isCapture || isPawnMove) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
        }

        switchTurn();

        boolean isCheck = BoardUtils.isKingInCheck(currentPlayerColor, getChessPieceMap());

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

        ChessTile kingStartTile = getTile(kingPos);
        ChessTile kingEndTile = getTiles()[kingRow][kingTargetCol];
        ChessTile rookStartTile = getTile(rookPos);
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
    public boolean performEnPassant(ChessMove move) {
        ChessPiece piece = getPiece(move.start());
        if (!(piece instanceof Pawn) || gameEnded) {
            logger.debug("Not a pawn or game ended at {}", move.start().toChessNotation());
            return false;
        }

        ChessMove lastMove = getLastMove();
        if (lastMove == null) {
            logger.debug("No last move for en passant check");
            return false;
        }

        ChessPiece lastMovedPiece = getPiece(lastMove.end());
        if (!(lastMovedPiece instanceof Pawn) || Math.abs(lastMove.start().row() - lastMove.end().row()) != 2 || lastMove.end().row() != move.start().row() || Math.abs(lastMove.end().col() - move.start().col()) != 1) {
            logger.debug("Last move does not qualify for en passant");
            return false;
        }

        int direction = piece.getColor().isWhite() ? 1 : -1;
        ChessPosition targetPos = move.end();
        if (targetPos.row() != move.start().row() + direction || targetPos.col() != lastMove.end().col()) {
            logger.debug("Invalid en passant target position");
            return false;
        }

        // Kiểm tra hợp lệ khi bị chiếu
        ChessPieceMap tempMap = BoardUtils.simulateMove(move, getChessPieceMap());
        tempMap.removePiece(lastMove.end()); // Xóa quân tốt bị bắt
        if (BoardUtils.isKingInCheck(piece.getColor(), tempMap)) {
            logger.debug("En passant invalid under check");
            return false;
        }

        // Thực hiện nước đi
        ChessTile startTile = getTile(move.start());
        ChessTile endTile = getTile(move.end());
        ChessTile capturedTile = getTile(lastMove.end());

        removePiece(lastMove.end()); // Xóa quân tốt bị bắt
        removePiece(move.start());
        setPiece(move.end(), piece);
        setLastMove(move);

        startTile.repaint();
        endTile.repaint();
        capturedTile.repaint();

        logger.info("En passant performed: {} to {}, captured at {}", move.start().toChessNotation(), move.end().toChessNotation(), lastMove.end().toChessNotation());

        return true;
    }

    @Override
    public ChessPiece promotePawn(ChessPosition position, PieceColor color) {
        PromotionDialog dialog = new PromotionDialog(frame, color);
        dialog.setVisible(true);
        String selectedPiece = dialog.getSelectedPiece();
        ChessPiece promotedPiece;

        switch (selectedPiece) {
            case "Queen":
                promotedPiece = new Queen(color);
                break;
            case "Rook":
                promotedPiece = new Rook(color);
                break;
            case "Bishop":
                promotedPiece = new Bishop(color);
                break;
            case "Knight":
                promotedPiece = new Knight(color);
                break;
            default:
                promotedPiece = new Queen(color);
                logger.error("Invalid promotion choice: {}, defaulting to Queen", selectedPiece);
        }

        logger.info("Pawn promoted to {} at {}", selectedPiece, position.toChessNotation());
        return promotedPiece;
    }

    @Override
    public boolean movePiece(ChessMove move) {
        ChessPiece piece = getPiece(move.start());
        if (piece == null) {
            return false;
        }

        // Xử lý nhập thành
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
                setLastMove(move);
                return true;
            } else {
                SoundPlayer.playMoveIllegal();
                return false;
            }
        }

        // Xử lý bắt tốt qua đường
        if (piece instanceof Pawn && getPiece(move.end()) == null && move.start().col() != move.end().col() && Math.abs(move.start().row() - move.end().row()) == 1) {
            if (performEnPassant(move)) {
                SoundPlayer.playCaptureSound();
                halfmoveClock = 0; // Reset đồng hồ vì có bắt quân
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

        // Xử lý nước đi thông thường
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