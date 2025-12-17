package ma.dentalTech.entities.users;

import lombok.Data;

@Data
public class Admin extends Utilisateur {

    // Constructeur vide explicite
    public Admin() {
        super();
    }

    // PAS d'autres constructeurs, PAS de Lombok @NoArgsConstructor / @AllArgsConstructor / @SuperBuilder ici
}
