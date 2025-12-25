package ma.dentalTech.service.modules.dossierMedical.dto.acte;

import ma.dentalTech.service.modules.dossierMedical.dto.common.PageRequestDTO;

public record FindActesRequestDTO(String keyword, String categorie, PageRequestDTO page) {}
