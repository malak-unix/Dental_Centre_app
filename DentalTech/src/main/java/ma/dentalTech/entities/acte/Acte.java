package ma.dentalTech.entities.acte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Acte extends BaseEntity {

    private Long idActe;
    private String libelle;
    private String categorie;
    private Double prixDeBase;
}
