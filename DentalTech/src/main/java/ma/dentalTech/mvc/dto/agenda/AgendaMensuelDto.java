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

    // ✅ pour la vue SEMAINE (maquette)
    private LocalDate semaineStart;                 // lundi de la semaine affichée
    private List<DetailJourneeDto> joursSemaine;    // jours de la semaine
    private List<RdvDto> rdvsSemaine;               // RDV de la semaine
}
