package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
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
        setBorder(new EmptyBorder(8, 14, 14, 14));

        this.fixedMedecinId = (role == LibelleRole.MEDECIN && userId != null) ? userId : null;

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        showPage("SEMAINE");
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabs.setOpaque(false);

        JButton bSemaine = new JButton("Semaine");
        JButton bRdv = new JButton("RDV");
        JButton bAgenda = new JButton("Agenda Mensuel");
        JButton bListe = new JButton("Liste d'attente");

        for (JButton b : new JButton[]{bSemaine, bRdv, bAgenda, bListe}) {
            styleTabButton(b);
        }

        bSemaine.addActionListener(e -> showPage("SEMAINE"));
        bRdv.addActionListener(e -> showPage("RDV"));
        bAgenda.addActionListener(e -> showPage("AGENDA"));
        bListe.addActionListener(e -> showPage("LISTE"));

        tabs.add(bSemaine);
        tabs.add(bRdv);
        tabs.add(bAgenda);
        tabs.add(bListe);

        header.add(tabs);

        return header;
    }

    private void styleTabButton(JButton b) {
        b.setFont(DentalTheme.textBold(12));
        b.setFocusPainted(false);
        b.setBackground(DentalTheme.PRIMARY_DARK);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        b.setOpaque(true);
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setBackground(DentalTheme.BG);

        CardPanel doctorsCard = new CardPanel("Medecins");
        doctorsCard.setPreferredSize(new Dimension(200, 0));

        DefaultListModel<MedecinItem> doctorsModel = new DefaultListModel<>();
        loadDoctors(doctorsModel, fixedMedecinId);

        JList<MedecinItem> doctorsList = new JList<>(doctorsModel);
        doctorsList.setFont(DentalTheme.textBold(12));
        doctorsList.setBackground(DentalTheme.BG);
        doctorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        doctorsList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel(value == null ? "" : value.toString());
            lbl.setOpaque(true);
            lbl.setFont(DentalTheme.textBold(12));
            lbl.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            lbl.setForeground(DentalTheme.TEXT2);
            lbl.setBackground(isSelected ? new Color(0xE8, 0xD9, 0xCC) : DentalTheme.BG);
            return lbl;
        });

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

        doctorsList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            if (fixedMedecinId != null) return;
            MedecinItem item = doctorsList.getSelectedValue();
            if (item == null) return;

            selectedMedecinId = item.id;

            semainePage.setMedecinId(selectedMedecinId);
            semainePage.reload();

            rdvPage.setMedecinId(selectedMedecinId);

            agendaMensuelPage.setMedecinId(selectedMedecinId, false);
            agendaMensuelPage.reload();
        });

        doctorsCard.add(new JScrollPane(doctorsList), BorderLayout.CENTER);

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

        semainePage.setMedecinId(selectedMedecinId);
        semainePage.setDate(LocalDate.now());
        rdvPage.setMedecinId(selectedMedecinId);
        agendaMensuelPage.setMedecinId(selectedMedecinId, fixedMedecinId != null);
        agendaMensuelPage.reload();

        return body;
    }

    private void showPage(String key) {
        card.show(content, key);
        revalidate();
        repaint();

        if ("SEMAINE".equals(key)) {
            semainePage.setMedecinId(selectedMedecinId);
            semainePage.setDate(LocalDate.now());
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
                model.addElement(new MedecinItem(1L, "Medecin #1"));
                return;
            }

            for (Medecin m : list) {
                String label = ((m.getNom() == null ? "" : m.getNom()) + " " +
                        (m.getPrenom() == null ? "" : m.getPrenom())).trim();
                if (label.isEmpty()) label = "Medecin #" + m.getId();
                model.addElement(new MedecinItem(m.getId(), label));
            }

        } catch (Exception ex) {
            model.addElement(new MedecinItem(1L, "Medecin #1"));
        }
    }
}
