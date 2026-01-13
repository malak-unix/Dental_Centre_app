package ma.dentalTech.mvc.dto.patient;

import lombok.*;
import ma.dentalTech.entities.enums.NiveauDeRisque;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AntecedentListDto {
    private Long id;
    private Long patientId;

    private String nom;
    private String categorie;
    private NiveauDeRisque niveauDeRisque;
    private String description;
}
