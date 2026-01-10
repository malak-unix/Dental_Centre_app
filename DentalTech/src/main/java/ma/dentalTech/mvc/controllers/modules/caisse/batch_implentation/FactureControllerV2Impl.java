package ma.dentalTech.mvc.controllers.modules.caisse.batch_implentation;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.mvc.controllers.modules.caisse.api.FactureControllerV2;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.service.modules.caisse.api.FactureServiceV2;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class FactureControllerV2Impl implements FactureControllerV2 {

    private final FactureServiceV2 service;

    @Override
    public CaisseFactureRowDTO create(FactureCreateDTO dto) {
        try {
            return service.create(dto);
        } catch (Exception e) {
            throw new RuntimeException("Erreur création facture: " + safeMsg(e), e);
        }
    }

    @Override
    public CaisseFactureRowDTO getById(Long id) {
        try {
            return service.getById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur récupération facture: " + safeMsg(e), e);
        }
    }

    @Override
    public List<CaisseFactureRowDTO> listBetween(LocalDateTime start, LocalDateTime end) {
        try {
            return service.listBetween(start, end);
        } catch (Exception e) {
            throw new RuntimeException("Erreur chargement factures: " + safeMsg(e), e);
        }
    }

    @Override
    public CaisseFactureRowDTO payer(Long factureId, FacturePaiementDTO dto) {
        try {
            return service.payer(factureId, dto);
        } catch (Exception e) {
            throw new RuntimeException("Erreur paiement facture: " + safeMsg(e), e);
        }
    }

    @Override
    public FacturePrintDTO getForPrint(Long factureId) {
        try {
            return service.getForPrint(factureId);
        } catch (Exception e) {
            throw new RuntimeException("Erreur préparation impression: " + safeMsg(e), e);
        }
    }

    @Override
    public byte[] exportPdf(Long factureId) {
        try {
            return service.exportPdf(factureId);
        } catch (Exception e) {
            throw new RuntimeException("Erreur export PDF: " + safeMsg(e), e);
        }
    }

    private String safeMsg(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }
}
