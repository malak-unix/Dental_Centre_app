package ma.dentalTech.service.modules.dossierMedical.dto.common;

import java.util.List;

public record PageResponseDTO<T>(List<T> items, long total) {}
