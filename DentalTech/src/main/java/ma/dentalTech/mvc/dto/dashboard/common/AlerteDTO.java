package ma.dentalTech.mvc.dto.dashboard.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlerteDTO {
    private Long id;

    // ex: "RETARD_RDV", "IMPAYE", "URGENCE", "INFO"
    private String type;
    private String titre;
    private String message;
    private String priorite;

    private boolean lue;
    private LocalDateTime date;

    // optionnel: pour l’UI (ouvrir dossier, ouvrir facture, etc.)
    private String action;      // ex: "OPEN_DOSSIER", "OPEN_FACTURE"
    private Long referenceId;   // ex: patientId / factureId / rdvId
}
