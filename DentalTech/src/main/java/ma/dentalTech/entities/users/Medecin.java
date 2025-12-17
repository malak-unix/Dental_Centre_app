package ma.dentalTech.entities.users;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Medecin extends Staff {

    private String specialite;
    private Double pourcentage;  // % de commission par acte
}
