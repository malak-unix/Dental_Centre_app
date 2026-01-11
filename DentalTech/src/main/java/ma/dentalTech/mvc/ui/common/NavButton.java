package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NavButton extends JButton {

    private boolean active = false;
    private boolean hover = false;

    // ✅ Constructeur utilisé par DashboardView / autres
    public NavButton(String text) {
        this(text, false);
    }

    // ✅ Constructeur utilisé par AgendaHomePanel : new NavButton("..", true/false)
    public NavButton(String text, boolean active) {
        super(text);
        this.active = active;

        setFont(DentalTheme.BASE_BOLD);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        // Sidebar style : texte aligné à gauche
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    // ✅ Méthode attendue par ton code AgendaHomePanel
    public void setActive(boolean value) {
        this.active = value;
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    // ✅ Compat avec mon code précédent (Dashboard)
    public void setSelectedStyle(boolean value) {
        setActive(value);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = DentalTheme.BTN_RADIUS;
        int w = getWidth();
        int h = getHeight();

        Color bg = DentalTheme.PRIMARY;

        if (hover) bg = DentalTheme.PRIMARY_2;
        if (active) bg = DentalTheme.PRIMARY_2;

        // shadow
        g2.setColor(new Color(0, 0, 0, 35));
        g2.fillRoundRect(3, 3, w - 6, h - 6, arc, arc);

        // fill
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w - 6, h - 6, arc, arc);

        // gold stroke
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(DentalTheme.STROKE);
        g2.drawRoundRect(0, 0, w - 6, h - 6, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
