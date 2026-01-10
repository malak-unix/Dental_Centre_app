package ma.dentalTech.mvc.dto.caisse;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaisseDashboardRequestDTO {
    private LocalDate dateDebut;
    private LocalDate dateFin;


    // "TOUTES" | "PAYEE" | "IMPAYEE"
    private String statut;

    // barre recherche "Rechercher un client..."
    private String search;
}
