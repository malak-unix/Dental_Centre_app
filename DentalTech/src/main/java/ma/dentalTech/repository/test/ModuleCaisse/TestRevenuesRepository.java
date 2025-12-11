package ma.dentalTech.repository.test.ModuleCaisse;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;

import java.time.LocalDateTime;

public class TestRevenuesRepository {

    private static RevenuesRepository revenuesRepository;

    public static void main(String[] args) {

        revenuesRepository = ApplicationContext.getBean(RevenuesRepository.class);

        System.out.println("=== TEST RevenuesRepository / module caisse - aya ===");

        testInsertSelectUpdateDelete();

        System.out.println("=== FIN TEST RevenuesRepository ===");
    }

    private static void testInsertSelectUpdateDelete() {

        try {
            // ---------- INSERT ----------
            System.out.println("\n--- INSERT Revenus ---");

            Revenues revenu = Revenues.builder()
                    .cabinetId(1L) // supposé exister dans la table cabinet_medical
                    .titre("Location de salle pour formation")
                    .description("Revenu exceptionnel - formation implantologie")
                    .montant(2000.0)
                    .dateRevenu(LocalDateTime.now())
                    .build();

            revenuesRepository.create(revenu);
            Long id = revenu.getId();
            System.out.println("Revenu inséré avec id = " + id);

            // ---------- SELECT ----------
            System.out.println("\n--- SELECT Revenus (findById) ---");
            Revenues fromDb = revenuesRepository.findById(id);
            if (fromDb != null) {
                System.out.println("Revenu trouvé : id=" + fromDb.getId()
                        + ", titre=" + fromDb.getTitre()
                        + ", montant=" + fromDb.getMontant());
            } else {
                System.out.println("⚠ Aucun revenu trouvé pour id = " + id);
                return;
            }

            // ---------- UPDATE ----------
            System.out.println("\n--- UPDATE Revenus ---");
            fromDb.setMontant(fromDb.getMontant() + 500.0);
            fromDb.setDescription(fromDb.getDescription() + " (ajusté)");

            revenuesRepository.update(fromDb);

            Revenues afterUpdate = revenuesRepository.findById(id);
            System.out.println("Après update : montant=" + afterUpdate.getMontant()
                    + ", description=" + afterUpdate.getDescription());

            // ---------- DELETE ----------
            System.out.println("\n--- DELETE Revenus ---");
            revenuesRepository.deleteById(id);

            Revenues afterDelete = revenuesRepository.findById(id);
            if (afterDelete == null) {
                System.out.println("Revenu supprimé avec succès, id=" + id);
            } else {
                System.out.println("⚠ Revenu toujours présent après delete, id=" + id);
            }

        } catch (Exception e) {
            System.err.println("Erreur pendant le test de RevenuesRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
