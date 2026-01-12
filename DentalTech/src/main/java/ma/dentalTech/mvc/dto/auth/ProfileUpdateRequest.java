package ma.dentalTech.mvc.dto.auth;

import lombok.Builder;
import ma.dentalTech.entities.enums.Sexe;
import java.time.LocalDate;

@Builder
public record ProfileUpdateRequest(
        Long id,
        String prenom,
        String nom,
        String email,
        String adresse,
        String cin,
        String tel,
        Sexe sexe,
        LocalDate dateNaissance,
        String avatar,

        // Champs liés au Staff
        Double salaire,
        Double prime,
        LocalDate dateRecrutement,
        Integer soldeConge,

        // Champs spécifiques aux rôles
        String specialite, // Pour le Médecin
        String numCNSS,    // Pour la Secrétaire
        Double commission  // Pour la Secrétaire
) {}