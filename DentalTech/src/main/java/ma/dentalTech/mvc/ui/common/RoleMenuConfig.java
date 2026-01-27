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

    public static List<NavItem> menuFor(LibelleRole role, String privilegesCsv) {
        LibelleRole resolvedRole = (role == null) ? LibelleRole.SECRETAIRE : role;
        if (privilegesCsv == null || privilegesCsv.isBlank()) {
            return menuFor(resolvedRole);
        }
        String upper = privilegesCsv.toUpperCase();
        if (upper.contains("ALL")) {
            return menuFor(resolvedRole);
        }

        java.util.Set<String> privs = new java.util.HashSet<>();
        for (String p : upper.split(",")) {
            String v = p.trim();
            if (!v.isBlank()) privs.add(v);
        }

        List<NavItem> base = menuFor(resolvedRole);
        return base.stream().filter(it -> isAllowed(it.getId(), resolvedRole, privs))
                .collect(java.util.stream.Collectors.toList());
    }

    private static boolean isAllowed(String pageId, LibelleRole role, java.util.Set<String> privs) {
        if (role == LibelleRole.ADMIN) return true;
        if (privs == null || privs.isEmpty()) return true;

        return switch (pageId) {
            case "caisse" -> privs.contains("CAISSE") || privs.contains("FACTURATION");
            case "rdv", "agenda_med", "liste_attente" -> privs.contains("AGENDA");
            case "consultations", "dossiers", "ordonnances", "certificats" -> privs.contains("CONSULTATION");
            default -> true;
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
