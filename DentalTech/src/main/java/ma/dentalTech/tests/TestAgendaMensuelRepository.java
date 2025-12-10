package ma.dentalTech.tests;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;

public class TestAgendaMensuelRepository {

    private static AgendaMensuelRepository agendaMensuelRepository;

    public static void main(String[] args) {

        agendaMensuelRepository = ApplicationContext.getBean(AgendaMensuelRepository.class);

        System.out.println("=== TEST AGENDA_MENSUEL REPOSITORY ===");

        // 1) INSERT (en gérant l'unicité)
        AgendaMensuel agenda = insertProcessTest();

        // 2) SELECT
        selectProcessTest(agenda);

        // 3) UPDATE
        updateProcessTest(agenda);

        // 4) DELETE
        deleteProcessTest(agenda);

        System.out.println("=== FIN TEST AGENDA_MENSUEL ===");
    }

    /**
     * Test d'insertion d'un AgendaMensuel.
     * Si un agenda existe déjà pour (medecinId, mois, annee),
     * on le supprime d'abord pour éviter la contrainte UNIQUE.
     */
    private static AgendaMensuel insertProcessTest() {
        System.out.println("\n--- insertProcessTest() ---");

        Long medecinId = 1L;
        Mois mois = Mois.DECEMBRE;
        int annee = 2025;

        // Vérifier si un agenda existe déjà pour ce triplet
        AgendaMensuel existant =
                agendaMensuelRepository.findByMedecinAndMonth(medecinId, String.valueOf(mois), annee);

        if (existant != null) {
            System.out.printf(
                    "Un agenda existe déjà pour medecinId=%d, mois=%s, annee=%d (id=%d). On le supprime pour le test.%n",
                    medecinId, mois, annee, existant.getId()
            );
            agendaMensuelRepository.deleteById(existant.getId());
        }

        // Maintenant on peut créer un nouvel agenda sans violer la contrainte UNIQUE
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecinId(medecinId)
                .mois(mois)
                .annee(annee)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        agendaMensuelRepository.create(agenda);

        System.out.printf("Agenda créé : ID=%d | medecinId=%d | mois=%s | annee=%d%n",
                agenda.getId(),
                agenda.getMedecinId(),
                agenda.getMois(),
                agenda.getAnnee());

        return agenda;
    }

    /**
     * Test de lecture (SELECT) via findByMedecinAndMonth.
     */
    private static void selectProcessTest(AgendaMensuel agendaRef) {
        System.out.println("\n--- selectProcessTest() ---");

        AgendaMensuel agenda =
                agendaMensuelRepository.findByMedecinAndMonth(
                        agendaRef.getMedecinId(),
                        String.valueOf(agendaRef.getMois()),
                        agendaRef.getAnnee()
                );

        if (agenda == null) {
            System.out.println("Aucun agenda trouvé.");
        } else {
            System.out.printf("Agenda trouvé : ID=%d | medecinId=%d | mois=%s | annee=%d%n",
                    agenda.getId(),
                    agenda.getMedecinId(),
                    agenda.getMois(),
                    agenda.getAnnee());
        }
    }

    /**
     * Test de mise à jour (UPDATE).
     */
    private static void updateProcessTest(AgendaMensuel agenda) {
        System.out.println("\n--- updateProcessTest() ---");

        if (agenda == null || agenda.getId() == null) {
            System.out.println("Agenda null ou sans id, impossible de tester l'update.");
            return;
        }

        agenda.setModifiePar("TEST_UPDATE");
        agendaMensuelRepository.update(agenda);

        AgendaMensuel agendaUpdated = agendaMensuelRepository.findById(agenda.getId());
        System.out.printf("Agenda mis à jour : ID=%d | modifiePar=%s%n",
                agendaUpdated.getId(),
                agendaUpdated.getModifiePar());
    }

    /**
     * Test de suppression (DELETE).
     */
    private static void deleteProcessTest(AgendaMensuel agenda) {
        System.out.println("\n--- deleteProcessTest() ---");

        if (agenda == null || agenda.getId() == null) {
            System.out.println("Agenda null ou sans id, impossible de tester la suppression.");
            return;
        }

        Long id = agenda.getId();
        agendaMensuelRepository.deleteById(id);

        AgendaMensuel afterDelete = agendaMensuelRepository.findById(id);
        if (afterDelete == null) {
            System.out.println("Agenda supprimé avec succès, id=" + id);
        } else {
            System.out.println("⚠ Agenda toujours présent après delete, id=" + id);
        }
    }
}
