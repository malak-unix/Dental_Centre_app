package ma.dentalTech.service.modules.dossierMedical.dto.readonly;

public record SituationFinanciereDTO(
        Long id,
        Long dossierId,
        Double totalDesActes,
        Double totalPaye,
        Double credit,
        String statut
) {}
