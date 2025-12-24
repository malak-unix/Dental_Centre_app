package ma.dentalTech.entities.dossierMedical;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.TypeDocument;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DocumentMedical extends BaseEntity {

    private Long dossierId;          // FK dossier_medical
    private Long consultationId;     // FK consultation (nullable)

    private TypeDocument typeDocument;

    private String titre;
    private String nomFichier;
    private String cheminFichier;

    private Long tailleOctets;
    private LocalDateTime dateDocument;
}
