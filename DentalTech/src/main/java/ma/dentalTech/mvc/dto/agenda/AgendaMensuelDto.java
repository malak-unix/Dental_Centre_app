package ma.dentalTech.mvc.dto.agenda;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaMensuelDto {
    private Long id;
    private Long medecinId;
    private String mois;   // ex: "JANVIER"
    private Integer annee;
}
