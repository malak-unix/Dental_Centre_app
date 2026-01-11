package ma.dentalTech.mvc.dto.dashboard.secretaire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretaireDashboardRequestDTO {

    private LocalDate date;          // par défaut: aujourd’hui
    private String searchPatient;    // texte barre recherche
}
