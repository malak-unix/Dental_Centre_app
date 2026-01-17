package ma.dentalTech.mvc.ui.common.components;

import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import java.awt.*;

public class TeethChartPanel extends JPanel {

    private static final int TOOTH_COUNT = 32;
    private final int[] states = new int[TOOTH_COUNT]; // 0 = sain, 1 = traitement, 2 = probleme
    private final Rectangle[] bounds = new Rectangle[TOOTH_COUNT];

    public TeethChartPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(300, 150));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int idx = findToothIndex(e.getPoint());
                if (idx >= 0) {
                    states[idx] = (states[idx] + 1) % 3;
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int toothWidth = 18;
        int toothHeight = 26;
        int gap = 4;
        
        // Upper Jaw (16 teeth)
        int startX = 10;
        int startY = 20;
        for (int i = 0; i < 16; i++) {
            int x = startX + (i * (toothWidth + gap));
            Rectangle r = new Rectangle(x, startY, toothWidth, toothHeight);
            bounds[i] = r;
            drawTooth(g2, r, states[i]);
        }

        // Lower Jaw (16 teeth)
        startY = 60;
        for (int i = 0; i < 16; i++) {
            int x = startX + (i * (toothWidth + gap));
            Rectangle r = new Rectangle(x, startY, toothWidth, toothHeight);
            bounds[16 + i] = r;
            drawTooth(g2, r, states[16 + i]);
        }

        // Legend
        g2.setFont(DentalTheme.textFont(11));
        g2.setColor(DentalTheme.TEXT2);
        
        int ly = 110;
        g2.setColor(Color.GREEN.darker());
        g2.fillOval(10, ly, 10, 10);
        g2.setColor(DentalTheme.TEXT2);
        g2.drawString("Sain", 25, ly+9);

        g2.setColor(Color.BLUE.darker());
        g2.fillOval(80, ly, 10, 10);
        g2.setColor(DentalTheme.TEXT2);
        g2.drawString("En traitement", 95, ly+9);

        g2.setColor(Color.ORANGE.darker());
        g2.fillOval(180, ly, 10, 10);
        g2.setColor(DentalTheme.TEXT2);
        g2.drawString("Probleme", 195, ly+9);
    }

    private void drawTooth(Graphics2D g2, Rectangle r, int state) {
        g2.setColor(colorForState(state));
        // Crown
        g2.fillRoundRect(r.x, r.y, r.width, r.height - 5, 8, 8);
        // Root (simple lines)
        g2.drawLine(r.x + 4, r.y + r.height - 5, r.x + 6, r.y + r.height);
        g2.drawLine(r.x + r.width - 4, r.y + r.height - 5, r.x + r.width - 6, r.y + r.height);
        
        g2.setColor(DentalTheme.STROKE);
        g2.drawRoundRect(r.x, r.y, r.width, r.height - 5, 8, 8);
    }

    private Color colorForState(int state) {
        return switch (state) {
            case 1 -> Color.BLUE.darker();
            case 2 -> Color.ORANGE.darker();
            default -> Color.GREEN.darker();
        };
    }

    private int findToothIndex(Point p) {
        for (int i = 0; i < bounds.length; i++) {
            Rectangle r = bounds[i];
            if (r != null && r.contains(p)) return i;
        }
        return -1;
    }
}
