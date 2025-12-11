package ma.dentalTech.repository.test.ModuleCaisse;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;

import java.time.LocalDateTime;

public class TestChargesRepository {

    private static ChargesRepository chargesRepository;

    public static void main(String[] args) {

        chargesRepository = ApplicationContext.getBean(ChargesRepository.class);

        System.out.println("=== TEST ChargesRepository / module caisse - aya ===");

        testInsertSelectUpdateDelete();

        System.out.println("=== FIN TEST ChargesRepository ===");
    }

    private static void testInsertSelectUpdateDelete() {

        try {
            System.out.println("\n--- INSERT Charge ---");

            Charges charge = Charges.builder()
                    .cabinetId(1L) // supposé exister dans cabinet_medical
                    .titre("Facture électricité")
                    .description("Electricité mois courant")
                    .montant(850.0)
                    .dateCharge(LocalDateTime.now())
                    .build();

            chargesRepository.create(charge);
            Long id = charge.getId();
            System.out.println("Charge insérée avec id = " + id);

            // ---------- SELECT ----------
            System.out.println("\n--- SELECT Charge (findById) ---");
            Charges fromDb = chargesRepository.findById(id);
            if (fromDb != null) {
                System.out.println("Charge trouvée : id=" + fromDb.getId()
                        + ", titre=" + fromDb.getTitre()
                        + ", montant=" + fromDb.getMontant());
            } else {
                System.out.println("Aucune charge trouvée pour id = " + id);
                return;
            }

            System.out.println("\n--- UPDATE Charge ---");
            fromDb.setMontant(fromDb.getMontant() + 50.0);
            fromDb.setDescription(fromDb.getDescription() + " (ajustée)");

            chargesRepository.update(fromDb);

            Charges afterUpdate = chargesRepository.findById(id);
            System.out.println("Après update : montant=" + afterUpdate.getMontant()
                    + ", description=" + afterUpdate.getDescription());

            System.out.println("\n--- DELETE Charge ---");
            chargesRepository.deleteById(id);

            Charges afterDelete = chargesRepository.findById(id);
            if (afterDelete == null) {
                System.out.println("Charge supprimée avec succès, id=" + id);
            } else {
                System.out.println("⚠ Charge toujours présente après delete, id=" + id);
            }

        } catch (Exception e) {
            System.err.println("Erreur pendant le test de ChargesRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
