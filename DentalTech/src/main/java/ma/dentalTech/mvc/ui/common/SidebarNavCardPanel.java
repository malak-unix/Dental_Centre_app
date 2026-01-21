package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;

public class SidebarNavCardPanel extends JPanel {

    private final int arc = DentalTheme.RADIUS;
    private static final int SIDE_MARGIN = 16; // space on each side


    public SidebarNavCardPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // padding interne (pour les boutons)
        setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        Container p = getParent();
        if (p != null && p.getWidth() > 0) {
            d.width = p.getWidth();
        }
        return d;
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        Container p = getParent();
        if (p != null && p.getWidth() > 0) {
            d.width = p.getWidth();
        }
        return d;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int shadow = 4;

        // shadow (right/bottom)
        g2.setColor(new Color(0, 0, 0, 18));
        g2.fillRoundRect(shadow, shadow, w - shadow - 1, h - shadow - 1, arc, arc);

        // fill (flush left)
        g2.setColor(DentalTheme.PANEL);
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // stroke
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(DentalTheme.STROKE);
        g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
