package ma.dentalTech.mvc.ui.common;

import ma.dentalTech.entities.enums.LibelleRole;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.image.BufferedImage;
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

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // IMPORTANT: no left padding so the card can start at x=0
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        setOpaque(true);
        setBackground(DentalTheme.BG2);
        buildUi();
    }

    private void buildUi() {
        add(Box.createVerticalStrut(6));

        CardPanel navCard = new CardPanel((String) null);
        navCard.setLayout(new BoxLayout(navCard, BoxLayout.Y_AXIS));
        navCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        navCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // ✅ THIS is the key: remove CardPanel internal padding for sidebar
        navCard.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ✅ let it expand full width
        navCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        List<NavItem> items = RoleMenuConfig.menuFor(role);
        for (int i = 0; i < items.size(); i++) {
            NavItem it = items.get(i);
            Icon icon = loadIcon(iconPathFor(it.getId()), 18, 18);

            navCard.add(makeNav(it.getLabel(), icon, it.getId()));
            if (i < items.size() - 1) navCard.add(Box.createVerticalStrut(8));
        }

        add(navCard);
        add(Box.createVerticalGlue());
        addBottomLogo();
    }

    private NavButton makeNav(String text, Icon icon, String pageKey) {
        NavButton b = new NavButton(text, icon, false);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        b.addActionListener(e -> { if (onNavigate != null) onNavigate.accept(pageKey); });
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
            ImageIcon icon = new ImageIcon(url);
            Image img = scaleImage(icon.getImage(), w, h);
            return new ImageIcon(img);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void addBottomLogo() {
        ImageIcon icon = loadIconKeepRatio("/assets/logo_enbas_gauche.png", 160);
        if (icon == null) return;

        JLabel logo = new JLabel(icon);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(16, 0, 6, 0));
        wrap.add(logo);
        add(wrap);
    }

    private Image scaleImage(Image src, int w, int h) {
        if (src == null) return null;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return image;
    }

    private ImageIcon loadIconKeepRatio(String path, int w) {
        if (path == null) return null;
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            ImageIcon icon = new ImageIcon(url);
            int ow = icon.getIconWidth();
            int oh = icon.getIconHeight();
            if (ow <= 0 || oh <= 0) return null;
            int h = Math.max(1, (int) Math.round((double) oh * w / ow));
            Image img = scaleImage(icon.getImage(), w, h);
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
