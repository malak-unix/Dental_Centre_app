package ma.dentalTech.service.modules.dossierMedical.dto.dossier;

import ma.dentalTech.service.modules.dossierMedical.dto.common.PageRequestDTO;

public record DossierListRequestDTO(
        String keyword,
        Long medecinId,
        PageRequestDTO page) {}
