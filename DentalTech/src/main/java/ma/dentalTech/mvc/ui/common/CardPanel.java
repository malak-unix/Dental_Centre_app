package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CardPanel extends JPanel {

    private String title;

    private int arc = 22;
    private int shadowSize = 10;     // épaisseur du shadow
    private int shadowOffset = 6;    // décalage du shadow (en bas/droite)

    public CardPanel() {
        this(null);
    }

    public CardPanel(String title) {
        this.title = title;
        setOpaque(false);
        setLayout(new BorderLayout());
        // padding interne (contenu)
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    public void setTitle(String title) {
        this.title = title;
        repaint();
    }

    public void setArc(int arc) {
        this.arc = arc;
        repaint();
    }

    public void setShadow(int shadowSize, int shadowOffset) {
        this.shadowSize = shadowSize;
        this.shadowOffset = shadowOffset;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // IMPORTANT: on dessine la carte à x=0,y=0 (donc collée à gauche si le conteneur le permet)
            int cardX = 0;
            int cardY = 0;
            int cardW = w - shadowOffset;
            int cardH = h - shadowOffset;

            // Shadow
            g2.setColor(new Color(0, 0, 0, 28));
            g2.fillRoundRect(cardX + shadowOffset, cardY + shadowOffset, cardW, cardH, arc, arc);

            // Card background
            g2.setColor(getBackground() != null ? getBackground() : new Color(250, 246, 240));
            g2.fillRoundRect(cardX, cardY, cardW, cardH, arc, arc);

            // Border
            g2.setColor(DentalTheme.BORDER);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(cardX, cardY, cardW, cardH, arc, arc);

            // Title
            if (title != null && !title.isBlank()) {
                g2.setFont(DentalTheme.titleFont(18));
                g2.setColor(DentalTheme.TEXT1);
                g2.drawString(title, cardX + 18, cardY + 28);
            }
        } finally {
            g2.dispose();
        }

        super.paintComponent(g);
    }
}
