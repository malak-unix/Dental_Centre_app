package ma.dentalTech.entities.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Ordonnance extends BaseEntity {

    private Long dossierId;
    private Long consultationId;
    private LocalDate date;
}
