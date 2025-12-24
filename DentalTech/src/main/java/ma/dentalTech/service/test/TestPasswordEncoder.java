package ma.dentalTech.service.test;

import ma.dentalTech.service.modules.auth.impl.PasswordEncoderImpl;

public class TestPasswordEncoder {
    public static void main(String[] args) {
        // 1. Instanciation de ton implémentation
        PasswordEncoderImpl encoder = new PasswordEncoderImpl();

        System.out.println("--- 🧪 Test du PasswordEncoder ---");

        // Simulation des données
        String motDePasseSaisi = "password123";
        String hashEnBDD = "password123"; // Dans ton impl actuelle, c'est du texte brut

        // 2. Test de correspondance (Cas positif)
        boolean estValide = encoder.matches(motDePasseSaisi, hashEnBDD);
        System.out.println("Test de correspondance : " + (estValide ? "✅ RÉUSSI" : "❌ ÉCHEC"));

        // 3. Test de non-correspondance (Cas négatif)
        boolean estInvalide = encoder.matches("mauvais_pass", hashEnBDD);
        System.out.println("Test de sécurité (mauvais pass) : " + (!estInvalide ? "✅ RÉUSSI" : "❌ ÉCHEC"));

        // 4. Test avec des valeurs nulles
        try {
            encoder.matches(null, hashEnBDD);
            System.out.println("Test de valeur nulle : ✅ RÉUSSI (Géré)");
        } catch (Exception e) {
            System.out.println("Test de valeur nulle : ❌ ÉCHEC (L'application a planté)");
        }
    }
}