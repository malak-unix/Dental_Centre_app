package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * Shell commun (Header + Sidebar + Content) pour respecter les maquettes.
 *
 * Objectif : éviter de dupliquer le layout dans chaque module.
 */
public class AppShellPanel extends JPanel {

    private final JPanel sidebar;
    private final JPanel header;
    private final JPanel content;

    public AppShellPanel() {
        setLayout(new BorderLayout());
        setBackground(DentalTheme.BG2);

        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setOpaque(false);
        sidebar.setLayout(new BorderLayout());

        header = new JPanel(new BorderLayout(12, 0));
        header.setPreferredSize(new Dimension(0, 78));
        header.setBackground(new Color(0xEAD3BF));
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(sidebar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    public JPanel sidebar() { return sidebar; }
    public JPanel header() { return header; }

    public void setContent(JComponent panel) {
        content.removeAll();
        content.add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
