package ma.dentalTech.mvc.dto.dossierMedicale.intervention;

public record InterventionMedecinDTO(
        Long id,
        Long consultationId,
        Long acteId,
        Double prixPatient,
        Integer numDent
) {}
