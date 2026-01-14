package ma.dentalTech.mvc.ui.modules.users;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.users.api.RoleManagementController;
import ma.dentalTech.mvc.dto.users.RoleDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoleManagementPanel extends JPanel {

    private final RoleManagementController controller;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "ID", "Libellé", "Privilèges" },
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    // Hardcoded list of meaningful privileges for the system
    private static final String[] AVAILABLE_PRIVILEGES = {
            "VIEW_DASHBOARD",
            "MANAGE_USERS",
            "MANAGE_ROLES",
            "MANAGE_PATIENTS",
            "MANAGE_DOSSIERS",
            "MANAGE_RENDEZVOUS",
            "MANAGE_CAISSE",
            "MANAGE_REFERENTIELS"
    };

    public RoleManagementPanel(RoleManagementController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnCreate = new JButton("Créer Rôle");
        JButton btnRefresh = new JButton("Rafraîchir");
        JButton btnPrivileges = new JButton("Gérer Privilèges");

        btnPanel.add(btnCreate);
        btnPanel.add(btnPrivileges);
        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refresh());
        btnCreate.addActionListener(e -> createRoleDialog());
        btnPrivileges.addActionListener(e -> managePrivilegesDialog());

        refresh();
    }

    private void refresh() {
        tableModel.setRowCount(0);
        try {
            List<RoleDTO> roles = controller.getAllRoles();
            if (roles != null) {
                for (RoleDTO r : roles) {
                    tableModel.addRow(new Object[] { r.getId(), r.getLibelle().name(), r.getPrivileges() });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur chargement roles: " + ex.getMessage());
        }
    }

    private void createRoleDialog() {
        JComboBox<LibelleRole> cbRole = new JComboBox<>(LibelleRole.values());
        int res = JOptionPane.showConfirmDialog(this, cbRole, "Nouveau Rôle", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                LibelleRole selected = (LibelleRole) cbRole.getSelectedItem();
                // Constructor expects: Long id, LibelleRole libelle, String privileges
                RoleDTO dto = new RoleDTO(null, selected, "");
                controller.createRole(dto);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur création: " + ex.getMessage());
            }
        }
    }

    private void managePrivilegesDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un rôle");
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String currentPrivs = (String) tableModel.getValueAt(row, 2);

        List<String> currentList = new ArrayList<>();
        if (currentPrivs != null && !currentPrivs.isBlank()) {
            currentList = Arrays.asList(currentPrivs.split(","));
            // trim items
            currentList = currentList.stream().map(String::trim).collect(Collectors.toList());
        }

        // Show all privileges with checkboxes
        JPanel p = new JPanel(new GridLayout(0, 2));
        List<JCheckBox> checks = new ArrayList<>();

        for (String priv : AVAILABLE_PRIVILEGES) {
            JCheckBox cb = new JCheckBox(priv);
            if (currentList.contains(priv)) {
                cb.setSelected(true);
            }
            checks.add(cb);
            p.add(cb);
        }

        int res = JOptionPane.showConfirmDialog(this, new JScrollPane(p), "Privilèges pour " + name,
                JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            List<String> selected = checks.stream()
                    .filter(JCheckBox::isSelected)
                    .map(JCheckBox::getText)
                    .collect(Collectors.toList());

            try {
                // The Controller interface has updatePrivileges(Long, List<String>)
                // OR updateRole(RoleDTO) depending on exact impl.
                // Based on previous view, it has updatePrivileges(Long, List<String>)
                controller.updatePrivileges(id, selected);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur update: " + ex.getMessage());
            }
        }
    }
}
