package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DentalButton extends JButton {

    public DentalButton(String text) {
        super(text);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBackground(DentalTheme.PRIMARY_DARK);
        setForeground(Color.WHITE);
        setFont(DentalTheme.textBold(12));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.GOLD, 2, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
    }
}
