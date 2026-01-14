package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.mvc.controllers.modules.security.api.SecurityController;
import ma.dentalTech.mvc.dto.security.BackupDTO;
import ma.dentalTech.mvc.dto.security.LogDTO;
import ma.dentalTech.mvc.dto.security.SessionDTO;
import ma.dentalTech.mvc.ui.modules.security.SecurityManagementPanel;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestSecurityUi {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            JFrame frame = new JFrame("Test Security UI (Navy/Gold Theme)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            SecurityController mockController = new SecurityController() {

                // MOCK STATE
                private final List<BackupDTO> backups = new ArrayList<>(List.of(
                        new BackupDTO("backup_20240101.sql", LocalDateTime.now(), 1024000),
                        new BackupDTO("backup_20231225.sql", LocalDateTime.now().minusDays(10), 512000)));

                private final List<LogDTO> logs = new ArrayList<>(List.of(
                        new LogDTO(1L, "admin", "LOGIN", "User logged in", LocalDateTime.now()),
                        new LogDTO(2L, "user1", "CREATE_PATIENT", "Created Patient X",
                                LocalDateTime.now().minusHours(1))));

                @Override
                public List<LogDTO> getAllLogs() {
                    return new ArrayList<>(logs);
                }

                @Override
                public void createBackup() {
                    backups.add(new BackupDTO("backup_" + System.currentTimeMillis() + ".sql", LocalDateTime.now(),
                            2048000));
                    JOptionPane.showMessageDialog(null, "Mock: Backup Added to list");
                }

                @Override
                public void restoreBackup(String fileName) {
                    JOptionPane.showMessageDialog(null, "Mock: Restoring " + fileName + "...");
                }

                @Override
                public List<BackupDTO> listBackups() {
                    return new ArrayList<>(backups);
                }

                @Override
                public void deleteBackup(String fileName) {
                    boolean removed = backups.removeIf(b -> b.fileName().equals(fileName));
                    if (removed) {
                        JOptionPane.showMessageDialog(null, "Mock: " + fileName + " deleted.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Mock: File not found.");
                    }
                }

                @Override
                public List<SessionDTO> getActiveSessions() {
                    return List.of(
                            new SessionDTO(1L, "admin", "ADMIN", LocalDateTime.now(), "ACTIVE"),
                            new SessionDTO(2L, "dr_smith", "MEDECIN", LocalDateTime.now().minusMinutes(20), "IDLE"));
                }
            };

            frame.setContentPane(new SecurityManagementPanel(mockController));
            frame.setVisible(true);
        });
    }
}
