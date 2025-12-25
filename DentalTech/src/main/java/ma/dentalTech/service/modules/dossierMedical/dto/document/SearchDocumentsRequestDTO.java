package ma.dentalTech.service.modules.dossierMedical.dto.document;

import ma.dentalTech.service.modules.dossierMedical.dto.common.PageRequestDTO;

public record SearchDocumentsRequestDTO(
        Long dossierId,
        Long consultationId,
        String keyword,
        PageRequestDTO page
) {}
