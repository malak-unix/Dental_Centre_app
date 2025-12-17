package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.dossierMedical.SituationFinanciere;

public interface SituationFinanciereService {

    SituationFinanciere getDerniereSituationFinanciere() throws ServiceException;
}
