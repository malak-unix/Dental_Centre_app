package ma.dentalTech.service.modules.agenda.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.enums.StatutJournee;

import java.time.LocalDate;
import java.util.List;

public interface AgendaService {

    // Créer un agenda pour un médecin sur un mois donné
    AgendaMensuel creerAgenda(Long medecinId, Mois mois, int annee) throws ServiceException;

    // Récupérer un agenda
    AgendaMensuel trouverAgenda(Long medecinId, Mois mois, int annee) throws ServiceException;

    // Lister les agendas d'un médecin
    List<AgendaMensuel> listerAgendasMedecin(Long medecinId) throws ServiceException;

    // Ajouter une journée dans un agenda
    DetailJournee ajouterJournee(Long agendaId, LocalDate jour, StatutJournee etat) throws ServiceException;

    // Modifier horaires d'une journée
    DetailJournee definirHoraires(Long detailId, String heureDebut, String heureFin) throws ServiceException;

    // Changer l'état d'une journée (OUVERT / FERME / FERIE / VACANCES)
    DetailJournee changerEtatJournee(Long detailId, StatutJournee etat) throws ServiceException;

    // Lister les journées d'un agenda
    List<DetailJournee> listerJournees(Long agendaId) throws ServiceException;

    // Récupérer la journée d'un agenda à une date donnée
    DetailJournee trouverJournee(Long agendaId, LocalDate jour) throws ServiceException;
}
