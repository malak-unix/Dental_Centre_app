package ma.dentalTech.mvc.ui.modules.dossierMedicale.certificat;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.CertificatController;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDetailDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Interface pour consulter les détails d'un certificat.
 * Selon la maquette fournie.
 */
public class CertificatDetailUI extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);

    private final CertificatDetailDTO detail;

    public CertificatDetailUI(CertificatController controller, Long certificatId, Runnable onClose) {

        // Charger les détails
        try {
            this.detail = controller.getDetail(certificatId);
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

        JLabel title = new JLabel("Consultation du certificat");
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

        // Date début
        String dateDebut = detail.getDateDebut() != null ? 
            detail.getDateDebut().format(DATE_FMT) : "Non spécifié";
        CardPanel dateDebutCard = createInfoCard("Date début:", dateDebut);
        infoCards.add(dateDebutCard);

        // Durée
        String duree = detail.getDuree() != null ? detail.getDuree() + " jours" : "Non spécifié";
        CardPanel dureeCard = createInfoCard("Durée:", duree);
        infoCards.add(dureeCard);

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

        JLabel val = new JLabel(value);
        val.setFont(DentalTheme.textBold(14));
        val.setForeground(DentalTheme.TEXT2);
        card.add(val, BorderLayout.CENTER);

        return card;
    }

    private JComponent buildContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Section Note du médecin
        JPanel noteSection = new JPanel(new BorderLayout(10, 10));
        noteSection.setOpaque(false);

        JLabel noteTitle = new JLabel("Note du médecin:");
        noteTitle.setFont(DentalTheme.textBold(16));
        noteTitle.setForeground(DentalTheme.TEXT2);
        noteSection.add(noteTitle, BorderLayout.NORTH);

        JTextArea noteArea = new JTextArea(detail.getNoteMedecin() != null ? detail.getNoteMedecin() : "");
        noteArea.setFont(DentalTheme.textFont(14));
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setEditable(false);
        noteArea.setOpaque(false);
        noteArea.setBorder(new EmptyBorder(10, 0, 10, 0));
        noteSection.add(noteArea, BorderLayout.CENTER);

        content.add(noteSection);
        content.add(Box.createVerticalStrut(20));

        // Section Information du certificat
        JPanel infoSection = new JPanel(new BorderLayout(10, 10));
        infoSection.setOpaque(false);

        JLabel infoTitle = new JLabel("Information du certificat:");
        infoTitle.setFont(DentalTheme.textBold(16));
        infoTitle.setForeground(DentalTheme.TEXT2);
        infoSection.add(infoTitle, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Médecin
        JLabel medecinLabel = new JLabel("Médecin: " + detail.getMedecinNom());
        medecinLabel.setFont(DentalTheme.textFont(14));
        medecinLabel.setForeground(DentalTheme.TEXT2);
        infoPanel.add(medecinLabel);
        infoPanel.add(Box.createVerticalStrut(8));

        // Date fin
        String dateFin = detail.getDateFin() != null ? 
            detail.getDateFin().format(DATE_FMT) : "Non spécifié";
        JLabel dateFinLabel = new JLabel("Date fin: " + dateFin);
        dateFinLabel.setFont(DentalTheme.textFont(14));
        dateFinLabel.setForeground(DentalTheme.TEXT2);
        infoPanel.add(dateFinLabel);
        infoPanel.add(Box.createVerticalStrut(8));

        // Note médicale
        if (detail.getNoteMedecin() != null && !detail.getNoteMedecin().isEmpty()) {
            JLabel noteMedLabel = new JLabel("Note médicale : " + detail.getNoteMedecin());
            noteMedLabel.setFont(DentalTheme.textFont(14));
            noteMedLabel.setForeground(DentalTheme.TEXT2);
            noteMedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(noteMedLabel);
        }

        infoSection.add(infoPanel, BorderLayout.CENTER);

        content.add(infoSection);

        return content;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);

        JButton btnPrint = new JButton("🖨️ Imprimer le certificat");
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
                // Générer un PDF simple du certificat
                JOptionPane.showMessageDialog(this,
                        "Impression du certificat #" + detail.getCertificatId() + "\n" +
                        "Patient: " + detail.getPatientNomComplet() + "\n" +
                        "Du " + (detail.getDateDebut() != null ? detail.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "") +
                        " au " + (detail.getDateFin() != null ? detail.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""),
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
}
