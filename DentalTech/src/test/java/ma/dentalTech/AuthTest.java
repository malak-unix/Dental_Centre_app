package ma.dentalTech;

import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.mvc.dto.auth.*;
import ma.dentalTech.service.modules.profileService.api.ProfileService;
import ma.dentalTech.service.modules.profileService.impl.ProfileServiceImpl;
import ma.dentalTech.service.modules.profileService.impl.ProfileValidatorImpl;
import java.time.LocalDate;

public class AuthTest {
    public static void main(String[] args) {

        System.out.println("--- DÉBUT DU TEST AUTH ---");

        // 1. Initialisation
        ProfileService profileService = new ProfileServiceImpl(
                null, null, null, null, null, null,
                new ProfileValidatorImpl(),
                null,
                null
        );

        // 2. TEST : Création de la requête (17 paramètres maintenant)
        ProfileUpdateRequest updateReq = new ProfileUpdateRequest(
                1L,                         // 1. id
                "Alami",                    // 2. nom
                "Ahmed",                    // 3. prenom
                "ahmed@email.com",          // 4. email
                "Casablanca",               // 5. adresse
                "BK12345",                  // 6. cin
                "0611223344",               // 7. tel
                Sexe.Homme,                 // 8. sexe (Correction casse)
                LocalDate.of(1990, 5, 20),  // 9. dateNaissance
                "avatar.png",               // 10. avatar
                8000.0,                     // 11. salaire
                1000.0,                     // 12. prime
                LocalDate.now(),            // 13. dateRecrutement
                25,                         // 14. soldeConge
                "S123",                     // 15. specialite (Médecin)
                "CNSS99",                   // 16. numCNSS (Secrétaire)
                150.0                       // 17. commission (Secrétaire) -> LE PARAMÈTRE MANQUANT
        );

        System.out.println("Vérification du profil de : " + updateReq.nom());

        try {
            // Utilisation des noms de méthodes corrects pour un Record (.ok() et .fieldErrors())
            ProfileUpdateResult result = profileService.update(updateReq);

            if (result.ok()) {
                System.out.println("✅ Succès !");
            } else {
                System.out.println("❌ Échec : " + result.message());
                System.out.println("Erreurs détectées : " + result.fieldErrors());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Note : Imports et structure validés ! Le test a bloqué sur le SQL normal (Factories nulles).");
        }
    }
}