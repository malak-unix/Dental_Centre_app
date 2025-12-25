package ma.dentalTech.service.modules.dossierMedical.dto.ordonnance;

import java.time.LocalDate;

public record OrdonnanceDTO(
        Long id,
        Long dossierId,
        Long consultationId,
        LocalDate date
) {}
