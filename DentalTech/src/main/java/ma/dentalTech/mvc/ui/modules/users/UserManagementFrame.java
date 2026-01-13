package ma.dentalTech.mvc.ui.modules.users;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementFrame extends JFrame {

    private final UserManagementController controller;

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Prénom", "Login", "Actif"}, 0
    );
    private final JTable table = new JTable(tableModel);

    // Form "Créer admin" (minimal pour commencer)
    private final JTextField nomField = new JTextField();
    private final JTextField prenomField = new JTextField();
    private final JTextField loginField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JComboBox<LibelleRole> roleCombo = new JComboBox<>(new LibelleRole[]{
            LibelleRole.ADMIN, LibelleRole.MEDECIN, LibelleRole.SECRETAIRE
    });

    public UserManagementFrame(UserManagementController controller) {
        super("Gestion des utilisateurs");
        this.controller = controller;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(12, 12));
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildCreatePanel(), BorderLayout.SOUTH);
    }

    private JComponent buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshBtn = new JButton("Rafraîchir la liste");
        refreshBtn.addActionListener(e -> refreshTableSafe());

        p.add(refreshBtn);
        return p;
    }

    private JComponent buildCenter() {
        JScrollPane sp = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        return sp;
    }

    private JComponent buildCreatePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Créer un utilisateur (démo)"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        // row 0
        c.gridx = 0; p.add(new JLabel("Nom"), c);
        c.gridx = 1; p.add(nomField, c);
        c.gridx = 2; p.add(new JLabel("Prénom"), c);
        c.gridx = 3; p.add(prenomField, c);

        // row 1
        c.gridy++;
        c.gridx = 0; p.add(new JLabel("Login"), c);
        c.gridx = 1; p.add(loginField, c);
        c.gridx = 2; p.add(new JLabel("Mot de passe"), c);
        c.gridx = 3; p.add(passwordField, c);

        // row 2
        c.gridy++;
        c.gridx = 0; p.add(new JLabel("Type"), c);
        c.gridx = 1; p.add(roleCombo, c);

        JButton createBtn = new JButton("Créer");
        createBtn.addActionListener(e -> createUserSafe());

        c.gridx = 3;
        p.add(createBtn, c);

        return p;
    }

    private void refreshTableSafe() {
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

    private void createUserSafe() {
        try {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String login = loginField.getText();
            String pwd = new String(passwordField.getPassword());
            LibelleRole role = (LibelleRole) roleCombo.getSelectedItem();

            // Pour l'instant : on déclenche selon le role
            // (on améliorera après avec un vrai écran par type)
            if (role == LibelleRole.ADMIN) {
                var req = new ma.dentalTech.mvc.dto.users.CreateAdminRequestDTO();
                req.setNom(nom);
                req.setPrenom(prenom);
                req.setLogin(login);
                req.setPassword(pwd);
                controller.createAdmin(req);

            } else if (role == LibelleRole.MEDECIN) {
                var req = new ma.dentalTech.mvc.dto.users.CreateMedecinRequestDTO();
                req.setNom(nom);
                req.setPrenom(prenom);
                req.setLogin(login);
                req.setPassword(pwd);
                // spécialité : on met vide pour l’instant
                req.setSpecialite("");
                controller.createMedecin(req);

            } else if (role == LibelleRole.SECRETAIRE) {
                var req = new ma.dentalTech.mvc.dto.users.CreateSecretaireRequestDTO();
                req.setNom(nom);
                req.setPrenom(prenom);
                req.setLogin(login);
                req.setPassword(pwd);
                // CNSS : vide pour l’instant
                req.setNumCNSS("");
                controller.createSecretaire(req);
            }

            JOptionPane.showMessageDialog(this, "Création déclenchée ✅ (DB requise pour réussir).");
        } catch (Exception ex) {
            showError("Création impossible (normal si DB non branchée)", ex);
        }
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                title + "\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
