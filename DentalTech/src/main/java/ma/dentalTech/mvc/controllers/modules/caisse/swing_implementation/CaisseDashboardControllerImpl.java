package ma.dentalTech.mvc.controllers.modules.caisse.swing_implementation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseDashboardController;
import ma.dentalTech.mvc.dto.CaisseDashboardDTO;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaisseDashboardControllerImpl implements CaisseDashboardController {

    private CaisseDashboardService caisseDashboardService;

    // ================== ADMIN ==================

    @Override
    public void showAdminDashboardToday() throws ServiceException {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end   = today.plusDays(1).atStartOfDay().minusNanos(1);

        CaisseDashboardDTO dto = caisseDashboardService.getDashboardBetween(start, end);
        showDashboardFrame(dto, "Dashboard Caisse - ADMIN - Aujourd'hui");
    }

    @Override
    public void showAdminDashboardForPeriod(LocalDate startDate, LocalDate endDate) throws ServiceException {
        LocalDateTime[] range = validateAndBuildRange(startDate, endDate);
        CaisseDashboardDTO dto = caisseDashboardService.getDashboardBetween(range[0], range[1]);
        String title = String.format("Dashboard Caisse - ADMIN - Du %s au %s", startDate, endDate);
        showDashboardFrame(dto, title);
    }

    // ================== MEDECIN ==================

    @Override
    public void showMedecinDashboardToday(Long medecinId) throws ServiceException {
        // Pour l'instant : même calcul global, mais titre différent
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end   = today.plusDays(1).atStartOfDay().minusNanos(1);

        CaisseDashboardDTO dto = caisseDashboardService.getDashboardBetween(start, end);
        String title = String.format("Dashboard Caisse - MEDECIN #%d - Aujourd'hui", medecinId);
        showDashboardFrame(dto, title);
    }

    @Override
    public void showMedecinDashboardForPeriod(Long medecinId, LocalDate startDate, LocalDate endDate) throws ServiceException {
        LocalDateTime[] range = validateAndBuildRange(startDate, endDate);
        CaisseDashboardDTO dto = caisseDashboardService.getDashboardBetween(range[0], range[1]);
        String title = String.format("Dashboard Caisse - MEDECIN #%d - Du %s au %s", medecinId, startDate, endDate);
        showDashboardFrame(dto, title);
    }

    // ================== SECRETAIRE ==================

    @Override
    public void showSecretaireDashboardToday(Long secretaireId) throws ServiceException {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end   = today.plusDays(1).atStartOfDay().minusNanos(1);

        CaisseDashboardDTO dto = caisseDashboardService.getDashboardBetween(start, end);
        String title = String.format("Dashboard Caisse - SECRÉTAIRE #%d - Aujourd'hui", secretaireId);
        showDashboardFrame(dto, title);
    }

    @Override
    public void showSecretaireDashboardForPeriod(Long secretaireId, LocalDate startDate, LocalDate endDate) throws ServiceException {
        LocalDateTime[] range = validateAndBuildRange(startDate, endDate);
        CaisseDashboardDTO dto = caisseDashboardService.getDashboardBetween(range[0], range[1]);
        String title = String.format("Dashboard Caisse - SECRÉTAIRE #%d - Du %s au %s", secretaireId, startDate, endDate);
        showDashboardFrame(dto, title);
    }

    // =================================================================
    //  Méthodes utilitaires
    // =================================================================

    private LocalDateTime[] validateAndBuildRange(LocalDate startDate, LocalDate endDate) throws ServiceException {
        if (startDate == null || endDate == null) {
            throw new ServiceException("Les dates de début et de fin ne doivent pas être null.");
        }
        if (endDate.isBefore(startDate)) {
            throw new ServiceException("La date de fin doit être >= la date de début.");
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end   = endDate.plusDays(1).atStartOfDay().minusNanos(1);
        return new LocalDateTime[]{start, end};
    }

    private void showDashboardFrame(CaisseDashboardDTO dto, String title) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(480, 320);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
            panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            addRow(panel, "Période début :", String.valueOf(dto.getDateDebut()));
            addRow(panel, "Période fin :", String.valueOf(dto.getDateFin()));

            panel.add(new JLabel("---------------"));
            panel.add(new JLabel("---------------"));

            addRow(panel, "Total factures :", format(dto.getTotalFactures()));
            addRow(panel, "Total réglé :", format(dto.getTotalRegle()));
            addRow(panel, "Total non réglé :", format(dto.getTotalNonRegle()));

            panel.add(new JLabel("---------------"));
            panel.add(new JLabel("---------------"));

            addRow(panel, "Autres revenus :", format(dto.getTotalRevenus()));
            addRow(panel, "Charges :", format(dto.getTotalCharges()));

            panel.add(new JLabel("==============="));
            panel.add(new JLabel("==============="));

            addRow(panel, "Solde net :", format(dto.getSoldeNet()));

            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }

    private void addRow(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        panel.add(new JLabel(value));
    }

    private String format(Double value) {
        if (value == null) return "0.00";
        return String.format("%.2f", value);
    }
}
