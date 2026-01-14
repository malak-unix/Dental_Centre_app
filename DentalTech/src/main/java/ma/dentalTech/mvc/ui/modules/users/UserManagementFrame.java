package ma.dentalTech.mvc.ui.modules.users;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController;
import ma.dentalTech.mvc.dto.users.UserSaveRequestDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementFrame extends JFrame {

    private final UserManagementController controller;

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Prénom", "Login", "Actif"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
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

    public UserManagementFrame(UserManagementController controller) {
        super("Gestion des utilisateurs");
        this.controller = controller;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 560);
        setLocationRelativeTo(null);

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

        tfSearch.addActionListener(e -> searchSafe()); // enter triggers search

        btnCreate.addActionListener(e -> openCreateFrame());

        btnEditProfile.addActionListener(e -> openEditProfileDialog());

        btnAssignRole.addActionListener(e -> assignRoleDialog());

        btnRemoveRole.addActionListener(e -> removeRoleDialog());
    }

    // ==========================
    // Actions
    // ==========================

    //private void refreshTableSafe() {
        //if (controller == null) {
           // showError("Controller non disponible (wiring).", new RuntimeException("controller=null"));
          //  return;
        //}

       // try {
        //    List<UserSummaryDTO> users = controller.getAllUsers();
           // fillTable(users);
        //} catch (Exception ex) {
           // showError("Impossible de charger les utilisateurs", ex);
        //}
   // }
       private void refreshTableSafe() {
           System.out.println("REFRESH CLICK ✅");
           try {
               List<UserSummaryDTO> users = controller.getAllUsers();
               tableModel.setRowCount(0);

               if (users != null) {
                   for (UserSummaryDTO u : users) {
                       tableModel.addRow(new Object[]{
                               u.getId(),
                               u.getNom(),
                               u.getPrenom(),
                               u.getLogin(),
                               u.isActif()
                       });
                   }
               }
           } catch (Exception ex) {
               showError("Impossible de charger les utilisateurs", ex);
           }
       }


    private void searchSafe() {
        if (controller == null) {
            showError("Controller non disponible (wiring).", new RuntimeException("controller=null"));
            return;
        }

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
        if (controller == null) {
            showError("Controller non disponible (wiring).", new RuntimeException("controller=null"));
            return;
        }

        CreateUserFrame f = new CreateUserFrame(controller);

        // ✅ Quand la fenêtre "Créer" se ferme, on recharge la table
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                refreshTableSafe();
            }
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                refreshTableSafe();
            }
        });

        f.setVisible(true);
    }

    private void openEditProfileDialog() {
        Long userId = getSelectedUserId();
        if (userId == null) return;
        int row = table.getSelectedRow();

        String currentNom = tableModel.getValueAt(row, 1).toString();
        String currentPrenom = tableModel.getValueAt(row, 2).toString();
        String currentLogin = tableModel.getValueAt(row, 3).toString();

        // Mini formulaire (profil)
        JTextField tfNom = new JTextField();
        JTextField tfPrenom = new JTextField();
        JTextField tfLogin = new JTextField();
        tfNom.setText(currentNom);
        tfPrenom.setText(currentPrenom);
        tfLogin.setText(currentLogin);

        JPasswordField pfPass = new JPasswordField();
        JComboBox<LibelleRole> cbRole = new JComboBox<>(new LibelleRole[]{
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

        int res = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Modifier profil (id=" + userId + ")",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (res != JOptionPane.OK_OPTION) return;

        UserSaveRequestDTO req = UserSaveRequestDTO.builder()
                .nom(tfNom.getText())
                .prenom(tfPrenom.getText())
                .login(tfLogin.getText())
                .password(new String(pfPass.getPassword()))
                .role((LibelleRole) cbRole.getSelectedItem())
                .build();

        try {
            controller.updateUserProfile(userId, req);
            JOptionPane.showMessageDialog(this, "Mise à jour déclenchée ✅ (DB requise).");
            refreshTableSafe();
        } catch (Exception ex) {
            showError("Mise à jour impossible (normal si DB non branchée)", ex);
        }
    }

    private void assignRoleDialog() {
        Long userId = getSelectedUserId();
        if (userId == null) return;

        LibelleRole role = (LibelleRole) JOptionPane.showInputDialog(
                this,
                "Choisir un rôle à assigner :",
                "Assigner rôle",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new LibelleRole[]{LibelleRole.ADMIN, LibelleRole.MEDECIN, LibelleRole.SECRETAIRE},
                LibelleRole.ADMIN
        );

        if (role == null) return;

        try {
            controller.assignRoleToUser(userId, role);
            JOptionPane.showMessageDialog(this, "Rôle assigné ✅ (DB requise).");
            refreshTableSafe(); // ✅ AJOUT
        } catch (Exception ex) {
            showError("Assignation impossible (normal si DB non branchée)", ex);
        }
    }

    private void removeRoleDialog() {
        Long userId = getSelectedUserId();
        if (userId == null) return;

        LibelleRole role = (LibelleRole) JOptionPane.showInputDialog(
                this,
                "Choisir un rôle à supprimer :",
                "Supprimer rôle",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new LibelleRole[]{LibelleRole.ADMIN, LibelleRole.MEDECIN, LibelleRole.SECRETAIRE},
                LibelleRole.ADMIN
        );

        if (role == null) return;

        try {
            controller.removeRoleFromUser(userId, role);
            JOptionPane.showMessageDialog(this, "Rôle supprimé ✅ (DB requise).");
            refreshTableSafe(); // ✅ AJOUT
        } catch (Exception ex) {
            showError("Suppression impossible (normal si DB non branchée)", ex);
        }
    }


    // ==========================
    // Helpers
    // ==========================

    private void fillTable(List<UserSummaryDTO> users) {
        tableModel.setRowCount(0);
        if (users == null) return;

        for (UserSummaryDTO u : users) {
            if (u == null) continue;
            tableModel.addRow(new Object[]{
                    u.getId(),
                    u.getNom(),
                    u.getPrenom(),
                    u.getLogin(),
                    u.isActif()
            });
        }
    }

    private Long getSelectedUserId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionne un utilisateur dans le tableau.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Object val = tableModel.getValueAt(row, 0);
        if (val == null) return null;

        try {
            return Long.valueOf(val.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "ID invalide dans la table.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                title + "\n" + ex.getClass().getSimpleName() +
                        (ex.getMessage() != null ? (": " + ex.getMessage()) : ""),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
