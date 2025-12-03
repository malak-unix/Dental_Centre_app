package ma.dentalTech.entities.statistique;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.TypeStatistique;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Statistique extends BaseEntity {

    private String nom;
    private TypeStatistique categorie;  // Enum
    private Double chiffre;
    private LocalDate dateCloture;
}
