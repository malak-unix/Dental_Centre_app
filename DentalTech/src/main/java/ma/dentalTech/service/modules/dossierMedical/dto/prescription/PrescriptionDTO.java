package ma.dentalTech.service.modules.dossierMedical.dto.prescription;

public record PrescriptionDTO(
        Long id,
        Long ordonnanceId,
        Long medicamentId,
        int quantite,
        String frequence,
        int dureeEnJours
) {}
