package ma.dentalTech.mvc.ui.modules.dashboard;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class DashboardPanel extends JPanel {

    // Définition des couleurs selon la charte graphique
    private static final Color PRIMARY = new Color(0x0B132B);   // Bleu foncé
    private static final Color SECONDARY = new Color(0xCBA135);  // Jaune or
    private static final Color LIGHT = new Color(0xFAF8F5);      // Blanc crème
    private static final Color GRAY = new Color(0xB7B7B7);       // Gris clair
    private static final Color BUTTON_COLOR = new Color(0x1C2541); // Bleu foncé
   private static final Color BEIGE = new Color(0xD4, 0xAF, 0x8F);   // D4AF8F
   private static final Color BEIGE_LIGHT = new Color(0xFA, 0xF8, 0xF5); // FAF8F5

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(LIGHT);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header avec le titre et le rôle
        JPanel header = buildHeader();
        add(header, BorderLayout.NORTH);

        // Main content (sections comme Caisse, RDV, Notifications, etc.)
        JPanel content = new JPanel(new GridLayout(2, 2, 20, 20));
        content.setOpaque(false);

        // Cartes : sections
        content.add(createCard("CAISSE", PRIMARY));
        content.add(createCard("Rendez-vous", SECONDARY));
        content.add(createCard("Notifications", GRAY));
        content.add(createCard("Statistiques", BUTTON_COLOR));

        add(content, BorderLayout.CENTER);
    }

    // Header avec titre et rôle
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY);

        JLabel roleLabel = new JLabel("Secrétaire");
        roleLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        roleLabel.setForeground(GRAY);

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(roleLabel);

        JButton refreshBtn = new JButton("Rafraîchir");
        refreshBtn.setFont(new Font("Roboto", Font.BOLD, 12));
        refreshBtn.setBackground(SECONDARY);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshContent());

        header.add(leftPanel, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);

        return header;
    }

    // Créer une carte pour chaque section
    private JPanel createCard(String title, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(GRAY, 1));
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(250, 150));

        JLabel cardTitle = new JLabel(title);
        cardTitle.setFont(new Font("Poppins", Font.BOLD, 14));
        cardTitle.setForeground(accentColor);

        card.add(cardTitle);
        card.add(Box.createVerticalStrut(10));

        // Exemple : ajouter un bouton (action à définir)
        JButton actionButton = new DashboardButton("Détails");
        actionButton.setPreferredSize(new Dimension(150, 30));
        card.add(actionButton);

        return card;
    }

    // Exemple d'action (rafraîchissement de contenu)
    private void refreshContent() {
        System.out.println("Rafraîchissement des données...");
    }
}
