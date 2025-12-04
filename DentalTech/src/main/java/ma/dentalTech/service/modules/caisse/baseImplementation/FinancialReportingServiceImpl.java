package ma.dentalTech.service.modules.caisse.baseImplementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.mvc.dto.BilanFinancierDTO;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.service.modules.caisse.api.FinancialReportingService;

@Data @AllArgsConstructor @NoArgsConstructor
public class FinancialReportingServiceImpl implements FinancialReportingService {

    private FactureRepository factureRepository;
    private ChargesRepository chargesRepository;
    private RevenuesRepository revenuesRepository;

    @Override
    public void createCharge(Charges charge) {
        // Logique métier: s'assurer que la date est correcte, valider le montant, etc.
        chargesRepository.create(charge);
    }

    @Override
    public void createRevenu(Revenues revenu) {
        // Logique métier: validation
        revenuesRepository.create(revenu);
    }

    @Override
    public BilanFinancierDTO calculateFinancialReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        // 1. Récupération des totaux via les Repositories
        Double totalPaidFacture = factureRepository.calculateTotalRevenue(start, end);
        Double totalOtherRevenue = revenuesRepository.calculateTotalOtherRevenue(start, end);
        Double totalExpenses = chargesRepository.calculateTotalExpenses(start, end);

        // 2. Calcul du résultat net
        BigDecimal recettesTotal = BigDecimal.valueOf(totalPaidFacture + totalOtherRevenue);
        BigDecimal depensesTotal = BigDecimal.valueOf(totalExpenses);
        BigDecimal resultatNet = recettesTotal.subtract(depensesTotal);

        // 3. Détermination du statut
        String statut = "EQUILIBRE";
        if (resultatNet.compareTo(BigDecimal.ZERO) > 0) {
            statut = "POSITIVE";
        } else if (resultatNet.compareTo(BigDecimal.ZERO) < 0) {
            statut = "NEGATIVE";
        }

        // 4. Construction du DTO de rapport
        return BilanFinancierDTO.builder()
                .periodeStart(startDate)
                .periodeEnd(endDate)
                .recettesTotalFacture(BigDecimal.valueOf(totalPaidFacture))
                .recettesAutres(BigDecimal.valueOf(totalOtherRevenue))
                .depensesTotal(depensesTotal)
                .resultatNet(resultatNet)
                .statut(statut)
                .build();
    }
}