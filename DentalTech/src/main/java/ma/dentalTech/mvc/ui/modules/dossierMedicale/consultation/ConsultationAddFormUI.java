package ma.dentalTech.mvc.ui.modules.dossierMedicale.consultation;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ActeController;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.acte.ActeAddFormUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Dialog modal pour ajouter une nouvelle consultation.
 * Selon la maquette fournie.
 */
public class ConsultationAddFormUI extends JDialog {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Champs du formulaire
    private final JComboBox<DossierComboItem> cbDossier = new JComboBox<>();
    private final JTextField tfDate = new JTextField();
    private final JComboBox<StatutConsultation> cbStatut = new JComboBox<>(StatutConsultation.values());
    private final JTextArea taObservation = new JTextArea(4, 30);

    private final JComboBox<ActeComboItem> cbActe = new JComboBox<>();
    private final DefaultListModel<ActeComboItem> modelActes = new DefaultListModel<>();
    private final JList<ActeComboItem> listActes = new JList<>(modelActes);
    private final JButton btnAddActe = new JButton("Ajouter acte");
    private final JButton btnRemoveActe = new JButton("Retirer acte");
    private final JButton btnNewActe = new JButton("+ Nouveau acte");

    private final String username;
    private ActeController acteController;

    private Optional<ConsultationFormResult> result = Optional.empty();

    /**
     * Item pour le combobox des dossiers médicaux.
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

    /**
     * Item pour le combobox des actes.
     */
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

    /**
     * Acte selectionne pour la consultation.
     */
    public static class SelectedActe {
        private final Long acteId;
        private final Double prix;

        public SelectedActe(Long acteId, Double prix) {
            this.acteId = acteId;
            this.prix = prix;
        }

        public Long getActeId() { return acteId; }
        public Double getPrix() { return prix; }
    }

    /**
     * Resultat du formulaire.
     */
    public static class ConsultationFormResult {
        private final ConsultationDTO consultation;
        private final List<SelectedActe> actes;

        public ConsultationFormResult(ConsultationDTO consultation, List<SelectedActe> actes) {
            this.consultation = consultation;
            this.actes = actes;
        }

        public ConsultationDTO getConsultation() { return consultation; }
        public List<SelectedActe> getActes() { return actes; }
    }

    /**
     * Affiche le dialog et retourne le resultat de consultation créé.
     * @param parent Composant parent
     * @param dossiers Liste des dossiers disponibles (pour le dropdown)
     * @return Optional avec ConsultationFormResult si validé, empty si annulé
     */
    public static Optional<ConsultationFormResult> showDialog(Component parent, java.util.List<DossierComboItem> dossiers) {
        return showDialog(parent, dossiers, "user");
    }

    public static Optional<ConsultationFormResult> showDialog(Component parent,
                                                              java.util.List<DossierComboItem> dossiers,
                                                              String username) {
        ConsultationAddFormUI dialog = new ConsultationAddFormUI(parent, dossiers, username);
        dialog.setVisible(true);
        return dialog.result;
    }

    private ConsultationAddFormUI(Component parent, java.util.List<DossierComboItem> dossiers, String username) {
        super(SwingUtilities.getWindowAncestor(parent), "Ajouter une consultation", ModalityType.APPLICATION_MODAL);

        this.username = username == null ? "user" : username;

        // Initialiser le dropdown des dossiers
        cbDossier.addItem(new DossierComboItem(null, "Selectionner un dossier medical..."));
        if (dossiers != null) {
            for (DossierComboItem item : dossiers) {
                cbDossier.addItem(item);
            }
        }

        loadActes();

        setContentPane(buildUI());
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);

        // Initialiser la date avec la date du jour
        tfDate.setText(LocalDate.now().format(DATE_FORMATTER));
    }

    private JComponent buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(DentalTheme.BG);

        // Titre
        JLabel title = new JLabel("Ajouter une consultation");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.TEXT2);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Formulaire
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        // Dossier médical
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        JLabel lblDossier = createLabel("Dossier médical du patient");
        form.add(lblDossier, c);
        c.gridx = 1; c.weightx = 1.0;
        cbDossier.setPreferredSize(new Dimension(300, 35));
        cbDossier.setFont(DentalTheme.BASE);
        styleField(cbDossier);
        form.add(cbDossier, c);

        // Date de consultation
        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        JLabel lblDate = createLabel("Date de consultation");
        form.add(lblDate, c);
        c.gridx = 1; c.weightx = 1.0;
        tfDate.setPreferredSize(new Dimension(300, 35));
        tfDate.setFont(DentalTheme.BASE);
        styleField(tfDate);
        // Ajouter une icône de calendrier (placeholder text)
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setOpaque(false);
        datePanel.add(tfDate, BorderLayout.CENTER);
        JLabel calendarIcon = new JLabel("CAL");
        calendarIcon.setBorder(new EmptyBorder(0, 5, 0, 5));
        datePanel.add(calendarIcon, BorderLayout.EAST);
        form.add(datePanel, c);

        // Statut
        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        JLabel lblStatut = createLabel("Statut");
        form.add(lblStatut, c);
        c.gridx = 1; c.weightx = 1.0;
        cbStatut.setPreferredSize(new Dimension(300, 35));
        cbStatut.setFont(DentalTheme.BASE);
        cbStatut.setSelectedItem(StatutConsultation.PLANIFIE); // Par défaut
        styleField(cbStatut);
        form.add(cbStatut, c);

        // Actes (selection)
        c.gridx = 0; c.gridy = 3; c.weightx = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblActes = createLabel("Actes");
        form.add(lblActes, c);
        c.gridx = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        JPanel acteSelectPanel = new JPanel(new BorderLayout(8, 0));
        acteSelectPanel.setOpaque(false);
        cbActe.setFont(DentalTheme.BASE);
        cbActe.setPreferredSize(new Dimension(300, 32));
        styleField(cbActe);
        acteSelectPanel.add(cbActe, BorderLayout.CENTER);

        JPanel acteBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        acteBtns.setOpaque(false);
        UiStyles.styleSecondaryButton(btnNewActe);
        UiStyles.styleSecondaryButton(btnAddActe);
        acteBtns.add(btnNewActe);
        acteBtns.add(btnAddActe);
        acteSelectPanel.add(acteBtns, BorderLayout.EAST);
        form.add(acteSelectPanel, c);

        // Liste des actes selectionnes
        c.gridx = 1; c.gridy = 4; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        listActes.setVisibleRowCount(4);
        listActes.setFont(DentalTheme.textFont(12));
        JScrollPane actesScroll = new JScrollPane(listActes);
        actesScroll.setPreferredSize(new Dimension(300, 90));
        form.add(actesScroll, c);

        // Bouton retirer
        c.gridx = 1; c.gridy = 5; c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        UiStyles.styleSecondaryButton(btnRemoveActe);
        form.add(btnRemoveActe, c);

        // Observation du medecin
        c.gridx = 0; c.gridy = 6; c.weightx = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblObservation = createLabel("Observation du medecin");
        form.add(lblObservation, c);
        c.gridx = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        taObservation.setFont(DentalTheme.BASE);
        styleField(taObservation);
        taObservation.setLineWrap(true);
        taObservation.setWrapStyleWord(true);
        JScrollPane scrollObservation = new JScrollPane(taObservation);
        scrollObservation.setPreferredSize(new Dimension(300, 100));
        form.add(scrollObservation, c);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnAnnuler = createButton("Annuler", false);
        JButton btnAjouter = createButton("+ Ajouter la consultation", true);

        btnAnnuler.addActionListener(e -> dispose());
        btnAjouter.addActionListener(e -> onSave());
        btnAddActe.addActionListener(e -> onAddActeToList());
        btnRemoveActe.addActionListener(e -> onRemoveActeFromList());
        btnNewActe.addActionListener(e -> onCreateNewActe());

        buttonPanel.add(btnAnnuler);
        buttonPanel.add(btnAjouter);

        // Assemblage
        CardPanel contentPanel = new CardPanel(null);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(DentalTheme.CARD);
        contentPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPanel.add(title, BorderLayout.NORTH);
        contentPanel.add(form, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        root.add(contentPanel, BorderLayout.CENTER);

        return root;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(DentalTheme.textBold(13));
        label.setForeground(DentalTheme.TEXT2);
        return label;
    }

    private JButton createButton(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(primary ? 220 : 140, 40));
        if (primary) UiStyles.stylePrimaryButton(button);
        else UiStyles.styleSecondaryButton(button);
        return button;
    }

    private void styleField(JComponent field) {
        if (field == null) return;
        if (field instanceof JTextField tf) {
            tf.setBorder(BorderFactory.createCompoundBorder(
                    UiStyles.roundedBorder(),
                    new EmptyBorder(6, 10, 6, 10)
            ));
            tf.setBackground(Color.WHITE);
        } else if (field instanceof JComboBox<?> cb) {
            cb.setBorder(BorderFactory.createCompoundBorder(
                    UiStyles.roundedBorder(),
                    new EmptyBorder(4, 8, 4, 8)
            ));
            cb.setBackground(Color.WHITE);
        } else if (field instanceof JTextArea ta) {
            ta.setBorder(BorderFactory.createCompoundBorder(
                    UiStyles.roundedBorder(),
                    new EmptyBorder(6, 10, 6, 10)
            ));
            ta.setBackground(Color.WHITE);
        }
    }

    private void onSave() {
        try {
            // Validation
            DossierComboItem selectedDossier = (DossierComboItem) cbDossier.getSelectedItem();
            if (selectedDossier == null || selectedDossier.getDossierId() == null) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner un dossier médical.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String dateStr = tfDate.getText().trim();
            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez saisir une date de consultation.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate date;
            try {
                date = LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this,
                        "Format de date invalide. Utilisez le format yyyy-MM-dd (ex: 2024-04-20).",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            StatutConsultation statut = (StatutConsultation) cbStatut.getSelectedItem();
            if (statut == null) {
                statut = StatutConsultation.PLANIFIE;
            }

            String observation = taObservation.getText() == null ? null : taObservation.getText().trim();
            if (observation != null && observation.isEmpty()) {
                observation = null;
            }

            // Créer le DTO
            ConsultationDTO consultation = new ConsultationDTO(
                    null, // pas d'id pour une nouvelle consultation
                    selectedDossier.getDossierId(),
                    date,
                    statut,
                    observation
            );

            List<SelectedActe> actes = new ArrayList<>();
            for (int i = 0; i < modelActes.getSize(); i++) {
                ActeComboItem item = modelActes.getElementAt(i);
                if (item != null && item.getActeId() != null) {
                    actes.add(new SelectedActe(item.getActeId(), item.getPrixBase()));
                }
            }

            result = Optional.of(new ConsultationFormResult(consultation, actes));
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadActes() {
        cbActe.removeAllItems();
        cbActe.addItem(new ActeComboItem(null, "-- Selectionner un acte --", null));
        Object bean = ApplicationContext.getBean("acteController");
        if (bean instanceof ActeController ctrl) {
            this.acteController = ctrl;
            try {
                List<ActeListItemDTO> actes = ctrl.findAll();
                if (actes != null) {
                    for (ActeListItemDTO a : actes) {
                        String label = a.getLibelle() != null ? a.getLibelle() : ("Acte #" + a.getActeId());
                        cbActe.addItem(new ActeComboItem(a.getActeId(), label, a.getPrixBase()));
                    }
                }
            } catch (Exception ignored) {
                // ignore load errors
            }
        }
    }

    private void onAddActeToList() {
        ActeComboItem selected = (ActeComboItem) cbActe.getSelectedItem();
        if (selected == null || selected.getActeId() == null) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner un acte.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (int i = 0; i < modelActes.getSize(); i++) {
            ActeComboItem item = modelActes.getElementAt(i);
            if (item != null && selected.getActeId().equals(item.getActeId())) {
                return;
            }
        }
        modelActes.addElement(selected);
    }

    private void onRemoveActeFromList() {
        int idx = listActes.getSelectedIndex();
        if (idx >= 0) modelActes.remove(idx);
    }

    private void onCreateNewActe() {
        if (acteController == null) return;
        Frame parentFrame = null;
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner instanceof Frame f) parentFrame = f;
        else if (owner instanceof Dialog d && d.getOwner() instanceof Frame f) parentFrame = f;

        ActeAddFormUI dialog = new ActeAddFormUI(parentFrame, acteController, username);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadActes();
        }
    }
}
