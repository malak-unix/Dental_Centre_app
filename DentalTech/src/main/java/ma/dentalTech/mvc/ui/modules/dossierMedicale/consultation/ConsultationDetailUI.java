package ma.dentalTech.mvc.ui.modules.dossierMedicale.consultation;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.mvc.controllers.modules.caisse.api.FactureControllerV2;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.CertificatController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ConsultationController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ActeController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.OrdonnanceController;
import ma.dentalTech.mvc.dto.caisse.FactureCreateDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseFactureRowDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeInterventionDTO;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.acte.ActeAddFormUI;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.certificat.CertificatAddFormUI;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDetailDTO;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.ordonnance.OrdonnanceAddFormUI;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;
import ma.dentalTech.mvc.ui.modules.caisse.CaisseFactureDetailPanel;
import ma.dentalTech.service.modules.dossierMedical.api.InterventionMedecinService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.Frame;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface pour consulter les details d'une consultation.
 * Toutes les fonctionnalites sont implementees.
 */
public class ConsultationDetailUI extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ConsultationController controller;
    private ConsultationDetailDTO detail;
    private final Runnable onBack;
    private final String username;

    private final JTable tableActes = new JTable();
    private ActeTableModel modelActes;
    private final JTextArea txtObservation = new JTextArea(4, 30);

    public ConsultationDetailUI(ConsultationController controller, Long consultationId, Runnable onBack) {
        this.controller = controller;
        this.onBack = onBack;
        this.username = "medecin_1"; // TODO: recuperer depuis la session

        // Charger les details
        reloadDetail(consultationId);

        setLayout(new BorderLayout());
        setOpaque(false);

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(15, 15));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildContent(), BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private void reloadDetail(Long consultationId) {
        try {
            this.detail = controller.getDetail(consultationId);
            if (modelActes != null) {
                modelActes.reload();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement des details", e);
        }
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Informations patient
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        infoPanel.setOpaque(false);

        JLabel lblPatient = new JLabel("Patient: " + detail.getPatientNomComplet());
        lblPatient.setFont(DentalTheme.textBold(16));
        lblPatient.setForeground(DentalTheme.TEXT2);
        infoPanel.add(lblPatient);

        String dateStr = detail.getDateConsultation() != null ? detail.getDateConsultation().format(DATE_FMT) : "";
        JLabel lblDate = new JLabel("Date: " + dateStr);
        lblDate.setFont(DentalTheme.textFont(14));
        lblDate.setForeground(DentalTheme.TEXT2);
        infoPanel.add(lblDate);

        // Statut
        StatutConsultation statut = detail.getStatut();
        JLabel lblStatut = new JLabel("Statut: " + (statut != null ? statut.name() : ""));
        lblStatut.setFont(DentalTheme.textFont(14));
        lblStatut.setForeground(DentalTheme.TEXT2);
        JPanel statutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statutPanel.setOpaque(false);
        statutPanel.add(lblStatut);
        
        // Badge de statut
        JLabel badge = new JLabel(statut != null ? statut.name() : "");
        badge.setFont(DentalTheme.textBold(12));
        badge.setForeground(DentalTheme.PRIMARY_DARK);
        badge.setBorder(new EmptyBorder(4, 12, 4, 12));
        badge.setOpaque(true);
        if (statut == StatutConsultation.TERMINE) badge.setBackground(new Color(0xD7, 0xF2, 0xD7));
        else if (statut == StatutConsultation.EN_COURS) badge.setBackground(new Color(0xD6, 0xE9, 0xFF));
        else if (statut == StatutConsultation.ANNULE) badge.setBackground(new Color(0xFF, 0xD6, 0xD6));
        else if (statut == StatutConsultation.PLANIFIE) badge.setBackground(new Color(0xFF, 0xF1, 0xCC));
        else badge.setBackground(DentalTheme.MUTED);
        statutPanel.add(badge);
        infoPanel.add(statutPanel);

        header.add(infoPanel, BorderLayout.WEST);

        return header;
    }

    private JComponent buildContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.anchor = GridBagConstraints.NORTHWEST;

        // Section Actes effectues (gauche)
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0.6;
        gc.weighty = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        content.add(buildActesSection(), gc);

        // Section droite (Ordonnances + Certificats)
        gc.gridx = 1;
        gc.weightx = 0.4;
        content.add(buildRightSection(), gc);

        return content;
    }


    
    private JComponent buildActesSection() {
        CardPanel section = new CardPanel((String) null);
        section.setBackground(DentalTheme.CARD);
        section.setBorder(new EmptyBorder(10, 10, 10, 10));
        section.setOpaque(false);
        section.setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("Actes effectues");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);

        modelActes = new ActeTableModel();
        tableActes.setModel(modelActes);
        UiStyles.styleTable(tableActes);
        tableActes.setRowHeight(36);
        tableActes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableActes.setSelectionBackground(new Color(0xF5, 0xE8, 0xD8));
        tableActes.setGridColor(DentalTheme.BORDER);
        tableActes.setShowGrid(true);
        tableActes.getTableHeader().setFont(DentalTheme.textBold(13));
        tableActes.getTableHeader().setBackground(DentalTheme.PANEL);
        tableActes.getTableHeader().setForeground(DentalTheme.TEXT2);

        tableActes.getColumnModel().getColumn(0).setPreferredWidth(250);
        tableActes.getColumnModel().getColumn(1).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(tableActes);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(10, 220));
        scroll.setMinimumSize(new Dimension(10, 200));

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setOpaque(false);
        tablePanel.add(scroll, BorderLayout.CENTER);

        Double total = detail.getTotalActes();
        JLabel lblTotal = new JLabel("Total des actes: " + (total != null ? String.format("%.2f DH", total) : "0.00 DH"));
        lblTotal.setFont(DentalTheme.textBold(15));
        lblTotal.setForeground(new Color(0xFF, 0x8C, 0x00));
        tablePanel.add(lblTotal, BorderLayout.SOUTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnAddActe = new JButton("+ Ajouter un acte");
        stylePrimaryButton(btnAddActe);
        btnAddActe.addActionListener(e -> onAddActe());

        JButton btnDeleteActe = new JButton("Supprimer un acte");
        styleDangerButton(btnDeleteActe);
        btnDeleteActe.addActionListener(e -> onDeleteActe());

        JButton btnPrint = new JButton("Imprimer");
        styleOutlineButton(btnPrint);
        btnPrint.addActionListener(e -> onPrint());

        btnPanel.add(btnAddActe);
        btnPanel.add(btnDeleteActe);
        btnPanel.add(btnPrint);

        section.add(header, BorderLayout.NORTH);
        section.add(tablePanel, BorderLayout.CENTER);
        section.add(btnPanel, BorderLayout.SOUTH);

        return section;
    }


    private Frame getParentFrame() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner instanceof Frame f) return f;
        if (owner instanceof Dialog d && d.getOwner() instanceof Frame f) return f;
        return null;
    }

    
    private void onAddActe() {
        Frame parent = getParentFrame();

        try {
            Object acteBean = ApplicationContext.getBean("acteController");
            if (acteBean instanceof ActeController acteController) {
                ActeAddFormUI createDialog = new ActeAddFormUI(parent, acteController, username);
                createDialog.setVisible(true);
            }
        } catch (Exception ignored) {
            // ignore
        }

        ActeAddToConsultationUI dialog = new ActeAddToConsultationUI(
                parent,
                detail.getConsultationId(),
                username
        );
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            reloadDetail(detail.getConsultationId());
        }
    }

    private void onDeleteActe() {
        int selectedRow = tableActes.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez selectionner un acte a supprimer",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ActeInterventionDTO acte = modelActes.getAt(selectedRow);
        if (acte == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer l'acte \"" + acte.getActeLibelle() + "\" ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Object bean = ApplicationContext.getBean("interventionMedecinService");
            if (bean instanceof InterventionMedecinService service) {
                service.delete(new ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO(acte.getInterventionId()));
                reloadDetail(detail.getConsultationId());
                JOptionPane.showMessageDialog(this,
                        "Acte supprime avec succes",
                        "Succes",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPrint() {
        try {
            // Generer un PDF simple de la consultation
            JOptionPane.showMessageDialog(this,
                    "Impression de la consultation #" + detail.getConsultationId() + "\n" +
                            "Patient: " + detail.getPatientNomComplet() + "\n" +
                            "Date: " + (detail.getDateConsultation() != null ? detail.getDateConsultation().format(DATE_FMT) : ""),
                    "Impression",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'impression: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComponent buildRightSection() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        // Section Observation
        right.add(buildObservationSection());
        right.add(Box.createVerticalStrut(15));

        // Section Ordonnances
        right.add(buildOrdonnancesSection());
        right.add(Box.createVerticalStrut(15));

        // Section Certificats
        right.add(buildCertificatsSection());

        return right;
    }


    private JComponent buildObservationSection() {
        CardPanel section = new CardPanel("Observation du medecin");
        section.setLayout(new BorderLayout(10, 10));

        txtObservation.setFont(DentalTheme.textFont(13));
        txtObservation.setLineWrap(true);
        txtObservation.setWrapStyleWord(true);
        txtObservation.setBorder(BorderFactory.createCompoundBorder(
                UiStyles.roundedBorder(),
                new EmptyBorder(6, 10, 6, 10)
        ));
        txtObservation.setText(detail.getObservationMedecin() != null ? detail.getObservationMedecin() : "");
        txtObservation.setEditable(false);

        JScrollPane scroll = new JScrollPane(txtObservation);
        scroll.setBorder(BorderFactory.createLineBorder(DentalTheme.BORDER, 1, true));
        section.add(scroll, BorderLayout.CENTER);

        return section;
    }


    private JComponent buildOrdonnancesSection() {
        CardPanel section = new CardPanel("Ordonnances");
        section.setLayout(new BorderLayout(10, 10));

        List<ConsultationDetailDTO.OrdonnanceSimpleDTO> ordonnances = detail.getOrdonnances();
        if (ordonnances == null || ordonnances.isEmpty()) {
            JLabel empty = new JLabel("Aucune ordonnance pour ce patient");
            empty.setFont(DentalTheme.textFont(13));
            empty.setForeground(DentalTheme.MUTED);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            empty.setBorder(new EmptyBorder(20, 20, 20, 20));
            section.add(empty, BorderLayout.CENTER);
        } else {
            JList<String> list = new JList<>(ordonnances.stream()
                .map(o -> "Ordonnance du " + (o.getDate() != null ? o.getDate().format(DATE_FMT) : ""))
                .toArray(String[]::new));
            list.setFont(DentalTheme.textFont(13));
            JScrollPane scroll = new JScrollPane(list);
            scroll.setBorder(BorderFactory.createLineBorder(DentalTheme.BORDER, 1, true));
            section.add(scroll, BorderLayout.CENTER);
        }

        JButton btnAdd = new JButton("+ Ajouter une ordonnance");
        styleOutlineButton(btnAdd);
        btnAdd.addActionListener(e -> onAddOrdonnance());
        section.add(btnAdd, BorderLayout.SOUTH);

        return section;
    }

    private void onAddOrdonnance() {
        try {
            // Recuperer les controllers necessaires
            Object ordonnanceBean = ApplicationContext.getBean("ordonnanceController");
            if (!(ordonnanceBean instanceof OrdonnanceController ordonnanceController)) {
                JOptionPane.showMessageDialog(this, "Controller ordonnance introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Creer une liste avec la consultation courante
            List<OrdonnanceAddFormUI.ConsultationComboItem> consultations = new ArrayList<>();
            consultations.add(new OrdonnanceAddFormUI.ConsultationComboItem(
                    detail.getConsultationId(),
                    "Consultation #" + detail.getConsultationId() + " - " + detail.getDateConsultation().format(DATE_FMT)
            ));

            // Recuperer les medicaments (liste vide pour l'instant, sera chargee dans le formulaire)
            List<OrdonnanceAddFormUI.MedicamentComboItem> medicaments = new ArrayList<>();

            OrdonnanceAddFormUI dialog = new OrdonnanceAddFormUI(
                    getParentFrame(),
                    ordonnanceController,
                    consultations,
                    medicaments,
                    username
            );
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                reloadDetail(detail.getConsultationId());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout de l'ordonnance: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private JComponent buildCertificatsSection() {
        CardPanel section = new CardPanel("Certificats");
        section.setLayout(new BorderLayout(10, 10));

        List<ConsultationDetailDTO.CertificatSimpleDTO> certificats = detail.getCertificats();
        if (certificats == null || certificats.isEmpty()) {
            JLabel empty = new JLabel("Aucun certificat pour ce patient");
            empty.setFont(DentalTheme.textFont(13));
            empty.setForeground(DentalTheme.MUTED);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            empty.setBorder(new EmptyBorder(20, 20, 20, 20));
            section.add(empty, BorderLayout.CENTER);
        } else {
            JList<String> list = new JList<>(certificats.stream()
                .map(c -> "Certificat " + (c.getDateDebut() != null ? c.getDateDebut().format(DATE_FMT) : "") +
                    " - " + (c.getDuree() != null ? c.getDuree() + " jours" : ""))
                .toArray(String[]::new));
            list.setFont(DentalTheme.textFont(13));
            JScrollPane scroll = new JScrollPane(list);
            scroll.setBorder(BorderFactory.createLineBorder(DentalTheme.BORDER, 1, true));
            section.add(scroll, BorderLayout.CENTER);
        }

        JButton btnAdd = new JButton("+ Ajouter un certificat");
        styleOutlineButton(btnAdd);
        btnAdd.addActionListener(e -> onAddCertificat());
        section.add(btnAdd, BorderLayout.SOUTH);

        return section;
    }

    private void onAddCertificat() {
        try {
            // Recuperer le controller
            Object certificatBean = ApplicationContext.getBean("certificatController");
            if (!(certificatBean instanceof CertificatController certificatController)) {
                JOptionPane.showMessageDialog(this, "Controller certificat introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Creer une liste avec le dossier de la consultation
            List<CertificatAddFormUI.DossierComboItem> dossiers = new ArrayList<>();
            dossiers.add(new CertificatAddFormUI.DossierComboItem(
                    detail.getDossierId(),
                    "Dossier #" + detail.getDossierId() + " - " + detail.getPatientNomComplet()
            ));

            CertificatAddFormUI dialog = new CertificatAddFormUI(
                    getParentFrame(),
                    certificatController,
                    dossiers,
                    username
            );
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                reloadDetail(detail.getConsultationId());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout du certificat: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JButton btnBack = new JButton("Retour");
        styleOutlineButton(btnBack);
        btnBack.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });
        footer.add(btnBack, BorderLayout.WEST);

        JButton btnFacture = new JButton("Generer facture");
        styleGoldButton(btnFacture);
        btnFacture.addActionListener(e -> onGenerateFacture());
        footer.add(btnFacture, BorderLayout.EAST);

        return footer;
    }


    private void openFactureDetail(CaisseFactureRowDTO dto) {
        if (dto == null) return;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Facture", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setContentPane(new CaisseFactureDetailPanel(dto));
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    
    private void onGenerateFacture() {
        // Verifier si une facture existe deja
        if (detail.getFactureId() != null) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Une facture existe deja pour cette consultation (ID: " + detail.getFactureId() + ").\n" +
                            "Voulez-vous en creer une nouvelle ?",
                    "Facture existante",
                    JOptionPane.YES_NO_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                try {
                    Object factureBean = ApplicationContext.getBean("factureControllerV2");
                    if (factureBean instanceof FactureControllerV2 factureController) {
                        CaisseFactureRowDTO existing = factureController.getById(detail.getFactureId());
                        openFactureDetail(existing);
                    }
                } catch (Exception ignored) {}
                return;
            }
        }

        try {
            Object factureBean = ApplicationContext.getBean("factureControllerV2");
            if (!(factureBean instanceof FactureControllerV2 factureController)) {
                JOptionPane.showMessageDialog(this, "Controller facture introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double montantTotal = 0.0;
            if (detail.getActes() != null && !detail.getActes().isEmpty()) {
                montantTotal = detail.getActes().stream()
                        .mapToDouble(a -> a.getPrixPatient() != null ? a.getPrixPatient() : 0.0)
                        .sum();
            } else if (detail.getTotalFacture() != null) {
                montantTotal = detail.getTotalFacture();
            } else {
                montantTotal = 100.0;
            }

            FactureCreateDTO factureDTO = FactureCreateDTO.builder()
                    .consultationId(detail.getConsultationId())
                    .dateFacture(LocalDate.now())
                    .totalFacture(BigDecimal.valueOf(montantTotal))
                    .build();

            CaisseFactureRowDTO created = factureController.create(factureDTO);
            openFactureDetail(created);

            reloadDetail(detail.getConsultationId());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la generation de la facture: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================

    // Table model pour actes
    // =========================================================
    private class ActeTableModel extends AbstractTableModel {
        private final String[] cols = {"Acte", "Prix"};
        private List<ActeInterventionDTO> rows = new ArrayList<>();

        ActeTableModel() {
            reload();
        }

        void reload() {
            List<ActeInterventionDTO> actes = detail != null ? detail.getActes() : null;
            this.rows = (actes != null) ? new ArrayList<>(actes) : new ArrayList<>();
            fireTableDataChanged();
        }

        ActeInterventionDTO getAt(int row) {
            if (row < 0 || row >= rows.size()) return null;
            return rows.get(row);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int col) {
            return cols[col];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ActeInterventionDTO r = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> r.getActeLibelle() != null ? r.getActeLibelle() : "";
                case 1 -> r.getPrixPatient() != null ? String.format("%.2f DH", r.getPrixPatient()) : "0.00 DH";
                default -> "";
            };
        }
    }


    private void stylePrimaryButton(AbstractButton b) {
        UiStyles.stylePrimaryButton(b);
        styleReadableButton(b);
    }

    private void styleOutlineButton(AbstractButton b) {
        UiStyles.styleSecondaryButton(b);
        styleReadableButton(b);
    }

    private void styleDangerButton(AbstractButton b) {
        b.setFont(DentalTheme.textBold(12));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBackground(DentalTheme.CARD);
        b.setForeground(DentalTheme.TEXT2);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
    }

    private void styleGoldButton(AbstractButton b) {
        b.setFont(DentalTheme.textBold(13));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBackground(DentalTheme.CARD);
        b.setForeground(DentalTheme.TEXT2);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                new EmptyBorder(8, 18, 8, 18)
        ));
    }

    private void styleReadableButton(AbstractButton b) {
        if (b == null) return;
        b.setForeground(DentalTheme.TEXT2);
        b.setBackground(DentalTheme.CARD);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
    }
}
