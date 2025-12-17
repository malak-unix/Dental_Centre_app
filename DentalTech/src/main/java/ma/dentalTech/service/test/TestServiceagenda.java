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

    //  On récupère les SERVICES via ApplicationContext
    // => ça teste: beans.properties + DI + Service + Repo + DB
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

        //  Test simple: lecture de tous les agendas mensuels
        // But: vérifier que le service appelle bien le repo et retourne une liste
        List<AgendaMensuel> agendas = agendaService.getAllAgendas();
        System.out.println("Agendas mensuels = " + agendas.size());

        //  Si on a au moins un agenda => tester les méthodes "liées" (relations)
        if (!agendas.isEmpty()) {
            AgendaMensuel a = agendas.get(0);
            System.out.println("Agenda ID=" + a.getId()
                    + " | medecinId=" + a.getMedecinId()
                    + " | mois=" + a.getMois()
                    + " | annee=" + a.getAnnee());

            //  Relation 1-N : AgendaMensuel -> DetailJournee
            // But: vérifier que le service sait récupérer les détails par agendaId
            List<DetailJournee> details = agendaService.getDetailsByAgenda(a.getId());
            System.out.println("Détails journées (agendaId=" + a.getId() + ") = " + details.size());

            //  Test "find by (agendaId + date)" si on a au moins 1 journée
            if (!details.isEmpty()) {
                LocalDate d0 = details.get(0).getDateJour();

                // But: vérifier méthode getDetailByAgendaAndDate(...)
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

        //  Test read all RDV
        List<RDV> rdvs = rdvService.getAll();
        System.out.println("Total RDV = " + rdvs.size());

        //  Test filtre par date
        LocalDate today = LocalDate.now();
        System.out.println("RDV aujourd'hui (" + today + ") = " + rdvService.getByDate(today).size());

        //  Test "upcoming" (date >= today)
        System.out.println("RDV à venir = " + rdvService.getUpcomingFromToday().size());

        //  Test filtre par statut
        // IMPORTANT: utiliser une valeur EXISTANTE dans ton enum EtatRendezVous
        System.out.println("RDV PREVU = " + rdvService.getByStatus(EtatRendezVous.PREVU).size());

        System.out.println("RdvService OK");
    }

    // =====================================================
    // LISTE ATTENTE SERVICE
    // =====================================================
    void testListeAttenteService() {
        System.out.println("\n=== TEST SERVICE LISTE ATTENTE ===");

        //  Test lecture de toutes les listes d'attente
        List<ListeAttente> listes = listeAttenteService.getAll();
        System.out.println("Listes d'attente = " + listes.size());

        if (!listes.isEmpty()) {
            ListeAttente l = listes.get(0);
            System.out.println("ListeAttente id=" + l.getId() + " | nom=" + l.getNom());

            //  Test recherche par nom
            // But: vérifier méthode service -> repo -> SQL LIKE/=
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

        //  Test lecture de toutes les plages
        List<PlageHoraire> all = plageHoraireService.getAll();
        System.out.println("Total plages = " + all.size());

        if (!all.isEmpty()) {
            PlageHoraire p = all.get(0);

            //  Afficher un exemple pour vérifier mapping
            System.out.println("Plage id=" + p.getId()
                    + " | detailJourneeId=" + p.getDetailJourneeId()
                    + " | " + p.getHeureDebut() + "-" + p.getHeureFin()
                    + " | dispo=" + p.getDisponible());

            //  Relation 1-N : DetailJournee -> PlageHoraire
            if (p.getDetailJourneeId() != null) {
                System.out.println("Plages par detailJournee = "
                        + plageHoraireService.getByDetailJournee(p.getDetailJourneeId()).size());

                //  Filtre dispo=true (utile dans planning)
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

        // ⚠️ IMPORTANT:
        // - patientId DOIT exister dans DB
        // - detailJourneeId / listeAttenteId si ta table les impose (NOT NULL)
        // => sinon erreur SQL FK / NOT NULL

        RDV r = RDV.builder()
                .patientId(1L) // ⚠️ doit exister
                .dateRdv(LocalDate.now().plusDays(1))
                .heure(LocalTime.of(10, 0))
                .motif("TEST SERVICE RDV")
                //  ATTENTION: ton entity RDV a "statut" STRING (selon ton mapping actuel)
                // donc on stocke le nom du enum en String
                .statut(EtatRendezVous.PREVU.name())
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        //  Ici, on teste que le service fait les validations + appelle repo.create()
        rdvService.create(r);

        //  Après insert, repo met généralement l'id généré (AUTO_INCREMENT)
        System.out.println("RDV créé ID=" + r.getId());
    }

    // =====================================================
    // MAIN
    // =====================================================
    public static void main(String[] args) {
        try {
            TestServiceagenda t = new TestServiceagenda();

            //  On lance les tests un par un pour voir clairement les résultats
            t.testAgendaService();
            t.testRdvService();
            t.testListeAttenteService();
            t.testPlageHoraireService();

            //  Décommente seulement si tu veux tester insert RDV (sinon risque FK)
            // t.insertRdvExample();

            System.out.println("\n✅ TEST SERVICE AGENDA TERMINÉ");
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR TEST SERVICE : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
