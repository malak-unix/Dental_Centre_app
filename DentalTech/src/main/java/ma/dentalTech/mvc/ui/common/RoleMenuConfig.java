package ma.dentalTech.mvc.ui.common;

import ma.dentalTech.entities.enums.LibelleRole;
import java.util.List;

public final class RoleMenuConfig {

    private RoleMenuConfig() {}

    public static List<NavItem> menuFor(LibelleRole role) {
        if (role == null) role = LibelleRole.SECRETAIRE;

        return switch (role) {
            case ADMIN -> List.of(
                    NavItem.of("Dashboard", "dashboard"),
                    NavItem.of("Utilisateurs", "utilisateurs"),
                    NavItem.of("Référentiels", "referentiels"),
                    NavItem.of("Actes", "actes"),
                    NavItem.of("Médicaments", "medicaments"),
                    NavItem.of("Antécédents", "antecedents"),
                    NavItem.of("Sauvegardes", "sauvegardes"),
                    NavItem.of("Rôles", "roles")
            );

            case MEDECIN -> List.of(
                    NavItem.of("Dashboard", "dashboard"),
                    NavItem.of("Mes patients", "patients"),
                    NavItem.of("Mes consultations", "consultations"),
                    NavItem.of("Planning", "agenda_med"),
                    NavItem.of("Les dossiers", "dossiers"),
                    NavItem.of("Certificats", "certificats"),
                    NavItem.of("Les ordonnances", "ordonnances")
            );

            case SECRETAIRE -> List.of(
                    NavItem.of("Dashboard", "dashboard"),
                    NavItem.of("Les patients", "patients"),
                    NavItem.of("Rendez-vous", "rdv"),
                    NavItem.of("Planning", "agenda_med"),        // ✅ AJOUT
                    NavItem.of("Caisse", "caisse"),
                    NavItem.of("Liste d'attente", "liste_attente")
            );


        };
    }
    public static String roleLabel(LibelleRole role) {
        if (role == null) return "Secrétaire";
        return switch (role) {
            case ADMIN -> "Admin";
            case MEDECIN -> "Médecin";
            case SECRETAIRE -> "Secrétaire";
        };
    }

}
