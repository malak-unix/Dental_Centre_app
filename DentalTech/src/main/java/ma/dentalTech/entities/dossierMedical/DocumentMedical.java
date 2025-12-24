package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.TypeDocument;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DocumentMedical extends BaseEntity {

    private Long dossierId;
    private Long consultationId;

    private TypeDocument typeDocument;

    private String titre;
    private String nomFichier;
    private String cheminFichier;

    private Long tailleOctets;
    private LocalDateTime dateDocument;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentMedical)) return false;
        DocumentMedical that = (DocumentMedical) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
