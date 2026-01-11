package ma.dentalTech.mvc.ui.modules.dashboard.medecin;

import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.PatientCurrentDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class MedecinDashboardPanel extends JPanel {

    private DefaultTableModel rdvModel;
    private JLabel footer;
    private JTextArea currentInfo;

    public MedecinDashboardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(18, 18));

        JLabel title = new JLabel("Dashboard");
        title.setFont(DentalTheme.H1);
        title.setForeground(DentalTheme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel main = new JPanel(new GridLayout(1, 2, 18, 18));
        main.setOpaque(false);
        add(main, BorderLayout.CENTER);

        main.add(rdvCard());
        main.add(currentPatientCard());

        setData(null);
    }

    public void setData(MedecinDashboardResponseDTO dto) {
        rdvModel.setRowCount(0);

        List<RdvDto> rdv = dto != null ? dto.getRdvDuJour() : null;
        if (rdv != null && !rdv.isEmpty()) {
            for (RdvDto r : rdv) {
                rdvModel.addRow(new Object[]{
                        r.getHeure() != null ? r.getHeure().toString() : "",
                        r.getPatientNom() != null ? r.getPatientNom() : "",
                        r.getMotif() != null ? r.getMotif() : "",
                        r.getStatut() != null ? r.getStatut().name() : ""
                });
            }
        } else {
            rdvModel.addRow(new Object[]{"", "Aucun RDV", "", ""});
        }

        int nbRdv = dto != null && dto.getNbRdvDuJour() != null ? dto.getNbRdvDuJour() : 0;
        int nbActes = dto != null && dto.getNbActesRealises() != null ? dto.getNbActesRealises() : 0;
        BigDecimal rec = dto != null ? dto.getRecetteDuJour() : null;
        footer.setText("Aujourd’hui : " + nbRdv + " RDV   |   Actes réalisés : " + nbActes + "   |   Recettes : " + formatDh(rec));

        PatientCurrentDTO p = dto != null ? dto.getPatientEnCours() : null;
        if (p == null) {
            currentInfo.setText("Aucun client en cours.");
        } else {
            currentInfo.setText(
                    (p.getNomComplet() != null ? p.getNomComplet() : "Patient") + "\n\n" +
                            "Actions :\n• Dossier\n• Consultation\n• Radio\n• Ordonnance\n"
            );
        }
    }

    private CardPanel rdvCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Rendez-vous du Jour");
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        String[] cols = {"Heure", "Patient", "Motif", "Statut"};
        rdvModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(rdvModel);
        table.setRowHeight(28);

        c.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel footerWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        footerWrap.setOpaque(false);
        footer = new JLabel("");
        footerWrap.add(footer);
        c.add(footerWrap, BorderLayout.SOUTH);

        return c;
    }

    private CardPanel currentPatientCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Client En cours");
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        currentInfo = new JTextArea();
        currentInfo.setOpaque(false);
        currentInfo.setEditable(false);
        currentInfo.setFont(DentalTheme.BASE);
        c.add(currentInfo, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 2, 10, 10));
        actions.setOpaque(false);
        actions.add(new JButton("+ Dossier"));
        actions.add(new JButton("+ Consultation"));
        actions.add(new JButton("+ Radio"));
        actions.add(new JButton("+ Ordonnance"));
        c.add(actions, BorderLayout.SOUTH);

        return c;
    }

    private String formatDh(BigDecimal v) {
        if (v == null) return "0 DH";
        return v.stripTrailingZeros().toPlainString() + " DH";
    }
}
