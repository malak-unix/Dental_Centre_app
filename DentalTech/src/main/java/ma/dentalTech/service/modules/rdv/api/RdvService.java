package ma.dentalTech.service.modules.rdv.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.rdv.RDV;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RdvService {

    // Planifier un nouveau RDV (par défaut statut = PREVU)
    RDV planifierRdv(RDV rdv) throws ServiceException;

    // Confirmer un RDV existant
    RDV confirmerRdv(Long rdvId) throws ServiceException;

    // Annuler un RDV (optionnellement avec motif dans note_medecin)
    RDV annulerRdv(Long rdvId, String motifAnnulation) throws ServiceException;

    // Marquer un RDV comme terminé
    RDV terminerRdv(Long rdvId) throws ServiceException;

    RDV modifierRdv(Long rdvId,
                    LocalDate nouvelleDate,
                    LocalTime nouvelleHeure,
                    String nouveauMotif) throws ServiceException;

    RDV marquerCommeAbsent(Long rdvId) throws ServiceException;

    // Lister les RDV d’une date donnée
    List<RDV> listerRdvsParDate(LocalDate date) throws ServiceException;

    // Lister les RDV à partir d’aujourd’hui
    List<RDV> listerRdvsAVenir() throws ServiceException;

    // Lister les RDV d’un patient donné
    List<RDV> listerRdvsParPatient(Long patientId) throws ServiceException;

    // Lister les RDV par statut
    List<RDV> listerRdvsParStatut(EtatRendezVous statut) throws ServiceException;
}
