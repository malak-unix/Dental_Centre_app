package ma.dentalTech.service.modules.ordonnance.baseImplementation;

import ma.dentalTech.service.common.ServiceException;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.dentalTech.service.modules.ordonnance.api.OrdonnanceService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;

    public OrdonnanceServiceImpl(OrdonnanceRepository ordonnanceRepository) {
        this.ordonnanceRepository = Objects.requireNonNull(ordonnanceRepository);
    }

    @Override
    public Ordonnance creerOrdonnance(Long dossierId, Long consultationId, LocalDate date, String utilisateur) {
        if (dossierId == null) {
            throw ServiceException.validation("dossierId obligatoire pour créer une ordonnance");
        }
        if (consultationId == null) {
            throw ServiceException.validation("consultationId obligatoire pour créer une ordonnance");
        }

        try {
            Ordonnance ordonnance = Ordonnance.builder()
                    .dossierId(dossierId)
                    .consultationId(consultationId)
                    .date(date != null ? date : LocalDate.now())
                    .creePar(utilisateur != null ? utilisateur : "SYSTEM")
                    .modifiePar(utilisateur != null ? utilisateur : "SYSTEM")
                    .dateCreation(LocalDateTime.now())
                    .build();

            ordonnanceRepository.create(ordonnance);
            return ordonnance;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la création de l'ordonnance", "ORDO_CREATE_ERROR", e);
        }
    }

    @Override
    public Ordonnance getById(Long id) {
        if (id == null) {
            throw ServiceException.validation("id obligatoire pour rechercher une ordonnance");
        }

        try {
            Ordonnance o = ordonnanceRepository.findById(id);
            if (o == null) {
                throw ServiceException.notFound("Ordonnance non trouvée pour id=" + id);
            }
            return o;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la récupération de l'ordonnance id=" + id,
                    "ORDO_GET_ERROR", e);
        }
    }

    @Override
    public List<Ordonnance> getByDossier(Long dossierId) {
        if (dossierId == null) {
            throw ServiceException.validation("dossierId obligatoire pour rechercher les ordonnances");
        }

        try {
            return ordonnanceRepository.findByDossierId(dossierId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la récupération des ordonnances du dossier " + dossierId,
                    "ORDO_BY_DOSSIER_ERROR", e);
        }
    }

    @Override
    public List<Ordonnance> getByConsultation(Long consultationId) {
        if (consultationId == null) {
            throw ServiceException.validation("consultationId obligatoire pour rechercher les ordonnances");
        }

        try {
            return ordonnanceRepository.findByConsultationId(consultationId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la récupération des ordonnances de la consultation " + consultationId,
                    "ORDO_BY_CONSULT_ERROR", e);
        }
    }

    @Override
    public List<Ordonnance> getByDate(LocalDate date) {
        if (date == null) {
            throw ServiceException.validation("date obligatoire pour rechercher les ordonnances par date");
        }

        try {
            return ordonnanceRepository.findByDate(date);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la récupération des ordonnances pour la date " + date,
                    "ORDO_BY_DATE_ERROR", e);
        }
    }

    @Override
    public void supprimerOrdonnance(Long id) {
        if (id == null) {
            throw ServiceException.validation("id obligatoire pour supprimer une ordonnance");
        }

        try {
            Ordonnance existing = ordonnanceRepository.findById(id);
            if (existing == null) {
                throw ServiceException.notFound("Impossible de supprimer : ordonnance non trouvée pour id=" + id);
            }
            ordonnanceRepository.deleteById(id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la suppression de l'ordonnance id=" + id,
                    "ORDO_DELETE_ERROR", e);
        }
    }
}
