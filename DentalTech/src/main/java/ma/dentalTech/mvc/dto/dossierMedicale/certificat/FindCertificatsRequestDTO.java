package ma.dentalTech.mvc.dto.dossierMedicale.certificat;

import ma.dentalTech.mvc.dto.dossierMedicale.common.PageRequestDTO;
import java.time.LocalDate;

public record FindCertificatsRequestDTO(
        Long dossierId,
        LocalDate start,
        LocalDate end,
        String noteKeyword,
        PageRequestDTO page
) {}
