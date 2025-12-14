package ma.dentalTech.tests;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestDetailJourneeRepository {

    private static DetailJourneeRepository detailJourneeRepository;
    private static AgendaMensuelRepository agendaMensuelRepository;

    public static void main(String[] args) {

        detailJourneeRepository = ApplicationContext.getBean(DetailJourneeRepository.class);
        agendaMensuelRepository = ApplicationContext.getBean(AgendaMensuelRepository.class);

        System.out.println("=== TEST DETAIL_JOURNEE REPOSITORY ===");

        // 1) INSERT (en créant d'abord un AgendaMensuel valide)
        DetailJournee journee = insertProcessTest();

        // 2) SELECT
        selectProcessTest(journee.getAgendaId());

        // 3) UPDATE
        updateProcessTest(journee);

        // 4) DELETE
        deleteProcessTest(journee);

        System.out.println("=== FIN TEST DETAIL_JOURNEE ===");
    }

    /**
     * Crée (ou recrée) un AgendaMensuel de test pour pouvoir
     * lui rattacher une DetailJournee sans casser la FK.
     */
    private static AgendaMensuel prepareAgendaForTest() {
        Long medecinId = 1L;
        Mois mois = Mois.JANVIER;
        int annee = 2030; // année de test pour éviter les conflits

        // Si un agenda existe déjà pour ce triplet, on le supprime pour ce test
        AgendaMensuel existant =
                agendaMensuelRepository.findByMedecinAndMonth(medecinId, String.valueOf(mois), annee);

        if (existant != null) {
            agendaMensuelRepository.deleteById(existant.getId());
        }

        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecinId(medecinId)
                .mois(mois)
                .annee(annee)
                .creePar("TEST_DETAIL")
                .modifiePar("TEST_DETAIL")
                .build();

        agendaMensuelRepository.create(agenda);

        System.out.printf("Agenda de test créé : ID=%d | medecinId=%d | mois=%s | annee=%d%n",
                agenda.getId(),
                agenda.getMedecinId(),
                agenda.getMois(),
                agenda.getAnnee());

        return agenda;
    }

    /**
     * INSERT DetailJournee (attachée à un AgendaMensuel valide).
     */
    private static DetailJournee insertProcessTest() {
        System.out.println("\n--- insertProcessTest() ---");

        // Préparer un agenda valide pour la FK
        AgendaMensuel agenda = prepareAgendaForTest();

        DetailJournee journee = DetailJournee.builder()
                .agendaId(agenda.getId())
                .dateJour(LocalDate.of(2030, 1, 15))
                .heureDebutTravaillee(LocalTime.of(9, 0))
                .heureFinTravaillee(LocalTime.of(17, 0))
                .etatJour(StatutJournee.OUVERT)
                .commentaire("Journée test repo DetailJournee")
                .creePar("TEST_DETAIL")
                .modifiePar("TEST_DETAIL")
                .build();

        detailJourneeRepository.create(journee);

        System.out.printf("Journee créée : ID=%d | agendaId=%d | date=%s%n",
                journee.getId(),
                journee.getAgendaId(),
                journee.getDateJour());

        return journee;
    }

    /**
     * SELECT : liste des journées pour un agenda donné.
     */
    private static void selectProcessTest(Long agendaId) {
        System.out.println("\n--- selectProcessTest() ---");

        List<DetailJournee> jours = detailJourneeRepository.findByAgendaId(agendaId);

        if (jours.isEmpty()) {
            System.out.println("Aucune journée trouvée pour agendaId=" + agendaId);
        } else {
            System.out.println("Journées trouvées pour agendaId=" + agendaId + " :");
            for (DetailJournee j : jours) {
                System.out.printf("ID=%d | date=%s | debut=%s | fin=%s | etat=%s%n",
                        j.getId(),
                        j.getDateJour(),
                        j.getHeureDebutTravaillee(),
                        j.getHeureFinTravaillee(),
                        j.getEtatJour());
            }
        }
    }

    /**
     * UPDATE : modifier la journée.
     */
    private static void updateProcessTest(DetailJournee journee) {
        System.out.println("\n--- updateProcessTest() ---");

        if (journee == null || journee.getId() == null) {
            System.out.println("Journee null ou sans id, impossible de tester l'update.");
            return;
        }

        journee.setHeureDebutTravaillee(LocalTime.of(8, 30));
        journee.setHeureFinTravaillee(LocalTime.of(16, 30));
        journee.setEtatJour(StatutJournee.FERME);
        journee.setModifiePar("TEST_DETAIL_UPDATE");

        detailJourneeRepository.update(journee);

        DetailJournee jUpdated = detailJourneeRepository.findById(journee.getId());
        System.out.printf("Journee mise à jour : ID=%d | date=%s | debut=%s | fin=%s | etat=%s%n",
                jUpdated.getId(),
                jUpdated.getDateJour(),
                jUpdated.getHeureDebutTravaillee(),
                jUpdated.getHeureFinTravaillee(),
                jUpdated.getEtatJour());
    }

    /**
     * DELETE : supprimer la journée.
     */
    private static void deleteProcessTest(DetailJournee journee) {
        System.out.println("\n--- deleteProcessTest() ---");

        if (journee == null || journee.getId() == null) {
            System.out.println("Journee null ou sans id, impossible de tester la suppression.");
            return;
        }

        Long id = journee.getId();
        detailJourneeRepository.deleteById(id);

        DetailJournee afterDelete = detailJourneeRepository.findById(id);
        if (afterDelete == null) {
            System.out.println("Journee supprimée avec succès, id=" + id);
        } else {
            System.out.println("⚠ Journee toujours présente après delete, id=" + id);
        }
    }
}
