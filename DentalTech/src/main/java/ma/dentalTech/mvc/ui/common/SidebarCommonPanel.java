package ma.dentalTech.mvc.ui.common;

import ma.dentalTech.entities.enums.LibelleRole;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SidebarCommonPanel extends JPanel {

    private final Map<String, NavButton> navButtons = new LinkedHashMap<>();
    private final Consumer<String> onNavigate;

    private final LibelleRole role;
    private final String fullName;

    public SidebarCommonPanel(LibelleRole role, String fullName, Consumer<String> onNavigate) {
        this.role = (role != null) ? role : LibelleRole.SECRETAIRE;
        this.fullName = (fullName != null) ? fullName : "";
        this.onNavigate = onNavigate;

        setPreferredSize(new Dimension(240, 780));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setOpaque(false);

        buildUi();
    }

    private void buildUi() {
        // Logo
        LogoHeaderPanel logo = new LogoHeaderPanel();
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(logo);

        // Menu (dans une card)
        CardPanel navCard = new CardPanel((String) null);
        navCard.setLayout(new BoxLayout(navCard, BoxLayout.Y_AXIS));
        navCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<NavItem> items = RoleMenuConfig.menuFor(role);
        for (int i = 0; i < items.size(); i++) {
            NavItem it = items.get(i);
            navCard.add(makeNav(it.getLabel(), it.getId()));
            if (i < items.size() - 1) navCard.add(Box.createVerticalStrut(8));
        }

        add(navCard);
        add(Box.createVerticalGlue());

        // User Card bottom
        String roleText = RoleMenuConfig.roleLabel(role);
        UserCardPanel userCard = new UserCardPanel(roleText, fullName);
        userCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(userCard);
    }

    private NavButton makeNav(String text, String pageKey) {
        NavButton b = new NavButton(text, false);
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

    public void setEnabledItem(String pageKey, boolean enabled) {
        NavButton b = navButtons.get(pageKey);
        if (b != null) b.setEnabled(enabled);
    }
}
