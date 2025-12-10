package ma.dentalTech.repository.modules.caisse;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.entities.situationFinanciere.SituationFinanciere;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.FactureRepositoryJdbcImpl;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.RevenuesRepositoryJdbcImpl;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.ChargesRepositoryJdbcImpl;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.SituationFinanciereRepositoryJdbcImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class CaisseRepositoriesTest {

    public static void main(String[] args) {
        System.out.println("===== TEST MODULE CAISSE - REPOSITORIES =====");

        testFactureRepository();
        testRevenuesRepository();
        testChargesRepository();
        testSituationFinanciereRepository();

        System.out.println("===== FIN DES TESTS =====");
    }

    private static void testFactureRepository() {
        System.out.println("\n--- TEST FactureRepository ---");
        FactureRepository repo = new FactureRepositoryJdbcImpl();

        try {
            // 1) findAll()
            List<Facture> factures = repo.findAll();
            System.out.println("Nombre de factures trouvées : " + factures.size());
            if (!factures.isEmpty()) {
                Facture first = factures.get(0);
                System.out.println("Première facture : id=" + first.getId()
                        + ", total=" + first.getTotalFacture()
                        + ", payé=" + first.getTotalPaye());

                // 2) findById()
                Facture byId = repo.findById(first.getId());
                System.out.println("Facture findById(" + first.getId() + ") -> " + byId);

                // 3) Méthodes de statistiques entre deux dates
                LocalDateTime start = LocalDate.of(2000, 1, 1).atStartOfDay();
                LocalDateTime end = LocalDateTime.now();

                Double totalFactures = repo.calculateTotalFactures(start, end);
                Double totalRegle = repo.calculateTotalRegle(start, end);
                Double totalNonRegle = repo.calculateTotalNonRegle(start, end);

                System.out.println("Total factures entre " + start + " et " + end + " = " + safe(totalFactures));
                System.out.println("Total réglé     = " + safe(totalRegle));
                System.out.println("Total non réglé = " + safe(totalNonRegle));
            } else {
                System.out.println("Aucune facture trouvée. Ajoutez des données pour tester davantage.");
            }

        } catch (DaoException | RuntimeException e) {
            System.err.println("ERREUR dans testFactureRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testRevenuesRepository() {
        System.out.println("\n--- TEST RevenuesRepository ---");
        RevenuesRepository repo = new RevenuesRepositoryJdbcImpl();

        try {
            // 1) findAll()
            List<Revenues> revenus = repo.findAll();
            System.out.println("Nombre de revenus trouvés : " + revenus.size());
            if (!revenus.isEmpty()) {
                Revenues first = revenus.get(0);
                System.out.println("Premier revenu : id=" + first.getId()
                        + ", titre=" + first.getTitre()
                        + ", montant=" + first.getMontant());

                // 2) findByDateBetween() + total
                LocalDateTime start = LocalDate.of(2000, 1, 1).atStartOfDay();
                LocalDateTime end = LocalDateTime.now();

                List<Revenues> revenusPeriode = repo.findByDateBetween(start, end);
                Double totalRevenus = repo.calculateTotalOtherRevenue(start, end);

                System.out.println("Revenus entre " + start + " et " + end + " : " + revenusPeriode.size());
                System.out.println("Total revenus   = " + safe(totalRevenus));
            } else {
                System.out.println("Aucun revenu trouvé. Ajoutez des données pour tester davantage.");
            }

        } catch (DaoException | RuntimeException e) {
            System.err.println("ERREUR dans testRevenuesRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testChargesRepository() {
        System.out.println("\n--- TEST ChargesRepository ---");
        ChargesRepository repo = new ChargesRepositoryJdbcImpl();

        try {
            // 1) findAll()
            List<Charges> charges = repo.findAll();
            System.out.println("Nombre de charges trouvées : " + charges.size());
            if (!charges.isEmpty()) {
                Charges first = charges.get(0);
                System.out.println("Première charge : id=" + first.getId()
                        + ", titre=" + first.getTitre()
                        + ", montant=" + first.getMontant());

                // 2) findByDateBetween() + total
                LocalDateTime start = LocalDate.of(2000, 1, 1).atStartOfDay();
                LocalDateTime end = LocalDateTime.now();

                List<Charges> chargesPeriode = repo.findByDateBetween(start, end);
                Double totalCharges = repo.calculateTotalCharges(start, end);

                System.out.println("Charges entre " + start + " et " + end + " : " + chargesPeriode.size());
                System.out.println("Total charges   = " + safe(totalCharges));
            } else {
                System.out.println("Aucune charge trouvée. Ajoutez des données pour tester davantage.");
            }

        } catch (DaoException | RuntimeException e) {
            System.err.println("ERREUR dans testChargesRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSituationFinanciereRepository() {
        System.out.println("\n--- TEST SituationFinanciereRepository ---");
        SituationFinanciereRepository repo = new SituationFinanciereRepositoryJdbcImpl();

        try {
            // 1) findAll()
            List<SituationFinanciere> liste = repo.findAll();
            System.out.println("Nombre de situations financières : " + liste.size());
            if (!liste.isEmpty()) {
                SituationFinanciere first = liste.get(0);
                System.out.println("Première situation : id=" + first.getId()
                        + ", totalActes=" + first.getTotalDesActes()
                        + ", totalPaye=" + first.getTotalPaye()
                        + ", credit=" + first.getCredit());

                // 2) findLast()
                SituationFinanciere last = repo.findLast();
                System.out.println("Dernière situation financière (findLast) : " + last);
            } else {
                System.out.println("Aucune situation financière trouvée. Ajoutez des données pour tester davantage.");
            }

        } catch (DaoException | RuntimeException e) {
            System.err.println("ERREUR dans testSituationFinanciereRepository : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static double safe(Double value) {
        return value != null ? value : 0.0;
    }
}
