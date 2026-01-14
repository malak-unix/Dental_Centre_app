package ma.dentalTech.mvc.ui.modules.users;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController;
import ma.dentalTech.mvc.dto.users.CreateAdminRequestDTO;
import ma.dentalTech.mvc.dto.users.CreateMedecinRequestDTO;
import ma.dentalTech.mvc.dto.users.CreateSecretaireRequestDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;

import javax.swing.*;
import java.awt.*;

public class CreateUserFrame extends JFrame {

    private final UserManagementController controller;

    private JTextField tfNom;
    private JTextField tfPrenom;
    private JTextField tfLogin;
    private JPasswordField pfPassword;
    private JComboBox<LibelleRole> cbType;

    private JButton btnCreer;
    private JButton btnAnnuler;

    public CreateUserFrame(UserManagementController controller) {
        this.controller = controller;

        setTitle("Créer un utilisateur");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);

        initUi();
        bindActions();
    }

    private void initUi() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        JLabel title = new JLabel("Créer un utilisateur");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        root.add(form, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        tfNom = new JTextField();
        tfPrenom = new JTextField();
        tfLogin = new JTextField();
        pfPassword = new JPasswordField();

        cbType = new JComboBox<>(new LibelleRole[]{
                LibelleRole.ADMIN,
                LibelleRole.MEDECIN,
                LibelleRole.SECRETAIRE
        });

        // Row 0
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        form.add(new JLabel("Nom"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(tfNom, c);

        // Row 1
        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        form.add(new JLabel("Prénom"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(tfPrenom, c);

        // Row 2
        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        form.add(new JLabel("Login"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(tfLogin, c);

        // Row 3
        c.gridx = 0; c.gridy = 3; c.weightx = 0;
        form.add(new JLabel("Mot de passe"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(pfPassword, c);

        // Row 4
        c.gridx = 0; c.gridy = 4; c.weightx = 0;
        form.add(new JLabel("Type"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(cbType, c);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCreer = new JButton("Créer");
        btnAnnuler = new JButton("Annuler");
        actions.add(btnCreer);
        actions.add(btnAnnuler);

        root.add(actions, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnCreer);
    }

    private void bindActions() {
        btnAnnuler.addActionListener(e -> dispose());
        btnCreer.addActionListener(e -> doCreate());
    }

    private void doCreate() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this,
                    "Controller non disponible (wiring).",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nom = tfNom.getText() != null ? tfNom.getText().trim() : "";
        String prenom = tfPrenom.getText() != null ? tfPrenom.getText().trim() : "";
        String login = tfLogin.getText() != null ? tfLogin.getText().trim() : "";
        String password = new String(pfPassword.getPassword());

        LibelleRole type = (LibelleRole) cbType.getSelectedItem();

        // ✅ mini validation UI (évite appels inutiles)
        if (nom.isBlank() || prenom.isBlank() || login.isBlank() || password.isBlank() || type == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs.",
                    "Formulaire invalide",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserSummaryDTO created;

            switch (type) {
                case ADMIN -> {
                    CreateAdminRequestDTO dto = new CreateAdminRequestDTO();
                    dto.setNom(nom);
                    dto.setPrenom(prenom);
                    dto.setLogin(login);
                    dto.setPassword(password);
                    created = controller.createAdmin(dto);
                }

                case MEDECIN -> {
                    String specialite = JOptionPane.showInputDialog(
                            this,
                            "Spécialité du médecin :",
                            "Information requise",
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (specialite == null || specialite.trim().isBlank()) {
                        JOptionPane.showMessageDialog(this,
                                "Création annulée : spécialité obligatoire pour un médecin.",
                                "Information manquante",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    CreateMedecinRequestDTO dto = new CreateMedecinRequestDTO();
                    dto.setNom(nom);
                    dto.setPrenom(prenom);
                    dto.setLogin(login);
                    dto.setPassword(password);
                    dto.setSpecialite(specialite.trim());
                    created = controller.createMedecin(dto);
                }

                case SECRETAIRE -> {
                    String cnss = JOptionPane.showInputDialog(
                            this,
                            "Numéro CNSS :",
                            "Information requise",
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (cnss == null || cnss.trim().isBlank()) {
                        JOptionPane.showMessageDialog(this,
                                "Création annulée : CNSS obligatoire pour une secrétaire.",
                                "Information manquante",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    CreateSecretaireRequestDTO dto = new CreateSecretaireRequestDTO();
                    dto.setNom(nom);
                    dto.setPrenom(prenom);
                    dto.setLogin(login);
                    dto.setPassword(password);
                    dto.setNumCNSS(cnss.trim());
                    created = controller.createSecretaire(dto);
                }

                default -> throw new IllegalStateException("Type non supporté: " + type);
            }

            JOptionPane.showMessageDialog(this,
                    "Utilisateur créé ✅ (id=" + (created != null ? created.getId() : "null") + ")",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            // reset
            tfNom.setText("");
            tfPrenom.setText("");
            tfLogin.setText("");
            pfPassword.setText("");
            cbType.setSelectedIndex(0);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la création (DB probablement non branchée).\n" +
                            ex.getClass().getSimpleName() + (ex.getMessage() != null ? (": " + ex.getMessage()) : ""),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
