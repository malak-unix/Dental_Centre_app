package ma.dentalTech.repository.modules.rdv.api;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.rdv.RDV;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface RdvRepository extends CrudRepository<RDV, Long> {

    /**
     * Retourne tous les rendez-vous d’un jour donné.
     */
    List<RDV> findByDate(LocalDate date);

    /**
     * Retourne tous les rendez-vous d’un patient.
     */
    List<RDV> findByPatientId(Long patientId);

    /**
     * Liste les rendez-vous par statut (PREVU, CONFIRME, EN_COURS, TERMINE, ANNULE, ABSENT).
     */
    List<RDV> findByStatus(EtatRendezVous status);

    /**
     * Liste des prochains rendez-vous à partir d’aujourd’hui (optionnel mais pratique).
     */
    List<RDV> findUpcomingFromToday();

    List<RDV> findByListeAttenteId(Long listeAttenteId);
}
