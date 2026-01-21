package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

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

    public AgendaHomePanel() {
        this(null, null);
    }

    public AgendaHomePanel(LibelleRole role, Long userId) {
        setLayout(new BorderLayout(12, 12));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(8, 14, 14, 14));

        this.fixedMedecinId = (role == LibelleRole.MEDECIN && userId != null) ? userId : null;

        add(buildBody(), BorderLayout.CENTER);

        showPage("SEMAINE");
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setBackground(DentalTheme.BG);

        content.setLayout(card);
        content.setBackground(DentalTheme.BG);

        content.add(semainePage, "SEMAINE");
        content.add(rdvPage, "RDV");
        content.add(agendaMensuelPage, "AGENDA");
        content.add(listeAttentePage, "LISTE");

        CardPanel centerCard = new CardPanel();
        centerCard.setLayout(new BorderLayout());
        centerCard.add(content, BorderLayout.CENTER);

        body.add(centerCard, BorderLayout.CENTER);

        selectedMedecinId = (fixedMedecinId != null) ? fixedMedecinId : 1L;
        semainePage.setOnMedecinChanged(id -> {
            selectedMedecinId = id;
            rdvPage.setMedecinId(selectedMedecinId);
            agendaMensuelPage.setMedecinId(selectedMedecinId, fixedMedecinId != null);
            agendaMensuelPage.reload();
        });
        semainePage.setFixedMedecinId(fixedMedecinId);
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

}
