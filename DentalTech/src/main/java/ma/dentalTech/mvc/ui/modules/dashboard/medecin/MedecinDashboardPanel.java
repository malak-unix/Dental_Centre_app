package ma.dentalTech.mvc.ui.modules.dashboard.medecin;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MedecinDashboardPanel extends JPanel {

    private final DashboardController dashboardController;
    private final Long userId;
    private final Consumer<String> navigate;

    private JLabel vRdv;
    private JLabel vActes;
    private JLabel vRecette;

    public MedecinDashboardPanel(DashboardController dashboardController, Long userId, Consumer<String> navigate) {
        this.dashboardController = dashboardController;
        this.userId = userId;
        this.navigate = navigate;

        setOpaque(false);
        setLayout(new BorderLayout(18, 18));

        JLabel title = new JLabel("Dashboard Médecin");
        title.setFont(DentalTheme.H1);
        title.setForeground(DentalTheme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        add(main, BorderLayout.CENTER);

        JPanel kpis = new JPanel(new GridLayout(1, 4, 18, 18));
        kpis.setOpaque(false);

        vRdv = new JLabel("—");
        vActes = new JLabel("—");
        vRecette = new JLabel("—");

        kpis.add(kpi(vRdv, "RDV du jour"));
        kpis.add(kpi(vActes, "Actes réalisés"));
        kpis.add(kpi(vRecette, "Recette du jour"));
        kpis.add(actionCard("Mes consultations", () -> navigate.accept("consultations")));

        main.add(kpis);
        main.add(Box.createVerticalStrut(18));

        JPanel actions = new JPanel(new GridLayout(1, 3, 18, 18));
        actions.setOpaque(false);

        actions.add(actionCard("Patients", () -> navigate.accept("patients")));
        actions.add(actionCard("Ordonnances", () -> navigate.accept("ordonnances")));
        actions.add(actionCard("Dossier médical", () -> navigate.accept("dossier_medical"))); // à brancher

        main.add(actions);

        reload();
    }

    private void reload() {
        try {
            DashboardDTO dto = dashboardController.getDashboardDTO(userId);
            MedecinDashboardResponseDTO med = dto != null ? dto.getMedecin() : null;

            int rdv = med != null && med.getNbRdvDuJour() != null ? med.getNbRdvDuJour() : 0;
            int actes = med != null && med.getNbActesRealises() != null ? med.getNbActesRealises() : 0;
            String recette = med != null && med.getRecetteDuJour() != null ? med.getRecetteDuJour() + " DH" : "0 DH";

            vRdv.setText(String.valueOf(rdv));
            vActes.setText(String.valueOf(actes));
            vRecette.setText(recette);

        } catch (ControllerException ex) {
            vRdv.setText("0");
            vActes.setText("0");
            vRecette.setText("0 DH");
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
}
