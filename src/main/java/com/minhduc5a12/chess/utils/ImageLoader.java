package com.minhduc5a12.chess.utils;

import java.awt.Image;
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
    }
}