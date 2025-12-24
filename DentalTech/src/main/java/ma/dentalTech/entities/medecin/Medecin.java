package ma.dentalTech.entities.medecin;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.staff.Staff;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Medecin extends Staff {

    private String specialite;
    private Double pourcentage;

    // Tu pourras ajouter List<RendezVous> rendezVous ici plus tard avec la même logique

    @Override
    public String toString() {
        return """
        Medecin {
          id = %d,
          nom = '%s',
          specialite = '%s',
          salaire = %.2f
        }
        """.formatted(
                id,
                getNom(),
                specialite,
                getSalaire() != null ? getSalaire() : 0.0
        );
    }
}