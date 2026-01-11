package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;
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
        ImageIcon icon = loadIcon("/assets/logo.png", 160, 60);
        if (icon != null) {
            return new JLabel(icon);
        }

        // fallback texte si pas d’image
        JLabel title = new JLabel("<html><div style='line-height:1.1'>DENTAL<br/>CENTER</div></html>");
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(DentalTheme.TEXT2);
        return title;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}
