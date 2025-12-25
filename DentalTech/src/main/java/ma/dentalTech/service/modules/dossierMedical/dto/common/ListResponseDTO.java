package ma.dentalTech.service.modules.dossierMedical.dto.common;

import java.util.List;

public record ListResponseDTO<T>(List<T> items) {}
