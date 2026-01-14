package ma.dentalTech.mvc.dto.dossierMedicale.ordonnance;

import java.time.LocalDate;

public record OrdonnanceDTO(
        Long id,
        Long dossierId,
        Long consultationId,
        LocalDate date
) {}
