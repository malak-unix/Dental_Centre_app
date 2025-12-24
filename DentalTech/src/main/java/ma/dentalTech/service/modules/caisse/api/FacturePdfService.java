package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.mvc.dto.caisse.FacturePrintDTO;

public interface FacturePdfService {
    byte[] generateFacturePdf(FacturePrintDTO dto);
}
