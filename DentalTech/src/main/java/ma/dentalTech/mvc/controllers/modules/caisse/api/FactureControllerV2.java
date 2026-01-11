package ma.dentalTech.mvc.controllers.modules.caisse.api;

import ma.dentalTech.mvc.dto.caisse.*;

import java.time.LocalDateTime;
import java.util.List;

public interface FactureControllerV2 {

    CaisseFactureRowDTO create(FactureCreateDTO dto);

    CaisseFactureRowDTO getById(Long id);

    List<CaisseFactureRowDTO> listBetween(LocalDateTime start, LocalDateTime end);

    CaisseFactureRowDTO payer(Long factureId, FacturePaiementDTO dto);

    FacturePrintDTO getForPrint(Long factureId);

    byte[] exportPdf(Long factureId);
}
