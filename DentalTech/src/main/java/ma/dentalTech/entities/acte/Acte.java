package ma.dentalTech.entities.acte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Acte extends BaseEntity {

    private String libelle;
    private String categorie;
    private Double prixDeBase;
    private String description;

}
