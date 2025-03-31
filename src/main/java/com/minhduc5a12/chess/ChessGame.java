package com.minhduc5a12.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.minhduc5a12.chess.utils.ImageLoader;
import com.minhduc5a12.chess.utils.SoundPlayer;

public class ChessGame {
    private static ChessBoard chessBoard;
    private static boolean isBoardFlipped = false;

    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.setProperty("sun.java2d.uiScale", "1.0");
            System.setProperty("sun.java2d.dpiaware", "true");
        }

        ImageLoader.preloadImages();
        SoundPlayer.preloadSounds();
        startNewGame();
    }

    public static void startNewGame() {
        GameController gameController = new GameController();
        gameController.setupInitialPosition();

        JFrame frame = new JFrame("Chess Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo ChessBoard
        chessBoard = new ChessBoard(gameController);

        // Tạo nút "Xoay bàn cờ"
        JButton flipBoardButton = new JButton("Xoay bàn cờ");
        flipBoardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBoardFlipped = !isBoardFlipped;
                chessBoard.flipBoard(); // Gọi phương thức xoay bàn cờ
            }
        });

        // Tạo panel chứa ChessBoard và nút
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(chessBoard, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(flipBoardButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);

        gameController.setFrame(frame);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                gameController.shutdown();
            }
        });
    }
}