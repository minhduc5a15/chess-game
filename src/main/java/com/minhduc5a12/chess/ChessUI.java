package com.minhduc5a12.chess;
import javax.swing.*;
import java.awt.*;

public class ChessUI {
    private final ChessController chessController;
    private final JFrame frame;

    public ChessUI() {
        this.chessController = new ChessController();
        this.chessController.setupInitialPosition();
        this.frame = new JFrame("Chess Board");
        setupUI();
    }

    private void setupUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());

        ChessBoard chessBoard = new ChessBoard(chessController);
        mainPanel.add(chessBoard, BorderLayout.CENTER);

        JPanel topPlayerPanel = createPlayerPanel("Player 1 (White)");
        mainPanel.add(topPlayerPanel, BorderLayout.NORTH);

        JPanel bottomPlayerPanel = createPlayerPanel("Player 2 (Black)");
        mainPanel.add(bottomPlayerPanel, BorderLayout.SOUTH);

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

    private JPanel createPlayerPanel(String playerName) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(800, 100));
        panel.setBackground(Color.LIGHT_GRAY);
        JLabel label = new JLabel(playerName);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label);
        return panel;
    }

    public void show() {
        frame.setVisible(true);
    }

    public ChessController getChessController() {
        return chessController;
    }
}