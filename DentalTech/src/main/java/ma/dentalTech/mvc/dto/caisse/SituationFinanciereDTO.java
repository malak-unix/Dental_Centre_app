package ma.dentalTech.mvc.dto.caisse;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SituationFinanciereDTO {
    private Long dossierId;
    private Long medecinId;

    private Double totalDesActes;
    private Double totalPaye;
    private Double credit;

    private String statut;
}
