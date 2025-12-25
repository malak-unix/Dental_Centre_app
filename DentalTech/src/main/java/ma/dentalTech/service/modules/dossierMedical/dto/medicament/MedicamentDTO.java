package ma.dentalTech.service.modules.dossierMedical.dto.medicament;

import ma.dentalTech.entities.enums.FormeMedicament;

public record MedicamentDTO(
        Long id,
        String nom,
        String laboratoire,
        String type,
        FormeMedicament forme,
        boolean remboursable,
        Double prixUnitaire,
        String description
) {}
