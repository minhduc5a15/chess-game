package com.minhduc5a12.chess.ui;

import com.minhduc5a12.chess.ChessController;
import com.minhduc5a12.chess.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ChessToolbar extends JPanel {
    private final ChessController chessController;
    private static final int ICON_SIZE = 24;
    private static final int TOOLBAR_HEIGHT = 50;

    public ChessToolbar(ChessController controller) {
        this.chessController = controller;
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        setLayout(new GridLayout(1, 0, 10, 0));
        setPreferredSize(new Dimension(0, TOOLBAR_HEIGHT));

        initializeButtons();
    }

    private void initializeButtons() {
        add(createButton("Undo", "images/resign.png", e -> {
        }));

        add(createButton("Flip Board", "images/flip-board.png", e -> {
        }));

        add(createButton("Resign", "images/resign.png", e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to resign?", "Resign Game", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                chessController.resignGame();
            }
        }));

        add(createButton("Exit", "images/resign.png", e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Game", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                chessController.shutdown();
                System.exit(0);
            }
        }));
    }

    private JButton createButton(String tooltip, String imgPath, ActionListener action) {
        Image iconImage = ImageLoader.getImage(imgPath, ICON_SIZE, ICON_SIZE);
        JButton button = new JButton(new ImageIcon(iconImage));
        button.setToolTipText(tooltip);
        button.setBackground(new Color(40, 40, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setFocusPainted(false);
        button.addActionListener(action);
        return button;
    }
}