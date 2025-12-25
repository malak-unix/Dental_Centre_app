package ma.dentalTech.service.modules.caisse.impl;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.mvc.dto.CaisseDashboardDTO;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardService;

import java.time.LocalDateTime;
import java.util.List;

public class CaisseDashboardServiceImpl implements CaisseDashboardService {

    private final FactureRepository factureRepository;
    private final RevenuesRepository revenuesRepository;
    private final ChargesRepository chargesRepository;

    public CaisseDashboardServiceImpl(FactureRepository factureRepository,
                                      RevenuesRepository revenuesRepository,
                                      ChargesRepository chargesRepository) {
        this.factureRepository = factureRepository;
        this.revenuesRepository = revenuesRepository;
        this.chargesRepository = chargesRepository;
    }

    @Override
    public List<Facture> getFacturesBetween(LocalDateTime start, LocalDateTime end) {
        return factureRepository.findByDateBetween(start, end);
    }

    @Override
    public List<Revenues> getRevenusBetween(LocalDateTime start, LocalDateTime end) {
        return revenuesRepository.findByDateBetween(start, end);
    }

    @Override
    public List<Charges> getChargesBetween(LocalDateTime start, LocalDateTime end) {
        return chargesRepository.findByDateBetween(start, end);
    }

    @Override
    public Double totalFactures(LocalDateTime start, LocalDateTime end) {
        return factureRepository.calculateTotalFactures(start, end);
    }

    @Override
    public Double totalRegle(LocalDateTime start, LocalDateTime end) {
        return factureRepository.calculateTotalRegle(start, end);
    }

    @Override
    public Double totalNonRegle(LocalDateTime start, LocalDateTime end) {
        return factureRepository.calculateTotalNonRegle(start, end);
    }

    @Override
    public Double totalRevenus(LocalDateTime start, LocalDateTime end) {
        return revenuesRepository.calculateTotalOtherRevenue(start, end);
    }

    @Override
    public Double totalCharges(LocalDateTime start, LocalDateTime end) {
        return chargesRepository.calculateTotalCharges(start, end);
    }

    @Override
    public Double solde(LocalDateTime start, LocalDateTime end) {
        return totalRevenus(start, end) - totalCharges(start, end);
    }

    @Override
    public CaisseDashboardDTO getDashboardToday() {
        return null;
    }
}
