package ma.dentalTech.mvc.dto.dashboard.medecin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedecinDashboardRequestDTO {

    private LocalDate date;       // aujourd’hui
    private Long medecinId;       // ou null si pris depuis session
    private String searchPatient; // recherche
}
