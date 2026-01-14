package ma.dentalTech.mvc.dto.dossierMedicale.consultation;

import ma.dentalTech.entities.enums.StatutConsultation;
import java.time.LocalDate;

public record ConsultationDTO(
        Long id,
        Long dossierId,
        LocalDate date,
        StatutConsultation statut,
        String observationMedecin
) {}
