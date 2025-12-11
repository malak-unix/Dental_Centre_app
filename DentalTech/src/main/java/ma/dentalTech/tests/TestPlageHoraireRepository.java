package ma.dentalTech.tests;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.entities.plageHoraire.PlageHoraire;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;
import ma.dentalTech.repository.modules.plageHoraire.api.PlageHoraireRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestPlageHoraireRepository {

    private static PlageHoraireRepository plageHoraireRepository;
    private static AgendaMensuelRepository agendaMensuelRepository;
    private static DetailJourneeRepository detailJourneeRepository;

    public static void main(String[] args) {

        plageHoraireRepository = ApplicationContext.getBean(PlageHoraireRepository.class);
        agendaMensuelRepository = ApplicationContext.getBean(AgendaMensuelRepository.class);
        detailJourneeRepository = ApplicationContext.getBean(DetailJourneeRepository.class);

        System.out.println("=== TEST PLAGE_HORAIRE REPOSITORY ===");

        // 0) On prépare d'abord une DetailJournee de test (avec un AgendaMensuel valide)
        DetailJournee journeeTest = prepareDetailJourneeForTest();
        Long detailJourneeId = journeeTest.getId();

        // 1) INSERT
        PlageHoraire ph = insertProcessTest(detailJourneeId);

        // 2) SELECT (findByDetailJourneeId)
        findByDetailJourneeTest(detailJourneeId);

        // 3) UPDATE (changer dispo)
        updateProcessTest(ph);

        // 4) DELETE
        deleteProcessTest(ph);

        System.out.println("=== FIN TEST PLAGE_HORAIRE ===");
    }

    /**
     * Prépare un AgendaMensuel + DetailJournee de test pour garantir
     * que la clé étrangère detail_journee_id pointe vers une ligne valide.
     */
    private static DetailJournee prepareDetailJourneeForTest() {
        System.out.println("\n--- prepareDetailJourneeForTest() ---");

        Long medecinId = 1L;
        Mois mois = Mois.FEVRIER;
        int annee = 2030;

        // On nettoie d'abord un éventuel ancien agenda de test
        AgendaMensuel existant =
                agendaMensuelRepository.findByMedecinAndMonth(medecinId, String.valueOf(mois), annee);

        if (existant != null) {
            agendaMensuelRepository.deleteById(existant.getId());
        }

        // Création d'un agenda mensuel de test
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecinId(medecinId)
                .mois(mois)
                .annee(annee)
                .creePar("TEST_PLAGE")
                .modifiePar("TEST_PLAGE")
                .build();

        agendaMensuelRepository.create(agenda);

        System.out.printf(
                "Agenda de test créé : ID=%d | medecinId=%d | mois=%s | annee=%d%n",
                agenda.getId(),
                agenda.getMedecinId(),
                agenda.getMois(),
                agenda.getAnnee()
        );

        // Création d'une journée de travail rattachée à cet agenda
        DetailJournee journee = DetailJournee.builder()
                .agendaId(agenda.getId())
                .dateJour(LocalDate.of(2030, 2, 10))
                .heureDebutTravaillee(LocalTime.of(9, 0))
                .heureFinTravaillee(LocalTime.of(17, 0))
                .etatJour(StatutJournee.OUVERT)
                .commentaire("Journée test pour PlageHoraire")
                .creePar("TEST_PLAGE")
                .modifiePar("TEST_PLAGE")
                .build();

        detailJourneeRepository.create(journee);

        System.out.printf(
                "DetailJournee de test créée : ID=%d | agendaId=%d | date=%s%n",
                journee.getId(),
                journee.getAgendaId(),
                journee.getDateJour()
        );

        return journee;
    }

    private static PlageHoraire insertProcessTest(Long detailJourneeId) {
        System.out.println("\n--- insertProcessTest() ---");

        PlageHoraire ph = PlageHoraire.builder()
                .detailJourneeId(detailJourneeId)
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(9, 30))
                .disponible(true)
                .creePar("TEST_PLAGE")
                .modifiePar("TEST_PLAGE")
                .build();

        plageHoraireRepository.create(ph);

        System.out.println("Plage créée avec id=" + ph.getId());
        return ph;
    }

    private static void findByDetailJourneeTest(Long detailJourneeId) {
        System.out.println("\n--- findByDetailJourneeTest() ---");

        List<PlageHoraire> list = plageHoraireRepository.findByDetailJourneeId(detailJourneeId);
        System.out.println("Nb de plages pour detailJourneeId=" + detailJourneeId + " : " + list.size());

        for (PlageHoraire ph : list) {
            System.out.printf("ID=%d | debut=%s | fin=%s | dispo=%s%n",
                    ph.getId(),
                    ph.getHeureDebut(),
                    ph.getHeureFin(),
                    ph.getDisponible());
        }
    }

    private static void updateProcessTest(PlageHoraire ph) {
        System.out.println("\n--- updateProcessTest() ---");

        if (ph == null || ph.getId() == null) {
            System.out.println("Plage null ou sans id, impossible de tester l'update.");
            return;
        }

        ph.setDisponible(false);
        ph.setModifiePar("TEST_PLAGE_UPDATE");

        plageHoraireRepository.update(ph);

        PlageHoraire updated = plageHoraireRepository.findById(ph.getId());
        System.out.printf("Plage mise à jour : ID=%d | dispo=%s%n",
                updated.getId(),
                updated.getDisponible());
    }

    private static void deleteProcessTest(PlageHoraire ph) {
        System.out.println("\n--- deleteProcessTest() ---");

        if (ph == null || ph.getId() == null) {
            System.out.println("Plage null ou sans id, impossible de tester la suppression.");
            return;
        }

        Long id = ph.getId();
        plageHoraireRepository.deleteById(id);

        PlageHoraire afterDelete = plageHoraireRepository.findById(id);
        if (afterDelete == null) {
            System.out.println("Plage supprimée avec succès, id=" + id);
        } else {
            System.out.println("⚠ Plage toujours présente après delete, id=" + id);
        }
    }
}
