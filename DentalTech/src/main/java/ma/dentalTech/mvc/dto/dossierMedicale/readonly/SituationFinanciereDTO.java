package ma.dentalTech.mvc.dto.dossierMedicale.readonly;

public record SituationFinanciereDTO(
        Long id,
        Long dossierId,
        Double totalDesActes,
        Double totalPaye,
        Double credit,
        String statut
) {}
