package ma.dentalTech.tests;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.listeDattente.ListeAttente;
import ma.dentalTech.repository.modules.listeAttente.api.ListeAttenteRepository;

import java.util.List;

public class TestListeAttenteRepository {

    private static ListeAttenteRepository listeAttenteRepository;

    public static void main(String[] args) {

        // Récupération du bean depuis l'ApplicationContext
        listeAttenteRepository = ApplicationContext.getBean(ListeAttenteRepository.class);

        System.out.println("=== TEST LISTE_ATTENTE REPOSITORY ===");

        // 1) INSERT
        ListeAttente listeCree = insertProcessTest();

        // 2) SELECT ALL
        findAllProcessTest();

        // 3) UPDATE
        updateProcessTest(listeCree);

        // 4) FIND BY NOM
        findByNomProcessTest(listeCree.getNomListe());

        // 5) DELETE
        deleteProcessTest(listeCree);

        System.out.println("=== FIN DES TESTS LISTE_ATTENTE ===");
    }

    /**
     * Test de création d'une liste d'attente.
     */
    private static ListeAttente insertProcessTest() {
        System.out.println("\n--- insertProcessTest() ---");

        ListeAttente liste = ListeAttente.builder()
                .nomListe("Liste Urgences TEST")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        listeAttenteRepository.create(liste);

        System.out.println("Liste d'attente créée avec id = " + liste.getId());
        return liste;
    }

    /**
     * Test de lecture de toutes les listes d'attente.
     */
    private static void findAllProcessTest() {
        System.out.println("\n--- findAllProcessTest() ---");

        List<ListeAttente> listes = listeAttenteRepository.findAll();
        if (listes.isEmpty()) {
            System.out.println("Aucune liste d'attente en base.");
            return;
        }

        for (ListeAttente l : listes) {
            System.out.printf("ID=%d | nom=%s | creePar=%s%n",
                    l.getId(),
                    l.getNomListe(),
                    l.getCreePar());
        }
    }

    /**
     * Test de mise à jour d'une liste d'attente.
     */
    private static void updateProcessTest(ListeAttente liste) {
        System.out.println("\n--- updateProcessTest() ---");

        if (liste == null || liste.getId() == null) {
            System.out.println("Liste null ou sans id, impossible de tester l'update.");
            return;
        }

        liste.setNomListe(liste.getNomListe() + " (modifiée)");
        liste.setModifiePar("TEST_UPDATE");

        listeAttenteRepository.update(liste);

        ListeAttente listeUpdated = listeAttenteRepository.findById(liste.getId());
        System.out.printf("Liste mise à jour : ID=%d | nom=%s | modifiePar=%s%n",
                listeUpdated.getId(),
                listeUpdated.getNomListe(),
                listeUpdated.getModifiePar());
    }

    /**
     * Test de recherche par nom.
     */
    private static void findByNomProcessTest(String nomListe) {
        System.out.println("\n--- findByNomProcessTest() ---");

        ListeAttente found = listeAttenteRepository.findByNomListe(nomListe);
        if (found == null) {
            System.out.println("Aucune liste d'attente trouvée avec nom=" + nomListe);
        } else {
            System.out.printf("Liste trouvée : ID=%d | nom=%s%n",
                    found.getId(),
                    found.getNomListe());
        }
    }

    /**
     * Test de suppression d'une liste d'attente.
     */
    private static void deleteProcessTest(ListeAttente liste) {
        System.out.println("\n--- deleteProcessTest() ---");

        if (liste == null || liste.getId() == null) {
            System.out.println("Liste null ou sans id, impossible de tester la suppression.");
            return;
        }

        Long id = liste.getId();
        listeAttenteRepository.deleteById(id);

        ListeAttente afterDelete = listeAttenteRepository.findById(id);
        if (afterDelete == null) {
            System.out.println("Liste d'attente supprimée avec succès, id=" + id);
        } else {
            System.out.println("⚠ Liste toujours présente après delete, id=" + id);
        }
    }
}
