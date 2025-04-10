package com.minhduc5a12.chess;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.minhduc5a12.chess.constants.GameMode;
import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.ui.MoveHistoryPanel;
import com.minhduc5a12.chess.ui.PlayerPanel;

public class ChessUI {
    private final ChessController chessController;
    private final JFrame frame;
    private static final Logger logger = LoggerFactory.getLogger(ChessUI.class);

    public ChessUI(int gameMode, PieceColor selectedColor) {
        this.chessController = new ChessController();
        this.chessController.setupInitialPosition();
        configureGame(this.chessController, gameMode, selectedColor);
        this.frame = new JFrame("Chess Game");
        setupUI();
    }

    private void setupUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        // ChessBoard
        ChessBoard chessBoard = new ChessBoard(chessController);
        mainPanel.add(chessBoard, BorderLayout.CENTER);

        logger.info("Game mode selected {}", chessController.getGameMode());

        PlayerPanel whitePlayerPanel;
        PlayerPanel blackPlayerPanel;
        switch (chessController.getGameMode()) {
            case GameMode.AI_VS_AI -> {
                whitePlayerPanel = new PlayerPanel("AI", PieceColor.WHITE, "images/stockfish.png");
                mainPanel.add(whitePlayerPanel, BorderLayout.WEST);
                blackPlayerPanel = new PlayerPanel("AI", PieceColor.BLACK, "images/stockfish.png");
                mainPanel.add(blackPlayerPanel, BorderLayout.EAST);
            }
            case GameMode.PLAYER_VS_AI -> {
                PieceColor playerColor = chessController.getHumanPlayerColor();
                whitePlayerPanel = new PlayerPanel("You", playerColor, "images/player.png");
                mainPanel.add(whitePlayerPanel, BorderLayout.WEST);
                blackPlayerPanel = new PlayerPanel("AI", playerColor.isWhite() ? PieceColor.BLACK : PieceColor.WHITE, "images/stockfish.png");
                mainPanel.add(blackPlayerPanel, BorderLayout.EAST);
            }
            case GameMode.PLAYER_VS_PLAYER -> {
                whitePlayerPanel = new PlayerPanel("Player 1", PieceColor.WHITE, "images/player.png");
                mainPanel.add(whitePlayerPanel, BorderLayout.WEST);
                blackPlayerPanel = new PlayerPanel("Player 2", PieceColor.BLACK, "images/player.png");
                mainPanel.add(blackPlayerPanel, BorderLayout.EAST);
            }
            default -> {
                logger.info("No game mode were selected!");
                return;
            }
        }

        chessController.setPlayerPanels(whitePlayerPanel, blackPlayerPanel);

        MoveHistoryPanel moveHistoryPanel = new MoveHistoryPanel();
        mainPanel.add(moveHistoryPanel, BorderLayout.NORTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        chessController.setFrame(frame);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                chessController.shutdown();
            }
        });
    }

    public void show() {
        frame.setVisible(true);
    }

    private void configureGame(ChessController controller, int mode, PieceColor playerColor) {
        switch (mode) {
            case GameMode.PLAYER_VS_PLAYER -> {
            }
            case GameMode.PLAYER_VS_AI -> controller.setPlayerVsAI(playerColor);
            case GameMode.AI_VS_AI -> controller.setAIVsAI();
        }
    }
}