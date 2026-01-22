package ma.dentalTech.mvc.ui.modules.dossierMedicale.ordonnance;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.OrdonnanceController;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.prescription.PrescriptionDetailDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface pour consulter les détails d'une ordonnance.
 * Selon la maquette fournie.
 */
public class OrdonnanceDetailUI extends JPanel {

    private final OrdonnanceDetailDTO detail;

    private final JTable tablePrescriptions = new JTable();
    private final PrescriptionTableModel modelPrescriptions = new PrescriptionTableModel();

    public OrdonnanceDetailUI(OrdonnanceController controller, Long ordonnanceId, Runnable onClose) {
        // Charger les détails
        try {
            this.detail = controller.getDetail(ordonnanceId);
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

        JLabel title = new JLabel("Consultation de l'ordonnance");
        title.setFont(DentalTheme.titleFont(24));
        title.setForeground(new Color(0x1C, 0x25, 0x41));
        header.add(title);

        header.add(Box.createVerticalStrut(20));

        // Cartes d'information
        JPanel infoCards = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        infoCards.setOpaque(false);

        // Patient
        CardPanel patientCard = createInfoCard("Patient:", detail.getPatientNomComplet());
        infoCards.add(patientCard);

        // Consultation
        String consultationText = detail.getConsultationLibelle();
        if (detail.getDate() != null) {
            consultationText += "\n" + detail.getDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale.FRENCH));
        }
        CardPanel consultationCard = createInfoCard("Consultation:", consultationText);
        infoCards.add(consultationCard);

        // Médecin
        CardPanel medecinCard = createInfoCard("Médecin:", detail.getMedecinNom());
        infoCards.add(medecinCard);

        header.add(infoCards);

        return header;
    }

    private CardPanel createInfoCard(String label, String value) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        card.setPreferredSize(new Dimension(200, 80));

        JLabel lbl = new JLabel(label);
        lbl.setFont(DentalTheme.textFont(12));
        lbl.setForeground(DentalTheme.MUTED);
        card.add(lbl, BorderLayout.NORTH);

        JLabel val = new JLabel("<html>" + value.replace("\n", "<br>") + "</html>");
        val.setFont(DentalTheme.textBold(14));
        val.setForeground(DentalTheme.TEXT2);
        card.add(val, BorderLayout.CENTER);

        return card;
    }

    private JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setOpaque(false);

        JLabel title = new JLabel("Prescriptions:");
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(new Color(0x1C, 0x25, 0x41));
        content.add(title, BorderLayout.NORTH);

        // Tableau des prescriptions
        tablePrescriptions.setModel(modelPrescriptions);
        tablePrescriptions.setRowHeight(60);
        tablePrescriptions.setFont(DentalTheme.textFont(13));
        tablePrescriptions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePrescriptions.setGridColor(DentalTheme.BORDER);
        tablePrescriptions.setShowGrid(true);
        tablePrescriptions.setIntercellSpacing(new Dimension(10, 5));

        tablePrescriptions.getColumnModel().getColumn(0).setPreferredWidth(300); // Médicament
        tablePrescriptions.getColumnModel().getColumn(1).setPreferredWidth(400); // Instructions

        JScrollPane scroll = new JScrollPane(tablePrescriptions);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        content.add(scroll, BorderLayout.CENTER);

        return content;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);

        JButton btnPrint = new JButton("🖨️ Imprimer l'ordonnance");
        btnPrint.setFont(DentalTheme.textBold(14));
        btnPrint.setBackground(DentalTheme.CARD);
        btnPrint.setForeground(DentalTheme.TEXT2);
        btnPrint.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(10, 20, 10, 20)
        ));
        btnPrint.setFocusPainted(false);
        btnPrint.addActionListener(e -> {
            try {
                // Générer un PDF simple de l'ordonnance
                JOptionPane.showMessageDialog(this,
                        "Impression de l'ordonnance #" + detail.getOrdonnanceId() + "\n" +
                        "Patient: " + detail.getPatientNomComplet() + "\n" +
                        "Date: " + (detail.getDate() != null ? detail.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""),
                        "Impression",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'impression: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(btnPrint);

        return footer;
    }

    // =========================================================
    // Table model pour prescriptions
    // =========================================================
    private class PrescriptionTableModel extends AbstractTableModel {
        private final String[] cols = {"Médicament", "Instructions"};
        private List<PrescriptionDetailDTO> rows = new ArrayList<>();

        PrescriptionTableModel() {
            List<PrescriptionDetailDTO> prescriptions = detail.getPrescriptions();
            this.rows = (prescriptions != null) ? new ArrayList<>(prescriptions) : new ArrayList<>();
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
            PrescriptionDetailDTO r = rows.get(rowIndex);
            if (columnIndex == 0) {
                return r.getMedicamentNom() != null ? r.getMedicamentNom() : "";
            } else {
                // Format: "✓ quantite, frequence pendant duree jours"
                StringBuilder sb = new StringBuilder("✓ ");
                sb.append(r.getQuantite());
                if (r.getMedicamentForme() != null && !r.getMedicamentForme().isEmpty()) {
                    sb.append(" ").append(r.getMedicamentForme().toLowerCase());
                } else {
                    sb.append(" comprimé");
                }
                if (r.getQuantite() > 1) sb.append("s");
                
                if (r.getFrequence() != null && !r.getFrequence().isEmpty()) {
                    sb.append(", ").append(r.getFrequence());
                }
                
                if (r.getDureeEnJours() > 0) {
                    sb.append(" pendant ").append(r.getDureeEnJours()).append(" jour");
                    if (r.getDureeEnJours() > 1) sb.append("s");
                }
                
                return sb.toString();
            }
        }
    }
}
