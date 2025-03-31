package com.minhduc5a12.chess.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageLoader {
    private static final Map<String, Image> imageCache = new HashMap<>();

    public static Image getImage(String path, int width, int height) {
        return imageCache.computeIfAbsent(path, k -> {
            try {
                java.net.URL imageUrl = ImageLoader.class.getClassLoader().getResource(path);
                if (imageUrl == null) throw new IOException("Cannot find image: " + path);
                Image img = ImageIO.read(imageUrl);
                return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                System.err.println("Failed to load image: " + path);
                return null;
            }
        });
    }

    public static void preloadImages() {
        String[] paths = {"images/white_pawn.png", "images/black_pawn.png", "images/white_rook.png", "images/black_rook.png", "images/white_knight.png", "images/black_knight.png", "images/white_bishop.png", "images/black_bishop.png", "images/white_queen.png", "images/black_queen.png", "images/white_king.png", "images/black_king.png"};
        for (String path : paths) {
            getImage(path, 95, 95);
        }
        getImage("images/chessboard.png", 800, 800);
    }

    public static Image rotateImage(BufferedImage original, double degrees) {
        int w = original.getWidth();
        int h = original.getHeight();
        BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        g2d.rotate(Math.toRadians(degrees), w / 2.0, h / 2.0);
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        return rotated.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }
}