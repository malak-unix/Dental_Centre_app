package ma.dentalTech.repository.test.ModuleCaisse;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;

import java.time.LocalDate;

public class TestFactureRepository {

    private static FactureRepository factureRepository;

    public static void main(String[] args) {

        factureRepository = ApplicationContext.getBean(FactureRepository.class);

        System.out.println("=== TEST FactureRepository / module caisse - aya ===");

        testInsertSelectUpdateDelete();

        System.out.println("=== FIN TEST FactureRepository ===");
    }

    private static void testInsertSelectUpdateDelete() {

        try {
            // ---------- INSERT ----------
            System.out.println("\n--- INSERT Facture ---");

            Facture facture = Facture.builder()
                    .consultationId(null) // FK nullable, on ne force pas une consultation ici
                    .dateFacture(LocalDate.now())
                    .totalFacture(1000.0)
                    .totalPaye(500.0)
                    .statut(StatutFacture.PARTIEL)
                    .build();

            factureRepository.create(facture);
            Long id = facture.getId();
            System.out.println("Facture insérée avec id = " + id);

            // ---------- SELECT ----------
            System.out.println("\n--- SELECT Facture (findById) ---");
            Facture fromDb = factureRepository.findById(id);
            if (fromDb != null) {
                System.out.println("Facture trouvée : id=" + fromDb.getId()
                        + ", total=" + fromDb.getTotalFacture()
                        + ", payé=" + fromDb.getTotalPaye()
                        + ", statut=" + fromDb.getStatut());
            } else {
                System.out.println("⚠ Aucune facture trouvée pour id = " + id);
                return;
            }

            // ---------- UPDATE ----------
            System.out.println("\n--- UPDATE Facture ---");
            fromDb.setTotalPaye(fromDb.getTotalFacture());
            fromDb.setStatut(StatutFacture.PAYEE);

            factureRepository.update(fromDb);

            Facture afterUpdate = factureRepository.findById(id);
            System.out.println("Après update : total=" + afterUpdate.getTotalFacture()
                    + ", payé=" + afterUpdate.getTotalPaye()
                    + ", statut=" + afterUpdate.getStatut());

            // ---------- DELETE ----------
            System.out.println("\n--- DELETE Facture ---");
            factureRepository.deleteById(id);

            Facture afterDelete = factureRepository.findById(id);
            if (afterDelete == null) {
                System.out.println("Facture supprimée avec succès, id=" + id);
            } else {
                System.out.println("⚠ Facture toujours présente après delete, id=" + id);
            }

        } catch (Exception e) {
            System.err.println("Erreur pendant le test de FactureRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
