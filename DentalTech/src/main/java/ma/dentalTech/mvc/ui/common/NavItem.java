package ma.dentalTech.mvc.ui.common;

public class NavItem {

    private final String id;
    private final String label;

    /** Factory helper: NavItem.of("Label", "id") */
    public static NavItem of(String label, String id) {
        return new NavItem(id, label);
    }

    public NavItem(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
}