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
                    NavItem.of("Referentiels", "referentiels"),
                    NavItem.of("Sauvegardes", "sauvegardes"),
                    NavItem.of("Roles", "roles")
            );

            case MEDECIN -> List.of(
                    NavItem.of("Dashboard", "dashboard"),
                    NavItem.of("Mes consultations", "consultations"),
                    NavItem.of("Planning", "agenda_med"),
                    NavItem.of("Les dossiers", "dossiers"),
                    NavItem.of("Caisse", "caisse"),
                    NavItem.of("Certificats", "certificats"),
                    NavItem.of("Les ordonnances", "ordonnances")
            );

            case SECRETAIRE -> List.of(
                    NavItem.of("Dashboard", "dashboard"),
                    NavItem.of("Les patients", "patients"),
                    NavItem.of("Rendez-vous", "rdv"),
                    NavItem.of("Planning", "agenda_med"),
                    NavItem.of("Caisse", "caisse"),
                    NavItem.of("Liste d'attente", "liste_attente")
            );
        };
    }
    public static String roleLabel(LibelleRole role) {
        if (role == null) return "Secretaire";
        return switch (role) {
            case ADMIN -> "Admin";
            case MEDECIN -> "Medecin";
            case SECRETAIRE -> "Secretaire";
        };
    }

}
