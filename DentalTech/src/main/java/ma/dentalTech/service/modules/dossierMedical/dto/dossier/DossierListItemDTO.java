package ma.dentalTech.service.modules.dossierMedical.dto.dossier;

public record DossierListItemDTO(
        Long dossierId,
        Long patientId,
        Long medecinId,
        String notesPreview) {}
