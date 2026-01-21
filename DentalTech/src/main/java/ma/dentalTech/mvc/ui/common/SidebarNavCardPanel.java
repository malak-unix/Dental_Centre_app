package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;

public class SidebarNavCardPanel extends JPanel {

    private final int arc = DentalTheme.RADIUS;

    public SidebarNavCardPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // padding interne (pour les boutons)
        setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 14));

        // ✅ pour étirement full width
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // ✅ shadow
        g2.setColor(new Color(0, 0, 0, 18));
        g2.fillRoundRect(4, 4, w - 8, h - 8, arc, arc);

        // ✅ fill
        g2.setColor(DentalTheme.PANEL);
        g2.fillRoundRect(0, 0, w - 8, h - 8, arc, arc);

        // ✅ stroke
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(DentalTheme.STROKE);
        g2.drawRoundRect(0, 0, w - 8, h - 8, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
