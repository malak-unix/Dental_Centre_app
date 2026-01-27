package ma.dentalTech.mvc.ui.modules.dossierMedicale.acte;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ActeController;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog modal pour ajouter/modifier un acte.
 */
public class ActeAddFormUI extends JDialog {

    private final ActeController controller;
    private final String username;
    private final ActeDTO acteToEdit; // null si création

    // Champs du formulaire
    private final JTextField txtLibelle = new JTextField(30);
    private final JTextField txtCategorie = new JTextField(30);
    private final JTextField txtPrixBase = new JTextField(15);
    private final JTextArea txtDescription = new JTextArea(5, 30);

    private final JButton btnCancel = new JButton("Annuler");
    private final JButton btnSave = new JButton("Enregistrer");

    private boolean confirmed = false;

    // Constructeur pour création
    public ActeAddFormUI(Frame parent, ActeController controller, String username) {
        this(parent, controller, username, null);
    }

    // Constructeur pour modification
    public ActeAddFormUI(Frame parent, ActeController controller, String username, ActeDTO acteToEdit) {
        super(parent, acteToEdit == null ? "Ajout d'un acte" : "Modification d'un acte", true);
        
        this.controller = controller;
        this.username = username;
        this.acteToEdit = acteToEdit;

        setSize(600, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Remplir les champs si modification
        if (acteToEdit != null) {
            txtLibelle.setText(acteToEdit.libelle() != null ? acteToEdit.libelle() : "");
            txtCategorie.setText(acteToEdit.categorie() != null ? acteToEdit.categorie() : "");
            txtPrixBase.setText(acteToEdit.prixBase() != null ? String.format("%.2f", acteToEdit.prixBase()) : "");
            txtDescription.setText(acteToEdit.description() != null ? acteToEdit.description() : "");
        }

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(20, 20));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(DentalTheme.BG);

        content.add(buildForm(), BorderLayout.CENTER);
        content.add(buildButtons(), BorderLayout.SOUTH);

        setContentPane(content);

        // Actions
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        btnSave.addActionListener(e -> {
            if (validateAndSave()) {
                confirmed = true;
                dispose();
            }
        });
    }

    private JComponent buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 0, 10, 10);
        gc.anchor = GridBagConstraints.WEST;

        // Libellé
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        form.add(new JLabel("Libellé:"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtLibelle.setFont(DentalTheme.textFont(13));
        form.add(txtLibelle, gc);

        // Catégorie
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Catégorie:"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtCategorie.setFont(DentalTheme.textFont(13));
        form.add(txtCategorie, gc);

        // Prix de base
        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Prix de base (€):"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtPrixBase.setFont(DentalTheme.textFont(13));
        txtPrixBase.setToolTipText("Ex: 50.00");
        form.add(txtPrixBase, gc);

        // Description
        gc.gridx = 0;
        gc.gridy = 3;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Description:"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.weighty = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        txtDescription.setFont(DentalTheme.textFont(13));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scroll = new JScrollPane(txtDescription);
        scroll.setBorder(null);
        form.add(scroll, gc);

        return form;
    }

    private JComponent buildButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        UiStyles.styleSecondaryButton(btnCancel);
        UiStyles.stylePrimaryButton(btnSave);

        buttons.add(btnCancel);
        buttons.add(btnSave);

        return buttons;
    }

    private boolean validateAndSave() {
        // Validation: Libellé obligatoire
        String libelle = txtLibelle.getText().trim();
        if (libelle.isEmpty()) {
            showError("Le libellé est obligatoire.");
            txtLibelle.requestFocus();
            return false;
        }

        // Validation: Prix
        Double prixBase = null;
        String prixStr = txtPrixBase.getText().trim();
        if (!prixStr.isEmpty()) {
            try {
                prixBase = Double.parseDouble(prixStr);
                if (prixBase < 0) {
                    showError("Le prix doit être positif ou nul.");
                    txtPrixBase.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Prix invalide. Format attendu: nombre décimal (ex: 50.00)");
                txtPrixBase.requestFocus();
                return false;
            }
        }

        // Création ou mise à jour
        try {
            ActeDTO acte = new ActeDTO(
                    acteToEdit != null ? acteToEdit.id() : null,
                    libelle,
                    txtCategorie.getText().trim().isEmpty() ? null : txtCategorie.getText().trim(),
                    prixBase,
                    txtDescription.getText().trim().isEmpty() ? null : txtDescription.getText().trim()
            );

            if (acteToEdit == null) {
                Long acteId = controller.create(acte, username);
                JOptionPane.showMessageDialog(this,
                        "Acte créé avec succès (ID: " + acteId + ")",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                controller.update(acte, username);
                JOptionPane.showMessageDialog(this,
                        "Acte modifié avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            return true;
        } catch (Exception ex) {
            showError("Erreur lors de l'enregistrement: " + ex.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Erreur de validation",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
