package ma.dentalTech.service.modules.dossierMedical.dto.dossier;

public record DossierDTO(
        Long id,
        Long patientId,
        Long medecinId,
        String notes
) {}
