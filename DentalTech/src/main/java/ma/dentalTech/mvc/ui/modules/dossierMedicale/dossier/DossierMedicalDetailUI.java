package ma.dentalTech.mvc.ui.modules.dossierMedicale.dossier;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.mvc.controllers.modules.caisse.api.FactureControllerV2;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.CertificatController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ConsultationController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.MedicamentController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.OrdonnanceController;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.caisse.FactureCreateDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.document.DocumentMedicalDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.AntecedentDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierDetailEnrichedDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.intervention.InterventionMedecinDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.intervention.SaveInterventionRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.readonly.FactureDTO;
import ma.dentalTech.mvc.dto.patient.AntecedentFormDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.certificat.CertificatAddFormUI;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.consultation.ConsultationAddFormUI;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.ordonnance.OrdonnanceAddFormUI;
import ma.dentalTech.mvc.ui.modules.patient.AntecedentFormDialog;
import ma.dentalTech.service.modules.dossierMedical.api.InterventionMedecinService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.awt.*;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Interface pour consulter les détails d'un dossier médical.
 * Affiche les informations du patient et des onglets pour :
 * - Consultations
 * - Ordonnances
 * - Certificats
 * - Situation financière (factures)
 * - Antécédents
 * - Documents
 */
public class DossierMedicalDetailUI extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DossierMedicalController controller;
    private final PatientController patientController;
    private final DossierDetailEnrichedDTO detail;
    private final Runnable onClose;
    private final String username;

    private final JTabbedPane tabbedPane = new JTabbedPane();

    public DossierMedicalDetailUI(DossierMedicalController controller, Long dossierId, Runnable onClose) {
        this.controller = controller;
        this.onClose = onClose;
        this.username = "user"; // TODO: récupérer depuis la session

        // Récupérer le PatientController depuis ApplicationContext
        Object bean = ApplicationContext.getBean("patientController");
        if (bean instanceof PatientController pc) {
            this.patientController = pc;
        } else {
            throw new RuntimeException("patientController introuvable dans ApplicationContext");
        }

        // Charger les détails
        try {
            this.detail = controller.getDetail(dossierId);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement des détails", e);
        }

        setLayout(new BorderLayout());
        setOpaque(false);

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(20, 20));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildTabs(), BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        // Informations patient
        JPanel patientInfo = new JPanel(new BorderLayout());
        patientInfo.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftPanel.setOpaque(false);

        // Avatar (icône simple)
        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 40));
        leftPanel.add(avatar);

        // Informations patient
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(detail.patientNomComplet());
        nameLabel.setFont(DentalTheme.titleFont(18));
        nameLabel.setForeground(new Color(0x1C, 0x25, 0x41));
        infoPanel.add(nameLabel);

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        detailsPanel.setOpaque(false);
        
        if (detail.patientAge() != null) {
            detailsPanel.add(new JLabel("Âge: " + detail.patientAge() + " ans"));
        }
        if (detail.patientTelephone() != null) {
            detailsPanel.add(new JLabel("| Téléphone: " + detail.patientTelephone()));
        }
        if (detail.patientGroupeSanguin() != null) {
            detailsPanel.add(new JLabel("| Groupe sanguin: " + detail.patientGroupeSanguin()));
        }
        
        infoPanel.add(detailsPanel);
        leftPanel.add(infoPanel);

        patientInfo.add(leftPanel, BorderLayout.WEST);

        // Boutons d'action
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnModifier = new DentalButton("Modifier infos dossier");
        btnModifier.setFont(DentalTheme.textFont(13));
        btnModifier.setBackground(DentalTheme.CARD);
        btnModifier.setForeground(DentalTheme.TEXT2);
        btnModifier.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnModifier.addActionListener(e -> {
            DossierMedicalAddFormUI dialog = new DossierMedicalAddFormUI(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    controller,
                    patientController,
                    username,
                    detail.dossier()
            );
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                // Recharger les détails après modification
                try {
                    controller.getDetail(detail.dossier().id());
                    // Note: On ne peut pas mettre à jour directement, il faudrait recréer le panel
                    // Pour l'instant, on ferme et on demande de rouvrir
                    JOptionPane.showMessageDialog(this,
                            "Dossier modifié avec succès. Veuillez fermer et rouvrir pour voir les modifications.",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors du rechargement: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnSupprimer = new DentalButton("Supprimer dossier");
        btnSupprimer.setFont(DentalTheme.textFont(13));
        btnSupprimer.setBackground(DentalTheme.CARD);
        btnSupprimer.setForeground(DentalTheme.TEXT2);
        btnSupprimer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDC, 0x35, 0x45), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnSupprimer.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(
                    this,
                    "Supprimer définitivement ce dossier médical ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (ok == JOptionPane.YES_OPTION) {
                try {
                    controller.delete(detail.dossier().id(), "user");
                    JOptionPane.showMessageDialog(this, "Dossier supprimé avec succès");
                    onClose.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonsPanel.add(btnModifier);
        buttonsPanel.add(btnSupprimer);

        patientInfo.add(buttonsPanel, BorderLayout.EAST);

        header.add(patientInfo);
        header.add(Box.createVerticalStrut(15));

        // Titre DOSSIER MÉDICAL
        JLabel title = new JLabel("DOSSIER MÉDICAL");
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(new Color(0x1C, 0x25, 0x41));
        header.add(title);

        return header;
    }

    private JComponent buildTabs() {
        tabbedPane.setFont(DentalTheme.textFont(13));
        tabbedPane.setOpaque(false);

        // Onglet Consultations
        tabbedPane.addTab("Consultations", buildConsultationsTab());

        // Onglet Ordonnances
        tabbedPane.addTab("Ordonnances", buildOrdonnancesTab());

        // Onglet Certificats
        tabbedPane.addTab("Certificats", buildCertificatsTab());

        // Onglet Situation financière
        tabbedPane.addTab("Situation financière", buildSituationFinanciereTab());

        // Onglet Antécédents
        tabbedPane.addTab("Antécédents", buildAntecedentsTab());

        // Onglet Documents
        tabbedPane.addTab("Documents", buildDocumentsTab());

        return tabbedPane;
    }

    private JComponent buildConsultationsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JButton btnAdd = new DentalButton("+ Nouvelle consultation");
        btnAdd.setFont(DentalTheme.textBold(13));
        btnAdd.setBackground(DentalTheme.CARD);
        btnAdd.setForeground(DentalTheme.TEXT2);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnAdd.addActionListener(e -> onCreateConsultation());
        panel.add(btnAdd, BorderLayout.NORTH);

        // Tableau des consultations
        JTable table = new JTable(new ConsultationsTableModel(detail.consultations()));
        table.setRowHeight(50);
        table.setFont(DentalTheme.textFont(13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(DentalTheme.BORDER);
        table.setShowGrid(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Date
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Statut
        table.getColumnModel().getColumn(2).setPreferredWidth(300); // Observation

        // Renderer pour le statut
        table.getColumnModel().getColumn(1).setCellRenderer(new StatutConsultationCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void onCreateConsultation() {
        try {
            // Récupérer le ConsultationController
            Object consultationBean = ApplicationContext.getBean("consultationController");
            if (!(consultationBean instanceof ConsultationController consultationController)) {
                JOptionPane.showMessageDialog(this, "Controller consultation introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer une liste avec le dossier courant
            List<ConsultationAddFormUI.DossierComboItem> dossiers = new ArrayList<>();
            dossiers.add(new ConsultationAddFormUI.DossierComboItem(
                    detail.dossier().id(),
                    "Dossier #" + detail.dossier().id() + " - " + detail.patientNomComplet()
            ));

            // Ouvrir le formulaire de création de consultation
            Optional<ConsultationAddFormUI.ConsultationFormResult> result = ConsultationAddFormUI.showDialog(this, dossiers, username);
            if (result.isPresent()) {
                ConsultationAddFormUI.ConsultationFormResult res = result.get();
                // Cr?er la consultation
                Long consultationId = consultationController.create(res.getConsultation(), username);
                createInterventions(consultationId, res.getActes());
                JOptionPane.showMessageDialog(this,
                        "Consultation cr??e avec succ?s (ID: " + consultationId + ")",
                        "Succ?s",
                        JOptionPane.INFORMATION_MESSAGE);
                // TODO: Recharger les d?tails du dossier pour mettre ? jour la liste
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la création de la consultation: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onLoadDocument() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner un document à charger");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this,
                    "Document sélectionné: " + selectedFile.getName() + "\n" +
                    "Taille: " + selectedFile.length() + " octets\n" +
                    "Fonctionnalité de chargement à implémenter dans le service",
                    "Document",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private Frame getParentFrame() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner instanceof Frame f) return f;
        if (owner instanceof Dialog d && d.getOwner() instanceof Frame f) return f;
        return null;
    }

    private void createInterventions(Long consultationId, List<ConsultationAddFormUI.SelectedActe> actes) {
        if (consultationId == null || actes == null || actes.isEmpty()) return;
        Object bean = ApplicationContext.getBean("interventionMedecinService");
        if (!(bean instanceof InterventionMedecinService service)) return;

        for (ConsultationAddFormUI.SelectedActe acte : actes) {
            InterventionMedecinDTO dto = new InterventionMedecinDTO(
                    null,
                    consultationId,
                    acte.getActeId(),
                    acte.getPrix(),
                    0
            );
            SaveInterventionRequestDTO req = new SaveInterventionRequestDTO(dto, new ActorDTO(username));
            service.create(req);
        }
    }

    private void onAddOrdonnanceFromDossier() {
        try {
            Object ordonnanceBean = ApplicationContext.getBean("ordonnanceController");
            if (!(ordonnanceBean instanceof OrdonnanceController ordonnanceController)) {
                JOptionPane.showMessageDialog(this, "Controller ordonnance introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (detail.consultations() == null || detail.consultations().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucune consultation disponible pour ce dossier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            List<OrdonnanceAddFormUI.ConsultationComboItem> consultations = new ArrayList<>();
            for (ConsultationDTO c : detail.consultations()) {
                if (c == null || c.id() == null) continue;
                String label = "Consultation du " + (c.date() != null ? c.date().format(DATE_FMT) : "-");
                consultations.add(new OrdonnanceAddFormUI.ConsultationComboItem(c.id(), label));
            }

            List<OrdonnanceAddFormUI.MedicamentComboItem> medicaments = loadMedicamentsForOrdonnance();
            OrdonnanceAddFormUI dialog = new OrdonnanceAddFormUI(getParentFrame(), ordonnanceController, consultations, medicaments, username);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                JOptionPane.showMessageDialog(this, "Ordonnance cr??e avec succ?s.", "Succ?s", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout d'ordonnance: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<OrdonnanceAddFormUI.MedicamentComboItem> loadMedicamentsForOrdonnance() {
        List<OrdonnanceAddFormUI.MedicamentComboItem> out = new ArrayList<>();
        Object bean = ApplicationContext.getBean(MedicamentController.class);
        if (!(bean instanceof MedicamentController medicamentController)) return out;
        try {
            List<Medicament> meds = medicamentController.getAll();
            if (meds != null) {
                for (Medicament m : meds) {
                    String label = m.getNom() != null ? m.getNom() : ("Medicament #" + m.getId());
                    out.add(new OrdonnanceAddFormUI.MedicamentComboItem(m.getId(), label));
                }
            }
        } catch (Exception ignored) {
            // ignore load errors
        }
        return out;
    }

    private void onAddCertificatFromDossier() {
        try {
            Object certificatBean = ApplicationContext.getBean("certificatController");
            if (!(certificatBean instanceof CertificatController certificatController)) {
                JOptionPane.showMessageDialog(this, "Controller certificat introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<CertificatAddFormUI.DossierComboItem> dossiers = new ArrayList<>();
            dossiers.add(new CertificatAddFormUI.DossierComboItem(
                    detail.dossier().id(),
                    "Dossier #" + detail.dossier().id() + " - " + detail.patientNomComplet()
            ));

            CertificatAddFormUI dialog = new CertificatAddFormUI(getParentFrame(), certificatController, dossiers, username);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                JOptionPane.showMessageDialog(this, "Certificat cr?? avec succ?s.", "Succ?s", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du certificat: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAddFactureFromDossier() {
        try {
            Object factureBean = ApplicationContext.getBean(FactureControllerV2.class);
            if (!(factureBean instanceof FactureControllerV2 factureController)) {
                JOptionPane.showMessageDialog(this, "Controller facture introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (detail.consultations() == null || detail.consultations().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucune consultation disponible pour cr?er une facture.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JComboBox<ConsultationItem> cb = new JComboBox<>();
            for (ConsultationDTO c : detail.consultations()) {
                if (c == null || c.id() == null) continue;
                String label = "Consultation du " + (c.date() != null ? c.date().format(DATE_FMT) : "-");
                cb.addItem(new ConsultationItem(c.id(), label));
            }

            JTextField tfMontant = new JTextField(10);
            tfMontant.setText("0");

            JPanel p = new JPanel(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(6, 6, 6, 6);
            gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.WEST;
            p.add(new JLabel("Consultation:"), gc);
            gc.gridx = 1; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;
            p.add(cb, gc);
            gc.gridx = 0; gc.gridy = 1; gc.fill = GridBagConstraints.NONE; gc.weightx = 0;
            p.add(new JLabel("Montant (DH):"), gc);
            gc.gridx = 1; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;
            p.add(tfMontant, gc);

            int ok = JOptionPane.showConfirmDialog(this, p, "Nouvelle facture", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) return;

            ConsultationItem item = (ConsultationItem) cb.getSelectedItem();
            if (item == null || item.id == null) {
                JOptionPane.showMessageDialog(this, "Veuillez s?lectionner une consultation.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double montant;
            try {
                montant = Double.parseDouble(tfMontant.getText().trim());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Montant invalide.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (montant <= 0) {
                JOptionPane.showMessageDialog(this, "Le montant doit ?tre > 0.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            FactureCreateDTO dto = FactureCreateDTO.builder()
                    .consultationId(item.id)
                    .dateFacture(LocalDate.now())
                    .totalFacture(BigDecimal.valueOf(montant))
                    .build();
            factureController.create(dto);
            JOptionPane.showMessageDialog(this, "Facture cr??e avec succ?s.", "Succ?s", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la cr?ation de la facture: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAddAntecedentFromDossier() {
        try {
            Object antecedentBean = ApplicationContext.getBean("antecedentController");
            if (!(antecedentBean instanceof AntecedentController antecedentController)) {
                JOptionPane.showMessageDialog(this, "Controller antecedent introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AntecedentFormDialog dialog = new AntecedentFormDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Nouvel antecedent",
                    null,
                    detail.patientId()
            );
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                AntecedentFormDto dto = dialog.getDto();
                antecedentController.create(detail.patientId(), dto);
                JOptionPane.showMessageDialog(this, "Antecedent cr?? avec succ?s.", "Succ?s", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'antecedent: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ConsultationItem {
        private final Long id;
        private final String label;
        ConsultationItem(Long id, String label) {
            this.id = id;
            this.label = label;
        }
        @Override public String toString() { return label; }
    }


    private JComponent buildOrdonnancesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        JButton btnAdd = new DentalButton("+ Nouvelle ordonnance");
        btnAdd.setFont(DentalTheme.textBold(13));
        btnAdd.setBackground(DentalTheme.CARD);
        btnAdd.setForeground(DentalTheme.TEXT2);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnAdd.addActionListener(e -> onAddOrdonnanceFromDossier());
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(btnAdd, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);


        // Tableau des ordonnances
        JTable table = new JTable(new OrdonnancesTableModel(detail.ordonnances()));
        table.setRowHeight(40);
        table.setFont(DentalTheme.textFont(13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(DentalTheme.BORDER);
        table.setShowGrid(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(150); // Date
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Consultation

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildCertificatsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        JButton btnAdd = new DentalButton("+ Certificat");
        btnAdd.setFont(DentalTheme.textBold(13));
        btnAdd.setBackground(DentalTheme.CARD);
        btnAdd.setForeground(DentalTheme.TEXT2);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnAdd.addActionListener(e -> onAddCertificatFromDossier());
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(btnAdd, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);


        // Tableau des certificats
        JTable table = new JTable(new CertificatsTableModel(detail.certificats()));
        table.setRowHeight(40);
        table.setFont(DentalTheme.textFont(13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(DentalTheme.BORDER);
        table.setShowGrid(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(120); // Date début
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Date fin
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Durée
        table.getColumnModel().getColumn(3).setPreferredWidth(300); // Note

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildSituationFinanciereTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        JButton btnAddFacture = new DentalButton("+ Nouvelle facture");
        btnAddFacture.setFont(DentalTheme.textBold(13));
        btnAddFacture.setBackground(DentalTheme.CARD);
        btnAddFacture.setForeground(DentalTheme.TEXT2);
        btnAddFacture.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnAddFacture.addActionListener(e -> onAddFactureFromDossier());
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(btnAddFacture, BorderLayout.EAST);


        // Résumé financier
        if (detail.situationFinanciere() != null) {
            JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
            summary.setOpaque(false);

            CardPanel cardTotal = createSummaryCard("Total dû", 
                    String.format("%.2f €", detail.situationFinanciere().totalDesActes() != null ? detail.situationFinanciere().totalDesActes() : 0.0));
            CardPanel cardPaye = createSummaryCard("Payé", 
                    String.format("%.2f €", detail.situationFinanciere().totalPaye() != null ? detail.situationFinanciere().totalPaye() : 0.0));
            CardPanel cardReste = createSummaryCard("Reste à payer", 
                    String.format("%.2f €", (detail.situationFinanciere().totalDesActes() != null ? detail.situationFinanciere().totalDesActes() : 0.0) -
                            (detail.situationFinanciere().totalPaye() != null ? detail.situationFinanciere().totalPaye() : 0.0)));

            summary.add(cardTotal);
            summary.add(cardPaye);
            summary.add(cardReste);
            top.add(summary, BorderLayout.WEST);
        }

        panel.add(top, BorderLayout.NORTH);
        // Tableau des factures
        JTable table = new JTable(new FacturesTableModel(detail.factures()));
        table.setRowHeight(40);
        table.setFont(DentalTheme.textFont(13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(DentalTheme.BORDER);
        table.setShowGrid(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(120); // Numéro
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Date
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Total
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Payé
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Reste
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Statut

        // Renderer pour le statut
        table.getColumnModel().getColumn(5).setCellRenderer(new StatutFactureCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildAntecedentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        JButton btnAdd = new DentalButton("+ Nouvel antecedent");
        btnAdd.setFont(DentalTheme.textBold(13));
        btnAdd.setBackground(DentalTheme.CARD);
        btnAdd.setForeground(DentalTheme.TEXT2);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnAdd.addActionListener(e -> onAddAntecedentFromDossier());
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(btnAdd, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);


        // Tableau des antécédents
        JTable table = new JTable(new AntecedentsTableModel(detail.antecedents()));
        table.setRowHeight(40);
        table.setFont(DentalTheme.textFont(13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(DentalTheme.BORDER);
        table.setShowGrid(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Nom
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Catégorie
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Niveau de risque
        table.getColumnModel().getColumn(3).setPreferredWidth(400); // Description

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildDocumentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JButton btnAdd = new DentalButton("+ Charger document");
        btnAdd.setFont(DentalTheme.textBold(13));
        btnAdd.setBackground(DentalTheme.CARD);
        btnAdd.setForeground(DentalTheme.TEXT2);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnAdd.addActionListener(e -> onLoadDocument());
        panel.add(btnAdd, BorderLayout.NORTH);

        // Tableau des documents
        JTable table = new JTable(new DocumentsTableModel(detail.documents()));
        table.setRowHeight(40);
        table.setFont(DentalTheme.textFont(13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(DentalTheme.BORDER);
        table.setShowGrid(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Titre
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Type
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Taille
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Date

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private CardPanel createSummaryCard(String label, String value) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        card.setPreferredSize(new Dimension(150, 80));

        JLabel lbl = new JLabel(label);
        lbl.setFont(DentalTheme.textFont(12));
        lbl.setForeground(DentalTheme.MUTED);
        card.add(lbl, BorderLayout.NORTH);

        JLabel val = new JLabel(value);
        val.setFont(DentalTheme.textBold(16));
        val.setForeground(DentalTheme.TEXT2);
        card.add(val, BorderLayout.CENTER);

        return card;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);

        JButton btnClose = new DentalButton("Fermer");
        btnClose.setFont(DentalTheme.textFont(13));
        btnClose.setBackground(DentalTheme.BEIGE);
        btnClose.setForeground(DentalTheme.TEXT2);
        btnClose.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnClose.addActionListener(e -> onClose.run());

        footer.add(btnClose);

        return footer;
    }

    // =========================================================
    // Table models
    // =========================================================
    private class ConsultationsTableModel extends AbstractTableModel {
        private final String[] cols = {"Date", "Statut", "Observation"};
        private final List<ConsultationDTO> consultations;

        ConsultationsTableModel(List<ConsultationDTO> consultations) {
            this.consultations = consultations != null ? consultations : new ArrayList<>();
        }

        @Override public int getRowCount() { return consultations.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ConsultationDTO c = consultations.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> c.date() != null ? c.date().format(DATE_FMT) : "";
                case 1 -> c.statut() != null ? c.statut().name() : "";
                case 2 -> c.observationMedecin() != null ? c.observationMedecin() : "";
                default -> "";
            };
        }
    }

    private class OrdonnancesTableModel extends AbstractTableModel {
        private final String[] cols = {"Date", "Consultation"};
        private final List<OrdonnanceDTO> ordonnances;

        OrdonnancesTableModel(List<OrdonnanceDTO> ordonnances) {
            this.ordonnances = ordonnances != null ? ordonnances : new ArrayList<>();
        }

        @Override public int getRowCount() { return ordonnances.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            OrdonnanceDTO o = ordonnances.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> o.date() != null ? o.date().format(DATE_FMT) : "";
                case 1 -> o.consultationId() != null ? "Consultation #" + o.consultationId() : "";
                default -> "";
            };
        }
    }

    private class CertificatsTableModel extends AbstractTableModel {
        private final String[] cols = {"Date début", "Date fin", "Durée (jours)", "Note médecin"};
        private final List<CertificatDTO> certificats;

        CertificatsTableModel(List<CertificatDTO> certificats) {
            this.certificats = certificats != null ? certificats : new ArrayList<>();
        }

        @Override public int getRowCount() { return certificats.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CertificatDTO c = certificats.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> c.dateDebut() != null ? c.dateDebut().format(DATE_FMT) : "";
                case 1 -> c.dateFin() != null ? c.dateFin().format(DATE_FMT) : "";
                case 2 -> c.duree() != null ? c.duree().toString() : "";
                case 3 -> c.noteMedecin() != null ? c.noteMedecin() : "";
                default -> "";
            };
        }
    }

    private class FacturesTableModel extends AbstractTableModel {
        private final String[] cols = {"Numéro", "Date", "Total", "Payé", "Reste", "Statut"};
        private final List<FactureDTO> factures;

        FacturesTableModel(List<FactureDTO> factures) {
            this.factures = factures != null ? factures : new ArrayList<>();
        }

        @Override public int getRowCount() { return factures.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            FactureDTO f = factures.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> "F" + String.format("%04d", f.id());
                case 1 -> f.dateFacture() != null ? f.dateFacture().format(DATE_FMT) : "";
                case 2 -> f.totalFacture() != null ? String.format("%.2f €", f.totalFacture()) : "0.00 €";
                case 3 -> f.totalPaye() != null ? String.format("%.2f €", f.totalPaye()) : "0.00 €";
                case 4 -> f.reste() != null ? String.format("%.2f €", f.reste()) : "0.00 €";
                case 5 -> f.statut() != null ? f.statut().name() : "";
                default -> "";
            };
        }
    }

    private class AntecedentsTableModel extends AbstractTableModel {
        private final String[] cols = {"Nom", "Catégorie", "Niveau de risque", "Description"};
        private final List<AntecedentDTO> antecedents;

        AntecedentsTableModel(List<AntecedentDTO> antecedents) {
            this.antecedents = antecedents != null ? antecedents : new ArrayList<>();
        }

        @Override public int getRowCount() { return antecedents.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            AntecedentDTO a = antecedents.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> a.nom() != null ? a.nom() : "";
                case 1 -> a.categorie() != null ? a.categorie() : "";
                case 2 -> a.niveauDeRisque() != null ? a.niveauDeRisque().name() : "";
                case 3 -> a.description() != null ? a.description() : "";
                default -> "";
            };
        }
    }

    private class DocumentsTableModel extends AbstractTableModel {
        private final String[] cols = {"Titre", "Type", "Taille", "Date"};
        private final List<DocumentMedicalDTO> documents;

        DocumentsTableModel(List<DocumentMedicalDTO> documents) {
            this.documents = documents != null ? documents : new ArrayList<>();
        }

        @Override public int getRowCount() { return documents.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DocumentMedicalDTO d = documents.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> d.titre() != null ? d.titre() : "";
                case 1 -> d.typeDocument() != null ? d.typeDocument().name() : "";
                case 2 -> {
                    if (d.tailleOctets() != null) {
                        long bytes = d.tailleOctets();
                        if (bytes < 1024) yield bytes + " B";
                        else if (bytes < 1024 * 1024) yield String.format("%.1f KB", bytes / 1024.0);
                        else yield String.format("%.1f MB", bytes / (1024.0 * 1024.0));
                    }
                    yield "";
                }
                case 3 -> d.dateDocument() != null ? d.dateDocument().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
                default -> "";
            };
        }
    }

    // =========================================================
    // Renderers
    // =========================================================
    private class StatutConsultationCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected && value instanceof StatutConsultation statut) {
                setOpaque(true);
                switch (statut) {
                    case TERMINE -> {
                        setBackground(new Color(0xD7, 0xF2, 0xD7));
                        setForeground(new Color(0x00, 0x66, 0x00));
                    }
                    case EN_COURS -> {
                        setBackground(new Color(0xFF, 0xF1, 0xCC));
                        setForeground(new Color(0x8B, 0x69, 0x1E));
                    }
                    case ANNULE -> {
                        setBackground(new Color(0xFF, 0xD6, 0xD6));
                        setForeground(new Color(0x8B, 0x00, 0x00));
                    }
                    case PLANIFIE -> {
                        setBackground(new Color(0xD6, 0xE9, 0xFF));
                        setForeground(new Color(0x00, 0x33, 0x66));
                    }
                }
            }

            return this;
        }
    }

    private class StatutFactureCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected && value instanceof StatutFacture statut) {
                setOpaque(true);
                switch (statut) {
                    case PAYEE -> {
                        setBackground(new Color(0xD7, 0xF2, 0xD7));
                        setForeground(new Color(0x00, 0x66, 0x00));
                    }
                    case PARTIEL -> {
                        setBackground(new Color(0xFF, 0xF1, 0xCC));
                        setForeground(new Color(0x8B, 0x69, 0x1E));
                    }
                    case NON_PAYEE -> {
                        setBackground(new Color(0xFF, 0xD6, 0xD6));
                        setForeground(new Color(0x8B, 0x00, 0x00));
                    }
                }
            }

            return this;
        }
    }
}
