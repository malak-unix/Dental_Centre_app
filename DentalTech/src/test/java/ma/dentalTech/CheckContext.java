package ma.dentalTech;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.service.modules.profileService.api.ProfileService;

public class CheckContext {
    public static void main(String[] args) {
        System.out.println("Lancement de la vérification manuelle...");

        try {
            // On récupère le service via l'ApplicationContext
            ProfileService profileService = ApplicationContext.getBean(ProfileService.class);

            if (profileService != null) {
                System.out.println("✅ BRAVO : Le ProfileService est opérationnel !");
            } else {
                System.out.println("⚠️ ALERTE : Le service est introuvable dans le contexte.");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur critique : " + e.getMessage());
            e.printStackTrace();
        }
    }
}