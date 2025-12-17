package ma.dentalTech.mvc.controllers.modules.caisse.batch_implentation;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseDashboardController;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardService;

import java.time.LocalDateTime;
import java.util.List;

public class CaisseDashboardControllerImpl implements CaisseDashboardController {

    private final CaisseDashboardService service;

    public CaisseDashboardControllerImpl(CaisseDashboardService service) {
        this.service = service;
    }

    @Override
    public List<Facture> facturesBetween(LocalDateTime start, LocalDateTime end) {
        return service.getFacturesBetween(start, end);
    }

    @Override
    public List<Revenues> revenusBetween(LocalDateTime start, LocalDateTime end) {
        return service.getRevenusBetween(start, end);
    }

    @Override
    public List<Charges> chargesBetween(LocalDateTime start, LocalDateTime end) {
        return service.getChargesBetween(start, end);
    }

    @Override
    public Double totalFactures(LocalDateTime start, LocalDateTime end) {
        return service.totalFactures(start, end);
    }

    @Override
    public Double totalRegle(LocalDateTime start, LocalDateTime end) {
        return service.totalRegle(start, end);
    }

    @Override
    public Double totalNonRegle(LocalDateTime start, LocalDateTime end) {
        return service.totalNonRegle(start, end);
    }

    @Override
    public Double totalRevenus(LocalDateTime start, LocalDateTime end) {
        return service.totalRevenus(start, end);
    }

    @Override
    public Double totalCharges(LocalDateTime start, LocalDateTime end) {
        return service.totalCharges(start, end);
    }

    @Override
    public Double solde(LocalDateTime start, LocalDateTime end) {
        return service.solde(start, end);
    }
}
