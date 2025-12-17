package ma.dentalTech.entities.staff;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.utilisateur.Utilisateur;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Staff extends Utilisateur {

    private Double salaire;
    private Double prime;

    @Override
    public String toString() {
        // On reprend les infos de base + infos staff
        return """
        Staff {
          id = %d,
          nom = '%s',
          prenom = '%s',
          role = %s,
          salaire = %.2f
        }
        """.formatted(
                id,
                getNom(),
                getPrenom(),
                getRole() == null ? "Aucun" : getRole().getLibelle(),
                salaire != null ? salaire : 0.0
        );
    }
}