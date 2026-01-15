package ma.dentalTech.mvc.ui.modules.admin;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.repository.modules.users.api.RoleRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RolesPanel extends JPanel {

    private final RoleRepository roleRepo;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Libellé"}, 0);

    public RolesPanel() {
        this.roleRepo = ApplicationContext.getBean(RoleRepository.class);

        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildList(), BorderLayout.CENTER);

        refresh();
    }

    private JComponent buildHeader() {
        JLabel l = new JLabel("Gestion des Rôles");
        l.setFont(DentalTheme.titleFont(20));
        l.setForeground(DentalTheme.TEXT);
        return l;
    }

    private JComponent buildList() {
        JTable table = new JTable(tableModel);
        CardPanel p = new CardPanel();
        p.setLayout(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refresh() {
        if (roleRepo == null) return;
        try {
            List<Role> roles = roleRepo.findAll();
            tableModel.setRowCount(0);
            for (Role r : roles) {
                tableModel.addRow(new Object[]{r.getId(), r.getLibelle()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
