package ma.dentalTech.service.modules.plageHoraire.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.plageHoraire.PlageHoraire;

import java.time.LocalTime;
import java.util.List;

public interface PlageHoraireService {

    /**
     * Génère automatiquement des plages horaires pour une journée,
     * entre heureDebut et heureFin, avec des créneaux de dureeMinutes.
     */
    List<PlageHoraire> genererPlagesPourJournee(Long detailJourneeId,
                                                LocalTime heureDebut,
                                                LocalTime heureFin,
                                                int dureeMinutes,
                                                String user) throws ServiceException;

    List<PlageHoraire> listerPlagesJournee(Long detailJourneeId) throws ServiceException;

    List<PlageHoraire> listerPlagesDisponibles(Long detailJourneeId) throws ServiceException;

    void marquerOccupee(Long plageId, String user) throws ServiceException;

    void marquerLibre(Long plageId, String user) throws ServiceException;
}
