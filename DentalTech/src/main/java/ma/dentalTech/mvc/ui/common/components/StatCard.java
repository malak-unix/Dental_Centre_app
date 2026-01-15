package ma.dentalTech.mvc.ui.common.components;

import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {

    private final JLabel valueLabel;
    private final JLabel titleLabel;
    private final JLabel iconLabel; // Text-based icon or image

    public StatCard(String title, String value, String iconText) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new RoundedBorder(DentalTheme.STROKE, 1, 15));
        setBackground(DentalTheme.CARD);

        // Icon (Left)
        iconLabel = new JLabel(iconText, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setForeground(DentalTheme.GOLD);
        iconLabel.setPreferredSize(new Dimension(60, 60));

        // Text (Center)
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        valueLabel = new JLabel(value);
        valueLabel.setFont(DentalTheme.titleFont(24));
        valueLabel.setForeground(DentalTheme.TEXT);

        titleLabel = new JLabel(title);
        titleLabel.setFont(DentalTheme.textFont(12));
        titleLabel.setForeground(DentalTheme.TEXT2);

        textPanel.add(valueLabel);
        textPanel.add(titleLabel);

        add(iconLabel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    public void setValue(String val) {
        valueLabel.setText(val);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
        g2.dispose();
        super.paintComponent(g);
    }

    // Helper Border Class
    private static class RoundedBorder implements javax.swing.border.Border {
        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }

        public boolean isBorderOpaque() { return false; }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
    }
}
