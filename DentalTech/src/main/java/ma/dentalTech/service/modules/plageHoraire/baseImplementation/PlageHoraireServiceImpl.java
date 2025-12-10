package ma.dentalTech.service.modules.plageHoraire.baseImplementation;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.plageHoraire.PlageHoraire;
import ma.dentalTech.repository.modules.plageHoraire.api.PlageHoraireRepository;
import ma.dentalTech.service.modules.plageHoraire.api.PlageHoraireService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PlageHoraireServiceImpl implements PlageHoraireService {

    private final PlageHoraireRepository plageHoraireRepository;

    public PlageHoraireServiceImpl(PlageHoraireRepository plageHoraireRepository) {
        this.plageHoraireRepository = plageHoraireRepository;
    }

    @Override
    public List<PlageHoraire> genererPlagesPourJournee(Long detailJourneeId,
                                                       LocalTime heureDebut,
                                                       LocalTime heureFin,
                                                       int dureeMinutes,
                                                       String user) throws ServiceException {
        try {
            if (detailJourneeId == null) {
                throw new IllegalArgumentException("detailJourneeId ne doit pas être null");
            }
            if (heureDebut == null || heureFin == null) {
                throw new IllegalArgumentException("Les heures début/fin doivent être renseignées");
            }
            if (!heureDebut.isBefore(heureFin)) {
                throw new IllegalArgumentException("L'heure de début doit être avant l'heure de fin");
            }
            if (dureeMinutes <= 0) {
                throw new IllegalArgumentException("La durée doit être > 0 minutes");
            }

            // (optionnel) : on pourrait supprimer les anciennes plages avant de régénérer

            List<PlageHoraire> plagesCreees = new ArrayList<>();

            LocalTime courant = heureDebut;
            while (courant.plusMinutes(dureeMinutes).compareTo(heureFin) <= 0) {
                LocalTime fin = courant.plusMinutes(dureeMinutes);

                PlageHoraire ph = PlageHoraire.builder()
                        .detailJourneeId(detailJourneeId)
                        .heureDebut(courant)
                        .heureFin(fin)
                        .disponible(true)
                        .creePar(user)
                        .modifiePar(user)
                        .build();

                plageHoraireRepository.create(ph);
                plagesCreees.add(ph);

                courant = fin;
            }

            return plagesCreees;

        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la génération des plages horaires", e);
        }
    }

    @Override
    public List<PlageHoraire> listerPlagesJournee(Long detailJourneeId) throws ServiceException {
        try {
            return plageHoraireRepository.findByDetailJourneeId(detailJourneeId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du listing des plages pour detailJourneeId=" + detailJourneeId, e);
        }
    }

    @Override
    public List<PlageHoraire> listerPlagesDisponibles(Long detailJourneeId) throws ServiceException {
        try {
            return plageHoraireRepository.findDisponiblesByDetailJournee(detailJourneeId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du listing des plages disponibles pour detailJourneeId=" + detailJourneeId, e);
        }
    }

    @Override
    public void marquerOccupee(Long plageId, String user) throws ServiceException {
        try {
            PlageHoraire ph = plageHoraireRepository.findById(plageId);
            if (ph == null) {
                throw new ServiceException("Aucune plage trouvée avec id=" + plageId);
            }
            ph.setDisponible(false);
            ph.setModifiePar(user);
            plageHoraireRepository.update(ph);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du marquage occupée de la plage id=" + plageId, e);
        }
    }

    @Override
    public void marquerLibre(Long plageId, String user) throws ServiceException {
        try {
            PlageHoraire ph = plageHoraireRepository.findById(plageId);
            if (ph == null) {
                throw new ServiceException("Aucune plage trouvée avec id=" + plageId);
            }
            ph.setDisponible(true);
            ph.setModifiePar(user);
            plageHoraireRepository.update(ph);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du marquage libre de la plage id=" + plageId, e);
        }
    }
}
