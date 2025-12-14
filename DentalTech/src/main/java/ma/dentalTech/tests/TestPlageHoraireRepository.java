package ma.dentalTech.tests;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.plageHoraire.PlageHoraire;
import ma.dentalTech.repository.modules.plageHoraire.api.PlageHoraireRepository;

import java.time.LocalTime;
import java.util.List;

public class TestPlageHoraireRepository {

    private static PlageHoraireRepository plageHoraireRepository;

    public static void main(String[] args) {

        plageHoraireRepository = ApplicationContext.getBean(PlageHoraireRepository.class);

        System.out.println("=== TEST PLAGE_HORAIRE REPOSITORY ===");

        // ATTENTION : ici je suppose qu'il existe déjà un DetailJournee avec id=1
        Long detailJourneeId = 1L;

        // 1) INSERT
        PlageHoraire ph = insertProcessTest(detailJourneeId);

        // 2) FIND ALL BY JOURNEE
        findByDetailJourneeTest(detailJourneeId);

        // 3) UPDATE (changer dispo)
        updateProcessTest(ph);

        // 4) DELETE
        deleteProcessTest(ph);

        System.out.println("=== FIN TEST PLAGE_HORAIRE ===");
    }

    private static PlageHoraire insertProcessTest(Long detailJourneeId) {
        System.out.println("\n--- insertProcessTest() ---");

        PlageHoraire ph = PlageHoraire.builder()
                .detailJourneeId(detailJourneeId)
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(9, 30))
                .disponible(true)
                .creePar("TEST")
                .modifiePar("TEST")
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
        ph.setModifiePar("TEST_UPDATE");

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
