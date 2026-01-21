package ma.dentalTech.mvc.ui.common;

import java.awt.*;

public final class DentalTheme {

    private DentalTheme() {
    }

    // Colors
    public static final Color BG = new Color(0xFA, 0xF8, 0xF5);
    public static final Color CARD = new Color(250, 246, 240);
    public static final Color PRIMARY_DARK = new Color(0x0B, 0x13, 0x2B);
    public static final Color PRIMARY = new Color(0x1C, 0x25, 0x41);
    public static final Color GOLD = new Color(0xCB, 0xA1, 0x35);

    public static final Color BORDER = new Color(0xCB, 0xA1, 0x35);
    public static final Color TEXT = new Color(30, 30, 30);
    public static final Color BEIGE = new Color(0xD4, 0xAF, 0x8F);
    public static final Color BEIGE_LIGHT = new Color(0xFA, 0xF8, 0xF5);
    public static final Color MUTED_TEXT = new Color(0xCB, 0xA1, 0x35);
    public static final Color MUTED        = new Color(110, 110, 110);

    public static final java.awt.Color BG2 = new java.awt.Color(0xF2E3D6);
    public static final java.awt.Color PANEL = new java.awt.Color(0xF8F1EA);
    public static final Color CARD_BG = CARD;        // used in cards sections
    public static final Color TEXT1   = TEXT;        // main text
    public static final Color TEXT2   = MUTED;       // secondary text

    // Header background (maquettes)
    public static final java.awt.Color BG_HEADER = PANEL;
    public static final java.awt.Color STROKE = new java.awt.Color(0xC7A26A);
    public static final java.awt.Color PRIMARY2 = new java.awt.Color(0x1F4C5B);
    public static final java.awt.Color PRIMARY_2 = new java.awt.Color(0x173A45);

    public static final int RADIUS = 18;
    public static final int BTN_RADIUS = 16;

    public static final java.awt.Font H1 = new java.awt.Font("Serif", java.awt.Font.BOLD, 28);
    public static final java.awt.Font H2 = new java.awt.Font("Serif", java.awt.Font.BOLD, 22);
    public static final java.awt.Font BASE = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13);
    public static final Font BASE_BOLD = new Font("SansSerif", Font.BOLD, 13);

    public static Font titleFont(int size) {
        return new Font("Poppins", Font.BOLD, size);
    }

    public static Font text(int size) {
        return new Font("SansSerif", Font.PLAIN, size);
    }
    public static Font textFont(int size) {
        return new Font("Roboto", Font.PLAIN, size);
    }

    public static Font textBold(int size) {
        return new Font("Roboto", Font.BOLD, size);
    }
    public static void setAntialias(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}