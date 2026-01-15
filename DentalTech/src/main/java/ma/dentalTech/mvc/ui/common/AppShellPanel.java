package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;

public class AppShellPanel extends JPanel {

    private final JPanel headerHolder = new JPanel(new BorderLayout());
    private final JPanel sidebarHolder = new JPanel(new BorderLayout());
    private final JPanel contentHolder = new JPanel(new BorderLayout());

    public AppShellPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(DentalTheme.BG2);

        headerHolder.setOpaque(false);
        sidebarHolder.setOpaque(false);
        contentHolder.setOpaque(false);

        add(headerHolder, BorderLayout.NORTH);
        add(sidebarHolder, BorderLayout.WEST);
        add(contentHolder, BorderLayout.CENTER);
    }

    public JPanel header() {
        return headerHolder;
    }

    public JPanel sidebar() {
        return sidebarHolder;
    }

    public void setContent(JComponent content) {
        contentHolder.removeAll();
        if (content != null) {
            contentHolder.add(content, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }
}
