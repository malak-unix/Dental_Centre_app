package ma.dentalTech.repository.modules.consultation.api;

import ma.dentalTech.entities.interventionMedecin.InterventionMedecin;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface InterventionMedecinRepository extends CrudRepository<InterventionMedecin, Long> {

    /**
     * Toutes les interventions effectuées lors d'une consultation.
     */
    List<InterventionMedecin> findByConsultationId(Long consultationId);

    /**
     * Supprime toutes les interventions liées à une consultation
     * (utile quand on supprime ou réédite complètement une consultation).
     */
    void deleteByConsultationId(Long consultationId);
}
