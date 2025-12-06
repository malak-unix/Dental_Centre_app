package ma.dentalTech.entities.rdv;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.EtatRendezVous;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RDV extends BaseEntity {
    private LocalDate date;
    private LocalTime heure;
    private String motif;
    private EtatRendezVous status;
    private String noteMedecin;
}
