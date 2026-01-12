package ma.dentalTech.mvc.dto.dossierMedicale.common;

import java.util.List;

public record ListResponseDTO<T>(List<T> items) {}
