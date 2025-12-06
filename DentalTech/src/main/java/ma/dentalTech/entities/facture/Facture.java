package ma.dentalTech.entities.facture;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutFacture;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Facture extends BaseEntity {

    private Long consultationId;
    private LocalDate dateFacture;
    private Double totalFacture;
    private Double totalPaye;
    private Double reste;
    private StatutFacture statut;
}
