package ma.dentalTech.entities.prescription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Prescription extends BaseEntity {

    private int quantite;
    private String frequence;
    private int dureeEnJours;
}
