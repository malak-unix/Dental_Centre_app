package ma.dentalTech.service.modules.rdv.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.rdv.RDV;
import ma.dentalTech.repository.modules.rdv.api.RdvRepository;
import ma.dentalTech.service.modules.rdv.api.RdvService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RdvServiceImpl implements RdvService {

    private final RdvRepository rdvRepository;

    // Utilisé par ApplicationContext (constructeur avec RdvRepository)
    public RdvServiceImpl(RdvRepository rdvRepository) {
        this.rdvRepository = rdvRepository;
    }

    @Override
    public RDV planifierRdv(RDV rdv) throws ServiceException {
        try {
            if (rdv == null) {
                throw new IllegalArgumentException("Le RDV ne doit pas être null");
            }
            if (rdv.getStatus() == null) {
                rdv.setStatus(EtatRendezVous.PREVU);
            }
            rdvRepository.create(rdv);
            return rdv;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la planification du RDV", e);
        }
    }

    @Override
    public RDV confirmerRdv(Long rdvId) throws ServiceException {
        try {
            RDV rdv = rdvRepository.findById(rdvId);
            if (rdv == null) {
                throw new ServiceException("Aucun RDV trouvé avec id=" + rdvId);
            }
            rdv.setStatus(EtatRendezVous.CONFIRME);
            rdvRepository.update(rdv);
            return rdv;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la confirmation du RDV id=" + rdvId, e);
        }
    }

    @Override
    public RDV annulerRdv(Long rdvId, String motifAnnulation) throws ServiceException {
        try {
            RDV rdv = rdvRepository.findById(rdvId);
            if (rdv == null) {
                throw new ServiceException("Aucun RDV trouvé avec id=" + rdvId);
            }
            rdv.setStatus(EtatRendezVous.ANNULE);
            if (motifAnnulation != null && !motifAnnulation.isBlank()) {
                String note = rdv.getNoteMedecin();
                if (note == null) note = "";
                note += (note.isEmpty() ? "" : "\n") + "Annulé : " + motifAnnulation;
                rdv.setNoteMedecin(note);
            }
            rdvRepository.update(rdv);
            return rdv;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de l'annulation du RDV id=" + rdvId, e);
        }
    }

    @Override
    public RDV marquerCommeAbsent(Long rdvId) throws ServiceException {
        try {
            RDV rdv = rdvRepository.findById(rdvId);
            if (rdv == null) {
                throw new ServiceException("Aucun RDV trouvé avec id=" + rdvId);
            }
            rdv.setStatus(EtatRendezVous.ABSENT);
            rdvRepository.update(rdv);
            return rdv;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du marquage absent du RDV id=" + rdvId, e);
        }
    }

    @Override
    public RDV terminerRdv(Long rdvId) throws ServiceException {
        try {
            RDV rdv = rdvRepository.findById(rdvId);
            if (rdv == null) {
                throw new ServiceException("Aucun RDV trouvé avec id=" + rdvId);
            }
            rdv.setStatus(EtatRendezVous.TERMINE);
            rdvRepository.update(rdv);
            return rdv;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la clôture du RDV id=" + rdvId, e);
        }
    }

    @Override
    public RDV modifierRdv(Long rdvId,
                           LocalDate nouvelleDate,
                           LocalTime nouvelleHeure,
                           String nouveauMotif) throws ServiceException {
        try {
            if (rdvId == null) {
                throw new IllegalArgumentException("L'id du RDV ne peut pas être null");
            }

            RDV rdv = rdvRepository.findById(rdvId);
            if (rdv == null) {
                throw new ServiceException("Aucun RDV trouvé avec id=" + rdvId);
            }

            // 🔐 Plus tard : ajouter des règles métier (plages horaires, conflits, etc.)

            if (nouvelleDate != null) {
                rdv.setDate(nouvelleDate);
            }
            if (nouvelleHeure != null) {
                rdv.setHeure(nouvelleHeure);
            }
            if (nouveauMotif != null && !nouveauMotif.isBlank()) {
                rdv.setMotif(nouveauMotif);
            }

            String note = rdv.getNoteMedecin();
            String suffix = " | RDV modifié via service.";
            rdv.setNoteMedecin((note != null ? note : "") + suffix);

            rdvRepository.update(rdv);

            // Optionnel : relire en base pour avoir la dernière version
            return rdvRepository.findById(rdvId);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la modification du RDV id=" + rdvId, e);
        }
    }

    @Override
    public List<RDV> listerRdvsParDate(LocalDate date) throws ServiceException {
        try {
            return rdvRepository.findByDate(date);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du listing des RDV pour la date " + date, e);
        }
    }

    @Override
    public List<RDV> listerRdvsAVenir() throws ServiceException {
        try {
            return rdvRepository.findUpcomingFromToday();
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du listing des RDV à venir", e);
        }
    }

    @Override
    public List<RDV> listerRdvsParPatient(Long patientId) throws ServiceException {
        try {
            return rdvRepository.findByPatientId(patientId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du listing des RDV pour le patient id=" + patientId, e);
        }
    }

    @Override
    public List<RDV> listerRdvsParStatut(EtatRendezVous statut) throws ServiceException {
        try {
            return rdvRepository.findByStatus(statut);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du listing des RDV avec statut " + statut, e);
        }
    }
}
