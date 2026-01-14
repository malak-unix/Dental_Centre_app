package ma.dentalTech.mvc.dto.dossierMedicale.prescription;

public record PrescriptionDTO(
        Long id,
        Long ordonnanceId,
        Long medicamentId,
        int quantite,
        String frequence,
        int dureeEnJours
) {}
