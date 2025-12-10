package ma.dentalTech.mvc.controllers.modules.patient.swing_implementation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.PatientDTO;
import ma.dentalTech.mvc.ui.modules.patient.PatientView;
import ma.dentalTech.service.modules.patient.api.PatientService;

import javax.swing.*;
import java.util.List;

/**
 * Implémentation Swing du contrôleur Patient.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientControllerImpl implements PatientController {

    private PatientService service;

    @Override
    public void showRecentPatients() {
        try {
            List<PatientDTO> dtos = service.getTodayPatientsAsDTO();
            PatientView.showAsync(dtos);
        } catch (ServiceException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Erreur lors du chargement des patients du jour : " + e.getMessage(),
                    "Erreur Patient",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
