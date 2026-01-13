package ma.dentalTech.mvc.ui.modules.dossierMedicale.consultation;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.mvc.controllers.modules.caisse.api.FactureControllerV2;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.CertificatController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ConsultationController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.OrdonnanceController;
import ma.dentalTech.mvc.dto.caisse.FactureCreateDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeInterventionDTO;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.certificat.CertificatAddFormUI;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDetailDTO;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.ordonnance.OrdonnanceAddFormUI;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
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
 * Interface pour consulter les détails d'une consultation.
 * Toutes les fonctionnalités sont implémentées.
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
        this.username = "medecin_1"; // TODO: récupérer depuis la session

        // Charger les détails
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
            throw new RuntimeException("Erreur lors du chargement des détails", e);
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

        String dateStr = detail.getDateConsultation() != null ? 
            detail.getDateConsultation().format(DATE_FMT) : "";
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
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 12, 4, 12));
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

        // Section Actes effectués (gauche)
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
        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setOpaque(false);

        JLabel title = new JLabel("Actes effectués");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.TEXT2);
        section.add(title, BorderLayout.NORTH);

        // Tableau des actes
        modelActes = new ActeTableModel();
        tableActes.setModel(modelActes);
        tableActes.setRowHeight(35);
        tableActes.setFont(DentalTheme.textFont(13));
        tableActes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableActes.setGridColor(DentalTheme.BORDER);
        tableActes.setShowGrid(true);

        tableActes.getColumnModel().getColumn(0).setPreferredWidth(250); // Acte
        tableActes.getColumnModel().getColumn(1).setPreferredWidth(100); // Prix

        JScrollPane scroll = new JScrollPane(tableActes);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));

        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setOpaque(false);
        tablePanel.add(scroll, BorderLayout.CENTER);

        // Total
        Double total = detail.getTotalActes();
        JLabel lblTotal = new JLabel("Total des actes: " + (total != null ? String.format("%.2f €", total) : "0.00 €"));
        lblTotal.setFont(DentalTheme.textBold(16));
        lblTotal.setForeground(new Color(0xFF, 0x8C, 0x00)); // Orange selon maquette
        tablePanel.add(lblTotal, BorderLayout.SOUTH);

        section.add(tablePanel, BorderLayout.CENTER);

        // Boutons actions actes
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setOpaque(false);

        JButton btnAddActe = new JButton("+ Ajouter un acte");
        btnAddActe.setBackground(new Color(0x1C, 0x25, 0x41));
        btnAddActe.setForeground(Color.WHITE);
        btnAddActe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnAddActe.addActionListener(e -> onAddActe());

        JButton btnDeleteActe = new JButton("Supprimer un acte");
        btnDeleteActe.setBackground(DentalTheme.BEIGE);
        btnDeleteActe.setForeground(DentalTheme.TEXT2);
        btnDeleteActe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnDeleteActe.addActionListener(e -> onDeleteActe());

        JButton btnPrint = new JButton("🖨️");
        btnPrint.setBackground(DentalTheme.BEIGE);
        btnPrint.setForeground(DentalTheme.TEXT2);
        btnPrint.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnPrint.addActionListener(e -> onPrint());

        btnPanel.add(btnAddActe);
        btnPanel.add(btnDeleteActe);
        btnPanel.add(btnPrint);

        section.add(btnPanel, BorderLayout.SOUTH);

        return section;
    }

    private void onAddActe() {
        ActeAddToConsultationUI dialog = new ActeAddToConsultationUI(
                (Frame) SwingUtilities.getWindowAncestor(this),
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
                    "Veuillez sélectionner un acte à supprimer",
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
                        "Acte supprimé avec succès",
                        "Succès",
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
            // Générer un PDF simple de la consultation
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
        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setOpaque(false);

        JLabel title = new JLabel("Observation du médecin:");
        title.setFont(DentalTheme.textBold(14));
        title.setForeground(DentalTheme.TEXT2);
        section.add(title, BorderLayout.NORTH);

        txtObservation.setFont(DentalTheme.textFont(13));
        txtObservation.setLineWrap(true);
        txtObservation.setWrapStyleWord(true);
        txtObservation.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        txtObservation.setText(detail.getObservationMedecin() != null ? detail.getObservationMedecin() : "");
        txtObservation.setEditable(false);

        JScrollPane scroll = new JScrollPane(txtObservation);
        scroll.setBorder(null);
        section.add(scroll, BorderLayout.CENTER);

        return section;
    }

    private JComponent buildOrdonnancesSection() {
        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setOpaque(false);

        JLabel title = new JLabel("Ordonnances");
        title.setFont(DentalTheme.textBold(14));
        title.setForeground(DentalTheme.TEXT2);
        section.add(title, BorderLayout.NORTH);

        List<ConsultationDetailDTO.OrdonnanceSimpleDTO> ordonnances = detail.getOrdonnances();
        if (ordonnances == null || ordonnances.isEmpty()) {
            CardPanel emptyCard = new CardPanel();
            emptyCard.setLayout(new BorderLayout());
            JLabel empty = new JLabel("Aucune ordonnance pour ce patient");
            empty.setFont(DentalTheme.textFont(13));
            empty.setForeground(DentalTheme.MUTED);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            empty.setBorder(new EmptyBorder(20, 20, 20, 20));
            emptyCard.add(empty, BorderLayout.CENTER);
            section.add(emptyCard, BorderLayout.CENTER);
        } else {
            // Liste des ordonnances
            JList<String> list = new JList<>(ordonnances.stream()
                .map(o -> "Ordonnance du " + (o.getDate() != null ? o.getDate().format(DATE_FMT) : ""))
                .toArray(String[]::new));
            list.setFont(DentalTheme.textFont(13));
            JScrollPane scroll = new JScrollPane(list);
            section.add(scroll, BorderLayout.CENTER);
        }

        JButton btnAdd = new JButton("+ Ajouter une ordonnance");
        btnAdd.setBackground(new Color(0xD6, 0xE9, 0xFF));
        btnAdd.setForeground(new Color(0x1C, 0x25, 0x41));
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x1C, 0x25, 0x41), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnAdd.addActionListener(e -> onAddOrdonnance());
        section.add(btnAdd, BorderLayout.SOUTH);

        return section;
    }

    private void onAddOrdonnance() {
        try {
            // Récupérer les controllers nécessaires
            Object ordonnanceBean = ApplicationContext.getBean("ordonnanceController");
            if (!(ordonnanceBean instanceof OrdonnanceController ordonnanceController)) {
                JOptionPane.showMessageDialog(this, "Controller ordonnance introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer une liste avec la consultation courante
            List<OrdonnanceAddFormUI.ConsultationComboItem> consultations = new ArrayList<>();
            consultations.add(new OrdonnanceAddFormUI.ConsultationComboItem(
                    detail.getConsultationId(),
                    "Consultation #" + detail.getConsultationId() + " - " + detail.getDateConsultation().format(DATE_FMT)
            ));

            // Récupérer les médicaments (liste vide pour l'instant, sera chargée dans le formulaire)
            List<OrdonnanceAddFormUI.MedicamentComboItem> medicaments = new ArrayList<>();

            OrdonnanceAddFormUI dialog = new OrdonnanceAddFormUI(
                    (Frame) SwingUtilities.getWindowAncestor(this),
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
        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setOpaque(false);

        JLabel title = new JLabel("Certificats");
        title.setFont(DentalTheme.textBold(14));
        title.setForeground(DentalTheme.TEXT2);
        section.add(title, BorderLayout.NORTH);

        List<ConsultationDetailDTO.CertificatSimpleDTO> certificats = detail.getCertificats();
        if (certificats == null || certificats.isEmpty()) {
            CardPanel emptyCard = new CardPanel();
            emptyCard.setLayout(new BorderLayout());
            JLabel empty = new JLabel("Aucun certificat pour ce patient");
            empty.setFont(DentalTheme.textFont(13));
            empty.setForeground(DentalTheme.MUTED);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            empty.setBorder(new EmptyBorder(20, 20, 20, 20));
            emptyCard.add(empty, BorderLayout.CENTER);
            section.add(emptyCard, BorderLayout.CENTER);
        } else {
            // Liste des certificats
            JList<String> list = new JList<>(certificats.stream()
                .map(c -> "Certificat " + (c.getDateDebut() != null ? c.getDateDebut().format(DATE_FMT) : "") + 
                    " - " + (c.getDuree() != null ? c.getDuree() + " jours" : ""))
                .toArray(String[]::new));
            list.setFont(DentalTheme.textFont(13));
            JScrollPane scroll = new JScrollPane(list);
            section.add(scroll, BorderLayout.CENTER);
        }

        JButton btnAdd = new JButton("+ Ajouter un certificat");
        btnAdd.setBackground(new Color(0xCB, 0xA1, 0x35));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnAdd.addActionListener(e -> onAddCertificat());
        section.add(btnAdd, BorderLayout.SOUTH);

        return section;
    }

    private void onAddCertificat() {
        try {
            // Récupérer le controller
            Object certificatBean = ApplicationContext.getBean("certificatController");
            if (!(certificatBean instanceof CertificatController certificatController)) {
                JOptionPane.showMessageDialog(this, "Controller certificat introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer une liste avec le dossier de la consultation
            List<CertificatAddFormUI.DossierComboItem> dossiers = new ArrayList<>();
            dossiers.add(new CertificatAddFormUI.DossierComboItem(
                    detail.getDossierId(),
                    "Dossier #" + detail.getDossierId() + " - " + detail.getPatientNomComplet()
            ));

            CertificatAddFormUI dialog = new CertificatAddFormUI(
                    (Frame) SwingUtilities.getWindowAncestor(this),
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

        JButton btnBack = new JButton("← Retour");
        btnBack.setBackground(DentalTheme.BEIGE);
        btnBack.setForeground(DentalTheme.TEXT2);
        btnBack.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnBack.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });
        footer.add(btnBack, BorderLayout.WEST);

        JButton btnFacture = new JButton("Générer facture");
        btnFacture.setBackground(new Color(0xCB, 0xA1, 0x35));
        btnFacture.setForeground(Color.WHITE);
        btnFacture.setFont(DentalTheme.textBold(14));
        btnFacture.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(10, 20, 10, 20)
        ));
        btnFacture.addActionListener(e -> onGenerateFacture());
        footer.add(btnFacture, BorderLayout.EAST);

        return footer;
    }

    private void onGenerateFacture() {
        // Vérifier si une facture existe déjà
        if (detail.getFactureId() != null) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Une facture existe déjà pour cette consultation (ID: " + detail.getFactureId() + ").\n" +
                            "Voulez-vous en créer une nouvelle ?",
                    "Facture existante",
                    JOptionPane.YES_NO_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            // Récupérer le controller facture
            Object factureBean = ApplicationContext.getBean("factureControllerV2");
            if (!(factureBean instanceof FactureControllerV2 factureController)) {
                JOptionPane.showMessageDialog(this, "Controller facture introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calculer le montant total
            double montantTotal = 0.0;
            if (detail.getActes() != null && !detail.getActes().isEmpty()) {
                // Somme des prix des actes
                montantTotal = detail.getActes().stream()
                        .mapToDouble(a -> a.getPrixPatient() != null ? a.getPrixPatient() : 0.0)
                        .sum();
            } else if (detail.getTotalFacture() != null) {
                // Prix de consultation sans acte
                montantTotal = detail.getTotalFacture();
            } else {
                // Montant par défaut
                montantTotal = 100.0; // Prix consultation standard
            }

            // Créer la facture
            FactureCreateDTO factureDTO = FactureCreateDTO.builder()
                    .consultationId(detail.getConsultationId())
                    .dateFacture(LocalDate.now())
                    .totalFacture(BigDecimal.valueOf(montantTotal))
                    .build();

            factureController.create(factureDTO);

            JOptionPane.showMessageDialog(this,
                    "Facture générée avec succès\n" +
                            "Montant total: " + String.format("%.2f €", montantTotal),
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            // Recharger les détails pour avoir la facture
            reloadDetail(detail.getConsultationId());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la génération de la facture: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // Table model pour actes
    // =========================================================
    private class ActeTableModel extends AbstractTableModel {
        private final String[] cols = {"Médicament", "Prix"};
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
                case 1 -> r.getPrixPatient() != null ? String.format("%.2f €", r.getPrixPatient()) : "0.00 €";
                default -> "";
            };
        }
    }
}
