package ma.dentalTech.mvc.dto.dossierMedicale.dossier;

import ma.dentalTech.mvc.dto.dossierMedicale.common.PageRequestDTO;

public record DossierListRequestDTO(
        String keyword,
        Long medecinId,
        PageRequestDTO page) {}
