package ma.dentalTech.tests;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestAgendaRepository {

    private static AgendaMensuelRepository agendaMensuelRepository;
    private static DetailJourneeRepository detailJourneeRepository;

    public static void main(String[] args) {

        agendaMensuelRepository = ApplicationContext.getBean(AgendaMensuelRepository.class);
        detailJourneeRepository = ApplicationContext.getBean(DetailJourneeRepository.class);

        System.out.println("=== TEST AGENDA REPOSITORIES ===");

        // 1) Créer ou retrouver un agenda mensuel
        AgendaMensuel agenda = insertAgendaTest();

        // 2) Trouver l'agenda par medecin + mois + année
        findAgendaByMedecinAndMonthTest(agenda.getMedecinId(), agenda.getMois(), agenda.getAnnee());

        // 3) Créer une journée liée à cet agenda
        DetailJournee journee = insertDetailJourneeTest(agenda);

        // 4) Lister les journées de l'agenda
        findJoursByAgendaTest(agenda.getId());

        // 5) Mettre à jour la journée (horaires + statut)
        updateDetailJourneeTest(journee);

        System.out.println("=== FIN DES TESTS AGENDA ===");
    }

    /**
     * Création (ou récupération) d'un agenda mensuel.
     * Si un agenda existe déjà pour ce médecin + mois + année,
     * on le réutilise pour éviter la contrainte UNIQUE.
     */
    private static AgendaMensuel insertAgendaTest() {
        System.out.println("\n--- insertAgendaTest() ---");

        Long medecinId = 1L; // id de test
        Mois mois = Mois.DECEMBRE;
        int annee = 2025;

        // D'abord, on vérifie si cet agenda existe déjà
        AgendaMensuel existant =
                agendaMensuelRepository.findByMedecinAndMonth(medecinId, String.valueOf(mois), annee);

        if (existant != null) {
            System.out.printf("Agenda déjà existant trouvé : ID=%d | medecinId=%d | mois=%s | annee=%d%n",
                    existant.getId(),
                    existant.getMedecinId(),
                    existant.getMois(),
                    existant.getAnnee());
            return existant;
        }

        // Sinon, on le crée
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecinId(medecinId)
                .mois(mois)
                .annee(annee)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        agendaMensuelRepository.create(agenda);

        System.out.printf("Agenda créé : ID=%d | medecinId=%d | mois=%s | annee=%d%n",
                agenda.getId(),
                agenda.getMedecinId(),
                agenda.getMois(),
                agenda.getAnnee());

        return agenda;
    }

    /**
     * Test de recherche d'un agenda par médecin + mois + année.
     */
    private static void findAgendaByMedecinAndMonthTest(Long medecinId, Mois mois, int annee) {
        System.out.println("\n--- findAgendaByMedecinAndMonthTest() ---");

        AgendaMensuel agenda = agendaMensuelRepository.findByMedecinAndMonth(medecinId, String.valueOf(mois), annee);
        if (agenda == null) {
            System.out.printf("Aucun agenda trouvé pour medecinId=%d, mois=%s, annee=%d%n",
                    medecinId, mois, annee);
        } else {
            System.out.printf("Agenda trouvé : ID=%d | medecinId=%d | mois=%s | annee=%d%n",
                    agenda.getId(),
                    agenda.getMedecinId(),
                    agenda.getMois(),
                    agenda.getAnnee());
        }
    }

    /**
     * Création d'une journée de travail liée à un agenda.
     */
    private static DetailJournee insertDetailJourneeTest(AgendaMensuel agenda) {
        System.out.println("\n--- insertDetailJourneeTest() ---");

        DetailJournee journee = DetailJournee.builder()
                .agendaId(agenda.getId())
                .dateJour(LocalDate.of(agenda.getAnnee(), 12, 10))
                .heureDebutTravaillee(LocalTime.of(9, 0))
                .heureFinTravaillee(LocalTime.of(17, 0))
                .etatJour(StatutJournee.OUVERT)
                .commentaire("Journée de test")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        detailJourneeRepository.create(journee);

        System.out.printf("Journée créée : ID=%d | agendaId=%d | date=%s | etat=%s%n",
                journee.getId(),
                journee.getAgendaId(),
                journee.getDateJour(),
                journee.getEtatJour());

        return journee;
    }

    /**
     * Lister toutes les journées d'un agenda.
     */
    private static void findJoursByAgendaTest(Long agendaId) {
        System.out.println("\n--- findJoursByAgendaTest() ---");

        List<DetailJournee> jours = detailJourneeRepository.findByAgendaId(agendaId);
        if (jours.isEmpty()) {
            System.out.println("Aucune journée trouvée pour agendaId=" + agendaId);
            return;
        }

        for (DetailJournee j : jours) {
            System.out.printf("Journee: ID=%d | date=%s | debut=%s | fin=%s | etat=%s%n",
                    j.getId(),
                    j.getDateJour(),
                    j.getHeureDebutTravaillee(),
                    j.getHeureFinTravaillee(),
                    j.getEtatJour());
        }
    }

    /**
     * Mise à jour de la journée : changement d'horaires + statut.
     */
    private static void updateDetailJourneeTest(DetailJournee journee) {
        System.out.println("\n--- updateDetailJourneeTest() ---");

        if (journee == null || journee.getId() == null) {
            System.out.println("Journee null ou sans id, impossible de tester l'update.");
            return;
        }

        journee.setHeureDebutTravaillee(LocalTime.of(8, 30));
        journee.setHeureFinTravaillee(LocalTime.of(16, 30));
        journee.setEtatJour(StatutJournee.FERME);
        journee.setModifiePar("TEST_UPDATE");

        detailJourneeRepository.update(journee);

        DetailJournee jUpdated = detailJourneeRepository.findById(journee.getId());
        System.out.printf("Journee mise à jour : ID=%d | date=%s | debut=%s | fin=%s | etat=%s%n",
                jUpdated.getId(),
                jUpdated.getDateJour(),
                jUpdated.getHeureDebutTravaillee(),
                jUpdated.getHeureFinTravaillee(),
                jUpdated.getEtatJour());
    }
}
