package ma.dentalTech.entities.agendaMensuel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.Mois;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AgendaMensuel extends BaseEntity {

    // ⚠ ajouté pour coller à la colonne medecin_id en base
    private Long medecinId;

    private Mois mois;
    private int annee;

    // Non stocké directement dans la table, mais utile côté objet
    private List<DetailJournee> listeJours;
}
