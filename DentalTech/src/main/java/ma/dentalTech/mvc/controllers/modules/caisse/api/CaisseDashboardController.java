package ma.dentalTech.mvc.controllers.modules.caisse.api;

import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.entities.revenues.Revenues;

import java.time.LocalDateTime;
import java.util.List;

public interface CaisseDashboardController {

    // listes
    List<Facture> facturesBetween(LocalDateTime start, LocalDateTime end);
    List<Revenues> revenusBetween(LocalDateTime start, LocalDateTime end);
    List<Charges> chargesBetween(LocalDateTime start, LocalDateTime end);

    // totaux
    Double totalFactures(LocalDateTime start, LocalDateTime end);
    Double totalRegle(LocalDateTime start, LocalDateTime end);
    Double totalNonRegle(LocalDateTime start, LocalDateTime end);
    Double totalRevenus(LocalDateTime start, LocalDateTime end);
    Double totalCharges(LocalDateTime start, LocalDateTime end);
    Double solde(LocalDateTime start, LocalDateTime end);
}
