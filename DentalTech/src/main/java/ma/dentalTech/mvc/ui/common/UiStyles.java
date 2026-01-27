package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UiStyles {

    private UiStyles() {}

    public static void stylePrimaryButton(AbstractButton b) {
        if (b == null) return;
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(DentalTheme.textBold(12));
        b.setForeground(Color.WHITE);

        if (!(b instanceof DentalButton)) {
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.setBackground(DentalTheme.PRIMARY_DARK);
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
        } else {
            b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        }
    }

    public static void styleSecondaryButton(AbstractButton b) {
        if (b == null) return;
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(DentalTheme.textBold(12));
        b.setForeground(Color.WHITE);

        // Même style global que les boutons principaux : fond bleu, bordure dorée
        if (!(b instanceof DentalButton)) {
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.setBackground(DentalTheme.PRIMARY);
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
        } else {
            b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        }
    }

    public static void stylePillButton(AbstractButton b, boolean selected) {
        if (b == null) return;
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(DentalTheme.textBold(12));
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)
        ));
        if (selected) {
            b.setBackground(DentalTheme.PRIMARY_DARK);
            b.setForeground(Color.WHITE);
        } else {
            b.setBackground(DentalTheme.CARD);
            b.setForeground(DentalTheme.PRIMARY_DARK);
        }
    }

    public static void styleTable(JTable table) {
        if (table == null) return;
        table.setRowHeight(30);
        table.setFont(DentalTheme.textFont(12));
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setFont(DentalTheme.textBold(12));
            header.setBackground(DentalTheme.CARD);
            header.setForeground(DentalTheme.TEXT1);
        }
    }

    public static Border roundedBorder() {
        return BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true);
    }

    public static Border shadowBorder() {
        return BorderFactory.createEmptyBorder(6, 6, 6, 6);
    }
}
