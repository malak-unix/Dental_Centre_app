package ma.dentalTech.service.modules.caisse.baseImplementation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.CaisseDashboardDTO;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardService;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaisseDashboardServiceImpl implements CaisseDashboardService {

    private FactureRepository factureRepository;
    private RevenuesRepository revenuesRepository;
    private ChargesRepository chargesRepository;

    @Override
    public CaisseDashboardDTO getDashboardBetween(LocalDateTime start, LocalDateTime end) throws ServiceException {
        if (start == null || end == null) {
            throw new ServiceException("Les dates de début et de fin ne doivent pas être null.");
        }
        if (end.isBefore(start)) {
            throw new ServiceException("La date de fin doit être >= la date de début.");
        }

        try {
            Double totalFactures   = safeDouble(factureRepository.calculateTotalFactures(start, end));
            Double totalRegle      = safeDouble(factureRepository.calculateTotalRegle(start, end));
            Double totalNonRegle   = safeDouble(factureRepository.calculateTotalNonRegle(start, end));

            Double totalRevenus    = safeDouble(revenuesRepository.calculateTotalOtherRevenue(start, end));

           Double totalCharges    = safeDouble(chargesRepository.calculateTotalCharges(start, end));

            Double soldeNet        = (totalFactures + totalRevenus) - totalCharges;

            return CaisseDashboardDTO.builder()
                    .dateDebut(start)
                    .dateFin(end)
                    .totalFactures(totalFactures)
                    .totalRegle(totalRegle)
                    .totalNonRegle(totalNonRegle)
                    .totalRevenus(totalRevenus)
                    .totalCharges(totalCharges)
                    .soldeNet(soldeNet)
                    .build();

        } catch (DaoException e) {
            throw new ServiceException("Erreur lors du calcul du Dashboard Caisse.", e);
        }
    }

    private Double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }
}
