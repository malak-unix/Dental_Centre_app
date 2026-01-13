package ma.dentalTech.mvc.dto.dossierMedicale.dossier;

public record DossierDTO(
        Long id,
        Long patientId,
        Long medecinId,
        String notes
) {}
