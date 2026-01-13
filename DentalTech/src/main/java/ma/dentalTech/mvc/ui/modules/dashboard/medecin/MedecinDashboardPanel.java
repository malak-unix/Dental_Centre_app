package ma.dentalTech.mvc.ui.modules.dashboard.medecin;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.PatientCurrentDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class MedecinDashboardPanel extends JPanel {

    private final Long userId;

    private JLabel vPatients, vRdv, vActes, vRecette;
    private DefaultTableModel rdvModel;
    private JLabel footer;
    private JTextArea currentInfo;

    public MedecinDashboardPanel() {
        this(1L);
    }

    public MedecinDashboardPanel(Long userId) {
        this.userId = (userId != null) ? userId : 1L;

        setOpaque(false);
        setLayout(new BorderLayout(18, 18));

        JLabel title = new JLabel("Dashboard Médecin");
        title.setFont(DentalTheme.H1);
        title.setForeground(DentalTheme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        add(wrap, BorderLayout.CENTER);

        // KPIs
        JPanel kpis = new JPanel(new GridLayout(1, 5, 18, 18));
        kpis.setOpaque(false);

        vPatients = new JLabel("—");
        vRdv = new JLabel("—");
        vActes = new JLabel("—");
        vRecette = new JLabel("—");

        kpis.add(kpi(vPatients, "Patients du jour"));
        kpis.add(kpi(vRdv, "RDV du jour"));
        kpis.add(kpi(vActes, "Actes réalisés"));
        kpis.add(kpi(vRecette, "Recette du jour"));
        kpis.add(actionCard("Rafraîchir", this::refresh));

        wrap.add(kpis);
        wrap.add(Box.createVerticalStrut(18));

        JPanel main = new JPanel(new GridLayout(1, 2, 18, 18));
        main.setOpaque(false);
        main.add(rdvCard());
        main.add(currentPatientCard());
        wrap.add(main);

        refresh();
    }

    public final void refresh() {
        MedecinDashboardResponseDTO medDto = null;
        try {
            DashboardDTO dash = fetchDashboardDTO();
            if (dash != null) medDto = dash.getMedecin();
        } catch (Exception ignore) {}

        setData(medDto);
    }

    private DashboardDTO fetchDashboardDTO() {
        Object bean = ApplicationContext.getBean("dashboardController");
        if (bean instanceof DashboardController ctrl) {
            try {
                return ctrl.getDashboardDTO(userId);
            } catch (Exception ignore) {}
        }

        DashboardService service = ApplicationContext.getBean(DashboardService.class);
        if (service != null) {
            try {
                return service.getDashboard(userId);
            } catch (Exception ignore) {}
        }
        return null;
    }

    public void setData(MedecinDashboardResponseDTO dto) {
        int nbPatients = dto != null && dto.getNbPatientsDuJour() != null ? dto.getNbPatientsDuJour() : 0;
        int nbRdv = dto != null && dto.getNbRdvDuJour() != null ? dto.getNbRdvDuJour() : 0;
        int nbActes = dto != null && dto.getNbActesRealises() != null ? dto.getNbActesRealises() : 0;
        BigDecimal rec = dto != null ? dto.getRecetteDuJour() : null;

        vPatients.setText(String.valueOf(nbPatients));
        vRdv.setText(String.valueOf(nbRdv));
        vActes.setText(String.valueOf(nbActes));
        vRecette.setText(formatDh(rec));

        // RDV
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

        footer.setText("Aujourd’hui : " + nbRdv + " RDV   |   Actes : " + nbActes + "   |   Recettes : " + formatDh(rec));

        PatientCurrentDTO p = dto != null ? dto.getPatientEnCours() : null;
        if (p == null) {
            currentInfo.setText("Aucun client en cours.");
        } else {
            String tel = p.getTel() != null ? p.getTel() : "—";
            String statut = p.getStatutTraitement() != null ? p.getStatutTraitement() : "—";
            currentInfo.setText(
                    (p.getNomComplet() != null ? p.getNomComplet() : "Patient") + "\n" +
                            "Tel: " + tel + "\n" +
                            "Statut: " + statut + "\n\n" +
                            "Actions :\n• Dossier\n• Consultation\n• Radio\n• Ordonnance\n"
            );
        }
    }

    private CardPanel kpi(JLabel valueLabel, String label) {
        CardPanel c = new CardPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        valueLabel.setFont(DentalTheme.H2);
        valueLabel.setForeground(DentalTheme.TEXT);

        JLabel l = new JLabel(label);
        l.setFont(DentalTheme.BASE);
        l.setForeground(DentalTheme.MUTED);

        c.add(valueLabel);
        c.add(Box.createVerticalStrut(6));
        c.add(l);
        return c;
    }

    private CardPanel actionCard(String text, Runnable action) {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout());
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.addActionListener(e -> action.run());
        c.add(b, BorderLayout.CENTER);
        return c;
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
