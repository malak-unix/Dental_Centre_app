package ma.dentalTech.repository.test.ModuleCaisse;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.entities.situationFinanciere.SituationFinanciere;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;

public class TestSituationFinanciereRepository {

    private static SituationFinanciereRepository situationRepository;

    public static void main(String[] args) {

        situationRepository = ApplicationContext.getBean(SituationFinanciereRepository.class);

        System.out.println("=== TEST SituationFinanciereRepository / module caisse - aya ===");

        testInsertSelectUpdateDelete();

        System.out.println("=== FIN TEST SituationFinanciereRepository ===");
    }

    private static void testInsertSelectUpdateDelete() {

        try {
            // ----- Choix d'un dossier existant (à adapter selon ta BD) -----
            Long dossierId = 1L;   // TODO : si erreur FK, mettre l'id d'un dossier_medical existant

            // ---------- (Sécurité) : vérifier si une situation existe déjà pour ce dossier ----------
            // Comme dossier_id est UNIQUE, on supprime l'existante avant de créer la nouvelle
            System.out.println("\n--- Vérification situation existante pour dossierId=" + dossierId + " ---");
            var allSituations = situationRepository.findAll();
            for (SituationFinanciere sf : allSituations) {
                if (dossierId.equals(sf.getDossierId())) {
                    System.out.println("Une SF existe déjà pour dossierId=" + dossierId
                            + " (id=" + sf.getId() + "), on la supprime pour le test.");
                    situationRepository.deleteById(sf.getId());
                }
            }

            // ---------- INSERT ----------
            System.out.println("\n--- INSERT SituationFinanciere ---");

            SituationFinanciere sfNew = SituationFinanciere.builder()
                    .dossierId(dossierId)
                    .medecinId(null)           // facultatif
                    .totalDesActes(1500.0)
                    .totalPaye(1000.0)
                    .credit(500.0)
                    .statut(StatutSituationFinanciere.EN_CREANCE)
                    .build();

            situationRepository.create(sfNew);
            Long id = sfNew.getId();
            System.out.println("SF insérée avec id = " + id);

            // ---------- SELECT ----------
            System.out.println("\n--- SELECT SituationFinanciere (findById) ---");
            SituationFinanciere fromDb = situationRepository.findById(id);
            if (fromDb != null) {
                System.out.println("SF trouvée : id=" + fromDb.getId()
                        + ", dossierId=" + fromDb.getDossierId()
                        + ", totalActes=" + fromDb.getTotalDesActes()
                        + ", totalPaye=" + fromDb.getTotalPaye()
                        + ", credit=" + fromDb.getCredit()
                        + ", statut=" + fromDb.getStatut());
            } else {
                System.out.println("⚠ Aucune SF trouvée pour id = " + id);
                return;
            }

            // ---------- UPDATE ----------
            System.out.println("\n--- UPDATE SituationFinanciere ---");
            fromDb.setTotalPaye(fromDb.getTotalDesActes());
            fromDb.setCredit(0.0);
            fromDb.setStatut(StatutSituationFinanciere.NORMAL);

            situationRepository.update(fromDb);

            SituationFinanciere afterUpdate = situationRepository.findById(id);
            System.out.println("Après update : totalActes=" + afterUpdate.getTotalDesActes()
                    + ", totalPaye=" + afterUpdate.getTotalPaye()
                    + ", credit=" + afterUpdate.getCredit()
                    + ", statut=" + afterUpdate.getStatut());

            // ---------- DELETE ----------
            System.out.println("\n--- DELETE SituationFinanciere ---");
            situationRepository.deleteById(id);

            SituationFinanciere afterDelete = situationRepository.findById(id);
            if (afterDelete == null) {
                System.out.println("Situation financière supprimée avec succès, id=" + id);
            } else {
                System.out.println("⚠ SF toujours présente après delete, id=" + id);
            }

        } catch (Exception e) {
            System.err.println("Erreur pendant le test de SituationFinanciereRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
