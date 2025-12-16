package ma.dentalTech.entities.dossierMedical;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Prescription extends BaseEntity {
    private Long ordonnanceId;
    private Long medicamentId;
    private int quantite;
    private String frequence;
    private int dureeEnJours;
}
