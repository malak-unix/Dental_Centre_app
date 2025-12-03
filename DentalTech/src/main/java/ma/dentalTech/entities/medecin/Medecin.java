package ma.dentalTech.entities.medecin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.staff.Staff;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Medecin extends Staff {

    private String specialite;
    private Double pourcentage;  // % de commission par acte
}
