package ma.dentalTech.mvc.ui.modules.users;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController;
import ma.dentalTech.mvc.dto.users.UserSaveRequestDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final UserManagementController controller;

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "ID", "Nom", "Prénom", "Login", "Actif" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    // Top bar
    private final JTextField tfSearch = new JTextField(22);
    private final JButton btnSearch = new JButton("Rechercher");
    private final JButton btnRefresh = new JButton("Rafraîchir");
    private final JButton btnCreate = new JButton("+ Créer");

    private final JButton btnEditProfile = new JButton("Modifier profil");
    private final JButton btnAssignRole = new JButton("Assigner rôle");
    private final JButton btnRemoveRole = new JButton("Supprimer rôle");
    private final JButton btnActivate = new JButton("Activer");
    private final JButton btnDeactivate = new JButton("Désactiver");

    public UserManagementPanel(UserManagementController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(12, 12));
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        bindActions();
        refreshTableSafe(); // load initial list
    }

    private JComponent buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        p.add(new JLabel("Recherche :"));
        p.add(tfSearch);
        p.add(btnSearch);
        p.add(btnRefresh);

        p.add(Box.createHorizontalStrut(16));

        p.add(btnCreate);
        p.add(btnEditProfile);
        p.add(btnAssignRole);
        p.add(btnRemoveRole);
        p.add(btnActivate);
        p.add(btnDeactivate);

        return p;
    }

    private JComponent buildCenter() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        return new JScrollPane(table);
    }

    private void bindActions() {
        btnRefresh.addActionListener(e -> refreshTableSafe());
        btnSearch.addActionListener(e -> searchSafe());
        tfSearch.addActionListener(e -> searchSafe());
        btnCreate.addActionListener(e -> openCreateFrame());
        btnEditProfile.addActionListener(e -> openEditProfileDialog());
        btnAssignRole.addActionListener(e -> assignRoleDialog());
        btnRemoveRole.addActionListener(e -> removeRoleDialog());
        btnActivate.addActionListener(e -> activateUser());
        btnDeactivate.addActionListener(e -> deactivateUser());
    }

    private void refreshTableSafe() {
        try {
            List<UserSummaryDTO> users = controller.getAllUsers();
            fillTable(users);
        } catch (Exception ex) {
            showError("Impossible de charger les utilisateurs", ex);
        }
    }

    private void searchSafe() {
        if (controller == null)
            return;
        String kw = tfSearch.getText() != null ? tfSearch.getText().trim() : "";
        if (kw.isBlank()) {
            refreshTableSafe();
            return;
        }
        try {
            List<UserSummaryDTO> users = controller.searchUsersByKeyword(kw);
            fillTable(users);
        } catch (Exception ex) {
            showError("Recherche impossible", ex);
        }
    }

    private void openCreateFrame() {
        if (controller == null)
            return;
        CreateUserFrame f = new CreateUserFrame(controller);
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                refreshTableSafe();
            }
        });
        f.setVisible(true);
    }

    private void openEditProfileDialog() {
        Long userId = getSelectedUserId();
        if (userId == null)
            return;
        int row = table.getSelectedRow();
        String currentNom = tableModel.getValueAt(row, 1).toString();
        String currentPrenom = tableModel.getValueAt(row, 2).toString();
        String currentLogin = tableModel.getValueAt(row, 3).toString();

        JTextField tfNom = new JTextField(currentNom);
        JTextField tfPrenom = new JTextField(currentPrenom);
        JTextField tfLogin = new JTextField(currentLogin);
        JPasswordField pfPass = new JPasswordField();
        JComboBox<LibelleRole> cbRole = new JComboBox<>(new LibelleRole[] {
                LibelleRole.ADMIN, LibelleRole.MEDECIN, LibelleRole.SECRETAIRE
        });

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Nom"));
        panel.add(tfNom);
        panel.add(new JLabel("Prénom"));
        panel.add(tfPrenom);
        panel.add(new JLabel("Login"));
        panel.add(tfLogin);
        panel.add(new JLabel("Nouveau mot de passe"));
        panel.add(pfPass);
        panel.add(new JLabel("Rôle"));
        panel.add(cbRole);

        int res = JOptionPane.showConfirmDialog(this, panel, "Modifier profil", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION)
            return;

        UserSaveRequestDTO req = UserSaveRequestDTO.builder()
                .nom(tfNom.getText())
                .prenom(tfPrenom.getText())
                .login(tfLogin.getText())
                .password(new String(pfPass.getPassword()))
                .role((LibelleRole) cbRole.getSelectedItem())
                .build();

        try {
            controller.updateUserProfile(userId, req);
            refreshTableSafe();
        } catch (Exception ex) {
            showError("Erreur mise à jour", ex);
        }
    }

    private void assignRoleDialog() {
        Long userId = getSelectedUserId();
        if (userId == null)
            return;
        LibelleRole role = (LibelleRole) JOptionPane.showInputDialog(this, "Choisir un rôle :", "Assigner rôle",
                JOptionPane.QUESTION_MESSAGE, null,
                new LibelleRole[] { LibelleRole.ADMIN, LibelleRole.MEDECIN, LibelleRole.SECRETAIRE },
                LibelleRole.ADMIN);
        if (role == null)
            return;
        try {
            controller.assignRoleToUser(userId, role);
            refreshTableSafe();
        } catch (Exception ex) {
            showError("Erreur assignation", ex);
        }
    }

    private void removeRoleDialog() {
        Long userId = getSelectedUserId();
        if (userId == null)
            return;
        LibelleRole role = (LibelleRole) JOptionPane.showInputDialog(this, "Choisir un rôle à retirer :",
                "Retirer rôle",
                JOptionPane.QUESTION_MESSAGE, null,
                new LibelleRole[] { LibelleRole.ADMIN, LibelleRole.MEDECIN, LibelleRole.SECRETAIRE },
                LibelleRole.ADMIN);
        if (role == null)
            return;
        try {
            controller.removeRoleFromUser(userId, role);
            refreshTableSafe();
        } catch (Exception ex) {
            showError("Erreur suppression rôle", ex);
        }
    }

    private void activateUser() {
        Long userId = getSelectedUserId();
        if (userId == null)
            return;
        try {
            controller.activateUser(userId);
            refreshTableSafe();
        } catch (Exception ex) {
            showError("Erreur activation", ex);
        }
    }

    private void deactivateUser() {
        Long userId = getSelectedUserId();
        if (userId == null)
            return;
        try {
            controller.deactivateUser(userId);
            refreshTableSafe();
        } catch (Exception ex) {
            showError("Erreur désactivation", ex);
        }
    }

    private void fillTable(List<UserSummaryDTO> users) {
        tableModel.setRowCount(0);
        if (users == null)
            return;
        for (UserSummaryDTO u : users) {
            tableModel.addRow(new Object[] { u.getId(), u.getNom(), u.getPrenom(), u.getLogin(), u.isActif() });
        }
    }

    private Long getSelectedUserId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur", "Attention",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return Long.valueOf(tableModel.getValueAt(row, 0).toString());
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this, title + "\n" + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
