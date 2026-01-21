package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NavButton extends JButton {

    private boolean active = false;
    private boolean hover = false;

    public NavButton(String text) {
        this(text, (Icon) null, false);
    }

    public NavButton(String text, boolean active) {
        this(text, (Icon) null, active);
    }

    public NavButton(String text, Icon icon, boolean active) {
        super(text);
        this.active = active;

        setFont(DentalTheme.textBold(13));
        setForeground(DentalTheme.TEXT2);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setHorizontalAlignment(SwingConstants.LEFT);
        setIcon(icon);

        // un peu plus d'espace entre icone et texte
        setIconTextGap(6);

        // padding + place icone
        int left = (icon != null) ? 8 : 4;
        setBorder(BorderFactory.createEmptyBorder(10, left, 10, 8)); // plus de padding
        setFont(DentalTheme.textBold(13)); // texte plus lisible

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    public void setActive(boolean value) {
        this.active = value;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = DentalTheme.BTN_RADIUS;
        int w = getWidth();
        int h = getHeight();

        boolean drawBg = hover || active;

        if (drawBg) {
            Color bg = active ? DentalTheme.PRIMARY : new Color(0xEAD9CB);
            if (active && hover) bg = DentalTheme.PRIMARY_2;

            // shadow
            g2.setColor(new Color(0, 0, 0, 22));
            g2.fillRoundRect(3, 3, w - 6, h - 6, arc, arc);

            // fill
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w - 6, h - 6, arc, arc);

            // stroke
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(DentalTheme.STROKE);
            g2.drawRoundRect(0, 0, w - 6, h - 6, arc, arc);
        }

        setForeground(active ? Color.WHITE : DentalTheme.TEXT2);

        g2.dispose();
        super.paintComponent(g);
    }
}
