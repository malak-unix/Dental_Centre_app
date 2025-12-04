package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.mvc.dto.BilanFinancierDTO;

import java.time.LocalDate;

public interface FinancialReportingService {

    void createCharge(Charges charge);

    void createRevenu(Revenues revenu);
    BilanFinancierDTO calculateFinancialReport(LocalDate startDate, LocalDate endDate);
}