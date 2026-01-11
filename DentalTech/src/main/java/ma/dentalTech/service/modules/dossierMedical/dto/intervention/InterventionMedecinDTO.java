package ma.dentalTech.service.modules.dossierMedical.dto.intervention;

public record InterventionMedecinDTO(
        Long id,
        Long consultationId,
        Long acteId,
        Double prixPatient,
        Integer numDent
) {}
