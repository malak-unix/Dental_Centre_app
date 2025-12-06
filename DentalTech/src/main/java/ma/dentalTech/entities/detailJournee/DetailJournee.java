package ma.dentalTech.entities.detailJournee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.entities.rdv.RDV;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DetailJournee extends BaseEntity {
    private LocalDate dateJour;
    private LocalTime heureDebutTravaillee;
    private LocalTime heureFinTravaillee;
    private StatutJournee etatJour;
    private List<RDV> listRDV;
    private String commentaire;
}
