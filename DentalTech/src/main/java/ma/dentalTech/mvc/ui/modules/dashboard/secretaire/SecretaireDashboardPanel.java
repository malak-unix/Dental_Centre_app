package ma.dentalTech.mvc.ui.modules.dashboard.secretaire;

import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class SecretaireDashboardPanel extends JPanel {

    private final DashboardController controller;
    private final Long userId;
    private final Consumer<String> navigate;

    // UI (minimal : adapte si tu as déjà des composants)
    private JLabel kpiPatients = new JLabel("-");
    private JLabel kpiRecette = new JLabel("-");
    private JLabel kpiRdv = new JLabel("-");
    private JLabel kpiAttente = new JLabel("-");

    public SecretaireDashboardPanel() {
        this(null, null, k -> {});
    }


    // ✅ ordre (controller, userId, navigate) compatible si ailleurs tu l’utilises dans cet ordre
    public SecretaireDashboardPanel(DashboardController controller, Long userId, Consumer<String> navigate) {
        this.controller = controller;
        this.userId = userId;
        this.navigate = (navigate != null) ? navigate : (k -> {});
        buildUi();
        reload();
    }

    private void buildUi() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 16));

        JLabel title = new JLabel("Revue du Jour");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 12));
        kpis.setOpaque(false);
        kpis.add(kpiCard("Nb patients", kpiPatients));
        kpis.add(kpiCard("Recette du jour", kpiRecette));
        kpis.add(kpiCard("RDV du jour", kpiRdv));
        kpis.add(kpiCard("Patients en attente", kpiAttente));

        JButton btnRdv = new JButton("Rendez-vous");
        btnRdv.addActionListener(e -> this.navigate.accept("rdv"));

        JButton btnPatients = new JButton("Patients");
        btnPatients.addActionListener(e -> this.navigate.accept("patients"));

        JButton btnCaisse = new JButton("La caisse");
        btnCaisse.addActionListener(e -> this.navigate.accept("caisse"));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnPatients);
        actions.add(btnRdv);
        actions.add(btnCaisse);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(kpis, BorderLayout.CENTER);
        top.add(actions, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
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

    public void setData(SecretaireDashboardResponseDTO dto) {
        if (dto == null) return;

        kpiPatients.setText(s(dto.getNbPatients()));
        kpiRdv.setText(s(dto.getNbRdvDuJour()));
        kpiAttente.setText(s(dto.getNbEnAttente()));
        kpiRecette.setText(formatDh(dto.getRecetteDuJour()));
    }

    public void reload() {
        if (controller == null || userId == null) return;
        try {
            DashboardDTO dto = controller.getDashboardDTO(userId);
            setData(dto != null ? dto.getSecretaire() : null);
        } catch (Exception ex) {
            // pas de crash UI
        }
    }

    private String s(Integer v) { return v == null ? "-" : String.valueOf(v); }

    private String formatDh(BigDecimal v) {
        if (v == null) return "-";
        return v.toPlainString() + " DH";
    }
}
