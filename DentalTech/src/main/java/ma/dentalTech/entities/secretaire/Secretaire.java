package ma.dentalTech.entities.secretaire;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.staff.Staff;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Secretaire extends Staff {

    // Pas de champs spécifiques pour l'instant, mais on garde la classe

    @Override
    public String toString() {
        return "Secretaire { " + super.toString() + " }";
    }
}