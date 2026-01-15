package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public final class UiTokens {

    private UiTokens() {}

    // Palette standard
    public static final Color BG_APP = new Color(0xF3E6D8);
    public static final Color BG_CARD = new Color(0xF7F7F7);
    public static final Color BORDER_GOLD = new Color(0xC9A56A);
    public static final Color NAVY = new Color(0x15263F);

    public static final int PAD = 14;

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GOLD, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }

    public static Border sectionBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GOLD, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    }

    public static void stylePrimaryButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(NAVY);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GOLD, 1, true),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
