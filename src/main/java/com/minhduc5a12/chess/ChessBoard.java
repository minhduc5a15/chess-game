package com.minhduc5a12.chess;

import com.minhduc5a12.chess.utils.ImageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class ChessBoard extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(ChessBoard.class);
    private static final int BOARD_SIZE = 800;
    private static final int TILE_SIZE = BOARD_SIZE / 8;
    private final GameController gameController;
    private final Image boardImage;

    public ChessBoard(GameController gameController) {
        this.gameController = gameController;
        this.boardImage = ImageLoader.getImage("images/chessboard.png", BOARD_SIZE, BOARD_SIZE);
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        setOpaque(true);
        setLayout(new GridLayout(8, 8, 0, 0));
        initializeBoard();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawDefaultBoard(g);
    }

    private void initializeBoard() {
        ChessTile[][] tiles = gameController.getTiles();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                add(tiles[row][col]);
            }
        }
    }

    private void drawDefaultBoard(Graphics g) {
        g.drawImage(this.boardImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }
}