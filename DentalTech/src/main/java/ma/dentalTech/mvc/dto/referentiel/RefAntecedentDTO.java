package ma.dentalTech.mvc.dto.referentiel;

import ma.dentalTech.entities.enums.NiveauDeRisque;

public record RefAntecedentDTO(
        Long id,
        String nom,
        String categorie,
        NiveauDeRisque risque) {
}
