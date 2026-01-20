package ma.dentalTech.mvc.ui.common.components;

import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import java.awt.*;

public class StatCardPro extends JPanel {

    private final JLabel titleLabel = new JLabel();
    private final JLabel valueLabel = new JLabel();

    public StatCardPro(String title, String value) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6)); // espace pour l'ombre

        JPanel card = new ShadowCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 16, 18));

        titleLabel.setText(title);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22)); // style “Caisse”
        titleLabel.setForeground(DentalTheme.TEXT2);

        valueLabel.setText(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLabel.setForeground(DentalTheme.PRIMARY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    public StatCardPro(String title, String value, String icon) {
        this(title, value);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    private static class ShadowCard extends JPanel {
        ShadowCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 22;
            int x = 0, y = 0;
            int w = getWidth() - 8;
            int h = getHeight() - 8;

            // shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(x + 6, y + 6, w, h, arc, arc);

            // fill
            g2.setColor(DentalTheme.CARD);
            g2.fillRoundRect(x, y, w, h, arc, arc);

            // stroke
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(DentalTheme.STROKE);
            g2.drawRoundRect(x, y, w, h, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
