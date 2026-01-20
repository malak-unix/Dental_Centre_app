package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class LogoHeaderPanel extends JPanel {

    public LogoHeaderPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel logo = buildLogoLabel();
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(logo, BorderLayout.WEST);
    }

    private JLabel buildLogoLabel() {
        ImageIcon icon = loadIcon("/assets/logo.png", 170, 60);
        if (icon != null) {
            return new JLabel(icon);
        }

        // fallback texte si pas d'image
        JLabel title = new JLabel("<html><div style='line-height:1.1'>DENTAL<br/>CENTER</div></html>");
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(DentalTheme.TEXT2);
        return title;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            ImageIcon icon = new ImageIcon(url);
            Image img = scaleImage(icon.getImage(), w, h);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    private Image scaleImage(Image src, int w, int h) {
        if (src == null) return null;
        Image scaled = src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
        return image;
    }
}
