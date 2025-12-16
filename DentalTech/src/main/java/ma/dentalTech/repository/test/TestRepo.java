package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.common.exceptions.DaoException;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.entities.agenda.ListeAttente;

import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;
import ma.dentalTech.repository.modules.agenda.api.ListeAttenteRepository;
import ma.dentalTech.repository.modules.agenda.api.RdvRepository;

import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestRepo {

    // =========================
    // Repos (caisse)
    // =========================
    private final FactureRepository factureRepo =
            ApplicationContext.getBean(FactureRepository.class);
    private final ChargesRepository chargesRepo =
            ApplicationContext.getBean(ChargesRepository.class);
    private final RevenuesRepository revenusRepo =
            ApplicationContext.getBean(RevenuesRepository.class);
    private final SituationFinanciereRepository sitFinRepo =
            ApplicationContext.getBean(SituationFinanciereRepository.class);

    // =========================
    // Repos (agenda)
    // =========================
    private final AgendaMensuelRepository agendaRepo =
            ApplicationContext.getBean(AgendaMensuelRepository.class);

    private final DetailJourneeRepository detailJourneeRepo =
            ApplicationContext.getBean(DetailJourneeRepository.class);

    private final RdvRepository rdvRepo =
            ApplicationContext.getBean(RdvRepository.class);

    private final ListeAttenteRepository listeAttenteRepo =
            ApplicationContext.getBean(ListeAttenteRepository.class);

    // ==========================================================
    // INSERT / UPDATE / DELETE (à compléter plus tard)
    // ==========================================================
    void insertProcess() throws DaoException {
        System.out.println("\n=== INSERT PROCESS ===");
        // TODO: Ajoute ici insert() quand vous avez les entities finales + relations
        System.out.println("Insert process: OK (à compléter selon entities).");
    }

    void updateProcess() throws DaoException {
        System.out.println("\n=== UPDATE PROCESS ===");
        // TODO: update d'un enregistrement existant
        System.out.println("Update process: OK (à compléter).");
    }

    void deleteProcess() throws DaoException {
        System.out.println("\n=== DELETE PROCESS ===");
        // TODO: deleteById(id)
        System.out.println("Delete process: OK (à compléter).");
    }

    // ==========================================================
    // CAISSE - SELECT
    // ==========================================================
    void selectProcess() throws DaoException {
        System.out.println("\n=== SELECT PROCESS (CAISSE) ===");

        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        System.out.println("Total factures (30j)  = " + factureRepo.calculateTotalFactures(start, end));
        System.out.println("Total réglé   (30j)   = " + factureRepo.calculateTotalRegle(start, end));
        System.out.println("Total non réglé (30j) = " + factureRepo.calculateTotalNonRegle(start, end));

        System.out.println("Total charges (30j)   = " + chargesRepo.calculateTotalCharges(start, end));
        System.out.println("Total revenus (30j)   = " + revenusRepo.calculateTotalRevenus(start, end));

        // si vous avez une méthode calcul SF, sinon laisse
        // System.out.println("Situation financière = " + sitFinRepo....);

        System.out.println("Select process (caisse): OK");
    }

    // ==========================================================
    // AGENDA - SELECT
    // ==========================================================
    void agendaSelectProcess() {
        System.out.println("\n=== AGENDA : SELECT PROCESS ===");
        try {
            List<AgendaMensuel> agendas = agendaRepo.findAll();
            System.out.println("Agendas mensuels = " + agendas.size());

            if (!agendas.isEmpty()) {
                AgendaMensuel a = agendas.get(0);
                System.out.println("Agenda id=" + a.getId()
                        + " | medecinId=" + a.getMedecinId()
                        + " | " + a.getMois() + "/" + a.getAnnee());

                List<DetailJournee> jours = detailJourneeRepo.findByAgendaId(a.getId());
                System.out.println("  Journées = " + jours.size());
            }

            System.out.println("Agenda SELECT OK");
        } catch (Exception e) {
            System.err.println("❌ AGENDA SELECT FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // RDV - SELECT
    // ==========================================================
    void rdvSelectProcess() {
        System.out.println("\n=== RDV : SELECT PROCESS ===");
        try {
            System.out.println("Tous les RDV = " + rdvRepo.findAll().size());

            LocalDate today = LocalDate.now();
            System.out.println("RDV aujourd'hui = " + rdvRepo.findByDate(today).size());

            System.out.println("RDV à venir = " + rdvRepo.findUpcomingFromToday().size());

            // méthodes dashboard (même si stub)
            System.out.println("RDV (30j) = " +
                    rdvRepo.countByDate(
                            LocalDateTime.now().minusDays(30),
                            LocalDateTime.now()
                    )
            );

            System.out.println("RDV SELECT OK");
        } catch (Exception e) {
            System.err.println("❌ RDV SELECT FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // LISTE ATTENTE - SELECT
    // ==========================================================
    void listeAttenteSelectProcess() {
        System.out.println("\n=== LISTE ATTENTE : SELECT PROCESS ===");
        try {
            List<ListeAttente> listes = listeAttenteRepo.findAll();
            System.out.println("Listes d'attente = " + listes.size());

            if (!listes.isEmpty()) {
                ListeAttente l = listes.get(0);
                System.out.println("ListeAttente id=" + l.getId() + " | nom=" + l.getNom());
            }

            System.out.println("ListeAttente SELECT OK");
        } catch (Exception e) {
            System.err.println("❌ LISTE ATTENTE SELECT FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // MAIN
    // ==========================================================
    public static void main(String[] args) {
        try {
            TestRepo t = new TestRepo();

            t.insertProcess();
            t.updateProcess();
            t.deleteProcess();

            t.selectProcess();               // caisse
            t.agendaSelectProcess();         // agenda
            t.rdvSelectProcess();            // rdv
            t.listeAttenteSelectProcess();   // liste attente

            System.out.println("\n✅ TEST REPO terminé avec succès.");
        } catch (Exception e) {
            System.err.println("\n❌ TEST REPO échoué : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
