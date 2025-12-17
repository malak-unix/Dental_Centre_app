package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CardPanel extends JPanel {

    public CardPanel(String title) {
        setBackground(DentalTheme.BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        setLayout(new BorderLayout(8, 8));

        JLabel t = new JLabel(title);
        t.setFont(DentalTheme.titleFont(14));
        t.setForeground(DentalTheme.PRIMARY_DARK);
        add(t, BorderLayout.NORTH);
    }
}
