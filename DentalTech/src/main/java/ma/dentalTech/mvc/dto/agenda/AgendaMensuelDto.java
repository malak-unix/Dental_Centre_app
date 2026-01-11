package ma.dentalTech.mvc.dto.agenda;

import lombok.*;
import ma.dentalTech.entities.enums.Mois;

import java.util.ArrayList;
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

    // ✅ Ajout : listes réelles au lieu de List.of()
    @Builder.Default
    private List<DetailJourneeDto> joursSemaine = new ArrayList<>();

    @Builder.Default
    private List<RdvDto> rdvsSemaine = new ArrayList<>();
}
