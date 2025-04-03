package com.minhduc5a12.chess.ui;

import com.minhduc5a12.chess.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MoveHistoryPanel extends JPanel {
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_BORDER_RADIUS = 30;

    public MoveHistoryPanel() {
        setOpaque(true);
        setBackground(new Color(40, 40, 40));
        setPreferredSize(new Dimension(800, 95));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel moveListPanel = new JPanel();
        moveListPanel.setLayout(new BoxLayout(moveListPanel, BoxLayout.X_AXIS));
        moveListPanel.setOpaque(false);

        List<String> moves = new ArrayList<>();
        moves.add("1. e4");
        moves.add("1...e5");
        moves.add("2. Nf3");
        moves.add("2...Nc6");
        moves.add("3. Bb5");
        moves.add("3...a6");
        moves.add("4. Ba4");
        moves.add("4...Nf6");
        moves.add("5. O-O");
        moves.add("5...Be7");
        moves.add("6. Re1");
        moves.add("6...b5");
        moves.add("7. Bb3");
        moves.add("7...d6");
        moves.add("8. c3");
        moves.add("8...O-O");
        moves.add("9. h3");
        moves.add("9...Nb8");
        moves.add("10. d4");
        moves.add("10...Nbd7");

        for (String move : moves) {
            JLabel moveLabel = new JLabel(move, SwingConstants.CENTER);
            moveLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
            moveLabel.setForeground(Color.WHITE);
            moveLabel.setPreferredSize(new Dimension(80, 30));
            moveListPanel.add(moveLabel);
            moveListPanel.add(Box.createHorizontalStrut(5));
        }

        JScrollPane scrollPane = new JScrollPane(moveListPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(740, 75));
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        scrollPane.setOpaque(true);
        scrollPane.setBackground(new Color(40, 40, 40));

        Image leftArrowImage = ImageLoader.getImage("images/left-arrow.png", 20, 20);
        JButton leftArrow = new RoundButton("");
        leftArrow.setIcon(new ImageIcon(leftArrowImage));
        leftArrow.setBackground(new Color(50, 50, 50));
        leftArrow.setFocusPainted(false);
        leftArrow.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        leftArrow.setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        leftArrow.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        leftArrow.addActionListener(e -> {
            JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();
            horizontalBar.setValue(horizontalBar.getValue() - 50);
        });

        JButton rightArrow = new RoundButton("");
        Image rightArrowImage = ImageLoader.getImage("images/right-arrow.png", 20, 20);
        rightArrow.setIcon(new ImageIcon(rightArrowImage));
        rightArrow.setBackground(new Color(50, 50, 50));
        rightArrow.setFocusPainted(false);
        rightArrow.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        rightArrow.setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        rightArrow.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        rightArrow.addActionListener(e -> {
            JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();
            horizontalBar.setValue(horizontalBar.getValue() + 50);
        });

        MouseAdapter hoverEffect = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((JButton) e.getSource()).setCursor(Cursor.getDefaultCursor());
            }
        };
        leftArrow.addMouseListener(hoverEffect);
        rightArrow.addMouseListener(hoverEffect);

        scrollPane.addMouseWheelListener(e -> {
            int units = e.getWheelRotation() * 20;
            JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();
            horizontalBar.setValue(horizontalBar.getValue() + units);
        });

        add(Box.createHorizontalGlue());
        add(leftArrow);
        add(Box.createHorizontalStrut(10));
        add(scrollPane);
        add(Box.createHorizontalStrut(10));
        add(rightArrow);
        add(Box.createHorizontalGlue());
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    private static class RoundButton extends JButton {
        public RoundButton(String text) {
            super(text);
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isArmed()) {
                g2d.setColor(getBackground().darker());
            } else {
                g2d.setColor(getBackground());
            }
            g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BUTTON_BORDER_RADIUS, BUTTON_BORDER_RADIUS);
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            // Không có border
        }

        @Override
        public boolean contains(int x, int y) {
            return new Rectangle(0, 0, getWidth(), getHeight()).contains(x, y);
        }
    }
}