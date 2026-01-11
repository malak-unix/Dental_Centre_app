package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface SituationFinanciereRepository extends CrudRepository<SituationFinanciere, Long> {

    SituationFinanciere findLast();
    //ajout des methode dont j'aurai besoin pour l'interface du dossier medical d'un patient
    SituationFinanciere findByDossierId(Long dossierId);
    SituationFinanciere findByPatientId(Long patientId);
    boolean resetByDossierId(Long dossierId, String modifiePar);
    List<SituationFinanciere> findPage(int limit, int offset);
    long count();


}
