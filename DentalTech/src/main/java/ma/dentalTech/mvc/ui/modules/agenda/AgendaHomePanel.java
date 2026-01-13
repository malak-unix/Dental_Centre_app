package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.NavButton;
import ma.dentalTech.mvc.ui.modules.agenda.AgendaSemainePagePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AgendaHomePanel extends JPanel {

    private final JPanel content = new JPanel();
    private final CardLayout card = new CardLayout();

    // pages
    private final JPanel semainePage = new AgendaSemainePagePanel();
    private final JPanel rdvPage = new RdvPagePanel();
    private final JPanel agendaMensuelPage = new AgendaMensuelPagePanel();
    private final JPanel listeAttentePage = new ListeAttentePagePanel();

    private final Map<String, NavButton> navButtons = new HashMap<>();

    public AgendaHomePanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(14, 14, 14, 14));

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

        // ✅ barre boutons
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        tabs.setOpaque(false);

        JButton bSemaine = new JButton("Semaine");
        JButton bRdv = new JButton("RDV");
        JButton bAgenda = new JButton("Agenda Mensuel");
        JButton bListe = new JButton("Liste d'attente");

        // style simple (tu peux adapter au thème)
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

        // ===== Colonne Médecins (à gauche) =====
        CardPanel doctorsCard = new CardPanel("Médecins");
        doctorsCard.setPreferredSize(new Dimension(220, 0));

        DefaultListModel<String> doctorsModel = new DefaultListModel<>();
        doctorsModel.addElement("TAMARA");
        doctorsModel.addElement("CIEL");
        doctorsModel.addElement("PLUIE");

        JList<String> doctorsList = new JList<>(doctorsModel);
        doctorsList.setFont(DentalTheme.textBold(12));
        doctorsList.setBackground(DentalTheme.BG);
        doctorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorsList.setSelectedIndex(0);

        // ✅ Renderer style maquette
        doctorsList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel(value);
            lbl.setOpaque(true);
            lbl.setFont(DentalTheme.textBold(12));
            lbl.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            lbl.setForeground(DentalTheme.TEXT2);

            if (isSelected) {
                lbl.setBackground(new Color(0xE8, 0xD9, 0xCC));
            } else {
                lbl.setBackground(DentalTheme.BG);
            }
            return lbl;
        });

        doctorsCard.add(new JScrollPane(doctorsList), BorderLayout.CENTER);


        // ===== Zone centrale (pages agenda) =====
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
        return body;

    }


    private void showPage(String key) {
        card.show(content, key);
        revalidate();
        repaint();
    }

    public void open(String key) {
        showPage(key);
    }


}
