package ma.dentalTech.tests;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.rdv.RDV;
import ma.dentalTech.repository.modules.rdv.api.RdvRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestRdvRepository {

    private static RdvRepository rdvRepository;

    public static void main(String[] args) {
        // Récupération du bean RDV depuis l'ApplicationContext
        rdvRepository = ApplicationContext.getBean(RdvRepository.class);

        System.out.println("=== TEST RDV REPOSITORY ===");

        // 1) INSERT
        RDV rdvCree = insertProcessTest();

        // 2) SELECT ALL
        findAllProcessTest();

        // 3) UPDATE
        updateProcessTest(rdvCree);

        // 4) DELETE
        deleteProcessTest(rdvCree);

        System.out.println("=== FIN DES TESTS RDV ===");
    }

    /**
     * Test de création d'un RDV (INSERT).
     */
    private static RDV insertProcessTest() {
        System.out.println("\n--- insertProcessTest() ---");

        RDV rdv = RDV.builder()
                .date(LocalDate.now().plusDays(1))
                .heure(LocalTime.of(10, 0))
                .motif("Consultation de contrôle")
                .status(EtatRendezVous.PREVU)
                .noteMedecin("RDV de test auto")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        rdvRepository.create(rdv);

        System.out.println("RDV créé avec id = " + rdv.getId());
        return rdv;
    }

    /**
     * Test de lecture de tous les RDV (SELECT * FROM rdv).
     */
    private static void findAllProcessTest() {
        System.out.println("\n--- findAllProcessTest() ---");

        List<RDV> rdvs = rdvRepository.findAll();
        if (rdvs.isEmpty()) {
            System.out.println("Aucun RDV en base.");
            return;
        }

        for (RDV r : rdvs) {
            System.out.printf("ID=%d | date=%s | heure=%s | statut=%s | motif=%s%n",
                    r.getId(),
                    r.getDate(),
                    r.getHeure(),
                    r.getStatus(),
                    r.getMotif());
        }
    }

    /**
     * Test de mise à jour d'un RDV (UPDATE).
     */
    private static void updateProcessTest(RDV rdv) {
        System.out.println("\n--- updateProcessTest() ---");

        if (rdv == null || rdv.getId() == null) {
            System.out.println("RDV null ou sans id, impossible de tester l'update.");
            return;
        }

        // On modifie quelques champs
        rdv.setMotif("Consultation modifiée (test UPDATE)");
        rdv.setStatus(EtatRendezVous.CONFIRME);
        rdv.setNoteMedecin((rdv.getNoteMedecin() != null ? rdv.getNoteMedecin() + " | " : "") + "Confirmé pour test.");

        rdvRepository.update(rdv);

        // Relire en base pour vérifier
        RDV rdvUpdated = rdvRepository.findById(rdv.getId());
        System.out.printf("RDV mis à jour : ID=%d | statut=%s | motif=%s%n",
                rdvUpdated.getId(),
                rdvUpdated.getStatus(),
                rdvUpdated.getMotif());
    }

    /**
     * Test de suppression d'un RDV (DELETE).
     */
    private static void deleteProcessTest(RDV rdv) {
        System.out.println("\n--- deleteProcessTest() ---");

        if (rdv == null || rdv.getId() == null) {
            System.out.println("RDV null ou sans id, impossible de tester la suppression.");
            return;
        }

        Long id = rdv.getId();
        rdvRepository.deleteById(id);

        RDV rdvAfterDelete = rdvRepository.findById(id);
        if (rdvAfterDelete == null) {
            System.out.println("RDV supprimé avec succès, id=" + id);
        } else {
            System.out.println("⚠ RDV toujours présent après delete, id=" + id);
        }
    }
}
