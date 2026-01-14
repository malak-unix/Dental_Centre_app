package ma.dentalTech.mvc.dto.dossierMedicale.document;

import ma.dentalTech.mvc.dto.dossierMedicale.common.PageRequestDTO;

public record SearchDocumentsRequestDTO(
        Long dossierId,
        Long consultationId,
        String keyword,
        PageRequestDTO page
) {}
