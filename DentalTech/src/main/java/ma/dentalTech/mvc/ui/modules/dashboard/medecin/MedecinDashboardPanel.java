package ma.dentalTech.mvc.ui.modules.dashboard.medecin;

import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;

import javax.swing.*;
import java.awt.*;

public class MedecinDashboardPanel extends JPanel {

    public MedecinDashboardPanel(MedecinDashboardResponseDTO dto) {
        setLayout(new GridLayout(4, 1, 10, 10));

        add(new JLabel("👨‍⚕️ RDV du jour : " + dto.getNbRdvDuJour()));
        add(new JLabel("🦷 Actes réalisés : " + dto.getNbActesRealises()));
        add(new JLabel("💰 Recette du jour : " + dto.getRecetteDuJour() + " DH"));

        if (dto.getPatientEnCours() != null) {
            add(new JLabel("▶ Patient en cours : " + dto.getPatientEnCours().getNomComplet()));
        }
    }
}
