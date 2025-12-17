package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NavButton extends JButton {

    public NavButton(String text, boolean active) {
        super(text);
        setFocusPainted(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setFont(DentalTheme.textBold(12));
        setBorder(new EmptyBorder(10, 14, 10, 14));

        if (active) {
            setBackground(DentalTheme.PRIMARY_DARK);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DentalTheme.GOLD, 2, true),
                    getBorder()
            ));
        } else {
            setBackground(DentalTheme.BG);
            setForeground(DentalTheme.PRIMARY_DARK);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DentalTheme.GOLD, 2, true),
                    getBorder()
            ));
        }
    }
}
