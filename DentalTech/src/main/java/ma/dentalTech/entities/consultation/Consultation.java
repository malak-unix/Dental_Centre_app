package ma.dentalTech.entities.consultation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutConsultation;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Consultation extends BaseEntity {

    private LocalDate date;
    private StatutConsultation status;  // Enum
    private String observationMedecin;
}
