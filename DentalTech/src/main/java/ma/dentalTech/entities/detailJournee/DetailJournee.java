package ma.dentalTech.entities.detailJournee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.entities.rdv.RDV;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailJournee extends BaseEntity {

    private LocalDate dateJour;
    private LocalTime heureDebutTravaillee;
    private LocalTime heureFinTravaillee;
    private StatutJournee etatJour;         // Enum
    private List<RDV> listRDV;
    private String commentaire;
}
