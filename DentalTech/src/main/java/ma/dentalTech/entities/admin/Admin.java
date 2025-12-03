package ma.dentalTech.entities.admin;

import lombok.Data;
import ma.dentalTech.entities.utilisateur.Utilisateur;

@Data
public class Admin extends Utilisateur {

    // Constructeur vide explicite
    public Admin() {
        super();
    }

    // PAS d'autres constructeurs, PAS de Lombok @NoArgsConstructor / @AllArgsConstructor / @SuperBuilder ici
}
