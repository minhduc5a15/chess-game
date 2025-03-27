package com.minhduc5a12.chess;

import javax.swing.JFrame;

import com.minhduc5a12.chess.utils.ImageLoader;
import com.minhduc5a12.chess.utils.SoundPlayer;

public class Main {
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
        frame.add(new ChessBoard(gameController));
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