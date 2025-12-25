package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel {

    private String title;
    private int padding = 16;

    /* =====================
       CONSTRUCTEURS SUPPORTÉS
       ===================== */

    public CardPanel() {
        this(null, new BorderLayout());
    }

    public CardPanel(String title) {
        this(title, new BorderLayout());
    }

    public CardPanel(LayoutManager layout) {
        this(null, layout);
    }

    public CardPanel(String title, LayoutManager layout) {
        this.title = title;
        setLayout(layout);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(
                padding + (title != null ? 24 : 0),
                padding,
                padding,
                padding
        ));
    }

    /* =====================
       RENDER MAQUETTE
       ===================== */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = DentalTheme.RADIUS;
        int w = getWidth();
        int h = getHeight();

        // Ombre
        g2.setColor(new Color(0, 0, 0, 25));
        g2.fillRoundRect(6, 6, w - 12, h - 12, arc, arc);

        // Fond
        g2.setColor(DentalTheme.PANEL);
        g2.fillRoundRect(0, 0, w - 12, h - 12, arc, arc);

        // Bordure dorée
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(DentalTheme.STROKE);
        g2.drawRoundRect(0, 0, w - 12, h - 12, arc, arc);

        // Titre (si présent)
        if (title != null) {
            g2.setFont(DentalTheme.H2);
            g2.setColor(DentalTheme.TEXT);
            g2.drawString(title, 24, 34);
        }

        g2.dispose();
    }
}
