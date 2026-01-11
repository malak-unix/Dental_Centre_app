package ma.dentalTech.mvc.dto.dashboard.medecin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.mvc.dto.agenda.RdvDto;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedecinDashboardResponseDTO {

    // KPIs
    private Integer nbPatientsDuJour;
    private Integer nbRdvDuJour;
    private Integer nbActesRealises;
    private BigDecimal recetteDuJour;

    // RDV table
    private List<RdvDto> rdvDuJour;

    // client en cours
    private PatientCurrentDTO patientEnCours;
}
