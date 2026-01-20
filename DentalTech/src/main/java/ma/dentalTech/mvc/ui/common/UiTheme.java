package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

public final class UiTheme {

    private UiTheme() {}

    public static final Font FONT_BASE = DentalTheme.textFont(13);
    public static final Font FONT_BOLD = DentalTheme.textBold(13);
    public static final Font FONT_TITLE = DentalTheme.titleFont(18);

    public static final Color BG = DentalTheme.BG;
    public static final Color CARD = DentalTheme.CARD;
    public static final Color TEXT = DentalTheme.TEXT2;
    public static final Color MUTED = DentalTheme.MUTED;

    public static final Color PRIMARY = DentalTheme.PRIMARY_DARK;
    public static final Color DANGER = new Color(0xC9, 0x43, 0x43);
    public static final Color BORDER = DentalTheme.STROKE;

    public static void install() {
        // Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Fonts / colors globales
        setUI("Label.font", new FontUIResource(FONT_BASE));
        setUI("Button.font", new FontUIResource(FONT_BOLD));
        setUI("TextField.font", new FontUIResource(FONT_BASE));
        setUI("TextArea.font", new FontUIResource(FONT_BASE));
        setUI("ComboBox.font", new FontUIResource(FONT_BASE));
        setUI("Table.font", new FontUIResource(FONT_BASE));
        setUI("TableHeader.font", new FontUIResource(FONT_BOLD));

        setUI("Panel.background", BG);
        setUI("ScrollPane.background", BG);
        setUI("Table.background", CARD);
        setUI("Table.gridColor", BORDER);
        setUI("TableHeader.background", DentalTheme.PANEL);
        setUI("TableHeader.foreground", TEXT);
        setUI("Label.foreground", TEXT);

        setUI("TextField.background", Color.WHITE);
        setUI("TextField.foreground", TEXT);
        setUI("TextArea.background", Color.WHITE);
        setUI("TextArea.foreground", TEXT);
        setUI("ComboBox.background", Color.WHITE);
        setUI("ComboBox.foreground", TEXT);

        setUI("Button.background", PRIMARY);
        setUI("Button.foreground", Color.WHITE);
        setUI("Button.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                new EmptyBorder(6, 14, 6, 14)
        ));

        setUI("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        setUI("ComboBox.border", BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true));
        setUI("TitledBorder.titleColor", TEXT);
    }

    private static void setUI(String key, Object value) {
        UIManager.put(key, value);
    }

    public static JPanel card(JComponent content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 2, true),
                new EmptyBorder(12,12,12,12)
        ));
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    public static JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(DANGER);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT);
        return l;
    }

    public static void errorDialog(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void infoDialog(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
