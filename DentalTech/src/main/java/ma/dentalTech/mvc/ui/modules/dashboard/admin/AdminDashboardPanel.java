package ma.dentalTech.mvc.ui.modules.dashboard.admin;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.BackupStatusDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.ReferentielStatsDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardPanel extends JPanel {

    private final Long userId;

    private JLabel vUsers, vAdmins, vRecette, vActes;

    private DefaultTableModel usersModel;

    // Référentiels
    private JLabel vRefActes, vRefMedic, vRefAnte, vRefAss;

    // Backup
    private JLabel vBackupStatut, vBackupDate, vBackupTaille;

    public AdminDashboardPanel() {
        this(1L);
    }

    public AdminDashboardPanel(Long userId) {
        this.userId = (userId != null) ? userId : 1L;

        setOpaque(false);
        setLayout(new BorderLayout(18, 18));

        JLabel title = new JLabel("Statistiques Globales");
        title.setFont(DentalTheme.H1);
        title.setForeground(DentalTheme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        add(main, BorderLayout.CENTER);

        JPanel kpis = new JPanel(new GridLayout(1, 5, 18, 18));
        kpis.setOpaque(false);

        vUsers = new JLabel("—");
        vAdmins = new JLabel("—");
        vRecette = new JLabel("—");
        vActes = new JLabel("—");

        kpis.add(kpi(vUsers, "Utilisateurs"));
        kpis.add(kpi(vAdmins, "Administrateurs"));
        kpis.add(kpi(vRecette, "Recette du jour"));
        kpis.add(kpi(vActes, "Actes réalisés"));
        kpis.add(actionCard("Rafraîchir", this::refresh));

        main.add(kpis);
        main.add(Box.createVerticalStrut(18));

        JPanel grid = new JPanel(new GridLayout(1, 2, 18, 18));
        grid.setOpaque(false);
        grid.add(usersTableCard());
        grid.add(rightCard()); // référentiels + backup
        main.add(grid);

        // charge
        refresh();
    }

    public final void refresh() {
        AdminDashboardResponseDTO adminDto = null;
        try {
            DashboardDTO dash = fetchDashboardDTO();
            if (dash != null) adminDto = dash.getAdmin();
        } catch (Exception ignore) {}

        setData(adminDto);
    }

    private DashboardDTO fetchDashboardDTO() {
        // 1) Controller (préféré, méthodologie prof)
        Object bean = ApplicationContext.getBean("dashboardController");
        if (bean instanceof DashboardController ctrl) {
            try {
                return ctrl.getDashboardDTO(userId);
            } catch (Exception ignore) { /* fallback */ }
        }

        // 2) Service fallback
        DashboardService service = ApplicationContext.getBean(DashboardService.class);
        if (service != null) {
            try {
                return service.getDashboard(userId);
            } catch (Exception ignore) {}
        }
        return null;
    }

    public void setData(AdminDashboardResponseDTO dto) {
        int nbUsers = dto != null && dto.getNbUtilisateurs() != null ? dto.getNbUtilisateurs() : 0;
        int nbAdmins = dto != null && dto.getNbAdmins() != null ? dto.getNbAdmins() : 0;
        int nbActes = dto != null && dto.getNbActesRealises() != null ? dto.getNbActesRealises() : 0;
        BigDecimal recette = dto != null ? dto.getRecetteDuJour() : null;

        vUsers.setText(String.valueOf(nbUsers));
        vAdmins.setText(String.valueOf(nbAdmins));
        vActes.setText(String.valueOf(nbActes));
        vRecette.setText(formatDh(recette));

        // table users
        usersModel.setRowCount(0);
        List<UserSummaryDTO> list = dto != null ? dto.getUtilisateurs() : null;
        if (list != null && !list.isEmpty()) {
            for (UserSummaryDTO u : list) {
                usersModel.addRow(new Object[]{
                        u.getRole() != null ? u.getRole().name() : "",
                        fullName(u),
                        u.getStatut() != null ? u.getStatut() : (u.isActif() ? "Actif" : "Désactivé"),
                        human(u.getDerniereActivite())
                });
            }
        } else {
            usersModel.addRow(new Object[]{"", "Aucun utilisateur", "", ""});
        }

        // référentiels
        ReferentielStatsDTO ref = dto != null ? dto.getReferentiels() : null;
        vRefActes.setText(String.valueOf(ref != null && ref.getNbActes() != null ? ref.getNbActes() : 0));
        vRefMedic.setText(String.valueOf(ref != null && ref.getNbMedicaments() != null ? ref.getNbMedicaments() : 0));
        vRefAnte.setText(String.valueOf(ref != null && ref.getNbAntecedents() != null ? ref.getNbAntecedents() : 0));
        vRefAss.setText(String.valueOf(ref != null && ref.getNbAssurances() != null ? ref.getNbAssurances() : 0));

        // backup
        BackupStatusDTO backup = dto != null ? dto.getSauvegarde() : null;
        if (backup == null) {
            vBackupStatut.setText("—");
            vBackupDate.setText("—");
            vBackupTaille.setText("—");
        } else {
            vBackupStatut.setText(safe(backup.getStatut(), "—"));
            vBackupTaille.setText(safe(backup.getTaille(), "—"));
            vBackupDate.setText(backup.getDerniereSauvegarde() != null
                    ? backup.getDerniereSauvegarde().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "—");
        }
    }

    private CardPanel kpi(JLabel valueLabel, String label) {
        CardPanel c = new CardPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        valueLabel.setFont(DentalTheme.H2);

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

    private CardPanel usersTableCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Utilisateurs");
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        String[] cols = {"Rôle", "Nom", "Statut", "Dernière activité"};
        usersModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(usersModel);
        table.setRowHeight(28);

        c.add(new JScrollPane(table), BorderLayout.CENTER);
        return c;
    }

    private CardPanel rightCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new GridLayout(1, 2, 12, 12));
        top.setOpaque(false);

        top.add(referentielCard());
        top.add(backupCard());

        c.add(top, BorderLayout.CENTER);
        return c;
    }

    private CardPanel referentielCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Référentiels");
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(4, 2, 10, 10));
        grid.setOpaque(false);

        vRefActes = new JLabel("0");
        vRefMedic = new JLabel("0");
        vRefAnte = new JLabel("0");
        vRefAss = new JLabel("0");

        grid.add(new JLabel("Actes"));
        grid.add(vRefActes);
        grid.add(new JLabel("Médicaments"));
        grid.add(vRefMedic);
        grid.add(new JLabel("Antécédents"));
        grid.add(vRefAnte);
        grid.add(new JLabel("Assurances"));
        grid.add(vRefAss);

        c.add(grid, BorderLayout.CENTER);
        return c;
    }

    private CardPanel backupCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Sauvegarde");
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 10));
        grid.setOpaque(false);

        vBackupStatut = new JLabel("—");
        vBackupDate = new JLabel("—");
        vBackupTaille = new JLabel("—");

        grid.add(new JLabel("Statut"));
        grid.add(vBackupStatut);
        grid.add(new JLabel("Dernière"));
        grid.add(vBackupDate);
        grid.add(new JLabel("Taille"));
        grid.add(vBackupTaille);

        c.add(grid, BorderLayout.CENTER);
        return c;
    }

    private String formatDh(BigDecimal v) {
        if (v == null) return "0 DH";
        return v.stripTrailingZeros().toPlainString() + " DH";
    }

    private String safe(String s, String dft) {
        return (s == null || s.isBlank()) ? dft : s;
    }

    private String fullName(UserSummaryDTO u) {
        String prenom = u.getPrenom() != null ? u.getPrenom() : "";
        String nom = u.getNom() != null ? u.getNom() : "";
        String fn = (prenom + " " + nom).trim();
        return fn.isBlank() ? "Utilisateur" : fn;
    }

    private String human(LocalDateTime dt) {
        if (dt == null) return "";
        Duration d = Duration.between(dt, LocalDateTime.now());
        long min = d.toMinutes();
        if (min < 60) return "il y a " + min + " min";
        long h = min / 60;
        if (h < 24) return "il y a " + h + " h";
        long j = h / 24;
        return "il y a " + j + " j";
    }
}
