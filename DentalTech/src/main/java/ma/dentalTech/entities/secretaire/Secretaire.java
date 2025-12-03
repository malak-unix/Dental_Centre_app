package ma.dentalTech.entities.secretaire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.staff.Staff;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Secretaire extends Staff {

    private String numCNSS;
    private Double commission;
}
