package ma.dentalTech.mvc.dto.dossierMedicale.acte;

import ma.dentalTech.mvc.dto.dossierMedicale.common.PageRequestDTO;

public record FindActesRequestDTO(String keyword, String categorie, PageRequestDTO page) {}
