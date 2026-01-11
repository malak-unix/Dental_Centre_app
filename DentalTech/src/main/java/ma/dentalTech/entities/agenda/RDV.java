package ma.dentalTech.entities.agenda;

import lombok.*;
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

    private Long patientId;
    private Long detailJourneeId;
    private Long listeAttenteId;

    private LocalDate dateRdv;
    private LocalTime heure;

    private String motif;
    private EtatRendezVous statut;   // ✅ enum aligné avec repo
    private String noteMedecin;
}
