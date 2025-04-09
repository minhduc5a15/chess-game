package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.GameMode;
import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.ui.MoveHistoryPanel;
import com.minhduc5a12.chess.ui.PlayerPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

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

        switch (chessController.getGameMode()) {
            case GameMode.AI_VS_AI -> {
                PlayerPanel playerPanel = new PlayerPanel("AI", PieceColor.WHITE, "images/stockfish.png");
                mainPanel.add(playerPanel, BorderLayout.WEST);
                playerPanel = new PlayerPanel("AI", PieceColor.BLACK, "images/stockfish.png");
                mainPanel.add(playerPanel, BorderLayout.EAST);
            }
            case GameMode.PLAYER_VS_AI -> {
                PieceColor playerColor = chessController.getHumanPlayerColor();
                PlayerPanel playerPanel = new PlayerPanel("You", playerColor, "images/player.png");
                mainPanel.add(playerPanel, BorderLayout.WEST);
                playerPanel = new PlayerPanel("AI", playerColor.isWhite() ? PieceColor.BLACK : PieceColor.WHITE, "images/stockfish.png");
                mainPanel.add(playerPanel, BorderLayout.EAST);
            }
            case GameMode.PLAYER_VS_PLAYER -> {
                PlayerPanel player1Panel = new PlayerPanel("Player 1", PieceColor.WHITE, "images/player.png");
                mainPanel.add(player1Panel, BorderLayout.WEST);
                PlayerPanel player2Panel = new PlayerPanel("Player 2", PieceColor.BLACK, "images/player.png");
                mainPanel.add(player2Panel, BorderLayout.EAST);
            }

            default -> {
                logger.info("No game mode were selected!");
                return;
            }
        }


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
            case GameMode.PLAYER_VS_PLAYER:
                break;
            case GameMode.PLAYER_VS_AI:
                controller.setPlayerVsAI(playerColor);
                break;
            case GameMode.AI_VS_AI:
                controller.setAIVsAI();
                break;
        }
    }
}