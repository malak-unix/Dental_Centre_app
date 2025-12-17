package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.mvc.dto.CaisseDashboardDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface CaisseDashboardService {

    List<Facture> getFacturesBetween(LocalDateTime start, LocalDateTime end);
    List<Revenues> getRevenusBetween(LocalDateTime start, LocalDateTime end);
    List<Charges> getChargesBetween(LocalDateTime start, LocalDateTime end);

    Double totalFactures(LocalDateTime start, LocalDateTime end);
    Double totalRegle(LocalDateTime start, LocalDateTime end);
    Double totalNonRegle(LocalDateTime start, LocalDateTime end);
    Double totalRevenus(LocalDateTime start, LocalDateTime end);
    Double totalCharges(LocalDateTime start, LocalDateTime end);

    Double solde(LocalDateTime start, LocalDateTime end); // revenus - charges
    CaisseDashboardDTO getDashboardToday();
}
