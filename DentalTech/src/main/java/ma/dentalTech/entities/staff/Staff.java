package ma.dentalTech.entities.staff;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.utilisateur.Utilisateur;
import java.time.LocalDate;

@Entity
@Table(name = "staffs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Staff extends Utilisateur {

    private Double salaire;
    private LocalDate dateEmbauche;
    private LocalDate dateFinContrat;
}