package ma.dentalTech.repository.test.ModuleCaisse;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.entities.situationFinanciere.SituationFinanciere;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestModuleCaisseRepositories {

    private static FactureRepository factureRepository;
    private static RevenuesRepository revenuesRepository;
    private static ChargesRepository chargesRepository;
    private static SituationFinanciereRepository situationRepository;

    public static void main(String[] args) {

        // Récupération des beans via ApplicationContext
        factureRepository    = ApplicationContext.getBean(FactureRepository.class);
        revenuesRepository   = ApplicationContext.getBean(RevenuesRepository.class);
        chargesRepository    = ApplicationContext.getBean(ChargesRepository.class);
        situationRepository  = ApplicationContext.getBean(SituationFinanciereRepository.class);

        System.out.println("===== TESTS MODULE CAISSE - REPOSITORIES (AYA) =====");

        testFactureRepository();
        testRevenuesRepository();
        testChargesRepository();
        testSituationFinanciereRepository();

        System.out.println("===== FIN DES TESTS MODULE CAISSE =====");
    }

    // =========================================================
    // 1. TEST FACTURE
    // =========================================================
    private static void testFactureRepository() {
        System.out.println("\n=== [FactureRepository] INSERT / SELECT / UPDATE / DELETE ===");

        try {
            // ---------- INSERT ----------
            Facture f = new Facture();
            f.setConsultationId(null);           // FK nullable, on laisse null pour le test
            f.setDateFacture(LocalDate.now());
            f.setTotalFacture(1000.0);
            f.setTotalPaye(500.0);              // statut géré par défaut côté BD ou entité

            factureRepository.create(f);
            Long id = f.getId();
            System.out.println("INSERT -> Facture créée avec id = " + id);

            // ---------- SELECT ----------
            Facture fromDb = factureRepository.findById(id);
            if (fromDb == null) {
                System.out.println("SELECT -> ⚠ Aucune facture trouvée pour id = " + id);
                return;
            }
            System.out.println("SELECT -> Facture : id=" + fromDb.getId()
                    + ", total=" + fromDb.getTotalFacture()
                    + ", payé=" + fromDb.getTotalPaye());

            // ---------- UPDATE ----------
            fromDb.setTotalPaye(fromDb.getTotalFacture()); // on simule un paiement complet
            factureRepository.update(fromDb);

            Facture afterUpdate = factureRepository.findById(id);
            System.out.println("UPDATE -> Après mise à jour : total=" + afterUpdate.getTotalFacture()
                    + ", payé=" + afterUpdate.getTotalPaye());

            // ---------- DELETE ----------
            factureRepository.deleteById(id);
            Facture afterDelete = factureRepository.findById(id);

            if (afterDelete == null) {
                System.out.println("DELETE -> Facture supprimée avec succès (id=" + id + ")");
            } else {
                System.out.println("DELETE -> ⚠ Facture toujours présente après suppression (id=" + id + ")");
            }

        } catch (Exception e) {
            System.err.println("ERREUR dans testFactureRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================
    // 2. TEST REVENUS
    // =========================================================
    private static void testRevenuesRepository() {
        System.out.println("\n=== [RevenuesRepository] INSERT / SELECT / UPDATE / DELETE ===");

        try {
            // ---------- INSERT ----------
            Revenues r = new Revenues();
            r.setCabinetId(1L); // doit correspondre à un cabinet existant dans ta BD (seed.sql : id=1)
            r.setTitre("Location salle formation");
            r.setDescription("Revenu exceptionnel - formation implantologie");
            r.setMontant(2000.0);
            r.setDateRevenu(LocalDateTime.now());

            revenuesRepository.create(r);
            Long id = r.getId();
            System.out.println("INSERT -> Revenu créé avec id = " + id);

            // ---------- SELECT ----------
            Revenues fromDb = revenuesRepository.findById(id);
            if (fromDb == null) {
                System.out.println("SELECT -> ⚠ Aucun revenu trouvé pour id = " + id);
                return;
            }
            System.out.println("SELECT -> Revenu : id=" + fromDb.getId()
                    + ", titre=" + fromDb.getTitre()
                    + ", montant=" + fromDb.getMontant());

            // ---------- UPDATE ----------
            fromDb.setMontant(fromDb.getMontant() + 300.0);
            fromDb.setDescription(fromDb.getDescription() + " (ajusté)");

            revenuesRepository.update(fromDb);

            Revenues afterUpdate = revenuesRepository.findById(id);
            System.out.println("UPDATE -> Après mise à jour : montant=" + afterUpdate.getMontant()
                    + ", description=" + afterUpdate.getDescription());

            // ---------- DELETE ----------
            revenuesRepository.deleteById(id);
            Revenues afterDelete = revenuesRepository.findById(id);

            if (afterDelete == null) {
                System.out.println("DELETE -> Revenu supprimé avec succès (id=" + id + ")");
            } else {
                System.out.println("DELETE -> ⚠ Revenu toujours présent après suppression (id=" + id + ")");
            }

        } catch (Exception e) {
            System.err.println("ERREUR dans testRevenuesRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================
    // 3. TEST CHARGES
    // =========================================================
    private static void testChargesRepository() {
        System.out.println("\n=== [ChargesRepository] INSERT / SELECT / UPDATE / DELETE ===");

        try {
            // ---------- INSERT ----------
            Charges c = new Charges();
            c.setCabinetId(1L); // cabinet existant (id=1 dans le seed.sql)
            c.setTitre("Facture eau");
            c.setDescription("Eau mois courant");
            c.setMontant(600.0);
            c.setDateCharge(LocalDateTime.now());

            chargesRepository.create(c);
            Long id = c.getId();
            System.out.println("INSERT -> Charge créée avec id = " + id);

            // ---------- SELECT ----------
            Charges fromDb = chargesRepository.findById(id);
            if (fromDb == null) {
                System.out.println("SELECT -> ⚠ Aucune charge trouvée pour id = " + id);
                return;
            }
            System.out.println("SELECT -> Charge : id=" + fromDb.getId()
                    + ", titre=" + fromDb.getTitre()
                    + ", montant=" + fromDb.getMontant());

            // ---------- UPDATE ----------
            fromDb.setMontant(fromDb.getMontant() + 100.0);
            fromDb.setDescription(fromDb.getDescription() + " (ajustée)");

            chargesRepository.update(fromDb);

            Charges afterUpdate = chargesRepository.findById(id);
            System.out.println("UPDATE -> Après mise à jour : montant=" + afterUpdate.getMontant()
                    + ", description=" + afterUpdate.getDescription());

            // ---------- DELETE ----------
            chargesRepository.deleteById(id);
            Charges afterDelete = chargesRepository.findById(id);

            if (afterDelete == null) {
                System.out.println("DELETE -> Charge supprimée avec succès (id=" + id + ")");
            } else {
                System.out.println("DELETE -> ⚠ Charge toujours présente après suppression (id=" + id + ")");
            }

        } catch (Exception e) {
            System.err.println("ERREUR dans testChargesRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================
    // 4. TEST SITUATION FINANCIERE
    // =========================================================
    private static void testSituationFinanciereRepository() {
        System.out.println("\n=== [SituationFinanciereRepository] INSERT / SELECT / UPDATE / DELETE ===");

        try {
            // IMPORTANT : dossierId doit exister dans la table dossier_medical
            Long dossierId = 1L; // adapte si besoin (si FK échoue)

            // Avant d'insérer, on supprime toute SF existante pour ce dossier (dossier_id UNIQUE)
            var all = situationRepository.findAll();
            for (SituationFinanciere sf : all) {
                if (dossierId.equals(sf.getDossierId())) {
                    System.out.println("Une SF existe déjà pour dossierId=" + dossierId
                            + " (id=" + sf.getId() + "), suppression pour le test...");
                    situationRepository.deleteById(sf.getId());
                }
            }

            // ---------- INSERT ----------
            SituationFinanciere sfNew = new SituationFinanciere();
            sfNew.setDossierId(dossierId);
            sfNew.setMedecinId(null);
            sfNew.setTotalDesActes(1500.0);
            sfNew.setTotalPaye(1000.0);
            sfNew.setCredit(500.0);
            // statut : on laisse la valeur par défaut

            situationRepository.create(sfNew);
            Long id = sfNew.getId();
            System.out.println("INSERT -> SF créée avec id = " + id);

            // ---------- SELECT ----------
            SituationFinanciere fromDb = situationRepository.findById(id);
            if (fromDb == null) {
                System.out.println("SELECT -> ⚠ Aucune SF trouvée pour id = " + id);
                return;
            }

            System.out.println("SELECT -> SF : id=" + fromDb.getId()
                    + ", dossierId=" + fromDb.getDossierId()
                    + ", totalActes=" + fromDb.getTotalDesActes()
                    + ", totalPaye=" + fromDb.getTotalPaye()
                    + ", credit=" + fromDb.getCredit());

            // ---------- UPDATE ----------
            fromDb.setTotalPaye(fromDb.getTotalDesActes());
            fromDb.setCredit(0.0);

            situationRepository.update(fromDb);

            SituationFinanciere afterUpdate = situationRepository.findById(id);
            System.out.println("UPDATE -> Après mise à jour : totalActes=" + afterUpdate.getTotalDesActes()
                    + ", totalPaye=" + afterUpdate.getTotalPaye()
                    + ", credit=" + afterUpdate.getCredit());

            // ---------- DELETE ----------
            situationRepository.deleteById(id);
            SituationFinanciere afterDelete = situationRepository.findById(id);

            if (afterDelete == null) {
                System.out.println("DELETE -> Situation financière supprimée avec succès (id=" + id + ")");
            } else {
                System.out.println("DELETE -> ⚠ SF toujours présente après suppression (id=" + id + ")");
            }

        } catch (Exception e) {
            System.err.println("ERREUR dans testSituationFinanciereRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
