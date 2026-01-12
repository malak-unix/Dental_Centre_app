package ma.dentalTech.mvc.ui.common;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AppSidebarPanel extends JPanel {

    private final Map<String, NavButton> buttons = new LinkedHashMap<>();

    public AppSidebarPanel(UserPrincipalDTO principal, Consumer<String> onNavigate) {
        setPreferredSize(new Dimension(240, 780));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setOpaque(false);

        // --- Logo / titre ---
        JLabel title = new JLabel("DENTAL CENTER");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.TEXT2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLbl = new JLabel(roleLabel(principal != null ? principal.rolePrincipal() : null));
        roleLbl.setFont(DentalTheme.textFont(12));
        roleLbl.setForeground(DentalTheme.MUTED);
        roleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLbl = new JLabel(principal != null ? safe(principal.nom()) : "Utilisateur");
        userLbl.setFont(DentalTheme.textFont(12));
        userLbl.setForeground(DentalTheme.TEXT2);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(title);
        add(Box.createVerticalStrut(4));
        add(userLbl);
        add(Box.createVerticalStrut(2));
        add(roleLbl);
        add(Box.createVerticalStrut(16));

        // --- Nav card ---
        CardPanel navCard = new CardPanel((String) null);
        navCard.setLayout(new BoxLayout(navCard, BoxLayout.Y_AXIS));
        navCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        LibelleRole role = principal != null ? principal.rolePrincipal() : LibelleRole.SECRETAIRE;

        // Menus selon rôle (maquettes)
        if (role == LibelleRole.SECRETAIRE) {
            addNav(navCard, "Dashboard", "dashboard", onNavigate);
            addGap(navCard);
            addNav(navCard, "Les patients", "patients", onNavigate);
            addGap(navCard);
            addNav(navCard, "Rendez-vous", "rdv", onNavigate);
            addGap(navCard);
            addNav(navCard, "Agenda", "agenda", onNavigate);
            addGap(navCard);
            addNav(navCard, "La caisse", "caisse", onNavigate);
            addGap(navCard);
            addNav(navCard, "Stock", "stock", onNavigate);
            addGap(navCard);
            addNav(navCard, "Agenda med", "agenda_med", onNavigate);
        } else if (role == LibelleRole.MEDECIN) {
            addNav(navCard, "Dashboard", "dashboard", onNavigate);
            addGap(navCard);
            addNav(navCard, "Mes patients", "patients", onNavigate);
            addGap(navCard);
            addNav(navCard, "Mes consultations", "consultations", onNavigate);
            addGap(navCard);
            addNav(navCard, "Ordonnances", "ordonnances", onNavigate);
            addGap(navCard);
            addNav(navCard, "Certificats", "certificats", onNavigate);
            addGap(navCard);
            addNav(navCard, "Situation financière", "situation_fin", onNavigate);
        } else { // ADMIN
            addNav(navCard, "Dashboard", "dashboard", onNavigate);
            addGap(navCard);
            addNav(navCard, "Utilisateurs", "users", onNavigate);
            addGap(navCard);
            addNav(navCard, "Référentiels", "refs", onNavigate);
            addGap(navCard);
            addNav(navCard, "Sauvegardes", "backup", onNavigate);
        }

        add(navCard);
        add(Box.createVerticalGlue());
    }

    private void addGap(JComponent parent) {
        parent.add(Box.createVerticalStrut(8));
    }

    private void addNav(JComponent parent, String text, String key, Consumer<String> onNavigate) {
        NavButton b = new NavButton(text, false);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.addActionListener(e -> onNavigate.accept(key));
        buttons.put(key, b);
        parent.add(b);
    }

    public void setActive(String key) {
        for (Map.Entry<String, NavButton> e : buttons.entrySet()) {
            e.getValue().setActive(e.getKey().equals(key));
        }
    }

    private String roleLabel(LibelleRole role) {
        if (role == null) return "Rôle";
        return switch (role) {
            case ADMIN -> "Admin";
            case MEDECIN -> "Médecin";
            case SECRETAIRE -> "Secrétaire";
        };
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "Utilisateur" : s;
    }
}
