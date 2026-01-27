package ma.dentalTech.mvc.ui.modules.admin;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;
import ma.dentalTech.repository.modules.users.api.RoleRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RolesPanel extends JPanel {

    private final RoleRepository roleRepo;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Libelle", "Privileges"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public RolesPanel() {
        this.roleRepo = ApplicationContext.getBean(RoleRepository.class);

        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        // Carte blanche similaire à SauvegardesPanel
        CardPanel card = new CardPanel(null);
        card.setBackground(DentalTheme.CARD);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 10));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildList(), BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        refresh();
    }

    private JComponent buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JLabel l = new JLabel("Gestion des Roles");
        l.setFont(DentalTheme.titleFont(20));
        l.setForeground(DentalTheme.TEXT);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnAdd = new DentalButton("Ajouter");
        JButton btnEdit = new DentalButton("Modifier");
        JButton btnDelete = new DentalButton("Supprimer");
        JButton btnRefresh = new DentalButton("Rafraichir");

        UiStyles.stylePrimaryButton(btnAdd);
        UiStyles.styleSecondaryButton(btnEdit);
        UiStyles.styleSecondaryButton(btnDelete);
        UiStyles.styleSecondaryButton(btnRefresh);

        btnAdd.addActionListener(e -> openForm(null));
        btnEdit.addActionListener(e -> {
            Role r = selectedRole();
            if (r != null) openForm(r);
        });
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> refresh());

        actions.add(btnAdd);
        actions.add(btnEdit);
        actions.add(btnDelete);
        actions.add(btnRefresh);

        bar.add(l, BorderLayout.WEST);
        bar.add(actions, BorderLayout.EAST);
        return bar;
    }

    private JComponent buildList() {
        UiStyles.styleTable(table);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return sp;
    }

    private void refresh() {
        if (roleRepo == null) return;
        try {
            List<Role> roles = roleRepo.findAll();
            tableModel.setRowCount(0);
            for (Role r : roles) {
                tableModel.addRow(new Object[]{
                        r.getId(),
                        r.getLibelle() != null ? r.getLibelle().name() : "",
                        r.getPrivileges() != null ? r.getPrivileges() : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Role selectedRole() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selectionne une ligne d'abord.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Long id = Long.valueOf(String.valueOf(tableModel.getValueAt(row, 0)));
        return roleRepo.findById(id);
    }

    private void deleteSelected() {
        Role r = selectedRole();
        if (r == null) return;

        int ok = JOptionPane.showConfirmDialog(this, "Supprimer le role " + r.getLibelle() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        roleRepo.deleteById(r.getId());
        refresh();
    }

    private void openForm(Role role) {
        boolean isEdit = role != null && role.getId() != null;

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), isEdit ? "Modifier role" : "Ajouter role", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        JComboBox<LibelleRole> cbLibelle = new JComboBox<>(LibelleRole.values());
        JTextField tfPrivileges = new JTextField();

        if (isEdit) {
            cbLibelle.setSelectedItem(role.getLibelle());
            tfPrivileges.setText(role.getPrivileges() != null ? role.getPrivileges() : "");
        }

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        form.add(new JLabel("Libelle"), c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(cbLibelle, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        form.add(new JLabel("Privileges (CSV)"), c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(tfPrivileges, c);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Annuler");
        JButton btnSave = new JButton("Enregistrer");
        actions.add(btnCancel);
        actions.add(btnSave);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            LibelleRole lib = (LibelleRole) cbLibelle.getSelectedItem();
            if (lib == null) {
                JOptionPane.showMessageDialog(dialog, "Libelle obligatoire", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Role r = isEdit ? role : new Role();
            r.setLibelle(lib);
            r.setPrivileges(tfPrivileges.getText() != null ? tfPrivileges.getText().trim() : null);
            r.setModifiePar("admin");
            if (!isEdit) r.setCreePar("admin");

            if (isEdit) roleRepo.update(r);
            else roleRepo.create(r);

            dialog.dispose();
            refresh();
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(480, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}

