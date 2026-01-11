package ma.dentalTech.service.modules.dossierMedical.dto.certificat;

import ma.dentalTech.service.modules.dossierMedical.dto.common.PageRequestDTO;
import java.time.LocalDate;

public record FindCertificatsRequestDTO(
        Long dossierId,
        LocalDate start,
        LocalDate end,
        String noteKeyword,
        PageRequestDTO page
) {}
