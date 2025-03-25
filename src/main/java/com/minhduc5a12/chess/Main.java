package com.minhduc5a12.chess;

import javax.swing.JFrame;

import com.minhduc5a12.chess.utils.ImageLoader;

public class Main {
    public static void main(String[] args) {
        ImageLoader.preloadImages();

        GameController matrix = new GameController();
        matrix.setupInitialPosition();

        JFrame frame = new JFrame("Chess Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChessBoard(matrix));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}