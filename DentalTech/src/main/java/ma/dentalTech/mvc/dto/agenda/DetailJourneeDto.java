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

    private String etatJour;   // ex: "OUVERT"
    private String commentaire;
}
