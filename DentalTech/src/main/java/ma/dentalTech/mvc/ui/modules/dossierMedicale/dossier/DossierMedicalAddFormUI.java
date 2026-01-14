package ma.dentalTech.mvc.ui.modules.dossierMedicale.dossier;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierDTO;
import ma.dentalTech.mvc.dto.patient.PatientListDto;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Frame;
import java.util.List;

/**
 * Dialog modal pour ajouter/modifier un dossier médical.
 * Avec validations : patient obligatoire, notes max 5000 caractères.
 */
public class DossierMedicalAddFormUI extends JDialog {

    private final DossierMedicalController dossierController;
    private final PatientController patientController;
    private final String username;
    private final DossierDTO dossierToEdit; // null si création

    // Champs du formulaire (partie malak - notes supprimées)
    private final JComboBox<PatientComboItem> cbPatient = new JComboBox<>();

    private final JButton btnCancel = new JButton("Annuler");
    private final JButton btnSave = new JButton("Enregistrer");

    private boolean confirmed = false;

    /**
     * Item pour le combobox des patients
     */
    public static class PatientComboItem {
        private final Long patientId;
        private final String displayText;

        public PatientComboItem(Long patientId, String displayText) {
            this.patientId = patientId;
            this.displayText = displayText;
        }

        public Long getPatientId() {
            return patientId;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    // Constructeur pour création
    public DossierMedicalAddFormUI(Frame parent, DossierMedicalController dossierController,
            PatientController patientController, String username) {
        this(parent, dossierController, patientController, username, null);
    }

    // Constructeur pour modification
    public DossierMedicalAddFormUI(Frame parent, DossierMedicalController dossierController,
            PatientController patientController, String username, DossierDTO dossierToEdit) {
        super(parent, dossierToEdit == null ? "Nouveau dossier médical" : "Modifier le dossier médical", true);

        this.dossierController = dossierController;
        this.patientController = patientController;
        this.username = username;
        this.dossierToEdit = dossierToEdit;

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Charger la liste des patients
        loadPatients();

        // Remplir les champs si modification
        if (dossierToEdit != null) {
            // Sélectionner le patient
            for (int i = 0; i < cbPatient.getItemCount(); i++) {
                PatientComboItem item = cbPatient.getItemAt(i);
                if (item != null && item.getPatientId() != null
                        && item.getPatientId().equals(dossierToEdit.patientId())) {
                    cbPatient.setSelectedIndex(i);
                    break;
                }
            }
            // partie malak - notes supprimées, plus besoin de remplir le champ
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

        // partie malak - listener notes supprimé
    }

    private void loadPatients() {
        try {
            List<PatientListDto> patients = patientController.lister();
            cbPatient.addItem(new PatientComboItem(null, "-- Sélectionner un patient --"));
            for (PatientListDto p : patients) {
                String displayText = p.getNomComplet() != null ? p.getNomComplet()
                        : (p.getId() != null ? "Patient #" + p.getId() : "Patient inconnu");
                if (p.getTelephone() != null && !p.getTelephone().isEmpty()) {
                    displayText += " (" + p.getTelephone() + ")";
                }
                cbPatient.addItem(new PatientComboItem(p.getId(), displayText));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des patients: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // partie malak - méthode updateCharCount() supprimée (notes supprimées)

    private JComponent buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 0, 10, 10);
        gc.anchor = GridBagConstraints.WEST;

        // Patient (obligatoire)
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        JLabel lblPatient = new JLabel("Patient *:");
        lblPatient.setFont(DentalTheme.textBold(13));
        lblPatient.setForeground(DentalTheme.TEXT2);
        form.add(lblPatient, gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        cbPatient.setFont(DentalTheme.textFont(13));
        cbPatient.setPreferredSize(new Dimension(300, 35));
        form.add(cbPatient, gc);

        // partie malak - section Notes supprimée

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
                new EmptyBorder(8, 16, 8, 16)));

        btnSave.setFont(DentalTheme.textBold(13));
        btnSave.setBackground(DentalTheme.PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.GOLD, 2),
                new EmptyBorder(8, 16, 8, 16)));
        btnSave.setFocusPainted(false);

        buttons.add(btnCancel);
        buttons.add(btnSave);

        return buttons;
    }

    private boolean validateAndSave() {
        // Validation : Patient obligatoire
        PatientComboItem selectedPatient = (PatientComboItem) cbPatient.getSelectedItem();
        if (selectedPatient == null || selectedPatient.getPatientId() == null) {
            showError("Veuillez sélectionner un patient.");
            cbPatient.requestFocus();
            return false;
        }

        // partie malak - validation notes supprimée

        // Création ou mise à jour
        try {
            DossierDTO dossier = new DossierDTO(
                    dossierToEdit != null ? dossierToEdit.id() : null,
                    selectedPatient.getPatientId(),
                    dossierToEdit != null && dossierToEdit.medecinId() != null ? dossierToEdit.medecinId() : null,
                    null // partie malak - notes toujours null (champ supprimé)
            );

            if (dossierToEdit == null) {
                Long dossierId = dossierController.create(dossier, username);
                JOptionPane.showMessageDialog(this,
                        "Dossier médical créé avec succès (ID: " + dossierId + ")",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                dossierController.update(dossier, username);
                JOptionPane.showMessageDialog(this,
                        "Dossier médical modifié avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            confirmed = true; // partie malak - CORRECTION: permet le rafraîchissement de la liste
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
                JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
