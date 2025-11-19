package ma.dentalTech.entities.secretaire;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.staff.Staff;

@Entity
@Table(name = "secretaires")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Secretaire extends Staff {

    private Double commission;
}