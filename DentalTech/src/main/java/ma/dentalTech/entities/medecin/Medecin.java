package ma.dentalTech.entities.medecin;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.staff.Staff;

@Entity
@Table(name = "medecins")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Medecin extends Staff {

    private String specialite;

}