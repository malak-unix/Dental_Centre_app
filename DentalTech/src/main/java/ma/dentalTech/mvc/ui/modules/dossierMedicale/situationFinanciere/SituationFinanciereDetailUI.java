package ma.dentalTech.mvc.ui.modules.dossierMedicale.situationFinanciere;

import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.SituationFinanciereController;
import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.FactureDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.SituationFinanciereDetailDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Interface pour consulter les détails d'une situation financière.
 * Affiche les factures avec leurs consultations associées.
 */
public class SituationFinanciereDetailUI extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final SituationFinanciereDetailDTO detail;
    private final Runnable onClose;

    private final JTable tableFactures = new JTable();
    private final FacturesTableModel modelFactures = new FacturesTableModel();

    public SituationFinanciereDetailUI(SituationFinanciereController controller, Long situationFinanciereId, Runnable onClose) {
        this.onClose = onClose;

        // Charger les détails
        try {
            this.detail = controller.getDetail(situationFinanciereId);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement des détails", e);
        }

        setLayout(new BorderLayout());
        setOpaque(false);

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(20, 20));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildContent(), BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Détails de la situation financière");
        title.setFont(DentalTheme.titleFont(24));
        title.setForeground(new Color(0x1C, 0x25, 0x41));
        header.add(title);

        header.add(Box.createVerticalStrut(20));

        // Cartes d'information
        JPanel infoCards = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        infoCards.setOpaque(false);

        // Patient
        CardPanel patientCard = createInfoCard("Patient:", detail.patientNomComplet());
        infoCards.add(patientCard);

        // Solde
        String soldeStr = String.format("%.2f €", detail.solde() != null ? detail.solde() : 0.0);
        CardPanel soldeCard = createInfoCard("Solde:", soldeStr);
        infoCards.add(soldeCard);

        // Total des actes
        String totalActesStr = String.format("%.2f €", detail.totalDesActes() != null ? detail.totalDesActes() : 0.0);
        CardPanel totalActesCard = createInfoCard("Total des actes:", totalActesStr);
        infoCards.add(totalActesCard);

        // Total payé
        String totalPayeStr = String.format("%.2f €", detail.totalPaye() != null ? detail.totalPaye() : 0.0);
        CardPanel totalPayeCard = createInfoCard("Total payé:", totalPayeStr);
        infoCards.add(totalPayeCard);

        header.add(infoCards);

        return header;
    }

    private CardPanel createInfoCard(String label, String value) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        card.setPreferredSize(new Dimension(180, 80));

        JLabel lbl = new JLabel(label);
        lbl.setFont(DentalTheme.textFont(12));
        lbl.setForeground(DentalTheme.MUTED);
        card.add(lbl, BorderLayout.NORTH);

        JLabel val = new JLabel(value);
        val.setFont(DentalTheme.textBold(14));
        val.setForeground(DentalTheme.TEXT2);
        card.add(val, BorderLayout.CENTER);

        return card;
    }

    private JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);

        JLabel title = new JLabel("Factures");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(new Color(0x1C, 0x25, 0x41));
        content.add(title, BorderLayout.NORTH);

        // Tableau des factures
        tableFactures.setModel(modelFactures);
        tableFactures.setRowHeight(40);
        tableFactures.setFont(DentalTheme.textFont(13));
        tableFactures.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableFactures.setGridColor(DentalTheme.BORDER);
        tableFactures.setShowGrid(true);

        tableFactures.getColumnModel().getColumn(0).setPreferredWidth(120); // Numéro
        tableFactures.getColumnModel().getColumn(1).setPreferredWidth(120); // Date
        tableFactures.getColumnModel().getColumn(2).setPreferredWidth(120); // Total
        tableFactures.getColumnModel().getColumn(3).setPreferredWidth(120); // Payé
        tableFactures.getColumnModel().getColumn(4).setPreferredWidth(120); // Reste
        tableFactures.getColumnModel().getColumn(5).setPreferredWidth(100); // Statut
        tableFactures.getColumnModel().getColumn(6).setPreferredWidth(300); // Consultation

        // Renderer pour le statut
        tableFactures.getColumnModel().getColumn(5).setCellRenderer(new StatutFactureCellRenderer());

        JScrollPane scroll = new JScrollPane(tableFactures);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));

        content.add(scroll, BorderLayout.CENTER);

        return content;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);

        JButton btnClose = new JButton("Fermer");
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
    // Table model pour les factures
    // =========================================================
    private class FacturesTableModel extends AbstractTableModel {
        private final String[] cols = {"Numéro", "Date", "Total", "Payé", "Reste", "Statut", "Consultation"};

        @Override
        public int getRowCount() {
            return detail.factures() != null ? detail.factures().size() : 0;
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
            if (detail.factures() == null || rowIndex >= detail.factures().size()) return "";
            FactureDetailDTO f = detail.factures().get(rowIndex);

            return switch (columnIndex) {
                case 0 -> f.numeroFacture() != null ? f.numeroFacture() : "";
                case 1 -> f.dateFacture() != null ? f.dateFacture().format(DATE_FMT) : "";
                case 2 -> f.totalFacture() != null ? String.format("%.2f €", f.totalFacture()) : "0.00 €";
                case 3 -> f.totalPaye() != null ? String.format("%.2f €", f.totalPaye()) : "0.00 €";
                case 4 -> f.reste() != null ? String.format("%.2f €", f.reste()) : "0.00 €";
                case 5 -> f.statut() != null ? f.statut().name() : "";
                case 6 -> f.consultationLibelle() != null ? f.consultationLibelle() : "";
                default -> "";
            };
        }
    }

    // =========================================================
    // Renderer pour le statut de facture
    // =========================================================
    private class StatutFactureCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                setOpaque(true);
                if (value instanceof StatutFacture statut) {
                    switch (statut) {
                        case PAYEE -> {
                            setBackground(new Color(0xD7, 0xF2, 0xD7)); // Vert clair
                            setForeground(new Color(0x00, 0x66, 0x00));
                        }
                        case PARTIEL -> {
                            setBackground(new Color(0xFF, 0xF1, 0xCC)); // Jaune clair
                            setForeground(new Color(0x8B, 0x69, 0x1E));
                        }
                        case NON_PAYEE -> {
                            setBackground(new Color(0xFF, 0xD6, 0xD6)); // Rouge clair
                            setForeground(new Color(0x8B, 0x00, 0x00));
                        }
                    }
                } else {
                    setBackground(Color.WHITE);
                    setForeground(DentalTheme.TEXT2);
                }
            }

            return this;
        }
    }
}
