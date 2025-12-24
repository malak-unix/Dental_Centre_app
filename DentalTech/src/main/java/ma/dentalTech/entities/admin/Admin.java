package ma.dentalTech.entities.admin;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.utilisateur.Utilisateur;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Admin extends Utilisateur {

    // Hérite de tout.

    @Override
    public String toString() {
        return "Admin { " + super.toString() + " }";
    }
}