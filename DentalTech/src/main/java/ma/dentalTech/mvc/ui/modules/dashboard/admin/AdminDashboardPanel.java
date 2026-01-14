package ma.dentalTech.mvc.ui.modules.dashboard.admin;

import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class AdminDashboardPanel extends JPanel {

    private final DashboardController controller;
    private final Long userId;
    private final Consumer<String> navigate;

    private JLabel kpiUsers = new JLabel("-");
    private JLabel kpiAdmins = new JLabel("-");
    private JLabel kpiRecette = new JLabel("-");
    private JLabel kpiActes = new JLabel("-");

    public AdminDashboardPanel() {
        this(null, null, k -> {});
    }



    public AdminDashboardPanel(DashboardController controller, Long userId, Consumer<String> navigate) {
        this.controller = controller;
        this.userId = userId;
        this.navigate = (navigate != null) ? navigate : (k -> {});
        buildUi();
        reload();
    }

    private void buildUi() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 16));

        JLabel title = new JLabel("Statistiques Globales");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 12));
        kpis.setOpaque(false);
        kpis.add(kpiCard("Utilisateurs", kpiUsers));
        kpis.add(kpiCard("Administrateurs", kpiAdmins));
        kpis.add(kpiCard("Recette du jour", kpiRecette));
        kpis.add(kpiCard("Actes réalisés", kpiActes));

        JButton btnUsers = new JButton("Utilisateurs");
        btnUsers.addActionListener(e -> this.navigate.accept("utilisateurs"));

        JButton btnRef = new JButton("Référentiels");
        btnRef.addActionListener(e -> this.navigate.accept("referentiels"));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnUsers);
        actions.add(btnRef);

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

    public void setData(AdminDashboardResponseDTO dto) {
        if (dto == null) return;

        kpiUsers.setText(s(dto.getNbUtilisateurs()));
        kpiAdmins.setText(s(dto.getNbAdmins()));
        kpiActes.setText(s(dto.getNbActesRealises()));
        kpiRecette.setText(formatDh(dto.getRecetteDuJour()));
    }

    public void reload() {
        if (controller == null || userId == null) return;
        try {
            DashboardDTO dto = controller.getDashboardDTO(userId);
            setData(dto != null ? dto.getAdmin() : null);
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
