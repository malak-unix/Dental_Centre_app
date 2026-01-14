package ma.dentalTech.mvc.dto.dossierMedicale.medicament;

import ma.dentalTech.mvc.dto.dossierMedicale.common.PageRequestDTO;

public record SearchMedicamentsRequestDTO(String keyword, Boolean remboursable, PageRequestDTO page) {}
