package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.entities.agenda.PlageHoraire;
import ma.dentalTech.entities.agenda.RDV;

import ma.dentalTech.entities.enums.EtatRendezVous;

import ma.dentalTech.service.modules.agenda.api.AgendaService;
import ma.dentalTech.service.modules.agenda.api.RdvService;
import ma.dentalTech.service.modules.agenda.api.ListeAttenteService;
import ma.dentalTech.service.modules.agenda.api.PlageHoraireService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestServiceagenda {

    private final AgendaService agendaService =
            ApplicationContext.getBean(AgendaService.class);

    private final RdvService rdvService =
            ApplicationContext.getBean(RdvService.class);

    private final ListeAttenteService listeAttenteService =
            ApplicationContext.getBean(ListeAttenteService.class);

    private final PlageHoraireService plageHoraireService =
            ApplicationContext.getBean(PlageHoraireService.class);

    // =====================================================
    // AGENDA SERVICE
    // =====================================================
    void testAgendaService() {
        System.out.println("\n=== TEST SERVICE AGENDA ===");

        List<AgendaMensuel> agendas = agendaService.getAllAgendas();
        System.out.println("Agendas mensuels = " + agendas.size());

        if (!agendas.isEmpty()) {
            AgendaMensuel a = agendas.get(0);
            System.out.println("Agenda ID=" + a.getId()
                    + " | medecinId=" + a.getMedecinId()
                    + " | mois=" + a.getMois()
                    + " | annee=" + a.getAnnee());

            List<DetailJournee> details = agendaService.getDetailsByAgenda(a.getId());
            System.out.println("Détails journées (agendaId=" + a.getId() + ") = " + details.size());

            // petit test getDetailByAgendaAndDate si on a au moins 1 détail
            if (!details.isEmpty()) {
                LocalDate d0 = details.get(0).getDateJour();
                DetailJournee dj = agendaService.getDetailByAgendaAndDate(a.getId(), d0);
                System.out.println("DetailJournee trouvé par (agendaId,date) = "
                        + (dj != null ? "OK id=" + dj.getId() : "NULL"));
            }
        }

        System.out.println("AgendaService OK");
    }

    // =====================================================
    // RDV SERVICE
    // =====================================================
    void testRdvService() {
        System.out.println("\n=== TEST SERVICE RDV ===");

        List<RDV> rdvs = rdvService.getAll();
        System.out.println("Total RDV = " + rdvs.size());

        LocalDate today = LocalDate.now();
        System.out.println("RDV aujourd'hui (" + today + ") = " + rdvService.getByDate(today).size());

        System.out.println("RDV à venir = " + rdvService.getUpcomingFromToday().size());

        // test par statut (utilise les valeurs EXISTANTES dans ton enum)
        System.out.println("RDV PREVU = " + rdvService.getByStatus(EtatRendezVous.PREVU).size());

        System.out.println("RdvService OK");
    }

    // =====================================================
    // LISTE ATTENTE SERVICE
    // =====================================================
    void testListeAttenteService() {
        System.out.println("\n=== TEST SERVICE LISTE ATTENTE ===");

        List<ListeAttente> listes = listeAttenteService.getAll();
        System.out.println("Listes d'attente = " + listes.size());

        if (!listes.isEmpty()) {
            ListeAttente l = listes.get(0);
            System.out.println("ListeAttente id=" + l.getId() + " | nom=" + l.getNom());

            // recherche
            List<ListeAttente> found = listeAttenteService.searchByNomListe(l.getNom());
            System.out.println("Recherche par nom = " + found.size());
        }

        System.out.println("ListeAttenteService OK");
    }

    // =====================================================
    // PLAGE HORAIRE SERVICE
    // =====================================================
    void testPlageHoraireService() {
        System.out.println("\n=== TEST SERVICE PLAGE HORAIRE ===");

        List<PlageHoraire> all = plageHoraireService.getAll();
        System.out.println("Total plages = " + all.size());

        if (!all.isEmpty()) {
            PlageHoraire p = all.get(0);
            System.out.println("Plage id=" + p.getId()
                    + " | detailJourneeId=" + p.getDetailJourneeId()
                    + " | " + p.getHeureDebut() + "-" + p.getHeureFin()
                    + " | dispo=" + p.getDisponible());

            if (p.getDetailJourneeId() != null) {
                System.out.println("Plages par detailJournee = "
                        + plageHoraireService.getByDetailJournee(p.getDetailJourneeId()).size());

                System.out.println("Plages disponibles par detailJournee = "
                        + plageHoraireService.getDisponiblesByDetailJournee(p.getDetailJourneeId()).size());
            }
        }

        System.out.println("PlageHoraireService OK");
    }

    // =====================================================
    // INSERT RDV (OPTIONNEL)
    // =====================================================
    void insertRdvExample() {
        System.out.println("\n=== INSERT RDV (TEST SERVICE) ===");

        RDV r = RDV.builder()
                .patientId(1L) // ⚠️ doit exister
                .dateRdv(LocalDate.now().plusDays(1))
                .heure(LocalTime.of(10, 0))
                .motif("TEST SERVICE RDV")
                .statut(String.valueOf(EtatRendezVous.PREVU))
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        rdvService.create(r);
        System.out.println("RDV créé ID=" + r.getId());
    }

    // =====================================================
    // MAIN
    // =====================================================
    public static void main(String[] args) {
        try {
            TestServiceagenda t = new TestServiceagenda();

            t.testAgendaService();
            t.testRdvService();
            t.testListeAttenteService();
            t.testPlageHoraireService();

            // Décommente seulement si tu veux tester insert RDV
            // t.insertRdvExample();

            System.out.println("\n✅ TEST SERVICE AGENDA TERMINÉ");
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR TEST SERVICE : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
