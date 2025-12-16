package ma.dentalTech.mvc.dto.agenda;

import lombok.*;
import ma.dentalTech.entities.enums.EtatRendezVous;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RdvDto {
    private Long id;
    private Long patientId;
    private Long detailJourneeId;
    private Long listeAttenteId;

    private LocalDate dateRdv;
    private LocalTime heure;
    private String motif;
    private EtatRendezVous statut;
}
