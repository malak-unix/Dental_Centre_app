package ma.dentalTech.entities.listeDattente;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.rdv.RDV;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ListeAttente extends BaseEntity {

    private String nomListe;              // exemple : "Matin", "Urgence"
    private List<RDV> rdvs;               // Composition : 1 ListeAttente → N RDV
}
