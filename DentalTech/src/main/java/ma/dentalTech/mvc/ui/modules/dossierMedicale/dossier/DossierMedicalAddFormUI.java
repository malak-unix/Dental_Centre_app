package ma.dentalTech.mvc.ui.modules.dossierMedicale.dossier;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierDTO;
import ma.dentalTech.mvc.dto.patient.PatientListDto;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

public class DossierMedicalAddFormUI extends JDialog {

    private final DossierMedicalController dossierController;
    private final PatientController patientController;
    private final String username;
    private final DossierDTO dossierToEdit;

    private final Long defaultMedecinId;

    private final JComboBox<PatientComboItem> cbPatient = new JComboBox<>();
    private final JComboBox<MedecinComboItem> cbMedecin = new JComboBox<>();
    private final JTextArea txtNotes = new JTextArea(6, 40);
    private final JLabel lblCharCount = new JLabel("0 / 5000 caracteres");

    private final JButton btnCancel = new DentalButton("Annuler");
    private final JButton btnSave = new DentalButton("Enregistrer");

    private boolean confirmed = false;

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

    private static class MedecinComboItem {
        private final Long medecinId;
        private final String displayText;

        MedecinComboItem(Long medecinId, String displayText) {
            this.medecinId = medecinId;
            this.displayText = displayText;
        }

        Long getMedecinId() {
            return medecinId;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    public DossierMedicalAddFormUI(Frame parent, DossierMedicalController dossierController,
                                  PatientController patientController, String username) {
        this(parent, dossierController, patientController, username, null, null);
    }

    public DossierMedicalAddFormUI(Frame parent, DossierMedicalController dossierController,
                                  PatientController patientController, String username, Long defaultMedecinId) {
        this(parent, dossierController, patientController, username, null, defaultMedecinId);
    }

    public DossierMedicalAddFormUI(Frame parent, DossierMedicalController dossierController,
                                  PatientController patientController, String username, DossierDTO dossierToEdit) {
        this(parent, dossierController, patientController, username, dossierToEdit, null);
    }

    private DossierMedicalAddFormUI(Frame parent, DossierMedicalController dossierController,
                                  PatientController patientController, String username,
                                  DossierDTO dossierToEdit, Long defaultMedecinId) {
        super(parent, dossierToEdit == null ? "Nouveau dossier medical" : "Modifier le dossier medical", true);

        this.dossierController = dossierController;
        this.patientController = patientController;
        this.username = username;
        this.dossierToEdit = dossierToEdit;
        this.defaultMedecinId = defaultMedecinId;

        setSize(640, 520);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        loadPatients();
        loadMedecins();

        if (dossierToEdit != null) {
            selectMedecinById(dossierToEdit.medecinId());
            for (int i = 0; i < cbPatient.getItemCount(); i++) {
                PatientComboItem item = cbPatient.getItemAt(i);
                if (item != null && item.getPatientId() != null && item.getPatientId().equals(dossierToEdit.patientId())) {
                    cbPatient.setSelectedIndex(i);
                    break;
                }
            }
            txtNotes.setText(dossierToEdit.notes() != null ? dossierToEdit.notes() : "");
            updateCharCount();
        }

        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(DentalTheme.BG);

        content.add(buildForm(), BorderLayout.CENTER);
        content.add(buildButtons(), BorderLayout.SOUTH);

        setContentPane(content);

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

        txtNotes.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateCharCount(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateCharCount(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateCharCount(); }
        });
    }

    private void loadPatients() {
        try {
            List<PatientListDto> patients = patientController.lister();
            cbPatient.addItem(new PatientComboItem(null, "-- Selectionner un patient --"));
            for (PatientListDto p : patients) {
                String displayText = p.getNomComplet() != null ? p.getNomComplet() :
                        (p.getId() != null ? "Patient #" + p.getId() : "Patient inconnu");
                if (p.getTelephone() != null && !p.getTelephone().isEmpty()) {
                    displayText += " (" + p.getTelephone() + ")";
                }
                cbPatient.addItem(new PatientComboItem(p.getId(), displayText));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement patients: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMedecins() {
        DefaultComboBoxModel<MedecinComboItem> model = new DefaultComboBoxModel<>();
        model.addElement(new MedecinComboItem(null, "-- Selectionner un medecin --"));

        try {
            var repo = new ma.dentalTech.repository.modules.users.impl.MedecinRepositoryImpl();
            List<ma.dentalTech.entities.users.Medecin> list;

            if (defaultMedecinId != null) {
                var m = repo.findById(defaultMedecinId);
                list = (m == null) ? List.of() : List.of(m);
            } else {
                list = repo.findAll();
            }

            if (list != null) {
                for (var m : list) {
                    String label = ((m.getNom() == null ? "" : m.getNom()) + " " +
                            (m.getPrenom() == null ? "" : m.getPrenom())).trim();
                    if (label.isBlank()) label = "Medecin #" + m.getId();
                    model.addElement(new MedecinComboItem(m.getId(), label));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement medecins: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }

        cbMedecin.setModel(model);
        if (defaultMedecinId != null) {
            selectMedecinById(defaultMedecinId);
            cbMedecin.setEnabled(false);
        }
    }

    private void selectMedecinById(Long medecinId) {
        if (medecinId == null) return;
        for (int i = 0; i < cbMedecin.getItemCount(); i++) {
            MedecinComboItem item = cbMedecin.getItemAt(i);
            if (item != null && medecinId.equals(item.getMedecinId())) {
                cbMedecin.setSelectedIndex(i);
                return;
            }
        }
    }

    private void updateCharCount() {
        int count = txtNotes.getText().length();
        lblCharCount.setText(count + " / 5000 caracteres");
        if (count > 5000) {
            lblCharCount.setForeground(new Color(0xDC, 0x35, 0x45));
        } else if (count > 4500) {
            lblCharCount.setForeground(new Color(0xFF, 0xA5, 0x00));
        } else {
            lblCharCount.setForeground(DentalTheme.MUTED);
        }
    }

    private JComponent buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 0, 10, 10);
        gc.anchor = GridBagConstraints.WEST;

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

        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.WEST;
        JLabel lblMedecin = new JLabel("Medecin *:");
        lblMedecin.setFont(DentalTheme.textBold(13));
        lblMedecin.setForeground(DentalTheme.TEXT2);
        form.add(lblMedecin, gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        cbMedecin.setFont(DentalTheme.textFont(13));
        cbMedecin.setPreferredSize(new Dimension(300, 35));
        form.add(cbMedecin, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblNotes = new JLabel("Notes:");
        lblNotes.setFont(DentalTheme.textFont(13));
        lblNotes.setForeground(DentalTheme.TEXT2);
        form.add(lblNotes, gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        JScrollPane scroll = new JScrollPane(txtNotes);
        txtNotes.setFont(DentalTheme.textFont(13));
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        scroll.setPreferredSize(new Dimension(300, 120));
        form.add(scroll, gc);

        gc.gridx = 1;
        gc.gridy = 3;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.EAST;
        lblCharCount.setFont(DentalTheme.textFont(11));
        lblCharCount.setForeground(DentalTheme.MUTED);
        form.add(lblCharCount, gc);

        return form;
    }

    private JComponent buildButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(btnCancel);
        buttons.add(btnSave);
        return buttons;
    }

    private boolean validateAndSave() {
        PatientComboItem selectedPatient = (PatientComboItem) cbPatient.getSelectedItem();
        if (selectedPatient == null || selectedPatient.getPatientId() == null) {
            showError("Veuillez selectionner un patient.");
            cbPatient.requestFocus();
            return false;
        }

        MedecinComboItem selectedMedecin = (MedecinComboItem) cbMedecin.getSelectedItem();
        Long medecinId = selectedMedecin == null ? null : selectedMedecin.getMedecinId();
        if (medecinId == null) {
            medecinId = (dossierToEdit != null ? dossierToEdit.medecinId() : defaultMedecinId);
        }
        if (medecinId == null) {
            showError("Veuillez selectionner un medecin.");
            cbMedecin.requestFocus();
            return false;
        }

        String notes = txtNotes.getText().trim();
        if (notes.length() > 5000) {
            showError("Les notes ne peuvent pas depasser 5000 caracteres. Actuellement : " + notes.length());
            txtNotes.requestFocus();
            return false;
        }

        try {
            DossierDTO dossier = new DossierDTO(
                    dossierToEdit != null ? dossierToEdit.id() : null,
                    selectedPatient.getPatientId(),
                    medecinId,
                    notes.isEmpty() ? null : notes
            );

            if (dossierToEdit == null) {
                Long dossierId = dossierController.create(dossier, username);
                JOptionPane.showMessageDialog(this,
                        "Dossier medical cree (ID: " + dossierId + ")",
                        "Succes",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                dossierController.update(dossier, username);
                JOptionPane.showMessageDialog(this,
                        "Dossier medical modifie",
                        "Succes",
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
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
