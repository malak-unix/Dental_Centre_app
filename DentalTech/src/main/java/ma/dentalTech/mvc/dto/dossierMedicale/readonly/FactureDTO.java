package ma.dentalTech.mvc.dto.dossierMedicale.readonly;

import ma.dentalTech.entities.enums.StatutFacture;
import java.time.LocalDate;

public record FactureDTO(
        Long id,
        Long consultationId,
        LocalDate dateFacture,
        Double totalFacture,
        Double totalPaye,
        Double reste,
        StatutFacture statut
) {}
