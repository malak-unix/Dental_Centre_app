package ma.dentalTech.entities.agenda;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutJournee;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DetailJournee extends BaseEntity {

    private Long agendaId;

    private LocalDate dateJour;
    private LocalTime heureDebutTravail;
    private LocalTime heureFinTravail;

    private StatutJournee etatJour; // OUVERT/FERME/FERIE/VACANCES
    private String commentaire;
}
