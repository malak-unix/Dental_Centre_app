package ma.dentalTech.mvc.ui.modules.dossierMedicale.consultation;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ActeController;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.intervention.InterventionMedecinDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.intervention.SaveInterventionRequestDTO;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.acte.ActeAddFormUI;
import ma.dentalTech.service.modules.dossierMedical.api.InterventionMedecinService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Frame;
import java.util.List;

/**
 * Dialog pour ajouter un acte à une consultation.
 */
public class ActeAddToConsultationUI extends JDialog {

    private final Long consultationId;
    private final String username;
    private final InterventionMedecinService interventionService;
    private final ActeController acteController;

    private final JComboBox<ActeComboItem> cbActe = new JComboBox<>();
    private final JTextField txtPrix = new JTextField(15);
    private final JTextField txtNumDent = new JTextField(10);

    private final JButton btnCancel = new JButton("Annuler");
    private final JButton btnSave = new JButton("Ajouter");
    private final JButton btnNewActe = new JButton("+ Nouveau acte");

    private boolean confirmed = false;

    public static class ActeComboItem {
        private final Long acteId;
        private final String displayText;
        private final Double prixBase;

        public ActeComboItem(Long acteId, String displayText, Double prixBase) {
            this.acteId = acteId;
            this.displayText = displayText;
            this.prixBase = prixBase;
        }

        public Long getActeId() { return acteId; }
        public Double getPrixBase() { return prixBase; }

        @Override
        public String toString() {
            return displayText;
        }
    }

    public ActeAddToConsultationUI(Frame parent, Long consultationId, String username) {
        super(parent, "Ajouter un acte à la consultation", true);

        this.consultationId = consultationId;
        this.username = username;

        // Récupérer les services
        Object interventionBean = ApplicationContext.getBean("interventionMedecinService");
        if (!(interventionBean instanceof InterventionMedecinService service)) {
            throw new RuntimeException("interventionMedecinService introuvable");
        }
        this.interventionService = service;

        Object acteBean = ApplicationContext.getBean("acteController");
        if (!(acteBean instanceof ActeController controller)) {
            throw new RuntimeException("acteController introuvable");
        }
        this.acteController = controller;

        setSize(450, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        loadActes();

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(20, 20));
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

        btnNewActe.addActionListener(e -> {
            Frame parentFrame = null;
            Window owner = SwingUtilities.getWindowAncestor(this);
            if (owner instanceof Frame f) parentFrame = f;
            else if (owner instanceof Dialog d && d.getOwner() instanceof Frame f) parentFrame = f;

            ActeAddFormUI dialog = new ActeAddFormUI(parentFrame, acteController, username);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                reloadActes();
            }
        });

        // Quand un acte est sélectionné, remplir le prix de base
        cbActe.addActionListener(e -> {
            ActeComboItem selected = (ActeComboItem) cbActe.getSelectedItem();
            if (selected != null && selected.getPrixBase() != null) {
                txtPrix.setText(String.format("%.2f", selected.getPrixBase()));
            }
        });
    }

    private void reloadActes() {
        loadActes();
    }

    private void loadActes() {
        try {
            cbActe.removeAllItems();
            List<ActeListItemDTO> actes = acteController.findAll();
            cbActe.addItem(new ActeComboItem(null, "-- Sélectionner un acte --", null));
            for (ActeListItemDTO acte : actes) {
                String displayText = acte.getLibelle() != null ? acte.getLibelle() : "Acte #" + acte.getActeId();
                if (acte.getPrixBase() != null) {
                    displayText += " (" + String.format("%.2f €", acte.getPrixBase()) + ")";
                }
                cbActe.addItem(new ActeComboItem(acte.getActeId(), displayText, acte.getPrixBase()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des actes: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComponent buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 0, 10, 10);
        gc.anchor = GridBagConstraints.WEST;

        // Acte
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        form.add(new JLabel("Acte *:"), gc);
        gc.gridx = 1; gc.weightx = 1.0; gc.fill = GridBagConstraints.HORIZONTAL;
        cbActe.setFont(DentalTheme.textFont(13));
        cbActe.setPreferredSize(new Dimension(250, 30));
        form.add(cbActe, gc);

        // Prix
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        form.add(new JLabel("Prix patient (€):"), gc);
        gc.gridx = 1; gc.weightx = 1.0; gc.fill = GridBagConstraints.HORIZONTAL;
        txtPrix.setFont(DentalTheme.textFont(13));
        form.add(txtPrix, gc);

        // Numéro dent (optionnel)
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        form.add(new JLabel("Numéro dent:"), gc);
        gc.gridx = 1; gc.weightx = 1.0; gc.fill = GridBagConstraints.HORIZONTAL;
        txtNumDent.setFont(DentalTheme.textFont(13));
        form.add(txtNumDent, gc);

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

        btnSave.setFont(DentalTheme.textBold(13));
        btnSave.setBackground(DentalTheme.PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.GOLD, 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnSave.setFocusPainted(false);

        btnNewActe.setFont(DentalTheme.textFont(13));
        btnNewActe.setBackground(DentalTheme.BEIGE);
        btnNewActe.setForeground(DentalTheme.TEXT2);
        btnNewActe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));

        buttons.add(btnNewActe);
        buttons.add(btnCancel);
        buttons.add(btnSave);

        return buttons;
    }

    private boolean validateAndSave() {
        // Validation : Acte obligatoire
        ActeComboItem selectedActe = (ActeComboItem) cbActe.getSelectedItem();
        if (selectedActe == null || selectedActe.getActeId() == null) {
            showError("Veuillez sélectionner un acte.");
            cbActe.requestFocus();
            return false;
        }

        // Validation : Prix
        Double prix = null;
        String prixStr = txtPrix.getText().trim();
        if (!prixStr.isEmpty()) {
            try {
                prix = Double.parseDouble(prixStr);
                if (prix < 0) {
                    showError("Le prix doit être positif ou nul.");
                    txtPrix.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Prix invalide. Format attendu: nombre décimal (ex: 50.00)");
                txtPrix.requestFocus();
                return false;
            }
        }

        // Validation : Numéro dent (optionnel)
        Integer numDent = null;
        String numDentStr = txtNumDent.getText().trim();
        if (!numDentStr.isEmpty()) {
            try {
                numDent = Integer.parseInt(numDentStr);
                if (numDent < 0 || numDent > 52) {
                    showError("Le numéro de dent doit être entre 0 et 52.");
                    txtNumDent.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Numéro de dent invalide.");
                txtNumDent.requestFocus();
                return false;
            }
        }

        // Création
        try {
            InterventionMedecinDTO intervention = new InterventionMedecinDTO(
                    null, // id
                    consultationId,
                    selectedActe.getActeId(),
                    prix != null ? prix : 0.0,
                    numDent
            );

            SaveInterventionRequestDTO request = new SaveInterventionRequestDTO(
                    intervention,
                    new ActorDTO(username)
            );

            interventionService.create(request);
            JOptionPane.showMessageDialog(this,
                    "Acte ajouté avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            showError("Erreur lors de l'ajout: " + ex.getMessage());
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
