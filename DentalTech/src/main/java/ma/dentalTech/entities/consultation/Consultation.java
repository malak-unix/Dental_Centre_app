package ma.dentalTech.entities.consultation;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutConsultation;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Consultation extends BaseEntity {
    private LocalDate date;
    private StatutConsultation status;
    private String observationMedecin;
}
