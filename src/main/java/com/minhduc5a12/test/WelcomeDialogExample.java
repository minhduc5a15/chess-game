package com.minhduc5a12.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeDialogExample {
    public static void main(String[] args) {
        // Tạo JFrame chính
        JFrame frame = new JFrame("Ứng dụng chính");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        // Tạo JButton để mở JDialog
        JButton openDialogButton = new JButton("Nhấn để chào mừng!");
        openDialogButton.setFont(new Font("Arial", Font.BOLD, 16));
        openDialogButton.setBackground(Color.CYAN);
        openDialogButton.setForeground(Color.BLUE);

        // Thêm sự kiện cho nút
        openDialogButton.addActionListener(e -> showWelcomeDialog(frame));

        // Thêm nút vào JFrame
        frame.add(openDialogButton, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void showWelcomeDialog(JFrame parent) {
        // Tạo JDialog
        JDialog dialog = new JDialog(parent, "Chào mừng bạn!", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        // Tạo nhãn chào mừng với hiệu ứng gradient
        JLabel welcomeLabel = new JLabel("Chào mừng đến với thế giới Java!", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, Color.RED, getWidth(), getHeight(), Color.YELLOW);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        welcomeLabel.setFont(new Font("Arial   ", Font.ITALIC, 20));
        welcomeLabel.setForeground(Color.BLACK);

        // Tạo nút OK với hiệu ứng hover
        JButton okButton = getJButton(dialog);

        // Thêm các thành phần vào JDialog
        dialog.add(welcomeLabel, BorderLayout.CENTER);
        dialog.add(okButton, BorderLayout.SOUTH);

        // Hiệu ứng nhỏ: làm dialog rung nhẹ khi xuất hiện
        new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    dialog.setLocation(dialog.getX() + (i % 2 == 0 ? 5 : -5), dialog.getY());
                    Thread.sleep(50);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();

        // Hiển thị dialog
        dialog.setVisible(true);
    }

    private static JButton getJButton(JDialog dialog) {
        JButton okButton = new JButton("OK") {
            @Override
            public void paintComponent(Graphics g) {
                if (getModel().isRollover()) {
                    setBackground(Color.GREEN);
                } else {
                    setBackground(Color.LIGHT_GRAY);
                }
                super.paintComponent(g);
            }
        };
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setFocusPainted(false);

        // Thêm sự kiện đóng dialog khi nhấn OK
        okButton.addActionListener(e -> dialog.dispose());
        return okButton;
    }
}