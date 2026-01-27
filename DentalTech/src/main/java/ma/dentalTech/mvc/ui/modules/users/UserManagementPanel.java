package ma.dentalTech.mvc.ui.modules.users;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController;
import ma.dentalTech.mvc.dto.users.UserSaveRequestDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

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

    public UserManagementPanel(UserManagementController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(12, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        // Carte principale comme pour SauvegardesPanel
        CardPanel card = new CardPanel(null);
        card.setBackground(DentalTheme.CARD);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 10));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildCenter(), BorderLayout.CENTER);
        card.add(buildCreatePanel(), BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
        
        // Auto-load
        refreshTableSafe();
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel("Gestion des utilisateurs");
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        DentalButton refreshBtn = new DentalButton("Rafraîchir la liste");
        DentalButton editBtn = new DentalButton("Modifier l'utilisateur");
        DentalButton deleteBtn = new DentalButton("Supprimer l'utilisateur");

        UiStyles.styleSecondaryButton(refreshBtn);
        UiStyles.styleSecondaryButton(editBtn);
        UiStyles.stylePrimaryButton(deleteBtn);

        refreshBtn.addActionListener(e -> refreshTableSafe());
        editBtn.addActionListener(e -> editUserSafe());
        deleteBtn.addActionListener(e -> deleteUserSafe());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(refreshBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JComponent buildCenter() {
        JScrollPane sp = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        return sp;
    }

    private JComponent buildCreatePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder("Créer un utilisateur (rapide)"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.weightx = 1.0;

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

        DentalButton createBtn = new DentalButton("Ajouter");
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
                req.setSpecialite("Généraliste");
                controller.createMedecin(req);

            } else if (role == LibelleRole.SECRETAIRE) {
                var req = new ma.dentalTech.mvc.dto.users.CreateSecretaireRequestDTO();
                req.setNom(nom);
                req.setPrenom(prenom);
                req.setLogin(login);
                req.setPassword(pwd);
                req.setNumCNSS("");
                controller.createSecretaire(req);
            }

            JOptionPane.showMessageDialog(this, "Utilisateur créé avec succès");

            // IMPORTANT: recharger la table
            refreshTableSafe();

            // clear
            nomField.setText("");
            prenomField.setText("");
            loginField.setText("");
            passwordField.setText("");

        } catch (Exception ex) {
            showError("Erreur lors de la création", ex);
        }
    }

    /**
     * Ouverture d'une petite fenêtre de modification pour l'utilisateur sélectionné.
     */
    private void editUserSafe() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un utilisateur à modifier.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object idObj = tableModel.getValueAt(row, 0);
        if (!(idObj instanceof Long) && !(idObj instanceof Integer)) {
            JOptionPane.showMessageDialog(this,
                    "ID utilisateur invalide.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Long id = (idObj instanceof Long) ? (Long) idObj : Long.valueOf((Integer) idObj);

        try {
            UserSummaryDTO user = controller.getUserById(id);
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                        "Utilisateur introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Modifier l'utilisateur", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setLayout(new GridBagLayout());

            JTextField nomFieldEdit = new JTextField(user.getNom(), 15);
            JTextField prenomFieldEdit = new JTextField(user.getPrenom(), 15);
            JTextField loginFieldEdit = new JTextField(user.getLogin(), 15);
            JPasswordField pwdFieldEdit = new JPasswordField(15);
            JComboBox<LibelleRole> roleComboEdit = new JComboBox<>(LibelleRole.values());
            if (user.getRole() != null) {
                roleComboEdit.setSelectedItem(user.getRole());
            }

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6, 8, 6, 8);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;

            dialog.add(new JLabel("Nom"), c);
            c.gridx = 1;
            dialog.add(nomFieldEdit, c);

            c.gridx = 0;
            c.gridy++;
            dialog.add(new JLabel("Prénom"), c);
            c.gridx = 1;
            dialog.add(prenomFieldEdit, c);

            c.gridx = 0;
            c.gridy++;
            dialog.add(new JLabel("Login"), c);
            c.gridx = 1;
            dialog.add(loginFieldEdit, c);

            c.gridx = 0;
            c.gridy++;
            dialog.add(new JLabel("Nouveau mot de passe"), c);
            c.gridx = 1;
            dialog.add(pwdFieldEdit, c);

            c.gridx = 0;
            c.gridy++;
            dialog.add(new JLabel("Rôle"), c);
            c.gridx = 1;
            dialog.add(roleComboEdit, c);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
            DentalButton cancelBtn = new DentalButton("Annuler");
            DentalButton saveBtn = new DentalButton("Enregistrer");

            cancelBtn.addActionListener(e -> dialog.dispose());
            saveBtn.addActionListener(e -> {
                try {
                    UserSaveRequestDTO req = UserSaveRequestDTO.builder()
                            .nom(nomFieldEdit.getText())
                            .prenom(prenomFieldEdit.getText())
                            .login(loginFieldEdit.getText())
                            .password(new String(pwdFieldEdit.getPassword()).isBlank()
                                    ? null
                                    : new String(pwdFieldEdit.getPassword()))
                            .role((LibelleRole) roleComboEdit.getSelectedItem())
                            .build();

                    controller.updateUserProfile(id, req);
                    JOptionPane.showMessageDialog(this,
                            "Utilisateur modifié avec succès.",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshTableSafe();
                } catch (Exception ex) {
                    showError("Erreur lors de la mise à jour", ex);
                }
            });

            buttons.add(cancelBtn);
            buttons.add(saveBtn);

            c.gridx = 0;
            c.gridy++;
            c.gridwidth = 2;
            c.anchor = GridBagConstraints.EAST;
            dialog.add(buttons, c);

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (Exception ex) {
            showError("Erreur lors du chargement de l'utilisateur", ex);
        }
    }

    /**
     * Suppression de l'utilisateur sélectionné dans la table.
     */
    private void deleteUserSafe() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un utilisateur à supprimer.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object idObj = tableModel.getValueAt(row, 0);
        if (!(idObj instanceof Long) && !(idObj instanceof Integer)) {
            JOptionPane.showMessageDialog(this,
                    "ID utilisateur invalide.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Long id = (idObj instanceof Long) ? (Long) idObj : Long.valueOf((Integer) idObj);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer cet utilisateur ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteUser(id);
            JOptionPane.showMessageDialog(this,
                    "Utilisateur supprimé avec succès.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshTableSafe();
        } catch (Exception ex) {
            showError("Erreur lors de la suppression", ex);
        }
    }





    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                title + "\n" + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
