package ma.dentalTech.mvc.ui.modules.dossierMedicale.certificat;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.CertificatController;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDTO;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog modal pour ajouter un nouveau certificat.
 * Selon la maquette fournie.
 */
public class CertificatAddFormUI extends JDialog {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Champs du formulaire
    private final JComboBox<DossierComboItem> cbDossier = new JComboBox<>();
    private final JTextField txtDateDebut = new JTextField();
    private final JTextField txtDateFin = new JTextField();
    private final JTextField txtDuree = new JTextField();
    private final JTextArea txtNoteMedecin = new JTextArea(4, 30);

    private final JButton btnCancel = new JButton("Annuler");
    private final JButton btnCreate = new JButton("+ Créer certificat");

    private boolean confirmed = false;

    /**
     * Item pour le combobox des dossiers
     */
    public static class DossierComboItem {
        private final Long dossierId;
        private final String displayText;

        public DossierComboItem(Long dossierId, String displayText) {
            this.dossierId = dossierId;
            this.displayText = displayText;
        }

        public Long getDossierId() {
            return dossierId;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    private final CertificatDTO certificatToEdit; // null si création

    // Constructeur pour création
    public CertificatAddFormUI(Frame parent, CertificatController controller, List<DossierComboItem> dossiers, String username) {
        this(parent, controller, dossiers, username, null);
    }

    // Constructeur pour modification
    public CertificatAddFormUI(Frame parent, CertificatController controller, List<DossierComboItem> dossiers, String username, CertificatDTO certificatToEdit) {
        super(parent, certificatToEdit == null ? "Ajouter un certificat" : "Modifier le certificat", true);

        this.certificatToEdit = certificatToEdit;

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Populate combobox
        cbDossier.addItem(new DossierComboItem(null, "-- Sélectionner un dossier --"));
        for (DossierComboItem item : dossiers) {
            cbDossier.addItem(item);
        }

        // Remplir les champs si modification
        if (certificatToEdit != null) {
            // Sélectionner le dossier
            for (int i = 0; i < cbDossier.getItemCount(); i++) {
                DossierComboItem item = cbDossier.getItemAt(i);
                if (item != null && item.getDossierId() != null && item.getDossierId().equals(certificatToEdit.dossierId())) {
                    cbDossier.setSelectedIndex(i);
                    break;
                }
            }
            txtDateDebut.setText(certificatToEdit.dateDebut() != null ? certificatToEdit.dateDebut().format(DATE_FORMATTER) : "");
            txtDateFin.setText(certificatToEdit.dateFin() != null ? certificatToEdit.dateFin().format(DATE_FORMATTER) : "");
            txtDuree.setText(certificatToEdit.duree() != null ? String.valueOf(certificatToEdit.duree()) : "");
            txtNoteMedecin.setText(certificatToEdit.noteMedecin() != null ? certificatToEdit.noteMedecin() : "");
            
            btnCreate.setText("Modifier le certificat");
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

        btnCreate.addActionListener(e -> {
            if (certificatToEdit == null) {
                if (validateAndCreate(controller, username)) {
                    confirmed = true;
                    dispose();
                }
            } else {
                if (validateAndUpdate(controller, username)) {
                    confirmed = true;
                    dispose();
                }
            }
        });

        // Enter key sur les champs texte
        txtDateDebut.addActionListener(e -> txtDateFin.requestFocus());
        txtDateFin.addActionListener(e -> txtDuree.requestFocus());
        txtDuree.addActionListener(e -> txtNoteMedecin.requestFocus());
    }

    private JComponent buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 0, 8, 10);
        gc.anchor = GridBagConstraints.WEST;

        // Dossier médical
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        form.add(new JLabel("Dossier médical du patient:"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        cbDossier.setFont(DentalTheme.textFont(13));
        form.add(cbDossier, gc);

        // Date début
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Date début:"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtDateDebut.setFont(DentalTheme.textFont(13));
        txtDateDebut.setToolTipText("Format: yyyy-MM-dd (ex: 2024-04-17)");
        form.add(txtDateDebut, gc);

        // Date fin
        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Date fin:"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtDateFin.setFont(DentalTheme.textFont(13));
        txtDateFin.setToolTipText("Format: yyyy-MM-dd (ex: 2024-04-20)");
        form.add(txtDateFin, gc);

        // Durée
        gc.gridx = 0;
        gc.gridy = 3;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Durée (jours):"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtDuree.setFont(DentalTheme.textFont(13));
        txtDuree.setToolTipText("Nombre de jours (sera calculé automatiquement si date début et fin sont renseignées)");
        form.add(txtDuree, gc);

        // Note médecin
        gc.gridx = 0;
        gc.gridy = 4;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Note médecin:"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;
        txtNoteMedecin.setFont(DentalTheme.textFont(13));
        txtNoteMedecin.setLineWrap(true);
        txtNoteMedecin.setWrapStyleWord(true);
        JScrollPane scrollNote = new JScrollPane(txtNoteMedecin);
        scrollNote.setBorder(txtDateDebut.getBorder());
        form.add(scrollNote, gc);

        return form;
    }

    private JComponent buildButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        btnCancel.setFont(DentalTheme.textFont(13));
        btnCancel.setBackground(DentalTheme.BEIGE);
        btnCancel.setForeground(DentalTheme.TEXT2);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));

        btnCreate.setFont(DentalTheme.textBold(13));
        btnCreate.setBackground(DentalTheme.CARD); // Bleu fonce
        btnCreate.setForeground(DentalTheme.TEXT2);
        btnCreate.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2), // Bordure dorée
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnCreate.setFocusPainted(false);

        buttons.add(btnCancel);
        buttons.add(btnCreate);

        return buttons;
    }

    private boolean validateAndCreate(CertificatController controller, String username) {
        // Validation: Dossier
        DossierComboItem selected = (DossierComboItem) cbDossier.getSelectedItem();
        if (selected == null || selected.getDossierId() == null) {
            showError("Veuillez sélectionner un dossier médical.");
            cbDossier.requestFocus();
            return false;
        }

        // Validation: Date début
        LocalDate dateDebut = null;
        String dateDebutStr = txtDateDebut.getText().trim();
        if (!dateDebutStr.isEmpty()) {
            try {
                dateDebut = LocalDate.parse(dateDebutStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                showError("Date début invalide. Format attendu: yyyy-MM-dd (ex: 2024-04-17)");
                txtDateDebut.requestFocus();
                return false;
            }
        } else {
            showError("La date début est obligatoire.");
            txtDateDebut.requestFocus();
            return false;
        }

        // Validation: Date fin
        LocalDate dateFin = null;
        String dateFinStr = txtDateFin.getText().trim();
        if (!dateFinStr.isEmpty()) {
            try {
                dateFin = LocalDate.parse(dateFinStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                showError("Date fin invalide. Format attendu: yyyy-MM-dd (ex: 2024-04-20)");
                txtDateFin.requestFocus();
                return false;
            }
        }

        // Validation: Durée
        Integer duree = null;
        String dureeStr = txtDuree.getText().trim();
        if (!dureeStr.isEmpty()) {
            try {
                int d = Integer.parseInt(dureeStr);
                if (d < 0) {
                    showError("La durée doit être un nombre positif.");
                    txtDuree.requestFocus();
                    return false;
                }
                duree = d;
            } catch (NumberFormatException e) {
                showError("Durée invalide. Veuillez entrer un nombre de jours.");
                txtDuree.requestFocus();
                return false;
            }
        }

        // Validation: Date fin >= Date début
        if (dateDebut != null && dateFin != null && dateFin.isBefore(dateDebut)) {
            showError("La date fin doit être postérieure ou égale à la date début.");
            txtDateFin.requestFocus();
            return false;
        }

        // Calcul automatique de la durée si dates renseignées
        if (dateDebut != null && dateFin != null && duree == null) {
            duree = (int) java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        }

        // Calcul automatique de la date fin si durée renseignée
        if (dateDebut != null && dateFin == null && duree != null && duree > 0) {
            dateFin = dateDebut.plusDays(duree - 1);
        }

        // Validation: Au moins date fin ou durée doit être renseignée
        if (dateFin == null && duree == null) {
            showError("Veuillez renseigner soit la date fin, soit la durée.");
            return false;
        }

        // Note médecin (optionnel)
        String noteMedecin = txtNoteMedecin.getText().trim();
        if (noteMedecin.isEmpty()) {
            noteMedecin = null;
        }

        // Création
        try {
            CertificatDTO certificat = new CertificatDTO(
                    null, // id
                    selected.getDossierId(),
                    dateDebut,
                    dateFin,
                    duree,
                    noteMedecin
            );

            Long certificatId = controller.create(certificat, username);
            JOptionPane.showMessageDialog(this,
                    "Certificat créé avec succès (ID: " + certificatId + ")",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            showError("Erreur lors de la création: " + ex.getMessage());
            return false;
        }
    }

    private boolean validateAndUpdate(CertificatController controller, String username) {
        // Réutiliser la même logique de validation que validateAndCreate
        // Validation: Dossier
        DossierComboItem selected = (DossierComboItem) cbDossier.getSelectedItem();
        if (selected == null || selected.getDossierId() == null) {
            showError("Veuillez sélectionner un dossier médical.");
            cbDossier.requestFocus();
            return false;
        }

        // Validation: Date début
        LocalDate dateDebut = null;
        String dateDebutStr = txtDateDebut.getText().trim();
        if (!dateDebutStr.isEmpty()) {
            try {
                dateDebut = LocalDate.parse(dateDebutStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                showError("Date début invalide. Format attendu: yyyy-MM-dd (ex: 2024-04-17)");
                txtDateDebut.requestFocus();
                return false;
            }
        } else {
            showError("La date début est obligatoire.");
            txtDateDebut.requestFocus();
            return false;
        }

        // Validation: Date fin
        LocalDate dateFin = null;
        String dateFinStr = txtDateFin.getText().trim();
        if (!dateFinStr.isEmpty()) {
            try {
                dateFin = LocalDate.parse(dateFinStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                showError("Date fin invalide. Format attendu: yyyy-MM-dd (ex: 2024-04-20)");
                txtDateFin.requestFocus();
                return false;
            }
        }

        // Validation: Durée
        Integer duree = null;
        String dureeStr = txtDuree.getText().trim();
        if (!dureeStr.isEmpty()) {
            try {
                int d = Integer.parseInt(dureeStr);
                if (d < 0) {
                    showError("La durée doit être un nombre positif.");
                    txtDuree.requestFocus();
                    return false;
                }
                duree = d;
            } catch (NumberFormatException e) {
                showError("Durée invalide. Veuillez entrer un nombre de jours.");
                txtDuree.requestFocus();
                return false;
            }
        }

        // Validation: Date fin >= Date début
        if (dateDebut != null && dateFin != null && dateFin.isBefore(dateDebut)) {
            showError("La date fin doit être postérieure ou égale à la date début.");
            txtDateFin.requestFocus();
            return false;
        }

        // Calcul automatique de la durée si dates renseignées
        if (dateDebut != null && dateFin != null && duree == null) {
            duree = (int) java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        }

        // Calcul automatique de la date fin si durée renseignée
        if (dateDebut != null && dateFin == null && duree != null && duree > 0) {
            dateFin = dateDebut.plusDays(duree - 1);
        }

        // Note médecin (optionnel)
        String noteMedecin = txtNoteMedecin.getText().trim();
        if (noteMedecin.isEmpty()) {
            noteMedecin = null;
        }

        // Mise à jour
        try {
            CertificatDTO certificat = new CertificatDTO(
                    certificatToEdit.id(), // id existant
                    selected.getDossierId(),
                    dateDebut,
                    dateFin,
                    duree,
                    noteMedecin
            );

            controller.update(certificat, username);
            JOptionPane.showMessageDialog(this,
                    "Certificat modifié avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            showError("Erreur lors de la modification: " + ex.getMessage());
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
