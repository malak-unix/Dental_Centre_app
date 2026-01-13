package ma.dentalTech.mvc.ui.modules.dashboard.secretaire;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.common.AlerteDTO;
import ma.dentalTech.mvc.dto.dashboard.common.NotificationDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class SecretaireDashboardPanel extends JPanel {

    private final DashboardController dashboardController;
    private final Long userId;
    private final Consumer<String> navigate;

    private JLabel vNbPatients;
    private JLabel vRecette;
    private JLabel vNbRdv;
    private JLabel vNbAttente;

    private JPanel waitRow;
    private DefaultListModel<String> activitiesModel;
    private JTextArea notifArea;
    private JLabel notifBadge;

    public SecretaireDashboardPanel(DashboardController dashboardController, Long userId, Consumer<String> navigate) {
        this.dashboardController = dashboardController;
        this.userId = userId;
        this.navigate = navigate;

        setOpaque(false);
        setLayout(new BorderLayout(18, 18));

        JLabel title = new JLabel("Revue du Jour");
        title.setFont(DentalTheme.H1);
        title.setForeground(DentalTheme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        add(main, BorderLayout.CENTER);

        JPanel kpis = new JPanel(new GridLayout(1, 5, 18, 18));
        kpis.setOpaque(false);

        vNbPatients = new JLabel("—");
        vRecette = new JLabel("—");
        vNbRdv = new JLabel("—");
        vNbAttente = new JLabel("—");

        kpis.add(kpi(vNbPatients, "Nb patients"));
        kpis.add(kpi(vRecette, "Recette du jour"));
        kpis.add(kpi(vNbRdv, "RDV du jour"));
        kpis.add(kpi(vNbAttente, "Patients en attente"));
        kpis.add(actionCard("Aller aux RDV", () -> navigate.accept("rdv")));

        main.add(kpis);
        main.add(Box.createVerticalStrut(18));

        CardPanel waitCard = new CardPanel();
        waitCard.setLayout(new BorderLayout(10, 10));
        JLabel wt = new JLabel("File d’Attente");
        wt.setFont(DentalTheme.H2);
        waitCard.add(wt, BorderLayout.NORTH);

        waitRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 8));
        waitRow.setOpaque(false);

        JScrollPane sp = new JScrollPane(waitRow);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        waitCard.add(sp, BorderLayout.CENTER);

        main.add(waitCard);
        main.add(Box.createVerticalStrut(18));

        JPanel bottom = new JPanel(new GridLayout(1, 2, 18, 18));
        bottom.setOpaque(false);
        bottom.add(activitiesCard());
        bottom.add(notifCard());
        main.add(bottom);

        reload();
    }

    private void reload() {
        try {
            DashboardDTO dto = dashboardController.getDashboardDTO(userId);
            setData(dto != null ? dto.getSecretaire() : null);
        } catch (ControllerException ex) {
            setData(null);
        }
    }

    public void setData(SecretaireDashboardResponseDTO dto) {
        int nbPatients = dto != null && dto.getNbPatients() != null ? dto.getNbPatients() : 0;
        int nbRdv = dto != null && dto.getNbRdvDuJour() != null ? dto.getNbRdvDuJour() : 0;
        int nbAtt = dto != null && dto.getNbEnAttente() != null ? dto.getNbEnAttente() : 0;

        BigDecimal recette = dto != null ? dto.getRecetteDuJour() : null;

        vNbPatients.setText(String.valueOf(nbPatients));
        vNbRdv.setText(String.valueOf(nbRdv));
        vNbAttente.setText(String.valueOf(nbAtt));
        vRecette.setText(formatDh(recette));

        waitRow.removeAll();
        List<ListeAttenteDto> file = dto != null ? dto.getFileAttente() : null;
        if (file == null || file.isEmpty()) {
            waitRow.add(patientChip("—"));
        } else {
            for (ListeAttenteDto p : file) {
                String name = (p.getPatientNom() != null && !p.getPatientNom().isBlank())
                        ? p.getPatientNom()
                        : (p.getNom() != null ? p.getNom() : "Patient");
                waitRow.add(patientChip(name));
            }
        }
        waitRow.revalidate();
        waitRow.repaint();

        activitiesModel.clear();
        List<NotificationDTO> notifs = dto != null ? dto.getNotifications() : null;
        if (notifs != null && !notifs.isEmpty()) {
            int limit = Math.min(8, notifs.size());
            for (int i = 0; i < limit; i++) {
                NotificationDTO n = notifs.get(i);
                activitiesModel.addElement(formatNotifLine(n));
            }
        } else {
            activitiesModel.addElement("Aucune activité récente.");
        }

        int nbA = dto != null && dto.getNbAlertesNonLues() != null ? dto.getNbAlertesNonLues() : 0;
        int nbN = dto != null && dto.getNbNotificationsNonLues() != null ? dto.getNbNotificationsNonLues() : 0;
        notifBadge.setText("  " + (nbA + nbN) + "  ");

        StringBuilder sb = new StringBuilder();
        List<AlerteDTO> alertes = dto != null ? dto.getAlertes() : null;
        if (alertes != null && !alertes.isEmpty()) {
            int limit = Math.min(6, alertes.size());
            for (int i = 0; i < limit; i++) {
                AlerteDTO a = alertes.get(i);
                sb.append("• ").append(a.getTitre() != null ? a.getTitre() : "Alerte")
                        .append(" : ")
                        .append(a.getMessage() != null ? a.getMessage() : "")
                        .append("\n");
            }
        } else {
            sb.append("• Aucune alerte.\n");
        }
        notifArea.setText(sb.toString());
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

    private JPanel patientChip(String name) {
        CardPanel chip = new CardPanel();
        chip.setPreferredSize(new Dimension(170, 70));
        chip.setLayout(new BorderLayout(8, 0));
        JLabel n = new JLabel(name);
        n.setFont(DentalTheme.BASE_BOLD);
        chip.add(n, BorderLayout.CENTER);

        JButton dossier = new JButton("Dossier");
        dossier.setFocusPainted(false);
        dossier.addActionListener(e -> navigate.accept("patients")); // ou dossier_medical si tu préfères
        chip.add(dossier, BorderLayout.EAST);
        return chip;
    }

    private CardPanel activitiesCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Activités Récentes");
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        activitiesModel = new DefaultListModel<>();
        JList<String> list = new JList<>(activitiesModel);
        list.setFont(DentalTheme.BASE);
        list.setBorder(null);

        c.add(new JScrollPane(list), BorderLayout.CENTER);
        return c;
    }

    private CardPanel notifCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);

        JLabel t = new JLabel("Notifications / Alertes");
        t.setFont(DentalTheme.H2);

        notifBadge = new JLabel("  0  ");
        notifBadge.setOpaque(true);
        notifBadge.setBackground(new Color(0x1F4C5B));
        notifBadge.setForeground(Color.WHITE);

        head.add(t, BorderLayout.WEST);
        head.add(notifBadge, BorderLayout.EAST);

        c.add(head, BorderLayout.NORTH);

        notifArea = new JTextArea();
        notifArea.setFont(DentalTheme.BASE);
        notifArea.setOpaque(false);
        notifArea.setEditable(false);
        c.add(notifArea, BorderLayout.CENTER);

        return c;
    }

    private String formatDh(BigDecimal v) {
        if (v == null) return "0 DH";
        return v.stripTrailingZeros().toPlainString() + " DH";
    }

    private String formatNotifLine(NotificationDTO n) {
        String titre = n.getTitre() != null ? n.getTitre() : "Notification";
        String msg = n.getMessage() != null ? n.getMessage() : "";
        String d = "";
        if (n.getDate() != null) {
            d = " • " + n.getDate().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        }
        return titre + " : " + msg + d;
    }
}
