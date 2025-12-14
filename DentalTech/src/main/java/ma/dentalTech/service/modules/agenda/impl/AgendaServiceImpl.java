package ma.dentalTech.service.modules.agenda.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;
import ma.dentalTech.service.modules.agenda.api.AgendaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AgendaServiceImpl implements AgendaService {

    private final AgendaMensuelRepository agendaRepo;
    private final DetailJourneeRepository detailRepo;

    public AgendaServiceImpl(AgendaMensuelRepository agendaRepo,
                             DetailJourneeRepository detailRepo) {
        this.agendaRepo = agendaRepo;
        this.detailRepo = detailRepo;
    }

    @Override
    public AgendaMensuel creerAgendaMensuel(Long medecinId, Mois mois, int annee) throws ServiceException {
        try {
            AgendaMensuel existant =
                    agendaRepo.findByMedecinAndMonth(medecinId, mois.name(), annee);
            if (existant != null) {
                return existant;
            }

            AgendaMensuel agenda = AgendaMensuel.builder()
                    .medecinId(medecinId)
                    .mois(mois)
                    .annee(annee)
                    .build();

            agendaRepo.create(agenda);
            return agenda;

        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors de la création de l'agenda mensuel", e);
        }
    }

    @Override
    public AgendaMensuel trouverAgenda(Long medecinId, Mois mois, int annee) throws ServiceException {
        try {
            return agendaRepo.findByMedecinAndMonth(medecinId, mois.name(), annee);
        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors de la recherche de l'agenda", e);
        }
    }

    @Override
    public List<AgendaMensuel> listerAgendasMedecin(Long medecinId) throws ServiceException {
        try {
            return agendaRepo.findByMedecin(medecinId);
        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors du listing des agendas du médecin", e);
        }
    }

    @Override
    public DetailJournee ajouterJournee(Long agendaId, LocalDate date) throws ServiceException {
        try {
            DetailJournee existante = detailRepo.findByAgendaIdAndDateJour(agendaId, date);
            if (existante != null) return existante;

            DetailJournee d = DetailJournee.builder()
                    .agendaId(agendaId)
                    .dateJour(date)
                    .etatJour(StatutJournee.OUVERT)
                    .build();

            detailRepo.create(d);
            return d;

        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors de l'ajout d'une journée à l'agenda", e);
        }
    }

    @Override
    public DetailJournee definirHoraires(Long detailJourneeId, LocalTime debut, LocalTime fin) throws ServiceException {
        try {
            DetailJournee d = detailRepo.findById(detailJourneeId);
            if (d == null) {
                throw new ServiceException("DetailJournee introuvable id=" + detailJourneeId);
            }

            d.setHeureDebutTravaillee(debut);
            d.setHeureFinTravaillee(fin);

            detailRepo.update(d);
            return d;

        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors de la définition des horaires de la journée", e);
        }
    }

    @Override
    public DetailJournee changerStatutJournee(Long detailJourneeId, String statutJournee) throws ServiceException {
        try {
            DetailJournee d = detailRepo.findById(detailJourneeId);
            if (d == null) {
                throw new ServiceException("DetailJournee introuvable id=" + detailJourneeId);
            }

            StatutJournee statut = StatutJournee.valueOf(statutJournee);
            d.setEtatJour(statut);
            detailRepo.update(d);
            return d;

        } catch (IllegalArgumentException e) {
            throw new ServiceException("StatutJournee invalide : " + statutJournee, e);
        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors du changement de statut de la journée", e);
        }
    }

    @Override
    public List<DetailJournee> listerJoursAgenda(Long agendaId) throws ServiceException {
        try {
            return detailRepo.findByAgendaId(agendaId);
        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors du listing des journées de l'agenda", e);
        }
    }
}
