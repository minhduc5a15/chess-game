package com.minhduc5a12.chess;

import javax.swing.JFrame;

import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.pieces.Pawn;
import com.minhduc5a12.chess.utils.ImageLoader;

public class Main {
    public static void main(String[] args) {
        ImageLoader.preloadImages();
        System.out.println("Hello, Chess!");

        JFrame frame = new JFrame("Chess Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); 
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}