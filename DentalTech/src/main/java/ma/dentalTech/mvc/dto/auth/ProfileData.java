package ma.dentalTech.mvc.dto.auth;

import java.time.LocalDate;

import lombok.Builder;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.enums.Sexe;

@Builder
public record ProfileData(
        Long id,
        LibelleRole rolePrincipal,

        // Utilisateur
        String prenom,
        String nom,
        String email,
        String adresse,
        String cin,
        String tel,
        Sexe sexe,
        String login,
        LocalDate lastLoginDate,
        LocalDate dateNaissance,
        String avatar,

        // Staff (optionnel)
        Double salaire,
        Double prime,
        LocalDate dateRecrutement,
        Integer soldeConge,
        Long cabinetId,

        // Medecin (optionnel)
        String specialite,

        // Secretaire (optionnel)
        String numCNSS,
        Double commission
) {}
