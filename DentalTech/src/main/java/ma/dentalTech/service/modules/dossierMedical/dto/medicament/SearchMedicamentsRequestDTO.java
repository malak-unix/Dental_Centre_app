package ma.dentalTech.service.modules.dossierMedical.dto.medicament;

import ma.dentalTech.service.modules.dossierMedical.dto.common.PageRequestDTO;

public record SearchMedicamentsRequestDTO(String keyword, Boolean remboursable, PageRequestDTO page) {}
