package ma.dentalTech.mvc.ui.modules.dashboard.medecin;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.components.StatCard;
import ma.dentalTech.mvc.ui.common.components.TeethChartPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.function.Consumer;

public class MedecinDashboardPanel extends JPanel {

    private final DashboardController controller;
    private final Long userId;
    private final Consumer<String> navigate;

    private final StatCard statPatients = new StatCard("Patients du jour", "0", "👤");
    private final StatCard statRdv      = new StatCard("RDV du jour", "0", "📅");
    private final StatCard statActes    = new StatCard("Actes réalisés", "0", "✅");
    private final StatCard statRecette  = new StatCard("Recette du jour", "0 DH", "💰");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Heure", "Patient", "Motif", "Statut"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    // Client en cours (infos)
    private final JLabel lblCurrentName = new JLabel("—");
    private final JLabel lblCurrentTel = new JLabel("");
    private final JLabel lblCurrentStatus = new JLabel("");

    public MedecinDashboardPanel(DashboardController controller, Long userId, Consumer<String> navigate) {
        this.controller = (controller != null)
                ? controller
                : ApplicationContext.getBean(DashboardController.class);
        this.userId = userId;
        this.navigate = (navigate != null) ? navigate : (k -> {});

        setLayout(new BorderLayout(15, 15));
        setBackground(DentalTheme.BG);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildTopStats(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        reload();
    }

    private JPanel buildTopStats() {
        JPanel p = new JPanel(new GridLayout(1, 4, 15, 15));
        p.setOpaque(false);
        p.add(statPatients);
        p.add(statRdv);
        p.add(statActes);
        p.add(statRecette);
        return p;
    }

    private JComponent buildBody() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setOpaque(false);

        JTable table = new JTable(model);
        table.setRowHeight(34);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Rendez-vous du Jour"));
        root.add(sp, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(360, 10));
        right.setBorder(BorderFactory.createTitledBorder("Client en cours"));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        lblCurrentName.setFont(DentalTheme.textBold(14));
        lblCurrentName.setForeground(DentalTheme.TEXT2);
        lblCurrentTel.setFont(DentalTheme.textFont(12));
        lblCurrentTel.setForeground(DentalTheme.TEXT2);
        lblCurrentStatus.setFont(DentalTheme.textFont(12));
        lblCurrentStatus.setForeground(DentalTheme.TEXT2);

        info.add(lblCurrentName);
        info.add(Box.createVerticalStrut(2));
        info.add(lblCurrentTel);
        info.add(Box.createVerticalStrut(2));
        info.add(lblCurrentStatus);
        info.setBorder(BorderFactory.createEmptyBorder(6, 6, 0, 6));

        right.add(info, BorderLayout.NORTH);
        right.add(new TeethChartPanel(), BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(3, 1, 8, 8));
        btns.setOpaque(false);

        DentalButton bDossier = new DentalButton("+ Dossier");
        bDossier.addActionListener(e -> navigate.accept("dossiers"));

        DentalButton bConsult = new DentalButton("+ Consultation");
        bConsult.addActionListener(e -> navigate.accept("consultations"));

        DentalButton bOrdo = new DentalButton("+ Ordonnance");
        bOrdo.addActionListener(e -> navigate.accept("ordonnances"));

        btns.add(bDossier);
        btns.add(bConsult);
        btns.add(bOrdo);

        right.add(btns, BorderLayout.SOUTH);

        root.add(right, BorderLayout.EAST);

        return root;
    }

    private void reload() {
        try {
            DashboardDTO dash = controller.getDashboardDTO(userId);
            MedecinDashboardResponseDTO dto = dash.getMedecin();
            if (dto == null) return;

            statPatients.setValue(String.valueOf(nvl(dto.getNbPatientsDuJour())));
            statRdv.setValue(String.valueOf(nvl(dto.getNbRdvDuJour())));
            statActes.setValue(String.valueOf(nvl(dto.getNbActesRealises())));
            statRecette.setValue(nvl(dto.getRecetteDuJour()) + " DH");

            // client en cours
            if (dto.getPatientEnCours() != null) {
                lblCurrentName.setText(dto.getPatientEnCours().getNomComplet() != null ? dto.getPatientEnCours().getNomComplet() : "—");
                lblCurrentTel.setText(dto.getPatientEnCours().getTel() != null ? ("Tél: " + dto.getPatientEnCours().getTel()) : "");
                lblCurrentStatus.setText(dto.getPatientEnCours().getStatutTraitement() != null ? ("Statut: " + dto.getPatientEnCours().getStatutTraitement()) : "");
            } else {
                lblCurrentName.setText("—");
                lblCurrentTel.setText("");
                lblCurrentStatus.setText("");
            }

            model.setRowCount(0);
            if (dto.getRdvDuJour() != null) {
                for (RdvDto r : dto.getRdvDuJour()) {
                    model.addRow(new Object[]{
                            (r.getHeure() != null) ? r.getHeure().toString() : "-",
                            (r.getPatientNom() != null) ? r.getPatientNom() : "-",
                            (r.getMotif() != null) ? r.getMotif() : "-",
                            (r.getStatut() != null) ? r.getStatut().name() : "-"
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int nvl(Integer v) { return (v != null) ? v : 0; }
    private static String nvl(Object v) { return (v != null) ? String.valueOf(v) : "0"; }
}
