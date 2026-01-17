package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RdvCardPanel extends JPanel {

    private final Long rdvId;
    private final String title;
    private final String time;
    private final String status;
    private boolean selected = false;

    public RdvCardPanel(Long rdvId, String title, String time, String status) {
        this.rdvId = rdvId;
        this.title = title;
        this.time = time;
        this.status = status;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 12, 10, 12));
        setPreferredSize(new Dimension(180, 78));

        JLabel t = new JLabel(title);
        t.setFont(DentalTheme.textBold(12));
        t.setForeground(DentalTheme.TEXT2);

        JLabel h = new JLabel(time);
        h.setFont(DentalTheme.textFont(11));
        h.setForeground(DentalTheme.MUTED);

        JLabel s = new JLabel(status);
        s.setFont(DentalTheme.textFont(11));
        s.setForeground(colorForStatus(status));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(t);
        box.add(Box.createVerticalStrut(3));
        box.add(h);
        box.add(Box.createVerticalStrut(6));
        box.add(s);

        add(box, BorderLayout.CENTER);
    }

    public Long getRdvId() {
        return rdvId;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 16;
        int w = getWidth();
        int h = getHeight();

        // shadow
        g2.setColor(new Color(0, 0, 0, 18));
        g2.fillRoundRect(3, 3, w - 6, h - 6, arc, arc);

        // card fill
        Color fill = colorForStatus(status);
        if (fill == null) fill = new Color(0xF7, 0xF2, 0xEC);
        if (selected) fill = fill.darker();
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w - 6, h - 6, arc, arc);

        // gold stroke
        g2.setColor(DentalTheme.STROKE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, w - 6, h - 6, arc, arc);

        g2.dispose();
    }

    private Color colorForStatus(String s) {
        if (s == null) return null;
        String v = s.trim().toUpperCase();
        return switch (v) {
            case "PLANIFIE" -> new Color(0xF8, 0xE6, 0xCC);
            case "CONFIRME" -> new Color(0xD6, 0xF0, 0xE0);
            case "TERMINE" -> new Color(0xD8, 0xE6, 0xF8);
            case "ANNULE" -> new Color(0xF1, 0xD6, 0xD6);
            default -> null;
        };
    }
}
