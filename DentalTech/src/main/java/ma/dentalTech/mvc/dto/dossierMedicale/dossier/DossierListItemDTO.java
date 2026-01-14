package ma.dentalTech.mvc.dto.dossierMedicale.dossier;

public record DossierListItemDTO(
        Long dossierId,
        Long patientId,
        Long medecinId,
        String notesPreview) {}
