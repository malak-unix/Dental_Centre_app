package ma.dentalTech.mvc.ui.modules.dashboard.admin;

import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {

    public AdminDashboardPanel(AdminDashboardResponseDTO dto) {
        setLayout(new GridLayout(4, 1, 10, 10));

        add(new JLabel("👥 Utilisateurs : " + dto.getNbUtilisateurs()));
        add(new JLabel("🛡 Admins : " + dto.getNbAdmins()));
        add(new JLabel("🦷 Actes réalisés : " + dto.getNbActesRealises()));
        add(new JLabel("💰 Recette du jour : " + dto.getRecetteDuJour() + " DH"));
    }
}
