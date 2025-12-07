package ma.dentalTech.service.modules.agenda.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.Mois;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AgendaService {

    AgendaMensuel creerAgendaMensuel(Long medecinId, Mois mois, int annee) throws ServiceException;

    AgendaMensuel trouverAgenda(Long medecinId, Mois mois, int annee) throws ServiceException;

    List<AgendaMensuel> listerAgendasMedecin(Long medecinId) throws ServiceException;

    DetailJournee ajouterJournee(Long agendaId, LocalDate date) throws ServiceException;

    DetailJournee definirHoraires(Long detailJourneeId, LocalTime debut, LocalTime fin) throws ServiceException;

    DetailJournee changerStatutJournee(Long detailJourneeId, String statutJournee) throws ServiceException;

    List<DetailJournee> listerJoursAgenda(Long agendaId) throws ServiceException;
}
