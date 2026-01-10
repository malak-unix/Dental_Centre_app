package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CaisseMainPanel extends JPanel {

    public CaisseMainPanel(LibelleRole role, Long currentUserId) {
        setLayout(new BorderLayout());
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", new CaisseDashboardPanel(role, currentUserId));
        tabs.addTab("Factures", new CaisseFacturesPanel());
        tabs.addTab("Charges", new CaisseChargesPanel());

        add(tabs, BorderLayout.CENTER);
    }
}
