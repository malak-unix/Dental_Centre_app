package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.mvc.dto.FactureDTO;

import java.util.List;

public interface FactureService {

    void createFacture(Facture facture);
    List<FactureDTO> getAllFacturesAsDTO();

    void payerFacture(Long factureId, Double montantPaye);
    String generateFactureReport(Long factureId);
}