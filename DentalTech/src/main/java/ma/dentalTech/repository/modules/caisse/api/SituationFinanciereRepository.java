package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.repository.common.CrudRepository;

public interface SituationFinanciereRepository extends CrudRepository<SituationFinanciere, Long> {

    SituationFinanciere findLast();
}
