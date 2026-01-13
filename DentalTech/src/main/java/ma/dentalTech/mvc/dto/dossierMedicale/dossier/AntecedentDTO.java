package ma.dentalTech.mvc.dto.dossierMedicale.dossier;

import ma.dentalTech.entities.enums.NiveauDeRisque;

/**
 * DTO pour afficher un antécédent médical.
 */
public record AntecedentDTO(
        Long id,
        Long patientId,
        String nom,
        String categorie,
        NiveauDeRisque niveauDeRisque,
        String description
) {}
