package ma.dentalTech.mvc.ui.common.components;

import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import java.awt.*;

public class TeethChartPanel extends JPanel {

    public TeethChartPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(300, 150));
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
            drawTooth(g2, startX + (i * (toothWidth + gap)), startY, toothWidth, toothHeight, (i == 4 || i == 12));
        }

        // Lower Jaw (16 teeth)
        startY = 60;
        for (int i = 0; i < 16; i++) {
            drawTooth(g2, startX + (i * (toothWidth + gap)), startY, toothWidth, toothHeight, (i == 3 || i == 10));
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

    private void drawTooth(Graphics2D g2, int x, int y, int w, int h, boolean highlight) {
        if (highlight) {
            g2.setColor(Color.ORANGE.darker());
        } else {
            g2.setColor(Color.WHITE);
        }
        // Crown
        g2.fillRoundRect(x, y, w, h - 5, 8, 8);
        // Root (simple lines)
        g2.drawLine(x + 4, y + h - 5, x + 6, y + h);
        g2.drawLine(x + w - 4, y + h - 5, x + w - 6, y + h);
        
        g2.setColor(DentalTheme.STROKE);
        g2.drawRoundRect(x, y, w, h - 5, 8, 8);
    }
}
