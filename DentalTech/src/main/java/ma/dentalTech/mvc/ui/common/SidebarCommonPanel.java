package ma.dentalTech.mvc.ui.common;

import ma.dentalTech.entities.enums.LibelleRole;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SidebarCommonPanel extends JPanel {

    private final Map<String, NavButton> navButtons = new LinkedHashMap<>();
    private final Consumer<String> onNavigate;

    private final LibelleRole role;

    public SidebarCommonPanel(LibelleRole role, String fullName, Consumer<String> onNavigate) {
        this.role = (role != null) ? role : LibelleRole.SECRETAIRE;
        this.onNavigate = onNavigate;

        setPreferredSize(new Dimension(270, 780));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setOpaque(false);

        buildUi();
    }

    private void buildUi() {
        add(Box.createVerticalStrut(8));

        CardPanel navCard = new CardPanel((String) null);
        navCard.setLayout(new BoxLayout(navCard, BoxLayout.Y_AXIS));
        navCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<NavItem> items = RoleMenuConfig.menuFor(role);
        for (int i = 0; i < items.size(); i++) {
            NavItem it = items.get(i);

            Icon icon = loadIcon(iconPathFor(it.getId()), 18, 18);
            navCard.add(makeNav(it.getLabel(), icon, it.getId()));

            if (i < items.size() - 1) navCard.add(Box.createVerticalStrut(8));
        }

        add(navCard);
        add(Box.createVerticalGlue());
    }

    private NavButton makeNav(String text, Icon icon, String pageKey) {
        NavButton b = new NavButton(text, icon, false);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        b.addActionListener(e -> {
            if (onNavigate != null) onNavigate.accept(pageKey);
        });

        navButtons.put(pageKey, b);
        return b;
    }

    public void setActive(String pageKey) {
        for (Map.Entry<String, NavButton> e : navButtons.entrySet()) {
            e.getValue().setActive(e.getKey().equals(pageKey));
        }
    }

    private Icon loadIcon(String path, int w, int h) {
        if (path == null) return null;
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String iconPathFor(String pageKey) {
        return switch (pageKey) {
            case "dashboard"      -> "/assets/icons/dashboard.png";
            case "patients"       -> "/assets/icons/patients.png";
            case "rdv"            -> "/assets/icons/calendar.png";
            case "liste_attente"  -> "/assets/icons/waiting.png";
            case "agenda_med"     -> "/assets/icons/planning.png";
            case "dossiers"       -> "/assets/icons/folder.png";
            case "consultations"  -> "/assets/icons/consultation.png";
            case "ordonnances"    -> "/assets/icons/medicine.png";
            case "certificats"    -> "/assets/icons/certificat.png";
            case "situation_fin"  -> "/assets/icons/money.png";
            case "actes"          -> "/assets/icons/teeth.png";
            case "medicaments"    -> "/assets/icons/medicine.png";
            case "antecedents"    -> "/assets/icons/antecedents.png";
            case "caisse"         -> "/assets/icons/caisse.png";
            case "utilisateurs"   -> "/assets/icons/users.png";
            case "referentiels"   -> "/assets/icons/referentiels.png";
            case "sauvegardes"    -> "/assets/icons/backup.png";
            case "roles"          -> "/assets/icons/lock.png";
            default -> null;
        };
    }
}
