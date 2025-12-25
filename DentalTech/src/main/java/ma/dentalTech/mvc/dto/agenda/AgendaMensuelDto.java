package ma.dentalTech.mvc.dto.agenda;

import lombok.*;
import ma.dentalTech.entities.enums.Mois;

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

    public List<DetailJourneeDto> getJoursSemaine() {
        return List.of();
    }

    public List<RdvDto> getRdvsSemaine() {
        return List.of();
    }
}
