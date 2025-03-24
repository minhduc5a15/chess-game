package com.minhduc5a12.chess;

import com.minhduc5a12.chess.model.ChessMatrix;
import com.minhduc5a12.chess.model.ChessPosition;
import com.minhduc5a12.chess.pieces.ChessPiece;
import com.minhduc5a12.chess.utils.ImageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ChessBoard extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(ChessBoard.class);
    private static final int BOARD_SIZE = 800;
    private static final int TILE_SIZE = BOARD_SIZE / 8;
    private static final int PIECE_SIZE = 95;
    private final ChessMatrix chessMatrix;
    private final Image boardImage;

    public ChessBoard(ChessMatrix chessMatrix) {
        this.chessMatrix = chessMatrix;
        this.boardImage = ImageLoader.getImage("images/chessboard.png", BOARD_SIZE, BOARD_SIZE);
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        setOpaque(true);
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawDefaultBoard(g);
        drawPieces(g);
    }

    private void drawDefaultBoard(Graphics g) {
        g.drawImage(this.boardImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }

    private void drawPieces(Graphics g) {
        logger.debug("Drawing chess pieces...");
        Map<ChessPosition, ChessPiece> pieceMap = chessMatrix.getChessPieceMap();
        for (Map.Entry<ChessPosition, ChessPiece> entry : pieceMap.entrySet()) {
            ChessPosition pos = entry.getKey();
            ChessPiece piece = entry.getValue();

            // Chuyển từ tọa độ Descartes sang tọa độ màn hình (matrix)
            int[] matrixCoords = pos.toMatrixCoords(); // [matrixRow, matrixCol]
            int x = matrixCoords[1] * TILE_SIZE; // matrixCol
            int y = matrixCoords[0] * TILE_SIZE; // matrixRow

            Image pieceImage = piece.getImage();
            if (pieceImage != null) {
                int offset = (TILE_SIZE - PIECE_SIZE) / 2;
                g.drawImage(pieceImage, x + offset, y + offset, PIECE_SIZE, PIECE_SIZE, null);
            } else {
                logger.warn("No image for piece at position: {}", pos.toChessNotation());
            }
        }
        logger.debug("Finished drawing chess pieces.");
    }

    public static void main(String[] args) {
        ImageLoader.preloadImages();

        // Tạo bàn cờ và đặt quân cờ
        ChessMatrix matrix = new ChessMatrix();
        matrix.setupInitialPosition();

        JFrame frame = new JFrame("Chess Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChessBoard(matrix));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}