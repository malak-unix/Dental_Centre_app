package ma.dentalTech.service.modules.caisse.baseImplementation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.situationFinanciere.SituationFinanciere;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.dentalTech.service.modules.caisse.api.SituationFinanciereService;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SituationFinanciereServiceImpl implements SituationFinanciereService {

    private SituationFinanciereRepository situationFinanciereRepository;

    @Override
    public SituationFinanciere getDerniereSituationFinanciere() throws ServiceException {
        try {
            SituationFinanciere situation = situationFinanciereRepository.findLast();
            if (situation == null) {
                throw new ServiceException("Aucune situation financière n'est encore enregistrée.");
            }
            return situation;
        } catch (DaoException e) {
            throw new ServiceException("Erreur lors de la récupération de la dernière situation financière.", e);
        }
    }
}
