package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RdvCardPanel extends JPanel {

    private final String title;
    private final String time;
    private final String status;

    public RdvCardPanel(String title, String time, String status) {
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
        s.setForeground(DentalTheme.MUTED);

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
        g2.setColor(new Color(0xF7, 0xF2, 0xEC));
        g2.fillRoundRect(0, 0, w - 6, h - 6, arc, arc);

        // gold stroke
        g2.setColor(DentalTheme.STROKE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, w - 6, h - 6, arc, arc);

        g2.dispose();
    }
}
