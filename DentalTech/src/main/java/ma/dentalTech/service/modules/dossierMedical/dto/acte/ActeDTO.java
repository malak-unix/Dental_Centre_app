package ma.dentalTech.service.modules.dossierMedical.dto.acte;

public record ActeDTO(
        Long id,
        String libelle,
        String categorie,
        Double prixBase,
        String description
) {}
