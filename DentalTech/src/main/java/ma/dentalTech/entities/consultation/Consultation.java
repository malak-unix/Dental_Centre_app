package ma.dentalTech.entities.consultation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutConsultation;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Consultation extends BaseEntity {

    private Long idConsultation;
    private LocalDate date;
    private StatutConsultation status;  // Enum
    private String observationMedecin;
}
