package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.cabinet.SituationFinanciere;

public interface SituationFinanciereService {

    SituationFinanciere getDerniereSituationFinanciere() throws ServiceException;
}
