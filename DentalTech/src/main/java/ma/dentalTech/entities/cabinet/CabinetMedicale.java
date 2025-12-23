package ma.dentalTech.entities.cabinet;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CabinetMedicale extends BaseEntity {
    private Long id ;

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

    // BaseEntity
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CabinetMedicale)) return false;
        CabinetMedicale that = (CabinetMedicale) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
