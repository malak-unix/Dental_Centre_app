package ma.dentalTech.service.modules.caisse.baseImplementation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.service.modules.caisse.api.FactureService;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureServiceImpl implements FactureService {

    private FactureRepository factureRepository;

    @Override
    public Facture createFacture(Facture facture) throws ServiceException {
        if (facture == null) {
            throw new ServiceException("La facture ne doit pas être null.");
        }

        try {
            factureRepository.create(facture);
            return facture;
        } catch (RuntimeException e) {
            // Les méthodes CRUD ne déclarent pas DaoException, donc on attrape RuntimeException
            throw new ServiceException("Erreur lors de la création de la facture.", e);
        }
    }

    @Override
    public Facture updateFacture(Facture facture) throws ServiceException {
        if (facture == null) {
            throw new ServiceException("La facture ne doit pas être null.");
        }
        if (facture.getId() == null) {
            throw new ServiceException("La facture doit avoir un id pour être modifiée.");
        }

        try {
            factureRepository.update(facture);
            return facture;
        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors de la mise à jour de la facture.", e);
        }
    }

    @Override
    public void annulerFacture(Long id) throws ServiceException {
        if (id == null) {
            throw new ServiceException("L'id de la facture à annuler ne doit pas être null.");
        }

        try {
            // Annulation la plus simple : suppression de la facture.
            // Si plus tard tu veux un StatutFacture.ANNULEE, tu pourras adapter ici.
            factureRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors de l'annulation (suppression) de la facture.", e);
        }
    }

    @Override
    public Facture getFactureById(Long id) throws ServiceException {
        if (id == null) {
            throw new ServiceException("L'id de la facture ne doit pas être null.");
        }

        try {
            Facture facture = factureRepository.findById(id);
            if (facture == null) {
                throw new ServiceException("Aucune facture trouvée avec l'id : " + id);
            }
            return facture;
        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors de la récupération de la facture.", e);
        }
    }

    @Override
    public List<Facture> getFacturesBetween(LocalDateTime start, LocalDateTime end) throws ServiceException {
        if (start == null || end == null) {
            throw new ServiceException("Les dates de début et de fin ne doivent pas être null.");
        }
        if (end.isBefore(start)) {
            throw new ServiceException("La date de fin doit être >= la date de début.");
        }

        try {
            return factureRepository.findByDateBetween(start, end);
        } catch (DaoException e) {
            throw new ServiceException("Erreur lors de la récupération des factures sur la période.", e);
        }
    }

    @Override
    public void enregistrerPaiement(Long factureId, Double montantPaye) throws ServiceException {
        if (factureId == null) {
            throw new ServiceException("L'id de la facture ne doit pas être null.");
        }
        if (montantPaye == null || montantPaye <= 0) {
            throw new ServiceException("Le montant payé doit être > 0.");
        }

        try {
            Facture facture = factureRepository.findById(factureId);
            if (facture == null) {
                throw new ServiceException("Aucune facture trouvée avec l'id : " + factureId);
            }

            Double actuel = facture.getTotalPaye() != null ? facture.getTotalPaye() : 0.0;
            facture.setTotalPaye(actuel + montantPaye);

            factureRepository.update(facture);
        } catch (RuntimeException e) {
            throw new ServiceException("Erreur lors de l'enregistrement du paiement de la facture.", e);
        }
    }
}
