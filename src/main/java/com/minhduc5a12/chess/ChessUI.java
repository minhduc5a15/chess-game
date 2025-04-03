package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.ui.MoveHistoryPanel;
import com.minhduc5a12.chess.ui.PlayerPanel;

import javax.swing.*;
import java.awt.*;

public class ChessUI {
    private final ChessController chessController;
    private final JFrame frame;

    public ChessUI() {
        this.chessController = new ChessController();
        this.chessController.setupInitialPosition();
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

        // Player Panels
        PlayerPanel player1Panel = new PlayerPanel("Player 1", PieceColor.WHITE, "images/player.png");
        mainPanel.add(player1Panel, BorderLayout.WEST);

        PlayerPanel player2Panel = new PlayerPanel("Player 2", PieceColor.BLACK, "images/player.png");
        mainPanel.add(player2Panel, BorderLayout.EAST);

        // Move History Panel
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

    public ChessController getChessController() {
        return chessController;
    }
}