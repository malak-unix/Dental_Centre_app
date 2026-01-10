package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DentalButton extends JButton {

    private boolean hover = false;

    public DentalButton(String text) {
        super(text);

        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // on peint nous-même (comme NavButton) => rendu stable sur tous les Look&Feel
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setForeground(Color.WHITE);
        setFont(DentalTheme.textBold(12));
        setBorder(new EmptyBorder(10, 16, 10, 16));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = DentalTheme.BTN_RADIUS;
        int w = getWidth();
        int h = getHeight();

        boolean pressed = getModel().isArmed() && getModel().isPressed();

        Color bg = DentalTheme.PRIMARY_DARK;
        if (hover) bg = DentalTheme.PRIMARY;
        if (pressed) bg = DentalTheme.PRIMARY_2;

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

        // dessine le texte/icon par dessus
        super.paintComponent(g);
    }
}
