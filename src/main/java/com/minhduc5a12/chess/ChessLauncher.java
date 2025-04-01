package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.GameMode;
import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.ui.GameModeSelectionDialog;

public class ChessLauncher {
    public static void launch() {
        // Hiển thị menu chọn chế độ chơi
        GameModeSelectionDialog dialog = new GameModeSelectionDialog(null);
        dialog.setVisible(true);

        // Lấy kết quả từ dialog
        int selectedMode = dialog.getSelectedMode();
        PieceColor selectedColor = dialog.getSelectedColor();

        // In ra lựa chọn của người dùng
        printSelectedOptions(selectedMode, selectedColor);

        // Khởi động giao diện với cấu hình dựa trên chế độ chơi
        ChessUI chessUI = new ChessUI();
        configureGame(chessUI.getChessController(), selectedMode, selectedColor);
        chessUI.show();
    }

    private static void printSelectedOptions(int mode, PieceColor color) {
        String modeStr;
        switch (mode) {
            case GameMode.PLAYER_VS_PLAYER:
                modeStr = "Người vs Người";
                System.out.println("Chế độ chơi: " + modeStr);
                break;
            case GameMode.PLAYER_VS_AI:
                modeStr = "Người vs Máy";
                System.out.println("Chế độ chơi: " + modeStr);
                System.out.println("Màu sắc: " + (color.isWhite() ? "Trắng" : "Đen"));
                break;
            case GameMode.AI_VS_AI:
                modeStr = "Máy vs Máy";
                System.out.println("Chế độ chơi: " + modeStr);
                break;
            default:
                modeStr = "Không xác định";
                System.out.println("Chế độ chơi: " + modeStr);
        }
    }

    private static void configureGame(ChessController controller, int mode, PieceColor playerColor) {
        switch (mode) {
            case GameMode.PLAYER_VS_PLAYER:
                // Không cần cấu hình gì thêm, cả hai đều là người chơi
                break;
            case GameMode.PLAYER_VS_AI:
                controller.setPlayerVsAI(playerColor); // Đặt chế độ người vs AI với màu người chơi
                break;
            case GameMode.AI_VS_AI:
                controller.setAIVsAI(); // Đặt chế độ AI vs AI
                break;
        }
    }
}