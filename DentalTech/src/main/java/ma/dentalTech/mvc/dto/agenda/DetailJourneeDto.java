package ma.dentalTech.mvc.dto.agenda;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailJourneeDto {
    private Long id;
    private Long agendaId;

    private LocalDate dateJour;
    private LocalTime heureDebutTravail;
    private LocalTime heureFinTravail;

    /** DB = enum SQL, côté entity tu as String => DTO pareil */
    private String etatJour;

    private String commentaire;
}
