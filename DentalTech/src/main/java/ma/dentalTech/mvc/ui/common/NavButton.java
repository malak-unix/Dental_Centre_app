package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;

public class NavButton extends JButton {

    private boolean active;

    public NavButton(String text, boolean active) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFont(DentalTheme.textBold(12));
        setActive(active);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setActive(boolean active) {
        this.active = active;

        if (active) {
            setBackground(DentalTheme.PRIMARY);
            setForeground(Color.WHITE);
        } else {
            setBackground(DentalTheme.CARD);
            setForeground(DentalTheme.PRIMARY_DARK);
        }
    }

    public boolean isActive() {
        return active;
    }
}
