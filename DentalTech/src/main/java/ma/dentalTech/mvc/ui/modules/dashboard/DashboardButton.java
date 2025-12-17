package ma.dentalTech.mvc.ui.modules.dashboard;

import javax.swing.*;
import java.awt.*;

public class DashboardButton extends JButton {

    // Couleurs selon la charte
    private static final Color BUTTON_COLOR = new Color(0x1C2541); // Bleu foncé

    public DashboardButton(String text) {
        super(text);

        setFont(new Font("Roboto", Font.BOLD, 14));
        setBackground(BUTTON_COLOR);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createLineBorder(BUTTON_COLOR));
        setPreferredSize(new Dimension(160, 40));
    }
}
