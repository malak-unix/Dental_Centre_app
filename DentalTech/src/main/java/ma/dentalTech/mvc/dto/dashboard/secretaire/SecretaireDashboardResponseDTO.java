package ma.dentalTech.mvc.dto.dashboard.secretaire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.mvc.dto.agenda.RdvDto;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretaireDashboardResponseDTO {

    // KPIs
    private Integer nbPatients;          // total patients (optionnel)
    private Integer nbRdvDuJour;
    private Integer nbEnAttente;
    private BigDecimal recetteDuJour;   // DH

    // lists
    private List<RdvDto> rdvDuJour;
    private List<ListeAttenteDto> fileAttente;
}
