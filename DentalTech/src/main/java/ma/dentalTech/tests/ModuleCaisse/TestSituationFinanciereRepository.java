package ma.dentalTech.tests.ModuleCaisse;

import ma.dentalTech.entities.situationFinanciere.SituationFinanciere;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.SituationFinanciereRepositoryJdbcImpl;

import java.util.List;

public class TestSituationFinanciereRepository {

    public static void main(String[] args) {
        System.out.println(" TEST SituationFinanciereRepository /module caisse -aya");

        SituationFinanciereRepository repo = new SituationFinanciereRepositoryJdbcImpl();

        try {
            // Test findAll()
            List<SituationFinanciere> toutes = repo.findAll();
            System.out.println("Nombre de situations financières : " + toutes.size());

            if (!toutes.isEmpty()) {
                SituationFinanciere s = toutes.get(0);
                System.out.println("Première situation : " + s);
            }

            // Test findLast()
            SituationFinanciere last = repo.findLast();
            System.out.println("Dernière situation (findLast) : " + last);

        } catch (Exception e) {
            System.out.println("Erreur pendant le test de SituationFinanciereRepository : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== FIN TEST SituationFinanciereRepository ===");
    }
}
