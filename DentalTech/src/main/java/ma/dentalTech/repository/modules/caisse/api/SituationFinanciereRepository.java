package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.entities.situationFinanciere.SituationFinanciere;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.Optional;

public interface SituationFinanciereRepository extends CrudRepository<SituationFinanciere, Long> {
    SituationFinanciere findLast();
    Optional<SituationFinanciere> findByDossierId(Long dossierId);

    // Note : Le calcul de la situation financière elle-même se fera dans la couche Service.
    // Cette interface se contente d'accéder aux rapports sauvegardés.
}