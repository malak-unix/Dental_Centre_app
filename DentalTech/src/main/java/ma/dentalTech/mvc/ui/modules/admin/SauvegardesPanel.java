package ma.dentalTech.mvc.ui.modules.admin;

import ma.dentalTech.entities.log.Log;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;
import ma.dentalTech.repository.modules.log.api.LogRepository;
import ma.dentalTech.repository.modules.log.impl.LogRepositoryImpl;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.repository.modules.users.impl.UtilisateurRepositoryImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SauvegardesPanel extends JPanel {

    private final LogRepository logRepo = new LogRepositoryImpl();
    private final UtilisateurRepository userRepo = new UtilisateurRepositoryImpl();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Date", "Utilisateur", "Action", "Description"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);

    public SauvegardesPanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        CardPanel card = new CardPanel(null);
        card.setBackground(DentalTheme.CARD);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Sauvegardes Systeme");
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        DentalButton btnBackup = new DentalButton("Lancer une sauvegarde");
        DentalButton btnExport = new DentalButton("Exporter activites (CSV)");
        UiStyles.stylePrimaryButton(btnBackup);
        UiStyles.styleSecondaryButton(btnExport);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(btnExport);
        actions.add(btnBackup);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        UiStyles.styleTable(table);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        card.add(header, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        btnBackup.addActionListener(e -> onBackup());
        btnExport.addActionListener(e -> onExportCsv());

        refreshLogs();
    }

    private void refreshLogs() {
        model.setRowCount(0);
        List<Log> logs = logRepo.findRecent(100);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (logs == null) return;

        for (Log l : logs) {
            String date = l.getDateAction() != null ? l.getDateAction().format(fmt) : "";
            String user = resolveUser(l.getUtilisateurId());
            model.addRow(new Object[]{
                    date,
                    user,
                    safe(l.getAction()),
                    safe(l.getDescription())
            });
        }
    }

    private String resolveUser(Long userId) {
        if (userId == null) return "-";
        try {
            Utilisateur u = userRepo.findById(userId);
            if (u == null) return "#" + userId;
            String nom = safe(u.getNom());
            String prenom = safe(u.getPrenom());
            String full = (nom + " " + prenom).trim();
            return full.isEmpty() ? ("#" + userId) : full;
        } catch (Exception ignored) {
            return "#" + userId;
        }
    }

    private void onBackup() {
        JOptionPane.showMessageDialog(this,
                "Sauvegarde simulee effectuee avec succes.",
                "Sauvegarde",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void onExportCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("activites_admin.csv"));
        int ok = fc.showSaveDialog(this);
        if (ok != JFileChooser.APPROVE_OPTION) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Date,Utilisateur,Action,Description\n");
        for (int i = 0; i < model.getRowCount(); i++) {
            sb.append(csv(model.getValueAt(i, 0))).append(',')
              .append(csv(model.getValueAt(i, 1))).append(',')
              .append(csv(model.getValueAt(i, 2))).append(',')
              .append(csv(model.getValueAt(i, 3))).append('\n');
        }

        try {
            Files.write(fc.getSelectedFile().toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
            JOptionPane.showMessageDialog(this, "Export CSV termine.", "Export", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur export: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csv(Object v) {
        String s = v == null ? "" : v.toString();
        s = s.replace("\"", "\"\"");
        return "\"" + s + "\"";
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
