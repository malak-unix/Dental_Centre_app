package ma.dentalTech.entities.certificat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Certificat extends BaseEntity {

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int duree;
    private String noteMedecin;
}
