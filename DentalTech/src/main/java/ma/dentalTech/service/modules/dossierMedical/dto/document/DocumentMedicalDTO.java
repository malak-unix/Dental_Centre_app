package ma.dentalTech.service.modules.dossierMedical.dto.document;

import ma.dentalTech.entities.enums.TypeDocument;
import java.time.LocalDateTime;

public record DocumentMedicalDTO(
        Long id,
        Long dossierId,
        Long consultationId,
        TypeDocument typeDocument,
        String titre,
        String nomFichier,
        String cheminFichier,
        Long tailleOctets,
        LocalDateTime dateDocument
) {}
