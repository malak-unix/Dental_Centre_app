package ma.dentalTech.mvc.dto.dashboard.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardRequestDTO {

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String role;     // Tous / ADMIN / MEDECIN / SECRETAIRE
    private String statut;   // Tous / ACTIF / DESACTIVE
    private String search;   // recherche utilisateur
}
