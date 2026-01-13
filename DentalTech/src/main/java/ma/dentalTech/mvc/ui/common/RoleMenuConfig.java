package ma.dentalTech.mvc.ui.common;

import ma.dentalTech.entities.enums.LibelleRole;
import java.util.ArrayList;
import java.util.List;

public final class RoleMenuConfig {

    private RoleMenuConfig() {}

    public static List<NavItem> menuFor(LibelleRole role) {
        if (role == null) role = LibelleRole.SECRETAIRE;

        return switch (role) {
            case SECRETAIRE -> secretaireMenu();
            case MEDECIN -> medecinMenu();
            case ADMIN -> adminMenu();
        };
    }

    private static List<NavItem> secretaireMenu() {
        List<NavItem> items = new ArrayList<>();
        items.add(new NavItem("dashboard", "Dashboard"));
        items.add(new NavItem("patients", "Les patients"));
        items.add(new NavItem("rdv", "Rendez-vous"));
        items.add(new NavItem("dossiers", "Dossiers"));
        items.add(new NavItem("caisse", "La caisse"));
        items.add(new NavItem("agenda_med", "Agenda med"));
        items.add(new NavItem("stock", "Stock"));
        items.add(new NavItem("liste_attente", "File d'attente"));
        return items;
    }

    private static List<NavItem> medecinMenu() {
        List<NavItem> items = new ArrayList<>();
        items.add(new NavItem("dashboard", "Dashboard"));
        items.add(new NavItem("patients", "Mes patients"));
        items.add(new NavItem("dossiers", "Dossiers"));
        items.add(new NavItem("consultations", "Mes consultations"));
        items.add(new NavItem("ordonnances", "Ordonnances"));
        items.add(new NavItem("certificats", "Certificats"));
        items.add(new NavItem("situation_fin", "Situation financière"));
        return items;
    }

    private static List<NavItem> adminMenu() {
        List<NavItem> items = new ArrayList<>();
        items.add(new NavItem("dashboard", "Dashboard"));
        items.add(new NavItem("utilisateurs", "Utilisateurs"));
        items.add(new NavItem("referentiels", "Référentiels"));
        items.add(new NavItem("sauvegardes", "Sauvegardes"));
        items.add(new NavItem("roles", "Rôles"));
        return items;
    }

    public static String roleLabel(LibelleRole role) {
        if (role == null) return "Secrétaire";
        return switch (role) {
            case SECRETAIRE -> "Secrétaire";
            case MEDECIN -> "Médecin";
            case ADMIN -> "Admin";
        };
    }
}
