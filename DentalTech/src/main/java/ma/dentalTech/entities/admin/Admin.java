package ma.dentalTech.entities.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.utilisateur.Utilisateur;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Admin extends Utilisateur {
    // Pas d’attributs supplémentaires dans le diagramme
}
