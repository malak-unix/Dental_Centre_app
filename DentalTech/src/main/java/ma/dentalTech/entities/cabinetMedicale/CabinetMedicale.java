package ma.dentalTech.entities.cabinetMedicale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CabinetMedicale extends BaseEntity {

    private Long idUser;
    private String nom;
    private String email;
    private String logo;
    private String adresse;
    private String cin;
    private String tel1;
    private String tel2;
    private String siteWeb;
    private String instagram;
    private String facebook;
    private String description;
}
