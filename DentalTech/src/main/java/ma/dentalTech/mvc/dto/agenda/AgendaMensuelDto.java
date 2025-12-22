package ma.dentalTech.mvc.dto.agenda;

import lombok.*;
import ma.dentalTech.entities.enums.Mois;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaMensuelDto {
    private Long id;
    private Long medecinId;

    private Mois mois;
    private Integer annee;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private List<RdvDto> rendezVous;
}
