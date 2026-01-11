package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private final JPanel sidebar;
    private final JPanel header;
    private final JPanel content;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(DentalTheme.BG);

        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(DentalTheme.BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));
        add(sidebar, BorderLayout.WEST);

        header = new JPanel(new BorderLayout(12, 0));
        header.setPreferredSize(new Dimension(0, 78));
        header.setBackground(new Color(0xEAD3BF));
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        add(header, BorderLayout.NORTH);

        content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        add(content, BorderLayout.CENTER);
    }

    public JPanel sidebar() { return sidebar; }
    public JPanel header() { return header; }

    public void setContent(JPanel panel) {
        content.removeAll();
        content.add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
