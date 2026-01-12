package ma.dentalTech.mvc.dto.dossierMedicale.common;

import java.util.List;

public record PageResponseDTO<T>(List<T> items, long total) {}
