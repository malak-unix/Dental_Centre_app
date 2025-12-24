package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.NavButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AgendaHomePanel extends JPanel {

    private final JPanel content = new JPanel();
    private final CardLayout card = new CardLayout();

    // pages
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

        showPage("RDV");
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setBackground(DentalTheme.BG);

        JLabel title = new JLabel("Module Agenda");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JLabel sub = new JLabel("RDV · Agenda Mensuel · Liste d'attente");
        sub.setFont(DentalTheme.textFont(12));
        sub.setForeground(DentalTheme.MUTED);

        header.add(title);
        header.add(sub);
        return header;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setBackground(DentalTheme.BG);

        JPanel nav = new JPanel(new GridLayout(3, 1, 10, 10));
        nav.setBackground(DentalTheme.BG);
        nav.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                "Navigation"
        ));

        NavButton bRdv = new NavButton("📄 RDV", true);
        NavButton bAgenda = new NavButton("🗓️ Agenda mensuel", false);
        NavButton bListe = new NavButton("⏳ Liste d'attente", false);

        navButtons.put("RDV", bRdv);
        navButtons.put("AGENDA", bAgenda);
        navButtons.put("LISTE", bListe);

        bRdv.addActionListener(e -> showPage("RDV"));
        bAgenda.addActionListener(e -> showPage("AGENDA"));
        bListe.addActionListener(e -> showPage("LISTE"));

        nav.add(bRdv);
        nav.add(bAgenda);
        nav.add(bListe);

        content.setLayout(card);
        content.setBackground(DentalTheme.BG);
        content.setBorder(BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true));

        content.add(rdvPage, "RDV");
        content.add(agendaMensuelPage, "AGENDA");
        content.add(listeAttentePage, "LISTE");

        body.add(nav, BorderLayout.WEST);
        body.add(content, BorderLayout.CENTER);
        return body;
    }

    private void showPage(String key) {
        card.show(content, key);

        navButtons.forEach((k, btn) -> btn.setActive(k.equals(key)));

        revalidate();
        repaint();
    }

}
