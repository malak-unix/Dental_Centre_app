package ma.dentalTech.mvc.ui.modules.dashboard.secretaire;

import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;

import javax.swing.*;
import java.awt.*;

public class SecretaireDashboardPanel extends JPanel {

    public SecretaireDashboardPanel(SecretaireDashboardResponseDTO dto) {
        setLayout(new GridLayout(3, 1, 10, 10));

        add(new JLabel("📅 RDV du jour : " + dto.getNbRdvDuJour()));
        add(new JLabel("⏳ En attente : " + dto.getNbEnAttente()));
        add(new JLabel("💰 Recette du jour : " + dto.getRecetteDuJour() + " DH"));
    }
}
