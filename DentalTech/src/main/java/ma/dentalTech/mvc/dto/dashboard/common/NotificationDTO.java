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
public class NotificationDTO {
    private Long id;

    // ex: "SYSTEM", "AGENDA", "CAISSE", "DOSSIER"
    private String source;

    private String titre;
    private String message;

    private boolean lue;
    private LocalDateTime date;

    // optionnel: navigation
    private String action;
    private Long referenceId;
}
