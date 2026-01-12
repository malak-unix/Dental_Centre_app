package ma.dentalTech.mvc.dto.dossierMedicale.certificat;

import java.time.LocalDate;

public record CertificatDTO(
        Long id,
        Long dossierId,
        LocalDate dateDebut,
        LocalDate dateFin,
        Integer duree,
        String noteMedecin
) {}
