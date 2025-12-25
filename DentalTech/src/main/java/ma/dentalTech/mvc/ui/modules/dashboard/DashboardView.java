package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.DashboardFeaturesDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.ReferentielStatsDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.PatientCurrentDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;
import ma.dentalTech.mvc.ui.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DashboardView extends JPanel {

    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");

    public DashboardView(DashboardDTO dto) {
        setLayout(new BorderLayout(16, 16));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMain(dto), BorderLayout.CENTER);
    }

    private JComponent buildSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(220, 0));
        side.setBackground(DentalTheme.BEIGE);
        side.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("DENTAL CENTER");
        logo.setFont(DentalTheme.titleFont(16));
        logo.setForeground(DentalTheme.PRIMARY_DARK);

        side.add(logo);
        side.add(Box.createVerticalStrut(14));

        side.add(new NavButton("Dashboard", true));
        side.add(Box.createVerticalStrut(8));
        side.add(new NavButton("Les patients", false));
        side.add(Box.createVerticalStrut(8));
        side.add(new NavButton("Rendez-vous", false));
        side.add(Box.createVerticalStrut(8));
        side.add(new NavButton("La caisse", false));
        side.add(Box.createVerticalStrut(8));
        side.add(new NavButton("Stock", false));
        side.add(Box.createVerticalStrut(8));
        side.add(new NavButton("Agenda", false));

        side.add(Box.createVerticalGlue());
        return side;
    }

    private JComponent buildMain(DashboardDTO dto) {
        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setOpaque(false);

        main.add(buildTopbar(dto), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setOpaque(false);

        if (dto == null || dto.getRole() == null) {
            CardPanel card = new CardPanel("Dashboard");
            card.add(new JLabel("DTO null / rôle null"), BorderLayout.CENTER);
            grid.add(card);
            main.add(grid, BorderLayout.CENTER);
            return main;
        }

        DashboardFeaturesDTO f = dto.getFeatures();

        switch (dto.getRole()) {
            case "SECRETAIRE" -> {
                SecretaireDashboardResponseDTO sec = dto.getSecretaire();
                if (f == null || f.isVoirRdvEtFileAttente()) {
                    grid.add(cardRdvSecretaire(sec));
                    grid.add(cardFileAttenteSecretaire(sec));
                }
                if (f == null || f.isVoirCaisse()) {
                    grid.add(cardRecetteDuJour(sec));
                }
            }
            case "MEDECIN" -> {
                MedecinDashboardResponseDTO med = dto.getMedecin();
                grid.add(cardKpiMedecin(med));
                grid.add(cardPatientEnCours(med));
                grid.add(cardRdvMedecin(med));
            }
            case "ADMIN" -> {
                AdminDashboardResponseDTO admin = dto.getAdmin();
                grid.add(cardStatsAdmin(admin));
                grid.add(cardReferentiels(admin));
                grid.add(cardUsersAdmin(admin));
            }
            default -> grid.add(new JLabel("Rôle non supporté: " + dto.getRole()));
        }

        main.add(grid, BorderLayout.CENTER);
        return main;
    }

    private JComponent buildTopbar(DashboardDTO dto) {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        String role = dto == null ? "" : safe(dto.getRole());
        JLabel title = new JLabel("Dashboard " + role);
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JTextField search = new JTextField("Rechercher ...");
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JButton nouveau = new DentalButton("+ Nouveau");

        top.add(title, BorderLayout.WEST);
        top.add(search, BorderLayout.CENTER);
        top.add(nouveau, BorderLayout.EAST);
        return top;
    }

    // =========================
    // SECRETAIRE
    // =========================

    private JComponent cardRdvSecretaire(SecretaireDashboardResponseDTO dto) {
        CardPanel c = new CardPanel("LISTE DE RENDEZ-VOUS");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(line("RDV du jour", String.valueOf(i(dto == null ? null : dto.getNbRdvDuJour()))));
        body.add(Box.createVerticalStrut(8));
        body.add(new JLabel("Liste (à brancher sur RdvDto)"));
        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardFileAttenteSecretaire(SecretaireDashboardResponseDTO dto) {
        CardPanel c = new CardPanel("FILE D’ATTENTE");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(line("En attente", String.valueOf(i(dto == null ? null : dto.getNbEnAttente()))));
        body.add(Box.createVerticalStrut(8));
        body.add(new JLabel("Liste (à brancher sur ListeAttenteDto)"));
        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardRecetteDuJour(SecretaireDashboardResponseDTO dto) {
        CardPanel c = new CardPanel("RECETTE DU JOUR");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        BigDecimal recette = dto == null ? BigDecimal.ZERO : nz(dto.getRecetteDuJour());
        body.add(line("Recette", money(recette) + " DH"));
        c.add(body, BorderLayout.CENTER);
        return c;
    }

    // =========================
    // MEDECIN
    // =========================

    private JComponent cardKpiMedecin(MedecinDashboardResponseDTO dto) {
        CardPanel c = new CardPanel("INDICATEURS (MEDECIN)");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(line("Patients du jour", String.valueOf(i(dto == null ? null : dto.getNbPatientsDuJour()))));
        body.add(line("RDV du jour", String.valueOf(i(dto == null ? null : dto.getNbRdvDuJour()))));
        body.add(line("Actes réalisés", String.valueOf(i(dto == null ? null : dto.getNbActesRealises()))));
        body.add(line("Recette", money(dto == null ? BigDecimal.ZERO : nz(dto.getRecetteDuJour())) + " DH"));

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardPatientEnCours(MedecinDashboardResponseDTO dto) {
        CardPanel c = new CardPanel("CLIENT EN COURS");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        PatientCurrentDTO p = dto == null ? null : dto.getPatientEnCours();
        body.add(line("Nom", p == null ? "—" : safe(p.getNomComplet())));
        body.add(line("Téléphone", p == null ? "—" : safe(p.getTel())));
        body.add(line("Statut", p == null ? "—" : safe(p.getStatutTraitement())));

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardRdvMedecin(MedecinDashboardResponseDTO dto) {
        CardPanel c = new CardPanel("RENDEZ-VOUS DU JOUR");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        int size = (dto == null || dto.getRdvDuJour() == null) ? 0 : dto.getRdvDuJour().size();
        body.add(line("RDV (liste)", String.valueOf(size)));
        body.add(Box.createVerticalStrut(8));
        body.add(new JLabel("Table (à brancher sur RdvDto)"));
        c.add(body, BorderLayout.CENTER);
        return c;
    }

    // =========================
    // ADMIN
    // =========================

    private JComponent cardStatsAdmin(AdminDashboardResponseDTO dto) {
        CardPanel c = new CardPanel("ADMIN - STATISTIQUES");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(line("Utilisateurs", String.valueOf(i(dto == null ? null : dto.getNbUtilisateurs()))));
        body.add(line("Admins", String.valueOf(i(dto == null ? null : dto.getNbAdmins()))));
        body.add(line("Actes réalisés", String.valueOf(i(dto == null ? null : dto.getNbActesRealises()))));
        body.add(line("Recette du jour", money(dto == null ? BigDecimal.ZERO : nz(dto.getRecetteDuJour())) + " DH"));

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardReferentiels(AdminDashboardResponseDTO dto) {
        CardPanel c = new CardPanel("RÉFÉRENTIELS");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        ReferentielStatsDTO r = dto == null ? null : dto.getReferentiels();
        body.add(line("Actes", String.valueOf(i(r == null ? null : r.getNbActes()))));
        body.add(line("Médicaments", String.valueOf(i(r == null ? null : r.getNbMedicaments()))));
        body.add(line("Antécédents", String.valueOf(i(r == null ? null : r.getNbAntecedents()))));
        body.add(line("Assurances", String.valueOf(i(r == null ? null : r.getNbAssurances()))));

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardUsersAdmin(AdminDashboardResponseDTO dto) {
        CardPanel c = new CardPanel("UTILISATEURS");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        int size = (dto == null || dto.getUtilisateurs() == null) ? 0 : dto.getUtilisateurs().size();
        body.add(line("Nombre", String.valueOf(size)));
        body.add(Box.createVerticalStrut(8));
        body.add(new JLabel("Table (à brancher sur UserSummaryDTO)"));

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    // =========================
    // Helpers UI
    // =========================

    private JPanel line(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(DentalTheme.textFont(12));
        l.setForeground(DentalTheme.MUTED);

        JLabel v = new JLabel(value);
        v.setFont(DentalTheme.textBold(12));
        v.setForeground(DentalTheme.PRIMARY_DARK);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private int i(Integer v) { return v == null ? 0 : v; }
    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
    private String money(BigDecimal v) { return DF.format(nz(v)); }
    private String safe(String s) { return s == null ? "" : s; }
}
