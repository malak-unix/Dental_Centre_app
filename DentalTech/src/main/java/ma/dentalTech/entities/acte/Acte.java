package ma.dentalTech.entities.acte;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Acte extends BaseEntity {

    private String libelle;
    private String categorie;
    private Double prixBase;
    private String description;
}
