package ma.dentalTech.entities.statistique;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.TypeStatistique;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Statistique extends BaseEntity {
    private String nom;
    private TypeStatistique categorie;
    private Double chiffre;
    private LocalDate dateCloture;
}
