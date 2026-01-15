package ma.dentalTech.mvc.dto.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AntecedentAdminRowDTO {
    private Long id;
    private Long patientId;
    private String patientNomComplet;

    private String nom;
    private String categorie;
    private String niveauDeRisque;
    private String description;
}
