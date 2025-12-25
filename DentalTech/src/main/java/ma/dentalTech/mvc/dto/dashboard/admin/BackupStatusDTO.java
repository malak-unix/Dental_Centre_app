package ma.dentalTech.mvc.dto.dashboard.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupStatusDTO {

    private LocalDateTime derniereSauvegarde;
    private String statut;          // OK / ECHEC / EN_COURS
    private String taille;          // ex: "120 MB"
    private String chemin;          // optionnel
}
