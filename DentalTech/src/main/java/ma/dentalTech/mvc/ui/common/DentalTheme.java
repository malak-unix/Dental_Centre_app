package ma.dentalTech.mvc.ui.common;

import java.awt.*;

public final class DentalTheme {
    private DentalTheme(){}

    // Couleurs (comme ma charte graphique )
    public static final Color BG = new Color(0xFA, 0xF8, 0xF5);        // FAF8F5
    public static final Color PRIMARY_DARK = new Color(0x0B, 0x13, 0x2B); // 0B132B
    public static final Color PRIMARY = new Color(0x1C, 0x25, 0x41);     // 1C2541
    public static final Color GOLD = new Color(0xCB, 0xA1, 0x35);        // CBA135
    public static final Color BORDER = new Color(0xCB, 0xA1, 0x35);      // border gold
    public static final Color TEXT = new Color(30, 30, 30);
    public static final Color MUTED = new Color(0x75, 0x75, 0x75);       // 757575
    public static final Color BEIGE = new Color(0xD4, 0xAF, 0x8F);   // D4AF8F
    public static final Color BEIGE_LIGHT = new Color(0xFA, 0xF8, 0xF5); // FAF8F5

    public static Font titleFont(int size){ return new Font("Poppins", Font.BOLD, size); }
    public static Font textFont(int size){ return new Font("Roboto", Font.PLAIN, size); }
    public static Font textBold(int size){ return new Font("Roboto", Font.BOLD, size); }
}
