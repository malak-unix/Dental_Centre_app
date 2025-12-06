package ma.dentalTech.entities.cabinetMedicale;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CabinetMedicale extends BaseEntity {
    private String nom;
    private String email;
    private String logo;
    private String adresse;
    private String tel1;
    private String tel2;
    private String siteWeb;
    private String instagram;
    private String facebook;
    private String slogan;
    private String description;
}
