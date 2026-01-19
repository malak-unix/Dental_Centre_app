package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AgendaHomePanel extends JPanel {

    private final JPanel content = new JPanel();
    private final CardLayout card = new CardLayout();

    // pages
    private final AgendaSemainePagePanel semainePage = new AgendaSemainePagePanel();
    private final RdvPagePanel rdvPage = new RdvPagePanel();
    private final AgendaMensuelPagePanel agendaMensuelPage = new AgendaMensuelPagePanel();
    private final ListeAttentePagePanel listeAttentePage = new ListeAttentePagePanel();

    private Long selectedMedecinId = null;
    private final Long fixedMedecinId;

    // item list
    private static class MedecinItem {
        final Long id;
        final String label;

        MedecinItem(Long id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override public String toString() {
            return label;
        }
    }

    public AgendaHomePanel() {
        this(null, null);
    }

    public AgendaHomePanel(LibelleRole role, Long userId) {
        setLayout(new BorderLayout(12, 12));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        this.fixedMedecinId = (role == LibelleRole.MEDECIN && userId != null) ? userId : null;

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        showPage("SEMAINE");
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(DentalTheme.BG);

        JLabel title = new JLabel("Module Agenda");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JLabel sub = new JLabel("Semaine · RDV · Agenda Mensuel · Liste d'attente");
        sub.setFont(DentalTheme.textFont(12));
        sub.setForeground(DentalTheme.MUTED);

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        tabs.setOpaque(false);

        JButton bSemaine = new JButton("Semaine");
        JButton bRdv = new JButton("RDV");
        JButton bAgenda = new JButton("Agenda Mensuel");
        JButton bListe = new JButton("Liste d'attente");

        for (JButton b : new JButton[]{bSemaine, bRdv, bAgenda, bListe}) {
            b.setFont(DentalTheme.textFont(12));
            b.setFocusPainted(false);
        }

        bSemaine.addActionListener(e -> showPage("SEMAINE"));
        bRdv.addActionListener(e -> showPage("RDV"));
        bAgenda.addActionListener(e -> showPage("AGENDA"));
        bListe.addActionListener(e -> showPage("LISTE"));

        tabs.add(bSemaine);
        tabs.add(bRdv);
        tabs.add(bAgenda);
        tabs.add(bListe);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        header.add(Box.createVerticalStrut(8));
        header.add(tabs);

        return header;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setBackground(DentalTheme.BG);

        // ===== Colonne Médecins =====
        CardPanel doctorsCard = new CardPanel("Médecins");
        doctorsCard.setPreferredSize(new Dimension(220, 0));

        DefaultListModel<MedecinItem> doctorsModel = new DefaultListModel<>();
        loadDoctors(doctorsModel, fixedMedecinId);

        JList<MedecinItem> doctorsList = new JList<>(doctorsModel);
        doctorsList.setFont(DentalTheme.textBold(12));
        doctorsList.setBackground(DentalTheme.BG);
        doctorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // style
        doctorsList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel(value == null ? "" : value.toString());
            lbl.setOpaque(true);
            lbl.setFont(DentalTheme.textBold(12));
            lbl.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            lbl.setForeground(DentalTheme.TEXT2);
            lbl.setBackground(isSelected ? new Color(0xE8, 0xD9, 0xCC) : DentalTheme.BG);
            return lbl;
        });

        // sélection initiale
        if (fixedMedecinId != null) {
            selectedMedecinId = fixedMedecinId;
            if (!doctorsModel.isEmpty()) {
                doctorsList.setSelectedIndex(0);
            }
            doctorsList.setEnabled(false);
        } else if (!doctorsModel.isEmpty()) {
            doctorsList.setSelectedIndex(0);
            selectedMedecinId = doctorsModel.getElementAt(0).id;
        } else {
            selectedMedecinId = 1L;
        }

        // 🔥 listener : quand on change médecin => reload pages
        doctorsList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            if (fixedMedecinId != null) return;
            MedecinItem item = doctorsList.getSelectedValue();
            if (item == null) return;

            selectedMedecinId = item.id;

            // �o. passe l'id �� semaine (et reload)
            semainePage.setMedecinId(selectedMedecinId);
            semainePage.reload();

            // optionnel: si tu veux filtrer RDV par medecin plus tard
            // rdvPage.setMedecinId(selectedMedecinId); rdvPage.reload();

            // optionnel agenda mensuel
            agendaMensuelPage.setMedecinId(selectedMedecinId, false);
            agendaMensuelPage.reload();
        });

        doctorsCard.add(new JScrollPane(doctorsList), BorderLayout.CENTER);

        // ===== Zone centrale =====
        content.setLayout(card);
        content.setBackground(DentalTheme.BG);

        content.add(semainePage, "SEMAINE");
        content.add(rdvPage, "RDV");
        content.add(agendaMensuelPage, "AGENDA");
        content.add(listeAttentePage, "LISTE");

        CardPanel centerCard = new CardPanel(null, new BorderLayout());
        centerCard.add(content, BorderLayout.CENTER);

        body.add(doctorsCard, BorderLayout.WEST);
        body.add(centerCard, BorderLayout.CENTER);

        // ✅ set medecinId sur semaine dès le début
        semainePage.setMedecinId(selectedMedecinId);
        semainePage.reload();
        agendaMensuelPage.setMedecinId(selectedMedecinId, fixedMedecinId != null);
        agendaMensuelPage.reload();

        return body;
    }

    private void showPage(String key) {
        card.show(content, key);
        revalidate();
        repaint();

        // ✅ au changement d’onglet, on recharge semaine si besoin
        if ("SEMAINE".equals(key)) {
            semainePage.setMedecinId(selectedMedecinId);
            semainePage.reload();
        }
        if ("AGENDA".equals(key)) {
            agendaMensuelPage.setMedecinId(selectedMedecinId, fixedMedecinId != null);
            agendaMensuelPage.reload();
        }
    }

    public void open(String key) {
        showPage(key);
    }

    private void loadDoctors(DefaultListModel<MedecinItem> model, Long onlyMedecinId) {
        model.clear();

        try {
            // Repo direct (simple) — tu as déjà MedecinRepositoryImpl
            ma.dentalTech.repository.modules.users.api.MedecinRepository repo =
                    new ma.dentalTech.repository.modules.users.impl.MedecinRepositoryImpl();

            List<Medecin> list;

            if (onlyMedecinId != null) {
                Medecin m = repo.findById(onlyMedecinId);
                list = (m == null) ? List.of() : List.of(m);
            } else {
                list = repo.findAll();
            }

            if (list == null || list.isEmpty()) {
                model.addElement(new MedecinItem(1L, "Médecin #1"));
                return;
            }

            for (Medecin m : list) {
                String label = ((m.getNom() == null ? "" : m.getNom()) + " " +
                        (m.getPrenom() == null ? "" : m.getPrenom())).trim();
                if (label.isEmpty()) label = "Médecin #" + m.getId();
                model.addElement(new MedecinItem(m.getId(), label));
            }

        } catch (Exception ex) {
            // fallback
            model.addElement(new MedecinItem(1L, "Médecin #1"));
        }
    }
}
