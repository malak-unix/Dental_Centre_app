package ma.dentalTech.entities.staff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.utilisateur.Utilisateur;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Staff extends Utilisateur {

    private Double salaire;
    private Double prime;
    private LocalDate dateRecrutement;
    private int soldeConge;
}
