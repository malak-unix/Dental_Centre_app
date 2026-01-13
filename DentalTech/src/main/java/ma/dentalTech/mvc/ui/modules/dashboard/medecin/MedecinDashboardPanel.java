package ma.dentalTech.mvc.ui.modules.dashboard.medecin;

import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.PatientCurrentDTO;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class MedecinDashboardPanel extends JPanel {

    private final DashboardController controller;
    private final Long userId;
    private final Consumer<String> navigate;

    private JLabel kpiPatients = new JLabel("-");
    private JLabel kpiRecette = new JLabel("-");
    private JLabel kpiRdv = new JLabel("-");
    private JLabel kpiActes = new JLabel("-");

    private JLabel patientCourant = new JLabel("—");

    public MedecinDashboardPanel() {
        this(null, null, k -> {});
    }



    public MedecinDashboardPanel(DashboardController controller, Long userId, Consumer<String> navigate) {
        this.controller = controller;
        this.userId = userId;
        this.navigate = (navigate != null) ? navigate : (k -> {});
        buildUi();
        reload();
    }

    private void buildUi() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 16));

        JLabel title = new JLabel("Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 12));
        kpis.setOpaque(false);
        kpis.add(kpiCard("Nb patients (jour)", kpiPatients));
        kpis.add(kpiCard("Recette du jour", kpiRecette));
        kpis.add(kpiCard("RDV du jour", kpiRdv));
        kpis.add(kpiCard("Actes réalisés", kpiActes));

        JButton btnPlanning = new JButton("Planning");
        btnPlanning.addActionListener(e -> this.navigate.accept("agenda_med"));

        JButton btnDossiers = new JButton("Dossiers");
        btnDossiers.addActionListener(e -> this.navigate.accept("dossiers"));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnPlanning);
        actions.add(btnDossiers);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(kpis, BorderLayout.CENTER);
        top.add(actions, BorderLayout.SOUTH);

        JPanel current = new JPanel(new BorderLayout());
        current.setBorder(BorderFactory.createTitledBorder("Client en cours"));
        current.add(patientCourant, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(current, BorderLayout.CENTER);
    }

    private JPanel kpiCard(String label, JLabel value) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel l = new JLabel(label);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 13f));
        value.setFont(value.getFont().deriveFont(Font.BOLD, 18f));
        p.add(l, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    public void setData(MedecinDashboardResponseDTO dto) {
        if (dto == null) return;

        kpiPatients.setText(s(dto.getNbPatientsDuJour()));
        kpiRdv.setText(s(dto.getNbRdvDuJour()));
        kpiActes.setText(s(dto.getNbActesRealises()));
        kpiRecette.setText(formatDh(dto.getRecetteDuJour()));

        // ✅ c’est patientEnCours (pas patientCourant)
        PatientCurrentDTO pc = dto.getPatientEnCours();
        if (pc == null) {
            patientCourant.setText("—");
        } else {
            String name = (pc.getNomComplet() != null) ? pc.getNomComplet() : "Patient";
            patientCourant.setText(name);
        }
    }

    public void reload() {
        if (controller == null || userId == null) return;
        try {
            DashboardDTO dto = controller.getDashboardDTO(userId);
            setData(dto != null ? dto.getMedecin() : null);
        } catch (Exception ex) {
            // silence
        }
    }

    private String s(Integer v) { return v == null ? "-" : String.valueOf(v); }

    private String formatDh(BigDecimal v) {
        if (v == null) return "-";
        return v.toPlainString() + " DH";
    }
}
