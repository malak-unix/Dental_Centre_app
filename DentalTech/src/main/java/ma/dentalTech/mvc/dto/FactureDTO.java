package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.enums.StatutFacture;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class FactureDTO {
    private Long id;
    private String nomCompletPatient;
    private Double total;
    private Double resteAPayer;
    private StatutFacture statut;
    private String dateFactureFormatee;
    // D'autres champs pour la vue (ex: nom du medecin, etc.)
}