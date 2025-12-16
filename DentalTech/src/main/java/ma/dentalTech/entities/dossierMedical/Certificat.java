package ma.dentalTech.entities.dossierMedical;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Certificat extends BaseEntity {
    private Long dossierId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int duree;
    private String noteMedecin;
}
