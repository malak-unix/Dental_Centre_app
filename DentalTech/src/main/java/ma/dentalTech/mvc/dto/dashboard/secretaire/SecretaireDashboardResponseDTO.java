package ma.dentalTech.mvc.dto.dashboard.secretaire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.dto.dashboard.common.AlerteDTO;
import ma.dentalTech.mvc.dto.dashboard.common.NotificationDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretaireDashboardResponseDTO {

    // KPIs
    private Integer nbPatients; // optionnel
    private Integer nbRdvDuJour;
    private Integer nbEnAttente;
    private BigDecimal recetteDuJour; // DH

    // Lists
    private List<RdvDto> rdvDuJour;
    private List<ListeAttenteDto> fileAttente;

    // Alertes & notifications (nouveau)
    private Integer nbAlertesNonLues;
    private Integer nbNotificationsNonLues;

    private List<AlerteDTO> alertes; // ex: retard RDV, impayés, urgence…
    private List<NotificationDTO> notifications; // ex: rappel, action système, etc.

    public BigDecimal getRecetteJour() {
        return recetteDuJour;
    }

    public Integer getNbRdvJour() {
        return nbRdvDuJour;
    }
}
