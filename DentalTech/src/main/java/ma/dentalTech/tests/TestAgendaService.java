package ma.dentalTech.tests;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.service.modules.agenda.api.AgendaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestAgendaService {

    public static void main(String[] args) {
        System.out.println("=== TEST AGENDA SERVICE ===");

        AgendaService agendaService = ApplicationContext.getBean(AgendaService.class);

        try {
            Long medecinId = 1L; // ⚠ Assure-toi qu'un médecin avec id=1 existe dans la BD

            // 1) Créer ou récupérer un agenda mensuel
            AgendaMensuel agenda = agendaService.creerAgendaMensuel(medecinId, Mois.DECEMBRE, 2025);
            System.out.println("Agenda ID = " + agenda.getId()
                    + " | Medecin=" + agenda.getMedecinId()
                    + " | Mois=" + agenda.getMois()
                    + " | Annee=" + agenda.getAnnee());

            // 2) Ajouter une journée dans cet agenda
            LocalDate today = LocalDate.now();
            DetailJournee jour = agendaService.ajouterJournee(agenda.getId(), today);
            System.out.println("DetailJournee ID = " + jour.getId()
                    + " | date=" + jour.getDateJour()
                    + " | statut=" + jour.getEtatJour());

            // 3) Définir les horaires
            jour = agendaService.definirHoraires(jour.getId(),
                    LocalTime.of(9, 0),
                    LocalTime.of(17, 0));
            System.out.println("Horaires mis à jour : "
                    + jour.getHeureDebutTravaillee() + " - " + jour.getHeureFinTravaillee());

            // 4) Fermer la journée
            jour = agendaService.changerStatutJournee(jour.getId(), "FERME");
            System.out.println("Statut après fermeture : " + jour.getEtatJour());

            // 5) Lister les journées de l'agenda
            List<DetailJournee> jours = agendaService.listerJoursAgenda(agenda.getId());
            System.out.println("Nombre de journées dans l'agenda : " + jours.size());

        } catch (ServiceException e) {
            e.printStackTrace();
        }

        System.out.println("=== FIN TEST AGENDA SERVICE ===");
    }
}
