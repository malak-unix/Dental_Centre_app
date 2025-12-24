package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class UiTheme {

    private UiTheme() {}

    public static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);

    public static final Color BG = new Color(245, 247, 250);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(33, 37, 41);
    public static final Color MUTED = new Color(110, 117, 125);

    public static final Color PRIMARY = new Color(13, 110, 253);
    public static final Color DANGER = new Color(220, 53, 69);
    public static final Color BORDER = new Color(222, 226, 230);

    public static void install() {
        // Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Fonts / colors globales
        setUI("Label.font", FONT_BASE);
        setUI("Button.font", FONT_BOLD);
        setUI("TextField.font", FONT_BASE);
        setUI("TextArea.font", FONT_BASE);
        setUI("ComboBox.font", FONT_BASE);
        setUI("Table.font", FONT_BASE);
        setUI("TableHeader.font", FONT_BOLD);

        setUI("Panel.background", BG);
        setUI("Table.background", CARD);
        setUI("Table.gridColor", BORDER);
    }

    private static void setUI(String key, Object value) {
        UIManager.put(key, value);
    }

    public static JPanel card(JComponent content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
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
