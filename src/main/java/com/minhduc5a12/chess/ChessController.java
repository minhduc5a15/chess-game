package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.GameMode;
import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.model.ChessMove;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.pieces.*;
import com.minhduc5a12.chess.players.StockfishPlayer;
import com.minhduc5a12.chess.ui.GameOverDialog;
import com.minhduc5a12.chess.ui.PlayerPanel;
import com.minhduc5a12.chess.ui.PromotionDialog;
import com.minhduc5a12.chess.utils.BoardUtils;
import com.minhduc5a12.chess.utils.SoundPlayer;

import javax.swing.*;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChessController extends BoardManager implements MoveExecutor {
    private static final int FIFTY_MOVE_RULE_LIMIT = 50;
    private boolean gameEnded;
    private JFrame frame;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private StockfishPlayer stockfishPlayer;
    private int gameMode = GameMode.PLAYER_VS_PLAYER;
    private PieceColor humanPlayerColor;
    private final TreeMap<ChessPiece, Integer> whiteCapturedPieces; // Multiset cho các quân trắng bị ăn
    private final TreeMap<ChessPiece, Integer> blackCapturedPieces; // Multiset cho các quân đen bị ăn
    private PlayerPanel whitePlayerPanel; // Tham chiếu đến PlayerPanel của bên trắng
    private PlayerPanel blackPlayerPanel; // Tham chiếu đến PlayerPanel của bên đen

    public ChessController() {
        super();
        this.gameEnded = false;
        // Khởi tạo TreeMap với Comparator dựa trên pieceValue
        Comparator<ChessPiece> pieceComparator = Comparator.comparingInt(ChessPiece::getPieceValue);
        this.whiteCapturedPieces = new TreeMap<>(pieceComparator);
        this.blackCapturedPieces = new TreeMap<>(pieceComparator);
        setupInitialPosition();
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    // Phương thức để thiết lập các PlayerPanel
    public void setPlayerPanels(PlayerPanel whitePanel, PlayerPanel blackPanel) {
        this.whitePlayerPanel = whitePanel;
        this.blackPlayerPanel = blackPanel;
    }

    public void setPlayerVsAI(PieceColor humanColor) {
        this.gameMode = GameMode.PLAYER_VS_AI;
        this.humanPlayerColor = humanColor;
        this.stockfishPlayer = new StockfishPlayer(this, humanColor.getOpponent());
        if (humanColor.isBlack()) {
            stockfishPlayer.makeMove();
        }
    }

    public void setAIVsAI() {
        this.gameMode = GameMode.AI_VS_AI;
        this.stockfishPlayer = new StockfishPlayer(this, PieceColor.WHITE);
        stockfishPlayer.makeMove();
    }

    private void updatePlayerScores() {
        int materialAdvantage = getChessPieceMap().getMaterialAdvantage();
        if (whitePlayerPanel != null && blackPlayerPanel != null) {
            SwingUtilities.invokeLater(() -> {
                whitePlayerPanel.updateScore(materialAdvantage);
                blackPlayerPanel.updateScore(-materialAdvantage); // Điểm số ngược lại cho đen
            });
        }
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

        // Nếu có quân bị ăn, thêm vào danh sách tương ứng và cập nhật PlayerPanel
        if (isCapture) {
            ChessPiece capturedPiece = getPiece(move.end());
            if (capturedPiece.getColor().isWhite()) {
                whiteCapturedPieces.merge(capturedPiece, 1, Integer::sum); // Tăng số lượng trong multiset
                if (blackPlayerPanel != null) {
                    SwingUtilities.invokeLater(() -> blackPlayerPanel.addCapturedPiece(capturedPiece));
                }
            } else {
                blackCapturedPieces.merge(capturedPiece, 1, Integer::sum); // Tăng số lượng trong multiset
                if (whitePlayerPanel != null) {
                    SwingUtilities.invokeLater(() -> whitePlayerPanel.addCapturedPiece(capturedPiece));
                }
            }
        }

        if (piece instanceof Pawn && (move.end().row() == 7 || move.end().row() == 0)) {
            piece = promotePawn(move.end(), piece.getColor());
            SoundPlayer.playMoveSound();
        }
        removePiece(move.end());
        setPiece(move.end(), piece);
        removePiece(move.start());

        setLastMove(move);
        updatePieceMovement(move);

        updateBoardStateHistory();
        if (BoardUtils.isThreefoldRepetition(this)) {
            gameEnded = true;
            SwingUtilities.invokeLater(() -> {
                GameOverDialog dialog = new GameOverDialog(frame, "Hòa do lặp lại 3 lần!");
                dialog.setVisible(true);
            });
            logger.info("Game ended due to threefold repetition (FIDE)");
            repaintTiles(startTile, endTile);
            return true;
        }

        repaintTiles(startTile, endTile);
        if (isCapture || isPawnMove) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
        }

        switchTurn();
        updatePlayerScores();

        boolean isCheck = BoardUtils.isKingInCheck(currentPlayerColor, getChessPieceMap());

        if (isCheck) {
            SoundPlayer.playMoveCheckSound();
        } else if (isCapture) {
            SoundPlayer.playCaptureSound();
        } else {
            SoundPlayer.playMoveSound();
        }

        logger.debug("Executed move: {} to {}", move.start().toChessNotation(), move.end().toChessNotation());

        executor.submit(this::checkGameEndConditions);

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

        setLastMove(new ChessMove(kingPos, new ChessPosition(kingTargetCol, kingRow)));
        updateBoardStateHistory();

        repaintTiles(kingStartTile, kingEndTile, rookStartTile, rookEndTile);
        logger.debug("Castling performed: {} for {}", isKingside ? "Kingside" : "Queenside", color);

        switchTurn();
        updatePlayerScores();
        halfmoveClock++;
        executor.submit(this::checkGameEndConditions);

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

        ChessPieceMap tempMap = BoardUtils.simulateMove(move, getChessPieceMap());
        tempMap.removePiece(lastMove.end());
        if (BoardUtils.isKingInCheck(piece.getColor(), tempMap)) {
            logger.debug("En passant invalid under check");
            return false;
        }

        ChessTile startTile = getTile(move.start());
        ChessTile endTile = getTile(move.end());
        ChessTile capturedTile = getTile(lastMove.end());

        // Thêm quân bị ăn (qua en passant) vào danh sách tương ứng và cập nhật PlayerPanel
        ChessPiece capturedPiece = getPiece(lastMove.end());
        if (capturedPiece.getColor().isWhite()) {
            whiteCapturedPieces.merge(capturedPiece, 1, Integer::sum); // Tăng số lượng trong multiset
            if (blackPlayerPanel != null) {
                SwingUtilities.invokeLater(() -> blackPlayerPanel.addCapturedPiece(capturedPiece));
            }
        } else {
            blackCapturedPieces.merge(capturedPiece, 1, Integer::sum); // Tăng số lượng trong multiset
            if (whitePlayerPanel != null) {
                SwingUtilities.invokeLater(() -> whitePlayerPanel.addCapturedPiece(capturedPiece));
            }
        }

        removePiece(lastMove.end());
        removePiece(move.start());
        setPiece(move.end(), piece);
        setLastMove(move);
        updatePieceMovement(move);
        updateBoardStateHistory();

        repaintTiles(startTile, endTile, capturedTile);
        logger.info("En passant performed: {} to {}, captured at {}", move.start().toChessNotation(), move.end().toChessNotation(), lastMove.end().toChessNotation());

        halfmoveClock = 0;
        switchTurn();
        updatePlayerScores();
        executor.submit(this::checkGameEndConditions);

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

    public boolean movePiece(ChessMove move) {
        ChessPiece piece = getPiece(move.start());
        boolean moveSuccessful = false;

        switch (piece) {
            case null -> {
                return false;
            }
            case King king when Math.abs(move.end().col() - move.start().col()) == 2 -> {
                boolean isKingside = move.end().col() > move.start().col();
                if (performCastling(isKingside, piece.getColor())) {
                    SoundPlayer.playCastleSound();
                    moveSuccessful = true;
                } else {
                    SoundPlayer.playMoveIllegal();
                }
            }
            case Pawn pawn when getPiece(move.end()) == null && move.start().col() != move.end().col() && Math.abs(move.start().row() - move.end().row()) == 1 -> {
                if (performEnPassant(move)) {
                    SoundPlayer.playCaptureSound();
                    moveSuccessful = true;
                } else {
                    SoundPlayer.playMoveIllegal();
                }
            }
            default -> {
                moveSuccessful = executeMove(move);
            }
        }

        if (moveSuccessful && stockfishPlayer != null) {
            if (gameMode == GameMode.PLAYER_VS_AI && currentPlayerColor != humanPlayerColor) {
                stockfishPlayer.makeMove();
            } else if (gameMode == GameMode.AI_VS_AI) {
                stockfishPlayer.makeMove();
            }
        }

        return moveSuccessful;
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
        if (this.gameMode == GameMode.AI_VS_AI) {
            stockfishPlayer.shutdown();
        }
    }

    private void checkGameEndConditions() {
        if (BoardUtils.isCheckmate(currentPlayerColor, getChessPieceMap())) {
            gameEnded = true;
            SwingUtilities.invokeLater(this::showGameOverDialog);
        } else if (halfmoveClock >= FIFTY_MOVE_RULE_LIMIT) {
            gameEnded = true;
            SwingUtilities.invokeLater(() -> {
                GameOverDialog dialog = new GameOverDialog(frame, "Hòa do luật 50 nước!");
                dialog.setVisible(true);
            });
            logger.info("Game ended due to 50-move rule");
        } else if (BoardUtils.isDeadPosition(getChessPieceMap())) {
            gameEnded = true;
            SwingUtilities.invokeLater(() -> {
                GameOverDialog dialog = new GameOverDialog(frame, "Hòa do không đủ quân để chiếu hết!");
                dialog.setVisible(true);
            });
            logger.info("Game ended due to dead position (insufficient material)");
        } else if (BoardUtils.isStalemate(currentPlayerColor, getChessPieceMap())) {
            gameEnded = true;
            SwingUtilities.invokeLater(() -> {
                GameOverDialog dialog = new GameOverDialog(frame, "Hòa do bất biến (Stalemate)!");
                dialog.setVisible(true);
            });
            logger.info("Game ended due to stalemate");
        }
    }

    public int getGameMode() {
        return gameMode;
    }

    public PieceColor getHumanPlayerColor() {
        if (gameMode == GameMode.PLAYER_VS_AI) {
            return humanPlayerColor;
        }
        return null;
    }
}