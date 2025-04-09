package com.minhduc5a12.chess;

import com.minhduc5a12.chess.constants.GameMode;
import com.minhduc5a12.chess.constants.PieceColor;
import com.minhduc5a12.chess.ui.GameModeSelectionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChessLauncher {
    private static final Logger log = LoggerFactory.getLogger(ChessLauncher.class);

    public static void launch() {
        GameModeSelectionDialog dialog = new GameModeSelectionDialog(null);
        dialog.setVisible(true);

        int selectedMode = dialog.getSelectedMode();
        PieceColor selectedColor = dialog.getSelectedColor();

        ChessUI chessUI = new ChessUI(selectedMode, selectedColor);
        chessUI.show();
    }

    private static void configureGame(ChessController controller, int mode, PieceColor playerColor) {
        switch (mode) {
            case GameMode.PLAYER_VS_PLAYER:
                break;
            case GameMode.PLAYER_VS_AI:
                controller.setPlayerVsAI(playerColor);
                break;
            case GameMode.AI_VS_AI:
                controller.setAIVsAI();
                break;
        }
    }
}