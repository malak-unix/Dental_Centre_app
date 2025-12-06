package ma.dentalTech.entities.secretaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.staff.Staff;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Secretaire extends Staff {
    private String numCNSS;
    private Double commission;
}
