package ma.dentalTech.entities.rdv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.EtatRendezVous;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RDV extends BaseEntity {

    private LocalDate date;
    private LocalTime heure;
    private String motif;
    private EtatRendezVous status;  // Enum
    private String noteMedecin;
}
