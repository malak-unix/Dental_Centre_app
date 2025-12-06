package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.situationFinanciere.SituationFinanciere;
import ma.dentalTech.repository.common.CrudRepository;

public interface SituationFinanciereRepository extends CrudRepository<SituationFinanciere, Long> {


    SituationFinanciere findLast() throws DaoException;
}
